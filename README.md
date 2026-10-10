# Strava Kalcer — Native Android Fitness Simulation & FIT Engine

[![Download APK](https://img.shields.io/badge/Download-APK%20(v1.0.0)-FF5722.svg?logo=android&logoColor=white)](https://github.com/ardianrifendy/strava-kalcer/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-API%2034%20(UpsideDownCake)-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Garmin FIT SDK](https://img.shields.io/badge/Garmin%20FIT%20SDK-v21.141.0-007ACC.svg)](https://developer.garmin.com/fit/overview/)
[![Tests](https://img.shields.io/badge/Unit%20%26%20Integration%20Tests-33%2F33%20Passed-brightgreen.svg)](#panduan-build--menjalankan--how-to-build--test)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

**Strava Kalcer** is a high-fidelity, native Android activity reconstruction and simulation engine built with **Kotlin**, **Jetpack Compose**, and the official **Garmin FIT SDK (`com.garmin:fit`)**.

Unlike simplistic GPX-to-FIT converters that merely stamp static speeds or arbitrary time intervals onto coordinates, **Strava Kalcer** runs a physics-driven numerical solver, continuous cardiovascular/neuromuscular models, power meter dynamics, and split interval structuring to transform raw GPX routes into realistic, internally consistent, and physiologically coherent sports activities.

---

## 📱 Unduh APK Siap Pakai / Download APK

Berkas APK Android yang sudah siap di-install (kompatibel untuk Android 8.0 Oreo ke atas / API 26+) tersedia di GitHub Releases:

| Berkas Instalasi | Versi | Ukuran | Tautan Unduh Langsung |
| :--- | :--- | :--- | :--- |
| **Strava Kalcer Android APK** | `v1.0.0` | ~21.0 MB | 👉 [**Download StravaKalcer-v1.0.0-debug.apk**](https://github.com/ardianrifendy/strava-kalcer/releases/download/v1.0.0/StravaKalcer-v1.0.0-debug.apk) |

> [!TIP]
> **Cara Instalasi di HP Android**:
> 1. Unduh file APK di atas melalui browser HP Anda (atau transfer dari PC).
> 2. Buka file `.apk` yang telah diunduh, lalu pilih **Install**.
> 3. Jika muncul peringatan keamanan, izinkan *Install unknown apps* / *Izinkan dari sumber ini*.

---

## 📑 Daftar Isi / Table of Contents
1. [Unduh APK Siap Pakai / Download APK](#-unduh-apk-siap-pakai--download-apk)
2. [Mengapa Strava Kalcer? / Why Strava Kalcer?](#mengapa-strava-kalcer--why-strava-kalcer)
3. [Arsitektur & Modularitas / Architecture](#arsitektur--modularitas--architecture)
4. [Alur Simulasi / Simulation Pipeline](#alur-simulasi--simulation-pipeline)
5. [Fitur Unggulan / Key Features](#fitur-unggulan--key-features)
6. [Daftar Perangkat yang Didukung / Supported Devices](#daftar-perangkat-yang-didukung--supported-devices)
7. [Berkas Sampel & Deliverables / Sample Deliverables](#berkas-sampel--sample-deliverables)
8. [Panduan Build & Menjalankan / How to Build & Test](#panduan-build--menjalankan--how-to-build--test)
9. [Dokumentasi Algoritma / Mathematical Formulations](#dokumentasi-algoritma--mathematical-formulations)

---

## Mengapa Strava Kalcer? / Why Strava Kalcer?

Perbandingan antara konverter GPX tradisional dengan simulasi cerdas Strava Kalcer:

| Fitur / Parameter | Konverter GPX Standar | **Strava Kalcer Engine** |
| :--- | :--- | :--- |
| **Profil Kecepatan** | Kecepatan konstan / flatline buatan | **Dinamis & berbasis kontur**: menanjak melambat, turunan meluncur cepat |
| **Akselerasi & Deselerasi** | Lompatan instan tidak realistis | **Terikat inersia fisik**: percepatan dan pengereman bertahap (kinematic bounds) |
| **Power Meter (Watts)** | Tidak ada data / 0W | **Simulasi Mekanika Fisik**: aerodinamika ($C_d A$), hambatan ban ($C_{rr}$), gradien kemiringan, & Normalized Power (NP) |
| **Auto-Lap Splits** | 1 lap utuh / tidak ada split | **Pecahan Split Otomatis**: 1 km untuk Lari, 5 km untuk Sepeda dengan statistik lap lengkap |
| **Sensor Suhu** | Tidak ada data | **Suhu Lingkungan Realistis**: Kurva suhu tropis ($27^\circ\text{C} - 30^\circ\text{C}$) pada Record & Lap FIT |
| **Kalkulasi Kalori** | Perkiraan kasar / kosong | **Beban Metabolik Fisik**: Dihitung dari kumulatif mekanis kJ & efisiensi metabolisme tubuh |
| **Detak Jantung (HR)** | Angka acak / konstan tanpa jeda | **Model Asimetris 1st-Order**: ada cardiac lag saat sprint (~12s) dan peluruhan lambat (~26s) |
| **Kadensi (Cadence)** | Flat konstan (misal selalu 90 rpm) | **Adaptif**: torsi tinggi saat tanjakan, freewheel coasting ($0\text{ rpm}$) saat turunan terjal |
| **Validasi Berkas FIT** | Langsung simpan tanpa verifikasi | **Parse-Back Validator**: berkas dibaca ulang oleh parser Garmin resmi untuk verifikasi CRC & konsistensi data |
| **Kepatuhan Protokol** | Sering gagal di platform analitik | **100% Garmin FIT Protocol**: FileId, Activity, Session, Lap, & Record messages |

---

## Arsitektur & Modularitas / Architecture

Aplikasi dirancang dengan arsitektur multi-module yang bersih:

```
Strava Kalcer
├── core-simulation/          # Pure Kotlin JVM library (independen dari Android UI/framework)
│   ├── gpx/                  # GPX 1.1 parser, elevation quality auditor, RouteProcessor, GeoMath
│   ├── intelligence/         # Route intelligence (kategorisasi tanjakan/turunan, grade smoothing)
│   ├── simulation/           # Cycling & running terrain physics, dynamic target-average root solver
│   ├── physiology/           # Continuous HR lag, Cadence, & PowerEngine (Watts, NP, Work kJ, Calories)
│   ├── device/               # Device profile registry (Garmin, Wahoo, COROS, Suunto, Polar, Apple Watch, dll.)
│   ├── fit/                  # Garmin FIT binary encoder & parse-back validator
│   ├── debug/                # CSV diagnostic export & explainability reasons
│   └── sample/               # Synthetic route generator untuk demonstrasi instan
│
└── app/                      # Android Jetpack Compose application module (Material 3 Dark Theme)
    ├── theme/                # Athletic color palette, typography & tokens
    ├── ui/components/        # Multi-chart canvas tersinkronisasi, route polyline map canvas
    ├── ui/screens/           # Home, Route Review, Settings, Preview, Device Select, Export, Debug
    └── viewmodel/            # Reactive state management berbasis Kotlin StateFlow & Coroutines
```

---

## Alur Simulasi / Simulation Pipeline

```mermaid
flowchart TD
    A[Import GPX File / Synthetic Sample] --> B[Route Intelligence & Elevation Audit]
    B --> C[Activity Configuration: Sport, Profile, Target Average]
    C --> D[Terrain Physics Simulation & Target Root Solver]
    D --> E[Power Meter Engine: Instantaneous Watts, NP, & Work kJ]
    E --> F[Continuous Physiology: HR Lag, Cadence, & Ambient Temp]
    F --> G[Unified Synchronized Timeline Multi-Chart Canvas]
    G --> H[Select Device Hardware Profile]
    H --> I[Garmin FIT SDK Binary Generation: Auto-Laps & Records]
    I --> J[Parse-Back Validation: CRC, Semicircles, Messages]
    J --> K[Auto-Upload Strava API / Deep-Link Native App]
```

1. **Import GPX**: Membaca file GPX atau menggunakan generator rute sintetis bawaan.
2. **Audit Kualitas Elevasi**: Mengklasifikasikan data elevasi sebagai *Original*, *Reconstructed*, atau *Unavailable*.
3. **Pengaturan Aktivitas**: Pilihan mode (Cycling / Running), gaya usaha (*Casual*, *Endurance*, *Tempo*, *Race*, *Climber*), dan target rata-rata kecepatan/pace.
4. **Fisika Medan**: Menghitung kecepatan per titik dengan hukum gravitasi, hambatan angin, dan batas akselerasi/deselerasi.
5. **Simulasi Power Meter**: Menghitung daya mekanik per detik (Watts), *Normalized Power* (NP), dan total kerja (*Work* dalam kJ).
6. **Model Fisiologi & Sensor**: Mensimulasikan respon kardiovaskular, kadensi adaptif, suhu lingkungan, dan estimasi kalori.
7. **Pratinjau Sinkron**: Satu kursor interaktif mengontrol peta, profil elevasi, kecepatan, daya, HR, dan kadensi secara *real-time*.
8. **Pilihan Perangkat**: Memilih metadata *head unit* (Garmin Edge, Wahoo, COROS, Suunto, Polar, dll.).
9. **Enkoding FIT & Auto-Laps**: Menghasilkan berkas biner FIT resmi dengan pemecahan lap otomatis (1 km lari / 5 km sepeda).
10. **Validasi Mandiri**: Membaca kembali (*parse-back*) berkas sebelum diekspor untuk menjamin integritas.
11. **Unggah Otomatis & Deep-Link**: Unggah ke Strava Cloud Feed dan buka langsung aktivitas di aplikasi resmi Strava Android.

---

## Fitur Unggulan / Key Features

- **Simulasi Power Meter (Watts & kJ)**:
  Perhitungan daya mekanik dinamis bersepeda berdasarkan hukum hambatan aerodinamis ($C_d A$), hambatan gelinding ban ($C_{rr}$), gradien kemiringan, dan efisiensi *drivetrain* (96.5%). Menghitung *Normalized Power (NP)* standar Coggan rolling 30 detik pangkat 4, serta total kerja mekanis (kJ). Model daya lari berbasis Stryd (1.04 W/kg per m/s + biaya elevasi).
- **Auto-Lap Splits Terstruktur (1 km Running / 5 km Cycling)**:
  File FIT otomatis dipecah menjadi lap-lap berkala dengan statistik per-split lengkap (waktu split, pace/speed rata-rata & maks, HR, kadensi, daya, suhu, elevasi naik/turun). Menghasilkan tab **Splits / Laps** resmi saat diunggah ke Strava atau Garmin Connect.
- **Sensor Suhu Lingkungan Realistis (Ambient Temperature)**:
  Sensor suhu virtual merekam profil suhu tropis ($27^\circ\text{C} - 30^\circ\text{C}$) ke dalam pesan FIT `RecordMesg`, `LapMesg`, dan `SessionMesg`.
- **Kalkulasi Kalori Akurat (Total Calories)**:
  Dihitung dari kerja mekanis kumulatif (kJ) dan efisiensi metabolisme tubuh manusia (~24%), menghasilkan estimasi kalori realistis yang cocok dengan *head unit* Garmin asli.
- **Fitur Balik Rute 1-Klik (Reverse Route)**:
  Tombol **REVERSE** pada layar Route Review memungkinkan pengguna membalik arah rute (titik finis menjadi titik mulai) dengan kalkulasi ulang otomatis untuk jarak, elevasi naik/turun, dan kurva gradien medan.
- **Deep-Link Langsung ke Aplikasi Resmi Strava**:
  Setelah ekspor atau unggah otomatis selesai, tombol **Open in Strava** langsung membuka aktivitas di aplikasi resmi Strava Android (`strava://activities/{activityId}`) dengan *fallback* otomatis ke browser jika aplikasi belum terpasang.
- **Fisika Berbasis Kontur Nyata**:
  Tanjakan curam mengurangi kecepatan secara realistis, sedangkan turunan meningkatkan kecepatan dengan batasan aerodinamika terminal dan perlambatan sebelum tikungan tajam.
- **Root Solver Target Rata-rata**:
  Target kecepatan rata-rata (misal 30 km/jam pada sepeda) atau target pace (misal 5:00 min/km pada lari) dicapai melalui pencarian biner/metode secant pada ruang tenaga, bukan memotong data secara konstan.
- **Fisiologi Berkelanjutan (Lag & Recovery)**:
  Detak jantung naik dengan jeda inersia kardiovaskular ($\tau_{\text{rise}} \approx 12\text{ detik}$) dan turun perlahan saat usaha mereda ($\tau_{\text{decay}} \approx 26\text{ detik}$).
- **Kadensi Fleksibel & Dapat Dikustomisasi (Customizable Cadence)**:
  Target kadensi bebas diatur sesuai preferensi (slider & preset chip RPM untuk sepeda: 60-115 RPM, SPM untuk lari: 145-195 SPM), lengkap dengan tombol kontrol *freewheeling coasting* saat turunan terjal (0 RPM vs 20 RPM) serta adaptasi penurunan torsi saat tanjakan.
- **Satu Kursor Waktu Terpadu**:
  Semua grafik (Elevation, Speed, HR, Cadence) dan titik pada peta diikat oleh satu timeline global. Menggeser kursor pada satu grafik menggerakkan seluruh visualisasi secara harmonis.
- **Full Auto Strava Cloud Upload**:
  Mendukung integrasi resmi Strava API (OAuth 2.0 & Personal Access Token). Cukup aktifkan *Full Auto*, dan setiap kali berkas FIT selesai dibuat, aplikasi otomatis mengunggah aktivitas ke server Strava di latar belakang.
- **Garmin FIT SDK Resmi & Self-Validation**:
  Menggunakan `com.garmin:fit` versi 21.141.0. Menghasilkan koordinat dalam bentuk *semicircles*, timestamp UTC berurutan, akumulasi jarak akurat, serta validasi CRC internal sebelum ekspor.
- **Antarmuka Elegan & Bebas Emotikon**:
  Desain bertema gelap *Athletic Dark Palette* menggunakan Material 3. Menggunakan vector icons resmi Android tanpa emoji.

---

## Daftar Perangkat yang Didukung / Supported Devices

Strava Kalcer menyertakan lebih dari 30 profil perangkat olahraga terverifikasi dengan metadata manufaktur dan produk resmi untuk identifikasi di Strava:

| Manufaktur | Model yang Didukung | Identifier / Tipe |
| :--- | :--- | :--- |
| **Garmin** | Edge 1040, Edge 1030 Plus, Edge 840, Edge 830, Edge 540, Edge 530, Forerunner 965, Forerunner 955, Forerunner 265, Fenix 7 Pro, Enduro 2 | ID Produsen Resmi Garmin (`GARMIN_PRODUCT`) |
| **Wahoo** | ELEMNT ROAM v2, ELEMNT BOLT v2, ELEMNT RIVAL | Metadata Wahoo Fitness |
| **Hammerhead** | Karoo 2, Karoo 3 | Android Cycling Computer Profile |
| **COROS** | PACE 3, PACE 2, VERTIX 2, APEX 2 Pro | Profile Wearable Multisport |
| **Suunto** | Suunto 9 Peak, Suunto Vertical, Suunto Race | Profile Barometric Multisport |
| **Polar** | Grit X Pro, Vantage V2, Pacer Pro | Precision Bio-sensor Profile |
| **Bryton** | Rider 750, Rider S800, Rider 420 | Cycling GPS Computer Profile |
| **Apple** | Apple Watch Ultra 2, Apple Watch Series 9 | WatchOS Workout Profile |

---

## Berkas Sampel & Deliverables / Sample Deliverables

- 📱 [**StravaKalcer-v1.0.0-debug.apk**](https://github.com/ardianrifendy/strava-kalcer/releases/download/v1.0.0/StravaKalcer-v1.0.0-debug.apk): Berkas APK Android siap pakai (~21 MB) untuk pengujian langsung di perangkat fisik atau emulator.
- 🗺️ [`samples/sample_loop.gpx`](samples/sample_loop.gpx): Rute loop rolling terrain 4.5 km lengkap dengan variasi tanjakan dan turunan.
- 🚴 [`samples/sample_validated.fit`](samples/sample_validated.fit): Berkas biner Garmin FIT hasil simulasi yang telah lulus validasi CRC dan siap diunggah ke Garmin Connect, Strava, atau TrainingPeaks.
- 📊 [`samples/sample_diagnostic.csv`](samples/sample_diagnostic.csv): Berkas diagnostik per-detik berisi data: waktu, koordinat, elevasi, grade %, kecepatan, detak jantung, kadensi, daya, suhu, dan alasan model fisika (*explainability log*).

---

## Panduan Build & Menjalankan / How to Build & Test

### Kebutuhan Sistem
- **Java Development Kit (JDK)**: Versi 17 (Microsoft OpenJDK 17 atau Eclipse Temurin 17).
- **Android SDK**: Platform 34 (Android 14) & Build-Tools 34.0.0.
- **Gradle**: 8.5 (sudah disediakan melalui `gradlew.bat`).

### Perintah Pembangunan

1. **Jalankan Seluruh Test Suite Otomatis**:
   ```powershell
   .\gradlew.bat test
   ```
   *Memverifikasi 33 pengujian unit & integrasi untuk fisika, pemecah akar, model jantung, power meter, auto-laps, dan serialisasi FIT biner.*

2. **Kompilasi & Hasilkan File APK**:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
   *Output APK akan berada di:*
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Install Langsung ke HP / Emulator Android**:
   ```powershell
   .\gradlew.bat installDebug
   ```

---

## Dokumentasi Algoritma / Mathematical Formulations

### 1. Jarak Haversine & Geodesik
$$a = \sin^2\left(\frac{\Delta \phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta \lambda}{2}\right)$$
$$s = 2 R \operatorname{atan2}(\sqrt{a}, \sqrt{1 - a})$$

### 2. Gradient Smoothing & Pemodelan Tanjakan
$$G_{\text{raw}} = \frac{\Delta h}{s} \times 100\%,\quad G_{\text{smooth}, i} = \frac{1}{2k + 1} \sum_{j = i - k}^{i + k} G_{\text{raw}, j}$$

### 3. Kinematika Kecepatan & Fisiologi Jantung
- **Akselerasi & Deselerasi Kinematik**: $v_i \le \sqrt{v_{i-1}^2 + 2 a_{\max} s_i}$
- **Respon Heart Rate Asimetris (1st-Order Lag)**: $\tau_{\text{rise}} \approx 12\text{s}$, $\tau_{\text{decay}} \approx 26\text{s}$

### 4. Daya Mekanik Sepeda (Cycling Power Model)
$$P_{\text{total}} = \frac{P_{\text{gravity}} + P_{\text{rolling}} + P_{\text{drag}}}{\eta_{\text{drivetrain}}}$$
- **Hambatan Udara (Aerodynamic Drag)**: $P_{\text{drag}} = \frac{1}{2} C_d A \, \rho \, v^3$
- **Hambatan Gelinding (Rolling Resistance)**: $P_{\text{rolling}} = C_{rr} \, m_{\text{total}} \, g \, v$
- **Gaya Berat Tanjakan (Gravity)**: $P_{\text{gravity}} = m_{\text{total}} \, g \, v \, \sin(\theta) \approx m_{\text{total}} \, g \, v \left(\frac{G\%}{100}\right)$

### 5. Normalized Power (NP Coggan 4th-Power Algorithm)
$$\text{NP} = \left(\frac{1}{N}\sum_{i=1}^N \bar{P}_{30, i}^4\right)^{1/4}$$
di mana $\bar{P}_{30, i}$ adalah rata-rata bergerak daya selama jendela 30 detik.

### 6. Pengeluaran Kalori Metabolik (Metabolic Energy Cost)
$$\text{Calories (kcal)} = \frac{\text{Total Work (Joules)}}{1000 \times 4.184 \times \eta_{\text{gross}}} \approx \frac{\text{Total Work (kJ)}}{1000 \times 0.95} \approx \text{Work (kJ)} \times 1.05$$
*(Asumsi efisiensi metabolisme kotor tubuh manusia $\eta_{\text{gross}} \approx 24\%$)*

### 7. Enkoding Koordinat Garmin FIT
$$\text{semicircles} = \text{degrees} \times \frac{2^{31}}{180}$$

---

## Lisensi / License

Project ini dilisensikan di bawah [MIT License](LICENSE).
Dikembangkan untuk kebutuhan analisis rute olahraga dan rekonstruksi aktivitas digital.
