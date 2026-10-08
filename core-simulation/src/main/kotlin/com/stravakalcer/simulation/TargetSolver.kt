package com.stravakalcer.simulation

import com.stravakalcer.gpx.GeoMath
import com.stravakalcer.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class TargetSolverResult(
    val solvedSpeedsMps: DoubleArray,
    val iterations: Int,
    val targetFeasible: Boolean,
    val feasibilityNote: String,
    val requestedTargetText: String,
    val achievedTargetText: String,
    val requestedMaxOrBestText: String?,
    val achievedMaxOrBestText: String?
)

object TargetSolver {

    private const val CYCLING_TOLERANCE_KMH = 0.2
    private const val RUNNING_TOLERANCE_SEC_PER_KM = 3.0

    /**
     * Solves for the target average speed or pace using a terrain-preserving root finder.
     * Preserves relative gradient and terrain variations while tuning base effort.
     */
    fun solve(
        track: RouteTrack,
        settings: SimulationSettings,
        downhillCandidate: DownhillCandidate?
    ): TargetSolverResult {
        val n = track.points.size
        val dists = DoubleArray(n) { track.points[it].segmentDistanceMeters }
        val grads = DoubleArray(n) { track.points[it].smoothedGradient }
        val totalDistance = track.totalDistanceMeters
        val params = settings.activeProfileParams

        val isCycling = settings.sport == SportType.CYCLING

        // Feasibility bounds for base flat cruising effort (in m/s)
        val (minEffortMps, maxEffortMps) = if (isCycling) {
            Pair(GeoMath.kmhToMps(10.0), GeoMath.kmhToMps(55.0))
        } else {
            // Running: 10:00 min/km (1.67 mps) to 2:45 min/km (6.06 mps)
            Pair(1.67, 6.06)
        }

        val targetSpeedKmh = if (isCycling) {
            settings.targetAverageSpeedKmh ?: 28.0
        } else {
            val paceSec = settings.targetAveragePaceSecondsPerKm ?: 330.0
            GeoMath.mpsToKmh(GeoMath.paceSecondsPerKmToMps(paceSec))
        }

        var lowEffort = minEffortMps
        var highEffort = maxEffortMps
        var bestEffort = (lowEffort + highEffort) / 2.0
        var bestSpeeds = DoubleArray(n)
        var bestAvgKmh = 0.0
        var iterations = 0
        var targetFeasible = true
        var note = "Target average achieved within realistic terrain parameters."

        // Evaluate extreme bounds to check feasibility
        val maxFeasibleAvgKmh = evaluateAverageKmh(
            maxEffortMps, grads, dists, totalDistance, isCycling, params, downhillCandidate, settings
        ).second
        val minFeasibleAvgKmh = evaluateAverageKmh(
            minEffortMps, grads, dists, totalDistance, isCycling, params, downhillCandidate, settings
        ).second

        if (targetSpeedKmh > maxFeasibleAvgKmh + 0.1) {
            targetFeasible = false
            note = if (isCycling) {
                "Requested target (${"%.1f".format(targetSpeedKmh)} km/h) exceeds physical limit for this route terrain (maximum feasible: ${"%.1f".format(maxFeasibleAvgKmh)} km/h)."
            } else {
                val reqPace = GeoMath.formatPace(GeoMath.mpsToPaceSecondsPerKm(GeoMath.kmhToMps(targetSpeedKmh)))
                val maxPace = GeoMath.formatPace(GeoMath.mpsToPaceSecondsPerKm(GeoMath.kmhToMps(maxFeasibleAvgKmh)))
                "Requested pace ($reqPace min/km) exceeds aerobic limit for this route terrain (maximum feasible: $maxPace min/km)."
            }
            bestEffort = maxEffortMps
            val (sp, avg) = evaluateAverageKmh(
                bestEffort, grads, dists, totalDistance, isCycling, params, downhillCandidate, settings
            )
            bestSpeeds = sp
            bestAvgKmh = avg
            iterations = 1
        } else if (targetSpeedKmh < minFeasibleAvgKmh - 0.1) {
            targetFeasible = false
            note = "Requested target is below minimum steady movement threshold."
            bestEffort = minEffortMps
            val (sp, avg) = evaluateAverageKmh(
                bestEffort, grads, dists, totalDistance, isCycling, params, downhillCandidate, settings
            )
            bestSpeeds = sp
            bestAvgKmh = avg
            iterations = 1
        } else {
            // Binary search to find base effort achieving target within tolerance
            val toleranceKmh = if (isCycling) CYCLING_TOLERANCE_KMH else 0.15
            for (iter in 1..25) {
                iterations = iter
                val midEffort = (lowEffort + highEffort) / 2.0
                val (speeds, avgKmh) = evaluateAverageKmh(
                    midEffort, grads, dists, totalDistance, isCycling, params, downhillCandidate, settings
                )
                bestSpeeds = speeds
                bestAvgKmh = avgKmh

                val diff = avgKmh - targetSpeedKmh
                if (abs(diff) <= toleranceKmh) {
                    break
                }
                if (diff < 0) {
                    lowEffort = midEffort
                } else {
                    highEffort = midEffort
                }
            }
        }

        // Requested max speed / best pace analysis
        val maxSpeedAchievedKmh = GeoMath.mpsToKmh(bestSpeeds.maxOrNull() ?: 0.0)
        val bestPaceAchievedSec = GeoMath.mpsToPaceSecondsPerKm(bestSpeeds.maxOrNull() ?: 1.0)

        val requestedTargetText = if (isCycling) {
            "${"%.1f".format(targetSpeedKmh)} km/h"
        } else {
            "${GeoMath.formatPace(settings.targetAveragePaceSecondsPerKm ?: 330.0)} min/km"
        }

        val achievedTargetText = if (isCycling) {
            "${"%.1f".format(bestAvgKmh)} km/h"
        } else {
            val achievedPace = GeoMath.mpsToPaceSecondsPerKm(GeoMath.kmhToMps(bestAvgKmh))
            "${GeoMath.formatPace(achievedPace)} min/km"
        }

        val (requestedMaxOrBestText, achievedMaxOrBestText) = if (isCycling) {
            val req = settings.requestedMaxSpeedKmh?.let { "${"%.1f".format(it)} km/h" }
            val ach = "${"%.1f".format(maxSpeedAchievedKmh)} km/h"
            Pair(req, ach)
        } else {
            val req = settings.requestedBestPaceSecondsPerKm?.let { "${GeoMath.formatPace(it)} min/km" }
            val ach = "${GeoMath.formatPace(bestPaceAchievedSec)} min/km"
            Pair(req, ach)
        }

        return TargetSolverResult(
            solvedSpeedsMps = bestSpeeds,
            iterations = iterations,
            targetFeasible = targetFeasible,
            feasibilityNote = note,
            requestedTargetText = requestedTargetText,
            achievedTargetText = achievedTargetText,
            requestedMaxOrBestText = requestedMaxOrBestText,
            achievedMaxOrBestText = achievedMaxOrBestText
        )
    }

