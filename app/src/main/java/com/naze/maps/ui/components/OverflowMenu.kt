package com.naze.maps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Destinations of the app. Moved here from BottomNavBar.kt as part of CH-101 (BUG-017):
 * the permanent bottom navigation was removed in favor of a compact overflow menu, so this
 * enum outlives its old host file. Order = display order in the menu.
 */
enum class NazeTab(val label: String) {
    MAP("Map"), FAVORITES("Favorites"), DISTANCE("Distance"), SETTINGS("Settings")
}

/**
 * CH-101 (BUG-017, ADR-005): compact overflow menu that replaces the permanent bottom
 * navigation so the map can render edge-to-edge. Same four destinations, same state-based
 * tab switcher in NazeNavHost — only the affordance changed.
 *
 * Compact circular 40dp surface button with a lightweight border; placed by the caller
 * (top-end, status-bar inset aware). Touch target stays comfortable via IconButton.
 */
@Composable
fun NavOverflowMenu(selected: NazeTab, onSelect: (NazeTab) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        ) {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "Menu navigasi",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            NazeTab.values().forEach { tab ->
                DropdownMenuItem(
                    text = { Text(tab.label) },
                    leadingIcon = { Icon(iconForTab(tab), contentDescription = null) },
                    trailingIcon = { if (tab == selected) Text("•") },
                    onClick = {
                        expanded = false
                        onSelect(tab)
                    },
                )
            }
        }
    }
}

private fun iconForTab(tab: NazeTab): ImageVector = when (tab) {
    NazeTab.MAP -> Icons.Filled.Place
    NazeTab.FAVORITES -> Icons.Filled.Favorite
    NazeTab.DISTANCE -> Icons.Filled.Straighten
    NazeTab.SETTINGS -> Icons.Filled.Settings
}
