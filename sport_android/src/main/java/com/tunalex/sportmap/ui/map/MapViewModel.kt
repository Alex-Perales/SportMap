package com.tunalex.sportmap.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.repository.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class SportFilter(
    val key: String,
    val label: String,
    val iconKey: String
)

val ALL_SPORTS = listOf(
    SportFilter("todos", "Todos", "all"),
    SportFilter("futbol", "Fútbol", "soccer"),
    SportFilter("voley", "Vóley", "volley"),
    SportFilter("tenis", "Tenis", "tennis"),
    SportFilter("correr", "Correr", "running"),
    SportFilter("natacion", "Natación", "swim"),
    SportFilter("ciclismo", "Ciclismo", "bike"),
    SportFilter("bienestar", "Bienestar", "wellness")
)

data class PlaceWithDistance(
    val place: PlaceEntity,
    val distanceKm: Double?
)

data class MapUiState(
    val selectedSport: String = "todos",
    val searchQuery: String = "",
    val places: List<PlaceEntity> = emptyList(),
    val placesWithDistance: List<PlaceWithDistance> = emptyList(),
    val suggestions: List<PlaceEntity> = emptyList(),
    val averageAqi: Int = 0,
    val userLocation: LatLng? = null,
    val nearbyOnly: Boolean = false
)

private data class FilterParams(
    val sport: String,
    val query: String,
    val userLocation: LatLng?,
    val nearbyOnly: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModel(private val repo: AppRepository) : ViewModel() {

    private val _selected = MutableStateFlow("todos")
    private val _searchQuery = MutableStateFlow("")
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    private val _nearbyOnly = MutableStateFlow(false)

    val state: StateFlow<MapUiState> = combine(
        _selected, _searchQuery, _userLocation, _nearbyOnly
    ) { sport, query, loc, nearby ->
        FilterParams(sport, query, loc, nearby)
    }.flatMapLatest { params ->
        val placesFlow = if (params.sport == "todos") repo.observePlaces()
        else repo.observePlacesBySport(params.sport)

        placesFlow.map { list ->
            val suggestions = if (params.query.isBlank()) emptyList()
            else list.filter { it.name.contains(params.query, ignoreCase = true) }.take(6)

            val searched = if (params.query.isBlank()) list
            else list.filter { it.name.contains(params.query, ignoreCase = true) }

            val withDist = searched
                .map { place ->
                    val dist = params.userLocation?.let { loc ->
                        haversineKm(loc.latitude, loc.longitude, place.lat, place.lng)
                    }
                    PlaceWithDistance(place, dist)
                }
                .sortedBy { it.distanceKm ?: Double.MAX_VALUE }

            val filtered = if (params.nearbyOnly && params.userLocation != null)
                withDist.filter { (it.distanceKm ?: Double.MAX_VALUE) <= 5.0 }
            else withDist

            MapUiState(
                selectedSport = params.sport,
                searchQuery = params.query,
                userLocation = params.userLocation,
                nearbyOnly = params.nearbyOnly,
                suggestions = suggestions,
                places = filtered.map { it.place },
                placesWithDistance = filtered,
                averageAqi = if (filtered.isEmpty()) 0
                else filtered.map { it.place.airQualityIndex }.average().toInt()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MapUiState())

    private val _focusedPlace = MutableStateFlow<PlaceEntity?>(null)
    val focusedPlace: StateFlow<PlaceEntity?> = _focusedPlace.asStateFlow()

    fun selectSport(key: String) { _selected.value = key }
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun focusPlace(place: PlaceEntity) {
        _searchQuery.value = ""
        _focusedPlace.value = place
    }
    fun clearFocusedPlace() { _focusedPlace.value = null }
    fun setUserLocation(lat: Double, lng: Double) {
        // Only accept coordinates plausibly within Peru/Lima area to ignore emulator defaults
        if (lat in -20.0..-2.0 && lng in -82.0..-68.0) {
            _userLocation.value = LatLng(lat, lng)
        }
    }
    fun toggleNearby() { _nearbyOnly.value = !_nearbyOnly.value }
}

fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val R = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2.0) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
    return R * 2 * asin(sqrt(a))
}
