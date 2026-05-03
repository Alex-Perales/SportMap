package com.tunalex.sportmap.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val user: UserEntity? = null,
    val totalKm: Double = 0.0,
    val placesVisited: Int = 0,
    val nextReservation: ReservationEntity? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val state: StateFlow<DashboardUiState> = prefs.currentUserId.flatMapLatest { id ->
        if (id <= 0L) flowOf(DashboardUiState())
        else combine(
            repo.observeUser(id),
            repo.observeTotalKm(id),
            repo.observePlacesVisited(id),
            repo.observeNextReservation(id)
        ) { user, km, places, next ->
            DashboardUiState(user, km, places, next)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun togglePremium() {
        val current = state.value.user ?: return
        viewModelScope.launch {
            repo.setPremium(current.id, !current.isPremium)
        }
    }
}
