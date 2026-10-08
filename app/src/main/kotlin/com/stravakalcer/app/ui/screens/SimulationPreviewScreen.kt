package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.app.ui.components.*
import com.stravakalcer.model.*

@Composable
fun SimulationPreviewScreen(
    track: RouteTrack,
    result: SimulationResult,
    cursorDistanceMeters: Double,
    cursorPoint: SimulationPoint?,
    onCursorMoved: (Double) -> Unit,
    onRegenerateClicked: () -> Unit,
    onProceedToDeviceClicked: () -> Unit,
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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SIMULATION PREVIEW",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${result.settings.profile.displayName} • ${result.settings.sport.displayName}",
                    color = KalcerCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onRegenerateClicked,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "TWEAK", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Feasibility Banner
        val bannerBg = if (result.targetFeasible) DarkSurfaceVariant else DarkSurfaceVariant
        val bannerBorder = if (result.targetFeasible) KalcerLime.copy(alpha = 0.5f) else KalcerAmber.copy(alpha = 0.5f)
        val bannerTextColor = if (result.targetFeasible) KalcerLime else KalcerAmber

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bannerBg, RoundedCornerShape(10.dp))
                .border(1.dp, bannerBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = if (result.targetFeasible) "TARGET ACHIEVED" else "FEASIBILITY LIMIT",
                        color = bannerTextColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Target: ${result.requestedTarget} → Result: ${result.achievedTarget}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.feasibilityNote,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Summary Metric Grid
        val isCycling = result.settings.sport == SportType.CYCLING
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                label = "Distance",
                value = "%.1f".format(result.totalDistanceMeters / 1000.0),
                unit = "km",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Moving Time",
                value = formatTime(result.movingTimeSeconds),
                unit = "",
                accentColor = KalcerCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = if (isCycling) "Avg Speed" else "Avg Pace",
                value = if (isCycling) "%.1f".format(result.averageMovingSpeedKmh) else result.averageMovingPaceFormatted,
                unit = if (isCycling) "km/h" else "min/km",
                accentColor = KalcerOrange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                label = if (isCycling) "Max Speed" else "Best Pace",
                value = if (isCycling) "%.1f".format(result.maxSpeedKmh) else result.bestPaceFormatted,
                unit = if (isCycling) "km/h" else "min/km",
                accentColor = KalcerOrange,
                modifier = Modifier.weight(1f)
            )
            if (result.averageHeartRate != null) {
                MetricCard(
                    label = "Avg HR",
                    value = "${result.averageHeartRate}",
                    unit = "bpm",
                    accentColor = ChartHeartRate,
                    modifier = Modifier.weight(1f)
                )
            }
            if (result.averageCadence != null) {
                MetricCard(
                    label = if (isCycling) "Avg Cadence" else "Avg SPM",
                    value = "${result.averageCadence}",
                    unit = if (isCycling) "rpm" else "spm",
                    accentColor = ChartCadence,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Synchronized Map
        Text(
            text = "SYNCHRONIZED ROUTE MAP",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        SynchronizedMapCanvas(
            track = track,
            cursorPoint = cursorPoint?.let {
                RoutePoint(
                    index = it.index,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    elevation = it.elevation,
                    elevationSource = it.elevationSource,
                    distanceFromStartMeters = it.distanceFromStartMeters
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Synchronized Elevation Chart
        val elePoints = result.points.map { ChartPoint(it.distanceFromStartMeters, it.elevation) }
        SynchronizedChartCanvas(
            title = "Elevation Profile",
            unit = "m",
            dataPoints = elePoints,
            totalDistanceMeters = result.totalDistanceMeters,
            cursorDistanceMeters = cursorDistanceMeters,
            currentValueAtCursor = cursorPoint?.elevation,
            accentColor = ChartElevation,
            onCursorMoved = onCursorMoved
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Synchronized Speed or Pace Chart
        if (isCycling) {
            val speedPoints = result.points.map { ChartPoint(it.distanceFromStartMeters, it.speedKmh) }
            SynchronizedChartCanvas(
                title = "Cycling Speed",
                unit = "km/h",
                dataPoints = speedPoints,
                totalDistanceMeters = result.totalDistanceMeters,
                cursorDistanceMeters = cursorDistanceMeters,
                currentValueAtCursor = cursorPoint?.speedKmh,
                accentColor = ChartSpeed,
                onCursorMoved = onCursorMoved
            )
        } else {
            val pacePoints = result.points.map { ChartPoint(it.distanceFromStartMeters, it.paceSecondsPerKm / 60.0) }
            SynchronizedChartCanvas(
                title = "Running Pace",
                unit = "min/km",
                dataPoints = pacePoints,
                totalDistanceMeters = result.totalDistanceMeters,
                cursorDistanceMeters = cursorDistanceMeters,
                currentValueAtCursor = cursorPoint?.let { it.paceSecondsPerKm / 60.0 },
                accentColor = ChartSpeed,
                onCursorMoved = onCursorMoved
            )
        }

        // Synchronized Heart Rate Chart (if enabled)
        if (result.settings.hrConfig.enabled) {
            Spacer(modifier = Modifier.height(12.dp))
            val hrPoints = result.points.mapNotNull { p ->
                p.heartRate?.let { ChartPoint(p.distanceFromStartMeters, it.toDouble()) }
            }
            SynchronizedChartCanvas(
                title = "Heart Rate",
                unit = "bpm",
                dataPoints = hrPoints,
                totalDistanceMeters = result.totalDistanceMeters,
                cursorDistanceMeters = cursorDistanceMeters,
                currentValueAtCursor = cursorPoint?.heartRate?.toDouble(),
                accentColor = ChartHeartRate,
                onCursorMoved = onCursorMoved
            )
        }

        // Synchronized Cadence Chart (if enabled)
        if (result.settings.cadenceConfig.enabled) {
            Spacer(modifier = Modifier.height(12.dp))
            val cadPoints = result.points.mapNotNull { p ->
                p.cadence?.let { ChartPoint(p.distanceFromStartMeters, it.toDouble()) }
            }
            SynchronizedChartCanvas(
                title = if (isCycling) "Cadence (RPM)" else "Cadence (SPM)",
                unit = if (isCycling) "rpm" else "spm",
                dataPoints = cadPoints,
                totalDistanceMeters = result.totalDistanceMeters,
                cursorDistanceMeters = cursorDistanceMeters,
                currentValueAtCursor = cursorPoint?.cadence?.toDouble(),
                accentColor = ChartCadence,
                onCursorMoved = onCursorMoved
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CTA: Select Device & Export
        Button(
            onClick = onProceedToDeviceClicked,
            colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "SELECT DEVICE & EXPORT FIT",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        "%d:%02d:%02d".format(hrs, mins, secs)
    } else {
        "%02d:%02d".format(mins, secs)
    }
}
