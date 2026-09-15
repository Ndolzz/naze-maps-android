package com.naze.maps.history

import android.content.Context
import kotlinx.coroutines.flow.Flow

class HistoryRepository(context: Context) {
    private val dao = HistoryDatabase.getInstance(context).historyDao()

    fun observeRecent(): Flow<List<HistoryEntity>> = dao.observeRecent()

    /** Records a visited place. Dedupes by coordinates so re-searching the same place just bumps it to the top. */
    suspend fun record(name: String, address: String, lat: Double, lng: Double) {
        dao.deleteAt(lat, lng)
        dao.insert(HistoryEntity(name = name, address = address, latitude = lat, longitude = lng))
        dao.trimOld()
    }

    suspend fun remove(entry: HistoryEntity) {
        dao.deleteAt(entry.latitude, entry.longitude)
    }

    suspend fun clear() {
        dao.clearAll()
    }
}
