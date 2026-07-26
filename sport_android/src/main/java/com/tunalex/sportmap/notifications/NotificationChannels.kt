package com.tunalex.sportmap.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.tunalex.sportmap.R

const val DEFAULT_CHANNEL_ID = "sportmap_notifications"

/** Crea el canal de notificaciones por defecto. Debe existir ANTES de que
 * llegue cualquier notificación (incluida la que Android muestra solo en
 * segundo plano, sin pasar por [SportMapMessagingService]), así que se
 * llama desde `SportMapApp.onCreate()`. En Android < 8 los canales no
 * existen y esto no hace nada. */
fun ensureDefaultNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val manager = context.getSystemService(NotificationManager::class.java) ?: return
    val channel = NotificationChannel(
        DEFAULT_CHANNEL_ID,
        context.getString(R.string.app_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Confirmaciones de pago, reservas y novedades de SportMap"
    }
    manager.createNotificationChannel(channel)
}
