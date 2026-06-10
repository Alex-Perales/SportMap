package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sportType: String,          // "futbol", "voley", "correr", "natacion", "ciclismo", "bienestar"
    val category: String,           // "field" (cancha), "trayecto" (ruta), "wellness" (bienestar)
    val lat: Double,
    val lng: Double,
    val isPrivate: Boolean,
    val description: String,
    val services: String,           // CSV: "seguridad,baños,iluminación"
    val photoUrls: String,          // CSV de URLs
    val rating: Double = 4.5,
    val pricePerHour: Double = 0.0,
    val airQualityIndex: Int = 50   // AQI: 0-50 bueno, 51-100 moderado, etc.
)
