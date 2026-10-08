package com.stravakalcer.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.stravakalcer.app.theme.*
import com.stravakalcer.model.RoutePoint
import com.stravakalcer.model.RouteTrack
import kotlin.math.max
import kotlin.math.min

@Composable
fun SynchronizedMapCanvas(
    track: RouteTrack,
    cursorPoint: RoutePoint?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(DarkSurface, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        val points = track.points
        if (points.isEmpty()) return@Box

        val bounds = track.bounds
        val latRange = max(0.0001, bounds.maxLatitude - bounds.minLatitude)
        val lonRange = max(0.0001, bounds.maxLongitude - bounds.minLongitude)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Calculate scaling preserving aspect ratio
            val padding = 20f
            val availW = w - padding * 2
            val availH = h - padding * 2

            val scale = min(availW / lonRange.toFloat(), availH / latRange.toFloat())
            val offsetX = padding + (availW - lonRange.toFloat() * scale) / 2f
            val offsetY = padding + (availH - latRange.toFloat() * scale) / 2f

            fun project(lat: Double, lon: Double): Offset {
                val x = offsetX + (lon - bounds.minLongitude).toFloat() * scale
                val y = offsetY + (bounds.maxLatitude - lat).toFloat() * scale
                return Offset(x, y)
            }

            // Draw route polyline
            val path = Path()
            val first = project(points.first().latitude, points.first().longitude)
            path.moveTo(first.x, first.y)

            // Downsample rendering points if track is huge to maintain 60 FPS
            val step = max(1, points.size / 500)
            for (i in 1 until points.size step step) {
                val pt = project(points[i].latitude, points[i].longitude)
                path.lineTo(pt.x, pt.y)
            }
            val last = project(points.last().latitude, points.last().longitude)
            path.lineTo(last.x, last.y)

            // Polyline glow and stroke
            drawPath(
                path = path,
                color = KalcerCyan.copy(alpha = 0.3f),
                style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = path,
                color = KalcerCyan,
                style = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Start point (Green)
            drawCircle(color = KalcerLime, radius = 6f, center = first)
            drawCircle(color = Color.White, radius = 3f, center = first)

            // Finish point (Orange)
            drawCircle(color = KalcerOrange, radius = 6f, center = last)
            drawCircle(color = Color.White, radius = 3f, center = last)

            // Active Cursor position (White Glowing Pin)
            if (cursorPoint != null) {
                val cursorOffset = project(cursorPoint.latitude, cursorPoint.longitude)
                // Outer glow
                drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 14f, center = cursorOffset)
                // Ring
                drawCircle(color = KalcerOrange, radius = 8f, center = cursorOffset)
                // Center pin
                drawCircle(color = Color.White, radius = 4f, center = cursorOffset)
            }
        }
    }
}
