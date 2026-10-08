# Debug Tools v2

Developer mode must make simulation behavior explainable.

## Show per point/sample
- raw coordinates
- processed coordinates
- elevation source
- raw gradient
- smoothed gradient
- terrain classification
- baseline speed/pace
- profile adjustment
- target adjustment
- final speed/pace
- HR
- cadence
- timestamp
- moving time
- total time
- stop state

## Show solver state
- requested target
- achieved target
- feasible/infeasible
- requested max/best
- achieved max/best
- downhill candidates
- selected candidate
- iteration count
- stopping reason

## Explainability
For suspicious samples, provide reason codes such as:
- UPHILL_SLOWDOWN
- DOWNHILL_ACCELERATION
- TARGET_ADJUSTMENT
- PROFILE_ADJUSTMENT
- DECELERATION
- COASTING
- STOP
- HR_LAG
- HR_RECOVERY

## CSV debug export
`index,latitude,longitude,elevation,elevationSource,distance,gradient,terrainClass,baselineSpeed,profileAdjustment,targetAdjustment,finalSpeed,heartRate,cadence,timestamp,movingTime,totalTime,stopState`

## Principle
When output looks wrong, the developer must be able to determine which stage created the value.
