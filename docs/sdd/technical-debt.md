# NAZE MAPS — Technical Debt Register (SDD-070)

- ID: SDD-070
- Title: Technical Debt
- Status: APPROVED (register; perbaikan menunggu migration plan)

## Architecture

- TD-ARCH-1: MapViewModel god-object (semua fitur share satu AndroidViewModel + satu MapUiState 12 field). Why: perubahan satu fitur berpotensi regresi fitur lain. Area: ui/screens. Risk: HIGH. Solution: split per-feature state (ADR-003). Difficulty: HIGH. Priority: HIGH (dilakukan bertahap, setelah boundary aman).
- TD-ARCH-2: Tidak ada DI; repository dibangun hard-coded di ViewModel constructor body. Why: tidak bisa unit test ViewModel dengan fake. Area: MapViewModel. Risk: MEDIUM. Solution: constructor injection manual (TASK-009). Difficulty: LOW. Priority: HIGH.
- TD-ARCH-3: DistanceScreen membuat SearchRepository sendiri → UI bergantung ke data layer. Risk: MEDIUM. Solution: TASK-007. Difficulty: LOW. Priority: HIGH.
- TD-ARCH-4: Model lintas layer: NominatimResult (data layer) dipakai langsung sebagai UI state (selectedPlace, searchResults). Risk: LOW-MEDIUM (masih wajar untuk ukuran app). Solution: tahan dulu; catat untuk ADR-003.
- TD-ARCH-5: history & favorites dua database Room terpisah dengan pola identik. Solution: konsolidasi satu NazeDatabase saat menyentuh area ini. Difficulty: LOW (versi 1, belum ada migrasi user). Priority: MEDIUM.

## Code quality

- TD-CQ-1: Read-modify-write race pada MutableStateFlow (BUG-005). Priority: HIGH.
- TD-CQ-2: Dead code (BUG-009, BUG-010, BUG-011). Priority: LOW.
- TD-CQ-3: String UI campur EN/ID dan sebagian hardcoded (bukan resource strings) — MapScreen/DistanceScreen. Priority: LOW.
- TD-CQ-4: MapScreen.kt 16 KB — banyak tanggung jawab (search UI, history UI, bottom sheet, FAB, overlay wiring). Solution: pecah composable saat refactor state (TASK-010). Priority: MEDIUM.

## Dependencies

- TD-DEP-1: navigation-compose 2.7.7 unused (BUG-011).
- TD-DEP-2: Version hard-coded tanpa version catalog / BOM terpusat. Difficulty: LOW. Priority: LOW.
- TD-DEP-3: Tidak ada dependency untuk test network (MockWebServer), coroutine-test, Robolectric — blok testing strategy. Priority: HIGH (bersama TASK-003).
- TD-DEP-4: Semua service publik keyless (Nominatim/OSRM demo/OpenFreeMap/Esri) tanpa fallback atau self-host — risiko ketersediaan. Priority: MEDIUM (document, no action now).

## Testing

- TD-TEST-1: Coverage 0%. Priority: CRITICAL. Solution: SDD-040 + TASK-003.

## Performance

- TD-PERF-1: Kamera recenter tiap update lokasi (3s) tanpa gesture-awareness → user tidak bisa pan bebas saat tracking. Priority: LOW-MEDIUM (product decision).
- TD-PERF-2: getMapAsync dipanggil di update block tiap recomposition (MapLibreMapView update lambda). Impact UNKNOWN. Priority: LOW.

## Security

- TD-SEC-1: OkHttp logging-interceptor ada di implementation (bukan debugImplementation) — log bisa bocor di release bila level aktif; saat ini default level NONE, risiko rendah. Solution: pindah ke debugImplementation saat menyentuh build file. Priority: LOW.
- TD-SEC-2: allowBackup=true tanpa rule; data favorit bisa terekstrak via adb backup. Priority: LOW.
- TD-SEC-3: Release minify enabled tanpa verifikasi keep-rules untuk MapLibre/Retrofit/Gson (P-2). Priority: MEDIUM.

## Maintainability

- TD-MAINT-1: README menyatakan "belum pernah dicompile" (sudah outdated setelah commit fix) — dokumentasi tidak sinkron dengan repo. Priority: LOW.
- TD-MAINT-2: Tidak ada CONTRIBUTING/CHANGELOG. Priority: LOW.

## Build system

- TD-BUILD-1: gradlew/wrapper jar hilang (BUG-013). Priority: MEDIUM-HIGH (DX).
- TD-BUILD-2: CI tidak menjalankan test/lint (karena memang belum ada test). Priority: setelah TASK-003.
- TD-BUILD-3: kotlinOptions deprecated warning level di AGP 8.5. Priority: LOW.
