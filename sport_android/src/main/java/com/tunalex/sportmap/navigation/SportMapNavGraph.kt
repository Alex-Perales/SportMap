package com.tunalex.sportmap.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tunalex.sportmap.ui.auth.LoginScreen
import com.tunalex.sportmap.ui.auth.SignUpScreen
import com.tunalex.sportmap.ui.dashboard.DashboardScreen
import com.tunalex.sportmap.ui.map.MapScreen
import com.tunalex.sportmap.ui.map.RouteScreen
import com.tunalex.sportmap.ui.place.PlaceDetailScreen
import com.tunalex.sportmap.ui.settings.SettingsScreen
import com.tunalex.sportmap.ui.settings.MedalsScreen
import com.tunalex.sportmap.ui.settings.PremiumScreen
import com.tunalex.sportmap.ui.settings.EditProfileScreen
import com.tunalex.sportmap.ui.settings.AboutScreen
import com.tunalex.sportmap.ui.settings.HelpFaqScreen
import com.tunalex.sportmap.ui.settings.ReservationHistoryScreen
import com.tunalex.sportmap.ui.store.CartScreen
import com.tunalex.sportmap.ui.store.ProductDetailScreen
import com.tunalex.sportmap.ui.store.StoreScreen

@Composable
fun SportMapNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.LOGIN
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (item.route == NavRoutes.DASHBOARD) {
                                    // Para "Inicio": hacer pop directo hasta DASHBOARD sin saveState
                                    val popped = navController.popBackStack(
                                        route = NavRoutes.DASHBOARD,
                                        inclusive = false
                                    )
                                    if (!popped) {
                                        navController.navigate(NavRoutes.DASHBOARD) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(NavRoutes.DASHBOARD) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onGoToSignUp = { navController.navigate(NavRoutes.SIGNUP) }
                )
            }
            composable(NavRoutes.SIGNUP) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(NavRoutes.DASHBOARD) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(NavRoutes.DASHBOARD) {
                DashboardScreen(
                    onStartActivity = {
                        navController.navigate(NavRoutes.MAP) {
                            popUpTo(NavRoutes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOpenPremium = { navController.navigate(NavRoutes.PREMIUM) }
                )
            }
            composable(NavRoutes.MAP) {
                MapScreen(
                    onPlaceClick = { id -> navController.navigate(NavRoutes.placeDetail(id)) }
                )
            }
            composable(
                route = NavRoutes.PLACE_DETAIL_ROUTE,
                arguments = listOf(navArgument(NavRoutes.PLACE_DETAIL_ARG) { type = NavType.LongType })
            ) { entry ->
                val placeId = entry.arguments?.getLong(NavRoutes.PLACE_DETAIL_ARG) ?: 0L
                PlaceDetailScreen(
                    placeId = placeId,
                    onBack = { navController.popBackStack() },
                    onNavigate = { id -> navController.navigate(NavRoutes.route(id)) }
                )
            }
            composable(NavRoutes.STORE) {
                StoreScreen(
                    onProductClick = { id -> navController.navigate(NavRoutes.productDetail(id)) },
                    onCartClick = { navController.navigate(NavRoutes.CART) }
                )
            }
            composable(
                route = NavRoutes.PRODUCT_DETAIL_ROUTE,
                arguments = listOf(navArgument(NavRoutes.PRODUCT_DETAIL_ARG) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(NavRoutes.PRODUCT_DETAIL_ARG) ?: 0L
                ProductDetailScreen(
                    productId = id,
                    onBack = { navController.popBackStack() },
                    onCartClick = { navController.navigate(NavRoutes.CART) }
                )
            }
            composable(NavRoutes.CART) {
                CartScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onLogout = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenMedals = { navController.navigate(NavRoutes.MEDALS) },
                    onOpenPremium = { navController.navigate(NavRoutes.PREMIUM) },
                    onEditProfile = { navController.navigate(NavRoutes.EDIT_PROFILE) },
                    onOpenHelp = { navController.navigate(NavRoutes.HELP_FAQ) },
                    onOpenAbout = { navController.navigate(NavRoutes.ABOUT) },
                    onOpenReservationHistory = { navController.navigate(NavRoutes.RESERVATION_HISTORY) }
                )
            }
            composable(NavRoutes.RESERVATION_HISTORY) {
                ReservationHistoryScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.MEDALS) {
                MedalsScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.PREMIUM) {
                PremiumScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.EDIT_PROFILE) {
                EditProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(NavRoutes.HELP_FAQ) {
                HelpFaqScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = NavRoutes.ROUTE_ROUTE,
                arguments = listOf(navArgument(NavRoutes.ROUTE_PLACE_ARG) { type = NavType.LongType })
            ) { entry ->
                val placeId = entry.arguments?.getLong(NavRoutes.ROUTE_PLACE_ARG) ?: 0L
                RouteScreen(
                    placeId = placeId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
