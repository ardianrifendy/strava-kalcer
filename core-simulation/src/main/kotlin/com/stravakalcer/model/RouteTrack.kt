package com.stravakalcer.model

data class RouteBounds(
    val minLatitude: Double,
    val maxLatitude: Double,
    val minLongitude: Double,
    val maxLongitude: Double
) {
    val centerLatitude: Double get() = (minLatitude + maxLatitude) / 2.0
    val centerLongitude: Double get() = (minLongitude + maxLongitude) / 2.0
}

data class RouteTrack(
    val name: String,
    val points: List<RoutePoint>,
    val totalDistanceMeters: Double,
    val elevationGainMeters: Double,
    val elevationLossMeters: Double,
    val minElevationMeters: Double,
    val maxElevationMeters: Double,
    val elevationQuality: ElevationQuality,
    val bounds: RouteBounds,
    val segmentCount: Int = 1,
    val hasOriginalTimestamps: Boolean = false
) {
    fun findPointAtDistance(distanceMeters: Double): RoutePoint? {
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
}
