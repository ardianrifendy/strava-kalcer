package com.stravakalcer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*

@Composable
fun MetricCard(
    label: String,
    value: String,
    unit: String = "",
    modifier: Modifier = Modifier,
    accentColor: Color = KalcerOrange
) {
    Box(
        modifier = modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = label.uppercase(),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.Bottom) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}
