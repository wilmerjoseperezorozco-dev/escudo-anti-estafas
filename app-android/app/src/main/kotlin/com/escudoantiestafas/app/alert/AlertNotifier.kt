package com.escudoantiestafas.app.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.escudoantiestafas.app.R

/**
 * Dispara la alerta de riesgo vía notificación full-screen-intent, que es
 * el mecanismo que Android permite para lanzar una pantalla desde background
 * sin usar SYSTEM_ALERT_WINDOW (permiso especial, más restringido y peor
 * visto por la política de Play Store para este tipo de app).
 */
object AlertNotifier {

    private const val CANAL_ID = "alerta_riesgo"
    private const val NOTIFICATION_ID = 2001

    fun dispararAlerta(context: Context, numero: String? = null) {
        crearCanalSiNecesario(context)

        val intent = Intent(context, AlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(AlertActivity.EXTRA_NUMERO, numero)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(context.getString(R.string.alerta_titulo))
            .setContentText(context.getString(R.string.alerta_cuerpo))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificacion)

        context.startActivity(intent)
    }

    private fun crearCanalSiNecesario(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val canal = NotificationChannel(
            CANAL_ID,
            context.getString(R.string.canal_alerta_nombre),
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(canal)
    }
}
