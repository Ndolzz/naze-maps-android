# Feature: Pixel-Art RPG World Map (Redesign)

- Spec ID: FEAT-WMAP
- Status: APPROVED (change request redesain, 2026-10-16)
- ADR terkait: ADR-006 (keputusan teknis redesain). ADR-002/ADR-003 tetap berlaku penuh.
- Dokumen terkait: SDD-010 (Constitution), SDD-020 (Product Spec), SDD-040 (Testing Strategy), FEAT-MAP, FEAT-SEARCH, FEAT-ROUTING, FEAT-SETTINGS.
- Branch implementasi: feature/pixel-art-worldmap

## Purpose

Redesain tampilan peta utama Naze Maps menjadi peta dunia bergaya pixel-art RPG dengan
cartography interaktif — bioma, landmark dunia, emblem pixel orisinal, dock navigasi
mengambang, dan layer kartografi (kompas, garis lintang/bujur) — tanpa mengganti engine,
framework, atau arsitektur yang sudah berlaku (Constitution: no rewrite, no regression).

## Scope & Non-Goals

- Scope: gaya visual peta, landmark terkurasi, emblem, dock, graticule, zoom adaptif.
- Non-Goals: kontur elevasi (data tidak tersedia — lihat WMAP-R5), POI generik/kategori
  massal (tetap non-goal; hanya dataset landmark terkurasi yang masuk scope, lihat
  resolusi konflik di ADR-006), globe 3D (tetap non-goal per SDD-020).

## Requirements

### WMAP-R1 — Peta dunia pixel-art & bioma
- ID: WMAP-R1
- Description: Style MapLibre kustom dibundel di assets aplikasi (light + dark), memakai
  OpenFreeMap vector tiles (OpenMapTiles schema). Bioma dibedakan warna datar konsisten:
  hutan (wood), padang rumput (grass/farmland), gurun (sand), tundra/es (ice), lahan basah
  (wetland), pegunungan/permukaan batu (rock/fell/scree), perairan (water: ocean/lake),
  jalan, bangunan, perbatasan negara, label tempat bertingkat.
- Acceptance Criteria:
  - AC1.1 Style light & dark dimuat dari assets tanpa crash (CI assembleDebug hijau).
  - AC1.2 Semua layer bioma memakai data OpenMapTiles nyata (landcover/water/landuse) —
    warna berubah mengikuti kelas data, bukan gambar statis.
  - AC1.3 Toggle tema dark/light mengganti style pixel-art (menggantikan fungsi FEAT-MAP R2;
    nilai R2 tetap dipertahankan: style mengikuti setting tema).
  - AC1.4 Detail bertingkat: bangunan muncul minzoom 13.5, jalan minor minzoom 10,
    label desa minzoom 11, label benua minzoom 1 (verifikasi via style JSON, unit test
    tidak mungkin; verifikasi CI + review diff + manual device).
- Dependencies: MapLibre SDK 11.5.2, OpenFreeMap (tiles + glyphs, keyless), MapStyle,
  MapLibreMapView (loader style dari JSON).
- Related: FEAT-MAP R1–R5 (dipertahankan), FEAT-SETTINGS (tema).

### WMAP-R2 — Landmark discovery
- ID: WMAP-R2
- Description: Dataset landmark dunia terkurasi (statis, dibundel; koordinat terverifikasi
  dari sumber publik — daftar & bukti di KDoc LandmarkRepositoryImpl): Candi Borobudur,
  Menara Eiffel, Piramida Giza, Colosseum, Taj Mahal, Tembok Besar China (Badaling).
  Ditampilkan sebagai symbol layer dengan emblem kategori, tingkat penonjolan bertingkat
  (tier WORLD/REGION/LOCAL → minzoom 0/4/8), clustering MapLibre pada zoom rendah,
  panel informasi interaktif (bottom sheet) dengan aksi nyata: rute (OSRM, 3 profil),
  simpan favorit (Room), bagikan (intent).
- Acceptance Criteria:
  - AC2.1 Unit test: dataset berisi tepat 6 landmark, id unik, koordinat dalam rentang
    valid [-90,90]/[-180,180], kategori & tier valid, deskripsi tidak kosong.
  - AC2.2 Unit test: koordinat tiap landmark sesuai nilai terverifikasi (toleransi
    presisi 4 desimal, sumber dicatat di KDoc).
  - AC2.3 Tier menentukan minzoom layer (WORLD 0, REGION 4, LOCAL 8) — verifikasi
    konstanta via unit test.
  - AC2.4 Tap landmark di peta membuka panel informasi; tombol Rute/Favorit/Share
    memanggil fungsi yang sudah ada (requestRoute, saveFavorite, share intent).
  - AC2.5 Kamera terbang ke landmark terpilih (zoom 11).
- Dependencies: WMAP-R3 (emblem), MapOverlay/WorldMapOverlay, MapViewModel, FEAT-ROUTING,
  FEAT-FAVORITES, OSRM, Room.
- Related: FEAT-MAP R7 (fly-to, pola sama), FEAT-SEARCH (pemakaian panel serupa).

### WMAP-R3 — Sistem emblem pixel-art
- ID: WMAP-R3
- Description: Emblem pixel-art orisinal didefinisikan sebagai grid ASCII 12x12 + palet
  warna (kode murni, tanpa aset biner): emblem kategori landmark (6), navigasi, eksplorasi,
  koleksi, pengaturan, cluster, graticule. Dua renderer: Bitmap untuk sprite peta
  (Style.addImage) dan Compose Canvas untuk UI. Emoji dilarang sebagai aset final.
