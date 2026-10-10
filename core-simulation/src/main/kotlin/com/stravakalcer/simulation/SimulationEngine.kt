package com.stravakalcer.simulation

import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.intelligence.RouteIntelligenceEngine
import com.stravakalcer.model.*
import com.stravakalcer.physiology.CadenceEngine
import com.stravakalcer.physiology.HeartRateEngine
import kotlin.math.max
import kotlin.math.roundToLong

object SimulationEngine {

    /**
     * Executes complete activity reconstruction and simulation pipeline.
     * Guaranteed deterministic output given same track, settings, and seed.
     */
    fun simulate(
        track: RouteTrack,
        settings: SimulationSettings
    ): SimulationResult {
        val points = track.points
        if (points.isEmpty()) {
            return createEmptyResult(settings)
        }

        // 1. Analyze route intelligence to find downhill candidates
        val intelligence = RouteIntelligenceEngine.analyze(track)
        val selectedDownhill = intelligence.bestDownhill

        // 2. Solve target average speed/pace
        val solverResult = TargetSolver.solve(track, settings, selectedDownhill)
        val solvedSpeedsMps = solverResult.solvedSpeedsMps

        val n = points.size
        val isCycling = settings.sport == SportType.CYCLING
        val params = settings.activeProfileParams

        // 3. Map stops to track points
        val stopPoints = BooleanArray(n)
        val stopDurationsSec = LongArray(n)
        val stopReasons = arrayOfNulls<String>(n)

        for (stop in settings.stops) {
            val pt = track.findPointAtDistance(stop.distanceMeters)
            if (pt != null) {
                stopPoints[pt.index] = true
                stopDurationsSec[pt.index] = stop.durationSeconds
                stopReasons[pt.index] = stop.reason
            }
        }

        // 4. Construct chronological timeline, moving time, and total time with floating-point precision
        var currentEpochMillis = settings.startEpochMillis
        var cumulativeMovingTimeSec = 0.0
        var cumulativeTotalTimeSec = 0.0

        val timeDeltasSec = DoubleArray(n)
        val simulationPoints = ArrayList<SimulationPoint>(n)

        for (i in 0 until n) {
            val routePt = points[i]
            val segDist = routePt.segmentDistanceMeters
            val solvedSpeed = solvedSpeedsMps[i]
            val isStop = stopPoints[i]

            // Time taken to traverse the segment into point i:
            val moveStepSec = if (i == 0 || solvedSpeed < 0.2) 0.0 else segDist / solvedSpeed
            val stopStepSec = stopDurationsSec[i].toDouble()

            val stepDurationSec = moveStepSec + stopStepSec
            timeDeltasSec[i] = if (i == 0) 1.0 else max(1.0, stepDurationSec)

            cumulativeMovingTimeSec += moveStepSec
            cumulativeTotalTimeSec += stepDurationSec
            currentEpochMillis += (stepDurationSec * 1000.0).roundToLong()

            val speedMps = if (isStop) 0.0 else solvedSpeed

            // Reason code explainability
            val reason = when {
                isStop -> "STOP"
                isCycling && routePt.smoothedGradient <= params.coastingThresholdGradient -> "COASTING"
                routePt.smoothedGradient > 2.0 -> "UPHILL_SLOWDOWN"
                routePt.smoothedGradient < -2.0 -> "DOWNHILL_ACCELERATION"
                else -> "CRUISING"
            }

            simulationPoints.add(
                SimulationPoint(
                    index = i,
                    latitude = routePt.latitude,
                    longitude = routePt.longitude,
                    elevation = routePt.elevation,
                    elevationSource = routePt.elevationSource,
                    gradient = routePt.smoothedGradient,
                    rawGradient = routePt.rawGradient,
                    distanceFromStartMeters = routePt.distanceFromStartMeters,
                    segmentDistanceMeters = segDist,
                    speedMps = speedMps,
                    speedKmh = GeoMath.mpsToKmh(speedMps),
                    paceSecondsPerKm = GeoMath.mpsToPaceSecondsPerKm(speedMps),
                    timestampEpochMillis = currentEpochMillis,
                    movingTimeSeconds = cumulativeMovingTimeSec.roundToLong(),
                    totalTimeSeconds = cumulativeTotalTimeSec.roundToLong(),
                    isStopped = isStop,
                    stopDurationSeconds = stopDurationsSec[i],
                    stopReason = stopReasons[i],
                    baselineSpeedKmh = GeoMath.mpsToKmh(solvedSpeedsMps[i]),
                    explainReason = reason
                )
            )
        }

        // 5. Simulate continuous Heart Rate
        val hrSeries = HeartRateEngine.simulateHeartRateSeries(
            speedsMps = solvedSpeedsMps,
            gradients = DoubleArray(n) { points[it].smoothedGradient },
            timeDeltasSec = timeDeltasSec,
            isStopped = stopPoints,
            sport = settings.sport,
            profileParams = params,
            hrConfig = settings.hrConfig
        )

        // 6. Simulate continuous Cadence
        val cadenceSeries = CadenceEngine.simulateCadenceSeries(
            speedsMps = solvedSpeedsMps,
            gradients = DoubleArray(n) { points[it].smoothedGradient },
            timeDeltasSec = timeDeltasSec,
            isStopped = stopPoints,
            sport = settings.sport,
            profileParams = params,
            cadenceConfig = settings.cadenceConfig
        )

        // 7. Simulate continuous Power (Watts) and Ambient Temperature
        val powerSeries = IntArray(n)
        val tempSeries = IntArray(n)
        var totalWorkJoules = 0L
        val baseTemp = 27

        for (i in 0 until n) {
            val p = simulationPoints[i]
            val isCoasting = isCycling && p.gradient <= params.coastingThresholdGradient
            val watts = if (isCycling) {
                com.stravakalcer.physiology.PowerEngine.calculateCyclingPowerWatts(
                    speedMps = p.speedMps,
                    gradientPercent = p.gradient,
                    isStopped = p.isStopped,
                    isCoasting = isCoasting
                )
            } else {
                com.stravakalcer.physiology.PowerEngine.calculateRunningPowerWatts(
                    speedMps = p.speedMps,
                    gradientPercent = p.gradient,
                    isStopped = p.isStopped
                )
            }
            powerSeries[i] = watts

            val dt = if (i == 0) 1.0 else (p.totalTimeSeconds - simulationPoints[i - 1].totalTimeSeconds).toDouble().coerceIn(0.5, 30.0)
            if (!p.isStopped) {
                totalWorkJoules += (watts * dt).toLong()
            }

            val progress = if (n > 1) i.toDouble() / (n - 1) else 0.0
            tempSeries[i] = baseTemp + (progress * 2.0).toInt()
        }

        // 8. Attach HR, Cadence, Power, and Temperature to SimulationPoints
        val finalPoints = ArrayList<SimulationPoint>(n)
        for (i in 0 until n) {
            val p = simulationPoints[i]
            finalPoints.add(
                p.copy(
                    heartRate = if (settings.hrConfig.enabled) hrSeries[i] else null,
                    cadence = if (settings.cadenceConfig.enabled) cadenceSeries[i] else null,
                    powerWatts = powerSeries[i],
                    temperatureCelsius = tempSeries[i]
                )
            )
        }

        // 9. Calculate summary statistics
        val finalMovingTimeSec = max(1L, cumulativeMovingTimeSec.roundToLong())
        val finalTotalTimeSec = max(1L, cumulativeTotalTimeSec.roundToLong())
        val avgMovingSpeedKmh = if (cumulativeMovingTimeSec > 0.0) {
            (track.totalDistanceMeters / cumulativeMovingTimeSec) * 3.6
        } else 0.0
        val avgMovingPaceSec = if (track.totalDistanceMeters > 0.0) {
            cumulativeMovingTimeSec / (track.totalDistanceMeters / 1000.0)
        } else 0.0

        val maxSpeedKmh = finalPoints.maxOfOrNull { it.speedKmh } ?: 0.0
        val bestPaceSec = finalPoints.filter { it.speedMps > 0.5 }.minOfOrNull { it.paceSecondsPerKm } ?: 300.0

        val validHr = finalPoints.mapNotNull { it.heartRate }
        val avgHr = if (validHr.isNotEmpty()) validHr.average().toInt() else null
        val maxHr = validHr.maxOrNull()

        val validCadence = finalPoints.mapNotNull { it.cadence }
        val nonZeroCadence = finalPoints.filter { (it.cadence ?: 0) > 0 }.mapNotNull { it.cadence }
        val avgCadence = if (nonZeroCadence.isNotEmpty()) {
            nonZeroCadence.average().toInt()
        } else if (validCadence.isNotEmpty()) {
            validCadence.average().toInt()
        } else null

        val validPower = powerSeries.filter { it > 0 }
        val avgPower = if (validPower.isNotEmpty()) validPower.average().toInt() else null
        val maxPower = powerSeries.maxOrNull()
        val normalizedPower = if (validPower.isNotEmpty()) com.stravakalcer.physiology.PowerEngine.calculateNormalizedPower(powerSeries) else null
        val totalCalories = com.stravakalcer.physiology.PowerEngine.calculateCalories(totalWorkJoules, finalMovingTimeSec, avgHr, settings.sport)
        val avgTemp = tempSeries.average().toInt()
        val maxTemp = tempSeries.maxOrNull()

        return SimulationResult(
            settings = settings,
            points = finalPoints,
            totalDistanceMeters = track.totalDistanceMeters,
            movingTimeSeconds = finalMovingTimeSec,
            totalTimeSeconds = finalTotalTimeSec,
            averageMovingSpeedKmh = avgMovingSpeedKmh,
            averageMovingPaceSecondsPerKm = avgMovingPaceSec,
            maxSpeedKmh = maxSpeedKmh,
            bestPaceSecondsPerKm = bestPaceSec,
            averageHeartRate = avgHr,
            maxHeartRate = maxHr,
            averageCadence = avgCadence,
            averagePower = avgPower,
            maxPower = maxPower,
            normalizedPower = normalizedPower,
            totalWorkJoules = totalWorkJoules,
            totalCalories = totalCalories,
            averageTemperature = avgTemp,
            maxTemperature = maxTemp,
            elevationGainMeters = track.elevationGainMeters,
            elevationLossMeters = track.elevationLossMeters,
            requestedTarget = solverResult.requestedTargetText,
            achievedTarget = solverResult.achievedTargetText,
            targetFeasible = solverResult.targetFeasible,
            feasibilityNote = solverResult.feasibilityNote,
            requestedMaxOrBest = solverResult.requestedMaxOrBestText,
            achievedMaxOrBest = solverResult.achievedMaxOrBestText,
            downhillCandidates = intelligence.downhillCandidates,
            selectedDownhillCandidate = selectedDownhill,
            solverIterations = solverResult.iterations
        )
    }

    private fun createEmptyResult(settings: SimulationSettings): SimulationResult {
        return SimulationResult(
            settings = settings,
            points = emptyList(),
            totalDistanceMeters = 0.0,
            movingTimeSeconds = 0L,
            totalTimeSeconds = 0L,
            averageMovingSpeedKmh = 0.0,
            averageMovingPaceSecondsPerKm = 0.0,
            maxSpeedKmh = 0.0,
            bestPaceSecondsPerKm = 0.0,
            averageHeartRate = null,
            maxHeartRate = null,
            averageCadence = null,
            elevationGainMeters = 0.0,
            elevationLossMeters = 0.0,
            requestedTarget = "-",
            achievedTarget = "-",
            targetFeasible = false,
            feasibilityNote = "No points in route",
            requestedMaxOrBest = null,
            achievedMaxOrBest = null
        )
    }
}
