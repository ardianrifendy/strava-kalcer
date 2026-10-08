# Android Build and Roadmap v2

## Target
Primary QA device: Samsung Galaxy A56.

Use a current stable Android Studio/Kotlin/Compose toolchain supported by the build environment. Keep SDK versions configurable in one place.

## Storage
Use Android Storage Access Framework for GPX import and FIT save.
Avoid unnecessary broad storage permissions.

## Background work
Do not block the main thread for:
- GPX parsing
- route processing
- terrain analysis
- simulation
- FIT generation
- FIT validation

Use coroutines and structured concurrency.

## Phases and acceptance criteria
### Phase 1 — Foundation
Project, Compose, navigation, theme, DI/state strategy, test setup.
Acceptance: clean build + launch + navigation test.

### Phase 2 — GPX/Route
Import, parse, process, map, elevation chart, route stats, data quality.
Acceptance: known GPX produces correct route metrics and responsive review.

### Phase 3 — Cycling
Terrain-aware speed, target average, max-speed opportunity, smoothing.
Acceptance: terrain relationships and feasibility tests pass.

### Phase 4 — Preview
Map + synchronized charts + summary + regenerate.
Acceptance: one cursor controls all visualizations.

### Phase 5 — Running
Pace model and running profiles.

### Phase 6 — Physiology
HR, cadence, zones, lag/recovery.

### Phase 7 — Profiles
Activity presets + Custom.

### Phase 8 — Device Profiles
Extensible profile registry + search/filter.

### Phase 9 — FIT
Generation + parse-back validation + export.

### Phase 10 — QA/Performance
Large files, error states, deterministic replay, UI polish.

After every phase:
Compile → unit tests → integration tests where relevant → manual smoke test → fix blockers → continue.

## Deliverables
- Android Studio project
- debug APK
- release APK if signing is available
- README/build instructions
- test report
- known limitations
- sample GPX
- sample validated FIT
- algorithm notes
- device profile documentation
