package com.naze.maps.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naze.maps.data.SettingsDataStore
import com.naze.maps.data.SettingsDataStoreImpl
import com.naze.maps.favorites.FavoriteEntity
import com.naze.maps.favorites.FavoritesRepository
import com.naze.maps.favorites.FavoritesRepositoryImpl
import com.naze.maps.history.HistoryEntity
import com.naze.maps.history.HistoryRepository
import com.naze.maps.history.HistoryRepositoryImpl
import com.naze.maps.location.CompassRepository
import com.naze.maps.location.CompassRepositoryImpl
import com.naze.maps.location.LocationRepository
import com.naze.maps.location.LocationRepositoryImpl
import com.naze.maps.location.NazeLocation
import com.naze.maps.location.PermissionUtils
import com.naze.maps.network.ConnectivityObserver
import com.naze.maps.network.ConnectivityObserverImpl
import com.naze.maps.routing.OsrmRoute
import com.naze.maps.routing.RouteOutcome
import com.naze.maps.routing.RoutingProfile
import com.naze.maps.routing.RoutingRepository
import com.naze.maps.routing.RoutingRepositoryImpl
import com.naze.maps.search.NominatimResult
import com.naze.maps.search.SearchOutcome
import com.naze.maps.search.SearchRepository
import com.naze.maps.search.SearchRepositoryImpl
import com.naze.maps.utils.DistanceUnit
import com.naze.maps.utils.GeoPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MapBanner {
    object GpsOff : MapBanner()
    object PermissionDenied : MapBanner()
    object NoInternet : MapBanner()
    data class Generic(val message: String) : MapBanner()
}

/**
 * TASK-010 (ADR-003): MapUiState is split per feature so each screen only depends on the
 * slice of state it actually renders, and every update site names the feature it touches.
 * Pure reshuffle — no behavior change.
 */
data class LocationState(
    val myLocation: NazeLocation? = null,
    val headingDegrees: Float? = null,
    val isTrackingMe: Boolean = false,
)

data class SearchState(
    val query: String = "",
    val results: List<NominatimResult> = emptyList(),
    val isSearching: Boolean = false,
)

data class RouteState(
    val selectedPlace: NominatimResult? = null,
    val activeRoute: OsrmRoute? = null,
)

data class MapViewState(
    val isSatelliteOn: Boolean = false,
)

data class SettingsState(
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val isDarkTheme: Boolean = true,
)

data class MapUiState(
    val location: LocationState = LocationState(),
    val search: SearchState = SearchState(),
    val route: RouteState = RouteState(),
    val map: MapViewState = MapViewState(),
    val settings: SettingsState = SettingsState(),
    val banner: MapBanner? = null,
)

/**
 * TASK-009 (TD-ARCH-2): all collaborators are now constructor-injected with sensible
 * defaults built from the Application, so production wiring is unchanged
 * (AndroidViewModelFactory still works via the @JvmOverloads single-arg constructor),
 * while unit tests can pass fakes for every dependency. No behavior change.
 * TASK-010b: collaborator types are now hand-rolled interfaces (Impl suffix = production
 * wiring), so unit tests can pass fakes without any Android framework.
 */
