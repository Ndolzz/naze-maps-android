# Feature: Map

- Spec ID: FEAT-MAP
- Status: DRAFT (perubahan tercatat di "Changes")

## Purpose
Menampilkan peta vector interaktif sebagai layar utama aplikasi.

## Current Behavior
MapScreen meng-host MapLibre MapView via AndroidView; style URL dipilih dari tema (MapStyle.forTheme). Loading overlay branded tampil sampai style loaded, lalu fade-out. Overlay: location dot, route line, satellite raster (hide Background/Fill/FillExtrusion saat on). Compass Fab reset bearing ke utara. Layers Fab toggle satellite. Pan/zoom/rotate native MapLibre. Kamera terbang (animateCamera) ke tempat yang dipilih dari search/riwayat pada zoom 15 (TASK-008b).

## Requirements
R1 peta render penuh layar; R2 style dark/light mengikuti setting; R3 loading state tanpa flash abu; R4 lifecycle MapView benar (START/RESUME/PAUSE/STOP/DESTROY + destroy saat leaving composition); R5 overlay survive style reload; R6 banner no-internet proaktif; R7 (baru, TASK-008b) memilih tempat menggerakkan kamera ke tempat tersebut.

## Inputs
isDarkTheme, myLocation, activeRoute, isSatelliteOn, headingDegrees, connectivity state, selectedPlace.

## Outputs
MapView render + camera position.

## States
loading | ready | satellite on/off | tracking (recenter per update) | fly-to-selected (baru).

## Error States
style URL gagal load → INFERRED default MapLibre. No internet → banner proaktif (TASK-006).

## Dependencies
MapLibre SDK 11.5.2, OpenFreeMap tiles, map/MapOverlay, ConnectivityObserver, MapViewModel.

## User Flow
Buka app → loading overlay → peta muncul → interaksi bebas → search → pilih → kamera fly-to + sheet.

## Data Flow
uiState.isDarkTheme → MapStyle.forTheme → styleUrl → setStyle → onStyleLoaded → re-apply overlays. selectedPlace → animateCamera(newLatLngZoom, 15.0). ConnectivityObserver → VM → banner.

## Acceptance Criteria
AC1 peta tampil tanpa crash di minSdk 24; AC2 toggle tema tidak crash dan overlay kembali muncul; AC3 satellite toggle menampilkan imagery; AC4 loading overlay hilang setelah style ready; AC5 toggle airplane mode → banner muncul/hilang; AC6 (baru) pilih tempat jauh → kamera bergerak halus ke lokasi tsb.

## Current Implementation
map/MapLibreMapView.kt (host+lifecycle; onDestroy di onDispose sejak TASK-005), map/MapStyle.kt, map/MapOverlay.kt, ui/screens/MapScreen.kt (flyTo sejak TASK-008b), network/ConnectivityObserver.kt.

## Known Problems
BUG-003 (camera reset saat recenter — perlu verifikasi runtime), marker pin untuk selectedPlace belum ada (fly-to saja; marker = future change kecil), TD-PERF-2.

## Future Changes
Marker pin selectedPlace; gesture-aware recenter (keputusan produk).

## Changes
- 2026-09-27 [TASK-005, BUG-001]: MapView.onDestroy kini dipanggil saat composable leaving composition dengan guard double-destroy.
- 2026-09-27 [TASK-006, BUG-010]: banner no-internet proaktif via ConnectivityObserver.
- 2026-09-27 [TASK-008b, BUG-004]: animateCamera fly-to selectedPlace (search & history) zoom 15.
