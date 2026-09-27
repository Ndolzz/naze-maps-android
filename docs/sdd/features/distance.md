# Feature: Distance Calculator

- Spec ID: FEAT-DIST
- Status: DRAFT (perubahan tercatat di "Changes")

## Purpose
Menghitung jarak haversine multi-titik + ETA.

## Current Behavior
DistanceScreen: daftar titik (mulai A,B) bertambah dinamis; mode TravelMode (WALKING 4.8 / CYCLING 16 / DRIVING 38 kmh); tombol Hitung → minimal 2 titik terisi → tiap titik: "lokasi saya" (pakai myLocation) atau geocodeOne VIA MAPVIEWMODEL (sejak TASK-007; dulu via SearchRepository buatan screen sendiri) → total haversine → format sesuai distanceUnit + menit ETA. Error inline per titik.

## Requirements
R1 titik bisa >2; R2 "lokasi saya" didukung; R3 unit mengikuti settings; R4 hasil format "X.X km • N menit"; R5 (baru) UI tidak membuat repository sendiri — geocoding lewat ViewModel (dependency rule 03-target-architecture).

## Inputs
Text per titik, travelMode, myLocation, distanceUnit.

## Outputs
resultText, errorText, spinner.

## States
idle | calculating | done | error.

## Error States
<2 titik → "Isi minimal 2 titik"; tempat tidak ketemu → "Tidak ditemukan: X"; (bug: "lokasi saya" tanpa fix → pesan menyesatkan, BUG-006).

## Dependencies
MapViewModel.geocodeOne (sejak TASK-007) → SearchRepository, DistanceUtils.

## User Flow
Isi titik → pilih mode → Hitung → hasil Card.

## Data Flow
Screen → MapViewModel.geocodeOne per titik → GeoPoint list → haversine → format.

## Acceptance Criteria
AC1 hasil akurat (karakterisasi vs nilai PWA); AC2 ETA berubah sesuai mode; AC3 unit berubah saat setting berubah; AC4 (baru) behavior identik dengan baseline setelah perubahan boundary (regression manual).

## Current Implementation
ui/screens/DistanceScreen.kt, ui/screens/MapViewModel.kt (geocodeOne), utils/DistanceUtils.kt.

## Known Problems
BUG-006, kalkulasi tetap jalan saat pindah tab (minor), sequential geocode (N+1 request).

## Future Changes
Feedback GPS (BUG-006); paralelisasi geocode.

## Changes
- 2026-09-27 [TASK-007, TD-ARCH-3]: geocoding diarahkan lewat MapViewModel.geocodeOne; SearchRepository milik screen dihapus. Behavior tidak berubah.
