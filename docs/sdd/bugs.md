# NAZE MAPS — Bug Register (SDD-060)

- ID: SDD-060
- Title: Bug Management
- Status: APPROVED (register; perbaikan menunggu migration plan)

Format per bug; severity berdasar dampak & evidence kode, bukan perkiraan.

## BUG-001 — MapView tidak di-destroy saat pindah tab (GL surface leak)
- Severity: HIGH
- Affected feature: Map / navigation
- Reproduction: buka app → pindah ke tab Favorites → kembali ke Map (berulang)
- Expected: MapView.onDestroy() dipanggil saat composable leaving composition
- Actual (baseline): DisposableEffect hanya remove observer — MapView.onDestroy tidak pernah dipanggil untuk instance yang dibuang saat pindah tab
- Root cause: lifecycle forwarding hanya pada LifecycleOwner activity, bukan disposal composition
- Evidence: NazeNavHost.kt (when(selectedTab)); MapLibreMapView.kt (DisposableEffect only removes observer)
- Affected files: map/MapLibreMapView.kt
- Risk: memory leak GL surface
- Status: FIXED (TASK-005): onDestroy dipanggil di onDispose + guard double-destroy. Verifikasi CI pending; runtime leak-check (LeakCanary, tab switching) WAJIB di device sebelum ditutup final.

## BUG-002 — Route request tanpa GPS fix berdiam tanpa feedback
- Severity: HIGH
- Reproduction: buka app, jangan aktifkan My Location, pilih tempat dari search, tekan "Mobil"
- Expected: feedback (banner/snackbar) bahwa lokasi saya belum tersedia
- Actual: requestRoute return early bila myLocation == null — tidak ada reaksi apa pun
- Evidence: MapViewModel.requestRoute (`val from = ... ?: return`)
- Affected files: ui/screens/MapViewModel.kt
- ROOT CAUSE: CONFIRMED (guard tanpa else-branch)
- Status: OPEN — TASK-008

## BUG-003 — Recent camera reset saat recenter (zoom/bearing mungkin ter-reset)
- Severity: MEDIUM
- Reproduction: zoom in manual → aktifkan tracking → tunggu update lokasi
- Expected: kamera pindah target TETAPI zoom dipertahankan
- Actual: UNKNOWN — CameraPosition.Builder().target(...).build() tanpa zoom/bearing; default builder MapLibre perlu verifikasi runtime
- Evidence: MapScreen LaunchedEffect recenter
- Affected files: ui/screens/MapScreen.kt
- Status: OPEN — perlu verifikasi runtime

## BUG-004 — Selected place tidak menggerakkan kamera / tanpa marker
- Severity: MEDIUM
- Reproduction: search tempat jauh → pilih → bottom sheet muncul
- Expected: kamera fly-to tempat + marker
- Actual: peta tetap di posisi lama; tidak ada flyTo/marker untuk selectedPlace
- Evidence: MapScreen hanya updateRouteLine dan updateLocationDot
- Affected files: ui/screens/MapScreen.kt, map/MapOverlay.kt
- Status: OPEN — TASK-008

## BUG-005 — Race condition update MapUiState (lost update)
- Severity: MEDIUM
- Evidence: MapViewModel read-modify-write paralel dari >=4 coroutine
- Affected files: ui/screens/MapViewModel.kt
- Status: FIXED & VERIFIED — TASK-004 commit c583eb7 (semua tulis via `MutableStateFlow.update {}`); CI hijau (konfirmasi user 2026-09-27). CLOSED.

## BUG-006 — "lokasi saya" di DistanceScreen gagal menyesatkan bila GPS belum ada
- Severity: MEDIUM
- Reproduction: tanpa GPS fix, isi titik A "lokasi saya", titik B "Jakarta", hitung
- Expected: pesan "aktifkan lokasi dulu"
- Actual: jatuh ke geocodeOne("lokasi saya") → error "Tidak ditemukan: lokasi saya"
- Evidence: DistanceScreen onClick handler
- Affected files: ui/screens/DistanceScreen.kt
- Status: OPEN

