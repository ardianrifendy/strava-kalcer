package com.stravakalcer.app.strava

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StravaAuthManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("strava_auth_prefs", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow(loadCurrentState())
    val authState: StateFlow<StravaAuthState> = _authState.asStateFlow()

    private fun loadCurrentState(): StravaAuthState {
        val accessToken = prefs.getString(KEY_ACCESS_TOKEN, "") ?: ""
        val clientId = prefs.getString(KEY_CLIENT_ID, "") ?: ""
        val clientSecret = prefs.getString(KEY_CLIENT_SECRET, "") ?: ""
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, "") ?: ""
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        val athleteName = prefs.getString(KEY_ATHLETE_NAME, "") ?: ""
        val athleteUsername = prefs.getString(KEY_ATHLETE_USERNAME, "") ?: ""
        val autoUpload = prefs.getBoolean(KEY_AUTO_UPLOAD, true)

        return StravaAuthState(
            isConnected = accessToken.isNotBlank(),
            clientId = clientId,
            clientSecret = clientSecret,
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAtEpochSeconds = expiresAt,
            athleteName = athleteName,
            athleteUsername = athleteUsername,
            autoUploadEnabled = autoUpload
        )
    }

    fun saveAppCredentials(clientId: String, clientSecret: String) {
        prefs.edit()
            .putString(KEY_CLIENT_ID, clientId.trim())
            .putString(KEY_CLIENT_SECRET, clientSecret.trim())
            .apply()
        _authState.value = loadCurrentState()
    }

    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiresAt: Long,
        athleteName: String,
        athleteUsername: String = ""
    ) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken.trim())
            .putString(KEY_REFRESH_TOKEN, refreshToken.trim())
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .putString(KEY_ATHLETE_NAME, athleteName.trim())
            .putString(KEY_ATHLETE_USERNAME, athleteUsername.trim())
            .apply()
        _authState.value = loadCurrentState()
    }

    fun setAutoUploadEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_UPLOAD, enabled).apply()
        _authState.value = loadCurrentState()
    }

    fun disconnect() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_EXPIRES_AT)
            .remove(KEY_ATHLETE_NAME)
            .remove(KEY_ATHLETE_USERNAME)
            .apply()
        _authState.value = loadCurrentState()
    }

    fun isTokenExpired(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        if (expiresAt == 0L) return false // manual token without expiry
        val currentEpoch = System.currentTimeMillis() / 1000
        return currentEpoch >= (expiresAt - 120) // 2-minute safety window
    }

    companion object {
        private const val KEY_CLIENT_ID = "strava_client_id"
        private const val KEY_CLIENT_SECRET = "strava_client_secret"
        private const val KEY_ACCESS_TOKEN = "strava_access_token"
        private const val KEY_REFRESH_TOKEN = "strava_refresh_token"
        private const val KEY_EXPIRES_AT = "strava_expires_at"
        private const val KEY_ATHLETE_NAME = "strava_athlete_name"
        private const val KEY_ATHLETE_USERNAME = "strava_athlete_username"
        private const val KEY_AUTO_UPLOAD = "strava_auto_upload"
    }
}
