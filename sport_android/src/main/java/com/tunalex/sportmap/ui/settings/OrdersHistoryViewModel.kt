package com.tunalex.sportmap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.OrderEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersHistoryViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val orders: StateFlow<List<OrderEntity>> = prefs.currentUserId.flatMapLatest { id ->
        if (id <= 0L) flowOf(emptyList()) else repo.observeOrders(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { repo.syncOrdersFromBackend() }
    }

    suspend fun reuploadProof(orderId: Long, file: File): Result<Unit> =
        repo.reuploadOrderProof(orderId, file).map { }
}
