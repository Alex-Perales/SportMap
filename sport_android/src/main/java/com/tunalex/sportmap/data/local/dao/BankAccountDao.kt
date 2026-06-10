package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Insert
    suspend fun insert(bankAccount: BankAccountEntity): Long

    @Update
    suspend fun update(bankAccount: BankAccountEntity)

    @Delete
    suspend fun delete(bankAccount: BankAccountEntity)

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId")
    fun getBankAccountsByUserId(userId: Long): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId AND isDefault = 1")
    fun getDefaultBankAccount(userId: Long): Flow<BankAccountEntity?>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId AND isActive = 1")
    fun getActiveBankAccounts(userId: Long): Flow<List<BankAccountEntity>>

    @Query("UPDATE bank_accounts SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultForUser(userId: Long)

    @Query("UPDATE bank_accounts SET isDefault = 1 WHERE id = :accountId")
    suspend fun setAsDefault(accountId: Long)

    @Query("DELETE FROM bank_accounts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: Long)

    @Query("SELECT COUNT(*) FROM bank_accounts WHERE userId = :userId")
    fun getAccountCountForUser(userId: Long): Flow<Int>
}
