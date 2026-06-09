package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.UserKycEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserKycDao {

    @Insert
    suspend fun insert(userKyc: UserKycEntity): Long

    @Update
    suspend fun update(userKyc: UserKycEntity)

    @Delete
    suspend fun delete(userKyc: UserKycEntity)

    @Query("SELECT * FROM user_kyc WHERE userId = :userId")
    fun getUserKycByUserId(userId: Long): Flow<UserKycEntity?>

    @Query("SELECT * FROM user_kyc WHERE userId = :userId")
    suspend fun getUserKycByUserIdOnce(userId: Long): UserKycEntity?

    @Query("SELECT * FROM user_kyc WHERE status = :status")
    fun getKycByStatus(status: String): Flow<List<UserKycEntity>>

    @Query("SELECT COUNT(*) FROM user_kyc WHERE status = 'approved'")
    fun getApprovedKycCount(): Flow<Int>

    @Query("DELETE FROM user_kyc WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)
}
