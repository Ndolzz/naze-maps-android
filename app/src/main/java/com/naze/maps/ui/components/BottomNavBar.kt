package com.naze.maps.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.naze.maps.emblems.PixelEmblem
import com.naze.maps.emblems.PixelEmblems

/**
 * WMAP-R4: setiap tab kini membawa emblem pixel-art-nya sendiri (AC3.1, tanpa emoji).
 */
enum class NazeTab(val label: String, val emblem: PixelEmblem) {
    MAP("Map", PixelEmblems.NAVIGATE),
    FAVORITES("Favorites", PixelEmblems.COLLECTION),
    DISTANCE("Distance", PixelEmblems.EXPLORE),
    SETTINGS("Settings", PixelEmblems.SETTINGS),
}

/**
 * WMAP-R4: adaptive floating dock — pengganti NavigationBar penuh-lebar yang lama.
 *
 * - Mengapung di atas konten (overlay), bukan mengurangi ruang layar, sehingga peta
 *   tidak pernah tertutup penuh (AC4.2).
 * - Bisa diciutkan menjadi satu tombol chevron (AC4.3).
 * - Lebar maksimum tetap muat pada layar 360dp (AC4.4).
 * - Setiap tombol memanggil navigasi tab nyata — tidak ada kontrol dekoratif (AC4.1).
 */
@Composable
fun NazeFloatingDock(
    selected: NazeTab,
    onSelect: (NazeTab) -> Unit,
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (!isCollapsed) {
                NazeTab.entries.forEach { tab ->
                    DockItem(
                        label = tab.label,
                        emblem = tab.emblem,
                        isSelected = selected == tab,
                        onClick = { onSelect(tab) },
                    )
                }
            }
            IconButton(onClick = onToggleCollapse) {
                Icon(
                    imageVector = if (isCollapsed) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isCollapsed) "Bentangkan dock navigasi" else "Ciutkan dock navigasi",
                )
            }
        }
    }
}

@Composable
private fun DockItem(
    label: String,
    emblem: PixelEmblem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else Color.Transparent,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PixelEmblemIcon(
                emblem = emblem,
                iconSize = 22.dp,
                contentDescription = label,
                monoColor = if (isSelected) MaterialTheme.colorScheme.primary else null,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
