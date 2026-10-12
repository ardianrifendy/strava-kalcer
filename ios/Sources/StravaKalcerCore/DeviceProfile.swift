import Foundation

public enum DeviceCategory: String, Codable, Hashable, CaseIterable {
    case cyclocomputer = "Cyclocomputer"
    case runningWatch = "Running Watch"
    case outdoorWatch = "Multisport / Outdoor"
    
    public var iconName: String {
        switch self {
        case .cyclocomputer: return "bicycle"
        case .runningWatch: return "applewatch"
        case .outdoorWatch: return "mountain.2"
        }
    }
}

public struct DeviceProfile: Identifiable, Codable, Hashable {
    public let id: String
    public let manufacturer: String
    public let modelName: String
    public let manufacturerId: UInt16
    public let productNumber: UInt16
    public let category: DeviceCategory
    public let supportedSports: [SportType]
    public let description: String
    
    public init(
        id: String,
        manufacturer: String,
        modelName: String,
        manufacturerId: UInt16,
        productNumber: UInt16,
        category: DeviceCategory,
        supportedSports: [SportType],
        description: String
    ) {
        self.id = id
        self.manufacturer = manufacturer
        self.modelName = modelName
        self.manufacturerId = manufacturerId
        self.productNumber = productNumber
        self.category = category
        self.supportedSports = supportedSports
        self.description = description
    }
    
    public var formattedDeviceName: String {
        if modelName.lowercased().starts(with: manufacturer.lowercased()) {
            return modelName
        }
        return "\(manufacturer) \(modelName)"
    }
    
    public var serialNumber: UInt32 {
        var hasher = Hasher()
        hasher.combine(id)
        hasher.combine(manufacturerId)
        hasher.combine(productNumber)
        let hash = UInt32(bitPattern: Int32(truncatingIfNeeded: hasher.finalize()))
        return 3_800_000_000 + (hash % 190_000_000)
    }
    
    public var softwareVersion: UInt16 {
        2118 // v21.18
    }
}

