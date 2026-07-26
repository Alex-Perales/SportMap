package com.tunalex.sportmap.ui.place

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tunalex.sportmap.ui.components.PaymentBottomSheet
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GreenSafe
import com.tunalex.sportmap.ui.theme.IndigoDeep
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    placeId: Long,
    onBack: () -> Unit,
    onNavigate: (placeId: Long) -> Unit = {},
    vm: PlaceDetailViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(placeId) { vm.load(placeId) }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            vm.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val place = state.place
        if (place == null) {
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
                val photos = place.photoUrls.split(",").filter { it.isNotBlank() }
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    items(photos) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(260.dp)
                                .fillParentMaxWidth()
                        )
                    }
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(IndigoDeep.copy(alpha = 0.6f), Color.Transparent, Color.Transparent)
                        )
                    )
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 24.dp, start = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                IconButton(
                    onClick = { vm.toggleFavorite() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                ) {
                    Icon(
                        if (state.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (state.isFavorite) "Quitar de favoritos" else "Guardar en favoritos",
                        tint = if (state.isFavorite) Color(0xFFEF4444) else Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = place.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(if (place.isPrivate) "Privado" else "Público") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (place.isPrivate) BlueVibrant.copy(alpha = 0.15f)
                            else Color(0xFF22C55E).copy(alpha = 0.15f)
                        )
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(
                        " ${"%.1f".format(state.averageRating)}" +
                            if (state.reviews.isNotEmpty()) " (${state.reviews.size})" else "",
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        " ${"%.4f".format(place.lat)}, ${"%.4f".format(place.lng)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(place.description, fontSize = 14.sp, lineHeight = 20.sp)

                Spacer(Modifier.height(20.dp))
                Text("Servicios", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                val services = place.services.split(",").filter { it.isNotBlank() }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    services.take(4).forEach { svc -> ServiceChip(svc.trim()) }
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { onNavigate(place.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSafe)
                ) {
                    Icon(Icons.Filled.Navigation, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Cómo llegar", fontWeight = FontWeight.SemiBold)
                }

                if (place.isPrivate) {
                    Spacer(Modifier.height(28.dp))
                    Text("Reservar", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    if (place.pricePerHour > 0) {
                        Text(
                            "S/. ${"%.2f".format(place.pricePerHour)} por hora",
                            color = BlueVibrant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    ReservationForm(
                        state = state,
                        onPickDate = { vm.setDate(it) },
                        onPickTime = { vm.setTime(it) },
                        onPeople = { vm.setPeople(it) },
                        onValidate = { vm.validateReservation() == null },
                        onSubmitPayment = { file -> vm.submitReservationPayment(file) }
                    )
                } else {
                    Spacer(Modifier.height(20.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF15803D))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Espacio público — acceso libre, no requiere reserva.",
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))
                ReviewsSection(
                    state = state,
                    onSubmitReview = { rating, comment -> vm.submitReview(rating, comment) }
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ReviewsSection(
    state: PlaceDetailUiState,
    onSubmitReview: (rating: Int, comment: String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Reseñas", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        TextButton(onClick = { showDialog = true }) {
            Text(if (state.myReview != null) "Editar mi reseña" else "Escribir reseña")
        }
    }
    Spacer(Modifier.height(8.dp))

    if (state.reviews.isEmpty()) {
        Text(
            "Aún no hay reseñas. ¡Sé el primero en calificar este lugar!",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            state.reviews.forEach { rw -> ReviewItem(rw) }
        }
    }

    if (showDialog) {
        ReviewDialog(
            initialRating = state.myReview?.rating ?: 5,
            initialComment = state.myReview?.comment ?: "",
            onDismiss = { showDialog = false },
            onSubmit = { rating, comment ->
                onSubmitReview(rating, comment)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ReviewItem(reviewWithUser: com.tunalex.sportmap.data.repository.AppRepository.ReviewWithUser) {
    val review = reviewWithUser.review
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(reviewWithUser.userName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Row {
                    repeat(5) { i ->
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i < review.rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(review.comment, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun ReviewDialog(
    initialRating: Int,
    initialComment: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tu reseña", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    repeat(5) { i ->
                        IconButton(onClick = { rating = i + 1 }) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "${i + 1} estrellas",
                                tint = if (i < rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it.take(300) },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) { Text("Publicar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun ServiceChip(label: String) {
    AssistChip(
        onClick = {},
        label = { Text(label, fontSize = 12.sp) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationForm(
    state: PlaceDetailUiState,
    onPickDate: (Long?) -> Unit,
    onPickTime: (String) -> Unit,
    onPeople: (Int) -> Unit,
    onValidate: () -> Boolean,
    onSubmitPayment: suspend (java.io.File) -> Result<Unit>
) {
    var dateDialog by remember { mutableStateOf(false) }
    var timeDialog by remember { mutableStateOf(false) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val dateText = state.selectedDateMillis?.let {
        SimpleDateFormat("EEE d MMM yyyy", Locale("es")).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(Date(it))
    } ?: "Seleccionar fecha"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { dateDialog = true },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.CalendarToday, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(dateText, fontSize = 13.sp, maxLines = 1)
        }
        OutlinedButton(
            onClick = { timeDialog = true },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Filled.Schedule, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(state.selectedTime, fontSize = 13.sp)
        }
    }

    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Personas", fontSize = 14.sp)
            Text(
                "Aforo máximo: 20",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = { onPeople(state.peopleCount - 1) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Disminuir personas")
        }
        Text(
            state.peopleCount.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        IconButton(onClick = { onPeople(state.peopleCount + 1) }) {
            Icon(Icons.Filled.Add, contentDescription = "Aumentar personas")
        }
    }

    Spacer(Modifier.height(20.dp))
    Button(
        onClick = {
            if (onValidate()) showConfirmDialog = true
        },
        enabled = !state.reservationDone,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
    ) {
        Text(
            if (state.reservationDone) "Pendiente de confirmación" else "Reservar",
            fontWeight = FontWeight.SemiBold
        )
    }

    // Payment bottom sheet (único medio de pago: Yape/Plin + comprobante)
    if (showPaymentSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        PaymentBottomSheet(
            amount = state.place?.pricePerHour ?: 0.0,
            sheetState = sheetState,
            onDismiss = { showPaymentSheet = false },
            onSubmitProof = onSubmitPayment,
            onSuccess = { showPaymentSheet = false }
        )
    }

    // Confirmation dialog
    if (showConfirmDialog) {
        val placeName = state.place?.name ?: "este lugar"
        val dateFormatted = state.selectedDateMillis?.let {
            SimpleDateFormat("d 'de' MMMM", Locale("es")).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(Date(it))
        } ?: ""
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("¿Confirmar reserva?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Realmente deseas reservar en $placeName el $dateFormatted a las ${state.selectedTime}?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        showPaymentSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (dateDialog) {
        // El DatePicker de Compose trabaja en UTC, así que "hoy" debe calcularse
        // como medianoche UTC del día calendario local, no medianoche en hora local
        // (si no, en zonas como Lima -UTC-5- el día de hoy queda bloqueado y las
        // fechas se corren un día).
        val todayStart = remember {
            val local = java.util.Calendar.getInstance()
            val year = local.get(java.util.Calendar.YEAR)
            val month = local.get(java.util.Calendar.MONTH)
            val day = local.get(java.util.Calendar.DAY_OF_MONTH)
            java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                clear()
                set(year, month, day, 0, 0, 0)
            }.timeInMillis
        }
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDateMillis ?: System.currentTimeMillis(),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= todayStart
            }
        )
        DatePickerDialog(
            onDismissRequest = { dateDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onPickDate(pickerState.selectedDateMillis)
                    dateDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { dateDialog = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
    if (timeDialog) {
        val tps = rememberTimePickerState(initialHour = 18, initialMinute = 0, is24Hour = true)
        androidx.compose.ui.window.Dialog(onDismissRequest = { timeDialog = false }) {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Selecciona la hora", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    TimePicker(state = tps)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { timeDialog = false }) { Text("Cancelar") }
                        TextButton(onClick = {
                            onPickTime("%02d:%02d".format(tps.hour, tps.minute))
                            timeDialog = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}

