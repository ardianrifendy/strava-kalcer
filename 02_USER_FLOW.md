# User Flow v2

## 1. Home
Primary CTA: **IMPORT GPX**
Secondary: recent simulations/projects.

## 2. Import
Use Android Storage Access Framework/system picker.

## 3. Processing
Parse GPX and calculate route metrics off the main thread.

Show processing state rather than freezing the UI.

## 4. Data Quality
Before Route Review, clearly show:
- elevation: Original / Reconstructed / Partial / Unavailable
- timestamps: Original / Generated
- track segments / point count

## 5. Route Review
Show:
- map
- route polyline
- start/finish
- distance
- elevation gain/loss
- min/max elevation
- interactive elevation graph
- route intelligence summary

Drag the cursor to update:
- map marker
- distance
- elevation
- gradient

## 6. Confirm Route
CTA: **CONFIRM ROUTE & NEXT**

## 7. Activity Settings
Choose Cycling or Running.

Cycling:
- profile
- target average speed
- requested maximum speed (optional)
- optional stops/pauses
- HR enabled/settings
- cadence enabled/settings

Running:
- profile
- target average pace
- requested best pace (optional)
- optional stops/pauses
- HR enabled/settings
- cadence enabled/settings

Show a small feasibility summary before simulation when possible.

## 8. Simulation
Generate progressively off the main thread. Show meaningful progress.

## 9. Preview
Show:
- map
- elevation
- speed/pace
- HR
- cadence
- distance
- moving time
- total time
- average / max metrics
- elevation gain/loss

All charts and the map use one synchronized cursor.

Allow **REGENERATE** without re-importing the GPX.

## 10. Device Profile
Search/filter by category.

## 11. FIT
Generate → reopen → parse → validate.

If invalid, show failure details and keep the user on the export step.

## 12. Save / Share
Use Android system save/share surfaces.