    private fun evaluateAverageKmh(
        baseEffortMps: Double,
        grads: DoubleArray,
        dists: DoubleArray,
        totalDistanceMeters: Double,
        isCycling: Boolean,
        params: ProfileParameters,
        downhillCandidate: DownhillCandidate?,
        settings: SimulationSettings
    ): Pair<DoubleArray, Double> {
        val n = grads.size
        val rawSpeeds = DoubleArray(n)

        for (i in 0 until n) {
            rawSpeeds[i] = if (isCycling) {
                CyclingSimulationEngine.computeTerrainSpeedMps(grads[i], baseEffortMps, params)
            } else {
                RunningSimulationEngine.computeTerrainSpeedMps(grads[i], baseEffortMps, params)
            }
        }

        // Apply requested max speed or best pace on downhill candidate if applicable
        if (downhillCandidate != null) {
            if (isCycling && settings.requestedMaxSpeedKmh != null) {
                val reqMax = settings.requestedMaxSpeedKmh
                val achievable = min(reqMax, downhillCandidate.achievableMaxSpeedKmh)
                CyclingSimulationEngine.applyMaxSpeedOpportunity(rawSpeeds, dists, downhillCandidate, achievable)
            } else if (!isCycling && settings.requestedBestPaceSecondsPerKm != null) {
                val reqPace = settings.requestedBestPaceSecondsPerKm
                RunningSimulationEngine.applyBestPaceOpportunity(rawSpeeds, downhillCandidate, reqPace)
            }
        }

        // Apply dynamic smoothing
        val smoothed = if (isCycling) {
            CyclingSimulationEngine.smoothSpeeds(rawSpeeds, dists, params)
        } else {
            RunningSimulationEngine.smoothSpeeds(rawSpeeds, dists, params)
        }

        // Compute moving time
        var movingTimeSec = 0.0
        for (i in 1 until n) {
            val d = dists[i]
            val s = max(0.5, smoothed[i])
            movingTimeSec += d / s
        }

        val avgKmh = if (movingTimeSec > 0.0) {
            (totalDistanceMeters / movingTimeSec) * 3.6
        } else {
            0.0
        }

        return Pair(smoothed, avgKmh)
    }
}
