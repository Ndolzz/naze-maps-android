# CH-110 — Saran pencarian dari riwayat

- ID: CH-110
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat pengguna mengetik di kotak pencarian, hasil Nominatim datang setelah debounce 500 milidetik. Tempat yang pernah dicari dan tersimpan di riwayat tidak ikut diusulkan, padahal kemungkinan besar pengguna mencari ulang tempat yang sama.

## Decision
- MapScreen: daftar riwayat kini tampil baik saat kotak pencarian kosong maupun saat pengguna sedang mengetik. Saat mengetik, entri riwayat yang nama atau alamatnya memuat teks kueri (case insensitive) ditampilkan sebagai bagian Saran dari riwayat, di atas hasil Nominatim yang datang belakangan.
- Judul daftar berubah menjadi Saran dari riwayat saat ada teks kueri, dan kembali menjadi Riwayat lokasi saat kosong.
- Saat hasil Nominatim tiba, daftar saran riwayat memberi jalan ke hasil pencarian (perilaku lama dipertahankan).
- Ketuk entri saran memilih tempat itu tanpa memanggil Nominatim ulang (pola selectHistoryEntry yang sudah ada).
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada pencarian fuzzy atau typo tolerance. Tidak ada pencadangan server. Riwayat tetap lokal di perangkat.

## Verification
- Ketik nama tempat yang pernah dicari: entri riwayat muncul sebagai Saran dari riwayat sebelum hasil Nominatim tiba.
- Ketuk saran: tempat terpilih, kamera bergerak, tanpa permintaan pencarian baru.
- Kotak pencarian kosong: daftar menampilkan seluruh riwayat seperti sebelumnya.
- Setelah hasil Nominatim tiba, daftar saran diganti hasil pencarian.
- Tidak ada karakter hubung pada teks UI.
