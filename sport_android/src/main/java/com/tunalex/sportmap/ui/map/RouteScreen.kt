package com.tunalex.sportmap.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.tunalex.sportmap.data.remote.RouteResult
import com.tunalex.sportmap.data.remote.RouteStep
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GreenSafe
import com.tunalex.sportmap.ui.theme.OrangeAlert
import com.tunalex.sportmap.ui.theme.RedDanger
import com.tunalex.sportmap.viewmodel.SportMapViewModels

private val LIMA_CENTER = LatLng(-12.1167, -77.0339)
private val RouteOrange = Color(0xFFFF6B2C)

@Composable
fun RouteScreen(
    placeId: Long,
    onBack: () -> Unit,
    vm: RouteViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LIMA_CENTER, 13f)
    }

    LaunchedEffect(placeId) { vm.initForPlace(placeId) }

    // Center camera on destination when place loads (Phase 1)
    LaunchedEffect(state.place) {
        if (state.phase == RoutePhase.SELECT_ORIGIN) {
            state.place?.let { place ->
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(place.lat, place.lng), 15f)
                )
            }
        }
    }

    // Fit route in camera view when route loads (Phase 2)
    LaunchedEffect(state.result) {
        val pts = state.result?.points ?: return@LaunchedEffect
        if (pts.size >= 2) {
            val bounds = LatLngBounds.builder().apply { pts.forEach { include(it) } }.build()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        }
    }

    // Reverse-geocode picked origin to a human-readable address
    LaunchedEffect(state.pickedOrigin) {
        val latLng = state.pickedOrigin ?: return@LaunchedEffect
        val label = withContext(Dispatchers.IO) {
            try {
                @Suppress("DEPRECATION")
                val results = Geocoder(context, Locale("es", "PE"))
                    .getFromLocation(latLng.latitude, latLng.longitude, 1)
                results?.firstOrNull()?.let { addr ->
                    listOfNotNull(
                        addr.thoroughfare,          // calle
                        addr.subLocality ?: addr.locality  // distrito o ciudad
                    ).filter { it.isNotBlank() }.joinToString(", ")
                }.takeIf { !it.isNullOrBlank() } ?: "Ubicación seleccionada"
            } catch (_: Exception) {
                "Ubicación seleccionada"
            }
        }
        vm.setOriginLabel(label)
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchGpsAndStartRoute(fusedClient, vm)
        else vm.setError("Se requiere permiso de ubicación para calcular la ruta.")
    }

    fun onUseCurrentLocation() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) fetchGpsAndStartRoute(fusedClient, vm)
        else permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Geocodifica el texto escrito (dirección o nombre de lugar) y arranca la
    // ruta desde esa coordenada.
    fun onSearchTypedOrigin(query: String) {
        val q = query.trim()
        if (q.isBlank()) return
        scope.launch {
            val latLng = withContext(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    Geocoder(context, Locale("es", "PE"))
                        .getFromLocationName("$q, Lima, Perú", 1)
                        ?.firstOrNull()
                        ?.let { LatLng(it.latitude, it.longitude) }
                } catch (_: Exception) {
                    null
                }
            }
            if (latLng != null) {
                vm.setOriginLabel(q)
                vm.startRoute(latLng)
            } else {
                vm.setError("No encontramos \"$q\". Prueba con una dirección más específica.")
            }
        }
    }

    when (state.phase) {
        RoutePhase.SELECT_ORIGIN -> {
            if (state.mapPickerActive) {
                MapPickerScreen(
                    state = state,
                    cameraPositionState = cameraPositionState,
                    onMapClick = { vm.pickOriginOnMap(it) },
                    onCancel = { vm.cancelMapPicker() },
                    onConfirm = { state.pickedOrigin?.let { vm.startRoute(it) } }
                )
            } else {
                OriginSelectionScreen(
                    state = state,
                    cameraPositionState = cameraPositionState,
                    onBack = onBack,
                    onUseCurrentLocation = { onUseCurrentLocation() },
                    onPickOnMap = { vm.activateMapPicker() },
                    onSearch = { state.pickedOrigin?.let { vm.startRoute(it) } },
                    onSearchAddress = { onSearchTypedOrigin(it) }
                )
            }
        }

        RoutePhase.ROUTE_RESULT -> {
            RouteResultScreen(
                state = state,
                cameraPositionState = cameraPositionState,
                onBack = { vm.backToSelectOrigin() }
            )
        }
    }
}

// ─── Phase 1: Origin Selection ───────────────────────────────────────────────

