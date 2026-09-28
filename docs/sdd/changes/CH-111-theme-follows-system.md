# CH-111 — Tema terang gelap atau ikuti sistem

- ID: CH-111
- Type: FEATURE
- Status: SPEC (implementasi menyusul per commit terisolasi)

## Problem
Pengaturan tema hanya punya sakelar gelap atau terang. Pengguna yang mengatur tema perangkat terang atau gelap otomatis harus mengubah tema aplikasi secara manual setiap kali, karena tidak ada opsi ikuti sistem.

## Decision
- SettingsDataStore menambah enum ThemeMode (LIGHT, DARK, SYSTEM), kunci datastore theme_mode, flow themeMode, dan fungsi setThemeMode. Migrasi: bila theme_mode belum pernah ditulis tetapi dark_theme lama ada, mode diturunkan dari nilai lama itu; bila keduanya belum ada, default SYSTEM.
- MapViewModel: SettingsState menambah themeMode; isDarkTheme tetap ada sebagai nilai terresolve. Saat mode SYSTEM, gelap atau terang dibaca dari konfigurasi UI mode perangkat. toggleTheme lama tetap dipertahankan (langsung LIGHT atau DARK).
- SettingsScreen: baris Tema gelap dengan sakelar diganti baris Tema aplikasi dengan tiga pilihan Terang, Gelap, Ikuti sistem (pola tombol pil yang sama seperti satuan jarak).
- MainActivity dan MapScreen tidak berubah: keduanya tetap memakai isDarkTheme terresolve.
- Tidak ada karakter hubung pada teks UI.

## Yang TIDAK ditambahkan
Tidak ada jadwal tema per waktu. Tidak ada tema warna kustom. Perubahan mode sistem saat aplikasi sedang terbuka diambil saat pengaturan tema berikutnya terbaca (batas praktis activity, tidak diklaim realtime penuh).

## Verification
- Pilih Ikuti sistem: tema aplikasi mengikuti mode terang atau gelap perangkat.
- Pilih Terang atau Gelap: tema aplikasi tetap, tidak terpengaruh mode sistem.
- Pilihan bertahan setelah aplikasi ditutup dan dibuka lagi (datastore).
- Pengguna lama yang sudah pernah mengatur sakelar gelap mendapat mode awal sesuai pilihan lamanya.
- Tidak ada karakter hubung pada teks UI.
