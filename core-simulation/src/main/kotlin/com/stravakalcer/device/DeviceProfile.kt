package com.stravakalcer.device

import com.garmin.fit.GarminProduct
import com.garmin.fit.Manufacturer
import com.stravakalcer.model.SportType

enum class DeviceCategory(val displayName: String) {
    CYCLOCOMPUTER("Bike Computer"),
    SPORTWATCH("GPS Watch"),
    GENERIC("Generic / Custom")
}

data class DeviceProfile(
    val id: String,
    val manufacturer: String,
    val modelName: String,
    val category: DeviceCategory,
    val supportedSports: Set<SportType>,
    val manufacturerId: Int, // FIT manufacturer ID code
    val productNumber: Int,  // FIT product ID code
    val description: String,
    val recordingIntervalSeconds: Int = 1,
    val includeSpeed: Boolean = true,
    val includeDistance: Boolean = true,
    val includeAltitude: Boolean = true,
    val includeHeartRate: Boolean = true,
    val includeCadence: Boolean = true,
    val includePower: Boolean = true,
    val includeTemperature: Boolean = true
) {
    val formattedDeviceName: String
        get() = if (modelName.startsWith(manufacturer, ignoreCase = true)) {
            modelName
        } else {
            "$manufacturer $modelName"
        }
}

object DeviceProfileRegistry {

