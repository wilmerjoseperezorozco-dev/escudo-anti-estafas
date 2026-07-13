# Política de privacidad — Escudo Anti-Estafas

**Última actualización: 13 de julio de 2026.**
Aplica a la aplicación Android "Escudo Anti-Estafas" (`com.escudoantiestafas.app`) y a su backend en `https://escudo-anti-estafas.vercel.app`. Responsable del tratamiento: Wilmer José Pérez Orozco, Barranquilla, Colombia. Contacto: **wilmerjoseperezorozco@gmail.com**.

> Este documento está redactado para cumplir con lo que exige la sección "Data Safety" de Google Play y, de forma anticipada, con el estándar de "App Privacy" (nutrition label) de Apple — aunque hoy la app es exclusivamente Android (ver la nota en la sección 9). También sigue los lineamientos de la Ley 1581 de 2012 de Colombia (Habeas Data). No reemplaza asesoría legal formal antes de una distribución masiva; ver `docs/legal-privacidad-colombia.md` para el detalle de esa recomendación.

## 1. Resumen en una frase

Escudo Anti-Estafas analiza, **únicamente dentro de tu teléfono**, si una llamada activa coincide con la llegada de un código de verificación por SMS; lo único que sale de tu dispositivo hacia nuestro servidor es un número telefónico, y solo en dos situaciones concretas descritas abajo — nunca el contenido de tus llamadas o mensajes.

## 2. Qué datos recogemos, y de quién

| Dato | ¿De quién? | ¿Cuándo viaja al servidor? |
|---|---|---|
| Número de teléfono de una llamada entrante | Del **llamante** (puede ser un tercero, no necesariamente tú) | Automáticamente, en cada llamada entrante, para consultar si ese número ya fue reportado por la comunidad (`GET /reputacion/:numero`) |
| Número de teléfono que decides reportar | Del número que tú reportas manualmente | Solo cuando tocas explícitamente "Reportar este número" (`POST /reportes`) |
| Dirección IP de tu conexión | Tuya | Automáticamente en cada solicitud al servidor — es un dato técnico estándar de cualquier petición web, usado solo para limitar abusos (ver sección 4) |

Sobre el número del llamante: es un dato personal de un tercero que no dio consentimiento directo a esta app. Lo tratamos bajo la misma base que usan apps de identificación de llamadas/spam (Truecaller y similares): interés legítimo en la prevención de fraude, limitado estrictamente al número — nunca se asocia a nombre, foto, ni ningún otro dato de esa persona, porque la app no accede a esa información en primer lugar.

## 3. Qué NUNCA se recoge

- El contenido de tus llamadas (no se graba audio, no hay transcripción en esta versión).
- El contenido de tus SMS — el texto se analiza localmente contra patrones de código OTP y se descarta de inmediato; nunca se transmite ni se guarda.
- Tu lista de contactos.
- Tu ubicación.
- Datos bancarios o financieros.
- Ningún identificador publicitario, ni SDK de analítica o rastreo de terceros — la app no tiene ninguno integrado.
- Nombre, correo o cualquier dato de registro — no existe cuenta de usuario ni login.

La lista completa de permisos que la app NUNCA solicita (contactos, ubicación, cámara, micrófono, almacenamiento, etc.) está verificada línea por línea contra el `AndroidManifest.xml` real en `docs/restricciones-tecnicas.md`.

## 4. Para qué usamos estos datos

- **Reputación de números**: cuando consultamos o guardamos un número, es exclusivamente para calcular si varias personas distintas lo han reportado como sospechoso, y así poder avisarte antes de que contestes.
- **Prevención de abuso del propio sistema**: la dirección IP se usa solo para aplicar un límite de solicitudes por IP (evita que una sola persona sature el sistema o "envenene" la reputación de un número legítimo reportándolo en masa). No se usa para identificarte, no se cruza con ningún otro dato, y no se muestra a otros usuarios.
- No usamos tus datos para publicidad, no los vendemos, y no construimos un perfil de tu comportamiento.

## 5. Con quién se comparten los datos

Usamos dos proveedores de infraestructura como **encargados de tratamiento** (procesan los datos por nuestra instrucción, no los usan para sus propios fines):

- **Supabase** (base de datos Postgres) — aloja los números reportados y las direcciones IP con fines de límite de solicitudes. Región del servidor: Estados Unidos (us-east-2).
- **Vercel** (hosting del backend) — ejecuta el servidor que recibe las solicitudes de la app. Región del servidor: Estados Unidos (iad1).

Esto implica una **transferencia internacional de datos** fuera de Colombia hacia Estados Unidos. Ambos proveedores tienen sus propias políticas de privacidad y certificaciones de seguridad (SOC 2 en el caso de Supabase y Vercel) — no compartimos tus datos con ningún otro tercero, no los vendemos, y no los usamos con fines distintos a los descritos en la sección 4.

