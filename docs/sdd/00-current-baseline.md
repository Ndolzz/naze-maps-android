# NAZE MAPS — Current Baseline (SDD-000)

- ID: SDD-000
- Title: Current Baseline
- Status: APPROVED (audit 2026-09-27)
- Basis commit: a8a7598e61b42707937f9f83e18f2a25ebc80439 (branch: main, satu-satunya branch)

Dokumen ini mendeskripsikan kondisi AKTUAL project. Label:
- CONFIRMED = terbaca langsung dari kode/konfigurasi
- INFERRED = kesimpulan dari bukti tidak langsung
- UNKNOWN = tidak dapat diverifikasi pada tahap audit

## 1. Project Overview

Aplikasi peta Android native bernama Naze Maps (applicationId `com.naze.maps`), dibangun dengan Jetpack Compose + MapLibre Native. Porting dari PWA sebelumnya. README.md menyatakan status "scaffold fungsional" dan awalnya "belum pernah dicompile"; setelahnya ada commit perbaikan ("fix bugs + fitur lokasi & riwayat", "Fix: Add API level check..."). Kondisi runtime saat ini: UNKNOWN (tidak ada emulator/device dalam audit ini).

## 2. Current Modules

- CONFIRMED: Single-module. `settings.gradle.kts` hanya `include(":app")`. Tidak ada modul :domain/:data/:feature.

## 3. Current Package Structure (CONFIRMED)

Semua kode di `app/src/main/java/com/naze/maps/`:

| Package | Isi | Peran nyata |
|---|---|---|
| (root) | MainActivity, NazeMapsApp | Entry point + Application (init MapLibre) |
| ui.screens | MapScreen, DistanceScreen, FavoritesScreen, SettingsScreen, MapViewModel | UI + seluruh state app |
| ui.components | SearchBar, MapFabs, ErrorBanner, MapLoadingOverlay, BottomNavBar | Komponen Compose reusable |
| ui.navigation | NazeNavHost | Tab switcher state-based (BUKAN Navigation-Compose) |
| ui.theme | Color, Theme, Type | Material3 theming |
| map | MapLibreMapView (interop AndroidView), MapOverlay (route line, location dot, satellite), MapStyle | Wrapper MapLibre |
| location | LocationRepository (FusedLocation), CompassRepository (rotation vector), PermissionUtils | GPS + heading |
| search | NominatimApi, SearchRepository, SearchResult | Geocoding/search |
| routing | OsrmApi, RoutingRepository, RouteResult | Rute OSRM |
| favorites | Room DB + DAO + Repository | Persist favorit |
| history | Room DB + DAO + Repository (terpisah dari favorites) | Persist riwayat pencarian |
| data | SettingsDataStore | DataStore (tema, satuan) |
| network | NetworkModule (OkHttp/Retrofit singleton), ConnectivityObserver (TIDAK terpakai) | Network layer |
| utils | DistanceUtils (haversine, GeoPoint, TravelMode, DistanceUnit) | Util + model dasar |

Total 41 file Kotlin. Tidak ada test source set (`app/src/test`, `app/src/androidTest` TIDAK ADA) — CONFIRMED.

## 4. Current Architecture (analisis nyata, bukan asumsi nama folder)

- CONFIRMED: MVVM-lite, satu ViewModel (`MapViewModel : AndroidViewModel`) memegang SATU monolitik `MapUiState` (12 field) di `MutableStateFlow`, di-share ke semua screen via `viewModel()` activity-scoped.
- CONFIRMED: Layering: Compose UI → MapViewModel → Repository (search/routing/location/compass/favorites/history) → DataSource (Retrofit API / Room DAO / FusedLocation / Sensor). TIDAK ADA layer Domain/UseCase. Repository adalah repository pattern nyata (class dengan data source di belakangnya), bukan hanya nama.
- CONFIRMED: Tidak ada DI framework. Semua dependency dibangun manual di dalam MapViewModel (dan DistanceScreen membuat SearchRepository sendiri).
- CONFIRMED: Navigation = state-based tab switcher (`when(selectedTab)`), dependency navigation-compose ada tapi tidak dipakai.
- CONFIRMED: Map = MapLibre MapView (View, bukan Compose) di-host via AndroidView, lifecycle di-forward manual via LifecycleEventObserver.

## 5. Main Features (CONFIRMED dari kode)

1. Peta MapLibre (style OpenFreeMap liberty/dark, pan/zoom/rotate)
2. My Location: permission (accompanist), tracking FusedLocation, dot + recenter kamera
3. Compass: rotation vector sensor, FAB rotasi + reset north (auto-hide bila sensor tidak ada)
4. Search Nominatim: debounce 350ms, hasil dropdown, bottom sheet detail
5. Riwayat pencarian: Room, dedupe by koordinat, trim 30, tampilan saat query kosong
6. Routing OSRM (driving/foot/bike) → garis rute di peta
7. Favorites: Room, save/delete (rename ada di VM tapi TIDAK ada UI-nya)
8. Distance calculator multi-titik (haversine, "lokasi saya" sebagai titik, ETA 3 mode)
9. Settings: tema dark/light (DataStore), satuan KM/MI
10. Satellite layer (Esri World Imagery, hide fill layers saat on)
11. Error banner (GPS off / permission / no internet / generic) + deep-link ke Settings
12. Share lokasi via Intent (OSM URL)

