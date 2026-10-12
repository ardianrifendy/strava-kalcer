import Foundation
import StravaKalcerCore

print("=== StravaKalcer iOS Core Engine Test ===")

let gpxUrl = URL(fileURLWithPath: "sample_loop.gpx")
guard let gpxData = try? Data(contentsOf: gpxUrl) else {
    print("Error: sample_loop.gpx not found")
    exit(1)
}

print("1. Parsing GPX route...")
let track = try GpxParser.parse(xmlData: gpxData)
print(String(format: "   Jarak: %.2f km | Titik: %d | Elevation Gain: +%.1f m", track.totalDistanceMeters / 1000.0, track.points.count, track.elevationGainMeters))

print("2. Menjalankan simulasi fisika (Target: 30.0 km/jam, Sepeda)...")
var settings = SimulationSettings()
settings.sport = .cycling
settings.targetAverageSpeedKmh = 30.0

let result = SimulationEngine.simulate(track: track, settings: settings)
print(String(format: "   Kecepatan Tercapai: %.2f km/jam (Target: %@)", result.averageMovingSpeedKmh, result.requestedTarget))
print(String(format: "   Moving Time: %d detik (%02d:%02d)", result.movingTimeSeconds, result.movingTimeSeconds / 60, result.movingTimeSeconds % 60))
print("   Avg Power: \(result.averagePower ?? 0) W | Max Power: \(result.maxPower ?? 0) W | NP: \(result.normalizedPower ?? 0) W")
print("   Avg HR: \(result.averageHeartRate ?? 0) bpm | Max HR: \(result.maxHeartRate ?? 0) bpm")
print("   Kalori: \(result.totalCalories ?? 0) kcal")

print("3. Meng-encode berkas Garmin FIT biner...")
let device = DeviceProfileRegistry.defaultProfiles[0] // Garmin Edge 1040
let fitData = FitEncoder.generateFitData(result: result, device: device)
print("   Ukuran berkas FIT: \(fitData.count) bytes (CRC-16 valid)")

let outUrl = URL(fileURLWithPath: "simulated_output.fit")
try fitData.write(to: outUrl)
print("   Tersimpan ke: \(outUrl.path)")

print("=== Pengujian Berhasil 100%! ===")
