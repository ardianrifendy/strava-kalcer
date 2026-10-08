package com.stravakalcer.gpx

import com.stravakalcer.model.ElevationQuality
import com.stravakalcer.model.ElevationSource
import kotlin.math.abs

object ElevationClassifier {

    /**
     * Inspects raw elevations and produces a detailed ElevationQuality report.
     * Respects the core rule: Never silently treat missing/all-zero elevation as valid terrain.
     */
    fun classify(rawElevations: List<Double?>): ElevationQuality {
        val total = rawElevations.size
        if (total == 0) {
            return ElevationQuality(
                source = ElevationSource.UNAVAILABLE,
                description = "Empty route data. No elevation records found.",
                totalPoints = 0,
                validElevationPoints = 0,
                missingElevationPoints = 0,
                allZero = true,
                minElevationMeters = 0.0,
                maxElevationMeters = 0.0,
                elevationGainMeters = 0.0,
                elevationLossMeters = 0.0,
                isReliableForSimulation = false
            )
        }

        val presentElevations = rawElevations.filterNotNull()
        val missingCount = total - presentElevations.size
        val allZero = presentElevations.isNotEmpty() && presentElevations.all { abs(it) < 0.001 }

        if (presentElevations.isEmpty() || allZero) {
            return ElevationQuality(
                source = ElevationSource.UNAVAILABLE,
                description = if (allZero) "Elevation data is all-zero (flat GPS placeholder)."
                else "Elevation data is completely missing in GPX file.",
                totalPoints = total,
                validElevationPoints = presentElevations.size,
                missingElevationPoints = missingCount,
                allZero = allZero,
                minElevationMeters = 0.0,
                maxElevationMeters = 0.0,
                elevationGainMeters = 0.0,
                elevationLossMeters = 0.0,
                isReliableForSimulation = false
            )
        }

        val minEle = presentElevations.minOrNull() ?: 0.0
        val maxEle = presentElevations.maxOrNull() ?: 0.0

        // Calculate gain/loss with 1.0m threshold to prevent sensor jitter
        var gain = 0.0
        var loss = 0.0
        for (i in 1 until presentElevations.size) {
            val diff = presentElevations[i] - presentElevations[i - 1]
            if (diff > 0.5) gain += diff
            else if (diff < -0.5) loss += abs(diff)
        }

        val source = when {
            missingCount == 0 -> ElevationSource.ORIGINAL
            missingCount.toDouble() / total < 0.20 -> ElevationSource.PARTIALLY_RECONSTRUCTED
            else -> ElevationSource.UNAVAILABLE
        }

        val description = when (source) {
            ElevationSource.ORIGINAL -> "Valid original GPS elevation recorded in track."
            ElevationSource.PARTIALLY_RECONSTRUCTED -> "Partially interpolated elevation ($missingCount missing points)."
            ElevationSource.UNAVAILABLE -> "Elevation unreliable ($missingCount of $total points missing)."
            ElevationSource.RECONSTRUCTED -> "Reconstructed DEM elevation."
        }

        return ElevationQuality(
            source = source,
            description = description,
            totalPoints = total,
            validElevationPoints = presentElevations.size,
            missingElevationPoints = missingCount,
            allZero = false,
            minElevationMeters = minEle,
            maxElevationMeters = maxEle,
            elevationGainMeters = gain,
            elevationLossMeters = loss,
            isReliableForSimulation = source == ElevationSource.ORIGINAL || source == ElevationSource.PARTIALLY_RECONSTRUCTED
        )
    }
}
