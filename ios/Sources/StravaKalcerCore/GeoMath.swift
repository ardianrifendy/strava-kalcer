import Foundation

public struct GeoMath {
    public static let earthRadiusMeters = 6_371_000.0
    public static let semicircleFactor = 2147483648.0 / 180.0 // 2^31 / 180
    
    public static func haversineDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ) -> Double {
        let dLat = (lat2 - lat1) * .pi / 180.0
        let dLon = (lon2 - lon1) * .pi / 180.0
        let lat1Rad = lat1 * .pi / 180.0
        let lat2Rad = lat2 * .pi / 180.0
        
        let a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2) * sin(dLon / 2)
        let c = 2 * atan2(sqrt(a), sqrt(max(0.0, 1.0 - a)))
        return earthRadiusMeters * c
    }
    
    public static func haversineDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ) -> Double {
        haversineDistance(lat1: lat1, lon1: lon1, lat2: lat2, lon2: lon2)
    }
    
    public static func bearingDegrees(
        fromLat lat1: Double, fromLon lon1: Double,
        toLat lat2: Double, toLon lon2: Double
    ) -> Double {
        let y = sin((lon2 - lon1) * .pi / 180.0) * cos(lat2 * .pi / 180.0)
        let x = cos(lat1 * .pi / 180.0) * sin(lat2 * .pi / 180.0) -
                sin(lat1 * .pi / 180.0) * cos(lat2 * .pi / 180.0) * cos((lon2 - lon1) * .pi / 180.0)
        let rad = atan2(y, x)
        let deg = rad * 180.0 / .pi
        return (deg + 360.0).truncatingRemainder(dividingBy: 360.0)
    }
    
    public static func mpsToKmh(_ mps: Double) -> Double {
        mps * 3.6
    }
    
    public static func kmhToMps(_ kmh: Double) -> Double {
        kmh / 3.6
    }
    
    public static func mpsToPaceSecondsPerKm(_ mps: Double) -> Double {
        if mps < 0.2 { return 0.0 }
        return 1000.0 / mps
    }
    
    public static func kmhToPaceSecondsPerKm(_ kmh: Double) -> Double {
        if kmh < 0.5 { return 0.0 }
        return 3600.0 / kmh
    }
    
    public static func toSemicircles(_ degrees: Double) -> Int32 {
        Int32((degrees * semicircleFactor).rounded())
    }
    
    public static func fromSemicircles(_ semicircles: Int32) -> Double {
        Double(semicircles) / semicircleFactor
    }
    
    /// Smooth raw gradient values using symmetrical window moving average
    public static func smoothGradients(rawGradients: [Double], halfWindow: Int = 3) -> [Double] {
        let n = rawGradients.count
        if n == 0 { return [] }
        var result = [Double](repeating: 0.0, count: n)
        
        for i in 0..<n {
            let start = max(0, i - halfWindow)
            let end = min(n - 1, i + halfWindow)
            var sum = 0.0
            for j in start...end {
                sum += rawGradients[j]
            }
            result[i] = sum / Double(end - start + 1)
        }
        return result
    }
}
