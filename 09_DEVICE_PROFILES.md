# Device Profiles v2

## Purpose
Device profiles describe **output/recording characteristics** for a device family/model. They are not proof that the activity was physically recorded by that device.

## Categories
### Cyclocomputer
- Garmin Edge
- Wahoo ELEMNT
- Bryton
- Hammerhead Karoo
- iGPSPORT
- Magene
- XOSS
- Sigma

### Sportwatch
- Garmin Forerunner
- Garmin Fenix
- Garmin Instinct
- COROS Pace
- COROS Apex
- COROS Vertix
- Suunto
- Polar
- Amazfit
- Apple Watch
- Samsung Galaxy Watch

Also:
- Generic FIT
- Custom FIT Profile

## Profile fields
A profile may define:
- manufacturer
- model/family
- sport
- FIT file/message preferences
- recording interval characteristics
- supported sensor fields
- precision/rounding preferences
- optional device-specific metadata where supported by the FIT format/library

Adding a device must not require changes to the simulation engine.

## UX
Search and filter by category/sport. Group device families; do not create a huge flat list.
