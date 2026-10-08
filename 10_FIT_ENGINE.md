# FIT Engine v2

## Goal
Generate a structurally valid FIT activity file that is internally consistent with the simulation result.

Prefer a mature FIT implementation/library and follow its supported profile definitions rather than hand-encoding binary FIT fields unnecessarily.

## Data consistency
FIT timestamps, distance, speed, altitude, HR and cadence must agree with the canonical SimulationTimeline.

When pauses exist, timestamps continue while moving time does not.

## Records
Include the fields supported/needed by the selected profile, typically including:
- timestamp
- position
- altitude
- speed
- distance
- heart rate (when enabled)
- cadence (when enabled)

Use correct FIT units/scales/semantics as required by the chosen implementation.

## Activity structure
Use the FIT message structures required by the selected library/profile for an Activity file, including appropriate file/session/lap/event constructs rather than treating a file as records-only.

## Validation
After generation:
1. write to a temporary file
2. reopen from bytes
3. parse using the FIT parser
4. validate file structure
5. validate message counts
6. validate timestamp ordering
7. validate coordinates
8. validate distance monotonicity
9. validate speed range
10. validate elevation consistency
11. validate HR if enabled
12. validate cadence if enabled
13. validate session/lap/activity summary fields where present
14. compare summary metrics against SimulationResult within defined rounding tolerance

If validation fails, the UI must not mark the file as ready.

## Export
Use Android save/share surfaces.

Suggested filename:
`StravaKalcer_<route>_<date>.fit`

No direct Strava upload in initial release.
