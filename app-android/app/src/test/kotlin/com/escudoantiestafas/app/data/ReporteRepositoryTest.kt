package com.escudoantiestafas.app.data

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class ColaDeReportesEnMemoria : ColaDeReportes {
    private var siguienteId = 1L
    val elementos = mutableMapOf<Long, ReportePendiente>()

    override fun encolar(numero: String, categoria: String) {
        val id = siguienteId++
        elementos[id] = ReportePendiente(id, numero, categoria, intentos = 0)
    }

    override fun listarPendientes(): List<ReportePendiente> = elementos.values.toList()

    override fun eliminar(id: Long) {
        elementos.remove(id)
    }

    override fun incrementarIntentos(id: Long) {
        elementos[id]?.let { elementos[id] = it.copy(intentos = it.intentos + 1) }
    }
}

class ReporteRepositoryTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `reporte exitoso no queda en la cola`() = runTest {
        val cola = ColaDeReportesEnMemoria()
        val repo = ReporteRepository(cola, enviar = { _, _ -> true })

        repo.reportar("3001234567", "whatsapp_otp")

        assertTrue(cola.elementos.isEmpty())
    }

    @Test
    fun `reporte fallido se encola para reintentar despues`() = runTest {
        val cola = ColaDeReportesEnMemoria()
        val repo = ReporteRepository(cola, enviar = { _, _ -> false })

        repo.reportar("3001234567", "whatsapp_otp")

        assertEquals(1, cola.elementos.size)
        assertEquals("3001234567", cola.elementos.values.first().numero)
    }

    @Test
    fun `reintentarPendientes elimina de la cola los que ya se pudieron enviar`() = runTest {
        val cola = ColaDeReportesEnMemoria()
        cola.encolar("3001234567", "whatsapp_otp")
        val repo = ReporteRepository(cola, enviar = { _, _ -> true })

        val fallidosRestantes = repo.reintentarPendientes()

        assertEquals(0, fallidosRestantes)
        assertTrue(cola.elementos.isEmpty())
    }

    @Test
    fun `reintentarPendientes mantiene en cola e incrementa intentos si sigue fallando`() = runTest {
        val cola = ColaDeReportesEnMemoria()
        cola.encolar("3001234567", "whatsapp_otp")
        val repo = ReporteRepository(cola, enviar = { _, _ -> false })

        val fallidosRestantes = repo.reintentarPendientes()

        assertEquals(1, fallidosRestantes)
        assertEquals(1, cola.elementos.values.first().intentos)
    }

    @Test
    fun `reintentarPendientes descarta un reporte tras superar el maximo de intentos`() = runTest {
        val cola = ColaDeReportesEnMemoria()
        cola.encolar("3001234567", "whatsapp_otp")
        repeat(10) { cola.incrementarIntentos(1L) }
        val repo = ReporteRepository(cola, enviar = { _, _ -> false })

        repo.reintentarPendientes()

        assertTrue(cola.elementos.isEmpty())
    }
}
