package com.naze.maps.utils

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class GeoPoint(val lat: Double, val lng: Double, val label: String = "")

enum class DistanceUnit { KM, MI }
enum class TravelMode(val speedKmh: Double) { WALKING(4.8), CYCLING(16.0), DRIVING(38.0) }

object DistanceUtils {

    /** Haversine great-circle distance in kilometers. Same formula as the PWA. */
    fun haversineKm(a: GeoPoint, b: GeoPoint): Double {
        val r = 6371.0
        val dLat = Math.toRadians(b.lat - a.lat)
        val dLng = Math.toRadians(b.lng - a.lng)
        val h = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(a.lat)) * cos(Math.toRadians(b.lat)) * sin(dLng / 2).pow(2)
        return 2 * r * asin(sqrt(h))
    }

    fun format(km: Double, unit: DistanceUnit): String = when (unit) {
        DistanceUnit.KM -> "%.1f km".format(km)
        DistanceUnit.MI -> "%.1f mi".format(km * 0.621371)
    }

    fun etaMinutes(km: Double, mode: TravelMode): Int = ((km / mode.speedKmh) * 60).toInt()
}
