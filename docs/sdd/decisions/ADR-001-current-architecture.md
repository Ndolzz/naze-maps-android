# ADR-001: Current Architecture (As-Is)

## Context
Audit 2026-09-27 atas commit a8a7598. Single module :app, 41 file Kotlin, Compose UI, MapLibre interop.

## Problem
Perlu dokumentasi arsitektur aktual sebagai baseline sebelum evolusi.

## Decision
Mengadopsi (men dokument-asli): MVVM-lite single-ViewModel (AndroidViewModel + satu StateFlow MapUiState) → repository per area (search/routing/location/compass/favorites/history/settings) → data source konkret (Retrofit/Room/FusedLocation/Sensor). Tanpa domain layer, tanpa DI, navigasi state-based.

## Alternatives Considered
N/A (dokumentasi kondisi eksisting).

## Consequences
Pro: sederhana, cepat dikembangkan, repository pattern sudah memisahkan I/O.
Kontra: god-ViewModel, tidak testable tanpa refactor injection, state monolit rawan race (BUG-005), coupling UI→repo di DistanceScreen.

## Status
ACCEPTED (sebagai baseline; lihat ADR-002 untuk target).
