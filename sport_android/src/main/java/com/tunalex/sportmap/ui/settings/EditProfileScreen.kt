package com.tunalex.sportmap.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tunalex.sportmap.data.remote.resolveBackendImageUrl
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    vm: SettingsViewModel = viewModel(factory = SportMapViewModels.Factory)
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var name by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    var uploadingPhoto by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Al tocar el avatar se abre directo la galería (sin menú intermedio).
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            uploadingPhoto = true
            // Copiamos la imagen a un archivo propio de la app: así se ve al
            // instante y sigue disponible aunque el servidor no responda o se
            // reinicie la app (la URI de la galería es temporal).
            val file = copyUriToProfileFile(context, uri)
            if (file != null) {
                profileImageUri = Uri.fromFile(file).toString()
                vm.uploadProfilePhoto(file).onSuccess { url ->
                    if (url.isNotBlank()) profileImageUri = url
                }.onFailure {
                    snackbar.showSnackbar("Foto guardada en este dispositivo (no se subió al servidor).")
                }
            } else {
                snackbar.showSnackbar("No se pudo leer la imagen elegida.")
            }
            uploadingPhoto = false
        }
    }

    LaunchedEffect(state.user) {
        state.user?.let {
            name = it.name
            district = it.district
            if (profileImageUri == null) profileImageUri = it.profileImageUrl
        }
    }

    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is SettingsViewModel.SettingsEvent.Toast -> snackbar.showSnackbar(ev.message)
                is SettingsViewModel.SettingsEvent.LoggedOut -> onLogout()
                is SettingsViewModel.SettingsEvent.ProfileSaved -> onBack()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                ProfileAvatar(
                    imageUrl = profileImageUri,
                    fallbackLetter = state.user?.name?.firstOrNull()?.uppercase() ?: "?",
                    size = 96,
                    modifier = Modifier.clickable { galleryLauncher.launch("image/*") }
                )
                if (uploadingPhoto) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(96.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(BlueVibrant)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AddAPhoto,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                "Toca para elegir una foto de tu galería",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))
            Text(
                "Información personal",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = state.user?.email ?: "",
                onValueChange = {},
                label = { Text("Correo (no editable)") },
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("Distrito preferido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { vm.updateProfile(name, district, profileImageUri) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueVibrant)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    fallbackLetter: String,
    size: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Normaliza la URL para que la app siempre pueda cargarla:
    //  - "/uploads/perfiles/x.jpg" (ruta relativa del backend) o un host
    //    "localhost" → se reescribe al host del backend de la app.
    //  - "file://…" (foto elegida de la galería, ya copiada localmente) y las
    //    URLs de Supabase / Unsplash → se dejan igual.
    //  - "emoji:…" (avatares antiguos) → se ignora y se muestra la inicial.
    val photoModel: String? = imageUrl
        ?.takeUnless { it.startsWith("emoji:") }
        ?.let { resolveBackendImageUrl(it) }
        ?.ifBlank { null }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(BlueVibrant),
        contentAlignment = Alignment.Center
    ) {
        // Inicial de respaldo SIEMPRE debajo: si la foto no carga, en vez de
        // un círculo azul vacío se ve la letra.
        Text(
            text = fallbackLetter,
            color = Color.White,
            fontSize = (size * 0.4f).sp,
            fontWeight = FontWeight.Bold
        )
        if (photoModel != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoModel)
                    .crossfade(true)
                    .build(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/** Copia la imagen elegida a un archivo propio de la app (persistente).
 *  Devuelve null si no se pudo leer la URI. */
private fun copyUriToProfileFile(context: android.content.Context, uri: Uri): java.io.File? = try {
    val input = context.contentResolver.openInputStream(uri) ?: return null
    val dir = java.io.File(context.filesDir, "profile").apply { mkdirs() }
    val file = java.io.File(dir, "avatar_${System.currentTimeMillis()}.jpg")
    input.use { stream -> file.outputStream().use { out -> stream.copyTo(out) } }
    file
} catch (_: Exception) {
    null
}