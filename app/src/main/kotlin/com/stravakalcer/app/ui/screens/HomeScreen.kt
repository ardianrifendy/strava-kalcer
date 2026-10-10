package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*

@Composable
fun HomeScreen(
    stravaConnected: Boolean,
    onOpenStravaSettingsClicked: () -> Unit,
    onImportGpxClicked: () -> Unit,
    onLoadSampleClicked: () -> Unit,
    onOpenDebugClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "STRAVA KALCER",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Activity Simulation & FIT Engine",
                    color = KalcerCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenStravaSettingsClicked,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, if (stravaConnected) KalcerLime.copy(alpha = 0.5f) else DarkBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Strava Integration",
                        tint = if (stravaConnected) KalcerLime else KalcerOrange
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onOpenDebugClicked,
                    modifier = Modifier
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Debug Tools",
                        tint = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hero Action Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(16.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Recreate Your Ride or Run",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Transform any GPX route into a complete, coherent activity with gradient-driven physics, physiological heart rate, realistic cadence, and validated FIT export.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Primary CTA Button
                Button(
                    onClick = onImportGpxClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "IMPORT GPX ROUTE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Sample Route Button
                OutlinedButton(
                    onClick = onLoadSampleClicked,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = KalcerCyan),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(KalcerCyan.copy(alpha = 0.5f))),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = KalcerCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LOAD SAMPLE ROUTE (DEMO)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "KEY CAPABILITIES",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Feature cards
        FeatureCard(
            title = "Target Your Average",
            description = "Set your desired average speed or running pace. The engine distributes effort naturally across hills and flats instead of forcing constant velocity."
        )
        Spacer(modifier = Modifier.height(10.dp))
        FeatureCard(
            title = "Realistic Uphill & Downhill",
            description = "Speed decreases naturally on climbs and increases smoothly on descents with realistic kinematic acceleration and braking limits."
        )
        Spacer(modifier = Modifier.height(10.dp))
        FeatureCard(
            title = "Physiological HR & Cadence",
            description = "Simulates continuous heart rate with lag and recovery curves, plus cadence responsive to climbing gear and downhill coasting."
        )
        Spacer(modifier = Modifier.height(10.dp))
        FeatureCard(
            title = "One Synchronized Cursor",
            description = "Inspect any position along the route. Map, elevation, speed/pace, HR and cadence all update in lockstep."
        )
        Spacer(modifier = Modifier.height(10.dp))
        FeatureCard(
            title = "Validated FIT Export",
            description = "Select from extensible cyclocomputer and smartwatch profiles (Garmin, Wahoo, Coros, Suunto, etc.) with parse-back verification."
        )
    }
}

@Composable
private fun FeatureCard(title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}
