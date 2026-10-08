package com.stravakalcer.model

data class DownhillCandidate(
    val id: Int,
    val startIndex: Int,
    val endIndex: Int,
    val startDistanceMeters: Double,
    val endDistanceMeters: Double,
    val lengthMeters: Double,
    val averageGradient: Double,
    val minGradient: Double, // Steepest negative %
    val elevationDropMeters: Double,
    val score: Double, // Calculated potential for high speed
    val achievableMaxSpeedKmh: Double,
    val reason: String
)
