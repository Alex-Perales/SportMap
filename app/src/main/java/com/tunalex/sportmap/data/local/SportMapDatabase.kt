package com.tunalex.sportmap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tunalex.sportmap.data.local.dao.ActivityDao
import com.tunalex.sportmap.data.local.dao.CartDao
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.PlaceDao
import com.tunalex.sportmap.data.local.dao.ProductDao
import com.tunalex.sportmap.data.local.dao.ReservationDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.ActivityEntity
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ActivityEntity::class,
        PlaceEntity::class,
        ReservationEntity::class,
        ProductEntity::class,
        CartItemEntity::class,
        MedalEntity::class
    ],
    version = 1,
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
