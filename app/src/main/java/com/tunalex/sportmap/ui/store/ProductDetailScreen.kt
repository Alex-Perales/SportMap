package com.tunalex.sportmap.ui.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@Composable
fun ProductDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    vm: ProductDetailViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(productId) { vm.load(productId) }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        val product = state.product
        if (product == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                Text("Cargando...")
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 24.dp, start = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                }
                IconButton(
                    onClick = onCartClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Filled.ShoppingCart, null, tint = Color.White)
                }
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                val displayPrice = if (product.isOnSale)
                    product.price * (1 - product.discountPercent / 100.0)
                else product.price
                Text(
                    "S/. ${"%.2f".format(displayPrice)}",
                    color = BlueVibrant,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
                Text(product.description, fontSize = 14.sp, lineHeight = 20.sp)

                Spacer(Modifier.height(20.dp))
                val sizes = product.sizes.split(",").map { it.trim() }.filter { it.isNotBlank() }
                if (sizes.size > 1 || (sizes.firstOrNull()?.length ?: 0) <= 4) {
                    Text("Talla", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sizes.forEach { sz ->
                            val isSel = sz == state.selectedSize
                            OutlinedButton(
                                onClick = { vm.setSize(sz) },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSel) BlueVibrant else Color.Transparent,
                                    contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text(sz)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { vm.setQuantity(state.quantity - 1) }) {
                        Icon(Icons.Filled.Remove, null)
                    }
                    Text(
                        state.quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(onClick = { vm.setQuantity(state.quantity + 1) }) {
                        Icon(Icons.Filled.Add, null)
                    }
                }

                Spacer(Modifier.height(28.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { vm.addToCart() },
                        modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Añadir")
                    }
                    Button(
                        onClick = {
                            vm.addToCart()
                            onCartClick()
                        },
                        modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
                    ) {
                        Text("Comprar ahora", fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
