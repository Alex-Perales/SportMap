package com.tunalex.sportmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tunalex.sportmap.navigation.NavRoutes
import com.tunalex.sportmap.navigation.SportMapNavGraph
import com.tunalex.sportmap.ui.theme.SportMapTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SportMapApp

        setContent {
            val systemDark = isSystemInDarkTheme()
            val savedDark by app.container.userPreferences.darkMode.collectAsStateWithLifecycle(
                initialValue = null
            )
            val isDark = savedDark ?: systemDark

            SportMapTheme(darkTheme = isDark) {
                // Resolvemos la pantalla inicial ANTES de montar el grafo de
                // navegación: así un usuario con sesión válida arranca directo en
                // DASHBOARD y nunca se ve el LOGIN (ni la barra inferior encima
                // de él) durante una transición.
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val userId = app.container.userPreferences.currentUserId.first()
                    val loggedIn = userId > 0L &&
                        app.container.database.userDao().findById(userId) != null
                    if (userId > 0L && !loggedIn) {
                        // Sesión obsoleta (DB reseteada): limpia el ID guardado
                        app.container.userPreferences.setCurrentUserId(-1L)
                    }
                    startDestination = if (loggedIn) NavRoutes.DASHBOARD else NavRoutes.LOGIN
                }

                val dest = startDestination
                if (dest != null) {
                    SportMapNavGraph(startDestination = dest)
                } else {
                    // Micro-splash mientras se resuelve la sesión (1-2 frames).
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}
