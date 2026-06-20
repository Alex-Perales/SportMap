package com.tunalex.sportmap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.AuthRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val user: UserEntity? = null,
    val medals: List<MedalEntity> = emptyList(),
    val darkMode: Boolean? = null,
    val gpsEnabled: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    private val repo: AppRepository,
    private val auth: AuthRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = prefs.currentUserId.flatMapLatest { id ->
        if (id <= 0L) flowOf(SettingsUiState())
        else combine(
            repo.observeUser(id),
            repo.observeMedals(id),
            prefs.darkMode,
            prefs.gpsEnabled
        ) { user: UserEntity?, medals: List<MedalEntity>, dark: Boolean?, gps: Boolean ->
            SettingsUiState(user, medals, dark, gps)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    init {
        // Detect stale session: ID exists in DataStore but user was deleted from DB
        viewModelScope.launch {
            val id = prefs.currentUserId.first()
            if (id > 0L && repo.findUserById(id) == null) {
                auth.logout()
                _events.emit(SettingsEvent.LoggedOut)
            }
        }
    }

    sealed class SettingsEvent {
        object LoggedOut : SettingsEvent()
        object AccountDeleted : SettingsEvent()
        object ProfileSaved : SettingsEvent()
        data class Toast(val message: String) : SettingsEvent()
    }

    fun setDarkMode(value: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(value) }
    }

    fun setGpsEnabled(value: Boolean) {
        viewModelScope.launch { prefs.setGpsEnabled(value) }
    }

    fun togglePremium() {
        val u = state.value.user ?: return
        viewModelScope.launch { repo.setPremium(u.id, !u.isPremium) }
    }

    fun updateProfile(name: String, district: String, profileImageUri: String? = null) {
        viewModelScope.launch {
            val u = state.value.user ?: run {
                val id = prefs.currentUserId.first()
                if (id <= 0L) return@launch
                repo.findUserById(id)
            } ?: return@launch
            repo.updateUser(u.copy(name = name, district = district, profileImageUrl = profileImageUri))
            _events.emit(SettingsEvent.ProfileSaved)
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.logout()
            _events.emit(SettingsEvent.LoggedOut)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val id = prefs.currentUserId.first()
            if (id > 0) {
                auth.deleteAccount(id)
                _events.emit(SettingsEvent.AccountDeleted)
            }
        }
    }
}
