package com.naze.maps.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.naze.maps.ui.components.NazeBottomNav
import com.naze.maps.ui.components.NazeTab
import com.naze.maps.ui.screens.DistanceScreen
import com.naze.maps.ui.screens.FavoritesScreen
import com.naze.maps.ui.screens.MapScreen
import com.naze.maps.ui.screens.SettingsScreen

/**
 * Simple state-based tab switcher rather than navigation-compose's back-stack — the four
 * tabs are peers (like the PWA's sidebar/bottom-nav), not a drill-down hierarchy, so this
 * keeps things un-over-engineered per requirement #16.
 */
@Composable
fun NazeNavHost(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(NazeTab.MAP) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { NazeBottomNav(selected = selectedTab, onSelect = { selectedTab = it }) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                NazeTab.MAP -> MapScreen(modifier = Modifier.fillMaxSize())
                NazeTab.FAVORITES -> FavoritesScreen(modifier = Modifier.fillMaxSize().padding(padding))
                NazeTab.DISTANCE -> DistanceScreen(modifier = Modifier.padding(padding))
                NazeTab.SETTINGS -> SettingsScreen(modifier = Modifier.padding(padding))
            }
        }
    }
}
