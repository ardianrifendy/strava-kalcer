import SwiftUI
import MapKit

// MARK: - Strava Feed Mockup Card Preview
public struct StravaFeedMockupCard: View {
    public let result: SimulationResult
    public let viewModel: AppViewModel
    public let isDark: Bool
    
    public init(result: SimulationResult, viewModel: AppViewModel, isDark: Bool) {
        self.result = result
        self.viewModel = viewModel
        self.isDark = isDark
    }
    
    public var body: some View {
        let isCycling = (viewModel.settings.sport == .cycling)
        let totalKm = result.totalDistanceMeters / 1000.0
        
        VStack(alignment: .leading, spacing: 14) {
            // Header: Kartu Feed Strava Badge
            HStack {
                HStack(spacing: 5) {
                    Image(systemName: "sparkles")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                    Text("PREVIEW TAMPILAN FEED STRAVA")
                        .font(.system(size: 11, weight: .bold))
                        .tracking(1.0)
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                Spacer()
                Text("Autentik")
                    .font(.system(size: 10, weight: .bold))
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(StravaTheme.orange.opacity(0.15))
                    .foregroundStyle(StravaTheme.orange)
                    .clipShape(Capsule())
            }
            
            // 1. Athlete Header
            HStack(spacing: 11) {
                ZStack {
                    Circle()
                        .fill(StravaTheme.orangeGradient)
                        .frame(width: 44, height: 44)
                    Text(userInitials(viewModel.athleteName))
                        .font(.system(size: 15, weight: .black))
                        .foregroundStyle(.white)
                }
                
                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 4) {
                        Text(viewModel.athleteName)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                        Image(systemName: "checkmark.seal.fill")
                            .font(.system(size: 11))
                            .foregroundStyle(StravaTheme.orange)
                    }
                    
                    Text("\(viewModel.activityDateText) • \(viewModel.activityLocation)")
                        .font(.system(size: 11))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                        .lineLimit(1)
                }
                
                Spacer()
                
                // Device Hardware Badge (Identitas Perangkat Terdeteksi)
                HStack(spacing: 4) {
                    Image(systemName: viewModel.selectedDevice.category.iconName)
                        .font(.system(size: 10, weight: .bold))
                    Text(viewModel.selectedDevice.formattedDeviceName)
                        .font(.system(size: 10, weight: .bold))
                        .lineLimit(1)
                }
                .padding(.horizontal, 8)
                .padding(.vertical, 5)
                .background(StravaTheme.subtlePill(isDark))
                .foregroundStyle(StravaTheme.textPrimary(isDark))
                .overlay(
                    Capsule()
                        .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
                )
                .clipShape(Capsule())
            }
            
            // 2. Activity Title & Sport Badge
            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 5) {
                    Image(systemName: isCycling ? "figure.outdoor.cycle" : "figure.run")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                    Text(isCycling ? "Ride" : "Run")
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(StravaTheme.orange)
                }
                
