import Foundation

public struct PowerEngine {
    // Cycling Aerodynamic and Mechanical parameters
    private static let cda = 0.32          // m^2
    private static let airDensity = 1.225   // kg/m^3
    private static let crr = 0.004          // Rolling resistance coefficient
    private static let totalMassKg = 78.0   // Rider + equipment
    private static let drivetrainEfficiency = 0.965 // 96.5%
    private static let gravity = 9.80665

    public static func calculateCyclingWatts(
        speedMps: Double,
        gradientPercent: Double,
        isStopped: Bool,
        isCoasting: Bool,
        cadenceRpm: Int = 85,
        targetCadence: Int = 85,
        totalMassKg: Double = 76.5,
        cda: Double = 0.32
    ) -> Int {
        if isStopped || isCoasting || speedMps < 0.3 || cadenceRpm <= 0 { return 0 }
        
        let pAero = 0.5 * cda * airDensity * pow(speedMps, 3)
        let pRolling = crr * totalMassKg * gravity * speedMps
        let pGravity = totalMassKg * gravity * speedMps * (gradientPercent / 100.0)
        
        let net = (pAero + pRolling + pGravity) / drivetrainEfficiency
        
        // Physically scale power during cadence degradation (spin-down / spin-up)
        let cadenceFactor = targetCadence > 0 ? min(1.0, Double(cadenceRpm) / Double(targetCadence)) : 1.0
        let scaledWatts = net * cadenceFactor
        
        return max(0, min(1600, Int(scaledWatts.rounded())))
    }

    /// Running power model based on Stryd empirical formulation
    public static func calculateRunningWatts(
        speedMps: Double,
        gradientPercent: Double,
        isStopped: Bool,
        runnerMassKg: Double = 68.0
    ) -> Int {
        if isStopped || speedMps < 0.3 { return 0 }
        // Base flat cost ~ 1.04 W/kg per m/s
        let basePower = 1.04 * runnerMassKg * speedMps
        let gradeCost = runnerMassKg * gravity * speedMps * (gradientPercent / 100.0) * 0.8
        let total = basePower + gradeCost
        return max(0, min(800, Int(total.rounded())))
    }

    /// Normalized Power (NP) 30s rolling 4th-power algorithm (Dr. Andrew Coggan)
    public static func calculateNormalizedPower(powerSeries: [Int]) -> Int? {
        let valid = powerSeries.filter { $0 > 0 }
        if valid.isEmpty { return nil }
        if powerSeries.count < 30 { return Int(Double(valid.reduce(0, +)) / Double(valid.count)) }
        
        var rollingPow4: [Double] = []
        var windowSum = powerSeries.prefix(30).reduce(0, +)
        rollingPow4.append(pow(Double(windowSum) / 30.0, 4))
        
        for i in 30..<powerSeries.count {
            windowSum += powerSeries[i] - powerSeries[i - 30]
            let avg30 = Double(windowSum) / 30.0
            rollingPow4.append(pow(avg30, 4))
        }
        
        let meanPow4 = rollingPow4.reduce(0.0, +) / Double(rollingPow4.count)
        return Int(pow(meanPow4, 0.25).rounded())
    }

    /// Metabolic calorie burn from mechanical work (kJ) and ~24% gross metabolic efficiency
    public static func calculateCalories(
        totalWorkJoules: Int64,
        movingTimeSeconds: Int64,
        averageHr: Int?,
        sport: SportType
    ) -> Int {
        if sport == .cycling && totalWorkJoules > 1000 {
            let workKj = Double(totalWorkJoules) / 1000.0
            // 1 kcal = 4.184 kJ. With ~24% efficiency, (1 / (4.184 * 0.24)) ~ 0.995 - 1.05 kcal/kJ
            return max(1, Int((workKj * 1.05).rounded()))
        }
        
        // Running fallback or HR-based metabolic estimation
        let hours = Double(movingTimeSeconds) / 3600.0
        let hr = Double(averageHr ?? 145)
        let kcalPerHour = max(350.0, (hr - 60.0) * 6.5)
        return max(1, Int((kcalPerHour * hours).rounded()))
    }
}

