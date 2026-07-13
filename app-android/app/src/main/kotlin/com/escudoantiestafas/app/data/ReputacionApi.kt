package com.escudoantiestafas.app.data

import android.util.Log
import com.escudoantiestafas.app.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Reputacion(val totalReportes: Int, val reportantesDistintos: Int, val sospechoso: Boolean)

/**
 * Cliente mínimo del backend de reportes comunitarios. Sin dependencias
 * externas de red (HttpURLConnection) para mantener el APK liviano.
 * BASE_URL viene de BuildConfig (ver app/build.gradle.kts): en builds
 * `debug` apunta a `http://localhost:3001` (usar `adb reverse tcp:3001
 * tcp:3001` con el dispositivo por USB — nunca exponer el backend de
 * desarrollo a la red WiFi), en `release` apunta al backend real.
 */
object ReputacionApi {

    private const val TAG = "ReputacionApi"
    private val BASE_URL = BuildConfig.BASE_URL
    private const val TIMEOUT_MS = 4000

    suspend fun consultarReputacion(numero: String): Reputacion? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/reputacion/${URLEncoder.encode(numero, "UTF-8")}")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.connectTimeout = TIMEOUT_MS
            conexion.readTimeout = TIMEOUT_MS
            conexion.requestMethod = "GET"

            if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
                conexion.disconnect()
                return@withContext null
            }

            val cuerpo = conexion.inputStream.bufferedReader().use { it.readText() }
            conexion.disconnect()

            val json = JSONObject(cuerpo)
            Reputacion(
                totalReportes = json.optInt("total_reportes"),
                reportantesDistintos = json.optInt("reportantes_distintos"),
                sospechoso = json.optBoolean("sospechoso"),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Falla de red no debe bloquear la llamada: se permite pasar
            // y queda solo la protección de correlación OTP+llamada. Se
            // registra el tipo de error (nunca el número ni datos del
            // usuario) para poder diagnosticar caídas del backend.
            Log.w(TAG, "No se pudo consultar reputación: ${e.javaClass.simpleName}")
            null
        }
    }

    suspend fun reportarNumero(numero: String, categoria: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/reportes")
            val conexion = url.openConnection() as HttpURLConnection
            conexion.connectTimeout = TIMEOUT_MS
            conexion.readTimeout = TIMEOUT_MS
            conexion.requestMethod = "POST"
            conexion.doOutput = true
            conexion.setRequestProperty("Content-Type", "application/json")

            val cuerpo = JSONObject().put("numero", numero).put("categoria", categoria)
            conexion.outputStream.use { it.write(cuerpo.toString().toByteArray()) }

            val exito = conexion.responseCode in 200..299
            conexion.disconnect()
            exito
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo enviar el reporte: ${e.javaClass.simpleName}")
            false
        }
    }
}
