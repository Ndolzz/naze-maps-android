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
- Actual: NazeNavHost memakai when(selectedTab) sehingga MapScreen keluar composition; DisposableEffect hanya remove observer — MapView.onDestroy tidak pernah dipanggil untuk instance yang dibuang
- Possible root cause: lifecycle forwarding terpasang pada LifecycleOwner activity, bukan pada disposal composition
- Evidence: NazeNavHost.kt (when(selectedTab)); MapLibreMapView.kt (DisposableEffect only removes observer)
- Affected files: ui/navigation/NazeNavHost.kt, map/MapLibreMapView.kt
- Risk: memory leak GL surface, device lambat pada tab switching berulang
- Status: OPEN (runtime impact INFERRED, perlu verifikasi LeakCanary)

## BUG-002 — Route request tanpa GPS fix berdiam tanpa feedback
- Severity: HIGH
- Reproduction: buka app, jangan aktifkan My Location, pilih tempat dari search, tekan "Mobil"
- Expected: feedback (banner/snackbar) bahwa lokasi saya belum tersedia
- Actual: requestRoute return early bila myLocation == null — tidak ada reaksi apa pun
- Evidence: MapViewModel.requestRoute (`val from = ... ?: return`)
- Affected files: ui/screens/MapViewModel.kt
- ROOT CAUSE: CONFIRMED (guard tanpa else-branch)
- Status: OPEN

## BUG-003 — Recent camera reset saat recenter (zoom/bearing mungkin ter-reset)
- Severity: MEDIUM
- Reproduction: zoom in manual → aktifkan tracking → tunggu update lokasi
- Expected: kamera pindah target TETAPI zoom dipertahankan
- Actual: UNKNOWN — CameraPosition.Builder().target(...).build() tanpa zoom/bearing; default builder MapLibre perlu verifikasi runtime
- Possible root cause: builder default mengganti zoom saat build()
- Evidence: MapScreen LaunchedEffect recenter
- Affected files: ui/screens/MapScreen.kt
- Status: OPEN — perlu verifikasi runtime sebelum diperbaiki

## BUG-004 — Selected place tidak menggerakkan kamera / tanpa marker
- Severity: MEDIUM
- Reproduction: search tempat jauh → pilih → bottom sheet muncul
- Expected: kamera fly-to tempat + marker
- Actual: peta tetap di posisi lama; tidak ada flyTo/marker untuk selectedPlace di kode mana pun
- Evidence: MapScreen hanya updateRouteLine (baru muncul kalau rute sukses) dan updateLocationDot
- Affected files: ui/screens/MapScreen.kt, map/MapOverlay.kt
- Status: OPEN

## BUG-005 — Race condition update MapUiState (lost update)
- Severity: MEDIUM
- Reproduction: race antara settings collect, tracking collect, compass collect, search job — semua melakukan _uiState.value = _uiState.value.copy(...)
- Expected: update state atomik
- Actual: read-modify-write non-atomik dari >=4 coroutine paralel; update bisa saling menimpa
- Evidence: MapViewModel (semua assignment _uiState)
- Affected files: ui/screens/MapViewModel.kt
- Status: OPEN (dampak probabilistik; fix: MutableStateFlow.update — TASK-004)

## BUG-006 — "lokasi saya" di DistanceScreen gagal menyesatkan bila GPS belum ada
- Severity: MEDIUM
- Reproduction: tanpa mengaktifkan My Location, isi titik A "lokasi saya", titik B "Jakarta", hitung
- Expected: pesan "aktifkan lokasi dulu"
- Actual: loc == null → jatuh ke geocodeOne("lokasi saya") → error "Tidak ditemukan: lokasi saya"
- Evidence: DistanceScreen onClick handler
- Affected files: ui/screens/DistanceScreen.kt
- Status: OPEN

## BUG-007 — Inconsistent history limits (trim 30 vs tampil 20)
- Severity: LOW
- Evidence: HistoryDao (LIMIT 20, trim LIMIT 30)
- Affected files: history/HistoryDao.kt
- Status: OPEN (behaviour masih konsisten-ish; konvensi ambigu)

## BUG-008 — Nominatim: tanpa accept-language; debounce 350ms agresif
- Severity: MEDIUM (risiko diblokir Nominatim 403)
- KOREKSI AUDIT: NominatimApi TIDAK kosong parameter — sudah mengirim format=json, addressdetails=1, limit=8. Yang belum ada: accept-language; dan debounce 350ms per keystroke tetap agresif terhadap usage policy Nominatim (1 req/s untuk aplikasi berat).
- Evidence: search/NominatimApi.kt; MapViewModel debounce 350
- Affected files: search/NominatimApi.kt, ui/screens/MapViewModel.kt
- Status: OPEN — TASK-011 (verifikasi policy dulu)

## BUG-009 — Dead code: renameFavorite & isSaved tidak pernah dipanggil UI
- Severity: LOW
- Evidence: FavoritesRepository.isSaved; MapViewModel.renameFavorite (FavoritesScreen hanya delete)
- Affected files: favorites/FavoritesRepository.kt, ui/screens/FavoritesScreen.kt
- Status: OPEN (pilihan: hapus atau implementasikan UI — keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Evidence: tidak ada referensi ConnectivityObserver di luar file-nya sendiri
- Affected files: network/ConnectivityObserver.kt
- Status: OPEN

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Evidence: NazeNavHost state-based; no androidx.navigation import
- Affected files: app/build.gradle.kts
- Status: OPEN

## BUG-012 — Tracking location tetap jalan saat user pindah tab
- Severity: MEDIUM
- Evidence: comment di LocationRepository ("collectors should cancel when the map leaves the foreground") vs NazeNavHost yang membongkar MapScreen tanpa memanggil stopTracking
- Affected files: ui/navigation/NazeNavHost.kt, ui/screens/MapViewModel.kt
- Status: OPEN (perlu keputusan spec: hentikan di tab lain atau tetap? — ADR-005)

## BUG-013 — Build lokal rusak: gradlew/gradle-wrapper.jar tidak ada di repo
- Severity: MEDIUM (DX; CI punya workaround)
- Evidence: file tree tidak berisi gradlew/gradlew.bat/gradle-wrapper.jar
- Affected files: repo root
- Status: IN PROGRESS — TASK-002 workflow `generate-gradle-wrapper.yml` akan auto-commit wrapper pada push berikutnya.

## BUG-014 — Locale-dependent number formatting di DistanceUtils.format (ditemukan saat TASK-003)
- Severity: LOW
- Reproduction: set device locale ID/DE → hasil "1,2 km" (koma); locale EN → "1.2 km"
- Expected: format konsisten (keputusan produk: Locale.US tetap vs ikut locale user)
- Actual: `"%.1f km".format(km)` memakai default locale — tidak deterministik lintas device
- Evidence: utils/DistanceUtils.kt; characterization test sengaja tidak assert exact-string
- Affected files: utils/DistanceUtils.kt
- Status: OPEN (keputusan produk dulu, baru fix; test sudah menghindari dependensi locale)

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN.
- P-2: Proguard release: rules 257 byte; MapLibre/Retrofit/Gson keep rules mungkin kurang → release build bisa crash. UNKNOWN (apakah release APK pernah dijalankan).
- P-3: Race setStyle saat toggle tema cepat (style reload drop overlays) — mitigasi sudah ada via LaunchedEffect, tetap UNKNOWN edge.
- P-4: selectHistoryEntry tidak record ulang & tidak fly-to (jika PWA expect "bump to top").
