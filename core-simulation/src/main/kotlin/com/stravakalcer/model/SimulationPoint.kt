package com.stravakalcer.model

import com.stravakalcer.gpx.GeoMath

data class SimulationPoint(
    val index: Int,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val elevationSource: ElevationSource,
    val gradient: Double,
    val rawGradient: Double,
    val distanceFromStartMeters: Double,
    val segmentDistanceMeters: Double,
    val speedMps: Double,
    val speedKmh: Double = GeoMath.mpsToKmh(speedMps),
    val paceSecondsPerKm: Double = GeoMath.mpsToPaceSecondsPerKm(speedMps),
    val paceFormatted: String = GeoMath.formatPace(paceSecondsPerKm),
    val heartRate: Int? = null,
    val cadence: Int? = null,
    val powerWatts: Int? = null,
    val temperatureCelsius: Int? = null,
    val timestampEpochMillis: Long,
    val movingTimeSeconds: Long,
    val totalTimeSeconds: Long,
    val isStopped: Boolean = false,
    val stopDurationSeconds: Long = 0L,
    val stopReason: String? = null,
    val baselineSpeedKmh: Double = 0.0,
    val profileAdjustmentKmh: Double = 0.0,
    val targetAdjustmentKmh: Double = 0.0,
    val explainReason: String = "CRUISING"
)
