package com.tunalex.sportmap.data.repository

import com.tunalex.sportmap.data.local.dao.ActivityDao
import com.tunalex.sportmap.data.local.dao.AdDao
import com.tunalex.sportmap.data.local.dao.CartDao
import com.tunalex.sportmap.data.local.dao.MedalDao
import com.tunalex.sportmap.data.local.dao.OrderDao
import com.tunalex.sportmap.data.local.dao.PlaceDao
import com.tunalex.sportmap.data.local.dao.FavoriteDao
import com.tunalex.sportmap.data.local.dao.ProductDao
import com.tunalex.sportmap.data.local.dao.ReservationDao
import com.tunalex.sportmap.data.local.dao.ReviewDao
import com.tunalex.sportmap.data.local.dao.UserDao
import com.tunalex.sportmap.data.local.entity.ActivityEntity
import com.tunalex.sportmap.data.local.entity.AdEntity
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.local.entity.OrderEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.ReviewEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.remote.ActivityRequest
import com.tunalex.sportmap.data.remote.ApiService
import com.tunalex.sportmap.data.remote.CartItemRequest
import com.tunalex.sportmap.data.remote.ReservationRequest
import com.tunalex.sportmap.data.remote.UserUpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AppRepository(
    private val userDao: UserDao,
    private val activityDao: ActivityDao,
    private val placeDao: PlaceDao,
    private val reservationDao: ReservationDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val medalDao: MedalDao,
    private val reviewDao: ReviewDao,
    private val favoriteDao: FavoriteDao,
    private val orderDao: OrderDao,
    private val adDao: AdDao,
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

    /**
     * Crea la reserva directamente contra el backend (requiere conexión) y la
     * guarda localmente. A diferencia de [createReservation], esta variante
     * no hace "fire and forget": el pago por Yape se aprueba desde el panel
     * admin contra la fila del backend, así que necesitamos el id real del
     * servidor (devuelto aquí) para poder referenciarlo desde el pedido.
     */
    suspend fun createReservationOnline(r: ReservationEntity): Result<Long> {
        val serverId = getServerUserId()
        if (serverId <= 0) return Result.failure(IllegalStateException("Debes iniciar sesión."))
        return try {
            val dto = api.createReservation(
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
            reservationDao.insert(r.copy(status = dto.status))
            Result.success(dto.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

    // === Reviews ===
    data class ReviewWithUser(val review: ReviewEntity, val userName: String)

    fun observeReviews(placeId: Long): Flow<List<ReviewWithUser>> =
        reviewDao.getReviewsForPlace(placeId).map { reviews ->
            reviews.map { r -> ReviewWithUser(r, userDao.findById(r.userId)?.name ?: "Usuario SportMap") }
        }

    suspend fun getUserReviewForPlace(placeId: Long, userId: Long): ReviewEntity? =
        reviewDao.getUserReviewForPlace(placeId, userId)

    suspend fun submitReview(review: ReviewEntity) {
        val existing = reviewDao.getUserReviewForPlace(review.placeId, review.userId)
        if (existing != null) {
            reviewDao.update(review.copy(id = existing.id, createdAt = existing.createdAt))
        } else {
            reviewDao.insert(review)
        }
    }

    // === Favorites ===
    fun observeIsFavorite(userId: Long, placeId: Long): Flow<Boolean> =
        favoriteDao.isFavorite(userId, placeId)

    fun observeFavoritePlaces(userId: Long): Flow<List<PlaceEntity>> =
        favoriteDao.observeFavoritePlaces(userId)

    suspend fun toggleFavorite(userId: Long, placeId: Long) {
        if (favoriteDao.isFavoriteOnce(userId, placeId)) {
            favoriteDao.delete(userId, placeId)
        } else {
            favoriteDao.insert(userId, placeId)
        }
    }

    // === Orders (pagos por Yape/Plin, pendientes de revisión) ===
    fun observeOrders(userId: Long): Flow<List<OrderEntity>> = orderDao.observeByUser(userId)

    /** Sube el comprobante y crea el pedido. Requiere conexión: la revisión
     * del pago la hace un humano contra el backend, así que no hay forma de
     * hacer esto "offline-first" como el resto del repositorio. */
    suspend fun createOrder(
        orderType: String,
        amount: Double,
        referenceId: Long?,
        itemsJson: String?,
        comprobante: File
    ): Result<OrderEntity> {
        val serverId = getServerUserId()
        if (serverId <= 0) return Result.failure(IllegalStateException("Debes iniciar sesión."))
        return try {
            val textType = "text/plain".toMediaType()
            val dto = api.createOrder(
                userId = serverId.toString().toRequestBody(textType),
                orderType = orderType.toRequestBody(textType),
                amount = amount.toString().toRequestBody(textType),
                referenceId = referenceId?.toString()?.toRequestBody(textType),
                itemsJson = itemsJson?.toRequestBody(textType),
                comprobante = MultipartBody.Part.createFormData(
                    "comprobante", comprobante.name, comprobante.asRequestBody("image/*".toMediaType())
                )
            )
            val entity = OrderEntity(
                id = dto.id,
                userId = dto.userId,
                orderType = dto.orderType,
                referenceId = dto.referenceId,
                amount = dto.amount,
                itemsJson = dto.itemsJson,
                status = dto.status,
                proofImagePath = dto.proofImagePath,
                motivoRechazo = dto.motivoRechazo,
                createdAt = dto.createdAt
            )
            orderDao.insertAll(listOf(entity))
            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Vuelve a subir el comprobante de un pedido rechazado. El mismo pedido
     * regresa a "pendiente" para que el admin lo revise otra vez, en vez de
     * crear uno nuevo. */
    suspend fun reuploadOrderProof(orderId: Long, comprobante: File): Result<OrderEntity> {
        return try {
            val dto = api.reuploadOrderProof(
                orderId,
                MultipartBody.Part.createFormData(
                    "comprobante", comprobante.name, comprobante.asRequestBody("image/*".toMediaType())
                )
            )
            val entity = OrderEntity(
                id = dto.id,
                userId = dto.userId,
                orderType = dto.orderType,
                referenceId = dto.referenceId,
                amount = dto.amount,
                itemsJson = dto.itemsJson,
                status = dto.status,
                proofImagePath = dto.proofImagePath,
                motivoRechazo = dto.motivoRechazo,
                createdAt = dto.createdAt
            )
            orderDao.insertAll(listOf(entity))
            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncOrdersFromBackend() {
        try {
            val serverId = getServerUserId()
            if (serverId <= 0) return
            val remote = api.getUserOrders(serverId)
            val entities = remote.map { dto ->
                OrderEntity(
                    id = dto.id,
                    userId = dto.userId,
                    orderType = dto.orderType,
                    referenceId = dto.referenceId,
                    amount = dto.amount,
                    itemsJson = dto.itemsJson,
                    status = dto.status,
                    proofImagePath = dto.proofImagePath,
                    motivoRechazo = dto.motivoRechazo,
                    createdAt = dto.createdAt
                )
            }
            orderDao.insertAll(entities)
        } catch (_: Exception) {}
    }

    /** Sube una foto de perfil nueva (bucket "perfiles" en Supabase, vía el
     * backend) y actualiza la URL guardada en Room. Devuelve la URL pública
     * para que la UI la use de inmediato en el formulario. */
    suspend fun uploadProfilePhoto(file: File): Result<String> {
        val serverId = getServerUserId()
        if (serverId <= 0) return Result.failure(IllegalStateException("Debes iniciar sesión."))
        return try {
            val part = MultipartBody.Part.createFormData(
                "photo", file.name, file.asRequestBody("image/*".toMediaType())
            )
            val dto = api.uploadProfilePhoto(serverId, part)
            val localId = prefs.currentUserId.first()
            userDao.findById(localId)?.let { userDao.update(it.copy(profileImageUrl = dto.profileImageUrl)) }
            Result.success(dto.profileImageUrl ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Registra el token FCM actual del dispositivo contra el usuario logueado.
     * Se llama al hacer login/registro y cada vez que Firebase renueva el
     * token (`onNewToken` en [com.tunalex.sportmap.notifications.SportMapMessagingService]). */
    suspend fun registerFcmToken(token: String) {
        try {
            val serverId = getServerUserId()
            if (serverId > 0) {
                api.updateFcmToken(serverId, com.tunalex.sportmap.data.remote.FcmTokenRequest(token))
            }
        } catch (_: Exception) {}
    }

    // === Ads ("Recomendados para ti") ===
    fun observeAds(): Flow<List<AdEntity>> = adDao.observeAll()

    /** Sincroniza los anuncios activos del backend. Reemplaza el caché local
     * completo para que un anuncio desactivado/borrado en el panel admin
     * también desaparezca de la app. */
    suspend fun syncAdsFromBackend() {
        try {
            val remote = api.getAds()
            val entities = remote.map { dto ->
                AdEntity(
                    id = dto.id,
                    imageUrl = dto.imageUrl,
                    badgeText = dto.badgeText,
                    title = dto.title,
                    subtitle = dto.subtitle,
                    price = dto.price,
                    linkType = dto.linkType,
                    linkTarget = dto.linkTarget,
                    sortOrder = dto.sortOrder
                )
            }
            adDao.replaceAll(entities)
        } catch (_: Exception) {}
    }

    /** Solo para pruebas en desarrollo: crea reservas de ejemplo (variadas en
     * deporte, día y hora) directamente en Room, para poder ver el Dashboard
     * "lleno" sin tener que reservar 15 veces a mano. No llama al backend. */
    suspend fun seedTestReservations(userId: Long, count: Int = 15) {
        val places = observePlaces().first()
        if (places.isEmpty()) return
        val hours = listOf("07:00", "09:00", "12:00", "15:00", "18:00", "19:00", "20:00")
        val dayOffsets = (listOf(0) + List(count - 1) { (0..13).random() })
        dayOffsets.forEach { dayOffset ->
            val place = places.random()
            val dateMillis = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                timeInMillis = System.currentTimeMillis()
                add(java.util.Calendar.DAY_OF_YEAR, -dayOffset)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
            reservationDao.insert(
                ReservationEntity(
                    userId = userId,
                    placeId = place.id,
                    placeName = place.name,
                    date = dateMillis,
                    time = hours.random(),
                    peopleCount = (1..6).random(),
                    status = "confirmed"
                )
            )
        }
    }

    // === Estadísticas del Dashboard (basadas en reservas reales) ===
    fun observeTotalReservations(userId: Long): Flow<Int> = reservationDao.observeTotalReservations(userId)
    fun observeSessionsThisWeek(userId: Long, weekStartMillis: Long): Flow<Int> =
        reservationDao.observeSessionsThisWeek(userId, weekStartMillis)
    fun observeSportBreakdown(userId: Long) = reservationDao.observeSportBreakdown(userId)
    fun observeWeeklyPattern(userId: Long) = reservationDao.observeWeeklyPattern(userId)
    fun observeHourlyPattern(userId: Long) = reservationDao.observeHourlyPattern(userId)
    fun observeDistinctReservationDates(userId: Long): Flow<List<Long>> =
        reservationDao.observeDistinctDates(userId)

    // ── helpers ──────────────────────────────────────────────────────────────

    private suspend fun getServerUserId(): Long = prefs.serverUserId.first()
}
