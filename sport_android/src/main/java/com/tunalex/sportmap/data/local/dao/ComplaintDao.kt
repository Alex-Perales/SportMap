package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.ComplaintEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {

    @Insert
    suspend fun insert(complaint: ComplaintEntity): Long

    @Update
    suspend fun update(complaint: ComplaintEntity)

    @Delete
    suspend fun delete(complaint: ComplaintEntity)

    @Query("SELECT * FROM complaints WHERE userId = :userId ORDER BY createdAt DESC")
    fun getComplaintsByUser(userId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE complaintAgainstUserId = :userId ORDER BY createdAt DESC")
    fun getComplaintsAgainstUser(userId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE complaintAgainstPlaceId = :placeId ORDER BY createdAt DESC")
    fun getComplaintsAgainstPlace(placeId: Long): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE status = :status ORDER BY createdAt DESC")
    fun getComplaintsByStatus(status: String): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE severity = :severity ORDER BY createdAt DESC")
    fun getComplaintsBySeverity(severity: String): Flow<List<ComplaintEntity>>

    @Query("SELECT COUNT(*) FROM complaints WHERE complaintAgainstUserId = :userId AND status = 'submitted'")
    fun getUnresolvedComplaintsCountAgainstUser(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM complaints WHERE status = 'submitted'")
    fun getUnresolvedComplaintsCount(): Flow<Int>

    @Query("DELETE FROM complaints WHERE userId = :userId")
    suspend fun deleteComplaintsByUser(userId: Long)

    @Query("DELETE FROM complaints WHERE complaintAgainstUserId = :userId")
    suspend fun deleteComplaintsAgainstUser(userId: Long)
}
