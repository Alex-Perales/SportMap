package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<OrderEntity>)

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY id DESC")
    fun observeByUser(userId: Long): Flow<List<OrderEntity>>
}
