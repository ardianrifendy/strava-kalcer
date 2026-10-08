# Strava Kalcer — Master Instructions v2

## Mission
Build **Strava Kalcer**, a native Android application that converts an existing GPX route into a configurable, internally consistent simulated activity and a validated FIT export.

This is **not** a simple GPX-to-FIT converter. The product is a route-aware activity simulation engine wrapped in a simple consumer fitness UI.

## Product pipeline
IMPORT GPX → DATA QUALITY → ROUTE REVIEW → ACTIVITY SETTINGS → SIMULATE → PREVIEW → DEVICE PROFILE → GENERATE FIT → VALIDATE FIT → SAVE/SHARE

## Highest-priority rules
1. Read every specification file before implementing the related subsystem.
2. Inspect the existing workspace/prototype first and preserve validated algorithms where useful.
3. Keep simulation independent from UI and device-profile code.
4. Never fabricate terrain, elevation, timestamps, or sensor behavior without an explicit model and source.
5. Never use random speed/HR/cadence as the primary generator.
6. Terrain → effort → speed/pace → time → HR/cadence must remain internally consistent.
7. Moving Time and Total Time are separate concepts and must remain separate throughout the data model and FIT export.
8. All visualizations use one canonical activity timeline and one synchronized cursor.
9. Heavy processing must run off the main thread.
10. After each major phase: compile, run automated tests, fix blockers, then continue.

## Elevation integrity
If GPX elevation is missing/invalid/all-zero:
- do NOT silently treat it as flat terrain;
- show the elevation status to the user;
- if a real DEM/elevation source is available, label the result as Reconstructed and record the source;
- if no real source is available, keep elevation unavailable and disable terrain-dependent features that require it, rather than inventing elevation.

## Time integrity
The simulation must have an explicit canonical timeline.
- Every SimulationPoint has a timestamp.
- Timestamps are strictly non-decreasing; moving records are chronological.
- Moving Time excludes pauses/stops.
- Total Time includes pauses/stops.
- When source timestamps exist, preserve them where possible and repair only clear inconsistencies.
- When source timestamps do not exist, generate them from simulated movement plus any configured stops.

## UI direction
Use Jetpack Compose and a modern fitness-app visual language inspired by the usability of Strava, Garmin, COROS and Suunto, but do not copy proprietary assets, logos, exact layouts or pixel-perfect UI.

ABSOLUTELY NO EMOJIS OR DECORATIVE UNICODE ICONS IN THE PRODUCT UI. Use consistent vector icons.

## Target device
Primary QA target: Samsung Galaxy A56. Do not hard-code a specific Android API level solely from this document. Use a current stable Android toolchain supported by the build environment and verify compatibility on the target device.

## Initial release scope
Required:
- GPX import/review
- route intelligence
- cycling and running simulation
- target average speed/pace
- requested max speed / best pace with feasibility handling
- optional stops/pauses
- HR and cadence simulation
- synchronized preview
- device profiles
- FIT generation and validation
- save/share
- debug mode

Do not implement direct Strava upload in the initial release.
