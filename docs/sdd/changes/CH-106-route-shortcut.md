# CH-106 — Rute satu ketuk dari Favorit dan Riwayat

- ID: CH-106
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Untuk mendapat rute ke favorit, pengguna harus mengetik ulang nama tempat di pencarian. Riwayat juga hanya bisa dipilih tanpa aksi rute langsung. Tidak ada jalan pintas satu ketik.

## Decision
- MapViewModel menambah `selectFavoriteAsPlace` (FavoriteEntity menjadi NominatimResult, pola sama seperti selectHistoryEntry) dan `routeFromFavorite` yang memilih tempat lalu langsung memanggil requestRoute profil DRIVING (OSRM, sudah ada).
- Layar Favorit: tiap kartu mendapat tombol ikon mobil berlabel Rute ke sini. Setelah tap, navigasi otomatis pindah ke tab Map supaya banner error (misal lokasi saya belum tersedia) dan garis rute benar benar terlihat, tidak tersembunyi di tab Favorit.
- NazeNavHost mengoper lambda `onNavigateToMap` ke FavoritesScreen (tab switcher berbasis state, tanpa nav library baru).
- Riwayat di MapScreen: tiap baris mendapat tombol ikon mobil yang memilih entri lalu langsung requestRoute DRIVING.

## Yang TIDAK ditambahkan
Tidak ada pilihan profil di tombol (default mobil; profil lain tetap tersedia lewat bottom sheet tempat). Tidak ada rute multi perhentian.

## Verification
- Tap Rute ke sini pada favorit membuka tab Map, tempat terpilih tampil di bottom sheet, garis rute tergambar.
- Tanpa My Location aktif, banner pengingkat muncul di tab Map (bukan hilang diam diam).
- Tombol rute riwayat berfungsi sama dari daftar riwayat.
- Tidak ada karakter hubung pada teks UI.
