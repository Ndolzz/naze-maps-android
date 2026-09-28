# CH-103 — Kompas dipindah ke bawah tombol menu [⋮] (top-end)

- ID: CH-103
- Type: UI/UX FIX (BUG-019)
- Status: IMPLEMENTED (CI + regression manual pending)

## Problem (evidence: screenshot device 2026-09-28 17:16)
Kompas berada di kolom FAB kanan-bawah bersama Layers/MyLocation, tetapi secara visual area kanan-atas (menu [⋮] + search bar end) terasa tertimpa/rapi tidak. User meminta kompas dipindah ke bawah titik tiga.

## Decision
CompassFab dipindah dari kolom FAB kanan-bawah ke top-end, tepat di bawah NavOverflowMenu:
- Posisi: align TopEnd + statusBarsPadding + padding top 60dp (menu: top 12 + tinggi 40 + gap 8), end 12dp — sejajar margin kanan menu.
- Kolom FAB kanan-bawah kini hanya Layers + MyLocation.
- Behavior CompassFab tidak berubah (rotasi -heading, tap reset north, hidden bila heading null).

## Non-goals
- Tidak mengubah logic compass (CompassRepository/heading) maupun tombol lain.

## Verification
- Kompas tampil rapi di bawah [⋮]; tidak menutupi search bar; tap reset-north tetap bekerja; landscape & gesture-nav inset aman.
