# Restricciones técnicas — qué NO hace esta app

Este documento existe para que las garantías de privacidad no dependan de la palabra de nadie: cada restricción está mapeada a un permiso que **no** está declarado en `app-android/app/src/main/AndroidManifest.xml`. Se puede verificar en cualquier momento con:

```bash
grep "uses-permission" app-android/app/src/main/AndroidManifest.xml
```

Los únicos 7 permisos que la app pide, y solo esos, son: `INTERNET`, `RECEIVE_SMS`, `READ_PHONE_STATE`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_PHONE_CALL`, `POST_NOTIFICATIONS`, `USE_FULL_SCREEN_INTENT`.

## Lo que la app nunca hace

| Restricción | Por qué es verificable |
|---|---|
| **No graba audio de ninguna llamada** | No declara `RECORD_AUDIO`. No hay ningún componente que abra el micrófono. |
| **No lee la agenda de contactos** | No declara `READ_CONTACTS`. |
| **No rastrea ni pide ubicación GPS/red** | No declara `ACCESS_FINE_LOCATION` ni `ACCESS_COARSE_LOCATION`. Ningún reporte hacia el backend incluye coordenadas — el schema de `backend/src/db/schema.js` solo guarda número, categoría e IP de origen del reporte. |
| **No lee el historial de mensajes** | No declara `READ_SMS`. `SmsOtpReceiver` solo reacciona al broadcast puntual `SMS_RECEIVED` de un mensaje que ya llegó — nunca abre la bandeja de entrada. |
| **No lee el registro de llamadas** | No declara `READ_CALL_LOG`. El estado de "llamada activa sí/no" viene de `TelephonyCallback`/`PhoneStateListener`, que no expone el historial. |
| **No envía SMS ni hace llamadas en nombre del usuario** | No declara `SEND_SMS` ni `CALL_PHONE`. |
| **No contacta, sanciona ni le escribe al número reportado o sospechoso** | La única acción posible sobre un número marcado como sospechoso es rechazar la llamada entrante (`CallResponse.setRejectCall`) o avisarle al *usuario* con la alerta en pantalla — nunca hay tráfico saliente hacia ese número. La app no tiene, y no debe tener, capacidad de "sancionar": eso es competencia exclusiva de la Fiscalía/Policía dentro de un proceso legal (ver `legal-privacidad-colombia.md`). |
| **No usa el permiso de overlay (`SYSTEM_ALERT_WINDOW`)** | La alerta se muestra vía notificación `fullScreenIntent`, el mecanismo que Android permite sin ese permiso especial — más restringido y mejor visto por la política de Play Store. |
| **No respalda datos a la nube de Google en segundo plano** | `android:allowBackup="false"` en el manifiesto. |
| **No corre oculto**: el monitoreo de llamadas requiere una notificación persistente mientras está activo (obligatorio para todo foreground service en Android) — el usuario siempre ve que la protección está encendida. | `MonitoringService` usa `startForeground()`, que exige notificación visible por diseño del sistema operativo, no por elección de la app. |

## Lo único que sí sale del teléfono

Solo el **número telefónico**, nunca contenido de llamadas o SMS, en dos casos (detallados en `legal-privacidad-colombia.md`):
1. Consulta automática de reputación en cada llamada entrante.
2. Reporte manual cuando el usuario toca "Reportar este número".
