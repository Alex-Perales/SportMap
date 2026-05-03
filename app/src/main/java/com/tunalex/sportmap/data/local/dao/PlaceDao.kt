package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Query("SELECT * FROM places")
    fun observeAll(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE sportType = :sportType")
    fun observeBySport(sportType: String): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): PlaceEntity?

    @Query("SELECT COUNT(*) FROM places")
    suspend fun count(): Int
}
