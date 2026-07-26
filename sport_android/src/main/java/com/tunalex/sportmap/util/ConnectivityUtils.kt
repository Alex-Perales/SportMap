package com.tunalex.sportmap.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberIsOnline(): State<Boolean> {
    val context = LocalContext.current
    val connectivityManager = remember(context) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val isOnline = remember { mutableStateOf(currentlyOnline(connectivityManager)) }

    DisposableEffect(connectivityManager) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isOnline.value = true
            }
            override fun onLost(network: Network) {
                isOnline.value = currentlyOnline(connectivityManager)
            }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                isOnline.value = currentlyOnline(connectivityManager)
            }
        }
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        onDispose { connectivityManager.unregisterNetworkCallback(callback) }
    }
    return isOnline
}

private fun currentlyOnline(cm: ConnectivityManager): Boolean {
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun OfflineBanner() {
    val isOnline by rememberIsOnline()
    AnimatedVisibility(visible = !isOnline) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB91C1C))
                .padding(vertical = 6.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Sin conexión a internet. Los cambios se sincronizarán al reconectar.",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
