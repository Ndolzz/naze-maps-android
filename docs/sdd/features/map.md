# Feature: Map

- Spec ID: FEAT-MAP
- Status: DRAFT (membekukan perilaku baseline)

## Purpose
Menampilkan peta vector interaktif sebagai layar utama aplikasi.

## Current Behavior
MapScreen meng-host MapLibre MapView via AndroidView; style URL dipilih dari tema (MapStyle.forTheme). Loading overlay branded tampil sampai style loaded, lalu fade-out. Overlay: location dot, route line, satellite raster (hide Background/Fill/FillExtrusion saat on). Compass Fab reset bearing ke utara. Layers Fab toggle satellite. Pan/zoom/rotate native MapLibre.

## Requirements
R1 peta render penuh layar; R2 style dark/light mengikuti setting; R3 loading state tanpa flash abu; R4 lifecycle MapView benar (START/RESUME/PAUSE/STOP/DESTROY); R5 overlay survive style reload.

## Inputs
isDarkTheme (settings), myLocation, activeRoute, isSatelliteOn, headingDegrees.

## Outputs
MapView render + camera position.

## States
loading (style==null) | ready | satellite on/off | tracking (recenter per update).

## Error States
style URL gagal load → INFERRED MapLibre default behavior (blank/last state). UNKNOWN fallback strategy. Tidak ada banner khusus peta.

## Dependencies
MapLibre SDK 11.5.2, OpenFreeMap tiles, map/MapOverlay, MapViewModel.

## User Flow
Buka app → loading overlay → peta muncul → interaksi bebas → (opsional) satellite via Layers Fab → reset north via Compass Fab.

## Data Flow
uiState.isDarkTheme → MapStyle.forTheme → styleUrl → setStyle → onStyleLoaded → LaunchedEffect(mapStyle) re-apply overlays.

## Acceptance Criteria
AC1 peta tampil tanpa crash di minSdk 24; AC2 toggle tema tidak crash dan overlay kembali muncul; AC3 satellite toggle menampilkan imagery; AC4 loading overlay hilang setelah style ready.

## Current Implementation
map/MapLibreMapView.kt (host+lifecycle), map/MapStyle.kt, map/MapOverlay.kt (updateRouteLine, updateLocationDot, setSatelliteVisible), ui/screens/MapScreen.kt.

## Known Problems
BUG-001 (lifecycle destroy), BUG-003 (camera reset), BUG-004 (no fly-to/marker), TD-PERF-2.

## Future Changes
Fly-to + marker untuk selectedPlace (TASK-008); gesture-aware recenter (TD-PERF-1, keputusan produk).

