package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.device.DeviceProfile
import com.stravakalcer.fit.FitValidationReport
import com.stravakalcer.model.SimulationResult

@Composable
fun ExportValidationScreen(
    result: SimulationResult,
    deviceProfile: DeviceProfile,
    fitBytes: ByteArray?,
    validationReport: FitValidationReport?,
    onSaveFitClicked: () -> Unit,
    onShareFitClicked: () -> Unit,
    onShareDebugCsvClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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

                    // Checklist items
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

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Share Buttons
        val canExport = validationReport?.isValid == true && fitBytes != null

        Button(
            onClick = onSaveFitClicked,
            enabled = canExport,
            colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE FIT FILE TO STORAGE",
                fontSize = 14.sp,
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
                .height(48.dp)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = if (canExport) KalcerCyan else TextTertiary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SHARE FIT VIA APPS",
                fontSize = 13.sp,
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
