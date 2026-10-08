package com.stravakalcer

import com.stravakalcer.debug.DebugExporter
import com.stravakalcer.device.DeviceProfileRegistry
import com.stravakalcer.fit.FitGenerator
import com.stravakalcer.fit.FitValidator
import com.stravakalcer.gpx.GpxParser
import com.stravakalcer.gpx.RouteProcessor
import com.stravakalcer.model.*
import com.stravakalcer.simulation.SimulationEngine
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream

class FitEngineTest {

    @Test
    fun testFitGenerationAndParseBackValidation() {
        val xml = TestFixtures.createRealisticGpx(80)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.FONDO,
            targetAverageSpeedKmh = 27.5,
            requestedMaxSpeedKmh = 48.0,
            hrConfig = HeartRateConfig(enabled = true),
            cadenceConfig = CadenceConfig(enabled = true)
        )

        val result = SimulationEngine.simulate(track, settings)
        val profile = DeviceProfileRegistry.getProfileById("garmin_edge_1040")

        // 1. Generate binary FIT bytes
        val fitBytes = FitGenerator.generateFitBytes(result, profile)
        assertTrue(fitBytes.isNotEmpty())
        assertTrue("FIT header should be at least 14 bytes", fitBytes.size > 100)

        // 2. Parse-back and validate
        val report = FitValidator.validate(fitBytes, result)

        if (!report.isValid) {
            fail("FIT validation failed with errors: ${report.errors.joinToString("; ")}")
        }

        assertTrue(report.isValid)
        assertTrue(report.hasFileId)
        assertTrue(report.hasActivity)
        assertTrue(report.hasSession)
        assertTrue(report.hasLap)
        assertTrue(report.timestampsMonotonic)
        assertTrue(report.coordinatesValid)
        assertTrue(report.distanceMonotonic)
        assertTrue(report.speedValid)
        assertTrue(report.summaryMetricsConsistent)
        assertEquals(result.points.size, report.totalRecordsParsed)
    }

    @Test
    fun testDebugCsvExportFormatting() {
        val xml = TestFixtures.createRealisticGpx(10)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            targetAverageSpeedKmh = 28.0
        )
        val result = SimulationEngine.simulate(track, settings)

        val csv = DebugExporter.exportToCsv(result)
        val lines = csv.trim().lines()

        assertTrue(lines.size >= 11) // Header + 10 points
        val header = lines.first()
        assertTrue(header.startsWith("index,latitude,longitude,elevation"))
        assertTrue(header.contains("terrainClass"))
        assertTrue(header.contains("baselineSpeed"))
        assertTrue(header.contains("finalSpeed"))
        assertTrue(header.contains("stopState"))
    }

    @Test
    fun testGenerateSampleDeliverables() {
        val samplesDir = java.io.File("../samples")
        samplesDir.mkdirs()

        val gpxXml = com.stravakalcer.sample.SampleRouteGenerator.createRealisticGpx(150)
        java.io.File(samplesDir, "sample_loop.gpx").writeText(gpxXml)

        val parsed = GpxParser.parse(ByteArrayInputStream(gpxXml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val settings = SimulationSettings(
            sport = SportType.CYCLING,
            profile = ActivityProfile.ENDURANCE,
            targetAverageSpeedKmh = 28.5,
            requestedMaxSpeedKmh = 52.0,
            hrConfig = HeartRateConfig(enabled = true, restingHr = 58, maxHr = 188),
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = 88)
        )
        val result = SimulationEngine.simulate(track, settings)
        val profile = DeviceProfileRegistry.getProfileById("garmin_edge_1040")

        val fitBytes = FitGenerator.generateFitBytes(result, profile)
        val validation = FitValidator.validate(fitBytes, result)
        assertTrue(validation.isValid)

        java.io.File(samplesDir, "sample_validated.fit").writeBytes(fitBytes)
        java.io.File(samplesDir, "sample_diagnostic.csv").writeText(DebugExporter.exportToCsv(result))
    }
}
