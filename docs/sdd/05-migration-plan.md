# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c).
- TASK-002 Verify build & git hygiene — DONE & VERIFIED (wrapper commit 7da8010 oleh bot; assembleDebug hijau).
- TASK-003 Add characterization tests — DONE & VERIFIED (commit 0fa3fa2; CI hijau).
- TASK-004 Fix state-update race — DONE & VERIFIED (commit c583eb7; CI hijau; BUG-005 closed).
- TASK-005 Fix MapView lifecycle leak — DONE (commit ce78f02). Verifikasi CI pending; regression manual (LeakCanary, tab switching) WAJIB sebelum BUG-001 ditutup final.
- TASK-006 Wire ConnectivityObserver — DONE (commit 63b3be6): banner NoInternet proaktif di init MapViewModel; online → bersihkan banner offline; tidak menimpa banner permission/GPS. Spec features/map.md diperbarui. Verifikasi CI + regression manual (airplane mode) pending.
- TASK-007 Remove DistanceScreen's own SearchRepository — DONE (commit 416a192): MapViewModel.geocodeOne jadi satu-satunya jalur geocoding screen. Spec features/distance.md diperbarui. Behavior tidak berubah. Verifikasi CI pending.
- TASK-008 Fix UX feedback: requestRoute tanpa lokasi (BUG-002) + selectedPlace tidak menggerakkan kamera (BUG-004) — masing-masing satu commit, spec di features/routing.md & features/map.md dulu.
- TASK-009 Introduce constructor injection for MapViewModel repos (manual DI) — testability. No behavior change. Prasyarat VM unit test.
- TASK-010 Split MapUiState per feature screen state (ADR-003) — paling besar, terakhir.
- TASK-011 Nominatim request hardening (accept-language, debounce >= 500ms) — setelah verifikasi policy.
- TASK-012 Remove unused navigation-compose (BUG-011) + pindah logging-interceptor ke debugImplementation (TD-SEC-1) — dependency-only change.
- TASK-013 Mitigasi CI flaky release (BUG-015): retry/cache lint deps.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
