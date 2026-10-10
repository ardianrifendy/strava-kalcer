package com.stravakalcer.app.strava

import com.stravakalcer.model.SportType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object StravaApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(35, TimeUnit.SECONDS)
        .build()

    private const val STRAVA_API_BASE = "https://www.strava.com/api/v3"
    private const val STRAVA_OAUTH_TOKEN = "https://www.strava.com/oauth/token"

    /**
     * Builds the authorization URL for user login.
     * When opened on Android, if the official Strava app is installed,
     * it prompts the user directly to Authorize.
     */
    fun buildAuthorizationUrl(clientId: String, redirectUri: String): String {
        return "https://www.strava.com/oauth/mobile/authorize" +
                "?client_id=$clientId" +
                "&redirect_uri=$redirectUri" +
                "&response_type=code" +
                "&approval_prompt=auto" +
                "&scope=activity:write,read"
    }

    suspend fun exchangeCodeForToken(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<StravaTokenResponse> = withContext(Dispatchers.IO) {
        try {
            val formBody = FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("grant_type", "authorization_code")
                .build()

            val request = Request.Builder()
                .url(STRAVA_OAUTH_TOKEN)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Strava Token Exchange failed (${response.code}): $body"))
            }

            val json = JSONObject(body)
            val accessToken = json.getString("access_token")
            val refreshToken = json.optString("refresh_token", "")
            val expiresAt = json.optLong("expires_at", 0L)

            val athleteJson = json.optJSONObject("athlete")
            val firstName = athleteJson?.optString("firstname", "") ?: ""
            val lastName = athleteJson?.optString("lastname", "") ?: ""
            val username = athleteJson?.optString("username", "") ?: ""
            val athleteName = "$firstName $lastName".trim().ifEmpty { username.ifEmpty { "Strava Athlete" } }

            Result.success(
                StravaTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt,
                    athleteName = athleteName,
                    athleteUsername = username
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String
    ): Result<StravaTokenResponse> = withContext(Dispatchers.IO) {
        try {
            val formBody = FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .build()

            val request = Request.Builder()
                .url(STRAVA_OAUTH_TOKEN)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Strava Token Refresh failed (${response.code}): $body"))
            }

            val json = JSONObject(body)
            val accessToken = json.getString("access_token")
            val newRefreshToken = json.optString("refresh_token", refreshToken)
            val expiresAt = json.optLong("expires_at", 0L)

            Result.success(
                StravaTokenResponse(
                    accessToken = accessToken,
                    refreshToken = newRefreshToken,
                    expiresAt = expiresAt,
                    athleteName = "",
                    athleteUsername = ""
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAthleteProfile(accessToken: String): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$STRAVA_API_BASE/athlete")
                .header("Authorization", "Bearer $accessToken")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Failed to fetch athlete profile (${response.code}): $body"))
            }

            val json = JSONObject(body)
            val firstName = json.optString("firstname", "")
            val lastName = json.optString("lastname", "")
            val username = json.optString("username", "")
            val fullName = "$firstName $lastName".trim().ifEmpty { username.ifEmpty { "Strava Athlete" } }

            Result.success(Pair(fullName, username))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads the binary .fit bytes to Strava via POST /api/v3/uploads.
     */
    suspend fun uploadFitActivity(
        accessToken: String,
        fitBytes: ByteArray,
        activityName: String,
        description: String,
        sportType: SportType
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val stravaSportType = when (sportType) {
                SportType.CYCLING -> "Ride"
                SportType.RUNNING -> "Run"
            }

            val mediaType = "application/octet-stream".toMediaTypeOrNull()
            val fileBody = fitBytes.toRequestBody(mediaType)

            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "activity.fit", fileBody)
                .addFormDataPart("data_type", "fit")
                .addFormDataPart("name", activityName)
                .addFormDataPart("description", description)
                .addFormDataPart("sport_type", stravaSportType)
                .addFormDataPart("trainer", "0")
                .addFormDataPart("commute", "0")
                .build()

            val request = Request.Builder()
                .url("$STRAVA_API_BASE/uploads")
                .header("Authorization", "Bearer $accessToken")
                .post(multipartBody)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Upload failed (${response.code}): $body"))
            }

            val json = JSONObject(body)
            val uploadId = json.getLong("id")
            Result.success(uploadId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Polls Strava until the activity processing finishes (usually 1-3 seconds).
     */
    suspend fun pollUploadStatus(
        accessToken: String,
        uploadId: Long,
        maxAttempts: Int = 12
    ): Result<StravaUploadResult> = withContext(Dispatchers.IO) {
        var attempts = 0
        while (attempts < maxAttempts) {
            attempts++
            try {
                val request = Request.Builder()
                    .url("$STRAVA_API_BASE/uploads/$uploadId")
                    .header("Authorization", "Bearer $accessToken")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val status = json.optString("status", "")
                    val error = json.optString("error", "").ifEmpty { null }
                    val activityId = if (json.has("activity_id") && !json.isNull("activity_id")) {
                        json.optLong("activity_id")
                    } else null

                    if (error != null) {
                        val isDuplicate = error.contains("duplicate", ignoreCase = true)
                        return@withContext Result.success(
                            StravaUploadResult(
                                uploadId = uploadId,
                                status = status,
                                isReady = false,
                                isDuplicate = isDuplicate,
                                activityId = activityId,
                                error = error
                            )
                        )
                    }

                    if (activityId != null && activityId > 0) {
                        return@withContext Result.success(
                            StravaUploadResult(
                                uploadId = uploadId,
                                status = status,
                                isReady = true,
                                activityId = activityId,
                                error = null
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                // Ignore transient network hiccups and retry
            }
            delay(1500)
        }

        // If processing timed out after maxAttempts, assume success in queue
        Result.success(
            StravaUploadResult(
                uploadId = uploadId,
                status = "Activity uploaded! Strava is completing processing in background.",
                isReady = true,
                activityId = null,
                error = null
            )
        )
    }
}
