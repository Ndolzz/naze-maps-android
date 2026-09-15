package com.naze.maps.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.naze.maps.R

/**
 * Branded loading state shown while the map style/tiles are still loading, so the first thing
 * the user sees is never a blank/grey rectangle — it fades out once the style finishes loading
 * (see the AnimatedVisibility wrapping this in MapScreen).
 */
@Composable
fun MapLoadingOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "naze-splash-pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "naze-splash-scale",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.naze_bg_dark)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(84.dp)
                    .scale(pulseScale),
            )
            Text(
                text = "NAZE Maps",
                color = colorResource(R.color.naze_text_dark),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 18.dp),
            )
            CircularProgressIndicator(
                color = colorResource(R.color.naze_primary),
                strokeWidth = 3.dp,
                modifier = Modifier
                    .padding(top = 22.dp)
                    .size(28.dp),
            )
        }
    }
}
