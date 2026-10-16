# ADR-006: Pixel-Art RPG World Map Redesign

## Context
Product meminta redesain visual peta utama menjadi peta dunia pixel-art RPG interaktif
(bioma, landmark, emblem, dock mengambang, graticule, zoom adaptif). Baseline: MapLibre
Native Android + OpenFreeMap, 12 fitur berjalan, CI hijau, migration plan DONE. Constitution
(SDD-010) melarang rewrite, ganti library tanpa justifikasi, dan fitur tanpa spec.

## Problem
Bagaimana menghadirkan identitas pixel-art RPG + landmark dunia tanpa mengganti engine,
mengarang data, atau merusak 12 fitur baseline?

## Decision
1. ENGINE TETAP: MapLibre Native 11.5.2 + OpenFreeMap. Tidak ada library/engine baru.
   Style kustom = satu-satunya perubahan rendering (sesuai anjuran OpenFreeMap: style
   kustom harus di-host sendiri → dibundel sebagai assets aplikasi).
2. PIXEL-ART VIA-STYLE: "Pixel-art" dicapai dengan palet retro datar (flat, saturasi
   terkontrol, warna Naze), garis square (butt caps), emblem sprite pixel, dan UI pixel —
   BUKAN re-rasterisasi tile (tidak mungkin via style; tile tetap vector halus).
   Batasan ini dinyatakan terbuka agar tidak ada klaim palsu (aturan anti-fake SDD-010).
3. LANDMARK TERKURASI: dataset statis 6 landmark dengan koordinat terverifikasi sumber
   publik (dicatat di KDoc). Repository interface (pola baseline) → pure model, tanpa
   network. Tier prominence + clustering MapLibre; TIDAK ada POI generik (SDD-020 non-goal
   dipertahankan; pengecualian bounded ini adalah bagian dari CR yang disetujui).
4. EMBLEM KODE: emblem pixel didefinisikan sebagai grid ASCII + palet dalam kode Kotlin
   (murni, testable, tanpa binary asset yang tak bisa direview); renderer Bitmap untuk
   sprite peta + Canvas Compose untuk UI. Emoji dilarang.
5. DOCK MENGAMBANG: mengganti NavigationBar Scaffold dengan overlay dock collapsible;
   NazeTab & kontrak onSelect dipertahankan; layar non-peta diberi padding bawah.
6. GRATICULE KODE: digenerate di kode (interval adaptif per zoom, ekuator + meridian
   utama ditonjolkan) pada proyeksi Web Mercator yang sama. Kontur elevasi TIDAK
   dibuat: OpenFreeMap/OpenMapTiles tidak menyediakan data elevasi — dicatat sebagai
   data-unavailable, bukan fake (sesuai gate E "jika data tersedia").

## Alternatives Considered
- Ganti engine (Mapbox/MapTiler berbayar) — ditolak: melanggar prinsip keyless & anti-chaos.
- Raster pixel world map statis (gambar peta dunia) — ditolak: bukan data geografis nyata,
  tidak bisa zoom ke lokal, melanggar gate A "data geografis nyata".
- Tile raster re-render offline — ditolak: infrastruktur build berat di luar scope.
- Font/sprite OpenFreeMap untuk landmark — ditolak: tidak ada sprite kategori yang sesuai;
  emblem orisinal adalah requirement.

## Consequences
- Style JSON harus dirawat per tema (light/dark) — duplikasi disiplin warna.
- Label teks bergantung glyphs OpenFreeMap (Noto Sans Regular) — risiko ketersediaan
  pihak ketiga, konsisten dengan risiko yang sudah tercatat di SDD-020.
- Klik landmark perlu queryRenderedFeatures — kompleksitas UI naik sedikit.
- Kompas & satellite tetap berfungsi; overlay route/location dot tidak berubah.
- Verifikasi: CI build+test, unit test data/graticule/emblem, regression manual device
  (12 fitur + checklist WMAP) — runtime device TIDAK bisa diverifikasi dari sandbox.

## Status
APPROVED (2026-10-16, change request redesain)
