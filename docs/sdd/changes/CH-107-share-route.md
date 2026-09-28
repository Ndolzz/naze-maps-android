# CH-107 — Bagikan rute

- ID: CH-107
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Tombol bagikan yang ada hanya mengirim lokasi tempat (tautan OSM). Ringkasan rute aktif (jarak, perkiraan waktu dari OSRM) tidak bisa dibagikan padahal datanya sudah tersimpan di state.

## Decision
- MapScreen bottom sheet: jika ada rute aktif, tampil tombol Bagikan rute.
- Teks bagian berisi: nama tujuan, jarak (DistanceUtils.format mengikuti satuan pengaturan KM atau MI), perkiraan waktu (durasi OSRM dibulatkan ke menit), dan tautan OSM Directions profil mobil dari lokasi saya ke tujuan (atau tautan lokasi bila lokasi saya tidak tersedia).
- Memakai Intent ACTION_SEND yang sudah ada di bottom sheet — tanpa library share baru.

## Yang TIDAK ditambahkan
Tidak ada navigasi giliran per giliran, tidak ada ekspor GPX, tidak ada berbagi gambar rute.

## Verification
- Tombol hanya muncul saat rute aktif; setelah clearSelection tombol hilang.
- Jarak mengikuti satuan pilihan di Pengaturan.
- Tautan directions valid saat lokasi saya aktif; fallback tautan lokasi saat tidak.
- Tidak ada karakter hubung pada teks UI.
