package com.naze.maps.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM search_history ORDER BY visitedAtMillis DESC LIMIT 20")
    fun observeRecent(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: HistoryEntity): Long

    @Query("DELETE FROM search_history WHERE latitude = :lat AND longitude = :lng")
    suspend fun deleteAt(lat: Double, lng: Double)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()

    // Keeps the table from growing forever — only the 30 most recent visits are kept.
    @Query(
        "DELETE FROM search_history WHERE id NOT IN " +
            "(SELECT id FROM search_history ORDER BY visitedAtMillis DESC LIMIT 30)",
    )
    suspend fun trimOld()
}
