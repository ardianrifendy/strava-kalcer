package com.stravakalcer.fit

import com.garmin.fit.*
import com.garmin.fit.DateTime
import com.garmin.fit.File
import com.stravakalcer.device.DeviceProfile
import com.stravakalcer.model.SimulationResult
import com.stravakalcer.model.SportType
import java.io.File as JavaFile
import java.util.Date
import kotlin.math.roundToInt

object FitGenerator {

    // 180 degrees = 2^31 semicircles
    private const val SEMICIRCLE_CONVERSION = 2147483648.0 / 180.0

    fun toSemicircles(degrees: Double): Int {
        return (degrees * SEMICIRCLE_CONVERSION).roundToInt()
    }

    fun fromSemicircles(semicircles: Int): Double {
        return semicircles.toDouble() / SEMICIRCLE_CONVERSION
    }

    /**
     * Generates standard Garmin FIT activity binary bytes from SimulationResult and DeviceProfile.
     */
    fun generateFitBytes(
        result: SimulationResult,
        deviceProfile: DeviceProfile
    ): ByteArray {
        val tempFile = JavaFile.createTempFile("strava_kalcer_", ".fit")
        try {
            generateFitFile(result, deviceProfile, tempFile)
            return tempFile.readBytes()
        } finally {
            tempFile.delete()
        }
    }

    /**
     * Generates FIT file and writes directly to target Java File.
     */
    fun generateFitFile(
        result: SimulationResult,
        deviceProfile: DeviceProfile,
        targetFile: JavaFile
    ) {
        val encoder = FileEncoder(targetFile, Fit.ProtocolVersion.V2_0)

        val points = result.points
        if (points.isEmpty()) {
            encoder.close()
            return
        }

        val startEpochMillis = points.first().timestampEpochMillis
        val endEpochMillis = points.last().timestampEpochMillis

        // 1. File ID Message
        val fileIdMesg = FileIdMesg().apply {
            type = File.ACTIVITY
            manufacturer = deviceProfile.manufacturerId
            product = deviceProfile.productNumber
            serialNumber = 123456789L
            timeCreated = DateTime(Date(startEpochMillis))
        }
        encoder.write(fileIdMesg)

        // 2. Timer Start Event
        val timerStartEvent = EventMesg().apply {
            timestamp = DateTime(Date(startEpochMillis))
            event = Event.TIMER
            eventType = EventType.START
        }
        encoder.write(timerStartEvent)

        // 3. Track Record Messages
        for (p in points) {
            val record = RecordMesg().apply {
                timestamp = DateTime(Date(p.timestampEpochMillis))
                positionLat = toSemicircles(p.latitude)
                positionLong = toSemicircles(p.longitude)
                if (deviceProfile.includeAltitude) {
                    setEnhancedAltitude(p.elevation.toFloat())
                }
                if (deviceProfile.includeDistance) {
                    distance = p.distanceFromStartMeters.toFloat()
                }
                if (deviceProfile.includeSpeed) {
                    setEnhancedSpeed(p.speedMps.toFloat())
                }
                if (deviceProfile.includeHeartRate && p.heartRate != null) {
                    heartRate = p.heartRate.toShort()
                }
                if (deviceProfile.includeCadence && p.cadence != null) {
                    cadence = p.cadence.toShort()
                }
            }
            encoder.write(record)
        }

        // 4. Timer Stop Event
        val timerStopEvent = EventMesg().apply {
            timestamp = DateTime(Date(endEpochMillis))
            event = Event.TIMER
            eventType = EventType.STOP_ALL
        }
        encoder.write(timerStopEvent)

        // 5. Lap Message
        val lapMesg = LapMesg().apply {
            timestamp = DateTime(Date(endEpochMillis))
            startTime = DateTime(Date(startEpochMillis))
            totalElapsedTime = result.totalTimeSeconds.toFloat()
            totalTimerTime = result.movingTimeSeconds.toFloat()
            totalDistance = result.totalDistanceMeters.toFloat()
            enhancedAvgSpeed = (result.totalDistanceMeters / maxOf(1L, result.movingTimeSeconds)).toFloat()
            enhancedMaxSpeed = (result.maxSpeedKmh / 3.6).toFloat()
            totalAscent = result.elevationGainMeters.toInt()
            totalDescent = result.elevationLossMeters.toInt()

            result.averageHeartRate?.let { avgHeartRate = it.toShort() }
            result.maxHeartRate?.let { maxHeartRate = it.toShort() }
            result.averageCadence?.let { avgCadence = it.toShort() }

            sport = if (result.settings.sport == SportType.CYCLING) Sport.CYCLING else Sport.RUNNING
            subSport = SubSport.GENERIC
        }
        encoder.write(lapMesg)

        // 6. Session Message
        val sessionMesg = SessionMesg().apply {
            timestamp = DateTime(Date(endEpochMillis))
            startTime = DateTime(Date(startEpochMillis))
            totalElapsedTime = result.totalTimeSeconds.toFloat()
            totalTimerTime = result.movingTimeSeconds.toFloat()
            totalDistance = result.totalDistanceMeters.toFloat()
            enhancedAvgSpeed = (result.totalDistanceMeters / maxOf(1L, result.movingTimeSeconds)).toFloat()
            enhancedMaxSpeed = (result.maxSpeedKmh / 3.6).toFloat()
            totalAscent = result.elevationGainMeters.toInt()
            totalDescent = result.elevationLossMeters.toInt()

            result.averageHeartRate?.let { avgHeartRate = it.toShort() }
            result.maxHeartRate?.let { maxHeartRate = it.toShort() }
            result.averageCadence?.let { avgCadence = it.toShort() }

            sport = if (result.settings.sport == SportType.CYCLING) Sport.CYCLING else Sport.RUNNING
            subSport = SubSport.GENERIC
            firstLapIndex = 0
            numLaps = 1

            // Bounding box
            val minLat = points.minOf { it.latitude }
            val maxLat = points.maxOf { it.latitude }
            val minLon = points.minOf { it.longitude }
            val maxLon = points.maxOf { it.longitude }

            necLat = toSemicircles(maxLat)
            necLong = toSemicircles(maxLon)
            swcLat = toSemicircles(minLat)
            swcLong = toSemicircles(minLon)
        }
        encoder.write(sessionMesg)

        // 7. Activity Message
        val activityMesg = ActivityMesg().apply {
            timestamp = DateTime(Date(endEpochMillis))
            totalTimerTime = result.movingTimeSeconds.toFloat()
            numSessions = 1
            type = Activity.MANUAL
        }
        encoder.write(activityMesg)

        encoder.close()
    }
}
