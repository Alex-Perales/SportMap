package com.tunalex.sportmap.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
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
import com.tunalex.sportmap.viewmodel.SportMapViewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

private const val ANIM_MS = 500
private val TEAL_COLLAPSED = 138.dp
// Altura a la que crece el teal durante la transición para "cubrir" la tarjeta.
private val TEAL_COVER = 640.dp
// Alto fijo de la tarjeta: login y registro miden EXACTAMENTE lo mismo, así la
// transición no cambia de tamaño. El contenido de login se reparte para llenar.
private val CARD_H = 580.dp

@Composable
fun AuthScreen(
    onEnterApp: () -> Unit,
    vm: AuthViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var mode by rememberSaveable { mutableStateOf(AuthMode.LOGIN) }
    var showPolicy by remember { mutableStateOf(false) }
    var transitioning by remember { mutableStateOf(false) }
    var covering by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Altura animada del bloque teal: pequeña en reposo, grande (cubre la
    // tarjeta) durante la transición.
    val tealHeight by animateDpAsState(
        targetValue = if (covering) TEAL_COVER else TEAL_COLLAPSED,
        animationSpec = tween(durationMillis = ANIM_MS, easing = FastOutSlowInEasing),
        label = "teal-height"
    )

    fun toggle() {
        if (transitioning) return
        transitioning = true
        scope.launch {
            covering = true                       // 1) el teal se expande y cubre
            delay((ANIM_MS + 40).toLong())
            mode = if (mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN
            vm.onSwitchMode()                     // 2) se cambia el formulario detrás
            delay(60)
            covering = false                      // 3) el teal se contrae al otro borde
            delay((ANIM_MS + 40).toLong())
            transitioning = false
        }
    }

    BackHandler(enabled = mode == AuthMode.REGISTER && !transitioning) { toggle() }

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

    val isLogin = mode == AuthMode.LOGIN

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbar) }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthBackgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("SportMap", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                Spacer(Modifier.height(14.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .height(CARD_H),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        // ── Formularios (se intercambian con deslizamiento) ──
                        // El área tiene el MISMO alto en login y registro; el
                        // contenido se reparte (SpaceEvenly) para no dejar hueco.
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = if (isLogin) TEAL_COLLAPSED + 8.dp else 20.dp,
                                    bottom = if (isLogin) 20.dp else TEAL_COLLAPSED + 8.dp,
                                    start = 22.dp,
                                    end = 22.dp
                                )
                        ) {
                            AnimatedVisibility(
                                visible = isLogin,
                                enter = slideInVertically(tween(ANIM_MS)) { -it } + fadeIn(tween(ANIM_MS)),
                                exit = slideOutVertically(tween(ANIM_MS)) { -it } + fadeOut(tween(ANIM_MS))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) { LoginContent(state, vm) }
                            }
                            AnimatedVisibility(
                                visible = !isLogin,
                                enter = slideInVertically(tween(ANIM_MS)) { it } + fadeIn(tween(ANIM_MS)),
                                exit = slideOutVertically(tween(ANIM_MS)) { it } + fadeOut(tween(ANIM_MS))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    RegisterContent(
                                        state = state,
                                        vm = vm,
                                        onShowPolicy = { showPolicy = true }
                                    )
                                }
                            }
                        }

                        // ── Bloque teal con altura animada ──────────────────
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tealHeight)
                                .align(if (isLogin) Alignment.TopCenter else Alignment.BottomCenter)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (isLogin) 0.dp else 44.dp,
                                        topEnd = if (isLogin) 0.dp else 44.dp,
                                        bottomStart = if (isLogin) 44.dp else 0.dp,
                                        bottomEnd = if (isLogin) 44.dp else 0.dp
                                    )
                                )
                                .background(TealPanelBrush),
                            contentAlignment = if (isLogin) Alignment.TopCenter else Alignment.BottomCenter
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
                            ) {
                                Text(
                                    if (isLogin) "¡Hola, deportista!" else "¡Bienvenido de vuelta!",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(14.dp))
                                OutlineChipButton(
                                    text = if (isLogin) "Registrarse" else "Iniciar sesión",
                                    onClick = { toggle() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginContent(state: AuthUiState, vm: AuthViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Iniciar sesión", fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = InkDark)
        Text(
            "Reserva canchas y espacios deportivos cerca de ti",
            fontSize = 12.sp,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel("Correo electrónico")
        AuthField(
            value = state.email,
            onValueChange = vm::onEmail,
            placeholder = "tu@correo.com",
            trailingIcon = Icons.Filled.PersonOutline,
            keyboardType = KeyboardType.Email
        )
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        FieldLabel("Contraseña")
        AuthPasswordField(
            value = state.password,
            onValueChange = vm::onPassword,
            placeholder = "Tu contraseña"
        )
    }

    if (state.error != null) {
        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }

    GradientButton(
        text = "Ingresar",
        onClick = { vm.login() },
        loading = state.loading,
        enabled = !state.loading
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = InkDark,
        modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
    )
}

@Composable
private fun RegisterContent(
    state: AuthUiState,
    vm: AuthViewModel,
    onShowPolicy: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Crear cuenta", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = InkDark)
        Text(
            "Regístrate para reservar y entrenar",
            fontSize = 11.sp,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
    }

    AuthField(
        value = state.name,
        onValueChange = vm::onName,
        placeholder = "Nombre completo",
        trailingIcon = Icons.Filled.PersonOutline
    )
    AuthField(
        value = state.email,
        onValueChange = vm::onEmail,
        placeholder = "Correo electrónico",
        trailingIcon = Icons.Filled.AlternateEmail,
        keyboardType = KeyboardType.Email
    )
    AuthPasswordField(
        value = state.password,
        onValueChange = vm::onPassword,
        placeholder = "Contraseña (mín. 6)"
    )

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
        Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }

    GradientButton(
        text = "Crear cuenta",
        onClick = { vm.signUp() },
        loading = state.loading,
        enabled = !state.loading && state.acceptedPolicy
    )
}
