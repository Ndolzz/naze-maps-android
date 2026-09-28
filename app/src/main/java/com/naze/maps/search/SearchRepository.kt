package com.naze.maps.search

import com.naze.maps.network.NetworkModule
import java.io.IOException
import java.util.Locale

sealed class SearchOutcome {
    data class Success(val results: List<NominatimResult>) : SearchOutcome()
    object NoInternet : SearchOutcome()
    object Empty : SearchOutcome()
    data class Error(val message: String) : SearchOutcome()
}

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface SearchRepository {
    suspend fun search(query: String): SearchOutcome
    suspend fun geocodeOne(query: String): NominatimResult?

    /** CH-113: alamat untuk satu titik (tekan lama di peta); null bila gagal atau kosong. */
    suspend fun reverseGeocode(lat: Double, lon: Double): NominatimResult?
}

class SearchRepositoryImpl(
    private val api: NominatimApi = NetworkModule.create(NominatimApi.BASE_URL, NominatimApi::class.java),
) : SearchRepository {
    override suspend fun search(query: String): SearchOutcome {
        if (query.isBlank()) return SearchOutcome.Empty
        return try {
            val results = api.search(query, acceptLanguage = Locale.getDefault().toLanguageTag())
            if (results.isEmpty()) SearchOutcome.Empty else SearchOutcome.Success(results)
        } catch (e: IOException) {
            SearchOutcome.NoInternet
        } catch (e: Exception) {
            SearchOutcome.Error(e.message ?: "Pencarian gagal")
        }
    }

    /** Used by the distance calculator to resolve a single place name to coordinates. */
    override suspend fun geocodeOne(query: String): NominatimResult? =
        (search(query) as? SearchOutcome.Success)?.results?.firstOrNull()

    /** CH-113: dipakai sheet tekan lama untuk menampilkan nama tempat di atas koordinat. */
    override suspend fun reverseGeocode(lat: Double, lon: Double): NominatimResult? =
        try {
            api.reverse(lat, lon)
        } catch (e: Exception) {
            null
        }
}
