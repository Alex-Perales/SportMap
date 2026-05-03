package com.tunalex.sportmap.ui.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tunalex.sportmap.ui.theme.BlueMedium
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GoldPremium
import com.tunalex.sportmap.ui.theme.IndigoDeep
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onStartActivity: () -> Unit,
    onOpenPremium: () -> Unit,
    vm: DashboardViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartActivity,
                containerColor = BlueVibrant,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.PlayArrow, null) },
                text = { Text("Empezar actividad ahora", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            HeroHeader(userName = state.user?.name ?: "deportista")
            Spacer(Modifier.height(20.dp))

            StatsRow(
                totalKm = state.totalKm,
                placesVisited = state.placesVisited
            )

            Spacer(Modifier.height(20.dp))
            NextReservationCard(state.nextReservation)

            Spacer(Modifier.height(20.dp))
            PremiumSection(
                isPremium = state.user?.isPremium == true,
                onClick = onOpenPremium
            )
            Spacer(Modifier.height(96.dp))
        }
    }
}

@Composable
private fun HeroHeader(userName: String) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1502082553048-f009c37129b9?w=1200",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, IndigoDeep.copy(alpha = 0.85f))
                )
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Malecón de Miraflores",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp
            )
            Text(
                text = "¡Hola, $userName!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "¿Listo para tu próximo entrenamiento?",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun StatsRow(totalKm: Double, placesVisited: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.DirectionsRun,
            value = String.format(Locale.US, "%.1f", totalKm),
            label = "Km recorridos"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.LocationOn,
            value = placesVisited.toString(),
            label = "Lugares visitados"
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlueMedium.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = BlueVibrant)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun NextReservationCard(reservation: com.tunalex.sportmap.data.local.entity.ReservationEntity?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BlueVibrant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.CalendarToday, null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Próxima reserva",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
                if (reservation == null) {
                    Text(
                        text = "Sin reservas próximas",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Reserva una cancha desde el mapa",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = reservation.placeName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    val date = SimpleDateFormat("EEE d MMM", Locale("es")).format(Date(reservation.date))
                    Text(
                        text = "$date · ${reservation.time} · ${reservation.peopleCount} pers.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumSection(isPremium: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(GoldPremium, Color(0xFFE8A92F)))
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFF6B4400))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isPremium) "Eres Pro ⭐" else "Hazte Pro",
                        color = Color(0xFF3B2400),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Pronósticos semanales: gráficas de rendimiento y horarios ideales según clima y afluencia.",
                    color = Color(0xFF3B2400),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TrendingUp, null, tint = Color(0xFF3B2400))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isPremium) "Ver pronóstico" else "Toca para conocer beneficios",
                        color = Color(0xFF3B2400),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
