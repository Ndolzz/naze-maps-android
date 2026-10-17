package com.naze.maps.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.naze.maps.emblems.PixelEmblem

/**
 * WMAP-R3: renderer Compose untuk grid emblem pixel — pasangan UI dari
 * EmblemBitmapFactory (yang merender grid yang sama ke Bitmap untuk sprite MapLibre).
 *
 * [monoColor] opsional meratakan semua pixel opaque ke satu warna — dipakai untuk
 * state terpilih/aktif di mana palet emblem sendiri tidak kontras dengan latarnya.
 */
@Composable
fun PixelEmblemIcon(
    emblem: PixelEmblem,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconSize: Dp = 24.dp,
    monoColor: Color? = null,
) {
    val description = contentDescription
    Canvas(
        modifier = modifier
            .size(iconSize)
            .semantics { if (description != null) this.contentDescription = description },
    ) {
        drawEmblem(emblem, monoColor)
    }
}

private fun DrawScope.drawEmblem(emblem: PixelEmblem, monoColor: Color?) {
    val cols = emblem.width
    val rows = emblem.height
    if (cols == 0 || rows == 0) return
    val cell = minOf(size.width / cols, size.height / rows)
    val gridWidth = cell * cols
    val gridHeight = cell * rows
    val originX = (size.width - gridWidth) / 2f
    val originY = (size.height - gridHeight) / 2f
    // Overlap +0.5px menghindari celah hairline antar sel pada skala pecahan.
    val cellSize = Size(cell + 0.5f, cell + 0.5f)
    for (y in 0 until rows) {
        val row = emblem.rows[y]
        val width = minOf(row.length, cols)
        for (x in 0 until width) {
            val gridColor = emblem.palette[row[x]] ?: continue
            val color = monoColor ?: Color(gridColor)
            drawRect(
                color = color,
                topLeft = Offset(originX + x * cell, originY + y * cell),
                size = cellSize,
            )
        }
    }
}
