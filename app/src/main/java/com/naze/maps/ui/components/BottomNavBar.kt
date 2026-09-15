package com.naze.maps.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class NazeTab(val label: String) {
    MAP("Map"), FAVORITES("Favorites"), DISTANCE("Distance"), SETTINGS("Settings")
}

@Composable
fun NazeBottomNav(selected: NazeTab, onSelect: (NazeTab) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier.height(72.dp)) {
        NavigationBarItem(
            selected = selected == NazeTab.MAP,
            onClick = { onSelect(NazeTab.MAP) },
            icon = { Icon(Icons.Filled.Place, contentDescription = null) },
            label = { Text("Map") },
        )
        NavigationBarItem(
            selected = selected == NazeTab.FAVORITES,
            onClick = { onSelect(NazeTab.FAVORITES) },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            label = { Text("Favorites") },
        )
        NavigationBarItem(
            selected = selected == NazeTab.DISTANCE,
            onClick = { onSelect(NazeTab.DISTANCE) },
            icon = { Icon(Icons.Filled.Straighten, contentDescription = null) },
            label = { Text("Distance") },
        )
        NavigationBarItem(
            selected = selected == NazeTab.SETTINGS,
            onClick = { onSelect(NazeTab.SETTINGS) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Settings") },
        )
    }
}
