package com.tunalex.sportmap.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tunalex.sportmap.ui.theme.BlueMedium
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GoldPremium
import com.tunalex.sportmap.ui.theme.GreenSafe
import com.tunalex.sportmap.ui.theme.IndigoDeep
import com.tunalex.sportmap.ui.theme.OrangeAlert
import com.tunalex.sportmap.ui.theme.RedDanger
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import java.util.Locale

data class AppNotification(val id: Int, val title: String, val body: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onStartActivity: () -> Unit,
    onOpenPremium: () -> Unit,
    vm: DashboardViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var fabExpanded by remember { mutableStateOf(true) }
    var showNotifications by remember { mutableStateOf(false) }
    var proExpanded by remember { mutableStateOf(false) }
    var notifications by remember {
        mutableStateOf(
            listOf(
                AppNotification(1, "Reserva confirmada", "Tu reserva en Larcomar para el Sáb 22 Jun fue confirmada."),
                AppNotification(2, "Cancha disponible", "Cancha Fútbol San Borja tiene horarios disponibles ahora mismo."),
                AppNotification(3, "¡Nueva medalla!", "Obtuviste la medalla \"Explorador\" por visitar 5 lugares.")
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(5_000L)
        fabExpanded = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onStartActivity,
                    expanded = fabExpanded,
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
                HeroHeader(
                    userName = state.user?.name ?: "deportista",
                    notificationCount = notifications.size,
                    onBellClick = { showNotifications = !showNotifications }
                )
                Spacer(Modifier.height(20.dp))
                StatsRow(totalKm = state.totalKm, placesVisited = state.placesVisited)
                Spacer(Modifier.height(20.dp))
                EresProBanner(
                    isPremium = state.user?.isPremium == true,
                    expanded = proExpanded,
                    onToggle = { proExpanded = !proExpanded }
                )
                AnimatedVisibility(
                    visible = proExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ProChartsBlock()
                }
                Spacer(Modifier.height(20.dp))
                DataSummarySection()
                Spacer(Modifier.height(20.dp))
                RecommendedSection(onOpenPremium)
                Spacer(Modifier.height(96.dp))
            }
        }

        AnimatedVisibility(
            visible = showNotifications,
            modifier = Modifier.zIndex(10f),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            NotificationsPanel(
                notifications = notifications,
                onDismiss = { showNotifications = false },
                onRemove = { id -> notifications = notifications.filter { it.id != id } }
            )
        }
    }
}

// ── Hero Header ───────────────────────────────────────────────────────────────

@Composable
private fun HeroHeader(userName: String, notificationCount: Int, onBellClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1502082553048-f009c37129b9?w=1200",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, IndigoDeep.copy(alpha = 0.85f)))
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, end = 8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) Badge { Text("$notificationCount", fontSize = 10.sp) }
                }
            ) {
                IconButton(onClick = onBellClick) {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("Costa Verde · Miraflores", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            Text("¡Hola, $userName!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("¿Listo para tu próximo entrenamiento?", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────

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
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
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
        Column(modifier = Modifier.padding(16.dp)) {
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

// ── Eres Pro Expandable Banner ────────────────────────────────────────────────

@Composable
private fun EresProBanner(isPremium: Boolean, expanded: Boolean, onToggle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(GoldPremium, Color(0xFFE8A92F))))
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFF6B4400), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        if (isPremium) "Eres Pro" else "Hazte Pro",
                        color = Color(0xFF3B2400),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Estadísticas avanzadas y rutas optimizadas",
                        color = Color(0xFF3B2400).copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF3B2400).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF3B2400)
                )
            }
        }
    }
}

// ── Pro Charts Block ──────────────────────────────────────────────────────────

