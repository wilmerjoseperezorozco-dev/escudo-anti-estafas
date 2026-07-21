package com.escudoantiestafas.app

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.escudoantiestafas.app.call.MonitoringService
import com.escudoantiestafas.app.data.VersionApi
import com.escudoantiestafas.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Onboarding: explica qué hace la app (todo el análisis es local al
 * dispositivo), pide los permisos necesarios y solicita el rol de
 * screening de llamadas. Sin este consentimiento explícito, el
 * monitoreo no arranca.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val permisosNecesarios = buildList {
        add(android.Manifest.permission.RECEIVE_SMS)
        add(android.Manifest.permission.READ_PHONE_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val solicitudPermisos =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultados ->
            if (resultados.values.all { it }) {
                solicitarRolDeScreeningDeLlamadas()
            } else {
                binding.textoEstado.text = getString(R.string.onboarding_permisos_denegados)
            }
        }

    private val solicitudRolScreening =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            solicitarExencionDeOptimizacionDeBateria()
        }

    private val solicitudExencionBateria =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Algunos fabricantes (Transsion/HiOS, MIUI, ColorOS) no devuelven un
            // resultado claro aquí — se sigue de todas formas, el estado real se
            // puede revisar luego en Ajustes si la detección falla en la práctica.
            iniciarMonitoreo()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonActivar.setOnClickListener { comenzarFlujoDePermisos() }

        if (todosLosPermisosConcedidos()) {
            binding.textoEstado.text = getString(R.string.onboarding_activo)
        }

        verificarActualizacionDisponible()
    }

    /**
     * La app se distribuye por APK directo (sin Play Store todavía), así
     * que no hay auto-actualización del sistema operativo — este chequeo
     * es lo que la reemplaza mientras tanto. No bloquea nada si falla.
     */
    private fun verificarActualizacionDisponible() {
        lifecycleScope.launch {
            val version = VersionApi.hayVersionMasReciente() ?: return@launch

            binding.textoActualizacion.text = getString(R.string.actualizacion_disponible, version.etiqueta)
            binding.tarjetaActualizacion.visibility = android.view.View.VISIBLE
            binding.botonActualizar.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(version.urlDescarga)))
            }
        }
    }

    private fun comenzarFlujoDePermisos() {
        val faltantes = permisosNecesarios.filter {
            ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (faltantes.isEmpty()) {
            solicitarRolDeScreeningDeLlamadas()
        } else {
            solicitudPermisos.launch(faltantes.toTypedArray())
        }
    }

    private fun todosLosPermisosConcedidos(): Boolean = permisosNecesarios.all {
        ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarRolDeScreeningDeLlamadas() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            iniciarMonitoreo()
            return
        }

        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            solicitarExencionDeOptimizacionDeBateria()
        } else {
            solicitudRolScreening.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
        }
    }

    /**
     * Sin esto, fabricantes con gestión agresiva de batería (Transsion/HiOS,
     * MIUI, ColorOS — muy comunes en teléfonos económicos en Colombia)
     * pueden congelar el proceso de la app y la detección deja de
     * funcionar en la práctica, aunque el código esté correcto.
     */
    private fun solicitarExencionDeOptimizacionDeBateria() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
            iniciarMonitoreo()
            return
        }

        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        solicitudExencionBateria.launch(intent)
    }

    private fun iniciarMonitoreo() {
        val intent = Intent(this, MonitoringService::class.java)
        ContextCompat.startForegroundService(this, intent)
        binding.textoEstado.text = getString(R.string.onboarding_activo)
    }
}
