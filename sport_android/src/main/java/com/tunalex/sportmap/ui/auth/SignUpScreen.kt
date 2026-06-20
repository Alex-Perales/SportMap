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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.resetSuccess()
            onSignUpSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueVibrant, IndigoDeep)))
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(top = 24.dp, start = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            BrandLogo(size = 90)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Crea tu cuenta y empieza a explorar",
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(32.dp))

            Text(
                text = "Registro",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            AuthTextField(
                value = state.name,
                onValueChange = vm::onName,
                label = "Nombre completo",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(Modifier.height(14.dp))
            AuthTextField(
                value = state.email,
                onValueChange = vm::onEmail,
                label = "Correo electrónico",
                leadingIcon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(14.dp))
            AuthTextField(
                value = state.password,
                onValueChange = vm::onPassword,
                label = "Contraseña (mín. 6)",
                leadingIcon = Icons.Filled.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(14.dp))
            AuthTextField(
                value = state.passwordConfirm,
                onValueChange = vm::onPasswordConfirm,
                label = "Confirmar contraseña",
                leadingIcon = Icons.Filled.Lock,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.error != null) {
                Spacer(Modifier.height(10.dp))
                Text(text = state.error!!, color = Color(0xFFFFD0D0), fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.signUp() },
                enabled = !state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = BlueVibrant
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                if (state.loading) {
                    CircularProgressIndicator(color = BlueVibrant, strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp))
                } else {
                    Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBack) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color.White)
            }
        }
    }
}
