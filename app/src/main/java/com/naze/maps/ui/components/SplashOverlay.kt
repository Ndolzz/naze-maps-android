package com.naze.maps.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naze.maps.R
import com.naze.maps.ui.theme.NazeAccent
import com.naze.maps.ui.theme.NazeBgDark
import com.naze.maps.ui.theme.NazeRoute
import com.naze.maps.ui.theme.NazeTextDark
import com.naze.maps.ui.theme.NazeTextDimDark

// Durasi total reveal — sesuai spec CH-102 (<= 900ms), tanpa infinite loop.
private const val SPLASH_DURATION_MS = 900

/**
 * CH-102 (BUG-018): brand splash presentation — "MAP COMES ALIVE".
 *
 * Sequence: dark background -> subtle route-line reveal + location dot (map context) ->
 * Naze mark fade + subtle scale (0.92 -> 1.0, NO rotation) -> "NAZE MAPS" typography fade-in.
 * Semua sub-animasi diturunkan dari SATU progress (<= 900ms, sekali jalan, tanpa loop).
 *
 * Startup logic per spec: VISUAL dan INITIALIZATION dipisah. [isReady] adalah sinyal
 * readiness sesungguhnya (map style loaded — lihat MapScreen); begitu true, splash
 * dismiss via fade-out pemanggil, bahkan di tengah animasi — aplikasi tidak pernah
 * membuat user menunggu demi visual. Jika initialization lambat, splash berhenti di
 * frame akhirnya (statis, berlabel) sampai ready — tanpa spinner loop, tanpa delay(3000).
 *
 * Composable ini PRESENTATION ONLY — tanpa logika map/search/routing/location.
 */
@Composable
fun SplashOverlay(isReady: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    // One-shot reveal; single source of truth for all sub-animations below.
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(SPLASH_DURATION_MS, easing = FastOutSlowInEasing))
    }

    // READY -> hand off to the map. Memotong animasi jika initialization lebih cepat,
    // per spec: kecepatan startup tidak dikorbankan demi visual.
    LaunchedEffect(isReady) {
        if (isReady) onDismiss()
    }

    val p = progress.value
    val logoAlpha = (p / 0.45f).coerceIn(0f, 1f)
    val logoScale = 0.92f + 0.08f * logoAlpha
    val textAlpha = ((p - 0.4f) / 0.4f).coerceIn(0f, 1f)
    val routeReveal = ((p - 0.15f) / 0.5f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NazeBgDark),
    ) {
        // Subtle map context: route line yang tumbuh melintasi layar di belakang mark,
        // dengan location dot di titik awal dan marker di tujuan. Mask/reveal via clipRect.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            if (routeReveal <= 0f) return@Canvas

            val start = Offset(w * 0.10f, h * 0.82f)
            val end = Offset(w * 0.92f, h * 0.24f)
            val path = Path().apply {
                moveTo(start.x, start.y)
                cubicTo(w * 0.45f, h * 0.88f, w * 0.40f, h * 0.55f, w * 0.58f, h * 0.48f)
                cubicTo(w * 0.75f, h * 0.42f, w * 0.88f, h * 0.40f, end.x, end.y)
            }

            clipRect(0f, 0f, w * routeReveal, h) {
                drawPath(
                    path = path,
                    color = NazeRoute.copy(alpha = 0.35f),
                    style = Stroke(width = 6f),
                )
            }

            // Location dot di titik awal — muncul bersama garis.
            drawCircle(
                color = NazeAccent.copy(alpha = 0.8f * routeReveal),
                radius = 7f,
                center = start,
            )
            // Marker di tujuan — menutup narasi rute.
            if (routeReveal >= 1f) {
                drawCircle(color = Color.White, radius = 5f, center = end)
                drawCircle(color = NazeRoute.copy(alpha = 0.6f), radius = 12f, center = end)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .alpha(logoAlpha)
                    .scale(logoScale),
            )
            Text(
                text = "NAZE MAPS",
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 6.sp,
                ),
                color = NazeTextDark,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .alpha(textAlpha),
            )
            Text(
                text = stringResource(R.string.tagline),
                style = TextStyle(fontSize = 11.sp, letterSpacing = 1.5.sp),
                color = NazeTextDimDark,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(textAlpha),
            )
            // Label status statis — tanpa spinner, tanpa loop; sekaligus fallback surface
            // INITIALIZING/ERROR jika readiness tertunda (map loading state di bawahnya).
            Text(
                text = "Menyiapkan peta…",
                style = TextStyle(fontSize = 10.sp),
                color = NazeTextDimDark.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(top = 28.dp)
                    .alpha(textAlpha),
            )
        }
    }
}
