package com.stravakalcer

import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.model.*
import com.stravakalcer.simulation.SimulationEngine
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import kotlin.math.abs

class SimulationEngineTest {

    private fun getTestTrack(): RouteTrack {
        val xml = TestFixtures.createRealisticGpx(100)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        return RouteProcessor.process(parsed)
    }

    @Test
    fun testCyclingTerrainRelationshipsAndTargetAverage() {
        val track = getTestTrack()
        val targetKmh = 28.0

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = targetKmh,
            requestedMaxSpeedKmh = 50.0
        )

        val result = SimulationEngine.simulate(track, settings)

        assertTrue(result.targetFeasible)
        // Average speed must be within configured tolerance (±0.2 km/h)
        val diff = abs(result.averageMovingSpeedKmh - targetKmh)
        assertTrue("Average speed $diff off target $targetKmh", diff <= 0.25)

        // Terrain Invariant: Climbs must be slower than downhills
        val climbPoints = result.points.filter { it.gradient > 4.0 }
        val downhillPoints = result.points.filter { it.gradient < -4.0 }

        assertTrue(climbPoints.isNotEmpty())
        assertTrue(downhillPoints.isNotEmpty())

        val avgClimbSpeed = climbPoints.map { it.speedKmh }.average()
        val avgDownhillSpeed = downhillPoints.map { it.speedKmh }.average()

        assertTrue("Climb speed ($avgClimbSpeed) should be strictly slower than downhill ($avgDownhillSpeed)",
            avgClimbSpeed < avgDownhillSpeed)

