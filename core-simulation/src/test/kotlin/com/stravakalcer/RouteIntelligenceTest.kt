package com.stravakalcer

import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.intelligence.RouteIntelligenceEngine
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class RouteIntelligenceTest {

    @Test
    fun testDetectsClimbsAndDownhillsAccurately() {
        val xml = TestFixtures.createRealisticGpx(100)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val intelligence = RouteIntelligenceEngine.analyze(track)

        assertTrue(intelligence.climbingDistanceMeters > 500.0)
        assertTrue(intelligence.downhillDistanceMeters > 500.0)
        assertTrue(intelligence.flatDistanceMeters > 500.0)

        // Hardest climb should be detected
        assertNotNull(intelligence.hardestClimb)
        val climb = intelligence.hardestClimb!!
        assertTrue(climb.elevationGainMeters > 30.0)
        assertTrue(climb.averageGradient > 2.0)

        // Downhill candidates should be detected and ranked
        assertTrue(intelligence.downhillCandidates.isNotEmpty())
        assertNotNull(intelligence.bestDownhill)
        val bestDownhill = intelligence.bestDownhill!!
        assertTrue(bestDownhill.averageGradient < -2.0)
        assertTrue(bestDownhill.achievableMaxSpeedKmh > 45.0)
    }
}
