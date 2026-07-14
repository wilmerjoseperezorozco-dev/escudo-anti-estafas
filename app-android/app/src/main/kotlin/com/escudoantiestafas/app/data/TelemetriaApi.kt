package com.escudoantiestafas.app.data

import android.util.Log
import com.escudoantiestafas.app.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Registra un conteo anónimo cada vez que RiskCorrelator dispara una
 * alerta real — sin número, sin nada que identifique el dispositivo o al
 * usuario (ver política de privacidad, sección 2). Es la evidencia de
 * impacto real que exige la revisión de Google Play para RECEIVE_SMS y
 * lo que permite mostrar cifras reales al presentar el proyecto.
 */
object TelemetriaApi {

    private const val TAG = "TelemetriaApi"
    private val BASE_URL = BuildConfig.BASE_URL
    private const val TIMEOUT_MS = 4000

    suspend fun registrarDeteccion() = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/telemetria/alerta")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.connectTimeout = TIMEOUT_MS
            conexion.readTimeout = TIMEOUT_MS
            conexion.requestMethod = "POST"
            conexion.doOutput = true
            conexion.setRequestProperty("Content-Type", "application/json")

            val cuerpo = JSONObject().put("version_app", BuildConfig.VERSION_NAME)
            conexion.outputStream.use { it.write(cuerpo.toString().toByteArray()) }
            conexion.responseCode
            conexion.disconnect()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // No debe afectar la experiencia del usuario si falla — es
            // solo una métrica agregada, no algo crítico para la protección.
            Log.w(TAG, "No se pudo registrar la detección anónima: ${e.javaClass.simpleName}")
        }
    }
}
