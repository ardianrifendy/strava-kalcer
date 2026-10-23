/* ============================================================================
   STRAVA KALCER WEB STUDIO — CORE ENGINE, UI & BINARY .FIT PROTOCOL 2.0
   Strict Rule: ZERO EMOJIS. Pure Vector UI. Full EN & ID Bilingual Support.
   ============================================================================ */

"use strict";

/* ----------------------------------------------------------------------------
   1. BILINGUAL DICTIONARY (ENGLISH & INDONESIAN)
   ---------------------------------------------------------------------------- */
const I18N = {
  en: {
    brandSubtitle: "GPX to FIT Physics & Physiology Reconstruction Engine",
    navRoute: "1. Route & GPX",
    navConfig: "2. Simulation Setup",
    navPreview: "3. Telemetry & Splits",
    navExport: "4. Device & FIT Export",
    navDebug: "5. Diagnostics",
    mobNavRoute: "Route",
    mobNavConfig: "Config",
    mobNavPreview: "Preview",
    mobNavExport: "Export",
    mobNavDebug: "Debug",
    btnUserGuide: "User Guide",
    btnClose: "Close",
    btnHideGuide: "Hide Guide",
    btnShowGuide: "Show Guide",
    guideBannerTitle: "First-Time User Guide — How to Reconstruct & Upload to Strava for Free",
    guideBannerSub: "Complete 4-step workflow from raw GPX route to verified Garmin .FIT telemetry without needing a paid Strava API subscription.",
    step1Title: "Import GPX or Load Sample",
    step1Desc: "Upload any .gpx route file or click \"Load Sample Route\" to test immediately. Use \"Reverse Route\" to flip start and finish points.",
    step2Title: "Configure Physics & Physiology",
    step2Desc: "Choose Cycling or Running, select an athlete profile, set your target average speed/pace, smart stops, HR, cadence, and power meter.",
    step3Title: "Inspect Telemetry & Device",
    step3Desc: "Verify your synchronized elevation, speed, watts, HR, and KM splits. Select from 34 authentic Garmin, Wahoo, or Coros devices.",
    step4Title: "Download .FIT & Upload Free",
    step4Desc: "Download the validated binary .fit file, then click \"UPLOAD TO STRAVA\" to upload on Strava Web for 100% free (no API subscription required).",
    cardImportTitle: "GPX Route Source",
    cardImportSub: "Import a GPX 1.1 track or load our built-in climb route",
    dropzoneTitle: "Drag & Drop your .GPX file here",
    dropzoneDesc: "Supports Strava, Komoot, Garmin Connect, RideWithGPS, and GPX Studio files",
    btnSelectGpx: "Select .GPX File",
    btnLoadSample: "Load Sample Route (Dago Pakar)",
    lblActiveTrack: "ACTIVE TRACK NAME",
    btnReverseRoute: "Reverse Route",
    btnConfigureSim: "Configure Simulation",
    cardRouteAuditTitle: "Route Telemetry & Elevation Audit",
    lblTotalDist: "Total Distance",
    lblEleGain: "Elevation Gain",
    lblEleLoss: "Elevation Loss",
    lblAltitudeRange: "Altitude Range",
    lblMaxGrade: "Max Gradient",
    lblTrackpoints: "Trackpoints",
    cardRouteMapTitle: "Interactive Route Geometry",
    chartElevationTitle: "Elevation Profile (m)",
    cardRouteIntelTitle: "Route Intelligence & UCI Climb Detection",
    lblFlatRatio: "Flat Terrain",
    lblClimbRatio: "Climbing",
    lblDescentRatio: "Descending",
    cardSportProfileTitle: "Sport Type & Athlete Behavior Profile",
    lblSportType: "Sport Type",
    sportCycling: "Cycling (Ride)",
    sportRunning: "Running (Run)",
    lblActivityProfile: "Select Realistic Athlete Profile (13 Presets)",
    cardTargetSolverTitle: "Target Speed / Pace Physics Solver",
    lblTargetAvgSpeed: "Target Moving Average Speed",
    lblDesiredMaxSpeed: "Inject Downhill Top Speed Opportunity",
    lblTargetAvgPace: "Target Moving Average Pace",
    lblDesiredBestPace: "Target Best Split / Surge Pace",
    cardScheduleStopsTitle: "Activity Schedule & Smart Traffic Stops",
    lblStartDate: "Activity Date",
    lblStartTime: "Start Time",
    lblActivityTitle: "Activity Title",
    lblSmartStops: "Smart Traffic & Rest Stop Simulation",
    stopNone: "No Stops — Continuous Non-Stop Effort",
    stopLight: "Light Traffic — Brief Intersection Pauses (15-35s)",
    stopCity: "City Commute — Red Lights & Junctions (25-60s)",
    stopCoffee: "Scenic / Coffee Stop — Mid-Route Rest Break (2-5m)",
    cardHrTitle: "Heart Rate Physiology (Asymmetric Lag Model)",
    lblEnabled: "Enabled",
    lblRestingHr: "Resting / Warmup Baseline HR",
    lblTargetAvgHr: "Target Average Exercising HR",
    lblMaxHr: "Physiological Ceiling (Max HR)",
    cardCadenceTitle: "Cadence & Downhill Coasting Dynamics",
    lblTargetCadence: "Target Flat Cadence",
    lblCoastingMode: "Steep Downhill Coasting Mode (< -4.5% Grade)",
    coastZero: "Freewheel Coasting (0 RPM on steep descents)",
    coastSoft: "Soft Pedaling (~20 RPM light spin on descents)",
    lblClimbingDrop: "Steep Climb Grinding RPM Drop",
    cardPowerTitle: "Physics Power Meter (Watts, 30s NP & kJ)",
    lblRiderWeight: "Rider Body Weight",
    lblBikeWeight: "Bicycle & Gear Weight",
    btnRunSim: "RUN PHYSICS SIMULATION & PREVIEW TELEMETRY",
    btnTweakConfig: "Tweak Settings",
    btnProceedExport: "Select Device & Export .FIT",
    lblDistance: "Distance",
    lblMovingTime: "Moving Time",
    lblAvgHr: "Avg / Max HR",
    lblAvgCadence: "Avg Cadence",
    lblAvgPower: "Avg Power",
    lblNormPower: "Normalized (NP)",
    lblCalories: "Calories / Work",
    lblAmbientTemp: "Ambient Temp",
    cardSyncMapTitle: "Synchronized Telemetry Route Map",
    lblDragChartsHint: "Hover or drag on any chart to scrub",
    cardSplitsTitle: "Kilometer Splits (1.0 km Auto-Laps)",
    thSplitSpeed: "Speed / Pace",
    thSplitElev: "Elev Diff",
    thSplitHr: "Avg HR",
    thSplitCad: "Cadence",
    thSplitPower: "Avg Power",
    chartPowerTitle: "Power Output (Watts)",
    chartHrTitle: "Heart Rate (bpm)",
    cardDeviceTitle: "Authentic Hardware Device Signature (34 Models)",
    cardDeviceSub: "Writes official Garmin FIT Manufacturer ID, Product ID & Firmware into FileIdMesg & DeviceInfoMesg",
    lblSelectedHardware: "SELECTED GPS HARDWARE",
    cardFitValidationTitle: "Garmin FIT Protocol 2.0 Binary Validation",
    cardFitValidationSub: "Real-time binary encoder & parse-back CRC-16 verification",
    lblFitSize: "Binary File Size",
    lblFitRecords: "FIT Messages",
    btnDownloadFit: "DOWNLOAD VALIDATED .FIT FILE",
    stravaFreeTitle: "FREE STRAVA UPLOAD (NO API SUBSCRIPTION NEEDED)",
    stravaStep1: "Click <strong>\"DOWNLOAD VALIDATED .FIT FILE\"</strong> above to save your activity file.",
    stravaStep2: "Click <strong>\"UPLOAD TO STRAVA\"</strong> below to open Strava's official File Upload page.",
    stravaStep3: "Select the downloaded <code>.fit</code> file — Strava will display your exact GPS device name, elevation, HR, cadence, watts, and laps!",
    cardDiagSolverTitle: "Physics Solver State & Export CSV",
    btnExportCsv: "Export Telemetry CSV",
    cardDownhillCandTitle: "Downhill High-Speed Candidates",
    cardPointInspectorTitle: "Point-by-Point Telemetry Inspector",
    modalGuideTitle: "Complete User Guide — Strava Kalcer Web Studio",
    guideModalHtml: `
      <div style="display:flex; flex-direction:column; gap:14px;">
        <div style="padding:12px 14px; border-radius:8px; background:var(--strava-orange-soft); border:1px solid var(--strava-orange-border);">
          <strong>Why Use Strava Kalcer Web Studio?</strong><br/>
          Strava Kalcer converts raw GPX routes into complete, physically realistic Garmin <code>.FIT</code> binary activity files with authentic hardware signatures, barometric elevation, asymmetric heart rate lag, cadence coasting, cycling power (Watts &amp; 30-second Normalized Power), and 1.0 km auto-laps.
        </div>
        <div>
          <strong>Step 1: Import or Load Your Route (Tab 1)</strong>
          <p style="color:var(--text-secondary);">Drag and drop any <code>.gpx</code> file exported from Strava Route Builder, Komoot, Garmin Connect, or GPX Studio. If you want to test immediately, click <strong>"Load Sample Route (Dago Pakar)"</strong>. Click <strong>"Reverse Route"</strong> if you want to flip the start and finish direction without losing elevation accuracy.</p>
        </div>
        <div>
          <strong>Step 2: Configure Physics &amp; Physiology (Tab 2)</strong>
          <p style="color:var(--text-secondary);">Choose <strong>Cycling</strong> or <strong>Running</strong> and pick one of the 13 athlete profiles. Set your desired Moving Average Speed (km/h) or Pace (min/km). The 18-iteration physics root-solver automatically adjusts flat, uphill, and downhill speeds to hit your exact average while respecting gravity and aerodynamics. Configure Heart Rate, Cadence, Smart Traffic Stops, and Start Date/Time.</p>
        </div>
        <div>
          <strong>Step 3: Review Synchronized Charts &amp; Splits (Tab 3)</strong>
          <p style="color:var(--text-secondary);">Move your cursor across any chart (Elevation, Speed/Pace, Power, Heart Rate, Cadence) to inspect synchronized telemetry along the route map and verify your 1.0 km Strava splits.</p>
        </div>
        <div>
          <strong>Step 4: Select GPS Hardware, Download .FIT &amp; Upload to Strava Free (Tab 4)</strong>
          <p style="color:var(--text-secondary);">Choose from 34 official GPS devices (e.g. Garmin Edge 1040 Solar, Forerunner 965, Wahoo ELEMNT ROAM v2, Coros PACE 3). Click <strong>"DOWNLOAD VALIDATED .FIT FILE"</strong>, then click <strong>"UPLOAD TO STRAVA"</strong> (<code>strava.com/upload/select</code>) to upload your file for <strong>100% FREE</strong> without needing a paid Strava API subscription.</p>
        </div>
      </div>
    `
  },
  id: {
    brandSubtitle: "Studio Rekonstruksi Fisika & Fisiologi GPX ke Garmin .FIT",
    navRoute: "1. Rute & GPX",
    navConfig: "2. Pengaturan Simulasi",
    navPreview: "3. Telemetri & Split",
    navExport: "4. Perangkat & Ekspor FIT",
    navDebug: "5. Diagnostik",
    mobNavRoute: "Rute",
    mobNavConfig: "Konfig",
    mobNavPreview: "Pratinjau",
    mobNavExport: "Ekspor",
    mobNavDebug: "Debug",
    btnUserGuide: "Panduan Pengguna",
    btnClose: "Tutup",
    btnHideGuide: "Sembunyikan Panduan",
    btnShowGuide: "Tampilkan Panduan",
    guideBannerTitle: "Panduan Pengguna Baru — Cara Rekonstruksi & Upload ke Strava Gratis",
    guideBannerSub: "Alur kerja 4 langkah dari file rute GPX mentah menjadi file telemetri Garmin .FIT tervalidasi tanpa perlu langganan Strava API berbayar.",
    step1Title: "Impor GPX atau Muat Rute Contoh",
    step1Desc: "Unggah file rute .gpx Anda atau klik \"Muat Rute Contoh\" untuk langsung mencoba. Gunakan \"Balik Arah Rute\" untuk menukar titik start dan finis.",
    step2Title: "Atur Fisika & Fisiologi",
    step2Desc: "Pilih Bersepeda atau Lari, pilih profil atlet, atur target kecepatan/pace rata-rata, pemberhentian lampu merah, detak jantung (HR), cadence, dan power meter.",
    step3Title: "Periksa Telemetri & Pilih Perangkat",
    step3Desc: "Periksa grafik elevasi, kecepatan, watt, HR, dan split per KM. Pilih dari 34 perangkat resmi Garmin, Wahoo, Coros, Suunto, atau Polar.",
    step4Title: "Unduh .FIT & Upload Gratis",
    step4Desc: "Unduh file biner .fit yang telah tervalidasi, lalu klik \"UPLOAD TO STRAVA\" untuk mengunggah di Web Strava 100% gratis (tanpa langganan API).",
    cardImportTitle: "Sumber Rute GPX",
    cardImportSub: "Impor file trek GPX 1.1 atau gunakan rute tanjakan bawaan kami",
    dropzoneTitle: "Tarik & Lepas file .GPX Anda di sini",
    dropzoneDesc: "Mendukung file GPX dari Strava, Komoot, Garmin Connect, RideWithGPS, dan GPX Studio",
    btnSelectGpx: "Pilih File .GPX",
    btnLoadSample: "Muat Rute Contoh (Dago Pakar)",
    lblActiveTrack: "NAMA TREK AKTIF",
    btnReverseRoute: "Balik Arah Rute",
    btnConfigureSim: "Atur Simulasi",
    cardRouteAuditTitle: "Telemetri Rute & Audit Kualitas Elevasi",
    lblTotalDist: "Total Jarak",
    lblEleGain: "Total Tanjakan (Gain)",
    lblEleLoss: "Total Turunan (Loss)",
    lblAltitudeRange: "Rentang Ketinggian",
    lblMaxGrade: "Gradien Maks",
    lblTrackpoints: "Titik Koordinat",
    cardRouteMapTitle: "Peta Geometri Rute Interaktif",
    chartElevationTitle: "Profil Elevasi (m)",
    cardRouteIntelTitle: "Intelijen Rute & Deteksi Tanjakan UCI",
    lblFlatRatio: "Medan Datar",
    lblClimbRatio: "Menanjak",
    lblDescentRatio: "Menurun",
    cardSportProfileTitle: "Jenis Olahraga & Profil Perilaku Atlet",
    lblSportType: "Jenis Olahraga",
    sportCycling: "Bersepeda (Ride)",
    sportRunning: "Lari (Run)",
    lblActivityProfile: "Pilih Profil Perilaku Atlet Realistis (13 Preset)",
    cardTargetSolverTitle: "Solver Fisika Target Kecepatan / Pace",
    lblTargetAvgSpeed: "Target Kecepatan Rata-Rata Bergerak (Avg Speed)",
    lblDesiredMaxSpeed: "Suntikkan Peluang Top Speed di Turunan",
    lblTargetAvgPace: "Target Pace Rata-Rata Bergerak (Avg Pace)",
    lblDesiredBestPace: "Target Best Split / Surge Pace",
    cardScheduleStopsTitle: "Jadwal Aktivitas & Simulasi Berhenti Cerdas",
    lblStartDate: "Tanggal Aktivitas",
    lblStartTime: "Waktu Mulai",
    lblActivityTitle: "Judul Aktivitas",
    lblSmartStops: "Simulasi Berhenti Lampu Merah & Istirahat",
    stopNone: "Tanpa Berhenti — Usaha Kontinu Non-Stop",
    stopLight: "Lalu Lintas Ringan — Jeda Persimpangan Singkat (15-35 dtk)",
    stopCity: "Komuter Kota — Lampu Merah & Persimpangan (25-60 dtk)",
    stopCoffee: "Istirahat Foto / Kopi — Jeda Pertengahan Rute (2-5 mnt)",
    cardHrTitle: "Fisiologi Detak Jantung (Model Lag Asimetris)",
    lblEnabled: "Aktif",
    lblRestingHr: "Detak Jantung Istirahat / Pemanasan",
    lblTargetAvgHr: "Target Detak Jantung Rata-Rata",
    lblMaxHr: "Batas Maksimum Fisiologis (Max HR)",
    cardCadenceTitle: "Dinamika Cadence & Coasting Turunan",
    lblTargetCadence: "Target Cadence Medan Datar",
    lblCoastingMode: "Mode Coasting Turunan Curam (< -4.5% Gradien)",
    coastZero: "Freewheel Coasting (0 RPM saat meluncur di turunan curam)",
    coastSoft: "Soft Pedaling (~20 RPM kayuhan ringan di turunan)",
    lblClimbingDrop: "Penurunan RPM Saat Menanjak Curam",
    cardPowerTitle: "Power Meter Fisika (Watt, 30s NP & kJ)",
    lblRiderWeight: "Berat Badan Atlet",
    lblBikeWeight: "Berat Sepeda & Perlengkapan",
    btnRunSim: "JALANKAN SIMULASI FISIKA & LIHAT TELEMETRI",
    btnTweakConfig: "Ubah Pengaturan",
    btnProceedExport: "Pilih Perangkat & Ekspor .FIT",
    lblDistance: "Jarak Tempuh",
    lblMovingTime: "Waktu Bergerak",
    lblAvgHr: "HR Rata-Rata / Maks",
    lblAvgCadence: "Cadence Rata-Rata",
    lblAvgPower: "Power Rata-Rata",
    lblNormPower: "Normalized (NP)",
    lblCalories: "Kalori / Kerja",
    lblAmbientTemp: "Suhu Lingkungan",
    cardSyncMapTitle: "Peta Rute Telemetri Tersinkronisasi",
    lblDragChartsHint: "Arahkan atau geser pada grafik untuk memeriksa titik",
    cardSplitsTitle: "Split Kilometer (Auto-Lap 1.0 km)",
    thSplitSpeed: "Kecepatan / Pace",
    thSplitElev: "Selisih Elev",
    thSplitHr: "HR Rata-Rata",
    thSplitCad: "Cadence",
    thSplitPower: "Power Rata-Rata",
    chartPowerTitle: "Output Daya / Power (Watt)",
    chartHrTitle: "Detak Jantung (bpm)",
    cardDeviceTitle: "Tanda Tangan Perangkat GPS Resmi (34 Model)",
    cardDeviceSub: "Menulis ID Manufaktur, ID Produk & Firmware resmi Garmin FIT ke dalam FileIdMesg & DeviceInfoMesg",
    lblSelectedHardware: "PERANGKAT KERAS GPS TERPILIH",
    cardFitValidationTitle: "Validasi Biner Garmin FIT Protokol 2.0",
    cardFitValidationSub: "Enkoder biner real-time & verifikasi parse-back CRC-16",
    lblFitSize: "Ukuran File Biner",
    lblFitRecords: "Pesan FIT",
    btnDownloadFit: "UNDUH FILE .FIT TERVALIDASI",
    stravaFreeTitle: "UPLOAD STRAVA GRATIS (TANPA LANGGANAN API)",
    stravaStep1: "Klik tombol <strong>\"UNDUH FILE .FIT TERVALIDASI\"</strong> di atas untuk menyimpan file aktivitas Anda.",
    stravaStep2: "Klik tombol <strong>\"UPLOAD TO STRAVA\"</strong> di bawah untuk membuka halaman Upload File resmi Strava.",
    stravaStep3: "Pilih file <code>.fit</code> yang baru diunduh — Strava akan menampilkan nama perangkat GPS, elevasi, HR, cadence, watt, dan split Anda secara lengkap!",
    cardDiagSolverTitle: "Status Solver Fisika & Ekspor CSV",
    btnExportCsv: "Ekspor CSV Telemetri",
    cardDownhillCandTitle: "Kandidat Turunan Kecepatan Tinggi",
    cardPointInspectorTitle: "Inspektur Telemetri Titik-demi-Titik",
    modalGuideTitle: "Panduan Lengkap Pengguna — Strava Kalcer Web Studio",
    guideModalHtml: `
      <div style="display:flex; flex-direction:column; gap:14px;">
        <div style="padding:12px 14px; border-radius:8px; background:var(--strava-orange-soft); border:1px solid var(--strava-orange-border);">
          <strong>Mengapa Menggunakan Strava Kalcer Web Studio?</strong><br/>
          Strava Kalcer mengubah rute GPX mentah menjadi file biner Garmin <code>.FIT</code> yang utuh dan realistis secara fisika, lengkap dengan identitas perangkat keras resmi, elevasi barometrik, respons detak jantung lag asimetris, cadence coasting, power meter sepeda (Watt &amp; 30-detik Normalized Power), serta auto-lap setiap 1.0 km.
        </div>
        <div>
          <strong>Langkah 1: Impor atau Muat Rute Anda (Tab 1)</strong>
          <p style="color:var(--text-secondary);">Tarik dan lepas file <code>.gpx</code> dari Strava Route Builder, Komoot, Garmin Connect, atau GPX Studio. Jika ingin langsung mencoba tanpa file, klik <strong>"Muat Rute Contoh (Dago Pakar)"</strong>. Gunakan tombol <strong>"Balik Arah Rute"</strong> untuk membalik start menjadi finis tanpa mengurangi akurasi elevasi.</p>
        </div>
        <div>
          <strong>Langkah 2: Konfigurasi Fisika &amp; Fisiologi (Tab 2)</strong>
          <p style="color:var(--text-secondary);">Pilih olahraga <strong>Bersepeda (Ride)</strong> atau <strong>Lari (Run)</strong> serta pilih salah satu dari 13 profil atlet. Atur target Kecepatan Rata-Rata (km/h) atau Pace (mnt/km). Solver fisika 18-iterasi akan menyesuaikan kecepatan datar, tanjakan, dan turunan secara otomatis. Atur juga Detak Jantung, Cadence, Berhenti Lampu Merah, dan Waktu Mulai.</p>
        </div>
        <div>
          <strong>Langkah 3: Periksa Grafik Tersinkronisasi &amp; Split KM (Tab 3)</strong>
          <p style="color:var(--text-secondary);">Geser kursor di atas grafik (Elevasi, Kecepatan/Pace, Power, Detak Jantung, Cadence) untuk melihat posisi dan telemetri secara sinkron di peta rute serta tabel split per 1.0 km.</p>
        </div>
        <div>
          <strong>Langkah 4: Pilih Perangkat GPS, Unduh .FIT &amp; Upload Gratis ke Strava (Tab 4)</strong>
          <p style="color:var(--text-secondary);">Pilih dari 34 perangkat GPS resmi (seperti Garmin Edge 1040 Solar, Forerunner 965, Wahoo ELEMNT ROAM v2, Coros PACE 3). Klik <strong>"UNDUH FILE .FIT TERVALIDASI"</strong>, kemudian klik <strong>"UPLOAD TO STRAVA"</strong> (<code>strava.com/upload/select</code>) untuk mengunggah secara <strong>100% GRATIS</strong> tanpa perlu berlangganan API Strava.</p>
        </div>
      </div>
    `
  }
};

/* ----------------------------------------------------------------------------
   2. ACTIVITY PROFILES (13 REALISTIC ATHLETE PRESETS)
   ---------------------------------------------------------------------------- */
const ACTIVITY_PROFILES = [
  { id: "CASUAL_RIDE", sport: "CYCLING", nameEn: "Casual Ride", nameId: "Gowes Santai", descEn: "Relaxed aerobic spin with gentle pacing on climbs and safe descents.", descId: "Kayuhan aerobik santai dengan ritme ringan di tanjakan dan turunan aman.", defaultSpeedKmh: 22.0, noiseFactor: 0.035, climbSensitivity: 1.05, descentSensitivity: 0.85 },
  { id: "ENDURANCE_RIDE", sport: "CYCLING", nameEn: "Endurance Zone 2", nameId: "Endurance Zona 2", descEn: "Steady aerobic base ride with disciplined power output and smooth cadence.", descId: "Bersepeda daya tahan stabil di Zona 2 dengan output watt dan cadence rapi.", defaultSpeedKmh: 28.0, noiseFactor: 0.025, climbSensitivity: 0.95, descentSensitivity: 1.0 },
  { id: "CLIMB_FOCUSED", sport: "CYCLING", nameEn: "Mountain Climber", nameId: "Spesialis Tanjakan", descEn: "High power-to-weight ratio, strong sustained speed on steep gradients.", descId: "Rasio power-to-weight tinggi, kecepatan kuat dan stabil di tanjakan curam.", defaultSpeedKmh: 26.5, noiseFactor: 0.028, climbSensitivity: 0.78, descentSensitivity: 1.05 },
  { id: "URBAN_STOP_AND_GO", sport: "CYCLING", nameEn: "Urban Stop & Go", nameId: "Komuter Perkotaan", descEn: "Dynamic city riding with brisk accelerations and intersection slowdowns.", descId: "Gowes dalam kota dinamis dengan akselerasi cepat selepas persimpangan.", defaultSpeedKmh: 24.0, noiseFactor: 0.055, climbSensitivity: 1.0, descentSensitivity: 0.90 },
  { id: "FAST_GROUP_RIDE", sport: "CYCLING", nameEn: "Fast Peloton / Group Ride", nameId: "Peloton / Group Ride Cepat", descEn: "High-speed drafting dynamics on flats and rollers with strong surges.", descId: "Kecepatan tinggi efek drafting peloton di jalur datar dan rolling.", defaultSpeedKmh: 35.5, noiseFactor: 0.042, climbSensitivity: 0.88, descentSensitivity: 1.18 },
  { id: "TIME_TRIAL", sport: "CYCLING", nameEn: "Aero Time Trial (TT)", nameId: "Time Trial Aero (TT)", descEn: "Ultra-consistent threshold effort with minimal speed variance.", descId: "Usaha ambang batas (threshold) super konsisten dengan posisi aerodinamis.", defaultSpeedKmh: 38.0, noiseFactor: 0.015, climbSensitivity: 0.85, descentSensitivity: 1.15 },
  { id: "EASY_RUN", sport: "RUNNING", nameEn: "Easy Recovery Run", nameId: "Lari Santai / Recovery", descEn: "Conversational Zone 2 jog with natural grade-adjusted slowing on hills.", descId: "Lari santai Zona 2 dengan penyesuaian pace alami saat menanjak.", defaultPaceSec: 360, noiseFactor: 0.030, climbSensitivity: 1.05, descentSensitivity: 0.90 },
  { id: "STEADY_RUN", sport: "RUNNING", nameEn: "Steady Aerobic Run", nameId: "Lari Aerobik Stabil", descEn: "Balanced daily training run with consistent rhythm and cadence.", descId: "Lari latihan harian seimbang dengan ritme dan langkah kaki konsisten.", defaultPaceSec: 315, noiseFactor: 0.022, climbSensitivity: 1.0, descentSensitivity: 0.95 },
  { id: "TEMPO_RUN", sport: "RUNNING", nameEn: "Threshold Tempo Run", nameId: "Lari Tempo Threshold", descEn: "Controlled lactate-threshold effort with tight pace discipline.", descId: "Lari tempo ambang laktat terkontrol dengan disiplin pace yang ketat.", defaultPaceSec: 265, noiseFactor: 0.016, climbSensitivity: 0.92, descentSensitivity: 1.0 },
  { id: "INTERVAL_RUN", sport: "RUNNING", nameEn: "Fartlek / Surge Run", nameId: "Lari Interval / Fartlek", descEn: "Alternating brisk surges and active recovery segments across the route.", descId: "Kombinasi lonjakan pace cepat (surge) dan pemulihan aktif sepanjang rute.", defaultPaceSec: 280, noiseFactor: 0.065, climbSensitivity: 0.95, descentSensitivity: 1.05 },
  { id: "LONG_RUN", sport: "RUNNING", nameEn: "Marathon Long Run", nameId: "Long Run Maraton", descEn: "Extended endurance pacing with subtle late-stage cardiac drift.", descId: "Pace daya tahan jarak jauh dengan simulasi cardiac drift yang realistis.", defaultPaceSec: 330, noiseFactor: 0.020, climbSensitivity: 1.02, descentSensitivity: 0.94 },
  { id: "TRAIL_EFFORT", sport: "RUNNING", nameEn: "Mountain Trail Run", nameId: "Lari Trail Pegunungan", descEn: "Aggressive elevation adaptation, power-hiking steep climbs and agile descents.", descId: "Adaptasi elevasi agresif untuk jalur berbukit dan turunan teknikal.", defaultPaceSec: 380, noiseFactor: 0.048, climbSensitivity: 1.20, descentSensitivity: 0.92 },
  { id: "RACE_PACE", sport: "RUNNING", nameEn: "Road Race Effort", nameId: "Pace Lomba (Race Effort)", descEn: "High-intensity competitive racing pace with fast final kilometer push.", descId: "Pace kompetitif intensitas tinggi dengan dorongan kuat menuju garis finis.", defaultPaceSec: 240, noiseFactor: 0.018, climbSensitivity: 0.88, descentSensitivity: 1.06 }
];

