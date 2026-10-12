import SwiftUI
import Charts
import MapKit

// MARK: - Strava Design System
public struct StravaTheme {
    public static let orange = Color(red: 0.988, green: 0.322, blue: 0.0) // Iconic Strava Orange #FC5200
    public static let orangeGradient = LinearGradient(
        colors: [Color(red: 0.988, green: 0.322, blue: 0.0), Color(red: 1.0, green: 0.45, blue: 0.15)],
        startPoint: .leading,
        endPoint: .trailing
    )
    
    public static func background(_ isDark: Bool) -> Color {
        isDark ? Color(red: 0.07, green: 0.07, blue: 0.08) : Color(red: 0.96, green: 0.96, blue: 0.97)
    }
    
    public static func cardBackground(_ isDark: Bool) -> Color {
        isDark ? Color(red: 0.11, green: 0.12, blue: 0.14) : Color.white
    }
    
    public static func cardBorder(_ isDark: Bool) -> Color {
        isDark ? Color.white.opacity(0.08) : Color.black.opacity(0.06)
    }
    
    public static func textPrimary(_ isDark: Bool) -> Color {
        isDark ? Color.white : Color(red: 0.12, green: 0.12, blue: 0.14)
    }
    
    public static func textSecondary(_ isDark: Bool) -> Color {
        isDark ? Color(red: 0.60, green: 0.62, blue: 0.68) : Color(red: 0.45, green: 0.47, blue: 0.52)
    }
    
    public static func subtlePill(_ isDark: Bool) -> Color {
        isDark ? Color.white.opacity(0.08) : Color.black.opacity(0.05)
    }
}

// MARK: - Main Content View
public struct MainContentView: View {
    @State private var viewModel = AppViewModel()
    
    public init() {}
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        NavigationStack {
            ZStack(alignment: .bottom) {
                StravaTheme.background(isDark)
                    .ignoresSafeArea()
                
                switch viewModel.currentScreen {
                case .home:
                    HomeView(viewModel: viewModel)
                case .routeStudio:
                    RouteStudioView(viewModel: viewModel)
                case .routeReview:
                    RouteReviewView(viewModel: viewModel)
                case .activitySettings:
                    ActivitySettingsView(viewModel: viewModel)
                case .preview:
                    SimulationPreviewView(viewModel: viewModel)
                case .export:
                    ExportView(viewModel: viewModel)
                }
                
                if shouldShowFloatingDock(viewModel.currentScreen) {
                    FloatingWorkflowDock(viewModel: viewModel, isDark: isDark)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 6)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
            }
            .navigationTitle(navTitleForScreen(viewModel.currentScreen))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                // TOMBOL KEMBALI (BACK BUTTON)
                ToolbarItem(placement: .topBarLeading) {
                    if viewModel.canGoBack {
                        Button {
                            withAnimation(.easeInOut(duration: 0.2)) {
                                viewModel.goBack()
                            }
                        } label: {
                            HStack(spacing: 3) {
                                Image(systemName: "chevron.left")
                                    .font(.system(size: 15, weight: .bold))
                                Text("Kembali")
                                    .font(.system(size: 14, weight: .semibold))
                            }
                            .foregroundStyle(StravaTheme.orange)
                        }
                        .sensoryFeedback(.selection, trigger: viewModel.currentScreen)
                    }
                }
                
                // TOMBOL PANDUAN TUTORIAL & PENGATURAN PROFIL PENGGUNA
                ToolbarItem(placement: .topBarTrailing) {
                    HStack(spacing: 12) {
                        Button {
                            viewModel.showTutorialSheet = true
                        } label: {
                            Image(systemName: "questionmark.circle")
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundStyle(StravaTheme.textSecondary(isDark))
                        }
                        .sensoryFeedback(.selection, trigger: viewModel.showTutorialSheet)
                        
                        Button {
                            viewModel.showProfileSettingsSheet = true
                        } label: {
                            ZStack {
                                Circle()
                                    .fill(StravaTheme.orangeGradient)
                                    .frame(width: 30, height: 30)
                                Text(userInitials(viewModel.settings.userProfile.name))
                                    .font(.system(size: 11, weight: .black))
                                    .foregroundStyle(.white)
                            }
                        }
                        .sensoryFeedback(.selection, trigger: viewModel.showProfileSettingsSheet)
                    }
                }
            }
            .sheet(isPresented: $viewModel.showTutorialSheet, onDismiss: {
                viewModel.markTutorialSeen()
            }) {
                TutorialSheet(viewModel: viewModel, isDark: isDark)
            }
            .sheet(isPresented: $viewModel.showProfileSettingsSheet) {
                ProfileSettingsSheet(viewModel: viewModel)
            }
            .sheet(isPresented: $viewModel.showHistorySheet) {
                ActivityHistorySheet(viewModel: viewModel)
            }
        }
        .preferredColorScheme(isDark ? .dark : .light)
    }
    
    private func userInitials(_ name: String) -> String {
        let parts = name.split(separator: " ")
        if parts.count >= 2 {
            return "\(parts[0].prefix(1))\(parts[1].prefix(1))".uppercased()
        }
        return String(name.prefix(2)).uppercased()
    }
    
    private func navTitleForScreen(_ screen: AppScreen) -> String {
        switch screen {
        case .home: return "Strava Kalcer"
        case .routeStudio: return "Route Studio"
        case .routeReview: return "Tinjauan Rute"
        case .activitySettings: return "Parameter Aktivitas"
        case .preview: return "Detail Aktivitas"
        case .export: return "Ekspor Berkas FIT"
        }
    }
    
    private func shouldShowFloatingDock(_ screen: AppScreen) -> Bool {
        screen == .routeReview || screen == .activitySettings || screen == .preview
    }
}

