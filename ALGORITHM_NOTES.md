# Strava Kalcer — Simulation Algorithm Notes

This document details the mathematical, physical, and physiological formulations implemented in the simulation engine.

---

## 1. Geodesic & Terrain Modeling

### Great-Circle Haversine Distance
The distance $s$ between adjacent coordinates $(\phi_1, \lambda_1)$ and $(\phi_2, \lambda_2)$ is given by:

$$a = \sin^2\left(\frac{\Delta \phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta \lambda}{2}\right)$$
$$s = 2 R \operatorname{atan2}(\sqrt{a}, \sqrt{1 - a})$$

where $R = 6,371,000 \text{ m}$.

### Gradient Calculation and Smoothing
Point-to-point elevation difference $\Delta h = h_i - h_{i-1}$.
Raw gradient $G_{\text{raw}}$:

$$G_{\text{raw}} = \frac{\Delta h}{s} \times 100\%$$

To remove GPS barometric and multipath flutter without destroying true mountain climbs and descents, a symmetric rolling window smoothing filter is applied:

$$G_{\text{smooth}, i} = \frac{1}{2k + 1} \sum_{j = i - k}^{i + k} G_{\text{raw}, j}$$

---

## 2. Cycling Speed Physics

Cycling velocity responds to resistive forces:
- Gravity resistance: $F_g = m g \sin(\theta) \approx m g \cdot (G / 100)$
- Rolling resistance: $F_{rr} = C_{rr} m g \cos(\theta)$
- Aerodynamic drag: $F_{\text{aero}} = \frac{1}{2} \rho C_d A v^2$

### Gradient Penalty & Boost
Given a flat base cruising effort speed $v_0$ (m/s):
- **Climbing ($G > 0$)**:
  $$v_{\text{climb}} = \frac{v_0}{1 + \left(\frac{G}{5.5}\right) \cdot \gamma_{\text{grade}}}$$
  clamped to a minimum crawl speed of $1.8 \text{ m/s}$ ($6.5 \text{ km/h}$).

- **Descending ($G < -0.5\%$)**:
  $$v_{\text{descent}} = v_0 + \sqrt{|G|} \cdot 2.8 \cdot (\beta_{\text{effort}} \cdot 0.9)$$
  clamped to a terminal velocity ceiling of $22.2 \text{ m/s}$ ($80 \text{ km/h}$).

### Kinematic Acceleration & Deceleration Bounds
Forward acceleration is bounded to prevent unrealistic sprint bursts:
$$v_i \le \sqrt{v_{i-1}^2 + 2 a_{\max} s_i}$$
where $a_{\max} \approx 0.8 \text{ m/s}^2 \cdot \alpha_{\text{resp}}$.

Braking deceleration is bounded similarly in the backward pass:
$$v_i \le \sqrt{v_{i+1}^2 + 2 d_{\max} s_{i+1}}$$
where $d_{\max} \approx 1.8 \text{ m/s}^2$.

---

## 3. Running Pace Mechanics

Running pace $P$ (seconds/km) is inverse to speed ($P = 1000 / v$):
- **Uphill ($G > 0$)**:
  $$v_{\text{run}} = \frac{v_0}{1 + \left(\frac{G}{10.0}\right) \cdot \gamma_{\text{grade}} \cdot 0.9}$$
  Every $+1\%$ slope increases pace time by approximately $12\text{--}15 \text{ s/km}$.

- **Gentle Downhill ($-6\% \le G \le -0.5\%$)**:
  $$v_{\text{run}} = v_0 \left(1 + \frac{|G|}{6.0} \cdot 0.22 \cdot \beta_{\text{effort}}\right)$$

- **Steep Downhill ($G < -6\%$)**:
  Biomechanical eccentric braking reduces speed efficiency:
  $$v_{\text{run}} = v_0 \cdot \max\left(1.05, 1.22 - (|G| - 6.0) \times 0.02\right)$$

---

## 4. Target-Average Solver

The solver finds the base cruising effort $v_0$ such that the resulting aggregate moving average speed:

$$\bar{v} = \frac{S_{\text{total}}}{\sum_{i=1}^n \frac{s_i}{v_i(v_0)}} \approx v_{\text{target}} \pm 0.2 \text{ km/h}$$

Because $\bar{v}$ is strictly monotonically increasing with $v_0$, binary search over the feasible physiological domain $[v_{\min}, v_{\max}]$ converges exponentially in 10–20 iterations.

If $v_{\text{target}}$ exceeds the aggregate physical limit achievable by maximum human power output on the specific elevation profile, the engine marks `targetFeasible = false` and reports the maximum achievable average.

---

## 5. Physiology Modeling

### Continuous Heart Rate Lag & Recovery
Instantaneous cardiovascular target $HR_{\text{target}}$:
$$HR_{\text{target}} = HR_{\text{rest}} + (HR_{\max} - HR_{\text{rest}}) \cdot f_{\text{effort}}$$

The continuous time-series evolution is governed by:
$$\frac{d HR}{dt} = \frac{HR_{\text{target}} - HR(t)}{\tau}$$

Discretized using exponential smoothing:
$$HR_{t + \Delta t} = HR(t) + \left(HR_{\text{target}} - HR(t)\right) \left(1 - e^{-\Delta t / \tau}\right)$$

where:
- Effort onset time constant $\tau_{\text{rise}} = 12.0 \text{ s}$
- Recovery decay time constant $\tau_{\text{rec}} = \frac{12.0 \times 2.2}{r_{\text{rec}}} \approx 26.0 \text{ s}$

### Cadence Simulation
- **Cycling Cadence**:
  - Flat: centered around $85\text{--}95 \text{ rpm}$.
  - Climbing: drops into torque range ($65\text{--}80 \text{ rpm}$).
  - Steep descent ($G \le G_{\text{coast}}$): freewheeling coasting drops cadence to $0 \text{ rpm}$.
- **Running Cadence**:
  - Modeled as steps per minute (SPM):
    $$SPM = 180 - (P - 300) \times 0.12$$
    bounded between $140$ and $205 \text{ SPM}$.

---

## 6. FIT Protocol Encoding & Validation

Binary generation conforms to the Garmin FIT Activity profile:
- Semicircle coordinates:
  $$\text{semicircles} = \operatorname{round}\left(\text{degrees} \times \frac{2^{31}}{180}\right)$$
- Strict monotonic timestamps and cumulative distances.
- In-memory validation parses the raw byte array with `Decode` and `MesgBroadcaster`, ensuring structure, record counts, and summary metrics match simulation results within tolerance before allowing file save or share.
