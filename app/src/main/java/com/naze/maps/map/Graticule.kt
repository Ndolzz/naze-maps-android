package com.naze.maps.map

import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

/**
 * WMAP-R5: graticule (latitude/longitude grid) generator. Pure code — drawn on the
 * same Web Mercator projection the map uses, so lines align with real coordinates.
 *
 * Interval adapts to zoom (WMAP-R6): 10 below z4, 5 for z4-<6, 1 from z6 on.
 * The equator (lat 0) and prime meridian (lng 0) are flagged major=true so the
 * overlay can emphasize them.
 *
 * Each line is emitted as a LineString feature (rendered by the LineLayers) plus a
 * Point label feature at the map edge (rendered by the graticule SymbolLayer; point
 * placement ignores line features and vice versa, so no layer filters are needed).
 */
object Graticule {

    private const val MERIDIAN_LAT_EXTENT = 85.0
    private const val PARALLEL_LNG_EXTENT = 180.0
    private const val LABEL_LNG = -170.0
    private const val LABEL_LAT = 76.0

    fun intervalForZoom(zoom: Double): Double = when {
        zoom < 4.0 -> 10.0
        zoom < 6.0 -> 5.0
        else -> 1.0
    }

    fun generate(interval: Double): FeatureCollection {
        require(interval > 0.0 && interval <= 30.0) { "interval out of range: $interval" }
        val features = mutableListOf<Feature>()
        var lat = -80.0
        while (lat <= 80.0 + 1e-9) {
            features += parallelLine(lat)
            if (shouldLabelParallel(lat, interval)) {
                features += labelPoint(LABEL_LNG, lat, formatLatitude(lat), major = lat == 0.0)
            }
            lat += interval
        }
        var lng = -180.0
        while (lng < 180.0 - 1e-9) {
            features += meridianLine(lng)
            features += labelPoint(lng, LABEL_LAT, formatLongitude(lng), major = lng == 0.0)
            lng += interval
        }
        return FeatureCollection.fromFeatures(features)
    }

    /** At world scale only every 3rd parallel gets a label, to avoid clutter. */
    private fun shouldLabelParallel(lat: Double, interval: Double): Boolean =
        interval < 10.0 || lat % 30.0 == 0.0

    private fun parallelLine(lat: Double): Feature {
        val line = LineString.fromLngLats(
            listOf(
                Point.fromLngLat(-PARALLEL_LNG_EXTENT, lat),
                Point.fromLngLat(PARALLEL_LNG_EXTENT, lat),
            ),
        )
        return Feature.fromGeometry(line).apply {
            addStringProperty("kind", "latitude")
            addNumberProperty("value", lat)
            addBooleanProperty("major", lat == 0.0)
            addStringProperty("label", formatLatitude(lat))
        }
    }

    private fun meridianLine(lng: Double): Feature {
        val line = LineString.fromLngLats(
            listOf(
                Point.fromLngLat(lng, -MERIDIAN_LAT_EXTENT),
                Point.fromLngLat(lng, MERIDIAN_LAT_EXTENT),
            ),
        )
        return Feature.fromGeometry(line).apply {
            addStringProperty("kind", "longitude")
            addNumberProperty("value", lng)
            addBooleanProperty("major", lng == 0.0)
            addStringProperty("label", formatLongitude(lng))
        }
    }

    private fun labelPoint(lng: Double, lat: Double, label: String, major: Boolean): Feature {
        val point = Point.fromLngLat(lng, lat)
        return Feature.fromGeometry(point).apply {
            addStringProperty("kind", "label")
            addStringProperty("label", label)
            addBooleanProperty("major", major)
        }
    }

    fun formatLatitude(value: Double): String = when {
        value == 0.0 -> "0°"
        value > 0.0 -> "${trim(value)}°N"
        else -> "${trim(-value)}°S"
    }

    fun formatLongitude(value: Double): String = when {
        value == 0.0 -> "0°"
        value > 0.0 -> "${trim(value)}°E"
        else -> "${trim(-value)}°W"
    }

    private fun trim(value: Double): String {
        val rounded = kotlin.math.round(value * 100.0) / 100.0
        return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
    }
}
