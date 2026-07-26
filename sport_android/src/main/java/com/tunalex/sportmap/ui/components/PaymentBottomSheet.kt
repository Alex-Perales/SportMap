package com.tunalex.sportmap.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tunalex.sportmap.R
import com.tunalex.sportmap.ui.theme.BlueVibrant
import kotlinx.coroutines.launch
import java.io.File

/**
 * Único medio de pago de la app: Yape/Plin por QR fijo + comprobante subido
 * a mano. El pago no se confirma al instante: el pedido queda "pendiente"
 * hasta que un administrador lo revisa desde el panel web.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    amount: Double,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSubmitProof: suspend (imageFile: File) -> Result<Unit>,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) pickedUri = uri }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pagar con Yape / Plin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Escanea el QR, paga y sube la captura de tu comprobante. " +
                    "Confirmaremos tu pago en cuanto lo revisemos.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Las reservas pagadas se cancelan solicitando reembolso manual al " +
                    "administrador; no existe reembolso automático.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Monto a pagar", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    "S/. ${"%.2f".format(amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BlueVibrant
                )
            }
            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.qr_yape),
                contentDescription = "Código QR de Yape para pagar",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            Text("Comprobante de pago", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(10.dp))

            if (pickedUri == null) {
                OutlinedButton(
                    onClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Filled.PhotoCamera, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Subir captura del comprobante")
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = pickedUri,
                        contentDescription = "Comprobante seleccionado",
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF22C55E))
                        Text("Comprobante listo para enviar", fontSize = 12.sp)
                    }
                    IconButton(onClick = {
                        pickedUri = null
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Cambiar comprobante")
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val uri = pickedUri
                    if (uri == null) {
                        errorMessage = "Sube la captura de tu comprobante para continuar."
                        return@Button
                    }
                    errorMessage = null
                    isUploading = true
                    scope.launch {
                        val file = copyUriToCacheFile(context, uri)
                        if (file == null) {
                            errorMessage = "No se pudo leer la imagen seleccionada."
                            isUploading = false
                            return@launch
                        }
                        val result = onSubmitProof(file)
                        isUploading = false
                        result.onSuccess {
                            sheetState.hide()
                            onSuccess()
                        }.onFailure {
                            errorMessage = "No se pudo enviar tu comprobante. Revisa tu conexión e intenta de nuevo."
                        }
                    }
                },
                enabled = !isUploading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Enviando comprobante...", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Enviar comprobante", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

private fun copyUriToCacheFile(context: android.content.Context, uri: Uri): File? = try {
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val file = File(context.cacheDir, "comprobante_${System.currentTimeMillis()}.jpg")
    input.use { stream -> file.outputStream().use { out -> stream.copyTo(out) } }
    file
} catch (_: Exception) {
    null
}
