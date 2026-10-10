package com.stravakalcer.physiology

import com.stravakalcer.model.SportType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object PowerEngine {

    private const val RIDER_MASS_KG = 72.0
    private const val BIKE_MASS_KG = 8.5
    private const val TOTAL_MASS_KG = RIDER_MASS_KG + BIKE_MASS_KG
    private const val GRAVITY = 9.80665
    private const val CRR = 0.0042 // Rolling resistance coefficient on asphalt
    private const val CDA = 0.315  // Aerodynamic drag area (m^2)
    private const val AIR_DENSITY = 1.225 // kg/m^3
    private const val DRIVETRAIN_EFFICIENCY = 0.965 // 96.5% mechanical efficiency

    /**
     * Calculates instantaneous cycling mechanical power in Watts based on road cycling physics.
     * P_total = (P_gravity + P_rolling + P_air_drag) / efficiency
     */
    fun calculateCyclingPowerWatts(
        speedMps: Double,
        gradientPercent: Double,
        isStopped: Boolean,
        isCoasting: Boolean
    ): Int {
        if (isStopped || speedMps < 0.3 || isCoasting) return 0

        // Gravity: P_g = m * g * v * sin(theta) ~ m * g * v * (gradient / 100)
        val pGravity = TOTAL_MASS_KG * GRAVITY * speedMps * (gradientPercent / 100.0)

        // Rolling resistance: P_rr = Crr * m * g * v
        val pRolling = CRR * TOTAL_MASS_KG * GRAVITY * speedMps

        // Aerodynamic drag: P_drag = 0.5 * CdA * rho * v^3
        val pDrag = 0.5 * CDA * AIR_DENSITY * speedMps.pow(3.0)

        val rawPower = (pGravity + pRolling + pDrag) / DRIVETRAIN_EFFICIENCY

        // If descending or coasting with negative power demand, rider coasts (0 W)
        if (rawPower <= 0.0) return 0

        // Realistic bounds: 40 W to 950 W sprint
        return rawPower.coerceIn(40.0, 950.0).toInt()
    }

    /**
     * Calculates running power (Stryd mechanical power model ~ 1.04 W/kg per m/s on flat).
     */
    fun calculateRunningPowerWatts(
        speedMps: Double,
        gradientPercent: Double,
        isStopped: Boolean
    ): Int {
        if (isStopped || speedMps < 0.3) return 0

        val flatPower = RIDER_MASS_KG * speedMps * 1.04
        val gradeCost = RIDER_MASS_KG * GRAVITY * speedMps * (gradientPercent / 100.0)
        val rawPower = flatPower + gradeCost

        return rawPower.coerceIn(50.0, 650.0).toInt()
    }

    /**
     * Computes Normalized Power (NP) using standard Coggan 30s rolling 4th-power algorithm.
     */
    fun calculateNormalizedPower(powerSeries: IntArray): Int {
        if (powerSeries.isEmpty()) return 0
        val nonZero = powerSeries.filter { it > 0 }
        if (nonZero.isEmpty()) return 0

        // If activity is short, return average
        if (powerSeries.size < 30) return nonZero.average().toInt()

        // 30-second moving average window of power
        val windowSize = 30
        var windowSum = 0.0
        val smoothed = ArrayList<Double>(powerSeries.size)

        for (i in powerSeries.indices) {
            windowSum += powerSeries[i]
            if (i >= windowSize) {
                windowSum -= powerSeries[i - windowSize]
                smoothed.add(windowSum / windowSize)
            } else {
                smoothed.add(windowSum / (i + 1))
            }
        }

        // 4th power average
        val sumFourthPower = smoothed.sumOf { it.pow(4.0) }
        val avgFourth = sumFourthPower / smoothed.size
        return avgFourth.pow(0.25).toInt()
    }

    /**
     * Computes total calories burned (kcal) using Work (kJ) / gross metabolic efficiency (~24%).
     * 1 kJ mechanical work ~ 1 kcal metabolic cost.
     */
    fun calculateCalories(
        totalWorkJoules: Long,
        movingTimeSeconds: Long,
        avgHr: Int?,
        sport: SportType
    ): Int {
        if (totalWorkJoules > 0) {
            val workKj = totalWorkJoules / 1000.0
            return (workKj * 1.05).toInt().coerceAtLeast(10)
        }

        val hours = movingTimeSeconds / 3600.0
        val baseRate = if (sport == SportType.CYCLING) 550.0 else 680.0
        val hrMultiplier = if (avgHr != null && avgHr > 100) (avgHr / 140.0) else 1.0
        return (hours * baseRate * hrMultiplier).toInt().coerceAtLeast(10)
    }
}
