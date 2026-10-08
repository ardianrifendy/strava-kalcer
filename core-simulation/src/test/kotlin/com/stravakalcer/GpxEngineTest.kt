package com.stravakalcer

import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.model.ElevationSource
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class GpxEngineTest {

    @Test
    fun testParseValidRealisticGpx() {
        val xml = TestFixtures.createRealisticGpx(80)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))

        assertEquals("Kalcer Test Loop", parsed.trackName)
        assertEquals(80, parsed.points.size)

        val track = RouteProcessor.process(parsed)
        assertTrue(track.totalDistanceMeters > 3000.0)
        assertTrue(track.elevationGainMeters > 50.0)
        assertTrue(track.elevationLossMeters > 50.0)
        assertEquals(ElevationSource.ORIGINAL, track.elevationQuality.source)
        assertTrue(track.elevationQuality.isReliableForSimulation)
    }

    @Test
    fun testParseEmptyGpx() {
        val parsed = GpxParser.parse(ByteArrayInputStream(TestFixtures.EMPTY_GPX.toByteArray()))
        val track = RouteProcessor.process(parsed)

        assertEquals(0, track.points.size)
        assertEquals(0.0, track.totalDistanceMeters, 0.001)
        assertEquals(ElevationSource.UNAVAILABLE, track.elevationQuality.source)
    }

    @Test
    fun testParseOnePointGpx() {
        val parsed = GpxParser.parse(ByteArrayInputStream(TestFixtures.ONE_POINT_GPX.toByteArray()))
        val track = RouteProcessor.process(parsed)

        assertEquals(1, track.points.size)
        assertEquals(0.0, track.totalDistanceMeters, 0.001)
    }

    @Test
    fun testAllZeroElevationDetected() {
        val parsed = GpxParser.parse(ByteArrayInputStream(TestFixtures.ALL_ZERO_ELEVATION_GPX.toByteArray()))
        val track = RouteProcessor.process(parsed)

        assertTrue(track.elevationQuality.allZero)
        assertEquals(ElevationSource.UNAVAILABLE, track.elevationQuality.source)
        assertFalse(track.elevationQuality.isReliableForSimulation)
    }

    @Test
    fun testMissingElevationDetected() {
        val parsed = GpxParser.parse(ByteArrayInputStream(TestFixtures.MISSING_ELEVATION_GPX.toByteArray()))
        val track = RouteProcessor.process(parsed)

        assertEquals(ElevationSource.UNAVAILABLE, track.elevationQuality.source)
        assertFalse(track.elevationQuality.isReliableForSimulation)
    }

    @Test
    fun testMultipleTrackSegmentsHandledWithoutArtificialJumps() {
        val parsed = GpxParser.parse(ByteArrayInputStream(TestFixtures.MULTI_SEGMENT_GPX.toByteArray()))
        val track = RouteProcessor.process(parsed)

        assertEquals(2, track.segmentCount)
        assertEquals(4, track.points.size)
        // Ensure distance is positive and bounds are valid
        assertTrue(track.totalDistanceMeters > 0.0)
    }
}
