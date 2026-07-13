package com.escudoantiestafas.app.core

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RiskCorrelatorTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        RiskCorrelator.resetParaPruebas()
    }

    @After
    fun tearDown() {
        RiskCorrelator.resetParaPruebas()
        unmockkStatic(Log::class)
    }

    @Test
    fun `no dispara alerta con solo la señal de llamada`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)

        assertEquals(0, disparos)
    }

    @Test
    fun `no dispara alerta con solo la señal de OTP`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onCodigoOtpRecibido(1_000L)

        assertEquals(0, disparos)
    }

    @Test
    fun `dispara alerta cuando llamada y OTP ocurren dentro de la ventana de 60s`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(31_000L) // 30s después, dentro de la ventana

        assertEquals(1, disparos)
    }

    @Test
    fun `no dispara alerta si el OTP llega fuera de la ventana de 60s`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(1_000L + 60_001L) // 1ms fuera de la ventana

        assertEquals(0, disparos)
    }

    @Test
    fun `dispara alerta exactamente en el borde de la ventana de 60s`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(1_000L + 60_000L) // exactamente 60s

        assertEquals(1, disparos)
    }

    @Test
    fun `el orden de las señales no importa`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onCodigoOtpRecibido(1_000L)
        RiskCorrelator.onLlamadaActiva(20_000L)

        assertEquals(1, disparos)
    }

    @Test
    fun `no reactiva la alerta repetidamente con el mismo par de señales`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(2_000L)
        assertEquals(1, disparos)

        // Una tercera señal de llamada (ej. el sistema notifica el mismo
        // estado otra vez) no debe volver a disparar sin un OTP nuevo.
        RiskCorrelator.onLlamadaActiva(3_000L)
        assertEquals(1, disparos)
    }

    @Test
    fun `una nueva pareja de señales despues de un disparo si activa una nueva alerta`() {
        var disparos = 0
        RiskCorrelator.establecerListenerDeAlerta { disparos++ }

        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(2_000L)
        assertEquals(1, disparos)

        RiskCorrelator.onLlamadaActiva(500_000L)
        RiskCorrelator.onCodigoOtpRecibido(510_000L)
        assertEquals(2, disparos)
    }

    @Test
    fun `sin listener registrado no lanza excepcion al disparar`() {
        RiskCorrelator.onLlamadaActiva(1_000L)
        RiskCorrelator.onCodigoOtpRecibido(2_000L)
        assertTrue(true) // llegar aquí sin excepción es la prueba
    }
}
