package com.naze.maps.map

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.naze.maps.emblems.PixelPalette
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * WMAP-R1 AC1.2: the bundled pixel style JSONs and the Kotlin PixelPalette (used by
 * the biome legend) must stay in sync, and the styles must reference the keyless
 * OpenFreeMap sources. Robolectric only provides asset access.
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [34], application = Application::class)
class PixelStyleTest {

    private fun styleJson(isDark: Boolean): String {
        val context = ApplicationProvider.getApplicationContext<Application>()
        return context.assets.open(MapStyle.pixelAssetForTheme(isDark))
            .use { it.readBytes().decodeToString() }
    }

    private fun layer(json: String, id: String): JSONObject {
        val layers = JSONObject(json).getJSONArray("layers")
        for (i in 0 until layers.length()) {
            val candidate = layers.getJSONObject(i)
            if (candidate.getString("id") == id) return candidate
        }
        error("layer not found: $id")
    }

    /** ["match", input, label, color, ..., fallback] → (label→color map, fallback). */
    private fun matchColors(expression: JSONArray): Pair<Map<String, String>, String> {
        val colors = mutableMapOf<String, String>()
        var i = 2
        while (i + 1 < expression.length() - 1) {
            colors[expression.getString(i)] = expression.getString(i + 1)
            i += 2
        }
        return colors to expression.getString(expression.length() - 1)
    }

    private fun Long.toHex(): String = "#%06X".format(this and 0xFFFFFFL)

    @Test
    fun 'both styles reference keyless OpenFreeMap tiles and glyphs'() {
        for (isDark in listOf(false, true)) {
            val root = JSONObject(styleJson(isDark))
            assertEquals(
                "https://tiles.openfreemap.org/planet",
                root.getJSONObject("sources").getJSONObject("openmaptiles").getString("url"),
            )
            assertTrue(root.getString("glyphs").startsWith("https://tiles.openfreemap.org/fonts/"))
        }
    }

    @Test
    fun 'landcover biome colors match PixelPalette'() {
        for (isDark in listOf(false, true)) {
            val palette = PixelPalette.forTheme(isDark)
            val paint = layer(styleJson(isDark), "landcover-biomes").getJSONObject("paint")
            val (colors, fallback) = matchColors(paint.getJSONArray("fill-color"))
            assertEquals(palette.forest.toHex(), colors["wood"])
            assertEquals(palette.grassland.toHex(), colors["grass"])
            assertEquals(palette.farmland.toHex(), colors["farmland"])
            assertEquals(palette.desert.toHex(), colors["sand"])
            assertEquals(palette.tundra.toHex(), colors["ice"])
            assertEquals(palette.wetland.toHex(), colors["wetland"])
            assertEquals(palette.mountain.toHex(), colors["rock"])
            assertEquals("rgba(0,0,0,0)", fallback)
        }
    }

    @Test
    fun 'water, background and motorway colors match palette'() {
        for (isDark in listOf(false, true)) {
            val palette = PixelPalette.forTheme(isDark)
            val water = matchColors(
                layer(styleJson(isDark), "water").getJSONObject("paint").getJSONArray("fill-color"),
            )
            assertEquals(palette.ocean.toHex(), water.first["ocean"])
            assertEquals(palette.water.toHex(), water.second)
            assertEquals(
                palette.background.toHex(),
                layer(styleJson(isDark), "background").getJSONObject("paint").getString("background-color"),
            )
            assertEquals(
                palette.road.toHex(),
                layer(styleJson(isDark), "road-motorway").getJSONObject("paint").getString("line-color"),
            )
        }
    }

    @Test
    fun 'staged detail minzooms are configured'() {
        val json = styleJson(false)
        assertEquals(13.5, layer(json, "buildings").getDouble("minzoom"), 0.0)
        assertEquals(10.0, layer(json, "road-minor").getDouble("minzoom"), 0.0)
        assertEquals(11.0, layer(json, "place-village").getDouble("minzoom"), 0.0)
        assertEquals(1.0, layer(json, "place-continent").getDouble("minzoom"), 0.0)
    }
}
