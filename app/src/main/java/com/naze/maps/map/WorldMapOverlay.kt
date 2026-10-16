package com.naze.maps.map

import com.naze.maps.emblems.PixelEmblem
import com.naze.maps.emblems.toBitmap
import com.naze.maps.landmarks.Landmark
import com.naze.maps.landmarks.ProminenceTier
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonOptions
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/**
 * WMAP-R2/R5/R6: world-map overlay — landmark sprites (tiered prominence + clustering)
 * and the adaptive graticule. All MapLibre API usage stays in the map package
 * (ADR-002 boundary); callers only pass pure models.
 */

private const val LANDMARK_SOURCE_ID = "naze-landmark-source"
private const val LANDMARK_CLUSTER_LAYER_ID = "naze-landmark-cluster"
private const val LANDMARK_LABEL_LAYER_ID = "naze-landmark-label"

private const val GRATICULE_SOURCE_ID = "naze-graticule-source"
private const val GRATICULE_LINE_LAYER_ID = "naze-graticule-line"
private const val GRATICULE_MAJOR_LAYER_ID = "naze-graticule-major"
private const val GRATICULE_LABEL_LAYER_ID = "naze-graticule-label"

/** Symbol layer ids per prominence tier — used for tap detection in MapScreen. */
val LANDMARK_TIER_LAYER_IDS: List<String> = ProminenceTier.entries.map(::tierLayerId)

fun tierLayerId(tier: ProminenceTier): String = "naze-landmark-tier-" + tier.name.lowercase()

/** WMAP-R2: landmark → GeoJSON feature (properties drive sprite & tier filters). */
fun Landmark.toFeature(): Feature {
    val feature = Feature.fromGeometry(Point.fromLngLat(longitude, latitude))
    feature.addStringProperty("id", id)
    feature.addStringProperty("name", name)
    feature.addStringProperty("emblem", category.emblemName)
    feature.addStringProperty("tier", tier.name)
    feature.addStringProperty("category", category.label)
    return feature
}

/**
 * WMAP-R2/R3/R6: registers emblem sprites and the clustered landmark source with its
 * layers. Idempotent — safe to call again after a style reload (theme switch), which
 * is the only time a fresh Style instance appears.
 *
 * Zoom behavior (AC6.1): below z4 the source aggregates landmarks into clusters
 * (clusterMaxZoom 4); the cluster layer maxes out at z4, from where the per-tier
 * symbol layers take over (tier minzoom 0/4/8, filter on the "tier" property).
 */
fun Style.installLandmarks(landmarks: List<Landmark>, emblems: List<PixelEmblem>) {
    emblems.forEach { emblem -> addImage(emblem.name, emblem.toBitmap()) }

    if (getSource(LANDMARK_SOURCE_ID) == null) {
        val collection = FeatureCollection.fromFeatures(landmarks.map { it.toFeature() })
        val options = GeoJsonOptions()
            .withCluster(true)
            .withClusterMaxZoom(4)
            .withClusterRadius(40)
        addSource(GeoJsonSource(LANDMARK_SOURCE_ID, collection, options))
    }

    if (getLayer(LANDMARK_CLUSTER_LAYER_ID) == null) {
        val clusterLayer = SymbolLayer(LANDMARK_CLUSTER_LAYER_ID, LANDMARK_SOURCE_ID).withProperties(
            PropertyFactory.iconImage("emblem-cluster"),
            PropertyFactory.iconSize(1.2f),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.textField("{point_count}"),
            PropertyFactory.textSize(9f),
            PropertyFactory.textColor("#FFFFFF"),
            PropertyFactory.textFont("Noto Sans Regular"),
        )
        clusterLayer.maxZoom = 4.0
        addLayer(clusterLayer)
    }

    ProminenceTier.entries.forEach { tier ->
        val layerId = tierLayerId(tier)
        if (getLayer(layerId) == null) {
            val layer = SymbolLayer(layerId, LANDMARK_SOURCE_ID).withProperties(
                PropertyFactory.iconImage("{emblem}"),
                PropertyFactory.iconSize(1.1f),
                PropertyFactory.iconAllowOverlap(true),
            )
            layer.setFilter(Expression.eq(Expression.get("tier"), Expression.literal(tier.name)))
            layer.minZoom = tier.minZoom
            addLayer(layer)
        }
    }

    if (getLayer(LANDMARK_LABEL_LAYER_ID) == null) {
        val labelLayer = SymbolLayer(LANDMARK_LABEL_LAYER_ID, LANDMARK_SOURCE_ID).withProperties(
            PropertyFactory.textField("{name}"),
            PropertyFactory.textSize(9.5f),
            PropertyFactory.textColor("#12233D"),
            PropertyFactory.textHaloColor("#FFFFFF"),
            PropertyFactory.textHaloWidth(1.2f),
            PropertyFactory.textFont("Noto Sans Regular"),
            PropertyFactory.textAnchor(Property.ANCHOR_TOP),
            PropertyFactory.textOffset(arrayOf(0f, 0.6f)),
            PropertyFactory.textIgnorePlacement(true),
        )
        labelLayer.minZoom = 4.0
        addLayer(labelLayer)
    }
}

/**
 * WMAP-R5: (re)generates the graticule source content. Only call when the zoom
 * interval actually changed (the WMAP-R6 guard lives in MapScreen).
 */
fun Style.updateGraticule(collection: FeatureCollection) {
    val existing = getSourceAs<GeoJsonSource>(GRATICULE_SOURCE_ID)
    if (existing != null) {
        existing.setGeoJson(collection)
        return
    }
    addSource(GeoJsonSource(GRATICULE_SOURCE_ID, collection))

    val lineLayer = LineLayer(GRATICULE_LINE_LAYER_ID, GRATICULE_SOURCE_ID).withProperties(
        PropertyFactory.lineColor("#6B8FAE"),
        PropertyFactory.lineWidth(0.7f),
        PropertyFactory.lineOpacity(0.8f),
        PropertyFactory.lineCap(Property.LINE_CAP_BUTT),
    )
    lineLayer.minZoom = 1.0
    addLayer(lineLayer)

    val majorLayer = LineLayer(GRATICULE_MAJOR_LAYER_ID, GRATICULE_SOURCE_ID).withProperties(
        PropertyFactory.lineColor("#1D4ED8"),
        PropertyFactory.lineWidth(1.4f),
        PropertyFactory.lineCap(Property.LINE_CAP_BUTT),
    )
    majorLayer.minZoom = 1.0
    majorLayer.setFilter(Expression.eq(Expression.get("major"), Expression.literal(true)))
    addLayer(majorLayer)

    val labelLayer = SymbolLayer(GRATICULE_LABEL_LAYER_ID, GRATICULE_SOURCE_ID).withProperties(
        PropertyFactory.textField("{label}"),
        PropertyFactory.textSize(9f),
        PropertyFactory.textColor("#1F3B5C"),
        PropertyFactory.textHaloColor("#FFFFFF"),
        PropertyFactory.textHaloWidth(1.0f),
        PropertyFactory.textFont("Noto Sans Regular"),
    )
    labelLayer.minZoom = 2.0
    addLayer(labelLayer)
}

/** WMAP-R5 AC5.3: graticule visibility toggle without a style reload. */
fun Style.setGraticuleVisible(visible: Boolean) {
    val value = if (visible) Property.VISIBLE else Property.NONE
    listOf(GRATICULE_LINE_LAYER_ID, GRATICULE_MAJOR_LAYER_ID, GRATICULE_LABEL_LAYER_ID).forEach { id ->
        getLayer(id)?.setProperties(PropertyFactory.visibility(value))
    }
}
