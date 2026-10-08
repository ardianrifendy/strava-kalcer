# Testing and QA v2

## GPX
Test:
- valid
- malformed
- empty
- one point
- multiple segments
- missing elevation
- all-zero elevation
- noisy elevation
- malformed coordinates
- missing timestamps
- duplicate timestamps
- 10k/50k/100k+ points

## Terrain
Verify:
- flat remains flat
- sustained climb is classified as climb
- sustained descent is classified as downhill
- noise does not create fake segments
- route segment boundaries do not create artificial gradients

## Cycling invariants
- uphill generally slower than comparable flat terrain
- downhill generally faster than comparable flat terrain
- no implausible point-to-point speed jumps
- acceleration/deceleration constraints hold
- average target is achieved when feasible
- infeasible targets are reported honestly
- requested max speed is only reached when route context permits

## Running invariants
- uphill generally slower pace
- downhill generally faster pace
- smooth transitions
- target pace feasibility handled

## HR
- effort leads HR
- HR follows with lag
- sustained effort raises HR gradually
- recovery lowers HR gradually
- no unexplained spikes

## Cadence
- cycling cadence responds coherently to speed/terrain/coasting
- running cadence responds coherently to pace
- cadence changes smoothly

## Time
Verify:
- timestamps monotonic
- movingTime excludes stops
- totalTime includes stops
- pause records/timestamps remain coherent

## FIT
Parse every generated fixture and compare to SimulationResult.

## Determinism
Same input + settings + seed must produce the same simulation within floating-point/serialization tolerances.

## Golden test
Use a known GPX fixture such as:
- cycling
- target average 28.0 km/h
- requested max 50.0 km/h
- HR enabled
- cadence enabled

Store expected ranges/relationships, not brittle exact point-by-point values, unless the prototype algorithm is intentionally frozen.

## UI QA
Verify:
- no emoji
- vector icons only
- synchronized cursor
- regenerated simulation replaces preview correctly
- no main-thread blocking
- large GPX remains responsive
- accessibility basics
