# Feature: Location & Compass

- Spec ID: FEAT-LOC
- Status: DRAFT

## Purpose
Menyediakan posisi pengguna (blue dot + recenter) dan arah heading kompas.

## Current Behavior
Permission runtime via accompanist (fine+coarse) di MapScreen; jika sudah granted saat launch, startTracking otomatis. startTracking: cek permission → cek GPS on (banner GpsOff bila off) → collect FusedLocation (PRIORITY_HIGH_ACCURACY, 3s) → myLocation state → dot + recenter kamera; compass (TYPE_ROTATION_VECTOR) → headingDegrees → CompassFab (hidden jika sensor tidak ada). stopTracking/onCleared membatalkan job.

## Requirements
R1 request permission saat FAB ditekan bila belum granted; R2 banner PermissionDenied / GpsOff dengan deep-link ke Settings; R3 heading hanya dari sensor nyata (tidak fake); R4 update berhenti saat tracking off atau VM cleared.

## Inputs
Permission result, GPS on/off state, sensor events.

## Outputs
myLocation (NazeLocation: lat/lng/accuracy/bearing?), headingDegrees (0-360).

## States
not-tracking | tracking | tracking-no-fix (myLocation null) | permission-denied | gps-off.

## Error States
PermissionDenied banner; GpsOff banner; tidak ada error untuk "no fix timeout" (menunggu diam-diam) — gap yang diketahui.

## Dependencies
play-services-location, accompanist-permissions, PermissionUtils, LocationRepository, CompassRepository.

## User Flow
Launch → (granted? auto-start) → tekan FAB My Location → grant → tracking + recenter; tekan lagi → stop.

## Data Flow
FusedLocation callbackFlow → VM → uiState.myLocation → MapScreen (dot + camera).

## Acceptance Criteria
AC1 FAB toggle tracking tanpa crash tanpa permission; AC2 banner muncul sesuai kondisi; AC3 compass hidden di device tanpa sensor; AC4 tracking berhenti saat app di-kill (VM cleared).

## Current Implementation
location/LocationRepository.kt, location/CompassRepository.kt, location/PermissionUtils.kt, ui/screens/MapScreen.kt (permission wiring), ui/components/MapFabs.kt.

## Known Problems
BUG-001 (view leak saat pindah tab), BUG-012 (tracking lanjut di tab lain — mismatch komentar vs implementasi), P-1 (rotasi landscape compass UNKNOWN).

## Future Changes
Keputusan spec: stop tracking saat meninggalkan tab MAP (BUG-012); feedback saat no-fix.