// MARK: - Home View
public struct HomeView: View {
    @Bindable var viewModel: AppViewModel
    @State private var showFilePicker = false
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        VStack(spacing: 24) {
            Spacer()
            
            // Strava Kalcer Brand Emblem
            VStack(spacing: 14) {
                ZStack {
                    Circle()
                        .fill(StravaTheme.orange.opacity(0.15))
                        .frame(width: 96, height: 96)
                    
                    Image(systemName: "figure.outdoor.cycle")
                        .font(.system(size: 46, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                }
                
                VStack(spacing: 6) {
                    Text("STRAVA KALCER")
                        .font(.system(size: 26, weight: .black, design: .rounded))
                        .tracking(1.2)
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                    
                    Text("Physics-driven GPX to Garmin FIT Simulation Engine")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                }
            }
            
            Spacer()
            
            // Action Buttons
            VStack(spacing: 12) {
                // Button 0: Buat Rute Sendiri (Route Studio)
                Button {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        viewModel.currentScreen = .routeStudio
                    }
                } label: {
                    HStack(spacing: 10) {
                        Image(systemName: "pencil.and.outline")
                            .font(.system(size: 16, weight: .bold))
                        Text("Buat Rute Sendiri (Route Studio)")
                            .font(.system(size: 16, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(
                        LinearGradient(
                            colors: [Color(red: 0.98, green: 0.38, blue: 0.12), Color(red: 0.85, green: 0.22, blue: 0.05)],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                    .shadow(color: StravaTheme.orange.opacity(0.35), radius: 8, x: 0, y: 4)
                }
                .sensoryFeedback(.impact, trigger: viewModel.currentScreen)
                
                // Button 1: Impor GPX
                Button {
                    showFilePicker = true
                } label: {
                    HStack(spacing: 10) {
                        Image(systemName: "square.and.arrow.down.fill")
                            .font(.system(size: 16, weight: .semibold))
                        Text("Impor Berkas GPX")
                            .font(.system(size: 16, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(StravaTheme.cardBackground(isDark))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                    .overlay(
                        RoundedRectangle(cornerRadius: 14, style: .continuous)
                            .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                }
                .sensoryFeedback(.impact, trigger: showFilePicker)
                
                // Button 2: Rute Sampel Berbukit
                Button {
                    viewModel.loadSampleRoute()
                } label: {
                    HStack(spacing: 10) {
                        Image(systemName: "map.fill")
                            .font(.system(size: 15, weight: .medium))
                        Text("Gunakan Rute Berbukit (8.9 km)")
                            .font(.system(size: 15, weight: .semibold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(StravaTheme.cardBackground(isDark))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                    .overlay(
                        RoundedRectangle(cornerRadius: 14, style: .continuous)
                            .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                }
                .sensoryFeedback(.selection, trigger: viewModel.currentScreen)
                
                // Button 3: Tutorial / Panduan Penggunaan
                Button {
                    viewModel.showTutorialSheet = true
                } label: {
                    HStack(spacing: 8) {
                        Image(systemName: "book.pages.fill")
                            .font(.system(size: 14))
                        Text("Panduan / Tutorial Penggunaan")
                            .font(.system(size: 14, weight: .semibold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 44)
                    .background(StravaTheme.subtlePill(isDark))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                }
                .sensoryFeedback(.selection, trigger: viewModel.showTutorialSheet)
                
                // Button 4: Riwayat Berkas .FIT
                if !viewModel.archivedActivities.isEmpty {
                    Button {
                        viewModel.showHistorySheet = true
                    } label: {
                        HStack(spacing: 8) {
                            Image(systemName: "clock.arrow.circlepath")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(StravaTheme.orange)
                            Text("Riwayat Berkas FIT (\(viewModel.archivedActivities.count))")
                                .font(.system(size: 14, weight: .bold))
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundStyle(StravaTheme.textSecondary(isDark))
                        }
                        .padding(.horizontal, 16)
                        .frame(maxWidth: .infinity)
                        .frame(height: 46)
                        .background(StravaTheme.cardBackground(isDark))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12, style: .continuous)
                                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                        )
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    }
                    .sensoryFeedback(.selection, trigger: viewModel.showHistorySheet)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .fileImporter(
            isPresented: $showFilePicker,
            allowedContentTypes: [.xml, .data],
            allowsMultipleSelection: false
        ) { result in
            switch result {
            case .success(let urls):
                if let url = urls.first, url.startAccessingSecurityScopedResource() {
                    defer { url.stopAccessingSecurityScopedResource() }
                    if let data = try? Data(contentsOf: url) {
                        viewModel.loadGpxData(data, fileName: url.lastPathComponent)
                    }
                }
            case .failure(let err):
                print("Picker error: \(err)")
            }
        }
    }
}

// MARK: - Route Review View
public struct RouteReviewView: View {
    @Bindable var viewModel: AppViewModel
    @State private var scrubDistanceKm: Double = 0.0
    @State private var rawSelectedKm: Double? = nil
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        guard let track = viewModel.currentTrack else {
            return AnyView(Text("Tidak ada rute").foregroundStyle(StravaTheme.textPrimary(isDark)))
        }
        
        let totalKm = track.totalDistanceMeters / 1000.0
        let targetM = scrubDistanceKm * 1000.0
        let currentPt = track.points.min(by: { abs($0.distanceFromStartMeters - targetM) < abs($1.distanceFromStartMeters - targetM) }) ?? track.points.first ?? RoutePoint(index: 0, latitude: 0, longitude: 0, elevation: 0)
        let currentCoord = CLLocationCoordinate2D(latitude: currentPt.latitude, longitude: currentPt.longitude)
        
        let g = currentPt.smoothedGradient
        let isCycling = (viewModel.settings.sport == .cycling)
        
        // Physics & Physiology Estimates for Current Scrubbed Point
        let maxHr = Double(viewModel.settings.userProfile.estimatedMaxHr)
        let baseHr = Double(viewModel.settings.hrConfig.baseHr)
        let estHr: Int = {
            if g > 0.5 {
                let climbIntensity = min(1.0, g / 10.0)
                let frac = 0.45 + 0.53 * pow(climbIntensity, 0.75)
                return Int((baseHr + (maxHr - baseHr) * frac).rounded())
            } else if g < -1.5 {
                let descentRelief = min(1.0, abs(g) / 8.0)
                let frac = max(0.12, 0.45 - 0.33 * descentRelief)
                return Int((baseHr + (maxHr - baseHr) * frac).rounded())
            } else {
                return Int(baseHr)
            }
        }()
        
        let targetSpeedKmh = viewModel.settings.targetAverageSpeedKmh
        let peakSpeedKmh = viewModel.settings.targetPeakSpeedKmh
        let estSpd: Double = {
            if isCycling {
                if g > 0.3 {
                    return max(5.4, targetSpeedKmh / (1.0 + 0.22 * pow(g, 0.94)))
                } else if g < -1.0 {
                    let intensity = min(1.0, pow(abs(g) / 7.0, 0.88))
                    return min(peakSpeedKmh, targetSpeedKmh + (peakSpeedKmh - targetSpeedKmh) * intensity)
                } else {
                    return targetSpeedKmh
                }
            } else {
                if g > 0.3 {
                    return max(4.0, targetSpeedKmh / (1.0 + 0.12 * pow(g, 0.88)))
                } else if g < -1.0 {
                    return min(peakSpeedKmh, targetSpeedKmh + 2.0)
                } else {
                    return targetSpeedKmh
                }
            }
        }()
        
        let estCad: Int = {
            if isCycling {
                if g <= -2.5 { return 0 }
                if g > 1.5 { return max(55, viewModel.settings.cadenceConfig.targetCadence - Int(min(32.0, (g - 1.5) * 3.2))) }
                if g < 0 { return min(110, viewModel.settings.cadenceConfig.targetCadence + 5) }
                return viewModel.settings.cadenceConfig.targetCadence
            } else {
                let penalty = (g > 2.5) ? Int(min(18.0, g * 1.8)) : 0
                return max(140, min(205, viewModel.settings.cadenceConfig.targetCadence - penalty))
            }
        }()
        
        let estWatts: Int = {
            if isCycling {
                return PowerEngine.calculateCyclingWatts(
                    speedMps: GeoMath.kmhToMps(estSpd),
                    gradientPercent: g,
                    isStopped: false,
                    isCoasting: g <= -2.5,
                    cadenceRpm: estCad,
                    targetCadence: viewModel.settings.cadenceConfig.targetCadence,
                    totalMassKg: viewModel.settings.userProfile.totalMassKg,
                    cda: viewModel.settings.userProfile.estimatedCdA
                )
            } else {
                return PowerEngine.calculateRunningWatts(
                    speedMps: GeoMath.kmhToMps(estSpd),
                    gradientPercent: g,
                    isStopped: false,
                    runnerMassKg: viewModel.settings.userProfile.weightKg
                )
            }
        }()
        
        // Course Level Predicted Avg & Max Heart Rate
        let courseClimbRatio = min(1.0, track.elevationGainMeters / max(100.0, track.totalDistanceMeters * 0.015))
        let predictedAvgHr = Int(baseHr + (maxHr - baseHr) * (0.42 + 0.18 * courseClimbRatio))
        let predictedMaxHr = Int(min(maxHr, baseHr + (maxHr - baseHr) * 0.96))
        
        return AnyView(
            ScrollView {
                VStack(spacing: 16) {
                    // Header Status & Balik Rute
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text(viewModel.trackFileName)
                                .font(.system(size: 17, weight: .bold))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                            
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(viewModel.isReversed ? .orange : .green)
                                    .frame(width: 8, height: 8)
                                Text(viewModel.isReversed ? "Arah Rute: Dibalik (Reversed)" : "Arah Rute: Normal")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundStyle(viewModel.isReversed ? .orange : .green)
                            }
                        }
                        Spacer()
                        Button {
                            viewModel.reverseRoute()
                        } label: {
                            HStack(spacing: 4) {
                                Image(systemName: "arrow.triangle.swap")
                                Text("Balik")
                                    .font(.system(size: 12, weight: .semibold))
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(StravaTheme.subtlePill(isDark))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                            .clipShape(Capsule())
                        }
                        .sensoryFeedback(.impact, trigger: viewModel.isReversed)
                    }
                    .padding(16)
                    .background(StravaTheme.cardBackground(isDark))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16, style: .continuous)
                            .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    
                    // 1. DETAIL JARAK & ELEV GAIN (Hero Stat Cards)
                    VStack(spacing: 8) {
                        HStack(spacing: 10) {
                            StravaStatCard(
                                title: "JARAK TOTAL",
                                value: String(format: "%.1f", totalKm),
                                unit: "km",
                                isDark: isDark
                            )
                            StravaStatCard(
                                title: "ELEV GAIN",
                                value: String(format: "+%.0f", track.elevationGainMeters),
                                unit: "m",
                                isDark: isDark
                            )
                            StravaStatCard(
                                title: "MAX ELEVASI",
                                value: String(format: "%.0f", track.maxElevation),
                                unit: "m",
                                isDark: isDark
                            )
                        }
                        
                        // Heart Rate & Grade Summary Card
                        HStack(spacing: 10) {
                            StravaStatCard(
                                title: "EST. AVG HR",
                                value: "\(predictedAvgHr)",
                                unit: "bpm (Z2)",
                                isDark: isDark
                            )
                            StravaStatCard(
                                title: "EST. MAX HR",
                                value: "\(predictedMaxHr)",
                                unit: "bpm (Z4/5)",
                                isDark: isDark
                            )
                            StravaStatCard(
                                title: "MAX GRADIEN",
                                value: String(format: "+%.1f", track.points.map { $0.smoothedGradient }.max() ?? 0.0),
                                unit: "%",
                                isDark: isDark
                            )
                        }
                    }
                    
                    // 2. MAPKIT PREVIEW DIBAWAH DETAIL STATISTIK
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("PETA RUTE")
                                    .font(.system(size: 11, weight: .bold))
                                    .tracking(1.0)
                                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                                Text("Marker oranye mengikuti posisi yang digeser")
                                    .font(.system(size: 11))
                                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                            }
                            Spacer()
                            Text(String(format: "KM %.1f", scrubDistanceKm))
                                .font(.system(size: 12, weight: .bold))
                                .foregroundStyle(StravaTheme.orange)
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 14)
                        
                        RouteMapView(
                            routePoints: track.points,
                            selectedCoordinate: currentCoord,
                            isDark: isDark
                        )
                        .frame(height: 220)
                        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                        .padding(.horizontal, 10)
                        .padding(.bottom, 12)
                    }
                    .background(StravaTheme.cardBackground(isDark))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16, style: .continuous)
                            .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    
                    // 3. ELEVASI & LIVE TELEMETRY SCRUBBER HUD
                    VStack(alignment: .leading, spacing: 14) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("PROFIL ELEVASI")
                                    .font(.system(size: 11, weight: .bold))
                                    .tracking(1.0)
                                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                                Text("Sentuh & geser grafik untuk melihat kontur rute")
                                    .font(.system(size: 11))
                                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                            }
                            Spacer()
                        }
                        .padding(.horizontal, 16)
                        .padding(.top, 14)
                        

                        
                        // Apple Charts Interactive Elevation Profile
                        let chartPts = downsampleRoutePoints(track.points, maxPoints: 300)
                        Chart(chartPts) { pt in
                            AreaMark(
                                x: .value("Jarak (km)", pt.distanceFromStartMeters / 1000.0),
                                y: .value("Elevasi (m)", pt.elevation)
                            )
                            .foregroundStyle(
                                LinearGradient(
                                    colors: [StravaTheme.orange.opacity(0.42), StravaTheme.orange.opacity(0.03)],
                                    startPoint: .top,
                                    endPoint: .bottom
                                )
                            )
                            
                            LineMark(
                                x: .value("Jarak (km)", pt.distanceFromStartMeters / 1000.0),
                                y: .value("Elevasi (m)", pt.elevation)
                            )
                            .foregroundStyle(StravaTheme.orange)
                            .lineStyle(StrokeStyle(lineWidth: 2.5))
                            
                            // Glowing Scrubber RuleMark
                            RuleMark(x: .value("Jarak", scrubDistanceKm))
                                .foregroundStyle(StravaTheme.orange.opacity(0.85))
                                .lineStyle(StrokeStyle(lineWidth: 1.5, dash: [4, 4]))
                            
                            PointMark(
                                x: .value("Jarak", currentPt.distanceFromStartMeters / 1000.0),
                                y: .value("Elevasi", currentPt.elevation)
                            )
                            .foregroundStyle(StravaTheme.orange)
                            .symbolSize(70)
                        }
                        .chartXScale(domain: 0...totalKm)
                        .chartXSelection(value: $rawSelectedKm)
                        .onChange(of: rawSelectedKm) { _, newVal in
                            if let newVal {
                                scrubDistanceKm = min(max(0, newVal), totalKm)
                            }
                        }
                        .chartPlotStyle { plot in
                            plot.clipped()
                        }
                        .frame(height: 180)
                        .chartXAxis {
                            AxisMarks(position: .bottom) {
                                AxisGridLine().foregroundStyle(StravaTheme.cardBorder(isDark))
                                AxisValueLabel().foregroundStyle(StravaTheme.textSecondary(isDark))
                            }
                        }
                        .chartYAxis {
                            AxisMarks(position: .leading) {
                                AxisGridLine().foregroundStyle(StravaTheme.cardBorder(isDark))
                                AxisValueLabel().foregroundStyle(StravaTheme.textSecondary(isDark))
                            }
                        }
                        .padding(.horizontal, 8)
                        
                        // Scrubber Slider Control
                        VStack(spacing: 6) {
                            HStack {
                                Text("Geser Penunjuk Rute:")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                                Spacer()
                                Text(String(format: "%.1f km / %.1f km", scrubDistanceKm, totalKm))
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundStyle(StravaTheme.orange)
                            }
                            Slider(value: $scrubDistanceKm, in: 0...totalKm, step: 0.1)
                                .tint(StravaTheme.orange)
                            
                            // Quick Jump Buttons
                            HStack(spacing: 6) {
                                quickJumpButton(title: "Start", targetKm: 0.0, current: $scrubDistanceKm, isDark: isDark)
                                if let peakPt = track.points.max(by: { $0.elevation < $1.elevation }) {
                                    quickJumpButton(title: "Puncak (\(String(format: "%.0fm", peakPt.elevation)))", targetKm: peakPt.distanceFromStartMeters / 1000.0, current: $scrubDistanceKm, isDark: isDark)
                                }
                                quickJumpButton(title: "Tengah (50%)", targetKm: totalKm * 0.5, current: $scrubDistanceKm, isDark: isDark)
                                quickJumpButton(title: "Finish", targetKm: totalKm, current: $scrubDistanceKm, isDark: isDark)
                            }
                        }
                        .padding(.horizontal, 14)
                        .padding(.bottom, 14)
                    }
                    .background(StravaTheme.cardBackground(isDark))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16, style: .continuous)
                            .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    
                    // Bottom Spacer for Floating Dock
                    Spacer().frame(height: 75)
                }
                .padding(16)
            }
        )
    }
}

// MARK: - Gradient and HUD Helpers
func gradientColor(_ g: Double) -> Color {
    if g >= 6.0 { return .red }
    if g >= 2.5 { return .orange }
    if g > 0.5 { return .yellow }
    if g >= -1.5 { return .green }
    if g > -4.0 { return .teal }
    return .blue
}

func gradientDescription(_ g: Double) -> String {
    if g >= 7.0 { return "Tanjakan Sangat Curam" }
    if g >= 4.0 { return "Tanjakan Curam" }
    if g >= 1.5 { return "Tanjakan Sedang" }
    if g > 0.5 { return "Tanjakan Landai" }
    if g >= -1.0 { return "Jalan Datar" }
    if g >= -3.5 { return "Turunan Landai" }
    return "Turunan Cepat (Coasting)"
}

func gearAdvice(_ g: Double) -> String {
    if g >= 6.0 { return "Granny Gear (34x32)" }
    if g >= 3.0 { return "Small Ring (34x24)" }
    if g >= 0.5 { return "Mid Gear (50x21)" }
    if g >= -2.0 { return "Big Ring (50x16)" }
    return "High Gear (50x11)"
}

@ViewBuilder
func liveMetricCell(title: String, value: String, isDark: Bool, highlight: Bool = false) -> some View {
    VStack(alignment: .leading, spacing: 2) {
        Text(title)
            .font(.system(size: 9, weight: .bold))
            .foregroundStyle(StravaTheme.textSecondary(isDark))
        Text(value)
            .font(.system(size: 13, weight: .heavy))
            .foregroundStyle(highlight ? StravaTheme.orange : StravaTheme.textPrimary(isDark))
    }
    .frame(maxWidth: .infinity, alignment: .leading)
}

@ViewBuilder
func quickJumpButton(title: String, targetKm: Double, current: Binding<Double>, isDark: Bool) -> some View {
    Button {
        current.wrappedValue = targetKm
    } label: {
        Text(title)
            .font(.system(size: 10, weight: .semibold))
            .padding(.horizontal, 8)
            .padding(.vertical, 5)
            .background(StravaTheme.cardBackground(isDark))
            .foregroundStyle(StravaTheme.textPrimary(isDark))
            .clipShape(Capsule())
            .overlay(Capsule().stroke(StravaTheme.cardBorder(isDark), lineWidth: 1))
    }
}

// MARK: - Activity Settings View & Modular Subcards

public struct SportSelectorCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let sport = viewModel.settings.sport
        VStack(alignment: .leading, spacing: 12) {
            Text("JENIS OLAHRAGA")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            HStack(spacing: 8) {
                SportButton(title: "Sepeda", icon: "figure.outdoor.cycle", isSelected: sport == .cycling, isDark: isDark) {
                    viewModel.updateSport(.cycling)
                }
                SportButton(title: "Lari", icon: "figure.run", isSelected: sport == .running, isDark: isDark) {
                    viewModel.updateSport(.running)
                }
                SportButton(title: "Hiking", icon: "figure.hiking", isSelected: sport == .hiking, isDark: isDark) {
                    viewModel.updateSport(.hiking)
                }
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct ActivityDateTimeCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("WAKTU & TANGGAL AKTIVITAS")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            DatePicker(
                "Pilih Waktu",
                selection: Binding(
                    get: { viewModel.activityDate },
                    set: { viewModel.updateActivityDate($0) }
                ),
                displayedComponents: [.date, .hourAndMinute]
            )
            .datePickerStyle(.compact)
            .tint(StravaTheme.orange)
            
            HStack(spacing: 6) {
                Image(systemName: "calendar.badge.clock")
                    .font(.system(size: 12))
                    .foregroundStyle(StravaTheme.orange)
                Text(viewModel.activityDateText)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct TargetSpeedAndPeakCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let sport = viewModel.settings.sport
        let avgSpeedText = String(format: "%.1f km/h", viewModel.settings.targetAverageSpeedKmh)
        let peakSpeedText = String(format: "%.1f km/h", viewModel.settings.targetPeakSpeedKmh)
        let paceSec = Int(viewModel.settings.targetAveragePaceSecondsPerKm)
        let avgPaceText = String(format: "%d:%02d /km", paceSec / 60, paceSec % 60)
        let peakPaceSec = Int(GeoMath.kmhToPaceSecondsPerKm(viewModel.settings.targetPeakSpeedKmh))
        let peakPaceText = String(format: "%.1f km/h (%d:%02d/km)", viewModel.settings.targetPeakSpeedKmh, peakPaceSec / 60, peakPaceSec % 60)
        
        VStack(alignment: .leading, spacing: 16) {
            // 1. Kecepatan / Pace Rata-rata
            VStack(alignment: .leading, spacing: 8) {
                if sport == .cycling {
                    HStack {
                        Text("TARGET KECEPATAN RATA-RATA")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text(avgSpeedText)
                            .font(.system(size: 17, weight: .black))
                            .foregroundStyle(StravaTheme.orange)
                    }
                    Slider(value: $viewModel.settings.targetAverageSpeedKmh, in: 15.0...50.0, step: 0.5)
                        .tint(StravaTheme.orange)
                    HStack {
                        Text("15.0 km/h (Santai)")
                        Spacer()
                        Text("50.0 km/h (Peloton)")
                    }
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    
                    // Quick Preset Chips Cycling
                    HStack(spacing: 6) {
                        ForEach([("24 km/h", 24.0), ("28.5 km/h", 28.5), ("33 km/h", 33.0), ("38 km/h", 38.0)], id: \.0) { item in
                            Button {
                                viewModel.settings.targetAverageSpeedKmh = item.1
                            } label: {
                                Text(item.0)
                                    .font(.system(size: 10, weight: .semibold))
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(abs(viewModel.settings.targetAverageSpeedKmh - item.1) < 0.3 ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                                    .foregroundStyle(abs(viewModel.settings.targetAverageSpeedKmh - item.1) < 0.3 ? .white : StravaTheme.textPrimary(isDark))
                                    .clipShape(Capsule())
                            }
                        }
                    }
                    .padding(.top, 2)
                } else {
                    HStack {
                        Text("TARGET AVERAGE PACE")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text(avgPaceText)
                            .font(.system(size: 17, weight: .black))
                            .foregroundStyle(StravaTheme.orange)
                    }
                    Slider(value: $viewModel.settings.targetAveragePaceSecondsPerKm, in: 180.0...540.0, step: 5.0)
                        .tint(StravaTheme.orange)
                    HStack {
                        Text("3:00 /km (Elite)")
                        Spacer()
                        Text("9:00 /km (Jogging)")
                    }
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    
                    // Quick Preset Chips Running Pace
                    HStack(spacing: 6) {
                        ForEach([("4:00", 240.0), ("4:30", 270.0), ("5:00", 300.0), ("5:30", 330.0), ("6:00", 360.0), ("6:30", 390.0)], id: \.0) { item in
                            Button {
                                viewModel.settings.targetAveragePaceSecondsPerKm = item.1
                            } label: {
                                Text(item.0)
                                    .font(.system(size: 10, weight: .semibold))
                                    .padding(.horizontal, 7)
                                    .padding(.vertical, 4)
                                    .background(abs(viewModel.settings.targetAveragePaceSecondsPerKm - item.1) < 3.0 ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                                    .foregroundStyle(abs(viewModel.settings.targetAveragePaceSecondsPerKm - item.1) < 3.0 ? .white : StravaTheme.textPrimary(isDark))
                                    .clipShape(Capsule())
                            }
                        }
                    }
                    .padding(.top, 2)
                }
            }
            
            Divider().background(StravaTheme.cardBorder(isDark))
            
            // 2. KECEPATAN PUNCAK (PEAK SPEED) UNTUK SEMUA JENIS OLAHRAGA
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    HStack(spacing: 4) {
                        Image(systemName: "bolt.fill")
                            .font(.system(size: 11))
                            .foregroundStyle(StravaTheme.orange)
                        Text(sport == .cycling ? "KECEPATAN PUNCAK (PEAK SPEED)" : "PACE SPRINT MAKSIMAL")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                    }
                    Spacer()
                    Text(sport == .cycling ? peakSpeedText : peakPaceText)
                        .font(.system(size: 16, weight: .black))
                        .foregroundStyle(StravaTheme.orange)
                }
                
                if sport == .cycling {
                    Slider(value: $viewModel.settings.targetPeakSpeedKmh, in: 35.0...85.0, step: 0.5)
                        .tint(StravaTheme.orange)
                    HStack {
                        Text("35.0 km/h (Turunan Santai)")
                        Spacer()
                        Text("85.0 km/h (Aero Descent)")
                    }
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                } else if sport == .running {
                    let avgSpeedKmh = 1000.0 / max(120.0, viewModel.settings.targetAveragePaceSecondsPerKm) * 3.6
                    let minAllowedPeak = max(10.0, (avgSpeedKmh * 1.10).rounded())
                    let maxAllowedPeak = max(26.0, (avgSpeedKmh * 1.8).rounded())
                    Slider(
                        value: Binding(
                            get: { max(minAllowedPeak, viewModel.settings.targetPeakSpeedKmh) },
                            set: { viewModel.settings.targetPeakSpeedKmh = max(minAllowedPeak, $0) }
                        ),
                        in: minAllowedPeak...maxAllowedPeak,
                        step: 0.5
                    )
                    .tint(StravaTheme.orange)
                    HStack {
                        let minPace = Int(GeoMath.kmhToPaceSecondsPerKm(minAllowedPeak))
                        let maxPace = Int(GeoMath.kmhToPaceSecondsPerKm(maxAllowedPeak))
                        Text(String(format: "%.1f km/h (Pace %d:%02d)", minAllowedPeak, minPace / 60, minPace % 60))
                        Spacer()
                        Text(String(format: "%.1f km/h (Sprint %d:%02d)", maxAllowedPeak, maxPace / 60, maxPace % 60))
                    }
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    
                    // Quick Preset Chips Running Peak Sprint
                    HStack(spacing: 6) {
                        ForEach([("Sprint 14 km/h", 14.0), ("Sprint 17 km/h", 17.0), ("Sprint 20 km/h", 20.0), ("Sprint 24 km/h", 24.0)], id: \.0) { item in
                            if item.1 >= minAllowedPeak {
                                Button {
                                    viewModel.settings.targetPeakSpeedKmh = item.1
                                } label: {
                                    Text(item.0)
                                        .font(.system(size: 10, weight: .semibold))
                                        .padding(.horizontal, 7)
                                        .padding(.vertical, 4)
                                        .background(abs(viewModel.settings.targetPeakSpeedKmh - item.1) < 0.4 ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                                        .foregroundStyle(abs(viewModel.settings.targetPeakSpeedKmh - item.1) < 0.4 ? .white : StravaTheme.textPrimary(isDark))
                                        .clipShape(Capsule())
                                }
                            }
                        }
                    }
                    .padding(.top, 2)
                } else {
                    Slider(value: $viewModel.settings.targetPeakSpeedKmh, in: 4.0...12.0, step: 0.5)
                        .tint(StravaTheme.orange)
                    HStack {
                        Text("4.0 km/h")
                        Spacer()
                        Text("12.0 km/h (Downhill Trail)")
                    }
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                
                Text("Kecepatan puncak otomatis dicapai saat turunan tercuram atau sprint lurus sesuai hukum fisika gravitasi & aerodinamika.")
                    .font(.system(size: 11))
                    .lineSpacing(2)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct AutoPauseSettingsCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let totalMin = viewModel.settings.autoPause.totalPauseMinutes
        let pauseMinutesText: String = {
            if totalMin >= 60.0 {
                let h = Int(totalMin) / 60
                let m = Int(totalMin) % 60
                return (m == 0) ? "\(h) Jam" : "\(h) Jam \(m) Menit"
            } else {
                return String(format: "%.0f Menit", totalMin)
            }
        }()
        let pauseCountText = "\(viewModel.settings.autoPause.pauseCount) Titik Perhentian"
        
        VStack(alignment: .leading, spacing: 14) {
            Toggle(isOn: $viewModel.settings.autoPause.enabled) {
                HStack(spacing: 8) {
                    Image(systemName: "pause.circle.fill")
                        .foregroundStyle(StravaTheme.orange)
                    VStack(alignment: .leading, spacing: 2) {
                        Text("SIMULASI ISTIRAHAT & AUTO-PAUSE")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Text("Multi-Stop, Coffee Stop & Rest Area")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                    }
                }
            }
            .tint(StravaTheme.orange)
            
            if viewModel.settings.autoPause.enabled {
                Divider().background(StravaTheme.cardBorder(isDark))
                
                // Tipe Perhentian
                VStack(alignment: .leading, spacing: 6) {
                    Text("TIPE PERHENTIAN")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    
                    Picker("Mode", selection: $viewModel.settings.autoPause.mode) {
                        ForEach(AutoPauseMode.allCases) { mode in
                            Label(mode.rawValue, systemImage: mode.icon).tag(mode)
                        }
                    }
                    .pickerStyle(.menu)
                    .tint(StravaTheme.orange)
                    
                    Text(viewModel.settings.autoPause.mode.description)
                        .font(.system(size: 11))
                        .lineSpacing(2)
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                
                // Total Menit Istirahat (2 Menit s/d 240 Menit / 4 Jam Long Ride)
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text("TOTAL DURASI ISTIRAHAT")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text(pauseMinutesText)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundStyle(StravaTheme.orange)
                    }
                    Slider(value: $viewModel.settings.autoPause.totalPauseMinutes, in: 2.0...240.0, step: 1.0)
                        .tint(StravaTheme.orange)
                    
                    HStack {
                        Text("2m (Singkat)")
                            .font(.system(size: 9))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text("60m (1 Jam)")
                            .font(.system(size: 9))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text("240m (4 Jam Long Ride)")
                            .font(.system(size: 9))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                    }
                }
                
                if viewModel.settings.autoPause.mode == .smartCity || viewModel.settings.autoPause.mode == .longRide || viewModel.settings.autoPause.mode == .custom {
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text("JUMLAH TITIK PERHENTIAN")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundStyle(StravaTheme.textSecondary(isDark))
                            Spacer()
                            Text(pauseCountText)
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                        }
                        Slider(
                            value: Binding(
                                get: { Double(viewModel.settings.autoPause.pauseCount) },
                                set: { viewModel.settings.autoPause.pauseCount = Int($0) }
                            ),
                            in: 1.0...8.0,
                            step: 1.0
                        )
                        .tint(StravaTheme.orange)
                    }
                }
                
                // Daftar Rinci Titik Perhentian Bervariasi
                if let track = viewModel.currentTrack {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("PREVIEW PERHENTIAN BERVARIASI (LONG RIDE)")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundStyle(StravaTheme.textSecondary(isDark))
                            Spacer()
                            Image(systemName: "clock.arrow.2.circlepath")
                                .font(.system(size: 11))
                                .foregroundStyle(StravaTheme.orange)
                        }
                        
                        let plannedStops = generatePlannedStopsPreview(track: track, settings: viewModel.settings)
                        ForEach(plannedStops) { stop in
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(StravaTheme.orange)
                                    .frame(width: 8, height: 8)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(stop.reason)
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                                    Text("KM \(String(format: "%.1f", stop.distanceMeters / 1000.0)) • Durasi: \(formatStopDuration(stop.durationSeconds))")
                                        .font(.system(size: 11))
                                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                                }
                                Spacer()
                            }
                            .padding(8)
                            .background(StravaTheme.subtlePill(isDark))
                            .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                        }
                    }
                    .padding(.top, 4)
                }
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

// MARK: - Heart Rate Engine Settings Card
public struct HeartRateSettingsCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let baseHr = viewModel.settings.hrConfig.baseHr
        let maxHr = viewModel.settings.userProfile.estimatedMaxHr
        
        VStack(alignment: .leading, spacing: 14) {
            Toggle(isOn: $viewModel.settings.hrConfig.enabled) {
                HStack(spacing: 8) {
                    Image(systemName: "heart.fill")
                        .foregroundStyle(Color.red)
                    VStack(alignment: .leading, spacing: 2) {
                        Text("HEART RATE ENGINE (DETAK JANTUNG)")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Text("Simulasi HR Merespons Tanjakan & Elevasi")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                    }
                }
            }
            .tint(StravaTheme.orange)
            
            if viewModel.settings.hrConfig.enabled {
                Divider().background(StravaTheme.cardBorder(isDark))
                
                // Base HR Slider (Zone 2 Aerobic Endurance)
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text("BASE ENDURANCE HR (ZONE 2)")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text("\(baseHr) bpm")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundStyle(StravaTheme.orange)
                    }
                    Slider(
                        value: Binding(
                            get: { Double(viewModel.settings.hrConfig.baseHr) },
                            set: { viewModel.settings.hrConfig.baseHr = Int($0) }
                        ),
                        in: 115.0...165.0,
                        step: 1.0
                    )
                    .tint(StravaTheme.orange)
                    Text("Detak jantung jelajah pada jalan datar. Nilai naik otomatis saat tanjakan (Zone 4-5) dan melandai saat turunan/istirahat.")
                        .font(.system(size: 11))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                
                // Max HR Tanaka Formula
                HStack(spacing: 12) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("TANAKA ESTIMATED MAX HR")
                            .font(.system(size: 10, weight: .bold))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Text("\(maxHr) bpm (Usia: \(viewModel.settings.userProfile.age) thn)")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                    }
                    Spacer()
                    Text("Formula Tanaka 208-0.7*Age")
                        .font(.system(size: 10, weight: .semibold))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(StravaTheme.subtlePill(isDark))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                        .clipShape(Capsule())
                }
                .padding(10)
                .background(StravaTheme.subtlePill(isDark))
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

// MARK: - Helper Functions for Planned Stops
func generatePlannedStopsPreview(track: RouteTrack, settings: SimulationSettings) -> [SimulationStop] {
    let totalPauseSec = Int64(max(30.0, settings.autoPause.totalPauseMinutes * 60.0))
    var stops: [SimulationStop] = []
    
    switch settings.autoPause.mode {
    case .smartCity:
        let count = max(1, min(6, settings.autoPause.pauseCount))
        let perStop = max(20, totalPauseSec / Int64(count))
        for s in 1...count {
            let dist = track.totalDistanceMeters * (Double(s) / Double(count + 1))
            stops.append(SimulationStop(distanceMeters: dist, durationSeconds: perStop, reason: "Lampu Merah / Persimpangan #\(s)"))
        }
    case .coffeeBreak:
        stops.append(SimulationStop(distanceMeters: track.totalDistanceMeters * 0.55, durationSeconds: totalPauseSec, reason: "Coffee Stop / Istirahat Kalcer"))
    case .hilltopRest:
        if let maxPt = track.points.max(by: { $0.elevation < $1.elevation }) {
            stops.append(SimulationStop(distanceMeters: maxPt.distanceFromStartMeters, durationSeconds: totalPauseSec, reason: "Puncak Bukit (\(String(format: "%.0fm", maxPt.elevation)))"))
        } else {
            stops.append(SimulationStop(distanceMeters: track.totalDistanceMeters * 0.5, durationSeconds: totalPauseSec, reason: "Rest Area Puncak"))
        }
    case .longRide:
        let count = max(2, min(6, settings.autoPause.pauseCount))
        let reasons = [
            "Minimarket / Water Refill",
            "Coffee Stop / Warung Makan",
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
            let dist = track.totalDistanceMeters * (Double(s + 1) / Double(count + 1))
            let stopSec = max(30, Int64((Double(totalPauseSec) * ratios[s]).rounded()))
            let reasonText = (s < reasons.count) ? reasons[s] : "Perhentian Long Ride #\(s + 1)"
            stops.append(SimulationStop(distanceMeters: dist, durationSeconds: stopSec, reason: reasonText))
        }
    case .custom:
        let count = max(1, min(8, settings.autoPause.pauseCount))
        let perStop = max(15, totalPauseSec / Int64(count))
        for s in 1...count {
            let dist = track.totalDistanceMeters * (Double(s) / Double(count + 1))
            stops.append(SimulationStop(distanceMeters: dist, durationSeconds: perStop, reason: "Auto-Pause Terjadwal #\(s)"))
        }
    }
    return stops
}

func formatStopDuration(_ seconds: Int64) -> String {
    if seconds >= 3600 {
        let h = seconds / 3600
        let m = (seconds % 3600) / 60
        return (m == 0) ? "\(h) Jam" : "\(h) Jam \(m) Menit"
    } else if seconds >= 60 {
        let m = seconds / 60
        let s = seconds % 60
        return (s == 0) ? "\(m) Menit" : "\(m)m \(s)d"
    } else {
        return "\(seconds) Detik"
    }
}

public struct CadenceSettingsCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let isCycling = (viewModel.settings.sport == .cycling)
        let cadenceVal = viewModel.settings.cadenceConfig.targetCadence
        let cadenceText = "\(cadenceVal) \(isCycling ? "RPM" : "SPM")"
        
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                Text(isCycling ? "TARGET BASE CADENCE (RPM)" : "TARGET STRIDE RATE (SPM)")
                    .font(.system(size: 11, weight: .bold))
                    .tracking(1.0)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Spacer()
                Text(cadenceText)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
            }
            
            if isCycling {
                Slider(
                    value: Binding(
                        get: { Double(viewModel.settings.cadenceConfig.targetCadence) },
                        set: { viewModel.settings.cadenceConfig.targetCadence = Int($0) }
                    ),
                    in: 60.0...110.0,
                    step: 1.0
                )
                .tint(StravaTheme.orange)
                
                Toggle(isOn: $viewModel.settings.cadenceConfig.allowCoastingZeroRpm) {
                    Text("Aktifkan Freewheel Coasting (0 RPM saat turunan)")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                .tint(StravaTheme.orange)
            } else {
                Slider(
                    value: Binding(
                        get: { Double(viewModel.settings.cadenceConfig.targetCadence) },
                        set: { viewModel.settings.cadenceConfig.targetCadence = Int($0) }
                    ),
                    in: 150.0...200.0,
                    step: 1.0
                )
                .tint(StravaTheme.orange)
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct CascadingDevicePickerCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let isCycling = (viewModel.settings.sport == .cycling)
        let brands = viewModel.availableBrands
        let models = viewModel.availableModelsForBrand
        let modelCountText = "\(models.count) Model Tersedia"
        
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                Image(systemName: isCycling ? "bicycle" : "applewatch")
                    .foregroundStyle(StravaTheme.orange)
                Text(isCycling ? "HEAD UNIT / CYCLOCOMPUTER" : "SPORT WATCH / RUNNING GPS")
                    .font(.system(size: 11, weight: .bold))
                    .tracking(1.0)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
            
            // 1. OPSI MEREK (BRAND) DAHULU
            VStack(alignment: .leading, spacing: 6) {
                Text("1. PILIH MEREK (BRAND)")
                    .font(.system(size: 10, weight: .bold))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(brands, id: \.self) { brand in
                            let isSelected = (brand.caseInsensitiveCompare(viewModel.selectedBrand) == .orderedSame)
                            Button {
                                viewModel.selectBrand(brand)
                            } label: {
                                Text(brand)
                                    .font(.system(size: 13, weight: isSelected ? .bold : .medium))
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 7)
                                    .background(isSelected ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                                    .foregroundStyle(isSelected ? .white : StravaTheme.textPrimary(isDark))
                                    .clipShape(Capsule())
                            }
                            .sensoryFeedback(.selection, trigger: viewModel.selectedBrand)
                        }
                    }
                }
            }
            
            Divider().background(StravaTheme.cardBorder(isDark))
            
            // 2. OPSI MODEL / TIPE DEVICE
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("2. PILIH MODEL (\(viewModel.selectedBrand))")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(modelCountText)
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                }
                
                Picker("Model", selection: $viewModel.selectedDevice) {
                    ForEach(models) { dev in
                        Text(dev.modelName).tag(dev)
                    }
                }
                .pickerStyle(.menu)
                .tint(StravaTheme.orange)
                .frame(maxWidth: .infinity, alignment: .leading)
                
                Text(viewModel.selectedDevice.description)
                    .font(.system(size: 12))
                    .lineSpacing(2)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    .padding(.top, 2)
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct ActivitySettingsView: View {
    @Bindable var viewModel: AppViewModel
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        ScrollView {
            VStack(spacing: 18) {
                SportSelectorCard(viewModel: viewModel, isDark: isDark)
                ActivityDateTimeCard(viewModel: viewModel, isDark: isDark)
                TargetSpeedAndPeakCard(viewModel: viewModel, isDark: isDark)
                AutoPauseSettingsCard(viewModel: viewModel, isDark: isDark)
                HeartRateSettingsCard(viewModel: viewModel, isDark: isDark)
                CadenceSettingsCard(viewModel: viewModel, isDark: isDark)
                CascadingDevicePickerCard(viewModel: viewModel, isDark: isDark)
                
                // Bottom Spacer for Floating Dock
                Spacer().frame(height: 75)
            }
            .padding(16)
        }
    }
}

// MARK: - Simulation Preview View (11:12 Identik dengan Strava)
public struct SimulationPreviewView: View {
    @Bindable var viewModel: AppViewModel
    @State private var selectedMetricTab: MetricChartTab = .elevation
    @State private var previewScrubKm: Double = 0.0
    @State private var rawSelectedPreviewKm: Double? = nil
    
    public enum MetricChartTab: String, CaseIterable, Identifiable {
        case elevation = "Elevasi"
        case speed = "Kecepatan"
        case power = "Power"
        case heartRate = "Heart Rate"
        case cadence = "Cadence"
        public var id: String { rawValue }
    }
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        if let res = viewModel.simulationResult {
            let isCycling = (viewModel.settings.sport == .cycling)
            let totalKm = res.totalDistanceMeters / 1000.0
            let targetM = previewScrubKm * 1000.0
            let currentPreviewPoint: SimulationPoint = res.points.min(by: { abs($0.distanceFromStartMeters - targetM) < abs($1.distanceFromStartMeters - targetM) }) ?? res.points.first ?? res.points[0]
            let currentPreviewCoord = CLLocationCoordinate2D(latitude: currentPreviewPoint.latitude, longitude: currentPreviewPoint.longitude)
            
            ScrollView {
                VStack(spacing: 16) {
                    athleteHeader(isDark: isDark)
                    activityTitleSection(isDark: isDark, isCycling: isCycling)
                    heroStatsBar(res: res, totalKm: totalKm, isDark: isDark, isCycling: isCycling)
                    mapPreviewSection(res: res, currentCoord: currentPreviewCoord, isDark: isDark)
                    telemetryChartCard(res: res, totalKm: totalKm, currentPt: currentPreviewPoint, isDark: isDark)
                    detailedStatsCard(res: res, isDark: isDark, isCycling: isCycling)
                    StravaFeedMockupCard(result: res, viewModel: viewModel, isDark: isDark)
                    Spacer().frame(height: 75)
                }
                .padding(16)
            }
        } else {
            Text("Menjalankan simulasi...")
                .foregroundStyle(StravaTheme.textPrimary(isDark))
        }
    }
    
    // MARK: - Subviews
    
    @ViewBuilder
    private func athleteHeader(isDark: Bool) -> some View {
        HStack(spacing: 12) {
            ZStack {
                Circle()
                    .fill(StravaTheme.orangeGradient)
                    .frame(width: 46, height: 46)
                Text(userInitials(viewModel.settings.userProfile.name))
                    .font(.system(size: 16, weight: .black))
                    .foregroundStyle(.white)
            }
            
            VStack(alignment: .leading, spacing: 3) {
                HStack(spacing: 6) {
                    Text(viewModel.settings.userProfile.name)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                    Image(systemName: "checkmark.seal.fill")
                        .font(.system(size: 12))
                        .foregroundStyle(.orange)
                }
                
                Text("\(viewModel.activityDateText) • \(viewModel.activityLocation)")
                    .font(.system(size: 12))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
            
            Spacer()
        }
        .padding(.horizontal, 4)
    }
    
    @ViewBuilder
    private func activityTitleSection(isDark: Bool, isCycling: Bool) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack(spacing: 6) {
                Image(systemName: isCycling ? "figure.outdoor.cycle" : "figure.run")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
                Text(isCycling ? "Ride" : "Run")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
            }
            
            Text(viewModel.activityTitle)
                .font(.system(size: 22, weight: .black))
                .foregroundStyle(StravaTheme.textPrimary(isDark))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 4)
    }
    
