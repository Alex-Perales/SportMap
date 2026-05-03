package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert
    suspend fun insert(reservation: ReservationEntity): Long

    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date ASC")
    fun observeByUser(userId: Long): Flow<List<ReservationEntity>>

    @Query("""
        SELECT * FROM reservations
        WHERE userId = :userId AND date >= :now
        ORDER BY date ASC LIMIT 1
    """)
    fun observeNextReservation(userId: Long, now: Long): Flow<ReservationEntity?>

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteById(id: Long)
}
