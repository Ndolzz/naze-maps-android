# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

Urutan berdasarkan risiko audit (dokumentasi SDD-000..030, bugs, technical-debt):

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c: dokumentasi SDD lengkap).
- TASK-002 Verify build & git hygiene — IN PROGRESS. Commit c7ada9d menambah workflow `generate-gradle-wrapper.yml` (self-limiting). VERIFIKASI PARSIAL dari log CI run c7ada9d: **assembleDebug BUILD SUCCESSFUL in 3m20s** (CONFIRMED compile hijau). assembleRelease FAILED — bukan kode: timeout download dependency lint (BUG-015, flaky). Status commit wrapper oleh workflow generate-gradle-wrapper: UNKNOWN (log run tersebut belum diperiksa).
- TASK-003 Add characterization tests — DONE (commit 0fa3fa2): DistanceUtilsTest, SearchRepositoryTest (MockWebServer), RoutingRepositoryTest; deps test (coroutines-test, mockwebserver); CI ditambah step `gradle test`. VERIFIKASI: CI run untuk commit ini belum terinspeksi — status UNKNOWN.
- TASK-004 Fix state-update race — DONE (commit c583eb7): semua penulisan `_uiState` di MapViewModel diganti ke `MutableStateFlow.update {}` atomik. Behavior tidak berubah. VERIFIKASI: mengandalkan CI run commit ini (step `gradle test` + assembleDebug); status UNKNOWN hingga log diperiksa.
- TASK-005 Fix MapView lifecycle leak (BUG-001) — pastikan MapView.onDestroy dipanggil saat composable meninggalkan composition (atau state preservation untuk tab). Test manual + leak check.
- TASK-006 Wire ConnectivityObserver (BUG-010) — banner proaktif no-internet di MapViewModel. Perlu spec update feature map.md.
- TASK-007 Remove DistanceScreen's own SearchRepository — injeksi via ViewModel (boundary rule). Behavior sama.
- TASK-008 Fix UX feedback: requestRoute tanpa lokasi (BUG-002) + selectedPlace tidak menggerakkan kamera (BUG-004) — masing-masing satu commit, spec di features/routing.md & features/map.md dulu.
- TASK-009 Introduce constructor injection for MapViewModel repos (manual DI) — testability. No behavior change. Prasyarat VM unit test.
- TASK-010 Split MapUiState per feature screen state (ADR-003) — paling besar, dilakukan terakhir setelah semua boundary aman.
- TASK-011 Nominatim request hardening (accept-language, debounce >= 500ms) — setelah verifikasi policy; spec update features/search.md. (limit=8 & format=json sudah ada di NominatimApi.)
- TASK-012 Remove unused navigation-compose dependency (BUG-011) — dependency-only change, verifikasi build.
- TASK-013 (BARU) Mitigasi CI flaky release: evaluasi retry/caching untuk lintVitalAnalyzeRelease (BUG-015) — build-system-only, bukan kode.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
