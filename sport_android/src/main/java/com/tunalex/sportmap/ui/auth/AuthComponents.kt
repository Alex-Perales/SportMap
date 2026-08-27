package com.tunalex.sportmap.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tunalex.sportmap.ui.components.BrandLogo
import com.tunalex.sportmap.ui.theme.BlueMedium
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.ui.theme.IndigoDeep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

enum class AuthTab { LOGIN, SIGNUP }

private val AccentPurple = Color(0xFF7C3AED)

/**
 * Fondo degradado (3 colores) + tarjeta central con cabecera degradada y un
 * selector "Iniciar sesión / Registrarse". Compartido por LoginScreen y
 * SignUpScreen para que ambas se vean iguales y sea claro cómo alternar.
 */
@Composable
fun AuthScaffold(
    subtitle: String,
    activeTab: AuthTab,
    onSelectTab: (AuthTab) -> Unit,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BlueMedium, BlueVibrant, IndigoDeep)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            BrandLogo(size = 84)
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                // Cabecera degradada (3 colores) con el selector de pestañas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(BlueVibrant, IndigoDeep, AccentPurple))
                        )
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(4.dp)
                    ) {
                        AuthTabButton(
                            text = "Iniciar sesión",
                            selected = activeTab == AuthTab.LOGIN,
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectTab(AuthTab.LOGIN) }
                        )
                        AuthTabButton(
                            text = "Registrarse",
                            selected = activeTab == AuthTab.SIGNUP,
                            modifier = Modifier.weight(1f),
                            onClick = { onSelectTab(AuthTab.SIGNUP) }
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                    content = content
                )
            }

            Spacer(Modifier.height(28.dp))
        }

        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }
        }
    }
}

@Composable
private fun AuthTabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BlueVibrant else Color.White,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}

/** Campo de texto sobre la tarjeta clara (texto oscuro sobre fondo claro). */
@Composable
fun AuthCardField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = BlueVibrant) },
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BlueVibrant,
            cursorColor = BlueVibrant,
            focusedLabelColor = BlueVibrant
        )
    )
}

/**
 * Campo de contraseña única. El ojito revela el texto SOLO mientras se
 * mantiene presionado; al soltar, se vuelve a ocultar.
 */
@Composable
fun HoldToRevealPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    AuthCardField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        leadingIcon = leadingIcon,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (revealed) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier,
        trailingIcon = {
            Icon(
                imageVector = if (revealed) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = "Mantén presionado para ver la contraseña",
                tint = BlueVibrant.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                revealed = true
                                tryAwaitRelease()
                                revealed = false
                            }
                        )
                    }
            )
        }
    )
}

// ── Política de privacidad ───────────────────────────────────────────────────

private const val POLICY_URL =
    "https://baconipsum.com/api/?type=meat-and-filler&paras=6&format=text"

private const val FALLBACK_POLICY = """SportMap valora tu privacidad.

1. Datos que recopilamos: nombre, correo electrónico y, opcionalmente, tu foto de perfil y distrito. También guardamos tus reservas, favoritos y actividad deportiva para mostrarte estadísticas.

2. Uso de los datos: se usan únicamente para el funcionamiento de la app (autenticación, reservas, recomendaciones y soporte). No vendemos tus datos a terceros.

3. Ubicación: la usamos solo mientras la app está abierta para mostrarte lugares cercanos y calcular rutas. Puedes desactivarla desde Ajustes.

4. Almacenamiento: tus datos se guardan de forma local en tu dispositivo y en nuestro servidor. Las fotos pueden alojarse en un proveedor de almacenamiento (Supabase).

5. Tus derechos: puedes editar tu perfil o eliminar tu cuenta en cualquier momento desde Ajustes. Al eliminar la cuenta se borran tus datos asociados.

6. Contacto: para consultas sobre privacidad escríbenos desde la sección de Ayuda."""

private suspend fun fetchPrivacyPolicy(): String = withContext(Dispatchers.IO) {
    try {
        val conn = (URL(POLICY_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8000
            readTimeout = 8000
        }
        val body = conn.inputStream.bufferedReader().use { it.readText() }.trim()
        conn.disconnect()
        // La API de ejemplo puede devolver texto plano o un arreglo JSON.
        val text = if (body.startsWith("[")) {
            val arr = JSONArray(body)
            (0 until arr.length()).joinToString("\n\n") { arr.getString(it) }
        } else body
        if (text.isBlank()) FALLBACK_POLICY else text
    } catch (_: Exception) {
        FALLBACK_POLICY
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    var body by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { body = fetchPrivacyPolicy() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Política de Privacidad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Contenido cargado desde internet",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .heightIn(max = 340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (body == null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = BlueVibrant
                            )
                            Text(
                                "   Cargando política…",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(body!!, fontSize = 13.sp, lineHeight = 19.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cerrar", color = BlueVibrant, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/** Estilo blanco sobre degradado — lo sigue usando ForgotPasswordScreen. */
@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.85f)) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color.White) },
        trailingIcon = trailingIcon,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.10f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.06f)
        )
    )
}
