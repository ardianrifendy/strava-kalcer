package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.intelligence.RouteIntelligenceSummary
import com.stravakalcer.model.*

@Composable
fun ActivitySettingsScreen(
    currentSettings: SimulationSettings,
    intelligence: RouteIntelligenceSummary?,
    onSettingsChanged: (SimulationSettings) -> Unit,
    onStartSimulationClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedSport by remember(currentSettings.sport) { mutableStateOf(currentSettings.sport) }
    var selectedProfile by remember(currentSettings.profile) { mutableStateOf(currentSettings.profile) }

    var targetSpeedKmh by remember(currentSettings.targetAverageSpeedKmh) {
        mutableStateOf(currentSettings.targetAverageSpeedKmh ?: 28.0)
    }
    var targetPaceSec by remember(currentSettings.targetAveragePaceSecondsPerKm) {
        mutableStateOf(currentSettings.targetAveragePaceSecondsPerKm ?: 330.0)
    }

    var requestedMaxSpeedKmh by remember(currentSettings.requestedMaxSpeedKmh) {
        mutableStateOf(currentSettings.requestedMaxSpeedKmh ?: 50.0)
    }
    var enableMaxSpeed by remember { mutableStateOf(currentSettings.requestedMaxSpeedKmh != null) }

    var hrEnabled by remember(currentSettings.hrConfig.enabled) { mutableStateOf(currentSettings.hrConfig.enabled) }
    var restingHr by remember(currentSettings.hrConfig.restingHr) { mutableStateOf(currentSettings.hrConfig.restingHr) }
    var maxHr by remember(currentSettings.hrConfig.maxHr) { mutableStateOf(currentSettings.hrConfig.maxHr) }

    var cadenceEnabled by remember(currentSettings.cadenceConfig.enabled) { mutableStateOf(currentSettings.cadenceConfig.enabled) }
    var baseCadence by remember(currentSettings.cadenceConfig.baseCadenceRpm) { mutableStateOf(currentSettings.cadenceConfig.baseCadenceRpm) }

    fun emitUpdate() {
        val updated = currentSettings.copy(
            sport = selectedSport,
            profile = selectedProfile,
            targetAverageSpeedKmh = if (selectedSport == SportType.CYCLING) targetSpeedKmh else null,
            targetAveragePaceSecondsPerKm = if (selectedSport == SportType.RUNNING) targetPaceSec else null,
            requestedMaxSpeedKmh = if (enableMaxSpeed && selectedSport == SportType.CYCLING) requestedMaxSpeedKmh else null,
            hrConfig = currentSettings.hrConfig.copy(
                enabled = hrEnabled,
                restingHr = restingHr,
                maxHr = maxHr
            ),
            cadenceConfig = currentSettings.cadenceConfig.copy(
                enabled = cadenceEnabled,
                baseCadenceRpm = baseCadence
            )
        )
        onSettingsChanged(updated)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "ACTIVITY SETTINGS",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Configure performance targets, style profiles, and sensors.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Sport Segmented Toggle
        Text(
            text = "SPORT DISCIPLINE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            val isCycling = selectedSport == SportType.CYCLING
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isCycling) KalcerOrange else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedSport = SportType.CYCLING
                        selectedProfile = ActivityProfile.ENDURANCE
                        baseCadence = 85
                        emitUpdate()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CYCLING",
                    color = if (isCycling) Color.White else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (!isCycling) KalcerOrange else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        selectedSport = SportType.RUNNING
                        selectedProfile = ActivityProfile.TEMPO_RUN
                        baseCadence = 165
                        emitUpdate()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RUNNING",
                    color = if (!isCycling) Color.White else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activity Profile Selector
        Text(
            text = "ACTIVITY PROFILE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val profiles = ActivityProfile.forSport(selectedSport)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            profiles.forEach { profile ->
                val isSelected = profile == selectedProfile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) DarkSurfaceVariant else DarkSurface,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) KalcerOrange else DarkBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedProfile = profile
                            emitUpdate()
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile.displayName,
                                color = if (isSelected) KalcerOrange else TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = profile.description,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Target Average Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                if (selectedSport == SportType.CYCLING) {
                    Text(
                        text = "TARGET AVERAGE SPEED",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${"%.1f".format(targetSpeedKmh)} km/h",
                            color = KalcerCyan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Preserves hills",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = targetSpeedKmh.toFloat(),
                        onValueChange = {
                            targetSpeedKmh = (it * 2).toInt() / 2.0
                            emitUpdate()
                        },
                        valueRange = 16f..45f,
                        colors = SliderDefaults.colors(
                            thumbColor = KalcerCyan,
                            activeTrackColor = KalcerCyan,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                } else {
                    Text(
                        text = "TARGET AVERAGE PACE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${GeoMath.formatPace(targetPaceSec)} min/km",
                            color = KalcerCyan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${"%.1f".format(GeoMath.mpsToKmh(GeoMath.paceSecondsPerKmToMps(targetPaceSec)))} km/h",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    Slider(
                        value = targetPaceSec.toFloat(),
                        onValueChange = {
                            targetPaceSec = it.toDouble()
                            emitUpdate()
                        },
                        valueRange = 210f..480f, // 3:30 to 8:00 min/km
                        colors = SliderDefaults.colors(
                            thumbColor = KalcerCyan,
                            activeTrackColor = KalcerCyan,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                }

                // Optional Max Speed goal (Cycling)
                if (selectedSport == SportType.CYCLING) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Requested Max Speed",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            intelligence?.bestDownhill?.let {
                                Text(
                                    text = "Route naturally supports ~${"%.0f".format(it.achievableMaxSpeedKmh)} km/h",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = enableMaxSpeed,
                            onCheckedChange = {
                                enableMaxSpeed = it
                                emitUpdate()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = KalcerOrange
                            )
                        )
                    }

                    if (enableMaxSpeed) {
                        Slider(
                            value = requestedMaxSpeedKmh.toFloat(),
                            onValueChange = {
                                requestedMaxSpeedKmh = it.toDouble()
                                emitUpdate()
                            },
                            valueRange = 35f..75f,
                            colors = SliderDefaults.colors(
                                thumbColor = KalcerOrange,
                                activeTrackColor = KalcerOrange,
                                inactiveTrackColor = DarkBorder
                            )
                        )
                        Text(
                            text = "Peak target: ${"%.0f".format(requestedMaxSpeedKmh)} km/h on best downhill",
                            color = KalcerOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sensors Section (Heart Rate & Cadence)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                // Heart Rate Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Heart Rate Simulation", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Continuous effort drive with physiological lag", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = hrEnabled,
                        onCheckedChange = {
                            hrEnabled = it
                            emitUpdate()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KalcerRed)
                    )
                }

                if (hrEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Resting: $restingHr bpm", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Max: $maxHr bpm", color = KalcerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = maxHr.toFloat(),
                        onValueChange = {
                            maxHr = it.toInt()
                            emitUpdate()
                        },
                        valueRange = 160f..210f,
                        colors = SliderDefaults.colors(
                            thumbColor = KalcerRed,
                            activeTrackColor = KalcerRed,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                }

                Divider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

                // Cadence Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Cadence Simulation", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (selectedSport == SportType.CYCLING) "RPM with downhill coasting drop" else "Running Steps Per Minute (SPM)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = cadenceEnabled,
                        onCheckedChange = {
                            cadenceEnabled = it
                            emitUpdate()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = KalcerLime)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // CTA: Run Simulation
        Button(
            onClick = {
                emitUpdate()
                onStartSimulationClicked()
            },
            colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "RUN RECONSTRUCTION & SIMULATION",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
