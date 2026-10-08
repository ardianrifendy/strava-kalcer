package com.stravakalcer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.device.DeviceCategory
import com.stravakalcer.device.DeviceProfile
import com.stravakalcer.device.DeviceProfileRegistry
import com.stravakalcer.model.SportType

@Composable
fun DeviceSelectionScreen(
    currentSport: SportType,
    selectedProfile: DeviceProfile,
    onDeviceSelected: (DeviceProfile) -> Unit,
    onProceedClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<DeviceCategory?>(null) }

    val filteredProfiles = remember(searchQuery, selectedCategory, currentSport) {
        DeviceProfileRegistry.filterProfiles(
            category = selectedCategory,
            sport = currentSport,
            searchQuery = searchQuery
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "SELECT DEVICE PROFILE",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose the recording characteristics and FIT header for your output file.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Garmin, Wahoo, COROS, Suunto...", color = TextTertiary, fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KalcerCyan,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All", fontSize = 12.sp) }
            )
            FilterChip(
                selected = selectedCategory == DeviceCategory.CYCLOCOMPUTER,
                onClick = { selectedCategory = DeviceCategory.CYCLOCOMPUTER },
                label = { Text("Bike Computers", fontSize = 12.sp) }
            )
            FilterChip(
                selected = selectedCategory == DeviceCategory.SPORTWATCH,
                onClick = { selectedCategory = DeviceCategory.SPORTWATCH },
                label = { Text("GPS Watches", fontSize = 12.sp) }
            )
            FilterChip(
                selected = selectedCategory == DeviceCategory.GENERIC,
                onClick = { selectedCategory = DeviceCategory.GENERIC },
                label = { Text("Generic", fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Device List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProfiles) { profile ->
                val isSelected = profile.id == selectedProfile.id
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
                        .clickable { onDeviceSelected(profile) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.manufacturer,
                                    color = if (isSelected) KalcerOrange else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${profile.category.displayName}",
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = profile.modelName,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = profile.description,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = KalcerOrange,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bottom CTA
        Button(
            onClick = onProceedClicked,
            colors = ButtonDefaults.buttonColors(containerColor = KalcerOrange),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "GENERATE & VALIDATE FIT",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}
