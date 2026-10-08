package com.stravakalcer.model

enum class ActivityProfile(
    val id: String,
    val displayName: String,
    val sport: SportType,
    val description: String
) {
    // Cycling profiles
    EASY_RIDE(
        "easy_ride",
        "Easy Ride",
        SportType.CYCLING,
        "Relaxed recovery pace with lower effort and gradual acceleration."
    ),
    ENDURANCE(
        "endurance",
        "Endurance",
        SportType.CYCLING,
        "Steady aerobic pace optimized for consistent long-distance energy expenditure."
    ),
    TEMPO_CYCLING(
        "tempo_cycling",
        "Tempo",
        SportType.CYCLING,
        "Sustained brisk pace, responsive on flats and controlled effort on climbs."
    ),
    RACE_CYCLING(
        "race_cycling",
        "Race",
        SportType.CYCLING,
        "Aggressive pace, sharp attacks on descents, high climbing effort."
    ),
    FONDO(
        "fondo",
        "Gran Fondo",
        SportType.CYCLING,
        "Paced endurance ride balancing climbing stamina with steady flat segments."
    ),
    CLIMB(
        "climb",
        "Climb Specialist",
        SportType.CYCLING,
        "Optimized climbing cadence and high effort bias on uphill gradients."
    ),
    CUSTOM_CYCLING(
        "custom_cycling",
        "Custom Cycling",
        SportType.CYCLING,
        "User-defined parameters for effort, grade sensitivity, and responsiveness."
    ),

    // Running profiles
    EASY_RUN(
        "easy_run",
        "Easy Run",
        SportType.RUNNING,
        "Comfortable conversational recovery pace with low impact and steady cadence."
    ),
    LONG_RUN(
        "long_run",
        "Long Run",
        SportType.RUNNING,
        "Paced endurance building aerobic stamina across distance with gradual fatigue."
    ),
    TEMPO_RUN(
        "tempo_run",
        "Tempo Run",
        SportType.RUNNING,
        "Lactate threshold pace, solid rhythm, and disciplined uphill management."
    ),
    RACE_RUN(
        "race_run",
        "Race",
        SportType.RUNNING,
        "High-performance competitive pacing with aggressive push on flat and downhill."
    ),
    INTERVALS(
        "intervals",
        "Intervals",
        SportType.RUNNING,
        "Dynamic pacing with surging high-effort bursts and recovery phases."
    ),
    CUSTOM_RUN(
        "custom_run",
        "Custom Running",
        SportType.RUNNING,
        "User-defined running pacing parameters."
    );

    companion object {
        fun forSport(sport: SportType): List<ActivityProfile> =
            entries.filter { it.sport == sport }
    }
}
