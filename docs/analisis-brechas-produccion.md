# Análisis de brechas para producción — telecomunicaciones y emprendimiento

Fecha del análisis: 2026-07-13. Estado del sistema en el momento de escribir esto: backend en `https://escudo-anti-estafas.vercel.app` (Vercel + Supabase Postgres), 0 errores runtime en las últimas 24h, app Android v0.1.0-mvp firmada y publicada como descarga directa en GitHub Releases. Este documento no repite lo que ya está resuelto — se enfoca en lo que falta para que esto pase de "funciona en producción" a "listo para escalar y, si se decide, para monetizar".

## 1. Monitoreo y observabilidad

**Lo que existe hoy**: Vercel expone logs de runtime y errores agregados por proyecto (verificado en este análisis: `get_runtime_errors` sobre las últimas 24h devolvió 0 errores). Supabase expone advisors de seguridad y performance sobre la base de datos.

**La brecha real**: nada de esto es proactivo. Si el backend empieza a fallar a las 3am, nadie se entera hasta que un usuario se queja o hasta que alguien entra manualmente a revisar. No hay alerting configurado.

**Qué hace falta**:
- Configurar un check de salud externo (ej. un cron gratuito que golpee `/salud` cada 5-10 min y avise por email/Telegram si falla) — no depende de Vercel/Supabase, es la primera línea de defensa.
- Revisar `get_runtime_errors` y los advisors de Supabase (`get_advisors`) al menos semanalmente mientras no haya alerting automático.
- Advisories de seguridad ya detectados en este análisis (los cuatro son decisiones de diseño intencionales, documentadas en el código, pero deben revisarse cada vez que se toque el esquema): RLS sin políticas en `intentos_reporte` (correcto — nadie debe poder leerla/escribirla directo, solo la función RPC), política de INSERT pública en `reportes` (correcto — es el mecanismo de reporte comunitario sin login), y la función `verificar_limite_ip` ejecutable por `anon`/`authenticated` como `SECURITY DEFINER` (correcto — es justamente lo que permite hacer rate limiting sin exponer una tabla de escritura directa).

## 2. Versionado y actualizaciones sin Play Store

**La brecha real**: la app se distribuye como APK firmado vía GitHub Releases, no por una tienda. Eso significa que **no hay auto-actualización**. Un usuario que instaló v0.1.0-mvp hoy seguirá en esa versión para siempre a menos que vuelva manualmente al repositorio y descargue la nueva.

Esto importa especialmente aquí porque el `BASE_URL` y la lógica de detección OTP+llamada pueden necesitar cambios (ej. si WhatsApp o los bancos cambian el formato de sus SMS, o si aparece un nuevo patrón de estafa). Sin mecanismo de aviso, versiones viejas quedan protegiendo con reglas desactualizadas sin que el usuario lo sepa.

**Qué hace falta** (no implementado en este MVP, queda como recomendación concreta):
- Endpoint `GET /version` en el backend que devuelva la última versión disponible y una URL de descarga.
- La app, al abrir, compara su `BuildConfig.VERSION_CODE` contra ese endpoint y muestra un aviso no bloqueante si hay una versión nueva.
- Alternativa de menor esfuerzo: publicar en Play Store más adelante (ver sección 5) — Play Store sí resuelve auto-update de forma nativa, a cambio de las restricciones de permisos ya documentadas en `docs/declaracion-permisos-play-store.md`.

## 3. Costos y límites de los planes gratuitos

Este proyecto corre hoy sobre los planes gratuitos ("Hobby" en Vercel, "Free" en Supabase) de las cuentas personales del usuario. Dos riesgos concretos, no hipotéticos:

- **Términos de uso de Vercel Hobby**: el plan gratuito de Vercel está pensado para proyectos personales/no comerciales — sus términos de servicio restringen el uso comercial bajo Hobby. Si este proyecto empieza a generar ingresos (donaciones, patrocinio, versión paga) o crece a uso intensivo, corresponde migrar a un plan Pro antes de monetizar, no después. Es una decisión de cumplimiento, no solo de capacidad técnica.
- **Proyectos Supabase Free se pausan por inactividad prolongada**: si la API no recibe tráfico durante varias semanas, el proyecto puede pausarse automáticamente y requerir una reactivación manual desde el dashboard. Con tráfico real y creciente esto no debería pasar, pero es un riesgo real en la fase actual de baja adopción — vale la pena revisar el dashboard de Supabase periódicamente mientras el uso sea bajo.
- Ninguno de los dos plan gratuitos tiene garantía de SLA. Para una herramienta que protege a personas de un fraude activo, esa falta de garantía es una limitación real que debe comunicarse con honestidad si el proyecto se presenta institucionalmente (ver `docs/protocolo-desarrollo-marco-etico.docx`).

