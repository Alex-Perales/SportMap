package com.tunalex.sportmap.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.repository.AppRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class SportFilter(
    val key: String,
    val label: String,
    val iconKey: String
)

val ALL_SPORTS = listOf(
    SportFilter("todos", "Todos", "all"),
    SportFilter("futbol", "Fútbol", "soccer"),
    SportFilter("voley", "Vóley", "volley"),
    SportFilter("correr", "Correr", "running"),
    SportFilter("natacion", "Natación", "swim"),
    SportFilter("ciclismo", "Ciclismo", "bike"),
    SportFilter("bienestar", "Bienestar", "wellness")
)

data class MapUiState(
    val selectedSport: String = "todos",
    val places: List<PlaceEntity> = emptyList(),
    val averageAqi: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModel(private val repo: AppRepository) : ViewModel() {

    private val _selected = MutableStateFlow("todos")
    val selected: StateFlow<String> = _selected.asStateFlow()

    val state: StateFlow<MapUiState> = _selected.flatMapLatest { sport ->
        val places = if (sport == "todos") repo.observePlaces()
        else repo.observePlacesBySport(sport)
        combine(places, _selected) { p, s ->
            MapUiState(
                selectedSport = s,
                places = p,
                averageAqi = if (p.isEmpty()) 0 else p.map { it.airQualityIndex }.average().toInt()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MapUiState())

    fun selectSport(key: String) {
        _selected.value = key
    }
}
