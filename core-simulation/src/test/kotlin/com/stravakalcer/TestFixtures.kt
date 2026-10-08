package com.stravakalcer

object TestFixtures {

    val EMPTY_GPX = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="StravaKalcer">
  <trk>
    <name>Empty Track</name>
    <trkseg></trkseg>
  </trk>
</gpx>""".trimIndent()

    val ONE_POINT_GPX = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="StravaKalcer">
  <trk>
    <name>One Point Track</name>
    <trkseg>
      <trkpt lat="-7.2575" lon="112.7521">
        <ele>10.0</ele>
        <time>2026-10-08T06:00:00Z</time>
      </trkpt>
    </trkseg>
  </trk>
</gpx>""".trimIndent()

    val ALL_ZERO_ELEVATION_GPX = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="StravaKalcer">
  <trk>
    <name>Zero Elevation Track</name>
    <trkseg>
      <trkpt lat="-7.2500" lon="112.7500"><ele>0.0</ele></trkpt>
      <trkpt lat="-7.2510" lon="112.7510"><ele>0.0</ele></trkpt>
      <trkpt lat="-7.2520" lon="112.7520"><ele>0.0</ele></trkpt>
    </trkseg>
  </trk>
</gpx>""".trimIndent()

    val MISSING_ELEVATION_GPX = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="StravaKalcer">
  <trk>
    <name>Missing Elevation Track</name>
    <trkseg>
      <trkpt lat="-7.2500" lon="112.7500"></trkpt>
      <trkpt lat="-7.2510" lon="112.7510"></trkpt>
      <trkpt lat="-7.2520" lon="112.7520"></trkpt>
    </trkseg>
  </trk>
</gpx>""".trimIndent()

    val MULTI_SEGMENT_GPX = """<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="StravaKalcer">
  <trk>
    <name>Multi Segment Track</name>
    <trkseg>
      <trkpt lat="-7.2500" lon="112.7500"><ele>10.0</ele></trkpt>
      <trkpt lat="-7.2510" lon="112.7510"><ele>12.0</ele></trkpt>
    </trkseg>
    <trkseg>
      <trkpt lat="-7.2600" lon="112.7600"><ele>15.0</ele></trkpt>
      <trkpt lat="-7.2610" lon="112.7610"><ele>17.0</ele></trkpt>
    </trkseg>
  </trk>
</gpx>""".trimIndent()

    /**
     * Generates a realistic synthetic route with flats, a climb section, and a downhill section.
     */
    fun createRealisticGpx(totalPoints: Int = 100): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gpx version="1.1" creator="StravaKalcer">""")
        sb.appendLine("""  <trk><name>Kalcer Test Loop</name><trkseg>""")

        var lat = -7.250000
        var lon = 112.750000
        var ele = 50.0

        for (i in 0 until totalPoints) {
            // Coordinate progression ~50m per step
            lat += 0.00045
            lon += 0.00030

            // Elevation profile:
            // 0..25: flat (~50m)
            // 26..55: climb (+5% to +8% gradient -> reaches ~180m)
            // 56..80: downhill (-6% to -10% gradient -> drops back to ~60m)
            // 81..99: flat (~60m)
            when {
                i in 25..55 -> ele += 4.5
                i in 56..80 -> ele -= 5.0
                else -> ele += (if (i % 2 == 0) 0.2 else -0.2)
            }

            sb.appendLine("""    <trkpt lat="${"%.6f".format(lat)}" lon="${"%.6f".format(lon)}">""")
            sb.appendLine("""      <ele>${"%.1f".format(ele)}</ele>""")
            sb.appendLine("""      <time>2026-10-08T06:00:${"%02d".format(i % 60)}Z</time>""")
            sb.appendLine("""    </trkpt>""")
        }

        sb.appendLine("""  </trkseg></trk>""")
        sb.appendLine("""</gpx>""")
        return sb.toString()
    }
}
