# CH-105 — Simpan favorit langsung dari peta (tekan lama)

- ID: CH-105
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat ini favorit hanya bisa disimpan dari bottom sheet hasil pencarian (tombol Simpan di MapScreen). Pengguna tidak bisa menyimpan titik arbitrer di peta yang tidak ada di hasil pencarian, misalnya rumah atau titik jalan tanpa nama.

## Decision
- MapLibreMapView mendapat callback opsional `onMapLongClick` yang di-wire ke `MapLibreMap.addOnMapLongClickListener` (API native MapLibre, bukan fitur fiktif).
- MapScreen: tekan lama di peta membuka ModalBottomSheet berisi koordinat titik (format 5 desimal), TextField nama (default Lokasi tersimpan), dan tombol Simpan ke Favorit.
- Simpan memakai `MapViewModel.saveFavorite` yang sudah ada (Room FavoriteDao) — tanpa endpoint baru, tanpa reverse geocoding (Nominatim reverse belum ada di NominatimApi; dicatat sebagai follow up, tidak diklaim).
- Konfirmasi memakai Toast singkat; sheet tertutup setelah simpan.

## Yang TIDAK ditambahkan (constitution: no fiction)
Tidak ada reverse geocoding otomatis nama alamat (API belum ada), tidak ada kategori favorit, tidak ada import ekspor favorit.

## Verification
- Tekan lama titik mana pun di peta membuka sheet dengan koordinat yang benar.
- Simpan muncul di layar Favorit dengan nama dan koordinat yang dimasukkan.
- Tekan lama saat sheet pencarian terbuka tidak mengganggu state pencarian.
- Tidak ada karakter hubung pada teks UI.
