package com.tunalex.sportmap.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tunalex.sportmap.ui.theme.BlueMedium
import com.tunalex.sportmap.ui.theme.BlueVibrant

private data class FaqItem(val question: String, val answer: String)
private data class FaqSection(val title: String, val icon: ImageVector, val items: List<FaqItem>)

private val faqSections = listOf(
    FaqSection(
        title = "Reservas",
        icon = Icons.Filled.DateRange,
        items = listOf(
            FaqItem(
                "¿Cómo reservo una cancha?",
                "Toca un marcador en el mapa, elige la cancha que quieras, selecciona la fecha y hora disponible y confirma tu reserva. Recibirás la confirmación en tu dashboard."
            ),
            FaqItem(
                "¿Puedo cancelar una reserva?",
                "Sí. Ve a tu dashboard, encuentra la reserva activa y toca «Cancelar». Las cancelaciones con más de 2 horas de anticipación son sin costo."
            ),
            FaqItem(
                "¿Con cuánta anticipación puedo reservar?",
                "Puedes reservar hasta 7 días antes. Los usuarios Pro tienen acceso a reservas con hasta 30 días de anticipación."
            )
        )
    ),
    FaqSection(
        title = "Mapa y canchas",
        icon = Icons.Filled.LocationOn,
        items = listOf(
            FaqItem(
                "¿Cómo filtro por tipo de deporte?",
                "Usa el botón de filtros en la pantalla del mapa para seleccionar fútbol, básquet, tenis u otros deportes disponibles."
            ),
            FaqItem(
                "¿Qué significan los colores de los marcadores?",
                "Verde = disponible ahora, Naranja = disponible hoy, Rojo = sin disponibilidad. Toca cualquier marcador para ver el detalle completo."
            )
        )
    ),
    FaqSection(
        title = "Cuenta y perfil",
        icon = Icons.Filled.Person,
        items = listOf(
            FaqItem(
                "¿Cómo cambio mi nombre o distrito?",
                "Ve a Ajustes → Editar perfil y actualiza tus datos. Los cambios se guardan automáticamente."
            ),
            FaqItem(
                "¿Cómo elimino mi cuenta?",
                "Ve a Ajustes → Eliminar cuenta. Esta acción es permanente e irreversible: se borrarán tu perfil, reservas e historial de actividad."
            )
        )
    ),
    FaqSection(
        title = "SportMap Pro",
        icon = Icons.Filled.Star,
        items = listOf(
            FaqItem(
                "¿Qué incluye SportMap Pro?",
                "Pronósticos del clima para canchas, reservas con hasta 30 días de anticipación, experiencia sin anuncios y descuentos exclusivos en la tienda."
            ),
            FaqItem(
                "¿Cómo cancelo mi suscripción Pro?",
                "Puedes cancelarla en cualquier momento desde la sección Premium en Ajustes. Mantendrás los beneficios hasta el fin del período pagado."
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFaqScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayuda y soporte", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(BlueVibrant, BlueMedium)))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Column {
                    Text(
                        "¿En qué podemos ayudarte?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Encuentra respuestas a las preguntas más frecuentes",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            faqSections.forEach { section ->
                FaqSectionBlock(section)
                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(4.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "¿No encontraste tu respuesta?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Nuestro equipo está listo para ayudarte.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(18.dp))
                    ContactRow(
                        icon = Icons.Filled.Email,
                        label = "Correo electrónico",
                        value = "support@sportmap.app"
                    )
                    Spacer(Modifier.height(12.dp))
                    ContactRow(
                        icon = Icons.Filled.Schedule,
                        label = "Horario de atención",
                        value = "Lun–Vie, 9am – 6pm"
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun FaqSectionBlock(section: FaqSection) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BlueVibrant.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(section.icon, null, tint = BlueVibrant, modifier = Modifier.size(15.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(
                section.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            section.items.forEachIndexed { idx, item ->
                val isExpanded = expandedIndex == idx
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedIndex = if (isExpanded) null else idx }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.question,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Text(
                            item.answer,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            lineHeight = 20.sp
                        )
                    }
                    if (idx < section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(BlueVibrant.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = BlueVibrant, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}
