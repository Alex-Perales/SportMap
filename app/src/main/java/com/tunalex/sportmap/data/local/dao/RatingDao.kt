package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {

    @Insert
    suspend fun insert(rating: RatingEntity): Long

    @Update
    suspend fun update(rating: RatingEntity)

    @Delete
    suspend fun delete(rating: RatingEntity)

    @Query("SELECT * FROM ratings WHERE toUserId = :userId")
    fun getRatingsForUser(userId: Long): Flow<List<RatingEntity>>

    @Query("SELECT * FROM ratings WHERE fromUserId = :userId")
    fun getRatingsFromUser(userId: Long): Flow<List<RatingEntity>>

    @Query("SELECT AVG(rating) FROM ratings WHERE toUserId = :userId")
    fun getAverageRatingForUser(userId: Long): Flow<Double>

    @Query("SELECT * FROM ratings WHERE reservationId = :reservationId")
    fun getRatingsByReservation(reservationId: Long): Flow<List<RatingEntity>>

    @Query("SELECT COUNT(*) FROM ratings WHERE toUserId = :userId")
    fun getRatingCountForUser(userId: Long): Flow<Int>

    @Query("SELECT * FROM ratings WHERE fromUserId = :fromUserId AND toUserId = :toUserId LIMIT 1")
    suspend fun getRatingBetweenUsers(fromUserId: Long, toUserId: Long): RatingEntity?

    @Query("DELETE FROM ratings WHERE toUserId = :userId")
    suspend fun deleteRatingsForUser(userId: Long)
}
