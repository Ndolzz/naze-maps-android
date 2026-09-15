package com.naze.maps.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.naze.maps.data.SettingsDataStore
import com.naze.maps.favorites.FavoritesRepository
import com.naze.maps.history.HistoryEntity
import com.naze.maps.history.HistoryRepository
import com.naze.maps.location.CompassRepository
import com.naze.maps.location.LocationRepository
import com.naze.maps.location.NazeLocation
import com.naze.maps.location.PermissionUtils
import com.naze.maps.routing.OsrmRoute
import com.naze.maps.routing.RouteOutcome
import com.naze.maps.routing.RoutingProfile
import com.naze.maps.routing.RoutingRepository
import com.naze.maps.search.NominatimResult
import com.naze.maps.search.SearchOutcome
import com.naze.maps.search.SearchRepository
import com.naze.maps.utils.DistanceUnit
import com.naze.maps.utils.GeoPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MapBanner {
    object GpsOff : MapBanner()
    object PermissionDenied : MapBanner()
    object NoInternet : MapBanner()
    data class Generic(val message: String) : MapBanner()
}

data class MapUiState(
    val myLocation: NazeLocation? = null,
    val headingDegrees: Float? = null,
    val isTrackingMe: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<NominatimResult> = emptyList(),
    val isSearching: Boolean = false,
    val selectedPlace: NominatimResult? = null,
    val activeRoute: OsrmRoute? = null,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val isDarkTheme: Boolean = true,
    val isSatelliteOn: Boolean = false,
    val banner: MapBanner? = null,
)

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepo = LocationRepository(application)
    private val compassRepo = CompassRepository(application)
    private val searchRepo = SearchRepository()
    private val routingRepo = RoutingRepository()
    private val favoritesRepo = FavoritesRepository(application)
    private val historyRepo = HistoryRepository(application)
    private val settings = SettingsDataStore(application)

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    val favorites = favoritesRepo.observeFavorites()
    val history = historyRepo.observeRecent()

    private var trackingJob: Job? = null
    private var compassJob: Job? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            settings.isDarkTheme.collect { dark -> _uiState.value = _uiState.value.copy(isDarkTheme = dark) }
        }
        viewModelScope.launch {
            settings.distanceUnit.collect { unit -> _uiState.value = _uiState.value.copy(distanceUnit = unit) }
        }
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startTracking()
        } else {
            _uiState.value = _uiState.value.copy(banner = MapBanner.PermissionDenied)
        }
    }

    fun startTracking() {
        val app = getApplication<Application>()
        if (!PermissionUtils.hasLocationPermission(app)) {
            _uiState.value = _uiState.value.copy(banner = MapBanner.PermissionDenied)
            return
        }
        if (!PermissionUtils.isLocationServiceEnabled(app)) {
            _uiState.value = _uiState.value.copy(banner = MapBanner.GpsOff)
            return
        }
        _uiState.value = _uiState.value.copy(isTrackingMe = true, banner = null)
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            locationRepo.observeLocation().collect { loc ->
                _uiState.value = _uiState.value.copy(myLocation = loc)
            }
        }
        startCompass()
    }

    fun stopTracking() {
        trackingJob?.cancel()
        compassJob?.cancel()
        _uiState.value = _uiState.value.copy(isTrackingMe = false)
    }

    private fun startCompass() {
        if (!compassRepo.isAvailable) return
        compassJob?.cancel()
        compassJob = viewModelScope.launch {
            compassRepo.observeHeading().collect { heading ->
                _uiState.value = _uiState.value.copy(headingDegrees = heading)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        searchJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            kotlinx.coroutines.delay(350) // light debounce so we don't hit Nominatim on every keystroke
            when (val outcome = searchRepo.search(query)) {
                is SearchOutcome.Success -> _uiState.value =
                    _uiState.value.copy(searchResults = outcome.results, isSearching = false, banner = null)
                SearchOutcome.Empty -> _uiState.value =
                    _uiState.value.copy(searchResults = emptyList(), isSearching = false)
                SearchOutcome.NoInternet -> _uiState.value =
                    _uiState.value.copy(isSearching = false, banner = MapBanner.NoInternet)
                is SearchOutcome.Error -> _uiState.value =
                    _uiState.value.copy(isSearching = false, banner = MapBanner.Generic(outcome.message))
            }
        }
    }

    fun selectPlace(place: NominatimResult) {
        _uiState.value = _uiState.value.copy(selectedPlace = place, searchResults = emptyList(), searchQuery = "")
        viewModelScope.launch {
            historyRepo.record(place.mainText, place.subText, place.latitude, place.longitude)
        }
    }

    /** Re-selects a place straight from history, without hitting Nominatim again. */
    fun selectHistoryEntry(entry: HistoryEntity) {
        val place = NominatimResult(
            placeId = entry.id,
            displayName = if (entry.address.isNotBlank()) "${entry.name}, ${entry.address}" else entry.name,
            lat = entry.latitude.toString(),
            lon = entry.longitude.toString(),
        )
        _uiState.value = _uiState.value.copy(selectedPlace = place, searchResults = emptyList())
    }

    fun deleteHistoryEntry(entry: HistoryEntity) {
        viewModelScope.launch { historyRepo.remove(entry) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepo.clear() }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedPlace = null, activeRoute = null)
    }

    fun requestRoute(to: GeoPoint, profile: RoutingProfile) {
        val from = _uiState.value.myLocation ?: return
        viewModelScope.launch {
            when (val outcome = routingRepo.getRoute(
                GeoPoint(from.latitude, from.longitude), to, profile,
            )) {
                is RouteOutcome.Success -> _uiState.value =
                    _uiState.value.copy(activeRoute = outcome.routes.first(), banner = null)
                RouteOutcome.NoInternet -> _uiState.value = _uiState.value.copy(banner = MapBanner.NoInternet)
                RouteOutcome.NoRouteFound -> _uiState.value =
                    _uiState.value.copy(banner = MapBanner.Generic("Rute tidak ditemukan"))
                is RouteOutcome.Error -> _uiState.value = _uiState.value.copy(banner = MapBanner.Generic(outcome.message))
            }
        }
    }

    fun saveFavorite(name: String, address: String, lat: Double, lng: Double) {
        viewModelScope.launch { favoritesRepo.save(name, address, lat, lng) }
    }

    fun deleteFavoriteFromScreen(favorite: com.naze.maps.favorites.FavoriteEntity) {
        viewModelScope.launch { favoritesRepo.delete(favorite) }
    }

    fun renameFavorite(favorite: com.naze.maps.favorites.FavoriteEntity, newName: String) {
        viewModelScope.launch { favoritesRepo.rename(favorite, newName) }
    }

    fun dismissBanner() {
        _uiState.value = _uiState.value.copy(banner = null)
    }

    fun toggleSatellite() {
        _uiState.value = _uiState.value.copy(isSatelliteOn = !_uiState.value.isSatelliteOn)
    }

    fun toggleTheme() {
        viewModelScope.launch { settings.setDarkTheme(!_uiState.value.isDarkTheme) }
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
