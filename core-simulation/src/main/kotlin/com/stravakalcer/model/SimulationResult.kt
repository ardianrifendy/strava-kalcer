package com.stravakalcer.model

import com.stravakalcer.gpx.GeoMath

data class SimulationResult(
    val settings: SimulationSettings,
    val points: List<SimulationPoint>,
    val totalDistanceMeters: Double,
    val movingTimeSeconds: Long,
    val totalTimeSeconds: Long,
    val averageMovingSpeedKmh: Double,
    val averageMovingPaceSecondsPerKm: Double,
    val averageMovingPaceFormatted: String = GeoMath.formatPace(averageMovingPaceSecondsPerKm),
    val maxSpeedKmh: Double,
    val bestPaceSecondsPerKm: Double,
    val bestPaceFormatted: String = GeoMath.formatPace(bestPaceSecondsPerKm),
    val averageHeartRate: Int?,
    val maxHeartRate: Int?,
    val averageCadence: Int?,
    val elevationGainMeters: Double,
    val elevationLossMeters: Double,
    val requestedTarget: String,
    val achievedTarget: String,
    val targetFeasible: Boolean,
    val feasibilityNote: String,
    val requestedMaxOrBest: String?,
    val achievedMaxOrBest: String?,
    val downhillCandidates: List<DownhillCandidate> = emptyList(),
    val selectedDownhillCandidate: DownhillCandidate? = null,
    val solverIterations: Int = 0
) {
    /**
     * Binary search to find simulation point matching a specific route distance.
     * Guaranteed single synchronized cursor across all charts and map.
     */
    fun findPointAtDistance(distanceMeters: Double): SimulationPoint? {
        if (points.isEmpty()) return null
        if (distanceMeters <= 0.0) return points.first()
        if (distanceMeters >= totalDistanceMeters) return points.last()

        var low = 0
        var high = points.size - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            val midDist = points[mid].distanceFromStartMeters
            when {
                midDist < distanceMeters -> low = mid + 1
                midDist > distanceMeters -> high = mid - 1
                else -> return points[mid]
            }
        }
        val idx = low.coerceIn(0, points.size - 1)
        return points[idx]
    }

    /**
     * Binary search to find simulation point matching an elapsed time in seconds.
     */
    fun findPointAtTime(totalTimeSec: Long): SimulationPoint? {
        if (points.isEmpty()) return null
        if (totalTimeSec <= 0) return points.first()
        if (totalTimeSec >= totalTimeSeconds) return points.last()

        var low = 0
        var high = points.size - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            val midTime = points[mid].totalTimeSeconds
            when {
                midTime < totalTimeSec -> low = mid + 1
                midTime > totalTimeSec -> high = mid - 1
                else -> return points[mid]
            }
        }
        val idx = low.coerceIn(0, points.size - 1)
        return points[idx]
    }
}
