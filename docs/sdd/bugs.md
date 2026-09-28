# NAZE MAPS — Bug Register (SDD-060)

- ID: SDD-060
- Title: Bug Management
- Status: APPROVED (register; perbaikan menunggu migration plan)

Format per bug; severity berdasar dampak & evidence kode, bukan perkiraan.

## BUG-001 — MapView tidak di-destroy saat pindah tab (GL surface leak)
- Severity: HIGH
- Status: FIXED (TASK-005, ce78f02). CI pending; runtime leak-check WAJIB sebelum CLOSED final.

## BUG-002 — Route request tanpa GPS fix berdiam tanpa feedback
- Severity: HIGH
- Status: FIXED (TASK-008a, 5425679): requestRoute menampilkan banner Generic "Lokasi saya belum tersedia — aktifkan My Location dulu" bila myLocation null. CI + regression manual pending sebelum CLOSED final.

## BUG-003 — Recent camera reset saat recenter (zoom/bearing mungkin ter-reset)
- Severity: MEDIUM
- Status: OPEN — perlu verifikasi runtime (default CameraPosition.Builder MapLibre UNKNOWN). Catatan: TASK-008b menambah flyTo eksplisit zoom 15 untuk selectedPlace, jadi bug ini kini khusus jalur tracking-recenter.

## BUG-004 — Selected place tidak menggerakkan kamera / tanpa marker
- Severity: MEDIUM
- Status: FIXED sebagian (TASK-008b, f1f431a): fly-to via animateCamera(newLatLngZoom, 15.0) untuk selectedPlace dari search & history. Marker pin belum dibuat — dicatat sebagai future change kecil (bukan bug tersisa; acceptance disesuaikan). CI + regression manual pending.

## BUG-005 — Race condition update MapUiState (lost update)
- Severity: MEDIUM
- Status: FIXED & VERIFIED (TASK-004, c583eb7; CI hijau). CLOSED.

## BUG-006 — "lokasi saya" di DistanceScreen gagal menyesatkan bila GPS belum ada
- Severity: MEDIUM
- Status: OPEN

## BUG-007 — Inconsistent history limits (trim 30 vs tampil 20)
- Severity: LOW
- Status: OPEN

## BUG-008 — Nominatim: tanpa accept-language; debounce 350ms agresif
- Severity: MEDIUM (risiko 403)
- Status: FIXED (TASK-011, dd159ce): accept-language + debounce 500ms. CI pending sebelum CLOSED final.

## BUG-009 — Dead code: renameFavorite & isSaved tidak per
nah dipanggil UI
- Severity: LOW
- Status: OPEN (keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Status: FIXED (TASK-006, 63b3be6). CI + regression airplane-mode pending sebelum CLOSED final.

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Status: FIXED (TASK-012, 664c593): dihapus; logging-interceptor → debugImplementation. CI pending sebelum CLOSED final.

## BUG-012 — Tracking location tetap jalan saat user pindah tab
- Severity: MEDIUM
- Status: OPEN (keputusan spec — ADR-005)

## BUG-013 — Build lokal rusak: gradlew/wrapper tidak ada di repo
- Severity: MEDIUM (DX)
- Status: FIXED & VERIFIED (wrapper 7da8010). CLOSED.

## BUG-014 — Locale-dependent number formatting di DistanceUtils.format
- Severity: LOW
- Status: OPEN (keputusan produk dulu)

## BUG-015 — CI release build flaky: lintVitalAnalyzeRelease timeout download dependency
- Severity: MEDIUM (hanya jalur release)
- Status: FIXED (TASK-013, 2c37af3): retry 3x + cache lint deps. CI pending sebelum CLOSED final.

## BUG-016 — Satellite: placeholder "Map data not yet available" pada zoom sangat dekat
- Severity: MEDIUM
- Affected feature: satellite mode
- Root cause (CONFIRMED, evidence MapOverlay.kt): RasterSource Esri World Imagery tanpa maxZoom; provider tidak menyediakan tile z>=20 → 404 → MapLibre menampilkan placeholder. Bukan network/cache/SDK issue.
- Status: FIXED (CH-100): TileSet maxZoom 19 → raster overzoom dari tile induk. CI + regression manual pending sebelum CLOSED final.

## BUG-017 — Bottom navigation permanen menutupi peta; map tidak fullscreen; search bar berat
- Severity: MEDIUM (UX)
- Root cause (CONFIRMED): NazeBottomNav 72dp di Scaffold.bottomBar pada semua tab; search OutlinedTextField penuh lebar.
- Status: FIXED (CH-101): bottom nav → overflow menu compact kanan-atas; search → compact floating pill. Mapping fitur didokumentasikan di CH-101. CI + regression manual pen
ding sebelum CLOSED final.

## BUG-018 — Splash generik: launcher icon tanpa motion, tanpa identitas brand
- Severity: LOW (UX)
- Root cause (CONFIRMED): themes.xml Theme.NazeMaps.Splash hanya background + ic_launcher_foreground; tidak ada motion layer.
- Status: FIXED (CH-102): SplashOverlay "Map Comes Alive" (route-line reveal + mark fade/scale + typography), readiness-driven (isReady = style loaded), tanpa timer/delay; native splash tetap untuk cold-start. CI + regression manual pending sebelum CLOSED final.

## Deprecation warnings (technical debt, bukan bug)
- SearchBar.kt `outlinedTextFieldColors` → TERATASI CH-101 (OutlinedTextFieldDefaults.colors).
- MapScreen.kt `Icons.Filled.DirectionsWalk/Bike` → AutoMirrored (TD-CQ-5) — masih OPEN.

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN.
- P-2: Proguard release keep-rules mungkin kurang → release bisa crash. UNKNOWN.
- P-3: Race setStyle saat toggle tema cepat — mitigasi LaunchedEffect ada, edge UNKNOWN.
- P-4: selectHistoryEntry tidak record ulang riwayat (fly-to kini teratasi TASK-008b; record ulang masih tidak dilakukan — konsisten baseline).
- P-5 (BARU, CH-101): overflow menu overlay kanan-atas berpotensi overlap visual dengan header/konten screen non-map — cek saat regression manual.