public struct HeartRateEngine {
    public static func simulateHeartRateSeries(
        speedsMps: [Double],
        gradients: [Double],
        timeDeltasSec: [Double],
        isStopped: [Bool],
        sport: SportType,
        profileParams: ProfileParameters,
        hrConfig: HeartRateConfig
    ) -> [Int] {
        let n = speedsMps.count
        if n == 0 { return [] }
        var result = [Int](repeating: hrConfig.baseHr, count: n)
        
        // Active aerobic base HR (Zone 2 endurance) and resting heart rate
        let activeBaseHr = Double(hrConfig.baseHr)
        let restingHr = Double(min(68, max(52, hrConfig.baseHr - 68))) // ~65 bpm resting
        let maxHr = Double(hrConfig.maxHr)
        let hrSpan = max(20.0, maxHr - activeBaseHr)
        
        var currentHr = activeBaseHr
        var cumulativeMovingSec = 0.0
        
        for i in 0..<n {
            let dt = max(0.5, timeDeltasSec[i])
            
            // 1. Stopped / Auto-Pause (Red light, coffee stop, rest)
            if isStopped[i] {
                // Smooth exponential recovery towards true resting heart rate (tau = 45s)
                let alpha = 1.0 - exp(-dt / 45.0)
                currentHr = currentHr + (restingHr - currentHr) * alpha
                result[i] = Int(currentHr.rounded().clamped(to: restingHr...maxHr))
                continue
            }
            
            cumulativeMovingSec += dt
            let movingHours = cumulativeMovingSec / 3600.0
            let g = gradients[i]
            
            // 2. True Physiological Effort Fraction of Heart Rate Reserve:
            // Flat (0%): ~45% of HR reserve (Zone 2 Endurance)
            // Uphill (g > 0): climbs towards 95%-100% of HR reserve (Zone 4-5)
            // Downhill (g < 0): drops towards recovery Zone 1 (120-130 bpm)
            var targetFraction: Double
            if g > 0.5 {
                let climbIntensity = min(1.0, g / 10.0)
                targetFraction = 0.45 + 0.53 * pow(climbIntensity, 0.75)
            } else if g < -1.5 {
                let descentRelief = min(1.0, abs(g) / 8.0)
                targetFraction = max(0.12, 0.45 - 0.33 * descentRelief)
            } else {
                targetFraction = 0.45
            }
            
            // 3. Cardiac Drift (Aerobic Decoupling & Fatigue Accumulation):
            // Over long endurance rides (especially past 45m - 1.5h or >40km), fluid loss, glycogen depletion,
            // and core body temperature rise cause heart rate to drift upward (+5 to +12 bpm)
            let progress = (n > 1) ? Double(i) / Double(n - 1) : 0.0
            let timeDrift = min(9.5, max(0.0, (movingHours - 0.6) * 2.6))
            let courseDrift = 3.5 * pow(progress, 0.9)
            let cardiacDrift = min(12.0, timeDrift + courseDrift)
            
            // 4. Biological micro-fluctuations (Respiratory Sinus Arrhythmia & pedal stroke oscillation)
            // Prevents digital flatlining across kilometers of flat terrain
            let biologicalWave = 1.6 * sin(Double(i) * 0.14) + 0.8 * cos(Double(i) * 0.31)
            
            let targetHr = (activeBaseHr + hrSpan * targetFraction + cardiacDrift + biologicalWave).clamped(to: restingHr...maxHr)
            
            // 5. Physiological Cardiac Lag (Differential Time Constants):
            // - Post-pause cardiac ramp-up lag: When resuming after rest (currentHr < activeBaseHr - 5),
            //   cardiovascular response takes 45-75 seconds to return to aerobic zones
            // - Regular uphill acceleration: tau = 14.0s (realistic cardiac lag, not instant digital jump)
            // - Recovery on descents: tau = 22.0s
            let tau: Double
            if targetHr >= currentHr {
                if currentHr < activeBaseHr - 5.0 {
                    tau = 45.0 // Smooth gradual ramp from rest
                } else {
                    tau = 14.0 // Natural cardiovascular rise on effort
                }
            } else {
                tau = 22.0 // Smooth parasympathetic cardiac deceleration
            }
            
            let alpha = 1.0 - exp(-dt / tau)
            currentHr = currentHr + (targetHr - currentHr) * alpha
            
            result[i] = Int(currentHr.rounded().clamped(to: restingHr...maxHr))
        }
        
        return result
    }
}

