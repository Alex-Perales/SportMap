package com.tunalex.sportmap.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

private val PANEL_REST = 150.dp
private val PANEL_RADIUS = 70.dp
private val CARD_MIN = 500.dp
private val CARD_MAX = 720.dp

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
        // La tarjeta ocupa casi toda la altura de la pantalla para que no haya
        // que hacer scroll: título + campos + política + botón caben.
        val screenH = LocalConfiguration.current.screenHeightDp.dp
        val cardHeight = (screenH - 150.dp).coerceIn(CARD_MIN, CARD_MAX)

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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "SportMap",
                    color = TealDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                )
                Spacer(Modifier.height(16.dp))

                AuthCard(
                    cardHeight = cardHeight,
                    mode = mode,
                    panelProgress = panelAnim.value,
                    state = state,
                    vm = vm,
                    onToggleMode = ::toggle,
                    onShowPolicy = { showPolicy = true }
                )
            }
        }
    }
}

@Composable
private fun AuthCard(
    cardHeight: Dp,
    mode: AuthMode,
    panelProgress: Float,
    state: AuthUiState,
    vm: AuthViewModel,
    onToggleMode: (AuthMode) -> Unit,
    onShowPolicy: () -> Unit
) {
    val isLogin = mode == AuthMode.LOGIN
    val panelH = PANEL_REST + (cardHeight - PANEL_REST) * panelProgress

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight),
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
                        top = if (isLogin) PANEL_REST + 12.dp else 20.dp,
                        bottom = if (isLogin) 20.dp else PANEL_REST + 12.dp,
                        start = 22.dp,
                        end = 22.dp
                    )
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (isLogin) {
                        LoginContent(state, vm)
                    } else {
                        RegisterContent(
                            state = state,
                            vm = vm,
                            onShowPolicy = onShowPolicy,
                            onBackToLogin = { onToggleMode(AuthMode.LOGIN) }
                        )
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
    onShowPolicy: () -> Unit,
    onBackToLogin: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Crear cuenta",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = InkDark,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onBackToLogin, contentPadding = PaddingValues(horizontal = 4.dp)) {
            Text("Iniciar sesión", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
    Text("Regístrate para reservar y entrenar", fontSize = 11.sp, color = InkMuted)
    Spacer(Modifier.height(12.dp))

    AuthField(
        value = state.name,
        onValueChange = vm::onName,
        placeholder = "Nombre completo",
        trailingIcon = Icons.Filled.PersonOutline
    )
    Spacer(Modifier.height(10.dp))
    AuthField(
        value = state.email,
        onValueChange = vm::onEmail,
        placeholder = "Correo electrónico",
        trailingIcon = Icons.Filled.AlternateEmail,
        keyboardType = KeyboardType.Email
    )
    Spacer(Modifier.height(10.dp))
    AuthPasswordField(
        value = state.password,
        onValueChange = vm::onPassword,
        placeholder = "Contraseña (mín. 6)"
    )

    Spacer(Modifier.height(10.dp))

    // Casilla de política: sobria, con el recuadro de borde oscuro para que
    // se note al seleccionar.
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.acceptedPolicy,
            onCheckedChange = vm::onAcceptPolicy,
            colors = CheckboxDefaults.colors(
                checkedColor = TealDark,
                uncheckedColor = Color(0xFF2B2B2B),
                checkmarkColor = Color.White
            )
        )
        Text("Acepto la ", fontSize = 12.sp, color = InkMuted)
        Text(
            "Política de Privacidad",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TealDark,
            modifier = Modifier.clickable { onShowPolicy() }
        )
    }

    if (state.error != null) {
        Spacer(Modifier.height(6.dp))
        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }

    Spacer(Modifier.height(12.dp))
    GradientButton(
        text = "Crear cuenta",
        onClick = { vm.signUp() },
        loading = state.loading,
        enabled = !state.loading && state.acceptedPolicy
    )
}
