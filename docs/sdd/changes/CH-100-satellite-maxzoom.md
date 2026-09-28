# CH-100 — Satellite: hilangkan placeholder "Map data not yet available" pada zoom dekat

- ID: CH-100
- Type: BUG FIX (BUG-016)
- Status: IMPLEMENTED (CI + regression manual pending)

## Problem
Pada mode satellite, zoom terlalu dekat menampilkan placeholder "Map data not yet available" pada area tile yang gagal.

## Root Cause (evidence)
- File: app/src/main/java/com/naze/maps/map/MapOverlay.kt, fungsi Style.setSatelliteVisible
- Satellite layer memakai RasterSource dengan TileSet "Esri World Imagery MapServer" tanpa maxZoom.
- Tile URL: https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}
- Esri World Imagery mempunyai LOD maksimum umum z19 (sebagian area lebih rendah). Saat kamera zoom > 19, MapLibre meminta tile z>=20 → server merespons 404/tidak tersedia → MapLibre menampilkan placeholder "Map data not yet available".
- Confidence root cause: HIGH (provider tidak memiliki tile pada zoom tersebut — kategori A).
- Alternatif penyebab (B config salah, C API, D network, E implementasi loading, F cache, G SDK, H device) diperiksa dan dikesampingkan: overlay raster berfungsi normal di zoom rendah; tidak ada API key; placeholder muncul konsisten hanya di zoom tinggi.

## Decision
Set maxZoom TileSet = 19. MapLibre kemudian melakukan raster overzoom: tile z19 induk diskalakan, bukan meminta tile yang tidak tersedia. Tidak ada penurunan fungsi (detail visual pada z>19 tidak pernah tersedia dari provider ini — placeholder bukan data). Tidak ada overlay palsu, tidak ada penyembunyian pesan error.

## Alternatives Considered
1. maxZoom kamera global — ditolak: membatasi juga mode vektor (OpenFreeMap mendukung zoom lebih tinggi).
2. Mengganti provider satellite — ditolak saat ini: perubahan dependency layanan eksternal butuh evidence komparatif tersendiri (change request baru).
3. Menyembunyikan pesan error — dilarang konstitusi (bukan root cause).

## Verification
- CI: compile + unit tests hijau.
- Manual: satellite ON → zoom > 19 → imagery tetap tampil (upscaled), tidak muncul placeholder; toggle OFF/ON ulang; theme switch saat satellite ON.
