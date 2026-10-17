package com.naze.maps.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WMAP-R5 AC5.1/AC5.2: zoom→interval mapping and graticule generation.
 * org.maplibre.geojson is a pure Java library, so these run as plain unit tests.
 */
class GraticuleTest {

    private val lineFeatures = { interval: Double ->
        Graticule.generate(interval).features().orEmpty().filter { it.geometry() is org.maplibre.geojson.LineString }
    }

    @Test
    fun `interval adapts to zoom`() {
        assertEquals(10.0, Graticule.intervalForZoom(0.0), 0.0)
        assertEquals(10.0, Graticule.intervalForZoom(3.9), 0.0)
        assertEquals(5.0, Graticule.intervalForZoom(4.0), 0.0)
        assertEquals(5.0, Graticule.intervalForZoom(5.9), 0.0)
        assertEquals(1.0, Graticule.intervalForZoom(6.0), 0.0)
        assertEquals(1.0, Graticule.intervalForZoom(15.0), 0.0)
    }

    @Test
    fun `10 degree graticule has 17 parallels and 36 meridians`() {
        val lines = lineFeatures(10.0)
        assertEquals(17, lines.count { it.getStringProperty("kind") == "latitude" })
        assertEquals(36, lines.count { it.getStringProperty("kind") == "longitude" })
    }

    @Test
    fun `finer interval produces more lines`() {
        assertTrue(lineFeatures(1.0).size > lineFeatures(10.0).size)
    }

    @Test
    fun `equator and prime meridian exist and are major`() {
        val lines = lineFeatures(10.0)
        val equator = lines.first {
            it.getStringProperty("kind") == "latitude" && it.getNumberProperty("value") == 0.0
        }
        val prime = lines.first {
            it.getStringProperty("kind") == "longitude" && it.getNumberProperty("value") == 0.0
        }
        assertTrue(equator.getBooleanProperty("major"))
        assertTrue(prime.getBooleanProperty("major"))
        assertEquals("0°", equator.getStringProperty("label"))
        assertEquals("0°", prime.getStringProperty("label"))
    }

    @Test
    fun `only equator and prime meridian are major`() {
        val majors = lineFeatures(10.0).filter { it.getBooleanProperty("major") }
        assertEquals(2, majors.size)
    }

    @Test
    fun `edge label points are emitted for the graticule symbol layer`() {
        val points = Graticule.generate(5.0).features().orEmpty()
            .filter { it.geometry() is org.maplibre.geojson.Point }
        assertTrue(points.isNotEmpty())
        assertTrue(points.all { it.getStringProperty("kind") == "label" })
        assertTrue(points.all { it.hasProperty("label") })
    }

    @Test
    fun `label formatting is hemisphere correct`() {
        assertEquals("10°N", Graticule.formatLatitude(10.0))
        assertEquals("10°S", Graticule.formatLatitude(-10.0))
        assertEquals("20°E", Graticule.formatLongitude(20.0))
        assertEquals("20°W", Graticule.formatLongitude(-20.0))
        assertEquals("0°", Graticule.formatLatitude(0.0))
    }
}