class MapViewModel @JvmOverloads constructor(
    application: Application,
    private val locationRepo: LocationRepository = LocationRepositoryImpl(application),
    private val compassRepo: CompassRepository = CompassRepositoryImpl(application),
    private val searchRepo: SearchRepository = SearchRepositoryImpl(),
    private val routingRepo: RoutingRepository = RoutingRepositoryImpl(),
    private val favoritesRepo: FavoritesRepository = FavoritesRepositoryImpl(application),
    private val historyRepo: HistoryRepository = HistoryRepositoryImpl(application),
    private val settings: SettingsDataStore = SettingsDataStoreImpl(application),
    private val connectivity: ConnectivityObserver = ConnectivityObserverImpl(application),
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    val favorites = favoritesRepo.observeFavorites()
    val history = historyRepo.observeRecent()

    private var trackingJob: Job? = null
    private var compassJob: Job? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            settings.isDarkTheme.collect { dark -> _uiState.update { it.copy(settings = it.settings.copy(isDarkTheme = dark)) } }
        }
        viewModelScope.launch {
            settings.distanceUnit.collect { unit -> _uiState.update { it.copy(settings = it.settings.copy(distanceUnit = unit)) } }
        }
        viewModelScope.launch {
            connectivity.observe().collect { online ->
                _uiState.update { state ->
                    when {
                        online -> if (state.banner == MapBanner.NoInternet) state.copy(banner = null) else state
                        else -> if (state.banner == null) state.copy(banner = MapBanner.NoInternet) else state
                    }
                }
            }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startTracking()
        } else {
            _uiState.update { it.copy(banner = MapBanner.PermissionDenied) }
        }
    }

    fun startTracking() {
        val app = getApplication<Application>()
        if (!PermissionUtils.hasLocationPermission(app)) {
            _uiState.update { it.copy(banner = MapBanner.PermissionDenied) }
            return
        }
        if (!PermissionUtils.isLocationServiceEnabled(app)) {
            _uiState.update { it.copy(banner = MapBanner.GpsOff) }
            return
        }
        _uiState.update { it.copy(location = it.location.copy(isTrackingMe = true), banner = null) }
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            locationRepo.observeLocation().collect { loc ->
                _uiState.update { it.copy(location = it.location.copy(myLocation = loc)) }
            }
        }
        startCompass()
    }

    fun stopTracking() {
        trackingJob?.cancel()
        compassJob?.cancel()
        _uiState.update { it.copy(location = it.location.copy(isTrackingMe = false)) }
    }

    private fun startCompass() {
        if (!compassRepo.isAvailable) return
        compassJob?.cancel()
        compassJob = viewModelScope.launch {
            compassRepo.observeHeading().collect { heading ->
                _uiState.update { it.copy(location = it.location.copy(headingDegrees = heading)) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(search = it.search.copy(query = query)) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(search = it.search.copy(results = emptyList(), isSearching = false)) }
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(search = it.search.copy(isSearching = true)) }
            kotlinx.coroutines.delay(500) // TASK-011: debounce >= 500ms sesuai policy penggunaan Nominatim
            when (val outcome = searchRepo.search(query)) {
                is SearchOutcome.Success -> _uiState.update {
                    it.copy(search = it.search.copy(results = outcome.results, isSearching = false), banner = null)
                }
                SearchOutcome.Empty -> _uiState.update {
                    it.copy(search = it.search.copy(results = emptyList(), isSearching = false))
                }
                SearchOutcome.NoInternet -> _uiState.update {
                    it.copy(search = it.search.copy(isSearching = false), banner = MapBanner.NoInternet)
                }
                is SearchOutcome.Error -> _uiState.update {
                    it.copy(search = it.search.copy(isSearching = false), banner = MapBanner.Generic(outcome.message))
                }
            }
        }
    }

    fun selectPlace(place: NominatimResult) {
        _uiState.update { it.copy(route = it.route.copy(selectedPlace = place), search = it.search.copy(results = emptyList(), query = "")) }
        viewModelScope.launch {
            historyRepo.record(place.mainText, place.subText, place.latitude, place.longitude)
        }
    }

    /** Re-selects a place straight from history, without hitting Nominatim again. */
    fun selectHistoryEntry(entry: HistoryEntity) {
        val place = NominatimResult(
            placeId = entry.id,
            displayName = if (entry.address.isNotBlank()) entry.name + ", " + entry.address else entry.name,
            lat = entry.latitude.toString(),
            lon = entry.longitude.toString(),
        )
        _uiState.update { it.copy(route = it.route.copy(selectedPlace = place), search = it.search.copy(results = emptyList())) }
    }

    /**
     * CH-106: selects a favorite as the current place, without hitting Nominatim.
     * Same pattern as selectHistoryEntry.
     */
    fun selectFavoriteAsPlace(favorite: FavoriteEntity) {
        val place = NominatimResult(
            placeId = favorite.id,
            displayName = if (favorite.address.isNotBlank()) favorite.name + ", " + favorite.address else favorite.name,
            lat = favorite.latitude.toString(),
            lon = favorite.longitude.toString(),
        )
        _uiState.update { it.copy(route = it.route.copy(selectedPlace = place), search = it.search.copy(results = emptyList(), query = "")) }
    }

    /** CH-106: one tap from the favorites list — select the place, then request a driving route. */
    fun routeFromFavorite(favorite: FavoriteEntity) {
        selectFavoriteAsPlace(favorite)
        requestRoute(GeoPoint(favorite.latitude, favorite.longitude), RoutingProfile.DRIVING)
    }

    fun deleteHistoryEntry(entry: HistoryEntity) {
        viewModelScope.launch { historyRepo.remove(entry) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepo.clear() }
    }

    fun clearSelection() {
        _uiState.update { it.copy(route = it.route.copy(selectedPlace = null, activeRoute = null)) }
    }

    fun requestRoute(to: GeoPoint, profile: RoutingProfile) {
        // TASK-008a (BUG-002): tell the user to enable My Location instead of failing silently.
        val from = _uiState.value.location.myLocation
        if (from == null) {
            _uiState.update {
                it.copy(banner = MapBanner.Generic("Lokasi saya belum tersedia — aktifkan My Location dulu"))
            }
            return
        }
        viewModelScope.launch {
            when (val outcome = routingRepo.getRoute(
                GeoPoint(from.latitude, from.longitude), to, profile,
            )) {
                is RouteOutcome.Success -> _uiState.update {
                    it.copy(route = it.route.copy(activeRoute = outcome.routes.first()), banner = null)
                }
                RouteOutcome.NoInternet -> _uiState.update { it.copy(banner = MapBanner.NoInternet) }
                RouteOutcome.NoRouteFound -> _uiState.update {
                    it.copy(banner = MapBanner.Generic("Rute tidak ditemukan"))
                }
                is RouteOutcome.Error -> _uiState.update {
                    it.copy(banner = MapBanner.Generic(outcome.message))
                }
            }
        }
    }

    fun saveFavorite(name: String, address: String, lat: Double, lng: Double) {
        viewModelScope.launch { favoritesRepo.save(name, address, lat, lng) }
    }

    fun deleteFavoriteFromScreen(favorite: FavoriteEntity) {
        viewModelScope.launch { favoritesRepo.delete(favorite) }
    }

    fun renameFavorite(favorite: FavoriteEntity, newName: String) {
        viewModelScope.launch { favoritesRepo.rename(favorite, newName) }
    }

    /**
     * TASK-007 (TD-ARCH-3): single geocoding entry point for the Distance screen.
     */
    suspend fun geocodeOne(query: String): NominatimResult? = searchRepo.geocodeOne(query)

    fun dismissBanner() {
        _uiState.update { it.copy(banner = null) }
    }

    fun toggleSatellite() {
        _uiState.update { it.copy(map = it.map.copy(isSatelliteOn = !it.map.isSatelliteOn)) }
    }

    fun toggleTheme() {
        viewModelScope.launch { settings.setDarkTheme(!_uiState.value.settings.isDarkTheme) }
    }

    fun setDistanceUnit(unit: DistanceUnit) {
        viewModelScope.launch { settings.setDistanceUnit(unit) }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
        compassJob?.cancel()
    }
}
