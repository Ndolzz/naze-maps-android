package com.naze.maps.map

import android.content.Context

/**
 * Style source for the map (WMAP-R1, ADR-006 #1/#2).
 *
 * The pixel-art redesign renders from style JSON bundled in the app assets (per
 * OpenFreeMap guidance, customized styles must be self-hosted — bundling them in the
 * app is the self-hosting form). Tiles and glyphs still come from OpenFreeMap,
 * keyless and free.
 *
 * The legacy remote style URLs (liberty/dark) were removed when WMAP-T6 switched
 * MapScreen over to the bundled pixel styles.
 */
object MapStyle {

    /** Bundled pixel-art styles (WMAP-R1). */
    const val PIXEL_LIGHT_ASSET = "styles/naze-pixel-light.json"
    const val PIXEL_DARK_ASSET = "styles/naze-pixel-dark.json"

    /** Keys passed to MapLibreMapView to detect theme switches without URI checks. */
    const val PIXEL_LIGHT_KEY = "pixel-light"
    const val PIXEL_DARK_KEY = "pixel-dark"

    fun pixelAssetForTheme(isDark: Boolean): String = if (isDark) PIXEL_DARK_ASSET else PIXEL_LIGHT_ASSET

    fun pixelKeyForTheme(isDark: Boolean): String = if (isDark) PIXEL_DARK_KEY else PIXEL_LIGHT_KEY

    /** Reads the bundled style JSON (loaded via Style.Builder().fromJson). */
    fun readPixelStyle(context: Context, isDark: Boolean): String {
        val asset = pixelAssetForTheme(isDark)
        return context.assets.open(asset).use { stream -> stream.readBytes().decodeToString() }
    }
}
