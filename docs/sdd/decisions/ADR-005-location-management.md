# ADR-005: Location Management

## Context
FusedLocationProviderClient (Play Services) + rotation vector sensor; tracking via callbackFlow; permission via accompanist; comment menyatakan "collectors should cancel when map leaves the foreground" tapi NazeNavHost membongkar MapScreen tanpa stopTracking.

## Problem
Kebocoran energi/privasi bila tracking jalan saat user di tab lain; lifecycle MapView (BUG-001); ambiguity contract.

## Decision
Spesifikasi eksplisit: (1) location & compass flow berhenti saat user meninggalkan tab MAP (tab switch → stopTracking) ATAU tracking tetap tapi hanya di VM — keputusan final menunggu verifikasi BUG-001/BUG-012 di runtime, ditetapkan sebelum TASK-005; (2) tetap FusedLocation (tidak pindah ke provider murni AOSP sekarang — device tanpa Play Services dicatat sebagai risiko UNKNOWN); (3) contract "no fake heading" dipertahankan (sensor null → compass hidden).

## Alternatives Considered
Foreground service untuk tracking kontinu (ditolak: bukan use case app); pindah ke LocationManager murni (ditunda).

## Consequences
Perlu spec update feature location.md saat keputusan diambil; regression checklist menambah skenario tab-switch.

## Status
PROPOSED (keputusan final = OPEN)
