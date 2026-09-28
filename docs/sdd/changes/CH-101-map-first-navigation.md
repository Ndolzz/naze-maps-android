# CH-101 — Map-first navigation: bottom nav → overflow menu + compact search

- ID: CH-101
- Type: UI/UX REDESIGN (BUG-017)
- Status: IMPLEMENTED (CI + regression manual pending)

## Problem
Bottom navigation permanen (72dp) menutupi area peta dan bertumpuk dengan system navigation bar; search bar OutlinedTextField penuh terasa berat; peta tidak terasa fullscreen.

## Decision
1. HAPUS visual bottom navigation; GANTI dengan compact overflow menu [⋮] kanan-atas (NazeNavHost overlay, statusBarsPadding, 40dp, circular).
2. NazeTab enum dipindah ke OverflowMenu.kt (BottomNavBar.kt dihapus — tidak ada lagi referensi).
3. Search bar → compact floating pill: shape 24dp radius, border transparan saat unfocus, container surface, tipografi bodyMedium, ikon 18dp. Behavior (query/debounce/loading/result/clear) tidak berubah — hanya visual. Fix deprecation TD-CQ-5 (outlinedTextFieldColors → OutlinedTextFieldDefaults.colors).
4. Peta sudah edge-to-edge (enableEdgeToEdge + systemBarsPadding di overlay column) — tanpa hardcoded padding; hanya overlay UI yang diberi inset. Search bar diberi end-padding 56dp untuk clearance menu (posisi overlay, bukan workaround inset).

## Mapping (fungsi lama tidak boleh hilang)
| Bottom nav lama | Akses baru |
|---|---|
| MAP | Overflow menu → "Map" |
| FAVORITES | Overflow menu → "Favorites" |
| DISTANCE | Overflow menu → "Distance" |
| SETTINGS | Overflow menu → "Settings" |

Semua tujuan tetap dapat dijangkau dari semua tab (menu overlay di semua screen, kanan-atas).

## Non-goals
- Tidak mengubah state management, ViewModel, repository, business logic search/rute.
- Tidak mengubah screen Favorites/Distance/Settings selain kehilangan bottomBar Scaffold padding.

## Verification
- CI: compile + unit tests hijau.
- Manual regression: buka semua 4 destinasi lewat menu; search focus/keyboard/query/clear; hasil klik; banner error tampil; landscape; gesture nav & 3-button nav (menu tidak tertutup); overlap menu vs header screen non-map (dicek; jika overlap visual ditemukan → follow-up kecil, bukan blocker fungsional).
