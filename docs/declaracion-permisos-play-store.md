# Declaración de permisos sensibles — Google Play

Este documento existe porque `RECEIVE_SMS` no es un permiso que Google apruebe automáticamente en Play Store — investigué la política real antes de escribir esto, no es una checklist genérica.

## La regla general y nuestra excepción

Google exige que, por defecto, solo la app de SMS/Teléfono/Asistente **predeterminada** del sistema pueda pedir permisos de SMS o registro de llamadas. Escudo Anti-Estafas no es ni quiere ser ninguna de esas — sería un cambio de alcance enorme e innecesario.

Existe una **excepción explícita** en la política de Google para: *"Caller ID, detección de spam y/o bloqueo de spam"* — que es exactamente nuestro caso de uso. Para calificar, sin ser la app predeterminada, hay que cumplir:

1. El uso del permiso debe habilitar la **funcionalidad central** de la app (no un extra).
2. No debe existir un método alternativo para lograr esa funcionalidad central.
3. La app debe pasar una revisión y aprobación específica de Google Play — no es automático.
4. **La barra más alta y menos garantizada**: Google pide evidencia de "trayectoria de protección real a los usuarios — reflejada en reportes de analistas, resultados de pruebas comparativas, publicaciones de la industria." Esto está pensado para vendors establecidos (Truecaller y similares) — una app nueva e independiente parte en desventaja real aquí, no es un trámite garantizado.

## Qué declarar sobre `RECEIVE_SMS`

- **Funcionalidad central que habilita**: detectar si un SMS entrante tiene forma de código de verificación (OTP), para correlacionarlo con el estado de la llamada activa y alertar al usuario del patrón de estafa "código pedido durante una llamada" — sin esto, la funcionalidad principal de la app no puede existir.
- **Por qué no hay alternativa**: Android no expone una API de solo-metadatos para "¿llegó un SMS con forma de OTP?" sin permiso de lectura de SMS entrantes.
- **Qué NO se hace con el permiso** (para la sección de salvaguardas del formulario): el contenido del SMS se analiza únicamente en el dispositivo contra patrones locales (`OtpPatterns.kt`), nunca se transmite, nunca se almacena, nunca se comparte con terceros ni se usa para publicidad — ver `docs/restricciones-tecnicas.md` para la lista verificable de permisos NO solicitados.

## Sobre `READ_PHONE_STATE`

Este permiso vive en un grupo de política distinto al de SMS/Registro de llamadas — está más orientado a evitar el mal uso para identificadores de dispositivo/suscriptor (IMEI, número de serie), no exige el mismo Formulario de Declaración de Permisos que `RECEIVE_SMS`. **Esto no está verificado al 100% contra la consola de Play Store actual** — antes de publicar, confirmar directamente en Play Console (la política cambia con frecuencia) o consultar con soporte de Google Play.

## Riesgo real a comunicar al usuario del proyecto

La aprobación **no está garantizada** por cumplir el formulario correctamente. El requisito de "trayectoria demostrable" es subjetivo y favorece a apps con historial. Estrategias que ayudan antes de someter la app a revisión:

- Acumular métricas reales de uso (número de detecciones correctas, tasa de falsos positivos) durante el piloto con las 4 personas ya planeado.
- Documentar públicamente el proyecto (el protocolo ético ya escrito, cobertura de prensa/académica si se logra) como evidencia de seriedad.
- Considerar contactar directamente al soporte de Google Play para una consulta previa si el rechazo inicial ocurre — Google permite apelar con evidencia adicional.

Sources:
- [Use of SMS or Call Log permission groups — Play Console Help](https://support.google.com/googleplay/android-developer/answer/10208820)
- [Permissions used only in default handlers — Android Developers](https://developer.android.com/guide/topics/permissions/default-handlers)
- [Permissions and APIs that Access Sensitive Information — Play Console Help](https://support.google.com/googleplay/android-developer/answer/16558241)
