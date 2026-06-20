package com.tunalex.sportmap.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                    onSearch = { state.pickedOrigin?.let { vm.startRoute(it) } }
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
    onSearch: () -> Unit
) {
    // Controls collapse/expand of the origin options
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Header panel (white card) ────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                // Back + search field + collapse toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // Origin field
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (state.pickedOrigin != null) GreenSafe
                                        else MaterialTheme.colorScheme.outline
                                    )
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = when {
                                    state.originLabel != null -> state.originLabel
                                    state.pickedOrigin != null -> "Obteniendo dirección…"
                                    else -> "Selecciona un punto de partida"
                                },
                                color = if (state.pickedOrigin != null)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Collapse / expand toggle  >
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                                          else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) "Contraer" else "Ampliar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Collapsible options ──────────────────────────────────────
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
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

                        // Search button — only enabled when a point was picked
                        Button(
                            onClick = onSearch,
                            enabled = state.pickedOrigin != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .heightIn(min = 48.dp),
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
                // Destination — RED pin (the sports place)
                state.place?.let { place ->
                    Marker(
                        state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                        title = place.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
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
            // Destination — RED pin (always visible so user knows where to go)
            state.place?.let { place ->
                Marker(
                    state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                    title = place.name,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
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
                Icon(Icons.Filled.ArrowBack, null, tint = Color.DarkGray)
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
            contentPadding = PaddingValues(bottom = 190.dp)
        ) {
            // Origin — GREEN pin (where user starts)
            state.origin?.let { o ->
                Marker(
                    state = rememberMarkerState(position = o),
                    title = "Mi ubicación",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            }
            // Destination — RED pin (the sports place)
            state.place?.let { place ->
                Marker(
                    state = rememberMarkerState(position = LatLng(place.lat, place.lng)),
                    title = place.name,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
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
                    Icon(Icons.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface)
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
                    result = state.result!!
                )
            }
        }
    }
}

// ─── Shared sub-composables ───────────────────────────────────────────────────

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = GreenSafe)
            Spacer(Modifier.width(12.dp))
            Text("Calculando ruta…", fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFFB71C1C),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun RouteInfoPanel(placeName: String, result: RouteResult) {
    var showSteps by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(placeName, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                RouteStatItem(Icons.Filled.Route, result.distanceText, "Distancia")
                RouteStatItem(Icons.Filled.Schedule, result.durationText, "Tiempo est.")
            }
            if (result.steps.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                if (!showSteps) {
                    TextButton(
                        onClick = { showSteps = true },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = BlueVibrant)
                    ) {
                        Icon(Icons.Filled.ExpandMore, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Indicaciones", fontSize = 13.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.ExpandLess, null,
                            tint = BlueVibrant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Indicaciones",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BlueVibrant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showSteps = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Cerrar indicaciones",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        itemsIndexed(result.steps) { idx, step ->
                            StepRow(num = idx + 1, step = step)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteStatItem(icon: ImageVector, value: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = GreenSafe, modifier = Modifier.size(22.dp))
        Column {
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StepRow(num: Int, step: RouteStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(50))
                .background(GreenSafe.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("$num", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenSafe)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(step.instruction, fontSize = 13.sp, lineHeight = 18.sp)
            Text(step.distanceText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
