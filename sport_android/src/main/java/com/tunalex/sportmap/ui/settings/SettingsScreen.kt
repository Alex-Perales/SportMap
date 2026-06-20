package com.tunalex.sportmap.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.GoldPremium
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onOpenMedals: () -> Unit,
    onOpenPremium: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenHelp: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenReservationHistory: () -> Unit = {},
    vm: SettingsViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is SettingsViewModel.SettingsEvent.LoggedOut -> onLogout()
                is SettingsViewModel.SettingsEvent.AccountDeleted -> onLogout()
                is SettingsViewModel.SettingsEvent.Toast -> snackbar.showSnackbar(ev.message)
                is SettingsViewModel.SettingsEvent.ProfileSaved -> {}
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ajustes", fontWeight = FontWeight.SemiBold) }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Profile header
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = onEditProfile
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        imageUriOrEmoji = state.user?.profileImageUrl,
                        fallbackLetter = state.user?.name?.firstOrNull()?.uppercase() ?: "?",
                        size = 56
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            state.user?.name ?: "Usuario",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            state.user?.email ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Distrito: ${state.user?.district ?: "—"}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Premium card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                onClick = onOpenPremium
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(GoldPremium, Color(0xFFE8A92F)))
                        )
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFF6B4400))
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (state.user?.isPremium == true) "Eres Pro ⭐" else "Hazte Pro",
                                color = Color(0xFF3B2400),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                "Pronósticos, sin anuncios, descuentos en tienda",
                                color = Color(0xFF3B2400),
                                fontSize = 12.sp
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFF3B2400))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionTitle("Personalización")
            SettingSwitchRow(
                icon = if (state.darkMode == true) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                title = "Modo oscuro",
                subtitle = if (state.darkMode == null) "Sigue al sistema"
                else if (state.darkMode == true) "Activado" else "Desactivado",
                checked = state.darkMode == true,
                onChange = { vm.setDarkMode(it) }
            )

            Spacer(Modifier.height(16.dp))
            SectionTitle("Perfil y juego")
            SettingClickRow(
                icon = Icons.Filled.EmojiEvents,
                title = "Mis medallas",
                subtitle = "Vitrina de logros y trofeos",
                onClick = onOpenMedals
            )
            SettingClickRow(
                icon = Icons.Filled.DateRange,
                title = "Historial de reservas",
                subtitle = "Tus canchas y horarios reservados",
                onClick = onOpenReservationHistory
            )
            SettingClickRow(
                icon = Icons.Filled.Person,
                title = "Editar perfil",
                subtitle = "Nombre, distrito preferido",
                onClick = onEditProfile
            )

            Spacer(Modifier.height(16.dp))
            SectionTitle("Privacidad")
            SettingSwitchRow(
                icon = Icons.Filled.LocationOn,
                title = "GPS",
                subtitle = "Permite que la app use tu ubicación",
                checked = state.gpsEnabled,
                onChange = { vm.setGpsEnabled(it) }
            )

            Spacer(Modifier.height(16.dp))
            SectionTitle("Cuenta y soporte")
            SettingClickRow(
                icon = Icons.Filled.HelpOutline,
                title = "Ayuda y FAQ",
                subtitle = "Preguntas frecuentes y contacto",
                onClick = onOpenHelp
            )
            SettingClickRow(
                icon = Icons.Filled.Info,
                title = "Acerca de",
                subtitle = "Versión 1.0 · SportMap",
                onClick = onOpenAbout
            )
            SettingClickRow(
                icon = Icons.Filled.Logout,
                title = "Cerrar sesión",
                subtitle = "Salir de tu cuenta",
                onClick = { showLogoutDialog = true }
            )
            SettingClickRow(
                icon = Icons.Filled.Delete,
                title = "Eliminar cuenta",
                subtitle = "Borra tus datos permanentemente",
                onClick = { showDeleteDialog = true }
            )

            Spacer(Modifier.height(40.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tendrás que iniciar sesión nuevamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    vm.logout()
                }) { Text("Sí, cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar tu cuenta?") },
            text = { Text("Esta acción no se puede deshacer. Se borrará tu perfil, reservas e historial.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.deleteAccount()
                }) { Text("Sí, eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun SettingClickRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon, tint)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium, color = tint)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun IconBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color = BlueVibrant
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
    }
}
