package com.tunalex.sportmap.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.ui.components.BrandLogo
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.IndigoDeep
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetSuccess: () -> Unit,
    vm: ForgotPasswordViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) onResetSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueVibrant, IndigoDeep)))
    ) {
        // El botón de volver va DESPUÉS del Column (no antes): un Column con
        // fillMaxSize + verticalScroll pintado encima le roba el toque a
        // cualquier elemento debajo de él en la misma región, aunque se vea
        // bien — por eso la flecha no respondía a los toques.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            BrandLogo(size = 90)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Recuperar contraseña",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (state.step == 1)
                    "Ingresa el correo de tu cuenta. Solo funciona en el dispositivo donde te registraste."
                else
                    "Responde tu pregunta de seguridad y elige una nueva contraseña.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            if (state.step == 1) {
                AuthTextField(
                    value = state.email,
                    onValueChange = vm::onEmail,
                    label = "Correo electrónico",
                    leadingIcon = Icons.Filled.Email,
                    keyboardType = KeyboardType.Email
                )

                if (state.error != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(state.error!!, color = Color(0xFFFFD0D0), fontSize = 13.sp)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.lookupQuestion() },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BlueVibrant),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(color = BlueVibrant, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Text(
                    state.question ?: "",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                AuthTextField(
                    value = state.answer,
                    onValueChange = vm::onAnswer,
                    label = "Tu respuesta",
                    leadingIcon = Icons.Filled.Help
                )
                Spacer(Modifier.height(14.dp))
                AuthTextField(
                    value = state.newPassword,
                    onValueChange = vm::onNewPassword,
                    label = "Nueva contraseña (mín. 6)",
                    leadingIcon = Icons.Filled.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(14.dp))
                AuthTextField(
                    value = state.newPasswordConfirm,
                    onValueChange = vm::onNewPasswordConfirm,
                    label = "Confirmar nueva contraseña",
                    leadingIcon = Icons.Filled.Lock,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )

                if (state.error != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(state.error!!, color = Color(0xFFFFD0D0), fontSize = 13.sp)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { vm.resetPassword() },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BlueVibrant),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(color = BlueVibrant, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Cambiar contraseña", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 24.dp, start = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
        }
    }
}
