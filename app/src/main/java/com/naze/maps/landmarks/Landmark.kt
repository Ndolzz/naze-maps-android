package com.naze.maps.landmarks

/**
 * WMAP-R2: a curated world landmark. Pure model — no SDK imports (ADR-002).
 */
data class Landmark(
    val id: String,
    val name: String,
    val country: String,
    val category: LandmarkCategory,
    val latitude: Double,
    val longitude: Double,
    val tier: ProminenceTier,
    val description: String,
)
