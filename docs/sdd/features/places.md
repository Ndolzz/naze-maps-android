# Feature: Favorites & History (Places)

- Spec ID: FEAT-PLACES
- Status: DRAFT

## Purpose
Persist tempat favorit (Room favorites) dan riwayat pencarian (Room search_history).

## Current Behavior
Favorites: save dari bottom sheet (insert REPLACE by exact lat/lng match via autoGenerate id — sebenarnya selalu insert baru; duplikat mungkin bila disave 2x). FavoritesScreen: list Card, delete per item, empty state. History: record saat selectPlace (deleteAt by lat/lng → insert → trim ke 30); tampil saat query kosong di MapScreen (20 teratas) dengan delete per item & hapus semua; selectHistoryEntry membangun NominatimResult dan membuka sheet tanpa request ulang.

## Requirements
R1 favorit bertahan antar restart; R2 riwayat dedupe by koordinat; R3 riwayat max 30 record; R4 aksi hapus tersedia.

## Inputs
selectedPlace (save), selectPlace (record), user actions (delete/rename/clear).

## Outputs
Flow<List<FavoriteEntity>>, Flow<List<HistoryEntity>>.

## States
empty | populated; history: visible saat query kosong.

## Error States
Tidak ada error handling DB (Room suspend; kegagalan → crash/exception tidak ditangani) — gap tercatat.

## Dependencies
Room 2.6.1 (KSP), dua database singleton (naze_favorites.db, naze_history.db), MapViewModel.

## User Flow
Search → sheet → Save → tab Favorites melihat item. Search → pilih → muncul di Riwayat.

## Data Flow
VM → Repository → DAO → Flow → collectAsState di screen.

## Acceptance Criteria
AC1 data survive restart; AC2 dedupe riwayat bekerja (re-search place sama → naik ke atas); AC3 delete bekerja.

## Current Implementation
favorites/* (Entity, Dao, Database singleton, Repository), history/* (sama), FavoritesScreen, bagian history di MapScreen.

## Known Problems
BUG-007 (trim 30 vs limit 20), BUG-009 (rename/isSaved dead code), duplikasi pola dua DB (TD-ARCH-5), selectHistoryEntry tidak fly-to.

## Future Changes
Konsolidasi DB saat menyentuh area; opsi rename UI (change request); marker fly-to.

