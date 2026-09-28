# CH-104 — Redesain layar Pengaturan

- ID: CH-104
- Type: UI/UX REDESIGN (BUG-020)
- Status: IMPLEMENTED (CI + regression manual pending)

## Problem (evidence: screenshot device + feedback user 2026-09-28)
Layar Pengaturan hanya berisi dua baris polos (tema gelap, satuan jarak) tanpa pengelompokan, tanpa ikon, tanpa hierarki — terasa tidak profesional. User juga meminta tidak ada karakter hubung (tanda minus) pada teks UI.

## Decision
Redesain penuh layar (visual saja, behavior tidak berubah):
- Tiga bagian: Tampilan, Peta, Tentang — SectionHeader berwarna primary.
- Kartu (SettingsCard, radius 16dp, surfaceContainerLow) berisi baris berikon (SettingsRow): ikon 22dp primary, judul bodyLarge, deskripsi labelSmall onSurfaceVariant.
- Satuan jarak menjadi pilihan chip (UnitOption, primaryContainer saat terpilih) menggantikan dua TextButton polos.
- Bagian Tentang: nama aplikasi + tagline + versi dari PackageManager (runtime, bukan hardcode).
- Seluruh teks UI bebas karakter hubung (divalidasi otomatis saat commit: semua string literal dicek).
- Scrollable (verticalScroll) untuk aman di layar pendek dan landscape.

## Yang TIDAK ditambahkan (constitution: no fiction)
Tidak ada fitur baru yang tidak didukung ViewModel (bahasa, notifikasi, cache, dll). Hanya fitur existing yang dipresentasikan lebih baik.

## Verification
- Toggle tema bekerja dan langsung menerapkan tema seluruh app.
- Pilihan Kilometer/Miles memengaruhi format jarak (rute + kalkulator).
- Versi tampil sesuai build.
- Tidak ada karakter hubung pada teks UI.
