# NAZE MAPS — Testing Strategy (SDD-040)

- ID: SDD-040
- Title: Testing Strategy
- Status: PROPOSED

## Kondisi saat ini

CONFIRMED: 0 test. Depedency junit 4.13.2 & androidx.test.ext ada tapi tidak ada file test.

## Test pyramid target

1. **Unit tests** (prioritas, cepat, tanpa emulator):
   - utils/DistanceUtils (haversine, format KM/MI, eta) — murni, gampang.
   - search/SearchRepository & routing/RoutingRepository: sealed outcome mapping, dengan Retrofit MockWebServer (tambah dependency test).
   - favorites/history repository logic (Room in-memory; tambakan room-testing / Robolectric bila perlu).
   - MapViewModel state logic: debounce search, startTracking guard (perlu konstruktor injection dulu — lihat TASK-009).
2. **Integration tests**: NetworkModule (header UA, timeout), DAO queries, DataStore settings round-trip.
3. **UI tests** (Compose test, emulator): banner error muncul, search flow, favorites empty-state. Mahal — hanya alur kritis.
4. **Regression tests**: manual checklist per rilis (fitur 12 baseline) sampai UI test menutupi.
5. **Architecture tests**: konsistensi package dependency (opsional: konsist/ArchUnit-like bisa manual via lint check di CI).

## Prioritas area risiko tinggi (dari audit)

| Area | Risiko | Test pertama yang wajib |
|---|---|---|
| State update race (MapUiState) | lost updates | unit test concurrent update (setelah refactor ke update{}) |
| Search debounce + outcome mapping | regresi UX + 403 Nominatim | unit test dengan fake repo |
| Routing outcome → banner/line | regresi rute | unit test outcome mapping |
| Haversine/ETA correctness | angka salah | characterization test nilai PWA |
| History dedupe/trim | data hilang/ganda | DAO test |
| MapView lifecycle | leak GL surface | manual + UI test bila memungkinkan |

## Characterization tests (prasyarat refactor)

Sebelum TASK boundary/isolasi, tulis test yang membekukan perilaku saat ini (termasuk bug yang diketahui, diberi tanda @KnownIssue).

## CI

Tambah step `gradle test` ke android-build.yml setelah test pertama ada (task terpisah, via change request).
