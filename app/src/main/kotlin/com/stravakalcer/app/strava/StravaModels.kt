package com.stravakalcer.app.strava

data class StravaAuthState(
    val isConnected: Boolean = false,
    val clientId: String = "",
    val clientSecret: String = "",
    val accessToken: String = "",
    val refreshToken: String = "",
    val expiresAtEpochSeconds: Long = 0L,
    val athleteName: String = "",
    val athleteUsername: String = "",
    val autoUploadEnabled: Boolean = false
)

data class StravaTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val athleteName: String,
    val athleteUsername: String
)

data class StravaUploadResult(
    val uploadId: Long,
    val status: String,
    val isReady: Boolean = false,
    val isDuplicate: Boolean = false,
    val activityId: Long? = null,
    val error: String? = null
)
