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

    @Test
    fun testRunningCadenceFitEncodingStravaCompatibility() {
        val xml = TestFixtures.createRealisticGpx(40)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val targetSpm = 168
        val settings = SimulationSettings(
            sport = SportType.RUNNING,
            profile = ActivityProfile.TEMPO_RUN,
            targetAveragePaceSecondsPerKm = 330.0,
            cadenceConfig = CadenceConfig(enabled = true, baseCadenceRpm = targetSpm)
        )
        val result = SimulationEngine.simulate(track, settings)
        val profile = DeviceProfileRegistry.getProfileById("garmin_forerunner_965")

        val fitBytes = FitGenerator.generateFitBytes(result, profile)
        val validation = FitValidator.validate(fitBytes, result)
        assertTrue(validation.isValid)

        // Decode FIT records and verify that cadence is encoded in strides/min (RPM = SPM / 2)
        val decode = com.garmin.fit.Decode()
        val broadcaster = com.garmin.fit.MesgBroadcaster()
        val parsedRecords = mutableListOf<com.garmin.fit.RecordMesg>()
        var sessionAvgCadence: Short? = null

        broadcaster.addListener(com.garmin.fit.RecordMesgListener { mesg ->
            parsedRecords.add(mesg)
        })
        broadcaster.addListener(com.garmin.fit.SessionMesgListener { mesg ->
            sessionAvgCadence = mesg.avgCadence
        })

        decode.read(ByteArrayInputStream(fitBytes), broadcaster)
        assertTrue(parsedRecords.isNotEmpty())

        // In FIT, running cadence field is RPM (strides/min, ~targetSpm / 2 = 84).
        // Strava reads this and multiplies by 2: stravaSpm = (cadence + fractionalCadence) * 2
        for (rec in parsedRecords) {
            val rawFitCadence = rec.cadence?.toInt() ?: 0
            val frac = rec.fractionalCadence ?: 0.0f
            assertTrue("FIT cadence field for running must be stored in strides/min (RPM), NOT raw SPM (was $rawFitCadence)",
                rawFitCadence in 60..110)

            val stravaReconstructedSpm = ((rawFitCadence + frac) * 2).toInt()
            assertTrue("Reconstructed Strava SPM must be in realistic running range (~140-195), NOT 300+ (was $stravaReconstructedSpm)",
                stravaReconstructedSpm in 140..195)
        }

        assertNotNull(sessionAvgCadence)
        assertTrue("Session avg cadence in FIT must be strides/min (~84)", sessionAvgCadence!!.toInt() in 70..95)
        val stravaSessionAvgSpm = sessionAvgCadence!!.toInt() * 2
        assertTrue("Strava session avg SPM must be around 168, NOT 300+ (was $stravaSessionAvgSpm)", stravaSessionAvgSpm in 150..185)
    }

    @Test
    fun testGarminDeviceMetadataStravaCompatibility() {
        val xml = TestFixtures.createRealisticGpx(20)
        val parsed = GpxParser.parse(ByteArrayInputStream(xml.toByteArray()))
        val track = RouteProcessor.process(parsed)

        val settings = SimulationSettings(
            sport = SportType.RUNNING,
            profile = ActivityProfile.EASY_RUN,
            targetAveragePaceSecondsPerKm = 360.0
        )
        val result = SimulationEngine.simulate(track, settings)
        val fr965Profile = DeviceProfileRegistry.getProfileById("garmin_forerunner_965")

        assertEquals("Manufacturer must be Garmin", com.garmin.fit.Manufacturer.GARMIN, fr965Profile.manufacturerId)
        assertEquals("FR965 product ID must be 4315, NOT 4105 (MARQ 2)", com.garmin.fit.GarminProduct.FR965, fr965Profile.productNumber)

        val fitBytes = FitGenerator.generateFitBytes(result, fr965Profile)

        val decode = com.garmin.fit.Decode()
        val broadcaster = com.garmin.fit.MesgBroadcaster()
        var parsedFileId: com.garmin.fit.FileIdMesg? = null
        val parsedDeviceInfo = mutableListOf<com.garmin.fit.DeviceInfoMesg>()

        broadcaster.addListener(com.garmin.fit.FileIdMesgListener { mesg ->
            parsedFileId = mesg
        })
        broadcaster.addListener(com.garmin.fit.DeviceInfoMesgListener { mesg ->
            parsedDeviceInfo.add(mesg)
        })

        decode.read(ByteArrayInputStream(fitBytes), broadcaster)

        assertNotNull("FileIdMesg must be present", parsedFileId)
        assertEquals(com.garmin.fit.Manufacturer.GARMIN, parsedFileId!!.manufacturer)
        assertEquals(com.garmin.fit.GarminProduct.FR965, parsedFileId!!.product)
        assertEquals(com.garmin.fit.GarminProduct.FR965, parsedFileId!!.garminProduct)
        assertEquals("Forerunner 965", parsedFileId!!.productName)

        val creatorDevice = parsedDeviceInfo.firstOrNull { it.deviceIndex == com.garmin.fit.DeviceIndex.CREATOR }
        assertNotNull("Creator DeviceInfoMesg must be present", creatorDevice)
        assertEquals(com.garmin.fit.Manufacturer.GARMIN, creatorDevice!!.manufacturer)
        assertEquals(com.garmin.fit.GarminProduct.FR965, creatorDevice.product)
        assertEquals(com.garmin.fit.GarminProduct.FR965, creatorDevice.garminProduct)
        assertEquals("Forerunner 965", creatorDevice.productName)
        assertEquals(com.garmin.fit.SourceType.LOCAL, creatorDevice.sourceType)
    }
}

