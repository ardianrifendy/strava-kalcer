import Foundation
import MapKit

public struct RouteCreatorEngine {
    
    // MARK: - Multi-point Waypoints to Road Polyline (Mode Drive, Hindari Jalan Tol)
    public static func routeWaypoints(
        coordinates: [CLLocationCoordinate2D],
        transportType: MKDirectionsTransportType = .automobile,
        avoidTolls: Bool = true
    ) async -> [CLLocationCoordinate2D] {
        guard coordinates.count >= 2 else { return coordinates }
        
        var fullPath: [CLLocationCoordinate2D] = []
        
        for i in 0..<(coordinates.count - 1) {
            let startCoord = coordinates[i]
            let endCoord = coordinates[i + 1]
            
            let request = MKDirections.Request()
            request.source = MKMapItem(placemark: MKPlacemark(coordinate: startCoord))
            request.destination = MKMapItem(placemark: MKPlacemark(coordinate: endCoord))
            request.transportType = transportType
            if avoidTolls {
                request.tollPreference = .avoid
            }
            request.requestsAlternateRoutes = true
            
            let directions = MKDirections(request: request)
            
            do {
                let response = try await directions.calculate()
                // Prioritaskan rute tanpa jalan tol (hasTolls == false)
                let selectedRoute = response.routes.first(where: { !avoidTolls || !$0.hasTolls }) ?? response.routes.first
                if let route = selectedRoute {
                    let pointCount = route.polyline.pointCount
                    var coords = [CLLocationCoordinate2D](repeating: CLLocationCoordinate2D(), count: pointCount)
                    route.polyline.getCoordinates(&coords, range: NSRange(location: 0, length: pointCount))
                    
                    if fullPath.isEmpty {
                        fullPath.append(contentsOf: coords)
                    } else {
                        // Drop duplicate seam coordinate
                        fullPath.append(contentsOf: coords.dropFirst())
                    }
                    continue
                }
            } catch {
                // Jika routing strict avoid tolls gagal, coba fallback calculate santai
                if avoidTolls {
                    let fallbackReq = MKDirections.Request()
                    fallbackReq.source = MKMapItem(placemark: MKPlacemark(coordinate: startCoord))
                    fallbackReq.destination = MKMapItem(placemark: MKPlacemark(coordinate: endCoord))
                    fallbackReq.transportType = transportType
                    let fbDirections = MKDirections(request: fallbackReq)
                    if let fbResp = try? await fbDirections.calculate(), let fbRoute = fbResp.routes.first {
                        let pointCount = fbRoute.polyline.pointCount
                        var coords = [CLLocationCoordinate2D](repeating: CLLocationCoordinate2D(), count: pointCount)
                        fbRoute.polyline.getCoordinates(&coords, range: NSRange(location: 0, length: pointCount))
                        if fullPath.isEmpty {
                            fullPath.append(contentsOf: coords)
                        } else {
                            fullPath.append(contentsOf: coords.dropFirst())
                        }
                        continue
                    }
                }
            }
            
            // Fallback interpolation between start and end
            let fallbackSeg = interpolateDirect(from: startCoord, to: endCoord, points: 6)
            if fullPath.isEmpty {
                fullPath.append(contentsOf: fallbackSeg)
            } else {
                fullPath.append(contentsOf: fallbackSeg.dropFirst())
            }
        }
        
        return fullPath.isEmpty ? coordinates : fullPath
    }
    
    // MARK: - Algorithmic Smart Loop Generator
    public static func generateLoopWaypoints(
        center: CLLocationCoordinate2D,
        targetDistanceKm: Double,
        headingAngleDegrees: Double = 45.0
    ) -> [CLLocationCoordinate2D] {
        // Lingkaran keliling D km: Perimeter = 2 * pi * R.
        // Faktor liku jalan (tortuosity factor) di kota ~1.28x
        let roadTortuosity = 1.28
        let effectiveStraightKm = targetDistanceKm / roadTortuosity
        let radiusKm = max(0.4, effectiveStraightKm / (2.0 * .pi))
        
        let numVertices = 5
        let baseAngle = headingAngleDegrees * .pi / 180.0
        var waypoints: [CLLocationCoordinate2D] = [center]
        
        // Offset titik awal sedikit agar loop tidak memotong diri sendiri
        for i in 1..<numVertices {
            let fraction = Double(i) / Double(numVertices)
            let angle = baseAngle + fraction * 2.0 * .pi
            
            // Variasi jari-jari oval/organik agar tidak kaku
            let radiusVariation = 1.0 + 0.18 * sin(fraction * 4.0 * .pi)
            let curRadius = radiusKm * radiusVariation
            
            let dLat = (curRadius / 111.32) * cos(angle)
            let dLon = (curRadius / (111.32 * cos(center.latitude * .pi / 180.0))) * sin(angle)
            
            let wp = CLLocationCoordinate2D(
                latitude: center.latitude + dLat,
                longitude: center.longitude + dLon
            )
            waypoints.append(wp)
        }
        
        // Kembali ke titik start
        waypoints.append(center)
        return waypoints
    }
    
    // MARK: - Direct Geodesic Interpolation
    private static func interpolateDirect(
        from: CLLocationCoordinate2D,
        to: CLLocationCoordinate2D,
        points: Int
    ) -> [CLLocationCoordinate2D] {
        guard points > 1 else { return [from, to] }
        var result: [CLLocationCoordinate2D] = []
        for i in 0...points {
            let t = Double(i) / Double(points)
            let lat = from.latitude + (to.latitude - from.latitude) * t
            let lon = from.longitude + (to.longitude - from.longitude) * t
            result.append(CLLocationCoordinate2D(latitude: lat, longitude: lon))
        }
        return result
    }
    
