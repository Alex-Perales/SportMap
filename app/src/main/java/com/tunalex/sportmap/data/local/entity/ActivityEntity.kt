package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String,           // "running", "cycling", "futbol", etc.
    val distanceKm: Double,
    val durationMinutes: Int,
    val placeId: Long? = null,
    val date: Long = System.currentTimeMillis()
)
