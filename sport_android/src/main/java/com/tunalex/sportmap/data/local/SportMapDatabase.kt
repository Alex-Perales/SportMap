package com.tunalex.sportmap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tunalex.sportmap.data.local.dao.ActivityDao
import com.tunalex.sportmap.data.local.dao.BankAccountDao
import com.tunalex.sportmap.data.local.dao.CartDao
import com.tunalex.sportmap.data.local.dao.ComplaintDao
import com.tunalex.sportmap.data.local.dao.DisputeDao
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.NotificationDao
import com.tunalex.sportmap.data.local.dao.PlaceDao
import com.tunalex.sportmap.data.local.dao.ProductDao
import com.tunalex.sportmap.data.local.dao.RatingDao
import com.tunalex.sportmap.data.local.dao.ReservationDao
import com.tunalex.sportmap.data.local.dao.ReviewDao
import com.tunalex.sportmap.data.local.dao.TransactionDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.dao.UserKycDao
import com.tunalex.sportmap.data.local.entity.ActivityEntity
import com.tunalex.sportmap.data.local.entity.BankAccountEntity
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.local.entity.ComplaintEntity
import com.tunalex.sportmap.data.local.entity.DisputeEntity
import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.data.local.entity.NotificationEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.local.entity.RatingEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.ReviewEntity
import com.tunalex.sportmap.data.local.entity.TransactionEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.local.entity.UserKycEntity

@Database(
    entities = [
        UserEntity::class,
        ActivityEntity::class,
        PlaceEntity::class,
        ReservationEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        MedalEntity::class,
        UserKycEntity::class,
        BankAccountEntity::class,
        RatingEntity::class,
        ReviewEntity::class,
        TransactionEntity::class,
        DisputeEntity::class,
        ComplaintEntity::class,
        NotificationEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class SportMapDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun activityDao(): ActivityDao
    abstract fun placeDao(): PlaceDao
    abstract fun reservationDao(): ReservationDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun medalDao(): MedalDao
    abstract fun userKycDao(): UserKycDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun ratingDao(): RatingDao
    abstract fun reviewDao(): ReviewDao
    abstract fun transactionDao(): TransactionDao
    abstract fun disputeDao(): DisputeDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: SportMapDatabase? = null

        fun getInstance(context: Context): SportMapDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SportMapDatabase::class.java,
                    "sportmap.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
