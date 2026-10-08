package com.stravakalcer.debug

import com.stravakalcer.model.SimulationResult
import java.io.StringWriter

object DebugExporter {

    /**
     * Generates CSV string matching the exact specification in 14_DEBUG_TOOLS.md:
     * index,latitude,longitude,elevation,elevationSource,distance,gradient,terrainClass,baselineSpeed,profileAdjustment,targetAdjustment,finalSpeed,heartRate,cadence,timestamp,movingTime,totalTime,stopState
     */
    fun exportToCsv(result: SimulationResult): String {
        val sw = StringWriter()
        sw.appendLine("index,latitude,longitude,elevation,elevationSource,distance,gradient,terrainClass,baselineSpeed,profileAdjustment,targetAdjustment,finalSpeed,heartRate,cadence,timestamp,movingTime,totalTime,stopState")

        for (p in result.points) {
            val terrainClass = when {
                p.gradient > 2.0 -> "CLIMB"
                p.gradient < -2.0 -> "DOWNHILL"
                else -> "FLAT"
            }

            val stopState = if (p.isStopped) "STOPPED" else "MOVING"

            sw.append("${p.index},")
            sw.append("${"%.6f".format(p.latitude)},")
            sw.append("${"%.6f".format(p.longitude)},")
            sw.append("${"%.1f".format(p.elevation)},")
            sw.append("${p.elevationSource},")
            sw.append("${"%.1f".format(p.distanceFromStartMeters)},")
            sw.append("${"%.2f".format(p.gradient)},")
            sw.append("$terrainClass,")
            sw.append("${"%.2f".format(p.baselineSpeedKmh)},")
            sw.append("${"%.2f".format(p.profileAdjustmentKmh)},")
            sw.append("${"%.2f".format(p.targetAdjustmentKmh)},")
            sw.append("${"%.2f".format(p.speedKmh)},")
            sw.append("${p.heartRate ?: ""},")
            sw.append("${p.cadence ?: ""},")
            sw.append("${p.timestampEpochMillis},")
            sw.append("${p.movingTimeSeconds},")
            sw.append("${p.totalTimeSeconds},")
            sw.appendLine(stopState)
        }

        return sw.toString()
    }
}
