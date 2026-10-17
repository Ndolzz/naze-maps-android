package com.naze.maps.emblems

import android.graphics.Bitmap

/**
 * WMAP-R3: renders a [PixelEmblem] to an Android Bitmap for use as a MapLibre sprite
 * (Style.addImage). Kept in the emblems package so the map layer stays the only place
 * that touches the MapLibre API (ADR-002 boundary); this file is the single Android
 * import point for emblem rendering.
 */
fun PixelEmblem.toBitmap(): Bitmap {
    require(width > 0 && height > 0) { "Emblem '$name' has an empty grid" }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    for (y in rows.indices) {
        val row = rows[y]
        for (x in row.indices) {
            val color = palette[row[x]] ?: continue
            bitmap.setPixel(x, y, color.toInt())
        }
    }
    return bitmap
}
