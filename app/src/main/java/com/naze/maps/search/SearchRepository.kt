package com.naze.maps.search

import com.naze.maps.network.NetworkModule
import java.io.IOException

sealed class SearchOutcome {
    data class Success(val results: List<NominatimResult>) : SearchOutcome()
    object NoInternet : SearchOutcome()
    object Empty : SearchOutcome()
    data class Error(val message: String) : SearchOutcome()
}

class SearchRepository(
    private val api: NominatimApi = NetworkModule.create(NominatimApi.BASE_URL, NominatimApi::class.java),
) {
    suspend fun search(query: String): SearchOutcome {
        if (query.isBlank()) return SearchOutcome.Empty
        return try {
            val results = api.search(query)
            if (results.isEmpty()) SearchOutcome.Empty else SearchOutcome.Success(results)
        } catch (e: IOException) {
            SearchOutcome.NoInternet
        } catch (e: Exception) {
            SearchOutcome.Error(e.message ?: "Pencarian gagal")
        }
    }

    /** Used by the distance calculator to resolve a single place name to coordinates. */
    suspend fun geocodeOne(query: String): NominatimResult? =
        (search(query) as? SearchOutcome.Success)?.results?.firstOrNull()
}
