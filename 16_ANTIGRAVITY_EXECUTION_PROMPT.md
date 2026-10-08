# Antigravity Execution Prompt

Read all Strava Kalcer `.md` specifications in this workspace before modifying code.

Your job is to build the actual Android application, not merely scaffold screens.

## Execution order
1. Inspect the current repository and previous GPX/FIT prototype.
2. Map existing code to the specification.
3. Identify reusable validated algorithms and preserve them where they are correct.
4. Establish the Android project/build baseline.
5. Implement phases in `13_ANDROID_BUILD_AND_ROADMAP.md` in order.
6. After each phase, compile and run the relevant tests.
7. Do not paper over failing tests or hide errors.

## Non-negotiable product behavior
- Do not randomize speed/HR/cadence as the main algorithm.
- Never fabricate elevation if the GPX has none unless a real elevation source is explicitly available.
- Keep Moving Time and Total Time separate.
- Support explicit stop/pause intervals and preserve them in timestamps/FIT.
- Target average is a result constraint, not a constant speed.
- Requested maximum speed/best pace must be route-aware and may be infeasible.
- Keep terrain relationships intact while solving the target.
- Same input/settings/seed should be reproducible.
- Map/elevation/speed/pace/HR/cadence must use one canonical timeline and synchronized cursor.
- Device profiles describe output/recording characteristics and must remain independent of simulation physics.
- FIT must be generated and parsed/validated before export is reported as successful.

## UI requirements
- Kotlin + Jetpack Compose.
- Premium fitness-app feel.
- Inspired by Strava/Garmin/COROS/Suunto, but not a clone.
- No emojis, emoticons or decorative Unicode icons.
- Vector icons only.
- Do not create generic dashboard UI.

## User-facing priorities
The product should prominently communicate:
- Recreate Your Ride
- Target Your Average
- Realistic Uphill & Downhill
- Find Fast Sections
- Ride Your Style
- Heart Rate + Cadence
- One Cursor. Everything Connected.
- Multiple Scenarios
- Device Profiles + FIT Export

## Error handling
Handle gracefully:
- invalid/empty GPX
- missing elevation
- missing timestamps
- multiple segments
- infeasible target
- impossible requested max/best
- FIT validation failure
- large GPX

Every failure should provide an actionable explanation instead of a generic exception message.

## Final verification
Before declaring the project complete:
- build succeeds
- unit tests pass
- critical integration tests pass
- sample GPX imports
- route review works
- cycling simulation works
- running simulation works
- HR/cadence work when enabled
- synchronized cursor works
- regeneration works
- device selection works
- FIT generation works
- FIT parse-back validation works
- save/share works
- no emojis appear in UI
- large GPX does not freeze the UI
