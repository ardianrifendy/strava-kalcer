import Foundation

public final class GpxParser: NSObject, XMLParserDelegate {
    
    private struct RawPoint {
        let lat: Double
        let lon: Double
        let ele: Double
    }
    
    private var rawPoints: [RawPoint] = []
    private var currentLat: Double?
    private var currentLon: Double?
    private var currentEle: Double?
    private var currentText = ""
    
    public static func parse(xmlData: Data) throws -> RouteTrack {
        let parser = GpxParser()
        let xmlParser = XMLParser(data: xmlData)
        xmlParser.delegate = parser
        
        guard xmlParser.parse() else {
            throw xmlParser.parserError ?? NSError(domain: "GpxParser", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to parse GPX XML"])
        }
        
        return parser.buildTrack()
    }
    
    public static func parse(xmlString: String) throws -> RouteTrack {
        guard let data = xmlString.data(using: .utf8) else {
            throw NSError(domain: "GpxParser", code: -2, userInfo: [NSLocalizedDescriptionKey: "Invalid string encoding"])
        }
        return try parse(xmlData: data)
    }
    
    // MARK: - XMLParserDelegate
    public func parser(
        _ parser: XMLParser,
        didStartElement elementName: String,
        namespaceURI: String?,
        qualifiedName qName: String?,
        attributes attributeDict: [String: String] = [:]
    ) {
        let tag = elementName.lowercased()
        if tag == "trkpt" || tag == "wpt" || tag == "rtept" {
            if let latStr = attributeDict["lat"], let lat = Double(latStr),
               let lonStr = attributeDict["lon"], let lon = Double(lonStr) {
                currentLat = lat
                currentLon = lon
                currentEle = 0.0
            }
        } else if tag == "ele" {
            currentText = ""
        }
    }
    
    public func parser(_ parser: XMLParser, foundCharacters string: String) {
        currentText += string
    }
    
    public func parser(
        _ parser: XMLParser,
        didEndElement elementName: String,
        namespaceURI: String?,
        qualifiedName qName: String?
    ) {
        let tag = elementName.lowercased()
        if tag == "ele" {
            if let ele = Double(currentText.trimmingCharacters(in: .whitespacesAndNewlines)) {
                currentEle = ele
            }
        } else if tag == "trkpt" || tag == "wpt" || tag == "rtept" {
            if let lat = currentLat, let lon = currentLon {
                rawPoints.append(RawPoint(lat: lat, lon: lon, ele: currentEle ?? 0.0))
            }
            currentLat = nil
            currentLon = nil
            currentEle = nil
        }
    }
    
    private func buildTrack() -> RouteTrack {
        let n = rawPoints.count
        if n == 0 {
            return RouteTrack(points: [], totalDistanceMeters: 0, elevationGainMeters: 0, elevationLossMeters: 0, minElevation: 0, maxElevation: 0)
        }
        
        var points: [RoutePoint] = []
        var totalDist = 0.0
        var totalGain = 0.0
        var totalLoss = 0.0
        var minEle = rawPoints[0].ele
        var maxEle = rawPoints[0].ele
        
        var rawGradients = [Double](repeating: 0.0, count: n)
        var segmentDistances = [Double](repeating: 0.0, count: n)
        var distanceAccum = [Double](repeating: 0.0, count: n)
        
        for i in 0..<n {
            let p = rawPoints[i]
            minEle = min(minEle, p.ele)
            maxEle = max(maxEle, p.ele)
            
            if i > 0 {
                let prev = rawPoints[i - 1]
                let seg = GeoMath.haversineDistance(lat1: prev.lat, lon1: prev.lon, lat2: p.lat, lon2: p.lon)
                segmentDistances[i] = seg
                totalDist += seg
                distanceAccum[i] = totalDist
                
                let eleDiff = p.ele - prev.ele
                if eleDiff > 0 { totalGain += eleDiff } else { totalLoss += abs(eleDiff) }
                
                if seg > 0.5 {
                    rawGradients[i] = (eleDiff / seg) * 100.0
                }
            }
        }
        
        let smoothedGradients = GeoMath.smoothGradients(rawGradients: rawGradients, halfWindow: 3)
        
        for i in 0..<n {
            points.append(
                RoutePoint(
                    index: i,
                    latitude: rawPoints[i].lat,
                    longitude: rawPoints[i].lon,
                    elevation: rawPoints[i].ele,
                    segmentDistanceMeters: segmentDistances[i],
                    distanceFromStartMeters: distanceAccum[i],
                    rawGradient: rawGradients[i],
                    smoothedGradient: smoothedGradients[i]
                )
            )
        }
        
        return RouteTrack(
            points: points,
            totalDistanceMeters: totalDist,
            elevationGainMeters: totalGain,
            elevationLossMeters: totalLoss,
            minElevation: minEle,
            maxElevation: maxEle
        )
    }
    
    /// Reverses the route direction: start becomes end, with recalculated distances, gradients, and elevation gain/loss
    public static func reverseTrack(_ track: RouteTrack) -> RouteTrack {
        let original = track.points
        let n = original.count
        if n <= 1 { return track }
        
        let reversedRaw = original.reversed()
        var newPoints: [RoutePoint] = []
        var totalDist = 0.0
        var totalGain = 0.0
        var totalLoss = 0.0
        var minEle = reversedRaw.first!.elevation
        var maxEle = reversedRaw.first!.elevation
        
        var rawGradients = [Double](repeating: 0.0, count: n)
        var segmentDistances = [Double](repeating: 0.0, count: n)
        var distanceAccum = [Double](repeating: 0.0, count: n)
        
        let rawArr = Array(reversedRaw)
        
        for i in 0..<n {
            let p = rawArr[i]
            minEle = min(minEle, p.elevation)
            maxEle = max(maxEle, p.elevation)
            
            if i > 0 {
                let prev = rawArr[i - 1]
                let seg = GeoMath.haversineDistance(lat1: prev.latitude, lon1: prev.longitude, lat2: p.latitude, lon2: p.longitude)
                segmentDistances[i] = seg
                totalDist += seg
                distanceAccum[i] = totalDist
                
                let eleDiff = p.elevation - prev.elevation
                if eleDiff > 0 { totalGain += eleDiff } else { totalLoss += abs(eleDiff) }
                
                if seg > 0.5 {
                    rawGradients[i] = (eleDiff / seg) * 100.0
                }
            }
        }
        
        let smoothedGradients = GeoMath.smoothGradients(rawGradients: rawGradients, halfWindow: 3)
        
        for i in 0..<n {
            newPoints.append(
                RoutePoint(
                    index: i,
                    latitude: rawArr[i].latitude,
                    longitude: rawArr[i].longitude,
                    elevation: rawArr[i].elevation,
                    segmentDistanceMeters: segmentDistances[i],
                    distanceFromStartMeters: distanceAccum[i],
                    rawGradient: rawGradients[i],
                    smoothedGradient: smoothedGradients[i]
                )
            )
        }
        
        return RouteTrack(
            points: newPoints,
            totalDistanceMeters: totalDist,
            elevationGainMeters: totalGain,
            elevationLossMeters: totalLoss,
            minElevation: minEle,
            maxElevation: maxEle
        )
    }
}
