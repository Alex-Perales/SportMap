package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bank_accounts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val accountType: String? = null,            // checking, savings, wallet
    val bankName: String? = null,
    val accountNumber: String? = null,          // Encriptado
    val routingNumber: String? = null,
    val accountHolderName: String? = null,
    val currency: String = "PEN",
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val verificationStatus: String = "pending", // pending, verified
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
