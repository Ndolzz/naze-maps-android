# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

Urutan berdasarkan risiko audit (dokumentasi SDD-000..030, bugs, technical-debt):

- TASK-001 Establish baseline — COMMIT INI (dokumentasi SDD lengkap). Status: DONE.
- TASK-002 Verify build & git hygiene — tambah gradlew + gradle-wrapper.jar agar build lokal jalan; verifikasi CI hijau. (Build system, bukan behavior.)
- TASK-003 Add characterization tests — DistanceUtils, SearchRepository, RoutingRepository, history/favorites DAO (MockWebServer/Room in-memory). Test-only change.
- TASK-004 Fix state-update race — ganti read-modify-write `_uiState.value = _uiState.value.copy(...)` dengan `update {}` atomik di MapViewModel. Setelah TASK-003.
- TASK-005 Fix MapView lifecycle leak (BUG-001) — pastikan MapView.onDestroy dipanggil saat composable meninggalkan composition (atau MoveableContentOf/state preservation untuk tab). Test manual + leak check.
- TASK-006 Wire ConnectivityObserver (BUG-010) — banner proaktif no-internet di MapViewModel. Perlu spec update feature map.md.
- TASK-007 Remove DistanceScreen's own SearchRepository — injeksi via ViewModel (boundary rule). Behavior sama.
- TASK-008 Fix UX feedback: requestRoute tanpa lokasi (BUG-002) + selectedPlace tidak menggerakkan kamera (BUG-004) — masing-masing satu commit, spec di features/routing.md & features/map.md dulu.
- TASK-009 Introduce constructor injection for MapViewModel repos (manual DI) — testability. No behavior change.
- TASK-010 Split MapUiState per feature screen state (ADR-003) — paling besar, dilakukan terakhir setelah semua boundary aman.
- TASK-011 Nominatim request hardening (limit param, accept-language, min debounce) — setelah verifikasi policy; spec update features/search.md.
- TASK-012 Remove unused navigation-compose dependency (BUG-011) — dependency-only change, verifikasi build.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
