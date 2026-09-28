package com.naze.maps.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.naze.maps.history.HistoryEntity
import com.naze.maps.location.PermissionUtils
import com.naze.maps.map.MapLibreMapView
import com.naze.maps.map.MapStyle
import com.naze.maps.map.setSatelliteVisible
import com.naze.maps.map.updateLocationDot
import com.naze.maps.map.updateRouteLine
import com.naze.maps.routing.RoutingProfile
import com.naze.maps.ui.components.CompassFab
import com.naze.maps.ui.components.ErrorBanner
import com.naze.maps.ui.components.LayersFab
import com.naze.maps.ui.components.MapLoadingOverlay
import com.naze.maps.ui.components.MyLocationFab
import com.naze.maps.ui.components.NazeSearchBar
import com.naze.maps.ui.components.SplashOverlay
import com.naze.maps.utils.DistanceUtils
import com.naze.maps.utils.GeoPoint
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val viewModel: MapViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState(initial = emptyList())
    val context = LocalContext.current

    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }

    // CH-102 (BUG-018): splash presentasi one-shot — sekali dismiss, tidak muncul lagi
    // (tab switch / theme switch tidak memutar ulang splash).
    var splashDismissed by remember { mutableStateOf(false) }

    // CH-105: titik yang sedang ditekan lama di peta (kandidat favorit), atau null.
    var longPressPoint by remember { mutableStateOf<LatLng?>(null) }

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

    Box(modifier = modifier.fillMaxSize()) {
        MapLibreMapView(
            modifier = Modifier.fillMaxSize(),
            styleUrl = MapStyle.forTheme(state.settings.isDarkTheme),
            onMapReady = { map -> maplibreMap = map },
            onStyleLoaded = { style -> mapStyle = style },
            // CH-105: tekan lama titik mana pun membuka sheet simpan favorit.
            onMapLongClick = { point -> longPressPoint = point },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp),
        ) {
            // CH-101: search bar reserves end space so it never runs under the
            // top-end overflow menu overlay (menu = 40dp + 12dp margin).
            NazeSearchBar(
                query = state.search.query,
                onQueryChange = viewModel::onSearchQueryChange,
                onClear = { viewModel.onSearchQueryChange("") },
                modifier = Modifier.fillMaxWidth().padding(end = 56.dp),
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
                                    // CH-106: satu ketuk rute mobil + hapus per baris riwayat.
                                    Row {
                                        IconButton(onClick = {
                                            viewModel.selectHistoryEntry(entry)
                                            viewModel.requestRoute(
                                                GeoPoint(entry.latitude, entry.longitude),
                                                RoutingProfile.DRIVING,
                                            )
                                        }) {
                                            Icon(
                                                Icons.Filled.DirectionsCar,
                                                contentDescription = "Rute ke sini",
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
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
            }

            state.banner?.let { banner ->
                Box(modifier = Modifier.padding(top = 8.dp)) {
                    ErrorBanner(banner = banner, onDismiss = viewModel::dismissBanner)
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
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

        // Map loading state (plain branded overlay) — kini hanya untuk reload style setelah
        // splash one-shot selesai (splash menutup first load; keduanya tidak menumpuk).
        AnimatedVisibility(
            visible = mapStyle == null && splashDismissed,
            exit = fadeOut(animationSpec = tween(durationMillis = 450)),
            modifier = Modifier.fillMaxSize(),
        ) {
            MapLoadingOverlay()
        }

        // CH-102 (BUG-018): brand splash "Map Comes Alive" — one-shot, readiness-driven.
        // isReady = map style loaded (real initialization signal, bukan timer). Fade-out
        // 450ms memberi transisi natural ke map; native SplashScreen API (system) tetap
        // menangani cold-start instan sebelum Compose tergambar.
        AnimatedVisibility(
            visible = !splashDismissed,
            exit = fadeOut(animationSpec = tween(durationMillis = 450)),
            modifier = Modifier.fillMaxSize(),
        ) {
            SplashOverlay(
                isReady = mapStyle != null,
                onDismiss = { splashDismissed = true },
            )
        }

        // CH-105: sheet simpan favorit untuk titik yang ditekan lama di peta.
        longPressPoint?.let { point ->
            ModalBottomSheet(onDismissRequest = { longPressPoint = null }) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Simpan lokasi ini", style = MaterialTheme.typography.titleMedium)
                    Text(
                        String.format(Locale.US, "%.5f, %.5f", point.latitude, point.longitude),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    var name by remember(point) { mutableStateOf("Lokasi tersimpan") }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama lokasi") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                    OutlinedButton(
                        onClick = {
                            val coordinateText = String.format(Locale.US, "%.5f, %.5f", point.latitude, point.longitude)
                            viewModel.saveFavorite(
                                name.ifBlank { "Lokasi tersimpan" },
                                coordinateText,
                                point.latitude,
                                point.longitude,
                            )
                            longPressPoint = null
                            Toast.makeText(context, "Tersimpan di Favorit", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.padding(top = 12.dp),
                    ) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp),
                        )
                        Text("Simpan ke Favorit")
                    }
                }
            }
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
                            val mapsUrl = "https://www.openstreetmap.org/?mlat=" + place.latitude + "&mlon=" + place.longitude +
                                "#map=17/" + place.latitude + "/" + place.longitude
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, place.mainText + "
" + mapsUrl)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan lokasi"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Share")
                        }
                    }

                    // CH-107: bagikan ringkasan rute aktif (jarak + waktu OSRM + tautan).
                    state.route.activeRoute?.let { route ->
                        OutlinedButton(
                            onClick = {
                                val routeKm = route.distanceMeters / 1000.0
                                val minutes = (route.durationSeconds / 60.0).toInt()
                                val distanceText = DistanceUtils.format(routeKm, state.settings.distanceUnit)
                                val placeUrl = "https://www.openstreetmap.org/?mlat=" + place.latitude + "&mlon=" + place.longitude +
                                    "#map=17/" + place.latitude + "/" + place.longitude
                                val origin = state.location.myLocation
                                val link = if (origin != null) {
                                    "https://www.openstreetmap.org/directions?engine=fossgis_osrm_car&route=" +
                                        String.format(
                                            Locale.US,
                                            "%.6f,%.6f;%.6f,%.6f",
                                            origin.latitude, origin.longitude, place.latitude, place.longitude,
                                        )
                                } else placeUrl
                                val shareText = "Rute ke " + place.mainText + "
" +
                                    "Jarak " + distanceText + "
" +
                                    "Perkiraan waktu " + minutes + " menit" + "
" + link
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan rute"))
                            },
                            modifier = Modifier.padding(top = 10.dp),
                        ) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Bagikan rute",
                                modifier = Modifier.padding(end = 6.dp),
                            )
                            Text("Bagikan rute")
                        }
                    }
                }
            }
        }
    }
}
