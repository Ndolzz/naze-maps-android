package com.naze.maps.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naze.maps.ui.components.NavOverflowMenu
import com.naze.maps.ui.components.NazeTab
import com.naze.maps.ui.screens.DistanceScreen
import com.naze.maps.ui.screens.FavoritesScreen
import com.naze.maps.ui.screens.MapScreen
import com.naze.maps.ui.screens.SettingsScreen

/**
 * Simple state-based tab switcher rather than navigation-compose's back-stack — the four
 * tabs are peers, not a drill-down hierarchy, so this keeps things un-over-engineered.
 *
 * CH-101 (BUG-017, ADR-005): the permanent bottom bar is gone. Destinations are now reached
 * through the compact overflow menu [⋮], overlaid top-end on every screen and inset-aware
 * (statusBarsPadding — no hardcoded paddings). The map tab renders edge-to-edge because
 * MapScreen manages its own overlay insets; other screens keep using the Scaffold padding.
 *
 * CH-106: FavoritesScreen receives an onNavigateToMap callback so the one-tap route button
 * can land the user on the map tab, where the route line and any banner are visible.
 */
@Composable
fun NazeNavHost(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(NazeTab.MAP) }

    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                NazeTab.MAP -> MapScreen(modifier = Modifier.fillMaxSize())
                NazeTab.FAVORITES -> FavoritesScreen(
                    onNavigateToMap = { selectedTab = NazeTab.MAP },
                    modifier = Modifier.fillMaxSize().padding(padding),
                )
                NazeTab.DISTANCE -> DistanceScreen(modifier = Modifier.padding(padding))
                NazeTab.SETTINGS -> SettingsScreen(modifier = Modifier.padding(padding))
            }

            NavOverflowMenu(
                selected = selectedTab,
                onSelect = { selectedTab = it },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 12.dp, end = 12.dp),
            )
        }
    }
}
