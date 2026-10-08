package com.stravakalcer.intelligence

import com.stravakalcer.model.DownhillCandidate
import com.stravakalcer.model.RouteTrack
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class ClimbSection(
    val startIndex: Int,
    val endIndex: Int,
    val startDistanceMeters: Double,
    val endDistanceMeters: Double,
    val lengthMeters: Double,
    val elevationGainMeters: Double,
    val averageGradient: Double,
    val maxGradient: Double,
    val category: String, // Cat 4, Cat 3, Cat 2, Cat 1, HC, or Uncategorized
    val difficultyScore: Double
)

data class RouteIntelligenceSummary(
    val totalDistanceMeters: Double,
    val elevationGainMeters: Double,
    val elevationLossMeters: Double,
    val flatDistanceMeters: Double,
    val climbingDistanceMeters: Double,
    val downhillDistanceMeters: Double,
    val flatPercentage: Double,
    val climbingPercentage: Double,
    val downhillPercentage: Double,
    val hardestClimb: ClimbSection?,
    val majorClimbs: List<ClimbSection>,
    val bestDownhill: DownhillCandidate?,
    val downhillCandidates: List<DownhillCandidate>,
    val estimatedMaxAchievableSpeedKmh: Double,
    val estimatedBestAchievablePaceSecondsPerKm: Double
)

object RouteIntelligenceEngine {

    /**
     * Performs comprehensive terrain intelligence on the processed route.
     * Extracts climbing sections, downhill high-speed opportunities, and gradient distribution.
     * Strictly avoids fabricating fake terrain.
     */
    fun analyze(track: RouteTrack): RouteIntelligenceSummary {
        val points = track.points
        if (points.size < 2) {
            return RouteIntelligenceSummary(
                totalDistanceMeters = track.totalDistanceMeters,
                elevationGainMeters = track.elevationGainMeters,
                elevationLossMeters = track.elevationLossMeters,
                flatDistanceMeters = track.totalDistanceMeters,
                climbingDistanceMeters = 0.0,
                downhillDistanceMeters = 0.0,
                flatPercentage = 100.0,
                climbingPercentage = 0.0,
                downhillPercentage = 0.0,
                hardestClimb = null,
                majorClimbs = emptyList(),
                bestDownhill = null,
                downhillCandidates = emptyList(),
                estimatedMaxAchievableSpeedKmh = 40.0,
                estimatedBestAchievablePaceSecondsPerKm = 240.0
            )
        }

        var flatDist = 0.0
        var climbDist = 0.0
        var downhillDist = 0.0

        for (p in points) {
            val dist = p.segmentDistanceMeters
            val grad = p.smoothedGradient
            when {
                grad > 1.5 -> climbDist += dist
                grad < -1.5 -> downhillDist += dist
                else -> flatDist += dist
            }
        }

        val totalDist = max(1.0, track.totalDistanceMeters)
        val flatPct = (flatDist / totalDist) * 100.0
        val climbPct = (climbDist / totalDist) * 100.0
        val downhillPct = (downhillDist / totalDist) * 100.0

        // Extract Climb sections (sustained gradient > 1.8% over at least 150m)
        val climbs = extractClimbSections(points)
        val hardestClimb = climbs.maxByOrNull { it.difficultyScore }

        // Extract Downhill sections (sustained gradient < -1.8% over at least 200m)
        val downhillCandidates = extractDownhillCandidates(points)
        val bestDownhill = downhillCandidates.maxByOrNull { it.score }

        // Estimate physical limits based on terrain
        val maxAchievableSpeed = if (bestDownhill != null) {
            bestDownhill.achievableMaxSpeedKmh
        } else {
            // On flat or mild terrain, realistic max speed under hard effort is ~45-52 km/h
            45.0
        }

        val bestAchievablePace = if (bestDownhill != null) {
            // Running downhill pace cap
            180.0 // 3:00 min/km
        } else {
            210.0 // 3:30 min/km
        }

        return RouteIntelligenceSummary(
            totalDistanceMeters = track.totalDistanceMeters,
            elevationGainMeters = track.elevationGainMeters,
            elevationLossMeters = track.elevationLossMeters,
            flatDistanceMeters = flatDist,
            climbingDistanceMeters = climbDist,
            downhillDistanceMeters = downhillDist,
            flatPercentage = flatPct,
            climbingPercentage = climbPct,
            downhillPercentage = downhillPct,
            hardestClimb = hardestClimb,
            majorClimbs = climbs,
            bestDownhill = bestDownhill,
            downhillCandidates = downhillCandidates,
            estimatedMaxAchievableSpeedKmh = maxAchievableSpeed,
            estimatedBestAchievablePaceSecondsPerKm = bestAchievablePace
        )
    }

