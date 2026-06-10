package com.tunalex.sportmap.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItemEntity> = emptyList(),
    val total: Double = 0.0
)

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val state: StateFlow<CartUiState> = prefs.currentUserId.flatMapLatest { id ->
        if (id <= 0L) flowOf(CartUiState())
        else combine(repo.observeCart(id), repo.observeCartTotal(id)) { items, total ->
            CartUiState(items, total)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CartUiState())

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    fun remove(id: Long) {
        viewModelScope.launch { repo.removeFromCart(id) }
    }

    fun checkout() {
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId > 0) {
                repo.clearCart(userId)
                _events.emit("¡Compra realizada! Recibirás un correo de confirmación.")
            }
        }
    }
}
