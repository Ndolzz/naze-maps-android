package com.naze.maps.routing

import com.google.gson.annotations.SerializedName

data class OsrmResponse(
    @SerializedName("code") val code: String,
    @SerializedName("routes") val routes: List<OsrmRoute> = emptyList(),
)

data class OsrmRoute(
    @SerializedName("distance") val distanceMeters: Double,
    @SerializedName("duration") val durationSeconds: Double,
    @SerializedName("geometry") val geometry: OsrmGeometry,
)

data class OsrmGeometry(
    @SerializedName("coordinates") val coordinates: List<List<Double>>, // [lng, lat] pairs
)

enum class RoutingProfile(val osrmName: String) {
    DRIVING("driving"),
    WALKING("foot"),
    CYCLING("bike"),
}