    private fun extractClimbSections(points: List<com.stravakalcer.model.RoutePoint>): List<ClimbSection> {
        val result = mutableListOf<ClimbSection>()
        var inClimb = false
        var startIdx = 0

        for (i in points.indices) {
            val grad = points[i].smoothedGradient
            if (!inClimb && grad >= 2.0) {
                inClimb = true
                startIdx = i
            } else if (inClimb && (grad < 0.5 || i == points.size - 1)) {
                inClimb = false
                val endIdx = i
                val startP = points[startIdx]
                val endP = points[endIdx]
                val length = endP.distanceFromStartMeters - startP.distanceFromStartMeters
                val gain = endP.elevation - startP.elevation

                if (length >= 150.0 && gain >= 5.0) {
                    val avgGrad = (gain / length) * 100.0
                    var maxG = 0.0
                    for (k in startIdx..endIdx) {
                        maxG = max(maxG, points[k].smoothedGradient)
                    }

                    // UCI style climbing difficulty index: (length in meters * gradient in %^2) / 1000
                    val difficulty = (length * avgGrad * avgGrad) / 1000.0
                    val category = categorizeClimb(gain, avgGrad, length)

                    result.add(
                        ClimbSection(
                            startIndex = startIdx,
                            endIndex = endIdx,
                            startDistanceMeters = startP.distanceFromStartMeters,
                            endDistanceMeters = endP.distanceFromStartMeters,
                            lengthMeters = length,
                            elevationGainMeters = gain,
                            averageGradient = avgGrad,
                            maxGradient = maxG,
                            category = category,
                            difficultyScore = difficulty
                        )
                    )
                }
            }
        }
        return result
    }

    private fun categorizeClimb(gain: Double, avgGrad: Double, length: Double): String {
        val score = length * avgGrad
        return when {
            gain >= 1000.0 || score > 80000.0 -> "Hors Catégorie (HC)"
            gain >= 600.0 || score > 50000.0 -> "Category 1"
            gain >= 350.0 || score > 30000.0 -> "Category 2"
            gain >= 180.0 || score > 15000.0 -> "Category 3"
            gain >= 80.0 || score > 8000.0 -> "Category 4"
            else -> "Short Climb"
        }
    }

    private fun extractDownhillCandidates(points: List<com.stravakalcer.model.RoutePoint>): List<DownhillCandidate> {
        val candidates = mutableListOf<DownhillCandidate>()
        var inDownhill = false
        var startIdx = 0
        var candidateId = 1

        for (i in points.indices) {
            val grad = points[i].smoothedGradient
            if (!inDownhill && grad <= -2.0) {
                inDownhill = true
                startIdx = i
            } else if (inDownhill && (grad > -0.8 || i == points.size - 1)) {
                inDownhill = false
                val endIdx = i
                val startP = points[startIdx]
                val endP = points[endIdx]
                val length = endP.distanceFromStartMeters - startP.distanceFromStartMeters
                val drop = startP.elevation - endP.elevation

                if (length >= 150.0 && drop >= 5.0) {
                    val avgGrad = -(drop / length) * 100.0
                    var minGrad = 0.0
                    for (k in startIdx..endIdx) {
                        minGrad = min(minGrad, points[k].smoothedGradient)
                    }

                    // Score based on gradient steepness, length, and acceleration potential
                    // Steeper gradient (-5% to -12%) + longer length = high speed potential
                    val steepnessFactor = min(15.0, abs(avgGrad))
                    val lengthFactor = min(2000.0, length) / 100.0
                    val score = steepnessFactor * lengthFactor

                    // Realistic terminal speed calculation based on gravity and air resistance:
                    // v_terminal ~ sqrt((2 * m * g * sin(theta)) / (rho * CdA))
                    // On -6% grade: ~55-65 km/h. On -10% grade: ~70-80 km/h.
                    val gradeRad = Math.toRadians(abs(avgGrad) * 0.57) // approximate slope angle
                    val gravityBoost = sqrt(abs(avgGrad)) * 12.0
                    val baseSpeed = 35.0 + gravityBoost
                    val clampedSpeed = baseSpeed.coerceIn(40.0, 85.0)

                    val reason = "Downhill section of ${"%.1f".format(length / 1000.0)} km with average ${"%.1f".format(avgGrad)}% gradient (steepest ${"%.1f".format(minGrad)}%)."

                    candidates.add(
                        DownhillCandidate(
                            id = candidateId++,
                            startIndex = startIdx,
                            endIndex = endIdx,
                            startDistanceMeters = startP.distanceFromStartMeters,
                            endDistanceMeters = endP.distanceFromStartMeters,
                            lengthMeters = length,
                            averageGradient = avgGrad,
                            minGradient = minGrad,
                            elevationDropMeters = drop,
                            score = score,
                            achievableMaxSpeedKmh = clampedSpeed,
                            reason = reason
                        )
                    )
                }
            }
        }

        return candidates.sortedByDescending { it.score }
    }
}
