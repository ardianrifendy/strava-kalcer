package com.stravakalcer.physiology

import com.stravakalcer.model.CadenceConfig
import com.stravakalcer.model.ProfileParameters
import com.stravakalcer.model.SportType
import kotlin.math.abs
import kotlin.math.exp

object CadenceEngine {

    /**
     * Simulates continuous cadence correlated with speed, gradient, and coasting states.
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
                // Coasting on steep downhill
                if (grad <= profileParams.coastingThresholdGradient) {
                    // Coasting: cadence drops to coasting rpm (0 to 20 rpm freewheeling)
                    targetCadence = cadenceConfig.coastingCadenceRpm.toDouble()
                } else if (grad > 3.0) {
                    // Climbing: cadence drops slightly into grinding torque range
                    val climbPenalty = ((grad - 3.0) * 2.2).coerceIn(0.0, 25.0)
                    targetCadence = (cadenceConfig.baseCadenceRpm - climbPenalty).coerceIn(60.0, 110.0)
                } else {
                    // Flat / rolling: responsive to speed cadence curve
                    val speedFactor = ((speed - 7.0) * 1.5).coerceIn(-10.0, 15.0)
                    targetCadence = (cadenceConfig.baseCadenceRpm + speedFactor).coerceIn(75.0, 115.0)
                }
            } else {
                // Running cadence SPM physics:
                // Slower pace (e.g. 6:00 min/km = 2.77 mps) -> ~156 SPM
                // Fast pace (e.g. 4:00 min/km = 4.16 mps) -> ~176 SPM
                val paceSecPerKm = 1000.0 / speed
                // Cadence formula: 180 - (paceSecPerKm - 300) * 0.12
                val spm = 180.0 - (paceSecPerKm - 300.0) * 0.12
                targetCadence = spm.coerceIn(140.0, 205.0)
            }

            // Smooth transition to target cadence
            val tau = if (targetCadence < 5.0) 2.0 else 5.0 // Dropping to 0 is faster (braking/coasting)
            val alpha = 1.0 - exp(-dt / tau)
            currentCadence += (targetCadence - currentCadence) * alpha

            result[i] = currentCadence.toInt()
        }

        return result
    }
}