    @ViewBuilder
    private func heroStatsBar(res: SimulationResult, totalKm: Double, isDark: Bool, isCycling: Bool) -> some View {
        HStack(alignment: .top, spacing: 0) {
            StravaHeroStat(
                title: "Jarak",
                value: String(format: "%.2f", totalKm),
                unit: "km",
                isDark: isDark
            )
            
            Divider().frame(height: 48).background(StravaTheme.cardBorder(isDark))
            
            if isCycling {
                StravaHeroStat(
                    title: "Kec Rata-rata",
                    value: String(format: "%.1f", res.averageMovingSpeedKmh),
                    unit: "km/h",
                    isDark: isDark
                )
            } else {
                let m = Int(res.averageMovingPaceSecondsPerKm) / 60
                let s = Int(res.averageMovingPaceSecondsPerKm) % 60
                StravaHeroStat(
                    title: "Avg Pace",
                    value: String(format: "%d:%02d", m, s),
                    unit: "/km",
                    isDark: isDark
                )
            }
            
            Divider().frame(height: 48).background(StravaTheme.cardBorder(isDark))
            
            StravaHeroStat(
                title: "Waktu Bergerak",
                value: formatMovingTime(res.movingTimeSeconds),
                unit: "",
                isDark: isDark
            )
            
            Divider().frame(height: 48).background(StravaTheme.cardBorder(isDark))
            
            StravaHeroStat(
                title: "Elev Gain",
                value: String(format: "+%.0f", res.elevationGainMeters),
                unit: "m",
                isDark: isDark
            )
        }
        .padding(.vertical, 14)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
    
    @ViewBuilder
    private func mapPreviewSection(res: SimulationResult, currentCoord: CLLocationCoordinate2D, isDark: Bool) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("PETA RUTE & REKAMAN AKTIVITAS")
                    .font(.system(size: 11, weight: .bold))
                    .tracking(1.0)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Spacer()
                Text(String(format: "KM %.1f", previewScrubKm))
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
            }
            .padding(.horizontal, 14)
            .padding(.top, 12)
            
