# GPX Engine v2

## Parsing
Support:
- trk
- trkseg
- trkpt
- lat/lon
- ele
- time
- multiple track segments
- common GPX namespaces

Reject clearly invalid coordinates and report parse errors with useful context.

## Canonical route processing
For each valid point calculate:
- segment distance (geodesic/Haversine or equivalent)
- cumulative distance
- elevation delta
- gradient
- elevation source/status

Do not use latitude/longitude degrees as a direct planar distance.

## Elevation quality
Classify elevation as:
- Original
- Reconstructed
- Partially reconstructed
- Unavailable

Detect:
- missing elevation
- all-zero elevation
- invalid values
- obvious spikes/noise

Smoothing may reduce sensor noise, but must preserve meaningful climbs/descents.

## Reconstruction rule
Elevation reconstruction must use a real source (for example a configured DEM service/dataset). Record the source and version when practical.

Never invent synthetic hills solely because a simulator needs elevation.

If no real elevation source is available, terrain-dependent simulation must degrade gracefully.

## Gradient
Gradient must be calculated using a distance window/smoothing strategy, not a single noisy point-to-point difference only.

Preserve both raw and smoothed gradient for debugging.

## Multiple segments
Handle track-segment boundaries explicitly. Do not accidentally create a huge artificial jump between disconnected segments.

## Rendering data
Keep high-resolution processed data for simulation/export. Create a separate downsampled representation for map/chart rendering so large GPX files remain responsive.

## Performance
Target:
- 10k
- 50k
- 100k+

Use background processing and bounded-memory structures.
