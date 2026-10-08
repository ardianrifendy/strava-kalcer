package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.app.ui.components.StatusBadge
import com.stravakalcer.model.SimulationResult

@Composable
fun DebugScreen(
    result: SimulationResult?,
    onExportCsvClicked: () -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEVELOPER DIAGNOSTICS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Physics solver state and point explainability",
                    color = KalcerCyan,
                    fontSize = 12.sp
                )
            }
            if (result != null) {
                IconButton(
                    onClick = onExportCsvClicked,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = "Export CSV", tint = KalcerCyan)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (result == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No simulation data available yet. Import a route and run simulation.", color = TextSecondary, fontSize = 13.sp)
            }
            return@Column
        }

        // Solver Diagnostics Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(text = "SOLVER ITERATION STATE", color = KalcerOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(8.dp))

                DebugRow("Solver Iterations", "${result.solverIterations}")
                DebugRow("Requested Target", result.requestedTarget)
                DebugRow("Achieved Target", result.achievedTarget)
                DebugRow("Target Feasible", if (result.targetFeasible) "YES" else "NO")
                DebugRow("Feasibility Note", result.feasibilityNote)
                result.requestedMaxOrBest?.let { DebugRow("Requested Max/Best", it) }
                result.achievedMaxOrBest?.let { DebugRow("Achieved Max/Best", it) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Downhill Candidate Diagnostics
        if (result.downhillCandidates.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "DOWNHILL HIGH-SPEED CANDIDATES", color = KalcerLime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    result.downhillCandidates.take(3).forEachIndexed { idx, candidate ->
                        val isSelected = candidate.id == result.selectedDownhillCandidate?.id
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "#${idx + 1} (${"%.1f".format(candidate.lengthMeters)}m @ ${"%.1f".format(candidate.averageGradient)}%)",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isSelected) {
                                    StatusBadge(text = "SELECTED", color = KalcerLime)
                                }
                            }
                            Text(
                                text = "Max potential: ${"%.0f".format(candidate.achievableMaxSpeedKmh)} km/h • Score: ${"%.1f".format(candidate.score)}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        if (idx < 2) Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Point Inspector
        var inspectIndex by remember { mutableStateOf(0) }
        val points = result.points
        val clampedIndex = inspectIndex.coerceIn(0, points.size - 1)
        val p = points[clampedIndex]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "POINT INSPECTOR (Index $clampedIndex of ${points.size})", color = KalcerCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    StatusBadge(text = p.explainReason, color = if (p.explainReason == "STOP") KalcerRed else KalcerCyan)
                }

                Slider(
                    value = clampedIndex.toFloat(),
                    onValueChange = { inspectIndex = it.toInt() },
                    valueRange = 0f..(points.size - 1).toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = KalcerCyan,
                        activeTrackColor = KalcerCyan,
                        inactiveTrackColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))
                DebugRow("Coordinates", "${"%.6f".format(p.latitude)}, ${"%.6f".format(p.longitude)}")
                DebugRow("Elevation / Source", "${"%.1f".format(p.elevation)} m (${p.elevationSource})")
                DebugRow("Smoothed Gradient", "${"%.2f".format(p.gradient)}% (raw: ${"%.2f".format(p.rawGradient)}%)")
                DebugRow("Distance from start", "${"%.1f".format(p.distanceFromStartMeters)} m")
                DebugRow("Speed / Pace", "${"%.2f".format(p.speedKmh)} km/h (${p.paceFormatted} min/km)")
                DebugRow("Baseline vs Final", "${"%.2f".format(p.baselineSpeedKmh)} → ${"%.2f".format(p.speedKmh)} km/h")
                DebugRow("Heart Rate", "${p.heartRate ?: "N/A"} bpm")
                DebugRow("Cadence", "${p.cadence ?: "N/A"}")
                DebugRow("Moving / Total Time", "${p.movingTimeSeconds}s / ${p.totalTimeSeconds}s")
                DebugRow("Stop State", if (p.isStopped) "STOPPED (${p.stopReason})" else "MOVING")
            }
        }
    }
}

@Composable
private fun DebugRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