            RouteMapView(
                points: res.points,
                selectedCoordinate: currentCoord,
                isDark: isDark
            )
            .frame(height: 220)
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .padding(.horizontal, 8)
            .padding(.bottom, 10)
        }
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
    
    @ViewBuilder
    private func telemetryChartCard(res: SimulationResult, totalKm: Double, currentPt: SimulationPoint, isDark: Bool) -> some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("ANALISIS GRAFIK TELEMETRI FISIKA")
                        .font(.system(size: 11, weight: .bold))
                        .tracking(1.0)
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text("Geser grafik untuk inspeksi elevasi, speed, HR, power, & cadence")
                        .font(.system(size: 11))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.top, 14)
            
            // Tab Selector
            let isCycling = (viewModel.settings.sport == .cycling)
            let visibleTabs: [MetricChartTab] = isCycling ? MetricChartTab.allCases : [.elevation, .speed, .heartRate, .cadence]
            
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 6) {
                    ForEach(visibleTabs) { tab in
                        Button {
                            selectedMetricTab = tab
                        } label: {
                            Text(tab.rawValue)
                                .font(.system(size: 12, weight: selectedMetricTab == tab ? .bold : .medium))
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(selectedMetricTab == tab ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                                .foregroundStyle(selectedMetricTab == tab ? .white : StravaTheme.textPrimary(isDark))
                                .clipShape(Capsule())
                        }
                    }
                }
                .padding(.horizontal, 16)
            }
            
            // Live Telemetry Inspector Pill
            liveTelemetryHud(currentPt: currentPt, totalKm: totalKm, isDark: isDark, isCycling: isCycling)
            
            // Interactive Chart
            let chartData = downsampleSimulationPoints(res.points, maxPoints: 350)
            metricChartView(chartData: chartData, totalKm: totalKm, isDark: isDark)
            
            // Scrubber Slider Control
            scrubberControls(res: res, totalKm: totalKm, isDark: isDark)
        }
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
    
    @ViewBuilder
    private func liveTelemetryHud(currentPt: SimulationPoint, totalKm: Double, isDark: Bool, isCycling: Bool) -> some View {
        VStack(spacing: 8) {
            HStack {
                HStack(spacing: 6) {
                    Circle()
                        .fill(gradientColor(currentPt.gradient))
                        .frame(width: 9, height: 9)
                    Text(gradientDescription(currentPt.gradient))
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(gradientColor(currentPt.gradient))
                }
                Spacer()
                Text(String(format: "KM %.2f / %.2f", previewScrubKm, totalKm))
                    .font(.system(size: 12, weight: .black))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
            }
            
            Divider().background(StravaTheme.cardBorder(isDark))
            
            HStack(spacing: 0) {
                liveMetricCell(title: "Elevasi", value: String(format: "%.0f m", currentPt.elevation), isDark: isDark)
                liveMetricCell(title: "Gradien", value: String(format: "%+.1f%%", currentPt.gradient), isDark: isDark)
                if isCycling {
                    liveMetricCell(title: "Kecepatan", value: String(format: "%.1f km/h", currentPt.speedKmh), isDark: isDark, highlight: selectedMetricTab == .speed)
                } else {
                    let pMin = Int(currentPt.paceSecondsPerKm) / 60
                    let pSec = Int(currentPt.paceSecondsPerKm) % 60
                    liveMetricCell(title: "Pace", value: String(format: "%d:%02d/km", pMin, pSec), isDark: isDark, highlight: selectedMetricTab == .speed)
                }
                liveMetricCell(title: "Heart Rate", value: "\(currentPt.heartRate ?? 0) bpm", isDark: isDark, highlight: selectedMetricTab == .heartRate)
            }
            
            HStack(spacing: 0) {
                if isCycling {
                    liveMetricCell(title: "Cadence", value: "\(currentPt.cadence ?? 0) RPM", isDark: isDark, highlight: selectedMetricTab == .cadence)
                    liveMetricCell(title: "Power", value: "\(currentPt.powerWatts ?? 0) W", isDark: isDark, highlight: selectedMetricTab == .power)
                    liveMetricCell(title: "Status", value: currentPt.isStopped ? "Berhenti" : (currentPt.cadence == 0 ? "Coasting" : "Kayuh"), isDark: isDark)
                } else {
                    let cadSpM = currentPt.cadence ?? 0
                    liveMetricCell(title: "Cadence", value: "\(cadSpM) SPM", isDark: isDark, highlight: selectedMetricTab == .cadence)
                    liveMetricCell(title: "Status", value: currentPt.isStopped ? "Berhenti" : "Lari", isDark: isDark)
                    liveMetricCell(title: "Suhu", value: "\(currentPt.temperatureCelsius ?? 28)°C", isDark: isDark)
                }
                liveMetricCell(title: "Alasan", value: currentPt.explainReason.replacingOccurrences(of: "AUTO_PAUSE_", with: "Stop: "), isDark: isDark)
            }
        }
        .padding(12)
        .background(StravaTheme.subtlePill(isDark))
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        .padding(.horizontal, 14)
    }
    
    @ViewBuilder
    private func metricChartView(chartData: [ChartDataPoint], totalKm: Double, isDark: Bool) -> some View {
        Chart {
            ForEach(chartData) { pt in
                switch selectedMetricTab {
                case .elevation:
                    AreaMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Elevasi (m)", pt.elevation)
                    )
                    .foregroundStyle(LinearGradient(colors: [StravaTheme.orange.opacity(0.4), StravaTheme.orange.opacity(0.04)], startPoint: .top, endPoint: .bottom))
                    LineMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Elevasi (m)", pt.elevation)
                    )
                    .foregroundStyle(StravaTheme.orange)
                    .lineStyle(StrokeStyle(lineWidth: 2.5))
                    
                case .speed:
                    AreaMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Kecepatan (km/h)", pt.speedKmh)
                    )
                    .foregroundStyle(LinearGradient(colors: [Color.blue.opacity(0.3), Color.blue.opacity(0.02)], startPoint: .top, endPoint: .bottom))
                    LineMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Kecepatan (km/h)", pt.speedKmh)
                    )
                    .foregroundStyle(.blue)
                    .lineStyle(StrokeStyle(lineWidth: 2.5))
                    
                case .power:
                    AreaMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Power (W)", pt.powerWatts)
                    )
                    .foregroundStyle(LinearGradient(colors: [Color.purple.opacity(0.3), Color.purple.opacity(0.02)], startPoint: .top, endPoint: .bottom))
                    LineMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Power (W)", pt.powerWatts)
                    )
                    .foregroundStyle(.purple)
                    .lineStyle(StrokeStyle(lineWidth: 2.5))
                    
                case .heartRate:
                    AreaMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Heart Rate (bpm)", pt.heartRate)
                    )
                    .foregroundStyle(LinearGradient(colors: [Color.red.opacity(0.3), Color.red.opacity(0.02)], startPoint: .top, endPoint: .bottom))
                    LineMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Heart Rate (bpm)", pt.heartRate)
                    )
                    .foregroundStyle(.red)
                    .lineStyle(StrokeStyle(lineWidth: 2.5))
                    
                case .cadence:
                    AreaMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Cadence (RPM)", pt.cadence)
                    )
                    .foregroundStyle(LinearGradient(colors: [Color.green.opacity(0.3), Color.green.opacity(0.02)], startPoint: .top, endPoint: .bottom))
                    LineMark(
                        x: .value("Jarak (km)", pt.distanceKm),
                        y: .value("Cadence (RPM)", pt.cadence)
                    )
                    .foregroundStyle(.green)
                    .lineStyle(StrokeStyle(lineWidth: 2.5))
                }
            }
            
            RuleMark(x: .value("Jarak", previewScrubKm))
                .foregroundStyle(StravaTheme.orange.opacity(0.85))
                .lineStyle(StrokeStyle(lineWidth: 1.5, dash: [4, 4]))
        }
        .chartXScale(domain: 0...totalKm)
        .chartXSelection(value: $rawSelectedPreviewKm)
        .onChange(of: rawSelectedPreviewKm) { _, newVal in
            if let newVal {
                previewScrubKm = min(max(0, newVal), totalKm)
            }
        }
        .chartPlotStyle { plot in
            plot.clipped()
        }
        .frame(height: 190)
        .chartXAxis {
            AxisMarks(position: .bottom) {
                AxisGridLine().foregroundStyle(StravaTheme.cardBorder(isDark))
                AxisValueLabel().foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading) {
                AxisGridLine().foregroundStyle(StravaTheme.cardBorder(isDark))
                AxisValueLabel().foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .padding(.horizontal, 8)
    }
    
    @ViewBuilder
    private func scrubberControls(res: SimulationResult, totalKm: Double, isDark: Bool) -> some View {
        VStack(spacing: 6) {
            HStack {
                Text("Geser Inspeksi Telemetri:")
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Spacer()
                Text(String(format: "%.1f km / %.1f km", previewScrubKm, totalKm))
                    .font(.system(size: 12, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
            }
            Slider(value: $previewScrubKm, in: 0...totalKm, step: 0.1)
                .tint(StravaTheme.orange)
            
            HStack(spacing: 6) {
                quickJumpButton(title: "Start", targetKm: 0.0, current: $previewScrubKm, isDark: isDark)
                if let peakPt = res.points.max(by: { $0.elevation < $1.elevation }) {
                    quickJumpButton(title: "Puncak (\(String(format: "%.0fm", peakPt.elevation)))", targetKm: peakPt.distanceFromStartMeters / 1000.0, current: $previewScrubKm, isDark: isDark)
                }
                quickJumpButton(title: "Tengah (50%)", targetKm: totalKm * 0.5, current: $previewScrubKm, isDark: isDark)
                quickJumpButton(title: "Finish", targetKm: totalKm, current: $previewScrubKm, isDark: isDark)
            }
        }
        .padding(.horizontal, 14)
        .padding(.bottom, 14)
    }
    
    @ViewBuilder
    private func detailedStatsCard(res: SimulationResult, isDark: Bool, isCycling: Bool) -> some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("STATISTIK RINCI")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            VStack(spacing: 12) {
                StatRow(
                    title: isCycling ? "Kecepatan Rata-rata" : "Avg Pace",
                    value: isCycling ? String(format: "%.1f km/h", res.averageMovingSpeedKmh) : String(format: "%d:%02d /km", Int(res.averageMovingPaceSecondsPerKm)/60, Int(res.averageMovingPaceSecondsPerKm)%60),
                    isDark: isDark
                )
                StatRow(
                    title: "Kecepatan Puncak (Peak Speed)",
                    value: isCycling ? String(format: "%.1f km/h", res.maxSpeedKmh) : String(format: "%.1f km/h (Pace %d:%02d /km)", res.maxSpeedKmh, Int(res.bestPaceSecondsPerKm)/60, Int(res.bestPaceSecondsPerKm)%60),
                    isDark: isDark
                )
                if !isCycling {
                    StatRow(
                        title: "Pace Terbaik (Best Pace)",
                        value: String(format: "%d:%02d /km", Int(res.bestPaceSecondsPerKm)/60, Int(res.bestPaceSecondsPerKm)%60),
                        isDark: isDark
                    )
                }
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                StatRow(
                    title: "Waktu Bergerak (Moving Time)",
                    value: formatMovingTime(res.movingTimeSeconds),
                    isDark: isDark
                )
                StatRow(
                    title: "Total Waktu Keseluruhan (Elapsed Time)",
                    value: formatMovingTime(res.totalTimeSeconds),
                    isDark: isDark
                )
                if res.pauseTimeSeconds > 0 {
                    StatRow(
                        title: "Waktu Istirahat (Auto-Pause)",
                        value: "\(formatMovingTime(res.pauseTimeSeconds)) (\(res.pauseCount) Perhentian)",
                        isDark: isDark
                    )
                }
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                StatRow(
                    title: "Detak Jantung Rata-rata",
                    value: "\(res.averageHeartRate ?? 0) bpm",
                    isDark: isDark
                )
                StatRow(
                    title: "Detak Jantung Maksimal (Tanjakan)",
                    value: "\(res.maxHeartRate ?? 0) bpm (Max HR: \(viewModel.settings.userProfile.estimatedMaxHr) bpm)",
                    isDark: isDark
                )
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                if isCycling {
                    StatRow(
                        title: "Daya Rata-rata (Avg Power)",
                        value: "\(res.averagePower ?? 0) W",
                        isDark: isDark
                    )
                    StatRow(
                        title: "Daya Maksimal (Max Power)",
                        value: "\(res.maxPower ?? 0) W",
                        isDark: isDark
                    )
                    StatRow(
                        title: "Normalized Power® (NP)",
                        value: "\(res.normalizedPower ?? 0) W",
                        isDark: isDark
                    )
                    StatRow(
                        title: "Total Kerja Mekanis (Work)",
                        value: "\(res.totalWorkJoules / 1000) kJ",
                        isDark: isDark
                    )
                }
                
                StatRow(
                    title: "Total Kalori Terbakar",
                    value: "\(res.totalCalories ?? 0) kcal (Massa: \(Int(viewModel.settings.userProfile.totalMassKg)) kg)",
                    isDark: isDark
                )
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                StatRow(
                    title: isCycling ? "Irama Kaki Rata-rata (Avg Cadence)" : "Rata-rata Langkah (Avg Cadence)",
                    value: "\(res.averageCadence ?? 0) \(isCycling ? "RPM" : "SPM")",
                    isDark: isDark
                )
                StatRow(
                    title: isCycling ? "Irama Kaki Maksimal (Max Cadence)" : "Langkah Maksimal (Max Cadence)",
                    value: "\(res.maxCadence ?? 0) \(isCycling ? "RPM" : "SPM")",
                    isDark: isDark
                )
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                StatRow(
                    title: "Elevation Gain / Loss",
                    value: String(format: "+%.0f m / -%.0f m", res.elevationGainMeters, res.elevationLossMeters),
                    isDark: isDark
                )
                StatRow(
                    title: "Suhu Udara Rata-rata",
                    value: "\(res.averageTemperature ?? 29) °C",
                    isDark: isDark
                )
                
                Divider().background(StravaTheme.cardBorder(isDark))
                
                HStack {
                    Image(systemName: isCycling ? "bicycle" : "applewatch")
                        .foregroundStyle(StravaTheme.orange)
                    Text("Perangkat Rekam")
                        .font(.system(size: 13, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(viewModel.selectedDevice.formattedDeviceName)
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
    
    private func userInitials(_ name: String) -> String {
        let parts = name.split(separator: " ")
        if parts.count >= 2 {
            return "\(parts[0].prefix(1))\(parts[1].prefix(1))".uppercased()
        }
        return String(name.prefix(2)).uppercased()
    }
    
    private func formatMovingTime(_ sec: Int64) -> String {
        let h = sec / 3600
        let m = (sec % 3600) / 60
        let s = sec % 60
        if h > 0 {
            return String(format: "%d:%02d:%02d", h, m, s)
        }
        return String(format: "%d:%02d", m, s)
    }
}

// MARK: - Export View
public struct ExportView: View {
    @Bindable var viewModel: AppViewModel
    @Environment(\.openURL) private var openURL
    @State private var showShareSheet = false
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        ScrollView {
            VStack(spacing: 20) {
                // Header badge
                VStack(spacing: 12) {
                    Image(systemName: "checkmark.seal.fill")
                        .font(.system(size: 52))
                        .foregroundStyle(StravaTheme.orange)
                    
                    Text("Berkas .FIT Siap!")
                        .font(.system(size: 22, weight: .black))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                    
                    if let fit = viewModel.generatedFitData {
                        Text(String(format: "Ukuran berkas: %d bytes (CRC-16 OK)", fit.count))
                            .font(.system(size: 13, weight: .medium))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        
                        Text("Diformat untuk \(viewModel.selectedDevice.formattedDeviceName) • Kompatibel dengan Strava & semua device")
                            .font(.system(size: 11))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                    }
                }
                
                // Strava Feed Card Mockup Preview
                if let res = viewModel.simulationResult {
                    StravaFeedMockupCard(result: res, viewModel: viewModel, isDark: isDark)
                }
                
                // Card: NAMA BERKAS .FIT (Bisa diedit & bebas dari '_simulated')
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text("NAMA BERKAS (.FIT)")
                            .font(.system(size: 11, weight: .bold))
                            .tracking(1.0)
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Spacer()
                        Text(viewModel.resolvedFitFileName)
                            .font(.system(size: 12, weight: .bold))
                            .foregroundStyle(StravaTheme.orange)
                            .lineLimit(1)
                    }
                    
                    HStack {
                        Image(systemName: "pencil.line")
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        TextField("Nama berkas .fit...", text: $viewModel.customExportFileName)
                            .textFieldStyle(.plain)
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                    }
                    .padding(12)
                    .background(StravaTheme.subtlePill(isDark))
                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                    
                    // Quick Preset Chips
                    HStack(spacing: 8) {
                        let routeName = viewModel.trackFileName.replacingOccurrences(of: ".gpx", with: "", options: .caseInsensitive).replacingOccurrences(of: "_simulated", with: "", options: .caseInsensitive).trimmingCharacters(in: .whitespaces)
                        if !routeName.isEmpty {
                            Button {
                                viewModel.customExportFileName = "\(routeName).fit"
                            } label: {
                                Text("Nama Rute")
                                    .font(.system(size: 11, weight: .semibold))
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(StravaTheme.subtlePill(isDark))
                                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                                    .clipShape(Capsule())
                            }
                        }
                        
                        Button {
                            let df = DateFormatter()
                            df.dateFormat = "yyyy-MM-dd-HH-mm-ss"
                            viewModel.customExportFileName = "\(df.string(from: viewModel.activityDate)).fit"
                        } label: {
                            Text("Garmin GPS")
                                .font(.system(size: 11, weight: .semibold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(StravaTheme.subtlePill(isDark))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                                .clipShape(Capsule())
                        }
                        
                        Button {
                            let cleanAct = viewModel.activityTitle.replacingOccurrences(of: " ", with: "_")
                            viewModel.customExportFileName = "\(cleanAct).fit"
                        } label: {
                            Text("Aktivitas")
                                .font(.system(size: 11, weight: .semibold))
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(StravaTheme.subtlePill(isDark))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                                .clipShape(Capsule())
                        }
                    }
                }
                .padding(16)
                .background(StravaTheme.cardBackground(isDark))
                .overlay(
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                
                // Export Card
                VStack(alignment: .leading, spacing: 14) {
                    Text("PILIHAN EKSPOR")
                        .font(.system(size: 11, weight: .bold))
                        .tracking(1.0)
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    
                    // 1. SAVE FILE .FIT
                    Button {
                        if let _ = viewModel.prepareExportUrl() {
                            showShareSheet = true
                        }
                    } label: {
                        HStack(spacing: 10) {
                            Image(systemName: "square.and.arrow.down.fill")
                                .font(.system(size: 16, weight: .bold))
                            Text("1. Simpan Berkas .FIT")
                                .font(.system(size: 15, weight: .bold))
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(StravaTheme.orange)
                        .foregroundStyle(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    }
                    .sensoryFeedback(.success, trigger: showShareSheet)
                    
                    // 2. SAVE & UPLOAD TO STRAVA (AUTO OPEN STRAVA UPLOAD WEBSITE)
                    Button {
                        _ = viewModel.prepareExportUrl()
                        if let uploadUrl = URL(string: "https://www.strava.com/upload/select") {
                            openURL(uploadUrl)
                        }
                    } label: {
                        HStack(spacing: 10) {
                            Image(systemName: "arrow.up.circle.fill")
                                .font(.system(size: 16, weight: .bold))
                            Text("2. Simpan & Upload ke Strava")
                                .font(.system(size: 15, weight: .bold))
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(isDark ? Color.white.opacity(0.12) : Color.black.opacity(0.08))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12, style: .continuous)
                                .stroke(StravaTheme.orange.opacity(0.8), lineWidth: 1.5)
                        )
                        .foregroundStyle(StravaTheme.orange)
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    }
                    
                    // 3. RIWAYAT BERKAS .FIT
                    Button {
                        viewModel.showHistorySheet = true
                    } label: {
                        HStack(spacing: 8) {
                            Image(systemName: "clock.arrow.circlepath")
                                .font(.system(size: 14, weight: .bold))
                            Text("Lihat Riwayat Berkas FIT (\(viewModel.archivedActivities.count))")
                                .font(.system(size: 14, weight: .bold))
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 46)
                        .background(StravaTheme.subtlePill(isDark))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    }
                    .sensoryFeedback(.selection, trigger: viewModel.showHistorySheet)
                    
                    Text("Tombol ke-2 otomatis membuka browser ke strava.com/upload/select untuk unggah berkas .FIT.")
                        .font(.system(size: 11))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                        .lineSpacing(2)
                        .padding(.horizontal, 4)
                }
                .padding(16)
                .background(StravaTheme.cardBackground(isDark))
                .overlay(
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                
                // Return to Home
                Button {
                    viewModel.currentScreen = .home
                } label: {
                    Text("Kembali ke Beranda")
                        .font(.system(size: 14, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                .padding(.top, 10)
            }
            .padding(16)
        }
        .sheet(isPresented: $showShareSheet) {
            if let fileUrl = viewModel.exportedFitUrl {
                ShareSheet(activityItems: [fileUrl])
            }
        }
    }
}

// MARK: - TUTORIAL & PANDUAN PENGGUNAAN (Sheet Modal)
public struct TutorialSheet: View {
    var viewModel: AppViewModel
    let isDark: Bool
    @Environment(\.dismiss) private var dismiss
    
    public var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    // Header Banner
                    VStack(spacing: 8) {
                        Image(systemName: "figure.outdoor.cycle")
                            .font(.system(size: 40, weight: .bold))
                            .foregroundStyle(StravaTheme.orange)
                            .padding(.top, 10)
                        
                        Text("Cara Menggunakan Strava Kalcer")
                            .font(.system(size: 20, weight: .black))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                        
                        Text("Simulasi rute realistis bertenaga hukum fisika mekanika dan fisiologi kardiak.")
                            .font(.system(size: 13))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 20)
                    }
                    
                    // Step-by-Step Cards
                    VStack(spacing: 12) {
                        TutorialStepCard(
                            stepNumber: "1",
                            icon: "map.fill",
                            title: "Pilih atau Impor Rute GPX",
                            desc: "Tekan 'Impor Berkas GPX' untuk memuat rute dari Files/iCloud, atau gunakan 'Rute Berbukit (8.9 km)' bawaan. Anda dapat membalik arah rute (Reverse) dengan 1 sentuhan.",
                            isDark: isDark
                        )
                        
                        TutorialStepCard(
                            stepNumber: "2",
                            icon: "person.text.rectangle.fill",
                            title: "Atur Profil Atlet & Biometrik",
                            desc: "Buka menu Profil (ikon avatar di pojok kanan atas) untuk mengisi Nama, Tanggal Lahir, Tinggi, dan Berat Badan. Bobot tubuh Anda langsung menentukan gaya berat tanjakan (Watts) & pembakaran Kalori riil.",
                            isDark: isDark
                        )
                        
                        TutorialStepCard(
                            stepNumber: "3",
                            icon: "speedometer",
                            title: "Tentukan Target & Perangkat",
                            desc: "Atur target kecepatan rata-rata (misal 32 km/jam). Pilih cyclocomputer resmi (Garmin Edge 1050/1040, Wahoo, Karoo) atau jam lari (Forerunner, Apple Watch).",
                            isDark: isDark
                        )
                        
                        TutorialStepCard(
                            stepNumber: "4",
                            icon: "waveform.path.ecg",
                            title: "Simulasi Fisika Riil & Ekspor",
                            desc: "Saat tanjakan, speed melambat, watt melonjak, cadence drop, dan heart rate naik. Di turunan, speed melejit dan cadence otomatis 0 RPM coasting. Ekspor file .FIT resmi atau bagikan langsung ke Strava!",
                            isDark: isDark
                        )
                    }
                    .padding(.horizontal, 16)
                    
                    // Close button
                    Button {
                        viewModel.markTutorialSeen()
                        dismiss()
                    } label: {
                        Text("Saya Mengerti, Mulai!")
                            .font(.system(size: 16, weight: .bold))
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(StravaTheme.orange)
                            .foregroundStyle(.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 10)
                    .padding(.bottom, 24)
                }
            }
            .background(StravaTheme.background(isDark).ignoresSafeArea())
            .navigationTitle("Panduan Penggunaan")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Tutup") {
                        viewModel.markTutorialSeen()
                        dismiss()
                    }
                    .foregroundStyle(StravaTheme.orange)
                }
            }
        }
        .presentationDetents([.large])
    }
}

public struct TutorialStepCard: View {
    let stepNumber: String
    let icon: String
    let title: String
    let desc: String
    let isDark: Bool
    
    public var body: some View {
        HStack(alignment: .top, spacing: 14) {
            ZStack {
                Circle()
                    .fill(StravaTheme.orange)
                    .frame(width: 28, height: 28)
                Text(stepNumber)
                    .font(.system(size: 14, weight: .black))
                    .foregroundStyle(.white)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 6) {
                    Image(systemName: icon)
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundStyle(StravaTheme.orange)
                    Text(title)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                
                Text(desc)
                    .font(.system(size: 12))
                    .lineSpacing(2)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }
}

// MARK: - PROFIL PENGGUNA & MENU PENGATURAN (Settings Sheet)
public struct AthleteBiometricsCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        let ageText = "Usia: \(viewModel.settings.userProfile.age) Tahun"
        let heightVal = Int(viewModel.settings.userProfile.heightCm)
        let heightText = "\(heightVal) cm"
        let weightText = String(format: "%.1f kg", viewModel.settings.userProfile.weightKg)
        let bikeWeightText = String(format: "%.1f kg", viewModel.settings.userProfile.bikeWeightKg)
        
        VStack(alignment: .leading, spacing: 14) {
            Text("PROFIL ATLET & BIOMETRIK")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            // Nama Atlet
            VStack(alignment: .leading, spacing: 6) {
                Text("Nama Lengkap")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                TextField("Nama Atlet", text: $viewModel.settings.userProfile.name)
                    .font(.system(size: 15, weight: .semibold))
                    .padding(12)
                    .background(StravaTheme.subtlePill(isDark))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }
            
            // Tanggal Lahir (Birth Date)
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Tanggal Lahir")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(ageText)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                }
                DatePicker(
                    "Tanggal Lahir",
                    selection: $viewModel.settings.userProfile.birthDate,
                    in: ...Date(),
                    displayedComponents: [.date]
                )
                .datePickerStyle(.compact)
                .tint(StravaTheme.orange)
            }
            
            // Tinggi Badan
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Tinggi Badan (Height)")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(heightText)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                Slider(value: $viewModel.settings.userProfile.heightCm, in: 140.0...215.0, step: 1.0)
                    .tint(StravaTheme.orange)
            }
            
            // Berat Badan (Rider Weight)
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Berat Badan Atlet (Body Weight)")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(weightText)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                }
                Slider(value: $viewModel.settings.userProfile.weightKg, in: 40.0...130.0, step: 0.5)
                    .tint(StravaTheme.orange)
            }
            
            // Berat Sepeda / Gear
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Berat Sepeda & Apparel (Bike & Kit)")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Spacer()
                    Text(bikeWeightText)
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                Slider(value: $viewModel.settings.userProfile.bikeWeightKg, in: 6.0...20.0, step: 0.5)
                    .tint(StravaTheme.orange)
            }
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct PhysicsSummaryCard: View {
    let profile: UserProfile
    let isDark: Bool
    
    public var body: some View {
        let totalMassText = String(format: "%.1f kg", profile.totalMassKg)
        let maxHrText = "\(profile.estimatedMaxHr) bpm"
        let cdaText = String(format: "%.3f m²", profile.estimatedCdA)
        
        VStack(alignment: .leading, spacing: 12) {
            Text("EFEKTIVITAS FISIKA & KALORI")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("TOTAL MASSA")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text(totalMassText)
                        .font(.system(size: 16, weight: .black))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                Spacer()
                VStack(alignment: .leading, spacing: 2) {
                    Text("MAX HEART RATE")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text(maxHrText)
                        .font(.system(size: 16, weight: .black))
                        .foregroundStyle(StravaTheme.orange)
                }
                Spacer()
                VStack(alignment: .leading, spacing: 2) {
                    Text("AERO DRAG (CdA)")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text(cdaText)
                        .font(.system(size: 16, weight: .black))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
            }
            
            Text("Berat badan langsung menentukan beban gravitasi saat tanjakan (P_gravity = m • g • v • Grade%). Semakin berat badan pesepeda, watt yang dibutuhkan semakin tinggi dan pembakaran kalori akan meningkat secara proporsional.")
                .font(.system(size: 11))
                .lineSpacing(2)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
                .padding(.top, 4)
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct ThemeSettingsCard: View {
    @Bindable var viewModel: AppViewModel
    let isDark: Bool
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("TEMA & TAMPILAN APLIKASI")
                .font(.system(size: 11, weight: .bold))
                .tracking(1.0)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            Picker("Tema", selection: $viewModel.appTheme) {
                Text("Dark Mode").tag(AppTheme.dark)
                Text("Light Mode").tag(AppTheme.light)
            }
            .pickerStyle(.segmented)
            .sensoryFeedback(.selection, trigger: viewModel.appTheme)
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

public struct ProfileSettingsSheet: View {
    @Bindable var viewModel: AppViewModel
    @Environment(\.dismiss) private var dismiss
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    AthleteBiometricsCard(viewModel: viewModel, isDark: isDark)
                    PhysicsSummaryCard(profile: viewModel.settings.userProfile, isDark: isDark)
                    ThemeSettingsCard(viewModel: viewModel, isDark: isDark)
                    
                    Button {
                        viewModel.updateAthleteName(viewModel.settings.userProfile.name)
                        dismiss()
                    } label: {
                        Text("Simpan Profil & Tutup")
                            .font(.system(size: 16, weight: .bold))
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(StravaTheme.orange)
                            .foregroundStyle(.white)
                            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
                    }
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
                .padding(.top, 10)
            }
            .background(StravaTheme.background(isDark).ignoresSafeArea())
            .navigationTitle("Profil & Pengaturan")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Selesai") {
                        viewModel.updateAthleteName(viewModel.settings.userProfile.name)
                        dismiss()
                    }
                    .foregroundStyle(StravaTheme.orange)
                }
            }
        }
        .presentationDetents([.large])
    }
}

// MARK: - Reusable UI Subviews & Helpers

public struct StravaHeroStat: View {
    let title: String
    let value: String
    let unit: String
    let isDark: Bool
    
    public var body: some View {
        VStack(spacing: 2) {
            Text(title.uppercased())
                .font(.system(size: 10, weight: .bold))
                .tracking(0.5)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            HStack(alignment: .firstTextBaseline, spacing: 2) {
                Text(value)
                    .font(.system(size: 18, weight: .black, design: .rounded))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                
                if !unit.isEmpty {
                    Text(unit)
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
            }
        }
        .frame(maxWidth: .infinity)
    }
}

public struct StravaStatCard: View {
    let title: String
    let value: String
    let unit: String
    let isDark: Bool
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.system(size: 10, weight: .bold))
                .tracking(0.6)
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            HStack(alignment: .firstTextBaseline, spacing: 2) {
                Text(value)
                    .font(.system(size: 18, weight: .black, design: .rounded))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                Text(unit)
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }
}

public struct StatRow: View {
    let title: String
    let value: String
    let isDark: Bool
    
    public var body: some View {
        HStack {
            Text(title)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            Spacer()
            Text(value)
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(StravaTheme.textPrimary(isDark))
        }
    }
}

public struct SportButton: View {
    let title: String
    let icon: String
    let isSelected: Bool
    let isDark: Bool
    let action: () -> Void
    
    public var body: some View {
        Button(action: action) {
            VStack(spacing: 6) {
                Image(systemName: icon)
                    .font(.system(size: 20, weight: isSelected ? .bold : .medium))
                Text(title)
                    .font(.system(size: 12, weight: isSelected ? .bold : .medium))
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(isSelected ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
            .foregroundStyle(isSelected ? .white : StravaTheme.textPrimary(isDark))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
        }
        .sensoryFeedback(.selection, trigger: isSelected)
    }
}

// MARK: - MapKit Polyline View
// MARK: - MapKit Polyline View
public struct RouteMapView: View {
    public let coordinates: [CLLocationCoordinate2D]
    public var selectedCoordinate: CLLocationCoordinate2D?
    public var isDark: Bool
    
    public init(
        coordinates: [CLLocationCoordinate2D],
        selectedCoordinate: CLLocationCoordinate2D? = nil,
        isDark: Bool = true
    ) {
        self.coordinates = coordinates
        self.selectedCoordinate = selectedCoordinate
        self.isDark = isDark
    }
    
    public init(
        points: [SimulationPoint],
        selectedCoordinate: CLLocationCoordinate2D? = nil,
        isDark: Bool = true
    ) {
        self.coordinates = points.map { CLLocationCoordinate2D(latitude: $0.latitude, longitude: $0.longitude) }
        self.selectedCoordinate = selectedCoordinate
        self.isDark = isDark
    }
    
    public init(
        routePoints: [RoutePoint],
        selectedCoordinate: CLLocationCoordinate2D? = nil,
        isDark: Bool = true
    ) {
        self.coordinates = routePoints.map { CLLocationCoordinate2D(latitude: $0.latitude, longitude: $0.longitude) }
        self.selectedCoordinate = selectedCoordinate
        self.isDark = isDark
    }
    
    public var body: some View {
        if coordinates.isEmpty {
            Color.gray.opacity(0.1)
        } else {
            let region = computeRegion(coords: coordinates)
            
            Map(initialPosition: .region(region)) {
                MapPolyline(coordinates: coordinates)
                    .stroke(StravaTheme.orange, lineWidth: 4.5)
                
                if let first = coordinates.first {
                    Annotation("Start", coordinate: first) {
                        ZStack {
                            Circle().fill(Color.green).frame(width: 14, height: 14)
                            Circle().stroke(Color.white, lineWidth: 2)
                        }
                    }
                }
                
                if let last = coordinates.last {
                    Annotation("Finish", coordinate: last) {
                        ZStack {
                            Circle().fill(Color.black).frame(width: 14, height: 14)
                            Circle().stroke(Color.white, lineWidth: 2)
                        }
                    }
                }
                
                if let sel = selectedCoordinate {
                    Annotation("Posisi", coordinate: sel) {
                        ZStack {
                            Circle()
                                .fill(StravaTheme.orange.opacity(0.35))
                                .frame(width: 34, height: 34)
                            Circle()
                                .fill(StravaTheme.orange)
                                .frame(width: 16, height: 16)
                                .overlay(Circle().stroke(Color.white, lineWidth: 2.5))
                        }
                    }
                }
            }
            .mapStyle(.standard(elevation: .realistic))
        }
    }
    
    private func computeRegion(coords: [CLLocationCoordinate2D]) -> MKCoordinateRegion {
        guard !coords.isEmpty else {
            return MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: -6.2088, longitude: 106.8456), span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05))
        }
        let lats = coords.map { $0.latitude }
        let lons = coords.map { $0.longitude }
        let minLat = lats.min()!
        let maxLat = lats.max()!
        let minLon = lons.min()!
        let maxLon = lons.max()!
        
        let center = CLLocationCoordinate2D(latitude: (minLat + maxLat) / 2.0, longitude: (minLon + maxLon) / 2.0)
        let span = MKCoordinateSpan(latitudeDelta: max(0.008, (maxLat - minLat) * 1.35), longitudeDelta: max(0.008, (maxLon - minLon) * 1.35))
        return MKCoordinateRegion(center: center, span: span)
    }
}

