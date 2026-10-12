# Strava Kalcer iOS — Native Swift Fitness Simulation & GPX Route Studio

[![iOS](https://img.shields.io/badge/iOS-17.0%2B-black.svg?logo=apple&logoColor=white)](.)
[![Swift](https://img.shields.io/badge/Swift-5.0-FA7343.svg?logo=swift&logoColor=white)](.)
[![SwiftUI](https://img.shields.io/badge/SwiftUI-MapKit-007AFF.svg)](.)
[![Garmin FIT](https://img.shields.io/badge/Garmin%20FIT-Protocol%20100%25-007ACC.svg)](.)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](../LICENSE)

**Strava Kalcer iOS** adalah aplikasi native iOS berkinerja tinggi yang dibangun menggunakan **Swift 5.0**, **SwiftUI**, **MapKit**, **CoreLocation**, dan **Swift Observation (`@Observable`)**. 

Aplikasi ini mengintegrasikan mesin pemodelan fisika kinetik dan fisiologis tubuh manusia untuk mengonversi berkas rute GPX menjadi berkas resmi **Garmin FIT (`.fit`)** secara presisi, dilengkapi dengan **Route Studio** interaktif untuk membuat rute jalan raya langsung di atas peta tanpa memerlukan berkas GPX eksternal.

---

## Fitur Utama iOS

### 1. Route Studio (GPX Route Builder Interaktif)
- **Snap-to-Road Otomatis (`MKDirections`)**:
  - Penarikan jalur otomatis mengikuti jalan raya nyata menggunakan mode **Drive (`.automobile`)**.
  - **Penghindaran Jalan Tol Aktif (`tollPreference = .avoid`)**: Jalur secara ketat menghindari jalan bebas hambatan berbayar (*toll roads*), menjamin keamanan rute untuk pesepeda dan pelari.
- **Generator Smart Loop Otomatis**:
  - Algoritma pembuatan rute memutar tertutup (*closed-loop*) berdasarkan target jarak (misal 5 km, 10 km, 25 km, 40 km) dan sudut mata angin (*heading azimuth*).
  - Mengintegrasikan faktor liku jalan (*road tortuosity factor* $\tau \approx 1.28$) dan variasi harmonik keliling agar bentuk loop organik dan realistis.
- **Pencarian Tempat Cepat (`MKLocalSearch`)**:
  - Cari nama lokasi, kafe, atau landmark kota langsung di peta Apple Maps dan tambahkan ke waypoint dengan satu ketukan.
- **Fitur GPS Start (`CoreLocation`)**:
  - Tombol **Mulai dari Lokasi Saya (GPS Start)** memusatkan peta dan menyetel titik awal rute persis di lokasi fisik Anda saat ini.
  - Tampilan visual *live location* dengan `UserAnnotation()`.
- **Sintesis Elevasi Topografi Alami**:
  - Gelombang Fourier multi-tingkat (makro, meso, mikro) dipadukan dengan *5-point Rolling Gaussian Filter* untuk merekonstruksi profil tanjakan/turunan aspal alami.
- **Ekspor Standar GPX 1.1**:
  - Berkas GPX XML standar yang kompatibel dengan seluruh perangkat GPS (Garmin, Wahoo, Strava, Komoot).

---

### 2. Mesin Simulasi Fisika & Sensorik
- **Pembedaan Olahraga Sepeda vs Lari (Cycling vs Running)**:
  - **Sepeda (Cycling)**: Simulasi daya mekanik (Watts & kJ), hambatan aerodinamis ($C_d A$), gesekan ban ($C_{rr}$), tanjakan gravitasi, Normalized Power (NP Coggan 4th-power), dan sensor power pedal Rally RS200.
  - **Lari (Running)**: Solver kinematik independen berdasarkan target pace (menghindari bias clamping kecepatan sepeda), sensor detak jantung HRM-Pro Plus, dan Running Dynamics Pod tanpa sensor pedal sepeda.
- **Protokol Resmi Garmin FIT 100%**:
  - Enkoder biner bertenaga native Swift: File ID, Device Info, Sport Message 12 (`MESG_SPORT`), Activity, Session, Laps, Records, dan Stop Events.
  - Koordinat dalam format *semicircles* presisi tinggi ($2^{31} / 180^\circ$).
  - Perhitungan CRC-16 standar Garmin.
- **Auto-Lap Splits Terstruktur**:
  - Split otomatis per 1 km (Lari) atau 5 km (Sepeda) lengkap dengan statistik waktu, elevasi, kecepatan rata-rata, suhu, dan detak jantung.
- **Sensor Suhu Lingkungan Realistis**:
  - Rekaman suhu cuaca tropis ($27^\circ\text{C} - 31^\circ\text{C}$) pada setiap data point.

---

### 3. Antarmuka Pengguna (UI & UX)
- **Desain Minimalis Elegan**:
  - Bebas dari emotikon untuk tampilan profesional yang bersih.
  - Dukungan tema ganda (*Dark Mode* dan *Light Mode*).
  - Floating Workflow Dock 3-Tahap yang melayang di bagian tengah bawah:
    1. **Atur Parameter Aktivitas**
    2. **Jalankan Simulasi Fisika**
    3. **Validasi dan Ekspor**
- **Riwayat Aktivitas Lokal**:
  - Simpan dan kelola berkas FIT yang telah diekspor langsung di penyimpanan lokal perangkat.
- **1-Click Share & Upload ke Strava**:
  - Tombol langsung untuk menyimpan berkas FIT atau unggah langsung ke portal web Strava.

---

## Struktur Folder `ios/`

```text
ios/
├── Package.swift                  # Swift Package Manager manifest
├── generate_xcodeproj.py          # Generator otomatis project Xcode (.xcodeproj)
├── sample_loop.gpx                # Sampel rute GPX berbukit (8.9 km)
├── StravaKalcer.xcodeproj/        # File project Xcode native
│   └── project.pbxproj
└── Sources/
    ├── StravaKalcerCore/          # Mesin komputasi dan enkoder independen
    │   ├── Models.swift           # Model data Track, Point, Settings, SimulationResult
    │   ├── GeoMath.swift          # Kalkulasi geodesik Haversine, bearing, gradient smoothing
    │   ├── GpxParser.swift        # XML Parser standar GPX 1.1
    │   ├── RouteCreatorEngine.swift # MapKit road routing, Smart Loop, elevasi topografi, GPX export
    │   ├── Physiology.swift       # Kardiovaskular 1st-order lag, kadensi adaptif, daya Stryd
    │   ├── SimulationEngine.swift # Numerical kinematic root solver & terrain resistance
    │   ├── DeviceProfile.swift    # Registri metadata Garmin, Wahoo, Hammerhead, COROS, Apple
    │   ├── FitEncoder.swift       # Native binary encoder protokol Garmin FIT & CRC-16
    │   └── StravaApiClient.swift  # Klien upload API Strava resmi
    │
    └── StravaKalcerApp/           # Antarmuka SwiftUI iOS
        ├── Info.plist             # Konfigurasi bundle, skema URL, dan izin CoreLocation
        ├── AppViewModel.swift     # State management berbasis Swift Observation (@Observable)
        ├── Views.swift            # Layar Home, Parameter, Preview, dan Floating Workflow Dock
        ├── RouteStudioView.swift  # Layar studio rute interaktif, kontrol peta, dan GPS Start
        ├── FeedAndHistoryViews.swift # Riwayat berkas FIT dan integrasi feed
        └── Assets.xcassets/       # App Icon dan aset grafis
```

---

## Panduan Membangun & Menjalankan (Build & Run)

### Kebutuhan Sistem
- **macOS**: Sonoma 14.0 atau Sequoia 15.0+
- **Xcode**: 15.0+ (iOS SDK 17.0+)
- **Perangkat**: iPhone fisik (iOS 17.0+) atau iOS Simulator

### Langkah Kompilasi & Deploy

1. **Regenerasi Project Xcode (Opsional)**:
   ```bash
   python3 generate_xcodeproj.py
   ```

2. **Kompilasi dengan Xcode Command Line**:
   ```bash
   xcodebuild -project StravaKalcer.xcodeproj -scheme StravaKalcer -destination "generic/platform=iOS" build
   ```

3. **Install ke iPhone Fisik Menggunakan devicectl**:
   ```bash
   # Ganti <DEVICE_UDID> dengan UDID iPhone Anda
   xcrun devicectl device install app --device <DEVICE_UDID> build/Products/Debug-iphoneos/StravaKalcer.app
   xcrun devicectl device process launch --device <DEVICE_UDID> com.ardianrifendy.StravaKalcer
   ```

---

## Lisensi

Project ini dilisensikan di bawah [MIT License](../LICENSE).