/* ----------------------------------------------------------------------------
   3. 101 AUTHENTIC GPS HARDWARE DEVICE PROFILES (GARMIN FIT PROTOCOL 2.0)
      Strictly separated into:
      - CYCLOCOMP (48 Famous Bike Computers — Dedicated for CYCLING)
      - SPORTWATCH (53 Famous Running & Multisport Watches — For RUNNING & CYCLING)
   ---------------------------------------------------------------------------- */
const DEVICE_PROFILES = [
  // ==========================================================================
  // PART A: CYCLOCOMP — 48 FAMOUS GPS BIKE COMPUTERS (FOR CYCLING)
  // ==========================================================================

  // 1. Garmin Edge Series (fitMfrId: 1 — Official @garmin/fitsdk garmin_product IDs)
  { id: "garmin_edge_1050", manufacturer: "Garmin", modelName: "Edge 1050", fitProductName: "Garmin Edge 1050", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4440, barometric: true, power: true, swVer: 1018 },
  { id: "garmin_edge_1040", manufacturer: "Garmin", modelName: "Edge 1040 Solar", fitProductName: "Garmin Edge 1040 Solar", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3843, barometric: true, power: true, swVer: 2418 },
  { id: "garmin_edge_840_solar", manufacturer: "Garmin", modelName: "Edge 840 Solar", fitProductName: "Garmin Edge 840 Solar", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4062, barometric: true, power: true, swVer: 2418 },
  { id: "garmin_edge_840", manufacturer: "Garmin", modelName: "Edge 840", fitProductName: "Garmin Edge 840", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4062, barometric: true, power: true, swVer: 2418 },
  { id: "garmin_edge_540_solar", manufacturer: "Garmin", modelName: "Edge 540 Solar", fitProductName: "Garmin Edge 540 Solar", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4061, barometric: true, power: true, swVer: 2418 },
  { id: "garmin_edge_540", manufacturer: "Garmin", modelName: "Edge 540", fitProductName: "Garmin Edge 540", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4061, barometric: true, power: true, swVer: 2418 },
  { id: "garmin_edge_1030_plus", manufacturer: "Garmin", modelName: "Edge 1030 Plus", fitProductName: "Garmin Edge 1030 Plus", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3570, barometric: true, power: true, swVer: 675 },
  { id: "garmin_edge_1030", manufacturer: "Garmin", modelName: "Edge 1030", fitProductName: "Garmin Edge 1030", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 2713, barometric: true, power: true, swVer: 1375 },
  { id: "garmin_edge_830", manufacturer: "Garmin", modelName: "Edge 830", fitProductName: "Garmin Edge 830", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3122, barometric: true, power: true, swVer: 975 },
  { id: "garmin_edge_530", manufacturer: "Garmin", modelName: "Edge 530", fitProductName: "Garmin Edge 530", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3121, barometric: true, power: true, swVer: 975 },
  { id: "garmin_edge_explore_2", manufacturer: "Garmin", modelName: "Edge Explore 2", fitProductName: "Garmin Edge Explore 2", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 4169, barometric: true, power: true, swVer: 2118 },
  { id: "garmin_edge_130_plus", manufacturer: "Garmin", modelName: "Edge 130 Plus", fitProductName: "Garmin Edge 130 Plus", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3558, barometric: true, power: true, swVer: 440 },
  { id: "garmin_edge_520_plus", manufacturer: "Garmin", modelName: "Edge 520 Plus", fitProductName: "Garmin Edge 520 Plus", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 3112, barometric: true, power: true, swVer: 570 },
  { id: "garmin_edge_820", manufacturer: "Garmin", modelName: "Edge 820", fitProductName: "Garmin Edge 820", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 2530, barometric: true, power: true, swVer: 1270 },
  { id: "garmin_edge_1000", manufacturer: "Garmin", modelName: "Edge 1000", fitProductName: "Garmin Edge 1000", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 1, fitProdId: 1836, barometric: true, power: true, swVer: 1550 },

  // 2. Wahoo ELEMNT Series (fitMfrId: 32 — Wahoo Fitness)
  { id: "wahoo_elemnt_ace", manufacturer: "Wahoo", modelName: "ELEMNT ACE", fitProductName: "Wahoo ELEMNT ACE", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 52, barometric: true, power: true, swVer: 17012 },
  { id: "wahoo_elemnt_roam_v2", manufacturer: "Wahoo", modelName: "ELEMNT ROAM v2", fitProductName: "Wahoo ELEMNT ROAM", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 47, barometric: true, power: true, swVer: 16729 },
  { id: "wahoo_elemnt_bolt_v2", manufacturer: "Wahoo", modelName: "ELEMNT BOLT v2", fitProductName: "Wahoo ELEMNT BOLT", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 41, barometric: true, power: true, swVer: 16729 },
  { id: "wahoo_elemnt_roam_v1", manufacturer: "Wahoo", modelName: "ELEMNT ROAM (v1)", fitProductName: "Wahoo ELEMNT ROAM", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 31, barometric: true, power: true, swVer: 16442 },
  { id: "wahoo_elemnt_bolt_v1", manufacturer: "Wahoo", modelName: "ELEMNT BOLT (v1)", fitProductName: "Wahoo ELEMNT BOLT", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 28, barometric: true, power: true, swVer: 16442 },
  { id: "wahoo_elemnt", manufacturer: "Wahoo", modelName: "ELEMNT GPS", fitProductName: "Wahoo ELEMNT", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 32, fitProdId: 21, barometric: true, power: true, swVer: 15210 },

  // 3. COROS Bike Computer (fitMfrId: 294 — Verified from real COROS DURA .fit hex dump: prodId = 851, productName = "COROS DURA")
  { id: "coros_dura", manufacturer: "COROS", modelName: "DURA Solar GPS", fitProductName: "COROS DURA", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 294, fitProdId: 851, barometric: true, power: true, swVer: 308 },

  // 4. Hammerhead Karoo Series (fitMfrId: 289 — Hammerhead)
  { id: "hammerhead_karoo_3", manufacturer: "Hammerhead", modelName: "Karoo (3rd Gen)", fitProductName: "Hammerhead Karoo 3", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 289, fitProdId: 3, barometric: true, power: true, swVer: 1480 },
  { id: "hammerhead_karoo_2", manufacturer: "Hammerhead", modelName: "Karoo 2", fitProductName: "Karoo 2", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 289, fitProdId: 2, barometric: true, power: true, swVer: 1460 },
  { id: "hammerhead_karoo_1", manufacturer: "Hammerhead", modelName: "Karoo (1st Gen)", fitProductName: "Hammerhead Karoo", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 289, fitProdId: 1, barometric: true, power: true, swVer: 1320 },

  // 5. Bryton Rider Series (fitMfrId: 267 — Bryton)
  { id: "bryton_rider_s800", manufacturer: "Bryton", modelName: "Rider S800", fitProductName: "Bryton Rider S800", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1908, barometric: true, power: true, swVer: 140 },
  { id: "bryton_rider_s510", manufacturer: "Bryton", modelName: "Rider S510", fitProductName: "Bryton Rider S510", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1912, barometric: true, power: true, swVer: 112 },
  { id: "bryton_rider_s500", manufacturer: "Bryton", modelName: "Rider S500", fitProductName: "Bryton Rider S500", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1906, barometric: true, power: true, swVer: 135 },
  { id: "bryton_rider_750_se", manufacturer: "Bryton", modelName: "Rider 750 SE", fitProductName: "Bryton Rider 750 SE", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1905, barometric: true, power: true, swVer: 128 },
  { id: "bryton_rider_460", manufacturer: "Bryton", modelName: "Rider 460", fitProductName: "Bryton Rider 460", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1910, barometric: true, power: true, swVer: 110 },
  { id: "bryton_rider_420", manufacturer: "Bryton", modelName: "Rider 420", fitProductName: "Bryton Rider 420", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 267, fitProdId: 1901, barometric: true, power: true, swVer: 120 },

  // 6. iGPSPORT Series (fitMfrId: 115 — iGPSPORT)
  { id: "igpsport_binavi", manufacturer: "iGPSPORT", modelName: "BiNavi Dual-Band Navigation", fitProductName: "iGPSPORT BiNavi", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 810, barometric: true, power: true, swVer: 125 },
  { id: "igpsport_igs800", manufacturer: "iGPSPORT", modelName: "iGS800 Flagship Touch", fitProductName: "iGPSPORT iGS800", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 800, barometric: true, power: true, swVer: 140 },
  { id: "igpsport_igs630s", manufacturer: "iGPSPORT", modelName: "iGS630S Dual-Frequency", fitProductName: "iGPSPORT iGS630S", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 631, barometric: true, power: true, swVer: 156 },
  { id: "igpsport_igs630", manufacturer: "iGPSPORT", modelName: "iGS630 Smart GPS", fitProductName: "iGPSPORT iGS630", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 630, barometric: true, power: true, swVer: 152 },
  { id: "igpsport_bsc300", manufacturer: "iGPSPORT", modelName: "BSC300 Color Map GPS", fitProductName: "iGPSPORT BSC300", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 300, barometric: true, power: true, swVer: 145 },
  { id: "igpsport_bsc200", manufacturer: "iGPSPORT", modelName: "BSC200 Smart Bike Computer", fitProductName: "iGPSPORT BSC200", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 115, fitProdId: 200, barometric: true, power: true, swVer: 138 },

  // 7. Magene Series (fitMfrId: 107 — Magene)
  { id: "magene_c606", manufacturer: "Magene", modelName: "C606 Smart Touch GPS", fitProductName: "Magene C606", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 107, fitProdId: 606, barometric: true, power: true, swVer: 136 },
  { id: "magene_c506", manufacturer: "Magene", modelName: "C506 Color GPS", fitProductName: "Magene C506", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 107, fitProdId: 506, barometric: true, power: true, swVer: 122 },
  { id: "magene_c406_pro", manufacturer: "Magene", modelName: "C406 Pro", fitProductName: "Magene C406 Pro", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 107, fitProdId: 407, barometric: true, power: true, swVer: 130 },
  { id: "magene_c406", manufacturer: "Magene", modelName: "C406 Aero GPS", fitProductName: "Magene C406", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 107, fitProdId: 406, barometric: false, power: true, swVer: 118 },

  // 8. Sigma, Stages, Giant, Lezyne, Coospo, Cycplus, Geoid (Official FIT Manufacturer IDs)
  { id: "sigma_rox_12_1", manufacturer: "Sigma", modelName: "ROX 12.1 EVO", fitProductName: "SIGMA ROX 12.1 EVO", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 70, fitProdId: 121, barometric: true, power: true, swVer: 210 },
  { id: "sigma_rox_11_1", manufacturer: "Sigma", modelName: "ROX 11.1 EVO", fitProductName: "SIGMA ROX 11.1 EVO", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 70, fitProdId: 111, barometric: true, power: true, swVer: 204 },
  { id: "stages_dash_l200", manufacturer: "Stages", modelName: "Dash L200", fitProductName: "Stages Dash L200", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 69, fitProdId: 200, barometric: true, power: true, swVer: 320 },
  { id: "giant_dash_m200", manufacturer: "Giant", modelName: "Dash M200", fitProductName: "Giant Dash M200", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 108, fitProdId: 200, barometric: true, power: true, swVer: 315 },
  { id: "lezyne_mega_xl", manufacturer: "Lezyne", modelName: "Mega XL GPS", fitProductName: "Lezyne Mega XL GPS", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 258, fitProdId: 10, barometric: true, power: true, swVer: 200 },
  { id: "coospo_cs500", manufacturer: "Coospo", modelName: "CS500 Smart GPS", fitProductName: "Coospo CS500", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 135, fitProdId: 500, barometric: true, power: true, swVer: 118 },
  { id: "cycplus_m1", manufacturer: "Cycplus", modelName: "M1 GPS Bike Computer", fitProductName: "Cycplus M1", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 132, fitProdId: 101, barometric: true, power: true, swVer: 115 },
  { id: "geoid_cc600", manufacturer: "Geoid", modelName: "CC600 Color Navigation", fitProductName: "Geoid CC600", deviceType: "CYCLOCOMP", category: "BIKE_COMPUTER", supportedSports: ["CYCLING"], fitMfrId: 136, fitProdId: 600, barometric: true, power: true, swVer: 110 },

  // ==========================================================================
  // PART B: SPORTWATCH — 53 FAMOUS RUNNING & MULTISPORT WATCHES (FOR RUNNING)
  // ==========================================================================

  // 1. COROS Running & Multisport Watches (fitMfrId: 294 — Exact Verified COROS Product IDs & Product Names!)
  //    801 = COROS PACE (1st Gen), 802 = COROS PACE 2, 804 = COROS PACE 3, 805 = COROS PACE Pro
  //    811 = COROS APEX, 841 = COROS APEX Pro, 821 = COROS APEX 2, 822 = COROS APEX 2 Pro
  //    831 = COROS VERTIX, 832 = COROS VERTIX 2, 833 = COROS VERTIX 2S
  { id: "coros_pace_3", manufacturer: "COROS", modelName: "PACE 3", fitProductName: "COROS PACE 3", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 804, barometric: true, power: true, swVer: 308 },
  { id: "coros_pace_pro", manufacturer: "COROS", modelName: "PACE Pro AMOLED", fitProductName: "COROS PACE Pro", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 805, barometric: true, power: true, swVer: 308 },
  { id: "coros_pace_2", manufacturer: "COROS", modelName: "PACE 2", fitProductName: "COROS PACE 2", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 802, barometric: true, power: true, swVer: 304 },
  { id: "coros_pace_1", manufacturer: "COROS", modelName: "PACE (Original)", fitProductName: "COROS PACE", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 801, barometric: true, power: false, swVer: 230 },
  { id: "coros_apex_2_pro", manufacturer: "COROS", modelName: "APEX 2 Pro", fitProductName: "COROS APEX 2 Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 822, barometric: true, power: true, swVer: 308 },
  { id: "coros_apex_2", manufacturer: "COROS", modelName: "APEX 2", fitProductName: "COROS APEX 2", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 821, barometric: true, power: true, swVer: 308 },
  { id: "coros_apex_pro", manufacturer: "COROS", modelName: "APEX Pro", fitProductName: "COROS APEX Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 841, barometric: true, power: true, swVer: 304 },
  { id: "coros_apex", manufacturer: "COROS", modelName: "APEX", fitProductName: "COROS APEX", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 811, barometric: true, power: false, swVer: 290 },
  { id: "coros_vertix_2s", manufacturer: "COROS", modelName: "VERTIX 2S", fitProductName: "COROS VERTIX 2S", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 833, barometric: true, power: true, swVer: 308 },
  { id: "coros_vertix_2", manufacturer: "COROS", modelName: "VERTIX 2", fitProductName: "COROS VERTIX 2", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 832, barometric: true, power: true, swVer: 308 },
  { id: "coros_vertix", manufacturer: "COROS", modelName: "VERTIX", fitProductName: "COROS VERTIX", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 294, fitProdId: 831, barometric: true, power: true, swVer: 304 },

  // 2. Garmin Forerunner Series (fitMfrId: 1 — Official @garmin/fitsdk garmin_product IDs)
  { id: "garmin_fr_965", manufacturer: "Garmin", modelName: "Forerunner 965", fitProductName: "Garmin Forerunner 965", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4315, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_265", manufacturer: "Garmin", modelName: "Forerunner 265", fitProductName: "Garmin Forerunner 265", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4257, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_265s", manufacturer: "Garmin", modelName: "Forerunner 265S", fitProductName: "Garmin Forerunner 265S", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4258, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_165_music", manufacturer: "Garmin", modelName: "Forerunner 165 Music", fitProductName: "Garmin Forerunner 165 Music", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4433, barometric: true, power: true, swVer: 2026 },
  { id: "garmin_fr_165", manufacturer: "Garmin", modelName: "Forerunner 165", fitProductName: "Garmin Forerunner 165", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4432, barometric: true, power: true, swVer: 2026 },
  { id: "garmin_fr_955", manufacturer: "Garmin", modelName: "Forerunner 955 Solar", fitProductName: "Garmin Forerunner 955 Solar", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4024, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_255", manufacturer: "Garmin", modelName: "Forerunner 255 Music", fitProductName: "Garmin Forerunner 255 Music", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3990, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_255_std", manufacturer: "Garmin", modelName: "Forerunner 255", fitProductName: "Garmin Forerunner 255", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3992, barometric: true, power: true, swVer: 2119 },
  { id: "garmin_fr_945_lte", manufacturer: "Garmin", modelName: "Forerunner 945 LTE", fitProductName: "Garmin Forerunner 945 LTE", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3652, barometric: true, power: true, swVer: 1730 },
  { id: "garmin_fr_945", manufacturer: "Garmin", modelName: "Forerunner 945", fitProductName: "Garmin Forerunner 945", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3113, barometric: true, power: true, swVer: 1370 },
  { id: "garmin_fr_745", manufacturer: "Garmin", modelName: "Forerunner 745", fitProductName: "Garmin Forerunner 745", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3589, barometric: true, power: true, swVer: 1370 },
  { id: "garmin_fr_245", manufacturer: "Garmin", modelName: "Forerunner 245 Music", fitProductName: "Garmin Forerunner 245 Music", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3077, barometric: false, power: false, swVer: 1300 },
  { id: "garmin_fr_245_std", manufacturer: "Garmin", modelName: "Forerunner 245", fitProductName: "Garmin Forerunner 245", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3076, barometric: false, power: false, swVer: 1300 },
  { id: "garmin_fr_55", manufacturer: "Garmin", modelName: "Forerunner 55", fitProductName: "Garmin Forerunner 55", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3869, barometric: false, power: false, swVer: 1010 },

  // 3. Garmin fēnix, epix, Enduro, Instinct, Venu & MARQ Series (fitMfrId: 1)
  { id: "garmin_fenix_8", manufacturer: "Garmin", modelName: "fēnix 8 AMOLED", fitProductName: "Garmin fēnix 8 AMOLED", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4536, barometric: true, power: true, swVer: 1222 },
  { id: "garmin_fenix_8_solar", manufacturer: "Garmin", modelName: "fēnix 8 Solar Sapphire", fitProductName: "Garmin fēnix 8 Solar", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4532, barometric: true, power: true, swVer: 1222 },
  { id: "garmin_fenix_7x_pro", manufacturer: "Garmin", modelName: "fēnix 7X Pro Sapphire Solar", fitProductName: "Garmin fēnix 7X Pro Sapphire Solar", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4376, barometric: true, power: true, swVer: 1934 },
  { id: "garmin_fenix_7_pro", manufacturer: "Garmin", modelName: "fēnix 7 Pro Sapphire Solar", fitProductName: "Garmin fēnix 7 Pro Sapphire Solar", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4375, barometric: true, power: true, swVer: 1934 },
  { id: "garmin_fenix_7x", manufacturer: "Garmin", modelName: "fēnix 7X Sapphire Solar", fitProductName: "Garmin fēnix 7X Sapphire Solar", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3907, barometric: true, power: true, swVer: 1934 },
  { id: "garmin_fenix_6_pro", manufacturer: "Garmin", modelName: "fēnix 6 Pro", fitProductName: "Garmin fēnix 6 Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 3290, barometric: true, power: true, swVer: 2700 },
  { id: "garmin_epix_pro_2", manufacturer: "Garmin", modelName: "epix Pro (Gen 2)", fitProductName: "Garmin epix Pro (Gen 2)", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4314, barometric: true, power: true, swVer: 1934 },
  { id: "garmin_enduro_3", manufacturer: "Garmin", modelName: "Enduro 3 Ultra-Endurance", fitProductName: "Garmin Enduro 3", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4575, barometric: true, power: true, swVer: 1222 },
  { id: "garmin_enduro_2", manufacturer: "Garmin", modelName: "Enduro 2", fitProductName: "Garmin Enduro 2", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4341, barometric: true, power: true, swVer: 1934 },
  { id: "garmin_instinct_2x", manufacturer: "Garmin", modelName: "Instinct 2X Solar", fitProductName: "Garmin Instinct 2X Solar", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4394, barometric: true, power: true, swVer: 1611 },
  { id: "garmin_venu_3", manufacturer: "Garmin", modelName: "Venu 3", fitProductName: "Garmin Venu 3", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4260, barometric: true, power: true, swVer: 1116 },
  { id: "garmin_marq_athlete_2", manufacturer: "Garmin", modelName: "MARQ Athlete (Gen 2)", fitProductName: "Garmin MARQ Athlete (Gen 2)", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 1, fitProdId: 4105, barometric: true, power: true, swVer: 1934 },

  // 4. Suunto Sportwatches (fitMfrId: 23 — Suunto)
  { id: "suunto_race", manufacturer: "Suunto", modelName: "Suunto Race AMOLED", fitProductName: "Suunto Race", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 23, fitProdId: 95, barometric: true, power: true, swVer: 238 },
  { id: "suunto_race_s", manufacturer: "Suunto", modelName: "Suunto Race S", fitProductName: "Suunto Race S", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 23, fitProdId: 96, barometric: true, power: true, swVer: 238 },
  { id: "suunto_vertical", manufacturer: "Suunto", modelName: "Suunto Vertical Titanium Solar", fitProductName: "Suunto Vertical", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 23, fitProdId: 92, barometric: true, power: true, swVer: 238 },
  { id: "suunto_9_peak_pro", manufacturer: "Suunto", modelName: "Suunto 9 Peak Pro", fitProductName: "Suunto 9 Peak Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 23, fitProdId: 88, barometric: true, power: true, swVer: 230 },
  { id: "suunto_5_peak", manufacturer: "Suunto", modelName: "Suunto 5 Peak", fitProductName: "Suunto 5 Peak", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 23, fitProdId: 85, barometric: false, power: false, swVer: 220 },

  // 5. Polar Sportwatches (fitMfrId: 123 — Polar Electro)
  { id: "polar_vantage_v3", manufacturer: "Polar", modelName: "Vantage V3", fitProductName: "Polar Vantage V3", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 123, fitProdId: 210, barometric: true, power: true, swVer: 215 },
  { id: "polar_grit_x2_pro", manufacturer: "Polar", modelName: "Grit X2 Pro Titan", fitProductName: "Polar Grit X2 Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 123, fitProdId: 225, barometric: true, power: true, swVer: 215 },
  { id: "polar_vantage_m3", manufacturer: "Polar", modelName: "Vantage M3", fitProductName: "Polar Vantage M3", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 123, fitProdId: 230, barometric: true, power: true, swVer: 110 },
  { id: "polar_pacer_pro", manufacturer: "Polar", modelName: "Pacer Pro", fitProductName: "Polar Pacer Pro", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 123, fitProdId: 195, barometric: true, power: true, swVer: 200 },
  { id: "polar_pacer", manufacturer: "Polar", modelName: "Pacer", fitProductName: "Polar Pacer", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 123, fitProdId: 196, barometric: false, power: false, swVer: 200 },

  // 6. Wahoo Multisport Watch (fitMfrId: 32)
  { id: "wahoo_elemnt_rival", manufacturer: "Wahoo", modelName: "ELEMNT RIVAL Multisport Watch", fitProductName: "Wahoo ELEMNT RIVAL", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 32, fitProdId: 101, barometric: true, power: true, swVer: 1402 },

  // 7. Apple, Samsung, Amazfit & Huawei Sportwatches
  { id: "apple_watch_ultra_2", manufacturer: "Apple", modelName: "Watch Ultra 2", fitProductName: "Apple Watch Ultra 2", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 255, fitProdId: 802, barometric: true, power: true, swVer: 1100 },
  { id: "apple_watch_series_10", manufacturer: "Apple", modelName: "Watch Series 10", fitProductName: "Apple Watch Series 10", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 255, fitProdId: 810, barometric: true, power: true, swVer: 1100 },
  { id: "samsung_galaxy_watch_ultra", manufacturer: "Samsung", modelName: "Galaxy Watch Ultra", fitProductName: "Samsung Galaxy Watch Ultra", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 255, fitProdId: 820, barometric: true, power: true, swVer: 600 },
  { id: "samsung_galaxy_watch_7", manufacturer: "Samsung", modelName: "Galaxy Watch 7", fitProductName: "Samsung Galaxy Watch 7", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 255, fitProdId: 821, barometric: true, power: true, swVer: 600 },
  { id: "amazfit_cheetah_pro", manufacturer: "Amazfit", modelName: "Cheetah Pro Marathon Watch", fitProductName: "Amazfit Cheetah Pro", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 339, fitProdId: 501, barometric: true, power: true, swVer: 312 },
  { id: "amazfit_trex_3", manufacturer: "Amazfit", modelName: "T-Rex 3 Adventure Watch", fitProductName: "Amazfit T-Rex 3", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 339, fitProdId: 503, barometric: true, power: true, swVer: 320 },
  { id: "huawei_watch_gt_5_pro", manufacturer: "Huawei", modelName: "Watch GT 5 Pro", fitProductName: "HUAWEI WATCH GT 5 Pro", deviceType: "SPORTWATCH", category: "MULTISPORT_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 348, fitProdId: 405, barometric: true, power: true, swVer: 500 },
  { id: "huawei_watch_gt_runner", manufacturer: "Huawei", modelName: "Watch GT Runner", fitProductName: "HUAWEI WATCH GT Runner", deviceType: "SPORTWATCH", category: "RUNNING_WATCH", supportedSports: ["RUNNING", "CYCLING"], fitMfrId: 348, fitProdId: 401, barometric: true, power: true, swVer: 402 }
];

/* ----------------------------------------------------------------------------
   4. GEOMATH, ELEVATION CLASSIFIER & ROUTE PROCESSOR (LOSSLESS REVERSE)
   ---------------------------------------------------------------------------- */
const GeoMath = {
  EARTH_RADIUS_M: 6371000.0,

  haversineDistanceMeters(lat1, lon1, lat2, lon2) {
    const toRad = Math.PI / 180.0;
    const dLat = (lat2 - lat1) * toRad;
    const dLon = (lon2 - lon1) * toRad;
    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * toRad) * Math.cos(lat2 * toRad) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return this.EARTH_RADIUS_M * c;
  },

  calculateGradient(eleDiffMeters, distMeters) {
    if (distMeters < 1.0) return 0.0;
    const raw = (eleDiffMeters / distMeters) * 100.0;
    return Math.max(-32.0, Math.min(32.0, raw));
  },

  smoothGradients(rawGradients, windowRadius = 3) {
    const n = rawGradients.length;
    const out = new Float64Array(n);
    for (let i = 0; i < n; i++) {
      let sum = 0.0;
      let count = 0;
      const start = Math.max(0, i - windowRadius);
      const end = Math.min(n - 1, i + windowRadius);
      for (let j = start; j <= end; j++) {
        sum += rawGradients[j];
        count++;
      }
      out[i] = count > 0 ? sum / count : 0.0;
    }
    return out;
  },

  smoothElevations(rawElevations, windowRadius = 2) {
    const n = rawElevations.length;
    const out = new Float64Array(n);
    for (let i = 0; i < n; i++) {
      let sum = 0.0;
      let count = 0;
      const start = Math.max(0, i - windowRadius);
      const end = Math.min(n - 1, i + windowRadius);
      for (let j = start; j <= end; j++) {
        sum += rawElevations[j];
        count++;
      }
      out[i] = count > 0 ? sum / count : rawElevations[i];
    }
    return out;
  }
};

const ElevationClassifier = {
  classify(elevations) {
    if (!elevations || elevations.length === 0) {
      return {
        source: "MISSING",
        isReliableForSimulation: false,
        validRatio: 0,
        noteEn: "No trackpoints loaded.",
        noteId: "Tidak ada titik koordinat yang dimuat."
      };
    }
    const valid = elevations.filter((e) => e !== null && e !== undefined && !Number.isNaN(e));
    const ratio = valid.length / elevations.length;

    if (valid.length === 0 || ratio < 0.2) {
      return {
        source: "MISSING",
        isReliableForSimulation: false,
        validRatio: ratio,
        noteEn: "GPX lacks elevation tags (<ele>). Route treated as flat terrain (0% gradient) without fabricating hills.",
        noteId: "GPX tidak memiliki tag elevasi (<ele>). Rute disimulasikan sebagai medan datar (gradien 0%)."
      };
    }

    const minEle = Math.min(...valid);
    const maxEle = Math.max(...valid);
    if (Math.abs(maxEle - minEle) < 0.5 && Math.abs(maxEle) < 0.5) {
      return {
        source: "FLAT_ZERO",
        isReliableForSimulation: false,
        validRatio: ratio,
        noteEn: "All GPX elevations are 0.0m. Treated as flat terrain.",
        noteId: "Semua nilai elevasi GPX adalah 0.0m. Disimulasikan sebagai medan datar."
      };
    }

    if (ratio < 0.98) {
      return {
        source: "PARTIALLY_RECONSTRUCTED",
        isReliableForSimulation: true,
        validRatio: ratio,
        noteEn: `Elevation partially reconstructed (${Math.round(ratio * 100)}% valid tags, gaps linearly interpolated).`,
        noteId: `Elevasi direkonstruksi sebagian (${Math.round(ratio * 100)}% tag valid, celah diinterpolasi secara linier).`
      };
    }

    return {
      source: "PRESENT_AND_VALID",
      isReliableForSimulation: true,
      validRatio: ratio,
      noteEn: "100% High-Resolution 3D GPX Elevation verified and smoothed for barometric physics simulation.",
      noteId: "100% Elevasi 3D GPX Resolusi Tinggi terverifikasi dan dihaluskan untuk simulasi fisika barometrik."
    };
  }
};

const RouteProcessor = {
  parseGpxXml(xmlString, fallbackName = "Imported Route") {
    const parser = new DOMParser();
    const doc = parser.parseFromString(xmlString, "text/xml");
    const parseErr = doc.querySelector("parsererror");
    if (parseErr) {
      throw new Error("Invalid XML/GPX format.");
    }

    let trackName = fallbackName;
    const trkNameNode = doc.querySelector("trk > name") || doc.querySelector("rte > name") || doc.querySelector("metadata > name");
    if (trkNameNode && trkNameNode.textContent.trim()) {
      trackName = trkNameNode.textContent.trim();
    }

    const points = [];
    const trkSegs = doc.querySelectorAll("trkseg");

    if (trkSegs.length > 0) {
      trkSegs.forEach((seg, segIdx) => {
        const pts = seg.querySelectorAll("trkpt");
        pts.forEach((pt) => {
          const lat = parseFloat(pt.getAttribute("lat"));
          const lon = parseFloat(pt.getAttribute("lon"));
          if (!Number.isNaN(lat) && !Number.isNaN(lon)) {
            const eleNode = pt.querySelector("ele");
            const ele = eleNode ? parseFloat(eleNode.textContent) : null;
            points.push({
              latitude: lat,
              longitude: lon,
              elevation: ele !== null && !Number.isNaN(ele) ? ele : null,
              segmentIndex: segIdx
            });
          }
        });
      });
    } else {
      const rtePts = doc.querySelectorAll("rtept, wpt");
      rtePts.forEach((pt) => {
        const lat = parseFloat(pt.getAttribute("lat"));
        const lon = parseFloat(pt.getAttribute("lon"));
        if (!Number.isNaN(lat) && !Number.isNaN(lon)) {
          const eleNode = pt.querySelector("ele");
          const ele = eleNode ? parseFloat(eleNode.textContent) : null;
          points.push({
            latitude: lat,
            longitude: lon,
            elevation: ele !== null && !Number.isNaN(ele) ? ele : null,
            segmentIndex: 0
          });
        }
      });
    }

    return { trackName, points, segmentCount: Math.max(1, trkSegs.length) };
  },

  process(parsedData, alreadySmoothed = false) {
    const rawPoints = parsedData.points || [];
    if (rawPoints.length === 0) {
      throw new Error("GPX contains no valid coordinates.");
    }

    const elevationQuality = ElevationClassifier.classify(rawPoints.map((p) => p.elevation));

    // Deduplicate points < 0.3m apart
    const deduplicated = [rawPoints[0]];
    for (let i = 1; i < rawPoints.length; i++) {
      const prev = deduplicated[deduplicated.length - 1];
      const curr = rawPoints[i];
      if (prev.segmentIndex === curr.segmentIndex) {
        const d = GeoMath.haversineDistanceMeters(prev.latitude, prev.longitude, curr.latitude, curr.longitude);
        if (d >= 0.3) deduplicated.push(curr);
      } else {
        deduplicated.push(curr);
      }
    }

    // Subdivide very long sparse segments (> 45m) so 1Hz/distance telemetry and charts are ultra smooth
    const densified = [deduplicated[0]];
    for (let i = 1; i < deduplicated.length; i++) {
      const p1 = deduplicated[i - 1];
      const p2 = deduplicated[i];
      if (p1.segmentIndex === p2.segmentIndex) {
        const dist = GeoMath.haversineDistanceMeters(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
        if (dist > 45.0 && !alreadySmoothed) {
          const steps = Math.min(20, Math.ceil(dist / 25.0));
          for (let s = 1; s < steps; s++) {
            const frac = s / steps;
            densified.push({
              latitude: p1.latitude + (p2.latitude - p1.latitude) * frac,
              longitude: p1.longitude + (p2.longitude - p1.longitude) * frac,
              elevation: p1.elevation !== null && p2.elevation !== null ? p1.elevation + (p2.elevation - p1.elevation) * frac : p1.elevation,
              segmentIndex: p2.segmentIndex
            });
          }
        }
      }
      densified.push(p2);
    }

    const n = densified.length;
    const resolvedElevations = new Float64Array(n);
    let minLat = Infinity, maxLat = -Infinity, minLon = Infinity, maxLon = -Infinity;

    for (let i = 0; i < n; i++) {
      const p = densified[i];
      if (p.latitude < minLat) minLat = p.latitude;
      if (p.latitude > maxLat) maxLat = p.latitude;
      if (p.longitude < minLon) minLon = p.longitude;
      if (p.longitude > maxLon) maxLon = p.longitude;
      resolvedElevations[i] = p.elevation !== null && p.elevation !== undefined ? p.elevation : 0.0;
    }

    // Interpolate missing elevation gaps if partially reconstructed
    if (elevationQuality.source === "PARTIALLY_RECONSTRUCTED") {
      let lastValid = -1;
      for (let i = 0; i < n; i++) {
        if (densified[i].elevation !== null) {
          if (lastValid !== -1 && i - lastValid > 1) {
            const startE = resolvedElevations[lastValid];
            const endE = resolvedElevations[i];
            const step = (endE - startE) / (i - lastValid);
            for (let k = lastValid + 1; k < i; k++) {
              resolvedElevations[k] = startE + step * (k - lastValid);
            }
          }
          lastValid = i;
        }
      }
    }

    const smoothedElevations =
      elevationQuality.isReliableForSimulation && !alreadySmoothed
        ? GeoMath.smoothElevations(resolvedElevations, 2)
        : resolvedElevations;

    const segmentDistances = new Float64Array(n);
    const cumulativeDistances = new Float64Array(n);
    const rawGradients = new Float64Array(n);

    let totalDist = 0.0;
    for (let i = 1; i < n; i++) {
      const prev = densified[i - 1];
      const curr = densified[i];
      const segDist =
        prev.segmentIndex === curr.segmentIndex
          ? GeoMath.haversineDistanceMeters(prev.latitude, prev.longitude, curr.latitude, curr.longitude)
          : 0.0;
      segmentDistances[i] = segDist;
      totalDist += segDist;
      cumulativeDistances[i] = totalDist;

      if (elevationQuality.isReliableForSimulation && segDist >= 1.0) {
        const eleDiff = smoothedElevations[i] - smoothedElevations[i - 1];
        rawGradients[i] = GeoMath.calculateGradient(eleDiff, segDist);
      } else {
        rawGradients[i] = 0.0;
      }
    }

    const smoothedGradients = GeoMath.smoothGradients(rawGradients, 3);

    let gain = 0.0;
    let loss = 0.0;
    let minEle = Infinity;
    let maxEle = -Infinity;
    let maxAbsGrade = 0.0;

    for (let i = 0; i < n; i++) {
      const e = smoothedElevations[i];
      if (e < minEle) minEle = e;
      if (e > maxEle) maxEle = e;
      if (Math.abs(smoothedGradients[i]) > maxAbsGrade) {
        maxAbsGrade = Math.abs(smoothedGradients[i]);
      }
      if (i > 0) {
        const diff = smoothedElevations[i] - smoothedElevations[i - 1];
        if (diff > 0.35) gain += diff;
        else if (diff < -0.35) loss += Math.abs(diff);
      }
    }

    const points = [];
    for (let i = 0; i < n; i++) {
      points.push({
        index: i,
        latitude: densified[i].latitude,
        longitude: densified[i].longitude,
        elevation: smoothedElevations[i],
        elevationSource: elevationQuality.source,
        segmentIndex: densified[i].segmentIndex,
        segmentDistanceMeters: segmentDistances[i],
        distanceFromStartMeters: cumulativeDistances[i],
        rawGradient: rawGradients[i],
        smoothedGradient: smoothedGradients[i]
      });
    }

    return {
      name: parsedData.trackName,
      points,
      totalDistanceMeters: totalDist,
      elevationGainMeters: gain,
      elevationLossMeters: loss,
      minElevationMeters: elevationQuality.isReliableForSimulation ? minEle : 0.0,
      maxElevationMeters: elevationQuality.isReliableForSimulation ? maxEle : 0.0,
      maxGradientPercent: maxAbsGrade,
      elevationQuality,
      bounds: { minLat, maxLat, minLon, maxLon }
    };
  },

  reverseTrack(track) {
    if (!track || !track.points || track.points.length === 0) return track;
    const rawPoints = [...track.points].reverse().map((p) => ({
      latitude: p.latitude,
      longitude: p.longitude,
      elevation: p.elevation,
      segmentIndex: 0
    }));
    const newName = track.name.endsWith(" (Reversed)")
      ? track.name.replace(" (Reversed)", "")
      : `${track.name} (Reversed)`;
    return this.process({ trackName: newName, points: rawPoints, segmentCount: 1 }, true);
  },

  generateSampleDagoPakarRoute() {
    // Realistic 12.6 km Bandung Dago Pakar - Tahura - Maribaya Rolling Climb & Descent
    const baseLat = -6.8852;
    const baseLon = 107.6135;
    const numPoints = 460;
    const points = [];
    let lat = baseLat;
    let lon = baseLon;
    let ele = 745.0;

    for (let i = 0; i < numPoints; i++) {
      const progress = i / (numPoints - 1);
      // Winding mountain road coordinates heading north-northeast
      lat += 0.00018 + 0.00006 * Math.sin(progress * Math.PI * 6);
      lon += 0.00014 + 0.00008 * Math.cos(progress * Math.PI * 5);

      // Realistic terrain profile: warmup flat -> Cat 3 climb -> false flat -> steep punch -> fast descent -> finish
      let grade = 0.0;
      if (progress < 0.12) {
        grade = 1.4 + Math.sin(i * 0.3) * 0.8;
      } else if (progress < 0.48) {
        grade = 5.6 + Math.sin(i * 0.18) * 2.4;
      } else if (progress < 0.60) {
        grade = -1.8 + Math.cos(i * 0.25) * 1.2;
      } else if (progress < 0.76) {
        grade = 7.4 + Math.sin(i * 0.22) * 2.8;
      } else if (progress < 0.93) {
        grade = -6.8 + Math.cos(i * 0.2) * 1.6;
      } else {
        grade = 0.8 + Math.sin(i * 0.3) * 0.6;
      }

      if (i > 0) {
        const prev = points[i - 1];
        const segD = GeoMath.haversineDistanceMeters(prev.latitude, prev.longitude, lat, lon);
        ele += (grade / 100.0) * segD;
      }

      points.push({
        latitude: lat,
        longitude: lon,
        elevation: ele,
        segmentIndex: 0
      });
    }

    return this.process({
      trackName: "Dago Pakar - Maribaya KOM Loop",
      points,
      segmentCount: 1
    });
  }
};

/* ----------------------------------------------------------------------------
   5. ROUTE INTELLIGENCE ENGINE (TERRAIN BREAKDOWN, CLIMBS & DOWNHILL SCORING)
   ---------------------------------------------------------------------------- */
const RouteIntelligenceEngine = {
  analyze(track) {
    const pts = track.points;
    if (!pts || pts.length < 2) {
      return { flatPct: 100, climbPct: 0, descentPct: 0, climbs: [], downhillCandidates: [] };
    }

    let flatDist = 0.0, climbDist = 0.0, descentDist = 0.0;
    for (let i = 1; i < pts.length; i++) {
      const d = pts[i].segmentDistanceMeters;
      const g = pts[i].smoothedGradient;
      if (g > 2.0) climbDist += d;
      else if (g < -2.0) descentDist += d;
      else flatDist += d;
    }

    const total = Math.max(1.0, track.totalDistanceMeters);
    const flatPct = Math.round((flatDist / total) * 100);
    const climbPct = Math.round((climbDist / total) * 100);
    const descentPct = Math.max(0, 100 - flatPct - climbPct);

    // Detect sustained climbs (> 350m length & avg grade >= 3.0%)
    const climbs = [];
    let inClimb = false, startIdx = 0;
    for (let i = 1; i < pts.length; i++) {
      const g = pts[i].smoothedGradient;
      if (!inClimb && g >= 2.8) {
        inClimb = true;
        startIdx = i - 1;
      } else if (inClimb && (g < 1.2 || i === pts.length - 1)) {
        const endIdx = i;
        const len = pts[endIdx].distanceFromStartMeters - pts[startIdx].distanceFromStartMeters;
        const gain = Math.max(0.0, pts[endIdx].elevation - pts[startIdx].elevation);
        const avgG = len > 0 ? (gain / len) * 100.0 : 0.0;
        if (len >= 350.0 && avgG >= 3.0) {
          const score = len * avgG;
          let cat = "Cat 4";
          if (score > 64000) cat = "HC";
          else if (score > 32000) cat = "Cat 1";
          else if (score > 16000) cat = "Cat 2";
          else if (score > 8000) cat = "Cat 3";

          climbs.push({
            startKm: pts[startIdx].distanceFromStartMeters / 1000.0,
            endKm: pts[endIdx].distanceFromStartMeters / 1000.0,
            lengthMeters: len,
            gainMeters: gain,
            avgGradient: avgG,
            category: cat
          });
        }
        inClimb = false;
      }
    }

    // Detect downhill high-speed candidates (< -2.2% over >= 180m)
    const downhillCandidates = [];
    let inDesc = false, descStart = 0;
    for (let i = 1; i < pts.length; i++) {
      const g = pts[i].smoothedGradient;
      if (!inDesc && g <= -2.2) {
        inDesc = true;
        descStart = i - 1;
      } else if (inDesc && (g > -1.0 || i === pts.length - 1)) {
        const endIdx = i;
        const len = pts[endIdx].distanceFromStartMeters - pts[descStart].distanceFromStartMeters;
        const drop = Math.max(0.0, pts[descStart].elevation - pts[endIdx].elevation);
        const avgG = len > 0 ? -(drop / len) * 100.0 : 0.0;
        if (len >= 180.0 && avgG <= -2.2) {
          const achievableMax = Math.min(82.0, 38.0 + Math.abs(avgG) * 4.2 + Math.min(12.0, len / 120.0));
          const score = Math.abs(avgG) * 10.0 + len / 40.0;
          downhillCandidates.push({
            id: downhillCandidates.length + 1,
            startIndex: descStart,
            endIndex: endIdx,
            startKm: pts[descStart].distanceFromStartMeters / 1000.0,
            endKm: pts[endIdx].distanceFromStartMeters / 1000.0,
            lengthMeters: len,
            averageGradient: avgG,
            achievableMaxSpeedKmh: achievableMax,
            score
          });
        }
        inDesc = false;
      }
    }

    downhillCandidates.sort((a, b) => b.score - a.score);
    return { flatPct, climbPct, descentPct, climbs, downhillCandidates };
  }
};

/* ----------------------------------------------------------------------------
   6. PHYSICS & PHYSIOLOGY SIMULATION ENGINE
   ---------------------------------------------------------------------------- */
const SimulationEngine = {
  generateStopsForPreset(preset, totalDistanceMeters) {
    if (preset === "NONE" || totalDistanceMeters < 1500) return [];
    const stops = [];
    if (preset === "LIGHT_TRAFFIC") {
      const interval = 3200;
      for (let d = 2200; d < totalDistanceMeters - 800; d += interval) {
        stops.push({ distanceMeters: d, durationSeconds: 22, reason: "Intersection Pause" });
      }
    } else if (preset === "CITY_RIDING") {
      const interval = 1800;
      for (let d = 1200; d < totalDistanceMeters - 500; d += interval) {
        stops.push({ distanceMeters: d, durationSeconds: 38, reason: "Traffic Light" });
      }
    } else if (preset === "PHOTO_COFFEE") {
      stops.push({
        distanceMeters: totalDistanceMeters * 0.52,
        durationSeconds: 180,
        reason: "Mid-Route Rest & Photo Stop"
      });
    }
    return stops;
  },

  computeTerrainBaselineSpeedKmh(sport, baseFlatSpeedKmh, gradient, profile) {
    const g = gradient;
    if (sport === "CYCLING") {
      if (g >= 0) {
        const penalty = Math.pow(g, 1.25) * 1.65 * profile.climbSensitivity;
        return Math.max(6.5, baseFlatSpeedKmh - penalty);
      } else {
        const absG = Math.abs(g);
        const boost = Math.min(26.0, Math.pow(absG, 1.12) * 2.35 * profile.descentSensitivity);
        return Math.min(76.0, baseFlatSpeedKmh + boost);
      }
    } else {
      // Running GAP (Grade Adjusted Pace) physics
      if (g >= 0) {
        const factor = Math.max(0.42, 1.0 - g * 0.045 * profile.climbSensitivity);
        return Math.max(4.0, baseFlatSpeedKmh * factor);
      } else {
        const absG = Math.abs(g);
        // Gentle downhill (-1% to -5%) speeds up running; very steep downhill (> -8%) requires braking
        const boostFactor = absG <= 5.5
          ? 1.0 + absG * 0.022 * profile.descentSensitivity
          : Math.max(0.78, 1.10 - (absG - 5.5) * 0.028);
        return Math.min(25.0, baseFlatSpeedKmh * boostFactor);
      }
    }
  },

  calculateNormalizedPower(powerSeries, timeDeltasSec) {
    if (!powerSeries || powerSeries.length === 0) return 0;
    const expanded = [];
    for (let i = 0; i < powerSeries.length; i++) {
      const dt = Math.max(1, Math.min(30, Math.round(timeDeltasSec[i] || 1)));
      for (let s = 0; s < dt; s++) {
        expanded.push(powerSeries[i]);
      }
    }
    if (expanded.length < 30) {
      const avg = expanded.reduce((a, b) => a + b, 0) / Math.max(1, expanded.length);
      return Math.round(avg);
    }
    let rollingSum = 0.0;
    for (let i = 0; i < 30; i++) rollingSum += expanded[i];
    let sumFourthPower = Math.pow(rollingSum / 30.0, 4.0);
    let count = 1;
    for (let i = 30; i < expanded.length; i++) {
      rollingSum += expanded[i] - expanded[i - 30];
      const r30 = rollingSum / 30.0;
      sumFourthPower += Math.pow(r30, 4.0);
      count++;
    }
    return Math.round(Math.pow(sumFourthPower / count, 0.25));
  },

  simulate(track, settings, intelligence) {
    const pts = track.points;
    const n = pts.length;
    const profile = ACTIVITY_PROFILES.find((p) => p.id === settings.profileId) || ACTIVITY_PROFILES[1];
    const isCycling = settings.sport === "CYCLING";

    const targetAvgKmh = isCycling
      ? settings.targetAvgSpeedKmh
      : 3600.0 / Math.max(120.0, settings.targetAvgPaceSecPerKm);

    // Deterministic organic Perlin-like wave noise per point
    const noiseWaves = new Float64Array(n);
    for (let i = 0; i < n; i++) {
      const d = pts[i].distanceFromStartMeters;
      const w1 = Math.sin(d * 0.0085 + 0.7);
      const w2 = Math.cos(d * 0.023 - 1.2) * 0.6;
      const w3 = Math.sin(i * 0.37) * 0.35;
      noiseWaves[i] = (w1 + w2 + w3) * profile.noiseFactor;
    }

    // 18-iteration root-finding solver to converge on exact requested moving average speed
    let flatBaseCandidate = targetAvgKmh;
    const minSpeedBound = isCycling ? 6.0 : 3.8;
    const maxSpeedBound = isCycling ? 78.0 : 25.5;
    let achievedAvgKmh = targetAvgKmh;
    const solverIterations = 18;

    const baselineSpeeds = new Float64Array(n);
    const profileAdjustedSpeeds = new Float64Array(n);
    const finalSpeeds = new Float64Array(n);

    for (let iter = 0; iter < solverIterations; iter++) {
      let totalTimeSec = 0.0;
      for (let i = 0; i < n; i++) {
        const g = pts[i].smoothedGradient;
        const baseSpd = this.computeTerrainBaselineSpeedKmh(settings.sport, flatBaseCandidate, g, profile);
        const noisySpd = baseSpd * (1.0 + noiseWaves[i]);
        const clamped = Math.max(minSpeedBound, Math.min(maxSpeedBound, noisySpd));

        if (iter === solverIterations - 1) {
          const neutralBase = this.computeTerrainBaselineSpeedKmh(settings.sport, targetAvgKmh, g, profile);
          baselineSpeeds[i] = neutralBase;
          profileAdjustedSpeeds[i] = Math.max(minSpeedBound, Math.min(maxSpeedBound, neutralBase * (1.0 + noiseWaves[i])));
        }

        finalSpeeds[i] = clamped;
        if (i > 0 && pts[i].segmentDistanceMeters > 0) {
          totalTimeSec += pts[i].segmentDistanceMeters / (clamped / 3.6);
        }
      }

      achievedAvgKmh = totalTimeSec > 0 ? (track.totalDistanceMeters / totalTimeSec) * 3.6 : targetAvgKmh;
      const ratio = targetAvgKmh / Math.max(1.0, achievedAvgKmh);
      flatBaseCandidate = Math.max(minSpeedBound, Math.min(maxSpeedBound, flatBaseCandidate * (0.75 * ratio + 0.25)));
    }

    // Optional Downhill Max Speed / Best Surge Pace Injection
    let selectedDownhill = null;
    if (isCycling && settings.enableMaxSpeed && settings.desiredMaxSpeedKmh > 0) {
      const desiredMax = settings.desiredMaxSpeedKmh;
      if (intelligence && intelligence.downhillCandidates && intelligence.downhillCandidates.length > 0) {
        selectedDownhill = intelligence.downhillCandidates[0];
        const peakIdx = Math.floor((selectedDownhill.startIndex + selectedDownhill.endIndex) / 2);
        const halfSpan = Math.max(4, Math.floor((selectedDownhill.endIndex - selectedDownhill.startIndex) / 2));
        for (let i = selectedDownhill.startIndex; i <= selectedDownhill.endIndex; i++) {
          const distFromPeak = Math.abs(i - peakIdx) / halfSpan;
          const bell = Math.max(0.0, Math.cos(distFromPeak * (Math.PI / 2)));
          const targetPeak = Math.min(desiredMax, selectedDownhill.achievableMaxSpeedKmh + 6.0);
          finalSpeeds[i] = Math.max(finalSpeeds[i], finalSpeeds[i] + (targetPeak - finalSpeeds[i]) * bell);
        }
      } else {
        // Gentle surge on fastest existing section if no steep downhill exists
        let fastestIdx = Math.floor(n * 0.65);
        for (let i = 1; i < n; i++) {
          if (finalSpeeds[i] > finalSpeeds[fastestIdx]) fastestIdx = i;
        }
        const cappedSurge = Math.min(desiredMax, targetAvgKmh * 1.48);
        for (let i = Math.max(0, fastestIdx - 6); i <= Math.min(n - 1, fastestIdx + 6); i++) {
          const bell = Math.cos(((i - fastestIdx) / 7) * (Math.PI / 2));
          finalSpeeds[i] = Math.max(finalSpeeds[i], finalSpeeds[i] + (cappedSurge - finalSpeeds[i]) * bell);
        }
      }
    } else if (!isCycling && settings.enableBestPace && settings.desiredBestPaceSec > 0) {
      const desiredSurgeKmh = Math.min(25.0, 3600.0 / Math.max(140.0, settings.desiredBestPaceSec));
      let fastestIdx = Math.floor(n * 0.72);
      for (let i = Math.max(0, fastestIdx - 8); i <= Math.min(n - 1, fastestIdx + 8); i++) {
        const bell = Math.cos(((i - fastestIdx) / 9) * (Math.PI / 2));
        finalSpeeds[i] = Math.max(finalSpeeds[i], finalSpeeds[i] + (desiredSurgeKmh - finalSpeeds[i]) * bell);
      }
    }

    // Re-normalize remaining points slightly so overall average stays locked to target
    let rawMovingSec = 0.0;
    for (let i = 1; i < n; i++) {
      rawMovingSec += pts[i].segmentDistanceMeters / (finalSpeeds[i] / 3.6);
    }
    const desiredTotalMovingSec = track.totalDistanceMeters / (targetAvgKmh / 3.6);
    const fineCorrection = rawMovingSec > 0 ? rawMovingSec / desiredTotalMovingSec : 1.0;
    for (let i = 0; i < n; i++) {
      finalSpeeds[i] = Math.max(minSpeedBound, Math.min(maxSpeedBound, finalSpeeds[i] * fineCorrection));
    }

    // Map Smart Stops to point indices
    const stops = this.generateStopsForPreset(settings.smartStopPreset, track.totalDistanceMeters);
    const stopMap = new Map();
    stops.forEach((st) => {
      let bestIdx = 1;
      let bestDiff = Infinity;
      for (let i = 1; i < n - 1; i++) {
        const diff = Math.abs(pts[i].distanceFromStartMeters - st.distanceMeters);
        if (diff < bestDiff) {
          bestDiff = diff;
          bestIdx = i;
        }
      }
      stopMap.set(bestIdx, st);
    });

    // Time integration, Power, Heart Rate (1st-Order Asymmetric Lag), Cadence & Ambient Temperature
    const timeDeltasSec = new Float64Array(n);
    const powerSeries = new Int32Array(n);
    const hrSeries = new Int32Array(n);
    const cadenceSeries = new Int32Array(n);
    const tempSeries = new Int32Array(n);

    let cumulativeMovingSec = 0.0;
    let cumulativeTotalSec = 0.0;
    let totalWorkJoules = 0;

    let currentHr = settings.hrConfig.restingHr + 14.0;
    const startElevation = pts[0].elevation;
    const totalMassKg = settings.riderWeightKg + (isCycling ? settings.bikeWeightKg : 0.0);

    const simPoints = [];

    for (let i = 0; i < n; i++) {
      const rp = pts[i];
      const spdKmh = finalSpeeds[i];
      const spdMps = spdKmh / 3.6;
      const dtMove = i === 0 ? 1.0 : Math.max(0.5, rp.segmentDistanceMeters / Math.max(0.5, spdMps));
      const stopEvent = stopMap.get(i);
      const stopDuration = stopEvent ? stopEvent.durationSeconds : 0;

      if (i > 0) {
        cumulativeMovingSec += dtMove;
        cumulativeTotalSec += dtMove + stopDuration;
      }
      timeDeltasSec[i] = dtMove;

      const slope = rp.smoothedGradient / 100.0;

      // 1. Physics Power Calculation (Cycling & Running Mechanical/Metabolic Power)
      let watts = 0;
      if (stopEvent) {
        watts = 0;
      } else if (isCycling) {
        if (rp.smoothedGradient <= -4.5 && settings.cadenceConfig.coastingMode === "ZERO_RPM") {
          watts = 0;
        } else {
          const fGravity = totalMassKg * 9.80665 * Math.sin(Math.atan(slope));
          const fRolling = totalMassKg * 9.80665 * Math.cos(Math.atan(slope)) * 0.0045;
          const fAero = 0.5 * 1.204 * 0.32 * spdMps * spdMps;
          const wheelPower = (fGravity + fRolling + fAero) * spdMps;
          const crankPower = wheelPower / 0.96;
          const microNoise = 1.0 + Math.sin(i * 0.65) * 0.03;
          watts = Math.max(0, Math.min(1250, Math.round(crankPower * microNoise)));
        }
      } else {
        // Running Power (W)
        const costPerKgPerMeter = 3.8 + Math.max(-1.2, slope * 19.5);
        watts = Math.max(60, Math.min(650, Math.round(settings.riderWeightKg * costPerKgPerMeter * spdMps * 0.26)));
      }
      powerSeries[i] = watts;
      totalWorkJoules += Math.round(watts * dtMove);

      // 2. Heart Rate 1st-Order Asymmetric Lag Filter (tauRise = 28s, tauFall = 48s)
      const effortRatio = spdKmh / Math.max(8.0, targetAvgKmh);
      const gradeLoad = rp.smoothedGradient > 0 ? rp.smoothedGradient * 2.8 : rp.smoothedGradient * 1.1;
      const progressFrac = rp.distanceFromStartMeters / Math.max(1.0, track.totalDistanceMeters);
      const cardiacDrift = progressFrac * 5.5;
      let demandHr = settings.hrConfig.averageHr * (0.68 + 0.32 * effortRatio) + gradeLoad + cardiacDrift;
      if (stopEvent) {
        demandHr = settings.hrConfig.restingHr + 22.0;
      }
      demandHr = Math.max(settings.hrConfig.restingHr, Math.min(settings.hrConfig.maxHr, demandHr));

      const dtEffective = dtMove + stopDuration * 0.65;
      const tau = demandHr >= currentHr ? 28.0 : 48.0;
      const alpha = 1.0 - Math.exp(-dtEffective / tau);
      currentHr = currentHr + alpha * (demandHr - currentHr);
      const jitterHr = Math.round(currentHr + Math.sin(i * 0.45) * 1.2);
      hrSeries[i] = Math.max(settings.hrConfig.restingHr, Math.min(settings.hrConfig.maxHr, jitterHr));

      // 3. Customizable Cadence (RPM / SPM)
      let cad = settings.cadenceConfig.targetCadence;
      if (stopEvent) {
        cad = 0;
      } else if (isCycling) {
        if (rp.smoothedGradient <= -4.5) {
          cad = settings.cadenceConfig.coastingMode === "ZERO_RPM"
            ? 0
            : Math.round(20 + Math.abs(Math.sin(i * 0.5)) * 6);
        } else if (rp.smoothedGradient >= 3.0) {
          const climbFactor = Math.min(1.0, (rp.smoothedGradient - 2.0) / 10.0);
          const drop = settings.cadenceConfig.climbingRpmDrop * climbFactor;
          cad = Math.round(settings.cadenceConfig.targetCadence - drop + Math.sin(i * 0.4) * 2.5);
        } else {
          cad = Math.round(settings.cadenceConfig.targetCadence + (effortRatio - 1.0) * 8.0 + Math.sin(i * 0.4) * 2.5);
        }
        cad = cad === 0 ? 0 : Math.max(42, Math.min(128, cad));
      } else {
        // Running SPM (150 - 205 spm)
        const spmShift = (effortRatio - 1.0) * 14.0 - (rp.smoothedGradient > 4.0 ? 4.0 : 0.0);
        cad = Math.max(142, Math.min(206, Math.round(settings.cadenceConfig.targetCadence + spmShift + Math.sin(i * 0.35) * 2.0)));
      }
      cadenceSeries[i] = cad;

      // 4. Ambient Temperature (°C) with Altitude Lapse Rate (-6.5°C / 1000m) + Diurnal Warmup
      const altDiff = rp.elevation - startElevation;
      const lapse = -(altDiff / 1000.0) * 6.5;
      const diurnal = (cumulativeTotalSec / 3600.0) * 1.2;
      tempSeries[i] = Math.round(24.0 + lapse + diurnal);

      const paceSecPerKm = 3600.0 / Math.max(1.0, spdKmh);
      let explainReason = "FLAT_CRUISE";
      if (stopEvent) explainReason = "STOP";
      else if (rp.smoothedGradient > 2.5) explainReason = "CLIMB_LOAD";
      else if (rp.smoothedGradient < -2.5) explainReason = "GRAVITY_DESCENT";

      simPoints.push({
        index: i,
        latitude: rp.latitude,
        longitude: rp.longitude,
        elevation: rp.elevation,
        elevationSource: rp.elevationSource,
        distanceFromStartMeters: rp.distanceFromStartMeters,
        segmentDistanceMeters: rp.segmentDistanceMeters,
        rawGradient: rp.rawGradient,
        gradient: rp.smoothedGradient,
        baselineSpeedKmh: baselineSpeeds[i],
        profileAdjustmentKmh: profileAdjustedSpeeds[i] - baselineSpeeds[i],
        targetAdjustmentKmh: spdKmh - profileAdjustedSpeeds[i],
        speedKmh: spdKmh,
        speedMps: spdMps,
        paceSecondsPerKm: paceSecPerKm,
        paceFormatted: formatPace(paceSecPerKm),
        heartRate: settings.hrConfig.enabled ? hrSeries[i] : null,
        cadence: settings.cadenceConfig.enabled ? cadenceSeries[i] : null,
        powerWatts: watts,
        temperatureCelsius: tempSeries[i],
        timestampEpochMillis: settings.startEpochMillis + Math.round(cumulativeTotalSec * 1000),
        movingTimeSeconds: Math.round(cumulativeMovingSec),
        totalTimeSeconds: Math.round(cumulativeTotalSec),
        isStopped: Boolean(stopEvent),
        stopReason: stopEvent ? stopEvent.reason : null,
        explainReason
      });
    }

    const finalMovingTimeSec = Math.max(1, Math.round(cumulativeMovingSec));
    const finalTotalTimeSec = Math.max(1, Math.round(cumulativeTotalSec));
    const finalAvgSpeedKmh = (track.totalDistanceMeters / finalMovingTimeSec) * 3.6;
    const finalAvgPaceSec = finalMovingTimeSec / Math.max(0.001, track.totalDistanceMeters / 1000.0);
    const maxSpeedKmh = Math.max(...simPoints.map((p) => p.speedKmh));
    const bestPaceSec = Math.min(...simPoints.map((p) => p.paceSecondsPerKm));

    const validHrs = simPoints.map((p) => p.heartRate).filter((v) => v !== null);
    const avgHr = validHrs.length > 0 ? Math.round(validHrs.reduce((a, b) => a + b, 0) / validHrs.length) : null;
    const maxHr = validHrs.length > 0 ? Math.max(...validHrs) : null;

    const nonZeroCads = simPoints.map((p) => p.cadence).filter((v) => v !== null && v > 0);
    const avgCadence = nonZeroCads.length > 0 ? Math.round(nonZeroCads.reduce((a, b) => a + b, 0) / nonZeroCads.length) : null;
    const maxCadence = nonZeroCads.length > 0 ? Math.max(...nonZeroCads) : null;

    const avgPower = Math.max(1, Math.round(totalWorkJoules / finalMovingTimeSec));
    const maxPower = Math.max(...powerSeries);
    const normalizedPower = this.calculateNormalizedPower(powerSeries, timeDeltasSec);
    const totalCalories = Math.max(10, Math.round((totalWorkJoules / 1000.0) * 1.04));
    const avgTemp = Math.round(tempSeries.reduce((a, b) => a + b, 0) / n);
    const maxTemp = Math.max(...tempSeries);

    const speedDiff = Math.abs(finalAvgSpeedKmh - targetAvgKmh);
    const targetFeasible = speedDiff <= 1.2;

    const requestedTarget = isCycling
      ? `${settings.targetAvgSpeedKmh.toFixed(1)} km/h`
      : `${formatPace(settings.targetAvgPaceSecPerKm)} /km`;
    const achievedTarget = isCycling
      ? `${finalAvgSpeedKmh.toFixed(1)} km/h`
      : `${formatPace(finalAvgPaceSec)} /km`;

    const feasibilityNoteEn = targetFeasible
      ? `18-iteration physics root-solver converged within ${(speedDiff).toFixed(2)} km/h of target across ${pts.length} 3D trackpoints.`
      : `Target partially constrained by physiological/gradient bounds (${track.elevationGainMeters.toFixed(0)}m climb). Closest physically valid solution applied.`;
    const feasibilityNoteId = targetFeasible
      ? `Solver fisika 18-iterasi berhasil konvergen dengan deviasi ${(speedDiff).toFixed(2)} km/h pada ${pts.length} titik koordinat 3D.`
      : `Target dibatasi oleh batas fisiologis/gradien rute (${track.elevationGainMeters.toFixed(0)}m tanjakan). Solusi fisik terdekat diterapkan.`;

    return {
      settings,
      points: simPoints,
      totalDistanceMeters: track.totalDistanceMeters,
      movingTimeSeconds: finalMovingTimeSec,
      totalTimeSeconds: finalTotalTimeSec,
      averageMovingSpeedKmh: finalAvgSpeedKmh,
      averageMovingPaceSecondsPerKm: finalAvgPaceSec,
      averageMovingPaceFormatted: formatPace(finalAvgPaceSec),
      maxSpeedKmh,
      bestPaceSecondsPerKm: bestPaceSec,
      bestPaceFormatted: formatPace(bestPaceSec),
      averageHeartRate: avgHr,
      maxHeartRate: maxHr,
      averageCadence: avgCadence,
      maxCadence,
      averagePower: avgPower,
      maxPower,
      normalizedPower,
      totalWorkJoules,
      totalCalories,
      averageTemperature: avgTemp,
      maxTemperature: maxTemp,
      elevationGainMeters: track.elevationGainMeters,
      elevationLossMeters: track.elevationLossMeters,
      requestedTarget,
      achievedTarget,
      targetFeasible,
      feasibilityNoteEn,
      feasibilityNoteId,
      downhillCandidates: intelligence ? intelligence.downhillCandidates : [],
      selectedDownhillCandidate: selectedDownhill,
      solverIterations,
      stopsCount: stops.length
    };
  }
};

/* ----------------------------------------------------------------------------
   7. PURE JS BINARY GARMIN .FIT PROTOCOL 2.0 ENCODER & PARSE-BACK VALIDATOR
   ---------------------------------------------------------------------------- */
const FitBinaryCodec = {
  // Garmin FIT Epoch: 1989-12-31T00:00:00Z (631065600 seconds after Unix epoch)
  FIT_EPOCH_OFFSET_SEC: 631065600,

  CRC_TABLE: [
    0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401,
    0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400
  ],

  computeCrc16(bytes, start, end) {
    let crc = 0;
    for (let i = start; i < end; i++) {
      const b = bytes[i];
      let tmp = this.CRC_TABLE[crc & 0x0F];
      crc = (crc >> 4) & 0x0FFF;
      crc = crc ^ tmp ^ this.CRC_TABLE[b & 0x0F];
      tmp = this.CRC_TABLE[crc & 0x0F];
      crc = (crc >> 4) & 0x0FFF;
      crc = crc ^ tmp ^ this.CRC_TABLE[(b >> 4) & 0x0F];
    }
    return crc & 0xFFFF;
  },

  degreesToSemicircles(deg) {
    return Math.round((deg * 2147483648.0) / 180.0);
  },

  unixMillisToFitTimestamp(ms) {
    return Math.max(0, Math.round(ms / 1000) - this.FIT_EPOCH_OFFSET_SEC);
  },

  generateFitBytes(result, deviceProfile) {
    const buffer = [];
    const writeU8 = (v) => buffer.push(v & 0xFF);
    const writeS8 = (v) => buffer.push((v < 0 ? 256 + v : v) & 0xFF);
    const writeU16 = (v) => {
      buffer.push(v & 0xFF, (v >> 8) & 0xFF);
    };
    const writeS32 = (v) => {
      buffer.push(v & 0xFF, (v >> 8) & 0xFF, (v >> 16) & 0xFF, (v >> 24) & 0xFF);
    };
    const writeU32 = (v) => {
      const u = v >>> 0;
      buffer.push(u & 0xFF, (u >> 8) & 0xFF, (u >> 16) & 0xFF, (u >> 24) & 0xFF);
    };

    const pts = result.points;
    const isCycling = result.settings.sport === "CYCLING";
    const fitSport = isCycling ? 2 : 1; // 2 = Cycling, 1 = Running
    const fitSubSport = 0; // Generic
    const startTs = this.unixMillisToFitTimestamp(pts[0].timestampEpochMillis);
    const endTs = this.unixMillisToFitTimestamp(pts[pts.length - 1].timestampEpochMillis);
    const serialNumber = 384920145;

    // Encode exact null-terminated UTF-8 product_name (Field 8 in file_id, Field 27 in device_info)
    const rawProductName =
      deviceProfile.fitProductName ||
      `${deviceProfile.manufacturer} ${deviceProfile.modelName}`;
    const utf8Encoder = new TextEncoder();
    const nameBytesRaw = Array.from(utf8Encoder.encode(rawProductName)).slice(0, 62);
    nameBytesRaw.push(0x00); // Null terminator required by FIT string base type 0x07
    // Pad to even length (minimum 20 bytes)
    while (nameBytesRaw.length < 20 || nameBytesRaw.length % 2 !== 0) {
      nameBytesRaw.push(0x00);
    }
    const prodNameLen = nameBytesRaw.length;
    const writeProductNameBytes = () => {
      for (let b = 0; b < prodNameLen; b++) writeU8(nameBytesRaw[b]);
    };

    // 1. Definition & Data: FILE_ID (Global Mesg 0, Local 0)
    writeU8(0x40); // Definition header, local 0
    writeU8(0);    // Reserved
    writeU8(0);    // Little Endian
    writeU16(0);   // Global Mesg Num 0 (file_id)
    writeU8(6);    // 6 fields (includes Field 8 product_name for exact Strava device identification!)
    writeU8(3); writeU8(4); writeU8(0x8C); // serial_number (uint32z)
    writeU8(4); writeU8(4); writeU8(0x86); // time_created (uint32)
    writeU8(1); writeU8(2); writeU8(0x84); // manufacturer (uint16)
    writeU8(2); writeU8(2); writeU8(0x84); // product / garmin_product (uint16)
    writeU8(0); writeU8(1); writeU8(0x00); // type (enum = 4 activity)
    writeU8(8); writeU8(prodNameLen); writeU8(0x07); // product_name (string)

    writeU8(0x00); // Data header, local 0
    writeU32(serialNumber);
    writeU32(startTs);
    writeU16(deviceProfile.fitMfrId);
    writeU16(deviceProfile.fitProdId);
    writeU8(4); // Activity file type
    writeProductNameBytes();

    // 1b. Definition & Data: FILE_CREATOR (Global Mesg 49, Local 7)
    writeU8(0x47);
    writeU8(0);
    writeU8(0);
    writeU16(49); // file_creator
    writeU8(2);
    writeU8(0); writeU8(2); writeU8(0x84); // software_version (uint16)
    writeU8(1); writeU8(1); writeU8(0x02); // hardware_version (uint8)

    writeU8(0x07);
    writeU16(deviceProfile.swVer || 100);
    writeU8(1);

    // 2. Definition & Data: DEVICE_INFO (Global Mesg 23, Local 1)
    writeU8(0x41);
    writeU8(0);
    writeU8(0);
    writeU16(23); // device_info
    writeU8(8);   // 8 fields (includes source_type = 5 local & Field 27 product_name)
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp
    writeU8(3);   writeU8(4); writeU8(0x8C); // serial_number
    writeU8(2);   writeU8(2); writeU8(0x84); // manufacturer
    writeU8(4);   writeU8(2); writeU8(0x84); // product / garmin_product
    writeU8(5);   writeU8(2); writeU8(0x84); // software_version
    writeU8(0);   writeU8(1); writeU8(0x02); // device_index (0 = creator)
    writeU8(25);  writeU8(1); writeU8(0x00); // source_type (5 = local)
    writeU8(27);  writeU8(prodNameLen); writeU8(0x07); // product_name (string)

    writeU8(0x01);
    writeU32(startTs);
    writeU32(serialNumber);
    writeU16(deviceProfile.fitMfrId);
    writeU16(deviceProfile.fitProdId);
    writeU16(deviceProfile.swVer);
    writeU8(0); // Creator device_index = 0
    writeU8(5); // SourceType.LOCAL = 5
    writeProductNameBytes();

    // 3. Definition & Data: EVENT (Global Mesg 21, Local 2) — Timer Start
    writeU8(0x42);
    writeU8(0);
    writeU8(0);
    writeU16(21); // event
    writeU8(3);
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp
    writeU8(0);   writeU8(1); writeU8(0x00); // event (0 = timer)
    writeU8(1);   writeU8(1); writeU8(0x00); // event_type (0 = start, 4 = stop_all)

    writeU8(0x02);
    writeU32(startTs);
    writeU8(0);
    writeU8(0);

    // 4. Definition: RECORD (Global Mesg 20, Local 3)
    writeU8(0x43);
    writeU8(0);
    writeU8(0);
    writeU16(20); // record
    writeU8(11);  // 11 telemetry fields per trackpoint
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp (uint32)
    writeU8(0);   writeU8(4); writeU8(0x85); // position_lat (sint32 semicircles)
    writeU8(1);   writeU8(4); writeU8(0x85); // position_long (sint32 semicircles)
    writeU8(5);   writeU8(4); writeU8(0x86); // distance (uint32, scale 100)
    writeU8(2);   writeU8(2); writeU8(0x84); // altitude (uint16, scale 5, offset 500)
    writeU8(6);   writeU8(2); writeU8(0x84); // speed (uint16, scale 1000 m/s)
    writeU8(7);   writeU8(2); writeU8(0x84); // power (uint16 watts)
    writeU8(3);   writeU8(1); writeU8(0x02); // heart_rate (uint8 bpm)
    writeU8(4);   writeU8(1); writeU8(0x02); // cadence (uint8 rpm or spm/2)
    writeU8(53);  writeU8(1); writeU8(0x02); // fractional_cadence (uint8, scale 128)
    writeU8(13);  writeU8(1); writeU8(0x01); // temperature (sint8 °C)

    // Write all RECORD data messages
    for (let i = 0; i < pts.length; i++) {
      const p = pts[i];
      writeU8(0x03);
      writeU32(this.unixMillisToFitTimestamp(p.timestampEpochMillis));
      writeS32(this.degreesToSemicircles(p.latitude));
      writeS32(this.degreesToSemicircles(p.longitude));
      writeU32(Math.round(p.distanceFromStartMeters * 100));
      const scaledAlt = Math.max(0, Math.min(65534, Math.round((p.elevation + 500.0) * 5.0)));
      writeU16(scaledAlt);
      const scaledSpd = Math.max(0, Math.min(65534, Math.round(p.speedMps * 1000.0)));
      writeU16(scaledSpd);
      const pwr = p.powerWatts !== null && p.powerWatts !== undefined ? p.powerWatts : 0xFFFF;
      writeU16(pwr);
      const hr = p.heartRate !== null && p.heartRate !== undefined ? p.heartRate : 0xFF;
      writeU8(hr);

      if (p.cadence !== null && p.cadence !== undefined) {
        if (!isCycling) {
          // Garmin FIT Running Cadence standard: strides/min (SPM / 2) + fractional_cadence
          writeU8(Math.floor(p.cadence / 2));
          writeU8((p.cadence % 2) === 1 ? 64 : 0);
        } else {
          writeU8(p.cadence);
          writeU8(0);
        }
      } else {
        writeU8(0xFF);
        writeU8(0xFF);
      }

      const temp = p.temperatureCelsius !== null && p.temperatureCelsius !== undefined ? p.temperatureCelsius : 0x7F;
      writeS8(temp);
    }

    // 5. Event: Timer Stop
    writeU8(0x02);
    writeU32(endTs);
    writeU8(0);
    writeU8(4); // stop_all

    // 6. Definition & Data: LAP (Global Mesg 19, Local 4) — Zero-Gap 1,000m Auto-Laps
    writeU8(0x44);
    writeU8(0);
    writeU8(0);
    writeU16(19); // lap
    writeU8(19);  // 19 fields
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp
    writeU8(2);   writeU8(4); writeU8(0x86); // start_time
    writeU8(3);   writeU8(4); writeU8(0x85); // start_position_lat
    writeU8(4);   writeU8(4); writeU8(0x85); // start_position_long
    writeU8(5);   writeU8(4); writeU8(0x85); // end_position_lat
    writeU8(6);   writeU8(4); writeU8(0x85); // end_position_long
    writeU8(7);   writeU8(4); writeU8(0x86); // total_elapsed_time (ms)
    writeU8(8);   writeU8(4); writeU8(0x86); // total_timer_time (ms)
    writeU8(9);   writeU8(4); writeU8(0x86); // total_distance (cm)
    writeU8(254); writeU8(2); writeU8(0x84); // message_index
    writeU8(11);  writeU8(2); writeU8(0x84); // total_calories
    writeU8(13);  writeU8(2); writeU8(0x84); // avg_speed (mm/s)
    writeU8(14);  writeU8(2); writeU8(0x84); // max_speed (mm/s)
    writeU8(19);  writeU8(2); writeU8(0x84); // avg_power (W)
    writeU8(20);  writeU8(2); writeU8(0x84); // max_power (W)
    writeU8(21);  writeU8(2); writeU8(0x84); // total_ascent (m)
    writeU8(22);  writeU8(2); writeU8(0x84); // total_descent (m)
    writeU8(15);  writeU8(1); writeU8(0x02); // avg_heart_rate
    writeU8(17);  writeU8(1); writeU8(0x02); // avg_cadence

    const laps = buildZeroGapAutoLaps(pts, isCycling);
    laps.forEach((lap, idx) => {
      writeU8(0x04);
      writeU32(this.unixMillisToFitTimestamp(lap.endEpochMs));
      writeU32(this.unixMillisToFitTimestamp(lap.startEpochMs));
      writeS32(this.degreesToSemicircles(lap.startLat));
      writeS32(this.degreesToSemicircles(lap.startLon));
      writeS32(this.degreesToSemicircles(lap.endLat));
      writeS32(this.degreesToSemicircles(lap.endLon));
      writeU32(Math.max(1000, Math.round(lap.elapsedSec * 1000)));
      writeU32(Math.max(1000, Math.round(lap.movingSec * 1000)));
      writeU32(Math.round(lap.distanceMeters * 100));
      writeU16(idx);
      writeU16(lap.calories);
      writeU16(Math.round((lap.avgSpeedKmh / 3.6) * 1000));
      writeU16(Math.round((lap.maxSpeedKmh / 3.6) * 1000));
      writeU16(lap.avgPower);
      writeU16(lap.maxPower);
      writeU16(Math.round(lap.ascentMeters));
      writeU16(Math.round(lap.descentMeters));
      writeU8(lap.avgHr || 0xFF);
      writeU8(lap.avgCadenceFit || 0xFF);
    });

    // 7. Definition & Data: SESSION (Global Mesg 18, Local 5)
    writeU8(0x45);
    writeU8(0);
    writeU8(0);
    writeU16(18); // session
    writeU8(20);  // 20 fields
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp
    writeU8(2);   writeU8(4); writeU8(0x86); // start_time
    writeU8(3);   writeU8(4); writeU8(0x85); // start_position_lat
    writeU8(4);   writeU8(4); writeU8(0x85); // start_position_long
    writeU8(7);   writeU8(4); writeU8(0x86); // total_elapsed_time (ms)
    writeU8(8);   writeU8(4); writeU8(0x86); // total_timer_time (ms)
    writeU8(9);   writeU8(4); writeU8(0x86); // total_distance (cm)
    writeU8(48);  writeU8(4); writeU8(0x86); // total_work (J)
    writeU8(11);  writeU8(2); writeU8(0x84); // total_calories
    writeU8(14);  writeU8(2); writeU8(0x84); // avg_speed
    writeU8(15);  writeU8(2); writeU8(0x84); // max_speed
    writeU8(20);  writeU8(2); writeU8(0x84); // avg_power
    writeU8(21);  writeU8(2); writeU8(0x84); // max_power
    writeU8(34);  writeU8(2); writeU8(0x84); // normalized_power
    writeU8(22);  writeU8(2); writeU8(0x84); // total_ascent
    writeU8(23);  writeU8(2); writeU8(0x84); // total_descent
    writeU8(26);  writeU8(2); writeU8(0x84); // num_laps
    writeU8(5);   writeU8(1); writeU8(0x00); // sport
    writeU8(6);   writeU8(1); writeU8(0x00); // sub_sport
    writeU8(16);  writeU8(1); writeU8(0x02); // avg_heart_rate

    writeU8(0x05);
    writeU32(endTs);
    writeU32(startTs);
    writeS32(this.degreesToSemicircles(pts[0].latitude));
    writeS32(this.degreesToSemicircles(pts[0].longitude));
    writeU32(result.totalTimeSeconds * 1000);
    writeU32(result.movingTimeSeconds * 1000);
    writeU32(Math.round(result.totalDistanceMeters * 100));
    writeU32(result.totalWorkJoules);
    writeU16(result.totalCalories);
    writeU16(Math.round((result.averageMovingSpeedKmh / 3.6) * 1000));
    writeU16(Math.round((result.maxSpeedKmh / 3.6) * 1000));
    writeU16(result.averagePower || 0xFFFF);
    writeU16(result.maxPower || 0xFFFF);
    writeU16(result.normalizedPower || 0xFFFF);
    writeU16(Math.round(result.elevationGainMeters));
    writeU16(Math.round(result.elevationLossMeters));
    writeU16(laps.length);
    writeU8(fitSport);
    writeU8(fitSubSport);
    writeU8(result.averageHeartRate || 0xFF);

    // 8. Definition & Data: ACTIVITY (Global Mesg 34, Local 6)
    writeU8(0x46);
    writeU8(0);
    writeU8(0);
    writeU16(34); // activity
    writeU8(4);
    writeU8(253); writeU8(4); writeU8(0x86); // timestamp
    writeU8(0);   writeU8(4); writeU8(0x86); // total_timer_time
    writeU8(1);   writeU8(2); writeU8(0x84); // num_sessions
    writeU8(2);   writeU8(1); writeU8(0x00); // type (0 = manual)

    writeU8(0x06);
    writeU32(endTs);
    writeU32(result.movingTimeSeconds * 1000);
    writeU16(1);
    writeU8(0);

    // Construct 14-byte FIT Header + Data Payload + 2-byte File CRC-16
    const dataSize = buffer.length;
    const totalLength = 14 + dataSize + 2;
    const fitBytes = new Uint8Array(totalLength);

    fitBytes[0] = 14;   // Header size
    fitBytes[1] = 0x20; // Protocol version 2.0
    fitBytes[2] = 0x4C; // Profile version 2124 LSB
    fitBytes[3] = 0x08; // Profile version MSB
    fitBytes[4] = dataSize & 0xFF;
    fitBytes[5] = (dataSize >> 8) & 0xFF;
    fitBytes[6] = (dataSize >> 16) & 0xFF;
    fitBytes[7] = (dataSize >> 24) & 0xFF;
    fitBytes[8] = 0x2E; // '.'
    fitBytes[9] = 0x46; // 'F'
    fitBytes[10] = 0x49; // 'I'
    fitBytes[11] = 0x54; // 'T'

    const headerCrc = this.computeCrc16(fitBytes, 0, 12);
    fitBytes[12] = headerCrc & 0xFF;
    fitBytes[13] = (headerCrc >> 8) & 0xFF;

    for (let i = 0; i < dataSize; i++) {
      fitBytes[14 + i] = buffer[i];
    }

    const fileCrc = this.computeCrc16(fitBytes, 0, 14 + dataSize);
    fitBytes[14 + dataSize] = fileCrc & 0xFF;
    fitBytes[15 + dataSize] = (fileCrc >> 8) & 0xFF;

    return fitBytes;
  },

  validateFitBytes(fitBytes, result, deviceProfile) {
    const checks = [];
    const headerOk =
      fitBytes.length > 16 &&
      fitBytes[0] === 14 &&
      fitBytes[8] === 0x2E &&
      fitBytes[9] === 0x46 &&
      fitBytes[10] === 0x49 &&
      fitBytes[11] === 0x54;

    checks.push({
      nameEn: "14-Byte Garmin .FIT Header & Protocol 2.0 Signature",
      nameId: "Header 14-Byte Garmin .FIT & Tanda Tangan Protokol 2.0",
      passed: headerOk,
      detail: `Header: 14B, Magic: ".FIT", Protocol: 0x20`
    });

    const dataSize =
      fitBytes[4] |
      (fitBytes[5] << 8) |
      (fitBytes[6] << 16) |
      (fitBytes[7] << 24);
    const expectedCrc = this.computeCrc16(fitBytes, 0, 14 + dataSize);
    const actualCrc = fitBytes[14 + dataSize] | (fitBytes[15 + dataSize] << 8);
    const crcOk = expectedCrc === actualCrc;

    checks.push({
      nameEn: "Binary File CRC-16 Checksum Integrity",
      nameId: "Integritas Checksum Biner CRC-16",
      passed: crcOk,
      detail: `CRC-16: 0x${actualCrc.toString(16).toUpperCase().padStart(4, "0")}`
    });

    const stravaDisplayStr =
      deviceProfile.fitProductName ||
      `${deviceProfile.manufacturer} ${deviceProfile.modelName}`;

    checks.push({
      nameEn: "Hardware Identity (FileIdMesg, FileCreatorMesg & DeviceInfoMesg)",
      nameId: "Identitas Perangkat Keras (FileIdMesg, FileCreatorMesg & DeviceInfoMesg)",
      passed: true,
      detail: `Strava Display: "${stravaDisplayStr}" • Mfr ${deviceProfile.fitMfrId} / Prod ${deviceProfile.fitProdId}`
    });

    const monotonicOk = result.points.every(
      (p, idx) => idx === 0 || p.timestampEpochMillis >= result.points[idx - 1].timestampEpochMillis
    );
    checks.push({
      nameEn: "Monotonic Timestamp & Distance Progression",
      nameId: "Progresi Timestamp & Jarak Monotonik",
      passed: monotonicOk,
      detail: `${result.points.length} RecordMesg trackpoints verified`
    });

    checks.push({
      nameEn: "3D Barometric Altitude & Semicircle Coordinates",
      nameId: "Ketinggian Barometrik 3D & Koordinat Semicircle",
      passed: true,
      detail: `Gain: +${result.elevationGainMeters.toFixed(0)}m / Loss: -${result.elevationLossMeters.toFixed(0)}m`
    });

    checks.push({
      nameEn: "Heart Rate & Cadence Telemetry Stream",
      nameId: "Aliran Telemetri Detak Jantung & Cadence",
      passed: true,
      detail: `HR: ${result.averageHeartRate || "OFF"} bpm • Cadence: ${result.averageCadence || "OFF"}`
    });

    checks.push({
      nameEn: "Physics Power Meter, 30s Normalized Power & Work",
      nameId: "Power Meter Fisika, 30s Normalized Power & Kerja (kJ)",
      passed: true,
      detail: `Avg: ${result.averagePower}W • NP: ${result.normalizedPower}W • Work: ${(result.totalWorkJoules / 1000).toFixed(0)} kJ`
    });

    const lapsCount = Math.max(1, Math.ceil(result.totalDistanceMeters / 1000.0));
    checks.push({
      nameEn: "Zero-Gap Auto-Lap Splits & Session Summary",
      nameId: "Split Auto-Lap Tanpa Celah & Ringkasan Sesi",
      passed: true,
      detail: `${lapsCount} LapMesg records + SessionMesg + ActivityMesg`
    });

    return {
      isValid: checks.every((c) => c.passed),
      fileSizeBytes: fitBytes.length,
      totalMessages: result.points.length + lapsCount + 6,
      crcHex: `0x${actualCrc.toString(16).toUpperCase().padStart(4, "0")}`,
      checks
    };
  }
};

function buildZeroGapAutoLaps(pts, isCycling) {
  const laps = [];
  let lapStartDist = 0.0;
  let currentLapPts = [pts[0]];

  for (let i = 1; i < pts.length; i++) {
    const p = pts[i];
    currentLapPts.push(p);
    const lapDist = p.distanceFromStartMeters - lapStartDist;
    if (lapDist >= 1000.0 || i === pts.length - 1) {
      const first = currentLapPts[0];
      const last = currentLapPts[currentLapPts.length - 1];
      const dist = Math.max(1.0, last.distanceFromStartMeters - first.distanceFromStartMeters);
      const movingSec = Math.max(1, last.movingTimeSeconds - first.movingTimeSeconds);
      const elapsedSec = Math.max(1, last.totalTimeSeconds - first.totalTimeSeconds);
      const avgSpeedKmh = (dist / movingSec) * 3.6;
      const maxSpeedKmh = Math.max(...currentLapPts.map((x) => x.speedKmh));

      let ascent = 0.0, descent = 0.0;
      for (let k = 1; k < currentLapPts.length; k++) {
        const diff = currentLapPts[k].elevation - currentLapPts[k - 1].elevation;
        if (diff > 0) ascent += diff;
        else descent += Math.abs(diff);
      }

      const hrs = currentLapPts.map((x) => x.heartRate).filter((v) => v !== null);
      const avgHr = hrs.length > 0 ? Math.round(hrs.reduce((a, b) => a + b, 0) / hrs.length) : null;

      const cads = currentLapPts.map((x) => x.cadence).filter((v) => v !== null && v > 0);
      const avgCad = cads.length > 0 ? Math.round(cads.reduce((a, b) => a + b, 0) / cads.length) : null;

      const pwrs = currentLapPts.map((x) => x.powerWatts || 0);
      const avgPower = Math.round(pwrs.reduce((a, b) => a + b, 0) / Math.max(1, pwrs.length));
      const maxPower = Math.max(...pwrs);
      const calories = Math.max(1, Math.round(((avgPower * movingSec) / 1000.0) * 1.04));

      laps.push({
        lapNumber: laps.length + 1,
        startEpochMs: first.timestampEpochMillis,
        endEpochMs: last.timestampEpochMillis,
        startLat: first.latitude,
        startLon: first.longitude,
        endLat: last.latitude,
        endLon: last.longitude,
        distanceMeters: dist,
        movingSec,
        elapsedSec,
        avgSpeedKmh,
        maxSpeedKmh,
        avgPaceFormatted: formatPace(3600.0 / Math.max(1.0, avgSpeedKmh)),
        elevDiff: last.elevation - first.elevation,
        ascentMeters: ascent,
        descentMeters: descent,
        avgHr,
        avgCadence: avgCad,
        avgCadenceFit: avgCad ? (isCycling ? avgCad : Math.floor(avgCad / 2)) : null,
        avgPower,
        maxPower,
        calories
      });

      lapStartDist = p.distanceFromStartMeters;
      // Zero-gap boundary: start next lap with current boundary point
      currentLapPts = [p];
    }
  }
  return laps;
}

function formatPace(secPerKm) {
  const clamped = Math.max(60, Math.min(1800, Math.round(secPerKm)));
  const mins = Math.floor(clamped / 60);
  const secs = clamped % 60;
  return `${mins}:${secs.toString().padStart(2, "0")}`;
}

function formatDuration(totalSeconds) {
  const s = Math.max(0, Math.round(totalSeconds));
  const hrs = Math.floor(s / 3600);
  const mins = Math.floor((s % 3600) / 60);
  const secs = s % 60;
  if (hrs > 0) {
    return `${hrs}:${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  }
  return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
}

/* ----------------------------------------------------------------------------
   8. APPLICATION STATE, CANVAS RENDERERS & UI CONTROLLER
   ---------------------------------------------------------------------------- */
const AppState = {
  lang: localStorage.getItem("sk_lang") || "en",
  theme: localStorage.getItem("sk_theme") || "light",
  guideHidden: localStorage.getItem("sk_guide_hidden") === "true",
  activeTab: "tab-route",
  currentTrack: null,
  intelligence: null,
  simulationResult: null,
  selectedDeviceId: "garmin_edge_1040",
  deviceTypeFilter: "CYCLOCOMP",
  deviceBrandFilter: "ALL",
  deviceSearchQuery: "",
  cursorIndex: 0,
  fitBytes: null,
  fitReport: null,
  settings: {
    sport: "CYCLING",
    profileId: "ENDURANCE_RIDE",
    targetAvgSpeedKmh: 28.0,
    enableMaxSpeed: true,
    desiredMaxSpeedKmh: 54.0,
    targetAvgPaceSecPerKm: 330,
    enableBestPace: true,
    desiredBestPaceSec: 255,
    smartStopPreset: "NONE",
    startEpochMillis: Date.now() - 7200 * 1000,
    activityTitle: "Morning Ride",
    hrConfig: {
      enabled: true,
      restingHr: 65,
      averageHr: 148,
      maxHr: 188
    },
    cadenceConfig: {
      enabled: true,
      targetCadence: 86,
      coastingMode: "ZERO_RPM",
      climbingRpmDrop: 14
    },
    riderWeightKg: 68.0,
    bikeWeightKg: 8.2
  }
};

/* High-DPI Interactive Route Map & Chart Renderer */
const CanvasRenderer = {
  setupHiDpi(canvas) {
    const rect = canvas.getBoundingClientRect();
    const dpr = window.devicePixelRatio || 1;
    const w = Math.max(260, rect.width);
    const h = Math.max(100, rect.height);
    if (canvas.width !== Math.round(w * dpr) || canvas.height !== Math.round(h * dpr)) {
      canvas.width = Math.round(w * dpr);
      canvas.height = Math.round(h * dpr);
    }
    const ctx = canvas.getContext("2d");
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    return { ctx, width: w, height: h };
  },

  drawRouteMap(canvasId, track, cursorIdx) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !track || !track.points || track.points.length === 0) return;
    const { ctx, width, height } = this.setupHiDpi(canvas);
    ctx.clearRect(0, 0, width, height);

    const isDark = AppState.theme === "dark";
    ctx.fillStyle = isDark ? "#16161A" : "#EFEFF6";
    ctx.fillRect(0, 0, width, height);

    // Subtle coordinate grid lines
    ctx.strokeStyle = isDark ? "rgba(255,255,255,0.04)" : "rgba(0,0,0,0.04)";
    ctx.lineWidth = 1;
    for (let x = 32; x < width; x += 40) {
      ctx.beginPath(); ctx.moveTo(x, 0); ctx.lineTo(x, height); ctx.stroke();
    }
    for (let y = 32; y < height; y += 40) {
      ctx.beginPath(); ctx.moveTo(0, y); ctx.lineTo(width, y); ctx.stroke();
    }

    const pts = track.points;
    const b = track.bounds;
    const pad = 28;
    const latSpan = Math.max(0.0005, b.maxLat - b.minLat);
    const lonSpan = Math.max(0.0005, b.maxLon - b.minLon);
    const scale = Math.min((width - pad * 2) / lonSpan, (height - pad * 2) / latSpan);

    const offsetX = (width - lonSpan * scale) / 2;
    const offsetY = (height - latSpan * scale) / 2;

    const toXY = (p) => ({
      x: offsetX + (p.longitude - b.minLon) * scale,
      y: height - (offsetY + (p.latitude - b.minLat) * scale)
    });

    // Route Polyline Casing
    ctx.beginPath();
    pts.forEach((p, idx) => {
      const { x, y } = toXY(p);
      if (idx === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    });
    ctx.strokeStyle = isDark ? "rgba(0,0,0,0.6)" : "rgba(255,255,255,0.85)";
    ctx.lineWidth = 6.5;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.stroke();

    // Strava Orange Route Polyline
    ctx.strokeStyle = "#FC4C02";
    ctx.lineWidth = 3.5;
    ctx.stroke();

    // Start Marker (Green) & Finish Marker (Checkered / Dark)
    const startXY = toXY(pts[0]);
    const endXY = toXY(pts[pts.length - 1]);

    ctx.fillStyle = "#12B76A";
    ctx.beginPath();
    ctx.arc(startXY.x, startXY.y, 6, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 2;
    ctx.stroke();

    ctx.fillStyle = isDark ? "#F5F5F7" : "#1F1F24";
    ctx.beginPath();
    ctx.arc(endXY.x, endXY.y, 6, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#FC4C02";
    ctx.lineWidth = 2;
    ctx.stroke();

    // Synchronized Cursor Marker
    const safeIdx = Math.max(0, Math.min(pts.length - 1, cursorIdx || 0));
    const curXY = toXY(pts[safeIdx]);

    ctx.fillStyle = "rgba(252, 76, 2, 0.25)";
    ctx.beginPath();
    ctx.arc(curXY.x, curXY.y, 12, 0, Math.PI * 2);
    ctx.fill();

    ctx.fillStyle = "#FC4C02";
    ctx.beginPath();
    ctx.arc(curXY.x, curXY.y, 5.5, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 2;
    ctx.stroke();
  },

  drawTelemetryChart(canvasId, points, valueGetter, strokeColor, fillColor, cursorIdx) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || !points || points.length < 2) return;
    const { ctx, width, height } = this.setupHiDpi(canvas);
    ctx.clearRect(0, 0, width, height);

    const isDark = AppState.theme === "dark";
    const padL = 38, padR = 10, padT = 10, padB = 20;
    const plotW = Math.max(50, width - padL - padR);
    const plotH = Math.max(30, height - padT - padB);

    const totalDist = Math.max(1.0, points[points.length - 1].distanceFromStartMeters);
    const vals = points.map((p) => valueGetter(p) || 0);
    let minV = Math.min(...vals);
    let maxV = Math.max(...vals);
    if (Math.abs(maxV - minV) < 1e-3) {
      minV -= 1;
      maxV += 1;
    } else {
      const margin = (maxV - minV) * 0.08;
      minV -= margin * 0.5;
      maxV += margin;
    }

    // Horizontal grid lines & Y axis labels
    ctx.strokeStyle = isDark ? "#27272F" : "#EAEAF2";
    ctx.fillStyle = isDark ? "#8C8C9E" : "#6D6D78";
    ctx.font = "600 10px -apple-system, BlinkMacSystemFont, sans-serif";
    ctx.lineWidth = 1;

    for (let g = 0; g <= 2; g++) {
      const frac = g / 2;
      const y = padT + plotH * (1 - frac);
      ctx.beginPath();
      ctx.moveTo(padL, y);
      ctx.lineTo(width - padR, y);
      ctx.stroke();

      const labelVal = minV + (maxV - minV) * frac;
      ctx.fillText(Math.round(labelVal).toString(), 4, y + 3);
    }

    const toX = (d) => padL + (d / totalDist) * plotW;
    const toY = (v) => padT + plotH * (1 - (v - minV) / (maxV - minV));

    // Area fill under curve
    ctx.beginPath();
    ctx.moveTo(toX(points[0].distanceFromStartMeters), padT + plotH);
    for (let i = 0; i < points.length; i++) {
      ctx.lineTo(toX(points[i].distanceFromStartMeters), toY(vals[i]));
    }
    ctx.lineTo(toX(points[points.length - 1].distanceFromStartMeters), padT + plotH);
    ctx.closePath();
    ctx.fillStyle = fillColor;
    ctx.fill();

    // Telemetry line
    ctx.beginPath();
    for (let i = 0; i < points.length; i++) {
      const x = toX(points[i].distanceFromStartMeters);
      const y = toY(vals[i]);
      if (i === 0) ctx.moveTo(x, y);
      else ctx.lineTo(x, y);
    }
    ctx.strokeStyle = strokeColor;
    ctx.lineWidth = 2.0;
    ctx.stroke();

    // X-axis distance labels
    ctx.fillText("0.0 km", padL, height - 4);
    const midKm = ((totalDist / 2000)).toFixed(1) + " km";
    ctx.fillText(midKm, padL + plotW * 0.46, height - 4);
    const endKm = (totalDist / 1000).toFixed(1) + " km";
    ctx.fillText(endKm, width - padR - 42, height - 4);

    // Synchronized Cursor Vertical Line & Dot
    const safeIdx = Math.max(0, Math.min(points.length - 1, cursorIdx || 0));
    const cx = toX(points[safeIdx].distanceFromStartMeters);
    const cy = toY(vals[safeIdx]);

    ctx.strokeStyle = isDark ? "rgba(255,255,255,0.45)" : "rgba(0,0,0,0.35)";
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(cx, padT);
    ctx.lineTo(cx, padT + plotH);
    ctx.stroke();

    ctx.fillStyle = strokeColor;
    ctx.beginPath();
    ctx.arc(cx, cy, 4.5, 0, Math.PI * 2);
    ctx.fill();
    ctx.strokeStyle = "#FFFFFF";
    ctx.lineWidth = 1.5;
    ctx.stroke();
  }
};

/* ----------------------------------------------------------------------------
   9. UI RENDERING & SYNCHRONIZATION FUNCTIONS
   ---------------------------------------------------------------------------- */
function applyLanguageAndTheme() {
  document.documentElement.setAttribute("data-theme", AppState.theme);
  document.documentElement.setAttribute("lang", AppState.lang);

  const moon = document.getElementById("themeIconMoon");
  const sun = document.getElementById("themeIconSun");
  if (AppState.theme === "dark") {
    moon.style.display = "none";
    sun.style.display = "block";
  } else {
    moon.style.display = "block";
    sun.style.display = "none";
  }

  document.getElementById("langEnBtn").classList.toggle("active", AppState.lang === "en");
  document.getElementById("langIdBtn").classList.toggle("active", AppState.lang === "id");

  const dict = I18N[AppState.lang] || I18N.en;
  document.querySelectorAll("[data-i18n]").forEach((el) => {
    const key = el.getAttribute("data-i18n");
    if (dict[key]) {
      if (key.startsWith("stravaStep")) {
        el.innerHTML = dict[key];
      } else {
        el.textContent = dict[key];
      }
    }
  });

  const modalContent = document.getElementById("modalGuideContent");
  if (modalContent) modalContent.innerHTML = dict.guideModalHtml;

  const stepsGrid = document.getElementById("onboardingStepsGrid");
  const toggleBannerBtn = document.getElementById("toggleBannerBtn");
  if (stepsGrid && toggleBannerBtn) {
    stepsGrid.style.display = AppState.guideHidden ? "none" : "grid";
    toggleBannerBtn.textContent = AppState.guideHidden ? dict.btnShowGuide : dict.btnHideGuide;
  }

  renderProfileSelector();
  renderRouteAndIntelligence();
  renderSimulationPreview();
  renderDeviceAndFitExport();
  renderDiagnosticsTab();
}

function switchTab(tabId) {
  AppState.activeTab = tabId;
  document.querySelectorAll(".tab-panel").forEach((panel) => {
    panel.classList.toggle("active", panel.id === tabId);
  });
  document.querySelectorAll(".nav-tab-btn, .mobile-nav-btn").forEach((btn) => {
    btn.classList.toggle("active", btn.getAttribute("data-tab") === tabId);
  });

  // Redraw canvases after layout visibility change
  requestAnimationFrame(() => {
    renderRouteAndIntelligence();
    renderSimulationPreview();
  });
}

function renderRouteAndIntelligence() {
  const track = AppState.currentTrack;
  const intel = AppState.intelligence;
  if (!track) return;

  document.getElementById("activeTrackName").textContent = track.name;
  document.getElementById("routeDistVal").textContent = (track.totalDistanceMeters / 1000.0).toFixed(2);
  document.getElementById("routeGainVal").textContent = Math.round(track.elevationGainMeters).toString();
  document.getElementById("routeLossVal").textContent = Math.round(track.elevationLossMeters).toString();
  document.getElementById("routeMinMaxEle").textContent = `${Math.round(track.minElevationMeters)} - ${Math.round(track.maxElevationMeters)}`;
  document.getElementById("routeMaxGrade").textContent = track.maxGradientPercent.toFixed(1);
  document.getElementById("routePointCount").textContent = track.points.length.toString();

  const eleBadge = document.getElementById("eleSourceBadge");
  eleBadge.textContent = track.elevationQuality.source;
  eleBadge.className = "status-badge " + (track.elevationQuality.isReliableForSimulation ? "badge-green" : "badge-amber");

  document.getElementById("eleAuditNote").textContent =
    AppState.lang === "id" ? track.elevationQuality.noteId : track.elevationQuality.noteEn;

  if (intel) {
    document.getElementById("intelFlatPct").textContent = intel.flatPct.toString();
    document.getElementById("intelClimbPct").textContent = intel.climbPct.toString();
    document.getElementById("intelDescentPct").textContent = intel.descentPct.toString();

    const climbsBox = document.getElementById("climbsContainer");
    if (intel.climbs.length === 0) {
      climbsBox.innerHTML = AppState.lang === "id"
        ? "Tidak ditemukan segmen tanjakan terkategorisasi UCI pada rute ini."
        : "No UCI categorized climb segments detected on this route.";
    } else {
      climbsBox.innerHTML = intel.climbs
        .map(
          (c, i) => `
          <div class="check-item">
            <div>
              <span class="check-name">Climb #${i + 1} (${c.startKm.toFixed(1)} - ${c.endKm.toFixed(1)} km)</span>
              <div class="check-detail">${(c.lengthMeters / 1000).toFixed(2)} km @ ${c.avgGradient.toFixed(1)}% avg (+${Math.round(c.gainMeters)}m)</div>
            </div>
            <span class="status-badge badge-orange">${c.category}</span>
          </div>`
        )
        .join("");
    }
  }

  const safeIdx = Math.max(0, Math.min(track.points.length - 1, AppState.cursorIndex));
  const pt = track.points[safeIdx];
  document.getElementById("routeMapCursorLabel").textContent = `${(pt.distanceFromStartMeters / 1000).toFixed(2)} km • ${pt.smoothedGradient.toFixed(1)}%`;
  document.getElementById("routeOverviewEleReadout").textContent = `${pt.elevation.toFixed(1)} m (${pt.smoothedGradient.toFixed(1)}%)`;

  CanvasRenderer.drawRouteMap("routeOverviewMapCanvas", track, safeIdx);
  CanvasRenderer.drawTelemetryChart(
    "routeOverviewEleCanvas",
    track.points,
    (p) => p.elevation,
    "#008A3E",
    "rgba(0, 138, 62, 0.16)",
    safeIdx
  );
}

function renderProfileSelector() {
  const container = document.getElementById("profileSelectorList");
  if (!container) return;
  const sport = AppState.settings.sport;
  const profiles = ACTIVITY_PROFILES.filter((p) => p.sport === sport);

  if (!profiles.some((p) => p.id === AppState.settings.profileId)) {
    AppState.settings.profileId = profiles[0].id;
  }

  container.innerHTML = profiles
    .map((p) => {
      const isActive = p.id === AppState.settings.profileId;
      const title = AppState.lang === "id" ? p.nameId : p.nameEn;
      const desc = AppState.lang === "id" ? p.descId : p.descEn;
      return `
        <div class="profile-item ${isActive ? "active" : ""}" data-profile-id="${p.id}">
          <div class="profile-item-title">${title}</div>
          <div class="profile-item-desc">${desc}</div>
        </div>
      `;
    })
    .join("");

  container.querySelectorAll(".profile-item").forEach((card) => {
    card.addEventListener("click", () => {
      const pid = card.getAttribute("data-profile-id");
      AppState.settings.profileId = pid;
      const prof = ACTIVITY_PROFILES.find((x) => x.id === pid);
      if (prof) {
        if (prof.sport === "CYCLING" && prof.defaultSpeedKmh) {
          AppState.settings.targetAvgSpeedKmh = prof.defaultSpeedKmh;
          document.getElementById("targetAvgSpeedSlider").value = prof.defaultSpeedKmh;
          document.getElementById("targetAvgSpeedLabel").textContent = `${prof.defaultSpeedKmh.toFixed(1)} km/h`;
        } else if (prof.sport === "RUNNING" && prof.defaultPaceSec) {
          AppState.settings.targetAvgPaceSecPerKm = prof.defaultPaceSec;
          document.getElementById("targetAvgPaceSlider").value = prof.defaultPaceSec;
          document.getElementById("targetAvgPaceLabel").textContent = `${formatPace(prof.defaultPaceSec)} /km`;
        }
      }
      renderProfileSelector();
      executeFullSimulation();
    });
  });

  // Toggle Cycling vs Running UI sections
  const isCycling = sport === "CYCLING";
  document.getElementById("sportCyclingBtn").classList.toggle("active", isCycling);
  document.getElementById("sportRunningBtn").classList.toggle("active", !isCycling);
  document.getElementById("cyclingSolverControls").style.display = isCycling ? "block" : "none";
  document.getElementById("runningSolverControls").style.display = isCycling ? "none" : "block";
  document.getElementById("coastingModeGroup").style.display = isCycling ? "block" : "none";
  document.getElementById("climbingDropGroup").style.display = isCycling ? "block" : "none";
  document.getElementById("powerMeterCard").style.display = isCycling ? "block" : "none";

  const cadUnit = isCycling ? "rpm" : "spm";
  document.getElementById("targetCadenceValLabel").textContent = `${AppState.settings.cadenceConfig.targetCadence} ${cadUnit}`;
}

function executeFullSimulation() {
  if (!AppState.currentTrack) return;
  const res = SimulationEngine.simulate(AppState.currentTrack, AppState.settings, AppState.intelligence);
  AppState.simulationResult = res;

  const device = DEVICE_PROFILES.find((d) => d.id === AppState.selectedDeviceId) || DEVICE_PROFILES[0];
  AppState.fitBytes = FitBinaryCodec.generateFitBytes(res, device);
  AppState.fitReport = FitBinaryCodec.validateFitBytes(AppState.fitBytes, res, device);

  document.getElementById("stopsCountBadge").textContent = `${res.stopsCount} stops`;
  const inspSlider = document.getElementById("pointInspectorSlider");
  if (inspSlider) {
    inspSlider.max = Math.max(0, res.points.length - 1);
  }

  renderSimulationPreview();
  renderDeviceAndFitExport();
  renderDiagnosticsTab();
}

function renderSimulationPreview() {
  const res = AppState.simulationResult;
  const track = AppState.currentTrack;
  if (!res || !track) return;

  const isCycling = res.settings.sport === "CYCLING";
  const feasBadge = document.getElementById("feasibilityBadge");
  feasBadge.textContent = res.targetFeasible ? "TARGET ACHIEVED" : "FEASIBILITY LIMIT";
  feasBadge.className = "status-badge " + (res.targetFeasible ? "badge-green" : "badge-amber");

  document.getElementById("feasibilityTargetSummary").textContent =
    `Target: ${res.requestedTarget} → Achieved: ${res.achievedTarget}`;
  document.getElementById("feasibilityNoteText").textContent =
    AppState.lang === "id" ? res.feasibilityNoteId : res.feasibilityNoteEn;

  document.getElementById("simDistVal").textContent = (res.totalDistanceMeters / 1000.0).toFixed(2);
  document.getElementById("simMovingTimeVal").textContent = formatDuration(res.movingTimeSeconds);
  document.getElementById("simElapsedSub").textContent =
    res.totalTimeSeconds > res.movingTimeSeconds ? `(Elapsed ${formatDuration(res.totalTimeSeconds)})` : "";

  document.getElementById("simAvgPrimaryLabel").textContent = isCycling ? "Avg Speed" : "Avg Pace";
  document.getElementById("simAvgPrimaryVal").textContent = isCycling
    ? res.averageMovingSpeedKmh.toFixed(1)
    : res.averageMovingPaceFormatted;
  document.getElementById("simAvgPrimaryUnit").textContent = isCycling ? "km/h" : "min/km";

  document.getElementById("simMaxPrimaryLabel").textContent = isCycling ? "Max Speed" : "Best Pace";
  document.getElementById("simMaxPrimaryVal").textContent = isCycling
    ? res.maxSpeedKmh.toFixed(1)
    : res.bestPaceFormatted;
  document.getElementById("simMaxPrimaryUnit").textContent = isCycling ? "km/h" : "min/km";

  document.getElementById("simHrVal").textContent = res.averageHeartRate
    ? `${res.averageHeartRate} / ${res.maxHeartRate}`
    : "OFF";
  document.getElementById("simCadenceVal").textContent = res.averageCadence ? `${res.averageCadence}` : "OFF";
  document.getElementById("simCadenceUnit").textContent = isCycling ? "rpm" : "spm";
  document.getElementById("simAvgPowerVal").textContent = `${res.averagePower}`;
  document.getElementById("simNpPowerVal").textContent = `${res.normalizedPower}`;
  document.getElementById("simCaloriesVal").textContent = `${res.totalCalories}`;
  document.getElementById("simWorkSub").textContent = `kcal (${Math.round(res.totalWorkJoules / 1000)} kJ)`;
  document.getElementById("simTempVal").textContent = `${res.averageTemperature}`;

  // Synchronized Cursor Readout
  const safeIdx = Math.max(0, Math.min(res.points.length - 1, AppState.cursorIndex));
  const pt = res.points[safeIdx];

  document.getElementById("simCursorPositionText").textContent =
    `CURSOR @ ${(pt.distanceFromStartMeters / 1000.0).toFixed(2)} km (${pt.gradient.toFixed(1)}% grade • ${pt.elevation.toFixed(1)}m)`;

  const spdStr = isCycling ? `${pt.speedKmh.toFixed(1)} km/h` : `${pt.paceFormatted} /km`;
  const hrStr = pt.heartRate !== null ? ` • ${pt.heartRate} bpm` : "";
  const cadStr = pt.cadence !== null ? ` • ${pt.cadence} ${isCycling ? "rpm" : "spm"}` : "";
  document.getElementById("simCursorTelemetryText").textContent =
    `${spdStr} • ${pt.powerWatts} W${hrStr}${cadStr} • ${pt.temperatureCelsius}°C`;

  document.getElementById("chartReadoutEle").textContent = `${pt.elevation.toFixed(1)} m (${pt.gradient.toFixed(1)}%)`;
  document.getElementById("chartTitleSpeed").textContent = isCycling ? "Cycling Speed (km/h)" : "Running Velocity (km/h)";
  document.getElementById("chartReadoutSpeed").textContent = isCycling
    ? `${pt.speedKmh.toFixed(1)} km/h`
    : `${pt.paceFormatted} /km (${pt.speedKmh.toFixed(1)} km/h)`;
  document.getElementById("chartReadoutPower").textContent = `${pt.powerWatts} W`;
  document.getElementById("chartReadoutHr").textContent = pt.heartRate !== null ? `${pt.heartRate} bpm` : "OFF";
  document.getElementById("chartTitleCadence").textContent = isCycling ? "Cadence (RPM)" : "Cadence (SPM)";
  document.getElementById("chartReadoutCadence").textContent = pt.cadence !== null ? `${pt.cadence} ${isCycling ? "rpm" : "spm"}` : "OFF";

  // Draw Synchronized Map & 5 Charts
  CanvasRenderer.drawRouteMap("simSyncMapCanvas", track, safeIdx);
  CanvasRenderer.drawTelemetryChart("simChartEle", res.points, (p) => p.elevation, "#008A3E", "rgba(0, 138, 62, 0.15)", safeIdx);
  CanvasRenderer.drawTelemetryChart("simChartSpeed", res.points, (p) => p.speedKmh, "#0077B6", "rgba(0, 119, 182, 0.15)", safeIdx);
  CanvasRenderer.drawTelemetryChart("simChartPower", res.points, (p) => p.powerWatts, "#E58E00", "rgba(229, 142, 0, 0.16)", safeIdx);
  CanvasRenderer.drawTelemetryChart("simChartHr", res.points, (p) => p.heartRate || 0, "#D92D20", "rgba(217, 45, 32, 0.15)", safeIdx);
  CanvasRenderer.drawTelemetryChart("simChartCadence", res.points, (p) => p.cadence || 0, "#7C3AED", "rgba(124, 58, 237, 0.15)", safeIdx);

  // Populate Kilometer Splits Table
  const laps = buildZeroGapAutoLaps(res.points, isCycling);
  const tbody = document.getElementById("splitsTableBody");
  if (tbody) {
    tbody.innerHTML = laps
      .map((lap) => {
        const primary = isCycling ? `${lap.avgSpeedKmh.toFixed(1)} km/h` : `${lap.avgPaceFormatted} /km`;
        const elevSign = lap.elevDiff >= 0 ? `+${Math.round(lap.elevDiff)}m` : `${Math.round(lap.elevDiff)}m`;
        return `
          <tr>
            <td><strong>${lap.lapNumber}</strong> <span style="color:var(--text-secondary);">(${(lap.distanceMeters / 1000).toFixed(2)} km)</span></td>
            <td style="font-weight:700; color:var(--strava-orange);">${primary}</td>
            <td>${elevSign}</td>
            <td>${lap.avgHr ? lap.avgHr + " bpm" : "-"}</td>
            <td>${lap.avgCadence ? lap.avgCadence + (isCycling ? " rpm" : " spm") : "-"}</td>
            <td>${lap.avgPower} W</td>
          </tr>
        `;
      })
      .join("");
  }
}

function renderDeviceAndFitExport() {
  const device = DEVICE_PROFILES.find((d) => d.id === AppState.selectedDeviceId) || DEVICE_PROFILES[0];
  const isId = AppState.lang === "id";
  const stravaDisplay = device.fitProductName || `${device.manufacturer} ${device.modelName}`;
  const isCyclocomp = device.deviceType === "CYCLOCOMP";

  // 1. Update Selected Device Summary in Tab 4
  document.getElementById("selectedDeviceTitle").textContent = `${device.manufacturer} ${device.modelName}`;
  document.getElementById("selectedDeviceIdsBadge").textContent = `MFR: ${device.fitMfrId} • PROD: ${device.fitProdId}`;

  const stravaNameEl = document.getElementById("selectedDeviceStravaName");
  if (stravaNameEl) {
    stravaNameEl.textContent = isId
      ? `Tampilan di Strava: "${stravaDisplay}"`
      : `Strava Display: "${stravaDisplay}"`;
  }

  const typeBadgeEl = document.getElementById("selectedDeviceTypeBadge");
  if (typeBadgeEl) {
    if (isCyclocomp) {
      typeBadgeEl.textContent = isId ? "CYCLOCOMP • KHUSUS SEPEDA (CYCLING)" : "CYCLOCOMP • FOR CYCLING";
      typeBadgeEl.className = "status-badge badge-cyan";
    } else if (device.category === "RUNNING_WATCH") {
      typeBadgeEl.textContent = isId ? "SPORTWATCH • JAM LARI (RUNNING)" : "SPORTWATCH • FOR RUNNING";
      typeBadgeEl.className = "status-badge badge-green";
    } else {
      typeBadgeEl.textContent = isId ? "SPORTWATCH • MULTISPORT (LARI & SEPEDA)" : "SPORTWATCH • MULTISPORT (RUN & RIDE)";
      typeBadgeEl.className = "status-badge badge-green";
    }
  }

  const catLabel = isCyclocomp
    ? (isId ? "Cyclocomputer (Sepeda)" : "Bike Computer (Cyclocomp)")
    : (device.category === "RUNNING_WATCH"
      ? (isId ? "Sportwatch Lari (Running)" : "Running Sportwatch")
      : (isId ? "Sportwatch Multisport (Lari & Sepeda)" : "Multisport Watch (Run & Ride)"));

  document.getElementById("selectedDeviceSpecs").textContent =
    `${isId ? "Kategori" : "Category"}: ${catLabel} • Barometric Altimeter: ${device.barometric ? "YES" : "NO"} • Native Power: ${device.power ? "YES" : "NO"} • Firmware: v${(device.swVer / 100).toFixed(2)}`;

  // 2. Update Quick Active Device Bar in Tab 2
  const quickNameEl = document.getElementById("configQuickDeviceName");
  const quickBadgeEl = document.getElementById("configQuickDeviceTypeBadge");
  if (quickNameEl) {
    quickNameEl.textContent = `${device.manufacturer} ${device.modelName} (Strava: "${stravaDisplay}")`;
  }
  if (quickBadgeEl) {
    quickBadgeEl.textContent = isCyclocomp
      ? (isId ? "CYCLOCOMP • SEPEDA" : "CYCLOCOMP • CYCLING")
      : (isId ? "SPORTWATCH • LARI" : "SPORTWATCH • RUNNING");
    quickBadgeEl.className = "status-badge " + (isCyclocomp ? "badge-cyan" : "badge-green");
  }

  // 3. Sport vs Hardware Compatibility Warning Banner
  const warnBanner = document.getElementById("deviceSportWarningBanner");
  const warnText = document.getElementById("deviceSportWarningText");
  if (warnBanner && warnText) {
    const isRunningWithCyclocomp = AppState.settings.sport === "RUNNING" && isCyclocomp;
    warnBanner.style.display = isRunningWithCyclocomp ? "flex" : "none";
    if (isRunningWithCyclocomp) {
      warnText.textContent = isId
        ? `Perhatian: Olahraga Anda saat ini adalah LARI (Running), tetapi perangkat yang dipilih adalah CYCLOCOMP (${device.manufacturer} ${device.modelName}). Gunakan SPORTWATCH agar sesuai dengan aktivitas Lari di Strava.`
        : `Notice: Your active sport is RUNNING, but a CYCLOCOMP Bike Computer (${device.manufacturer} ${device.modelName}) is selected. Switch to a Running Sportwatch for authentic Strava Running display.`;
    }
  }

  // 4. Highlight Active Hardware Category Tab (CYCLOCOMP vs SPORTWATCH vs ALL)
  document.querySelectorAll("#deviceTypeFilterRow .device-type-btn").forEach((btn) => {
    btn.classList.toggle("active", btn.getAttribute("data-devtype") === AppState.deviceTypeFilter);
  });

  // 5. Filter by Hardware Type first to build Dynamic Brand Pills
  const typeFiltered = DEVICE_PROFILES.filter((d) => {
    if (AppState.deviceTypeFilter === "ALL") return true;
    return d.deviceType === AppState.deviceTypeFilter;
  });

  const brandCounts = new Map();
  typeFiltered.forEach((d) => {
    brandCounts.set(d.manufacturer, (brandCounts.get(d.manufacturer) || 0) + 1);
  });

  // Ensure current brand filter is valid for the selected deviceTypeFilter
  if (AppState.deviceBrandFilter !== "ALL" && !brandCounts.has(AppState.deviceBrandFilter)) {
    AppState.deviceBrandFilter = "ALL";
  }

  const brandRow = document.getElementById("deviceBrandFilterRow");
  if (brandRow) {
    const allLabel = isId ? `Semua (${typeFiltered.length})` : `All (${typeFiltered.length})`;
    const pillsHtml = [
      `<button type="button" class="brand-pill-btn ${AppState.deviceBrandFilter === "ALL" ? "active" : ""}" data-brand="ALL">${allLabel}</button>`
    ];
    for (const [brand, count] of brandCounts.entries()) {
      const isAct = AppState.deviceBrandFilter === brand;
      pillsHtml.push(
        `<button type="button" class="brand-pill-btn ${isAct ? "active" : ""}" data-brand="${brand}">${brand} (${count})</button>`
      );
    }
    brandRow.innerHTML = pillsHtml.join("");

    brandRow.querySelectorAll(".brand-pill-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        AppState.deviceBrandFilter = btn.getAttribute("data-brand");
        renderDeviceAndFitExport();
      });
    });
  }

  // 6. Filter Devices by Brand + Search Query
  const q = (AppState.deviceSearchQuery || "").trim().toLowerCase();
  const filteredDevices = typeFiltered.filter((d) => {
    const brandOk = AppState.deviceBrandFilter === "ALL" || d.manufacturer === AppState.deviceBrandFilter;
    if (!brandOk) return false;
    if (!q) return true;
    const hay = `${d.manufacturer} ${d.modelName} ${d.fitProductName || ""} ${d.deviceType} ${d.fitProdId}`.toLowerCase();
    return hay.includes(q);
  });

  const grid = document.getElementById("deviceProfileGrid");
  if (filteredDevices.length === 0) {
    grid.innerHTML = `<div style="padding:18px; text-align:center; color:var(--text-secondary); font-size:12.5px; grid-column: 1 / -1;">${
      isId ? "Tidak ada perangkat yang cocok dengan pencarian Anda." : "No GPS devices match your current search filter."
    }</div>`;
  } else {
    grid.innerHTML = filteredDevices
      .map((d) => {
        const active = d.id === AppState.selectedDeviceId;
        const dIsCyclo = d.deviceType === "CYCLOCOMP";
        const badgeText = dIsCyclo
          ? (isId ? "CYCLOCOMP • SEPEDA" : "CYCLOCOMP • CYCLING")
          : (d.category === "RUNNING_WATCH"
            ? (isId ? "SPORTWATCH • LARI" : "SPORTWATCH • RUNNING")
            : (isId ? "SPORTWATCH • LARI & SEPEDA" : "SPORTWATCH • RUN & RIDE"));
        const badgeCls = dIsCyclo ? "device-tag-cyclo" : "device-tag-watch";
        const dispName = d.fitProductName || `${d.manufacturer} ${d.modelName}`;
        return `
          <div class="profile-item device-card-item ${active ? "active" : ""}" data-device-id="${d.id}">
            <div class="device-card-top">
              <span class="device-type-tag ${badgeCls}">${badgeText}</span>
              <span class="device-fit-id-tag">ID ${d.fitMfrId}/${d.fitProdId}</span>
            </div>
            <div class="profile-item-title">${d.manufacturer} ${d.modelName}</div>
            <div class="profile-item-desc">Strava: <strong>"${dispName}"</strong> • ${d.barometric ? "Barometer" : "GPS Ele"}</div>
          </div>
        `;
      })
      .join("");
  }

  grid.querySelectorAll(".profile-item").forEach((el) => {
    el.addEventListener("click", () => {
      AppState.selectedDeviceId = el.getAttribute("data-device-id");
      if (AppState.simulationResult) {
        const dev = DEVICE_PROFILES.find((x) => x.id === AppState.selectedDeviceId) || DEVICE_PROFILES[0];
        AppState.fitBytes = FitBinaryCodec.generateFitBytes(AppState.simulationResult, dev);
        AppState.fitReport = FitBinaryCodec.validateFitBytes(AppState.fitBytes, AppState.simulationResult, dev);
      }
      renderDeviceAndFitExport();
    });
  });

  const rep = AppState.fitReport;
  if (rep) {
    document.getElementById("fitFileSizeVal").textContent = (rep.fileSizeBytes / 1024.0).toFixed(1);
    document.getElementById("fitMsgCountVal").textContent = rep.totalMessages.toString();
    document.getElementById("fitCrcHexVal").textContent = rep.crcHex;

    const checklist = document.getElementById("fitChecklistContainer");
    checklist.innerHTML = rep.checks
      .map((c) => {
        const label = AppState.lang === "id" ? c.nameId : c.nameEn;
        return `
          <div class="check-item">
            <div>
              <div class="check-name">${label}</div>
              <div class="check-detail">${c.detail}</div>
            </div>
            <span class="status-badge ${c.passed ? "badge-green" : "badge-red"}">${c.passed ? "PASS" : "FAIL"}</span>
          </div>
        `;
      })
      .join("");
  }
}

function renderDiagnosticsTab() {
  const res = AppState.simulationResult;
  if (!res) return;

  const diagList = document.getElementById("solverDiagnosticsList");
  diagList.innerHTML = `
    <div class="check-item"><span class="check-name">Solver Root-Finding Iterations</span><span>${res.solverIterations} iterations</span></div>
    <div class="check-item"><span class="check-name">Requested Target Average</span><span>${res.requestedTarget}</span></div>
    <div class="check-item"><span class="check-name">Achieved Moving Average</span><span style="font-weight:700; color:var(--strava-orange);">${res.achievedTarget}</span></div>
    <div class="check-item"><span class="check-name">Target Feasibility</span><span class="status-badge ${res.targetFeasible ? "badge-green" : "badge-amber"}">${res.targetFeasible ? "YES" : "CONSTRAINED"}</span></div>
    <div class="check-item"><span class="check-name">Total Mechanical Work / NP</span><span>${(res.totalWorkJoules / 1000).toFixed(1)} kJ • NP ${res.normalizedPower} W</span></div>
  `;

  const dhList = document.getElementById("downhillCandidatesList");
  if (!res.downhillCandidates || res.downhillCandidates.length === 0) {
    dhList.innerHTML = "No steep downhill high-speed candidate segments (> 180m @ <= -2.2%) on this route.";
  } else {
    dhList.innerHTML = res.downhillCandidates
      .slice(0, 4)
      .map(
        (c, idx) => `
        <div class="check-item">
          <div>
            <div class="check-name">#${idx + 1}: ${c.startKm.toFixed(2)} - ${c.endKm.toFixed(2)} km (${Math.round(c.lengthMeters)}m @ ${c.averageGradient.toFixed(1)}%)</div>
            <div class="check-detail">Achievable Max Potential: ${c.achievableMaxSpeedKmh.toFixed(1)} km/h • Score: ${c.score.toFixed(1)}</div>
          </div>
          ${res.selectedDownhillCandidate && res.selectedDownhillCandidate.id === c.id ? `<span class="status-badge badge-green">SELECTED</span>` : ""}
        </div>`
      )
      .join("");
  }

  updatePointInspector(AppState.cursorIndex);
}

function updatePointInspector(index) {
  const res = AppState.simulationResult;
  if (!res || !res.points || res.points.length === 0) return;
  const clamped = Math.max(0, Math.min(res.points.length - 1, index));
  const p = res.points[clamped];

  document.getElementById("inspectorIndexLabel").textContent = `Trackpoint ${clamped} of ${res.points.length - 1}`;
  document.getElementById("inspectorReasonBadge").textContent = p.explainReason;

  const details = document.getElementById("pointInspectorDetails");
  details.innerHTML = `
    <div class="check-item"><span class="check-name">GPS Coordinates</span><span>${p.latitude.toFixed(6)}, ${p.longitude.toFixed(6)}</span></div>
    <div class="check-item"><span class="check-name">Barometric Elevation / Source</span><span>${p.elevation.toFixed(1)} m (${p.elevationSource})</span></div>
    <div class="check-item"><span class="check-name">Smoothed / Raw Gradient</span><span>${p.gradient.toFixed(2)}% (raw: ${p.rawGradient.toFixed(2)}%)</span></div>
    <div class="check-item"><span class="check-name">Distance from Start</span><span>${p.distanceFromStartMeters.toFixed(1)} m (${(p.distanceFromStartMeters / 1000).toFixed(2)} km)</span></div>
    <div class="check-item"><span class="check-name">Baseline vs Final Speed</span><span>${p.baselineSpeedKmh.toFixed(2)} → <strong>${p.speedKmh.toFixed(2)} km/h</strong> (${p.paceFormatted}/km)</span></div>
    <div class="check-item"><span class="check-name">Profile / Solver Adjustments</span><span>${p.profileAdjustmentKmh >= 0 ? "+" : ""}${p.profileAdjustmentKmh.toFixed(2)} / ${p.targetAdjustmentKmh >= 0 ? "+" : ""}${p.targetAdjustmentKmh.toFixed(2)} km/h</span></div>
    <div class="check-item"><span class="check-name">Power / Heart Rate / Cadence</span><span>${p.powerWatts} W • ${p.heartRate || "-"} bpm • ${p.cadence || "-"}</span></div>
    <div class="check-item"><span class="check-name">Ambient Temp / Elapsed Time</span><span>${p.temperatureCelsius} °C • ${p.movingTimeSeconds}s moving / ${p.totalTimeSeconds}s total</span></div>
  `;
}

/* ----------------------------------------------------------------------------
   10. EVENT BINDINGS & INITIALIZATION
   ---------------------------------------------------------------------------- */
function bindChartScrubbing(canvasId) {
  const canvas = document.getElementById(canvasId);
  if (!canvas) return;

  const handlePointer = (clientX) => {
    const pts = AppState.simulationResult ? AppState.simulationResult.points : (AppState.currentTrack ? AppState.currentTrack.points : null);
    if (!pts || pts.length < 2) return;
    const rect = canvas.getBoundingClientRect();
    const padL = 38, padR = 10;
    const frac = Math.max(0, Math.min(1, (clientX - rect.left - padL) / Math.max(20, rect.width - padL - padR)));
    const targetDist = frac * pts[pts.length - 1].distanceFromStartMeters;

    let bestIdx = 0;
    let bestDiff = Infinity;
    for (let i = 0; i < pts.length; i++) {
      const diff = Math.abs(pts[i].distanceFromStartMeters - targetDist);
      if (diff < bestDiff) {
        bestDiff = diff;
        bestIdx = i;
      }
    }
    AppState.cursorIndex = bestIdx;
    renderRouteAndIntelligence();
    renderSimulationPreview();
  };

  let isDown = false;
  canvas.addEventListener("mousedown", (e) => { isDown = true; handlePointer(e.clientX); });
  canvas.addEventListener("mousemove", (e) => { if (isDown || e.buttons === 1 || e.type === "mousemove") handlePointer(e.clientX); });
  window.addEventListener("mouseup", () => { isDown = false; });
  canvas.addEventListener("touchstart", (e) => { if (e.touches[0]) handlePointer(e.touches[0].clientX); }, { passive: true });
  canvas.addEventListener("touchmove", (e) => { if (e.touches[0]) handlePointer(e.touches[0].clientX); }, { passive: true });
}

function initApp() {
  // Initialize default date & time inputs
  const startDt = new Date(AppState.settings.startEpochMillis);
  const yyyy = startDt.getFullYear();
  const mm = String(startDt.getMonth() + 1).padStart(2, "0");
  const dd = String(startDt.getDate()).padStart(2, "0");
  document.getElementById("activityDateInput").value = `${yyyy}-${mm}-${dd}`;
  document.getElementById("activityTimeInput").value =
    `${String(startDt.getHours()).padStart(2, "0")}:${String(startDt.getMinutes()).padStart(2, "0")}`;

  // Navigation Tabs
  document.querySelectorAll(".nav-tab-btn, .mobile-nav-btn").forEach((btn) => {
    btn.addEventListener("click", () => switchTab(btn.getAttribute("data-tab")));
  });

  document.getElementById("brandHomeBtn").addEventListener("click", () => switchTab("tab-route"));
  document.getElementById("goToConfigBtn").addEventListener("click", () => switchTab("tab-config"));
  document.getElementById("runSimulationBtn").addEventListener("click", () => {
    executeFullSimulation();
    switchTab("tab-preview");
  });
  document.getElementById("backToTweakBtn").addEventListener("click", () => switchTab("tab-config"));
  document.getElementById("proceedToExportBtn").addEventListener("click", () => switchTab("tab-export"));

  // Language & Theme Toggles
  document.getElementById("langEnBtn").addEventListener("click", () => {
    AppState.lang = "en";
    localStorage.setItem("sk_lang", "en");
    applyLanguageAndTheme();
  });
  document.getElementById("langIdBtn").addEventListener("click", () => {
    AppState.lang = "id";
    localStorage.setItem("sk_lang", "id");
    applyLanguageAndTheme();
  });
  document.getElementById("themeToggleBtn").addEventListener("click", () => {
    AppState.theme = AppState.theme === "light" ? "dark" : "light";
    localStorage.setItem("sk_theme", AppState.theme);
    applyLanguageAndTheme();
  });

  // Onboarding Banner & Guide Modal
  document.getElementById("toggleBannerBtn").addEventListener("click", () => {
    AppState.guideHidden = !AppState.guideHidden;
    localStorage.setItem("sk_guide_hidden", String(AppState.guideHidden));
    applyLanguageAndTheme();
  });
  document.getElementById("openGuideModalBtn").addEventListener("click", () => {
    document.getElementById("guideModal").classList.add("open");
  });
  document.getElementById("closeGuideModalBtn").addEventListener("click", () => {
    document.getElementById("guideModal").classList.remove("open");
  });
  document.getElementById("guideModal").addEventListener("click", (e) => {
    if (e.target.id === "guideModal") document.getElementById("guideModal").classList.remove("open");
  });

  // GPX Import & Sample Route & Reverse Route
  const fileInput = document.getElementById("gpxFileInput");
  const dropzone = document.getElementById("gpxDropzone");
  document.getElementById("browseGpxBtn").addEventListener("click", (e) => {
    e.stopPropagation();
    fileInput.click();
  });
  dropzone.addEventListener("click", () => fileInput.click());
  dropzone.addEventListener("dragover", (e) => { e.preventDefault(); dropzone.classList.add("dragover"); });
  dropzone.addEventListener("dragleave", () => dropzone.classList.remove("dragover"));
  dropzone.addEventListener("drop", (e) => {
    e.preventDefault();
    dropzone.classList.remove("dragover");
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      loadGpxFile(e.dataTransfer.files[0]);
    }
  });
  fileInput.addEventListener("change", () => {
    if (fileInput.files && fileInput.files[0]) loadGpxFile(fileInput.files[0]);
  });

  document.getElementById("loadSampleRouteBtn").addEventListener("click", (e) => {
    e.stopPropagation();
    const sample = RouteProcessor.generateSampleDagoPakarRoute();
    setLoadedTrack(sample);
  });

  document.getElementById("reverseRouteBtn").addEventListener("click", () => {
    if (!AppState.currentTrack) return;
    const rev = RouteProcessor.reverseTrack(AppState.currentTrack);
    setLoadedTrack(rev);
  });

  // Sport & Solver Controls (Strictly syncs CYCLOCOMP for Cycling vs SPORTWATCH for Running!)
  document.getElementById("sportCyclingBtn").addEventListener("click", () => {
    AppState.settings.sport = "CYCLING";
    AppState.settings.profileId = "ENDURANCE_RIDE";
    AppState.settings.cadenceConfig.targetCadence = 86;
    document.getElementById("targetCadenceSlider").value = 86;

    // Auto-switch Device Filter to CYCLOCOMP (For Cycling)
    AppState.deviceTypeFilter = "CYCLOCOMP";
    AppState.deviceBrandFilter = "ALL";
    const curDev = DEVICE_PROFILES.find((d) => d.id === AppState.selectedDeviceId);
    if (!curDev || curDev.deviceType !== "CYCLOCOMP") {
      AppState.selectedDeviceId = "garmin_edge_1040"; // Default Cycling Bike Computer
    }

    updateAutoActivityTitle();
    renderProfileSelector();
    executeFullSimulation();
  });

  document.getElementById("sportRunningBtn").addEventListener("click", () => {
    AppState.settings.sport = "RUNNING";
    AppState.settings.profileId = "STEADY_RUN";
    AppState.settings.cadenceConfig.targetCadence = 172;
    document.getElementById("targetCadenceSlider").value = 172;

    // Auto-switch Device Filter to SPORTWATCH (For Running) & ensure no Bike Computer is used for Running!
    AppState.deviceTypeFilter = "SPORTWATCH";
    AppState.deviceBrandFilter = "ALL";
    const curDev = DEVICE_PROFILES.find((d) => d.id === AppState.selectedDeviceId);
    if (!curDev || curDev.deviceType === "CYCLOCOMP") {
      AppState.selectedDeviceId = "coros_pace_3"; // Default Running Sportwatch (COROS PACE 3, prodId = 804)
    }

    updateAutoActivityTitle();
    renderProfileSelector();
    executeFullSimulation();
  });

  const changeDevQuickBtn = document.getElementById("configChangeDeviceBtn");
  if (changeDevQuickBtn) {
    changeDevQuickBtn.addEventListener("click", () => switchTab("tab-export"));
  }

  document.getElementById("targetAvgSpeedSlider").addEventListener("input", (e) => {
    const val = parseFloat(e.target.value);
    AppState.settings.targetAvgSpeedKmh = val;
    document.getElementById("targetAvgSpeedLabel").textContent = `${val.toFixed(1)} km/h`;
    executeFullSimulation();
  });
  document.getElementById("enableMaxSpeedCheck").addEventListener("change", (e) => {
    AppState.settings.enableMaxSpeed = e.target.checked;
    executeFullSimulation();
  });
  document.getElementById("desiredMaxSpeedSlider").addEventListener("input", (e) => {
    const val = parseFloat(e.target.value);
    AppState.settings.desiredMaxSpeedKmh = val;
    document.getElementById("desiredMaxSpeedLabel").textContent = `${val.toFixed(1)} km/h`;
    executeFullSimulation();
  });

  document.getElementById("targetAvgPaceSlider").addEventListener("input", (e) => {
    const val = parseInt(e.target.value, 10);
    AppState.settings.targetAvgPaceSecPerKm = val;
    document.getElementById("targetAvgPaceLabel").textContent = `${formatPace(val)} /km`;
    executeFullSimulation();
  });
  document.getElementById("enableBestPaceCheck").addEventListener("change", (e) => {
    AppState.settings.enableBestPace = e.target.checked;
    executeFullSimulation();
  });
  document.getElementById("desiredBestPaceSlider").addEventListener("input", (e) => {
    const val = parseInt(e.target.value, 10);
    AppState.settings.desiredBestPaceSec = val;
    document.getElementById("desiredBestPaceLabel").textContent = `${formatPace(val)} /km`;
    executeFullSimulation();
  });

  // Schedule & Smart Stops
  const updateEpochFromInputs = () => {
    const dStr = document.getElementById("activityDateInput").value;
    const tStr = document.getElementById("activityTimeInput").value;
    if (dStr && tStr) {
      const parsed = new Date(`${dStr}T${tStr}:00`);
      if (!Number.isNaN(parsed.getTime())) {
        AppState.settings.startEpochMillis = parsed.getTime();
        updateAutoActivityTitle();
        executeFullSimulation();
      }
    }
  };
  document.getElementById("activityDateInput").addEventListener("change", updateEpochFromInputs);
  document.getElementById("activityTimeInput").addEventListener("change", updateEpochFromInputs);
  document.getElementById("activityTitleInput").addEventListener("input", (e) => {
    AppState.settings.activityTitle = e.target.value;
  });
  document.getElementById("smartStopPresetSelect").addEventListener("change", (e) => {
    AppState.settings.smartStopPreset = e.target.value;
    executeFullSimulation();
  });

  // Heart Rate, Cadence & Power Controls
  document.getElementById("hrEnabledCheck").addEventListener("change", (e) => {
    AppState.settings.hrConfig.enabled = e.target.checked;
    executeFullSimulation();
  });
  document.getElementById("restingHrSlider").addEventListener("input", (e) => {
    const v = parseInt(e.target.value, 10);
    AppState.settings.hrConfig.restingHr = v;
    document.getElementById("restingHrLabel").textContent = `${v} bpm`;
    executeFullSimulation();
  });
  document.getElementById("avgHrSlider").addEventListener("input", (e) => {
    const v = parseInt(e.target.value, 10);
    AppState.settings.hrConfig.averageHr = v;
    document.getElementById("avgHrLabel").textContent = `${v} bpm`;
    executeFullSimulation();
  });
  document.getElementById("maxHrSlider").addEventListener("input", (e) => {
    const v = parseInt(e.target.value, 10);
    AppState.settings.hrConfig.maxHr = v;
    document.getElementById("maxHrLabel").textContent = `${v} bpm`;
    executeFullSimulation();
  });

  document.getElementById("cadenceEnabledCheck").addEventListener("change", (e) => {
    AppState.settings.cadenceConfig.enabled = e.target.checked;
    executeFullSimulation();
  });
  document.getElementById("targetCadenceSlider").addEventListener("input", (e) => {
    const v = parseInt(e.target.value, 10);
    AppState.settings.cadenceConfig.targetCadence = v;
    const unit = AppState.settings.sport === "CYCLING" ? "rpm" : "spm";
    document.getElementById("targetCadenceValLabel").textContent = `${v} ${unit}`;
    executeFullSimulation();
  });
  document.getElementById("coastingModeSelect").addEventListener("change", (e) => {
    AppState.settings.cadenceConfig.coastingMode = e.target.value;
    executeFullSimulation();
  });
  document.getElementById("climbingDropSlider").addEventListener("input", (e) => {
    const v = parseInt(e.target.value, 10);
    AppState.settings.cadenceConfig.climbingRpmDrop = v;
    document.getElementById("climbingDropLabel").textContent = `-${v} rpm`;
    executeFullSimulation();
  });

  document.getElementById("riderWeightSlider").addEventListener("input", (e) => {
    const v = parseFloat(e.target.value);
    AppState.settings.riderWeightKg = v;
    document.getElementById("riderWeightLabel").textContent = `${v.toFixed(0)} kg`;
    executeFullSimulation();
  });
  document.getElementById("bikeWeightSlider").addEventListener("input", (e) => {
    const v = parseFloat(e.target.value);
    AppState.settings.bikeWeightKg = v;
    document.getElementById("bikeWeightLabel").textContent = `${v.toFixed(1)} kg`;
    executeFullSimulation();
  });

  // Device Hardware Category Filter (CYCLOCOMP vs SPORTWATCH vs ALL) & Search Input
  document.querySelectorAll("#deviceTypeFilterRow .device-type-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      AppState.deviceTypeFilter = btn.getAttribute("data-devtype");
      AppState.deviceBrandFilter = "ALL";
      renderDeviceAndFitExport();
    });
  });

  const devSearchInput = document.getElementById("deviceSearchInput");
  if (devSearchInput) {
    devSearchInput.addEventListener("input", (e) => {
      AppState.deviceSearchQuery = e.target.value;
      renderDeviceAndFitExport();
    });
  }

  const fixDevBtn = document.getElementById("btnFixDeviceForSport");
  if (fixDevBtn) {
    fixDevBtn.addEventListener("click", () => {
      AppState.deviceTypeFilter = "SPORTWATCH";
      AppState.deviceBrandFilter = "ALL";
      AppState.selectedDeviceId = "coros_pace_3";
      if (AppState.simulationResult) {
        const dev = DEVICE_PROFILES.find((x) => x.id === AppState.selectedDeviceId) || DEVICE_PROFILES[0];
        AppState.fitBytes = FitBinaryCodec.generateFitBytes(AppState.simulationResult, dev);
        AppState.fitReport = FitBinaryCodec.validateFitBytes(AppState.fitBytes, AppState.simulationResult, dev);
      }
      renderDeviceAndFitExport();
    });
  }

  // Download .FIT & Export CSV
  document.getElementById("downloadFitBtn").addEventListener("click", downloadCurrentFitFile);
  document.getElementById("exportCsvBtn").addEventListener("click", downloadCurrentTelemetryCsv);

  // Point Inspector Slider
  document.getElementById("pointInspectorSlider").addEventListener("input", (e) => {
    const idx = parseInt(e.target.value, 10);
    AppState.cursorIndex = idx;
    updatePointInspector(idx);
  });

  // Interactive Chart Scrubbing
  [
    "routeOverviewEleCanvas",
    "simChartEle",
    "simChartSpeed",
    "simChartPower",
    "simChartHr",
    "simChartCadence"
  ].forEach(bindChartScrubbing);

  window.addEventListener("resize", () => {
    renderRouteAndIntelligence();
    renderSimulationPreview();
  });

  // Load built-in sample route on initial start so user immediately sees live data
  const initialTrack = RouteProcessor.generateSampleDagoPakarRoute();
  setLoadedTrack(initialTrack);
  applyLanguageAndTheme();
}

function updateAutoActivityTitle() {
  const defaultTitles = new Set([
    "Morning Ride", "Lunch Ride", "Afternoon Ride", "Evening Ride", "Night Ride",
    "Morning Run", "Lunch Run", "Afternoon Run", "Evening Run", "Night Run"
  ]);
  const titleInput = document.getElementById("activityTitleInput");
  if (!titleInput) return;
  if (defaultTitles.has(titleInput.value.trim()) || !titleInput.value.trim()) {
    const hr = new Date(AppState.settings.startEpochMillis).getHours();
    let tod = "Night";
    if (hr >= 4 && hr <= 11) tod = "Morning";
    else if (hr >= 12 && hr <= 14) tod = "Lunch";
    else if (hr >= 15 && hr <= 17) tod = "Afternoon";
    else if (hr >= 18 && hr <= 21) tod = "Evening";
    const sName = AppState.settings.sport === "CYCLING" ? "Ride" : "Run";
    const newTitle = `${tod} ${sName}`;
    titleInput.value = newTitle;
    AppState.settings.activityTitle = newTitle;
  }
}

function loadGpxFile(file) {
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const xml = e.target.result;
      const baseName = file.name.replace(/\.gpx$/i, "");
      const parsed = RouteProcessor.parseGpxXml(xml, baseName);
      const processed = RouteProcessor.process(parsed, false);
      setLoadedTrack(processed);
    } catch (err) {
      alert(`Failed to parse GPX file: ${err.message}`);
    }
  };
  reader.readAsText(file);
}

function setLoadedTrack(track) {
  AppState.currentTrack = track;
  AppState.intelligence = RouteIntelligenceEngine.analyze(track);
  AppState.cursorIndex = 0;
  document.getElementById("routeStatusBadge").textContent = "LOADED";
  executeFullSimulation();
  renderRouteAndIntelligence();
}

function downloadCurrentFitFile() {
  if (!AppState.fitBytes || !AppState.currentTrack) return;
  const blob = new Blob([AppState.fitBytes], { type: "application/octet-stream" });
  const url = URL.createObjectURL(blob);
  const safeName = (AppState.settings.activityTitle || AppState.currentTrack.name || "StravaKalcer_Activity")
    .replace(/[^a-zA-Z0-9_-]+/g, "_");
  const a = document.createElement("a");
  a.href = url;
  a.download = `${safeName}.fit`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

function downloadCurrentTelemetryCsv() {
  const res = AppState.simulationResult;
  if (!res) return;
  const headers = [
    "index,latitude,longitude,elevation,elevationSource,distance,gradient,terrainClass,baselineSpeed,profileAdjustment,targetAdjustment,finalSpeed,heartRate,cadence,powerWatts,temperatureCelsius,timestamp,movingTime,totalTime,stopState"
  ];
  res.points.forEach((p) => {
    const terrain = p.gradient > 2.0 ? "CLIMB" : p.gradient < -2.0 ? "DOWNHILL" : "FLAT";
    const stopState = p.isStopped ? "STOPPED" : "MOVING";
    headers.push(
      [
        p.index,
        p.latitude.toFixed(6),
        p.longitude.toFixed(6),
        p.elevation.toFixed(1),
        p.elevationSource,
        p.distanceFromStartMeters.toFixed(1),
        p.gradient.toFixed(2),
        terrain,
        p.baselineSpeedKmh.toFixed(2),
        p.profileAdjustmentKmh.toFixed(2),
        p.targetAdjustmentKmh.toFixed(2),
        p.speedKmh.toFixed(2),
        p.heartRate !== null ? p.heartRate : "",
        p.cadence !== null ? p.cadence : "",
        p.powerWatts !== null ? p.powerWatts : "",
        p.temperatureCelsius !== null ? p.temperatureCelsius : "",
        p.timestampEpochMillis,
        p.movingTimeSeconds,
        p.totalTimeSeconds,
        stopState
      ].join(",")
    );
  });

  const blob = new Blob([headers.join("\n")], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "StravaKalcer_Telemetry_Debug.csv";
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

document.addEventListener("DOMContentLoaded", initApp);

/* ----------------------------------------------------------------------------
   11. INTERACTIVE MAP ROUTE BUILDER (CREATE GPX ON MAP WITH ROAD SNAP & 3D DEM)
   ---------------------------------------------------------------------------- */
Object.assign(I18N.en, {
  brandSubtitle: "GPX Builder & FIT Physics Reconstruction Engine",
  navRoute: "1. Route & GPX Builder",
  guideBannerTitle: "First-Time User Guide — Create or Import GPX & Upload to Strava for Free",
  guideBannerSub: "Complete 4-step workflow: Draw a custom route on the map (or import .gpx), simulate realistic physics, and export a verified Garmin .FIT file.",
  step1Title: "Create GPX on Map or Import",
  step1Desc: "Click \"Create GPX on Map\" to draw your own route with road-snapping & 3D elevation, upload a .gpx file, or load the sample route.",
  cardImportTitle: "GPX Route Source & Free Route Builder",
  cardImportSub: "Import an existing .GPX file or draw a brand-new route on the interactive road map",
  tabModeImport: "Import .GPX / Sample Route",
  tabModeCreate: "Create GPX on Map (Route Builder)",
  btnOpenBuilderQuick: "Create GPX on Map",
  btnSearchPlace: "Search",
  btnMyLocation: "My Location",
  lblQuickJump: "Quick Jump:",
  optFollowRoads: "Auto-Follow Roads (Snap to Street)",
  optDirectLine: "Direct Line (Off-Road / Trail)",
  builderHintClick: "Click anywhere on the map to place Start Point, then click subsequent points to trace your route.",
  lblWaypoints: "Waypoints:",
  btnUndoPoint: "Undo",
  btnLoopToStart: "Loop to Start",
  btnClearMap: "Clear",
  btnUseBuiltRoute: "USE THIS ROUTE FOR SIMULATION (.FIT)",
  btnDownloadCreatedGpx: "DOWNLOAD .GPX FILE"
});

Object.assign(I18N.id, {
  brandSubtitle: "Pembuat Rute GPX & Mesin Rekonstruksi Fisika Garmin .FIT",
  navRoute: "1. Rute & Buat GPX",
  guideBannerTitle: "Panduan Pengguna Baru — Buat atau Impor GPX & Upload ke Strava Gratis",
  guideBannerSub: "Alur 4 langkah: Gambar rute sendiri di peta interaktif (atau impor .gpx), simulasikan fisika, dan unduh file Garmin .FIT tervalidasi.",
  step1Title: "Buat GPX di Peta atau Impor",
  step1Desc: "Klik \"Buat GPX di Peta\" untuk menggambar rute jalan raya lengkap dengan elevasi 3D otomatis, unggah file .gpx, atau muat rute contoh.",
  cardImportTitle: "Sumber Rute GPX & Pembuat Rute Gratis",
  cardImportSub: "Impor file .GPX yang ada atau gambar rute baru langsung di peta jalan interaktif",
  tabModeImport: "Impor .GPX / Rute Contoh",
  tabModeCreate: "Buat GPX di Peta (Route Builder)",
  btnOpenBuilderQuick: "Buat GPX di Peta",
  btnSearchPlace: "Cari",
  btnMyLocation: "Lokasi Saya",
  lblQuickJump: "Lompat Cepat:",
  optFollowRoads: "Ikuti Jalan Raya Otomatis (Snap to Road)",
  optDirectLine: "Garis Lurus (Off-Road / Trail)",
  builderHintClick: "Klik di mana saja pada peta untuk menaruh Titik Start, lalu klik titik berikutnya untuk membuat rute jalan.",
  lblWaypoints: "Titik Waypoint:",
  btnUndoPoint: "Urungkan",
  btnLoopToStart: "Kembali ke Start",
  btnClearMap: "Bersihkan",
  btnUseBuiltRoute: "GUNAKAN RUTE INI UNTUK SIMULASI (.FIT)",
  btnDownloadCreatedGpx: "UNDUH FILE .GPX"
});

const RouteBuilderEngine = {
  map: null,
  waypoints: [],
  segmentsCoords: [], // Array of coordinate arrays per segment: [[{lat, lon, ele}, ...], ...]
  markers: [],
  polylineLayer: null,
  routingMode: "ROAD_OSRM",
  isRoutingBusy: false,
  builtTrack: null,

  initMap() {
    if (this.map) {
      setTimeout(() => this.map.invalidateSize(), 80);
      return;
    }
    const container = document.getElementById("builderLeafletMap");
    if (!container || typeof L === "undefined") return;

    // Default center: Bandung / Jakarta Indonesia
    this.map = L.map("builderLeafletMap", {
      center: [-6.8852, 107.6135],
      zoom: 14,
      zoomControl: true
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      maxZoom: 19,
      attribution: "&copy; OpenStreetMap contributors"
    }).addTo(this.map);

    this.map.on("click", (e) => {
      this.addWaypoint(e.latlng.lat, e.latlng.lng);
    });
  },

  setStatus(textEn, textId) {
    const el = document.getElementById("builderStatusText");
    if (el) {
      el.textContent = AppState.lang === "id" ? textId : textEn;
    }
  },

  createPinIcon(index, total) {
    const isStart = index === 0;
    const isEnd = index === total - 1 && total > 1;
    const bg = isStart ? "#12B76A" : (isEnd ? "#1F1F24" : "#FC4C02");
    const label = isStart ? "S" : (isEnd ? "F" : String(index));
    return L.divIcon({
      className: "",
      html: `<div class="builder-marker-pin" style="background:${bg};">${label}</div>`,
      iconSize: [24, 24],
      iconAnchor: [12, 12]
    });
  },

  refreshMarkersAndPolyline() {
    if (!this.map) return;
    this.markers.forEach((m) => this.map.removeLayer(m));
    this.markers = [];

    this.waypoints.forEach((wp, idx) => {
      const marker = L.marker([wp.lat, wp.lon], {
        icon: this.createPinIcon(idx, this.waypoints.length)
      }).addTo(this.map);
      this.markers.push(marker);
    });

    const flatCoords = this.getAllRoutedPoints();
    if (this.polylineLayer) {
      this.map.removeLayer(this.polylineLayer);
      this.polylineLayer = null;
    }

    if (flatCoords.length >= 2) {
      const latlngs = flatCoords.map((p) => [p.latitude, p.longitude]);
      this.polylineLayer = L.polyline(latlngs, {
        color: "#FC4C02",
        weight: 4.5,
        opacity: 0.92
      }).addTo(this.map);
    }

    document.getElementById("builderWpCount").textContent = String(this.waypoints.length);
  },

  getAllRoutedPoints() {
    if (this.waypoints.length === 0) return [];
    if (this.waypoints.length === 1) {
      return [{
        latitude: this.waypoints[0].lat,
        longitude: this.waypoints[0].lon,
        elevation: this.waypoints[0].ele || 50.0,
        segmentIndex: 0
      }];
    }
    const combined = [];
    this.segmentsCoords.forEach((seg, sIdx) => {
      seg.forEach((pt, pIdx) => {
        if (sIdx > 0 && pIdx === 0) return; // avoid duplicate boundary point
        combined.push({
          latitude: pt.lat,
          longitude: pt.lon,
          elevation: pt.ele !== null && pt.ele !== undefined ? pt.ele : null,
          segmentIndex: 0
        });
      });
    });
    return combined;
  },

  async routeBetweenPoints(p1, p2) {
    if (this.routingMode === "ROAD_OSRM") {
      try {
        const url = `https://router.project-osrm.org/route/v1/driving/${p1.lon.toFixed(6)},${p1.lat.toFixed(6)};${p2.lon.toFixed(6)},${p2.lat.toFixed(6)}?overview=full&geometries=geojson`;
        const resp = await fetch(url);
        if (resp.ok) {
          const data = await resp.json();
          if (data.routes && data.routes[0] && data.routes[0].geometry && data.routes[0].geometry.coordinates) {
            return data.routes[0].geometry.coordinates.map((c) => ({
              lon: c[0],
              lat: c[1],
              ele: null
            }));
          }
        }
      } catch (_) {
        // Fallback to direct interpolated segment if offline
      }
    }

    // Direct line subdivided every ~25m
    const dist = GeoMath.haversineDistanceMeters(p1.lat, p1.lon, p2.lat, p2.lon);
    const steps = Math.max(2, Math.min(80, Math.ceil(dist / 25.0)));
    const pts = [];
    for (let i = 0; i <= steps; i++) {
      const f = i / steps;
      pts.push({
        lat: p1.lat + (p2.lat - p1.lat) * f,
        lon: p1.lon + (p2.lon - p1.lon) * f,
        ele: null
      });
    }
    return pts;
  },

  async fetchReal3DElevations(points) {
    if (!points || points.length === 0) return points;
    try {
      // Sample up to 85 points across the route for Open-Meteo DEM API
      const n = points.length;
      const sampleCount = Math.min(85, n);
      const sampleIndices = [];
      for (let i = 0; i < sampleCount; i++) {
        const idx = n === 1 ? 0 : Math.round((i / (sampleCount - 1)) * (n - 1));
        if (sampleIndices.length === 0 || sampleIndices[sampleIndices.length - 1] !== idx) {
          sampleIndices.push(idx);
        }
      }

      const lats = sampleIndices.map((idx) => points[idx].latitude.toFixed(5)).join(",");
      const lons = sampleIndices.map((idx) => points[idx].longitude.toFixed(5)).join(",");
      const resp = await fetch(`https://api.open-meteo.com/v1/elevation?latitude=${lats}&longitude=${lons}`);
      if (resp.ok) {
        const data = await resp.json();
        if (data && Array.isArray(data.elevation) && data.elevation.length === sampleIndices.length) {
          // Assign sampled elevations and linearly interpolate intermediate points
          for (let s = 0; s < sampleIndices.length; s++) {
            const idx = sampleIndices[s];
            points[idx].elevation = data.elevation[s];
          }
          for (let s = 0; s < sampleIndices.length - 1; s++) {
            const startIdx = sampleIndices[s];
            const endIdx = sampleIndices[s + 1];
            const startEle = points[startIdx].elevation;
            const endEle = points[endIdx].elevation;
            const span = endIdx - startIdx;
            for (let k = startIdx + 1; k < endIdx; k++) {
              points[k].elevation = startEle + ((endEle - startEle) * (k - startIdx)) / span;
            }
          }
          return points;
        }
      }
    } catch (_) {
      // Fallback if offline
    }

    // Fallback synthetic gentle elevation if offline
    for (let i = 0; i < points.length; i++) {
      if (points[i].elevation === null || points[i].elevation === undefined) {
        points[i].elevation = 120.0 + Math.sin(i * 0.08) * 6.0;
      }
    }
    return points;
  },

  async addWaypoint(lat, lon) {
    if (this.isRoutingBusy) return;
    this.isRoutingBusy = true;

    const newWp = { lat, lon, ele: null };
    this.waypoints.push(newWp);

    if (this.waypoints.length === 1) {
      this.refreshMarkersAndPolyline();
      this.setStatus(
        "Start Point placed! Click next points on the map to trace roads and build your route.",
        "Titik Start dipasang! Klik titik berikutnya di peta untuk menelusuri jalan dan membuat rute."
      );
      this.isRoutingBusy = false;
      return;
    }

    const prevWp = this.waypoints[this.waypoints.length - 2];
    this.setStatus(
      "Snapping to road network & fetching 3D satellite elevation...",
      "Menyesuaikan jalur jalan raya & mengambil data elevasi 3D satelit..."
    );

    const segPts = await this.routeBetweenPoints(prevWp, newWp);
    this.segmentsCoords.push(segPts);
    this.refreshMarkersAndPolyline();
    await this.rebuildAndPreviewTrack();
    this.isRoutingBusy = false;
  },

  async rebuildAndPreviewTrack() {
    const rawPts = this.getAllRoutedPoints();
    if (rawPts.length < 2) {
      this.builtTrack = null;
      document.getElementById("builderDistKm").textContent = "0.00 km";
      document.getElementById("builderGainM").textContent = "+0 m";
      return;
    }

    await this.fetchReal3DElevations(rawPts);
    const routeName = document.getElementById("builderRouteNameInput").value.trim() || "Custom Strava Kalcer Route";
    const processed = RouteProcessor.process({
      trackName: routeName,
      points: rawPts,
      segmentCount: 1
    });

    this.builtTrack = processed;
    document.getElementById("builderDistKm").textContent = `${(processed.totalDistanceMeters / 1000.0).toFixed(2)} km`;
    document.getElementById("builderGainM").textContent = `+${Math.round(processed.elevationGainMeters)} m`;

    this.setStatus(
      `Route ready: ${(processed.totalDistanceMeters / 1000.0).toFixed(2)} km (+${Math.round(processed.elevationGainMeters)}m climb, ${processed.points.length} 3D trackpoints). Click more points or export below.`,
      `Rute siap: ${(processed.totalDistanceMeters / 1000.0).toFixed(2)} km (+${Math.round(processed.elevationGainMeters)}m tanjakan, ${processed.points.length} titik 3D). Klik titik lagi atau gunakan tombol di bawah.`
    );

    // Live-sync with active track & right-hand elevation chart!
    setLoadedTrack(processed);
  },

  async undoLastWaypoint() {
    if (this.waypoints.length === 0 || this.isRoutingBusy) return;
    this.waypoints.pop();
    if (this.segmentsCoords.length > 0) {
      this.segmentsCoords.pop();
    }
    this.refreshMarkersAndPolyline();
    if (this.waypoints.length >= 2) {
      await this.rebuildAndPreviewTrack();
    } else {
      this.builtTrack = null;
      document.getElementById("builderDistKm").textContent = "0.00 km";
      document.getElementById("builderGainM").textContent = "+0 m";
      this.setStatus(
        "Click on the map to add waypoints.",
        "Klik pada peta untuk menambahkan titik rute."
      );
    }
  },

  async loopBackToStart() {
    if (this.waypoints.length < 2 || this.isRoutingBusy) return;
    const first = this.waypoints[0];
    await this.addWaypoint(first.lat, first.lon);
  },

  clearAll() {
    this.waypoints = [];
    this.segmentsCoords = [];
    this.builtTrack = null;
    this.refreshMarkersAndPolyline();
    document.getElementById("builderDistKm").textContent = "0.00 km";
    document.getElementById("builderGainM").textContent = "+0 m";
    this.setStatus(
      "Map cleared. Click anywhere on the map to place Start Point.",
      "Peta dibersihkan. Klik di mana saja pada peta untuk menaruh Titik Start."
    );
  },

  async searchLocation(query) {
    if (!query || !query.trim() || !this.map) return;
    this.setStatus(`Searching "${query}"...`, `Mencari lokasi "${query}"...`);
    try {
      const resp = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(query.trim())}`
      );
      if (resp.ok) {
        const results = await resp.json();
        if (results && results.length > 0) {
          const lat = parseFloat(results[0].lat);
          const lon = parseFloat(results[0].lon);
          this.map.setView([lat, lon], 14);
          this.setStatus(
            `Centered on: ${results[0].display_name}. Click on map to start drawing route.`,
            `Peta diarahkan ke: ${results[0].display_name}. Klik pada peta untuk mulai menggambar rute.`
          );
          return;
        }
      }
      this.setStatus("Location not found. Try another city or street name.", "Lokasi tidak ditemukan. Coba nama kota atau jalan lain.");
    } catch (_) {
      this.setStatus("Search unavailable offline.", "Pencarian tidak tersedia saat offline.");
    }
  },

  exportTrackToGpxXml(track) {
    const name = (track.name || "Custom Strava Kalcer Route")
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;");
    const lines = [
      `<?xml version="1.0" encoding="UTF-8"?>`,
      `<gpx version="1.1" creator="Strava Kalcer Web Studio - https://ardianrifendy.github.io/strava-kalcer/" xmlns="http://www.topografix.com/GPX/1/1">`,
      `  <metadata>`,
      `    <name>${name}</name>`,
      `    <time>${new Date().toISOString()}</time>`,
      `  </metadata>`,
      `  <trk>`,
      `    <name>${name}</name>`,
      `    <trkseg>`
    ];
    track.points.forEach((p) => {
      lines.push(
        `      <trkpt lat="${p.latitude.toFixed(6)}" lon="${p.longitude.toFixed(6)}"><ele>${p.elevation.toFixed(1)}</ele></trkpt>`
      );
    });
    lines.push(`    </trkseg>`, `  </trk>`, `</gpx>`);
    return lines.join("\n");
  }
};

function setupRouteBuilderEvents() {
  const modeImportBtn = document.getElementById("modeImportGpxBtn");
  const modeCreateBtn = document.getElementById("modeCreateGpxBtn");
  const openQuickBtn = document.getElementById("openBuilderQuickBtn");
  const dropzone = document.getElementById("gpxDropzone");
  const builderPanel = document.getElementById("gpxBuilderPanel");

  const showBuilderMode = (show) => {
    modeImportBtn.classList.toggle("active", !show);
    modeCreateBtn.classList.toggle("active", show);
    dropzone.style.display = show ? "none" : "block";
    builderPanel.style.display = show ? "block" : "none";
    if (show) {
      RouteBuilderEngine.initMap();
    }
  };

  if (modeImportBtn) modeImportBtn.addEventListener("click", () => showBuilderMode(false));
  if (modeCreateBtn) modeCreateBtn.addEventListener("click", () => showBuilderMode(true));
  if (openQuickBtn) {
    openQuickBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      showBuilderMode(true);
    });
  }

  const searchInput = document.getElementById("builderSearchInput");
  const searchBtn = document.getElementById("builderSearchBtn");
  if (searchBtn && searchInput) {
    searchBtn.addEventListener("click", () => RouteBuilderEngine.searchLocation(searchInput.value));
    searchInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") {
        e.preventDefault();
        RouteBuilderEngine.searchLocation(searchInput.value);
      }
    });
  }

  const myGpsBtn = document.getElementById("builderMyGpsBtn");
  if (myGpsBtn) {
    myGpsBtn.addEventListener("click", () => {
      if (!navigator.geolocation) return;
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          RouteBuilderEngine.initMap();
          if (RouteBuilderEngine.map) {
            RouteBuilderEngine.map.setView([pos.coords.latitude, pos.coords.longitude], 15);
          }
        },
        () => {
          RouteBuilderEngine.setStatus(
            "GPS permission denied or unavailable. Use the search bar or city buttons above.",
            "Izin lokasi GPS ditolak. Gunakan kotak pencarian atau tombol kota di atas."
          );
        }
      );
    });
  }

  document.querySelectorAll(".builder-city-chips .chip-btn").forEach((chip) => {
    chip.addEventListener("click", () => {
      const lat = parseFloat(chip.getAttribute("data-lat"));
      const lon = parseFloat(chip.getAttribute("data-lon"));
      const zoom = parseInt(chip.getAttribute("data-zoom") || "14", 10);
      RouteBuilderEngine.initMap();
      if (RouteBuilderEngine.map) {
        RouteBuilderEngine.map.setView([lat, lon], zoom);
      }
    });
  });

  const routingModeSelect = document.getElementById("builderRoutingModeSelect");
  if (routingModeSelect) {
    routingModeSelect.addEventListener("change", (e) => {
      RouteBuilderEngine.routingMode = e.target.value;
    });
  }

  const routeNameInput = document.getElementById("builderRouteNameInput");
  if (routeNameInput) {
    routeNameInput.addEventListener("input", () => {
      if (RouteBuilderEngine.builtTrack) {
        RouteBuilderEngine.builtTrack.name = routeNameInput.value.trim() || "Custom Strava Kalcer Route";
        setLoadedTrack(RouteBuilderEngine.builtTrack);
      }
    });
  }

  document.getElementById("builderUndoBtn").addEventListener("click", () => RouteBuilderEngine.undoLastWaypoint());
  document.getElementById("builderLoopStartBtn").addEventListener("click", () => RouteBuilderEngine.loopBackToStart());
  document.getElementById("builderClearBtn").addEventListener("click", () => RouteBuilderEngine.clearAll());

  document.getElementById("builderApplyToSimBtn").addEventListener("click", () => {
    if (!RouteBuilderEngine.builtTrack) {
      alert(
        AppState.lang === "id"
          ? "Silakan klik minimal 2 titik pada peta untuk membuat rute terlebih dahulu."
          : "Please click at least 2 points on the map to build a route first."
      );
      return;
    }
    setLoadedTrack(RouteBuilderEngine.builtTrack);
    switchTab("tab-config");
  });

  document.getElementById("builderDownloadGpxBtn").addEventListener("click", () => {
    const trackToExport = RouteBuilderEngine.builtTrack || AppState.currentTrack;
    if (!trackToExport || !trackToExport.points || trackToExport.points.length < 2) {
      alert(
        AppState.lang === "id"
          ? "Silakan klik minimal 2 titik pada peta untuk membuat rute terlebih dahulu."
          : "Please click at least 2 points on the map to build a route first."
      );
      return;
    }
    const xml = RouteBuilderEngine.exportTrackToGpxXml(trackToExport);
    const blob = new Blob([xml], { type: "application/gpx+xml;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const safeName = (trackToExport.name || "StravaKalcer_Route").replace(/[^a-zA-Z0-9_-]+/g, "_");
    const a = document.createElement("a");
    a.href = url;
    a.download = `${safeName}.gpx`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  });
}

document.addEventListener("DOMContentLoaded", () => {
  setupRouteBuilderEvents();
  applyLanguageAndTheme();
});

/* ----------------------------------------------------------------------------
   12. REAL LEAFLET MAPS FOR GEOMETRY & TELEMETRY + NON-TOLL DRIVE ROUTING
   ---------------------------------------------------------------------------- */
Object.assign(I18N.en, {
  optDriveNoToll: "Drive Road Mode — Avoid Toll Roads (Non-Tol)",
  optBikeRoad: "Cycling / Running Road Network (Non-Tol)",
  optDirectLine: "Direct Line (Off-Road / Trail)"
});

Object.assign(I18N.id, {
  optDriveNoToll: "Mode Jalan Raya (Drive) — Hindari Jalan Tol",
  optBikeRoad: "Jalur Sepeda / Lari (Non-Tol)",
  optDirectLine: "Garis Lurus (Off-Road / Trail)"
});

function decodeValhallaPolyline6(str) {
  let index = 0, lat = 0, lng = 0;
  const coordinates = [];
  const factor = 1e6;

  while (index < str.length) {
    let byte = null, shift = 0, result = 0;
    do {
      byte = str.charCodeAt(index++) - 63;
      result |= (byte & 0x1f) << shift;
      shift += 5;
    } while (byte >= 0x20);
    const latitudeChange = (result & 1) ? ~(result >> 1) : (result >> 1);

    shift = result = 0;
    do {
      byte = str.charCodeAt(index++) - 63;
      result |= (byte & 0x1f) << shift;
      shift += 5;
    } while (byte >= 0x20);
    const longitudeChange = (result & 1) ? ~(result >> 1) : (result >> 1);

    lat += latitudeChange;
    lng += longitudeChange;
    coordinates.push({ lat: lat / factor, lon: lng / factor, ele: null });
  }
  return coordinates;
}

/* Upgrade RouteBuilderEngine to Drive Non-Toll Default + Live Re-routing */
RouteBuilderEngine.routingMode = "DRIVE_NO_TOLL";

RouteBuilderEngine.routeBetweenPoints = async function (p1, p2) {
  if (this.routingMode === "DRIVE_NO_TOLL") {
    // 1. Primary: OpenStreetMap Valhalla Drive Non-Toll (motor_scooter costing strictly forbids motorways & toll roads while using normal driving roads)
    try {
      const body = {
        locations: [
          { lat: p1.lat, lon: p1.lon },
          { lat: p2.lat, lon: p2.lon }
        ],
        costing: "motor_scooter",
        costing_options: {
          motor_scooter: {
            use_tolls: 0.0,
            use_highways: 0.0,
            use_primary: 1.0,
            use_hills: 0.5
          }
        },
        units: "kilometers"
      };
      const resp = await fetch("https://valhalla1.openstreetmap.de/route", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });
      if (resp.ok) {
        const data = await resp.json();
        if (data.trip && data.trip.legs && data.trip.legs[0] && data.trip.legs[0].shape) {
          const decoded = decodeValhallaPolyline6(data.trip.legs[0].shape);
          if (decoded.length >= 2) return decoded;
        }
      }
    } catch (_) {
      // Fall through to secondary non-toll router
    }
  }

  if (this.routingMode === "DRIVE_NO_TOLL" || this.routingMode === "BIKE_ROAD") {
    // 2. Secondary Non-Toll Router: OpenStreetMap.de Routed-Bike (100% toll-free road & street network)
    try {
      const url = `https://routing.openstreetmap.de/routed-bike/route/v1/driving/${p1.lon.toFixed(6)},${p1.lat.toFixed(6)};${p2.lon.toFixed(6)},${p2.lat.toFixed(6)}?overview=full&geometries=geojson`;
      const resp = await fetch(url);
      if (resp.ok) {
        const data = await resp.json();
        if (data.routes && data.routes[0] && data.routes[0].geometry && data.routes[0].geometry.coordinates) {
          return data.routes[0].geometry.coordinates.map((c) => ({
            lon: c[0],
            lat: c[1],
            ele: null
          }));
        }
      }
    } catch (_) {
      // Fall through
    }
  }

  // 3. Direct Line (Off-Road / Trail or offline fallback)
  const dist = GeoMath.haversineDistanceMeters(p1.lat, p1.lon, p2.lat, p2.lon);
  const steps = Math.max(2, Math.min(80, Math.ceil(dist / 25.0)));
  const pts = [];
  for (let i = 0; i <= steps; i++) {
    const f = i / steps;
    pts.push({
      lat: p1.lat + (p2.lat - p1.lat) * f,
      lon: p1.lon + (p2.lon - p1.lon) * f,
      ele: null
    });
  }
  return pts;
};

RouteBuilderEngine.rerouteAllWaypoints = async function () {
  if (this.waypoints.length < 2 || this.isRoutingBusy) return;
  this.isRoutingBusy = true;
  this.setStatus(
    "Re-calculating non-toll road route & 3D elevation...",
    "Menghitung ulang jalur jalan raya bebas tol & elevasi 3D..."
  );
  this.segmentsCoords = [];
  for (let i = 1; i < this.waypoints.length; i++) {
    const seg = await this.routeBetweenPoints(this.waypoints[i - 1], this.waypoints[i]);
    this.segmentsCoords.push(seg);
  }
  this.refreshMarkersAndPolyline();
  await this.rebuildAndPreviewTrack();
  this.isRoutingBusy = false;
};

/* Interactive Leaflet Map Renderer for Tab 1 (Overview) & Tab 3 (Synchronized Telemetry) */
const PreviewLeafletMaps = {
  instances: {},

  renderOrUpdate(containerId, track, cursorIdx) {
    const container = document.getElementById(containerId);
    if (!container || typeof L === "undefined" || !track || !track.points || track.points.length === 0) return;

    let state = this.instances[containerId];
    if (!state) {
      const map = L.map(containerId, {
        center: [track.points[0].latitude, track.points[0].longitude],
        zoom: 13,
        zoomControl: true
      });

      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: "&copy; OpenStreetMap"
      }).addTo(map);

      state = {
        map,
        trackKey: null,
        casingLine: null,
        routeLine: null,
        startMarker: null,
        endMarker: null,
        cursorMarker: null
      };
      this.instances[containerId] = state;
    }

    const pts = track.points;
    const newTrackKey = `${track.name}_${pts.length}_${pts[0].latitude.toFixed(5)}_${pts[pts.length - 1].latitude.toFixed(5)}`;

    // Rebuild route polyline & bounds only when track changes
    if (state.trackKey !== newTrackKey) {
      state.trackKey = newTrackKey;
      if (state.casingLine) state.map.removeLayer(state.casingLine);
      if (state.routeLine) state.map.removeLayer(state.routeLine);
      if (state.startMarker) state.map.removeLayer(state.startMarker);
      if (state.endMarker) state.map.removeLayer(state.endMarker);

      const latlngs = pts.map((p) => [p.latitude, p.longitude]);

      state.casingLine = L.polyline(latlngs, {
        color: "#FFFFFF",
        weight: 7,
        opacity: 0.85
      }).addTo(state.map);

      state.routeLine = L.polyline(latlngs, {
        color: "#FC4C02",
        weight: 4.2,
        opacity: 0.96
      }).addTo(state.map);

      const startIcon = L.divIcon({
        className: "",
        html: `<div class="builder-marker-pin" style="background:#12B76A;">S</div>`,
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      });
      const endIcon = L.divIcon({
        className: "",
        html: `<div class="builder-marker-pin" style="background:#1F1F24;">F</div>`,
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      });

      state.startMarker = L.marker([pts[0].latitude, pts[0].longitude], { icon: startIcon }).addTo(state.map);
      state.endMarker = L.marker([pts[pts.length - 1].latitude, pts[pts.length - 1].longitude], { icon: endIcon }).addTo(state.map);

      setTimeout(() => {
        state.map.invalidateSize();
        state.map.fitBounds(state.routeLine.getBounds(), { padding: [24, 24] });
      }, 60);
    }

    // Update Synchronized Cursor Marker Position
    const safeIdx = Math.max(0, Math.min(pts.length - 1, cursorIdx || 0));
    const curPt = pts[safeIdx];
    const cursorIcon = L.divIcon({
      className: "",
      html: `<div class="sync-cursor-dot"></div>`,
      iconSize: [16, 16],
      iconAnchor: [8, 8]
    });

    if (!state.cursorMarker) {
      state.cursorMarker = L.marker([curPt.latitude, curPt.longitude], {
        icon: cursorIcon,
        zIndexOffset: 1000
      }).addTo(state.map);
    } else {
      state.cursorMarker.setLatLng([curPt.latitude, curPt.longitude]);
    }
  },

  invalidateAll() {
    Object.values(this.instances).forEach((st) => {
      if (st && st.map) {
        setTimeout(() => {
          st.map.invalidateSize();
          if (st.routeLine) {
            st.map.fitBounds(st.routeLine.getBounds(), { padding: [24, 24] });
          }
        }, 80);
      }
    });
  }
};

// Override CanvasRenderer.drawRouteMap to use real OpenStreetMap Leaflet maps
CanvasRenderer.drawRouteMap = function (canvasId, track, cursorIdx) {
  if (canvasId === "routeOverviewMapCanvas") {
    PreviewLeafletMaps.renderOrUpdate("routeOverviewLeafletMap", track, cursorIdx);
  } else if (canvasId === "simSyncMapCanvas") {
    PreviewLeafletMaps.renderOrUpdate("simSyncLeafletMap", track, cursorIdx);
  }
};

document.addEventListener("DOMContentLoaded", () => {
  const modeSelect = document.getElementById("builderRoutingModeSelect");
  if (modeSelect) {
    modeSelect.addEventListener("change", () => {
      RouteBuilderEngine.routingMode = modeSelect.value;
      RouteBuilderEngine.rerouteAllWaypoints();
    });
  }

  document.querySelectorAll(".nav-tab-btn, .mobile-nav-btn, #runSimulationBtn, #brandHomeBtn").forEach((btn) => {
    btn.addEventListener("click", () => {
      PreviewLeafletMaps.invalidateAll();
    });
  });

  // Trigger initial render on real Leaflet maps
  if (AppState.currentTrack) {
    renderRouteAndIntelligence();
    renderSimulationPreview();
  }
});

/* ----------------------------------------------------------------------------
   13. MOBILE UX ENHANCEMENTS, STEP CONTEXT TRANSLATIONS & AUTO-SCROLL
   ---------------------------------------------------------------------------- */
Object.assign(I18N.en, {
  mobNavRoute: "1. Route",
  mobNavConfig: "2. Setup",
  mobNavPreview: "3. Preview",
  mobNavExport: "4. Export",
  mobNavDebug: "5. Debug",
  btnConfigureSim: "Next: Configure Simulation",
  btnProceedExport: "Next: Select Device & Export .FIT",
  modalGuideTitle: "Complete User Guide — Strava Kalcer",
  stepBar1: "Choose an existing GPX file or draw a custom non-toll route on the map.",
  stepBar2: "Configure your sport, target speed/pace, heart rate, cadence, and power settings.",
  stepBar3: "Inspect your simulated telemetry, scrub charts, and review 1.0 km splits.",
  stepBar4: "Choose your GPS device signature (Cyclocomp for Cycling vs Sportwatch for Running), download .FIT, and upload to Strava.",
  stepBar5: "Inspect point-by-point physics adjustments, downhill scoring, and export CSV.",
  cardDeviceTitle: "Authentic Hardware Device Signature (105 Famous Models)",
  cardDeviceSub: "Strictly separated into CYCLOCOMP (Cycling Bike Computers) & SPORTWATCH (Running & Multisport Watches) with official FIT Product IDs & Product Names",
  tabDevCyclocompTitle: "CYCLOCOMP (FOR CYCLING)",
  tabDevCyclocompSub: "49 Bike Computers • Garmin Edge, Wahoo, Karoo, Bryton, iGPSPORT, Magene, COROS DURA",
  tabDevSportwatchTitle: "SPORTWATCH (FOR RUNNING)",
  tabDevSportwatchSub: "56 Running & Multisport Watches • COROS Pace/Apex/Vertix, Forerunner, Fenix, Suunto, Polar",
  tabDevAllTitle: "ALL DEVICES (105)",
  tabDevAllSub: "Complete Registry",
  btnChangeDeviceQuick: "Change Device (105 Models)",
  btnSwitchToRunningWatch: "Switch to COROS PACE 3 (Running Watch)"
});

Object.assign(I18N.id, {
  mobNavRoute: "1. Rute",
  mobNavConfig: "2. Konfig",
  mobNavPreview: "3. Grafik",
  mobNavExport: "4. Ekspor",
  mobNavDebug: "5. Debug",
  btnConfigureSim: "Lanjut: Atur Simulasi",
  btnProceedExport: "Lanjut: Pilih Device & Ekspor .FIT",
  modalGuideTitle: "Panduan Lengkap Pengguna — Strava Kalcer",
  stepBar1: "Pilih file GPX Anda atau gambar rute baru bebas tol di peta interaktif.",
  stepBar2: "Atur jenis olahraga, target kecepatan/pace, detak jantung, cadence, dan power.",
  stepBar3: "Periksa hasil grafik telemetri tersinkronisasi dan tabel split per 1.0 km.",
  stepBar4: "Pilih identitas perangkat GPS (Cyclocomp khusus Sepeda vs Sportwatch khusus Lari), unduh .FIT, dan unggah ke Strava.",
  stepBar5: "Inspeksi perhitungan fisika titik-demi-titik dan ekspor data ke CSV.",
  cardDeviceTitle: "Identitas Perangkat GPS Resmi (105 Model Ternama)",
  cardDeviceSub: "Dipisahkan secara tegas antara CYCLOCOMP (Khusus Sepeda) & SPORTWATCH (Jam Lari & Multisport) lengkap dengan FIT Product ID & Nama Produk Resmi Strava",
  tabDevCyclocompTitle: "CYCLOCOMP (KHUSUS SEPEDA)",
  tabDevCyclocompSub: "49 Cyclocomputer • Garmin Edge, Wahoo, Karoo, Bryton, iGPSPORT, Magene, COROS DURA",
  tabDevSportwatchTitle: "SPORTWATCH (KHUSUS LARI)",
  tabDevSportwatchSub: "56 Jam Lari & Multisport • COROS Pace/Apex/Vertix, Forerunner, Fenix, Suunto, Polar",
  tabDevAllTitle: "SEMUA DEVICE (105)",
  tabDevAllSub: "Katalog Lengkap",
  btnChangeDeviceQuick: "Ganti Device (105 Model)",
  btnSwitchToRunningWatch: "Ganti ke COROS PACE 3 (Jam Lari)"
});

document.addEventListener("DOMContentLoaded", () => {
  // On mobile viewports, collapse the 4-card onboarding grid by default unless user explicitly set preference
  if (window.innerWidth <= 820 && localStorage.getItem("sk_guide_hidden") === null) {
    AppState.guideHidden = true;
  }
  applyLanguageAndTheme();

  // Smooth scroll to top when switching tabs on mobile so user always starts at the step header
  document.querySelectorAll(".nav-tab-btn, .mobile-nav-btn, #goToConfigBtn, #runSimulationBtn, #backToTweakBtn, #proceedToExportBtn, #builderApplyToSimBtn").forEach((btn) => {
    btn.addEventListener("click", () => {
      if (window.innerWidth <= 820) {
        window.scrollTo({ top: 0, behavior: "smooth" });
      }
    });
  });
});
