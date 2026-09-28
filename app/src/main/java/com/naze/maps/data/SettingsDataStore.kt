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

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface SettingsDataStore {
    val isDarkTheme: Flow<Boolean>
    val distanceUnit: Flow<DistanceUnit>
    suspend fun setDarkTheme(enabled: Boolean)
    suspend fun setDistanceUnit(unit: DistanceUnit)
}

class SettingsDataStoreImpl(private val context: Context) : SettingsDataStore {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val DISTANCE_UNIT = stringPreferencesKey("distance_unit")
    }

    override val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_THEME] ?: true }

    override val distanceUnit: Flow<DistanceUnit> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.DISTANCE_UNIT] ?: DistanceUnit.KM.name
        runCatching { DistanceUnit.valueOf(raw) }.getOrDefault(DistanceUnit.KM)
    }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    override suspend fun setDistanceUnit(unit: DistanceUnit) {
        context.dataStore.edit { it[Keys.DISTANCE_UNIT] = unit.name }
    }
}
