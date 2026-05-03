package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert
    suspend fun insert(item: CartItemEntity): Long

    @Update
    suspend fun update(item: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun observeByUser(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT IFNULL(SUM(unitPrice * quantity), 0.0) FROM cart_items WHERE userId = :userId")
    fun observeTotal(userId: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    fun observeCount(userId: Long): Flow<Int>

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearForUser(userId: Long)
}