                Text(viewModel.activityTitle)
                    .font(.system(size: 20, weight: .black))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
            }
            
            // 3. Map Canvas with Route Polyline
            RouteMapView(
                points: result.points,
                selectedCoordinate: nil,
                isDark: isDark
            )
            .frame(height: 195)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            
            // 4. Hero Metrics (Iconic 3-Column Strava Card)
            HStack(spacing: 0) {
                feedStatBox(title: "Jarak", value: String(format: "%.2f km", totalKm))
                
                Divider().frame(height: 36).background(StravaTheme.cardBorder(isDark))
                
                feedStatBox(title: "Elev Gain", value: String(format: "+%.0f m", result.elevationGainMeters))
                
                Divider().frame(height: 36).background(StravaTheme.cardBorder(isDark))
                
                feedStatBox(title: "Waktu Bergerak", value: formatDuration(result.movingTimeSeconds))
            }
            .padding(.vertical, 8)
            .background(StravaTheme.subtlePill(isDark))
            .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            
            // 5. Secondary Badges (Speed, HR, Power, Temp)
            HStack(spacing: 0) {
                if isCycling {
                    feedMiniBadge(title: "Kec Rata-rata", value: String(format: "%.1f km/h", result.averageMovingSpeedKmh))
                } else {
                    let m = Int(result.averageMovingPaceSecondsPerKm) / 60
                    let s = Int(result.averageMovingPaceSecondsPerKm) % 60
                    feedMiniBadge(title: "Avg Pace", value: String(format: "%d:%02d /km", m, s))
                }
                
                if let hr = result.averageHeartRate {
                    feedMiniBadge(title: "Avg HR", value: "\(hr) bpm")
                }
                
                if isCycling, let pwr = result.averagePower, pwr > 0 {
                    feedMiniBadge(title: "Avg Power", value: "\(pwr) W")
                } else if !isCycling, let cad = result.averageCadence, cad > 0 {
                    feedMiniBadge(title: "Cadence", value: "\(cad) SPM")
                }
                
                if let temp = result.averageTemperature {
                    feedMiniBadge(title: "Suhu", value: "\(temp)°C")
                }
            }
            
            Divider().background(StravaTheme.cardBorder(isDark))
            
            // 6. Social Kudos Bar
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: "hand.thumbsup.fill")
                        .font(.system(size: 13))
                        .foregroundStyle(StravaTheme.orange)
                    Text("34 atlet memberi kudos")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
                
                Spacer()
                
                HStack(spacing: 16) {
                    Image(systemName: "bubble.right")
                        .font(.system(size: 14))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Image(systemName: "square.and.arrow.up")
                        .font(.system(size: 14))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                }
            }
            .padding(.top, 2)
        }
        .padding(16)
        .background(StravaTheme.cardBackground(isDark))
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
    
    private func feedStatBox(title: String, value: String) -> some View {
        VStack(spacing: 2) {
            Text(title)
                .font(.system(size: 11, weight: .medium))
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            Text(value)
                .font(.system(size: 16, weight: .black, design: .rounded))
                .foregroundStyle(StravaTheme.textPrimary(isDark))
        }
        .frame(maxWidth: .infinity)
    }
    
    private func feedMiniBadge(title: String, value: String) -> some View {
        VStack(spacing: 1) {
            Text(title)
                .font(.system(size: 10, weight: .medium))
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            Text(value)
                .font(.system(size: 12, weight: .bold))
                .foregroundStyle(StravaTheme.textPrimary(isDark))
        }
        .frame(maxWidth: .infinity)
    }
    
    private func userInitials(_ name: String) -> String {
        let parts = name.split(separator: " ")
        if parts.count >= 2 {
            return "\(parts[0].prefix(1))\(parts[1].prefix(1))".uppercased()
        }
        return String(name.prefix(2)).uppercased()
    }
    
    private func formatDuration(_ seconds: Int64) -> String {
        let h = seconds / 3600
        let m = (seconds % 3600) / 60
        let s = seconds % 60
        if h > 0 {
            return String(format: "%dj %02dm", h, m)
        }
        return String(format: "%dm %02ds", m, s)
    }
}

// MARK: - Activity History Sheet
public struct ActivityHistorySheet: View {
    @Bindable var viewModel: AppViewModel
    @Environment(\.dismiss) private var dismiss
    @Environment(\.openURL) private var openURL
    @State private var selectedActivityForShare: ArchivedActivity?
    @State private var showShareSheet = false
    
    public init(viewModel: AppViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        
        NavigationStack {
            ZStack {
                StravaTheme.background(isDark).ignoresSafeArea()
                
                if viewModel.archivedActivities.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "tray")
                            .font(.system(size: 48))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                        Text("Belum Ada Berkas .FIT")
                            .font(.system(size: 17, weight: .bold))
                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                        Text("Setiap simulasi yang berhasil diekspor akan tersimpan otomatis di sini untuk diakses atau dibagikan kapan saja.")
                            .font(.system(size: 13))
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 36)
                    }
                } else {
                    List {
                        ForEach(viewModel.archivedActivities) { item in
                            ArchivedActivityRow(
                                item: item,
                                isDark: isDark,
                                onShare: {
                                    selectedActivityForShare = item
                                    showShareSheet = true
                                },
                                onUploadStrava: {
                                    if let url = URL(string: "https://www.strava.com/upload/select") {
                                        openURL(url)
                                    }
                                }
                            )
                            .listRowBackground(StravaTheme.cardBackground(isDark))
                            .listRowSeparatorTint(StravaTheme.cardBorder(isDark))
                        }
                        .onDelete { indexSet in
                            for index in indexSet {
                                let item = viewModel.archivedActivities[index]
                                viewModel.deleteArchivedActivity(id: item.id)
                            }
                        }
                    }
                    .listStyle(.insetGrouped)
                    .scrollContentBackground(.hidden)
                }
            }
            .navigationTitle("Riwayat Berkas .FIT")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Tutup") {
                        dismiss()
                    }
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
                }
            }
            .sheet(isPresented: $showShareSheet) {
                if let act = selectedActivityForShare, let fileUrl = viewModel.fileUrlForArchivedActivity(act) {
                    ShareSheet(activityItems: [fileUrl])
                }
            }
        }
    }
}