## 6. Main Dependencies (CONFIRMED, app/build.gradle.kts)

Compose BOM 2024.06.00, Kotlin 1.9.24, AGP 8.5.2, KSP 1.9.24-1.0.20, compileSdk/targetSdk 34, minSdk 24, JVM 17, MapLibre android-sdk 11.5.2, play-services-location 21.3.0, Retrofit 2.11.0 + Gson, OkHttp 4.12.0 (+logging), Room 2.6.1 (KSP), DataStore 1.1.1, Accompanist permissions 0.34.0, navigation-compose 2.7.7 (TIDAK DIPAKAI), material 1.12.0, core-splashscreen 1.0.1. Test: junit 4.13.2, androidx.test.ext junit 1.2.1 (deklarasi saja, tidak ada test).

## 7. Data Flow (CONFIRMED)

Peta/track: FusedLocation callbackFlow → LocationRepository → MapViewModel.collect → MapUiState.myLocation → MapScreen LaunchedEffect → updateLocationDot + recenter kamera.
Search: SearchBar onTextChange → VM debounce 350ms → SearchRepository → NominatimApi (Retrofit) → SearchOutcome → uiState.searchResults → dropdown → selectPlace → history.record + bottom sheet.
Route: bottom sheet → requestRoute → RoutingRepository → OSRM → RouteOutcome → uiState.activeRoute → MapScreen → Style.updateRouteLine.
Settings: DataStore flow → VM init collect → uiState (isDarkTheme dipakai MainActivity untuk theming + MapScreen untuk style URL).

## 8. Navigation Flow (CONFIRMED)

MainActivity → NazeMapsTheme → NazeNavHost: Scaffold + NazeBottomNav; `when(selectedTab)` — MAP | FAVORITES | DISTANCE | SETTINGS. Tab tidak preserve composition state (MapScreen dibongkar saat pindah tab) — implikasi lihat bugs.md BUG-001.

## 9. API Flow (CONFIRMED)

NetworkModule: singleton OkHttpClient (UA "NazeMaps-Android/1.0", timeout 10s) + Retrofit builder. Nominatim: GET search (base https://nominatim.openstreetmap.org/, tanpa param limit/accept-language — per NominatimApi.kt). OSRM: GET route/v1 (https://router.project-osrm.org/, overview=full, geojson, alternatives=true).

## 10. Location Flow (CONFIRMED)

PermissionUtils (fine/coarse) + isLocationEnabled → startTracking: cek permission → cek GPS on → collect observeLocation (PRIORITY_HIGH_ACCURACY, interval 3s) + compass flow. Permission request via accompanist di MapScreen. Tidak ada foreground service; tracking berhenti via stopTracking/onCleared.

## 11. Map Flow (CONFIRMED)

MapLibreMapView factory: MapView.onCreate + getMapAsync + setStyle(styleUrl). Update block: setStyle ulang jika uri beda (theme switch). MapOverlay.kt mengelola 3 pasang source/layer: route line, location dot, satellite raster (index 0 + hide Background/Fill/FillExtrusion). Overlay di-reapply via LaunchedEffect(style) karena setStyle menghapus layer custom.

## 12. Known Bugs → lihat docs/sdd/bugs.md (BUG-001 s.d. BUG-013).

## 13. Potential Bugs (belum terkonfirmasi runtime) → bugs.md bagian "Potential".

## 14. Technical Debt → docs/sdd/technical-debt.md.

## 15. Testing Status

- CONFIRMED: Tidak ada satu pun test (tidak ada direktori test). Coverage = 0%.

## 16. Build Status

- CONFIRMED: CI ada (.github/workflows/android-build.yml): push/PR main, JDK 17, setup-android v3 (packages kosong), setup-gradle v4 gradle 8.7, assembleDebug + assembleRelease (unsigned), upload artifact. Gradle wrapper properties ada (8.7) TAPI `gradlew`, `gradlew.bat`, dan `gradle-wrapper.jar` TIDAK ada di repo.
- UNKNOWN: apakah CI terakhir sukses (status workflow run tidak dapat diverifikasi saat audit).
- CONFIRMED: Build lokal tidak bisa jalan tanpa `gradle wrapper` manual (jar binary tidak di-commit).

## 17. Architectural Risks (ringkas; detail di technical-debt.md)

1. Monolitik MapUiState + satu ViewModel = god-object, semua fitur coupled via state yang sama. (CONFIRMED)
2. Tidak ada DI → test sulit, coupling ViewModel↔konkrete repository. (CONFIRMED)
3. Tidak ada test → setiap refactor = blind. (CONFIRMED)
4. View-interop MapView + lifecycle manual → risiko leak GL surface. (CONFIRMED pola, INFERRED dampak)
5. DistanceScreen membuat SearchRepository sendiri → bypass arsitektur, duplikasi. (CONFIRMED)
6. Ketergantungan service publik gratis (Nominatim/OSRM demo/OpenFreeMap/Esri) tanpa fallback/rate-limit guard. (CONFIRMED)
