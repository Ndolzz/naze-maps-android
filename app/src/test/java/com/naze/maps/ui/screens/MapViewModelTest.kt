package com.naze.maps.ui.screens

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.naze.maps.data.SettingsDataStore
import com.naze.maps.data.ThemeMode
import com.naze.maps.favorites.FavoriteEntity
import com.naze.maps.favorites.FavoritesRepository
import com.naze.maps.history.HistoryEntity
import com.naze.maps.history.HistoryRepository
import com.naze.maps.location.CompassRepository
import com.naze.maps.location.LocationRepository
import com.naze.maps.location.NazeLocation
import com.naze.maps.network.ConnectivityObserver
import com.naze.maps.routing.RouteOutcome
import com.naze.maps.routing.RoutingProfile
import com.naze.maps.routing.RoutingRepository
import com.naze.maps.search.NominatimResult
import com.naze.maps.search.SearchOutcome
import com.naze.maps.search.SearchRepository
import com.naze.maps.utils.DistanceUnit
import com.naze.maps.utils.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * TASK-010b: unit tests for MapViewModel using constructor-injected fakes
 * (TASK-009/TASK-010b interfaces). Robolectric is only here to provide the
 * Application instance that AndroidViewModel requires.
 *
 * NOTE: application is overridden to plain android.app.Application because the
 * manifest's NazeMapsApp calls MapLibre.getInstance() in onCreate, and MapLibre's
 * native library cannot load on the JVM (UnsatisfiedLinkError).
 */

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], application = Application::class)
class MapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // ---- Fakes ----

    private class FakeLocationRepository : LocationRepository {
        val location = MutableStateFlow<NazeLocation?>(null)
        override suspend fun getCurrentLocationOnce(): NazeLocation? = location.value
        override fun observeLocation(intervalMs: Long): Flow<NazeLocation> = location.filterNotNull()
    }

    private class FakeCompassRepository : CompassRepository {
        override val isAvailable: Boolean = false
        override fun observeHeading(): Flow<Float> = flowOf()
    }

    private class FakeSearchRepository : SearchRepository {
        val queries = mutableListOf<String>()
        var outcome: SearchOutcome = SearchOutcome.Empty
        override suspend fun search(query: String): SearchOutcome {
            queries.add(query)
            return outcome
        }
        override suspend fun geocodeOne(query: String): NominatimResult? =
            (search(query) as? SearchOutcome.Success)?.results?.firstOrNull()
    }

    private class FakeRoutingRepository : RoutingRepository {
        val requests = mutableListOf<Pair<GeoPoint, GeoPoint>>()
        var outcome: RouteOutcome = RouteOutcome.NoRouteFound
        override suspend fun getRoute(from: GeoPoint, to: GeoPoint, profile: RoutingProfile): RouteOutcome {
            requests.add(from to to)
            return outcome
        }
    }

    private class FakeFavoritesRepository : FavoritesRepository {
        val saved = mutableListOf<FavoriteEntity>()
        private val favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
        override fun observeFavorites(): Flow<List<FavoriteEntity>> = favorites
        override suspend fun save(name: String, address: String, lat: Double, lng: Double) {
            saved.add(FavoriteEntity(name = name, address = address, latitude = lat, longitude = lng))
        }
        override suspend fun rename(favorite: FavoriteEntity, newName: String) = Unit
        override suspend fun delete(favorite: FavoriteEntity) = Unit
        override suspend fun isSaved(lat: Double, lng: Double): Boolean = false
    }

    private class FakeHistoryRepository : HistoryRepository {
        val recorded = mutableListOf<HistoryEntity>()
        private val history = MutableStateFlow<List<HistoryEntity>>(emptyList())
        override fun observeRecent(): Flow<List<HistoryEntity>> = history
        override suspend fun record(name: String, address: String, lat: Double, lng: Double) {
            recorded.add(HistoryEntity(name = name, address = address, latitude = lat, longitude = lng))
        }
        override suspend fun remove(entry: HistoryEntity) = Unit
        override suspend fun clear() {
            recorded.clear()
        }
    }

    // BUG-022: FakeSettingsDataStore must implement the CH-111 themeMode members.
    private class FakeSettingsDataStore : SettingsDataStore {
        val darkTheme = MutableStateFlow(true)
        val mode = MutableStateFlow(ThemeMode.DARK)
        val unit = MutableStateFlow(DistanceUnit.KM)
        override val isDarkTheme: Flow<Boolean> = darkTheme
        override val themeMode: Flow<ThemeMode> = mode
        override val distanceUnit: Flow<DistanceUnit> = unit
        override suspend fun setDarkTheme(enabled: Boolean) { darkTheme.value = enabled }
        override suspend fun setThemeMode(mode: ThemeMode) { this.mode.value = mode }
        override suspend fun setDistanceUnit(unit: DistanceUnit) { this.unit.value = unit }
    }

    private class FakeConnectivityObserver : ConnectivityObserver {
        val online = MutableStateFlow(true)
        override fun isOnlineNow(): Boolean = online.value
        override fun observe(): Flow<Boolean> = online
    }

    // ---- Setup ----

    private val locationRepo = FakeLocationRepository()
    private val compassRepo = FakeCompassRepository()
    private val searchRepo = FakeSearchRepository()
    private val routingRepo = FakeRoutingRepository()
    private val favoritesRepo = FakeFavoritesRepository()
    private val historyRepo = FakeHistoryRepository()
    private val settings = FakeSettingsDataStore()
    private val connectivity = FakeConnectivityObserver()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createVm(): MapViewModel = MapViewModel(
        ApplicationProvider.getApplicationContext(),
        locationRepo,
        compassRepo,
        searchRepo,
        routingRepo,
        favoritesRepo,
        historyRepo,
        settings,
        connectivity,
    )

    private fun advance() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private val place = NominatimResult(
        placeId = 1L,
        displayName = "Monas, Jakarta",
        lat = "-6.1954",
        lon = "106.8229",
    )

    // ---- Tests ----

    @Test
    fun `going offline shows NoInternet banner, going back online clears it`() {
        connectivity.online.value = false
        val vm = createVm()
        advance()
        assertTrue(vm.uiState.value.banner is MapBanner.NoInternet)

        connectivity.online.value = true
        advance()
        assertNull(vm.uiState.value.banner)
    }

    @Test
    fun `other banners are not replaced by the offline banner, and offline does not clear them`() {
        // offline first
        connectivity.online.value = false
        val vm = createVm()
        advance()
        assertTrue(vm.uiState.value.banner is MapBanner.NoInternet)

        // a Generic banner wins (NoInternet is only set when no banner is showing)
        vm.dismissBanner()
        advance()
        vm.requestRoute(GeoPoint(-6.2, 106.8), RoutingProfile.DRIVING) // no location -> Generic banner
        advance()
        val banner = vm.uiState.value.banner
        assertTrue(banner is MapBanner.Generic)
    }

    @Test
    fun `search success populates results and stops the spinner`() {
        searchRepo.outcome = SearchOutcome.Success(listOf(place))
        val vm = createVm()
        advance()

        vm.onSearchQueryChange("monas")
        testDispatcher.scheduler.advanceTimeBy(500)
        advance()

        val search = vm.uiState.value.search
        assertFalse(search.isSearching)
        assertEquals(listOf(place), search.results)
    }

    @Test
    fun `blank query clears results immediately`() {
        searchRepo.outcome = SearchOutcome.Success(listOf(place))
        val vm = createVm()
        advance()

        vm.onSearchQueryChange("monas")
        testDispatcher.scheduler.advanceTimeBy(500)
        advance()
        assertEquals(listOf(place), vm.uiState.value.search.results)

        vm.onSearchQueryChange("")
        val search = vm.uiState.value.search
        assertTrue(search.results.isEmpty())
        assertFalse(search.isSearching)
    }

    @Test
    fun `search NoInternet outcome raises the NoInternet banner`() {
        searchRepo.outcome = SearchOutcome.NoInternet
        val vm = createVm()
        advance()

        vm.onSearchQueryChange("monas")
        testDispatcher.scheduler.advanceTimeBy(500)
        advance()

        assertTrue(vm.uiState.value.banner is MapBanner.NoInternet)
        assertFalse(vm.uiState.value.search.isSearching)
    }

    @Test
    fun `selectPlace selects the place, clears search and records history`() {
        val vm = createVm()
        advance()

        vm.selectPlace(place)
        advance()

        val state = vm.uiState.value
        assertEquals(place, state.route.selectedPlace)
        assertTrue(state.search.results.isEmpty())
        assertEquals("", state.search.query)
        assertEquals(1, historyRepo.recorded.size)
        assertEquals("Monas", historyRepo.recorded.first().name)
    }

    @Test
    fun `selectHistoryEntry re-selects a place without hitting search`() {
        val vm = createVm()
        advance()

        val entry = HistoryEntity(
            id = 7L,
            name = "Monas",
            address = "Jakarta",
            latitude = -6.1954,
            longitude = 106.8229,
        )
        vm.selectHistoryEntry(entry)

        val selected = vm.uiState.value.route.selectedPlace
        assertEquals("Monas", selected?.mainText)
        assertEquals("Jakarta", selected?.subText)
        assertTrue(searchRepo.queries.isEmpty()) // history re-selection never hits Nominatim
    }

    @Test
    fun `requestRoute without a location shows a banner and skips the routing repo`() {
        val vm = createVm()
        advance()

        vm.requestRoute(GeoPoint(-6.2, 106.8), RoutingProfile.DRIVING)
        advance()

        assertTrue(vm.uiState.value.banner is MapBanner.Generic)
        assertTrue(routingRepo.requests.isEmpty())
    }

    // Note: the "route with location" success path requires GPS tracking to be running,
    // which needs runtime permission + location service — that stays in manual device
    // regression (see docs/sdd/04-testing-strategy.md), not in Robolectric unit tests.

    @Test
    fun `toggleSatellite flips the map slice`() {
        val vm = createVm()
        advance()

        assertFalse(vm.uiState.value.map.isSatelliteOn)
        vm.toggleSatellite()
        assertTrue(vm.uiState.value.map.isSatelliteOn)
        vm.toggleSatellite()
        assertFalse(vm.uiState.value.map.isSatelliteOn)
    }

    // BUG-022: toggleTheme now writes ThemeMode (CH-111), not the legacy dark_theme flag.
    @Test
    fun `toggleTheme persists the opposite mode and updates the settings slice`() {
        val vm = createVm()
        advance()
        assertTrue(vm.uiState.value.settings.isDarkTheme) // fake default mode is DARK

        vm.toggleTheme()
        advance()
        assertEquals(ThemeMode.LIGHT, settings.mode.value)
        assertEquals(ThemeMode.LIGHT, vm.uiState.value.settings.themeMode)
        assertFalse(vm.uiState.value.settings.isDarkTheme)
    }

    // BUG-022: CH-111 regression test for the explicit theme picker.
    @Test
    fun `setThemeMode persists and isDarkTheme follows LIGHT and DARK`() {
        val vm = createVm()
        advance()
        assertTrue(vm.uiState.value.settings.isDarkTheme) // default DARK

        vm.setThemeMode(ThemeMode.LIGHT)
        advance()
        assertEquals(ThemeMode.LIGHT, settings.mode.value)
        assertFalse(vm.uiState.value.settings.isDarkTheme)

        vm.setThemeMode(ThemeMode.DARK)
        advance()
        assertEquals(ThemeMode.DARK, settings.mode.value)
        assertTrue(vm.uiState.value.settings.isDarkTheme)
    }

    @Test
    fun `setDistanceUnit persists and updates the settings slice`() {
        val vm = createVm()
        advance()
        assertEquals(DistanceUnit.KM, vm.uiState.value.settings.distanceUnit)

        vm.setDistanceUnit(DistanceUnit.MI)
        advance()
        assertEquals(DistanceUnit.MI, vm.uiState.value.settings.distanceUnit)
    }
}
