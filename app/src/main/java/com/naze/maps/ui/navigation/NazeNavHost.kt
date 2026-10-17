package com.naze.maps.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naze.maps.ui.components.NazeFloatingDock
import com.naze.maps.ui.components.NazeTab
import com.naze.maps.ui.screens.DistanceScreen
import com.naze.maps.ui.screens.FavoritesScreen
import com.naze.maps.ui.screens.MapScreen
import com.naze.maps.ui.screens.SettingsScreen

/**
 * Simple state-based tab switcher rather than navigation-compose's back-stack — the four
 * tabs are peers (like the PWA's sidebar/bottom-nav), not a drill-down hierarchy, so this
 * keeps things un-over-engineered per requirement #16.
 *
 * WMAP-R4: Scaffold diganti Box full-bleed + floating dock overlay. MapScreen dirender
 * full-bleed di belakang dock (peta tidak pernah terpotong oleh sistem navigasi);
 * layar non-peta memakai statusBarsPadding sendiri karena inset Scaffold sudah tidak ada.
 */
@Composable
fun NazeNavHost(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(NazeTab.MAP) }
    var dockCollapsed by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (selectedTab) {
            NazeTab.MAP -> MapScreen(modifier = Modifier.fillMaxSize())
            NazeTab.FAVORITES -> FavoritesScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 96.dp),
            )
            NazeTab.DISTANCE -> DistanceScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 96.dp),
            )
            NazeTab.SETTINGS -> SettingsScreen(
                modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 96.dp),
            )
        }
        NazeFloatingDock(
            selected = selectedTab,
            onSelect = { selectedTab = it },
            isCollapsed = dockCollapsed,
            onToggleCollapse = { dockCollapsed = !dockCollapsed },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
        )
    }
}
