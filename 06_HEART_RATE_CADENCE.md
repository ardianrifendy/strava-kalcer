# Heart Rate and Cadence Engine v2

## Heart Rate
HR is a continuous state/time series.

Inputs can include:
- resting HR
- max HR
- profile/effort
- gradient
- speed/pace
- previous HR state
- recovery state

Model behavior:
- effort changes first
- HR follows with a lag
- sustained effort gradually raises HR
- reduced effort gradually lowers HR
- recovery should be visible
- avoid arbitrary spikes

Do not imply clinical/physiological precision. Present it as a simulation.

## Cadence — Cycling
Cadence responds to:
- speed
- gradient
- effort
- activity profile
- coasting/downhill context

Cadence may fall strongly during coasting and can approach zero when stopped.

## Cadence — Running
Use steps per minute.

It should correlate with pace and profile and change smoothly.

## Zones
Optional Z1–Z5 summary based on the configured HR model.

## Correlation checks
The engine should expose tests for:
- higher sustained effort → generally higher HR
- recovery → gradual HR decline
- cycling coasting → cadence reduction
- faster running sections → cadence/pace relationship remains coherent
