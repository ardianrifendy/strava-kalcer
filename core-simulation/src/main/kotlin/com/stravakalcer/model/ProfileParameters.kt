package com.stravakalcer.model

/**
 * Mathematical parameters controlling how physics and physiological formulas
 * behave for a specific activity profile.
 */
data class ProfileParameters(
    val baseEffortFactor: Double,            // 0.8 (easy) to 1.3 (race)
    val gradeSensitivity: Double,            // How strongly gradient penalizes speed / increases pace
    val accelerationResponsiveness: Double,  // Rate of speed change (m/s^2 limit)
    val speedVariationTolerance: Double,     // Permitted deviation around baseline
    val coastingThresholdGradient: Double,   // Gradient below which cycling coasting begins (-2.0% to -4.0%)
    val targetHrPercentage: Double,          // Expected % of HR max for steady flats (0.60 to 0.88)
    val fatigueRatePerKm: Double = 0.001     // Gradual stamina decay on long routes
) {
    companion object {
        fun defaultsFor(profile: ActivityProfile): ProfileParameters = when (profile) {
            ActivityProfile.EASY_RIDE -> ProfileParameters(
                baseEffortFactor = 0.85,
                gradeSensitivity = 1.15,
                accelerationResponsiveness = 0.6,
                speedVariationTolerance = 0.15,
                coastingThresholdGradient = -2.0,
                targetHrPercentage = 0.65
            )
            ActivityProfile.ENDURANCE -> ProfileParameters(
                baseEffortFactor = 1.00,
                gradeSensitivity = 1.00,
                accelerationResponsiveness = 0.8,
                speedVariationTolerance = 0.20,
                coastingThresholdGradient = -2.5,
                targetHrPercentage = 0.72
            )
            ActivityProfile.TEMPO_CYCLING -> ProfileParameters(
                baseEffortFactor = 1.12,
                gradeSensitivity = 0.90,
                accelerationResponsiveness = 1.0,
                speedVariationTolerance = 0.22,
                coastingThresholdGradient = -3.0,
                targetHrPercentage = 0.80
            )
            ActivityProfile.RACE_CYCLING -> ProfileParameters(
                baseEffortFactor = 1.25,
                gradeSensitivity = 0.80,
                accelerationResponsiveness = 1.4,
                speedVariationTolerance = 0.30,
                coastingThresholdGradient = -3.5,
                targetHrPercentage = 0.88
            )
            ActivityProfile.FONDO -> ProfileParameters(
                baseEffortFactor = 1.05,
                gradeSensitivity = 0.95,
                accelerationResponsiveness = 0.85,
                speedVariationTolerance = 0.20,
                coastingThresholdGradient = -2.5,
                targetHrPercentage = 0.75
            )
            ActivityProfile.CLIMB -> ProfileParameters(
                baseEffortFactor = 1.15,
                gradeSensitivity = 0.75, // Climbers push harder through steep grades
                accelerationResponsiveness = 0.9,
                speedVariationTolerance = 0.25,
                coastingThresholdGradient = -3.0,
                targetHrPercentage = 0.82
            )
            ActivityProfile.CUSTOM_CYCLING -> ProfileParameters(
                baseEffortFactor = 1.00,
                gradeSensitivity = 1.00,
                accelerationResponsiveness = 0.85,
                speedVariationTolerance = 0.20,
                coastingThresholdGradient = -2.5,
                targetHrPercentage = 0.75
            )
            ActivityProfile.EASY_RUN -> ProfileParameters(
                baseEffortFactor = 0.85,
                gradeSensitivity = 1.10,
                accelerationResponsiveness = 0.5,
                speedVariationTolerance = 0.12,
                coastingThresholdGradient = -999.0, // No coasting for running
                targetHrPercentage = 0.65
            )
            ActivityProfile.LONG_RUN -> ProfileParameters(
                baseEffortFactor = 1.00,
                gradeSensitivity = 1.00,
                accelerationResponsiveness = 0.6,
                speedVariationTolerance = 0.15,
                coastingThresholdGradient = -999.0,
                targetHrPercentage = 0.72,
                fatigueRatePerKm = 0.002
            )
            ActivityProfile.TEMPO_RUN -> ProfileParameters(
                baseEffortFactor = 1.12,
                gradeSensitivity = 0.90,
                accelerationResponsiveness = 0.8,
                speedVariationTolerance = 0.18,
                coastingThresholdGradient = -999.0,
                targetHrPercentage = 0.82
            )
            ActivityProfile.RACE_RUN -> ProfileParameters(
                baseEffortFactor = 1.25,
                gradeSensitivity = 0.80,
                accelerationResponsiveness = 1.0,
                speedVariationTolerance = 0.22,
                coastingThresholdGradient = -999.0,
                targetHrPercentage = 0.89
            )
            ActivityProfile.INTERVALS -> ProfileParameters(
                baseEffortFactor = 1.20,
                gradeSensitivity = 0.85,
                accelerationResponsiveness = 1.2,
                speedVariationTolerance = 0.35,
                coastingThresholdGradient = -999.0,
                targetHrPercentage = 0.85
            )
            ActivityProfile.CUSTOM_RUN -> ProfileParameters(
                baseEffortFactor = 1.00,
                gradeSensitivity = 1.00,
                accelerationResponsiveness = 0.7,
                speedVariationTolerance = 0.20,
                coastingThresholdGradient = -999.0,
                targetHrPercentage = 0.75
            )
        }
    }
}
