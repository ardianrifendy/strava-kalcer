package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Sync
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
import com.stravakalcer.intelligence.RouteIntelligenceSummary
import com.stravakalcer.model.RoutePoint
import com.stravakalcer.model.RouteTrack

@Composable
fun RouteReviewScreen(
    track: RouteTrack,
    intelligence: RouteIntelligenceSummary?,
    cursorDistanceMeters: Double,
    cursorPoint: RoutePoint?,
    onCursorMoved: (Double) -> Unit,
    onReverseRouteClicked: () -> Unit = {},
    onConfirmRouteClicked: () -> Unit,
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
        // Track Name & Quality Badges
        Text(
            text = track.name,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevationSourceBadge(source = track.elevationQuality.source)
            StatusBadge(
                text = "${track.points.size} Points",
                color = TextSecondary
            )
            if (track.hasOriginalTimestamps) {
                StatusBadge(text = "GPS Timestamps", color = KalcerCyan)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary Metric Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricCard(
                label = "Distance",
                value = "%.1f".format(track.totalDistanceMeters / 1000.0),
                unit = "km",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Elev Gain",
                value = "+%.0f".format(track.elevationGainMeters),
                unit = "m",
                accentColor = KalcerLime,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Elev Loss",
                value = "-%.0f".format(track.elevationLossMeters),
                unit = "m",
                accentColor = KalcerCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Map Section
        Text(
            text = "ROUTE MAP",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        SynchronizedMapCanvas(
            track = track,
            cursorPoint = cursorPoint
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Elevation Profile Chart with Interactive Cursor
        Text(
            text = "ELEVATION PROFILE (DRAG TO INSPECT)",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        val chartPoints = track.points.map { ChartPoint(it.distanceFromStartMeters, it.elevation) }
        SynchronizedChartCanvas(
            title = "Elevation",
            unit = "m",
            dataPoints = chartPoints,
            totalDistanceMeters = track.totalDistanceMeters,
            cursorDistanceMeters = cursorDistanceMeters,
            currentValueAtCursor = cursorPoint?.elevation,
            accentColor = ChartElevation,
            onCursorMoved = onCursorMoved
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Cursor inspection readout card
        if (cursorPoint != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "POINT AT", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${"%.2f".format(cursorPoint.distanceFromStartMeters / 1000.0)} km",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ELEVATION", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${"%.1f".format(cursorPoint.elevation)} m",
                            color = KalcerCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "GRADIENT", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        val grad = cursorPoint.smoothedGradient
                        val gradColor = if (grad > 2.0) KalcerRed else if (grad < -2.0) KalcerLime else TextSecondary
                        Text(
                            text = "${"%.1f".format(grad)}%",
                            color = gradColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Route Intelligence Highlights Card
        if (intelligence != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "ROUTE INTELLIGENCE",
                        color = KalcerOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Terrain Split:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${"%.0f".format(intelligence.climbingPercentage)}% Climb / ${"%.0f".format(intelligence.flatPercentage)}% Flat / ${"%.0f".format(intelligence.downhillPercentage)}% Downhill",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    intelligence.hardestClimb?.let { climb ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Hardest Climb:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${climb.category} (+${"%.0f".format(climb.elevationGainMeters)}m @ ${"%.1f".format(climb.averageGradient)}%)",
                                color = KalcerAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    intelligence.bestDownhill?.let { downhill ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Best Downhill:", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = "${"%.1f".format(downhill.lengthMeters / 1000.0)} km @ ${"%.1f".format(downhill.averageGradient)}% (Up to ${"%.0f".format(downhill.achievableMaxSpeedKmh)} km/h)",
                                color = KalcerLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Confirmation & Reverse Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onReverseRouteClicked,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = KalcerCyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, KalcerCyan.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = KalcerCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REVERSE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KalcerCyan
                )
            }

            Button(
                onClick = onConfirmRouteClicked,
                colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(2f)
                    .height(50.dp)
            ) {
                Text(
                    text = "CONFIRM ROUTE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
            }
        }
    }
}
