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
    // Official Garmin FIT SDK Manufacturer IDs:
    // Garmin = 1, Wahoo = 32, COROS = 294, Hammerhead = 289, Bryton = 267, iGPSPORT = 115, Magene = 107, Suunto = 23, Polar = 123, Sigma = 70, Lezyne = 258, Stages = 69, Apple/Samsung = 255
    public static let allProfiles: [DeviceProfile] = [
        // =====================================================================
        // PART A: CYCLOCOMP — FOR CYCLING
        // =====================================================================
        // GARMIN (Mfr: 1)
        DeviceProfile(id: "garmin_edge_1050", manufacturer: "Garmin", modelName: "Edge 1050", manufacturerId: 1, productNumber: 4440, category: .cyclocomputer, supportedSports: [.cycling], description: "Flagship LCD cycling computer with multi-band GNSS."),
        DeviceProfile(id: "garmin_edge_mtb", manufacturer: "Garmin", modelName: "Edge MTB", manufacturerId: 1, productNumber: 4540, category: .cyclocomputer, supportedSports: [.cycling], description: "Rugged 5Hz GPS mountain bike cyclocomputer."),
        DeviceProfile(id: "garmin_edge_1040_solar", manufacturer: "Garmin", modelName: "Edge 1040 Solar", manufacturerId: 1, productNumber: 3843, category: .cyclocomputer, supportedSports: [.cycling], description: "Solar-charging cyclocomputer with multi-band GNSS."),
        DeviceProfile(id: "garmin_edge_840", manufacturer: "Garmin", modelName: "Edge 840 Solar", manufacturerId: 1, productNumber: 4062, category: .cyclocomputer, supportedSports: [.cycling], description: "Touchscreen and button performance bike computer."),
        DeviceProfile(id: "garmin_edge_540", manufacturer: "Garmin", modelName: "Edge 540 Solar", manufacturerId: 1, productNumber: 4061, category: .cyclocomputer, supportedSports: [.cycling], description: "Button-operated pro peloton cycling computer."),
        DeviceProfile(id: "garmin_edge_1030_plus", manufacturer: "Garmin", modelName: "Edge 1030 Plus", manufacturerId: 1, productNumber: 3570, category: .cyclocomputer, supportedSports: [.cycling], description: "Large display endurance bike computer."),
        DeviceProfile(id: "garmin_edge_830", manufacturer: "Garmin", modelName: "Edge 830", manufacturerId: 1, productNumber: 3122, category: .cyclocomputer, supportedSports: [.cycling], description: "Compact touchscreen cycling computer."),
        DeviceProfile(id: "garmin_edge_530", manufacturer: "Garmin", modelName: "Edge 530", manufacturerId: 1, productNumber: 3121, category: .cyclocomputer, supportedSports: [.cycling], description: "Performance GPS bike computer."),
        DeviceProfile(id: "garmin_edge_130_plus", manufacturer: "Garmin", modelName: "Edge 130 Plus", manufacturerId: 1, productNumber: 3558, category: .cyclocomputer, supportedSports: [.cycling], description: "Ultralight compact monochrome cycling computer."),
        DeviceProfile(id: "garmin_edge_explore_2", manufacturer: "Garmin", modelName: "Edge Explore 2", manufacturerId: 1, productNumber: 4169, category: .cyclocomputer, supportedSports: [.cycling], description: "Touring navigation GPS cycling computer."),

        // WAHOO FITNESS (Mfr: 32)
        DeviceProfile(id: "wahoo_elemnt_ace", manufacturer: "Wahoo", modelName: "ELEMNT ACE", manufacturerId: 32, productNumber: 52, category: .cyclocomputer, supportedSports: [.cycling], description: "Flagship Wahoo bike computer with integrated wind sensor."),
        DeviceProfile(id: "wahoo_elemnt_roam_v3", manufacturer: "Wahoo", modelName: "ELEMNT ROAM 3", manufacturerId: 32, productNumber: 54, category: .cyclocomputer, supportedSports: [.cycling], description: "Gen 3 dual-band GPS cycling computer."),
        DeviceProfile(id: "wahoo_elemnt_bolt_v3", manufacturer: "Wahoo", modelName: "ELEMNT BOLT 3", manufacturerId: 32, productNumber: 53, category: .cyclocomputer, supportedSports: [.cycling], description: "Gen 3 aero race GPS bike computer."),
        DeviceProfile(id: "wahoo_elemnt_roam_v2", manufacturer: "Wahoo", modelName: "ELEMNT ROAM v2", manufacturerId: 32, productNumber: 47, category: .cyclocomputer, supportedSports: [.cycling], description: "Dual-band GPS with 64-color screen."),
        DeviceProfile(id: "wahoo_elemnt_bolt_v2", manufacturer: "Wahoo", modelName: "ELEMNT BOLT v2", manufacturerId: 32, productNumber: 41, category: .cyclocomputer, supportedSports: [.cycling], description: "Aero-optimized compact bike computer."),

        // COROS CYCLOCOMP (Mfr: 294)
        DeviceProfile(id: "coros_dura", manufacturer: "COROS", modelName: "DURA", manufacturerId: 294, productNumber: 851, category: .cyclocomputer, supportedSports: [.cycling], description: "Solar-powered ultra-endurance dual-frequency GPS bike computer."),

        // HAMMERHEAD (Mfr: 289)
        DeviceProfile(id: "hammerhead_karoo_3", manufacturer: "Hammerhead", modelName: "Karoo 3", manufacturerId: 289, productNumber: 3, category: .cyclocomputer, supportedSports: [.cycling], description: "High-definition display cycling computer with SRAM AXS."),
        DeviceProfile(id: "hammerhead_karoo_2", manufacturer: "Hammerhead", modelName: "Karoo 2", manufacturerId: 289, productNumber: 2, category: .cyclocomputer, supportedSports: [.cycling], description: "Touchscreen navigation cycling computer."),

        // BRYTON (Mfr: 267)
        DeviceProfile(id: "bryton_rider_s800", manufacturer: "Bryton", modelName: "Rider S800", manufacturerId: 267, productNumber: 1908, category: .cyclocomputer, supportedSports: [.cycling], description: "Flagship 3.4-inch color touchscreen bike computer."),
        DeviceProfile(id: "bryton_rider_s510", manufacturer: "Bryton", modelName: "Rider S510", manufacturerId: 267, productNumber: 1912, category: .cyclocomputer, supportedSports: [.cycling], description: "Color touchscreen climbing cyclocomputer."),
        DeviceProfile(id: "bryton_rider_s500", manufacturer: "Bryton", modelName: "Rider S500", manufacturerId: 267, productNumber: 1906, category: .cyclocomputer, supportedSports: [.cycling], description: "Compact performance GPS cyclocomputer."),
        DeviceProfile(id: "bryton_rider_750_se", manufacturer: "Bryton", modelName: "Rider 750 SE", manufacturerId: 267, productNumber: 1905, category: .cyclocomputer, supportedSports: [.cycling], description: "Endurance touchscreen bike computer."),

        // iGPSPORT (Mfr: 115)
        DeviceProfile(id: "igpsport_binavi", manufacturer: "iGPSPORT", modelName: "BiNavi", manufacturerId: 115, productNumber: 810, category: .cyclocomputer, supportedSports: [.cycling], description: "3.5-inch flagship navigation bike computer."),
        DeviceProfile(id: "igpsport_igs800", manufacturer: "iGPSPORT", modelName: "iGS800", manufacturerId: 115, productNumber: 800, category: .cyclocomputer, supportedSports: [.cycling], description: "Touchscreen dual-band GNSS bike computer."),
        DeviceProfile(id: "igpsport_igs630s", manufacturer: "iGPSPORT", modelName: "iGS630S", manufacturerId: 115, productNumber: 631, category: .cyclocomputer, supportedSports: [.cycling], description: "Dual-frequency color navigation bike computer."),
        DeviceProfile(id: "igpsport_igs630", manufacturer: "iGPSPORT", modelName: "iGS630", manufacturerId: 115, productNumber: 630, category: .cyclocomputer, supportedSports: [.cycling], description: "Color map navigation cycling computer."),
        DeviceProfile(id: "igpsport_bsc300", manufacturer: "iGPSPORT", modelName: "BSC300", manufacturerId: 115, productNumber: 300, category: .cyclocomputer, supportedSports: [.cycling], description: "Ultra-slim color bike computer."),

        // MAGENE (Mfr: 107)
        DeviceProfile(id: "magene_c606", manufacturer: "Magene", modelName: "C606", manufacturerId: 107, productNumber: 606, category: .cyclocomputer, supportedSports: [.cycling], description: "Smart touchscreen GPS bike computer."),
        DeviceProfile(id: "magene_c506", manufacturer: "Magene", modelName: "C506", manufacturerId: 107, productNumber: 506, category: .cyclocomputer, supportedSports: [.cycling], description: "Compact color route navigation bike computer."),
        DeviceProfile(id: "magene_c406_pro", manufacturer: "Magene", modelName: "C406 Pro", manufacturerId: 107, productNumber: 407, category: .cyclocomputer, supportedSports: [.cycling], description: "2.4-inch smart GPS cycling computer."),

        // SIGMA, STAGES, LEZYNE
        DeviceProfile(id: "sigma_rox_12_1", manufacturer: "Sigma", modelName: "ROX 12.1 EVO", manufacturerId: 70, productNumber: 121, category: .cyclocomputer, supportedSports: [.cycling], description: "German engineered map navigation cycling computer."),
        DeviceProfile(id: "sigma_rox_11_1", manufacturer: "Sigma", modelName: "ROX 11.1 EVO", manufacturerId: 70, productNumber: 111, category: .cyclocomputer, supportedSports: [.cycling], description: "Compact ANT+ & BLE sensor bike computer."),
        DeviceProfile(id: "stages_dash_l200", manufacturer: "Stages", modelName: "Dash L200", manufacturerId: 69, productNumber: 200, category: .cyclocomputer, supportedSports: [.cycling], description: "Full-color power analysis cycling computer."),
        DeviceProfile(id: "lezyne_mega_xl", manufacturer: "Lezyne", modelName: "Mega XL GPS", manufacturerId: 258, productNumber: 10, category: .cyclocomputer, supportedSports: [.cycling], description: "48-hour battery GPS bike computer."),

        // =====================================================================
        // PART B: SPORTWATCH — FOR RUNNING & MULTISPORT
        // =====================================================================
        // COROS (Mfr: 294 — Verified Official Product IDs)
        DeviceProfile(id: "coros_pace_4", manufacturer: "COROS", modelName: "PACE 4", manufacturerId: 294, productNumber: 806, category: .runningWatch, supportedSports: [.running, .cycling], description: "Ultralight 32g AMOLED dual-frequency running watch with voice pins."),
        DeviceProfile(id: "coros_pace_pro", manufacturer: "COROS", modelName: "PACE Pro", manufacturerId: 294, productNumber: 805, category: .runningWatch, supportedSports: [.running, .cycling], description: "AMOLED performance marathon running watch."),
        DeviceProfile(id: "coros_pace_3", manufacturer: "COROS", modelName: "PACE 3", manufacturerId: 294, productNumber: 804, category: .runningWatch, supportedSports: [.running, .cycling], description: "30g dual-frequency GPS marathon running watch."),
        DeviceProfile(id: "coros_pace_2", manufacturer: "COROS", modelName: "PACE 2", manufacturerId: 294, productNumber: 802, category: .runningWatch, supportedSports: [.running, .cycling], description: "Lightweight road running GPS watch."),
        DeviceProfile(id: "coros_nomad", manufacturer: "COROS", modelName: "NOMAD", manufacturerId: 294, productNumber: 861, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Rugged adventure and trail GPS watch."),
        DeviceProfile(id: "coros_apex_2_pro", manufacturer: "COROS", modelName: "APEX 2 Pro", manufacturerId: 294, productNumber: 822, category: .runningWatch, supportedSports: [.running, .hiking, .cycling], description: "Titanium sapphire trail & marathon watch."),
        DeviceProfile(id: "coros_apex_2", manufacturer: "COROS", modelName: "APEX 2", manufacturerId: 294, productNumber: 821, category: .runningWatch, supportedSports: [.running, .hiking, .cycling], description: "Sapphire titanium outdoor multisport watch."),
        DeviceProfile(id: "coros_vertix_2s", manufacturer: "COROS", modelName: "VERTIX 2S", manufacturerId: 294, productNumber: 833, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "High-altitude adventure watch with dual-frequency GNSS."),

        // GARMIN FORERUNNER, FENIX & ENDURO (Mfr: 1)
        DeviceProfile(id: "garmin_fr_970", manufacturer: "Garmin", modelName: "Forerunner 970", manufacturerId: 1, productNumber: 4565, category: .runningWatch, supportedSports: [.running, .cycling], description: "Flagship AMOLED triathlon running watch with sapphire lens & LED flashlight."),
        DeviceProfile(id: "garmin_fr_570", manufacturer: "Garmin", modelName: "Forerunner 570", manufacturerId: 1, productNumber: 4570, category: .runningWatch, supportedSports: [.running, .cycling], description: "AMOLED performance GPS running watch."),
        DeviceProfile(id: "garmin_fr_965", manufacturer: "Garmin", modelName: "Forerunner 965", manufacturerId: 1, productNumber: 4315, category: .runningWatch, supportedSports: [.running, .cycling], description: "AMOLED running watch with titanium bezel & full maps."),
        DeviceProfile(id: "garmin_fr_955", manufacturer: "Garmin", modelName: "Forerunner 955 Solar", manufacturerId: 1, productNumber: 4024, category: .runningWatch, supportedSports: [.running, .cycling], description: "Solar marathon watch with multi-band GNSS."),
        DeviceProfile(id: "garmin_fr_265", manufacturer: "Garmin", modelName: "Forerunner 265", manufacturerId: 1, productNumber: 4257, category: .runningWatch, supportedSports: [.running, .cycling], description: "AMOLED running watch with training readiness."),
        DeviceProfile(id: "garmin_fr_255", manufacturer: "Garmin", modelName: "Forerunner 255 Music", manufacturerId: 1, productNumber: 3990, category: .runningWatch, supportedSports: [.running, .cycling], description: "Lightweight MIP marathon running watch."),
        DeviceProfile(id: "garmin_fr_165", manufacturer: "Garmin", modelName: "Forerunner 165", manufacturerId: 1, productNumber: 4432, category: .runningWatch, supportedSports: [.running], description: "AMOLED GPS running watch."),
        DeviceProfile(id: "garmin_fenix_8_amoled", manufacturer: "Garmin", modelName: "fēnix 8", manufacturerId: 1, productNumber: 4536, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Flagship dive-rated AMOLED multisport GPS watch."),
        DeviceProfile(id: "garmin_fenix_8_solar", manufacturer: "Garmin", modelName: "fēnix 8 Solar", manufacturerId: 1, productNumber: 4532, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Solar sapphire flagship outdoor GPS watch."),
        DeviceProfile(id: "garmin_enduro_3", manufacturer: "Garmin", modelName: "Enduro 3", manufacturerId: 1, productNumber: 4575, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Ultra-endurance 100-mile trail running watch."),
        DeviceProfile(id: "garmin_fenix_7_pro", manufacturer: "Garmin", modelName: "fēnix 7 Pro", manufacturerId: 1, productNumber: 4375, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Solar sapphire multisport watch with integrated flashlight."),
        DeviceProfile(id: "garmin_epix_pro", manufacturer: "Garmin", modelName: "epix Pro (Gen 2)", manufacturerId: 1, productNumber: 4314, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "High-performance AMOLED outdoor watch."),
        DeviceProfile(id: "garmin_instinct_2x", manufacturer: "Garmin", modelName: "Instinct 2X Solar", manufacturerId: 1, productNumber: 4394, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Rugged MIL-STD-810 solar GPS watch."),

        // SUUNTO (Mfr: 23)
        DeviceProfile(id: "suunto_run", manufacturer: "Suunto", modelName: "Suunto Run", manufacturerId: 23, productNumber: 98, category: .runningWatch, supportedSports: [.running], description: "Ultralight 36g AMOLED marathon running watch."),
        DeviceProfile(id: "suunto_race", manufacturer: "Suunto", modelName: "Suunto Race", manufacturerId: 23, productNumber: 95, category: .runningWatch, supportedSports: [.running, .cycling], description: "AMOLED titanium performance watch."),
        DeviceProfile(id: "suunto_race_s", manufacturer: "Suunto", modelName: "Suunto Race S", manufacturerId: 23, productNumber: 96, category: .runningWatch, supportedSports: [.running, .cycling], description: "Compact multi-band GPS running watch."),
        DeviceProfile(id: "suunto_vertical", manufacturer: "Suunto", modelName: "Suunto Vertical", manufacturerId: 23, productNumber: 92, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Offline maps solar adventure watch."),

        // POLAR (Mfr: 123)
        DeviceProfile(id: "polar_vantage_v3", manufacturer: "Polar", modelName: "Vantage V3", manufacturerId: 123, productNumber: 210, category: .runningWatch, supportedSports: [.running, .cycling], description: "Biosensing AMOLED multisport watch."),
        DeviceProfile(id: "polar_grit_x2_pro", manufacturer: "Polar", modelName: "Grit X2 Pro", manufacturerId: 123, productNumber: 225, category: .outdoorWatch, supportedSports: [.running, .hiking, .cycling], description: "Sapphire AMOLED outdoor watch."),
        DeviceProfile(id: "polar_pacer_pro", manufacturer: "Polar", modelName: "Pacer Pro", manufacturerId: 123, productNumber: 195, category: .runningWatch, supportedSports: [.running], description: "Ultralight running watch with barometer."),

        // APPLE & SAMSUNG (Mfr: 255)
        DeviceProfile(id: "apple_watch_ultra_2", manufacturer: "Apple", modelName: "Watch Ultra 2", manufacturerId: 255, productNumber: 802, category: .runningWatch, supportedSports: [.running, .hiking, .cycling], description: "49mm titanium case with dual-frequency L1+L5 GPS."),
        DeviceProfile(id: "apple_watch_s10", manufacturer: "Apple", modelName: "Watch Series 10", manufacturerId: 255, productNumber: 810, category: .runningWatch, supportedSports: [.running, .cycling], description: "Wide-angle OLED running watch."),
        DeviceProfile(id: "samsung_galaxy_watch_ultra", manufacturer: "Samsung", modelName: "Galaxy Watch Ultra", manufacturerId: 255, productNumber: 820, category: .runningWatch, supportedSports: [.running, .hiking, .cycling], description: "Titanium dual-GPS multisport watch.")
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
        let preferred = ["COROS", "Garmin", "Wahoo", "Hammerhead", "Bryton", "iGPSPORT", "Magene", "Suunto", "Polar", "Apple", "Sigma", "Stages", "Lezyne", "Samsung"]
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
        if sport == .running {
            return allProfiles.first(where: { $0.id == "coros_pace_4" }) ?? profiles(for: sport).first ?? allProfiles[0]
        }
        return allProfiles.first(where: { $0.id == "garmin_edge_1040_solar" }) ?? profiles(for: sport).first ?? allProfiles[0]
    }
    
    public static func findProfile(byId id: String) -> DeviceProfile {
        allProfiles.first(where: { $0.id == id }) ?? allProfiles[0]
    }
}
