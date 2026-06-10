package com.tunalex.sportmap.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.DASHBOARD, "Inicio", Icons.Filled.Home),
    BottomNavItem(NavRoutes.MAP, "Mapa", Icons.Filled.Map),
    BottomNavItem(NavRoutes.STORE, "Tienda", Icons.Filled.ShoppingBag),
    BottomNavItem(NavRoutes.SETTINGS, "Ajustes", Icons.Filled.Settings)
)
