package com.stravakalcer.gpx

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Instant
import java.time.OffsetDateTime
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
     * Strips UTF-8 BOM (Byte Order Mark) if present.
     */
    fun stripBom(bytes: ByteArray): ByteArray {
        if (bytes.size >= 3 &&
            bytes[0] == 0xEF.toByte() &&
            bytes[1] == 0xBB.toByte() &&
            bytes[2] == 0xBF.toByte()
        ) {
            return bytes.copyOfRange(3, bytes.size)
        }
        return bytes
    }

    /**
     * Parses a byte array of GPX XML content.
     */
    fun parse(bytes: ByteArray): ParsedGpxData {
        val cleanBytes = stripBom(bytes)
        return parseInternal(ByteArrayInputStream(cleanBytes))
    }

    /**
     * Parses an input stream of GPX XML content.
     * Reads all bytes immediately to prevent "stream closed" race conditions.
     */
    fun parse(inputStream: InputStream): ParsedGpxData {
        val bytes = inputStream.readBytes()
        return parse(bytes)
    }

    private fun parseInternal(inputStream: InputStream): ParsedGpxData {
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

        // 1. Check for track segments (<trkseg>)
        val trkSegNodes = doc.getElementsByTagName("trkseg")
        if (trkSegNodes.length > 0) {
            for (i in 0 until trkSegNodes.length) {
                val segNode = trkSegNodes.item(i)
                parsePointsFromContainer(segNode, segmentIndex, "trkpt", rawPoints)
                segmentIndex++
            }
        } else {
            // 2. Check for tracks without segments (<trk>)
            val trkNodes = doc.getElementsByTagName("trk")
            if (trkNodes.length > 0) {
                for (i in 0 until trkNodes.length) {
                    val trkNode = trkNodes.item(i)
                    parsePointsFromContainer(trkNode, segmentIndex, "trkpt", rawPoints)
                    segmentIndex++
                }
            } else {
                // 3. Check for standalone trkpt anywhere in doc
                val trkptNodes = doc.getElementsByTagName("trkpt")
                if (trkptNodes.length > 0) {
                    for (i in 0 until trkptNodes.length) {
                        parseSinglePointElement(trkptNodes.item(i) as Element, 0)?.let { rawPoints.add(it) }
                    }
                    segmentIndex = 1
                } else {
                    // 4. Check for route elements (<rte> / <rtept>)
                    val rteNodes = doc.getElementsByTagName("rte")
                    if (rteNodes.length > 0) {
                        for (i in 0 until rteNodes.length) {
                            val rteNode = rteNodes.item(i)
                            parsePointsFromContainer(rteNode, segmentIndex, "rtept", rawPoints)
                            segmentIndex++
                        }
                    } else {
                        val rteptNodes = doc.getElementsByTagName("rtept")
                        if (rteptNodes.length > 0) {
                            for (i in 0 until rteptNodes.length) {
                                parseSinglePointElement(rteptNodes.item(i) as Element, 0)?.let { rawPoints.add(it) }
                            }
                            segmentIndex = 1
                        } else {
                            // 5. Check for waypoints (<wpt>)
                            val wptNodes = doc.getElementsByTagName("wpt")
                            if (wptNodes.length > 0) {
                                for (i in 0 until wptNodes.length) {
                                    parseSinglePointElement(wptNodes.item(i) as Element, 0)?.let { rawPoints.add(it) }
                                }
                                segmentIndex = 1
                            }
                        }
                    }
                }
            }
        }

        return ParsedGpxData(
            trackName = trackName,
            points = rawPoints,
            segmentCount = maxOf(1, segmentIndex)
        )
    }

    private fun parsePointsFromContainer(
        containerNode: Node,
        segmentIndex: Int,
        tagName: String,
        outList: MutableList<RawGpxPoint>
    ) {
        if (containerNode is Element) {
            val ptNodes = containerNode.getElementsByTagName(tagName)
            if (ptNodes.length > 0) {
                for (i in 0 until ptNodes.length) {
                    val el = ptNodes.item(i) as Element
                    parseSinglePointElement(el, segmentIndex)?.let { outList.add(it) }
                }
                return
            }
        }

        // Fallback: iterate immediate child nodes
        val childNodes = containerNode.childNodes
        for (i in 0 until childNodes.length) {
            val child = childNodes.item(i)
            if (child.nodeType == Node.ELEMENT_NODE && (child.nodeName.equals(tagName, ignoreCase = true) || child.localName.equals(tagName, ignoreCase = true))) {
                parseSinglePointElement(child as Element, segmentIndex)?.let { outList.add(it) }
            }
        }
    }

    private fun parseSinglePointElement(el: Element, segmentIndex: Int): RawGpxPoint? {
        val latStr = el.getAttribute("lat")
        val lonStr = el.getAttribute("lon")
        val lat = latStr.toDoubleOrNull() ?: return null
        val lon = lonStr.toDoubleOrNull() ?: return null

        if (lat !in -90.0..90.0 || lon !in -180.0..180.0) return null

        var elevation: Double? = null
        var timestampMillis: Long? = null

        val innerChildren = el.childNodes
        for (j in 0 until innerChildren.length) {
            val inner = innerChildren.item(j)
            if (inner.nodeType != Node.ELEMENT_NODE) continue
            val name = inner.localName ?: inner.nodeName
            when (name.lowercase()) {
                "ele" -> {
                    elevation = inner.textContent.trim().toDoubleOrNull()
                }
                "time" -> {
                    val timeStr = inner.textContent.trim()
                    try {
                        val instant = try {
                            Instant.parse(timeStr)
                        } catch (_: Exception) {
                            OffsetDateTime.parse(timeStr).toInstant()
                        }
                        timestampMillis = instant.toEpochMilli()
                    } catch (_: Exception) {
                        // Timestamp format unparseable, ignore
                    }
                }
            }
        }

        return RawGpxPoint(
            latitude = lat,
            longitude = lon,
            elevation = elevation,
            timestampEpochMillis = timestampMillis,
            segmentIndex = segmentIndex
        )
    }
}