@Composable
private fun ProChartsBlock() {
    val weeklyData = remember {
        listOf(
            Triple("Lun", 0.42f, 14),
            Triple("Mar", 0.58f, 10),
            Triple("Mié", 0.71f, 7),
            Triple("Jue", 0.63f, 9),
            Triple("Vie", 0.89f, 3),
            Triple("Sáb", 0.96f, 1),
            Triple("Dom", 0.74f, 6)
        )
    }
    val hourlyData = remember {
        listOf(
            "6h" to 0.18f, "8h" to 0.42f, "10h" to 0.65f,
            "12h" to 0.78f, "14h" to 0.70f, "16h" to 0.85f,
            "18h" to 0.94f, "20h" to 0.72f, "22h" to 0.30f
        )
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(12.dp))
        SportActivityDonutChart()
        Spacer(Modifier.height(14.dp))
        WeeklyUsageChart(weeklyData)
        Spacer(Modifier.height(14.dp))
        HourlyActivityChart(hourlyData)
        Spacer(Modifier.height(8.dp))
    }
}

// ── Sport Activity Donut Chart ────────────────────────────────────────────────

@Composable
private fun SportActivityDonutChart() {
    val sportData = remember {
        listOf(
            Triple("Fútbol", 18f, BlueVibrant),
            Triple("Running", 12f, GoldPremium),
            Triple("Tenis", 10f, GreenSafe),
            Triple("Ciclismo", 5f, OrangeAlert)
        )
    }
    val total = sportData.sumOf { it.second.toDouble() }.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueVibrant.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.BarChart, null, tint = BlueVibrant, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("Actividad por deporte", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 26.dp.toPx()
                        val inset = strokeWidth / 2f
                        var startAngle = -90f
                        sportData.forEach { (_, value, color) ->
                            val sweep = (value / total) * 360f
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweep - 4f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth)
                            )
                            startAngle += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${total.toInt()}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("sesiones", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                sportData.forEach { (sport, sessions, color) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(color)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(sport, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                        Text(
                            "${sessions.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// ── Weekly Usage Chart ────────────────────────────────────────────────────────

@Composable
private fun WeeklyUsageChart(data: List<Triple<String, Float, Int>>) {
    val peakValue = data.maxOf { it.second }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueVibrant.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.BarChart, null, tint = BlueVibrant, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Afluencia Semanal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Uso de canchas esta semana", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val w = size.width
                val h = size.height
                val topPad = 22.dp.toPx()
                val chartH = h - topPad
                val gap = w / data.size
                val barW = gap * 0.55f

                listOf(0.25f, 0.5f, 0.75f).forEach { pct ->
                    val lineY = topPad + chartH * (1f - pct)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.11f),
                        start = Offset(0f, lineY),
                        end = Offset(w, lineY),
                        strokeWidth = 0.8.dp.toPx()
                    )
                }

                data.forEachIndexed { index, (_, value, _) ->
                    val isPeak = value == peakValue
                    val barH = value * chartH
                    val x = index * gap + (gap - barW) / 2f
                    val yTop = topPad + chartH - barH
                    drawRoundRect(
                        brush = if (isPeak)
                            Brush.verticalGradient(
                                listOf(GoldPremium, Color(0xFFE8A92F).copy(alpha = 0.55f)),
                                startY = yTop, endY = topPad + chartH
                            )
                        else
                            Brush.verticalGradient(
                                listOf(BlueVibrant, IndigoDeep.copy(alpha = 0.7f)),
                                startY = yTop, endY = topPad + chartH
                            ),
                        topLeft = Offset(x, yTop),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                }

                val nativeCanvas = drawContext.canvas.nativeCanvas
                data.forEachIndexed { index, (_, value, _) ->
                    val isPeak = value == peakValue
                    val barH = value * chartH
                    val x = index * gap + (gap - barW) / 2f
                    val yTop = topPad + chartH - barH
                    val textPaint = android.graphics.Paint().apply {
                        color = if (isPeak)
                            android.graphics.Color.argb(255, 176, 112, 0)
                        else
                            android.graphics.Color.argb(255, 96, 128, 160)
                        textSize = 21f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    nativeCanvas.drawText("${(value * 100).toInt()}%", x + barW / 2f, yTop - 3.dp.toPx(), textPaint)
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                data.forEach { (day, value, available) ->
                    val isPeak = value == peakValue
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            day, fontSize = 10.sp,
                            fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                            color = if (isPeak) GoldPremium else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text("$available", fontSize = 9.sp, color = GreenSafe, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ChartLegendDot(BlueVibrant, "% Uso")
                ChartLegendDot(GreenSafe, "Canchas libres", round = true)
                ChartLegendDot(GoldPremium, "Día pico")
            }
        }
    }
}

// ── Hourly Activity Chart ─────────────────────────────────────────────────────

@Composable
private fun HourlyActivityChart(data: List<Pair<String, Float>>) {
    val maxOccupancy = data.maxOf { it.second }
    val minOccupancy = data.minOf { it.second }
    val peakIndex = data.indexOfFirst { it.second == maxOccupancy }
    val bestIndex = data.indexOfFirst { it.second == minOccupancy }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(OrangeAlert.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AccessTime, null, tint = OrangeAlert, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Horas Más Activas", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Afluencia por hora del día", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val w = size.width
                val h = size.height
                val topPad = 8.dp.toPx()
                val chartH = h - topPad
                val stepX = w / (data.size - 1).toFloat()
                val maxVal = data.maxOf { it.second }

                fun yFor(v: Float) = topPad + chartH * (1f - v / maxVal)
                fun xFor(i: Int) = i * stepX

                val linePath = Path()
                val fillPath = Path()

                data.forEachIndexed { i, (_, value) ->
                    val x = xFor(i); val y = yFor(value)
                    if (i == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, topPad + chartH)
                        fillPath.lineTo(x, y)
                    } else {
                        val px = xFor(i - 1); val py = yFor(data[i - 1].second)
                        val cp = stepX / 2.2f
                        linePath.cubicTo(px + cp, py, x - cp, y, x, y)
                        fillPath.cubicTo(px + cp, py, x - cp, y, x, y)
                    }
                }
                fillPath.lineTo(xFor(data.size - 1), topPad + chartH)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        listOf(BlueVibrant.copy(alpha = 0.28f), Color.Transparent),
                        startY = topPad, endY = topPad + chartH
                    )
                )
                drawPath(
                    path = linePath,
                    color = BlueVibrant,
                    style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                data.forEachIndexed { i, (_, value) ->
                    val x = xFor(i); val y = yFor(value)
                    val isPeak = i == peakIndex; val isBest = i == bestIndex
                    val dotColor = when { isPeak -> RedDanger; isBest -> GreenSafe; else -> BlueVibrant }
                    val radius = if (isPeak || isBest) 6.dp.toPx() else 3.5.dp.toPx()
                    drawCircle(dotColor, radius, Offset(x, y))
                    drawCircle(Color.White, radius * 0.48f, Offset(x, y))
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                data.forEach { (hour, _) ->
                    Text(hour, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = RedDanger.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(RedDanger))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("Hora pico", fontSize = 10.sp, color = RedDanger, fontWeight = FontWeight.SemiBold)
                            Text(data[peakIndex].first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RedDanger)
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenSafe.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(GreenSafe))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("Mejor hora", fontSize = 10.sp, color = GreenSafe, fontWeight = FontWeight.SemiBold)
                            Text(data[bestIndex].first, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GreenSafe)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartLegendDot(color: Color, label: String, round: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(if (round) RoundedCornerShape(5.dp) else RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Data Summary Section ──────────────────────────────────────────────────────

@Composable
private fun DataSummarySection() {
    var showDetails by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Resumen de datos", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "Ver detalles →",
                    fontSize = 12.sp,
                    color = BlueVibrant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { showDetails = true }
                )
            }
            Spacer(Modifier.height(14.dp))
            DataSummaryRow(text = "El recuento de visitas diario medio de los últimos 7 días es 4.8 visitas")
            Spacer(Modifier.height(10.dp))
            DataSummaryRow(text = "Promedio de km por sesión en últimos 7 días: 2.3 km")
            Spacer(Modifier.height(10.dp))
            DataSummaryRow(text = "Horas de actividad diaria promedio en últimos 7 días: 1.5h")
        }
    }

    if (showDetails) {
        DataDetailsDialog(onDismiss = { showDetails = false })
    }
}

@Composable
private fun DataDetailsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Resumen de datos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar", modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    "Últimos 7 días",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                )

                DetailStatRow(label = "Visitas diarias (promedio)", value = "4.8", unit = "visitas/día", color = GreenSafe)
                DetailStatRow(label = "Km por sesión (promedio)", value = "2.3", unit = "km", color = OrangeAlert)
                DetailStatRow(label = "Actividad diaria (promedio)", value = "1.5", unit = "h/día", color = BlueVibrant)

                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(Modifier.height(14.dp))

                DetailStatRow(label = "Racha actual", value = "5", unit = "días seguidos", color = GoldPremium)
                DetailStatRow(label = "Sesiones esta semana", value = "12", unit = "sesiones", color = IndigoDeep)
                DetailStatRow(label = "Deporte favorito", value = "Fútbol", unit = "", color = BlueVibrant)
                DetailStatRow(label = "Meta semanal cumplida", value = "80", unit = "%", color = GreenSafe)
            }
        }
    }
}

