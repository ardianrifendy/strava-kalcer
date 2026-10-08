package com.stravakalcer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import com.stravakalcer.model.ElevationSource

@Composable
fun StatusBadge(
    text: String,
    color: Color = KalcerCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ElevationSourceBadge(source: ElevationSource) {
    val (color, label) = when (source) {
        ElevationSource.ORIGINAL -> Pair(KalcerLime, "Original GPS Elevation")
        ElevationSource.PARTIALLY_RECONSTRUCTED -> Pair(KalcerAmber, "Interpolated Elevation")
        ElevationSource.RECONSTRUCTED -> Pair(KalcerCyan, "Reconstructed DEM")
        ElevationSource.UNAVAILABLE -> Pair(KalcerRed, "Elevation Unavailable")
    }
    StatusBadge(text = label, color = color)
}
