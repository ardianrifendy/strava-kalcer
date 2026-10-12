import Foundation

public enum SportType: String, Codable, CaseIterable, Identifiable {
    case cycling = "CYCLING"
    case running = "RUNNING"
    case hiking = "HIKING"
    
    public var id: String { rawValue }
    
    public var displayName: String {
        switch self {
        case .cycling: return "Cycling (Sepeda)"
        case .running: return "Running (Lari)"
        case .hiking: return "Hiking (Jalan / Trail)"
        }
    }
}

public enum ActivityProfile: String, Codable, CaseIterable, Identifiable {
    case casual = "CASUAL"
    case endurance = "ENDURANCE"
    case tempo = "TEMPO"
    case race = "RACE"
    
    public var id: String { rawValue }
    
    public var displayName: String {
        switch self {
        case .casual: return "Casual / Recovery"
        case .endurance: return "Endurance Base"
        case .tempo: return "Tempo / Sweetspot"
        case .race: return "Race / High Intensity"
        }
    }
}

public struct ProfileParameters: Codable {
    public var minSpeedKmh: Double
    public var maxSpeedKmh: Double
    public var uphillSpeedPenaltyFactor: Double
    public var downhillSpeedBonusFactor: Double
    public var coastingThresholdGradient: Double
    public var maxAccelerationMps2: Double
    public var maxDecelerationMps2: Double
    
    public init(
        minSpeedKmh: Double,
        maxSpeedKmh: Double,
        uphillSpeedPenaltyFactor: Double,
        downhillSpeedBonusFactor: Double,
        coastingThresholdGradient: Double = -3.5,
        maxAccelerationMps2: Double = 1.2,
        maxDecelerationMps2: Double = 2.5
    ) {
        self.minSpeedKmh = minSpeedKmh
        self.maxSpeedKmh = maxSpeedKmh
        self.uphillSpeedPenaltyFactor = uphillSpeedPenaltyFactor
        self.downhillSpeedBonusFactor = downhillSpeedBonusFactor
        self.coastingThresholdGradient = coastingThresholdGradient
        self.maxAccelerationMps2 = maxAccelerationMps2
        self.maxDecelerationMps2 = maxDecelerationMps2
    }
    
    public static func defaultParameters(for sport: SportType, profile: ActivityProfile) -> ProfileParameters {
        switch sport {
        case .cycling:
            switch profile {
            case .casual:
                return ProfileParameters(minSpeedKmh: 12.0, maxSpeedKmh: 42.0, uphillSpeedPenaltyFactor: 0.65, downhillSpeedBonusFactor: 1.35)
            case .endurance:
                return ProfileParameters(minSpeedKmh: 14.0, maxSpeedKmh: 48.0, uphillSpeedPenaltyFactor: 0.70, downhillSpeedBonusFactor: 1.40)
            case .tempo:
                return ProfileParameters(minSpeedKmh: 16.0, maxSpeedKmh: 54.0, uphillSpeedPenaltyFactor: 0.75, downhillSpeedBonusFactor: 1.45)
            case .race:
                return ProfileParameters(minSpeedKmh: 18.0, maxSpeedKmh: 62.0, uphillSpeedPenaltyFactor: 0.80, downhillSpeedBonusFactor: 1.50)
            }
        case .running:
            switch profile {
            case .casual:
                return ProfileParameters(minSpeedKmh: 6.0, maxSpeedKmh: 13.0, uphillSpeedPenaltyFactor: 0.80, downhillSpeedBonusFactor: 1.15)
            case .endurance:
                return ProfileParameters(minSpeedKmh: 7.0, maxSpeedKmh: 15.0, uphillSpeedPenaltyFactor: 0.82, downhillSpeedBonusFactor: 1.18)
            case .tempo:
                return ProfileParameters(minSpeedKmh: 8.5, maxSpeedKmh: 18.0, uphillSpeedPenaltyFactor: 0.85, downhillSpeedBonusFactor: 1.20)
            case .race:
                return ProfileParameters(minSpeedKmh: 10.0, maxSpeedKmh: 22.0, uphillSpeedPenaltyFactor: 0.88, downhillSpeedBonusFactor: 1.22)
            }
        case .hiking:
            switch profile {
            case .casual:
                return ProfileParameters(minSpeedKmh: 3.0, maxSpeedKmh: 6.5, uphillSpeedPenaltyFactor: 0.85, downhillSpeedBonusFactor: 1.10)
            case .endurance:
                return ProfileParameters(minSpeedKmh: 3.5, maxSpeedKmh: 7.5, uphillSpeedPenaltyFactor: 0.85, downhillSpeedBonusFactor: 1.12)
            case .tempo:
                return ProfileParameters(minSpeedKmh: 4.0, maxSpeedKmh: 9.0, uphillSpeedPenaltyFactor: 0.88, downhillSpeedBonusFactor: 1.15)
            case .race:
                return ProfileParameters(minSpeedKmh: 4.5, maxSpeedKmh: 11.0, uphillSpeedPenaltyFactor: 0.90, downhillSpeedBonusFactor: 1.18)
            }
        }
    }
}

