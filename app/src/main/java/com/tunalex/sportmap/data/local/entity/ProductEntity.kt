package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,           // "calzado", "balones", "hidratación", "accesorios"
    val sizes: String,              // CSV: "S,M,L,XL" o "38,39,40,41,42"
    val stock: Int = 25,
    val isOnSale: Boolean = false,
    val discountPercent: Int = 0
)
