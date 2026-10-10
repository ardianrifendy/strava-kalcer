package com.stravakalcer.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stravakalcer.app.strava.*
import com.stravakalcer.device.DeviceProfile
import com.stravakalcer.device.DeviceProfileRegistry
import com.stravakalcer.fit.FitGenerator
import com.stravakalcer.fit.FitValidationReport
import com.stravakalcer.fit.FitValidator
import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.intelligence.RouteIntelligenceEngine
import com.stravakalcer.intelligence.RouteIntelligenceSummary
import com.stravakalcer.model.*
import com.stravakalcer.simulation.SimulationEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

data class StravaUploadProgress(
    val isUploading: Boolean = false,
    val message: String = "",
    val lastResult: StravaUploadResult? = null
)

data class AppUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String? = null,
    val currentTrack: RouteTrack? = null,
    val intelligence: RouteIntelligenceSummary? = null,
    val settings: SimulationSettings = SimulationSettings(),
    val simulationResult: SimulationResult? = null,
    val selectedDeviceProfile: DeviceProfile = DeviceProfileRegistry.getProfileById("garmin_edge_1040"),
    val fitBytes: ByteArray? = null,
    val fitValidationReport: FitValidationReport? = null,
    val cursorDistanceMeters: Double = 0.0,
    val cursorRoutePoint: RoutePoint? = null,
    val cursorSimPoint: SimulationPoint? = null,
    val stravaUploadProgress: StravaUploadProgress = StravaUploadProgress(),
    val showStravaConnectDialog: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    val stravaAuthManager = StravaAuthManager(application)
    val stravaAuthState: StateFlow<StravaAuthState> = stravaAuthManager.authState

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setStravaConnectDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showStravaConnectDialog = visible) }
    }

    fun clearStravaUploadResult() {
        _uiState.update { it.copy(stravaUploadProgress = StravaUploadProgress()) }
    }

    /**
     * Parses and processes imported GPX bytes on background thread.
     */
    fun importGpxBytes(bytes: ByteArray, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Parsing GPX route data...") }
            try {
                val (track, intelligence) = withContext(Dispatchers.Default) {
                    val parsed = GpxParser.parse(bytes)
                    val processed = RouteProcessor.process(parsed)
                    val intel = RouteIntelligenceEngine.analyze(processed)
                    Pair(processed, intel)
                }

                if (track.points.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "GPX file contains no valid GPS coordinates."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentTrack = track,
                        intelligence = intelligence,
                        cursorDistanceMeters = 0.0,
                        cursorRoutePoint = track.points.firstOrNull(),
                        simulationResult = null,
                        fitBytes = null,
                        fitValidationReport = null,
                        stravaUploadProgress = StravaUploadProgress()
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to parse GPX: ${e.message}"
                    )
                }
            }
        }
    }

    fun importGpx(inputStream: InputStream, onSuccess: () -> Unit = {}) {
        try {
            val bytes = inputStream.readBytes()
            importGpxBytes(bytes, onSuccess)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to read GPX stream: ${e.message}"
                )
            }
        }
    }

    fun setCursorDistance(distMeters: Double) {
        val state = _uiState.value
        val clampedDist = distMeters.coerceIn(0.0, state.currentTrack?.totalDistanceMeters ?: 0.0)
        val routePt = state.currentTrack?.findPointAtDistance(clampedDist)
        val simPt = state.simulationResult?.findPointAtDistance(clampedDist)

        _uiState.update {
            it.copy(
                cursorDistanceMeters = clampedDist,
                cursorRoutePoint = routePt,
                cursorSimPoint = simPt
            )
        }
    }

    fun updateSettings(newSettings: SimulationSettings) {
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun selectDeviceProfile(profile: DeviceProfile) {
        _uiState.update { it.copy(selectedDeviceProfile = profile) }
    }

    fun runSimulation() {
        val track = _uiState.value.currentTrack ?: return
        val settings = _uiState.value.settings

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Simulating physics & physiology...") }
            try {
                val result = withContext(Dispatchers.Default) {
                    SimulationEngine.simulate(track, settings)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        simulationResult = result,
                        cursorSimPoint = result.findPointAtDistance(it.cursorDistanceMeters),
                        fitBytes = null,
                        fitValidationReport = null,
                        stravaUploadProgress = StravaUploadProgress()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Simulation error: ${e.message}"
                    )
                }
            }
        }
    }

    fun generateAndValidateFit() {
        val result = _uiState.value.simulationResult ?: return
        val profile = _uiState.value.selectedDeviceProfile

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Generating and validating FIT file...") }
            try {
                val (bytes, report) = withContext(Dispatchers.Default) {
                    val rawBytes = FitGenerator.generateFitBytes(result, profile)
                    val valReport = FitValidator.validate(rawBytes, result)
                    Pair(rawBytes, valReport)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        fitBytes = bytes,
                        fitValidationReport = report
                    )
                }

                // Full auto: automatically trigger Strava cloud upload if enabled and connected!
                if (report.isValid && stravaAuthState.value.isConnected && stravaAuthState.value.autoUploadEnabled) {
                    uploadCurrentFitToStrava()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "FIT generation error: ${e.message}"
                    )
                }
            }
        }
    }

    // ==================== STRAVA CLOUD API INTEGRATION ====================

    fun handleStravaOAuthCallback(code: String) {
        val auth = stravaAuthState.value
        if (auth.clientId.isBlank() || auth.clientSecret.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Strava Client ID or Secret missing. Please enter them in settings.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Authorizing with Strava...") }
            val res = StravaApiClient.exchangeCodeForToken(auth.clientId, auth.clientSecret, code)
            _uiState.update { it.copy(isLoading = false) }

            if (res.isSuccess) {
                val tokenResp = res.getOrThrow()
                stravaAuthManager.saveTokens(
                    accessToken = tokenResp.accessToken,
                    refreshToken = tokenResp.refreshToken,
                    expiresAt = tokenResp.expiresAt,
                    athleteName = tokenResp.athleteName,
                    athleteUsername = tokenResp.athleteUsername
                )
                _uiState.update { it.copy(showStravaConnectDialog = false) }
            } else {
                _uiState.update { it.copy(errorMessage = "OAuth failed: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun connectWithManualToken(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Verifying Strava Token...") }
            val athleteRes = StravaApiClient.fetchAthleteProfile(token)
            _uiState.update { it.copy(isLoading = false) }

            if (athleteRes.isSuccess) {
                val (name, username) = athleteRes.getOrThrow()
                stravaAuthManager.saveTokens(
                    accessToken = token,
                    refreshToken = "",
                    expiresAt = 0L,
                    athleteName = name,
                    athleteUsername = username
                )
                _uiState.update { it.copy(showStravaConnectDialog = false) }
            } else {
                _uiState.update { it.copy(errorMessage = "Invalid Token: ${athleteRes.exceptionOrNull()?.message}") }
            }
        }
    }

    fun disconnectStrava() {
        stravaAuthManager.disconnect()
        _uiState.update {
            it.copy(
                showStravaConnectDialog = false,
                stravaUploadProgress = StravaUploadProgress()
            )
        }
    }

    fun toggleAutoUpload(enabled: Boolean) {
        stravaAuthManager.setAutoUploadEnabled(enabled)
    }

    fun uploadCurrentFitToStrava(customTitle: String? = null) {
        val bytes = _uiState.value.fitBytes ?: return
        val result = _uiState.value.simulationResult ?: return
        val profile = _uiState.value.selectedDeviceProfile
        val auth = stravaAuthState.value

        if (!auth.isConnected) {
            _uiState.update { it.copy(showStravaConnectDialog = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    stravaUploadProgress = StravaUploadProgress(
                        isUploading = true,
                        message = "Uploading FIT activity to Strava cloud..."
                    )
                )
            }

            var token = auth.accessToken

            // Refresh token if expired
            if (stravaAuthManager.isTokenExpired() && auth.refreshToken.isNotBlank() && auth.clientId.isNotBlank()) {
                val refreshRes = StravaApiClient.refreshAccessToken(auth.clientId, auth.clientSecret, auth.refreshToken)
                if (refreshRes.isSuccess) {
                    val newTok = refreshRes.getOrThrow()
                    stravaAuthManager.saveTokens(
                        accessToken = newTok.accessToken,
                        refreshToken = newTok.refreshToken,
                        expiresAt = newTok.expiresAt,
                        athleteName = auth.athleteName,
                        athleteUsername = auth.athleteUsername
                    )
                    token = newTok.accessToken
                }
            }

            val defaultTitle = "${result.settings.sport.name.lowercase().replaceFirstChar { it.uppercase() }} - Strava Kalcer"
            val title = customTitle?.ifBlank { defaultTitle } ?: defaultTitle
            val desc = "Reconstructed with Strava Kalcer • ${profile.manufacturer} ${profile.modelName}"

            val uploadRes = StravaApiClient.uploadFitActivity(
                accessToken = token,
                fitBytes = bytes,
                activityName = title,
                description = desc,
                sportType = result.settings.sport
            )

            if (uploadRes.isFailure) {
                val err = uploadRes.exceptionOrNull()?.message ?: "Upload failed"
                _uiState.update {
                    it.copy(
                        stravaUploadProgress = StravaUploadProgress(
                            isUploading = false,
                            message = "Upload failed: $err",
                            lastResult = StravaUploadResult(0L, "Failed", error = err)
                        )
                    )
                }
                return@launch
            }

            val uploadId = uploadRes.getOrThrow()
            _uiState.update {
                it.copy(
                    stravaUploadProgress = StravaUploadProgress(
                        isUploading = true,
                        message = "Processing activity on Strava..."
                    )
                )
            }

            val pollRes = StravaApiClient.pollUploadStatus(token, uploadId)
            val finalResult = pollRes.getOrDefault(
                StravaUploadResult(uploadId, "Activity processed", isReady = true)
            )

            _uiState.update {
                it.copy(
                    stravaUploadProgress = StravaUploadProgress(
                        isUploading = false,
                        message = if (finalResult.error != null) "Upload error: ${finalResult.error}" else "Activity successfully published to Strava!",
                        lastResult = finalResult
                    )
                )
            }
        }
    }
}
