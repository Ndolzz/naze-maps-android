package com.naze.maps.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.naze.maps.utils.DistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "naze_settings")

/** CH-111: mode tema aplikasi. SYSTEM berarti mengikuti mode terang atau gelap perangkat. */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface SettingsDataStore {
    val isDarkTheme: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val distanceUnit: Flow<DistanceUnit>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDistanceUnit(unit: DistanceUnit)
}

class SettingsDataStoreImpl(private val context: Context) : SettingsDataStore {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
    }

    override val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_THEME] ?: true }

    // CH-111: mode baru theme_mode. Migrasi: bila theme_mode belum pernah ditulis, mode
    // diturunkan dari sakelar dark_theme lama; bila keduanya belum ada, default SYSTEM.
    override val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.THEME_MODE]
        if (raw != null) {
            runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
        } else {
            val legacyDark = prefs[Keys.DARK_THEME]
            when {
                legacyDark == null -> ThemeMode.SYSTEM
                legacyDark -> ThemeMode.DARK
                else -> ThemeMode.LIGHT
            }
        }
    }

    override val distanceUnit: Flow<DistanceUnit> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.DISTANCE_UNIT] ?: DistanceUnit.KM.name
        runCatching { DistanceUnit.valueOf(raw) }.getOrDefault(DistanceUnit.KM)
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        context.dataStore.edit { it[Keys.DISTANCE_UNIT] = unit.name }
    }
}
