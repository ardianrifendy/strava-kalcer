# Strava Kalcer — Native Android Fitness Simulation & FIT Engine

**Strava Kalcer** is a native Android activity reconstruction and simulation application built with Kotlin, Jetpack Compose, and the Garmin FIT SDK.

Unlike simple GPX-to-FIT converters, **Strava Kalcer** transforms raw GPX routes into complete, internally consistent, physically and physiologically coherent activities.

---

## Architecture & Modular Design

```
Strava Kalcer
├── core-simulation/          # Pure Kotlin JVM simulation engine (independent from Android UI)
│   ├── gpx/                  # GPX parser, elevation quality classification, GeoMath
│   ├── intelligence/         # Route intelligence (climbs, descents, downhill opportunities)
│   ├── simulation/           # Cycling & running terrain models, target-average root solver
│   ├── physiology/           # Continuous heart rate (lag & recovery) and cadence models
│   ├── device/               # Extensible device profile registry (Garmin, Wahoo, Coros, Suunto, etc.)
│   ├── fit/                  # Garmin FIT binary generator & parse-back validator
│   ├── debug/                # CSV diagnostic export & explainability reasons
│   └── sample/               # Synthetic route generator
│
└── app/                      # Android Jetpack Compose application module
    ├── theme/                # Athletic color palette, typography & Material 3 Dark theme
    ├── ui/components/        # Synchronized multi-chart canvas, route map polyline canvas
    ├── ui/screens/           # Home, Route Review, Settings, Preview, Device Select, Export, Debug
    └── viewmodel/            # Reactive state management with Kotlin Coroutines
```

---

## Core Pipeline

```
IMPORT GPX
  ↓
ROUTE INTELLIGENCE & QUALITY AUDIT (Elevation classification: Original / Reconstructed / Unavailable)
  ↓
ACTIVITY SETTINGS (Sport: Cycling / Running, Profile: Endurance / Race / Climb / etc., Target Average)
  ↓
SIMULATION (Terrain-preserving root solver, dynamic acceleration & braking constraints)
  ↓
PHYSIOLOGY MODELING (Continuous Heart Rate with lag & recovery, Cadence with downhill coasting drop)
  ↓
SYNCHRONIZED PREVIEW (One cursor controlling Map, Elevation, Speed/Pace, HR, and Cadence)
  ↓
DEVICE PROFILE SELECTION (Garmin Edge, Wahoo ELEMNT, Hammerhead, COROS, Suunto, Polar, etc.)
  ↓
GARMIN FIT ENGINE (Binary encoding of FileId, Activity, Session, Lap, and Record messages)
  ↓
PARSE-BACK VALIDATION (Reopens file, verifies CRC, monotonic timestamps, sensor data & metrics)
  ↓
SAVE / SHARE (Android Storage Access Framework & Share Sheet)
```

---

## Key Features & Invariants

1. **Terrain-Authoritative Physics**:
   - Uphill grades naturally reduce speed / increase running pace per km.
   - Downhill grades naturally produce high speed with realistic terminal limits and cornering deceleration.
   - Acceleration and deceleration are bounded to prevent unrealistic point jumps (e.g. `27 → 29 → 32 → 35 → 38`, never `27 → 27 → 44 → 29`).

2. **Target-Average Solver**:
   - Target speed (cycling) and target pace (running) are aggregate results, not point-by-point flatlines.
   - Solved using iterative binary search / secant method over effort levels while strictly preserving the relative terrain curve.
   - Infeasible targets are reported honestly with achievable metrics.

3. **Continuous Physiology**:
   - Heart rate is modeled using asymmetric first-order differential equations: effort drive with ~12s lag, and gradual ~26s recovery decay.
   - Cadence responds to speed, climbing gradient, and drops to coasting levels on steep descents.

4. **Single Synchronized Timeline**:
   - Exactly one canonical cursor controls all charts and the map. Dragging anywhere updates every visual element in lockstep.

5. **Strict Data Integrity**:
   - Missing or all-zero elevations are detected and flagged as `UNAVAILABLE` rather than silently fabricated.
   - Moving time and total time are tracked separately; pauses keep total time moving while moving time stops and speed is zero.
   - Timestamps are strictly non-decreasing and chronological.

6. **Garmin FIT SDK Integration & Parse-back Validation**:
   - Generates official Garmin FIT protocol binaries (`com.garmin:fit`).
   - Every file is automatically parsed back to check header CRC, message counts, coordinate semicircles, and metric consistency before enabling export.

7. **Zero Emojis**:
   - The UI strictly uses vector icons (`androidx.compose.material.icons`).

---

## Deliverables

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk` (16.4 MB)
- **Sample GPX Route**: `samples/sample_loop.gpx` (18.2 KB)
- **Sample Validated FIT Activity**: `samples/sample_validated.fit` (4.4 KB)
- **Sample Diagnostic CSV**: `samples/sample_diagnostic.csv` (17.4 KB)

---

## How to Build & Test

### Prerequisites
- JDK 17 (Microsoft OpenJDK or Eclipse Temurin)
- Android SDK Platform 34 & Build-Tools 34.0.0 (configured in `local.properties`)

### Commands

1. **Run full automated test suite**:
   ```powershell
   .\gradlew.bat test
   ```

2. **Compile and assemble debug APK**:
   ```powershell
   .\gradlew.bat assembleDebug
   ```

3. **Install on connected device (e.g. Samsung Galaxy A56)**:
   ```powershell
   .\gradlew.bat installDebug
   ```
