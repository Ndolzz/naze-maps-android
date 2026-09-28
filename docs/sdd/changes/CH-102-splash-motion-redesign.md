# CH-102 — Splash redesign: "Map Comes Alive" (SPEC, belum diimplementasi)

- ID: CH-102
- Type: UI/UX REDESIGN (BUG-018)
- Status: PROPOSED — implementation sebagai task terpisah setelah CH-100/101 diverifikasi.

## Current (evidence)
- Manifest: MainActivity theme Theme.NazeMaps.Splash.
- themes.xml: Theme.SplashScreen, background @color/naze_bg_dark, animatedIcon = ic_launcher_foreground (launcher icon), postSplashScreenTheme → Theme.NazeMaps.
- MainActivity: installSplashScreen() API 31+; tidak ada motion design; transisi langsung.

## Target
Konsep MAP COMES ALIVE: background → subtle map element (garis rute samar + titik lokasi, mask/reveal) → mark Naze reveal (fade + subtle scale 0.92→1.0) → typography "NAZE MAPS" fade-in → transisi mulus ke map.

## Motion rules
- Hanya: fade, subtle scale, mask/reveal, route-line reveal. TIDAK: rotasi 360°, spinner, partikel, neon, 3D, loop, flash, cinematic panjang.
- Durasi target total ≤ 900ms; tidak ada delay(3000).

## Startup logic (wajib)
Visual dan initialization dipisah. Splash selesai berdasarkan readiness (settings/DataStore/first map style), bukan timer. Jika init lebih cepat dari animasi → transisi pendek natural. States: INITIALIZING / READY / ERROR (fallback: langsung ke main screen dengan banner error, tanpa infinite animation).

## Implementation sketch (untuk task implementasi)
- Pertahankan native splash (system API) untuk cold-start instan; tambahkan Compose splash composable singkat sebagai brand layer di atas map screen selama state INITIALIZING (MapLoadingOverlay sudah ada — extend pattern ini), lalu fade-out.
- Tidak ada file/Activity baru yang menunda startup.

## Non-goals
- Splash tidak menyentuh map rendering, search, routing, location business logic.
