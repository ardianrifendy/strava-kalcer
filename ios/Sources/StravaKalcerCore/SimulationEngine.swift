import Foundation

public struct SolverResult {
    public let solvedSpeedsMps: [Double]
    public let achievedAvgKmh: Double
    public let targetFeasible: Bool
    public let iterations: Int
}

public struct TargetSolver {
    public static func solve(
        track: RouteTrack,
        settings: SimulationSettings
    ) -> SolverResult {
        let points = track.points
        let n = points.count
        if n == 0 {
            return SolverResult(solvedSpeedsMps: [], achievedAvgKmh: 0, targetFeasible: false, iterations: 0)
        }
        
        let isCycling = (settings.sport == .cycling)
        let isRunning = (settings.sport == .running)
        
        // Target moving average speed in m/s
        let targetSpeedMps: Double = isCycling ?
            GeoMath.kmhToMps(settings.targetAverageSpeedKmh) :
            (1000.0 / max(120.0, settings.targetAveragePaceSecondsPerKm))
            
        // Peak Speed in m/s as requested by user (dijamin secara kinematis selalu >= target average speed)
        let rawPeakSpeedMps: Double = GeoMath.kmhToMps(settings.targetPeakSpeedKmh)
        let peakSpeedMps: Double = max(targetSpeedMps * 1.15, rawPeakSpeedMps)
        
        let absMinMps = isCycling ? 1.5 : (isRunning ? 1.0 : 0.6)
        let absMaxMps = max(peakSpeedMps * 1.08, targetSpeedMps * 1.25)
        
        // 1. Calculate Base Gradient-Responsive Speeds based on Newtonian Mechanics & Physiology
        var baseSpeedsMps = [Double](repeating: targetSpeedMps, count: n)
        
        for i in 0..<n {
            let g = points[i].smoothedGradient
            
            if isCycling {
                if g > 0.3 {
                    // Physics climb slowdown:
                    // Power is limited by rider threshold. P_gravity dominates (m * g * v * grade).
                    // On steep climbs (g >= 6%), amateur riders shift into granny gear (34T-32T) grinding at 7-14 km/h.
                    let uphillScale = 1.0 / (1.0 + 0.22 * pow(g, 0.94))
                    baseSpeedsMps[i] = max(absMinMps, targetSpeedMps * uphillScale)
                } else if g < -1.0 {
                    // Descent gravitational acceleration:
                    // Bike accelerates under gravity towards terminal velocity / peak speed.
                    // On steep descents (g <= -6%), rider stops pedaling (coasting) and reaches peak speed.
                    let descentIntensity = min(1.0, pow(abs(g) / 7.0, 0.88))
                    let descentSpeed = targetSpeedMps + (peakSpeedMps - targetSpeedMps) * descentIntensity
                    baseSpeedsMps[i] = min(peakSpeedMps, descentSpeed)
                } else {
                    // Flat terrain cruising (-1.0% to +0.3%):
                    // Add natural field dynamics (pedaling cadence rhythm & wind micro-variations)
                    let roadWave = 1.0 + 0.025 * sin(Double(i) * 0.14) + 0.015 * cos(Double(i) * 0.33)
                    baseSpeedsMps[i] = targetSpeedMps * roadWave
                }
            } else if isRunning {
                if g > 0.3 {
                    // Minetti energetic cost for running on incline:
                    let uphillScale = 1.0 / (1.0 + 0.12 * pow(g, 0.88))
                    baseSpeedsMps[i] = max(absMinMps, targetSpeedMps * uphillScale)
                } else if g < -1.0 && g >= -6.0 {
                    // Gentle downhill stride extension towards peak running sprint speed
                    let descentIntensity = min(1.0, abs(g) / 5.0)
                    let downhillSpeed = targetSpeedMps + (peakSpeedMps - targetSpeedMps) * descentIntensity
                    baseSpeedsMps[i] = min(peakSpeedMps, downhillSpeed)
                } else if g < -6.0 {
                    // Steep downhill eccentric impact: runners brake to protect joints and maintain control
                    baseSpeedsMps[i] = targetSpeedMps * 0.90
                } else {
                    let runWave = 1.0 + 0.02 * sin(Double(i) * 0.12)
                    baseSpeedsMps[i] = targetSpeedMps * runWave
                }
            } else {
                // Hiking (Tobler's function derivative)
                if g > 0.3 {
                    let uphillScale = 1.0 / (1.0 + 0.15 * pow(g, 0.85))
                    baseSpeedsMps[i] = max(absMinMps, targetSpeedMps * uphillScale)
                } else if g < -1.0 {
                    let descentIntensity = min(1.0, abs(g) / 6.0)
                    baseSpeedsMps[i] = min(peakSpeedMps, targetSpeedMps + (peakSpeedMps - targetSpeedMps) * descentIntensity)
                } else {
                    baseSpeedsMps[i] = targetSpeedMps
                }
            }
        }
        
        // 2. Cornering Braking (Curvature / Turn Deceleration)
        // In real life, athletes slow down into sharp bends (15-20 km/h) and accelerate out
        if n > 2 {
            for i in 1..<(n - 1) {
                let prev = points[i - 1]
                let curr = points[i]
                let next = points[i + 1]
                
                let b1 = GeoMath.bearingDegrees(fromLat: prev.latitude, fromLon: prev.longitude, toLat: curr.latitude, toLon: curr.longitude)
                let b2 = GeoMath.bearingDegrees(fromLat: curr.latitude, fromLon: curr.longitude, toLat: next.latitude, toLon: next.longitude)
                var diff = abs(b2 - b1)
                if diff > 180.0 { diff = 360.0 - diff }
                
                if diff > 45.0 && curr.segmentDistanceMeters < 80.0 {
                    // Turn sharpness scale: 45° to 120°+
                    let turnSharpness = min(1.0, (diff - 45.0) / 75.0)
                    // For cycling: slow down to 15.0 - 22.0 km/h; for running: 8.0 - 11.5 km/h
                    let cornerSpeedKmh = isCycling ?
                        (22.0 - turnSharpness * 7.0) : // 15 to 22 km/h
                        (11.5 - turnSharpness * 3.5)   // 8 to 11.5 km/h
                    let cornerSpeedMps = GeoMath.kmhToMps(cornerSpeedKmh)
                    baseSpeedsMps[i] = min(baseSpeedsMps[i], cornerSpeedMps)
                }
            }
        }
        
        // 3. Physical Momentum Inertia & Continuous Kinematics
        // In real physics, a cyclist/runner carries momentum from downhills onto flats
        var momentumSpeeds = baseSpeedsMps
        for i in 1..<n {
            let segDist = points[i].segmentDistanceMeters
            let prev = momentumSpeeds[i - 1]
            let curr = momentumSpeeds[i]
            if prev > curr {
                // Kinetic momentum decay over distance (180m for cycling, 40m for running)
                let coastingDecayDist = isCycling ? 180.0 : 40.0
                let decay = exp(-segDist / coastingDecayDist)
                let carriedSpeed = curr + (prev - curr) * decay
                momentumSpeeds[i] = max(curr, carriedSpeed)
            }
        }
        
        // Time-bounded rate of acceleration and deceleration
        // Cyclist acceleration: max ~0.70 m/s^2 (~2.5 km/h/s); Braking: max ~1.20 m/s^2
        let aMax = isCycling ? 0.70 : 0.85
        let dMax = isCycling ? 1.20 : 1.40
        
        var smoothSpeeds = momentumSpeeds
        for i in 1..<n {
            let segDist = points[i].segmentDistanceMeters
            let vPrev = smoothSpeeds[i - 1]
            let estDt = segDist / max(1.5, vPrev)
            let maxAllowed = vPrev + aMax * estDt
            if smoothSpeeds[i] > maxAllowed {
                smoothSpeeds[i] = maxAllowed
            }
        }
        for i in stride(from: n - 2, through: 0, by: -1) {
            let segDist = points[i + 1].segmentDistanceMeters
            let vNext = smoothSpeeds[i + 1]
            let estDt = segDist / max(1.5, vNext)
            let maxAllowed = vNext + dMax * estDt
            if smoothSpeeds[i] > maxAllowed {
                smoothSpeeds[i] = maxAllowed
            }
        }
        
        // 4. Binary search multiplier to reach exact average target speed
        var low = 0.35
        var high = 2.5
        var bestDiff = Double.infinity
        var finalSpeeds = smoothSpeeds
        var iter = 0
        
        while iter < 30 {
            iter += 1
            let mid = (low + high) / 2.0
            var candidateSpeeds = [Double](repeating: 0.0, count: n)
            var totalTime = 0.0
            
            for i in 0..<n {
                let s = (smoothSpeeds[i] * mid).clamped(to: absMinMps...absMaxMps)
                candidateSpeeds[i] = s
                if i > 0 && s > 0.1 {
                    totalTime += points[i].segmentDistanceMeters / s
                }
            }
            
            let achievedSpeed = (totalTime > 0) ? (track.totalDistanceMeters / totalTime) : 0.0
            let diff = achievedSpeed - targetSpeedMps
            
            if abs(diff) < bestDiff {
                bestDiff = abs(diff)
                finalSpeeds = candidateSpeeds
            }
            
            if abs(diff) < 0.015 { break }
            if diff < 0 {
                low = mid
            } else {
                high = mid
            }
        }
        
        var finalTime = 0.0
        for i in 1..<n {
            if finalSpeeds[i] > 0.1 {
                finalTime += points[i].segmentDistanceMeters / finalSpeeds[i]
            }
        }
        let achievedKmh = (finalTime > 0) ? GeoMath.mpsToKmh(track.totalDistanceMeters / finalTime) : 0.0
        
        return SolverResult(
            solvedSpeedsMps: finalSpeeds,
            achievedAvgKmh: achievedKmh,
            targetFeasible: bestDiff < 1.0,
            iterations: iter
        )
    }
}

