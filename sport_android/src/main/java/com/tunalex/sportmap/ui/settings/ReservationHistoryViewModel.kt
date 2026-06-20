package com.tunalex.sportmap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationHistoryViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val reservations: StateFlow<List<ReservationEntity>> =
        prefs.currentUserId.flatMapLatest { id ->
            if (id <= 0L) flowOf(emptyList())
            else repo.observeReservations(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cancelReservation(id: Long) {
        viewModelScope.launch { repo.cancelReservation(id) }
    }
}
