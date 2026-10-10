package com.stravakalcer.model

data class HeartRateConfig(
    val enabled: Boolean = true,
    val restingHr: Int = 60,
    val maxHr: Int = 185,
    val lagTimeSeconds: Double = 12.0,
    val recoveryRate: Double = 0.85
) {
    // 5-zone boundaries calculated from Karvonen or % max HR
    val z1Max: Int get() = (restingHr + (maxHr - restingHr) * 0.59).toInt()
    val z2Max: Int get() = (restingHr + (maxHr - restingHr) * 0.71).toInt()
    val z3Max: Int get() = (restingHr + (maxHr - restingHr) * 0.81).toInt()
    val z4Max: Int get() = (restingHr + (maxHr - restingHr) * 0.90).toInt()
    val z5Max: Int get() = maxHr
}

data class CadenceConfig(
    val enabled: Boolean = true,
    val baseCadenceRpm: Int = 85,          // 60-120 RPM for cycling, 140-200 SPM for running
    val coastingCadenceRpm: Int = 0,        // Freewheeling coasting RPM on steep downhills
    val allowCoasting: Boolean = true,      // Whether cadence drops when descending
    val climbDropIntensity: Double = 1.0    // 0.0 (maintain base cadence) to 1.5 (heavier torque drop)
)

data class StopConfig(
    val distanceMeters: Double,
    val durationSeconds: Long,
    val reason: String = "Rest / Traffic Stop"
)

enum class SmartStopPreset(val title: String, val subtitle: String) {
    NONE("None (Non-stop)", "Continuous movement, 0 traffic stops"),
    LIGHT("Light (1-2 Stops)", "1-2 brief pauses (30-45s) at intersections"),
    CITY_TRAFFIC("City Traffic (3-5 Stops)", "3-5 red lights / stops (45-60s)")
}

data class SimulationSettings(
    val sport: SportType = SportType.CYCLING,
    val profile: ActivityProfile = ActivityProfile.ENDURANCE,
    val customParams: ProfileParameters? = null,
    val targetAverageSpeedKmh: Double? = 28.0,       // Cycling target
    val targetAveragePaceSecondsPerKm: Double? = 330.0, // Running target (5:30 min/km)
    val requestedMaxSpeedKmh: Double? = null,        // Cycling preferred max
    val requestedBestPaceSecondsPerKm: Double? = null, // Running preferred best pace
    val smartStopPreset: SmartStopPreset = SmartStopPreset.NONE,
    val stops: List<StopConfig> = emptyList(),
    val hrConfig: HeartRateConfig = HeartRateConfig(),
    val cadenceConfig: CadenceConfig = CadenceConfig(),
    val randomSeed: Long = 42L,
    val selectedDeviceProfileId: String = "garmin_edge_1040",
    val startEpochMillis: Long = System.currentTimeMillis()
) {
    val activeProfileParams: ProfileParameters
        get() = customParams ?: ProfileParameters.defaultsFor(profile)

    companion object {
        fun generateStopsForPreset(preset: SmartStopPreset, totalDistanceMeters: Double): List<StopConfig> {
            if (preset == SmartStopPreset.NONE || totalDistanceMeters < 1000.0) return emptyList()
            val count = when (preset) {
                SmartStopPreset.NONE -> 0
                SmartStopPreset.LIGHT -> if (totalDistanceMeters >= 12000.0) 2 else 1
                SmartStopPreset.CITY_TRAFFIC -> if (totalDistanceMeters >= 15000.0) 5 else 3
            }
            val list = ArrayList<StopConfig>(count)
            val step = totalDistanceMeters / (count + 1)
            for (i in 1..count) {
                val dist = step * i
                val duration = when (preset) {
                    SmartStopPreset.LIGHT -> 35L + (i % 2) * 10L
                    SmartStopPreset.CITY_TRAFFIC -> 45L + (i % 3) * 10L
                    SmartStopPreset.NONE -> 0L
                }
                list.add(StopConfig(distanceMeters = dist, durationSeconds = duration, reason = "Traffic Stop #$i"))
            }
            return list
        }
    }
}
