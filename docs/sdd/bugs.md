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
- Status: OPEN — TASK-011

## BUG-009 — Dead code: renameFavorite & isSaved tidak pernah dipanggil UI
- Severity: LOW
- Status: OPEN (keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Status: FIXED (TASK-006, 63b3be6). CI + regression airplane-mode pending sebelum CLOSED final.

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Status: OPEN — TASK-012

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
- Status: OPEN — TASK-013

## Deprecation warnings (technical debt, bukan bug)
- SearchBar.kt:42 `outlinedTextFieldColors` → `OutlinedTextFieldDefaults.colors`
- MapScreen.kt `Icons.Filled.DirectionsWalk/Bike` → AutoMirrored (TD-CQ-5)

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN.
- P-2: Proguard release keep-rules mungkin kurang → release bisa crash. UNKNOWN.
- P-3: Race setStyle saat toggle tema cepat — mitigasi LaunchedEffect ada, edge UNKNOWN.
- P-4: selectHistoryEntry tidak record ulang riwayat (fly-to kini teratasi TASK-008b; record ulang masih tidak dilakukan — konsisten baseline).
