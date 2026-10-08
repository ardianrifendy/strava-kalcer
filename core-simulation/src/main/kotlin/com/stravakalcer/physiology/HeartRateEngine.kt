package com.stravakalcer.physiology

import com.stravakalcer.model.HeartRateConfig
import com.stravakalcer.model.ProfileParameters
import com.stravakalcer.model.SportType
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

object HeartRateEngine {

    /**
     * Simulates continuous heart rate across the activity points using a first-order
     * physiological lag model with effort drive and recovery decay.
     *
     * dHR/dt = (HR_target(effort) - HR(t)) / tau
     *
     * Tau (lag) is longer for recovery than for onset of high effort, reflecting human physiology.
     */
    fun simulateHeartRateSeries(
        speedsMps: DoubleArray,
        gradients: DoubleArray,
        timeDeltasSec: DoubleArray,
        isStopped: BooleanArray,
        sport: SportType,
        profileParams: ProfileParameters,
        hrConfig: HeartRateConfig
    ): IntArray {
        val n = speedsMps.size
        val result = IntArray(n)
        if (n == 0 || !hrConfig.enabled) {
            return result
        }

        val restingHr = hrConfig.restingHr.toDouble()
        val maxHr = hrConfig.maxHr.toDouble()
        val hrReserve = maxHr - restingHr

        // Base steady-state HR baseline derived from profile effort
        val steadyEffortHr = restingHr + hrReserve * profileParams.targetHrPercentage
        var currentHr = steadyEffortHr * 0.90 // Start warmed up slightly below steady

        val baseSpeedForSport = if (sport == SportType.CYCLING) 7.5 else 3.0 // ~27 km/h cycling, ~5:33 min/km running

        for (i in 0 until n) {
            val dt = timeDeltasSec[i].coerceIn(0.5, 30.0)
            val stopped = isStopped[i]
            val speed = speedsMps[i]
            val grad = gradients[i]

            val targetHr: Double
            if (stopped) {
                // Recovery towards resting + warm delta
                targetHr = restingHr + hrReserve * 0.25
            } else {
                // Effort calculation combining speed ratio and gradient impact
                val speedRatio = (speed / baseSpeedForSport).coerceIn(0.5, 2.0)
                // Uphill adds cardiovascular load; downhill eases effort
                val gradeLoad = when {
                    grad > 0.0 -> (grad / 10.0) * 0.45 * profileParams.gradeSensitivity
                    grad < -2.0 -> (grad / 15.0) * 0.30
                    else -> 0.0
                }

                val instantaneousEffort = (speedRatio * 0.5 + gradeLoad + 0.5)
                val targetFraction = (profileParams.targetHrPercentage * instantaneousEffort).coerceIn(0.50, 0.94)
                targetHr = restingHr + hrReserve * targetFraction
            }

            // Asymmetric physiological time constants:
            // Raising HR takes ~10-15s (faster), Recovery takes ~25-40s (slower)
            val tau = if (targetHr >= currentHr) {
                hrConfig.lagTimeSeconds // e.g. 12s
            } else {
                hrConfig.lagTimeSeconds * 2.2 / hrConfig.recoveryRate // e.g. ~26s
            }

            // Exponential smoothing step: current = target + (current - target) * exp(-dt / tau)
            val alpha = 1.0 - exp(-dt / tau)
            currentHr += (targetHr - currentHr) * alpha

            // Clamp within physiological bounds
            currentHr = currentHr.coerceIn(restingHr, maxHr)
            result[i] = currentHr.toInt()
        }

        return result
    }
}
