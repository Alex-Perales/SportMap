package com.tunalex.sportmap.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.ReviewEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

private const val MAX_PEOPLE = 20

data class PlaceDetailUiState(
    val place: PlaceEntity? = null,
    val loading: Boolean = true,
    val selectedDateMillis: Long? = null,
    val selectedTime: String = "18:00",
    val peopleCount: Int = 6,
    val reservationDone: Boolean = false,
    val message: String? = null,
    val reviews: List<AppRepository.ReviewWithUser> = emptyList(),
    val myReview: ReviewEntity? = null,
    val isFavorite: Boolean = false
) {
    val averageRating: Double
        get() = if (reviews.isEmpty()) place?.rating ?: 0.0 else reviews.map { it.review.rating }.average()
}

class PlaceDetailViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(PlaceDetailUiState())
    val state: StateFlow<PlaceDetailUiState> = _state.asStateFlow()

    fun load(placeId: Long) {
        viewModelScope.launch {
            val p = repo.getPlace(placeId)
            _state.value = _state.value.copy(place = p, loading = false)

            val userId = prefs.currentUserId.first()
            if (userId > 0L) {
                _state.update { it.copy(myReview = repo.getUserReviewForPlace(placeId, userId)) }
            }
        }
        viewModelScope.launch {
            repo.observeReviews(placeId).collectLatest { reviews ->
                _state.update { it.copy(reviews = reviews) }
            }
        }
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId <= 0L) return@launch
            repo.observeIsFavorite(userId, placeId).collectLatest { fav ->
                _state.update { it.copy(isFavorite = fav) }
            }
        }
    }

    fun toggleFavorite() {
        val place = _state.value.place ?: return
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId <= 0L) {
                _state.update { it.copy(message = "Debes iniciar sesión para guardar favoritos.") }
                return@launch
            }
            repo.toggleFavorite(userId, place.id)
        }
    }

    fun submitReview(rating: Int, comment: String) {
        val place = _state.value.place ?: return
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId <= 0L) {
                _state.update { it.copy(message = "Debes iniciar sesión para dejar una reseña.") }
                return@launch
            }
            repo.submitReview(
                ReviewEntity(
                    placeId = place.id,
                    userId = userId,
                    rating = rating.coerceIn(1, 5),
                    comment = comment.ifBlank { null }
                )
            )
            _state.update { it.copy(myReview = repo.getUserReviewForPlace(place.id, userId), message = "¡Gracias por tu reseña!") }
        }
    }

    fun setDate(millis: Long?) { _state.value = _state.value.copy(selectedDateMillis = millis) }
    fun setTime(time: String) { _state.value = _state.value.copy(selectedTime = time) }
    fun setPeople(count: Int) { _state.value = _state.value.copy(peopleCount = count.coerceIn(1, MAX_PEOPLE)) }
    fun clearMessage() { _state.value = _state.value.copy(message = null) }

    /** Valida fecha/hora/aforo. Si hay un error, lo deja en `message` y lo devuelve. */
    fun validateReservation(): String? {
        val s = _state.value
        val date = s.selectedDateMillis
        if (date == null) {
            val msg = "Selecciona una fecha."
            _state.update { it.copy(message = msg) }
            return msg
        }
        val (hour, minute) = s.selectedTime.split(":").let { it[0].toInt() to it[1].toInt() }
        // `date` viene del DatePicker de Compose como medianoche UTC del día elegido.
        // Hay que leer año/mes/día en UTC y recién ahí combinarlos con la hora local,
        // si no, el día se corre (p. ej. en Lima, UTC-5, "hoy" se interpreta como
        // "ayer a las 7pm" y toda reserva de hoy parece estar en el pasado).
        val pickedDayUtc = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = date
        }
        val reservationMoment = Calendar.getInstance().apply {
            clear()
            set(
                pickedDayUtc.get(Calendar.YEAR),
                pickedDayUtc.get(Calendar.MONTH),
                pickedDayUtc.get(Calendar.DAY_OF_MONTH),
                hour,
                minute,
                0
            )
        }
        if (reservationMoment.timeInMillis < System.currentTimeMillis()) {
            val msg = "Elige una fecha y hora que no hayan pasado."
            _state.update { it.copy(message = msg) }
            return msg
        }
        if (s.peopleCount > MAX_PEOPLE) {
            val msg = "El aforo máximo es de $MAX_PEOPLE personas."
            _state.update { it.copy(message = msg) }
            return msg
        }
        return null
    }

    /** Crea la reserva (pendiente de pago) y sube el comprobante de Yape/Plin. */
    suspend fun submitReservationPayment(comprobante: java.io.File): Result<Unit> {
        val s = _state.value
        val place = s.place ?: return Result.failure(IllegalStateException("Lugar no cargado."))
        if (validateReservation() != null) return Result.failure(IllegalStateException("Datos de reserva inválidos."))

        val userId = prefs.currentUserId.first()
        if (userId <= 0L) return Result.failure(IllegalStateException("Debes iniciar sesión."))

        val reservationResult = repo.createReservationOnline(
            ReservationEntity(
                userId = userId,
                placeId = place.id,
                placeName = place.name,
                date = s.selectedDateMillis!!,
                time = s.selectedTime,
                peopleCount = s.peopleCount,
                status = "pendiente_pago"
            )
        )
        val serverReservationId = reservationResult.getOrElse { return Result.failure(it) }

        val orderResult = repo.createOrder(
            orderType = "reservation",
            amount = place.pricePerHour,
            referenceId = serverReservationId,
            itemsJson = null,
            comprobante = comprobante
        )
        return orderResult.map { }.onSuccess {
            _state.update {
                it.copy(
                    reservationDone = true,
                    message = "¡Listo! Tu reserva en ${place.name} quedó pendiente de confirmar el pago."
                )
            }
        }
    }
}
