package com.stravakalcer.simulation

import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.model.DownhillCandidate
import com.stravakalcer.model.ProfileParameters
import kotlin.math.*

object RunningSimulationEngine {

    /**
     * Calculates baseline running speed (m/s) based on gradient and target pace effort.
     * Running pace physics (Minetti et al. cost of running vs gradient):
     * - Uphill slows pace roughly 12-18 sec/km per 1% gradient.
     * - Downhill increases pace up to -5%, then levels off due to braking impact.
     */
    fun computeTerrainSpeedMps(
        gradient: Double,
        baseFlatSpeedMps: Double,
        profileParams: ProfileParameters
    ): Double {
        val flatSpeed = baseFlatSpeedMps

        return when {
            // Climbing (positive gradient)
            gradient > 0.0 -> {
                // Slower pace -> lower speed
                // Each +1% adds ~3.5% energy cost
                val penalty = 1.0 / (1.0 + (gradient / 10.0) * profileParams.gradeSensitivity * 0.9)
                val climbSpeed = flatSpeed * penalty
                climbSpeed.coerceAtLeast(1.2) // ~13:50 min/km walk/crawl floor
            }
            // Gentle to moderate downhill (-1% to -6%)
            gradient in -6.0..-0.5 -> {
                val drop = abs(gradient)
                // Downhill speed boost up to ~22%
                val boost = 1.0 + (drop / 6.0) * 0.22 * profileParams.baseEffortFactor
                flatSpeed * boost
            }
            // Steep downhill (< -6%): braking biomechanics prevent reckless overspeed
            gradient < -6.0 -> {
                val boost = 1.0 + 0.22 * profileParams.baseEffortFactor
                // Mild deceleration due to eccentric quad braking
                val braking = (abs(gradient) - 6.0) * 0.02
                flatSpeed * max(1.05, boost - braking)
            }
            else -> flatSpeed
        }
    }

    /**
     * Applies acceleration and pace transition smoothing.
     * Humans cannot sprint instantly or change pace drastically across 10 meters.
     */
    fun smoothSpeeds(
        rawSpeedsMps: DoubleArray,
        distancesMeters: DoubleArray,
        profileParams: ProfileParameters
    ): DoubleArray {
        val n = rawSpeedsMps.size
        if (n <= 1) return rawSpeedsMps.clone()

        val smoothed = rawSpeedsMps.clone()
        val maxAccelMps2 = 0.45 * profileParams.accelerationResponsiveness // ~0.45 m/s^2 running acceleration limit
        val maxDecelMps2 = 0.90 // ~0.90 m/s^2 running braking limit

        for (i in 1 until n) {
            val dist = distancesMeters[i]
            if (dist > 0.1) {
                val prevV = smoothed[i - 1]
                val currentV = smoothed[i]
                if (currentV > prevV) {
                    val maxAllowedV = sqrt(prevV.pow(2.0) + 2.0 * maxAccelMps2 * dist)
                    smoothed[i] = min(currentV, maxAllowedV)
                }
            }
        }

        for (i in n - 2 downTo 0) {
            val dist = distancesMeters[i + 1]
            if (dist > 0.1) {
                val nextV = smoothed[i + 1]
                val currentV = smoothed[i]
                if (currentV > nextV) {
                    val maxAllowedV = sqrt(nextV.pow(2.0) + 2.0 * maxDecelMps2 * dist)
                    smoothed[i] = min(currentV, maxAllowedV)
                }
            }
        }

        return smoothed
    }

    /**
     * Applies requested best pace on the top downhill section smoothly.
     */
    fun applyBestPaceOpportunity(
        speedsMps: DoubleArray,
        candidate: DownhillCandidate,
        bestPaceSecondsPerKm: Double
    ) {
        val targetMps = GeoMath.paceSecondsPerKmToMps(bestPaceSecondsPerKm)
        val startIdx = candidate.startIndex
        val endIdx = candidate.endIndex
        val peakIdx = (startIdx + endIdx) / 2

        if (peakIdx !in speedsMps.indices) return

        speedsMps[peakIdx] = max(speedsMps[peakIdx], targetMps)

        for (i in startIdx until peakIdx) {
            val progress = (i - startIdx).toDouble() / (peakIdx - startIdx)
            val current = speedsMps[i]
            val ramp = current + (targetMps - current) * (progress * progress)
            speedsMps[i] = max(speedsMps[i], ramp)
        }

        for (i in (peakIdx + 1)..min(endIdx, speedsMps.size - 1)) {
            val progress = (i - peakIdx).toDouble() / (endIdx - peakIdx)
            val current = speedsMps[i]
            val ramp = targetMps - (targetMps - current) * (progress * 0.8)
            speedsMps[i] = max(speedsMps[i], ramp)
        }
    }
}
