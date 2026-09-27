# NAZE MAPS — Target Architecture (SDD-030)

- ID: SDD-030
- Title: Target Architecture Proposal
- Status: PROPOSED

## Prinsip

Target TIDAK memaksakan Clean Architecture penuh. Audit menunjukkan project kecil (41 file, 1 modul) dengan repository-pattern yang sudah sehat. Target: memperkuat boundary, mengurangi coupling god-ViewModel, membuat testable — dengan perubahan bertahap minimal.

## Layering (konseptual, di dalam :app dulu, tanpa multi-module prematur)

UI (Compose screens + components)
  ↓ state/one-way events
ViewModel (per-feature, bukan satu monolit)
  ↓
Domain (opsional; saat ini hanya logic murni di utils/ — haversine, format, geocode orchestration)
  ↓
Repository (search, routing, location, compass, favorites, history, settings)
  ↓
DataSource: Retrofit API / Room DAO / FusedLocation / Sensor / MapLibre Style ops
  ↓
External: Nominatim, OSRM, OpenFreeMap, Esri, Android platform

## Dependency Rules

- UI → ViewModel (saja). UI tidak boleh membuat Repository sendiri (melanggar: DistanceScreen saat ini — diperbaiki di TASK-007).
- ViewModel → Repository + model (utils). ViewModel tidak menyentuh Retrofit/Room/Sensor/MapLibre SDK secara langsung.
- Repository → DataSource. Repository mengembalikan model/outcome (sealed class) yang TIDAK bocor tipe SDK eksternal (NominatimResult dsb sudah model sendiri — dipertahankan).
- utils/domain murni: tanpa import androidx/android (saat ini sudah bersih — dipertahankan).
- MapLibre / Play Services / Room / Retrofit hanya boleh di-import oleh package map/, location/, favorites/, history/, network/, search/, routing/ masing-masing; tidak boleh bocor ke ui/ (pengecualian MapScreen via wrapper map/ yang sudah ada).
- Satu source of truth per state; tidak ada state duplikat antar-ViewModel.

## Batasan penting (dari audit)

- NazeNavHost tab switching menghancurkan MapScreen → butuh state preservation (lihat ADR-002, bugs BUG-001).
- MapUiState monolitik → dipecah per-feature screen state (ADR-003).
- DI: manual constructor injection dulu; Hilt DITUNDA sampai ada 3+ consumer/use case (anti-over-engineering).

## Non-Goals arsitektur

- Multi-module Gradle (belum perlu di ukuran ini)
- UseCase classes untuk setiap method (repository cukup)
- MVI/full unidirectional framework

## Review point

Dokumen ini di-review ulang setelah TASK-001..004 selesai.