        // Smoothness Invariant: No erratic jumps between adjacent points (> 15 km/h jump per 50m)
        for (i in 1 until result.points.size) {
            val jump = abs(result.points[i].speedKmh - result.points[i - 1].speedKmh)
            assertTrue("Point $i has unnatural speed jump: $jump km/h", jump < 12.0)
        }
    }

    @Test
    fun testRunningTerrainRelationshipsAndTargetPace() {
        val track = getTestTrack()
        val targetPaceSec = 330.0 // 5:30 min/km

        val settings = SimulationSettings(
            sport = SportType.RUNNING,
            profile = ActivityProfile.TEMPO_RUN,
            targetAveragePaceSecondsPerKm = targetPaceSec
        )

        val result = SimulationEngine.simulate(track, settings)

        assertTrue(result.targetFeasible)
        val paceDiff = abs(result.averageMovingPaceSecondsPerKm - targetPaceSec)
        assertTrue("Average pace $paceDiff s off target", paceDiff <= 5.0)

        // Running uphill is slower (higher sec/km) than downhill (lower sec/km)
        val climbPoints = result.points.filter { it.gradient > 3.0 }
        val downhillPoints = result.points.filter { it.gradient < -3.0 }

        val avgClimbPace = climbPoints.map { it.paceSecondsPerKm }.average()
        val avgDownhillPace = downhillPoints.map { it.paceSecondsPerKm }.average()

        assertTrue("Climb pace ($avgClimbPace) should be higher/slower than downhill ($avgDownhillPace)",
            avgClimbPace > avgDownhillPace)
    }

    @Test
    fun testStopsAndSeparationOfMovingTimeVsTotalTime() {
        val track = getTestTrack()
        val stopDistance = 1500.0
        val stopDurationSec = 300L // 5 minutes rest

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.EASY_RIDE,
            targetAverageSpeedKmh = 25.0,
            stops = listOf(StopConfig(stopDistance, stopDurationSec, "Cafe Pause"))
        )

        val result = SimulationEngine.simulate(track, settings)

        // Total time must strictly exceed moving time by at least stop duration
        val timeDifference = result.totalTimeSeconds - result.movingTimeSeconds
        assertEquals(stopDurationSec, timeDifference)

        // The stopped point must have speed = 0 and isStopped = true
        val stoppedPoint = result.findPointAtDistance(stopDistance)
        assertNotNull(stoppedPoint)
        assertTrue(stoppedPoint!!.isStopped)
        assertEquals(0.0, stoppedPoint.speedKmh, 0.001)

        // Monotonic timestamps
        for (i in 1 until result.points.size) {
            assertTrue("Timestamps must be non-decreasing",
                result.points[i].timestampEpochMillis >= result.points[i - 1].timestampEpochMillis)
        }
    }

    @Test
    fun testPhysiologyHeartRateAndCadence() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.RACE_CYCLING,
            targetAverageSpeedKmh = 32.0,
            hrConfig = HeartRateConfig(enabled = true, restingHr = 55, maxHr = 190),
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = 90)
        )

        val result = SimulationEngine.simulate(track, settings)

        assertNotNull(result.averageHeartRate)
        assertNotNull(result.maxHeartRate)
        assertNotNull(result.averageCadence)

        assertTrue(result.averageHeartRate!! in 100..185)
        assertTrue(result.maxHeartRate!! in 120..190)

        // Verify coasting cadence reduction on steep downhill
        val steepDownhillPoints = result.points.filter { it.gradient < -4.0 }
        assertTrue(steepDownhillPoints.isNotEmpty())
        val coastingPoint = steepDownhillPoints.first()
        assertTrue("Coasting cadence should decrease on steep descent",
            (coastingPoint.cadence ?: 100) < 50)
    }

    @Test
    fun testDeterministicReplay() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 28.0,
            randomSeed = 12345L
        )

        val run1 = SimulationEngine.simulate(track, settings)
        val run2 = SimulationEngine.simulate(track, settings)

        assertEquals(run1.movingTimeSeconds, run2.movingTimeSeconds)
        assertEquals(run1.totalTimeSeconds, run2.totalTimeSeconds)
        assertEquals(run1.averageMovingSpeedKmh, run2.averageMovingSpeedKmh, 0.0001)
        assertEquals(run1.points.size, run2.points.size)

        for (i in run1.points.indices) {
            assertEquals(run1.points[i].speedKmh, run2.points[i].speedKmh, 0.0001)
            assertEquals(run1.points[i].heartRate, run2.points[i].heartRate)
            assertEquals(run1.points[i].cadence, run2.points[i].cadence)
        }
    }

    @Test
    fun testCustomCadenceHighSpinner() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.RACE_CYCLING,
            targetAverageSpeedKmh = 35.0,
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = 105)
        )

        val result = SimulationEngine.simulate(track, settings)
        assertNotNull(result.averageCadence)
        assertTrue("Average cadence should reflect high spinner target", result.averageCadence!! >= 95)
    }

    @Test
    fun testCustomCoastingDisabled() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 28.0,
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = 90, allowCoasting = false)
        )

        val result = SimulationEngine.simulate(track, settings)
        val steepDownhillPoints = result.points.filter { it.gradient < -4.0 }
        assertTrue(steepDownhillPoints.isNotEmpty())
        for (pt in steepDownhillPoints) {
            assertTrue("Cadence should stay high when coasting is disabled", (pt.cadence ?: 0) >= 70)
        }
    }

    @Test
    fun testCustomRunningCadence() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.RUNNING,
            profile = ActivityProfile.TEMPO_RUN,
            targetAveragePaceSecondsPerKm = 300.0,
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = 178)
        )

        val result = SimulationEngine.simulate(track, settings)
        assertNotNull(result.averageCadence)
        assertTrue("Running cadence should match custom SPM target", result.averageCadence!! in 170..186)
    }

    @Test
    fun testSmartStopPresetsAndPhysicsSeparation() {
        val track = getTestTrack()
        val stops = SimulationSettings.generateStopsForPreset(SmartStopPreset.CITY_TRAFFIC, track.totalDistanceMeters)
        assertTrue("City traffic preset should generate intermediate stops", stops.isNotEmpty())

        val totalExpectedStopDuration = stops.sumOf { it.durationSeconds }

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 27.0,
            smartStopPreset = SmartStopPreset.CITY_TRAFFIC,
            stops = stops
        )

        val result = SimulationEngine.simulate(track, settings)
        val timeDiff = result.totalTimeSeconds - result.movingTimeSeconds
        assertEquals("Total time must exceed moving time by total stopped duration", totalExpectedStopDuration, timeDiff)

        val stopPoints = result.points.filter { it.isStopped }
        assertTrue("Should have stop points in simulation result", stopPoints.isNotEmpty())

        for (pt in stopPoints) {
            assertEquals("Stop speed must be 0", 0.0, pt.speedKmh, 0.001)
            assertEquals("Stop cadence must be 0", 0, pt.cadence ?: 0)
            assertTrue("Stop duration must be recorded", pt.stopDurationSeconds > 0)
            assertEquals("Explain reason must be STOP", "STOP", pt.explainReason)
        }
    }

    @Test
    fun testCustomStartEpochPropagation() {
        val track = getTestTrack()
        val customStartEpoch = 1770000000000L // Custom historical timestamp

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.EASY_RIDE,
            startEpochMillis = customStartEpoch
        )

        val result = SimulationEngine.simulate(track, settings)
        assertEquals("First point timestamp must match startEpochMillis", customStartEpoch, result.points.first().timestampEpochMillis)
        assertTrue("Subsequent points must advance from custom start epoch", result.points.last().timestampEpochMillis > customStartEpoch)
    }

    @Test
    fun testCyclingPowerEnergyAndTemperature() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 30.0,
            hrConfig = HeartRateConfig(enabled = true, restingHr = 60, maxHr = 185)
        )

        val result = SimulationEngine.simulate(track, settings)

        // 1. Power validation
        assertNotNull("Average power must be computed", result.averagePower)
        assertNotNull("Max power must be computed", result.maxPower)
        assertNotNull("Normalized power must be computed", result.normalizedPower)

        assertTrue("Average power should be realistic (100-350W)", result.averagePower!! in 100..350)
        assertTrue("Max power should be >= average power", result.maxPower!! >= result.averagePower!!)
        assertTrue("Normalized power should be > 0", result.normalizedPower!! > 0)
        assertTrue("Total work must be positive", result.totalWorkJoules > 1000)

        // Climbs demand higher power than flats/descents
        val climbPoints = result.points.filter { it.gradient > 4.0 && !it.isStopped }
        val flatPoints = result.points.filter { abs(it.gradient) <= 1.0 && !it.isStopped }
        if (climbPoints.isNotEmpty() && flatPoints.isNotEmpty()) {
            val avgClimbPower = climbPoints.mapNotNull { it.powerWatts }.average()
            val avgFlatPower = flatPoints.mapNotNull { it.powerWatts }.average()
            assertTrue("Climbing power ($avgClimbPower) should exceed flat power ($avgFlatPower)", avgClimbPower > avgFlatPower)
        }

        // 2. Calories validation
        assertNotNull("Total calories must be computed", result.totalCalories)
        assertTrue("Calories must be realistic (> 50 kcal)", result.totalCalories!! > 50)

        // 3. Ambient Temperature validation
        assertNotNull("Average temperature must be computed", result.averageTemperature)
        assertNotNull("Max temperature must be computed", result.maxTemperature)
        assertTrue("Ambient temperature must be in tropical range (25-35°C)", result.averageTemperature!! in 25..35)
        assertTrue("Max temperature must be >= average temperature", result.maxTemperature!! >= result.averageTemperature!!)
    }

    @Test
    fun testRunningPowerAndCalories() {
        val track = getTestTrack()
        val settings = SimulationSettings(
            sport = SportType.RUNNING,
            profile = ActivityProfile.TEMPO_RUN,
            targetAveragePaceSecondsPerKm = 300.0,
            hrConfig = HeartRateConfig(enabled = true)
        )

        val result = SimulationEngine.simulate(track, settings)

        assertNotNull(result.averagePower)
        assertTrue("Running power should be realistic (150-450W)", result.averagePower!! in 150..450)
        assertNotNull(result.totalCalories)
        assertTrue("Running calories should be > 50 kcal", result.totalCalories!! > 50)
    }

    @Test
    fun testRouteReversalIntegrity() {
        val originalTrack = getTestTrack()
        val reversedTrack = RouteProcessor.reverseTrack(originalTrack)

        // Size matches
        assertEquals("Reversed track should have same number of points", originalTrack.points.size, reversedTrack.points.size)

        // Distance matches within 1% margin
        val distDiff = abs(originalTrack.totalDistanceMeters - reversedTrack.totalDistanceMeters)
        assertTrue("Reversed total distance should be virtually identical (diff: $distDiff)", distDiff < 5.0)

        // Reversed start matches original end coordinates
        val origStart = originalTrack.points.first()
        val origEnd = originalTrack.points.last()
        val revStart = reversedTrack.points.first()
        val revEnd = reversedTrack.points.last()

        assertEquals("Reversed start lat should match original end lat", origEnd.latitude, revStart.latitude, 0.0001)
        assertEquals("Reversed start lon should match original end lon", origEnd.longitude, revStart.longitude, 0.0001)
        assertEquals("Reversed end lat should match original start lat", origStart.latitude, revEnd.latitude, 0.0001)
        assertEquals("Reversed end lon should match original start lon", origStart.longitude, revEnd.longitude, 0.0001)

        // Reversed name has suffix
        assertTrue("Reversed track name should indicate reversal", reversedTrack.name.contains("(Reversed)"))

        // Double reversal restores original name
        val doubleReversed = RouteProcessor.reverseTrack(reversedTrack)
        assertFalse("Double reversal should remove (Reversed) suffix", doubleReversed.name.contains("(Reversed)"))
    }
}
