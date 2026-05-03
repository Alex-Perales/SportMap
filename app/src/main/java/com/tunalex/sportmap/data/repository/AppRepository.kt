package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.ActivityDao
import com.tunalex.sportmap.data.local.dao.CartDao
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.PlaceDao
import com.tunalex.sportmap.data.local.dao.ProductDao
import com.tunalex.sportmap.data.local.dao.ReservationDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.ActivityEntity
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio general que expone los datos al resto de la app.
 * Convierte DAOs en una API limpia para los ViewModels.
 */
class AppRepository(
    private val userDao: UserDao,
    private val activityDao: ActivityDao,
    private val placeDao: PlaceDao,
    private val reservationDao: ReservationDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val medalDao: MedalDao
) {
    // === User ===
    fun observeUser(userId: Long) = userDao.observeById(userId)
    suspend fun updateUser(user: UserEntity) = userDao.update(user)
    suspend fun setPremium(userId: Long, premium: Boolean) {
        val u = userDao.findById(userId) ?: return
        userDao.update(u.copy(isPremium = premium))
    }

    // === Activities ===
    fun observeTotalKm(userId: Long): Flow<Double> =
        activityDao.observeTotalKm(userId).map { it ?: 0.0 }
    fun observePlacesVisited(userId: Long): Flow<Int> = activityDao.observePlacesVisited(userId)
    fun observeActivities(userId: Long): Flow<List<ActivityEntity>> = activityDao.observeByUser(userId)
    suspend fun logActivity(activity: ActivityEntity) = activityDao.insert(activity)

    // === Places ===
    fun observePlaces(): Flow<List<PlaceEntity>> = placeDao.observeAll()
    fun observePlacesBySport(sport: String): Flow<List<PlaceEntity>> = placeDao.observeBySport(sport)
    suspend fun getPlace(id: Long): PlaceEntity? = placeDao.findById(id)

    // === Reservations ===
    fun observeNextReservation(userId: Long): Flow<ReservationEntity?> =
        reservationDao.observeNextReservation(userId, System.currentTimeMillis())
    fun observeReservations(userId: Long): Flow<List<ReservationEntity>> =
        reservationDao.observeByUser(userId)
    suspend fun createReservation(r: ReservationEntity): Long = reservationDao.insert(r)
    suspend fun cancelReservation(id: Long) = reservationDao.deleteById(id)

    // === Products ===
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeAll()
    fun observeProductsByCategory(category: String): Flow<List<ProductEntity>> =
        productDao.observeByCategory(category)
    suspend fun getProduct(id: Long): ProductEntity? = productDao.findById(id)

    // === Cart ===
    fun observeCart(userId: Long): Flow<List<CartItemEntity>> = cartDao.observeByUser(userId)
    fun observeCartTotal(userId: Long): Flow<Double> = cartDao.observeTotal(userId)
    fun observeCartCount(userId: Long): Flow<Int> = cartDao.observeCount(userId)
    suspend fun addToCart(item: CartItemEntity) = cartDao.insert(item)
    suspend fun removeFromCart(id: Long) = cartDao.deleteById(id)
    suspend fun clearCart(userId: Long) = cartDao.clearForUser(userId)

    // === Medals ===
    fun observeMedals(userId: Long) = medalDao.observeByUser(userId)
}