// MARK: - Share Sheet Bridge
public struct ShareSheet: UIViewControllerRepresentable {
    public let activityItems: [Any]
    public let applicationActivities: [UIActivity]? = nil

    public func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: applicationActivities)
    }

    public func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

// MARK: - Downsampling Helpers for Apple Charts Performance
public struct ChartDataPoint: Identifiable {
    public let id: Int
    public let distanceKm: Double
    public let elevation: Double
    public let speedKmh: Double
    public let powerWatts: Double
    public let heartRate: Double
    public let cadence: Double
    public let gradient: Double
    public let latitude: Double
    public let longitude: Double
}

func downsampleRoutePoints(_ pts: [RoutePoint], maxPoints: Int) -> [RoutePoint] {
    guard pts.count > maxPoints else { return pts }
    let step = max(1, pts.count / maxPoints)
    var result: [RoutePoint] = []
    for i in stride(from: 0, to: pts.count, by: step) {
        result.append(pts[i])
    }
    if let last = pts.last, result.last?.index != last.index {
        result.append(last)
    }
    return result
}

func downsampleSimulationPoints(_ pts: [SimulationPoint], maxPoints: Int) -> [ChartDataPoint] {
    guard pts.count > maxPoints else {
        return pts.map {
            ChartDataPoint(
                id: $0.index,
                distanceKm: $0.distanceFromStartMeters / 1000.0,
                elevation: $0.elevation,
                speedKmh: $0.speedKmh,
                powerWatts: Double($0.powerWatts ?? 0),
                heartRate: Double($0.heartRate ?? 0),
                cadence: Double($0.cadence ?? 0),
                gradient: $0.gradient,
                latitude: $0.latitude,
                longitude: $0.longitude
            )
        }
    }
    let step = max(1, pts.count / maxPoints)
    var result: [ChartDataPoint] = []
    for i in stride(from: 0, to: pts.count, by: step) {
        let p = pts[i]
        result.append(ChartDataPoint(
            id: p.index,
            distanceKm: p.distanceFromStartMeters / 1000.0,
            elevation: p.elevation,
            speedKmh: p.speedKmh,
            powerWatts: Double(p.powerWatts ?? 0),
            heartRate: Double(p.heartRate ?? 0),
            cadence: Double(p.cadence ?? 0),
            gradient: p.gradient,
            latitude: p.latitude,
            longitude: p.longitude
        ))
    }
    return result
}
