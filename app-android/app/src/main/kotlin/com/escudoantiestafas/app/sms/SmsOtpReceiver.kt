package com.escudoantiestafas.app.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.escudoantiestafas.app.core.OtpPatterns
import com.escudoantiestafas.app.core.RiskCorrelator

/**
 * Reacciona a SMS entrantes. Solo mira si el texto tiene forma de código
 * OTP (ver [OtpPatterns]) para alimentar al [RiskCorrelator] — el contenido
 * del mensaje nunca se envía a ningún servidor ni se registra en logs.
 */
class SmsOtpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val mensajes = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        val cuerpoCompleto = mensajes.joinToString(separator = "") { it.messageBody ?: "" }

        val coincide = OtpPatterns.esMensajeConCodigoOtp(cuerpoCompleto)
        Log.d(TAG, "SMS recibido (${cuerpoCompleto.length} caracteres) — ¿coincide con patrón OTP? $coincide")

        if (coincide) {
            RiskCorrelator.onCodigoOtpRecibido()
        }
    }

    private companion object {
        const val TAG = "SmsOtpReceiver"
    }
}
