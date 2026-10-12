import SwiftUI
import MapKit
import CoreLocation

public struct RouteStudioView: View {
    @Bindable var viewModel: AppViewModel
    
    public enum StudioMode: String, CaseIterable, Identifiable {
        case waypoints = "Titik Jalan"
        case smartLoop = "Smart Loop"
        case search = "Cari Tempat"
        public var id: String { rawValue }
    }
    
    @State private var mode: StudioMode = .waypoints
    @State private var waypoints: [CLLocationCoordinate2D] = []
    @State private var routedCoordinates: [CLLocationCoordinate2D] = []
    @State private var isCalculating: Bool = false
    
    // GPS Location State
    @State private var locationService = UserLocationService()
    @State private var gpsStatusMessage: String? = nil
    
    // Smart Loop State
    @State private var targetLoopKm: Double = 5.0
    @State private var loopHeadingAngle: Double = 45.0
    
    // Search State
    @State private var searchQuery: String = ""
    @State private var searchResults: [MKMapItem] = []
    @State private var isSearching: Bool = false
    
    // Metadata & Export
    @State private var routeName: String = "Rute Kalcer Baru"
    @State private var showGpxShareSheet: Bool = false
    @State private var exportedGpxUrl: URL? = nil
    
    // Map Camera
    @State private var cameraPosition: MapCameraPosition = .region(
        MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: -7.2575, longitude: 112.7521), // Default Surabaya / Jawa Timur
            span: MKCoordinateSpan(latitudeDelta: 0.05, longitudeDelta: 0.05)
        )
    )
    @State private var mapCenterCoord: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: -7.2575, longitude: 112.7521)
    
    public init(viewModel: AppViewModel) {
        self.viewModel = viewModel
    }
    
    public var body: some View {
        let isDark = viewModel.appTheme.isDark
        let isCycling = (viewModel.settings.sport == .cycling)
        
        ZStack(alignment: .bottom) {
            // 1. INTERACTIVE MAP CANVAS
            MapReader { proxy in
                Map(position: $cameraPosition) {
                    UserAnnotation()
                    
                    // Polyline rute jalan nyata
                    if routedCoordinates.count >= 2 {
                        MapPolyline(coordinates: routedCoordinates)
                            .stroke(StravaTheme.orange, lineWidth: 5)
                    }
                    
                    // Waypoint markers
                    ForEach(0..<waypoints.count, id: \.self) { idx in
                        let coord = waypoints[idx]
                        Annotation(idx == 0 ? "Start" : (idx == waypoints.count - 1 ? "Finish" : "Titik \(idx + 1)"), coordinate: coord) {
                            ZStack {
                                Circle()
                                    .fill(idx == 0 ? Color.green : (idx == waypoints.count - 1 ? Color.red : StravaTheme.orange))
                                    .frame(width: 24, height: 24)
                                    .shadow(radius: 3)
                                
                                if idx == 0 {
                                    Image(systemName: isCycling ? "figure.outdoor.cycle" : "figure.run")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundStyle(.white)
                                } else if idx == waypoints.count - 1 {
                                    Image(systemName: "flag.checkered")
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundStyle(.white)
                                } else {
                                    Text("\(idx + 1)")
                                        .font(.system(size: 10, weight: .bold))
                                        .foregroundStyle(.white)
                                }
                            }
                        }
                    }
                }
                .mapStyle(isDark ? .standard(elevation: .realistic) : .standard(elevation: .realistic))
                .onTapGesture { screenPoint in
                    if mode == .waypoints {
                        if let coord = proxy.convert(screenPoint, from: .local) {
                            addWaypoint(coord)
                        }
                    } else if mode == .smartLoop {
                        if let coord = proxy.convert(screenPoint, from: .local) {
                            mapCenterCoord = coord
                        }
                    }
                }
                .onMapCameraChange { context in
                    mapCenterCoord = context.region.center
                }
            }
            .ignoresSafeArea(edges: .bottom)
            
            // 2. FLOATING GPS BUTTON (Sisi Kanan Peta)
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    Button {
                        useGpsLocationAsStart()
                    } label: {
                        ZStack {
                            Circle()
                                .fill(StravaTheme.cardBackground(isDark).opacity(0.96))
                                .frame(width: 44, height: 44)
                                .shadow(color: Color.black.opacity(isDark ? 0.45 : 0.2), radius: 8, x: 0, y: 4)
                                .overlay(
                                    Circle().stroke(StravaTheme.orange.opacity(0.85), lineWidth: 1.5)
                                )
                            
                            if locationService.isLocating {
                                ProgressView()
                                    .tint(StravaTheme.orange)
                            } else {
                                Image(systemName: "location.fill")
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundStyle(StravaTheme.orange)
                            }
                        }
                    }
                    .sensoryFeedback(.impact, trigger: locationService.isLocating)
                    .padding(.trailing, 16)
                    .padding(.bottom, 220)
                }
            }
            
            // 3. OVERLAY CONTROLS
            VStack(spacing: 12) {
                // Top Floating Toolbar: Mode Selector
                VStack(spacing: 6) {
                    topModeSelector(isDark: isDark)
                    
                    if let msg = gpsStatusMessage {
                        HStack(spacing: 6) {
                            Image(systemName: "location.fill")
                                .font(.system(size: 10, weight: .bold))
                                .foregroundStyle(StravaTheme.orange)
                            Text(msg)
                                .font(.system(size: 11, weight: .semibold))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(StravaTheme.cardBackground(isDark).opacity(0.95))
                        .clipShape(Capsule())
                        .overlay(Capsule().stroke(StravaTheme.orange.opacity(0.7), lineWidth: 1))
                        .shadow(color: Color.black.opacity(isDark ? 0.3 : 0.1), radius: 6, x: 0, y: 3)
                        .transition(.move(edge: .top).combined(with: .opacity))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                
                // Mode-specific parameter card
                if mode == .smartLoop {
                    smartLoopControlCard(isDark: isDark, isCycling: isCycling)
                        .padding(.horizontal, 16)
                        .transition(.move(edge: .top).combined(with: .opacity))
                } else if mode == .search {
                    searchBarAndResultsCard(isDark: isDark)
                        .padding(.horizontal, 16)
                        .transition(.move(edge: .top).combined(with: .opacity))
                }
                
                Spacer()
                
                // Bottom HUD Panel & Execution Dock
                bottomStatsAndActionCard(isDark: isDark)
                    .padding(.horizontal, 14)
                    .padding(.bottom, 10)
            }
        }
        .onAppear {
            if let first = viewModel.currentTrack?.points.first {
                let center = CLLocationCoordinate2D(latitude: first.latitude, longitude: first.longitude)
                mapCenterCoord = center
                cameraPosition = .region(MKCoordinateRegion(center: center, span: MKCoordinateSpan(latitudeDelta: 0.04, longitudeDelta: 0.04)))
            } else {
                locationService.fetchCurrentLocation { coord in
                    if let coord = coord {
                        self.mapCenterCoord = coord
                        self.cameraPosition = .region(MKCoordinateRegion(center: coord, span: MKCoordinateSpan(latitudeDelta: 0.025, longitudeDelta: 0.025)))
                    }
                }
            }
            targetLoopKm = isCycling ? 25.0 : 5.0
        }
        .sheet(isPresented: $showGpxShareSheet) {
            if let url = exportedGpxUrl {
                ShareSheet(activityItems: [url])
            }
        }
    }
    
    // MARK: - Top Mode Selector
    private func topModeSelector(isDark: Bool) -> some View {
        HStack(spacing: 6) {
            ForEach(StudioMode.allCases) { item in
                Button {
                    withAnimation(.easeInOut(duration: 0.2)) {
                        mode = item
                    }
                } label: {
                    HStack(spacing: 4) {
                        Image(systemName: item == .waypoints ? "point.topleft.down.to.point.bottomright.curvepath" : (item == .smartLoop ? "arrow.triangle.2.circlepath" : "magnifyingglass"))
                            .font(.system(size: 11, weight: .bold))
                        Text(item.rawValue)
                            .font(.system(size: 12, weight: mode == item ? .bold : .medium))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 36)
                    .background(mode == item ? StravaTheme.orange : StravaTheme.cardBackground(isDark).opacity(0.92))
                    .foregroundStyle(mode == item ? .white : StravaTheme.textPrimary(isDark))
                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                    .shadow(color: Color.black.opacity(isDark ? 0.3 : 0.1), radius: 6, x: 0, y: 3)
                }
            }
        }
        .padding(4)
        .background(StravaTheme.cardBackground(isDark).opacity(0.85))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
    }
    
    // MARK: - Smart Loop Card
    private func smartLoopControlCard(isDark: Bool, isCycling: Bool) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Image(systemName: "sparkles")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
                Text("GENERATOR RUTE LOOP OTOMATIS")
                    .font(.system(size: 11, weight: .bold))
                    .tracking(1.0)
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Spacer()
                Text(String(format: "%.1f km", targetLoopKm))
                    .font(.system(size: 15, weight: .black))
                    .foregroundStyle(StravaTheme.orange)
            }
            
            // Preset Jarak Cepat
            HStack(spacing: 6) {
                let presets: [(String, Double)] = isCycling ?
                    [("15k", 15.0), ("25k", 25.0), ("40k", 40.0), ("60k", 60.0), ("100k", 100.0)] :
                    [("3k", 3.0), ("5k", 5.0), ("10k", 10.0), ("15k", 15.0), ("21.1k", 21.1)]
                
                ForEach(presets, id: \.0) { p in
                    Button {
                        targetLoopKm = p.1
                    } label: {
                        Text(p.0)
                            .font(.system(size: 11, weight: .bold))
                            .padding(.horizontal, 9)
                            .padding(.vertical, 5)
                            .background(abs(targetLoopKm - p.1) < 0.2 ? StravaTheme.orange : StravaTheme.subtlePill(isDark))
                            .foregroundStyle(abs(targetLoopKm - p.1) < 0.2 ? .white : StravaTheme.textPrimary(isDark))
                            .clipShape(Capsule())
                    }
                }
            }
            
            // Arah Loop
            HStack(spacing: 6) {
                Text("Arah Loop:")
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                
                ForEach([("Utara", 0.0), ("Timur", 90.0), ("Selatan", 180.0), ("Barat", 270.0)], id: \.0) { item in
                    Button {
                        loopHeadingAngle = item.1
                    } label: {
                        Text(item.0)
                            .font(.system(size: 10, weight: .semibold))
                            .padding(.horizontal, 7)
                            .padding(.vertical, 4)
                            .background(abs(loopHeadingAngle - item.1) < 5.0 ? StravaTheme.orange.opacity(0.85) : StravaTheme.subtlePill(isDark))
                            .foregroundStyle(abs(loopHeadingAngle - item.1) < 5.0 ? .white : StravaTheme.textPrimary(isDark))
                            .clipShape(Capsule())
                    }
                }
            }
            
            // Tombol Generate Loop
            Button {
                generateSmartLoop()
            } label: {
                HStack(spacing: 6) {
                    if isCalculating {
                        ProgressView().tint(.white)
                        Text("Menghubungkan Jaringan Jalan...")
                            .font(.system(size: 13, weight: .bold))
                    } else {
                        Image(systemName: "arrow.triangle.2.circlepath")
                            .font(.system(size: 13, weight: .bold))
                        Text("Buat Rute Loop Sekarang")
                            .font(.system(size: 13, weight: .bold))
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 38)
                .background(StravaTheme.orangeGradient)
                .foregroundStyle(.white)
                .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            }
            .disabled(isCalculating)
        }
        .padding(12)
        .background(StravaTheme.cardBackground(isDark).opacity(0.96))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .shadow(color: Color.black.opacity(isDark ? 0.35 : 0.15), radius: 8, x: 0, y: 4)
    }
    
    // MARK: - Search Bar & Results
    private func searchBarAndResultsCard(isDark: Bool) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                TextField("Cari Monas, GBK, Taman Bungkul, pantai...", text: $searchQuery)
                    .textFieldStyle(.plain)
                    .font(.system(size: 13, weight: .medium))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
                    .onSubmit {
                        performSearch()
                    }
                
                if !searchQuery.isEmpty {
                    Button {
                        searchQuery = ""
                        searchResults = []
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                    }
                }
                
                Button("Cari") {
                    performSearch()
                }
                .font(.system(size: 13, weight: .bold))
                .foregroundStyle(StravaTheme.orange)
            }
            .padding(10)
            .background(StravaTheme.subtlePill(isDark))
            .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
            
            if !searchResults.isEmpty {
                ScrollView(.vertical, showsIndicators: true) {
                    VStack(alignment: .leading, spacing: 4) {
                        ForEach(searchResults.prefix(4), id: \.self) { item in
                            Button {
                                addWaypoint(item.placemark.coordinate)
                                cameraPosition = .region(MKCoordinateRegion(center: item.placemark.coordinate, span: MKCoordinateSpan(latitudeDelta: 0.02, longitudeDelta: 0.02)))
                                searchResults = []
                                searchQuery = ""
                                mode = .waypoints
                            } label: {
                                HStack(spacing: 8) {
                                    Image(systemName: "mappin.circle.fill")
                                        .foregroundStyle(StravaTheme.orange)
                                    VStack(alignment: .leading, spacing: 1) {
                                        Text(item.name ?? "Lokasi")
                                            .font(.system(size: 13, weight: .bold))
                                            .foregroundStyle(StravaTheme.textPrimary(isDark))
                                        Text(item.placemark.title ?? "")
                                            .font(.system(size: 11))
                                            .foregroundStyle(StravaTheme.textSecondary(isDark))
                                            .lineLimit(1)
                                    }
                                    Spacer()
                                    Image(systemName: "plus.circle")
                                        .foregroundStyle(StravaTheme.orange)
                                }
                                .padding(8)
                                .background(StravaTheme.subtlePill(isDark))
                                .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                            }
                        }
                    }
                }
                .frame(maxHeight: 140)
            }
        }
        .padding(12)
        .background(StravaTheme.cardBackground(isDark).opacity(0.96))
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(StravaTheme.cardBorder(isDark), lineWidth: 1)
        )
        .shadow(color: Color.black.opacity(isDark ? 0.35 : 0.15), radius: 8, x: 0, y: 4)
    }
    
    // MARK: - Bottom Stats & Action Card
    private func bottomStatsAndActionCard(isDark: Bool) -> some View {
        let stats = calculateCurrentStats()
        
        return VStack(alignment: .leading, spacing: 10) {
            // Stats Row & Controls
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 1) {
                    Text("Jarak Rute")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text(String(format: "%.2f km", stats.distanceKm))
                        .font(.system(size: 17, weight: .black))
                        .foregroundStyle(StravaTheme.orange)
                }
                
                Divider().frame(height: 28).background(StravaTheme.cardBorder(isDark))
                
                VStack(alignment: .leading, spacing: 1) {
                    Text("Estimasi Elev")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text(String(format: "+%.0f m", stats.elevationGainM))
                        .font(.system(size: 15, weight: .black))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                
                Divider().frame(height: 28).background(StravaTheme.cardBorder(isDark))
                
                VStack(alignment: .leading, spacing: 1) {
                    Text("Waypoint")
                        .font(.system(size: 10, weight: .bold))
                        .foregroundStyle(StravaTheme.textSecondary(isDark))
                    Text("\(waypoints.count) Titik")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundStyle(StravaTheme.textPrimary(isDark))
                }
                
                Spacer()
                
                // Helper Buttons (GPS Start, Undo, Close Loop, Clear)
                HStack(spacing: 6) {
                    Button {
                        useGpsLocationAsStart()
                    } label: {
                        Image(systemName: "location.fill")
                            .font(.system(size: 13, weight: .bold))
                            .frame(width: 32, height: 32)
                            .background(StravaTheme.subtlePill(isDark))
                            .foregroundStyle(StravaTheme.orange)
                            .clipShape(Circle())
                    }
                    
                    if waypoints.count >= 2 {
                        Button {
                            closeLoop()
                        } label: {
                            Image(systemName: "arrow.triangle.capsulepath")
                                .font(.system(size: 13, weight: .bold))
                                .frame(width: 32, height: 32)
                                .background(StravaTheme.subtlePill(isDark))
                                .foregroundStyle(StravaTheme.orange)
                                .clipShape(Circle())
                        }
                    }
                    
                    if !waypoints.isEmpty {
                        Button {
                            undoLastWaypoint()
                        } label: {
                            Image(systemName: "arrow.uturn.backward")
                                .font(.system(size: 13, weight: .bold))
                                .frame(width: 32, height: 32)
                                .background(StravaTheme.subtlePill(isDark))
                                .foregroundStyle(StravaTheme.textPrimary(isDark))
                                .clipShape(Circle())
                        }
                        
                        Button {
                            resetStudio()
                        } label: {
                            Image(systemName: "trash")
                                .font(.system(size: 13, weight: .bold))
                                .frame(width: 32, height: 32)
                                .background(StravaTheme.subtlePill(isDark))
                                .foregroundStyle(Color.red)
                                .clipShape(Circle())
                        }
                    }
                }
            }
            
            // Mode Banner: Drive (Tanpa Jalan Tol)
            HStack(spacing: 6) {
                Image(systemName: "car.fill")
                    .font(.system(size: 11, weight: .bold))
                    .foregroundStyle(StravaTheme.orange)
                Text("Mode Rute: Drive (Menghindari Jalan Tol)")
                    .font(.system(size: 11, weight: .semibold))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                Spacer()
                if isCalculating {
                    HStack(spacing: 4) {
                        ProgressView().scaleEffect(0.6).tint(StravaTheme.orange)
                        Text("Menghubungkan aspal...")
                            .font(.system(size: 10, weight: .medium))
                            .foregroundStyle(StravaTheme.orange)
                    }
                }
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 5)
            .background(StravaTheme.subtlePill(isDark))
            .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
            
            // Tombol Mulai dari Lokasi Saya (GPS Start) saat waypoints kosong
            if waypoints.isEmpty {
                Button {
                    useGpsLocationAsStart()
                } label: {
                    HStack(spacing: 8) {
                        if locationService.isLocating {
                            ProgressView().tint(StravaTheme.orange)
                        } else {
                            Image(systemName: "location.fill")
                                .font(.system(size: 13, weight: .bold))
                        }
                        Text("Mulai dari Lokasi Saya (GPS Start)")
                            .font(.system(size: 13, weight: .bold))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 38)
                    .background(StravaTheme.orange.opacity(0.14))
                    .foregroundStyle(StravaTheme.orange)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10, style: .continuous)
                            .stroke(StravaTheme.orange.opacity(0.6), lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 10, style: .continuous))
                }
                .sensoryFeedback(.impact, trigger: waypoints.count)
            }
            
            // Editable Route Name Field
            HStack(spacing: 8) {
                Image(systemName: "pencil")
                    .font(.system(size: 11))
                    .foregroundStyle(StravaTheme.textSecondary(isDark))
                TextField("Nama rute...", text: $routeName)
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(StravaTheme.textPrimary(isDark))
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(StravaTheme.subtlePill(isDark))
            .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
            
            // Action Buttons
            HStack(spacing: 8) {
                // Button 1: Gunakan Rute (Lanjut ke Simulasi)
                Button {
                    commitRouteToSimulation()
                } label: {
                    HStack(spacing: 6) {
                        Image(systemName: "play.circle.fill")
                            .font(.system(size: 15, weight: .bold))
                        Text("Gunakan Rute Ini")
                            .font(.system(size: 14, weight: .black))
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 46)
                    .background(routedCoordinates.count >= 2 ? StravaTheme.orangeGradient : LinearGradient(colors: [Color.gray], startPoint: .leading, endPoint: .trailing))
                    .foregroundStyle(.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    .shadow(color: StravaTheme.orange.opacity(routedCoordinates.count >= 2 ? 0.35 : 0), radius: 6, x: 0, y: 3)
                }
                .disabled(routedCoordinates.count < 2 || isCalculating)
                
                // Button 2: Ekspor Berkas .GPX
                Button {
                    exportGpxFile()
                } label: {
                    HStack(spacing: 5) {
                        Image(systemName: "square.and.arrow.up")
                            .font(.system(size: 13, weight: .bold))
                        Text("GPX")
                            .font(.system(size: 13, weight: .bold))
                    }
                    .frame(width: 82, height: 46)
                    .background(StravaTheme.subtlePill(isDark))
                    .foregroundStyle(StravaTheme.orange)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12, style: .continuous)
                            .stroke(StravaTheme.orange.opacity(0.8), lineWidth: 1.5)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                }
                .disabled(routedCoordinates.count < 2 || isCalculating)
            }
        }
        .padding(14)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(isDark ? Color(red: 0.12, green: 0.13, blue: 0.16).opacity(0.97) : Color.white.opacity(0.97))
                .shadow(color: Color.black.opacity(isDark ? 0.45 : 0.15), radius: 14, x: 0, y: 6)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(isDark ? Color.white.opacity(0.12) : Color.black.opacity(0.08), lineWidth: 1)
        )
    }
    
    // MARK: - Logic & Actions
    
    private func addWaypoint(_ coord: CLLocationCoordinate2D) {
        waypoints.append(coord)
        recalculateRoadPolyline()
    }
    
    private func undoLastWaypoint() {
        guard !waypoints.isEmpty else { return }
        waypoints.removeLast()
        if waypoints.count < 2 {
            routedCoordinates = waypoints
        } else {
            recalculateRoadPolyline()
        }
    }
    
    private func closeLoop() {
        guard waypoints.count >= 2, let first = waypoints.first else { return }
        waypoints.append(first)
        recalculateRoadPolyline()
    }
    
    private func resetStudio() {
        waypoints = []
        routedCoordinates = []
        searchResults = []
        searchQuery = ""
    }
    
    private func recalculateRoadPolyline() {
        guard waypoints.count >= 2 else {
            routedCoordinates = waypoints
            return
        }
        
        isCalculating = true
        let pts = waypoints
        // Mode drive (.automobile) dengan proteksi penghindaran jalan tol
        let transport: MKDirectionsTransportType = .automobile
        
        Task {
            let roadPath = await RouteCreatorEngine.routeWaypoints(coordinates: pts, transportType: transport, avoidTolls: true)
            await MainActor.run {
                self.routedCoordinates = roadPath
                self.isCalculating = false
            }
        }
    }
    
    private func generateSmartLoop() {
        let center = mapCenterCoord
        let targetKm = targetLoopKm
        let heading = loopHeadingAngle
        // Mode drive (.automobile) dengan proteksi penghindaran jalan tol
        let transport: MKDirectionsTransportType = .automobile
        
        isCalculating = true
        let loopPoints = RouteCreatorEngine.generateLoopWaypoints(
            center: center,
            targetDistanceKm: targetKm,
            headingAngleDegrees: heading
        )
        self.waypoints = loopPoints
        
        Task {
            let roadPath = await RouteCreatorEngine.routeWaypoints(coordinates: loopPoints, transportType: transport, avoidTolls: true)
            await MainActor.run {
                self.routedCoordinates = roadPath
                self.isCalculating = false
                
                // Animate zoom to show loop
                if let minLat = roadPath.map({ $0.latitude }).min(),
                   let maxLat = roadPath.map({ $0.latitude }).max(),
                   let minLon = roadPath.map({ $0.longitude }).min(),
                   let maxLon = roadPath.map({ $0.longitude }).max() {
                    let center = CLLocationCoordinate2D(latitude: (minLat + maxLat) / 2.0, longitude: (minLon + maxLon) / 2.0)
                    let span = MKCoordinateSpan(latitudeDelta: max(0.015, (maxLat - minLat) * 1.3), longitudeDelta: max(0.015, (maxLon - minLon) * 1.3))
                    self.cameraPosition = .region(MKCoordinateRegion(center: center, span: span))
                }
            }
        }
    }
    
    private func performSearch() {
        guard !searchQuery.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        isSearching = true
        let request = MKLocalSearch.Request()
        request.naturalLanguageQuery = searchQuery
        request.region = MKCoordinateRegion(center: mapCenterCoord, span: MKCoordinateSpan(latitudeDelta: 0.15, longitudeDelta: 0.15))
        
        let search = MKLocalSearch(request: request)
        search.start { response, error in
            isSearching = false
            if let items = response?.mapItems {
                self.searchResults = items
            }
        }
    }
    
    private func calculateCurrentStats() -> (distanceKm: Double, elevationGainM: Double) {
        guard routedCoordinates.count >= 2 else { return (0, 0) }
        var distM = 0.0
        var prev = routedCoordinates[0]
        for c in routedCoordinates.dropFirst() {
            distM += GeoMath.haversineDistance(lat1: prev.latitude, lon1: prev.longitude, lat2: c.latitude, lon2: c.longitude)
            prev = c
        }
        // Estimasi gain moderat realistis (~6-8m per km untuk perkotaan)
        let gainM = (distM / 1000.0) * 7.5
        return (distM / 1000.0, gainM)
    }
    
    private func commitRouteToSimulation() {
        guard routedCoordinates.count >= 2 else { return }
        let track = RouteCreatorEngine.buildRouteTrack(from: routedCoordinates)
        let name = routeName.trimmingCharacters(in: .whitespaces).isEmpty ? "Rute Kalcer" : routeName
        viewModel.applyCreatedRoute(track, routeName: name)
    }
    
    private func exportGpxFile() {
        guard routedCoordinates.count >= 2 else { return }
        let track = RouteCreatorEngine.buildRouteTrack(from: routedCoordinates)
        let name = routeName.trimmingCharacters(in: .whitespaces).isEmpty ? "Rute_Kalcer" : routeName
        let xml = RouteCreatorEngine.exportGpxXml(track: track, routeName: name)
        
        let safeName = name.replacingOccurrences(of: " ", with: "_")
        let tempDir = FileManager.default.temporaryDirectory
        let fileUrl = tempDir.appendingPathComponent("\(safeName).gpx")
        
        do {
            try xml.write(to: fileUrl, atomically: true, encoding: .utf8)
            self.exportedGpxUrl = fileUrl
            self.showGpxShareSheet = true
        } catch {
            print("Gagal ekspor GPX: \(error)")
        }
    }
    
    // MARK: - GPS Start Action
    private func useGpsLocationAsStart() {
        locationService.fetchCurrentLocation { coord in
            guard let coord = coord else { return }
            withAnimation(.easeInOut(duration: 0.3)) {
                self.mapCenterCoord = coord
                self.cameraPosition = .region(
                    MKCoordinateRegion(
                        center: coord,
                        span: MKCoordinateSpan(latitudeDelta: 0.025, longitudeDelta: 0.025)
                    )
                )
                
                if mode == .smartLoop {
                    self.gpsStatusMessage = "Pusat loop disetel ke lokasi GPS Anda"
                } else {
                    if waypoints.isEmpty {
                        self.waypoints = [coord]
                        self.gpsStatusMessage = "Titik 1 (Start) disetel ke lokasi GPS Anda"
                    } else {
                        self.waypoints[0] = coord
                        self.gpsStatusMessage = "Titik Start (Titik 1) diperbarui ke lokasi GPS Anda"
                        self.recalculateRoadPolyline()
                    }
                }
            }
            
            Task {
                try? await Task.sleep(nanoseconds: 3_000_000_000)
                await MainActor.run {
                    withAnimation {
                        self.gpsStatusMessage = nil
                    }
                }
            }
        }
    }
}

