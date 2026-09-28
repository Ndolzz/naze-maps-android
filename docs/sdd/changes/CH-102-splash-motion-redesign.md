# CH-102 — Splash redesign: "Map Comes Alive" (SPEC, belum diimplementasi)

- ID: CH-102
- Type: UI/UX REDESIGN (BUG-018)
- Status: IMPLEMENTED (commit di branch sdd/phase3-ui; CI + regression manual pending)

## Implementation notes (final)
- File baru: app/src/main/java/com/naze/maps/ui/components/SplashOverlay.kt — presentasi only.
- Splash one-shot di MapScreen (splashDismissed state), isReady = mapStyle != null (real readiness signal, bukan timer). Bila ready lebih cepat dari animasi, splash dismiss segera (fade 450ms) — user tidak pernah menunggu art. Bila init lambat, splash diam di frame akhir (statis, berlabel) — tanpa loop.
- MapLoadingOverlay tetap untuk reload style berikutnya; kondisinya kini `mapStyle == null && splashDismissed` agar tidak menumpuk dengan splash first-load.
- Native SplashScreen API (Theme.NazeMaps.Splash di manifest) tidak diubah — tetap menangani cold-start instan pre-Compose.
- Motion: SATU progress Animatable 900ms (FastOutSlowIn): route-line mask/reveal (clipRect, alpha 0.35, NazeRoute) + location dot + marker tujuan; mark fade + scale 0.92→1.0 (tanpa rotasi); typography "NAZE MAPS" + tagline fade-in. Tanpa spinner/particle/neon/3D/loop.

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
