# NAZE MAPS — Pixel-Art Redesign Implementation Plan (SDD-055)

- ID: SDD-055
- Title: Pixel-Art World Map Redesign Plan
- Status: IN PROGRESS (branch feature/pixel-art-worldmap; T1–T6 DONE, T7 PENDING)
- Spec: FEAT-WMAP; ADR: ADR-006

Pola per task (sama dengan SDD-050): Spec → implementasi minimal → test → CI →
regression manual device → update status.

- WMAP-T1 Specification & ADR — DONE (050e8ba): FEAT-WMAP, ADR-006, update SDD-020,
  update FEAT-MAP (cross-ref), plan ini.
- WMAP-T2 Landmark dataset + repository + unit tests — DONE (a4d988b): Landmark,
  LandmarkCategory, ProminenceTier (minZoom 0/4/8), LandmarkRepository + Impl,
  6 landmark koordinat terverifikasi, test repository.
- WMAP-T3 Emblem system (grid murni + renderer) + unit tests — DONE (4a61907):
  PixelEmblem/PixelEmblems (19 emblem 12×12), EmblemBitmapFactory, PixelPalette.
- WMAP-T4 Pixel style assets (light/dark) + loader (MapStyle, MapLibreMapView JSON)
  — DONE (50a67a1): assets/styles/naze-pixel-{light,dark}.json, konstanta pixel di
  MapStyle, fromJson di MapLibreMapView, test PixelStyleTest.
- WMAP-T5 Graticule + world map overlay (landmark layers, cluster, graticule) + tests
  — DONE (50a67a1): Graticule (interval 10/5/1° per zoom), WorldMapOverlay
  (tier symbol layers, clustering clusterMaxZoom 4, graticule layers + toggle),
  test GraticuleTest.
- WMAP-T6 UI: floating dock, MapScreen integration, panel landmark, toggle graticule,
  VM state slice landmark + tests — DONE (bc84161, 3cf6148, c84704c): MapLibreMapView
  styleJson/styleKey + guard re-apply, MapScreen (tap landmark, panel info, legend,
  FAB graticule), PixelEmblemIcon (Compose), NazeFloatingDock pill collapsible,
  NazeTab emblem, MapViewModel landmark slice + toggleGraticule, +4 test MapViewModel.
- WMAP-T7 Verification: CI hijau; regression manual device 12 fitur + checklist WMAP
  (PENDING — CI dijalankan via draft PR ke main; regression manual device TIDAK
  BISA dijalankan dari sandbox dan wajib dijalankan di perangkat sebelum merge).

## Regression manual device (WAJIB sebelum merge — checklist)
1. 12 fitur baseline (lihat SDD-000 §5) tetap berfungsi di atas style pixel.
2. Landmark tampil bertingkat per zoom; tap → panel; rute/favorit/share bekerja.
3. Graticule toggle + interval adaptif; ekuator/meridian utama ditonjolkan.
4. Dock: 4 tab + ciut; peta tidak tertutup; FAB tidak tertabrak dock.
5. Responsivitas HP kecil (360dp) & desktop/tablet.
6. Performa perangkat kelas rendah: pan/zoom tanpa jank mencolok.
