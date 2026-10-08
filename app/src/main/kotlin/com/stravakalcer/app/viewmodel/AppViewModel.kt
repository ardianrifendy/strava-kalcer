package com.stravakalcer.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val cursorSimPoint: SimulationPoint? = null
)

class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Parses and processes an imported GPX stream on background IO thread.
     */
    fun importGpx(inputStream: InputStream) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadingMessage = "Parsing GPX route data...") }
            try {
                val (track, intelligence) = withContext(Dispatchers.Default) {
                    val parsed = GpxParser.parse(inputStream)
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
                        fitValidationReport = null
                    )
                }
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

    /**
     * Updates the canonical cursor across all graphs and map.
     */
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

    /**
     * Runs complete simulation pipeline off the main thread.
     */
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
                        fitValidationReport = null
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

    /**
     * Generates FIT binary file and executes parse-back validation.
     */
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
}
