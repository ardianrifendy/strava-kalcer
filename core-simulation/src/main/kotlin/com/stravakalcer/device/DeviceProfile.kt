package com.stravakalcer.device

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
)

object DeviceProfileRegistry {

    private val profiles = listOf(
        // Garmin Bike Computers (Garmin = 1)
        DeviceProfile("garmin_edge_1040", "Garmin", "Edge 1040 Solar", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 1, 3843, "Garmin flagship cycling computer with multi-band GNSS."),
        DeviceProfile("garmin_edge_840", "Garmin", "Edge 840", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 1, 3844, "Performance cycling computer with touchscreen and physical buttons."),
        DeviceProfile("garmin_edge_540", "Garmin", "Edge 540", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 1, 3845, "Button-operated performance cycling computer."),

        // Wahoo Bike Computers (Wahoo Fitness = 32)
        DeviceProfile("wahoo_elemnt_roam", "Wahoo", "ELEMNT ROAM v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 32, 28, "Large color screen GPS bike computer with smart navigation."),
        DeviceProfile("wahoo_elemnt_bolt", "Wahoo", "ELEMNT BOLT v2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 32, 27, "Aerodynamic compact GPS cycling computer."),

        // Hammerhead (SRAM / Hammerhead = 97)
        DeviceProfile("hammerhead_karoo_3", "Hammerhead", "Karoo (3rd Gen)", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 97, 3, "High-definition display cycling computer running Android core."),
        DeviceProfile("hammerhead_karoo_2", "Hammerhead", "Karoo 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 97, 2, "Smartphone-quality touchscreen cycling computer."),

        // Bryton (Bryton = 40)
        DeviceProfile("bryton_rider_s800", "Bryton", "Rider S800", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 40, 10, "Flagship color touch cycling computer."),
        DeviceProfile("bryton_rider_750", "Bryton", "Rider 750 SE", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 40, 11, "Endurance GPS cycling computer with voice search."),

        // iGPSPORT (iGPSPORT = 294)
        DeviceProfile("igpsport_igs630", "iGPSPORT", "iGS630", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 294, 630, "Color display full GNSS cycling computer."),
        DeviceProfile("igpsport_bsc300", "iGPSPORT", "BSC300", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 294, 300, "Ultra-thin smart cycling computer with color navigation."),

        // Magene (Magene = 267)
        DeviceProfile("magene_c606", "Magene", "C606 Smart GPS", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 267, 606, "Smart cycling computer with color touch screen and WiFi sync."),
        DeviceProfile("magene_c406", "Magene", "C406 Pro", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 267, 406, "Affordable aerodynamic FSTN screen bike computer."),

        // XOSS (XOSS = 289)
        DeviceProfile("xoss_nav", "XOSS", "NAV Smart GPS", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 289, 101, "Route guidance navigation bike computer."),
        DeviceProfile("xoss_g_plus", "XOSS", "G+ Gen 2", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 289, 102, "Entry level GPS speedometer."),

        // Sigma (Sigma Sport = 86)
        DeviceProfile("sigma_rox_12_1", "Sigma", "ROX 12.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 86, 12, "Full-map navigation cycling computer made in Germany."),
        DeviceProfile("sigma_rox_11_1", "Sigma", "ROX 11.1 EVO", DeviceCategory.CYCLOCOMPUTER, setOf(SportType.CYCLING), 86, 11, "Compact training and sensor computer."),

        // Garmin Watches
        DeviceProfile("garmin_forerunner_965", "Garmin", "Forerunner 965", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 1, 4105, "Premium AMOLED running and triathlon GPS smartwatch."),
        DeviceProfile("garmin_forerunner_265", "Garmin", "Forerunner 265", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 1, 4106, "Lightweight AMOLED running smartwatch with training readiness."),
        DeviceProfile("garmin_fenix_7_pro", "Garmin", "Fenix 7 Pro Sapphire Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 1, 4125, "Multisport endurance smartwatch with solar charging."),
        DeviceProfile("garmin_instinct_2x", "Garmin", "Instinct 2X Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 1, 4070, "Rugged military-standard outdoor GPS watch."),

        // COROS Watches (COROS = 287)
        DeviceProfile("coros_pace_3", "COROS", "Pace 3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 287, 30, "Ultra-lightweight high-precision running GPS watch (30g)."),
        DeviceProfile("coros_apex_2_pro", "COROS", "Apex 2 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 287, 21, "Titanium outdoor multisport GPS watch."),
        DeviceProfile("coros_vertix_2", "COROS", "Vertix 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 287, 22, "Extreme adventure GPS watch with dual-frequency satellite."),

        // Suunto Watches (Suunto = 23)
        DeviceProfile("suunto_race", "Suunto", "Race", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 23, 100, "AMOLED titanium performance watch with HRV recovery."),
        DeviceProfile("suunto_vertical", "Suunto", "Vertical Solar", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 23, 101, "Adventure watch with free offline global maps and solar battery."),

        // Polar Watches (Polar = 123)
        DeviceProfile("polar_vantage_v3", "Polar", "Vantage V3", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 123, 80, "Premium multisport watch with biosensing technologies."),
        DeviceProfile("polar_pacer_pro", "Polar", "Pacer Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 123, 81, "Ultralight advanced running watch with barometer."),

        // Amazfit (Zepp / Amazfit = 255)
        DeviceProfile("amazfit_cheetah_pro", "Amazfit", "Cheetah Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 255, 501, "Dedicated marathon running watch with MaxTrack dual-band antenna."),

        // Apple & Samsung Watches (Apple = 111, Samsung = 112)
        DeviceProfile("apple_watch_ultra_2", "Apple", "Watch Ultra 2", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 111, 2, "Rugged titanium smartwatch with precision dual-frequency GPS."),
        DeviceProfile("samsung_galaxy_watch_6", "Samsung", "Galaxy Watch 6 Pro", DeviceCategory.SPORTWATCH, setOf(SportType.CYCLING, SportType.RUNNING), 112, 6, "Wear OS sport smartwatch with personalized HR zones."),

        // Generic / Custom
        DeviceProfile("generic_fit", "Generic", "Standard FIT 2.0 Activity", DeviceCategory.GENERIC, setOf(SportType.CYCLING, SportType.RUNNING), 255, 1, "Universal specification-compliant FIT activity file."),
        DeviceProfile("custom_profile", "Custom", "Custom FIT Profile", DeviceCategory.GENERIC, setOf(SportType.CYCLING, SportType.RUNNING), 255, 99, "User-configured recording interval and sensor preferences.")
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
