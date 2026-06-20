package com.tunalex.sportmap.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GreenSafe
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import kotlinx.coroutines.launch

private val LIMA_CENTER = LatLng(-12.1167, -77.0339)

@Composable
fun MapScreen(
    onPlaceClick: (Long) -> Unit,
    vm: MapViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val focusedPlace by vm.focusedPlace.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LIMA_CENTER, 13f)
    }

    LaunchedEffect(state.userLocation) {
        state.userLocation?.let { loc ->
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(loc, 14f))
        }
    }

    LaunchedEffect(focusedPlace) {
        focusedPlace?.let { place ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(place.lat, place.lng), 16f)
            )
            vm.clearFocusedPlace()
        }
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchUserLocation(fusedClient) { loc ->
            vm.setUserLocation(loc.latitude, loc.longitude)
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) fetchUserLocation(fusedClient) { loc ->
            vm.setUserLocation(loc.latitude, loc.longitude)
        } else {
            permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.55f)) {
                SportFilterDrawer(
                    selected = state.selectedSport,
                    nearbyOnly = state.nearbyOnly,
                    hasLocation = state.userLocation != null,
                    onSelect = { vm.selectSport(it) },
                    onToggleNearby = { vm.toggleNearby() },
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                state.userLocation?.let { loc ->
                    Marker(
                        state = rememberMarkerState(position = loc),
                        title = "Mi ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }

                state.placesWithDistance.forEach { (place, distKm) ->
                    PlaceMapContent(
                        place = place,
                        distanceKm = distKm,
                        onPlaceClick = onPlaceClick
                    )
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                PlaceSearchBar(
                    query = state.searchQuery,
                    suggestions = state.suggestions,
                    onQueryChange = { vm.setSearchQuery(it) },
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSuggestionClick = { vm.focusPlace(it) }
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (state.places.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (state.searchQuery.isNotBlank())
                                    "Sin resultados para \"${state.searchQuery}\"."
                                else if (state.nearbyOnly)
                                    "No hay canchas en un radio de 5 km."
                                else
                                    "No hay lugares para este deporte.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceMapContent(
    place: PlaceEntity,
    distanceKm: Double?,
    onPlaceClick: (Long) -> Unit
) {
    val pos = LatLng(place.lat, place.lng)
    val markerState = rememberMarkerState(position = pos)
    val snippet = buildString {
        if (distanceKm != null) append("${"%.1f".format(distanceKm)} km · ")
        append(if (place.isPrivate) "Privado" else "Público")
        append(" · ⭐ ${place.rating}")
    }

    Marker(
        state = markerState,
        title = place.name,
        snippet = snippet,
        onClick = { _ -> onPlaceClick(place.id); true }
    )
}

@Composable
private fun PlaceSearchBar(
    query: String,
    suggestions: List<com.tunalex.sportmap.data.local.entity.PlaceEntity>,
    onQueryChange: (String) -> Unit,
    onMenuClick: () -> Unit,
    onSuggestionClick: (com.tunalex.sportmap.data.local.entity.PlaceEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Filled.Menu, "Abrir menú", tint = Color.Gray)
                }
            }
            Spacer(Modifier.width(8.dp))
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Buscar lugar...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Close, "Limpiar", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(50))
                    .height(52.dp)
            )
        }

        if (query.isNotBlank() && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .heightIn(max = 300.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                LazyColumn {
                    itemsIndexed(suggestions) { index, place ->
                        SuggestionItem(place = place, onClick = { onSuggestionClick(place) })
                        if (index < suggestions.lastIndex) {
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    place: com.tunalex.sportmap.data.local.entity.PlaceEntity,
    onClick: () -> Unit
) {
    val sportLabel = ALL_SPORTS.find { it.key == place.sportType }?.label ?: place.sportType
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = place.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(text = sportLabel, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun SportFilterDrawer(
    selected: String,
    nearbyOnly: Boolean,
    hasLocation: Boolean,
    onSelect: (String) -> Unit,
    onToggleNearby: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Menu, "Contraer menú")
            }
            Spacer(Modifier.width(8.dp))
            Text("Deportes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        if (hasLocation) {
            FilterChip(
                label = "Cerca (5km)",
                icon = Icons.Filled.MyLocation,
                isSelected = nearbyOnly,
                activeColor = GreenSafe,
                onClick = onToggleNearby,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }

        ALL_SPORTS.forEach { sport ->
            FilterChip(
                label = sport.label,
                icon = iconForSport(sport.iconKey),
                isSelected = sport.key == selected,
                activeColor = BlueVibrant,
                onClick = { onSelect(sport.key) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) activeColor else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}

internal fun iconForSport(key: String): ImageVector = when (key) {
    "soccer" -> Icons.Filled.SportsSoccer
    "volley" -> Icons.Filled.SportsVolleyball
    "tennis" -> Icons.Filled.SportsTennis
    "running" -> Icons.Filled.DirectionsRun
    "swim" -> Icons.Filled.Pool
    "bike" -> Icons.Filled.DirectionsBike
    "wellness" -> Icons.Filled.SelfImprovement
    else -> Icons.Filled.AllInclusive
}

@SuppressLint("MissingPermission")
private fun fetchUserLocation(
    client: com.google.android.gms.location.FusedLocationProviderClient,
    onResult: (LatLng) -> Unit
) {
    client.lastLocation
        .addOnSuccessListener { loc ->
            if (loc != null) {
                onResult(LatLng(loc.latitude, loc.longitude))
            } else {
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { l ->
                        l?.let { onResult(LatLng(it.latitude, it.longitude)) }
                    }
            }
        }
}
