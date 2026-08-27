package com.tunalex.sportmap.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.ui.components.BrandLogo
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

private val CARD_HEIGHT = 560.dp
private val PANEL_REST = 176.dp
private val PANEL_RADIUS = 76.dp

@Composable
fun AuthScreen(
    onEnterApp: () -> Unit,
    vm: AuthViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var mode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
    var showPolicy by remember { mutableStateOf(false) }
    var switching by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val panelAnim = remember { Animatable(0f) }

    BackHandler(enabled = mode == AuthMode.REGISTER && !switching) {
        mode = AuthMode.LOGIN
        vm.onSwitchMode()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            val wasRegister = state.justRegistered
            vm.resetSuccess()
            if (wasRegister) {
                mode = AuthMode.LOGIN
                snackbar.showSnackbar("Cuenta creada. Inicia sesión para reservar.")
            } else {
                onEnterApp()
            }
        }
    }

    if (showPolicy) PrivacyPolicyDialog(onDismiss = { showPolicy = false })

    fun toggle(target: AuthMode) {
        if (switching) return
        switching = true
        scope.launch {
            panelAnim.animateTo(1f, tween(300))   // el panel cubre la tarjeta
            mode = target
            vm.onSwitchMode()
            panelAnim.animateTo(0f, tween(340))   // y vuelve a su tamaño
            switching = false
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthBackgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BrandLogo(size = 66, onPrimaryColor = TealDark, nameColor = TealDark)
                Spacer(Modifier.height(12.dp))

                AuthCard(
                    mode = mode,
                    panelProgress = panelAnim.value,
                    state = state,
                    vm = vm,
                    onToggleMode = ::toggle,
                    onShowPolicy = { showPolicy = true }
                )

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun AuthCard(
    mode: AuthMode,
    panelProgress: Float,
    state: AuthUiState,
    vm: AuthViewModel,
    onToggleMode: (AuthMode) -> Unit,
    onShowPolicy: () -> Unit
) {
    val isLogin = mode == AuthMode.LOGIN
    val panelH = PANEL_REST + (CARD_HEIGHT - PANEL_REST) * panelProgress

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Formulario ─────────────────────────────────────────────
            // El padding reservado para el panel es SIEMPRE >= su alto en
            // reposo, así el contenido nunca queda debajo del panel.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (isLogin) PANEL_REST + 14.dp else 22.dp,
                        bottom = if (isLogin) 22.dp else PANEL_REST + 14.dp,
                        start = 22.dp,
                        end = 22.dp
                    )
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (isLogin) {
                        LoginContent(state, vm)
                    } else {
                        RegisterContent(state, vm, onShowPolicy)
                    }
                }
            }

            // ── Panel teal deslizante ─────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(panelH)
                    .align(if (isLogin) Alignment.TopCenter else Alignment.BottomCenter)
                    .clip(
                        if (isLogin)
                            RoundedCornerShape(bottomStart = PANEL_RADIUS, bottomEnd = PANEL_RADIUS)
                        else
                            RoundedCornerShape(topStart = PANEL_RADIUS, topEnd = PANEL_RADIUS)
                    )
                    .background(TealPanelBrush),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 28.dp)
                ) {
                    Text(
                        if (isLogin) "¡Hola, deportista!" else "¡Bienvenido de vuelta!",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 23.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (isLogin)
                            "¿Aún no tienes cuenta? Únete y reserva tus canchas."
                        else
                            "¿Ya tienes cuenta? Inicia sesión y reserva.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlineChipButton(
                        text = if (isLogin) "Registrarse" else "Iniciar sesión",
                        onClick = { onToggleMode(if (isLogin) AuthMode.REGISTER else AuthMode.LOGIN) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginContent(state: AuthUiState, vm: AuthViewModel) {
    Text("Iniciar sesión", fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, color = InkDark)
    Text("Reserva canchas y espacios deportivos cerca de ti", fontSize = 12.sp, color = InkMuted)
    Spacer(Modifier.height(20.dp))

    AuthField(
        value = state.email,
        onValueChange = vm::onEmail,
        placeholder = "Correo electrónico",
        trailingIcon = Icons.Filled.PersonOutline,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(14.dp))
    AuthPasswordField(
        value = state.password,
        onValueChange = vm::onPassword,
        placeholder = "Contraseña"
    )

    if (state.error != null) {
        Spacer(Modifier.height(10.dp))
        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }

    Spacer(Modifier.height(18.dp))
    GradientButton(
        text = "Ingresar",
        onClick = { vm.login() },
        loading = state.loading,
        enabled = !state.loading
    )
}

@Composable
private fun RegisterContent(
    state: AuthUiState,
    vm: AuthViewModel,
    onShowPolicy: () -> Unit
) {
    Text("Crear cuenta", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = InkDark)
    Text("Regístrate para reservar y entrenar", fontSize = 12.sp, color = InkMuted)
    Spacer(Modifier.height(18.dp))

    AuthField(
        value = state.name,
        onValueChange = vm::onName,
        placeholder = "Nombre completo",
        trailingIcon = Icons.Filled.PersonOutline
    )
    Spacer(Modifier.height(12.dp))
    AuthField(
        value = state.email,
        onValueChange = vm::onEmail,
        placeholder = "Correo electrónico",
        trailingIcon = Icons.Filled.AlternateEmail,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(12.dp))
    AuthPasswordField(
        value = state.password,
        onValueChange = vm::onPassword,
        placeholder = "Contraseña (mín. 6)"
    )

    Spacer(Modifier.height(16.dp))

    // ── Aceptación de la política (destacada) ──────────────────────────
    val policyBorder = if (state.acceptedPolicy) TealDark else Color(0xFFC24A4A)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (state.acceptedPolicy) TealDark.copy(alpha = 0.10f)
                else Color(0xFFC24A4A).copy(alpha = 0.10f)
            )
            .border(1.5.dp, policyBorder, RoundedCornerShape(14.dp))
            .clickable { vm.onAcceptPolicy(!state.acceptedPolicy) }
            .padding(end = 10.dp)
    ) {
        Checkbox(
            checked = state.acceptedPolicy,
            onCheckedChange = vm::onAcceptPolicy,
            colors = CheckboxDefaults.colors(
                checkedColor = TealDark,
                uncheckedColor = policyBorder
            )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text("Acepto la Política de Privacidad", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkDark)
            Text(
                "Obligatorio para crear la cuenta · toca para leerla",
                fontSize = 10.sp,
                color = TealDark,
                modifier = Modifier.clickable { onShowPolicy() }
            )
        }
    }

    if (state.error != null) {
        Spacer(Modifier.height(8.dp))
        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }

    Spacer(Modifier.height(16.dp))
    GradientButton(
        text = "Crear cuenta",
        onClick = { vm.signUp() },
        loading = state.loading,
        enabled = !state.loading && state.acceptedPolicy
    )
}
