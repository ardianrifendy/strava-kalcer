package com.stravakalcer.model

enum class SportType(val displayName: String, val fitSportId: Int) {
    CYCLING("Cycling", 2), // FIT Garmin Sport: CYCLING = 2
    RUNNING("Running", 1); // FIT Garmin Sport: RUNNING = 1

    val isSpeedBased: Boolean get() = this == CYCLING
    val isPaceBased: Boolean get() = this == RUNNING
}
