# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c).
- TASK-002 Verify build & git hygiene — DONE & VERIFIED (wrapper 7da8010 oleh bot; assembleDebug hijau).
- TASK-003 Add characterization tests — DONE & VERIFIED (0fa3fa2; CI hijau).
- TASK-004 Fix state-update race — DONE & VERIFIED (c583eb7; CI hijau; BUG-005 CLOSED).
- TASK-005 Fix MapView lifecycle leak — DONE (ce78f02). CI pending; regression manual (LeakCanary, tab switching) WAJIB sebelum BUG-001 CLOSED final.
- TASK-006 Wire ConnectivityObserver — DONE (63b3be6). CI + regression airplane-mode pending.
- TASK-007 Remove DistanceScreen's own SearchRepository — DONE (416a192). CI pending.
- TASK-008 Fix UX feedback — DONE:
  - TASK-008a (commit 5425679): requestRoute tanpa GPS fix → banner "Lokasi saya belum tersedia — aktifkan My Location dulu" (BUG-002).
  - TASK-008b (commit f1f431a): animateCamera fly-to selectedPlace dari search/riwayat, zoom 15 (BUG-004; marker pin dicatat sebagai future change kecil, bukan scope task ini).
  - Spec routing.md & map.md diperbarui. Verifikasi CI + regression manual pending.
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
