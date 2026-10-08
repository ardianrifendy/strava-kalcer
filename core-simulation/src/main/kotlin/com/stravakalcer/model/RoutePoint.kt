package com.stravakalcer.model

data class RoutePoint(
    val index: Int,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val elevationSource: ElevationSource,
    val segmentIndex: Int = 0,
    val segmentDistanceMeters: Double = 0.0,
    val distanceFromStartMeters: Double = 0.0,
    val rawGradient: Double = 0.0,
    val smoothedGradient: Double = 0.0,
    val sourceTimestampEpochMillis: Long? = null
)
