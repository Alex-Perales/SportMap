package com.tunalex.sportmap.ui.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.entity.CartItemEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val product: ProductEntity? = null,
    val selectedSize: String = "",
    val quantity: Int = 1,
    val message: String? = null
)

class ProductDetailViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailUiState())
    val state: StateFlow<ProductDetailUiState> = _state.asStateFlow()

    fun load(productId: Long) {
        viewModelScope.launch {
            val p = repo.getProduct(productId) ?: return@launch
            val sizes = p.sizes.split(",").map { it.trim() }.filter { it.isNotBlank() }
            _state.value = _state.value.copy(
                product = p,
                selectedSize = sizes.firstOrNull() ?: ""
            )
        }
    }

    fun setSize(s: String) { _state.value = _state.value.copy(selectedSize = s) }
    fun setQuantity(q: Int) { _state.value = _state.value.copy(quantity = q.coerceIn(1, 20)) }
    fun clearMessage() { _state.value = _state.value.copy(message = null) }

    fun addToCart() {
        val s = _state.value
        val p = s.product ?: return
        viewModelScope.launch {
            val userId = prefs.currentUserId.first()
            if (userId <= 0L) {
                _state.value = s.copy(message = "Debes iniciar sesión.")
                return@launch
            }
            val unitPrice = if (p.isOnSale) p.price * (1 - p.discountPercent / 100.0) else p.price
            repo.addToCart(
                CartItemEntity(
                    userId = userId,
                    productId = p.id,
                    productName = p.name,
                    productImageUrl = p.imageUrl,
                    unitPrice = unitPrice,
                    quantity = s.quantity,
                    selectedSize = s.selectedSize
                )
            )
            _state.value = s.copy(message = "Añadido al carrito ✔")
        }
    }
}
