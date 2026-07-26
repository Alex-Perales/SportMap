package com.tunalex.sportmap.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tunalex.sportmap.data.local.entity.ProductEntity
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels

private val CATEGORIES = listOf(
    "todos" to "Todos",
    "calzado" to "Calzado",
    "balones" to "Balones",
    "hidratación" to "Hidratación",
    "accesorios" to "Accesorios",
    "ropa" to "Ropa"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit,
    vm: StoreViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda SportMap", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (state.cartCount > 0) Badge { Text(state.cartCount.toString()) }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CATEGORIES.forEach { (key, label) ->
                    val isSel = key == state.selectedCategory
                    Card(
                        onClick = { vm.selectCategory(key) },
                        shape = RoundedCornerShape(50),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) BlueVibrant else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            when {
                state.loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(color = BlueVibrant)
                    }
                }
                state.products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No hay productos en esta categoría todavía.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.products) { product ->
                            ProductCard(product = product, onClick = { onProductClick(product.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: ProductEntity, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                if (product.isOnSale) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFFEF4444))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    product.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    product.category.replaceFirstChar { it.uppercase() },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                if (product.isOnSale) {
                    val discounted = product.price * (1 - product.discountPercent / 100.0)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "S/. ${"%.2f".format(discounted)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BlueVibrant
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "S/. ${"%.2f".format(product.price)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                } else {
                    Text(
                        "S/. ${"%.2f".format(product.price)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = BlueVibrant
                    )
                }
            }
        }
    }
}
