# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

Urutan berdasarkan risiko audit (dokumentasi SDD-000..030, bugs, technical-debt):

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c).
- TASK-002 Verify build & git hygiene — DONE & VERIFIED. Workflow `generate-gradle-wrapper.yml` (c7ada9d) auto-commit wrapper: commit 7da8010 "build: add Gradle wrapper 8.7 [TASK-002]" oleh github-actions[bot] — CONFIRMED. assembleDebug hijau (log CI c7ada9d). Workflow generate sudah no-op dan boleh dihapus kapan saja (opsional).
- TASK-003 Add characterization tests — DONE & VERIFIED (commit 0fa3fa2; CI run hijau per konfirmasi user 2026-09-27: step `gradle test` lulus bersama assembleDebug).
- TASK-004 Fix state-update race — DONE & VERIFIED (commit c583eb7; CI hijau per konfirmasi user; BUG-005 closed).
- TASK-005 Fix MapView lifecycle leak — DONE (commit ce78f02): onDestroy() kini dipanggil di onDispose (composable meninggalkan composition / pindah tab) dengan guard `destroyed` mencegah double-destroy saat Activity ON_DESTROY. VERIFIKASI: CI run commit ini (compile+test); runtime leak-check (LeakCanary, tab switching berulang) tetap perlu di device — REGRESSION MANUAL WAJIB sebelum BUG-001 ditutup final.
- TASK-006 Wire ConnectivityObserver (BUG-010) — banner proaktif no-internet di MapViewModel. Perlu spec update feature map.md.
- TASK-007 Remove DistanceScreen's own SearchRepository — injeksi via ViewModel (boundary rule). Behavior sama.
- TASK-008 Fix UX feedback: requestRoute tanpa lokasi (BUG-002) + selectedPlace tidak menggerakkan kamera (BUG-004) — masing-masing satu commit, spec di features/routing.md & features/map.md dulu.
- TASK-009 Introduce constructor injection for MapViewModel repos (manual DI) — testability. No behavior change. Prasyarat VM unit test.
- TASK-010 Split MapUiState per feature screen state (ADR-003) — paling besar, dilakukan terakhir setelah semua boundary aman.
- TASK-011 Nominatim request hardening (accept-language, debounce >= 500ms) — setelah verifikasi policy; spec update features/search.md. (limit=8 & format=json sudah ada.)
- TASK-012 Remove unused navigation-compose dependency (BUG-011) — dependency-only change; gabung aman dengan TD-SEC-1 (pindah logging-interceptor ke debugImplementation).
- TASK-013 Mitigasi CI flaky release (BUG-015): retry/caching untuk lintVitalAnalyzeRelease. Build-system-only.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
