# Feature: Search

- Spec ID: FEAT-SEARCH
- Status: DRAFT

## Purpose
Mencari tempat berdasarkan teks (geocoding Nominatim).

## Current Behavior
NazeSearchBar → VM.onSearchQueryChange: set searchQuery, cancel job sebelumnya; blank → clear results; else debounce 350ms → SearchRepository.search → SearchOutcome → Success(results)/Empty/NoInternet/Error → dropdown hasil; klik → selectPlace (bottom sheet + record history + clear query/results).

## Requirements
R1 debounce; R2 hasil menampilkan mainText/subText; R3 pilihan hasil membuka bottom sheet; R4 error ditampilkan sebagai banner; R5 User-Agent deskriptif (policy Nominatim).

## Inputs
Query text.

## Outputs
searchResults (List<NominatimResult>), selectedPlace, banner.

## States
idle | typing-debouncing | searching | has-results | empty | error.

## Error States
NoInternet → banner; Error → generic banner; Empty → hanya hilangnya dropdown (tidak ada pesan "tidak ditemukan").

## Dependencies
NominatimApi (Retrofit), NetworkModule, MapViewModel, history.

## User Flow
Ketik → hasil live → pilih → sheet detail.

## Data Flow
UI text → VM debounce → repo → API → outcome → state → dropdown.

## Acceptance Criteria
AC1 hasil muncul < ~1.5s pada koneksi normal; AC2 clear button menghapus query+hasil; AC3 pilih hasil merekam history.

## Current Implementation
search/NominatimApi.kt (GET search, base https://nominatim.openstreetmap.org/), search/SearchRepository.kt (sealed SearchOutcome, geocodeOne), search/SearchResult.kt (NominatimResult mapping).

## Known Problems
BUG-008 (tanpa limit/accept-language, debounce agresif → risiko 403), race BUG-005, dropdown menutupi hasil saat keyboard (UI polish, UNKNOWN).

## Future Changes
TASK-011 hardening; string resource; empty-state message.