public struct SimulationEngine {
    public static func simulate(
        track: RouteTrack,
        settings: SimulationSettings
    ) -> SimulationResult {
        let points = track.points
        let n = points.count
        if n == 0 {
            return SimulationResult(
                settings: settings,
                points: [],
                totalDistanceMeters: 0,
                movingTimeSeconds: 0,
                totalTimeSeconds: 0,
                pauseTimeSeconds: 0,
                pauseCount: 0,
                averageMovingSpeedKmh: 0,
                averageMovingPaceSecondsPerKm: 0,
                maxSpeedKmh: 0,
                bestPaceSecondsPerKm: 0,
                averageHeartRate: nil,
                maxHeartRate: nil,
                averageCadence: nil,
                maxCadence: nil,
                averagePower: nil,
                maxPower: nil,
                normalizedPower: nil,
                totalWorkJoules: 0,
                totalCalories: nil,
                averageTemperature: nil,
                maxTemperature: nil,
                elevationGainMeters: 0,
                elevationLossMeters: 0,
                requestedTarget: "-",
                achievedTarget: "-",
                targetFeasible: false,
                solverIterations: 0
            )
        }
        
        // 1. Numerical target root solver
        let solver = TargetSolver.solve(track: track, settings: settings)
        let solvedSpeeds = solver.solvedSpeedsMps
        let isCycling = (settings.sport == .cycling)
        let params = settings.activeProfileParams
        
        // 2. Map stops & Auto-Pause (Lampu Merah, Coffee Stop, Rest Point)
        var isStop = [Bool](repeating: false, count: n)
        var stopDuration = [Int64](repeating: 0, count: n)
        var stopReason = [String](repeating: "", count: n)
        
        var activeStops = settings.stops
        if activeStops.isEmpty && settings.autoPause.enabled && n > 15 {
            let totalPauseSec = Int64(max(30.0, settings.autoPause.totalPauseMinutes * 60.0))
            switch settings.autoPause.mode {
            case .smartCity:
                // 2-4 perhentian lampu lalu lintas & persimpangan
                let count = max(1, min(5, settings.autoPause.pauseCount))
                let perStop = max(20, totalPauseSec / Int64(count))
                for s in 1...count {
                    let targetDist = track.totalDistanceMeters * (Double(s) / Double(count + 1))
                    if let pt = track.findPointAtDistance(targetDist) {
                        activeStops.append(SimulationStop(distanceMeters: pt.distanceFromStartMeters, durationSeconds: perStop, reason: "Lampu Merah / Persimpangan"))
                    }
                }
            case .coffeeBreak:
                // 1 istirahat santai di warung / cafe kalcer di pertengahan rute
                let targetDist = track.totalDistanceMeters * 0.55
                if let pt = track.findPointAtDistance(targetDist) {
                    activeStops.append(SimulationStop(distanceMeters: pt.distanceFromStartMeters, durationSeconds: totalPauseSec, reason: "Coffee Stop / Istirahat Kalcer"))
                }
            case .hilltopRest:
                // Istirahat di puncak elevasi tertinggi
                if let maxPt = points.max(by: { $0.elevation < $1.elevation }) {
                    activeStops.append(SimulationStop(distanceMeters: maxPt.distanceFromStartMeters, durationSeconds: totalPauseSec, reason: "Puncak Bukit / Viewpoint"))
                }
            case .longRide:
                // Long Ride / Century (Multi-Stop dengan durasi & alasan BERVARIASI)
                // Stop 1: Minimarket / Refill Air (durasi 5-8 menit, ~20%)
                // Stop 2: Coffee Stop / Makan Kalcer (durasi 25-45 menit, ~55%)
                // Stop 3: Regroup / Peregangan Kaki (durasi 10-15 menit, ~25%)
                let count = max(2, min(6, settings.autoPause.pauseCount))
                let reasons = [
                    "Minimarket / Water Refill",
                    "Coffee Stop / Warung",
                    "Regroup / Istirahat Kaki",
                    "Pit Stop Foto / Viewpoint",
                    "Perhentian Terakhir"
                ]
                let ratios: [Double]
                switch count {
                case 2: ratios = [0.30, 0.70]
                case 3: ratios = [0.20, 0.55, 0.25]
                case 4: ratios = [0.15, 0.45, 0.25, 0.15]
                default:
                    var r = [Double](repeating: 1.0 / Double(count), count: count)
                    r[1] = r[1] * 1.8
                    let sum = r.reduce(0, +)
                    ratios = r.map { $0 / sum }
                }
                
                for s in 0..<count {
                    let fraction = Double(s + 1) / Double(count + 1)
                    let targetDist = track.totalDistanceMeters * fraction
                    let stopSec = max(30, Int64((Double(totalPauseSec) * ratios[s]).rounded()))
                    let reasonText = (s < reasons.count) ? reasons[s] : "Perhentian Long Ride #\(s + 1)"
                    if let pt = track.findPointAtDistance(targetDist) {
                        activeStops.append(SimulationStop(distanceMeters: pt.distanceFromStartMeters, durationSeconds: stopSec, reason: reasonText))
                    }
                }
            case .custom:
                let count = max(1, min(8, settings.autoPause.pauseCount))
                let perStop = max(15, totalPauseSec / Int64(count))
                for s in 1...count {
                    let targetDist = track.totalDistanceMeters * (Double(s) / Double(count + 1))
                    if let pt = track.findPointAtDistance(targetDist) {
                        activeStops.append(SimulationStop(distanceMeters: pt.distanceFromStartMeters, durationSeconds: perStop, reason: "Auto-Pause Terjadwal"))
                    }
                }
            }
        }
        
        for stop in activeStops {
            if let pt = track.findPointAtDistance(stop.distanceMeters) {
                isStop[pt.index] = true
                stopDuration[pt.index] = stop.durationSeconds
                stopReason[pt.index] = stop.reason
            }
        }
        
        // 3. Build chronological timeline with stop deceleration/acceleration ramps
        var effectiveSpeeds = solvedSpeeds
        for i in 0..<n {
            if isStop[i] {
                effectiveSpeeds[i] = 0.0
                
                // Deceleration approach ramps before stop
                if i >= 1 && !isStop[i - 1] {
                    effectiveSpeeds[i - 1] = min(effectiveSpeeds[i - 1], solvedSpeeds[i - 1] * 0.25)
                }
                if i >= 2 && !isStop[i - 2] {
                    effectiveSpeeds[i - 2] = min(effectiveSpeeds[i - 2], solvedSpeeds[i - 2] * 0.55)
                }
                if i >= 3 && !isStop[i - 3] {
                    effectiveSpeeds[i - 3] = min(effectiveSpeeds[i - 3], solvedSpeeds[i - 3] * 0.85)
                }
                
                // Acceleration exit ramps after stop
                if i + 1 < n && !isStop[i + 1] {
                    effectiveSpeeds[i + 1] = min(effectiveSpeeds[i + 1], solvedSpeeds[i + 1] * 0.35)
                }
                if i + 2 < n && !isStop[i + 2] {
                    effectiveSpeeds[i + 2] = min(effectiveSpeeds[i + 2], solvedSpeeds[i + 2] * 0.70)
                }
                if i + 3 < n && !isStop[i + 3] {
                    effectiveSpeeds[i + 3] = min(effectiveSpeeds[i + 3], solvedSpeeds[i + 3] * 0.90)
                }
            }
        }
        
        var currentEpoch = settings.startEpochMillis
        var cumulativeMovingTime = 0.0
        var cumulativeTotalTime = 0.0
        var timeDeltas = [Double](repeating: 1.0, count: n)
        var simPoints: [SimulationPoint] = []
        
        for i in 0..<n {
            let pt = points[i]
            let segDist = pt.segmentDistanceMeters
            let spd = isStop[i] ? 0.0 : effectiveSpeeds[i]
            
            let moveSec = (i == 0 || spd < 0.2) ? 0.0 : (segDist / spd)
            let stopSec = Double(stopDuration[i])
            let stepSec = moveSec + stopSec
            timeDeltas[i] = (i == 0) ? 1.0 : max(1.0, stepSec)
            
            cumulativeMovingTime += moveSec
            cumulativeTotalTime += stepSec
            currentEpoch += Int64((stepSec * 1000.0).rounded())
            
            let reason: String
            if isStop[i] {
                reason = "AUTO_PAUSE_\(stopReason[i])"
            } else if isCycling && pt.smoothedGradient <= params.coastingThresholdGradient {
                reason = "COASTING"
            } else if pt.smoothedGradient > 2.0 {
                reason = "UPHILL_SLOWDOWN"
            } else if pt.smoothedGradient < -2.0 {
                reason = "DOWNHILL_ACCELERATION"
            } else {
                reason = "CRUISING"
            }
            
            simPoints.append(
                SimulationPoint(
                    index: i,
                    latitude: pt.latitude,
                    longitude: pt.longitude,
                    elevation: pt.elevation,
                    gradient: pt.smoothedGradient,
                    rawGradient: pt.rawGradient,
                    distanceFromStartMeters: pt.distanceFromStartMeters,
                    segmentDistanceMeters: segDist,
                    speedMps: spd,
                    speedKmh: GeoMath.mpsToKmh(spd),
                    paceSecondsPerKm: GeoMath.mpsToPaceSecondsPerKm(spd),
                    timestampEpochMillis: currentEpoch,
                    movingTimeSeconds: Int64(cumulativeMovingTime.rounded()),
                    totalTimeSeconds: Int64(cumulativeTotalTime.rounded()),
                    isStopped: isStop[i],
                    explainReason: reason
                )
            )
        }
        
        // 4. Heart Rate, Cadence, Power, Temperature series
        let gradients = points.map { $0.smoothedGradient }
        var activeHrConfig = settings.hrConfig
        activeHrConfig.maxHr = settings.userProfile.estimatedMaxHr
        
        let hrSeries = HeartRateEngine.simulateHeartRateSeries(
            speedsMps: effectiveSpeeds,
            gradients: gradients,
            timeDeltasSec: timeDeltas,
            isStopped: isStop,
            sport: settings.sport,
            profileParams: params,
            hrConfig: activeHrConfig
        )
        
        let cadenceSeries = CadenceEngine.simulateCadenceSeries(
            speedsMps: effectiveSpeeds,
            gradients: gradients,
            timeDeltasSec: timeDeltas,
            isStopped: isStop,
            sport: settings.sport,
            cadenceConfig: settings.cadenceConfig
        )
        
        var rawPowerSeries = [Int](repeating: 0, count: n)
        for i in 0..<n {
            let p = simPoints[i]
            let cad = cadenceSeries[i]
            let isCoasting = isCycling && (p.gradient <= params.coastingThresholdGradient || cad == 0)
            let watts = isCycling ?
                PowerEngine.calculateCyclingWatts(
                    speedMps: p.speedMps,
                    gradientPercent: p.gradient,
                    isStopped: p.isStopped,
                    isCoasting: isCoasting,
                    cadenceRpm: cad,
                    targetCadence: settings.cadenceConfig.targetCadence,
                    totalMassKg: settings.userProfile.totalMassKg,
                    cda: settings.userProfile.estimatedCdA
                ) :
                PowerEngine.calculateRunningWatts(
                    speedMps: p.speedMps,
                    gradientPercent: p.gradient,
                    isStopped: p.isStopped,
                    runnerMassKg: settings.userProfile.weightKg
                )
            rawPowerSeries[i] = watts
        }
        
        // Exponential torque smoothing / crank inertia (tau = 2.0s)
        var powerSeries = [Int](repeating: 0, count: n)
        var currentPwr = Double(rawPowerSeries.first ?? 0)
        for i in 0..<n {
            let dt = timeDeltas[i]
            let targetPwr = Double(rawPowerSeries[i])
            if targetPwr == 0 || cadenceSeries[i] == 0 {
                let alpha = 1.0 - exp(-dt / 1.5)
                currentPwr = currentPwr * (1.0 - alpha)
                if currentPwr < 5.0 { currentPwr = 0.0 }
            } else {
                let alpha = 1.0 - exp(-dt / 2.0)
                currentPwr = currentPwr + (targetPwr - currentPwr) * alpha
            }
            powerSeries[i] = Int(currentPwr.rounded())
        }
        
        var totalWorkJoules: Int64 = 0
        let calendar = Calendar.current
        let startDate = Date(timeIntervalSince1970: Double(settings.startEpochMillis) / 1000.0)
        let startHourOfDay = Double(calendar.component(.hour, from: startDate)) + Double(calendar.component(.minute, from: startDate)) / 60.0
        
        var simulatedTemps: [Int] = []
        
        for i in 0..<n {
            let p = simPoints[i]
            let watts = powerSeries[i]
            let dt = (i == 0) ? 1.0 : max(0.5, min(30.0, Double(p.totalTimeSeconds - simPoints[i - 1].totalTimeSeconds)))
            if !p.isStopped {
                totalWorkJoules += Int64((Double(watts) * dt).rounded())
            }
            
            // Dynamic Diurnal Temperature Curve:
            // Scaled realistically by time of day (e.g. 23-24°C at 06:00 rising to 32-34°C by midday)
            let pointHour = fmod(startHourOfDay + (Double(p.totalTimeSeconds) / 3600.0), 24.0)
            let tempValue: Double
            if pointHour >= 5.5 && pointHour <= 19.5 {
                let solarFraction = (pointHour - 5.5) / 14.0
                let solarIntensity = sin(solarFraction * .pi)
                tempValue = 23.5 + 10.5 * pow(solarIntensity, 0.85)
            } else {
                tempValue = 22.5 + 1.5 * cos((pointHour / 24.0) * 2.0 * .pi)
            }
            let microNoise = 0.35 * sin(Double(i) * 0.19)
            let temp = Int((tempValue + microNoise).rounded().clamped(to: 18.0...40.0))
            simulatedTemps.append(temp)
            
            simPoints[i].heartRate = settings.hrConfig.enabled ? hrSeries[i] : nil
            simPoints[i].cadence = settings.cadenceConfig.enabled ? cadenceSeries[i] : nil
            simPoints[i].powerWatts = watts
            simPoints[i].temperatureCelsius = temp
        }
        
        // 5. Aggregate statistics
        let finalMovingTime = max(1, Int64(cumulativeMovingTime.rounded()))
        let finalTotalTime = max(1, Int64(cumulativeTotalTime.rounded()))
        let pauseTime = max(0, finalTotalTime - finalMovingTime)
        let totalStopCount = activeStops.count
        
        let avgSpeedKmh = (finalMovingTime > 0) ? (track.totalDistanceMeters / Double(finalMovingTime)) * 3.6 : 0.0
        let avgPaceSec = (track.totalDistanceMeters > 0) ? Double(finalMovingTime) / (track.totalDistanceMeters / 1000.0) : 0.0
        let maxSpeedKmh = simPoints.map { $0.speedKmh }.max() ?? 0.0
        let bestPace = simPoints.filter { $0.speedMps > 0.5 }.map { $0.paceSecondsPerKm }.min() ?? 300.0
        
        let validHr = simPoints.compactMap { $0.heartRate }
        let avgHr = validHr.isEmpty ? nil : Int(validHr.reduce(0, +) / validHr.count)
        let maxHr = validHr.max()
        
        let validCadence = simPoints.compactMap { $0.cadence }.filter { $0 > 0 }
        let avgCadence = validCadence.isEmpty ? nil : Int(validCadence.reduce(0, +) / validCadence.count)
        let maxCadence = validCadence.max()
        
        let validPower = powerSeries.filter { $0 > 0 }
        let avgPower = validPower.isEmpty ? nil : Int(validPower.reduce(0, +) / validPower.count)
        let maxPower = powerSeries.max()
        let np = PowerEngine.calculateNormalizedPower(powerSeries: powerSeries)
        let calories = PowerEngine.calculateCalories(totalWorkJoules: totalWorkJoules, movingTimeSeconds: finalMovingTime, averageHr: avgHr, sport: settings.sport)
        
        let avgTemp = simulatedTemps.isEmpty ? nil : Int(simulatedTemps.reduce(0, +) / simulatedTemps.count)
        let maxTemp = simulatedTemps.max()
        
        let requestedText = isCycling ? String(format: "%.1f km/h", settings.targetAverageSpeedKmh) : String(format: "%d:%02d /km", Int(settings.targetAveragePaceSecondsPerKm) / 60, Int(settings.targetAveragePaceSecondsPerKm) % 60)
        let achievedText = isCycling ? String(format: "%.1f km/h", avgSpeedKmh) : String(format: "%d:%02d /km", Int(avgPaceSec) / 60, Int(avgPaceSec) % 60)
        
        return SimulationResult(
            settings: settings,
            points: simPoints,
            totalDistanceMeters: track.totalDistanceMeters,
            movingTimeSeconds: finalMovingTime,
            totalTimeSeconds: finalTotalTime,
            pauseTimeSeconds: pauseTime,
            pauseCount: totalStopCount,
            averageMovingSpeedKmh: avgSpeedKmh,
            averageMovingPaceSecondsPerKm: avgPaceSec,
            maxSpeedKmh: maxSpeedKmh,
            bestPaceSecondsPerKm: bestPace,
            averageHeartRate: avgHr,
            maxHeartRate: maxHr,
            averageCadence: avgCadence,
            maxCadence: maxCadence,
            averagePower: avgPower,
            maxPower: maxPower,
            normalizedPower: np,
            totalWorkJoules: totalWorkJoules,
            totalCalories: calories,
            averageTemperature: avgTemp,
            maxTemperature: maxTemp,
            elevationGainMeters: track.elevationGainMeters,
            elevationLossMeters: track.elevationLossMeters,
            requestedTarget: requestedText,
            achievedTarget: achievedText,
            targetFeasible: solver.targetFeasible,
            solverIterations: solver.iterations
        )
    }
}
