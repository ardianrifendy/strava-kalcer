# UI / UX Specification v2

## Visual direction
Premium, clean, sporty, technical, minimal and data-centric.

Use strong typography, generous spacing, clear hierarchy, restrained surfaces and graph-first activity review.

## Never
- emoji
- emoticons
- decorative Unicode symbols
- mixed icon families
- generic admin-dashboard layouts
- excessive gradients/colors/shadows
- fake "AI" styling
- pixel-perfect Strava clone

Use consistent Android/vector icons.

## Main screens
1. Home
2. GPX Import
3. Data Quality
4. Route Review
5. Activity Settings
6. Simulation Progress
7. Simulation Preview
8. Device Profile
9. FIT Export/Validation
10. Settings
11. Debug (developer mode)

## Home
Make the value obvious immediately:
- Import GPX CTA
- recent routes/projects
- simple explanation of what the app creates

## Route Review
Map and elevation graph should dominate the screen. Statistics should be compact.

## Activity Settings
Use progressive disclosure: show the settings relevant to the selected sport/profile and hide unnecessary controls.

## Preview
The user should be able to compare the whole activity at a glance and inspect any point precisely.

## Synchronized cursor
There is one logical cursor state:
- canonical timeline position
- corresponding route distance
- corresponding simulation point/time

Dragging any graph updates all graphs and the map.

Use interpolation when visual sampling differs between charts; do not duplicate separate cursor logic per chart.

## Accessibility
- readable metric sizes
- sufficient contrast
- touch targets suitable for mobile
- support dynamic font scaling where practical
- never encode state using color alone
