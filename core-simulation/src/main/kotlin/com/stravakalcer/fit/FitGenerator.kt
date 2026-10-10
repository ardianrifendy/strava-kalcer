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

        val effectiveProductName = if (deviceProfile.manufacturerId == Manufacturer.GARMIN) {
            deviceProfile.modelName
        } else {
            deviceProfile.formattedDeviceName
        }

        // 1. File ID Message
        val fileIdMesg = FileIdMesg().apply {
            type = File.ACTIVITY
            manufacturer = deviceProfile.manufacturerId
            product = deviceProfile.productNumber
            if (deviceProfile.manufacturerId == Manufacturer.GARMIN) {
                garminProduct = deviceProfile.productNumber
            }
            productName = effectiveProductName
            serialNumber = 123456789L
            timeCreated = DateTime(Date(startEpochMillis))
        }
        encoder.write(fileIdMesg)

        // 1b. Creator Device Info Message (Ensures accurate device detection on Strava & Garmin Connect)
        val creatorDeviceInfo = DeviceInfoMesg().apply {
            timestamp = DateTime(Date(startEpochMillis))
            deviceIndex = DeviceIndex.CREATOR
            deviceType = 0
            manufacturer = deviceProfile.manufacturerId
            product = deviceProfile.productNumber
            if (deviceProfile.manufacturerId == Manufacturer.GARMIN) {
                garminProduct = deviceProfile.productNumber
            }
            productName = effectiveProductName
            serialNumber = 123456789L
            softwareVersion = 20.0f
            sourceType = SourceType.LOCAL
        }
        encoder.write(creatorDeviceInfo)

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
                    if (result.settings.sport == SportType.RUNNING) {
                        // Official Garmin FIT specification for Running:
                        // The 'cadence' field stores full stride cycles per minute (RPM / strides/min, where 1 stride = 2 steps).
                        // Platforms like Strava and Garmin Connect multiply this value by 2 to display SPM (Steps Per Minute):
                        val spm = p.cadence
                        cadence = (spm / 2).toShort()
                        fractionalCadence = if (spm % 2 != 0) 0.5f else 0.0f
                    } else {
                        // Cycling: crank arm revolutions per minute (RPM)
                        cadence = p.cadence.toShort()
                    }
                }
                if (deviceProfile.includePower && p.powerWatts != null) {
                    power = p.powerWatts
                }
                if (deviceProfile.includeTemperature && p.temperatureCelsius != null) {
                    temperature = p.temperatureCelsius.toByte()
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

        val maxCadenceValue = points.mapNotNull { it.cadence }.maxOrNull()

        // 5. Auto-Lap Splits (1 KM for Running, 5 KM for Cycling)
        val lapIntervalMeters = if (result.settings.sport == SportType.CYCLING) 5000.0 else 1000.0
        val laps = mutableListOf<List<com.stravakalcer.model.SimulationPoint>>()
        var currentLapPoints = mutableListOf<com.stravakalcer.model.SimulationPoint>()
        var nextMilestoneMeters = lapIntervalMeters

        for (p in points) {
            currentLapPoints.add(p)
            if (p.distanceFromStartMeters >= nextMilestoneMeters && currentLapPoints.size >= 5) {
                laps.add(currentLapPoints)
                currentLapPoints = mutableListOf()
                nextMilestoneMeters += lapIntervalMeters
            }
        }
        if (currentLapPoints.isNotEmpty()) {
            laps.add(currentLapPoints)
        }

        for (lapIdx in laps.indices) {
            val lapPts = laps[lapIdx]
            val lapStartEpoch = lapPts.first().timestampEpochMillis
            val lapEndEpoch = lapPts.last().timestampEpochMillis
            val lapDist = (lapPts.last().distanceFromStartMeters - lapPts.first().distanceFromStartMeters).coerceAtLeast(0.0)
            val lapTimerTime = (lapPts.last().movingTimeSeconds - lapPts.first().movingTimeSeconds).coerceAtLeast(1L)
            val lapElapsedTime = (lapPts.last().totalTimeSeconds - lapPts.first().totalTimeSeconds).coerceAtLeast(1L)

            val lapMesg = LapMesg().apply {
                timestamp = DateTime(Date(lapEndEpoch))
                startTime = DateTime(Date(lapStartEpoch))
                totalElapsedTime = lapElapsedTime.toFloat()
                totalTimerTime = lapTimerTime.toFloat()
                totalMovingTime = lapTimerTime.toFloat()
                totalDistance = lapDist.toFloat()
                enhancedAvgSpeed = if (lapTimerTime > 0) (lapDist / lapTimerTime).toFloat() else 0.0f
                enhancedMaxSpeed = ((lapPts.maxOfOrNull { it.speedKmh } ?: 0.0) / 3.6).toFloat()
                messageIndex = lapIdx

                val lapElevations = lapPts.map { it.elevation }
                var ascent = 0.0
                var descent = 0.0
                for (k in 1 until lapElevations.size) {
                    val diff = lapElevations[k] - lapElevations[k - 1]
                    if (diff > 0) ascent += diff else descent += -diff
                }
                totalAscent = ascent.toInt()
                totalDescent = descent.toInt()

                val lapHr = lapPts.mapNotNull { it.heartRate }
                if (lapHr.isNotEmpty()) {
                    avgHeartRate = lapHr.average().toInt().toShort()
                    maxHeartRate = lapHr.maxOrNull()?.toShort()
                }

                val lapCadence = lapPts.mapNotNull { it.cadence }
                val nonZeroLapCadence = lapPts.filter { (it.cadence ?: 0) > 0 }.mapNotNull { it.cadence }
                val lapAvgCadence = if (nonZeroLapCadence.isNotEmpty()) nonZeroLapCadence.average().toInt() else if (lapCadence.isNotEmpty()) lapCadence.average().toInt() else null
                val lapMaxCadence = lapCadence.maxOrNull()

                if (result.settings.sport == SportType.RUNNING) {
                    lapAvgCadence?.let {
                        avgCadence = (it / 2).toShort()
                        avgFractionalCadence = if (it % 2 != 0) 0.5f else 0.0f
                    }
                    lapMaxCadence?.let {
                        maxCadence = (it / 2).toShort()
                        maxFractionalCadence = if (it % 2 != 0) 0.5f else 0.0f
                    }
                } else {
                    lapAvgCadence?.let { avgCadence = it.toShort() }
                    lapMaxCadence?.let { maxCadence = it.toShort() }
                }

                val lapPower = lapPts.mapNotNull { it.powerWatts }.filter { it > 0 }
                if (lapPower.isNotEmpty()) {
                    avgPower = lapPower.average().toInt()
                    maxPower = lapPower.maxOrNull()
                }

                lapPts.mapNotNull { it.temperatureCelsius }.takeIf { it.isNotEmpty() }?.let {
                    avgTemperature = it.average().toInt().toByte()
                    maxTemperature = it.maxOrNull()?.toByte()
                }

                sport = if (result.settings.sport == SportType.CYCLING) Sport.CYCLING else Sport.RUNNING
                subSport = SubSport.GENERIC
            }
            encoder.write(lapMesg)
        }

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

            if (result.settings.sport == SportType.RUNNING) {
                result.averageCadence?.let {
                    avgCadence = (it / 2).toShort()
                    avgFractionalCadence = if (it % 2 != 0) 0.5f else 0.0f
                }
                maxCadenceValue?.let {
                    maxCadence = (it / 2).toShort()
                    maxFractionalCadence = if (it % 2 != 0) 0.5f else 0.0f
                }
            } else {
                result.averageCadence?.let { avgCadence = it.toShort() }
                maxCadenceValue?.let { maxCadence = it.toShort() }
            }

            result.averagePower?.let { avgPower = it }
            result.maxPower?.let { maxPower = it }
            result.normalizedPower?.let { normalizedPower = it }
            result.totalCalories?.let { totalCalories = it }
            result.averageTemperature?.let { avgTemperature = it.toByte() }
            result.maxTemperature?.let { maxTemperature = it.toByte() }

            sport = if (result.settings.sport == SportType.CYCLING) Sport.CYCLING else Sport.RUNNING
            subSport = SubSport.GENERIC
            firstLapIndex = 0
            numLaps = laps.size

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
