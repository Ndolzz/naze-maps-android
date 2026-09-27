# NAZE MAPS — Technical Debt Register (SDD-070)

- ID: SDD-070
- Title: Technical Debt
- Status: APPROVED (register; perbaikan menunggu migration plan)

## Architecture

- TD-ARCH-1: MapViewModel god-object (semua fitur share satu AndroidViewModel + satu MapUiState 12 field). Why: perubahan satu fitur berpotensi regresi fitur lain. Area: ui/screens. Risk: HIGH. Solution: split per-feature state (ADR-003). Difficulty: HIGH. Priority: HIGH (bertahap, setelah boundary aman).
- TD-ARCH-2: Tidak ada DI; repository dibangun hard-coded di ViewModel constructor body. Why: tidak bisa unit test ViewModel dengan fake. Area: MapViewModel. Risk: MEDIUM. Solution: constructor injection manual (TASK-009). Difficulty: LOW. Priority: HIGH.
- TD-ARCH-3: DistanceScreen membuat SearchRepository sendiri → UI bergantung ke data layer. Risk: MEDIUM. Solution: TASK-007. Difficulty: LOW. Priority: HIGH.
- TD-ARCH-4: Model lintas layer: NominatimResult (data layer) dipakai langsung sebagai UI state. Risk: LOW-MEDIUM. Solution: tahan dulu; catat untuk ADR-003.
- TD-ARCH-5: history & favorites dua database Room terpisah dengan pola identik. Solution: konsolidasi satu NazeDatabase saat menyentuh area ini. Difficulty: LOW (versi 1, belum ada migrasi user). Priority: MEDIUM.

## Code quality

- TD-CQ-1: Read-modify-write race pada MutableStateFlow (BUG-005). Status: FIXED (TASK-004, menunggu verifikasi CI). Priority: HIGH.
- TD-CQ-2: Dead code (BUG-009, BUG-010, BUG-011). Priority: LOW.
- TD-CQ-3: String UI campur EN/ID dan sebagian hardcoded (bukan resource strings) — MapScreen/DistanceScreen. Priority: LOW.
- TD-CQ-4: MapScreen.kt 16 KB — banyak tanggung jawab. Solution: pecah composable saat refactor state (TASK-010). Priority: MEDIUM.
- TD-CQ-5 (BARU, dari log CI c7ada9d): deprecation warnings: SearchBar `outlinedTextFieldColors` → `OutlinedTextFieldDefaults.colors`; MapScreen `Icons.Filled.DirectionsWalk/Bike` → AutoMirrored. Why: build warning noise; AutoMirrored penting untuk RTL. Risk: LOW (manifest supportsRtl=false, tapi tetap konvensi). Solution: ganti saat menyentuh file terkait (bukan commit khusus). Priority: LOW.

## Dependencies

- TD-DEP-1: navigation-compose 2.7.7 unused (BUG-011).
- TD-DEP-2: Version hard-coded tanpa version catalog / BOM terpusat. Difficulty: LOW. Priority: LOW.
- TD-DEP-3: Test deps untuk network/coroutine — TERPENUHI sebagian (TASK-003: coroutines-test + mockwebserver). Robolectric/Room-testing masih belum. Priority: MEDIUM (ikut TASK-005+).
- TD-DEP-4: Semua service publik keyless tanpa fallback. Priority: MEDIUM (document, no action now).

## Testing

- TD-TEST-1: Coverage 0% → sebagian teratasi TASK-003 (20 characterization tests; verifikasi CI pending). Prioritas tetap: tambah Room in-memory + VM tests (TASK-009 prerequisite). Priority: HIGH.

## Performance

- TD-PERF-1: Kamera recenter tiap update lokasi (3s) tanpa gesture-awareness. Priority: LOW-MEDIUM (product decision).
- TD-PERF-2: getMapAsync dipanggil di update block tiap recomposition (MapLibreMapView update lambda). Impact UNKNOWN. Priority: LOW.

## Security

- TD-SEC-1: OkHttp logging-interceptor di implementation (bukan debugImplementation). Solution: pindah saat menyentuh build file (bisa digabung aman dengan TASK-012). Priority: LOW.
- TD-SEC-2: allowBackup=true tanpa rule. Priority: LOW.
- TD-SEC-3: Release minify enabled tanpa verifikasi keep-rules (P-2); diperparah BUG-015 (release build CI flaky → jarang terverifikasi). Priority: MEDIUM.

## Maintainability

- TD-MAINT-1: README menyatakan "belum pernah dicompile" — outdated; assembleDebug kini CONFIRMED hijau. Solution: update README (satu commit docs). Priority: LOW-MEDIUM.
- TD-MAINT-2: Tidak ada CONTRIBUTING/CHANGELOG. Priority: LOW.

## Build system

- TD-BUILD-1: gradlew/wrapper jar hilang (BUG-013) — mitigasi TASK-002 terpasang; konfirmasi pending. Priority: MEDIUM-HIGH.
- TD-BUILD-2: CI tidak menjalankan test — TERATASI TASK-003 (step `gradle test` ditambahkan; run pertama dengan step ini pending). Priority: —
- TD-BUILD-3: kotlinOptions deprecated warning level di AGP 8.5. Priority: LOW.
- TD-BUILD-4 (BARU): assembleRelease CI flaky karena timeout download dependency lint (BUG-015) + step lint ~5 menit. Solution: TASK-013 (retry/cache; opsi: pre-cache lint deps atau `--refresh-dependencies` tidak — justru retry). Priority: MEDIUM.
