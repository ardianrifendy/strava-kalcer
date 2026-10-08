package com.stravakalcer.physiology

import com.stravakalcer.model.CadenceConfig
import com.stravakalcer.model.ProfileParameters
import com.stravakalcer.model.SportType
import kotlin.math.exp

object CadenceEngine {

    /**
     * Simulates continuous cadence correlated with speed, gradient, and coasting states.
     * Fully customizable via CadenceConfig:
     * - baseCadenceRpm (custom target RPM or SPM)
     * - allowCoasting & coastingCadenceRpm
     * - climbDropIntensity (0.0 = fixed cadence, 1.0 = realistic torque drop)
     */
    fun simulateCadenceSeries(
        speedsMps: DoubleArray,
        gradients: DoubleArray,
        timeDeltasSec: DoubleArray,
        isStopped: BooleanArray,
        sport: SportType,
        profileParams: ProfileParameters,
        cadenceConfig: CadenceConfig
    ): IntArray {
        val n = speedsMps.size
        val result = IntArray(n)
        if (n == 0 || !cadenceConfig.enabled) {
            return result
        }

        var currentCadence = cadenceConfig.baseCadenceRpm.toDouble()

        for (i in 0 until n) {
            val dt = timeDeltasSec[i].coerceIn(0.5, 30.0)
            val stopped = isStopped[i]
            val speed = speedsMps[i]
            val grad = gradients[i]

            val targetCadence: Double
            if (stopped || speed < 0.5) {
                targetCadence = 0.0
            } else if (sport == SportType.CYCLING) {
                // Cycling cadence physics:
                if (cadenceConfig.allowCoasting && grad <= profileParams.coastingThresholdGradient) {
                    // Coasting on steep downhill (freewheeling)
                    targetCadence = cadenceConfig.coastingCadenceRpm.toDouble()
                } else if (grad > 3.0) {
                    // Climbing: cadence drops according to custom climbDropIntensity
                    val climbPenalty = ((grad - 3.0) * 2.2 * cadenceConfig.climbDropIntensity).coerceIn(0.0, 35.0)
                    val minAllowed = if (cadenceConfig.climbDropIntensity > 0.0) 50.0 else cadenceConfig.baseCadenceRpm.toDouble()
                    targetCadence = (cadenceConfig.baseCadenceRpm - climbPenalty).coerceIn(minAllowed, 125.0)
                } else {
                    // Flat / rolling: responsive to speed cadence curve
                    val speedFactor = ((speed - 7.0) * 1.2).coerceIn(-8.0, 12.0)
                    targetCadence = (cadenceConfig.baseCadenceRpm + speedFactor).coerceIn(55.0, 130.0)
                }
            } else {
                // Running cadence SPM physics:
                val paceSecPerKm = 1000.0 / speed
                val base = cadenceConfig.baseCadenceRpm.toDouble().coerceIn(130.0, 215.0)
                // Faster pace slightly increases SPM, slower pace slightly reduces SPM
                val paceOffset = (paceSecPerKm - 330.0) * 0.10
                val spm = base - paceOffset
                targetCadence = spm.coerceIn(base - 22.0, base + 22.0).coerceIn(120.0, 220.0)
            }

            // Smooth transition to target cadence with exponential moving response
            val tau = if (targetCadence < 5.0) 2.0 else 5.0 // Dropping to 0 is faster (braking/coasting)
            val alpha = 1.0 - exp(-dt / tau)
            currentCadence += (targetCadence - currentCadence) * alpha

            result[i] = currentCadence.toInt().coerceAtLeast(0)
        }

        return result
    }
}
