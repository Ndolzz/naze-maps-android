# CH-114 — Mode ikuti kamera

- ID: CH-114
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Saat pengguna berjalan atau bersepeda, peta selalu menghadap utara. Pengguna harus memutar peta manual agar arah hadapnya cocok dengan layar, terutama saat menentukan arah belok.

## Decision
- MapViewState menambah penanda isFollowCameraOn, default mati. Fungsi toggleFollowCamera di MapViewModel membalik penanda itu.
- Komponen baru FollowCameraFab di MapFabs, ikon tiga ratus enam puluh derajat, judul Mode ikuti kamera, warna primer saat aktif. Tombol hanya tampil bila sensor arah tersedia, sama seperti tombol kompas.
- Saat mode aktif dan heading kompas berubah, MapScreen menggerakkan kamera agar bearing peta mengikuti heading, target dan zoom tetap. Saat mode mati tidak ada gerakan kamera otomatis.
- Tap tombol kompas (reset utara) juga mematikan mode ikuti kamera agar tidak saling menimpa.
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada pitch atau kemiringan kamera tiga dimensi, tidak ada mode pandangan berkendara dengan banner jalan, tidak ada penyesuaian zoom otomatis, tidak ada penyimpanan preferensi ikuti kamera antar sesi.

## Verification
- Dengan sensor arah tersedia, tombol baru tampil di kolom tombol peta; tanpa sensor, tombol tidak tampil.
- Mode aktif: memutar perangkat memutar peta mengikuti arah hadap; zoom dan posisi tengah tidak berubah.
- Tap tombol kompas saat mode aktif mematikan mode dan peta kembali menghadap utara.
- Unit test: toggleFollowCamera membalik isFollowCameraOn di slice map.
- Tidak ada karakter hubung pada teks UI.
