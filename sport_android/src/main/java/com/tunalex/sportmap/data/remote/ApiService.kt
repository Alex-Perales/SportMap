package com.tunalex.sportmap.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.*

// ── DTOs ────────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val name: String,
    val email: String,
    @SerializedName("password_hash") val passwordHash: String
)

data class LoginRequest(
    val email: String,
    @SerializedName("password_hash") val passwordHash: String
)

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val district: String,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    @SerializedName("created_at") val createdAt: Long
)

data class PlaceDto(
    val id: Long,
    val name: String,
    @SerializedName("sport_type") val sportType: String,
    val category: String,
    val lat: Double,
    val lng: Double,
    @SerializedName("is_private") val isPrivate: Boolean,
    val description: String?,
    val services: String?,
    @SerializedName("photo_urls") val photoUrls: String?,
    val rating: Double,
    @SerializedName("price_per_hour") val pricePerHour: Double,
    @SerializedName("air_quality_index") val airQualityIndex: Int
)

data class ActivityRequest(
    @SerializedName("user_id") val userId: Long,
    val type: String,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("place_id") val placeId: Long?,
    val date: Long
)

data class ActivityDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val type: String,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("place_id") val placeId: Long?,
    val date: Long
)

data class ReservationRequest(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("place_name") val placeName: String,
    val date: Long,
    val time: String,
    @SerializedName("people_count") val peopleCount: Int,
    val status: String = "confirmed",
    @SerializedName("created_at") val createdAt: Long
)

data class ReservationDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("place_id") val placeId: Long,
    @SerializedName("place_name") val placeName: String,
    val date: Long,
    val time: String,
    @SerializedName("people_count") val peopleCount: Int,
    val status: String,
    @SerializedName("created_at") val createdAt: Long
)

data class ProductDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("image_url") val imageUrl: String?,
    val category: String,
    val sizes: String?,
    val stock: Int,
    @SerializedName("is_on_sale") val isOnSale: Boolean,
    @SerializedName("discount_percent") val discountPercent: Int
)

data class CartItemRequest(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("product_id") val productId: Long,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_image_url") val productImageUrl: String,
    @SerializedName("unit_price") val unitPrice: Double,
    val quantity: Int,
    @SerializedName("selected_size") val selectedSize: String
)

data class CartItemDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("product_id") val productId: Long,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_image_url") val productImageUrl: String,
    @SerializedName("unit_price") val unitPrice: Double,
    val quantity: Int,
    @SerializedName("selected_size") val selectedSize: String
)

data class MedalDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val name: String,
    val description: String,
    @SerializedName("icon_key") val iconKey: String,
    val earned: Boolean,
    @SerializedName("earned_date") val earnedDate: Long?,
    val tier: String
)

data class UserUpdateRequest(
    val name: String? = null,
    val district: String? = null,
    @SerializedName("profile_image_url") val profileImageUrl: String? = null,
    @SerializedName("is_premium") val isPremium: Boolean? = null
)

// ── Interface ────────────────────────────────────────────────────────────────

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    // Users
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Long): UserResponse

    @PATCH("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: UserUpdateRequest): UserResponse

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    // Places
    @GET("api/places/")
    suspend fun getPlaces(@Query("sport_type") sportType: String? = null): List<PlaceDto>

    @GET("api/places/{id}")
    suspend fun getPlace(@Path("id") id: Long): PlaceDto

    // Activities
    @POST("api/activities/")
    suspend fun createActivity(@Body request: ActivityRequest): ActivityDto

    @GET("api/activities/user/{userId}")
    suspend fun getUserActivities(@Path("userId") userId: Long): List<ActivityDto>

    // Reservations
    @POST("api/reservations/")
    suspend fun createReservation(@Body request: ReservationRequest): ReservationDto

    @GET("api/reservations/user/{userId}")
    suspend fun getUserReservations(@Path("userId") userId: Long): List<ReservationDto>

    @DELETE("api/reservations/{id}")
    suspend fun cancelReservation(@Path("id") id: Long)

    // Products
    @GET("api/products/")
    suspend fun getProducts(@Query("category") category: String? = null): List<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProduct(@Path("id") id: Long): ProductDto

    // Cart
    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Long): List<CartItemDto>

    @POST("api/cart/")
    suspend fun addToCart(@Body request: CartItemRequest): CartItemDto

    @DELETE("api/cart/item/{itemId}")
    suspend fun removeCartItem(@Path("itemId") itemId: Long)

    @DELETE("api/cart/user/{userId}")
    suspend fun clearCart(@Path("userId") userId: Long)

    // Medals
    @GET("api/medals/user/{userId}")
    suspend fun getUserMedals(@Path("userId") userId: Long): List<MedalDto>
}
