# CH-108 — Pin penanda tempat terpilih di peta

- ID: CH-108
- Type: FEATURE (follow up BUG-004)
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat tempat dipilih (dari pencarian, riwayat, atau tombol Rute ke sini), kamera bergerak ke lokasi tersebut tetapi tidak ada penanda yang tertinggal di peta. Begitu kamera digeser pengguna, tempat terpilih tidak lagi terlihat dan pengguna kehilangan acuan.

## Decision
- MapOverlay.kt: fungsi baru updateSelectedPlaceMarker(lat, lng?) menggambar penanda titik merah (CircleLayer radius 10, stroke putih) lewat GeoJsonSource, pola sama seperti updateLocationDot. Null menghapus penanda.
- Penanda digambar di atas layer location dot supaya selalu terlihat.
- MapScreen: LaunchedEffect(selectedPlace, mapStyle) memanggil updateSelectedPlaceMarker; re-fire saat style reload (toggle tema) karena MapLibre menjatuhkan source layer kustom saat setStyle.
- clearSelection otomatis menghapus penanda (tempat null).

## Yang TIDAK ditambahkan (constitution: no fiction)
Tidak ada ikon pin bergambar (butuh aset icon + SymbolLayer; penanda titik warna kontras adalah representasi jujur yang didukung penuh). Tidak ada label nama tempat di peta (map style sudah punya label POI sendiri).

## Verification
- Pilih tempat dari pencarian: penanda merah muncul tepat di lokasi.
- Geser peta menjauh: penanda tetap ada; geser kembali, penanda masih di tempat yang benar.
- Toggle tema: penanda tetap muncul setelah style reload.
- Tutup bottom sheet (clearSelection): penanda hilang.
- Tidak ada karakter hubung pada teks UI.