// MARK: - Archived Activity Row
public struct ArchivedActivityRow: View {
    public let item: ArchivedActivity
    public let isDark: Bool
    public let onShare: () -> Void
    public let onUploadStrava: () -> Void
    
    public init(item: ArchivedActivity, isDark: Bool, onShare: @escaping () -> Void, onUploadStrava: @escaping () -> Void) {
        self.item = item
        self.isDark = isDark
        self.onShare = onShare
        self.onUploadStrava = onUploadStrava
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                HStack(spacing: 6) {
                    Image(systemName: item.sport == .cycling ? "figure.outdoor.cycle" : "figure.run")
                        .foregroundStyle(StravaTheme.orange)
                    Text(item.activityTitle)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                Spacer()
                Text(item.deviceName)
                    .font(.system(size: 10, weight: .bold))
                    .padding(.horizontal, 7)
                    .padding(.vertical, 3)
                    .background(StravaTheme.subtlePill(isDark))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                    .clipShape(Capsule())
            }
            
            Text("\(item.dateText) • \(item.fileName)")
                .font(.system(size: 11))
                .foregroundStyle(StravaTheme.textSecondary(isDark))
            
            HStack(spacing: 12) {
                Text(String(format: "%.2f km", item.distanceMeters / 1000.0))
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                Text(String(format: "+%.0f m", item.elevationGainMeters))
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Text(String(format: "%.1f km/h", item.averageSpeedKmh))
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
                Spacer()
                Text(String(format: "%.1f KB", Double(item.fileSize) / 1024.0))
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
            }
            
            HStack(spacing: 8) {
                Button {
                    onShare()
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: "square.and.arrow.up")
                        Text("Bagikan")
                    }
                    .font(.system(size: 12, weight: .bold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(StravaTheme.orange)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                }
                .buttonStyle(.plain)
                
                Button {
                    onUploadStrava()
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: "arrow.up.circle.fill")
                        Text("Upload ke Strava")
                    }
                    .font(.system(size: 12, weight: .bold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 6)
                    .background(StravaTheme.subtlePill(isDark))
                    .foregroundStyle(StravaTheme.orange)
                    .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                }
                .buttonStyle(.plain)
            }
            .padding(.top, 4)
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Floating Workflow Bottom Dock
public struct FloatingWorkflowDock: View {
    @Bindable var viewModel: AppViewModel
    public let isDark: Bool
    
    public init(viewModel: AppViewModel, isDark: Bool) {
        self.viewModel = viewModel
        self.isDark = isDark
    }
    
    public var body: some View {
        VStack(spacing: 8) {
            // 3-Step Breadcrumbs Indicator
            HStack(spacing: 2) {
                stepBadge(
                    stepNumber: 1,
                    title: "Parameter",
                    isActive: viewModel.currentScreen == .routeReview || viewModel.currentScreen == .activitySettings,
                    isDone: viewModel.currentScreen == .preview || viewModel.currentScreen == .export
                ) {
                    if viewModel.currentScreen == .preview {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            viewModel.currentScreen = .activitySettings
                        }
                    }
                }
                
                stepChevron
                
                stepBadge(
                    stepNumber: 2,
                    title: "Simulasi",
                    isActive: viewModel.currentScreen == .activitySettings || viewModel.isSimulating,
                    isDone: viewModel.currentScreen == .preview || viewModel.currentScreen == .export
                ) {
                    if viewModel.currentScreen == .routeReview {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            viewModel.currentScreen = .activitySettings
                        }
                    }
                }
                
                stepChevron
                
                stepBadge(
                    stepNumber: 3,
                    title: "Ekspor",
                    isActive: viewModel.currentScreen == .preview || viewModel.currentScreen == .export,
                    isDone: viewModel.currentScreen == .export
                ) {
                    if viewModel.simulationResult != nil {
                        withAnimation(.easeInOut(duration: 0.2)) {
                            viewModel.currentScreen = .export
                        }
                    }
                }
            }
            .padding(.horizontal, 6)
            .padding(.top, 2)
            
