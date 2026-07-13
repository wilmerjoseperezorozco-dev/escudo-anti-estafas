package com.escudoantiestafas.app.data

import android.util.Log

/**
 * Punto único para reportar un número: intenta enviarlo de inmediato, y si
 * falla (sin internet, backend caído) lo encola localmente en vez de
 * perderlo. [reintentarPendientes] se llama desde [com.escudoantiestafas.app.work.ReintentoReportesWorker]
 * cuando vuelve la conectividad.
 */
class ReporteRepository(
    private val cola: ColaDeReportes,
    private val enviar: suspend (numero: String, categoria: String) -> Boolean = ReputacionApi::reportarNumero,
) {

    suspend fun reportar(numero: String, categoria: String) {
        val exito = enviar(numero, categoria)
        if (!exito) {
            Log.w(TAG, "Envío inmediato falló, encolando reporte para reintento")
            cola.encolar(numero, categoria)
        }
    }

    /** @return cuántos reportes pendientes quedaron sin poder enviarse. */
    suspend fun reintentarPendientes(): Int {
        val pendientes = cola.listarPendientes()
        var fallidos = 0

        for (pendiente in pendientes) {
            if (pendiente.intentos >= MAX_INTENTOS) {
                Log.w(TAG, "Reporte ${pendiente.id} superó el máximo de reintentos, se descarta")
                cola.eliminar(pendiente.id)
                continue
            }

            val exito = enviar(pendiente.numero, pendiente.categoria)
            if (exito) {
                cola.eliminar(pendiente.id)
            } else {
                cola.incrementarIntentos(pendiente.id)
                fallidos++
            }
        }

        return fallidos
    }

    private companion object {
        const val TAG = "ReporteRepository"
        const val MAX_INTENTOS = 10
    }
}
