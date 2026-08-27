package com.tunalex.sportmap.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit,
    vm: AuthViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showPolicy by remember { mutableStateOf(false) }

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.resetSuccess()
            onSignUpSuccess()
        }
    }

    if (showPolicy) {
        PrivacyPolicyDialog(onDismiss = { showPolicy = false })
    }

    AuthScaffold(
        subtitle = "Crea tu cuenta y empieza a explorar",
        activeTab = AuthTab.SIGNUP,
        onSelectTab = { tab -> if (tab == AuthTab.LOGIN) onBack() },
        onBack = onBack
    ) {
        Text(
            "Crea tu cuenta",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))

        AuthCardField(
            value = state.name,
            onValueChange = vm::onName,
            label = "Nombre completo",
            leadingIcon = Icons.Filled.Person
        )
        Spacer(Modifier.height(12.dp))
        AuthCardField(
            value = state.email,
            onValueChange = vm::onEmail,
            label = "Correo electrónico",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))
        HoldToRevealPasswordField(
            value = state.password,
            onValueChange = vm::onPassword,
            label = "Contraseña (mín. 6)",
            leadingIcon = Icons.Filled.Lock
        )

        Spacer(Modifier.height(16.dp))
        Text(
            "Pregunta de seguridad (para recuperar tu cuenta)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                state.securityQuestion,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {
                val idx = SECURITY_QUESTIONS.indexOf(state.securityQuestion)
                vm.onSecurityQuestion(SECURITY_QUESTIONS[(idx + 1) % SECURITY_QUESTIONS.size])
            }) {
                Text("Cambiar", color = BlueVibrant, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(6.dp))
        AuthCardField(
            value = state.securityAnswer,
            onValueChange = vm::onSecurityAnswer,
            label = "Tu respuesta",
            leadingIcon = Icons.Filled.Help
        )

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.acceptedPolicy,
                onCheckedChange = vm::onAcceptPolicy,
                colors = CheckboxDefaults.colors(checkedColor = BlueVibrant)
            )
            Text(
                "Acepto la ",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Política de Privacidad",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BlueVibrant,
                modifier = Modifier.clickable { showPolicy = true }
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { vm.signUp() },
            enabled = !state.loading && state.acceptedPolicy,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text("Crear cuenta", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = BlueVibrant, fontSize = 13.sp)
        }
    }
}
