package com.naze.maps.favorites

import android.content.Context
import kotlinx.coroutines.flow.Flow

/** TASK-010b: abstraction so MapViewModel can be unit-tested with fakes. */
interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteEntity>>
    suspend fun save(name: String, address: String, lat: Double, lng: Double)
    suspend fun rename(favorite: FavoriteEntity, newName: String)
    suspend fun delete(favorite: FavoriteEntity)
    suspend fun isSaved(lat: Double, lng: Double): Boolean
}

class FavoritesRepositoryImpl(context: Context) : FavoritesRepository {
    private val dao = FavoritesDatabase.getInstance(context).favoriteDao()

    override fun observeFavorites(): Flow<List<FavoriteEntity>> = dao.observeAll()

    override suspend fun save(name: String, address: String, lat: Double, lng: Double) {
        dao.insert(FavoriteEntity(name = name, address = address, latitude = lat, longitude = lng))
    }

    override suspend fun rename(favorite: FavoriteEntity, newName: String) {
        dao.update(favorite.copy(name = newName))
    }

    override suspend fun delete(favorite: FavoriteEntity) {
        dao.delete(favorite)
    }

    override suspend fun isSaved(lat: Double, lng: Double): Boolean = dao.countAt(lat, lng) > 0
}
