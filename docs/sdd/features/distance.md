# Feature: Distance Calculator

- Spec ID: FEAT-DIST
- Status: DRAFT

## Purpose
Menghitung jarak haversine multi-titik + ETA.

## Current Behavior
DistanceScreen: daftar titik (mulai A,B) bertambah dinamis; mode TravelMode (WALKING 4.8 / CYCLING 16 / DRIVING 38 kmh); tombol Hitung → minimal 2 titik terisi → tiap titik: "lokasi saya" (pakai myLocation) atau geocodeOne via SearchRepository sendiri (bukan via VM) → total haversine → format sesuai distanceUnit + menit ETA. Error inline per titik ("Tidak ditemukan: X").

## Requirements
R1 titik bisa >2; R2 "lokasi saya" didukung; R3 unit mengikuti settings; R4 hasil format "X.X km • N menit".

## Inputs
Text per titik, travelMode, myLocation, distanceUnit.

## Outputs
resultText, errorText, spinner.

## States
idle | calculating | done | error.

## Error States
<2 titik → "Isi minimal 2 titik"; tempat tidak ketemu → "Tidak ditemukan: X"; (bug: "lokasi saya" tanpa fix → pesan menyesatkan, BUG-006).

## Dependencies
SearchRepository (dibuat sendiri di screen — TD-ARCH-3), DistanceUtils, MapViewModel (state share).

## User Flow
Isi titik → pilih mode → Hitung → hasil Card.

## Data Flow
Screen → SearchRepository.geocodeOne per titik → GeoPoint list → haversine → format.

## Acceptance Criteria
AC1 hasil akurat (karakterisasi vs nilai PWA); AC2 ETA berubah sesuai mode; AC3 unit berubah saat setting berubah.

## Current Implementation
ui/screens/DistanceScreen.kt, utils/DistanceUtils.kt (GeoPoint, DistanceUnit, TravelMode, haversineKm, format, etaMinutes).

## Known Problems
BUG-006, TD-ARCH-3 (bypass VM), tidak ada cancel scope saat user pindah tab (kalkulasi tetap jalan — minor), hasil sequential geocode (N+1 request rate risk).

## Future Changes
TASK-007 (lewat VM/repo ter-inject); feedback GPS; paralelisasi geocode.

