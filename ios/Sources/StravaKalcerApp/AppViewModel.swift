import Foundation
import SwiftUI

public enum AppTheme: String, CaseIterable, Identifiable {
    case dark = "Dark Mode"
    case light = "Light Mode"
    
    public var id: String { rawValue }
    
    public var isDark: Bool {
        self == .dark
    }
}

public enum AppScreen: Hashable {
    case home
    case routeStudio
    case routeReview
    case activitySettings
    case preview
    case export
}

public struct ArchivedActivity: Identifiable, Codable, Hashable {
    public let id: UUID
    public let fileName: String
    public let activityTitle: String
    public let athleteName: String
    public let sport: SportType
    public let date: Date
    public let dateText: String
    public let distanceMeters: Double
    public let elevationGainMeters: Double
    public let movingTimeSeconds: Int64
    public let averageSpeedKmh: Double
    public let deviceName: String
    public let fileSize: Int
    public let relativeFilePath: String
    
    public init(
        id: UUID = UUID(),
        fileName: String,
        activityTitle: String,
        athleteName: String,
        sport: SportType,
        date: Date,
        dateText: String,
        distanceMeters: Double,
        elevationGainMeters: Double,
        movingTimeSeconds: Int64,
        averageSpeedKmh: Double,
        deviceName: String,
        fileSize: Int,
        relativeFilePath: String
    ) {
        self.id = id
        self.fileName = fileName
        self.activityTitle = activityTitle
        self.athleteName = athleteName
        self.sport = sport
        self.date = date
        self.dateText = dateText
        self.distanceMeters = distanceMeters
        self.elevationGainMeters = elevationGainMeters
        self.movingTimeSeconds = movingTimeSeconds
        self.averageSpeedKmh = averageSpeedKmh
        self.deviceName = deviceName
        self.fileSize = fileSize
        self.relativeFilePath = relativeFilePath
    }
}

@Observable
public final class AppViewModel {
    public var currentScreen: AppScreen = .home
    public var appTheme: AppTheme = .dark
    
    // Athlete Profile (Signature Strava Look)
    public var athleteName: String = "Ardian Rifendy"
    public var activityTitle: String = "Morning Kalcer Ride"
    public var activityLocation: String = "Jakarta, Indonesia"
    public var activityDateText: String = "Hari ini pukul 06:15 WIB"
    public var activityDate: Date = Date()
    
    // UI Modal Sheets
    public var showProfileSettingsSheet: Bool = false
    public var showTutorialSheet: Bool = false
    public var showHistorySheet: Bool = false
    
    // Local Archive History
    public var archivedActivities: [ArchivedActivity] = []
    public var lastSavedHistoryActivity: ArchivedActivity?
    
    // Track & GPX State
    public var currentTrack: RouteTrack?
    public var trackFileName: String = ""
    public var isReversed: Bool = false
    
    // Simulation Configuration
    public var settings: SimulationSettings = SimulationSettings()
    public var selectedDevice: DeviceProfile = DeviceProfileRegistry.defaultProfile(for: .cycling)
    
    // Simulation Output
    public var simulationResult: SimulationResult?
    public var isSimulating: Bool = false
    public var selectedPointIndex: Int?
    
    // FIT Data & Export
    public var generatedFitData: Data?
    public var exportedFitUrl: URL?
    public var customExportFileName: String = ""
    
    public var resolvedFitFileName: String {
        let trimmedCustom = customExportFileName.trimmingCharacters(in: .whitespacesAndNewlines)
        if !trimmedCustom.isEmpty {
            return trimmedCustom.lowercased().hasSuffix(".fit") ? trimmedCustom : "\(trimmedCustom).fit"
        }
        
        // 1. Bersihkan nama file asal GPX (hapus ekstensi dan kata simulated jika ada)
        var base = trackFileName
        for ext in [".gpx", ".GPX", ".fit", ".FIT", ".tcx", ".TCX", ".xml", ".XML"] {
            base = base.replacingOccurrences(of: ext, with: "")
        }
        base = base.replacingOccurrences(of: "_simulated", with: "", options: .caseInsensitive)
        base = base.replacingOccurrences(of: "-simulated", with: "", options: .caseInsensitive)
        base = base.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if !base.isEmpty && base.lowercased() != "route" && base.lowercased() != "sample_loop" {
            let clean = base.components(separatedBy: CharacterSet(charactersIn: "/\\?%*|\":<>")).joined()
            return "\(clean).fit"
        }
        
        // 2. Format autentik perangkat GPS Garmin (Timestamp Aktivitas: YYYY-MM-DD-HH-mm-ss.fit)
        let df = DateFormatter()
        df.dateFormat = "yyyy-MM-dd-HH-mm-ss"
        return "\(df.string(from: activityDate)).fit"
    }
    
