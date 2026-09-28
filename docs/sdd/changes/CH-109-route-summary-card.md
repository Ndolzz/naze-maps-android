# CH-109 — Kartu ringkasan rute jarak waktu dan mode

- ID: CH-109
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat rute aktif, pengguna hanya melihat garis rute di peta. Jarak dan waktu tempuh dari OSRM hanya muncul di teks bagikan rute. Tidak ada ringkasan yang selalu terlihat selama rute dipakai.

## Decision
- MapViewModel: RouteState menambah field activeProfile (RoutingProfile nullable). requestRoute menyimpan profil yang dipakai saat sukses; clearSelection melepaskan keduanya; fungsi baru clearRoute menutup garis rute tanpa melepas pilihan tempat.
- MapScreen: kartu ringkasan melayang di bawah tengah layar saat activeRoute ada. Isi: ikon mode, label mode (Mobil, Jalan kaki, Sepeda), jarak memakai DistanceUtils.format sesuai satuan pengaturan, dan perkiraan waktu dari durationSeconds OSRM (menit, atau jam menit bila lebih dari satu jam).
- Kartu punya tombol tutup yang memanggil clearRoute.
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada navigasi belok demi belok. Tidak ada ETA lalu lintas langsung (data tidak tersedia dari OSRM demo). Tidak ada multi rute alternatif.

## Verification
- Setelah meminta rute mobil, kartu muncul dengan ikon mobil, jarak, dan waktu.
- Ganti satuan di Pengaturan (km ke mi): teks jarak kartu mengikuti.
- Tombol tutup menutup kartu dan garis rute, pilihan tempat tetap di bottom sheet.
- Rute jalan kaki dan sepeda menampilkan ikon dan label sesuai mode.
- Tidak ada karakter hubung pada teks UI.
