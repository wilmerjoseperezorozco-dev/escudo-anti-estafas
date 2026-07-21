package com.escudoantiestafas.app.alert

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.escudoantiestafas.app.R
import com.escudoantiestafas.app.data.ColaDeReportesSqlite
import com.escudoantiestafas.app.data.ReporteRepository
import com.escudoantiestafas.app.data.TelemetriaApi
import com.escudoantiestafas.app.databinding.ActivityAlertBinding
import kotlinx.coroutines.launch

/**
 * Pantalla de alerta a pantalla completa. Se muestra encima del lockscreen
 * y enciende la pantalla si estaba apagada — igual que una llamada entrante
 * o una alarma, porque el objetivo es interrumpir a la víctima ANTES de que
 * lea el código en voz alta.
 */
class AlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configurarVentanaSobreLockscreen()

        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonEntendido.setOnClickListener { finish() }
        configurarBotonReportar()

        // Conteo anónimo de que la protección funcionó de verdad — ver
        // TelemetriaApi y la política de privacidad, sección 2.
        lifecycleScope.launch { TelemetriaApi.registrarDeteccion() }
    }

    /**
     * Solo se muestra si CallScreeningServiceImpl pudo resolver el número
     * de la llamada (puede venir nulo en números ocultos/privados) — nunca
     * se muestra un botón que fuera a fallar al tocarlo.
     */
    private fun configurarBotonReportar() {
        val numero = intent.getStringExtra(EXTRA_NUMERO)
        if (numero.isNullOrBlank()) {
            binding.botonReportar.visibility = android.view.View.GONE
            return
        }

        binding.botonReportar.visibility = android.view.View.VISIBLE
        binding.botonReportar.setOnClickListener {
            binding.botonReportar.isEnabled = false
            binding.botonReportar.text = getString(R.string.alerta_reportando)

            lifecycleScope.launch {
                val repositorio = ReporteRepository(ColaDeReportesSqlite(applicationContext))
                repositorio.reportar(numero, CATEGORIA_ALERTA)
                binding.botonReportar.text = getString(R.string.alerta_reportado)
            }
        }
    }

    companion object {
        const val EXTRA_NUMERO = "numero"
        private const val CATEGORIA_ALERTA = "whatsapp_otp"
    }

    private fun configurarVentanaSobreLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}
