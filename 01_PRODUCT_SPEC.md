# Product Specification v2

## User-facing value
Strava Kalcer lets a user take a GPX route and turn it into a complete, configurable activity simulation.

### Core features
**Recreate Your Ride / Run** — Turn a GPX route into a full activity with route, elevation, speed/pace, time, HR and cadence.

**Target Your Average** — Set a target average speed for cycling or target average pace for running. The engine treats the target as an end result, not as a constant value.

**Realistic Uphill & Downhill** — Performance changes with actual route gradient and transitions smoothly between terrain types.

**Find Fast Sections** — Analyze the real route to identify strong downhill opportunities, major climbs and potentially fast sections.

**Activity Profiles** — Easy, Endurance, Tempo, Race, Fondo, Climb, Long Run, Intervals and Custom.

**Heart Rate + Cadence** — Generate correlated sensor time series with gradual response and recovery.

**Synchronized Preview** — Map, elevation, speed/pace, HR and cadence share one cursor.

**Multiple Scenarios** — Re-run the same GPX with different settings without importing it again.

**Device Profiles** — Select a configurable output profile representing the recording characteristics and FIT field choices of a device family/model.

**FIT Export** — Generate, reopen, validate and save/share the FIT file.

## Important product semantics
A device profile is an **output/recording profile**, not a claim that the file was physically recorded by that exact device.

A requested maximum speed is a **preferred ceiling/target**. If the route cannot plausibly reach it, show the best achievable value.

A target average speed/pace is subject to route feasibility. When infeasible, show the achievable result rather than forcing an unrealistic curve.

## MVP vs later
### MVP
Everything listed in Core features.

### Later candidates
Direct platform upload, cloud sync, social sharing, advanced DEM providers, training plans, route editing, live GPS recording.
