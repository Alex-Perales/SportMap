package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert
    suspend fun insert(review: ReviewEntity): Long

    @Update
    suspend fun update(review: ReviewEntity)

    @Delete
    suspend fun delete(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY createdAt DESC")
    fun getReviewsForPlace(placeId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY createdAt DESC")
    fun getReviewsByUser(userId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT AVG(rating) FROM reviews WHERE placeId = :placeId")
    fun getAverageRatingForPlace(placeId: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM reviews WHERE placeId = :placeId")
    fun getReviewCountForPlace(placeId: Long): Flow<Int>

    @Query("SELECT * FROM reviews WHERE placeId = :placeId AND userId = :userId LIMIT 1")
    suspend fun getUserReviewForPlace(placeId: Long, userId: Long): ReviewEntity?

    @Query("SELECT * FROM reviews WHERE placeId = :placeId AND rating = :rating")
    fun getReviewsByRating(placeId: Long, rating: Int): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews WHERE placeId = :placeId")
    suspend fun deleteReviewsForPlace(placeId: Long)

    @Query("DELETE FROM reviews WHERE userId = :userId")
    suspend fun deleteReviewsByUser(userId: Long)
}