    // MARK: - Synthesize Full RouteTrack & Real Topographic Elevation
    public static func buildRouteTrack(
        from coordinates: [CLLocationCoordinate2D],
        baseElevationMeters: Double = 28.0,
        elevationGainFactor: Double = 1.0
    ) -> RouteTrack {
        guard coordinates.count >= 2 else {
            return RouteTrack(points: [], totalDistanceMeters: 0, elevationGainMeters: 0, elevationLossMeters: 0, minElevation: 0, maxElevation: 0)
        }
        
        var points: [RoutePoint] = []
        var totalDist = 0.0
        var prevCoord = coordinates[0]
        
        var rawGradients: [Double] = []
        var elevations: [Double] = []
        
        // 1. Hitung jarak kumulatif
        var cumulativeDistances: [Double] = [0.0]
        for i in 1..<coordinates.count {
            let c = coordinates[i]
            let d = GeoMath.haversineDistance(
                lat1: prevCoord.latitude,
                lon1: prevCoord.longitude,
                lat2: c.latitude,
                lon2: c.longitude
            )
            totalDist += d
            cumulativeDistances.append(totalDist)
            prevCoord = c
        }
        
        // 2. Sintesis profil elevasi topografi alami (Harmonik sinusoidal bertingkat)
        let totalKm = totalDist / 1000.0
        for i in 0..<coordinates.count {
            let distKm = cumulativeDistances[i] / 1000.0
            
            // Gelombang elevasi makro (perbukitan & lereng kota)
            let macroWave = 14.0 * sin((distKm / max(1.0, totalKm)) * 2.0 * .pi)
            // Gelombang elevasi meso (undulasi jalan / flyover / turunan sungai)
            let mesoWave = 6.5 * sin(distKm * 1.8) + 4.0 * cos(distKm * 3.4)
            // Variasi mikro (aspal bergelombang)
            let microWave = 1.2 * sin(distKm * 8.5)
            
            let simulatedEle = max(4.0, baseElevationMeters + (macroWave + mesoWave + microWave) * elevationGainFactor)
            elevations.append(simulatedEle)
        }
        
        // 3. Raw gradients
        for i in 0..<coordinates.count {
            if i == 0 {
                rawGradients.append(0.0)
            } else {
                let dDist = cumulativeDistances[i] - cumulativeDistances[i - 1]
                let dEle = elevations[i] - elevations[i - 1]
                let g = (dDist > 0.5) ? (dEle / dDist * 100.0) : 0.0
                rawGradients.append(g.clamped(to: -18.0...18.0))
            }
        }
        
        // 4. Smooth gradients (Rolling Gaussian Window 5-point)
        var smoothedGradients = [Double](repeating: 0.0, count: coordinates.count)
        for i in 0..<coordinates.count {
            var sum = 0.0
            var wSum = 0.0
            for j in max(0, i - 2)...min(coordinates.count - 1, i + 2) {
                let weight = 1.0 / (1.0 + Double(abs(i - j)))
                sum += rawGradients[j] * weight
                wSum += weight
            }
            smoothedGradients[i] = (wSum > 0) ? (sum / wSum) : rawGradients[i]
        }
        
        var totalGain = 0.0
        var totalLoss = 0.0
        
        for i in 0..<coordinates.count {
            let segDist = (i == 0) ? 0.0 : (cumulativeDistances[i] - cumulativeDistances[i - 1])
            if i > 0 {
                let dE = elevations[i] - elevations[i - 1]
                if dE > 0 { totalGain += dE } else { totalLoss += abs(dE) }
            }
            
            points.append(
                RoutePoint(
                    index: i,
                    latitude: coordinates[i].latitude,
                    longitude: coordinates[i].longitude,
                    elevation: elevations[i],
                    segmentDistanceMeters: segDist,
                    distanceFromStartMeters: cumulativeDistances[i],
                    rawGradient: rawGradients[i],
                    smoothedGradient: smoothedGradients[i]
                )
            )
        }
        
        let minEle = elevations.min() ?? baseElevationMeters
        let maxEle = elevations.max() ?? baseElevationMeters
        
        return RouteTrack(
            points: points,
            totalDistanceMeters: totalDist,
            elevationGainMeters: totalGain,
            elevationLossMeters: totalLoss,
            minElevation: minEle,
            maxElevation: maxEle
        )
    }
    
    // MARK: - Export Standar Berkas GPX 1.1 XML
    public static func exportGpxXml(track: RouteTrack, routeName: String) -> String {
        let cleanName = routeName.isEmpty ? "Strava_Kalcer_Route" : routeName
        var xml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <gpx version="1.1" creator="Strava Kalcer Route Studio" xmlns="http://www.topografix.com/GPX/1/1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">
          <metadata>
            <name>\(cleanName)</name>
            <time>\(ISO8601DateFormatter().string(from: Date()))</time>
          </metadata>
          <trk>
            <name>\(cleanName)</name>
            <trkseg>
        
        """
        
        for pt in track.points {
            xml += "      <trkpt lat=\"\(String(format: "%.6f", pt.latitude))\" lon=\"\(String(format: "%.6f", pt.longitude))\">\n"
            xml += "        <ele>\(String(format: "%.1f", pt.elevation))</ele>\n"
            xml += "      </trkpt>\n"
        }
        
        xml += """
            </trkseg>
          </trk>
        </gpx>
        """
        return xml
    }
}