    private val profiles = listOf(
        // Garmin Bike Computers (Garmin = 1)
        DeviceProfile("garmin_edge_1050", "Garmin", "Edge 1050", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, 4440, "Garmin flagship LCD cycling computer with multi-band GNSS."),
        DeviceProfile("garmin_edge_1040", "Garmin", "Edge 1040 Solar", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_1040, "Garmin flagship solar cycling computer with multi-band GNSS."),
        DeviceProfile("garmin_edge_840", "Garmin", "Edge 840 Solar", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_840, "Performance cycling computer with touchscreen and physical buttons."),
        DeviceProfile("garmin_edge_540", "Garmin", "Edge 540 Solar", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_540, "Button-operated performance cycling computer."),
        DeviceProfile("garmin_edge_1030_plus", "Garmin", "Edge 1030 Plus", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_1030_PLUS, "Large screen high-end touring and performance cycling computer."),
        DeviceProfile("garmin_edge_830", "Garmin", "Edge 830", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_830, "Touchscreen performance cycling computer with dynamic metrics."),
        DeviceProfile("garmin_edge_530", "Garmin", "Edge 530", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_530, "Popular button-controlled performance bike computer."),
        DeviceProfile("garmin_edge_explore_2", "Garmin", "Edge Explore 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_EXPLORE2, "Touring and e-bike navigation GPS cycling computer."),
        DeviceProfile("garmin_edge_130_plus", "Garmin", "Edge 130 Plus", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_130_PLUS, "Compact lightweight GPS cycling computer."),

        // Wahoo Bike Computers (Wahoo Fitness = 32, Official Wahoo FIT Product IDs: ACE = 52, ROAM v2 = 47, BOLT v2 = 41, ROAM v1 = 31, BOLT v1 = 28)
        DeviceProfile("wahoo_elemnt_ace", "Wahoo", "ELEMNT ACE", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.WAHOO_FITNESS, 52, "Flagship Wahoo wind-sensor touchscreen GPS bike computer."),
        DeviceProfile("wahoo_elemnt_roam", "Wahoo", "ELEMNT ROAM v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.WAHOO_FITNESS, 47, "Large color screen dual-band GPS bike computer with smart navigation."),
        DeviceProfile("wahoo_elemnt_bolt", "Wahoo", "ELEMNT BOLT v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.WAHOO_FITNESS, 41, "Aerodynamic compact color GPS cycling computer."),

        // COROS Bike Computer (COROS = 294, Verified DURA Product ID = 851)
        DeviceProfile("coros_dura", "COROS", "DURA", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.COROS, 851, "Solar-powered ultra-endurance dual-frequency GPS bike computer."),

        // Hammerhead (Hammerhead = 289)
        DeviceProfile("hammerhead_karoo_3", "Hammerhead", "Karoo 3", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.HAMMERHEAD, 3, "High-definition display cycling computer running Android core."),
        DeviceProfile("hammerhead_karoo_2", "Hammerhead", "Karoo 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.HAMMERHEAD, 2, "Smartphone-quality touchscreen cycling computer."),

        // Bryton (Bryton = 267)
        DeviceProfile("bryton_rider_s800", "Bryton", "Rider S800", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.BRYTON, 1908, "Flagship color touch cycling computer."),
        DeviceProfile("bryton_rider_s500", "Bryton", "Rider S500", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.BRYTON, 1906, "Compact performance color touchscreen cycling computer."),
        DeviceProfile("bryton_rider_750", "Bryton", "Rider 750 SE", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.BRYTON, 1905, "Endurance GPS cycling computer with voice search."),

        // iGPSPORT (iGPSPORT = 115)
        DeviceProfile("igpsport_igs800", "iGPSPORT", "iGS800", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 800, "Flagship touchscreen dual-band GNSS cycling computer."),
        DeviceProfile("igpsport_igs630s", "iGPSPORT", "iGS630S", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 631, "Dual-frequency color navigation cycling computer."),
        DeviceProfile("igpsport_igs630", "iGPSPORT", "iGS630", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 630, "Color display full GNSS cycling computer."),
        DeviceProfile("igpsport_bsc300", "iGPSPORT", "BSC300", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 300, "Ultra-thin smart cycling computer with color navigation."),

        // Magene (Magene = 107)
        DeviceProfile("magene_c606", "Magene", "C606", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.MAGENE, 606, "Smart cycling computer with color touch screen and WiFi sync."),
        DeviceProfile("magene_c506", "Magene", "C506", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.MAGENE, 506, "Compact color route navigation smart GPS bike computer."),
        DeviceProfile("magene_c406", "Magene", "C406 Pro", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.MAGENE, 407, "Aerodynamic FSTN screen bike computer."),

        // Coospo & Cycplus
        DeviceProfile("coospo_cs500", "Coospo", "CS500", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.COOSPO, 500, "Smart navigation cycling computer."),
        DeviceProfile("cycplus_m1", "Cycplus", "M1", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.CYCPLUS, 101, "ANT+ power & cadence GPS cycling computer."),

        // Sigma (Sigma Sport = 70)
        DeviceProfile("sigma_rox_12_1", "Sigma", "ROX 12.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.SIGMASPORT, 121, "Full-map navigation cycling computer made in Germany."),
        DeviceProfile("sigma_rox_11_1", "Sigma", "ROX 11.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.SIGMASPORT, 111, "Compact training and sensor computer."),

        // Garmin Watches (Official GarminProduct IDs)
        DeviceProfile("garmin_forerunner_965", "Garmin", "Forerunner 965", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR965, "Premium AMOLED running and triathlon GPS smartwatch."),
        DeviceProfile("garmin_forerunner_265", "Garmin", "Forerunner 265", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR265_LARGE, "Lightweight AMOLED running smartwatch with training readiness."),
        DeviceProfile("garmin_forerunner_165", "Garmin", "Forerunner 165", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, 4432, "AMOLED GPS running smartwatch."),
        DeviceProfile("garmin_forerunner_955", "Garmin", "Forerunner 955", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR955, "Triathlon solar multi-sport watch with dual-frequency satellite."),
        DeviceProfile("garmin_forerunner_255", "Garmin", "Forerunner 255", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR255, "Essential GPS running smartwatch with HRV status."),
        DeviceProfile("garmin_fenix_8", "Garmin", "Fenix 8 AMOLED", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, 4536, "Flagship dive-rated AMOLED multisport GPS smartwatch."),
        DeviceProfile("garmin_fenix_7_pro", "Garmin", "Fenix 7 Pro Sapphire Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FENIX7_PRO_SOLAR, "Multisport endurance smartwatch with solar charging and flashlight."),
        DeviceProfile("garmin_fenix_7", "Garmin", "Fenix 7", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FENIX7, "Rugged multisport adventure GPS smartwatch."),
        DeviceProfile("garmin_epix_pro", "Garmin", "Epix Pro (Gen 2)", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.EPIX_GEN2_PRO_47, "High-performance AMOLED outdoor smartwatch."),
        DeviceProfile("garmin_instinct_2x", "Garmin", "Instinct 2X Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.INSTINCT_2X, "Rugged military-standard outdoor GPS watch with solar charging."),
        DeviceProfile("garmin_enduro_2", "Garmin", "Enduro 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.ENDURO2, "Ultra-endurance multisport GPS watch with extreme battery life."),

        // COROS Watches (COROS = 294 — Verified Official Product IDs: PACE = 801, PACE 2 = 802, PACE 3 = 804, PACE Pro = 805, PACE 4 = 806, APEX 2 = 821, APEX 2 Pro = 822, VERTIX 2 = 832, VERTIX 2S = 833, NOMAD = 861)
        DeviceProfile("coros_pace_4", "COROS", "PACE 4", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 806, "Ultra-lightweight 32g AMOLED dual-frequency running GPS watch with voice pins."),
        DeviceProfile("coros_pace_pro", "COROS", "PACE Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 805, "AMOLED performance marathon and running GPS watch."),
        DeviceProfile("coros_pace_3", "COROS", "PACE 3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 804, "Ultra-lightweight dual-frequency running GPS watch (30g)."),
        DeviceProfile("coros_pace_2", "COROS", "PACE 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 802, "Lightweight marathon running GPS watch."),
        DeviceProfile("coros_nomad", "COROS", "NOMAD", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 861, "Rugged adventure and trail GPS watch with action button."),
        DeviceProfile("coros_apex_2_pro", "COROS", "APEX 2 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 822, "Titanium outdoor multisport GPS watch."),
        DeviceProfile("coros_apex_2", "COROS", "APEX 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 821, "Sapphire titanium outdoor multisport GPS watch."),
        DeviceProfile("coros_vertix_2s", "COROS", "VERTIX 2S", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 833, "Extreme adventure GPS watch with dual-frequency satellite."),
        DeviceProfile("coros_vertix_2", "COROS", "VERTIX 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 832, "High-altitude expedition multisport GPS watch."),

        // Suunto Watches (Suunto = 23)
        DeviceProfile("suunto_race", "Suunto", "Race", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.SUUNTO, 95, "AMOLED titanium performance watch with HRV recovery."),
        DeviceProfile("suunto_vertical", "Suunto", "Vertical", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.SUUNTO, 92, "Adventure watch with free offline global maps and solar battery."),

        // Polar Watches (Polar = 123)
        DeviceProfile("polar_vantage_v3", "Polar", "Vantage V3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.POLAR_ELECTRO, 210, "Premium multisport watch with biosensing technologies."),
        DeviceProfile("polar_grit_x2_pro", "Polar", "Grit X2 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.POLAR_ELECTRO, 225, "Sapphire AMOLED outdoor multisport watch."),
        DeviceProfile("polar_pacer_pro", "Polar", "Pacer Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.POLAR_ELECTRO, 195, "Ultralight advanced running watch with barometer."),

        // Amazfit (Development / Custom = 255)
        DeviceProfile("amazfit_cheetah_pro", "Amazfit", "Cheetah Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.DEVELOPMENT, 501, "Dedicated marathon running watch with MaxTrack dual-band antenna."),

        // Apple & Samsung Watches (Development / Custom = 255)
        DeviceProfile("apple_watch_ultra_2", "Apple", "Watch Ultra 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.DEVELOPMENT, 2, "Rugged titanium smartwatch with precision dual-frequency GPS."),
        DeviceProfile("samsung_galaxy_watch_6", "Samsung", "Galaxy Watch 6 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.DEVELOPMENT, 6, "Wear OS sport smartwatch with personalized HR zones."),

        // Generic / Custom
        DeviceProfile("generic_fit", "Generic", "Standard FIT 2.0 Activity", DeviceCategory.GENERIC, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.DEVELOPMENT, 1, "Universal specification-compliant FIT activity file."),
        DeviceProfile("custom_profile", "Custom", "Custom FIT Profile", DeviceCategory.GENERIC, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.DEVELOPMENT, 99, "User-configured recording interval and sensor preferences.")
    )

    fun getAllProfiles(): List<DeviceProfile> = profiles

    fun getProfileById(id: String): DeviceProfile =
        profiles.firstOrNull { it.id == id } ?: profiles.first()

    fun filterProfiles(
        category: DeviceCategory? = null,
        sport: SportType? = null,
        searchQuery: String = ""
    ): List<DeviceProfile> {
        val query = searchQuery.trim().lowercase()
        return profiles.filter { profile ->
            (category == null || profile.category == category) &&
            (sport == null || profile.supportedSports.contains(sport)) &&
            (query.isEmpty() ||
             profile.manufacturer.lowercase().contains(query) ||
             profile.modelName.lowercase().contains(query) ||
             profile.description.lowercase().contains(query))
        }
    }
}
