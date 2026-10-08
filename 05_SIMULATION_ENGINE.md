# Simulation Engine v2

## Core model
The simulation is a constrained time-series generator, not a random number generator.

Primary relationship:
**Terrain → Effort → Speed/Pace → Time → HR/Cadence**

Terrain remains authoritative. Profile parameters modify behavior, not route geometry.

## Inputs
Common:
- processed route
- elevation/gradient quality
- activity profile
- sport
- target average
- max-speed/best-pace request (optional)
- stop/pause configuration
- HR settings
- cadence settings
- simulation seed if deterministic replay is desired

Cycling target:
- average moving speed in km/h
- optional requested maximum speed

Running target:
- average pace in min/km
- optional requested best pace

## Cycling behavior
- uphill generally reduces speed
- flat terrain remains comparatively stable with natural variation
- downhill increases speed when appropriate
- transitions are smooth
- acceleration/deceleration are bounded
- coasting can reduce cadence substantially and may reduce speed depending on grade

## Running behavior
- uphill generally increases pace time per km (slower)
- downhill generally decreases pace time per km (faster)
- flat sections remain comparatively stable
- transitions are smooth

## Target-average solver
1. Generate a terrain-aware baseline.
2. Apply profile behavior.
3. Apply smoothness/acceleration constraints.
4. Calculate moving time and average.
5. Compare with target.
6. Determine whether target is feasible.
7. Adjust only the appropriate degrees of freedom while preserving terrain relationships.
8. Recalculate.
9. Stop when within tolerance or when feasibility limits are reached.

Never use a blind global multiplier if it creates unrealistic uphill/downhill behavior.

Default cycling target tolerance: ±0.2 km/h when feasible.

For running, define a comparable tolerance in seconds/km based on the target pace.

## Maximum speed / best pace
Find candidates using:
- sustained gradient
- segment length
- approach speed
- acceleration potential
- following terrain
- braking/deceleration context

Do not force one point to equal the requested maximum.

If the route cannot plausibly reach the requested maximum/best pace, expose:
- requested value
- achievable value
- a concise reason

## Stops / pauses
Support optional stops so Total Time can exceed Moving Time.

During a stop:
- elapsed total time continues
- moving time does not advance
- speed should be 0 or represent a valid stopped state
- location should remain stationary unless a configured transition is explicitly modeled

The same pause must be represented consistently in preview and FIT timestamps.

## Smoothing
Use explicit constraints for rate of change, not arbitrary noise.

Good:
27 → 29 → 32 → 35 → 38 → 41 → 44 → 42 → 39

Bad:
27 → 27 → 44 → 29

## Determinism
Given the same GPX, settings and seed, the simulator should be reproducible. This is critical for debugging and testing.

## Feasibility
The engine must distinguish:
- target requested
- target feasible
- target achieved

Never distort terrain to make an impossible target appear achievable.
