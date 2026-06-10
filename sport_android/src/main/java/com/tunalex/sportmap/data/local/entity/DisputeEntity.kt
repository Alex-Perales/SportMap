package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "disputes",
    foreignKeys = [
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["initiatedByUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["assignedToAdminId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("reservationId"), Index("initiatedByUserId"), Index("assignedToAdminId")]
)
data class DisputeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reservationId: Long? = null,
    val initiatedByUserId: Long,
    val reason: String? = null,
    val description: String? = null,
    val status: String = "open",                 // open, in_review, resolved, closed
    val resolution: String? = null,
    val evidenceUrls: String? = null,            // CSV de URLs
    val assignedToAdminId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
