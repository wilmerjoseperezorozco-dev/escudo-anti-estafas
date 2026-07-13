package com.escudoantiestafas.app.core

import android.util.Log
import java.util.concurrent.atomic.AtomicLong

/**
 * Correlaciona dos señales que por separado son normales pero juntas son
 * la firma del ataque: llamada activa + SMS de código OTP llegando en la
 * misma ventana de tiempo. No intenta entender el contenido de la llamada.
 */
object RiskCorrelator {

    private const val TAG = "RiskCorrelator"
    private const val VENTANA_RIESGO_MS = 60_000L

    private val ultimaLlamadaActivaEnMs = AtomicLong(0)
    private val ultimoOtpRecibidoEnMs = AtomicLong(0)

    private var listener: (() -> Unit)? = null

    fun establecerListenerDeAlerta(callback: () -> Unit) {
        listener = callback
    }

    /** Solo para pruebas: limpia el estado estático entre tests. */
    fun resetParaPruebas() {
        ultimaLlamadaActivaEnMs.set(0)
        ultimoOtpRecibidoEnMs.set(0)
        listener = null
    }

    fun onLlamadaActiva(ahoraMs: Long = System.currentTimeMillis()) {
        Log.d(TAG, "Señal: llamada activa en $ahoraMs")
        ultimaLlamadaActivaEnMs.set(ahoraMs)
        evaluarRiesgo(ahoraMs)
    }

    fun onCodigoOtpRecibido(ahoraMs: Long = System.currentTimeMillis()) {
        Log.d(TAG, "Señal: SMS con forma de OTP en $ahoraMs")
        ultimoOtpRecibidoEnMs.set(ahoraMs)
        evaluarRiesgo(ahoraMs)
    }

    private fun evaluarRiesgo(ahoraMs: Long) {
        val llamada = ultimaLlamadaActivaEnMs.get()
        val otp = ultimoOtpRecibidoEnMs.get()

        if (llamada == 0L || otp == 0L) return

        val dentroDeLaVentana =
            kotlin.math.abs(ahoraMs - llamada) <= VENTANA_RIESGO_MS &&
                kotlin.math.abs(ahoraMs - otp) <= VENTANA_RIESGO_MS

        if (dentroDeLaVentana) {
            Log.w(TAG, "CORRELACIÓN DE RIESGO: llamada=$llamada otp=$otp ahora=$ahoraMs — disparando alerta")
            // Evita reactivar la alerta repetidamente por el mismo par de eventos.
            ultimaLlamadaActivaEnMs.set(0)
            ultimoOtpRecibidoEnMs.set(0)
            listener?.invoke()
        }
    }
}
