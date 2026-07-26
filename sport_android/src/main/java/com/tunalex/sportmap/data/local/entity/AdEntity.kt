package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ads")
data class AdEntity(
    @PrimaryKey val id: Long,
    val imageUrl: String,
    val badgeText: String? = null,
    val title: String,
    val subtitle: String? = null,
    val price: Double? = null,
    val linkType: String,      // "none" | "premium" | "product" | "external"
    val linkTarget: String? = null,
    val sortOrder: Int = 0
)
