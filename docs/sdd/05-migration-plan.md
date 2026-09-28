# NAZE MAPS — Migration Plan (SDD-050)

- ID: SDD-050
- Title: Refactoring Migration Plan
- Status: PROPOSED (status per-task di bawah)

Pola per task: Audit → Baseline → Characterization test → Introduce boundary → Move one responsibility → Test → Build → Regression check → Commit.

- TASK-001 Establish baseline — DONE (commits d5f3b0e, 2d93e1c).
- TASK-002 Verify build & git hygiene — DONE & VERIFIED (wrapper 7da8010 oleh bot; assembleDebug hijau).
- TASK-003 Add characterization tests — DONE & VERIFIED (0fa3fa2; CI hijau).
- TASK-004 Fix state-update race — DONE & VERIFIED (c583eb7; CI hijau; BUG-005 CLOSED).
- TASK-005 Fix MapView lifecycle leak — DONE (ce78f02). CI pending; regression manual WAJIB sebelum BUG-001 CLOSED final.
- TASK-006 Wire ConnectivityObserver — DONE (63b3be6). CI + regression airplane-mode pending.
- TASK-007 Remove DistanceScreen's own SearchRepository — DONE (416a192). CI pending.
- TASK-008 Fix UX feedback — DONE:
  - TASK-008a (5425679): banner saat request rute tanpa GPS (BUG-002).
  - TASK-008b (f1f431a): fly-to selectedPlace zoom 15 (BUG-004; marker pin = future change kecil).
- TASK-009 Constructor injection MapViewModel — DONE (commit 5a5d3c4): semua collaborator kini parameter constructor dengan default dari Application; @JvmOverloads mempertahankan compatibility dengan AndroidViewModelFactory; production wiring & behavior tidak berubah. CI pending. Follow-up natural: VM unit test dengan fake (masuk scope TASK-010).
- TASK-010 Split MapUiState per feature screen state (ADR-003) + VM unit tests dengan fakes — DONE & VERIFIED (CI hijau):
  - TASK-010a (0194112 + fix e6bbfda): MapUiState dipecah per fitur (LocationState, SearchState, RouteState, MapViewState, SettingsState); semua update site & pemakaian kini lewat slice-nya, termasuk MainActivity (uiState.settings.isDarkTheme — terlewat di commit pertama, menyebabkan build merah, diperbaiki di e6bbfda).
  - TASK-010b: DONE (2c39061 + 68eccdf + fix a77e8ed + fix e06dd47): repo dijadikan interface dengan impl *Impl (2c39061); 12 VM unit test dengan fakes via Robolectric (68eccdf). Fix lanjutan: import setMain/resetMain (f1bb961), characterization tests konstruksi *Impl (a77e8ed), dan @Config(application = Application::class) agar NazeMapsApp.onCreate (MapLibre native) tidak dijalankan di JVM Robolectric (e06dd47 — sebelumnya 11 test UnsatisfiedLinkError). CI hijau. Path yang butuh GPS permission/service (tracking + rute dengan lokasi) tetap di regression manual device.
- TASK-011 Nominatim request hardening — DONE (dd159ce): accept-language dikirim dari Locale perangkat (default "id" di NominatimApi) dan debounce pencarian dinaikkan 350ms → 500ms sesuai policy Nominatim. CI pending.
- TASK-012 Remove unused navigation-compose (BUG-011) + pindah logging-interceptor ke debugImplementation (TD-SEC-1) — DONE (664c593): dependency-only change; diverifikasi NazeNavHost adalah state-based tab switcher (tidak ada import androidx.navigation) dan NetworkModule tidak memakai HttpLoggingInterceptor. CI pending.
- TASK-013 Mitigasi CI flaky release (BUG-015): retry/cache lint deps.

## Aturan eksekusi

- Satu task = satu (atau sedikit) commit terisolasi.
- Setiap task selesai: build CI hijau + test hijau + regression checklist baseline (12 fitur).
- Bug fix dan refactor TIDAK boleh satu commit.
- Task di luar daftar = change request baru + spec.
