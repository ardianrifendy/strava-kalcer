package com.stravakalcer.fit

import com.garmin.fit.*
import com.stravakalcer.model.SimulationResult
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.math.abs

data class FitValidationReport(
    val isValid: Boolean,
    val totalRecordsParsed: Int,
    val hasFileId: Boolean,
    val hasActivity: Boolean,
    val hasSession: Boolean,
    val hasLap: Boolean,
    val timestampsMonotonic: Boolean,
    val coordinatesValid: Boolean,
    val distanceMonotonic: Boolean,
    val speedValid: Boolean,
    val elevationValid: Boolean,
    val hrValid: Boolean,
    val cadenceValid: Boolean,
    val summaryMetricsConsistent: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)

object FitValidator {

    /**
     * Parses generated FIT bytes and validates integrity against Garmin FIT standard
     * and against the original SimulationResult.
     */
    fun validate(
        fitBytes: ByteArray,
        expectedResult: SimulationResult
    ): FitValidationReport {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        var hasFileId = false
        var hasActivity = false
        var hasSession = false
        var hasLap = false
        var sessionDistanceMeters = 0.0f
        var sessionTimerTime = 0.0f

        val parsedRecords = mutableListOf<RecordMesg>()

        try {
            val decode = Decode()
            val broadcaster = MesgBroadcaster()

            broadcaster.addListener(FileIdMesgListener { mesg ->
                hasFileId = true
                if (mesg.type != File.ACTIVITY) {
                    errors.add("FileId type is not ACTIVITY (was: ${mesg.type})")
                }
            })

            broadcaster.addListener(ActivityMesgListener {
                hasActivity = true
            })

            broadcaster.addListener(SessionMesgListener { mesg ->
                hasSession = true
                sessionDistanceMeters = mesg.totalDistance ?: 0.0f
                sessionTimerTime = mesg.totalTimerTime ?: 0.0f
            })

            broadcaster.addListener(LapMesgListener {
                hasLap = true
            })

            broadcaster.addListener(RecordMesgListener { mesg ->
                parsedRecords.add(mesg)
            })

            val stream: InputStream = ByteArrayInputStream(fitBytes)
            if (!decode.checkFileIntegrity(stream)) {
                errors.add("FIT integrity check failed. Corrupted header or CRC checksum mismatch.")
            }

            // Reset stream and read messages through decode
            val readStream: InputStream = ByteArrayInputStream(fitBytes)
            decode.read(readStream, broadcaster)

        } catch (e: Exception) {
            errors.add("FIT parser exception: ${e.message}")
        }

        // Structural validation
        if (!hasFileId) errors.add("Missing required FileIdMesg.")
        if (!hasSession) errors.add("Missing required SessionMesg.")
        if (!hasLap) errors.add("Missing required LapMesg.")
        if (!hasActivity) errors.add("Missing required ActivityMesg.")

        if (parsedRecords.isEmpty()) {
            errors.add("FIT contains 0 Record messages.")
        }

        val expectedPoints = expectedResult.points
        if (parsedRecords.size != expectedPoints.size) {
            errors.add("Parsed records count (${parsedRecords.size}) does not match simulated points (${expectedPoints.size}).")
        }

        // Validate record contents
        var timestampsMonotonic = true
        var coordinatesValid = true
        var distanceMonotonic = true
        var speedValid = true
        var elevationValid = true
        var hrValid = true
        var cadenceValid = true

        var lastTimestamp = 0L
        var lastDistance = -0.1f

        for (i in parsedRecords.indices) {
            val rec = parsedRecords[i]

            // 1. Timestamps
            val ts = rec.timestamp?.timestamp ?: 0L
            if (i > 0 && ts < lastTimestamp) {
                timestampsMonotonic = false
                errors.add("Record $i timestamp non-monotonic: $ts < $lastTimestamp")
            }
            lastTimestamp = ts

            // 2. Coordinates
            val latSemi = rec.positionLat
            val lonSemi = rec.positionLong
            if (latSemi == null || lonSemi == null) {
                coordinatesValid = false
                errors.add("Record $i has null coordinates.")
            } else {
                val latDeg = FitGenerator.fromSemicircles(latSemi)
                val lonDeg = FitGenerator.fromSemicircles(lonSemi)
                if (latDeg !in -90.0..90.0 || lonDeg !in -180.0..180.0) {
                    coordinatesValid = false
                    errors.add("Record $i coordinates out of range: ($latDeg, $lonDeg)")
                }
            }

            // 3. Distance monotonicity
            val dist = rec.distance ?: 0.0f
            if (dist < lastDistance - 0.01f) {
                distanceMonotonic = false
                errors.add("Record $i distance reversed: $dist < $lastDistance")
            }
            lastDistance = dist

            // 4. Speed range (0 to 120 km/h)
            val spd = rec.enhancedSpeed ?: rec.speed ?: 0.0f
            if (spd < 0.0f || spd > 35.0f) {
                speedValid = false
                warnings.add("Record $i speed out of normal range: ${spd * 3.6f} km/h")
            }

            // 5. Elevation
            val ele = rec.enhancedAltitude ?: rec.altitude
            if (ele != null && (ele < -500.0f || ele > 9000.0f)) {
                elevationValid = false
                warnings.add("Record $i altitude out of plausible terrestrial bounds: $ele m")
            }

            // 6. HR if enabled
            if (expectedResult.settings.hrConfig.enabled) {
                val hr = rec.heartRate?.toInt()
                if (hr != null && (hr < 30 || hr > 240)) {
                    hrValid = false
                    warnings.add("Record $i heart rate out of human physiological range: $hr bpm")
                }
            }

            // 7. Cadence if enabled
            if (expectedResult.settings.cadenceConfig.enabled) {
                val cad = rec.cadence?.toInt()
                if (cad != null && (cad < 0 || cad > 250)) {
                    cadenceValid = false
                    warnings.add("Record $i cadence out of range: $cad")
                }
            }
        }

        // Summary metric comparison
        var summaryConsistent = true
        val distDiff = abs(sessionDistanceMeters.toDouble() - expectedResult.totalDistanceMeters)
        if (distDiff > 10.0) { // 10 meter tolerance
            summaryConsistent = false
            errors.add("Session distance ($sessionDistanceMeters m) deviates from simulated distance (${expectedResult.totalDistanceMeters} m)")
        }

        val timeDiff = abs(sessionTimerTime.toLong() - expectedResult.movingTimeSeconds)
        if (timeDiff > 5L) { // 5 second tolerance
            summaryConsistent = false
            errors.add("Session timer time ($sessionTimerTime s) deviates from simulated moving time (${expectedResult.movingTimeSeconds} s)")
        }

        val isValid = errors.isEmpty()

        return FitValidationReport(
            isValid = isValid,
            totalRecordsParsed = parsedRecords.size,
            hasFileId = hasFileId,
            hasActivity = hasActivity,
            hasSession = hasSession,
            hasLap = hasLap,
            timestampsMonotonic = timestampsMonotonic,
            coordinatesValid = coordinatesValid,
            distanceMonotonic = distanceMonotonic,
            speedValid = speedValid,
            elevationValid = elevationValid,
            hrValid = hrValid,
            cadenceValid = cadenceValid,
            summaryMetricsConsistent = summaryConsistent,
            errors = errors,
            warnings = warnings
        )
    }
}