    // Strava Cloud State
    public var stravaAccessToken: String = ""
    public var isUploadingToStrava: Bool = false
    public var stravaUploadStatus: String = ""
    public var uploadedActivityId: Int64?
    
    public init() {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "EEEE, d MMM yyyy • HH:mm 'WIB'"
        self.activityDateText = df.string(from: Date())
        self.athleteName = self.settings.userProfile.name
        
        self.loadArchivedActivities()
        
        let hasSeenTutorial = UserDefaults.standard.bool(forKey: "hasSeenTutorial_v1")
        if !hasSeenTutorial {
            self.showTutorialSheet = true
        }
    }
    
    public func markTutorialSeen() {
        UserDefaults.standard.set(true, forKey: "hasSeenTutorial_v1")
        self.showTutorialSheet = false
    }
    
    public func updateAthleteName(_ name: String) {
        self.athleteName = name
        self.settings.userProfile.name = name
    }
    
    public var canGoBack: Bool {
        currentScreen != .home
    }
    
    public func goBack() {
        switch currentScreen {
        case .home:
            break
        case .routeStudio:
            currentScreen = .home
        case .routeReview:
            currentScreen = .home
        case .activitySettings:
            currentScreen = .routeReview
        case .preview:
            currentScreen = .activitySettings
        case .export:
            currentScreen = .preview
        }
    }
    
    public func updateActivityDate(_ newDate: Date) {
        self.activityDate = newDate
        self.settings.startEpochMillis = Int64(newDate.timeIntervalSince1970 * 1000)
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "EEEE, d MMM yyyy • HH:mm 'WIB'"
        self.activityDateText = df.string(from: newDate)
    }
    
    public var selectedBrand: String = "Garmin"
    
    public var availableBrands: [String] {
        DeviceProfileRegistry.brands(for: settings.sport)
    }
    
    public var availableModelsForBrand: [DeviceProfile] {
        DeviceProfileRegistry.profiles(for: settings.sport, brand: selectedBrand)
    }
    
    public func selectBrand(_ brand: String) {
        self.selectedBrand = brand
        let models = DeviceProfileRegistry.profiles(for: settings.sport, brand: brand)
        if let first = models.first {
            self.selectedDevice = first
            self.settings.deviceId = first.id
        }
    }
    
    public func updateSport(_ newSport: SportType) {
        settings.sport = newSport
        switch newSport {
        case .cycling:
            activityTitle = "Morning Kalcer Ride"
            settings.targetAverageSpeedKmh = 28.5
            settings.targetPeakSpeedKmh = 58.0
        case .running:
            activityTitle = "Morning Easy Run"
            settings.targetAveragePaceSecondsPerKm = 330.0 // 5:30 /km
            settings.targetPeakSpeedKmh = 16.0 // 3:45 /km
        case .hiking:
            activityTitle = "Weekend Trail Hike"
            settings.targetAverageSpeedKmh = 4.5
            settings.targetPeakSpeedKmh = 6.8
        }
        
        let brands = DeviceProfileRegistry.brands(for: newSport)
        if !brands.contains(selectedBrand) {
            selectedBrand = brands.first ?? "Garmin"
        }
        let models = DeviceProfileRegistry.profiles(for: newSport, brand: selectedBrand)
        selectedDevice = models.first ?? DeviceProfileRegistry.defaultProfile(for: newSport)
        settings.deviceId = selectedDevice.id
    }
    
