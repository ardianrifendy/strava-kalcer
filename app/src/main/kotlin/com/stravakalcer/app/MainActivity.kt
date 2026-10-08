package com.stravakalcer.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stravakalcer.app.navigation.Screen
import com.stravakalcer.app.theme.DarkBackground
import com.stravakalcer.app.theme.KalcerCyan
import com.stravakalcer.app.theme.KalcerOrange
import com.stravakalcer.app.theme.StravaKalcerTheme
import com.stravakalcer.app.ui.screens.*
import com.stravakalcer.app.viewmodel.AppViewModel
import com.stravakalcer.debug.DebugExporter
import java.io.ByteArrayInputStream

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StravaKalcerTheme {
                val navController = rememberNavController()
                val uiState by viewModel.uiState.collectAsState()

                // Storage Access Framework: GPX File Picker
                val gpxPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    uri?.let {
                        contentResolver.openInputStream(it)?.use { stream ->
                            viewModel.importGpx(stream)
                            navController.navigate(Screen.RouteReview.route)
                        }
                    }
                }

                // Storage Access Framework: FIT Save File Picker
                val fitSaveLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("application/octet-stream")
                ) { uri: Uri? ->
                    uri?.let {
                        uiState.fitBytes?.let { bytes ->
                            contentResolver.openOutputStream(it)?.use { out ->
                                out.write(bytes)
                                Toast.makeText(this, "FIT file saved successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // Share Sheet Helper
                fun shareFitFile(bytes: ByteArray) {
                    try {
                        val cacheFile = java.io.File(cacheDir, "StravaKalcer_${System.currentTimeMillis()}.fit")
                        cacheFile.writeBytes(bytes)
                        val uri = androidx.core.content.FileProvider.getUriForFile(
                            this,
                            "$packageName.fileprovider",
                            cacheFile
                        )
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/octet-stream"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(Intent.createChooser(intent, "Share FIT Activity"))
                    } catch (_: Exception) {
                        Toast.makeText(this, "Please save the FIT file to disk to export.", Toast.LENGTH_SHORT).show()
                    }
                }

                fun shareCsvFile(csvText: String) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, csvText)
                    }
                    startActivity(Intent.createChooser(intent, "Share Diagnostic CSV"))
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onImportGpxClicked = {
                                    gpxPickerLauncher.launch(arrayOf("*/*", "application/gpx+xml", "application/xml"))
                                },
                                onLoadSampleClicked = {
                                    val sampleGpx = com.stravakalcer.sample.SampleRouteGenerator.createRealisticGpx(120)
                                    viewModel.importGpx(ByteArrayInputStream(sampleGpx.toByteArray()))
                                    navController.navigate(Screen.RouteReview.route)
                                },
                                onOpenDebugClicked = {
                                    navController.navigate(Screen.Debug.route)
                                }
                            )
                        }

                        composable(Screen.RouteReview.route) {
                            val track = uiState.currentTrack
                            if (track != null) {
                                RouteReviewScreen(
                                    track = track,
                                    intelligence = uiState.intelligence,
                                    cursorDistanceMeters = uiState.cursorDistanceMeters,
                                    cursorPoint = uiState.cursorRoutePoint,
                                    onCursorMoved = { viewModel.setCursorDistance(it) },
                                    onConfirmRouteClicked = {
                                        navController.navigate(Screen.Settings.route)
                                    }
                                )
                            }
                        }

                        composable(Screen.Settings.route) {
                            ActivitySettingsScreen(
                                currentSettings = uiState.settings,
                                intelligence = uiState.intelligence,
                                onSettingsChanged = { viewModel.updateSettings(it) },
                                onStartSimulationClicked = {
                                    viewModel.runSimulation()
                                    navController.navigate(Screen.Preview.route)
                                }
                            )
                        }

                        composable(Screen.Preview.route) {
                            val track = uiState.currentTrack
                            val result = uiState.simulationResult
                            if (track != null && result != null) {
                                SimulationPreviewScreen(
                                    track = track,
                                    result = result,
                                    cursorDistanceMeters = uiState.cursorDistanceMeters,
                                    cursorPoint = uiState.cursorSimPoint,
                                    onCursorMoved = { viewModel.setCursorDistance(it) },
                                    onRegenerateClicked = {
                                        navController.navigate(Screen.Settings.route)
                                    },
                                    onProceedToDeviceClicked = {
                                        navController.navigate(Screen.DeviceSelect.route)
                                    }
                                )
                            }
                        }

                        composable(Screen.DeviceSelect.route) {
                            DeviceSelectionScreen(
                                currentSport = uiState.settings.sport,
                                selectedProfile = uiState.selectedDeviceProfile,
                                onDeviceSelected = { viewModel.selectDeviceProfile(it) },
                                onProceedClicked = {
                                    viewModel.generateAndValidateFit()
                                    navController.navigate(Screen.Export.route)
                                }
                            )
                        }

                        composable(Screen.Export.route) {
                            val result = uiState.simulationResult
                            if (result != null) {
                                ExportValidationScreen(
                                    result = result,
                                    deviceProfile = uiState.selectedDeviceProfile,
                                    fitBytes = uiState.fitBytes,
                                    validationReport = uiState.fitValidationReport,
                                    onSaveFitClicked = {
                                        val filename = "StravaKalcer_${result.settings.sport.name.lowercase()}_${System.currentTimeMillis()}.fit"
                                        fitSaveLauncher.launch(filename)
                                    },
                                    onShareFitClicked = {
                                        uiState.fitBytes?.let { bytes -> shareFitFile(bytes) }
                                    },
                                    onShareDebugCsvClicked = {
                                        val csv = DebugExporter.exportToCsv(result)
                                        shareCsvFile(csv)
                                    }
                                )
                            }
                        }

                        composable(Screen.Debug.route) {
                            DebugScreen(
                                result = uiState.simulationResult,
                                onExportCsvClicked = {
                                    uiState.simulationResult?.let {
                                        val csv = DebugExporter.exportToCsv(it)
                                        shareCsvFile(csv)
                                    }
                                }
                            )
                        }
                    }

                    // Background Loading Indicator
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = KalcerOrange)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = uiState.loadingMessage,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    // Error Alert Dialog
                    uiState.errorMessage?.let { error ->
                        AlertDialog(
                            onDismissRequest = { viewModel.clearError() },
                            title = { Text("Error") },
                            text = { Text(error) },
                            confirmButton = {
                                TextButton(onClick = { viewModel.clearError() }) {
                                    Text("OK", color = KalcerCyan)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
