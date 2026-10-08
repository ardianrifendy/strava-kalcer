package com.stravakalcer.gpx

import java.io.InputStream
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node

data class RawGpxPoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double?,
    val timestampEpochMillis: Long?,
    val segmentIndex: Int
)

data class ParsedGpxData(
    val trackName: String,
    val points: List<RawGpxPoint>,
    val segmentCount: Int
)

object GpxParser {

    /**
     * Parses an input stream of GPX XML content.
     * Uses standard DOM parsing with namespace tolerance.
     * Robust against invalid/empty/malformed GPX files.
     */
    fun parse(inputStream: InputStream): ParsedGpxData {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isValidating = false
            try {
                setFeature("http://xml.org/sax/features/namespaces", true)
                setFeature("http://xml.org/sax/features/validation", false)
                setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            } catch (_: Exception) {
                // Ignore unsupported parser feature settings
            }
        }

        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(inputStream)
        doc.documentElement.normalize()

        var trackName = "Imported Route"
        val nameNodes = doc.getElementsByTagName("name")
        if (nameNodes.length > 0) {
            val candidate = nameNodes.item(0).textContent.trim()
            if (candidate.isNotEmpty()) trackName = candidate
        }

        val rawPoints = mutableListOf<RawGpxPoint>()
        var segmentIndex = 0

        // Look for track segments
        val trkSegNodes = doc.getElementsByTagName("trkseg")
        if (trkSegNodes.length > 0) {
            for (i in 0 until trkSegNodes.length) {
                val segNode = trkSegNodes.item(i)
                parsePointsFromNode(segNode, segmentIndex, rawPoints)
                segmentIndex++
            }
        } else {
            // Check for direct trkpt or rtept elements
            val trkptNodes = doc.getElementsByTagName("trkpt")
            if (trkptNodes.length > 0) {
                parsePointsFromNode(doc.documentElement, 0, rawPoints)
                segmentIndex = 1
            } else {
                val rteptNodes = doc.getElementsByTagName("rtept")
                if (rteptNodes.length > 0) {
                    parseRoutePointsFromNode(doc.documentElement, 0, rawPoints)
                    segmentIndex = 1
                }
            }
        }

        return ParsedGpxData(
            trackName = trackName,
            points = rawPoints,
            segmentCount = maxOf(1, segmentIndex)
        )
    }

    private fun parsePointsFromNode(parentNode: Node, segmentIndex: Int, outList: MutableList<RawGpxPoint>) {
        val childNodes = parentNode.childNodes
        for (i in 0 until childNodes.length) {
            val child = childNodes.item(i)
            if (child.nodeType == Node.ELEMENT_NODE && (child.nodeName == "trkpt" || child.localName == "trkpt")) {
                val el = child as Element
                val latStr = el.getAttribute("lat")
                val lonStr = el.getAttribute("lon")
                val lat = latStr.toDoubleOrNull()
                val lon = lonStr.toDoubleOrNull()

                // Validate coordinate ranges
                if (lat != null && lon != null && lat >= -90.0 && lat <= 90.0 && lon >= -180.0 && lon <= 180.0) {
                    var elevation: Double? = null
                    var timestampMillis: Long? = null

                    val innerChildren = el.childNodes
                    for (j in 0 until innerChildren.length) {
                        val inner = innerChildren.item(j)
                        val name = inner.localName ?: inner.nodeName
                        when (name.lowercase()) {
                            "ele" -> {
                                elevation = inner.textContent.trim().toDoubleOrNull()
                            }
                            "time" -> {
                                val timeStr = inner.textContent.trim()
                                try {
                                    val instant = Instant.parse(timeStr)
                                    timestampMillis = instant.toEpochMilli()
                                } catch (_: DateTimeParseException) {
                                    // Timestamp format unparseable, ignore
                                }
                            }
                        }
                    }

                    outList.add(
                        RawGpxPoint(
                            latitude = lat,
                            longitude = lon,
                            elevation = elevation,
                            timestampEpochMillis = timestampMillis,
                            segmentIndex = segmentIndex
                        )
                    )
                }
            }
        }
    }

    private fun parseRoutePointsFromNode(parentNode: Node, segmentIndex: Int, outList: MutableList<RawGpxPoint>) {
        val childNodes = parentNode.childNodes
        for (i in 0 until childNodes.length) {
            val child = childNodes.item(i)
            if (child.nodeType == Node.ELEMENT_NODE && (child.nodeName == "rtept" || child.localName == "rtept")) {
                val el = child as Element
                val lat = el.getAttribute("lat").toDoubleOrNull()
                val lon = el.getAttribute("lon").toDoubleOrNull()
                if (lat != null && lon != null && lat in -90.0..90.0 && lon in -180.0..180.0) {
                    var elevation: Double? = null
                    val innerChildren = el.childNodes
                    for (j in 0 until innerChildren.length) {
                        val inner = innerChildren.item(j)
                        val name = inner.localName ?: inner.nodeName
                        if (name.equals("ele", ignoreCase = true)) {
                            elevation = inner.textContent.trim().toDoubleOrNull()
                        }
                    }
                    outList.add(
                        RawGpxPoint(
                            latitude = lat,
                            longitude = lon,
                            elevation = elevation,
                            timestampEpochMillis = null,
                            segmentIndex = segmentIndex
                        )
                    )
                }
            }
        }
    }
}
