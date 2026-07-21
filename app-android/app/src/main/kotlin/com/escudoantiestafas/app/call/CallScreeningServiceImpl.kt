package com.escudoantiestafas.app.call

import android.telecom.Call
import android.telecom.CallScreeningService
import com.escudoantiestafas.app.core.RiskCorrelator
import com.escudoantiestafas.app.data.ReputacionApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Coteja el número entrante contra la reputación comunitaria (backend).
 * Nunca bloquea silenciosamente por defecto — solo marca la llamada como
 * posible estafa para que el usuario decida; el bloqueo silencioso
 * automático solo aplica a números con reputación claramente sospechosa.
 *
 * Requiere que el usuario haya asignado este servicio como rol de
 * screening de llamadas (RoleManager.ROLE_CALL_SCREENING) desde el
 * onboarding — Android no lo activa solo con el permiso del manifiesto.
 */
class CallScreeningServiceImpl : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val numero = callDetails.handle?.schemeSpecificPart

        if (numero.isNullOrBlank()) {
            responderPermitiendoLlamada(callDetails)
            return
        }

        // Se registra aquí porque este es el único punto del sistema donde
        // la app recibe el número de la llamada entrante — RiskCorrelator
        // lo necesita para poder ofrecer "reportar este número" si la
        // llamada termina disparando la alerta de riesgo (ver AlertActivity).
        RiskCorrelator.registrarNumeroLlamada(numero)

        // onScreenCall no puede suspenderse; se lanza una corrutina de vida
        // corta y se responde en cuanto se tiene la reputación o al vencer
        // el margen de tiempo que exige el sistema (5 segundos).
        CoroutineScope(Dispatchers.IO).launch {
            val reputacion = ReputacionApi.consultarReputacion(numero)
            if (reputacion?.sospechoso == true) {
                responderBloqueandoLlamada(callDetails)
            } else {
                responderPermitiendoLlamada(callDetails)
            }
        }
    }

    private fun responderPermitiendoLlamada(callDetails: Call.Details) {
        respondToCall(callDetails, CallResponse.Builder().build())
    }

    private fun responderBloqueandoLlamada(callDetails: Call.Details) {
        val respuesta = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipNotification(false)
            .build()
        respondToCall(callDetails, respuesta)
    }
}
