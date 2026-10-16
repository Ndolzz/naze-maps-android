package com.naze.maps.emblems

/**
 * WMAP-R1: biome colors of the bundled pixel-art styles. This is the Kotlin mirror of
 * the colors used in assets/styles/naze-pixel-light.json and naze-pixel-dark.json —
 * the biome legend renders from these constants and PixelStyleTest verifies that the
 * two sources stay in sync (AC1.2: biome colors come from real data classes, consistently).
 */
object PixelPalette {

    data class BiomeColors(
        val forest: Long,
        val grassland: Long,
        val farmland: Long,
        val desert: Long,
        val tundra: Long,
        val wetland: Long,
        val mountain: Long,
        val ocean: Long,
        val water: Long,
        val background: Long,
        val road: Long,
    )

    val LIGHT = BiomeColors(
        forest = 0xFF6FAF5CL,
        grassland = 0xFF9CCB7AL,
        farmland = 0xFFE5D68CL,
        desert = 0xFFF1E2A4L,
        tundra = 0xFFE8F5FDL,
        wetland = 0xFF71B389L,
        mountain = 0xFFB5ADA0L,
        ocean = 0xFF3E97D1L,
        water = 0xFF63B7E6L,
        background = 0xFFE8EFD9L,
        road = 0xFFE8A33DL,
    )

    val DARK = BiomeColors(
        forest = 0xFF2C5A3CL,
        grassland = 0xFF3E6B47L,
        farmland = 0xFF6E6034L,
        desert = 0xFFC0A24FL,
        tundra = 0xFF8FB9CEL,
        wetland = 0xFF27543EL,
        mountain = 0xFF57534CL,
        ocean = 0xFF12283FL,
        water = 0xFF1B3A5CL,
        background = 0xFF0E1626L,
        road = 0xFF60709AL,
    )

    fun forTheme(isDark: Boolean): BiomeColors = if (isDark) DARK else LIGHT
}
