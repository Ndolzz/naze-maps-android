package com.naze.maps.search

import com.google.gson.annotations.SerializedName

data class NominatimResult(
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String,
    @SerializedName("type") val type: String? = null,
) {
    val latitude: Double get() = lat.toDouble()
    val longitude: Double get() = lon.toDouble()

    /** First segment of display_name as the "main" line, rest as subtitle — mirrors PWA's sr-main/sr-sub. */
    val mainText: String get() = displayName.substringBefore(",")
    val subText: String get() = displayName.substringAfter(",", "").trim()
}
