package com.escudoantiestafas.app.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.escudoantiestafas.app.core.RiskCorrelator

/**
 * Escucha el broadcast del sistema `ACTION_PHONE_STATE_CHANGED`, que se
 * dispara sin importar cuál SIM está en la llamada. Esto es lo que hace
 * que la detección funcione en dispositivos de doble SIM — muy comunes
 * en Colombia — donde [MonitoringService] escuchando solo la suscripción
 * "por defecto" se perdería llamadas contestadas en el otro SIM.
 */
class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val estado = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        if (estado == TelephonyManager.EXTRA_STATE_RINGING || estado == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            RiskCorrelator.onLlamadaActiva()
        }
    }
}
