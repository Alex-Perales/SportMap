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
import com.tunalex.sportmap.data.remote.ActivityRequest
import com.tunalex.sportmap.data.remote.ApiService
import com.tunalex.sportmap.data.remote.CartItemRequest
import com.tunalex.sportmap.data.remote.ReservationRequest
import com.tunalex.sportmap.data.remote.UserUpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppRepository(
    private val userDao: UserDao,
    private val activityDao: ActivityDao,
    private val placeDao: PlaceDao,
    private val reservationDao: ReservationDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val medalDao: MedalDao,
    private val api: ApiService,
    private val prefs: UserPreferences
) {
    // === User ===
    fun observeUser(userId: Long) = userDao.observeById(userId)
    suspend fun findUserById(id: Long): UserEntity? = userDao.findById(id)
    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.updateUser(
                    serverId,
                    UserUpdateRequest(
                        name = user.name,
                        district = user.district,
                        profileImageUrl = user.profileImageUrl,
                        isPremium = user.isPremium
                    )
                )
            }
        } catch (_: Exception) {}
    }

    suspend fun setPremium(userId: Long, premium: Boolean) {
        val u = userDao.findById(userId) ?: return
        userDao.update(u.copy(isPremium = premium))
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.updateUser(serverId, UserUpdateRequest(isPremium = premium))
            }
        } catch (_: Exception) {}
    }

    // === Activities ===
    fun observeTotalKm(userId: Long): Flow<Double> =
        activityDao.observeTotalKm(userId).map { it ?: 0.0 }
    fun observePlacesVisited(userId: Long): Flow<Int> = activityDao.observePlacesVisited(userId)
    fun observeActivities(userId: Long): Flow<List<ActivityEntity>> = activityDao.observeByUser(userId)

    suspend fun logActivity(activity: ActivityEntity) {
        activityDao.insert(activity)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.createActivity(
                    ActivityRequest(
                        userId = serverId,
                        type = activity.type,
                        distanceKm = activity.distanceKm,
                        durationMinutes = activity.durationMinutes,
                        placeId = activity.placeId,
                        date = activity.date
                    )
                )
            }
        } catch (_: Exception) {}
    }

    // === Places ===
    fun observePlaces(): Flow<List<PlaceEntity>> = placeDao.observeAll()
    fun observePlacesBySport(sport: String): Flow<List<PlaceEntity>> = placeDao.observeBySport(sport)
    suspend fun getPlace(id: Long): PlaceEntity? = placeDao.findById(id)

    /** Sincroniza lugares del backend al Room local. */
    suspend fun syncPlacesFromBackend() {
        try {
            val remote = api.getPlaces()
            val entities = remote.map { dto ->
                PlaceEntity(
                    id = dto.id,
                    name = dto.name,
                    sportType = dto.sportType,
                    category = dto.category,
                    lat = dto.lat,
                    lng = dto.lng,
                    isPrivate = dto.isPrivate,
                    description = dto.description ?: "",
                    services = dto.services ?: "",
                    photoUrls = dto.photoUrls ?: "",
                    rating = dto.rating,
                    pricePerHour = dto.pricePerHour,
                    airQualityIndex = dto.airQualityIndex
                )
            }
            placeDao.insertAll(entities)
        } catch (_: Exception) {}
    }

    // === Reservations ===
    fun observeNextReservation(userId: Long): Flow<ReservationEntity?> =
        reservationDao.observeNextReservation(userId, System.currentTimeMillis())
    fun observeReservations(userId: Long): Flow<List<ReservationEntity>> =
        reservationDao.observeByUser(userId)

    suspend fun createReservation(r: ReservationEntity): Long {
        val localId = reservationDao.insert(r)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.createReservation(
                    ReservationRequest(
                        userId = serverId,
                        placeId = r.placeId,
                        placeName = r.placeName,
                        date = r.date,
                        time = r.time,
                        peopleCount = r.peopleCount,
                        status = r.status,
                        createdAt = r.createdAt
                    )
                )
            }
        } catch (_: Exception) {}
        return localId
    }

    suspend fun cancelReservation(id: Long) {
        reservationDao.deleteById(id)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.cancelReservation(id)
            }
        } catch (_: Exception) {}
    }

    // === Products ===
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeAll()
    fun observeProductsByCategory(category: String): Flow<List<ProductEntity>> =
        productDao.observeByCategory(category)
    suspend fun getProduct(id: Long): ProductEntity? = productDao.findById(id)

    /** Sincroniza productos del backend al Room local. */
    suspend fun syncProductsFromBackend() {
        try {
            val remote = api.getProducts()
            val entities = remote.map { dto ->
                ProductEntity(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description ?: "",
                    price = dto.price,
                    imageUrl = dto.imageUrl ?: "",
                    category = dto.category,
                    sizes = dto.sizes ?: "",
                    stock = dto.stock,
                    isOnSale = dto.isOnSale,
                    discountPercent = dto.discountPercent
                )
            }
            productDao.insertAll(entities)
        } catch (_: Exception) {}
    }

    // === Cart ===
    fun observeCart(userId: Long): Flow<List<CartItemEntity>> = cartDao.observeByUser(userId)
    fun observeCartTotal(userId: Long): Flow<Double> = cartDao.observeTotal(userId)
    fun observeCartCount(userId: Long): Flow<Int> = cartDao.observeCount(userId)

    suspend fun addToCart(item: CartItemEntity) {
        cartDao.insert(item)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.addToCart(
                    CartItemRequest(
                        userId = serverId,
                        productId = item.productId,
                        productName = item.productName,
                        productImageUrl = item.productImageUrl,
                        unitPrice = item.unitPrice,
                        quantity = item.quantity,
                        selectedSize = item.selectedSize
                    )
                )
            }
        } catch (_: Exception) {}
    }

    suspend fun removeFromCart(id: Long) {
        cartDao.deleteById(id)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) api.removeCartItem(id)
        } catch (_: Exception) {}
    }

    suspend fun clearCart(userId: Long) {
        cartDao.clearForUser(userId)
        try {
            val serverId = getServerUserId()
            if (serverId > 0) api.clearCart(serverId)
        } catch (_: Exception) {}
    }

    // === Medals ===
    fun observeMedals(userId: Long) = medalDao.observeByUser(userId)

    // ── helpers ──────────────────────────────────────────────────────────────

    private suspend fun getServerUserId(): Long = prefs.serverUserId.first()
}
