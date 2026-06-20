package com.tunalex.sportmap.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.ui.theme.BlueMedium
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GoldPremium
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedalsScreen(
    onBack: () -> Unit,
    vm: SettingsViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val earned = state.medals.count { it.earned }
    val total = state.medals.size
    val progress = if (total > 0) earned.toFloat() / total else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200),
        label = "medals_progress"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medallas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(listOf(BlueVibrant, BlueMedium))
                    )
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Tu colección",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "$earned de $total medallas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = GoldPremium,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${(progress * 100).toInt()}% completado",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Spacer(Modifier.size(16.dp))
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = GoldPremium,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.medals) { medal ->
                    MedalCard(medal)
                }
            }
        }
    }
}

@Composable
private fun MedalCard(medal: MedalEntity) {
    val tierColor = when (medal.tier) {
        "gold"   -> Color(0xFFFFC857)
        "silver" -> Color(0xFFB0BEC5)
        else     -> Color(0xFFCD7F32)
    }
    val iconGradient = when {
        !medal.earned -> Brush.linearGradient(listOf(Color(0xFFCFD4DC), Color(0xFFADB5BD)))
        medal.tier == "gold" ->
            Brush.linearGradient(listOf(Color(0xFFFFD770), Color(0xFFE67E00)))
        medal.tier == "silver" ->
            Brush.linearGradient(listOf(Color(0xFFD4DCE8), Color(0xFF8899AA)))
        else ->
            Brush.linearGradient(listOf(Color(0xFFE0A060), Color(0xFF8B4513)))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (medal.earned) 5.dp else 1.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (medal.earned)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .aspectRatio(0.82f)
            .border(
                width = if (medal.earned) 1.5.dp else 0.5.dp,
                color = if (medal.earned) tierColor.copy(alpha = 0.55f)
                        else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .shadow(
                        elevation = if (medal.earned) 6.dp else 0.dp,
                        shape = CircleShape
                    )
                    .background(iconGradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconForMedal(medal.iconKey),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Name
            Text(
                text = medal.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp,
                color = if (medal.earned)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )

            // Status badge
            if (medal.earned) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tierColor.copy(alpha = 0.14f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "✓ Ganada",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tierColor
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f),
                        modifier = Modifier.size(9.dp)
                    )
                    Text(
                        text = "Bloqueada",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
                    )
                }
            }
        }
    }
}

private fun iconForMedal(key: String): ImageVector = when (key) {
    "morning"  -> Icons.Filled.WbSunny
    "field"    -> Icons.Filled.SportsKabaddi
    "explorer" -> Icons.Filled.Flag
    "cycling"  -> Icons.Filled.DirectionsBike
    "swimming" -> Icons.Filled.Pool
    "marathon" -> Icons.Filled.DirectionsRun
    "wellness" -> Icons.Filled.SelfImprovement
    "captain"  -> Icons.Filled.Star
    else       -> Icons.Filled.EmojiEvents
}
