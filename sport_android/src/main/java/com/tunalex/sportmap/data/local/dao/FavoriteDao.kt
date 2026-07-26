package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.FavoriteEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("INSERT INTO favorites (userId, placeId) VALUES (:userId, :placeId)")
    suspend fun insert(userId: Long, placeId: Long)

    @Query("DELETE FROM favorites WHERE userId = :userId AND placeId = :placeId")
    suspend fun delete(userId: Long, placeId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND placeId = :placeId)")
    fun isFavorite(userId: Long, placeId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND placeId = :placeId)")
    suspend fun isFavoriteOnce(userId: Long, placeId: Long): Boolean

    @Query(
        """SELECT places.* FROM places
           INNER JOIN favorites ON favorites.placeId = places.id
           WHERE favorites.userId = :userId
           ORDER BY favorites.createdAt DESC"""
    )
    fun observeFavoritePlaces(userId: Long): Flow<List<PlaceEntity>>
}
