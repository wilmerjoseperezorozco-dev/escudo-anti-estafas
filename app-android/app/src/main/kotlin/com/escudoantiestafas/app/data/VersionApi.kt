package com.escudoantiestafas.app.data

import android.util.Log
import com.escudoantiestafas.app.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class VersionDisponible(val etiqueta: String, val urlDescarga: String)

/**
 * Consulta directamente la API pública de GitHub Releases — sin backend
 * propio de por medio. La fuente de verdad de "cuál es la última versión"
 * ya es GitHub (ahí se publican los releases), así que replicar ese dato
 * en nuestro backend solo agregaría un paso más para desincronizarse.
 */
object VersionApi {

    private const val TAG = "VersionApi"
    private const val URL_ULTIMO_RELEASE =
        "https://api.github.com/repos/wilmerjoseperezorozco-dev/escudo-anti-estafas/releases/latest"
    private const val TIMEOUT_MS = 4000

    suspend fun hayVersionMasReciente(): VersionDisponible? = withContext(Dispatchers.IO) {
        try {
            val conexion = URL(URL_ULTIMO_RELEASE).openConnection() as HttpURLConnection
            conexion.connectTimeout = TIMEOUT_MS
            conexion.readTimeout = TIMEOUT_MS
            conexion.setRequestProperty("Accept", "application/vnd.github+json")

            if (conexion.responseCode != HttpURLConnection.HTTP_OK) {
                conexion.disconnect()
                return@withContext null
            }

            val cuerpo = conexion.inputStream.bufferedReader().use { it.readText() }
            conexion.disconnect()

            val json = JSONObject(cuerpo)
            val etiqueta = json.optString("tag_name")
            val etiquetaActual = "v${BuildConfig.VERSION_NAME}"

            if (etiqueta.isBlank() || etiqueta == etiquetaActual) {
                return@withContext null
            }

            VersionDisponible(
                etiqueta = etiqueta,
                urlDescarga = "https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas" +
                    "/releases/latest/download/app-release.apk",
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Nunca debe bloquear el uso de la app — si falla la consulta
            // (sin internet, GitHub caído, límite de tasa), simplemente no
            // se muestra el aviso de actualización esta vez.
            Log.w(TAG, "No se pudo consultar la última versión: ${e.javaClass.simpleName}")
            null
        }
    }
}
