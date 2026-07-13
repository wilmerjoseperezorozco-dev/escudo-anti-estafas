package com.escudoantiestafas.app

import android.app.Application
import com.escudoantiestafas.app.alert.AlertNotifier
import com.escudoantiestafas.app.core.RiskCorrelator
import com.escudoantiestafas.app.work.ReintentoReportesWorker

class EscudoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RiskCorrelator.establecerListenerDeAlerta {
            AlertNotifier.dispararAlerta(applicationContext)
        }
        ReintentoReportesWorker.programar(applicationContext)
    }
}
