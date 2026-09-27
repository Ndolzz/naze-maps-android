# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

Urutan berdasarkan risiko audit (dokumentasi SDD-000..030, bugs, technical-debt):

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c: dokumentasi SDD lengkap).
- TASK-002 Verify build & git hygiene — IN PROGRESS. Commit c7ada9d menambah workflow `generate-gradle-wrapper.yml` (self-limiting): CI akan generate & commit gradlew/gradlew.bat/gradle-wrapper.jar pada push berikutnya; setelah wrapper ada, workflow menjadi no-op dan boleh dihapus. Verifikasi: gradlew muncul di repo; build lokal (`./gradlew assembleDebug`) jalan.
- TASK-003 Add characterization tests — DONE (commit 0fa3fa2): DistanceUtilsTest, SearchRepositoryTest (MockWebServer), RoutingRepositoryTest; deps test (coroutines-test, mockwebserver); CI ditambah step `gradle test`. VERIFIKASI: status run CI "Android Build" terbaru setelah commit ini (hijau = tests lulus). Status CI saat commit ini: UNKNOWN (belum diverifikasi saat penulisan).
- TASK-004 Fix state-update race — ganti read-modify-write `_uiState.value = _uiState.value.copy(...)` dengan `update {}` atomik di MapViewModel. Setelah TASK-003 terverifikasi hijau.
- TASK-005 Fix MapView lifecycle leak (BUG-001) — pastikan MapView.onDestroy dipanggil saat composable meninggalkan composition (atau state preservation untuk tab). Test manual + leak check.
- TASK-006 Wire ConnectivityObserver (BUG-010) — banner proaktif no-internet di MapViewModel. Perlu spec update feature map.md.
- TASK-007 Remove DistanceScreen's own SearchRepository — injeksi via ViewModel (boundary rule). Behavior sama.
- TASK-008 Fix UX feedback: requestRoute tanpa lokasi (BUG-002) + selectedPlace tidak menggerakkan kamera (BUG-004) — masing-masing satu commit, spec di features/routing.md & features/map.md dulu.
- TASK-009 Introduce constructor injection for MapViewModel repos (manual DI) — testability. No behavior change. Prasyarat VM unit test.
- TASK-010 Split MapUiState per feature screen state (ADR-003) — paling besar, dilakukan terakhir setelah semua boundary aman.
- TASK-011 Nominatim request hardening (accept-language, debounce >= 500ms) — setelah verifikasi policy; spec update features/search.md. (limit=8 & format=json sudah ada di NominatimApi — lihat koreksi BUG-008.)
- TASK-012 Remove unused navigation-compose dependency (BUG-011) — dependency-only change, verifikasi build.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