// MARK: - User Location Service
@Observable
public final class UserLocationService: NSObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    public var userLocation: CLLocationCoordinate2D? = nil
    public var isLocating: Bool = false
    public var locationErrorMessage: String? = nil
    private var pendingHandler: ((CLLocationCoordinate2D?) -> Void)? = nil
    
    public override init() {
        super.init()
        manager.delegate = self
        manager.desiredAccuracy = kCLLocationAccuracyBest
    }
    
    public func fetchCurrentLocation(completion: @escaping (CLLocationCoordinate2D?) -> Void) {
        self.isLocating = true
        self.locationErrorMessage = nil
        let status = manager.authorizationStatus
        
        if status == .notDetermined {
            self.pendingHandler = completion
            manager.requestWhenInUseAuthorization()
        } else if status == .authorizedWhenInUse || status == .authorizedAlways {
            if let cached = manager.location?.coordinate {
                self.userLocation = cached
                self.isLocating = false
                completion(cached)
            } else {
                self.pendingHandler = completion
                manager.requestLocation()
            }
        } else {
            self.isLocating = false
            self.locationErrorMessage = "Izin GPS tidak aktif."
            completion(nil)
        }
    }
    
    public func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        let status = manager.authorizationStatus
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            manager.requestLocation()
        } else if status == .denied || status == .restricted {
            self.isLocating = false
            self.locationErrorMessage = "Akses GPS ditolak."
            pendingHandler?(nil)
            pendingHandler = nil
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        self.isLocating = false
        if let loc = locations.last?.coordinate {
            self.userLocation = loc
            pendingHandler?(loc)
        } else {
            pendingHandler?(nil)
        }
        pendingHandler = nil
    }
    
    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        self.isLocating = false
        self.locationErrorMessage = error.localizedDescription
        pendingHandler?(nil)
        pendingHandler = nil
    }
}
