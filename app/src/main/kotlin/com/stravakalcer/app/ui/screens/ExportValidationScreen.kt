package com.stravakalcer.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.strava.StravaAuthState
import com.stravakalcer.app.theme.*
import com.stravakalcer.app.viewmodel.StravaUploadProgress
import com.stravakalcer.device.DeviceProfile
import com.stravakalcer.fit.FitValidationReport
import com.stravakalcer.model.SimulationResult

@Composable
fun ExportValidationScreen(
    result: SimulationResult,
    deviceProfile: DeviceProfile,
    fitBytes: ByteArray?,
    validationReport: FitValidationReport?,
    stravaAuthState: StravaAuthState,
    stravaUploadProgress: StravaUploadProgress,
    activityTitle: String = "Morning Ride",
    activityDescription: String = "Reconstructed with Strava Kalcer",
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onUploadToStravaClicked: (title: String, description: String) -> Unit,
    onOpenStravaSettingsClicked: () -> Unit,
    onSaveFitClicked: () -> Unit,
    onShareFitClicked: () -> Unit,
    onShareDebugCsvClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val canExport = validationReport?.isValid == true && fitBytes != null
    val context = LocalContext.current

    var titleText by remember(activityTitle) { mutableStateOf(activityTitle) }
    var descText by remember(activityDescription) { mutableStateOf(activityDescription) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "FIT EXPORT & VALIDATION",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Every file is generated, reopened and validated for structure & consistency.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // File overview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "StravaKalcer_${result.settings.sport.name.lowercase()}.fit",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${deviceProfile.manufacturer} ${deviceProfile.modelName}",
                            color = KalcerCyan,
                            fontSize = 12.sp
                        )
                    }
                    if (fitBytes != null) {
                        Text(
                            text = "${fitBytes.size / 1024} KB",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Validation Results Card
        if (validationReport != null) {
            val isValid = validationReport.isValid
            val statusColor = if (isValid) KalcerLime else KalcerRed

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isValid) "FIT VALIDATION PASSED" else "VALIDATION FAILED",
                                color = statusColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isValid) "File complies with Garmin FIT protocol specifications."
                                else "Integrity discrepancies were detected.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    ValidationCheckItem("Header & CRC checksum integrity", isValid)
                    ValidationCheckItem("FileId, Activity, Session & Lap structure", validationReport.hasFileId && validationReport.hasSession)
                    ValidationCheckItem("Parsed record messages (${validationReport.totalRecordsParsed})", validationReport.totalRecordsParsed > 0)
                    ValidationCheckItem("Monotonic chronological timestamps", validationReport.timestampsMonotonic)
                    ValidationCheckItem("GPS coordinates inside geographical limits", validationReport.coordinatesValid)
                    ValidationCheckItem("Monotonic distance advancement", validationReport.distanceMonotonic)
                    ValidationCheckItem("Speed & altitude sanity bounds", validationReport.speedValid && validationReport.elevationValid)
                    ValidationCheckItem("Summary metric consistency with simulation", validationReport.summaryMetricsConsistent)

                    if (validationReport.errors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "ERRORS:", color = KalcerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        validationReport.errors.forEach { err ->
                            Text(text = "• $err", color = KalcerRed, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Strava Cloud Upload Section (Full Auto & Manual)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                .border(1.dp, KalcerOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = KalcerOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "STRAVA CLOUD UPLOAD",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onOpenStravaSettingsClicked, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Strava Settings",
                            tint = KalcerCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (stravaAuthState.isConnected) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = KalcerLime,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Connected as: ${stravaAuthState.athleteName.ifEmpty { "Strava Athlete" }}",
                            color = KalcerLime,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (stravaAuthState.autoUploadEnabled) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Full Auto Active",
                                color = KalcerCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Auto-upload via API (Note: Strava now requires a paid subscription to generate new API keys. If your account is free, use the 100% Free Web Upload below).",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                // Upload Progress & Result Messages
                if (stravaUploadProgress.isUploading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = KalcerOrange,
                        trackColor = DarkBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stravaUploadProgress.message,
                        color = KalcerOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else if (stravaUploadProgress.lastResult != null) {
                    val res = stravaUploadProgress.lastResult
                    Spacer(modifier = Modifier.height(10.dp))
                    if (res.isReady || res.activityId != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Activity published to Strava!",
                                color = KalcerLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(onClick = {
                                if (res.activityId != null) {
                                    val appUri = Uri.parse("strava://activities/${res.activityId}")
                                    val webUri = Uri.parse("https://www.strava.com/activities/${res.activityId}")
                                    try {
                                        val appIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(appIntent)
                                    } catch (_: Exception) {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                    }
                                } else {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.strava.com")))
                                }
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Open in Strava", color = KalcerCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = KalcerCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    } else if (res.error != null) {
                        Text(
                            text = if (res.isDuplicate) "Notice: This activity is already on your Strava." else "Error: ${res.error}",
                            color = if (res.isDuplicate) KalcerOrange else KalcerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (stravaAuthState.isConnected) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = titleText,
                        onValueChange = {
                            titleText = it
                            onTitleChanged(it)
                        },
                        label = { Text("Activity Title (Strava)", color = TextSecondary, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = KalcerOrange,
                            unfocusedBorderColor = DarkBorder,
                            cursorColor = KalcerOrange
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descText,
                        onValueChange = {
                            descText = it
                            onDescriptionChanged(it)
                        },
                        label = { Text("Activity Notes / Description", color = TextSecondary, fontSize = 11.sp) },
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = KalcerOrange,
                            unfocusedBorderColor = DarkBorder,
                            cursorColor = KalcerOrange
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onUploadToStravaClicked(titleText, descText) },
                        enabled = canExport && !stravaUploadProgress.isUploading,
                        colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (stravaUploadProgress.isUploading) "UPLOADING TO STRAVA..." else "UPLOAD DIRECTLY TO STRAVA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onOpenStravaSettingsClicked,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = KalcerOrange),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(KalcerOrange)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = KalcerOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CONNECT STRAVA (FULL AUTO)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Free Web Upload Helper Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                .border(1.dp, KalcerLime.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = KalcerLime,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cara Upload 100% Gratis (Tanpa Langganan & Tanpa API):",
                        color = KalcerLime,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Klik tombol 'SAVE FIT FILE' untuk menyimpan file ke HP.\n2. Klik tombol 'OPEN STRAVA WEB UPLOAD' di bawah.\n3. Pilih file .fit yang baru disimpan — selesai! Otomatis terdeteksi dengan nama device & metrik lengkap.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save & Share Buttons
        Button(
            onClick = onSaveFitClicked,
            enabled = canExport,
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE FIT FILE TO STORAGE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.strava.com/upload/select")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(webIntent)
                } catch (_: Exception) {}
            },
            enabled = canExport,
            colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "OPEN STRAVA WEB UPLOAD (100% FREE)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onShareFitClicked,
            enabled = canExport,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = KalcerCyan),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(if (canExport) KalcerCyan else DarkBorder)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = if (canExport) KalcerCyan else TextTertiary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SHARE FIT VIA APPS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onShareDebugCsvClicked,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = "EXPORT DIAGNOSTIC CSV",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ValidationCheckItem(label: String, passed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimary, fontSize = 12.sp)
        Text(
            text = if (passed) "PASS" else "FAIL",
            color = if (passed) KalcerLime else KalcerRed,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
