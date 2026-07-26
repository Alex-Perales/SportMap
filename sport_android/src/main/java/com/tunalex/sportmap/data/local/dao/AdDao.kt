package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tunalex.sportmap.data.local.entity.AdEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ads: List<AdEntity>)

    @Query("DELETE FROM ads")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(ads: List<AdEntity>) {
        clear()
        insertAll(ads)
    }

    @Query("SELECT * FROM ads ORDER BY sortOrder ASC, id ASC")
    fun observeAll(): Flow<List<AdEntity>>
}
