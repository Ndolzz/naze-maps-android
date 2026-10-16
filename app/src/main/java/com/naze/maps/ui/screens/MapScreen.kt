package com.naze.maps.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.naze.maps.emblems.PixelEmblems
import com.naze.maps.emblems.PixelPalette
import com.naze.maps.history.HistoryEntity
import com.naze.maps.landmarks.LandmarkCategory
import com.naze.maps.location.PermissionUtils
import com.naze.maps.map.Graticule
import com.naze.maps.map.LANDMARK_TIER_LAYER_IDS
import com.naze.maps.map.MapLibreMapView
import com.naze.maps.map.MapStyle
import com.naze.maps.map.installLandmarks
import com.naze.maps.map.setGraticuleVisible
import com.naze.maps.map.setSatelliteVisible
import com.naze.maps.map.updateGraticule
import com.naze.maps.map.updateLocationDot
import com.naze.maps.map.updateRouteLine
import com.naze.maps.routing.RoutingProfile
import com.naze.maps.ui.components.CompassFab
import com.naze.maps.ui.components.ErrorBanner
import com.naze.maps.ui.components.GraticuleFab
import com.naze.maps.ui.components.LayersFab
import com.naze.maps.ui.components.LegendFab
import com.naze.maps.ui.components.MapLoadingOverlay
import com.naze.maps.ui.components.MyLocationFab
import com.naze.maps.ui.components.NazeSearchBar
import com.naze.maps.ui.components.PixelEmblemIcon
import com.naze.maps.utils.GeoPoint
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }
    var showLegend by remember { mutableStateOf(false) }
    // WMAP-R6: interval graticule terakhir yang dirender — regenerasi hanya saat interval
    // benar-benar berubah, agar listener camera-idle tetap murah di perangkat kelas rendah.
    var lastGraticuleInterval by remember { mutableStateOf<Double?>(null) }

    val isDark = state.settings.isDarkTheme
    // WMAP-R1: style pixel-art dibundel di assets — dibaca ulang hanya saat tema berganti.
    val styleJson = remember(isDark) { MapStyle.readPixelStyle(context, isDark) }

    val permissionState = rememberMultiplePermissionsState(PermissionUtils.requiredPermissions.toList()) { results ->
        viewModel.onPermissionResult(results.values.any { it })
    }

    // Requirement #4: My Location must actually request permission, not sit there as decoration.
    LaunchedEffect(Unit) {
        if (permissionState.permissions.any { it.status.isGranted }) {
            viewModel.startTracking()
        }
    }

    // Recenter camera whenever a fresh location arrives while tracking is on.
    LaunchedEffect(state.location.myLocation, state.location.isTrackingMe) {
        val loc = state.location.myLocation
        if (loc != null && state.location.isTrackingMe) {
            maplibreMap?.cameraPosition = CameraPosition.Builder()
                .target(LatLng(loc.latitude, loc.longitude))
                .build()
        }
    }

    // TASK-008b (BUG-004): selecting a place (from search or history) now moves the camera to
    // it, so the user is not left staring at an unrelated area behind the bottom sheet.
    LaunchedEffect(state.route.selectedPlace, maplibreMap) {
        val place = state.route.selectedPlace ?: return@LaunchedEffect
        maplibreMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(place.latitude, place.longitude), 15.0)
        )
    }

    // Draw (or clear) the "my location" dot independently of camera tracking — it should show
    // wherever we last heard from GPS, whether or not the camera is actively following it.
    LaunchedEffect(state.location.myLocation, mapStyle) {
        mapStyle?.updateLocationDot(state.location.myLocation?.latitude, state.location.myLocation?.longitude)
    }

    // Draw (or clear) the active route's line. Re-fires on style reload (e.g. theme switch)
    // since MapLibre drops custom sources/layers whenever setStyle() runs.
    LaunchedEffect(state.route.activeRoute, mapStyle) {
        mapStyle?.updateRouteLine(state.route.activeRoute)
    }

    // Applies the satellite toggle to whichever Style instance is currently loaded — also
    // re-fires after a style reload so the toggle survives a theme switch.
    LaunchedEffect(state.map.isSatelliteOn, mapStyle) {
        mapStyle?.setSatelliteVisible(state.map.isSatelliteOn)
    }

    // WMAP-R2/R5/R6: pasang ulang sprite landmark + graticule setiap kali style selesai
    // dimuat ulang (ganti tema membuang source/layer kustom bersama instance Style lama).
    LaunchedEffect(mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        style.installLandmarks(viewModel.landmarks, PixelEmblems.all())
        val interval = Graticule.intervalForZoom(maplibreMap?.cameraPosition?.zoom ?: 2.0)
        lastGraticuleInterval = interval
        style.updateGraticule(Graticule.generate(interval))
        style.setGraticuleVisible(state.map.isGraticuleOn)
    }

    // WMAP-R5 (AC5.3): toggle graticule hanya mengubah visibilitas layer, tanpa reload style.
    LaunchedEffect(state.map.isGraticuleOn, mapStyle) {
        mapStyle?.setGraticuleVisible(state.map.isGraticuleOn)
    }

    // WMAP-R2 (AC2.4): memilih landmark (tap sprite) menerbangkan kamera ke lokasinya.
    LaunchedEffect(state.landmark.selected, maplibreMap) {
        val landmark = state.landmark.selected ?: return@LaunchedEffect
        maplibreMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(landmark.latitude, landmark.longitude), 11.0)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapLibreMapView(
            modifier = Modifier.fillMaxSize(),
            styleJson = styleJson,
            styleKey = MapStyle.pixelKeyForTheme(isDark),
            onMapReady = { map ->
                maplibreMap = map
                // WMAP-R2 (AC2.4): deteksi tap pada symbol layer tier landmark.
                map.addOnMapClickListener { latLng ->
                    val tapped = map.queryRenderedFeatures(latLng, *LANDMARK_TIER_LAYER_IDS.toTypedArray())
                    val landmarkId = tapped.firstOrNull()?.getStringProperty("id")
                    val landmark = landmarkId?.let { id -> viewModel.landmarks.firstOrNull { it.id == id } }
                    if (landmark != null) {
                        viewModel.selectLandmark(landmark)
                        true
                    } else {
                        false
                    }
                }
                // WMAP-R6: graticule adaptif — regenerasi hanya jika interval zoom berubah.
                map.addOnCameraIdleListener {
                    val style = mapStyle ?: return@addOnCameraIdleListener
                    val interval = Graticule.intervalForZoom(map.cameraPosition.zoom)
                    if (interval != lastGraticuleInterval) {
                        lastGraticuleInterval = interval
                        style.updateGraticule(Graticule.generate(interval))
                    }
                }
            },
            onStyleLoaded = { style -> mapStyle = style },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
        ) {
            NazeSearchBar(
                query = state.search.query,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = { viewModel.onSearchQueryChange("") },
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.search.results.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 4.dp,
                ) {
                    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                        items(state.search.results) { result ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectPlace(result) }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Text(result.mainText, style = MaterialTheme.typography.bodyLarge)
                                Text(result.subText, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            } else if (state.search.query.isBlank() && history.isNotEmpty()) {
                // Riwayat lokasi — muncul saat kotak pencarian kosong, hilang begitu ada hasil pencarian.
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = 4.dp,
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Riwayat lokasi", style = MaterialTheme.typography.labelLarge)
                            TextButton(onClick = viewModel::clearHistory) { Text("Hapus semua") }
                        }
                        LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                            items(history, key = { it.id }) { entry: HistoryEntity ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectHistoryEntry(entry) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.History,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 12.dp),
                                        )
                                        Column {
                                            Text(entry.name, style = MaterialTheme.typography.bodyLarge)
                                            if (entry.address.isNotBlank()) {
                                                Text(entry.address, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                    IconButton(onClick = { viewModel.deleteHistoryEntry(entry) }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Hapus dari riwayat")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            state.banner?.let { banner ->
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    ErrorBanner(banner = banner, onDismiss = viewModel::dismissBanner)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    // WMAP-R4: clearance ekstra agar FAB tidak tertutup floating dock.
                    modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 84.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CompassFab(
                        headingDegrees = state.location.headingDegrees,
                        onResetNorth = {
                            maplibreMap?.let { map ->
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(map.cameraPosition.target)
                                    .bearing(0.0)
                                    .zoom(map.cameraPosition.zoom)
                                    .build()
                            }
                        },
                    )
                    GraticuleFab(
                        isActive = state.map.isGraticuleOn,
                        onClick = viewModel::toggleGraticule,
                    )
                    LegendFab(onClick = { showLegend = true })
                    LayersFab(
                        isActive = state.map.isSatelliteOn,
                        onClick = viewModel::toggleSatellite,
                    )
                    MyLocationFab(
                        isActive = state.location.isTrackingMe,
                        onClick = {
                            if (permissionState.allPermissionsGranted) {
                                if (state.location.isTrackingMe) viewModel.stopTracking() else viewModel.startTracking()
                            } else {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                    )
                }
            }
        }

        // Selalu di atas — biar buka app gak pernah nge-flash peta kosong/abu-abu sebelum
        // style dan tile pertama kelar dimuat. Fade out mulus begitu peta siap.
        AnimatedVisibility(
            visible = mapStyle == null,
            exit = fadeOut(animationSpec = tween(durationMillis = 450)),
            modifier = Modifier.fillMaxSize(),
        ) {
            MapLoadingOverlay()
        }

        state.route.selectedPlace?.let { place ->
            ModalBottomSheet(onDismissRequest = viewModel::clearSelection) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(place.mainText, style = MaterialTheme.typography.titleMedium)
                    Text(place.subText, style = MaterialTheme.typography.bodyMedium)

                    Text(
                        "Directions",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(
                                GeoPoint(place.latitude, place.longitude),
                                RoutingProfile.DRIVING,
                            )
                        }) {
                            Icon(
                                Icons.Filled.DirectionsCar,
                                contentDescription = "Mobil",
                                modifier = Modifier.padding(end = 6.dp),
                            )
                            Text("Mobil")
                        }
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(
                                GeoPoint(place.latitude, place.longitude),
                                RoutingProfile.WALKING,
                            )
                        }) {
                            Icon(
                                Icons.Filled.DirectionsWalk,
                                contentDescription = "Jalan kaki",
                                modifier = Modifier.padding(end = 6.dp),
                            )
                            Text("Jalan")
                        }
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(
                                GeoPoint(place.latitude, place.longitude),
                                RoutingProfile.CYCLING,
                            )
                        }) {
                            Icon(
                                Icons.Filled.DirectionsBike,
                                contentDescription = "Sepeda",
                                modifier = Modifier.padding(end = 6.dp),
                            )
                            Text("Sepeda")
                        }
                    }
                    // Catatan: OSRM demo server publik (router.project-osrm.org) kadang cuma
                    // andal untuk profil mobil — kalau jalan kaki/sepeda gagal, banner
                    // "Rute tidak ditemukan" akan muncul otomatis lewat RouteOutcome di atas.

                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(onClick = {
                            viewModel.saveFavorite(place.mainText, place.subText, place.latitude, place.longitude)
                        }) { Text("Save") }
                        OutlinedButton(onClick = {
                            val mapsUrl = "https://www.openstreetmap.org/?mlat=${place.latitude}&mlon=${place.longitude}" +
                                "#map=17/${place.latitude}/${place.longitude}"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${place.mainText}\n$mapsUrl")
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan lokasi"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                    }
                }
            }
        }

        // WMAP-R2: panel informasi landmark — koordinat, kategori, rute, simpan, bagikan.
        state.landmark.selected?.let { landmark ->
            ModalBottomSheet(onDismissRequest = viewModel::clearLandmarkSelection) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PixelEmblems.byName(landmark.category.emblemName)?.let { emblem ->
                            PixelEmblemIcon(
                                emblem = emblem,
                                iconSize = 30.dp,
                                contentDescription = landmark.category.label,
                            )
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(landmark.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${landmark.country} • ${landmark.category.label}",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                    Text(
                        landmark.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    Text(
                        "Koordinat: ${Graticule.formatLatitude(landmark.latitude)}, ${Graticule.formatLongitude(landmark.longitude)}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    Text(
                        "Rute ke landmark",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(GeoPoint(landmark.latitude, landmark.longitude), RoutingProfile.DRIVING)
                        }) {
                            Icon(Icons.Filled.DirectionsCar, contentDescription = "Mobil", modifier = Modifier.padding(end = 6.dp))
                            Text("Mobil")
                        }
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(GeoPoint(landmark.latitude, landmark.longitude), RoutingProfile.WALKING)
                        }) {
                            Icon(Icons.Filled.DirectionsWalk, contentDescription = "Jalan kaki", modifier = Modifier.padding(end = 6.dp))
                            Text("Jalan")
                        }
                        OutlinedButton(onClick = {
                            viewModel.requestRoute(GeoPoint(landmark.latitude, landmark.longitude), RoutingProfile.CYCLING)
                        }) {
                            Icon(Icons.Filled.DirectionsBike, contentDescription = "Sepeda", modifier = Modifier.padding(end = 6.dp))
                            Text("Sepeda")
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OutlinedButton(onClick = {
                            viewModel.saveFavorite(landmark.name, landmark.country, landmark.latitude, landmark.longitude)
                        }) { Text("Save") }
                        OutlinedButton(onClick = {
                            val mapsUrl = "https://www.openstreetmap.org/?mlat=${landmark.latitude}&mlon=${landmark.longitude}" +
                                "#map=17/${landmark.latitude}/${landmark.longitude}"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${landmark.name}\n$mapsUrl")
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan lokasi"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                    }
                }
            }
        }

        // WMAP-R1: legenda bioma (warna sinkron PixelPalette) + kategori landmark.
        if (showLegend) {
            ModalBottomSheet(onDismissRequest = { showLegend = false }) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text("Legenda Peta Dunia", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Bioma",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                    val palette = PixelPalette.forTheme(isDark)
                    listOf(
                        Triple("Hutan", PixelEmblems.FOREST, palette.forest),
                        Triple("Padang rumput", PixelEmblems.GRASSLAND, palette.grassland),
                        Triple("Gurun", PixelEmblems.DESERT, palette.desert),
                        Triple("Pegunungan", PixelEmblems.MOUNTAIN, palette.mountain),
                        Triple("Tundra", PixelEmblems.TUNDRA, palette.tundra),
                        Triple("Lahan basah", PixelEmblems.WETLAND, palette.wetland),
                        Triple("Perairan", PixelEmblems.WATER, palette.water),
                    ).forEach { (label, emblem, color) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PixelEmblemIcon(emblem = emblem, iconSize = 20.dp, contentDescription = label)
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .size(16.dp)
                                    .background(Color(color), CircleShape),
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Text(
                        "Kategori landmark",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    LandmarkCategory.entries.forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val emblem = PixelEmblems.byName(category.emblemName)
                            if (emblem != null) {
                                PixelEmblemIcon(emblem = emblem, iconSize = 20.dp, contentDescription = category.label)
                            }
                            Text(
                                category.label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