public struct CadenceEngine {
    public static func simulateCadenceSeries(
        speedsMps: [Double],
        gradients: [Double],
        timeDeltasSec: [Double],
        isStopped: [Bool],
        sport: SportType,
        cadenceConfig: CadenceConfig
    ) -> [Int] {
        let n = speedsMps.count
        if n == 0 { return [] }
        var result = [Int](repeating: 0, count: n)
        
        let targetCadence = Double(cadenceConfig.targetCadence)
        var currentCadence = targetCadence
        
        for i in 0..<n {
            let dt = max(0.5, timeDeltasSec[i])
            let isStop = isStopped[i] || speedsMps[i] < 0.2
            let isCoasting = (sport == .cycling && cadenceConfig.allowCoastingZeroRpm && gradients[i] <= -2.5)
            
            if sport == .cycling {
                if isStop || isCoasting {
                    // Exponential Spin-Down Degradation:
                    // Crankarms smoothly decelerate with physical inertia (tau = 2.5s)
                    // Snaps cleanly to 0 RPM only when decaying below 6 RPM
                    let alpha = 1.0 - exp(-dt / 2.5)
                    currentCadence = currentCadence * (1.0 - alpha)
                    if currentCadence < 6.0 {
                        currentCadence = 0.0
                    }
                    result[i] = Int(currentCadence.rounded())
                    continue
                }
                
                // Active Pedaling Dynamics:
                // - Climb cadence penalty: on steep gradients, cadence drops naturally (gear torque grinding)
                // - Slight descent / flat spin: spinning up cadence
                // - Natural pedaling rhythm harmonics (prevents static integer flatline)
                var desiredCadence = targetCadence
                if gradients[i] > 1.5 {
                    let climbDrop = min(32.0, (gradients[i] - 1.5) * 3.2)
                    desiredCadence = max(55.0, targetCadence - climbDrop)
                } else if gradients[i] < 0.0 && gradients[i] > -2.5 {
                    let spinBoost = min(8.0, abs(gradients[i]) * 3.0)
                    desiredCadence = min(112.0, targetCadence + spinBoost)
                }
                
                // Add natural peloton/solo harmonic cadence modulation
                let cadenceHarmonic = 2.2 * sin(Double(i) * 0.18) + 1.2 * cos(Double(i) * 0.41)
                desiredCadence = max(50.0, desiredCadence + cadenceHarmonic)
                
                // Exponential Spin-Up / Dynamic tracking:
                // - From coasting (currentCadence == 0 or low): smooth spin-up ramp (tau = 3.5s)
                // - Steady pedaling: responsive cadence tracking (tau = 2.0s)
                let tau = (currentCadence < desiredCadence * 0.5) ? 3.5 : 2.0
                let alpha = 1.0 - exp(-dt / tau)
                currentCadence = currentCadence + (desiredCadence - currentCadence) * alpha
                
                result[i] = Int(currentCadence.rounded())
            } else {
                // Running SPM (steps per minute)
                if isStop {
                    let alpha = 1.0 - exp(-dt / 1.8)
                    currentCadence = currentCadence * (1.0 - alpha)
                    if currentCadence < 20.0 { currentCadence = 0.0 }
                    result[i] = Int(currentCadence.rounded())
                    continue
                }
                
                let gradePenalty = (gradients[i] > 2.5) ? min(18.0, gradients[i] * 1.8) : 0.0
                let runHarmonic = 1.8 * sin(Double(i) * 0.15)
                let desiredSpm = (targetCadence - gradePenalty + runHarmonic).clamped(to: 140.0...205.0)
                
                let tau = (currentCadence < 80.0) ? 2.5 : 1.5
                let alpha = 1.0 - exp(-dt / tau)
                currentCadence = currentCadence + (desiredSpm - currentCadence) * alpha
                
                result[i] = Int(currentCadence.rounded())
            }
        }
        
        return result
    }
}

extension Comparable {
    func clamped(to range: ClosedRange<Self>) -> Self {
        min(max(self, range.lowerBound), range.upperBound)
    }
}
