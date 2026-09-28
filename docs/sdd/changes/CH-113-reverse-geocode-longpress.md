# CH-113 — Alamat saat tekan lama

- ID: CH-113
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat pengguna menekan lama titik di peta, sheet Simpan lokasi ini (CH-105) hanya menampilkan koordinat mentah. Pengguna tidak tahu nama tempat yang sedang ditekan, dan bila menyimpan favorit tanpa mengetik nama, favoritnya hanya berjudul Lokasi tersimpan.

## Decision
- NominatimApi menambah endpoint reverse (lat, lon, format json, zoom 16, accept-language id) yang mengembalikan satu NominatimResult.
- SearchRepository menambah fungsi reverseGeocode(lat, lon) yang mengembalikan NominatimResult atau null bila gagal atau sedang luring. Semua fake di unit test ikut mengimplementasi fungsi ini.
- MapViewModel menambah slice longPress: alamat hasil reverse geocoding (null bila belum selesai atau gagal) dan penanda sedang memuat. Fungsi resolveLongPressAddress(lat, lon) memanggil repository dan memperbarui slice.
- MapScreen memanggil resolveLongPressAddress saat sheet tekan lama terbuka, lalu menampilkan nama tempat di atas koordinat. Teks pengganti: Menunggu alamat saat memuat, dan Alamat tidak ditemukan bila hasil null. Bila luring, teks tetap koordinat seperti semula.
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada tombol rute ke titik tekan lama (masih backlog), tidak ada penyimpanan riwayat untuk tekan lama, tidak ada tombol coba ulang di sheet.

## Verification
- Tekan lama titik mana pun di peta: sheet menampilkan nama tempat hasil reverse geocoding, koordinat tetap sebagai teks kedua.
- Saat memuat, sheet menampilkan teks menunggu, bukan kosong.
- Bila jaringan gagal atau hasil kosong, sheet kembali menampilkan koordinat dengan teks alamat tidak ditemukan, tanpa crash.
- Unit test: fake SearchRepository mengimplementasi reverseGeocode; ada test bahwa hasil reverse mengisi slice longPress.
- Tidak ada karakter hubung pada teks UI.
