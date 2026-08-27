package com.tunalex.sportmap.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

// ── Paleta (mismos tonos que el diseño de referencia) ───────────────────────
val TealDark = Color(0xFF0E4C59)
val TealMid = Color(0xFF16788C)
val CyanBright = Color(0xFF12C6D6)
val AuthBgTop = Color(0xFFBFE6E9)
val AuthBgBottom = Color(0xFF8FD1D8)
val CardSurface = Color(0xFFECEDEE)
val FieldSurface = Color(0xFFE1E3E4)
val InkDark = Color(0xFF1E2A2E)
val InkMuted = Color(0xFF5B6B70)

val AuthBackgroundBrush = Brush.verticalGradient(listOf(AuthBgTop, AuthBgBottom))
val TealPanelBrush = Brush.verticalGradient(listOf(TealDark, TealMid))
val PrimaryButtonBrush = Brush.horizontalGradient(listOf(TealDark, CyanBright))

// ── Campo de texto gris redondeado con icono a la derecha ───────────────────
@Composable
fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = InkMuted) },
        trailingIcon = trailing ?: { Icon(trailingIcon, contentDescription = null, tint = InkDark) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = FieldSurface,
            unfocusedContainerColor = FieldSurface,
            disabledContainerColor = FieldSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = TealDark,
            focusedTextColor = InkDark,
            unfocusedTextColor = InkDark
        )
    )
}

/** Contraseña única: el ojito revela el texto SOLO mientras se mantiene presionado. */
@Composable
fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    AuthField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        trailingIcon = Icons.Filled.VisibilityOff,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (revealed) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier,
        trailing = {
            Icon(
                imageVector = if (revealed) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = "Mantén presionado para ver la contraseña",
                tint = InkDark,
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            revealed = true
                            tryAwaitRelease()
                            revealed = false
                        })
                    }
            )
        }
    )
}

/** Botón principal con degradado teal → cian. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (enabled) PrimaryButtonBrush else Brush.horizontalGradient(listOf(InkMuted, InkMuted)))
            .then(
                if (enabled && !loading)
                    Modifier.pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

/** Botón con borde (el de la franja teal: "Registrarse" / "Iniciar sesión"). */
@Composable
fun OutlineChipButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }
            .padding(horizontal = 34.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

// ── Política de privacidad (contenido traído de internet) ───────────────────
private const val POLICY_URL =
    "https://baconipsum.com/api/?type=meat-and-filler&paras=6&format=text"

private const val FALLBACK_POLICY = """SportMap valora tu privacidad.

1. Datos que recopilamos: nombre, correo electrónico y, opcionalmente, tu foto de perfil y distrito. También guardamos tus reservas, favoritos y actividad deportiva para mostrarte estadísticas.

2. Uso de los datos: se usan únicamente para el funcionamiento de la app (autenticación, reservas de canchas y espacios, recomendaciones y soporte). No vendemos tus datos a terceros.

3. Ubicación: la usamos solo mientras la app está abierta para mostrarte lugares cercanos y calcular rutas hacia tu reserva. Puedes desactivarla desde Ajustes.

4. Almacenamiento: tus datos se guardan de forma local en tu dispositivo y en nuestro servidor. Las fotos pueden alojarse en un proveedor de almacenamiento externo.

5. Tus derechos: puedes editar tu perfil o eliminar tu cuenta cuando quieras desde Ajustes. Al eliminar la cuenta se borran tus datos asociados.

6. Contacto: para consultas sobre privacidad escríbenos desde la sección de Ayuda."""

private suspend fun fetchPrivacyPolicy(): String = withContext(Dispatchers.IO) {
    try {
        val conn = (URL(POLICY_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8000
            readTimeout = 8000
        }
        val raw = conn.inputStream.bufferedReader().use { it.readText() }.trim()
        conn.disconnect()
        val text = if (raw.startsWith("[")) {
            val arr = JSONArray(raw)
            (0 until arr.length()).joinToString("\n\n") { arr.getString(it) }
        } else raw
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
                                color = TealDark
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
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cerrar", color = TealDark, fontWeight = FontWeight.SemiBold)
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
