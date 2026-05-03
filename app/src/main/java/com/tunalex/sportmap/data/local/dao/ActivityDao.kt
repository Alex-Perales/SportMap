package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert
    suspend fun insert(activity: ActivityEntity): Long

    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY date DESC")
    fun observeByUser(userId: Long): Flow<List<ActivityEntity>>

    @Query("""
        SELECT SUM(distanceKm) FROM activities
        WHERE userId = :userId AND (type = 'running' OR type = 'cycling')
    """)
    fun observeTotalKm(userId: Long): Flow<Double?>

    @Query("SELECT COUNT(DISTINCT placeId) FROM activities WHERE userId = :userId AND placeId IS NOT NULL")
    fun observePlacesVisited(userId: Long): Flow<Int>
}
