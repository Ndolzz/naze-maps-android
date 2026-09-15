package com.naze.maps.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyLocationFab(isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
    ) {
        Icon(Icons.Filled.MyLocation, contentDescription = "Lokasi saya")
    }
}

/** Toggles the satellite imagery overlay on/off. */
@Composable
fun LayersFab(isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface,
    ) {
        Icon(Icons.Filled.Layers, contentDescription = "Tampilan satellite")
    }
}

/**
 * Compass button — rotates to always point toward true north on screen and, per requirement #6,
 * resets the camera bearing to north when tapped. Hidden by the caller when [headingDegrees]
 * stays null (no rotation sensor on device).
 */
@Composable
fun CompassFab(headingDegrees: Float?, onResetNorth: () -> Unit, modifier: Modifier = Modifier) {
    if (headingDegrees == null) return
    val animatedRotation = animateFloatAsState(targetValue = -headingDegrees, label = "compass").value
    FloatingActionButton(
        onClick = onResetNorth,
        modifier = modifier.size(48.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Icon(
            Icons.Filled.Explore,
            contentDescription = "Reset arah ke utara",
            modifier = Modifier.rotate(animatedRotation),
        )
    }
}
