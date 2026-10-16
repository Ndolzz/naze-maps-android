package com.naze.maps.landmarks

/**
 * WMAP-R2: landmark category — drives its pixel emblem ([emblemName] matches a
 * PixelEmblems registry name).
 */
enum class LandmarkCategory(val emblemName: String, val label: String) {
    TEMPLE("emblem-temple", "Candi / Kuil"),
    TOWER("emblem-tower", "Menara"),
    PYRAMID("emblem-pyramid", "Piramida"),
    ARENA("emblem-arena", "Amfiteater"),
    MAUSOLEUM("emblem-mausoleum", "Mausoleum"),
    FORTIFICATION("emblem-wall", "Fortifikasi"),
}

/**
 * WMAP-R2/WMAP-R6: at which camera zoom a landmark becomes prominent on the world map.
 * Used as the minzoom of the landmark's symbol layer (AC2.3).
 */
enum class ProminenceTier(val minZoom: Double) {
    WORLD(0.0),
    REGION(4.0),
    LOCAL(8.0),
}
