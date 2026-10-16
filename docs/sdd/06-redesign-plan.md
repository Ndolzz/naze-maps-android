# NAZE MAPS — Pixel-Art Redesign Implementation Plan (SDD-055)

- ID: SDD-055
- Title: Pixel-Art World Map Redesign Plan
- Status: IN PROGRESS (branch feature/pixel-art-worldmap)
- Spec: FEAT-WMAP; ADR: ADR-006

Pola per task (sama dengan SDD-050): Spec → implementasi minimal → test → CI →
regression manual device → update status.

- WMAP-T1 Specification & ADR — DONE (commit ini): FEAT-WMAP, ADR-006, update SDD-020,
  update FEAT-MAP (cross-ref).
- WMAP-T2 Landmark dataset + repository + unit tests — TODO.
- WMAP-T3 Emblem system (grid murni + renderer) + unit tests — TODO.
- WMAP-T4 Pixel style assets (light/dark) + loader (MapStyle, MapLibreMapView JSON) — TODO.
- WMAP-T5 Graticule + world map overlay (landmark layers, cluster, graticule) + tests — TODO.
- WMAP-T6 UI: floating dock, MapScreen integration, panel landmark, toggle graticule,
  VM state slice landmark + tests — TODO.
- WMAP-T7 Verification: CI hijau; regression manual device 12 fitur + checklist WMAP
  (PENDING — tidak bisa dijalankan dari sandbox; wajib dijalankan di perangkat).

## Regression manual device (WAJIB sebelum merge — checklist)
1. 12 fitur baseline (lihat SDD-000 §5) tetap berfungsi di atas style pixel.
2. Landmark tampil bertingkat per zoom; tap → panel; rute/favorit/share bekerja.
3. Graticule toggle + interval adaptif; ekuator/meridian utama ditonjolkan.
4. Dock: 4 tab + ciut; peta tidak tertutup; FAB tidak tertabrak dock.
5. Responsivitas HP kecil (360dp) & desktop/tablet.
6. Performa perangkat kelas rendah: pan/zoom tanpa jank mencolok.