public struct RoutePoint: Identifiable, Codable {
    public var id: Int { index }
    public let index: Int
    public let latitude: Double
    public let longitude: Double
    public let elevation: Double
    public var segmentDistanceMeters: Double
    public var distanceFromStartMeters: Double
    public var rawGradient: Double
    public var smoothedGradient: Double
    
    public init(
        index: Int,
        latitude: Double,
        longitude: Double,
        elevation: Double,
        segmentDistanceMeters: Double = 0.0,
        distanceFromStartMeters: Double = 0.0,
        rawGradient: Double = 0.0,
        smoothedGradient: Double = 0.0
    ) {
        self.index = index
        self.latitude = latitude
        self.longitude = longitude
        self.elevation = elevation
        self.segmentDistanceMeters = segmentDistanceMeters
        self.distanceFromStartMeters = distanceFromStartMeters
        self.rawGradient = rawGradient
        self.smoothedGradient = smoothedGradient
    }
}

public struct RouteTrack: Codable {
    public let points: [RoutePoint]
    public let totalDistanceMeters: Double
    public let elevationGainMeters: Double
    public let elevationLossMeters: Double
    public let minElevation: Double
    public let maxElevation: Double
    
    public init(
        points: [RoutePoint],
        totalDistanceMeters: Double,
        elevationGainMeters: Double,
        elevationLossMeters: Double,
        minElevation: Double,
        maxElevation: Double
    ) {
        self.points = points
        self.totalDistanceMeters = totalDistanceMeters
        self.elevationGainMeters = elevationGainMeters
        self.elevationLossMeters = elevationLossMeters
        self.minElevation = minElevation
        self.maxElevation = maxElevation
    }
    
    public func findPointAtDistance(_ distanceMeters: Double) -> RoutePoint? {
        if points.isEmpty { return nil }
        if distanceMeters <= 0 { return points.first }
        if distanceMeters >= totalDistanceMeters { return points.last }
        return points.min(by: { abs($0.distanceFromStartMeters - distanceMeters) < abs($1.distanceFromStartMeters - distanceMeters) })
    }
}

public struct SimulationPoint: Identifiable, Codable {
    public var id: Int { index }
    public let index: Int
    public let latitude: Double
    public let longitude: Double
    public let elevation: Double
    public let gradient: Double
    public let rawGradient: Double
    public let distanceFromStartMeters: Double
    public let segmentDistanceMeters: Double
    public let speedMps: Double
    public let speedKmh: Double
    public let paceSecondsPerKm: Double
    public let timestampEpochMillis: Int64
    public let movingTimeSeconds: Int64
    public let totalTimeSeconds: Int64
    public let isStopped: Bool
    public let explainReason: String
    public var heartRate: Int?
    public var cadence: Int?
    public var powerWatts: Int?
    public var temperatureCelsius: Int?
    
