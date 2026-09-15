package com.naze.maps.routing

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsrmApi {
    @GET("route/v1/{profile}/{coordinates}")
    suspend fun route(
        @Path("profile") profile: String,
        @Path("coordinates") coordinates: String, // "lng1,lat1;lng2,lat2"
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson",
        @Query("alternatives") alternatives: Boolean = true,
    ): OsrmResponse

    companion object {
        const val BASE_URL = "https://router.project-osrm.org/"
    }
}
