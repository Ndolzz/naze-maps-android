# CH-112 — Halaman bantuan

- ID: CH-112
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Fitur utama (tekan lama untuk favorit, saran dari riwayat, mode rute, tema ikuti sistem) tidak ada penjelasannya di dalam aplikasi. Pengguna baru harus menebak sendiri.

## Decision
- Composable baru HelpScreen menampilkan cara pakai setiap fitur yang benar benar ada: cari tempat, rute dengan tiga mode, favorit lewat tekan lama, riwayat dan saran, tema aplikasi, dan satuan jarak. Isi statis, hanya fitur nyata.
- Entri dari layar Pengaturan, bagian Tentang, baris Bantuan dengan ikon tanda tanya. SettingsScreen mendapat parameter onOpenHelp.
- NazeNavHost menambah state showHelp; saat aktif HelpScreen tampil penuh menutupi tab apa pun, dengan tombol kembali di bar atas. Tidak ada tab baru di menu overflow.
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada FAQ interaktif, pencarian dalam bantuan, video, atau tautan eksternal yang mengklaim hal di luar aplikasi.

## Verification
- Pengaturan, bagian Tentang: baris Bantuan bisa diketuk dan halaman bantuan terbuka.
- Halaman bantuan menjelaskan keenam area fitur tanpa menyebut kemampuan yang tidak ada.
- Tombol kembali menutup halaman bantuan dan kembali ke tab sebelumnya.
- Tidak ada karakter hubung pada teks UI.
