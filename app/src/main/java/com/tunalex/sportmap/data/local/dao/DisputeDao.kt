package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.DisputeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DisputeDao {

    @Insert
    suspend fun insert(dispute: DisputeEntity): Long

    @Update
    suspend fun update(dispute: DisputeEntity)

    @Delete
    suspend fun delete(dispute: DisputeEntity)

    @Query("SELECT * FROM disputes WHERE initiatedByUserId = :userId ORDER BY createdAt DESC")
    fun getDisputesByUser(userId: Long): Flow<List<DisputeEntity>>

    @Query("SELECT * FROM disputes WHERE reservationId = :reservationId")
    fun getDisputesByReservation(reservationId: Long): Flow<DisputeEntity?>

    @Query("SELECT * FROM disputes WHERE status = :status ORDER BY createdAt DESC")
    fun getDisputesByStatus(status: String): Flow<List<DisputeEntity>>

    @Query("SELECT * FROM disputes WHERE assignedToAdminId = :adminId AND status = 'in_review' ORDER BY createdAt ASC")
    fun getOpenDisputesForAdmin(adminId: Long): Flow<List<DisputeEntity>>

    @Query("SELECT COUNT(*) FROM disputes WHERE status = 'open'")
    fun getOpenDisputesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM disputes WHERE initiatedByUserId = :userId AND status = 'open'")
    fun getOpenDisputesCountForUser(userId: Long): Flow<Int>

    @Query("DELETE FROM disputes WHERE reservationId = :reservationId")
    suspend fun deleteDisputesByReservation(reservationId: Long)

    @Query("DELETE FROM disputes WHERE initiatedByUserId = :userId")
    suspend fun deleteDisputesByUser(userId: Long)
}
