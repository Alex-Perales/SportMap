package com.tunalex.sportmap.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "complaints",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["complaintAgainstUserId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["complaintAgainstPlaceId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("userId"), Index("complaintAgainstUserId"), Index("complaintAgainstPlaceId")]
)
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val complaintAgainstUserId: Long? = null,
    val complaintAgainstPlaceId: Long? = null,
    val category: String,                        // harassment, fraud, bad_service, etc.
    val title: String? = null,
    val description: String? = null,
    val severity: String = "low",                // low, medium, high
    val status: String = "submitted",            // submitted, reviewing, resolved, closed
    val attachmentUrls: String? = null,          // CSV de URLs
    val resolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