public struct DeviceProfileRegistry {
    // Official Manufacturer IDs:
    // Garmin = 1, Wahoo = 32, Apple = 255, Hammerhead = 260, COROS = 281, Bryton = 38, Suunto = 23, Polar = 123, Sigma = 15, Lezyne = 86, Stages = 68, SRM = 88
    public static let allProfiles: [DeviceProfile] = [
        // =====================================================================
        // CYCLOCOMPUTERS (Semua Brand Terkenal)
        // =====================================================================
        // GARMIN
        DeviceProfile(id: "garmin_edge_1050", manufacturer: "Garmin", modelName: "Edge 1050", manufacturerId: 1, productNumber: 4710, category: .cyclocomputer, supportedSports: [.cycling], description: "Flagship Garmin terbaru berlayar LCD liquid crystal tajam dengan built-in speaker & Garmin Pay."),
        DeviceProfile(id: "garmin_edge_1040_solar", manufacturer: "Garmin", modelName: "Edge 1040", manufacturerId: 1, productNumber: 4024, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomputer solar charging dengan Multi-Band GNSS."),
        DeviceProfile(id: "garmin_edge_840", manufacturer: "Garmin", modelName: "Edge 840", manufacturerId: 1, productNumber: 4061, category: .cyclocomputer, supportedSports: [.cycling], description: "Performa tinggi kombinasi layar sentuh dan tombol tactile."),
        DeviceProfile(id: "garmin_edge_540", manufacturer: "Garmin", modelName: "Edge 540", manufacturerId: 1, productNumber: 4062, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit tombol legendaris andalan peloton pro."),
        DeviceProfile(id: "garmin_edge_830", manufacturer: "Garmin", modelName: "Edge 830", manufacturerId: 1, productNumber: 3123, category: .cyclocomputer, supportedSports: [.cycling], description: "Touchscreen cyclocomp ringkas dan teruji."),
        DeviceProfile(id: "garmin_edge_530", manufacturer: "Garmin", modelName: "Edge 530", manufacturerId: 1, productNumber: 3122, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomp sejuta umat goweser kalcer."),
        DeviceProfile(id: "garmin_edge_130_plus", manufacturer: "Garmin", modelName: "Edge 130 Plus", manufacturerId: 1, productNumber: 3558, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomp ultra kompak dengan layar monokrom tajam di bawah sinar terik."),
        DeviceProfile(id: "garmin_edge_explore_2", manufacturer: "Garmin", modelName: "Edge Explore 2", manufacturerId: 1, productNumber: 3990, category: .cyclocomputer, supportedSports: [.cycling], description: "Navigator touring sepeda dengan layar 3 inci jernih."),

        // WAHOO FITNESS
        DeviceProfile(id: "wahoo_elemnt_ace", manufacturer: "Wahoo", modelName: "ELEMNT ACE", manufacturerId: 32, productNumber: 40, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit aero layar raksasa terbaru Wahoo dengan integrated wind sensor."),
        DeviceProfile(id: "wahoo_elemnt_roam_v2", manufacturer: "Wahoo", modelName: "ELEMNT ROAM v2", manufacturerId: 32, productNumber: 35, category: .cyclocomputer, supportedSports: [.cycling], description: "Dual-band GPS dengan 64-color screen dan PerfectView Zoom."),
        DeviceProfile(id: "wahoo_elemnt_bolt_v2", manufacturer: "Wahoo", modelName: "ELEMNT BOLT v2", manufacturerId: 32, productNumber: 34, category: .cyclocomputer, supportedSports: [.cycling], description: "Aero-optimized bike computer paling aerodinamis."),

        // HAMMERHEAD (SRAM)
        DeviceProfile(id: "hammerhead_karoo_3", manufacturer: "Hammerhead", modelName: "Karoo (3rd Gen)", manufacturerId: 260, productNumber: 3, category: .cyclocomputer, supportedSports: [.cycling], description: "Smartphone-grade retina display dengan integrasi SRAM AXS sempurna."),
        DeviceProfile(id: "hammerhead_karoo_2", manufacturer: "Hammerhead", modelName: "Karoo 2", manufacturerId: 260, productNumber: 2, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit berbasis Android favorit pecinta navigasi grafis."),

        // BRYTON
        DeviceProfile(id: "bryton_rider_s800", manufacturer: "Bryton", modelName: "Rider S800", manufacturerId: 38, productNumber: 800, category: .cyclocomputer, supportedSports: [.cycling], description: "Layar besar 3.4 inci dengan daya tahan baterai 36 jam."),
        DeviceProfile(id: "bryton_rider_s500", manufacturer: "Bryton", modelName: "Rider S500", manufacturerId: 38, productNumber: 500, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomputer kompetitif berfitur voice search."),
        DeviceProfile(id: "bryton_rider_750_se", manufacturer: "Bryton", modelName: "Rider 750 SE", manufacturerId: 38, productNumber: 750, category: .cyclocomputer, supportedSports: [.cycling], description: "Full color touchscreen dengan navigasi rerouting otomatis."),

        // MAGENE
        DeviceProfile(id: "magene_c606", manufacturer: "Magene", modelName: "C606 Smart GPS", manufacturerId: 255, productNumber: 606, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomp pintar 2.8 inci berfitur radar tail-light sync."),
        DeviceProfile(id: "magene_c406_pro", manufacturer: "Magene", modelName: "C406 Pro", manufacturerId: 255, productNumber: 406, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomp 2.4 inci 97 metrik terlengkap di kelasnya."),

        // iGPSPORT
        DeviceProfile(id: "igpsport_igs630", manufacturer: "iGPSPORT", modelName: "iGS630", manufacturerId: 255, productNumber: 630, category: .cyclocomputer, supportedSports: [.cycling], description: "Color navigation cyclocomp dengan satellite positioning 6 sistem."),
        DeviceProfile(id: "igpsport_bsc300", manufacturer: "iGPSPORT", modelName: "BSC300", manufacturerId: 255, productNumber: 300, category: .cyclocomputer, supportedSports: [.cycling], description: "Ultra-slim 14.5mm body dengan full-color map navigation."),

        // GIANT / STAGES
        DeviceProfile(id: "stages_dash_m200", manufacturer: "Stages", modelName: "Dash M200", manufacturerId: 68, productNumber: 200, category: .cyclocomputer, supportedSports: [.cycling], description: "Display EverBrite berkontras tinggi khusus analisis live power profile."),
        DeviceProfile(id: "stages_dash_l200", manufacturer: "Stages", modelName: "Dash L200", manufacturerId: 68, productNumber: 201, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit format horizontal/vertikal layar besar."),
        DeviceProfile(id: "giant_neostrack", manufacturer: "Giant", modelName: "NeosTrack GPS", manufacturerId: 255, productNumber: 901, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit resmi Giant Bicycles dengan sinkronisasi RideLife."),

        // SIGMA SPORT
        DeviceProfile(id: "sigma_rox_12_1", manufacturer: "SIGMA", modelName: "ROX 12.1 EVO", manufacturerId: 15, productNumber: 121, category: .cyclocomputer, supportedSports: [.cycling], description: "German engineered GPS computer dengan navigasi map cerdas."),
        DeviceProfile(id: "sigma_rox_11_1", manufacturer: "SIGMA", modelName: "ROX 11.1 EVO", manufacturerId: 15, productNumber: 111, category: .cyclocomputer, supportedSports: [.cycling], description: "Kompak 150 fungsi dengan transmisi ANT+ & BLE lengkap."),

        // LEZYNE
        DeviceProfile(id: "lezyne_mega_xl", manufacturer: "Lezyne", modelName: "Mega XL GPS", manufacturerId: 86, productNumber: 80, category: .cyclocomputer, supportedSports: [.cycling], description: "Layar besar resolusi tinggi dengan rotasi horizontal/vertikal dan baterai 48 jam."),

        // SRM (Schoberer Rad Messtechnik - Pure Pro Kalcer)
        DeviceProfile(id: "srm_pc8", manufacturer: "SRM", modelName: "PowerControl 8 (PC8)", manufacturerId: 88, productNumber: 8, category: .cyclocomputer, supportedSports: [.cycling], description: "Head unit anodized aluminium legendaris pembalap Tour de France."),

        // COOSPO & XOSS
        DeviceProfile(id: "coospo_bc200", manufacturer: "Coospo", modelName: "BC200 GPS", manufacturerId: 255, productNumber: 200, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomp 2.6 inci dengan tri-mode satellite positioning."),
        DeviceProfile(id: "xoss_nav_plus", manufacturer: "XOSS", modelName: "NAV+ Smart GPS", manufacturerId: 255, productNumber: 950, category: .cyclocomputer, supportedSports: [.cycling], description: "Cyclocomputer navigasi offline dengan sensor hybrid."),

        // =====================================================================
        // RUNNING WATCHES (Semua Brand Terkenal)
        // =====================================================================
        // GARMIN FORERUNNER & ENDURO
        DeviceProfile(id: "garmin_fr_965", manufacturer: "Garmin", modelName: "Forerunner 965", manufacturerId: 1, productNumber: 5003, category: .runningWatch, supportedSports: [.running], description: "Flagship running watch AMOLED dengan titanium bezel & running dynamics bawaan."),
        DeviceProfile(id: "garmin_fr_955", manufacturer: "Garmin", modelName: "Forerunner 955 Solar", manufacturerId: 1, productNumber: 4024, category: .runningWatch, supportedSports: [.running], description: "Jam maraton baterai solar dengan multi-band GNSS."),
        DeviceProfile(id: "garmin_fr_265", manufacturer: "Garmin", modelName: "Forerunner 265", manufacturerId: 1, productNumber: 5201, category: .runningWatch, supportedSports: [.running], description: "Jam tangan lari AMOLED ringan dengan metrik training readiness."),
        DeviceProfile(id: "garmin_fr_255", manufacturer: "Garmin", modelName: "Forerunner 255 Music", manufacturerId: 1, productNumber: 3927, category: .runningWatch, supportedSports: [.running], description: "Jam tangan lari MIP transflective klasik favorit atlet marathon."),
        DeviceProfile(id: "garmin_fr_165", manufacturer: "Garmin", modelName: "Forerunner 165", manufacturerId: 1, productNumber: 5625, category: .runningWatch, supportedSports: [.running], description: "Jam AMOLED entry-level dengan fitur race adaptive training plans."),
        DeviceProfile(id: "garmin_enduro_3", manufacturer: "Garmin", modelName: "Enduro 3", manufacturerId: 1, productNumber: 4521, category: .runningWatch, supportedSports: [.running, .hiking], description: "Ultra-endurance smartwatch untuk lomba lari trail 100 mil."),

        // APPLE WATCH
        DeviceProfile(id: "apple_watch_ultra_2", manufacturer: "Apple", modelName: "Apple Watch Ultra 2", manufacturerId: 255, productNumber: 1002, category: .runningWatch, supportedSports: [.running, .hiking], description: "Titanium case 49mm dengan dual-frequency GPS dan layar 3000 nits."),
        DeviceProfile(id: "apple_watch_s10", manufacturer: "Apple", modelName: "Apple Watch Series 10", manufacturerId: 255, productNumber: 1010, category: .runningWatch, supportedSports: [.running], description: "Desain tertipis dengan layar OLED sudut pandang lebar untuk workout lari."),
        DeviceProfile(id: "apple_watch_s9", manufacturer: "Apple", modelName: "Apple Watch Series 9", manufacturerId: 255, productNumber: 1001, category: .runningWatch, supportedSports: [.running], description: "Apple Watch dengan sensor heart rate generasi ke-3."),

        // COROS
        DeviceProfile(id: "coros_pace_pro", manufacturer: "COROS", modelName: "PACE Pro AMOLED", manufacturerId: 281, productNumber: 26, category: .runningWatch, supportedSports: [.running], description: "Jam tangan lari flagship terbaru COROS berlayar AMOLED 1500 nits."),
        DeviceProfile(id: "coros_pace_3", manufacturer: "COROS", modelName: "PACE 3", manufacturerId: 281, productNumber: 23, category: .runningWatch, supportedSports: [.running], description: "Jam paling populer di kalangan pelari maraton kalcer: bobot hanya 30 gram."),
        DeviceProfile(id: "coros_apex_2_pro", manufacturer: "COROS", modelName: "APEX 2 Pro", manufacturerId: 281, productNumber: 21, category: .runningWatch, supportedSports: [.running, .hiking], description: "Jam ketahanan trail & road maraton dengan bezel titanium grade 5."),

        // SUUNTO
        DeviceProfile(id: "suunto_race", manufacturer: "Suunto", modelName: "Race AMOLED", manufacturerId: 23, productNumber: 101, category: .runningWatch, supportedSports: [.running], description: "Finnish premium running watch dengan HRV recovery measurement."),
        DeviceProfile(id: "suunto_race_s", manufacturer: "Suunto", modelName: "Race S", manufacturerId: 23, productNumber: 103, category: .runningWatch, supportedSports: [.running], description: "Versi ringan dan ringkas dengan akurasi GPS multi-band teruji."),

        // POLAR
        DeviceProfile(id: "polar_vantage_v3", manufacturer: "Polar", modelName: "Vantage V3", manufacturerId: 123, productNumber: 301, category: .runningWatch, supportedSports: [.running], description: "Biosensing sports watch dengan ECG, SpO2, dan skin temperature sensor."),
        DeviceProfile(id: "polar_pacer_pro", manufacturer: "Polar", modelName: "Pacer Pro", manufacturerId: 123, productNumber: 205, category: .runningWatch, supportedSports: [.running], description: "Advanced running watch berbobot 41g dengan barometer akurat."),

        // SAMSUNG
        DeviceProfile(id: "samsung_galaxy_watch_ultra", manufacturer: "Samsung", modelName: "Galaxy Watch Ultra", manufacturerId: 255, productNumber: 701, category: .runningWatch, supportedSports: [.running, .hiking], description: "Smartwatch titanium tahan banting dengan dual-GPS & multi-sport tracker."),

        // =====================================================================
        // OUTDOOR & HIKING (Semua Brand Terkenal)
        // =====================================================================
        DeviceProfile(id: "garmin_fenix_8_solar", manufacturer: "Garmin", modelName: "Fenix 8 Solar", manufacturerId: 1, productNumber: 5932, category: .outdoorWatch, supportedSports: [.hiking], description: "Multisport outdoor watch paling mutakhir dengan mic, speaker, dan sensor dive."),
        DeviceProfile(id: "garmin_fenix_7_pro", manufacturer: "Garmin", modelName: "Fenix 7 Pro Sapphire Solar", manufacturerId: 1, productNumber: 4626, category: .outdoorWatch, supportedSports: [.hiking], description: "Peta topografi bawaan dan senter LED terintegrasi untuk ekspedisi."),
        DeviceProfile(id: "garmin_epix_pro", manufacturer: "Garmin", modelName: "Epix Pro (Gen 2)", manufacturerId: 1, productNumber: 4375, category: .outdoorWatch, supportedSports: [.hiking], description: "AMOLED outdoor watch dengan kontur bukit dan navigasi NextFork."),
        DeviceProfile(id: "garmin_instinct_2x", manufacturer: "Garmin", modelName: "Instinct 2X Solar", manufacturerId: 1, productNumber: 4378, category: .outdoorWatch, supportedSports: [.hiking], description: "Jam rugged standar militer MIL-STD-810 dengan daya tahan baterai tanpa batas."),
        DeviceProfile(id: "suunto_vertical", manufacturer: "Suunto", modelName: "Vertical Solar", manufacturerId: 23, productNumber: 102, category: .outdoorWatch, supportedSports: [.hiking], description: "Peta offline gratis 3D dengan akurasi ketinggian barometrik superior."),
        DeviceProfile(id: "coros_vertix_2s", manufacturer: "COROS", modelName: "VERTIX 2S", manufacturerId: 281, productNumber: 25, category: .outdoorWatch, supportedSports: [.hiking], description: "Jam ekspedisi gunung tertinggi di dunia dengan baterai GPS 118 jam."),
        DeviceProfile(id: "casio_gshock_gpr_h1000", manufacturer: "Casio", modelName: "G-Shock Rangeman GPR-H1000", manufacturerId: 255, productNumber: 1000, category: .outdoorWatch, supportedSports: [.hiking], description: "Survival watch tangguh dengan GPS & 6 sensor Polar powered.")
    ]
    
    public static var defaultProfiles: [DeviceProfile] {
        allProfiles
    }
    
    public static func profiles(for sport: SportType) -> [DeviceProfile] {
        let matching = allProfiles.filter { $0.supportedSports.contains(sport) }
        return matching.isEmpty ? allProfiles : matching
    }
    
    public static func brands(for sport: SportType) -> [String] {
        let sportsProfiles = profiles(for: sport)
        var seen = Set<String>()
        var result: [String] = []
        let preferred = ["Garmin", "Wahoo", "Hammerhead", "Apple", "COROS", "Bryton", "Magene", "iGPSPORT", "Polar", "Suunto", "Stages", "SRM", "SIGMA", "Samsung", "Casio"]
        for p in preferred {
            if sportsProfiles.contains(where: { $0.manufacturer.caseInsensitiveCompare(p) == .orderedSame }) {
                seen.insert(p.lowercased())
                result.append(p)
            }
        }
        for dev in sportsProfiles {
            let m = dev.manufacturer
            if !seen.contains(m.lowercased()) {
                seen.insert(m.lowercased())
                result.append(m)
            }
        }
        return result
    }
    
    public static func profiles(for sport: SportType, brand: String) -> [DeviceProfile] {
        let matching = profiles(for: sport).filter { $0.manufacturer.caseInsensitiveCompare(brand) == .orderedSame }
        return matching.isEmpty ? profiles(for: sport) : matching
    }
    
    public static func defaultProfile(for sport: SportType) -> DeviceProfile {
        profiles(for: sport).first ?? allProfiles[0]
    }
    
    public static func findProfile(byId id: String) -> DeviceProfile {
        allProfiles.first(where: { $0.id == id }) ?? allProfiles[0]
    }
}
