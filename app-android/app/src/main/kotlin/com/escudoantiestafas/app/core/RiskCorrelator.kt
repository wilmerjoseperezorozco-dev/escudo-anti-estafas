package com.escudoantiestafas.app.core

import android.util.Log
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

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

    // El número viene de CallScreeningServiceImpl (que sí lo recibe del
    // sistema), no de la señal de "llamada activa" en sí — esta llega vía
    // PhoneStateReceiver, que por diseño no expone el número entrante.
    private val numeroLlamadaActual = AtomicReference<String?>(null)

    private var listener: ((String?) -> Unit)? = null

    fun establecerListenerDeAlerta(callback: (String?) -> Unit) {
        listener = callback
    }

    /** Solo para pruebas: limpia el estado estático entre tests. */
    fun resetParaPruebas() {
        ultimaLlamadaActivaEnMs.set(0)
        ultimoOtpRecibidoEnMs.set(0)
        numeroLlamadaActual.set(null)
        listener = null
    }

    /** Se llama desde CallScreeningServiceImpl.onScreenCall en cuanto entra la llamada. */
    fun registrarNumeroLlamada(numero: String?) {
        numeroLlamadaActual.set(numero)
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
            val numero = numeroLlamadaActual.getAndSet(null)
            listener?.invoke(numero)
        }
    }
}
