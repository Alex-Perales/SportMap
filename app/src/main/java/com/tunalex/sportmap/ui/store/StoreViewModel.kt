package com.tunalex.sportmap.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class StoreUiState(
    val products: List<ProductEntity> = emptyList(),
    val cartCount: Int = 0,
    val selectedCategory: String = "todos"
)

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _category = MutableStateFlow("todos")
    val category: StateFlow<String> = _category.asStateFlow()

    val state: StateFlow<StoreUiState> = combine(
        _category.flatMapLatest { c ->
            if (c == "todos") repo.observeProducts() else repo.observeProductsByCategory(c)
        },
        prefs.currentUserId.flatMapLatest { id ->
            if (id <= 0L) flowOf(0) else repo.observeCartCount(id)
        },
        _category
    ) { products, cartCount, c ->
        StoreUiState(products = products, cartCount = cartCount, selectedCategory = c)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoreUiState())

    fun selectCategory(cat: String) { _category.value = cat }
}
