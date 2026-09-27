# ADR-004: Network Layer

## Context
Retrofit+OkHttp singleton (NetworkModule), dua API keyless: Nominatim, OSRM demo. Timeout 10s, UA deskriptif. Outcome pattern sealed class per repo.

## Problem
Konsistensi & kepatuhan policy pihak ketiga; ketergantungan service publik.

## Decision
Pertahankan pattern: satu NetworkModule, per-area repo dengan sealed Outcome (Success/Empty/NoInternet/Error). Tambahan ke depan: param limit & accept-language untuk Nominatim (TASK-011), dan ConnectivityObserver di-wire untuk banner proaktif (TASK-006). Tidak ada ganti provider/library.

## Alternatives Considered
Ktor (ditolak: ganti library tanpa kebutuhan); self-host OSRM/Nominatim (ditunda: biaya operasional, hanya bila rate-limit jadi masalah nyata).

## Consequences
Risiko 403 Nominatim berkurang; perilaku error konsisten; perlu test MockWebServer (TD-DEP-3).

## Status
PROPOSED
