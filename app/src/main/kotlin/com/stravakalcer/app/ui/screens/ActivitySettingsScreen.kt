package com.stravakalcer.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.intelligence.RouteIntelligenceSummary
import com.stravakalcer.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ActivitySettingsScreen(
    currentSettings: SimulationSettings,
    intelligence: RouteIntelligenceSummary?,
    activityTitle: String = "Morning Ride",
    activityDescription: String = "Reconstructed with Strava Kalcer",
    onTitleChanged: (String) -> Unit = {},
    onDescriptionChanged: (String) -> Unit = {},
    onSettingsChanged: (SimulationSettings) -> Unit,
    onStartSimulationClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var startEpochMillis by remember(currentSettings.startEpochMillis) {
        mutableLongStateOf(currentSettings.startEpochMillis)
    }
    var selectedStopPreset by remember(currentSettings.smartStopPreset) {
        mutableStateOf(currentSettings.smartStopPreset)
    }

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
    var allowCoasting by remember(currentSettings.cadenceConfig.allowCoasting) { mutableStateOf(currentSettings.cadenceConfig.allowCoasting) }
    var coastingCadence by remember(currentSettings.cadenceConfig.coastingCadenceRpm) { mutableStateOf(currentSettings.cadenceConfig.coastingCadenceRpm) }
    var climbDropIntensity by remember(currentSettings.cadenceConfig.climbDropIntensity) { mutableStateOf(currentSettings.cadenceConfig.climbDropIntensity) }

    val dateFormatter = remember { SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale.getDefault()) }

    fun emitUpdate() {
        val updated = currentSettings.copy(
            sport = selectedSport,
            profile = selectedProfile,
            targetAverageSpeedKmh = if (selectedSport == SportType.CYCLING) targetSpeedKmh else null,
            targetAveragePaceSecondsPerKm = if (selectedSport == SportType.RUNNING) targetPaceSec else null,
            requestedMaxSpeedKmh = if (enableMaxSpeed && selectedSport == SportType.CYCLING) requestedMaxSpeedKmh else null,
            smartStopPreset = selectedStopPreset,
            startEpochMillis = startEpochMillis,
            hrConfig = currentSettings.hrConfig.copy(
                enabled = hrEnabled,
                restingHr = restingHr,
                maxHr = maxHr
            ),
            cadenceConfig = currentSettings.cadenceConfig.copy(
                enabled = cadenceEnabled,
                baseCadenceRpm = baseCadence,
                allowCoasting = allowCoasting,
                coastingCadenceRpm = coastingCadence,
                climbDropIntensity = climbDropIntensity
            )
        )
        onSettingsChanged(updated)
    }

    fun openDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = startEpochMillis }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val h = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)

            TimePickerDialog(context, { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startEpochMillis = newCal.timeInMillis
                emitUpdate()
            }, h, min, true).show()
        }, y, m, d).show()
    }

    fun setQuickTime(dayOffset: Int, hour: Int, minute: Int) {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        startEpochMillis = cal.timeInMillis
        emitUpdate()
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

        Spacer(modifier = Modifier.height(20.dp))

        // Activity Details & Strava Sync
        Text(
            text = "ACTIVITY DETAILS & STRAVA",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = activityTitle,
                    onValueChange = onTitleChanged,
                    label = { Text("Activity Title (Strava)", color = TextSecondary, fontSize = 12.sp) },
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

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = activityDescription,
                    onValueChange = onDescriptionChanged,
                    label = { Text("Activity Description / Notes", color = TextSecondary, fontSize = 12.sp) },
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
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Activity Start Date & Time
        Text(
            text = "ACTIVITY START DATE & TIME",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dateFormatter.format(Date(startEpochMillis)),
                            color = KalcerCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Determines activity date & morning/afternoon categorization on Strava",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            startEpochMillis = System.currentTimeMillis()
                            emitUpdate()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Now", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { setQuickTime(0, 6, 0) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Today 06:00", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { setQuickTime(-1, 6, 0) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Yest 06:00", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { openDatePicker() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KalcerCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = KalcerCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pick Custom Date & Time",
                        color = KalcerCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
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

        // Traffic Stops & Smart Pause
        Text(
            text = "TRAFFIC STOPS & SMART PAUSE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Red Light / Stop Simulation",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Simulates 0 km/h speed, 0 RPM cadence & heart rate recovery drop",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Traffic,
                        contentDescription = null,
                        tint = KalcerOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmartStopPreset.values().forEach { preset ->
                        val isSelected = selectedStopPreset == preset
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) KalcerOrange.copy(alpha = 0.15f) else DarkSurfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) KalcerOrange else DarkBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedStopPreset = preset
                                    emitUpdate()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.title,
                                color = if (isSelected) KalcerOrange else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = selectedStopPreset.subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
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

                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 12.dp))

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

                if (cadenceEnabled) {
                    Spacer(modifier = Modifier.height(14.dp))

                    val unit = if (selectedSport == SportType.CYCLING) "RPM" else "SPM"
                    val label = if (selectedSport == SportType.CYCLING) "Target Base Cadence" else "Target Step Cadence"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = label, color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "$baseCadence $unit",
                            color = KalcerLime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val minCadence = if (selectedSport == SportType.CYCLING) 60f else 145f
                    val maxCadence = if (selectedSport == SportType.CYCLING) 115f else 195f

                    Slider(
                        value = baseCadence.toFloat().coerceIn(minCadence, maxCadence),
                        onValueChange = {
                            baseCadence = it.toInt()
                            emitUpdate()
                        },
                        valueRange = minCadence..maxCadence,
                        colors = SliderDefaults.colors(
                            thumbColor = KalcerLime,
                            activeTrackColor = KalcerLime,
                            inactiveTrackColor = DarkBorder
                        )
                    )

                    val presets = if (selectedSport == SportType.CYCLING) {
                        listOf(
                            Pair(75, "75 (Grind)"),
                            Pair(85, "85 (Standard)"),
                            Pair(95, "95 (Spinner)"),
                            Pair(105, "105 (Crit)")
                        )
                    } else {
                        listOf(
                            Pair(155, "155 (Easy)"),
                            Pair(165, "165 (Base)"),
                            Pair(175, "175 (Optimal)"),
                            Pair(185, "185 (Fast)")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { (rpm, chipLabel) ->
                            val isSelected = baseCadence == rpm
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) KalcerLime.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) KalcerLime else DarkBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        baseCadence = rpm
                                        emitUpdate()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chipLabel,
                                    color = if (isSelected) KalcerLime else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    if (selectedSport == SportType.CYCLING) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Downhill Coasting",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (allowCoasting) "Drops to $coastingCadence RPM on steep descents" else "Pedals continuously on downhills",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = allowCoasting,
                                onCheckedChange = {
                                    allowCoasting = it
                                    emitUpdate()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = KalcerLime
                                )
                            )
                        }

                        if (allowCoasting) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val coastModes = listOf(
                                    Pair(0, "0 RPM (Freewheel)"),
                                    Pair(20, "20 RPM (Soft Spin)")
                                )
                                coastModes.forEach { (rpm, cLabel) ->
                                    val isSelected = coastingCadence == rpm
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) KalcerLime.copy(alpha = 0.15f) else DarkSurfaceVariant,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) KalcerLime else DarkBorder,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                coastingCadence = rpm
                                                emitUpdate()
                                            }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cLabel,
                                            color = if (isSelected) KalcerLime else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Climbing Cadence Adaptation",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val climbModes = listOf(
                                Pair(1.0, "Realistic Drop"),
                                Pair(0.5, "Light Drop"),
                                Pair(0.0, "Fixed RPM")
                            )
                            climbModes.forEach { (intensity, dropLabel) ->
                                val isSelected = climbDropIntensity == intensity
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) KalcerLime.copy(alpha = 0.15f) else DarkSurfaceVariant,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) KalcerLime else DarkBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            climbDropIntensity = intensity
                                            emitUpdate()
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dropLabel,
                                        color = if (isSelected) KalcerLime else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
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
