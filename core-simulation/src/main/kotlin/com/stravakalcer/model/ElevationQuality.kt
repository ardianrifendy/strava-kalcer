package com.stravakalcer.model

enum class ElevationSource {
    ORIGINAL,
    RECONSTRUCTED,
    PARTIALLY_RECONSTRUCTED,
    UNAVAILABLE
}

data class ElevationQuality(
    val source: ElevationSource,
    val description: String,
    val totalPoints: Int,
    val validElevationPoints: Int,
    val missingElevationPoints: Int,
    val allZero: Boolean,
    val minElevationMeters: Double,
    val maxElevationMeters: Double,
    val elevationGainMeters: Double,
    val elevationLossMeters: Double,
    val isReliableForSimulation: Boolean
)
