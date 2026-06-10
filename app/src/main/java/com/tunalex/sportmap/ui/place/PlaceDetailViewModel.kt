package com.tunalex.sportmap.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaceDetailUiState(
    val place: PlaceEntity? = null,
    val loading: Boolean = true,
    val selectedDateMillis: Long? = null,
    val selectedTime: String = "18:00",
    val peopleCount: Int = 6,
    val reservationDone: Boolean = false,
    val message: String? = null
)

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
        }
    }

    fun setDate(millis: Long?) { _state.value = _state.value.copy(selectedDateMillis = millis) }
    fun setTime(time: String) { _state.value = _state.value.copy(selectedTime = time) }
    fun setPeople(count: Int) { _state.value = _state.value.copy(peopleCount = count.coerceIn(1, 50)) }
    fun clearMessage() { _state.value = _state.value.copy(message = null) }

    fun reserve() {
        val s = _state.value
        val place = s.place ?: return
        val date = s.selectedDateMillis
        if (date == null) {
            _state.update { it.copy(message = "Selecciona una fecha.") }
            return
        }
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId <= 0L) {
                _state.update { it.copy(message = "Debes iniciar sesión.") }
                return@launch
            }
            repo.createReservation(
                ReservationEntity(
                    userId = userId,
                    placeId = place.id,
                    placeName = place.name,
                    date = date,
                    time = s.selectedTime,
                    peopleCount = s.peopleCount
                )
            )
            _state.update { it.copy(
                reservationDone = true,
                message = "¡Reserva confirmada en ${place.name}!"
            ) }
        }
    }
}
