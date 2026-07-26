package com.tunalex.sportmap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel(
    repo: AppRepository,
    prefs: UserPreferences
) : ViewModel() {

    val favorites: StateFlow<List<PlaceEntity>> = prefs.currentUserId
        .flatMapLatest { userId ->
            if (userId <= 0L) flowOf(emptyList<PlaceEntity>()) else repo.observeFavoritePlaces(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
