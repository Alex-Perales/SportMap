package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tunalex.sportmap.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clear()

    /**
     * Reemplaza todo el catálogo local por el del backend en una sola
     * transacción. Necesario para que un producto borrado desde el panel
     * admin también desaparezca de la app (antes solo se hacían upserts, así
     * que el producto eliminado quedaba para siempre en Room).
     */
    @Transaction
    suspend fun replaceAll(products: List<ProductEntity>) {
        clear()
        insertAll(products)
    }

    @Query("SELECT * FROM products")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category = :category")
    fun observeByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ProductEntity?

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
