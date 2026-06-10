package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medals")
data class MedalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,                       // "Corredor madrugador"
    val description: String,                // "Completa 5 carreras antes de las 7am"
    val iconKey: String,                    // identificador para mapear a un Icons.Filled
    val earned: Boolean = false,
    val earnedDate: Long? = null,
    val tier: String = "bronze"             // bronze, silver, gold
)