- Acceptance Criteria:
  - AC3.1 Unit test: semua emblem valid — baris non-kosong, lebar seragam, setiap karakter
    ada di palet atau transparan ('.'), nama unik.
  - AC3.2 Unit test: setiap kategori landmark punya emblem yang terdaftar di registry.
  - AC3.3 Emblem dipakai di: sprite landmark (map), cluster, dock (4 tab), toggle
    graticule — tanpa emoji.
- Dependencies: MapLibre Style.addImage, Compose Canvas.
- Related: ADR-002 boundary (emblem model murni; bitmap hanya dibuat di layer map/).

### WMAP-R4 — Adaptive floating dock
- ID: WMAP-R4
- Description: NazeBottomNav (NavigationBar Material) diganti dock mengambang (floating):
  pill di bottom-center, bisa diciutkan ke ikon saja, translucent, tidak memakan ruang
  layout Scaffold sehingga peta full-bleed; tab non-peta diberi padding bawah agar
  konten tidak tertutup. Setiap tombol tetap tersambung ke fungsi nyata (pindah tab).
- Acceptance Criteria:
  - AC4.1 Dock punya 4 tab (Map/Favorites/Distance/Settings) + tombol ciut — semua
    melakukan aksi nyata (verifikasi kode + manual).
  - AC4.2 State ciut: default terbuka (label + ikon); ciut menampilkan ikon saja.
  - AC4.3 Layar peta tidak di-cover oleh area dock yang dipesan layout (dock overlay,
    bukan bottomBar Scaffold) — FAB di atas dock (padding).
  - AC4.4 Responsif: lebar dock ≤ 320dp agar muat layar 360dp (verifikasi komposisi +
    manual device).
- Dependencies: NazeTab (BottomNavBar.kt), NazeNavHost, WMAP-R3 (emblem).
- Related: BUG-001/TASK-005 (lifecycle MapView saat pindah tab tetap berlaku).

### WMAP-R5 — Kompas & layer kartografi
- ID: WMAP-R5
- Description: Kompas + reset utara (fitur lama, dipertahankan). Layer kartografi baru:
  graticule (garis lintang & bujur) adaptif per zoom (10° < z4; 5° z4–<6; 1° z≥6),
  garis ekuator & meridian utama ditonjolkan, label koordinat, toggle on/off.
  Semua digambar pada proyeksi Web Mercator yang sama dengan peta (konsisten secara
  koordinat). Kontur elevasi: TIDAK diimplementasikan — OpenFreeMap/OpenMapTiles tidak
  menyediakan data elevasi/terrain; dicatat sebagai data-unavailable (bukan fake).
- Acceptance Criteria:
  - AC5.1 Unit test: intervalForZoom memetakan zoom → interval dengan benar.
  - AC5.2 Unit test: generate(interval) menghasilkan garis ekuator (lat 0) dan meridian
    utama (lng 0) dengan flag major=true; jumlah garis sesuai interval; label ada.
  - AC5.3 Toggle graticule on/off bekerja tanpa reload style (visibility layer).
  - AC5.4 Reset utara (CompassFab) tetap berfungsi (regresi).
- Dependencies: MapLibre LineLayer/SymbolLayer, GeoJsonSource, CompassRepository (lama).
- Related: FEAT-MAP (CompassFab), WMAP-R6 (interval adaptif).

### WMAP-R6 — Zoom adaptif & performa
- ID: WMAP-R6
- Description: Detail bertingkat via minzoom per layer (style), tier landmark per zoom,
  clustering GeoJsonSource MapLibre (clusterMaxZoom 4), graticule regenerasi HANYA saat
  interval berubah (listener camera-idle, bukan tiap frame).
- Acceptance Criteria:
  - AC6.1 Clustering aktif pada zoom < 4 (cluster emblem + jumlah), individual ≥ 4
    (verifikasi konfigurasi + manual device).
  - AC6.2 Graticule hanya di-regenerate saat interval berubah (kode: guard lastInterval).
  - AC6.3 Tidak ada pekerjaan per-frame baru di UI thread selain bawaan MapLibre.
  - AC6.4 CI (assembleDebug + unit test) hijau; regression 12 fitur baseline manual.
- Dependencies: WMAP-R1, WMAP-R2, WMAP-R5.
- Related: TD-PERF-1, TD-PERF-2 (tetap tercatat).

## Resolusi Konflik dengan Spesifikasi Lama

1. SDD-020 Non-Goals "POI categories": dataset landmark terkurasi adalah pengecualian
   bounded yang disetujui lewat ADR-006; POI generik tetap non-goal. SDD-020 diperbarui.
2. FEAT-MAP R2 (style URL dark/light): digantikan oleh style pixel-art dark/light dengan
   semantik sama (mengikuti tema); perubahan dicatat di Changes FEAT-MAP.
3. NazeBottomNav digantikan floating dock (WMAP-R4); NazeTab enum & kontrak onSelect
   dipertahankan agar diff minimal.
4. Semua fitur baseline 12 (peta, lokasi, kompas, search, routing, favorit, riwayat,
   distance, settings, satellite, banner, share) dipertahankan; satellite tetap bekerja
   di atas style baru (hide fill layers — background/fill tetap ada di style pixel).

## Changes
- 2026-10-16 [WMAP]: Spec baru — redesain pixel-art RPG world map (R1–R6), ADR-006.
