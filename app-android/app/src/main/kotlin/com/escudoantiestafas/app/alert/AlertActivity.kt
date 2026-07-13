package com.escudoantiestafas.app.alert

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.escudoantiestafas.app.databinding.ActivityAlertBinding

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
