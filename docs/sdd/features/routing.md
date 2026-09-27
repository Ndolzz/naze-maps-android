# Feature: Routing

- Spec ID: FEAT-ROUTE
- Status: DRAFT (perubahan tercatat di "Changes")

## Purpose
Rute dari lokasi pengguna ke tempat terpilih, digambar sebagai garis di peta.

## Current Behavior
Bottom sheet selectedPlace → tombol Mobil/Jalan/Sepeda → requestRoute(to, profile) → RoutingRepository (OSRM demo, overview=full, geojson, alternatives) → RouteOutcome → Success: activeRoute=routes.first() → garis LineLayer biru; NoRouteFound → banner "Rute tidak ditemukan"; NoInternet → banner; Error → banner generic. Bila myLocation null: banner "Lokasi saya belum tersedia — aktifkan My Location dulu" (sejak TASK-008a; dulu diam tanpa feedback). clearSelection menghapus rute. Kamera terbang ke tempat terpilih saat dipilih (TASK-008b, di layer Map).

## Requirements
R1 tiga profil (driving/foot/bike) via osrmName; R2 garis rute di bawah location dot; R3 rute hilang saat sheet ditutup; R4 error termap ke banner; R5 (baru, TASK-008a) request tanpa GPS fix wajib memberi feedback ke user, bukan diam.

## Inputs
GeoPoint destination, RoutingProfile, myLocation (origin).

## Outputs
activeRoute (OsrmRoute), banner.

## States
idle | requesting (no loading indicator — gap diketahui) | routed | no-route | no-internet | error | no-gps-fix (baru).

## Error States
NoRouteFound/NoInternet/Error → banner. No GPS fix → banner Generic "Lokasi saya belum tersedia — aktifkan My Location dulu" (TASK-008a).

## Dependencies
OsrmApi, NetworkModule, MapOverlay.updateRouteLine, MapViewModel.

## User Flow
Search → pilih tempat (kamera fly-to) → sheet → pilih mode → garis rute muncul.

## Data Flow
Sheet click → VM → repo → OSRM → outcome → state → Style.updateRouteLine.

## Acceptance Criteria
AC1 rute jalan dari lokasi nyata; AC2 garis tergambar tanpa duplikat layer saat re-request; AC3 garis hilang saat clearSelection; AC4 (baru) tekan tombol rute tanpa GPS → banner muncul, tidak ada aksi senyap.

## Current Implementation
routing/OsrmApi.kt, routing/RouteResult.kt, routing/RoutingRepository.kt (RouteOutcome), ui/screens/MapViewModel.kt (requestRoute).

## Known Problems
Tidak ada tampilkan jarak/durasi rute di UI (data tersedia tapi tidak dirender), tidak ada spinner saat request rute, profil foot/bike di server demo kadang gagal (catatan kode), BUG-003 (camera recenter behavior UNKNOWN).

## Future Changes
Render distance/duration; loading state rute.

## Changes
- 2026-09-27 [TASK-008a, BUG-002]: requestRoute tanpa GPS fix kini menampilkan banner instruksi (dulu `?: return` senyap).
