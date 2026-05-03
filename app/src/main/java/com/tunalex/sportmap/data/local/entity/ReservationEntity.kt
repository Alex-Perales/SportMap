package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val placeId: Long,
    val placeName: String,
    val date: Long,                 // millis
    val time: String,               // "18:30"
    val peopleCount: Int,
    val status: String = "confirmed",
    val createdAt: Long = System.currentTimeMillis()
)
