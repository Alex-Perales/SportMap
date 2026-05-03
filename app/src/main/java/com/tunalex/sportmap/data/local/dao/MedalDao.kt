package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.MedalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(medals: List<MedalEntity>)

    @Update
    suspend fun update(medal: MedalEntity)

    @Query("SELECT * FROM medals WHERE userId = :userId ORDER BY earned DESC, tier DESC")
    fun observeByUser(userId: Long): Flow<List<MedalEntity>>

    @Query("SELECT COUNT(*) FROM medals WHERE userId = :userId")
    suspend fun countForUser(userId: Long): Int
}
