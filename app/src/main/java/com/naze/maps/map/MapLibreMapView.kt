package com.naze.maps.map

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

/**
 * Hosts MapLibre's classic (non-Compose) MapView inside Compose, wiring its lifecycle to the
 * host Composable's lifecycle so the GL surface pauses/resumes correctly instead of leaking
 * (requirement #14: no memory leaks, no unnecessary background rendering).
 *
 * BUG-001 fix [TASK-005]: onDestroy() is now also invoked from onDispose, so the MapView is
 * properly destroyed when the composable leaves composition (e.g. tab switch), not only when
 * the Activity is destroyed. A guard flag prevents double-destroy when both paths would fire.
 *
 * CH-105: optional [onMapLongClick] is wired to MapLibreMap.addOnMapLongClickListener so the
 * map can offer a press-and-hold affordance (save this point as a favorite). Null by default:
 * existing callers are unaffected.
 *
 * No globe/3D projection: MapLibre Native for Android doesn't implement it yet (web-only via
 * maplibre-gl-js). This renders a solid 2D vector map — no fake globe, per requirement #8's
 * own rule against claiming 360° when the engine is actually flat.
 */
@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    styleUrl: String,
    onMapReady: (MapLibreMap) -> Unit,
    onStyleLoaded: (Style) -> Unit = {},
    onMapLongClick: ((LatLng) -> Unit)? = null,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    // Guards against calling onDestroy() twice (Activity destroy + composition dispose).
    val destroyed = remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                onCreate(Bundle())
                mapViewState.value = this
                getMapAsync { map ->
                    onMapReady(map)
                    onMapLongClick?.let { handler ->
                        map.addOnMapLongClickListener { point ->
                            handler(point)
                            true
                        }
                    }
                    map.setStyle(styleUrl) { style -> onStyleLoaded(style) }
                }
            }
        },
        update = { view ->
            view.getMapAsync { map ->
                if (map.style?.uri != styleUrl) {
                    map.setStyle(styleUrl) { style -> onStyleLoaded(style) }
                }
            }
        },
    )

    // Forward Android lifecycle events to MapView so GL resources are managed correctly
    // (requirement #14: no leaks, no wasted background rendering).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val view = mapViewState.value ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> view.onStart()
                Lifecycle.Event.ON_RESUME -> view.onResume()
                Lifecycle.Event.ON_PAUSE -> view.onPause()
                Lifecycle.Event.ON_STOP -> view.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    if (!destroyed.value) {
                        destroyed.value = true
                        view.onDestroy()
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Leaving composition (e.g. switching tab): destroy the GL surface too.
            mapViewState.value?.let { view ->
                if (!destroyed.value) {
                    destroyed.value = true
                    view.onDestroy()
                }
            }
            mapViewState.value = null
        }
    }
}
