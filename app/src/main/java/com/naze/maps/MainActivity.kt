package com.naze.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naze.maps.ui.navigation.NazeNavHost
import com.naze.maps.ui.screens.MapViewModel
import com.naze.maps.ui.theme.NazeMapsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate() per the SplashScreen API contract.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // requirement #3: true edge-to-edge, not a webview wrapped in a status bar

        setContent {
            // Theme preference is read here (not per-screen) so every tab shares one source of truth.
            val sharedViewModel: MapViewModel = viewModel()
            val uiState by sharedViewModel.uiState.collectAsState()

            NazeMapsTheme(darkTheme = uiState.isDarkTheme) {
                NazeNavHost()
            }
        }
    }
}
