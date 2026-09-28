# ADR-005 — Navigasi map-first: ganti bottom navigation dengan overflow menu

## Context
Naze Maps adalah aplikasi peta; MAP > CONTROLS > CONTENT. Bottom navigation permanen (NazeBottomNav, 72dp) menempati area bawah layar di semua tab dan menutupi peta (BUG-017). Navigasi antar 4 tab bersifat state-based switcher tanpa back-stack (adr: NazeNavHost).

## Problem
Peta tidak fullscreen; bottom nav overlap dengan system navigation; konsumsi ruang untuk fungsi yang jarang dipakai (3 dari 4 tab bukan map).

## Decision
Ganti permanent bottom navigation dengan compact overflow menu [⋮] kanan-atas (40dp, circular, surface + border), overlay di NazeNavHost, tersedia di semua tab. NazeTab enum dipindah dari BottomNavBar.kt ke OverflowMenu.kt; BottomNavBar.kt dihapus setelah mapping diverifikasi. Search bar dikecilkan jadi compact floating pill (CH-101).

## Alternatives Considered
1. Pertahankan bottom nav, lebih tipis — ditolak: tetap menutupi peta, tetap memakan area.
2. Navigation rail — ditolak: over-engineered untuk 4 peer destination, memakan sisi layar.
3. Gesture/swipe antar tab — ditolak: konflik gesture peta.

## Consequences
+ Peta fullscreen edge-to-edge; visual modern/minimal; akses semua fitur tetap 1-tap.
- Discovery 3 fitur sekunder (Favorites/Distance/Settings) turun (harus buka menu) — trade-off disengaja: fitur utama adalah peta.
- Menu overlay kanan-atas berpotensi overlap visual dengan header screen non-map → dicatat di regression checklist CH-101.

## Status
ACCEPTED — implementasi di CH-101 (branch sdd/phase3-ui).
