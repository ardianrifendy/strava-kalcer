# Data Architecture v2

## Stack
- Kotlin
- Jetpack Compose
- MVVM or Clean Architecture
- Kotlin Coroutines

## Suggested package/module boundaries
- app
- core
- gpx
- terrain
- simulation
- physiology
- fit
- export
- map
- deviceprofile

## Canonical data model
### RoutePoint
- latitude
- longitude
- elevation
- elevationSource
- distanceFromStart
- rawGradient
- smoothedGradient
- sourceTimestamp

### SimulationSettings
- sport
- activityProfile
- targetAverageSpeed or targetAveragePace
- requestedMaxSpeed or requestedBestPace
- stop configuration
- HR configuration
- cadence configuration
- randomSeed
- selectedDeviceProfileId

### SimulationPoint
- latitude
- longitude
- elevation
- gradient
- distance
- speed
- pace
- heartRate?
- cadence?
- timestamp
- movingTime
- totalTime
- stopState

### SimulationResult
- distance
- movingTime
- totalTime
- averageMovingSpeed
- averagePace
- maxSpeed
- bestPace
- averageHeartRate?
- maxHeartRate?
- averageCadence?
- elevationGain
- elevationLoss
- requestedTarget
- achievedTarget
- targetFeasible
- points

## Canonical timeline
SimulationTimeline is the single source of truth for preview and FIT export.

UI charts, map cursor and FIT records must derive from the same timeline rather than independently recalculating activity data.

## Independence
Simulation engine must not import Compose/UI classes or device-profile UI models.
Device profiles must not alter terrain or simulation physics.