## BUG-007 — Inconsistent history limits (trim 30 vs tampil 20)
- Severity: LOW
- Evidence: HistoryDao (LIMIT 20, trim LIMIT 30)
- Affected files: history/HistoryDao.kt
- Status: OPEN

## BUG-008 — Nominatim: tanpa accept-language; debounce 350ms agresif
- Severity: MEDIUM (risiko 403)
- KOREKSI AUDIT: NominatimApi sudah mengirim format=json, addressdetails=1, limit=8. Kurang: accept-language; debounce agresif.
- Evidence: search/NominatimApi.kt; MapViewModel debounce 350
- Affected files: search/NominatimApi.kt, ui/screens/MapViewModel.kt
- Status: OPEN — TASK-011

## BUG-009 — Dead code: renameFavorite & isSaved tidak pernah dipanggil UI
- Severity: LOW
- Evidence: FavoritesRepository.isSaved; MapViewModel.renameFavorite
- Affected files: favorites/FavoritesRepository.kt, ui/screens/FavoritesScreen.kt
- Status: OPEN (keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Evidence: tidak ada referensi ConnectivityObserver di luar file-nya
- Affected files: network/ConnectivityObserver.kt
- Status: OPEN — TASK-006

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Evidence: NazeNavHost state-based; no androidx.navigation import
- Affected files: app/build.gradle.kts
- Status: OPEN — TASK-012

## BUG-012 — Tracking location tetap jalan saat user pindah tab
- Severity: MEDIUM
- Evidence: comment LocationRepository vs NazeNavHost membongkar MapScreen tanpa stopTracking
- Affected files: ui/navigation/NazeNavHost.kt, ui/screens/MapViewModel.kt
- Status: OPEN (keputusan spec — ADR-005; CATATAN: dengan TASK-005, MapView kini benar-benar di-destroy saat pindah tab, jadi tracking di VM saat user di tab lain kini lebih jelas terasa sebagai pekerjaan yang sia-sia → mendukung opsi stop saat leaving tab, tapi keputusan final tetap butuh verifikasi dampak UX recenter)

## BUG-013 — Build lokal rusak: gradlew/wrapper tidak ada di repo
- Severity: MEDIUM (DX)
- Status: FIXED & VERIFIED — wrapper committed oleh workflow (7da8010, github-actions[bot]). CLOSED.

## BUG-014 — Locale-dependent number formatting di DistanceUtils.format
- Severity: LOW
- Evidence: `"%.1f km".format(km)` default locale
- Affected files: utils/DistanceUtils.kt
- Status: OPEN (keputusan produk dulu)

## BUG-015 — CI release build flaky: lintVitalAnalyzeRelease timeout download dependency
- Severity: MEDIUM (hanya jalur release)
- Actual: `lintVitalAnalyzeRelease` FAILED — "Could not download intellij-core-31.5.2.jar ... Read timed out" (dl.google.com)
- Root cause: transient network timeout; bukan defect kode
- Evidence: log CI run c7ada9d (17:02:26Z)
- Affected files: .github/workflows/android-build.yml
- Status: OPEN — TASK-013 (retry/cache). NOTE: setelah TASK-002..005, run release berikutnya akan mendapat manfaat cache dependency Gradle (setup-gradle v4), kemungkinan mengurangi rekurensi.

## Deprecation warnings (technical debt, bukan bug)
- SearchBar.kt:42 `outlinedTextFieldColors` → `OutlinedTextFieldDefaults.colors`
- MapScreen.kt:293/306 `Icons.Filled.DirectionsWalk/Bike` → AutoMirrored (TD-CQ-5)

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN.
- P-2: Proguard release keep-rules mungkin kurang → release bisa crash. UNKNOWN.
- P-3: Race setStyle saat toggle tema cepat — mitigasi LaunchedEffect ada, edge UNKNOWN.
- P-4: selectHistoryEntry tidak record ulang & tidak fly-to.
