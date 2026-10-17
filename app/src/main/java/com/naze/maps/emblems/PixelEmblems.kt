package com.naze.maps.emblems

/**
 * WMAP-R3: original pixel-art emblems (12x12 grids). Names double as MapLibre sprite ids
 * ("emblem-*" for map sprites). Colors follow the Naze blue-based design system.
 *
 * Registry is name-keyed; LandmarkCategory.emblemName references the landmark emblems,
 * the floating dock references NAVIGATE / EXPLORE / COLLECTION / SETTINGS, the map uses
 * CLUSTER + GRATICULE, and the biome legend uses the biome emblems.
 */
object PixelEmblems {

    val TEMPLE = PixelEmblem(
        name = "emblem-temple",
        palette = mapOf(
            'g' to 0xFFE0B84CL, // gilded stupa
            's' to 0xFF9C8E76L, // volcanic stone
            'd' to 0xFF4E9C5AL, // ground green
        ),
        rows = listOf(
            "............",
            ".....gg.....",
            "....gggg....",
            ".....gg.....",
            ".....ss.....",
            "....ssss....",
            "....ssss....",
            "....ssss....",
            "....ssss....",
            "...ssssss...",
            "..ssssssss..",
            ".dddddddddd.",
        ),
    )

    val TOWER = PixelEmblem(
        name = "emblem-tower",
        palette = mapOf('w' to 0xFF37474FL), // wrought iron
        rows = listOf(
            ".....ww.....",
            ".....ww.....",
            "....w..w....",
            "....w..w....",
            "...w....w...",
            "...w....w...",
            "..w......w..",
            "..w......w..",
            ".w........w.",
            ".ww......ww.",
            ".wwwwwwwwww.",
            "..w......w..",
        ),
    )

    val PYRAMID = PixelEmblem(
        name = "emblem-pyramid",
        palette = mapOf('s' to 0xFFE3C57AL), // sandstone
        rows = listOf(
            "............",
            ".....ss.....",
            "....ssss....",
            "....ssss....",
            "...ssssss...",
            "...ssssss...",
            "..ssssssss..",
            "..ssssssss..",
            ".ssssssssss.",
            ".ssssssssss.",
            "ssssssssssss",
            "ssssssssssss",
        ),
    )

    val ARENA = PixelEmblem(
        name = "emblem-arena",
        palette = mapOf(
            'a' to 0xFFB08D57L, // travertine
            'o' to 0xFF4A3B2AL, // arch openings
        ),
        rows = listOf(
            "...aaaaaa...",
            "..a......a..",
            ".a.oo..oo.a.",
            ".a.oo..oo.a.",
            "a..........a",
            "a.oo....oo.a",
            "a.oo....oo.a",
            ".a........a.",
            ".a.oo..oo.a.",
            "..a......a..",
            "...aaaaaa...",
            "............",
        ),
    )

    val MAUSOLEUM = PixelEmblem(
        name = "emblem-mausoleum",
        palette = mapOf(
            'w' to 0xFFF4F1EAL, // white marble
            'd' to 0xFFD9D4C5L, // marble shade
        ),
        rows = listOf(
            "............",
            ".....ww.....",
            "....wwww....",
            "...wwwwww...",
            "...wddddw...",
            "....wwww....",
            "...wwwwww...",
            "..wwwwwwww..",
            ".wwwwwwwwww.",
            ".wddddddddw.",
            "wwwwwwwwwwww",
            "wwwwwwwwwwww",
        ),
    )

    val WALL = PixelEmblem(
        name = "emblem-wall",
        palette = mapOf(
            'c' to 0xFF9A8F7AL, // rampart stone
            'o' to 0xFF544739L, // watch openings
        ),
        rows = listOf(
            "cc.cc.cc.cc.",
            "cccccccccccc",
            "c.o......o.c",
            "c.o......o.c",
            "cccccccccccc",
            "cccccccccccc",
            "c.o......o.c",
            "c.o......o.c",
            "cccccccccccc",
            "cccccccccccc",
            "cccccccccccc",
            "cccccccccccc",
        ),
    )

    val NAVIGATE = PixelEmblem(
        name = "emblem-navigate",
        palette = mapOf(
            'c' to 0xFF2563EBL, // primary blue
            'b' to 0xFF1D4ED8L, // deep blue core
        ),
        rows = listOf(
            ".....cc.....",
            "....cccc....",
            "...cc..cc...",
            "..cc....cc..",
            ".cc..bb..cc.",
            "cc...bb...cc",
            ".cc..bb..cc.",
            "..cc....cc..",
            "...cc..cc...",
            "....cccc....",
            ".....cc.....",
            "............",
        ),
    )

    val EXPLORE = PixelEmblem(
        name = "emblem-explore",
        palette = mapOf(
            'p' to 0xFFE4572EL, // start marker
            'h' to 0xFF2563EBL, // route path
            'e' to 0xFF16A34AL, // end marker
        ),
        rows = listOf(
            "pp..........",
            "pp..........",
            "..hh........",
            "...hh.......",
            "....hh......",
            ".....hh.....",
            "......hh....",
            ".......hh...",
            "........hh..",
            "..........ee",
            "............",
            "............",
        ),
    )

    val COLLECTION = PixelEmblem(
        name = "emblem-collection",
        palette = mapOf(
            'c' to 0xFF8B5A2BL, // chest wood
            'l' to 0xFFE0B84CL, // golden lock
        ),
        rows = listOf(
            "............",
            ".cccccccccc.",
            ".c........c.",
            ".c........c.",
            ".cccccccccc.",
            "....llll....",
            "....llll....",
            ".cccccccccc.",
            ".c........c.",
            ".cccccccccc.",
            "............",
            "............",
        ),
    )