    public func loadGpxData(_ data: Data, fileName: String = "route.gpx") {
        do {
            let track = try GpxParser.parse(xmlData: data)
            self.currentTrack = track
            self.trackFileName = fileName
            self.isReversed = false
            self.currentScreen = .routeReview
        } catch {
            print("Failed to parse GPX: \(error)")
        }
    }
    
    public func applyCreatedRoute(_ track: RouteTrack, routeName: String) {
        self.currentTrack = track
        self.trackFileName = "\(routeName).gpx"
        self.isReversed = false
        self.currentScreen = .routeReview
    }
    
    public func loadSampleRoute() {
        // Built-in synthetic rolling loop: 8.9 km with realistic climbs and descents
        let centerLat = -6.9175
        let centerLon = 107.6191
        let numPts = 180
        var pts: [RoutePoint] = []
        var totalDist = 0.0
        var totalGain = 0.0
        var totalLoss = 0.0
        var rawGradients: [Double] = []
        
        for i in 0..<numPts {
            let angle = (Double(i) / Double(numPts)) * 2.0 * .pi
            let lat = centerLat + 0.025 * sin(angle)
            let lon = centerLon + 0.035 * cos(angle)
            // Undulating hill profile: +85m climbs and descents
            let ele = 720.0 + 65.0 * sin(angle * 2.0) + 30.0 * cos(angle * 3.0)
            
            var seg = 0.0
            var rawGrad = 0.0
            if i > 0 {
                let prev = pts[i - 1]
                seg = GeoMath.haversineDistance(lat1: prev.latitude, lon1: prev.longitude, lat2: lat, lon2: lon)
                totalDist += seg
                let dE = ele - prev.elevation
                if dE > 0 { totalGain += dE } else { totalLoss += abs(dE) }
                rawGrad = (seg > 0.5) ? (dE / seg) * 100.0 : 0.0
            }
            rawGradients.append(rawGrad)
            
            pts.append(RoutePoint(
                index: i,
                latitude: lat,
                longitude: lon,
                elevation: ele,
                segmentDistanceMeters: seg,
                distanceFromStartMeters: totalDist,
                rawGradient: rawGrad,
                smoothedGradient: rawGrad
            ))
        }
        
        // Apply gradient moving average
        let smoothed = GeoMath.smoothGradients(rawGradients: rawGradients, halfWindow: 3)
        for i in 0..<numPts {
            pts[i].smoothedGradient = smoothed[i]
        }
        
        let track = RouteTrack(
            points: pts,
            totalDistanceMeters: totalDist,
            elevationGainMeters: totalGain,
            elevationLossMeters: totalLoss,
            minElevation: pts.map { $0.elevation }.min() ?? 680,
            maxElevation: pts.map { $0.elevation }.max() ?? 820
        )
        
        self.currentTrack = track
        self.trackFileName = "sample_loop.gpx"
        self.isReversed = false
        self.currentScreen = .routeReview
    }
    
    public func reverseRoute() {
        guard let track = currentTrack else { return }
        self.currentTrack = GpxParser.reverseTrack(track)
        self.isReversed.toggle()
    }
    
    public static var archiveDirectory: URL {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        let dir = docs.appendingPathComponent("SavedActivities", isDirectory: true)
        if !FileManager.default.fileExists(atPath: dir.path) {
            try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        }
        return dir
    }

    public func loadArchivedActivities() {
        if let data = UserDefaults.standard.data(forKey: "saved_activity_history"),
           let list = try? JSONDecoder().decode([ArchivedActivity].self, from: data) {
            self.archivedActivities = list.sorted(by: { $0.date > $1.date })
        }
    }

