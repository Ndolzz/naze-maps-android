package com.naze.maps.emblems

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * WMAP-R3 AC3.1/AC3.2: emblem grids must be structurally valid and every landmark
 * category emblem must be present in the registry.
 */
class PixelEmblemTest {

    @Test
    fun `all emblems have uniform non-empty rows`() {
        for (emblem in PixelEmblems.all()) {
            assertTrue("emblem ${emblem.name} has no rows", emblem.rows.isNotEmpty())
            val width = emblem.rows.first().length
            assertTrue("emblem ${emblem.name} has an empty first row", width > 0)
            emblem.rows.forEach { row ->
                assertEquals("emblem ${emblem.name} row width mismatch: '$row'", width, row.length)
            }
        }
    }

    @Test
    fun `all pixels are transparent or defined in the palette`() {
        for (emblem in PixelEmblems.all()) {
            emblem.rows.forEachIndexed { y, row ->
                row.forEachIndexed { x, ch ->
                    assertTrue(
                        "emblem ${emblem.name} has undefined color '$ch' at ($x,$y)",
                        ch == '.' || emblem.palette.containsKey(ch),
                    )
                }
            }
        }
    }

    @Test
    fun `emblem names are unique`() {
        val names = PixelEmblems.all().map { it.name }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `landmark category emblem names resolve to real emblems`() {
        val expected = listOf(
            "emblem-temple", "emblem-tower", "emblem-pyramid",
            "emblem-arena", "emblem-mausoleum", "emblem-wall",
        )
        for (name in expected) {
            assertNotNull("missing emblem for landmark category: $name", PixelEmblems.byName(name))
        }
    }

    @Test
    fun `dock, cluster, graticule and biome emblems exist`() {
        for (name in listOf(
            "emblem-navigate", "emblem-explore", "emblem-collection", "emblem-settings",
            "emblem-cluster", "emblem-graticule",
            "emblem-biome-forest", "emblem-biome-desert", "emblem-biome-mountain",
            "emblem-biome-tundra", "emblem-biome-grassland", "emblem-biome-wetland",
            "emblem-biome-water",
        )) {
            assertNotNull("missing emblem: $name", PixelEmblems.byName(name))
        }
    }

    @Test
    fun `palette colors are opaque ARGB values`() {
        for (emblem in PixelEmblems.all()) {
            emblem.palette.values.forEach { argb ->
                assertEquals("emblem ${emblem.name} color must be opaque", 0xFFL, argb ushr 24)
            }
        }
    }
}
