package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tunalex.sportmap.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert
    suspend fun insert(notification: NotificationEntity): Long

    @Update
    suspend fun update(notification: NotificationEntity)

    @Delete
    suspend fun delete(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationCount(userId: Long): Flow<Int>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getNotificationsByType(userId: Long, type: String): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1, readAt = :readTime WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Long, readTime: Long = System.currentTimeMillis())

    @Query("UPDATE notifications SET isRead = 1, readAt = :readTime WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long, readTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteNotificationsForUser(userId: Long)

    @Query("DELETE FROM notifications WHERE userId = :userId AND createdAt < :beforeTime")
    suspend fun deleteOldNotifications(userId: Long, beforeTime: Long)

    @Query("SELECT * FROM notifications WHERE referenceId = :referenceId AND referenceType = :referenceType")
    fun getNotificationsByReference(referenceId: Long, referenceType: String): Flow<List<NotificationEntity>>
}
