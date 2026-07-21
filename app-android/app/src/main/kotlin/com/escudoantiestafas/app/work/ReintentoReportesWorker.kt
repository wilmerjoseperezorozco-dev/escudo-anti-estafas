package com.escudoantiestafas.app.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.escudoantiestafas.app.data.ColaDeReportesSqlite
import com.escudoantiestafas.app.data.ReporteRepository
import java.util.concurrent.TimeUnit

/**
 * Reintenta los reportes que no se pudieron enviar por falta de internet.
 * WorkManager exige un mínimo de 15 minutos para trabajo periódico — es
 * aceptable aquí porque un reporte no es urgente en sí mismo, solo no debe
 * perderse.
 */
class ReintentoReportesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repositorio = ReporteRepository(ColaDeReportesSqlite(applicationContext))
        val fallidosRestantes = repositorio.reintentarPendientes()
        return if (fallidosRestantes == 0) Result.success() else Result.retry()
    }

    companion object {
        private const val NOMBRE_TRABAJO = "reintento_reportes"

        fun programar(context: Context) {
            val restricciones = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val solicitud = PeriodicWorkRequestBuilder<ReintentoReportesWorker>(15, TimeUnit.MINUTES)
                .setConstraints(restricciones)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    NOMBRE_TRABAJO,
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    solicitud,
                )
        }
    }
}
