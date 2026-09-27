# NAZE MAPS — Bug Register (SDD-060)

- ID: SDD-060
- Title: Bug Management
- Status: APPROVED (register; perbaikan menunggu migration plan)

Format per bug; severity berdasar dampak & evidence kode, bukan perkiraan.

## BUG-001 — MapView tidak di-destroy saat pindah tab (GL surface leak)
- Severity: HIGH
- Status: FIXED (TASK-005, commit ce78f02): onDestroy di onDispose + guard double-destroy. Verifikasi CI pending; runtime leak-check WAJIB sebelum CLOSED final.

## BUG-002 — Route request tanpa GPS fix berdiam tanpa feedback
- Severity: HIGH
- Evidence: MapViewModel.requestRoute (`?: return` tanpa else)
- Status: OPEN — TASK-008

## BUG-003 — Recent camera reset saat recenter (zoom/bearing mungkin ter-reset)
- Severity: MEDIUM
- Status: OPEN — perlu verifikasi runtime (default builder MapLibre UNKNOWN)

## BUG-004 — Selected place tidak menggerakkan kamera / tanpa marker
- Severity: MEDIUM
- Status: OPEN — TASK-008

## BUG-005 — Race condition update MapUiState (lost update)
- Severity: MEDIUM
- Status: FIXED & VERIFIED (TASK-004, commit c583eb7; CI hijau). CLOSED.

## BUG-006 — "lokasi saya" di DistanceScreen gagal menyesatkan bila GPS belum ada
- Severity: MEDIUM
- Status: OPEN

## BUG-007 — Inconsistent history limits (trim 30 vs tampil 20)
- Severity: LOW
- Status: OPEN

## BUG-008 — Nominatim: tanpa accept-language; debounce 350ms agresif
- Severity: MEDIUM (risiko 403)
- KOREKSI AUDIT: format=json, addressdetails=1, limit=8 sudah ada. Kurang: accept-language, debounce.
- Status: OPEN — TASK-011

## BUG-009 — Dead code: renameFavorite & isSaved tidak pernah dipanggil UI
- Severity: LOW
- Status: OPEN (keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Status: FIXED (TASK-006, commit 63b3be6): ConnectivityObserver.observe() di-collect di init MapViewModel; offline → banner NoInternet (tanpa menimpa banner permission/GPS); online kembali → banner dibersihkan. Verifikasi CI + regression manual (airplane mode) pending sebelum CLOSED final.

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Status: OPEN — TASK-012

## BUG-012 — Tracking location tetap jalan saat user pindah tab
- Severity: MEDIUM
- Status: OPEN (keputusan spec — ADR-005; setelah TASK-005, MapView benar-benar di-destroy saat pindah tab → tracking di tab lain kini jelas sia-sia; opsi stop-saat-leaving semakin didukung)

## BUG-013 — Build lokal rusak: gradlew/wrapper tidak ada di repo
- Severity: MEDIUM (DX)
- Status: FIXED & VERIFIED (wrapper 7da8010 oleh bot). CLOSED.

## BUG-014 — Locale-dependent number formatting di DistanceUtils.format
- Severity: LOW
- Status: OPEN (keputusan produk dulu)

## BUG-015 — CI release build flaky: lintVitalAnalyzeRelease timeout download dependency
- Severity: MEDIUM (hanya jalur release)
- Evidence: log CI run c7ada9d — "Could not download intellij-core-31.5.2.jar ... Read timed out"
- Status: OPEN — TASK-013; cache Gradle run berikutnya kemungkinan mengurangi rekurensi.

## Deprecation warnings (technical debt, bukan bug)
- SearchBar.kt:42 `outlinedTextFieldColors` → `OutlinedTextFieldDefaults.colors`
- MapScreen.kt:293/306 `Icons.Filled.DirectionsWalk/Bike` → AutoMirrored (TD-CQ-5)

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN.
- P-2: Proguard release keep-rules mungkin kurang → release bisa crash. UNKNOWN.
- P-3: Race setStyle saat toggle tema cepat — mitigasi LaunchedEffect ada, edge UNKNOWN.
- P-4: selectHistoryEntry tidak record ulang & tidak fly-to.
