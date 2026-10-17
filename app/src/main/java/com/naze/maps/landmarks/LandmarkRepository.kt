package com.naze.maps.landmarks

/**
 * WMAP-R2: read access to the curated landmark dataset. Interface + Impl follows the
 * baseline repository pattern (TASK-010b) so MapViewModel stays testable with fakes.
 */
interface LandmarkRepository {
    val landmarks: List<Landmark>
}

class LandmarkRepositoryImpl : LandmarkRepository {
    override val landmarks: List<Landmark> = LANDMARKS
}

/**
 * Curated world landmarks — coordinates verified against public references on
 * 2026-10-16 (sources noted per entry). Facts kept short and verifiable; no invented
 * data (Gate B rule). This is a static curated set, NOT a dynamic POI layer — the
 * generic-POI non-goal of SDD-020 stays in force (ADR-006).
 */
private val LANDMARKS = listOf(
    Landmark(
        id = "borobudur",
        name = "Candi Borobudur",
        country = "Indonesia",
        category = LandmarkCategory.TEMPLE,
        latitude = -7.6079,
        longitude = 110.2038,
        tier = ProminenceTier.REGION,
        description = "Candi Buddha Mahayana abad ke-9 di Magelang, Jawa Tengah — " +
            "candi Buddha terbesar di dunia dan Situs Warisan Dunia UNESCO. " +
            "(Koordinat: latlong.net / worldplacesexplained.com)",
    ),
    Landmark(
        id = "eiffel",
        name = "Menara Eiffel",
        country = "Prancis",
        category = LandmarkCategory.TOWER,
        latitude = 48.8584,
        longitude = 2.2945,
        tier = ProminenceTier.WORLD,
        description = "Menara besi setinggi 330 m di Champ de Mars, Paris, selesai 1889 — " +
            "dirancang Gustave Eiffel. (Koordinat: latlong.net)",
    ),
    Landmark(
        id = "giza",
        name = "Piramida Giza",
        country = "Mesir",
        category = LandmarkCategory.PYRAMID,
        latitude = 29.9792,
        longitude = 31.1342,
        tier = ProminenceTier.WORLD,
        description = "Piramida Khufu (±2560 SM) di kompleks Giza, Kairo — " +
            "satu-satunya keajaiban dunia kuno yang masih berdiri. (Koordinat: latlong.net)",
    ),
    Landmark(
        id = "colosseum",
        name = "Colosseum",
        country = "Italia",
        category = LandmarkCategory.ARENA,
        latitude = 41.8902,
        longitude = 12.4922,
        tier = ProminenceTier.REGION,
        description = "Amfiteater Flavia (selesai 80 M) di pusat Roma — " +
            "amfiteater terbesar yang pernah dibangun, Situs Warisan Dunia UNESCO. " +
            "(Koordinat: latlong.net)",
    ),
    Landmark(
        id = "taj-mahal",
        name = "Taj Mahal",
        country = "India",
        category = LandmarkCategory.MAUSOLEUM,
        latitude = 27.1751,
        longitude = 78.0421,
        tier = ProminenceTier.WORLD,
        description = "Mausoleum marmer di Agra, selesai 1653 — dibangun kaisar Mughal " +
            "Shah Jahan untuk Mumtaz Mahal; Situs Warisan Dunia UNESCO. " +
            "(Koordinat: findlatitudeandlongitude.com; selaras rentang latitude.to)",
    ),
    Landmark(
        id = "great-wall-badaling",
        name = "Tembok Besar China (Badaling)",
        country = "China",
        category = LandmarkCategory.FORTIFICATION,
        latitude = 40.3544,
        longitude = 116.0157,
        tier = ProminenceTier.WORLD,
        description = "Bagian paling terkenal Tembok Besar Dinasti Ming (total ±21.196 km); " +
            "titik: gerbang utama Badaling, Distrik Yanqing, Beijing. " +
            "(Koordinat: chinatrailfolk.com / greatwallforum.com)",
    ),
)
