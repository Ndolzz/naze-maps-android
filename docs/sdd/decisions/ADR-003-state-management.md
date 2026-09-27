# ADR-003: State Management

## Context
Saat ini satu MapUiState (12 field) di MutableStateFlow, di-update read-modify-write dari >=4 coroutine paralel (settings collectors, location, compass, search).

## Problem
Lost-update race (BUG-005) + god-state membuat fitur coupled.

## Decision
Fase 1 (minimum): semua update wajib via MutableStateFlow.update {} (atomik). Fase 2 (setelah boundary aman): pecah state per-feature (MapTrackingState, SearchState, RouteState, SettingsState) tetap dalam VM yang sama dulu; pemecahan ViewModel menunggu hasil TASK-009/010.

## Alternatives Considered
MVI single immutable event pipeline (ditolak: rewrite); stay as-is (ditolak: race nyata).

## Consequences
TASK-004 dikerjakan paling awal setelah characterization test; UI tidak berubah.

## Status
PROPOSED
