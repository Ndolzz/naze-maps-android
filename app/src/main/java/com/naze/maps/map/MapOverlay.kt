package com.naze.maps.map

import com.naze.maps.routing.OsrmRoute
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillExtrusionLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

private const val ROUTE_SOURCE_ID = "naze-route-source"
private const val ROUTE_LAYER_ID = "naze-route-layer"
private const val LOCATION_SOURCE_ID = "naze-location-source"
private const val LOCATION_LAYER_ID = "naze-location-layer"
private const val SATELLITE_SOURCE_ID = "naze-satellite-source"
private const val SATELLITE_LAYER_ID = "naze-satellite-layer"

/**
 * Draws the active route as a line on the map (or clears it when [route] is null).
 * Safe to call on every recomposition — updates the existing source instead of re-adding layers.
 */
fun Style.updateRouteLine(route: OsrmRoute?) {
    val existing = getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
    if (route == null) {
        existing?.setGeoJson(FeatureCollection.fromFeatures(emptyArray()))
        return
    }
    val line = LineString.fromLngLats(route.geometry.coordinates.map { Point.fromLngLat(it[0], it[1]) })
    if (existing != null) {
        existing.setGeoJson(line)
    } else {
        addSource(GeoJsonSource(ROUTE_SOURCE_ID, line))
        val routeLayer = LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
            PropertyFactory.lineColor("#2F6FED"),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
        )
        // Keep the route line under the location dot when both exist, but don't assume the
        // dot layer is already there — addLayerBelow throws if its target id is missing.
        if (getLayer(LOCATION_LAYER_ID) != null) addLayerBelow(routeLayer, LOCATION_LAYER_ID) else addLayer(routeLayer)
    }
}

/** Draws (or clears) a small dot marking the user's current location — independent of camera recentring. */
fun Style.updateLocationDot(lat: Double?, lng: Double?) {
    val existing = getSourceAs<GeoJsonSource>(LOCATION_SOURCE_ID)
    if (lat == null || lng == null) {
        existing?.setGeoJson(FeatureCollection.fromFeatures(emptyArray()))
        return
    }
    val point = Point.fromLngLat(lng, lat)
    if (existing != null) {
        existing.setGeoJson(point)
    } else {
        addSource(GeoJsonSource(LOCATION_SOURCE_ID, point))
        addLayer(
            CircleLayer(LOCATION_LAYER_ID, LOCATION_SOURCE_ID).withProperties(
                PropertyFactory.circleRadius(8f),
                PropertyFactory.circleColor("#2F6FED"),
                PropertyFactory.circleStrokeColor("#FFFFFF"),
                PropertyFactory.circleStrokeWidth(2.5f),
            ),
        )
    }
}

/**
 * Toggles a free, keyless Esri World Imagery raster layer as a satellite view.
 * Since the base style's fill/background layers (land, water, buildings) are opaque, they'd
 * otherwise completely cover the raster — so this hides them while satellite is on, and
 * restores them when it's off. Line/symbol layers (roads, labels) stay visible on top either way.
 */
fun Style.setSatelliteVisible(visible: Boolean) {
    if (visible) {
        if (getLayer(SATELLITE_LAYER_ID) == null) {
            val tileSet = TileSet(
                "2.1.0",
                "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
            )
            addSource(RasterSource(SATELLITE_SOURCE_ID, tileSet, 256))
            addLayerAt(RasterLayer(SATELLITE_LAYER_ID, SATELLITE_SOURCE_ID), 0)
        }
        layers.forEach { layer ->
            if (layer is BackgroundLayer || layer is FillLayer || layer is FillExtrusionLayer) {
                layer.setProperties(PropertyFactory.visibility(Property.NONE))
            }
        }
    } else {
        getLayer(SATELLITE_LAYER_ID)?.let { removeLayer(it) }
        getSourceAs<RasterSource>(SATELLITE_SOURCE_ID)?.let { removeSource(it) }
        layers.forEach { layer ->
            if (layer is BackgroundLayer || layer is FillLayer || layer is FillExtrusionLayer) {
                layer.setProperties(PropertyFactory.visibility(Property.VISIBLE))
            }
        }
    }
}