## 4. Brecha de detección por RCS (ya documentada, se resume aquí por completitud)

Confirmado en pruebas reales con dispositivos físicos: cuando Google Messages usa RCS en vez de SMS clásico, el mensaje nunca dispara el `BroadcastReceiver` de `SMS_RECEIVED` — la detección OTP+llamada no ve ese mensaje. Migrar a cubrir RCS requeriría `NotificationListenerService`, un permiso mucho más invasivo (acceso a todas las notificaciones del teléfono, no solo SMS) que cambiaría el perfil de privacidad de la app. Deliberadamente no implementado en este MVP — queda como decisión de producto pendiente, no como bug.

## 5. Camino a Play Store

El keystore de release ya existe y el `build.gradle.kts` ya firma builds de release con él — esa parte del camino está lista. Falta:
- Completar el Formulario de Declaración de Permisos de Google Play para `RECEIVE_SMS`/`READ_CALL_LOG` bajo la excepción de "detección de spam/llamadas" (ver `docs/declaracion-permisos-play-store.md` para el detalle de qué exige Google y el riesgo real de que la aprobación no esté garantizada — Google rechaza manualmente muchas apps que piden estos permisos si no puede verificar el caso de uso).
- Cuenta de desarrollador de Google Play (pago único de USD 25, requiere verificación de identidad).
- Política de privacidad pública alojada en una URL estable (hoy vive como markdown en el repo — Play Store exige una URL, no un archivo de GitHub es suficiente técnicamente pero conviene una página dedicada).

## 6. CI/CD

No implementado. Hoy cada build/deploy es manual (Gradle local para la app, MCP de Vercel para el backend). Para un proyecto de un solo desarrollador esto es aceptable en esta etapa, pero antes de aceptar contribuciones externas o de escalar el ritmo de cambios, vale la pena un GitHub Actions mínimo: correr `npm test` (backend) y `./gradlew testDebugUnitTest` (Android) en cada push, bloqueando merges si fallan. No se implementa ahora porque el usuario no lo pidió explícitamente — queda anotado como la mejora de mayor relación costo/beneficio si el proyecto gana colaboradores.

## 7. Formalización como empresa

Si el usuario decide que este proyecto pase de "herramienta gratuita" a algo que pueda cobrar, buscar aliados institucionales (operadoras, Fiscalía, banca) o recibir financiación, hace falta una persona jurídica detrás:

- **Registro en Cámara de Comercio de Barranquilla** (matrícula mercantil) — es el paso base para operar formalmente en Colombia.
- **RUT/NIT ante la DIAN** — necesario para facturar, para cualquier convenio institucional formal, y para recibir pagos o donaciones de forma trazable.
- Esto también resuelve un problema de credibilidad: instituciones como operadoras de telecomunicaciones o la Fiscalía (relevante para la "palanca regulatoria" descrita en `docs/palanca-regulatoria.md`) normalmente no firman convenios con una persona natural sin figura jurídica.
- No es bloqueante para seguir operando gratis y de forma informal como está ahora — es una decisión de negocio, no técnica, y depende de si el usuario quiere llevar esto a esa escala.

## 8. Pendiente de esta misma sesión

La verificación de extremo a extremo con infraestructura real quedó completa en el lado del backend (endpoints reales probados contra producción: reporte, reputación, denuncia, rate limiting — los siete confirmados con la URL pública real, no localhost) y en el lado de distribución (el APK descargado desde el Release de GitHub es *byte-idéntico* y tiene la misma firma que el build local firmado). Lo único que falta para cerrar el ciclo completo es instalar ese APK descargado en un teléfono físico por USB y confirmar que arranca contra el backend real — no se pudo completar en esta sesión porque no había ningún dispositivo Android conectado al momento de la verificación. Es la única pieza que requiere acción física del usuario (conectar el teléfono).

## Resumen ejecutivo

| Área | Estado | Bloqueante para uso actual |
|---|---|---|
| Backend en producción | Verificado, 0 errores | No |
| Distribución del APK | Verificado, firma correcta | No |
| Monitoreo proactivo | No existe (solo bajo demanda) | No, pero crece en importancia con más usuarios |
| Auto-actualización | No existe | No, pero genera versiones desactualizadas silenciosas |
| Costos/límites free-tier | Dentro de límites hoy, sin garantía de SLA | No hoy, sí si el uso crece o se monetiza |
| RCS | Brecha de plataforma conocida, no trivial de cerrar | No — es una limitación aceptada, no un bug |
| Play Store | Camino técnico listo, papeleo pendiente | No — la descarga directa ya funciona |
| CI/CD | No existe | No, mientras sea un solo desarrollador |
| Formalización empresarial | No iniciada | Solo si se decide monetizar/institucionalizar |
