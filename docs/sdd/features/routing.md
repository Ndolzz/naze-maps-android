# Feature: Routing

- Spec ID: FEAT-ROUTE
- Status: DRAFT

## Purpose
Rute dari lokasi pengguna ke tempat terpilih, digambar sebagai garis di peta.

## Current Behavior
Bottom sheet selectedPlace → tombol Mobil/Jalan/Sepeda → requestRoute(to, profile) → RoutingRepository (OSRM demo, overview=full, geojson, alternatives) → RouteOutcome → Success: activeRoute=routes.first() → MapScreen menggambar LineLayer biru #2F6FED width 5; NoRouteFound → banner "Rute tidak ditemukan"; NoInternet → banner; Error → banner generic. Bila myLocation null: diam (BUG-002). clearSelection menghapus rute.

## Requirements
R1 tiga profil (driving/foot/bike) via parameter osrmName; R2 garis rute di bawah location dot; R3 rute hilang saat sheet ditutup; R4 error termap ke banner.

## Inputs
GeoPoint destination, RoutingProfile, myLocation (origin).

## Outputs
activeRoute (OsrmRoute: distance, duration, geometry), banner.

## States
idle | requesting (no loading indicator di sheet — gap) | routed | no-route | no-internet | error.

## Error States
Sesuai di atas. Gap: tidak ada spinner saat request rute.

## Dependencies
OsrmApi, NetworkModule, MapOverlay.updateRouteLine, MapViewModel.

## User Flow
Search → pilih tempat → sheet → pilih mode → garis rute muncul.

## Data Flow
Sheet click → VM → repo → OSRM → outcome → state → Style.updateRouteLine.

## Acceptance Criteria
AC1 rute jalan dari lokasi nyata; AC2 garis tergambar tanpa duplikat layer saat re-request; AC3 garis hilang saat clearSelection.

## Current Implementation
routing/OsrmApi.kt, routing/RouteResult.kt (OsrmResponse/Route/Geometry, RoutingProfile), routing/RoutingRepository.kt (RouteOutcome).

## Known Problems
BUG-002 (silent no-GPS), tidak ada tampilkan jarak/durasi rute di UI (data tersedia tapi tidak dirender — CONFIRMED), profil foot/bike di server demo kadang gagal (catatan kode), BUG-005.

## Future Changes
TASK-008 feedback; render distance/duration; loading state.