    val SETTINGS = PixelEmblem(
        name = "emblem-settings",
        palette = mapOf('s' to 0xFF64748BL), // slate gear
        rows = listOf(
            "...ss..ss...",
            "..ssssssss..",
            "..ssssssss..",
            ".ss..ss..ss.",
            ".ss..ss..ss.",
            "ssss....ssss",
            "ssss....ssss",
            ".ss..ss..ss.",
            ".ss..ss..ss.",
            "..ssssssss..",
            "..ssssssss..",
            "...ss..ss...",
        ),
    )

    val CLUSTER = PixelEmblem(
        name = "emblem-cluster",
        palette = mapOf('c' to 0xFF1D4ED8L), // deep blue cluster diamond
        rows = listOf(
            "............",
            ".....cc.....",
            "....cccc....",
            "...cccccc...",
            "..cccccccc..",
            ".cccccccccc.",
            "..cccccccc..",
            "...cccccc...",
            "....cccc....",
            ".....cc.....",
            "............",
            "............",
        ),
    )

    val GRATICULE = PixelEmblem(
        name = "emblem-graticule",
        palette = mapOf('g' to 0xFF2563EBL),
        rows = listOf(
            "...gggggg...",
            "..g..gg..g..",
            ".g...gg...g.",
            ".g..g..g..g.",
            "gg..g..g..gg",
            "gg..g..g..gg",
            ".g..g..g..g.",
            ".g...gg...g.",
            "..g..gg..g..",
            "...gggggg...",
            "............",
            "............",
        ),
    )

    // ---- Biome emblems (legend, WMAP-R1/R3) ----

    val FOREST = PixelEmblem(
        name = "emblem-biome-forest",
        palette = mapOf(
            'g' to 0xFF3F8F4FL, // canopy
            't' to 0xFF7A5230L, // trunk
        ),
        rows = listOf(
            "....gggg....",
            "...gggggg...",
            "..gggggggg..",
            "..gggggggg..",
            "...gggggg...",
            "....gggg....",
            ".....tt.....",
            ".....tt.....",
            ".....tt.....",
            "....tttt....",
            "............",
            "............",
        ),
    )

    val DESERT = PixelEmblem(
        name = "emblem-biome-desert",
        palette = mapOf('s' to 0xFFE3C57AL),
        rows = listOf(
            "............",
            ".....ss.....",
            "....ssss....",
            "............",
            "............",
            "............",
            "............",
            "............",
            "..ssss......",
            ".sssssss....",
            "sssssssssss.",
            "............",
        ),
    )

    val MOUNTAIN = PixelEmblem(
        name = "emblem-biome-mountain",
        palette = mapOf(
            's' to 0xFF8C8578L, // rock
            'w' to 0xFFF4F7FAL, // snow cap
        ),
        rows = listOf(
            "......ww....",
            ".....wssw...",
            "....wssssw..",
            "...wssssssw.",
            "..wssssssssw",
            "......ss....",
            ".....ssss...",
            "....ssssss..",
            "...ssssssss.",
            "..ssssssssss",
            "............",
            "............",
        ),
    )

    val TUNDRA = PixelEmblem(
        name = "emblem-biome-tundra",
        palette = mapOf('s' to 0xFFBFE3F2L), // ice blue
        rows = listOf(
            "............",
            "....s..s....",
            "..s..ss..s..",
            "...ssssss...",
            "s..ss..ss..s",
            "ssss....ssss",
            "ssss....ssss",
            "s..ss..ss..s",
            "...ssssss...",
            "..s..ss..s..",
            "....s..s....",
            "............",
        ),
    )

    val GRASSLAND = PixelEmblem(
        name = "emblem-biome-grassland",
        palette = mapOf('g' to 0xFF7CBF6AL),
        rows = listOf(
            "............",
            "....g..g....",
            "....g..g....",
            ".g..g..g..g.",
            ".g..g..g..g.",
            ".gg.gg.gg.g.",
            "..gggggggg..",
            "...gggggg...",
            "............",
            "............",
            "............",
            "............",
        ),
    )

    val WETLAND = PixelEmblem(
        name = "emblem-biome-wetland",
        palette = mapOf(
            'r' to 0xFF4E9C5AL, // reeds
            'w' to 0xFF4FA3D9L, // water
        ),
        rows = listOf(
            "............",
            "...r....r...",
            "...r....r...",
            "...r....r...",
            "...r....r...",
            "...r....r...",
            "............",
            "wwwwwwwwwwww",
            "wwwwwwwwwwww",
            "wwwwwwwwwwww",
            "............",
            "............",
        ),
    )

    val WATER = PixelEmblem(
        name = "emblem-biome-water",
        palette = mapOf('w' to 0xFF4FA3D9L),
        rows = listOf(
            "............",
            "............",
            "..ww..ww....",
            ".wwwwwwww...",
            "wwwwwwwwwww.",
            "............",
            "............",
            "....ww..ww..",
            "...wwwwwwww.",
            ".wwwwwwwwww.",
            "............",
            "............",
        ),
    )

    val LANDMARK_EMBLEMS = listOf(TEMPLE, TOWER, PYRAMID, ARENA, MAUSOLEUM, WALL)
    val BIOME_EMBLEMS = listOf(FOREST, DESERT, MOUNTAIN, TUNDRA, GRASSLAND, WETLAND, WATER)

    fun all(): List<PixelEmblem> = LANDMARK_EMBLEMS + listOf(
        NAVIGATE, EXPLORE, COLLECTION, SETTINGS, CLUSTER, GRATICULE,
    ) + BIOME_EMBLEMS

    fun byName(name: String): PixelEmblem? = all().firstOrNull { it.name == name }
}