            // Primary Action Button (Sesuai Urutan 1, 2, 3)
            switch viewModel.currentScreen {
            case .routeReview:
                Button {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        viewModel.currentScreen = .activitySettings
                    }
                } label: {
                    HStack(spacing: 8) {
                        Text("1. Atur parameter Aktivitas")
                            .font(.system(size: 15, weight: .black))
                        Image(systemName: "arrow.right.circle.fill")
                            .font(.system(size: 16, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(StravaTheme.orangeGradient)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    .shadow(color: StravaTheme.orange.opacity(0.35), radius: 6, x: 0, y: 3)
                }
                .sensoryFeedback(.impact, trigger: viewModel.currentScreen)
                
            case .activitySettings:
                Button {
                    viewModel.runSimulation()
                } label: {
                    HStack(spacing: 8) {
                        if viewModel.isSimulating {
                            ProgressView().tint(.white)
                            Text("Menghitung Simulasi Fisika...")
                                .font(.system(size: 15, weight: .black))
                        } else {
                            Image(systemName: "play.circle.fill")
                                .font(.system(size: 17, weight: .bold))
                            Text("2. Jalankan simulasi Fisika")
                                .font(.system(size: 15, weight: .black))
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(StravaTheme.orangeGradient)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    .shadow(color: StravaTheme.orange.opacity(0.35), radius: 6, x: 0, y: 3)
                }
                .disabled(viewModel.isSimulating)
                .sensoryFeedback(.impact, trigger: viewModel.isSimulating)
                
            case .preview:
                Button {
                    withAnimation(.easeInOut(duration: 0.25)) {
                        viewModel.currentScreen = .export
                    }
                } label: {
                    HStack(spacing: 8) {
                        Image(systemName: "checkmark.seal.fill")
                            .font(.system(size: 16, weight: .bold))
                        Text("3. Validasi dan Ekspor")
                            .font(.system(size: 15, weight: .black))
                        Image(systemName: "arrow.right")
                            .font(.system(size: 14, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(StravaTheme.orangeGradient)
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    .shadow(color: StravaTheme.orange.opacity(0.35), radius: 6, x: 0, y: 3)
                }
                .sensoryFeedback(.selection, trigger: viewModel.currentScreen)
                
            default:
                EmptyView()
            }
        }
        .padding(8)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(isDark ? Color(red: 0.12, green: 0.13, blue: 0.16).opacity(0.96) : Color.white.opacity(0.96))
                .shadow(color: Color.black.opacity(isDark ? 0.45 : 0.15), radius: 14, x: 0, y: 6)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(isDark ? Color.white.opacity(0.12) : Color.black.opacity(0.08), lineWidth: 1)
        )
    }
    
    private func stepBadge(stepNumber: Int, title: String, isActive: Bool, isDone: Bool, onTap: @escaping () -> Void) -> some View {
        Button {
            onTap()
        } label: {
            HStack(spacing: 4) {
                ZStack {
                    Circle()
                        .fill(isDone ? Color.green : (isActive ? StravaTheme.orange : StravaTheme.subtlePill(isDark)))
                        .frame(width: 16, height: 16)
                    if isDone {
                        Image(systemName: "checkmark")
                            .font(.system(size: 8, weight: .black))
                            .foregroundStyle(.white)
                    } else {
                        Text("\(stepNumber)")
                            .font(.system(size: 9, weight: .black))
                            .foregroundStyle(isActive ? .white : StravaTheme.textSecondary(isDark))
                    }
                }
                Text(title)
                    .font(.system(size: 11, weight: isActive ? .bold : .medium))
                    .foregroundStyle(isActive ? StravaTheme.textPrimary(isDark) : StravaTheme.textSecondary(isDark))
            }
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(.plain)
    }
    
    private var stepChevron: some View {
        Image(systemName: "chevron.right")
            .font(.system(size: 8, weight: .bold))
            .foregroundStyle(StravaTheme.textSecondary(isDark).opacity(0.5))
    }
}

