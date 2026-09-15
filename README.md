# NAZE MAPS — Native Android (scaffold)

Status: **scaffold fungsional**, belum pernah dicompile (sandbox pembuat tidak punya Android SDK/network). Baca `LIMITASI & LANGKAH BERIKUTNYA` di bawah sebelum push ke CI.

Map engine: **MapLibre Native** — open source, gratis, tanpa API key, tanpa kartu pembayaran, tanpa akun apa pun.

## Yang sudah diimplementasikan

| Area | Status |
|---|---|
| Map (MapLibre, vector tile OpenFreeMap) | ✅ pan/zoom/rotate, style dark/light ikut tema app |
| My Location (native FusedLocationProviderClient) | ✅ permission request, tracking, recenter kamera |
| Compass (sensor rotation vector) | ✅ heading + reset-to-north, disembunyikan otomatis kalau sensor tidak ada |
| Search (Nominatim) | ✅ debounce, hasil, pilih → bottom sheet detail |
| Routing (OSRM) | ✅ request rute dari lokasi saya ke tempat dipilih |
| Distance calculator multi-titik | ✅ port dari logika haversine PWA, dukung "lokasi saya" sebagai titik |
| Favorites | ✅ **persist beneran** via Room (PWA lama: in-memory saja) |
| Settings (tema, satuan jarak) | ✅ persist via DataStore |
| Error states (GPS off / permission / no internet) | ✅ banner + tombol ke Settings |
| Branding (logo, warna, dark UI) | ✅ pakai aset asli, warna sama persis dengan PWA |
| App icon semua density | ✅ digenerate dari `icon-512.png` / `icon-maskable-512.png` asli |
| GitHub Actions build APK | ✅ `.github/workflows/android-build.yml` — tanpa secret apa pun |
| Globe 3D | ❌ **tidak diimplementasikan** — MapLibre Native Android belum mendukung globe projection (masih web-only via maplibre-gl-js). Sesuai instruksi di dokumen requirement kamu sendiri (jangan klaim 360° kalau engine cuma 2D), fitur ini sengaja tidak dibuat palsu. Kalau nanti butuh globe asli, satu-satunya opsi realistis adalah engine berbayar (Mapbox/MapTiler) yang butuh kartu pembayaran terdaftar. |
| Layers (satellite toggle) | ⏳ belum — MapLibre mendukung, tinggal tambah style satellite |
| Distance-line di peta (garis A-B tergambar) | ⏳ belum — perlu tambah LineManager dari plugin annotation MapLibre |
| Share lokasi | ⏳ tombol ada, intent share belum disambung |
| Recent searches | ⏳ belum |

## LIMITASI & LANGKAH BERIKUTNYA (penting)

1. **Belum pernah dicompile.** Lingkungan saya tidak punya Android SDK, emulator, atau akses internet untuk menjalankan Gradle. Semua kode ditulis manual mengikuti API resmi MapLibre/AndroxX — kemungkinan ada typo/ketidakcocokan versi kecil yang baru kelihatan saat build pertama di mesin kamu atau di GitHub Actions.
2. **Tidak butuh token/kartu apa pun.** MapLibre + OpenFreeMap + Nominatim + OSRM semuanya gratis tanpa signup. Build langsung jalan begitu SDK Android tersedia.
3. **`gradle-wrapper.jar` (binary) tidak saya sertakan** karena tools saya cuma bisa nulis teks, bukan file biner. Workflow GitHub Actions sudah diset supaya tidak butuh file itu (pakai `gradle/actions/setup-gradle` langsung). Untuk build lokal, jalankan sekali: `gradle wrapper --gradle-version 8.7` di root project supaya `gradlew`/`gradlew.bat` muncul.
4. Merge ke repo `Ndolzz/naze.maps` yang sudah ada perlu kamu lakukan manual — saya tidak punya akses push ke GitHub dari sini.
5. Bagian "⏳ belum" di atas adalah fitur yang wajib ada di dokumen kamu tapi belum sempat ditulis di sesi ini — strukturnya sudah siap menampung.

## Struktur

```
app/src/main/java/com/naze/maps/
  ui/          Compose screens, theme, komponen (search bar, FAB, bottom nav)
  map/         Wrapper MapLibre MapView + style
  location/    GPS (FusedLocationProviderClient) + compass sensor
  search/      Nominatim
  routing/     OSRM
  favorites/   Room (persist asli, bukan mock)
  data/        DataStore settings
  network/     Retrofit/OkHttp shared client + connectivity observer
  utils/       Haversine/format distance
```