    public func archiveCurrentActivity() -> URL? {
        guard let fitData = generatedFitData, let res = simulationResult else { return nil }
        let dir = Self.archiveDirectory
        let fileName = resolvedFitFileName
        let targetUrl = dir.appendingPathComponent(fileName)
        do {
            try fitData.write(to: targetUrl)
            let item = ArchivedActivity(
                fileName: fileName,
                activityTitle: activityTitle,
                athleteName: athleteName,
                sport: settings.sport,
                date: activityDate,
                dateText: activityDateText,
                distanceMeters: res.totalDistanceMeters,
                elevationGainMeters: res.elevationGainMeters,
                movingTimeSeconds: res.movingTimeSeconds,
                averageSpeedKmh: res.averageMovingSpeedKmh,
                deviceName: selectedDevice.formattedDeviceName,
                fileSize: fitData.count,
                relativeFilePath: fileName
            )
            var updated = archivedActivities.filter { $0.fileName != fileName }
            updated.insert(item, at: 0)
            self.archivedActivities = updated
            self.lastSavedHistoryActivity = item
            if let enc = try? JSONEncoder().encode(updated) {
                UserDefaults.standard.set(enc, forKey: "saved_activity_history")
            }
            return targetUrl
        } catch {
            print("Failed to archive activity: \(error)")
            return nil
        }
    }

    public func deleteArchivedActivity(id: UUID) {
        if let idx = archivedActivities.firstIndex(where: { $0.id == id }) {
            let item = archivedActivities[idx]
            let fileUrl = Self.archiveDirectory.appendingPathComponent(item.relativeFilePath)
            try? FileManager.default.removeItem(at: fileUrl)
            archivedActivities.remove(at: idx)
            if let enc = try? JSONEncoder().encode(archivedActivities) {
                UserDefaults.standard.set(enc, forKey: "saved_activity_history")
            }
        }
    }

    public func fileUrlForArchivedActivity(_ item: ArchivedActivity) -> URL? {
        let fileUrl = Self.archiveDirectory.appendingPathComponent(item.relativeFilePath)
        if FileManager.default.fileExists(atPath: fileUrl.path) {
            return fileUrl
        }
        return nil
    }

    public func runSimulation() {
        guard let track = currentTrack else { return }
        self.isSimulating = true
        
        // Run on background queue
        DispatchQueue.global(qos: .userInitiated).async {
            let res = SimulationEngine.simulate(track: track, settings: self.settings)
            let fit = FitEncoder.generateFitData(result: res, device: self.selectedDevice)
            
            DispatchQueue.main.async {
                self.simulationResult = res
                self.generatedFitData = fit
                self.isSimulating = false
                _ = self.archiveCurrentActivity()
                self.currentScreen = .preview
            }
        }
    }
    
    public func prepareExportUrl() -> URL? {
        guard let fitData = generatedFitData else { return nil }
        let tempDir = FileManager.default.temporaryDirectory
        let fileName = resolvedFitFileName
        let fileUrl = tempDir.appendingPathComponent(fileName)
        do {
            try fitData.write(to: fileUrl)
            self.exportedFitUrl = fileUrl
            _ = archiveCurrentActivity()
            return fileUrl
        } catch {
            print("Failed to write export file: \(error)")
            return nil
        }
    }
    
    public func uploadToStrava(activityName: String = "Strava Kalcer Activity") async {
        guard let fitData = generatedFitData, !stravaAccessToken.isEmpty else { return }
        self.isUploadingToStrava = true
        self.stravaUploadStatus = "Mengunggah berkas FIT ke Strava..."
        
        do {
            let uploadId = try await StravaApiClient.uploadFitActivity(
                accessToken: stravaAccessToken,
                fitData: fitData,
                activityName: activityName,
                description: "Simulated activity created by Strava Kalcer iOS Engine.",
                sportType: settings.sport
            )
            
            self.stravaUploadStatus = "Memproses aktivitas di server Strava..."
            let pollResult = await StravaApiClient.pollUploadStatus(accessToken: stravaAccessToken, uploadId: uploadId)
            
            self.isUploadingToStrava = false
            if let actId = pollResult.activityId {
                self.uploadedActivityId = actId
                self.stravaUploadStatus = "Berhasil diunggah ke Strava! ID: \(actId)"
            } else if let err = pollResult.error {
                self.stravaUploadStatus = "Gagal: \(err)"
            } else {
                self.stravaUploadStatus = pollResult.status
            }
        } catch {
            self.isUploadingToStrava = false
            self.stravaUploadStatus = "Error: \(error.localizedDescription)"
        }
    }
}