    public init(
        index: Int,
        latitude: Double,
        longitude: Double,
        elevation: Double,
        gradient: Double,
        rawGradient: Double,
        distanceFromStartMeters: Double,
        segmentDistanceMeters: Double,
        speedMps: Double,
        speedKmh: Double,
        paceSecondsPerKm: Double,
        timestampEpochMillis: Int64,
        movingTimeSeconds: Int64,
        totalTimeSeconds: Int64,
        isStopped: Bool,
        explainReason: String,
        heartRate: Int? = nil,
        cadence: Int? = nil,
        powerWatts: Int? = nil,
        temperatureCelsius: Int? = nil
    ) {
        self.index = index
        self.latitude = latitude
        self.longitude = longitude
        self.elevation = elevation
        self.gradient = gradient
        self.rawGradient = rawGradient
        self.distanceFromStartMeters = distanceFromStartMeters
        self.segmentDistanceMeters = segmentDistanceMeters
        self.speedMps = speedMps
        self.speedKmh = speedKmh
        self.paceSecondsPerKm = paceSecondsPerKm
        self.timestampEpochMillis = timestampEpochMillis
        self.movingTimeSeconds = movingTimeSeconds
        self.totalTimeSeconds = totalTimeSeconds
        self.isStopped = isStopped
        self.explainReason = explainReason
        self.heartRate = heartRate
        self.cadence = cadence
        self.powerWatts = powerWatts
        self.temperatureCelsius = temperatureCelsius
    }
}

public struct HeartRateConfig: Codable {
    public var enabled: Bool
    public var baseHr: Int
    public var maxHr: Int
    
    public init(enabled: Bool = true, baseHr: Int = 135, maxHr: Int = 182) {
        self.enabled = enabled
        self.baseHr = baseHr
        self.maxHr = maxHr
    }
}

public struct CadenceConfig: Codable {
    public var enabled: Bool
    public var targetCadence: Int
    public var allowCoastingZeroRpm: Bool
    
    public init(enabled: Bool = true, targetCadence: Int = 85, allowCoastingZeroRpm: Bool = true) {
        self.enabled = enabled
        self.targetCadence = targetCadence
        self.allowCoastingZeroRpm = allowCoastingZeroRpm
    }
}

public struct SimulationStop: Codable, Identifiable, Hashable {
    public var id: UUID
    public var distanceMeters: Double
    public var durationSeconds: Int64
    public var reason: String
    
    public init(id: UUID = UUID(), distanceMeters: Double, durationSeconds: Int64, reason: String = "REST_STOP") {
        self.id = id
        self.distanceMeters = distanceMeters
        self.durationSeconds = durationSeconds
        self.reason = reason
    }
}

public struct UserProfile: Codable {
    public var name: String
    public var birthDate: Date
    public var heightCm: Double
    public var weightKg: Double
    public var bikeWeightKg: Double
    public var gender: String
    
    public init(
        name: String = "Ardian Rifendy",
        birthDate: Date = Calendar.current.date(byAdding: .year, value: -28, to: Date()) ?? Date(),
        heightCm: Double = 175.0,
        weightKg: Double = 68.0,
        bikeWeightKg: Double = 8.5,
        gender: String = "Pria"
    ) {
        self.name = name
        self.birthDate = birthDate
        self.heightCm = heightCm
        self.weightKg = weightKg
        self.bikeWeightKg = bikeWeightKg
        self.gender = gender
    }
    
    public var age: Int {
        Calendar.current.dateComponents([.year], from: birthDate, to: Date()).year ?? 28
    }
    
    public var estimatedMaxHr: Int {
        // Tanaka formula: 208 - 0.7 * age
        max(150, min(210, Int((208.0 - 0.7 * Double(age)).rounded())))
    }
    
    public var totalMassKg: Double {
        weightKg + bikeWeightKg
    }
    
    public var estimatedCdA: Double {
        let scale = (heightCm / 175.0) * sqrt(weightKg / 68.0)
        return max(0.26, min(0.48, 0.32 * scale))
    }
}

public enum AutoPauseMode: String, Codable, CaseIterable, Identifiable {
    case smartCity = "Lampu Merah & Persimpangan"
    case coffeeBreak = "Coffee Stop / Warung Kalcer"
    case hilltopRest = "Puncak Bukit / Viewpoint"
    case longRide = "Long Ride / Century (Multi-Stop Variatif)"
    case custom = "Kustom / Terjadwal Mandiri"
    
    public var id: String { rawValue }
    
    public var icon: String {
        switch self {
        case .smartCity: return "trafficlight"
        case .coffeeBreak: return "cup.and.saucer.fill"
        case .hilltopRest: return "mountain.2.fill"
        case .longRide: return "map.fill"
        case .custom: return "slider.horizontal.3"
        }
    }
    
