package com.naze.maps.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Characterization tests (SDD TASK-003) — membekukan perilaku DistanceUtils saat ini
 * SEBELUM refactor. Jangan "perbaiki" assertion jika perilaku berubah; perilaku berubah
 * harus lewat change request + spec update.
 *
 * Catatan: format() sengaja TIDAK diuji exact-string karena menggunakan default locale
 * ("%.1f".format) — lihat BUG-014 di docs/sdd/bugs.md.
 */
class DistanceUtilsTest {

    @Test
    fun samePointIsZero() {
        assertEquals(0.0, DistanceUtils.haversineKm(GeoPoint(-6.2, 106.8), GeoPoint(-6.2, 106.8)), 1e-9)
    }

    @Test
    fun oneDegreeLongitudeAtEquator() {
        // 2 * R * asin(sin(0.5 deg)) with R = 6371 km
        val d = DistanceUtils.haversineKm(GeoPoint(0.0, 0.0), GeoPoint(0.0, 1.0))
        assertEquals(111.195, d, 0.05)
    }

    @Test
    fun quarterMeridianIsQuarterCircumference() {
        // (0,0) -> (90,0): a quarter of a great circle = 2*pi*6371/4
        val d = DistanceUtils.haversineKm(GeoPoint(0.0, 0.0), GeoPoint(90.0, 0.0))
        assertEquals(10007.54, d, 0.1)
    }

    @Test
    fun formatKmEndsWithKm() {
        assertTrue(DistanceUtils.format(1.234, DistanceUnit.KM).endsWith("km"))
    }

    @Test
    fun formatMiEndsWithMi() {
        assertTrue(DistanceUtils.format(1.234, DistanceUnit.MI).endsWith("mi"))
    }

    @Test
    fun etaDrivingTenKmTruncates() {
        // (10 / 38) * 60 = 15.789 -> 15
        assertEquals(15, DistanceUtils.etaMinutes(10.0, TravelMode.DRIVING))
    }

    @Test
    fun etaWalkingFiveKmTruncates() {
        // (5 / 4.8) * 60 = 62.5 -> 62
        assertEquals(62, DistanceUtils.etaMinutes(5.0, TravelMode.WALKING))
    }
}
