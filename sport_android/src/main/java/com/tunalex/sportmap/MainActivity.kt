package com.tunalex.sportmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.tunalex.sportmap.navigation.NavRoutes
import com.tunalex.sportmap.navigation.SportMapNavGraph
import com.tunalex.sportmap.ui.theme.SportMapTheme

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

            val currentUserId by app.container.userPreferences.currentUserId.collectAsStateWithLifecycle(
                initialValue = -1L
            )

            SportMapTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val start = if (currentUserId > 0L) NavRoutes.DASHBOARD else NavRoutes.LOGIN
                SportMapNavGraph(
                    navController = navController,
                    startDestination = start
                )
            }
        }
    }
}
