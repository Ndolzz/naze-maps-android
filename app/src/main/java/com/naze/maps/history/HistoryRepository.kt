package com.naze.maps.history

import android.content.Context
import kotlinx.coroutines.flow.Flow

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface HistoryRepository {
    fun observeRecent(): Flow<List<HistoryEntity>>
    suspend fun record(name: String, address: String, lat: Double, lng: Double)
    suspend fun remove(entry: HistoryEntity)
    suspend fun clear()
}

class HistoryRepositoryImpl(context: Context) : HistoryRepository {
    private val dao = HistoryDatabase.getInstance(context).historyDao()

    override fun observeRecent(): Flow<List<HistoryEntity>> = dao.observeRecent()

    /** Records a visited place. Dedupes by coordinates so re-searching the same place just bumps it to the top. */
    override suspend fun record(name: String, address: String, lat: Double, lng: Double) {
        dao.deleteAt(lat, lng)
        dao.insert(HistoryEntity(name = name, address = address, latitude = lat, longitude = lng))
        dao.trimOld()
    }

    override suspend fun remove(entry: HistoryEntity) {
        dao.deleteAt(entry.latitude, entry.longitude)
    }

    override suspend fun clear() {
        dao.clearAll()
    }
}
