package com.tunalex.sportmap.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
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
import com.tunalex.sportmap.ui.theme.BlueVibrant
import com.tunalex.sportmap.viewmodel.SportMapViewModels

private val SPORT_EMOJIS = listOf(
    "⚽", "🏀", "🎾", "🏈", "⚾", "🏐",
    "🏉", "🎱", "🏓", "🏸", "🥊", "🤼",
    "🏊", "🚴", "🏋️", "🤸", "⛷️", "🏄",
    "🎯", "🏹", "🥋", "🤺", "🧗", "🤾",
    "🦁", "🐯", "🦅", "🦊", "🐺", "🦋"
)

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
    var showAvatarSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) { }
            profileImageUri = it.toString()
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

    if (showAvatarSheet) {
        ModalBottomSheet(onDismissRequest = { showAvatarSheet = false }) {
            AvatarPickerSheet(
                onPickGallery = {
                    showAvatarSheet = false
                    galleryLauncher.launch(arrayOf("image/*"))
                },
                onPickEmoji = { emoji ->
                    profileImageUri = "emoji:$emoji"
                    showAvatarSheet = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, null) } }
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
                    imageUriOrEmoji = profileImageUri,
                    fallbackLetter = state.user?.name?.firstOrNull()?.uppercase() ?: "?",
                    size = 96,
                    modifier = Modifier.clickable { showAvatarSheet = true }
                )
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
                "Toca para cambiar tu avatar",
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
    imageUriOrEmoji: String?,
    fallbackLetter: String,
    size: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(BlueVibrant),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUriOrEmoji?.startsWith("emoji:") == true -> {
                Text(
                    text = imageUriOrEmoji.removePrefix("emoji:"),
                    fontSize = (size * 0.44f).sp
                )
            }
            imageUriOrEmoji != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(Uri.parse(imageUriOrEmoji))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Text(
                    text = fallbackLetter,
                    color = Color.White,
                    fontSize = (size * 0.4f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvatarPickerSheet(
    onPickGallery: () -> Unit,
    onPickEmoji: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp)
    ) {
        Text("Elige tu avatar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onPickGallery,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Image, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Elegir desde Galería")
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Emojis deportivos",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(260.dp)
        ) {
            items(SPORT_EMOJIS) { emoji ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onPickEmoji(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 22.sp)
                }
            }
        }
    }
}