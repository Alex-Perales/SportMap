package com.tunalex.sportmap.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.data.local.entity.OrderEntity
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    onBack: () -> Unit,
    vm: OrdersHistoryViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val orders by vm.orders.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Receipt, null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Aún no tienes pedidos", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.id }) { order ->
                    OrderCard(order = order, snackbar = snackbar, vm = vm)
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

private val ORDER_TYPE_LABELS = mapOf(
    "store" to "Compra en tienda",
    "reservation" to "Reserva",
    "premium" to "Suscripción Premium"
)

@Composable
private fun OrderCard(
    order: OrderEntity,
    snackbar: androidx.compose.material3.SnackbarHostState,
    vm: OrdersHistoryViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
            scope.launch {
                uploading = true
                val file = copyUriToCacheFileOrder(context, uri)
                if (file != null) {
                    vm.reuploadProof(order.id, file)
                        .onSuccess { snackbar.showSnackbar("Comprobante reenviado. Quedó pendiente de revisión.") }
                        .onFailure { snackbar.showSnackbar("No se pudo reenviar el comprobante. Intenta de nuevo.") }
                }
                uploading = false
                pickedUri = null
            }
        }
    }

    val dateText = remember(order.createdAt) {
        SimpleDateFormat("d MMM yyyy", Locale("es")).format(Date(order.createdAt))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        ORDER_TYPE_LABELS[order.orderType] ?: order.orderType,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(dateText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    "S/. ${"%.2f".format(order.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = BlueVibrant
                )
            }
            Spacer(Modifier.height(10.dp))
            OrderStatusBadge(order.status)

            if (order.status == "rechazado") {
                Spacer(Modifier.height(10.dp))
                if (!order.motivoRechazo.isNullOrBlank()) {
                    Text(
                        "Motivo: ${order.motivoRechazo}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                }
                OutlinedButton(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    enabled = !uploading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uploading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Enviando...")
                    } else {
                        Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Volver a subir comprobante")
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusBadge(status: String) {
    val (label, color) = when (status) {
        "pendiente" -> "En revisión" to Color(0xFFB45309)
        "aprobado" -> "Pagado ✓" to Color(0xFF16A34A)
        "rechazado" -> "Rechazado" to Color(0xFFDC2626)
        "cancelado_reembolsado" -> "Reembolsado ✓" to Color(0xFF6B7280)
        else -> status to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
    }
}

private fun copyUriToCacheFileOrder(context: android.content.Context, uri: Uri): File? = try {
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "comprobante_reenvio_${System.currentTimeMillis()}.jpg")
    input.use { stream -> file.outputStream().use { out -> stream.copyTo(out) } }
    file
} catch (_: Exception) {
    null
}