@Composable
private fun DetailStatRow(label: String, value: String, unit: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            if (unit.isNotEmpty()) {
                Spacer(Modifier.width(3.dp))
                Text(unit, fontSize = 11.sp, color = color.copy(alpha = 0.75f))
            }
        }
    }
}

@Composable
private fun DataSummaryRow(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp
    )
}

// ── Recommended Section ───────────────────────────────────────────────────────

@Composable
private fun RecommendedSection(onOpenPremium: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Recomendados para ti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        AdCardChallenge()
        Spacer(Modifier.height(12.dp))
        AdCardPremium(onClick = onOpenPremium)
        Spacer(Modifier.height(12.dp))
        AdCardStayFit()
    }
}

@Composable
private fun AdCardChallenge() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=800",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4A2800).copy(alpha = 0.92f), Color(0xFF4A2800).copy(alpha = 0.55f), Color.Transparent)
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
                    Text("30", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 40.sp)
                    Column {
                        Text("Vamos, Papá!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Únete a la comunidad", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                    }
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.25f))
                    ) {
                        Text(
                            "SPORT",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("S/. 129.9", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdCardPremium(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=800",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(GoldPremium.copy(alpha = 0.9f), Color(0xFF3B2400).copy(alpha = 0.75f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B2400).copy(alpha = 0.3f))
                ) {
                    Text(
                        "Beneficios Premium",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Activar Entrenamiento Inteligente ♥", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text("Plan personalizado para mejorar tu rendimiento", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AdCardStayFit() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=800",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(IndigoDeep.copy(alpha = 0.88f), Color(0xFF7E57C2).copy(alpha = 0.65f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = BlueVibrant.copy(alpha = 0.6f))
                ) {
                    Text(
                        "Nuevo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text("Stay Fit Plan", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(3.dp))
                    Text("Reach your weight goal faster", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
        }
    }
}

// ── Notifications Panel ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsPanel(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onRemove: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onDismiss() }
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, null, tint = BlueVibrant)
                        Spacer(Modifier.width(8.dp))
                        Text("Notificaciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }
                Spacer(Modifier.height(4.dp))
                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin notificaciones", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                } else {
                    Text(
                        "Desliza a la derecha para eliminar",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    notifications.forEach { notification ->
                        NotificationItem(notification = notification, onRemove = { onRemove(notification.id) })
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationItem(notification: AppNotification, onRemove: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { it == SwipeToDismissBoxValue.StartToEnd },
        positionalThreshold = { it * 0.4f }
    )
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) onRemove()
    }
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(RedDanger.copy(alpha = 0.85f)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlueVibrant.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Notifications, null, tint = BlueVibrant, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(notification.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        notification.body,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
