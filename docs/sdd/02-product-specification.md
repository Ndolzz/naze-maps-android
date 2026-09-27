# NAZE MAPS — Product Specification (SDD-020)

- ID: SDD-020
- Title: Product Specification
- Status: APPROVED (mendeskripsikan kondisi NYATA saat ini, bukan aspirasi)

## Tujuan

Aplikasi peta Android gratis & terbuka: peta, pencarian tempat, rute, pengukur jarak — tanpa API key berbayar. Porting dari PWA Naze Maps dengan tambahan persistensi asli (Room).

## Target Pengguna

- INFERRED: pengguna umum Indonesia yang butuh peta/rute gratis tanpa akun; tidak ada persona terdokumentasi di repo.

## Core Functionality (CONFIRMED implemented)

1. Peta vector interaktif (pan/zoom/rotate), dark/light
2. My Location (permission, tracking, blue dot, recenter)
3. Compass heading + reset north
4. Search tempat (Nominatim) + detail bottom sheet + share
5. Riwayat pencarian (Room, dedupe, max 30, tampil 20)
6. Rute dari lokasi saya ke tempat terpilih (OSRM: mobil/jalan kaki/sepeda)
7. Favorites (Room)
8. Distance calculator multi-titik + ETA ("lokasi saya" sebagai titik)
9. Settings: tema, satuan jarak
10. Satellite toggle (Esri raster)
11. Error banner: GPS off, permission denied, no internet, generic

## Non-Goals (saat ini)

- Globe/3D projection (tidak didukung MapLibre Native Android — README eksplisit)
- Turn-by-turn navigation / voice guidance
- Offline map/tiles/cache
- Akun user, sync cloud
- Background location / foreground service navigation
- Reverse geocoding, POI categories
- Multi-bahasa (string campur EN/ID saat ini)

## Feature Boundaries

- Routing hanya dari "lokasi saya" (myLocation) ke satu destinasi; bukan multi-waypoint, bukan from arbitrary point (CONFIRMED MapViewModel.requestRoute).
- Search hanya text query; tidak ada kategori/nearby.
- Favorites hanya point (lat/lng + nama); tidak ada folder/route.

## Platform Assumptions

- Android minSdk 24 – targetSdk 34; perangkat dengan Play Services untuk FusedLocation (CONFIRMED). Perangkat tanpa Play Services: location rusak — UNKNOWN/risiko.

## Offline/Online Behavior

- Peta, search, routing butuh internet; tidak ada cache offline. Banner "no internet" hanya muncul reaktif saat search/route gagal (CONFIRMED; ConnectivityObserver belum terpakai).

## Permission Requirements

- INTERNET, ACCESS_NETWORK_STATE (manifest), ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION (runtime, via accompanist). (CONFIRMED)

## External Service Dependencies (semua gratis, keyless — CONFIRMED)

- OpenFreeMap tiles (style liberty/dark)
- Nominatim (search; wajib User-Agent deskriptif — sudah ada)
- OSRM demo server router.project-osrm.org (routing; README mencatat profil non-mobil kadang tidak andal)
- Esri World Imagery raster (satellite)
- Konsekuensi: ketersediaan & rate limit pihak ketiga di luar kendali; belum ada fallback.
