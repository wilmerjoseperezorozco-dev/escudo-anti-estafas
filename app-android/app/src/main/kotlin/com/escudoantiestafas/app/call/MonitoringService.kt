package com.escudoantiestafas.app.call

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.escudoantiestafas.app.R

/**
 * Servicio en primer plano cuya única función es mantener viva la
 * notificación persistente ("protección activa") — señal visible para el
 * usuario de que el monitoreo está encendido. La detección real de estado
 * de llamada vive en [PhoneStateReceiver] (broadcast del sistema, funciona
 * en cualquier SIM de un dispositivo de doble SIM) y en [com.escudoantiestafas.app.sms.SmsOtpReceiver].
 *
 * Usa el tipo `specialUse` (no `phoneCall`): este servicio no marca ni
 * gestiona llamadas — `phoneCall` está reservado por Android 14+ a apps
 * con el rol de marcador (`MANAGE_OWN_CALLS`/`DIALER`), que esta app no
 * necesita ni debe pedir.
 */
class MonitoringService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        iniciarNotificacionPrimerPlano()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    private fun iniciarNotificacionPrimerPlano() {
        val canal = NotificationChannel(
            CANAL_ID,
            getString(R.string.canal_monitoreo_nombre),
            NotificationManager.IMPORTANCE_MIN
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(canal)

        val notificacion: Notification = NotificationCompat.Builder(this, CANAL_ID)
            .setContentTitle(getString(R.string.notificacion_monitoreo_titulo))
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notificacion, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notificacion, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notificacion)
        }
    }

    companion object {
        private const val CANAL_ID = "monitoreo_llamadas"
        private const val NOTIFICATION_ID = 1001
    }
}
