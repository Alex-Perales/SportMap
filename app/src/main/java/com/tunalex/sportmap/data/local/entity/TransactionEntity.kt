package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("transactionHash", unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val type: String,                            // purchase, reservation, withdrawal
    val amount: Double,
    val currency: String = "PEN",
    val status: String = "pending",              // pending, completed, failed, refunded
    val description: String? = null,
    val referenceId: Long? = null,               // ID de orden, reserva, etc
    val referenceType: String? = null,           // product, reservation
    val paymentMethod: String? = null,           // card, bank_transfer, wallet
    val transactionHash: String? = null,         // Hash único
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
