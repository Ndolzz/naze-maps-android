package com.naze.maps.emblems

/**
 * WMAP-R3 (ADR-006 #4): a pixel-art emblem defined as a pure-Kotlin ASCII grid plus a
 * color palette. No binary assets, no emoji — the grid is reviewable and unit-testable.
 *
 * Conventions:
 * - Every row must have the same width (12 px for the current set).
 * - '.' marks a transparent pixel; every other character must exist in [palette].
 * - Palette values are ARGB colors stored as Long (0xAARRGGBB) to keep this file pure
 *   Kotlin (an 0xFF...... literal does not fit in an Int literal in Kotlin).
 */
class PixelEmblem(
    val name: String,
    val palette: Map<Char, Long>,
    val rows: List<String>,
) {
    val width: Int get() = rows.firstOrNull()?.length ?: 0
    val height: Int get() = rows.size
}
