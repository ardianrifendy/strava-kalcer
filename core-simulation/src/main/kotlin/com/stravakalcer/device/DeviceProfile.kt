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
    val includeCadence: Boolean = true
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
        DeviceProfile("garmin_edge_1040", "Garmin", "Edge 1040 Solar", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_1040, "Garmin flagship cycling computer with multi-band GNSS."),
        DeviceProfile("garmin_edge_840", "Garmin", "Edge 840", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_840, "Performance cycling computer with touchscreen and physical buttons."),
        DeviceProfile("garmin_edge_540", "Garmin", "Edge 540", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_540, "Button-operated performance cycling computer."),
        DeviceProfile("garmin_edge_1030_plus", "Garmin", "Edge 1030 Plus", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_1030_PLUS, "Large screen high-end touring and performance cycling computer."),
        DeviceProfile("garmin_edge_830", "Garmin", "Edge 830", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_830, "Touchscreen performance cycling computer with dynamic metrics."),
        DeviceProfile("garmin_edge_530", "Garmin", "Edge 530", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.GARMIN, GarminProduct.EDGE_530, "Popular button-controlled performance bike computer."),

        // Wahoo Bike Computers (Wahoo Fitness = 32, Official ANT FIT Profile IDs: ROAM = 1163, BOLT = 1164)
        DeviceProfile("wahoo_elemnt_roam", "Wahoo", "ELEMNT ROAM v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.WAHOO_FITNESS, 1163, "Large color screen GPS bike computer with smart navigation."),
        DeviceProfile("wahoo_elemnt_bolt", "Wahoo", "ELEMNT BOLT v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.WAHOO_FITNESS, 1164, "Aerodynamic compact GPS cycling computer."),

        // Hammerhead (Hammerhead = 289)
        DeviceProfile("hammerhead_karoo_3", "Hammerhead", "Karoo (3rd Gen)", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.HAMMERHEAD, 3, "High-definition display cycling computer running Android core."),
        DeviceProfile("hammerhead_karoo_2", "Hammerhead", "Karoo 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.HAMMERHEAD, 2, "Smartphone-quality touchscreen cycling computer."),

        // Bryton (Bryton = 267)
        DeviceProfile("bryton_rider_s800", "Bryton", "Rider S800", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.BRYTON, 800, "Flagship color touch cycling computer."),
        DeviceProfile("bryton_rider_750", "Bryton", "Rider 750 SE", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.BRYTON, 750, "Endurance GPS cycling computer with voice search."),

        // iGPSPORT (iGPSPORT = 115)
        DeviceProfile("igpsport_igs630", "iGPSPORT", "iGS630", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 630, "Color display full GNSS cycling computer."),
        DeviceProfile("igpsport_bsc300", "iGPSPORT", "BSC300", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.IGPSPORT, 300, "Ultra-thin smart cycling computer with color navigation."),

        // Magene (Magene = 107)
        DeviceProfile("magene_c606", "Magene", "C606 Smart GPS", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.MAGENE, 606, "Smart cycling computer with color touch screen and WiFi sync."),
        DeviceProfile("magene_c406", "Magene", "C406 Pro", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.MAGENE, 406, "Affordable aerodynamic FSTN screen bike computer."),

        // XOSS (Standard Development / Custom = 255)
        DeviceProfile("xoss_nav", "XOSS", "NAV Smart GPS", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.DEVELOPMENT, 101, "Route guidance navigation bike computer."),
        DeviceProfile("xoss_g_plus", "XOSS", "G+ Gen 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.DEVELOPMENT, 102, "Entry level GPS speedometer."),

        // Sigma (Sigma Sport = 70)
        DeviceProfile("sigma_rox_12_1", "Sigma", "ROX 12.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.SIGMASPORT, 12, "Full-map navigation cycling computer made in Germany."),
        DeviceProfile("sigma_rox_11_1", "Sigma", "ROX 11.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), Manufacturer.SIGMASPORT, 11, "Compact training and sensor computer."),

        // Garmin Watches (Official GarminProduct IDs)
        DeviceProfile("garmin_forerunner_965", "Garmin", "Forerunner 965", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR965, "Premium AMOLED running and triathlon GPS smartwatch."),
        DeviceProfile("garmin_forerunner_265", "Garmin", "Forerunner 265", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR265_LARGE, "Lightweight AMOLED running smartwatch with training readiness."),
        DeviceProfile("garmin_forerunner_955", "Garmin", "Forerunner 955", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR955, "Triathlon solar multi-sport watch with dual-frequency satellite."),
        DeviceProfile("garmin_forerunner_255", "Garmin", "Forerunner 255", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FR255, "Essential GPS running smartwatch with HRV status."),
        DeviceProfile("garmin_fenix_7_pro", "Garmin", "Fenix 7 Pro Sapphire Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FENIX7_PRO_SOLAR, "Multisport endurance smartwatch with solar charging and flashlight."),
        DeviceProfile("garmin_fenix_7", "Garmin", "Fenix 7", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.FENIX7, "Rugged multisport adventure GPS smartwatch."),
        DeviceProfile("garmin_epix_pro", "Garmin", "Epix Pro (Gen 2)", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.EPIX_GEN2_PRO_47, "High-performance AMOLED outdoor smartwatch."),
        DeviceProfile("garmin_instinct_2x", "Garmin", "Instinct 2X Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.INSTINCT_2X, "Rugged military-standard outdoor GPS watch with solar charging."),
        DeviceProfile("garmin_enduro_2", "Garmin", "Enduro 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.GARMIN, GarminProduct.ENDURO2, "Ultra-endurance multisport GPS watch with extreme battery life."),

        // COROS Watches (COROS = 294)
        DeviceProfile("coros_pace_3", "COROS", "Pace 3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 30, "Ultra-lightweight high-precision running GPS watch (30g)."),
        DeviceProfile("coros_apex_2_pro", "COROS", "Apex 2 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 21, "Titanium outdoor multisport GPS watch."),
        DeviceProfile("coros_vertix_2", "COROS", "Vertix 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.COROS, 22, "Extreme adventure GPS watch with dual-frequency satellite."),

        // Suunto Watches (Suunto = 23)
        DeviceProfile("suunto_race", "Suunto", "Race", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.SUUNTO, 100, "AMOLED titanium performance watch with HRV recovery."),
        DeviceProfile("suunto_vertical", "Suunto", "Vertical Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.SUUNTO, 101, "Adventure watch with free offline global maps and solar battery."),

        // Polar Watches (Polar = 123)
        DeviceProfile("polar_vantage_v3", "Polar", "Vantage V3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.POLAR_ELECTRO, 80, "Premium multisport watch with biosensing technologies."),
        DeviceProfile("polar_pacer_pro", "Polar", "Pacer Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), Manufacturer.POLAR_ELECTRO, 81, "Ultralight advanced running watch with barometer."),

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
