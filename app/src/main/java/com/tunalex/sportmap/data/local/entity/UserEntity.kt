package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val district: String = "Miraflores",
    val isPremium: Boolean = false,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
