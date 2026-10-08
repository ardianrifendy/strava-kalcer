# Activity Profiles v2

Profiles are parameter presets, not fixed route generators.

## Cycling
- Easy Ride
- Endurance
- Tempo
- Race
- Climb
- Fondo
- Custom

## Running
- Easy Run
- Long Run
- Tempo
- Race
- Intervals
- Custom

Profiles influence model parameters such as:
- preferred effort
- tolerance for speed/pace variation
- acceleration response
- recovery behavior
- HR response
- cadence tendency

Do not hard-code a complete route output for a profile.

Custom profile should override exposed parameters while preserving safety/consistency constraints from the engine.
