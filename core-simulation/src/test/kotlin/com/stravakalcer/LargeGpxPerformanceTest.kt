package com.stravakalcer

import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.model.ActivityProfile
import com.stravakalcer.model.SimulationSettings
import com.stravakalcer.model.SportType
import com.stravakalcer.simulation.SimulationEngine
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class LargeGpxPerformanceTest {

    @Test
    fun testLargeGpx10kPointsPerformance() {
        val count = 10_000
        val xml = TestFixtures.createRealisticGpx(count)
        val startTime = System.currentTimeMillis()

        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        assertEquals(count, parsed.points.size)

        val track = RouteProcessor.process(parsed)
        assertEquals(count, track.points.size)

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 28.0
        )
        val result = SimulationEngine.simulate(track, settings)

        val duration = System.currentTimeMillis() - startTime
        println("Processed and simulated $count points in ${duration}ms")

        assertEquals(count, result.points.size)
        assertTrue(result.targetFeasible)
        assertTrue("10k points should process within 3000ms", duration < 3000)
    }
}
