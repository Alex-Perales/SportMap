package com.tunalex.sportmap.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.remote.DirectionsRepository
import com.tunalex.sportmap.data.remote.RouteResult
import com.tunalex.sportmap.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RoutePhase { SELECT_ORIGIN, ROUTE_RESULT }

data class RouteUiState(
    val phase: RoutePhase = RoutePhase.SELECT_ORIGIN,
    val isLoading: Boolean = false,
    val place: PlaceEntity? = null,
    val origin: LatLng? = null,
    val pickedOrigin: LatLng? = null,
    val originLabel: String? = null,    // human-readable address of picked origin
    val mapPickerActive: Boolean = false,
    val result: RouteResult? = null,
    val error: String? = null
)

class RouteViewModel(
    private val repo: AppRepository,
    private val directionsRepo: DirectionsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RouteUiState())
    val state: StateFlow<RouteUiState> = _state.asStateFlow()

    fun initForPlace(placeId: Long) {
        if (_state.value.place != null) return
        viewModelScope.launch {
            val place = repo.getPlace(placeId)
            _state.value = _state.value.copy(place = place)
        }
    }

    fun activateMapPicker() {
        _state.value = _state.value.copy(mapPickerActive = true)
    }

    fun pickOriginOnMap(latLng: LatLng) {
        _state.value = _state.value.copy(pickedOrigin = latLng, mapPickerActive = false, originLabel = null)
    }

    fun setOriginLabel(label: String) {
        _state.value = _state.value.copy(originLabel = label)
    }

    fun cancelMapPicker() {
        _state.value = _state.value.copy(mapPickerActive = false)
    }

    fun startRoute(origin: LatLng) {
        val place = _state.value.place ?: return
        _state.value = _state.value.copy(
            phase = RoutePhase.ROUTE_RESULT,
            isLoading = true,
            origin = origin,
            error = null
        )
        viewModelScope.launch {
            val dest = LatLng(place.lat, place.lng)
            directionsRepo.getRoute(origin, dest).fold(
                onSuccess = { route ->
                    _state.value = _state.value.copy(isLoading = false, result = route)
                },
                onFailure = { err ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = err.message ?: "Error al calcular la ruta."
                    )
                }
            )
        }
    }

    fun backToSelectOrigin() {
        _state.value = _state.value.copy(
            phase = RoutePhase.SELECT_ORIGIN,
            isLoading = false,
            result = null,
            error = null,
            origin = null,
            originLabel = null,
            mapPickerActive = false
        )
    }

    fun setError(message: String) {
        _state.value = _state.value.copy(isLoading = false, error = message)
    }
}
