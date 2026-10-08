package com.stravakalcer.gpx

import kotlin.math.*

object GeoMath {
    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calculates great-circle distance between two coordinates in meters using Haversine formula.
     */
    fun haversineDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2.0).pow(2.0) +
                cos(rLat1) * cos(rLat2) * sin(dLon / 2.0).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates gradient in percentage: (elevationDelta / distance) * 100
     */
    fun calculateGradient(elevationDeltaMeters: Double, distanceMeters: Double): Double {
        if (distanceMeters < 0.5) return 0.0
        val grad = (elevationDeltaMeters / distanceMeters) * 100.0
        // Clamp extreme GPS noise gradient between -40% and +40%
        return grad.coerceIn(-40.0, 40.0)
    }

    fun mpsToKmh(mps: Double): Double = mps * 3.6

    fun kmhToMps(kmh: Double): Double = kmh / 3.6

    fun mpsToPaceSecondsPerKm(mps: Double): Double {
        if (mps < 0.2) return 3600.0 // Very slow / stopped upper bound
        return 1000.0 / mps
    }

    fun paceSecondsPerKmToMps(paceSecPerKm: Double): Double {
        if (paceSecPerKm <= 0.0) return 0.0
        return 1000.0 / paceSecPerKm
    }

    fun formatPace(paceSecondsPerKm: Double): String {
        if (paceSecondsPerKm <= 0.0 || paceSecondsPerKm > 3600.0) return "--:--"
        val totalSec = paceSecondsPerKm.roundToInt()
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return "%d:%02d".format(minutes, seconds)
    }

    fun parsePaceStringToSecondsPerKm(paceStr: String): Double? {
        val clean = paceStr.trim()
        val parts = clean.split(":")
        if (parts.size != 2) return null
        val min = parts[0].toIntOrNull() ?: return null
        val sec = parts[1].toIntOrNull() ?: return null
        if (min < 0 || sec < 0 || sec >= 60) return null
        return (min * 60 + sec).toDouble()
    }

    /**
     * Gaussian or moving-window smoother for elevations and gradients to eliminate micro-jitter
     * without flattening real climbs or descents.
     */
    fun smoothGradients(gradients: DoubleArray, windowRadius: Int = 3): DoubleArray {
        val n = gradients.size
        if (n <= 2) return gradients.clone()
        val result = DoubleArray(n)
        for (i in 0 until n) {
            val start = max(0, i - windowRadius)
            val end = min(n - 1, i + windowRadius)
            var sum = 0.0
            var count = 0
            for (j in start..end) {
                sum += gradients[j]
                count++
            }
            result[i] = if (count > 0) sum / count else gradients[i]
        }
        return result
    }

    /**
     * Smooths elevations using a moving average window.
     */
    fun smoothElevations(elevations: DoubleArray, windowRadius: Int = 2): DoubleArray {
        val n = elevations.size
        if (n <= 2) return elevations.clone()
        val result = DoubleArray(n)
        for (i in 0 until n) {
            val start = max(0, i - windowRadius)
            val end = min(n - 1, i + windowRadius)
            var sum = 0.0
            var count = 0
            for (j in start..end) {
                sum += elevations[j]
                count++
            }
            result[i] = if (count > 0) sum / count else elevations[i]
        }
        return result
    }
}
