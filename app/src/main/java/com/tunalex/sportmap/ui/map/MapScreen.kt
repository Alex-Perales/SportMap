package com.tunalex.sportmap.ui.map

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GreenSafe
import com.tunalex.sportmap.ui.theme.IndigoDeep
import com.tunalex.sportmap.ui.theme.OrangeAlert
import com.tunalex.sportmap.ui.theme.RedDanger
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private val LIMA_CENTER = GeoPoint(-12.1167, -77.0339)

@Composable
fun MapScreen(
    onPlaceClick: (Long) -> Unit,
    vm: MapViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Configuración inicial de osmdroid (user-agent, cache)
    remember(context) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = context.packageName
        }
    }

    // Una sola instancia de MapView que sobrevive a recomposiciones
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(13.5)
            controller.setCenter(LIMA_CENTER)
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
        }
    }

    // Ata el ciclo de vida del MapView al Composable (onResume/onPause/onDestroy)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SportChipsRow(
            selected = state.selectedSport,
            onSelect = { vm.selectSport(it) }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { mv ->
                    // Limpia overlays anteriores y vuelve a dibujar según el estado actual
                    mv.overlays.clear()
                    state.places.forEach { place ->
                        addPlaceOverlay(mv, place, onPlaceClick)
                    }
                    mv.invalidate()
                }
            )

            AqiBadge(
                aqi = state.averageAqi,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            )

            // Atribución requerida por OpenStreetMap
            Text(
                text = "© OpenStreetMap contributors",
                fontSize = 9.sp,
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 4.dp, bottom = 2.dp)
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )

            if (state.places.isEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(
                        "No hay lugares para este deporte.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

private fun addPlaceOverlay(
    mapView: MapView,
    place: PlaceEntity,
    onClick: (Long) -> Unit
) {
    val ctx = mapView.context
    val center = GeoPoint(place.lat, place.lng)

    // Marcador
    val marker = Marker(mapView).apply {
        position = center
        title = place.name
        subDescription = if (place.isPrivate) "Privado · ⭐ ${place.rating}"
        else "Público · ⭐ ${place.rating}"
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        setOnMarkerClickListener { _, _ ->
            onClick(place.id)
            true
        }
    }
    mapView.overlays.add(marker)

    when (place.category) {
        "trayecto" -> {
            // B. Línea verde sólida representando una ruta segura
            val line = Polyline(mapView).apply {
                outlinePaint.color = GreenSafe.toArgb()
                outlinePaint.strokeWidth = 12f
                outlinePaint.strokeCap = Paint.Cap.ROUND
                outlinePaint.strokeJoin = Paint.Join.ROUND
                setPoints(generateRoutePoints(place.lat, place.lng))
            }
            mapView.overlays.add(line)
        }
        "wellness" -> {
            // C. Línea punteada para zonas tranquilas
            val line = Polyline(mapView).apply {
                outlinePaint.color = IndigoDeep.toArgb()
                outlinePaint.strokeWidth = 9f
                outlinePaint.pathEffect = android.graphics.DashPathEffect(
                    floatArrayOf(12f, 12f), 0f
                )
                setPoints(generateWellnessPath(place.lat, place.lng))
            }
            mapView.overlays.add(line)
        }
    }
}

private fun generateRoutePoints(lat: Double, lng: Double): List<GeoPoint> = listOf(
    GeoPoint(lat - 0.005, lng - 0.003),
    GeoPoint(lat - 0.002, lng - 0.001),
    GeoPoint(lat, lng),
    GeoPoint(lat + 0.002, lng + 0.002),
    GeoPoint(lat + 0.005, lng + 0.004),
    GeoPoint(lat + 0.008, lng + 0.001)
)

private fun generateWellnessPath(lat: Double, lng: Double): List<GeoPoint> = listOf(
    GeoPoint(lat, lng),
    GeoPoint(lat + 0.001, lng + 0.001),
    GeoPoint(lat + 0.0015, lng - 0.0005),
    GeoPoint(lat + 0.002, lng + 0.0015)
)

@Composable
private fun SportChipsRow(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ALL_SPORTS.forEach { sport ->
            val isSelected = sport.key == selected
            Card(
                onClick = { onSelect(sport.key) },
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) BlueVibrant
                    else MaterialTheme.colorScheme.surface
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
                        imageVector = iconForSport(sport.iconKey),
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = sport.label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

internal fun iconForSport(key: String): ImageVector = when (key) {
    "soccer" -> Icons.Filled.SportsSoccer
    "volley" -> Icons.Filled.SportsVolleyball
    "running" -> Icons.Filled.DirectionsRun
    "swim" -> Icons.Filled.Pool
    "bike" -> Icons.Filled.DirectionsBike
    "wellness" -> Icons.Filled.SelfImprovement
    else -> Icons.Filled.AllInclusive
}

@Composable
private fun AqiBadge(aqi: Int, modifier: Modifier = Modifier) {
    val (color, label) = when {
        aqi <= 50 -> GreenSafe to "Bueno"
        aqi <= 100 -> OrangeAlert to "Moderado"
        else -> RedDanger to "Alto"
    }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Air, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("AQI $aqi", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
