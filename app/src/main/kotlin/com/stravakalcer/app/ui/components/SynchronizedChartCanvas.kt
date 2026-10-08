package com.stravakalcer.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stravakalcer.app.theme.*
import kotlin.math.max
import kotlin.math.min

data class ChartPoint(
    val distanceMeters: Double,
    val value: Double
)

@Composable
fun SynchronizedChartCanvas(
    title: String,
    unit: String,
    dataPoints: List<ChartPoint>,
    totalDistanceMeters: Double,
    cursorDistanceMeters: Double,
    currentValueAtCursor: Double?,
    accentColor: Color,
    onCursorMoved: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(DarkSurface, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Title and active readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
                if (currentValueAtCursor != null) {
                    Text(
                        text = "${"%.1f".format(currentValueAtCursor)} $unit",
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (dataPoints.isEmpty() || totalDistanceMeters <= 0.0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available", color = TextTertiary, fontSize = 12.sp)
                }
                return@Column
            }

            val minVal = dataPoints.minOfOrNull { it.value } ?: 0.0
            val maxVal = dataPoints.maxOfOrNull { it.value } ?: 100.0
            val valRange = max(1.0, maxVal - minVal)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(totalDistanceMeters) {
                        detectTapGestures { offset ->
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            onCursorMoved(fraction.toDouble() * totalDistanceMeters)
                        }
                    }
                    .pointerInput(totalDistanceMeters) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                            onCursorMoved(fraction.toDouble() * totalDistanceMeters)
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Background horizontal guide lines (3 dashed rows)
                for (step in 1..3) {
                    val y = h * (step / 4f)
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                fun toCanvas(dist: Double, value: Double): Offset {
                    val x = (dist / totalDistanceMeters).toFloat().coerceIn(0f, 1f) * w
                    val y = h - ((value - minVal) / valRange).toFloat().coerceIn(0f, 1f) * (h * 0.85f) - (h * 0.07f)
                    return Offset(x, y)
                }

                // Construct line path & gradient area
                val path = Path()
                val areaPath = Path()

                val first = toCanvas(dataPoints.first().distanceMeters, dataPoints.first().value)
                path.moveTo(first.x, first.y)
                areaPath.moveTo(first.x, h)
                areaPath.lineTo(first.x, first.y)

                // Downsample for rendering smooth 60fps graph
                val stride = max(1, dataPoints.size / 300)
                for (i in 1 until dataPoints.size step stride) {
                    val pt = toCanvas(dataPoints[i].distanceMeters, dataPoints[i].value)
                    path.lineTo(pt.x, pt.y)
                    areaPath.lineTo(pt.x, pt.y)
                }
                val last = toCanvas(dataPoints.last().distanceMeters, dataPoints.last().value)
                path.lineTo(last.x, last.y)
                areaPath.lineTo(last.x, last.y)
                areaPath.lineTo(last.x, h)
                areaPath.close()

                // Draw filled gradient under curve
                drawPath(
                    path = areaPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                        startY = 0f,
                        endY = h
                    )
                )

                // Draw curve stroke
                drawPath(
                    path = path,
                    color = accentColor,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )

                // Synchronized cursor indicator
                val cursorFraction = (cursorDistanceMeters / totalDistanceMeters).toFloat().coerceIn(0f, 1f)
                val cursorX = cursorFraction * w

                // Vertical cursor line
                drawLine(
                    color = ChartCursor.copy(alpha = 0.8f),
                    start = Offset(cursorX, 0f),
                    end = Offset(cursorX, h),
                    strokeWidth = 2f
                )

                // Intersection point indicator dot
                if (currentValueAtCursor != null) {
                    val cursorPt = toCanvas(cursorDistanceMeters, currentValueAtCursor)
                    drawCircle(color = accentColor, radius = 6f, center = Offset(cursorX, cursorPt.y))
                    drawCircle(color = Color.White, radius = 3f, center = Offset(cursorX, cursorPt.y))
                }
            }
        }
    }
}