## 6. Cuánto tiempo se conservan los datos

- Los números reportados se conservan indefinidamente por ahora, porque son la base del sistema de reputación comunitaria (un número reportado hace 6 meses sigue siendo información relevante). Esta política de retención está bajo revisión — ver `docs/analisis-brechas-produccion.md`.
- Los registros de IP usados para limitar solicitudes tienen una ventana de relevancia de 15 minutos (es la ventana del propio límite de tasa), aunque hoy no se purgan automáticamente de la base de datos — es una mejora pendiente, documentada como tal.
- Puedes solicitar en cualquier momento que se elimine un número que tú mismo reportaste (ver sección 7) — por diseño, la base de datos no permite el borrado directo desde la app (así ningún estafador puede borrar los reportes hechos sobre su propio número), pero sí podemos hacerlo manualmente desde el lado del servidor ante una solicitud legítima tuya.

## 7. Tus derechos (Ley 1581 de 2012 — Habeas Data)

Si eres residente en Colombia, tienes derecho a:

- **Conocer** qué datos tuyos (o de un número que administras) tenemos.
- **Actualizar y rectificar** datos inexactos.
- **Suprimir** datos cuando no exista un deber legal o contractual de conservarlos.
- **Revocar** la autorización de tratamiento en cualquier momento.
- Presentar quejas ante la **Superintendencia de Industria y Comercio (SIC)** si consideras que tus derechos no fueron atendidos.

Para ejercer cualquiera de estos derechos, escribe a **wilmerjoseperezorozco@gmail.com** indicando el número telefónico involucrado y qué quieres solicitar. Al no requerir cuenta ni login, no podemos verificar tu identidad mediante un sistema automático — la solicitud se atiende de forma manual, verificando razonablemente que quien la hace tiene relación legítima con el número.

## 8. Seguridad

- Todo el tráfico entre la app y el servidor viaja cifrado (HTTPS/TLS) — la app bloquea explícitamente el tráfico sin cifrar en la versión de producción (`network_security_config.xml`).
- La base de datos tiene Row Level Security (RLS) habilitado: solo se permite insertar y leer reportes, nunca modificarlos o borrarlos desde el cliente.
- No hay contraseñas ni datos de sesión que proteger porque no existe sistema de cuentas.

## 9. Menores de edad

La app no está dirigida a menores de edad y no solicita ni verifica edad de forma activa, porque no recolecta datos de identificación personal directa (nombre, foto, fecha de nacimiento). Si tienes razones para creer que un menor usó la app de forma que generó una preocupación de privacidad, contáctanos.

## 10. Sobre Apple / iOS

Hoy Escudo Anti-Estafas es una aplicación **exclusivamente Android** — la detección central depende de `CallScreeningService` y lectura de SMS entrantes, APIs que iOS no expone a apps de terceros por política de la plataforma (ver la decisión de arquitectura documentada en el plan del proyecto). Esta política se redacta pensando también en el formato de "App Privacy" de Apple para que, si en el futuro se desarrolla una versión iOS con capacidades distintas, ya exista una base de lenguaje y categorías de datos lista para adaptar — no porque exista hoy una app en la App Store.

## 11. Mapeo a la sección "Data Safety" de Google Play

| Categoría de Google Play | ¿Se recolecta? | ¿Se comparte con terceros? | Propósito declarado |
|---|---|---|---|
| Información personal (número de teléfono) | Sí | No (solo encargados de tratamiento, sección 5) | Funcionalidad de la app (prevención de fraude) |
| Identificadores de dispositivo | No | No | — |
| Ubicación | No | No | — |
| Contactos | No | No | — |
| Información financiera | No | No | — |
| Fotos/video/audio | No | No | — |
| Actividad en la app / analítica | No | No | — |
| Registros de diagnóstico (IP, técnico) | Sí (solo IP, automático) | No | Prevención de abuso / seguridad |

Todos los datos declarados como "recolectados" viajan cifrados en tránsito. No se ofrece mecanismo de borrado de cuenta porque no existe cuenta; el borrado de un reporte específico se solicita por contacto directo (sección 7).

## 12. Cambios a esta política

Si esta política cambia de forma material, se actualizará la fecha al inicio del documento y, cuando la app tenga mecanismo de aviso in-app (ver `docs/analisis-brechas-produccion.md`, sección de versionado), se notificará ahí también. Mientras tanto, la fuente de verdad es esta página en el repositorio público del proyecto.

## 13. Contacto

**wilmerjoseperezorozco@gmail.com** — para preguntas sobre esta política, solicitudes de acceso/rectificación/supresión de datos, o reportar una inquietud de privacidad.
