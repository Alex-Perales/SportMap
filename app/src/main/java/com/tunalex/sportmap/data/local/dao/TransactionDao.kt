package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsForUser(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    fun getTransactionsByStatus(userId: Long, status: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getTransactionsByType(userId: Long, type: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND status = 'completed'")
    fun getTotalSpentByUser(userId: Long): Flow<Double>

    @Query("SELECT * FROM transactions WHERE transactionHash = :hash LIMIT 1")
    suspend fun getTransactionByHash(hash: String): TransactionEntity?

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId AND status = 'completed'")
    fun getCompletedTransactionCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM transactions WHERE referenceId = :referenceId AND referenceType = :referenceType")
    fun getTransactionsByReference(referenceId: Long, referenceType: String): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    suspend fun deleteTransactionsForUser(userId: Long)

    @Query("SELECT * FROM transactions WHERE createdAt BETWEEN :startTime AND :endTime ORDER BY createdAt DESC")
    fun getTransactionsInDateRange(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>
}
