package com.naze.maps.map

/**
 * Style URLs — free, keyless vector-tile styles compatible with MapLibre's style spec.
 * OpenFreeMap hosts community-run, no-signup tile infra: no API key, no payment method,
 * no account required at all.
 */
object MapStyle {
    const val LIGHT = "https://tiles.openfreemap.org/styles/liberty"
    const val DARK = "https://tiles.openfreemap.org/styles/dark"

    fun forTheme(isDark: Boolean): String = if (isDark) DARK else LIGHT
}
