package com.naze.maps.routing

import com.naze.maps.network.NetworkModule
import com.naze.maps.utils.GeoPoint
import java.io.IOException
import java.util.Locale

sealed class RouteOutcome {
    data class Success(val routes: List<OsrmRoute>) : RouteOutcome()
    object NoInternet : RouteOutcome()
    object NoRouteFound : RouteOutcome()
    data class Error(val message: String) : RouteOutcome()
}

class RoutingRepository(
    private val api: OsrmApi = NetworkModule.create(OsrmApi.BASE_URL, OsrmApi::class.java),
) {
    suspend fun getRoute(from: GeoPoint, to: GeoPoint, profile: RoutingProfile): RouteOutcome {
        val coords = String.format(
            Locale.US, "%f,%f;%f,%f", from.lng, from.lat, to.lng, to.lat,
        )
        return try {
            val response = api.route(profile.osrmName, coords)
            if (response.code != "Ok" || response.routes.isEmpty()) {
                RouteOutcome.NoRouteFound
            } else {
                RouteOutcome.Success(response.routes)
            }
        } catch (e: IOException) {
            RouteOutcome.NoInternet
        } catch (e: Exception) {
            RouteOutcome.Error(e.message ?: "Gagal memuat rute")
        }
    }
}
