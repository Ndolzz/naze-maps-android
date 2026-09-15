package com.naze.maps.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class NazeLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val bearing: Float?, // heading in degrees, only present while moving / GPS-derived
)

private fun Location.toNaze() = NazeLocation(
    latitude = latitude,
    longitude = longitude,
    accuracyMeters = accuracy,
    bearing = if (hasBearing()) bearing else null,
)

/**
 * Wraps Play Services FusedLocationProviderClient. Used only for positioning — this app has
 * no dependency on Google Maps SDK, so it stays usable regardless of map engine choice.
 */
class LocationRepository(context: Context) {
    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission") // caller must check PermissionUtils.hasLocationPermission first
    suspend fun getCurrentLocationOnce(): NazeLocation? {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        val location = client.getCurrentLocation(request, null).await()
        return location?.toNaze()
    }

    /**
     * Continuous updates for the "My Location" blue-dot + camera-follow mode.
     * Per requirement #5, this only runs while the screen requesting it is active
     * (collectors should cancel when the map leaves the foreground).
     */
    @SuppressLint("MissingPermission")
    fun observeLocation(intervalMs: Long = 3000L): Flow<NazeLocation> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toNaze()) }
            }
        }

        client.requestLocationUpdates(request, callback, null)
        awaitClose { client.removeLocationUpdates(callback) }
    }
}