@Composable
private fun OriginSelectionScreen(
    state: RouteUiState,
    cameraPositionState: CameraPositionState,
    onBack: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    onPickOnMap: () -> Unit,
    onSearch: () -> Unit,
    onSearchAddress: (String) -> Unit
) {
    // Opciones colapsadas por defecto: se despliegan al tocar el chevron.
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Refleja en el campo la dirección obtenida al elegir en el mapa / usar GPS.
    LaunchedEffect(state.originLabel) {
        state.originLabel?.let { if (it != query) query = it }
    }

    val hasText = query.isNotBlank()
    val dotColor = if (state.pickedOrigin != null || hasText) GreenSafe
        else MaterialTheme.colorScheme.outline

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header panel ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                // Back + campo editable + chevron
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // Campo de origen — EDITABLE
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(start = 14.dp, end = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(BlueVibrant),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearchAddress(query) }),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 14.dp),
                            decorationBox = { inner ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (query.isEmpty()) {
                                        Text(
                                            "Escribe una dirección o lugar",
                                            color = MaterialTheme.colorScheme.outline,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    inner()
                                }
                            }
                        )
                        IconButton(
                            onClick = { onSearchAddress(query) },
                            enabled = hasText
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Buscar dirección",
                                tint = if (hasText) BlueVibrant else MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Colapsar / desplegar opciones
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                                          else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Contraer" else "Ampliar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                state.error?.let { err ->
                    Text(
                        err,
                        color = RedDanger,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                }

                // ── Opciones colapsables ─────────────────────────────────────
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider()
                        OriginOption(
                            icon = Icons.Filled.MyLocation,
                            iconTint = BlueVibrant,
                            label = "Tu ubicación actual",
                            onClick = onUseCurrentLocation
                        )
                        HorizontalDivider()
                        OriginOption(
                            icon = Icons.Filled.Map,
                            iconTint = RouteOrange,
                            label = "Elige en el mapa",
                            onClick = onPickOnMap
                        )
                        HorizontalDivider()

                        // Botón — activo cuando ya hay un punto elegido en el mapa/GPS
                        Button(
                            onClick = onSearch,
                            enabled = state.pickedOrigin != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .heightIn(min = 50.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenSafe,
                                disabledContainerColor = GreenSafe.copy(alpha = 0.35f)
                            )
                        ) {
                            Icon(Icons.Filled.Navigation, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Buscar rutas", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }

        // ── Map (fills remaining space) ──────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Destination — pin with the sport emoji (the sports place)
                state.place?.let { place ->
                    val sizePx = with(LocalDensity.current) { 44.dp.roundToPx() }
                    Marker(
                        state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                        title = "${emojiForSport(place.sportType)} ${place.name}",
                        icon = sportMarkerIcon(place.sportType, sizePx)
                    )
                }
                // Picked origin — GREEN pin
                state.pickedOrigin?.let { origin ->
                    Marker(
                        state = rememberMarkerState(position = origin),
                        title = "Punto de partida",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }
            }
        }
    }
}

@Composable
private fun OriginOption(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Map Picker (full-screen tap-to-select mode) ─────────────────────────────

@Composable
private fun MapPickerScreen(
    state: RouteUiState,
    cameraPositionState: CameraPositionState,
    onMapClick: (LatLng) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    // When user picks a point, center camera on it
    LaunchedEffect(state.pickedOrigin) {
        state.pickedOrigin?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 16f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = onMapClick
        ) {
            // Destination — pin with the sport emoji (always visible so user knows where to go)
            state.place?.let { place ->
                val sizePx = with(LocalDensity.current) { 44.dp.roundToPx() }
                Marker(
                    state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                    title = "${emojiForSport(place.sportType)} ${place.name}",
                    icon = sportMarkerIcon(place.sportType, sizePx)
                )
            }
            // Picked origin — GREEN pin (appears once user taps the map)
            state.pickedOrigin?.let { origin ->
                Marker(
                    state = rememberMarkerState(position = origin),
                    title = "Punto de partida",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            }
        }

        // Top instruction bar
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Cancelar", tint = Color.DarkGray)
            }
            Text(
                text = when {
                    state.pickedOrigin == null -> "Toca el mapa para elegir tu punto de partida"
                    state.originLabel != null  -> state.originLabel
                    else -> "Obteniendo dirección…"
                },
                fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
        }

        // Confirm button (appears when a point is picked)
        AnimatedVisibility(
            visible = state.pickedOrigin != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenSafe)
            ) {
                Icon(Icons.Filled.Navigation, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Buscar rutas desde aquí", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }
    }
}

// ─── Phase 2: Route Result ────────────────────────────────────────────────────

@Composable
private fun RouteResultScreen(
    state: RouteUiState,
    cameraPositionState: CameraPositionState,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true,
                scrollGesturesEnabled = true
            ),
            // Push zoom controls and attribution above the bottom info panel
            contentPadding = PaddingValues(bottom = 240.dp)
        ) {
            // Origin — GREEN pin (where user starts)
            state.origin?.let { o ->
                Marker(
                    state = rememberMarkerState(position = o),
                    title = "Mi ubicación",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            }
            // Destination — pin with the sport emoji (the sports place)
            state.place?.let { place ->
                val sizePx = with(LocalDensity.current) { 44.dp.roundToPx() }
                Marker(
                    state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                    title = "${emojiForSport(place.sportType)} ${place.name}",
                    icon = sportMarkerIcon(place.sportType, sizePx)
                )
            }
            // Route polyline
            state.result?.let { route ->
                Polyline(
                    points = route.points,
                    color = BlueVibrant,
                    width = 20f
                )
            }
        }

        // Top bar overlay (white card)
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.onSurface)
                }
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.place?.name ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Bottom info panel
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> LoadingCard()
                state.error != null -> ErrorCard(state.error!!)
                state.result != null -> RouteInfoPanel(
                    placeName = state.place?.name ?: "",
                    result = state.result!!,
                    destLatLng = state.place?.let { LatLng(it.lat, it.lng) }
                )
            }
        }
    }
}

