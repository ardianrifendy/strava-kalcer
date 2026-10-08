package com.stravakalcer.gpx

import com.stravakalcer.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object RouteProcessor {

    /**
     * Converts parsed GPX data into an internally consistent canonical RouteTrack.
     * Enforces all data integrity rules:
     * - Haversine distance
     * - Elevation quality audit
     * - Graceful elevation handling without fabricating artificial hills
     * - Segment boundary handling
     * - Smoothed gradient calculations
     */
    fun process(parsedData: ParsedGpxData): RouteTrack {
        val rawPoints = parsedData.points

        if (rawPoints.isEmpty()) {
            val emptyQuality = ElevationClassifier.classify(emptyList())
            return RouteTrack(
                name = parsedData.trackName,
                points = emptyList(),
                totalDistanceMeters = 0.0,
                elevationGainMeters = 0.0,
                elevationLossMeters = 0.0,
                minElevationMeters = 0.0,
                maxElevationMeters = 0.0,
                elevationQuality = emptyQuality,
                bounds = RouteBounds(0.0, 0.0, 0.0, 0.0),
                segmentCount = parsedData.segmentCount,
                hasOriginalTimestamps = false
            )
        }

        // 1. Audit elevation quality
        val elevationQuality = ElevationClassifier.classify(rawPoints.map { it.elevation })

        // 2. Filter duplicate consecutive points (distance < 0.2 meters within same segment)
        val deduplicated = mutableListOf<RawGpxPoint>()
        deduplicated.add(rawPoints.first())
        for (i in 1 until rawPoints.size) {
            val prev = deduplicated.last()
            val curr = rawPoints[i]
            if (prev.segmentIndex == curr.segmentIndex) {
                val dist = GeoMath.haversineDistanceMeters(
                    prev.latitude, prev.longitude,
                    curr.latitude, curr.longitude
                )
                if (dist >= 0.3) {
                    deduplicated.add(curr)
                }
            } else {
                deduplicated.add(curr)
            }
        }

        val n = deduplicated.size
        var minLat = Double.MAX_VALUE
        var maxLat = -Double.MAX_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = -Double.MAX_VALUE

        // 3. Resolve elevation per point (interpolate small gaps if partially reconstructed)
        val resolvedElevations = DoubleArray(n)
        for (i in 0 until n) {
            val p = deduplicated[i]
            minLat = min(minLat, p.latitude)
            maxLat = max(maxLat, p.latitude)
            minLon = min(minLon, p.longitude)
            maxLon = max(maxLon, p.longitude)

            resolvedElevations[i] = p.elevation ?: 0.0
        }

        // Linear interpolation for missing gaps if quality is partially reconstructed
        if (elevationQuality.source == ElevationSource.PARTIALLY_RECONSTRUCTED) {
            var lastValidIdx = -1
            for (i in 0 until n) {
                if (deduplicated[i].elevation != null) {
                    if (lastValidIdx != -1 && i - lastValidIdx > 1) {
                        val startEle = resolvedElevations[lastValidIdx]
                        val endEle = resolvedElevations[i]
                        val step = (endEle - startEle) / (i - lastValidIdx)
                        for (k in (lastValidIdx + 1) until i) {
                            resolvedElevations[k] = startEle + step * (k - lastValidIdx)
                        }
                    }
                    lastValidIdx = i
                }
            }
        }

        // 4. Calculate distances and gradients
        val segmentDistances = DoubleArray(n)
        val cumulativeDistances = DoubleArray(n)
        val rawGradients = DoubleArray(n)

        var totalDist = 0.0
        segmentDistances[0] = 0.0
        cumulativeDistances[0] = 0.0
        rawGradients[0] = 0.0

        for (i in 1 until n) {
            val prev = deduplicated[i - 1]
            val curr = deduplicated[i]

            val segDist = if (prev.segmentIndex == curr.segmentIndex) {
                GeoMath.haversineDistanceMeters(
                    prev.latitude, prev.longitude,
                    curr.latitude, curr.longitude
                )
            } else {
                // Segment boundary transition: avoid huge jump
                0.0
            }

            segmentDistances[i] = segDist
            totalDist += segDist
            cumulativeDistances[i] = totalDist

            if (elevationQuality.isReliableForSimulation && segDist >= 1.0) {
                val eleDiff = resolvedElevations[i] - resolvedElevations[i - 1]
                rawGradients[i] = GeoMath.calculateGradient(eleDiff, segDist)
            } else {
                rawGradients[i] = 0.0
            }
        }

        // 5. Smooth gradients across a rolling window
        val smoothedGradients = GeoMath.smoothGradients(rawGradients, windowRadius = 3)

        // 6. Recalculate robust elevation gain / loss from smoothed elevations
        val smoothedElevations = if (elevationQuality.isReliableForSimulation) {
            GeoMath.smoothElevations(resolvedElevations, windowRadius = 2)
        } else {
            resolvedElevations
        }

        var gain = 0.0
        var loss = 0.0
        for (i in 1 until n) {
            val diff = smoothedElevations[i] - smoothedElevations[i - 1]
            if (diff > 0.4) gain += diff
            else if (diff < -0.4) loss += abs(diff)
        }

        val hasOriginalTimestamps = deduplicated.any { it.timestampEpochMillis != null }

        // 7. Construct final RoutePoint list
        val routePoints = ArrayList<RoutePoint>(n)
        for (i in 0 until n) {
            val raw = deduplicated[i]
            routePoints.add(
                RoutePoint(
                    index = i,
                    latitude = raw.latitude,
                    longitude = raw.longitude,
                    elevation = smoothedElevations[i],
                    elevationSource = elevationQuality.source,
                    segmentIndex = raw.segmentIndex,
                    segmentDistanceMeters = segmentDistances[i],
                    distanceFromStartMeters = cumulativeDistances[i],
                    rawGradient = rawGradients[i],
                    smoothedGradient = smoothedGradients[i],
                    sourceTimestampEpochMillis = raw.timestampEpochMillis
                )
            )
        }

        return RouteTrack(
            name = parsedData.trackName,
            points = routePoints,
            totalDistanceMeters = totalDist,
            elevationGainMeters = gain,
            elevationLossMeters = loss,
            minElevationMeters = if (elevationQuality.isReliableForSimulation) smoothedElevations.minOrNull() ?: 0.0 else 0.0,
            maxElevationMeters = if (elevationQuality.isReliableForSimulation) smoothedElevations.maxOrNull() ?: 0.0 else 0.0,
            elevationQuality = elevationQuality.copy(
                elevationGainMeters = gain,
                elevationLossMeters = loss
            ),
            bounds = RouteBounds(
                minLatitude = if (minLat == Double.MAX_VALUE) 0.0 else minLat,
                maxLatitude = if (maxLat == -Double.MAX_VALUE) 0.0 else maxLat,
                minLongitude = if (minLon == Double.MAX_VALUE) 0.0 else minLon,
                maxLongitude = if (maxLon == -Double.MAX_VALUE) 0.0 else maxLon
            ),
            segmentCount = parsedData.segmentCount,
            hasOriginalTimestamps = hasOriginalTimestamps
        )
    }
}
