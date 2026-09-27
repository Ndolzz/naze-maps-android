# Feature: Map

- Spec ID: FEAT-MAP
- Status: DRAFT (membekukan perilaku baseline; perubahan tercatat di "Changes")

## Purpose
Menampilkan peta vector interaktif sebagai layar utama aplikasi.

## Current Behavior
MapScreen meng-host MapLibre MapView via AndroidView; style URL dipilih dari tema (MapStyle.forTheme). Loading overlay branded tampil sampai style loaded, lalu fade-out. Overlay: location dot, route line, satellite raster (hide Background/Fill/FillExtrusion saat on). Compass Fab reset bearing ke utara. Layers Fab toggle satellite. Pan/zoom/rotate native MapLibre.

## Requirements
R1 peta render penuh layar; R2 style dark/light mengikuti setting; R3 loading state tanpa flash abu; R4 lifecycle MapView benar (START/RESUME/PAUSE/STOP/DESTROY + destroy saat leaving composition); R5 overlay survive style reload; R6 (baru, TASK-006) banner no-internet muncul proaktif saat koneksi hilang dan bersih saat koneksi kembali.

## Inputs
isDarkTheme (settings), myLocation, activeRoute, isSatelliteOn, headingDegrees, connectivity state (baru).

## Outputs
MapView render + camera position.

## States
loading (style==null) | ready | satellite on/off | tracking (recenter per update).

## Error States
style URL gagal load → INFERRED MapLibre default behavior. UNKNOWN fallback strategy.
No internet → banner proaktif (TASK-006): muncul saat koneksi hilang (tanpa menimpa banner permission/GPS yang lebih spesifik); hilang otomatis saat online kembali; tombol OK manual tetap ada.

## Dependencies
MapLibre SDK 11.5.2, OpenFreeMap tiles, map/MapOverlay, ConnectivityObserver (sejak TASK-006), MapViewModel.

## User Flow
Buka app → loading overlay → peta muncul → interaksi bebas → (opsional) satellite via Layers Fab → reset north via Compass Fab.

## Data Flow
uiState.isDarkTheme → MapStyle.forTheme → styleUrl → setStyle → onStyleLoaded → LaunchedEffect(mapStyle) re-apply overlays.
ConnectivityObserver.observe() → VM → banner (TASK-006).

## Acceptance Criteria
AC1 peta tampil tanpa crash di minSdk 24; AC2 toggle tema tidak crash dan overlay kembali muncul; AC3 satellite toggle menampilkan imagery; AC4 loading overlay hilang setelah style ready; AC5 (baru) toggle airplane mode saat app terbuka → banner muncul < beberapa detik, matikan → banner hilang.

## Current Implementation
map/MapLibreMapView.kt (host+lifecycle; sejak TASK-005 onDestroy juga di onDispose), map/MapStyle.kt, map/MapOverlay.kt, ui/screens/MapScreen.kt, network/ConnectivityObserver.kt (di-wire di MapViewModel sejak TASK-006).

## Known Problems
BUG-003 (camera reset — perlu verifikasi runtime), BUG-004 (no fly-to — TASK-008), TD-PERF-2.

## Future Changes
Fly-to + marker untuk selectedPlace (TASK-008); gesture-aware recenter (keputusan produk).

## Changes
- 2026-09-27 [TASK-005, BUG-001]: MapView.onDestroy kini dipanggil saat composable leaving composition (tab switch) dengan guard double-destroy.
- 2026-09-27 [TASK-006, BUG-010]: banner no-internet proaktif via ConnectivityObserver di MapViewModel init.
