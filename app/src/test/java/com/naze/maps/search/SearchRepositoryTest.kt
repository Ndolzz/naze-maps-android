package com.naze.maps.search

import com.naze.maps.network.NetworkModule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Characterization tests (SDD TASK-003) — SearchRepository outcome mapping
 * via MockWebServer. Behavior yang dibekukan sesuai baseline (commit sebelum refactor):
 * blank -> Empty (tanpa request), IOException -> NoInternet, lainnya -> Error.
 */
class SearchRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var repo: SearchRepository
    private var running = true

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        repo = SearchRepository(
            api = NetworkModule.create(server.url("/").toString(), NominatimApi::class.java),
        )
    }

    @After
    fun tearDown() {
        if (running) server.shutdown()
    }

    private val oneResultBody = """
        [
          {"place_id": 123, "display_name": "Monas, Jakarta, Indonesia", "lat": "-6.1754", "lon": "106.8272", "type": "attraction"}
        ]
    """.trimIndent()

    @Test
    fun blankQueryReturnsEmptyWithoutNetworkCall() = runTest {
        assertEquals(SearchOutcome.Empty, repo.search("   "))
        assertEquals(0, server.requestCount)
    }

    @Test
    fun successParsesResults() = runTest {
        server.enqueue(MockResponse().setBody(oneResultBody))
        val outcome = repo.search("monas")
        assertTrue(outcome is SearchOutcome.Success)
        val results = (outcome as SearchOutcome.Success).results
        assertEquals(1, results.size)
        assertEquals(-6.1754, results[0].latitude, 1e-6)
        assertEquals(106.8272, results[0].longitude, 1e-6)
        assertEquals("Monas", results[0].mainText)
        assertEquals("Jakarta, Indonesia", results[0].subText)
    }

    @Test
    fun emptyArrayReturnsEmptyOutcome() = runTest {
        server.enqueue(MockResponse().setBody("[]"))
        assertEquals(SearchOutcome.Empty, repo.search("zzzz-not-a-place"))
    }

    @Test
    fun serverErrorReturnsErrorOutcome() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("boom"))
        val outcome = repo.search("monas")
        assertTrue(outcome is SearchOutcome.Error)
    }

    @Test
    fun connectionFailureReturnsNoInternet() = runTest {
        val deadUrl = server.url("/").toString()
        server.shutdown()
        running = false
        val deadRepo = SearchRepository(api = NetworkModule.create(deadUrl, NominatimApi::class.java))
        assertEquals(SearchOutcome.NoInternet, deadRepo.search("monas"))
    }

    @Test
    fun geocodeOneReturnsFirstResult() = runTest {
        server.enqueue(MockResponse().setBody(oneResultBody))
        val result = repo.geocodeOne("monas")
        assertNotNull(result)
        assertEquals("Monas", result!!.mainText)
    }

    @Test
    fun geocodeOneReturnsNullOnEmpty() = runTest {
        server.enqueue(MockResponse().setBody("[]"))
        assertNull(repo.geocodeOne("zzzz"))
    }
}
