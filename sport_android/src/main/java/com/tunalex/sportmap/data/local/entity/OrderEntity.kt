package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Long,
    val userId: Long,
    val orderType: String,           // "store" | "reservation" | "premium"
    val referenceId: Long? = null,   // id de la reserva en el backend, si aplica
    val amount: Double,
    val itemsJson: String? = null,
    val status: String,              // "pendiente" | "aprobado" | "rechazado" | "cancelado_reembolsado"
    val proofImagePath: String,
    val motivoRechazo: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