// ─── Shared sub-composables ───────────────────────────────────────────────────

/** Contenedor común de las tarjetas inferiores: hoja redondeada arriba, con
 *  "asa" (drag handle) para que se lea como un bottom sheet profesional. */
@Composable
private fun RouteSheet(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            content()
        }
    }
}

@Composable
private fun LoadingCard() {
    RouteSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = BlueVibrant)
            Spacer(Modifier.width(14.dp))
            Text("Calculando la mejor ruta…", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    RouteSheet {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(RedDanger.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Close, null, tint = RedDanger, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(message, color = RedDanger, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun RouteInfoPanel(placeName: String, result: RouteResult, destLatLng: LatLng?) {
    var showSteps by remember { mutableStateOf(false) }
    var minimized by remember { mutableStateOf(false) }
    val context = LocalContext.current

    RouteSheet {
        Column(modifier = Modifier.padding(start = 20.dp, end = 8.dp, top = 6.dp, bottom = 14.dp)) {

            // Encabezado: destino + botón minimizar / restaurar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(RedDanger.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Place, null, tint = RedDanger, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "CÓMO LLEGAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Text(
                        placeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { minimized = !minimized }) {
                    Icon(
                        if (minimized) Icons.Filled.ExpandLess else Icons.Filled.Close,
                        contentDescription = if (minimized) "Expandir" else "Minimizar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tarjetas de distancia y tiempo (se ocultan al minimizar)
            AnimatedVisibility(
                visible = !minimized,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RouteStatTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Route,
                            accent = OrangeAlert,
                            value = result.distanceText,
                            label = "Distancia"
                        )
                        RouteStatTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Filled.Schedule,
                            accent = BlueVibrant,
                            value = result.durationText,
                            label = "Tiempo est."
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // CTA — abrir navegación en Google Maps
            Button(
                onClick = {
                    destLatLng?.let { d ->
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=${d.latitude},${d.longitude}&mode=w")
                            ).setPackage("com.google.android.apps.maps")
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("geo:${d.latitude},${d.longitude}"))
                                )
                            } catch (_: Exception) { /* sin app de mapas */ }
                        }
                    }
                },
                enabled = destLatLng != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenSafe)
            ) {
                Icon(Icons.AutoMirrored.Filled.DirectionsWalk, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Iniciar navegación", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            if (result.steps.isNotEmpty() && !minimized) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Cabecera desplegable de indicaciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showSteps = !showSteps }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Navigation,
                        null,
                        tint = BlueVibrant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Indicaciones paso a paso",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${result.steps.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueVibrant,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BlueVibrant.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        if (showSteps) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AnimatedVisibility(
                    visible = showSteps,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 240.dp)) {
                        itemsIndexed(result.steps) { idx, step ->
                            StepRow(num = idx + 1, step = step, isLast = idx == result.steps.lastIndex)
                        }
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun RouteStatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    accent: Color,
    value: String,
    label: String
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StepRow(num: Int, step: RouteStep, isLast: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(BlueVibrant),
                contentAlignment = Alignment.Center
            ) {
                Text("$num", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .width(2.dp)
                        .height(22.dp)
                        .background(BlueVibrant.copy(alpha = 0.25f))
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                step.instruction,
                fontSize = 14.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                step.distanceText,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
private fun fetchGpsAndStartRoute(
    client: com.google.android.gms.location.FusedLocationProviderClient,
    vm: RouteViewModel
) {
    client.lastLocation.addOnSuccessListener { loc ->
        if (loc != null) {
            vm.startRoute(LatLng(loc.latitude, loc.longitude))
        } else {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { l ->
                    if (l != null) vm.startRoute(LatLng(l.latitude, l.longitude))
                    else vm.setError("No se pudo obtener tu ubicación GPS.")
                }
                .addOnFailureListener { vm.setError("Error al obtener ubicación.") }
        }
    }.addOnFailureListener { vm.setError("Error al obtener ubicación.") }
}
