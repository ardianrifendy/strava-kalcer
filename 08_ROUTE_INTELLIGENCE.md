# Route Intelligence v2

Analyze the real processed GPX.

Calculate:
- total distance
- elevation gain/loss
- min/max elevation
- flat distance
- climbing distance
- downhill distance
- hardest climb
- best downhill
- notable effort sections
- max-speed opportunities
- fastest running opportunities

## Segment detection
Classify terrain using smoothed gradient with configurable thresholds and minimum segment duration/distance so tiny GPS fluctuations do not become fake climbs/descents.

## Candidate ranking
Downhill candidates can be ranked by:
- average/sustained gradient
- length
- approach speed opportunity
- recovery/braking requirements
- road/route geometry available in GPX

Do not infer traffic, road surface or legal speed limits unless the data source explicitly provides them.

## Output
Expose both user-friendly summary metrics and developer-level candidate details.

All route intelligence must originate from actual route data.
