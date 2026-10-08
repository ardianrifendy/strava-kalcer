package com.stravakalcer.simulation

import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.model.DownhillCandidate
import com.stravakalcer.model.ProfileParameters
import kotlin.math.*

object CyclingSimulationEngine {

    /**
     * Calculates baseline speed for a point based on gradient and effort.
     * Uses simplified physical bike resistance:
     * - Gravity resistance: F_g = m * g * grade
     * - Rolling resistance: F_rr = C_rr * m * g
     * - Aerodynamic drag: F_aero = 0.5 * rho * CdA * v^2
     *
     * Result is in meters per second (mps).
     */
    fun computeTerrainSpeedMps(
        gradient: Double,
        baseEffortMps: Double,
        profileParams: ProfileParameters
    ): Double {
        val flatSpeed = baseEffortMps

        return when {
            // Climbing (positive gradient)
            gradient > 0.0 -> {
                // Higher gradient drops speed progressively
                // At +3%: ~78% speed, at +6%: ~58% speed, at +10%: ~38% speed
                val gradePenalty = 1.0 / (1.0 + (gradient / 5.5) * profileParams.gradeSensitivity)
                val climbSpeed = flatSpeed * gradePenalty
                climbSpeed.coerceAtLeast(1.8) // Minimum crawling cycling speed ~6.5 km/h
            }
            // Descending (negative gradient)
            gradient < -0.5 -> {
                val dropPercent = abs(gradient)
                // Downhill gravity acceleration: terminal velocity cap around 22 m/s (80 km/h)
                val gravityBoostMps = sqrt(dropPercent) * 2.8 * (profileParams.baseEffortFactor * 0.9)
                val downhillSpeed = flatSpeed + gravityBoostMps
                downhillSpeed.coerceAtMost(22.2) // ~80 km/h physical cap
            }
            // Relatively flat (-0.5% to 0.0%)
            else -> flatSpeed
        }
    }

    /**
     * Applies dynamic acceleration and deceleration limits to ensure realistic, smooth transitions.
     * Prevents unnatural jumps like 27 -> 44 -> 29.
     */
    fun smoothSpeeds(
        rawSpeedsMps: DoubleArray,
        distancesMeters: DoubleArray,
        profileParams: ProfileParameters
    ): DoubleArray {
        val n = rawSpeedsMps.size
        if (n <= 1) return rawSpeedsMps.clone()

        val smoothed = rawSpeedsMps.clone()
        val maxAccelMps2 = 0.8 * profileParams.accelerationResponsiveness // ~0.8 m/s^2 max forward acceleration
        val maxDecelMps2 = 1.8 // ~1.8 m/s^2 braking deceleration

        // Forward pass: limit acceleration
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

        // Backward pass: ensure deceleration into steep corners / climbs is physically achievable
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

        // Enforce smooth point-to-point step continuity (max ~7 km/h jump between adjacent points)
        val maxStepMps = 2.0
        for (i in 1 until n) {
            val prev = smoothed[i - 1]
            val curr = smoothed[i]
            if (curr - prev > maxStepMps) {
                smoothed[i] = prev + maxStepMps
            } else if (prev - curr > maxStepMps) {
                smoothed[i] = prev - maxStepMps
            }
        }

        return smoothed
    }

    /**
     * Applies requested max speed specifically on the selected downhill candidate
     * through a natural acceleration ramp and smooth braking recovery.
     */
    fun applyMaxSpeedOpportunity(
        speedsMps: DoubleArray,
        distancesMeters: DoubleArray,
        candidate: DownhillCandidate,
        targetMaxKmh: Double
    ) {
        val targetMps = GeoMath.kmhToMps(targetMaxKmh)
        val startIdx = candidate.startIndex
        val endIdx = candidate.endIndex
        val peakIdx = (startIdx + endIdx) / 2

        if (peakIdx !in speedsMps.indices || peakIdx <= startIdx) return

        // Set peak speed at the sweet spot of the downhill
        speedsMps[peakIdx] = max(speedsMps[peakIdx], targetMps)

        // Smoothly ramp up towards the peak
        for (i in startIdx until peakIdx) {
            val progress = (i - startIdx).toDouble() / (peakIdx - startIdx)
            val current = speedsMps[i]
            val ramp = current + (targetMps - current) * (progress * progress)
            speedsMps[i] = max(speedsMps[i], ramp)
        }

        // Smoothly ramp down after the peak back to baseline
        for (i in (peakIdx + 1)..min(endIdx, speedsMps.size - 1)) {
            val progress = (i - peakIdx).toDouble() / max(1, endIdx - peakIdx)
            val current = speedsMps[i]
            val ramp = targetMps - (targetMps - current) * progress
            speedsMps[i] = max(speedsMps[i], ramp)
        }
    }
}
