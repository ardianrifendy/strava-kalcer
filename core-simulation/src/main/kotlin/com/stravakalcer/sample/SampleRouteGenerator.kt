package com.stravakalcer.sample

object SampleRouteGenerator {

    /**
     * Generates a realistic synthetic loop route with flats, a climb section, and a downhill section.
     */
    fun createRealisticGpx(totalPoints: Int = 120): String {
        val sb = StringBuilder()
        sb.appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.appendLine("""<gpx version="1.1" creator="StravaKalcer">""")
        sb.appendLine("""  <trk><name>Kalcer Realistic Loop</name><trkseg>""")

        var lat = -7.250000
        var lon = 112.750000
        var ele = 50.0

        for (i in 0 until totalPoints) {
            lat += 0.00045
            lon += 0.00030

            // 0..30: flat
            // 31..70: sustained climb (+5% to +8%)
            // 71..100: downhill (-6% to -10%)
            // 101..end: flat recovery
            when {
                i in 30..70 -> ele += 4.2
                i in 71..100 -> ele -= 4.8
                else -> ele += (if (i % 2 == 0) 0.1 else -0.1)
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
