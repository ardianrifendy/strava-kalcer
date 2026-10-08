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

    @Test
    fun testParseGpxWithUtf8Bom() {
        val rawXml = TestFixtures.createRealisticGpx(20)
        val bomBytes = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) + rawXml.toByteArray(Charsets.UTF_8)
        val parsed = GpxParser.parse(bomBytes)

        assertEquals("Kalcer Test Loop", parsed.trackName)
        assertEquals(20, parsed.points.size)
    }

    @Test
    fun testParseGpxWithoutTrkseg() {
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="Test">
  <trk>
    <name>Direct Points Track</name>
    <trkpt lat="-7.2500" lon="112.7500"><ele>10.0</ele></trkpt>
    <trkpt lat="-7.2510" lon="112.7510"><ele>15.0</ele></trkpt>
  </trk>
</gpx>""".trimIndent()
        val parsed = GpxParser.parse(xml.toByteArray(Charsets.UTF_8))
        assertEquals("Direct Points Track", parsed.trackName)
        assertEquals(2, parsed.points.size)
    }

    @Test
    fun testParseGpxWithRouteElements() {
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="Test">
  <rte>
    <name>Planned Route</name>
    <rtept lat="-7.2500" lon="112.7500"><ele>25.0</ele></rtept>
    <rtept lat="-7.2520" lon="112.7520"><ele>30.0</ele></rtept>
  </rte>
</gpx>""".trimIndent()
        val parsed = GpxParser.parse(xml.toByteArray(Charsets.UTF_8))
        assertEquals("Planned Route", parsed.trackName)
        assertEquals(2, parsed.points.size)
    }

    @Test
    fun testParseGpxWithTimezoneOffsetTimestamps() {
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="Test">
  <trk><trkseg>
    <trkpt lat="-7.2500" lon="112.7500">
      <ele>20.0</ele>
      <time>2026-10-08T07:30:00+07:00</time>
    </trkpt>
    <trkpt lat="-7.2510" lon="112.7510">
      <ele>22.0</ele>
      <time>2026-10-08T07:30:15+07:00</time>
    </trkpt>
  </trkseg></trk>
</gpx>""".trimIndent()
        val parsed = GpxParser.parse(xml.toByteArray(Charsets.UTF_8))
        assertEquals(2, parsed.points.size)
        assertNotNull(parsed.points[0].timestampEpochMillis)
        assertNotNull(parsed.points[1].timestampEpochMillis)
        assertEquals(15000L, parsed.points[1].timestampEpochMillis!! - parsed.points[0].timestampEpochMillis!!)
    }
}
