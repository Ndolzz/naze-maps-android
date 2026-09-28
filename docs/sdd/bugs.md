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
- Status: FIXED (TASK-008a, 5425679). CI + regression manual pending sebelum CLOSED final.

## BUG-003 — Recent camera reset saat recenter (zoom/bearing mungkin ter-reset)
- Severity: MEDIUM
- Status: OPEN — perlu verifikasi runtime (default CameraPosition.Builder MapLibre UNKNOWN).

## BUG-004 — Selected place tidak menggerakkan kamera / tanpa marker
- Severity: MEDIUM
- Status: FIXED sebagian (TASK-008b, f1f431a): fly-to zoom 15 untuk selectedPlace. Marker pin = future change kecil. CI + regression manual pending.

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
- Status: FIXED (TASK-011, dd159ce). CI pending sebelum CLOSED final.

## BUG-009 — Dead code: renameFavorite & isSaved tidak pernah dipanggil UI
- Severity: LOW
- Status: OPEN (keputusan via change request)

## BUG-010 — ConnectivityObserver tidak terpakai; banner no-internet hanya reaktif
- Severity: MEDIUM
- Status: FIXED (TASK-006, 63b3be6). CI + regression airplane-mode pending sebelum CLOSED final.

## BUG-011 — Dependency navigation-compose tidak dipakai
- Severity: LOW
- Status: FIXED (TASK-012, 664c593). CI pending sebelum CLOSED final.

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
- Status: FIXED (TASK-013, 2c37af3). CI pending sebelum CLOSED final.

## BUG-016 — Satellite: placeholder "Map data not yet available" pada zoom sangat dekat
- Severity: MEDIUM
- Root cause (CONFIRMED, evidence MapOverlay.kt): RasterSource Esri World Imagery tanpa maxZoom; placeholder muncul saat tile tidak tersedia pada zoom tersebut.
- Evidence tambahan (2026-09-28, MapServer f=json): layanan mendeklarasikan LOD hingga level 23, ketersediaan aktual per-area bervariasi ("one meter or better in many parts of the world"). maxZoom 19 = floor aman global untuk raster overzoom.
- Status: FIXED (CH-100, build 61b98a9): setMaxZoom(19f). CATATAN: screenshot device 2026-09-28 17:19 masih menampilkan placeholder — APK uji dibangun SEBELUM fix yang compilable (98c804d/8323107 gagal compile; fix valid pertama = 61b98a9). Verifikasi ulang wajib dengan build >= 61b98a9. Bila placeholder masih muncul pada build tersebut → buka investigasi baru (network/UA/region coverage), jangan tutup bug.

## BUG-017 — Bottom navigation permanen menutupi peta; map tidak fullscreen; search bar berat
- Severity: MEDIUM (UX)
- Status: FIXED (CH-101): bottom nav → overflow menu; search → compact pill. CI + regression manual pending sebelum CLOSED final.

## BUG-018 — Splash generik: launcher icon tanpa motion, tanpa identitas brand
- Severity: LOW (UX)
- Status: FIXED (CH-102): SplashOverlay "Map Comes Alive", readiness-driven. CI + regression manual pend
ing sebelum CLOSED final.

## BUG-019 — Kompas menumpuk/berasa tidak rapi di area kanan map
- Severity: LOW (UX)
- Root cause (CONFIRMED, evidence screenshot device 2026-09-28 17:16): CompassFab berada di kolom FAB kanan-bawah sementara menu [⋮] + search bar end berdesakan di kanan-atas; hierarchy kontrol kanan tidak konsisten.
- Status: FIXED (CH-103): CompassFab dipindah ke top-end tepat di bawah tombol [⋮] (statusBarsPadding + top 60dp + end 12dp); kolom kanan-bawah kini hanya Layers + MyLocation. Behavior kompas tidak berubah. CI + regression manual pending sebelum CLOSED final.

## BUG-021 — MapScreen.kt: newline mentah di dalam string Kotlin (build gagal kspDebugKotlin)
- Severity: HIGH (build gagal total, PR #1 merah).
- Root cause (CONFIRMED, evidence CI build 209b238, Expecting quotation mark baris 430-462): literal escape newline pada teks bagikan ditulis melalui template literal TypeScript tanpa escape ganda, sehingga yang tersimpan adalah newline mentah di dalam string Kotlin (4 kemunculan: bagikan lokasi + bagikan rute).
- Catatan proses: bukan korupsi transit GitHub. Deteksi indikasi transit (token terbelah) ternyata artefak fetch raw; CI hanya melaporkan error string, tidak ada identifier rusak.
- Status: FIXED (09e689f): tiap newline mentah dalam string diganti kembali menjadi literal escape. Verifikasi fetch balik: 4 literal escape ada, 0 newline mentah. CI + regression pending sebelum CLOSED final.

## Deprecation warnings (technical debt, bukan bug)
- SearchBar.kt outlinedTextFieldColors → TERATASI CH-101.
- MapScreen.kt Icons.Filled.DirectionsWalk/Bike → AutoMirrored (TD-CQ-5) — masih OPEN.

## Potential (belum ada evidence runtime)

- P-1: CompassFab -headingDegrees rotasi vs screen rotation (landscape) — UNKNOWN. (Semakin relevan setelah CH-103: cek di landscape.)
- P-2: Proguard release keep-rules mungkin kurang → release bisa crash. UNKNOWN.
- P-3: Race setStyle saat toggle tema cepat — mitigasi LaunchedEffect ada, edge UNKNOWN.
- P-4: select
HistoryEntry tidak record ulang riwayat — konsisten baseline.
- P-5 (BERGESER ke BUG-019): overlap visual kontrol kanan-atas — kini FIXED (CH-103); sisa cek: overlap menu vs header screen non-map saat regression manual.

## BUG-022 — FakeSettingsDataStore tidak mengikuti interface baru CH-111 (kompilasi test gagal)
- Severity: HIGH (compileDebugUnitTestKotlin gagal; CI merah untuk commit CH-111 ke atas).
- Root cause (CONFIRMED, inspeksi MapViewModelTest.kt): CH-111 menambah themeMode dan setThemeMode ke interface SettingsDataStore, tetapi FakeSettingsDataStore di unit test tidak diperbarui, sehingga fake tidak lagi mengimplementasikan interface (error: does not implement abstract member). Selain itu toggleTheme kini menulis mode, bukan sakelar dark_theme lama, sehingga assertion lama pada settings.darkTheme tidak lagi relevan.
- Perbaikan: fake menambah MutableStateFlow themeMode (default DARK agar isDarkTheme awal tetap benar) dan setThemeMode; test toggleTheme diperbarui mengamati themeMode; ditambah test setThemeMode memastikan pilihan persist dan isDarkTheme mengikuti resolve LIGHT/DARK.
- Status: CLOSED — fix pada commit c011e2f, CI hijau dikonfirmasi pengguna (CH-113 juga hijau pada bced5d5).
