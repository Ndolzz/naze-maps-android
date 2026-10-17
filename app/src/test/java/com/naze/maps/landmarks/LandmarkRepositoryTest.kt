package com.naze.maps.landmarks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WMAP-R2 AC2.1/AC2.2/AC2.3: dataset integrity of the curated landmark set.
 */
class LandmarkRepositoryTest {

    private val repository = LandmarkRepositoryImpl()

    @Test
    fun `dataset contains exactly the six curated landmarks`() {
        val ids = repository.landmarks.map { it.id }.toSet()
        assertEquals(
            setOf("borobudur", "eiffel", "giza", "colosseum", "taj-mahal", "great-wall-badaling"),
            ids,
        )
    }

    @Test
    fun `all coordinates are within valid geographic ranges`() {
        for (landmark in repository.landmarks) {
            assertTrue(
                "${landmark.id} latitude out of range: ${landmark.latitude}",
                landmark.latitude >= -90.0 && landmark.latitude <= 90.0,
            )
            assertTrue(
                "${landmark.id} longitude out of range: ${landmark.longitude}",
                landmark.longitude >= -180.0 && landmark.longitude <= 180.0,
            )
        }
    }

    @Test
    fun `coordinates match the verified reference values`() {
        val expected = mapOf(
            "borobudur" to (-7.6079 to 110.2038),
            "eiffel" to (48.8584 to 2.2945),
            "giza" to (29.9792 to 31.1342),
            "colosseum" to (41.8902 to 12.4922),
            "taj-mahal" to (27.1751 to 78.0421),
            "great-wall-badaling" to (40.3544 to 116.0157),
        )
        for (landmark in repository.landmarks) {
            val (lat, lng) = expected.getValue(landmark.id)
            assertEquals("${landmark.id} latitude mismatch", lat, landmark.latitude, 1e-4)
            assertEquals("${landmark.id} longitude mismatch", lng, landmark.longitude, 1e-4)
        }
    }

    @Test
    fun `descriptions and names are non-blank`() {
        for (landmark in repository.landmarks) {
            assertTrue(landmark.name.isNotBlank())
            assertTrue(landmark.description.isNotBlank())
            assertTrue(landmark.country.isNotBlank())
        }
    }

    @Test
    fun `tier minzoom values match the spec`() {
        assertEquals(0.0, ProminenceTier.WORLD.minZoom, 0.0)
        assertEquals(4.0, ProminenceTier.REGION.minZoom, 0.0)
        assertEquals(8.0, ProminenceTier.LOCAL.minZoom, 0.0)
    }

    @Test
    fun `every category references an emblem that exists`() {
        for (category in LandmarkCategory.entries) {
            assertFalse(category.emblemName.isBlank())
        }
    }
}
