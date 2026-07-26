package com.tunalex.sportmap.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tunalex.sportmap.MainActivity
import com.tunalex.sportmap.R
import com.tunalex.sportmap.SportMapApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Recibe los push de FCM. Cuando la app está en segundo plano, Android ya
 * muestra la notificación solo (usa el canal declarado en el manifest), así
 * que este método solo se ejecuta con la app en primer plano — ahí sí hay
 * que pintarla a mano, si no, el usuario no ve nada. */
class SportMapMessagingService : FirebaseMessagingService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val app = application as? SportMapApp ?: return
        scope.launch {
            val serverId = app.container.userPreferences.serverUserId.first()
            if (serverId > 0) {
                try {
                    app.container.appRepository.registerFcmToken(token)
                } catch (_: Exception) {}
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "SportMap"
        val body = message.notification?.body ?: return
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        ensureDefaultNotificationChannel(this)

        val hasPermission = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val intent = android.content.Intent(this, MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notification)
    }
}
