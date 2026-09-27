# Feature: Settings

- Spec ID: FEAT-SET
- Status: DRAFT

## Purpose
Preferensi user: tema gelap, satuan jarak.

## Current Behavior
SettingsDataStore (DataStore preferences "naze_settings"): isDarkTheme default TRUE, distanceUnit default KM. SettingsScreen: switch tema; TextButton KM/MI (bold = aktif). MapViewModel init: collect kedua flow → uiState → MainActivity pakai isDarkTheme untuk NazeMapsTheme; MapScreen pakai untuk style URL. Toggle theme menulis ke DataStore (flow balik ke state).

## Requirements
R1 persist; R2 tema berlaku app-wide; R3 satuan berlaku di MapScreen (format rute) & DistanceScreen.

## Inputs
User toggle.

## Outputs
DataStore keys: dark_theme (boolean), distance_unit (string enum).

## States
dark|light; km|mi.

## Error States
Value corrupt di store → runCatching fallback KM (CONFIRMED robust).

## Dependencies
DataStore 1.1.1, MapViewModel, MainActivity/MapScreen/DistanceScreen consumers.

## User Flow
Tab Settings → ubah → langsung efektif.

## Data Flow
Screen → VM → DataStore → flow → VM collect → uiState → UI.

## Acceptance Criteria
AC1 restart mempertahankan pilihan; AC2 tema mengganti style peta; AC3 satuan mengubah format jarak.

## Current Implementation
data/SettingsDataStore.kt, ui/screens/SettingsScreen.kt.

## Known Problems
Default dark TRUE (bukan follow-system — komentar Theme.kt mengklaim defaulting to system; INKONSISTEN: DataStore default true, NazeMapsTheme default isSystemInDarkTheme() tapi MainActivity selalu kirim nilai eksplisit). Tidak ada opsi "ikuti sistem".

## Future Changes
Keputusan produk: default follow-system? change request bila ya.