    public var description: String {
        switch self {
        case .smartCity: return "2-4 perhentian singkat (30-90 detik) mensimulasikan lampu merah & persimpangan kota."
        case .coffeeBreak: return "1 perhentian istirahat santai (10-35 menit) di tengah rute khas gowes kalcer."
        case .hilltopRest: return "Istirahat pemulihan nafas di puncak elevasi tertinggi rute."
        case .longRide: return "Simulasi gowes jarak jauh (50K-200K) dengan perhentian bervariasi: minimarket/refill (5-8m), coffee stop/makan (25-45m), dan regroup kaki (10-15m)."
        case .custom: return "Atur sendiri frekuensi dan total durasi istirahat sesuai target Anda."
        }
    }
}

public struct AutoPauseSettings: Codable {
    public var enabled: Bool
    public var mode: AutoPauseMode
    public var totalPauseMinutes: Double
    public var pauseCount: Int
    
    public init(
        enabled: Bool = true,
        mode: AutoPauseMode = .smartCity,
        totalPauseMinutes: Double = 15.0,
        pauseCount: Int = 3
    ) {
        self.enabled = enabled
        self.mode = mode
        self.totalPauseMinutes = totalPauseMinutes
        self.pauseCount = pauseCount
    }
}

public struct SimulationSettings: Codable {
    public var sport: SportType
    public var profile: ActivityProfile
    public var targetAverageSpeedKmh: Double
    public var targetPeakSpeedKmh: Double
    public var targetAveragePaceSecondsPerKm: Double
    public var startEpochMillis: Int64
    public var hrConfig: HeartRateConfig
    public var cadenceConfig: CadenceConfig
    public var autoPause: AutoPauseSettings
    public var stops: [SimulationStop]
    public var deviceId: String
    public var userProfile: UserProfile
    
    public init(
        sport: SportType = .cycling,
        profile: ActivityProfile = .endurance,
        targetAverageSpeedKmh: Double = 28.5,
        targetPeakSpeedKmh: Double = 58.0,
        targetAveragePaceSecondsPerKm: Double = 330.0,
        startEpochMillis: Int64 = Int64(Date().timeIntervalSince1970 * 1000),
        hrConfig: HeartRateConfig = HeartRateConfig(),
        cadenceConfig: CadenceConfig = CadenceConfig(),
        autoPause: AutoPauseSettings = AutoPauseSettings(),
        stops: [SimulationStop] = [],
        deviceId: String = "garmin_edge_1050",
        userProfile: UserProfile = UserProfile()
    ) {
        self.sport = sport
        self.profile = profile
        self.targetAverageSpeedKmh = targetAverageSpeedKmh
        self.targetPeakSpeedKmh = targetPeakSpeedKmh
        self.targetAveragePaceSecondsPerKm = targetAveragePaceSecondsPerKm
        self.startEpochMillis = startEpochMillis
        self.hrConfig = hrConfig
        self.cadenceConfig = cadenceConfig
        self.autoPause = autoPause
        self.stops = stops
        self.deviceId = deviceId
        self.userProfile = userProfile
    }
    
    public var activeProfileParams: ProfileParameters {
        ProfileParameters.defaultParameters(for: sport, profile: profile)
    }
}

public struct SimulationResult: Codable {
    public let settings: SimulationSettings
    public let points: [SimulationPoint]
    public let totalDistanceMeters: Double
    public let movingTimeSeconds: Int64
    public let totalTimeSeconds: Int64
    public let pauseTimeSeconds: Int64
    public let pauseCount: Int
    public let averageMovingSpeedKmh: Double
    public let averageMovingPaceSecondsPerKm: Double
    public let maxSpeedKmh: Double
    public let bestPaceSecondsPerKm: Double
    public let averageHeartRate: Int?
    public let maxHeartRate: Int?
    public let averageCadence: Int?
    public let maxCadence: Int?
    public let averagePower: Int?
    public let maxPower: Int?
    public let normalizedPower: Int?
    public let totalWorkJoules: Int64
    public let totalCalories: Int?
    public let averageTemperature: Int?
    public let maxTemperature: Int?
    public let elevationGainMeters: Double
    public let elevationLossMeters: Double
    public let requestedTarget: String
    public let achievedTarget: String
    public let targetFeasible: Bool
    public let solverIterations: Int
}
