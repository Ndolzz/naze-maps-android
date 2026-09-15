package com.naze.maps.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Shared OkHttp/Retrofit setup for the two free, keyless providers the PWA already used:
 * Nominatim (search) and OSRM demo (routing). No secrets here by design (see requirement #15) —
 * if a paid/keyed provider is swapped in later, read the key from local.properties / env,
 * never hardcode it in source.
 */
object NetworkModule {

    // Nominatim's usage policy requires a descriptive User-Agent identifying the app.
    private val userAgentInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "NazeMaps-Android/1.0 (contact: naze-app)")
            .build()
        chain.proceed(request)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun <T> create(baseUrl: String, service: Class<T>): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
}
