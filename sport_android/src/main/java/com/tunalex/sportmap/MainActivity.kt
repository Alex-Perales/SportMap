package com.tunalex.sportmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
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
                val navController = rememberNavController()

                // Siempre arranca en LOGIN. Después del primer frame,
                // revisa DataStore y redirige a DASHBOARD si ya hay sesión válida.
                LaunchedEffect(Unit) {
                    val userId = app.container.userPreferences.currentUserId.first()
                    if (userId > 0L) {
                        val userExists = app.container.database.userDao().findById(userId) != null
                        if (userExists) {
                            navController.navigate(NavRoutes.DASHBOARD) {
                                popUpTo(NavRoutes.LOGIN) { inclusive = true }
                            }
                        } else {
                            // Sesión obsoleta (DB reseteada): limpia el ID guardado
                            app.container.userPreferences.setCurrentUserId(-1L)
                        }
                    }
                }

                SportMapNavGraph(
                    navController = navController,
                    startDestination = NavRoutes.LOGIN
                )
            }
        }
    }
}
