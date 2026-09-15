package com.naze.maps.favorites

import android.content.Context
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(context: Context) {
    private val dao = FavoritesDatabase.getInstance(context).favoriteDao()

    fun observeFavorites(): Flow<List<FavoriteEntity>> = dao.observeAll()

    suspend fun save(name: String, address: String, lat: Double, lng: Double) {
        dao.insert(FavoriteEntity(name = name, address = address, latitude = lat, longitude = lng))
    }

    suspend fun rename(favorite: FavoriteEntity, newName: String) {
        dao.update(favorite.copy(name = newName))
    }

    suspend fun delete(favorite: FavoriteEntity) {
        dao.delete(favorite)
    }

    suspend fun isSaved(lat: Double, lng: Double): Boolean = dao.countAt(lat, lng) > 0
}
