package com.naze.maps.routing

import com.naze.maps.network.NetworkModule
import com.naze.maps.utils.GeoPoint
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Characterization tests (SDD TASK-003) — RoutingRepository outcome mapping
 * via MockWebServer: code != "Ok" ATAU routes kosong -> NoRouteFound;
 * IOException -> NoInternet; lainnya -> Error.
 * TASK-010b: repo kini interface; test menguji RoutingRepositoryImpl langsung.
 */
class RoutingRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var repo: RoutingRepository
    private var running = true

    private val from = GeoPoint(-6.1754, 106.8272)
    private val to = GeoPoint(-6.2, 106.9)

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        repo = RoutingRepositoryImpl(
            api = NetworkModule.create(server.url("/").toString(), OsrmApi::class.java),
        )
    }

    @After
    fun tearDown() {
        if (running) server.shutdown()
    }

    private val okBody = """
        {"code": "Ok", "routes": [
          {"distance": 1000.0, "duration": 120.0, "geometry": {"coordinates": [[106.8272, -6.1754], [106.9, -6.2]]}}
        ]}
    """.trimIndent()

    @Test
    fun successReturnsRoutesAndUsesProfilePath() = runTest {
        server.enqueue(MockResponse().setBody(okBody))
        val outcome = repo.getRoute(from, to, RoutingProfile.DRIVING)
        assertTrue(outcome is RouteOutcome.Success)
        val route = (outcome as RouteOutcome.Success).routes.first()
        assertEquals(1000.0, route.distanceMeters, 1e-6)
        assertEquals(120.0, route.durationSeconds, 1e-6)
        assertEquals(2, route.geometry.coordinates.size)
        val request = server.takeRequest()
        assertTrue(request.path!!.startsWith("/route/v1/driving/"))
    }

    @Test
    fun nonOkCodeReturnsNoRouteFound() = runTest {
        server.enqueue(MockResponse().setBody("""{"code": "NoRoute", "routes": []}"""))
        assertEquals(RouteOutcome.NoRouteFound, repo.getRoute(from, to, RoutingProfile.WALKING))
    }

    @Test
    fun okCodeWithEmptyRoutesReturnsNoRouteFound() = runTest {
        server.enqueue(MockResponse().setBody("""{"code": "Ok", "routes": []}"""))
        assertEquals(RouteOutcome.NoRouteFound, repo.getRoute(from, to, RoutingProfile.CYCLING))
    }

    @Test
    fun serverErrorReturnsErrorOutcome() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("boom"))
        val outcome = repo.getRoute(from, to, RoutingProfile.DRIVING)
        assertTrue(outcome is RouteOutcome.Error)
    }

    @Test
    fun connectionFailureReturnsNoInternet() = runTest {
        val deadUrl = server.url("/").toString()
        server.shutdown()
        running = false
        val deadRepo = RoutingRepositoryImpl(api = NetworkModule.create(deadUrl, OsrmApi::class.java))
        assertEquals(RouteOutcome.NoInternet, deadRepo.getRoute(from, to, RoutingProfile.DRIVING))
    }
}
