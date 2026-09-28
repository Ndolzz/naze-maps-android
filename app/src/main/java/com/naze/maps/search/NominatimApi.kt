package com.naze.maps.search

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("limit") limit: Int = 8,
        @Query("accept-language") acceptLanguage: String = "id",
    ): List<NominatimResult>

    // CH-113: reverse geocoding untuk titik yang ditekan lama di peta.
    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Query("zoom") zoom: Int = 16,
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") acceptLanguage: String = "id",
    ): NominatimResult

    companion object {
        const val BASE_URL = "https://nominatim.openstreetmap.org/"
    }
}
