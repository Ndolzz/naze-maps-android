# Feature: Map

- Spec ID: FEAT-MAP
- Status: DRAFT (perubahan tercatat di "Changes")

## Purpose
Menampilkan peta vector interaktif sebagai layar utama aplikasi.

## Current Behavior
MapScreen meng-host MapLibre MapView via AndroidView; style dimuat dari JSON pixel-art yang dibundel (assets) — sebelumnya URL OpenFreeMap liberty/dark. Loading overlay branded tampil sampai style loaded, lalu fade-out. Overlay: location dot, route line, satellite raster (hide Background/Fill/FillExtrusion saat on), landmark symbols (tier + cluster), graticule. Compass Fab reset bearing ke utara. Layers Fab toggle satellite. Graticule Fab toggle garis lintang/bujur. Pan/zoom/rotate native MapLibre. Kamera terbang (animateCamera) ke tempat yang dipilih dari search/riwayat pada zoom 15 (TASK-008b) dan ke landmark terpilih pada zoom 11 (FEAT-WMAP R2).

## Requirements
R1 peta render penuh layar; R2 style dark/light pixel-art mengikuti setting (diubah oleh FEAT-WMAP R1 — semantik tetap); R3 loading state tanpa flash abu; R4 lifecycle MapView benar (START/RESUME/PAUSE/STOP/DESTROY + destroy saat leaving composition); R5 overlay survive style reload; R6 banner no-internet proaktif; R7 memilih tempat menggerakkan kamera ke tempat tersebut; R8 [WMAP] klik landmark membuka panel informasi (FEAT-WMAP R2).

## Inputs
isDarkTheme, myLocation, activeRoute, isSatelliteOn, isGraticuleOn, headingDegrees, connectivity state, selectedPlace, selectedLandmark.

## Outputs
MapView render + camera position.

## States
loading | ready | satellite on/off | graticule on/off | tracking (recenter per update) | fly-to-selected | fly-to-landmark.

## Error States
style JSON gagal load → INFERRED default MapLibre. No internet → banner proaktif (TASK-006).

## Dependencies
MapLibre SDK 11.5.2, OpenFreeMap tiles + glyphs, map/MapOverlay, map/WorldMapOverlay (WMAP), ConnectivityObserver, MapViewModel.

## User Flow
Buka app → loading overlay → peta pixel-art muncul → interaksi bebas → search → pilih → kamera fly-to + sheet; atau tap landmark → panel landmark → rute/favorit/share.

## Data Flow
uiState.isDarkTheme → MapStyle.pixelAssetForTheme → styleJson → setStyle(Builder.fromJson) → onStyleLoaded → re-apply overlays (route, location, satellite, landmark, graticule). selectedPlace → animateCamera(newLatLngZoom, 15.0). selectedLandmark → animateCamera(zoom 11). ConnectivityObserver → VM → banner.

## Acceptance Criteria
AC1 peta tampil tanpa crash di minSdk 24; AC2 toggle tema tidak crash dan overlay kembali muncul; AC3 satellite toggle menampilkan imagery; AC4 loading overlay hilang setelah style ready; AC5 toggle airplane mode → banner muncul/hilang; AC6 pilih tempat jauh → kamera bergerak halus ke lokasi tsb; AC7 [WMAP] graticule toggle tidak me-reload style (visibility layer).

## Current Implementation
map/MapLibreMapView.kt (host+lifecycle+loader JSON; onDestroy di onDispose sejak TASK-005), map/MapStyle.kt (asset style pixel), map/MapOverlay.kt, map/WorldMapOverlay.kt (WMAP), map/Graticule.kt (WMAP), ui/screens/MapScreen.kt, network/ConnectivityObserver.kt.

## Known Problems
BUG-003 (camera reset saat recenter — perlu verifikasi runtime), marker pin untuk selectedPlace belum ada (future change kecil), TD-PERF-2, label teks bergantung glyphs OpenFreeMap (risiko pihak ketiga, ADR-006).

## Future Changes
Marker pin selectedPlace; gesture-aware recenter (keputusan produk); kontur elevasi jika kelak tersedia sumber data gratis.

## Changes
- 2026-09-27 [TASK-005, BUG-001]: MapView.onDestroy kini dipanggil saat composable leaving composition dengan guard double-destroy.
- 2026-09-27 [TASK-006, BUG-010]: banner no-internet proaktif via ConnectivityObserver.
- 2026-09-27 [TASK-008b, BUG-004]: animateCamera fly-to selectedPlace (search & history) zoom 15.
- 2026-10-16 [FEAT-WMAP R1/R2/R5]: style pixel-art kustom dari assets (R2 nilai semantik tetap), overlay landmark + graticule, klik landmark → panel. Lihat FEAT-WMAP & ADR-006.
