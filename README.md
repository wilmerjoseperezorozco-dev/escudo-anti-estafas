# Escudo Anti-Estafas

Defensa contra el secuestro de WhatsApp vía ingeniería social + robo de código OTP: el vector que casi le funcionó a un estafador contra una víctima de 54 años en Colombia. Ver contexto completo en `docs/guia-anti-estafas.md`.

## Estado: en producción

- **Backend real, funcionando ahora**: `https://escudo-anti-estafas.vercel.app` (Vercel + Supabase Postgres).
- **App Android**: descarga el APK firmado desde la sección [Releases](https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas/releases) de este repositorio — no requiere Play Store. Verificada en dos dispositivos físicos reales (Tecno BG6, Samsung Galaxy A05), incluyendo una correlación real de llamada+OTP.

## Backend

Node/Express, desplegado como función serverless en Vercel, con Postgres (Supabase) como base de datos — Row Level Security habilitado, rate limiting vía una función `security definer` en la propia base de datos (no en memoria del proceso, para que funcione correctamente en serverless).

Para correrlo local (opcional, la app ya apunta al backend real en producción):

```bash
cd backend
npm install
export SUPABASE_URL="https://TU_PROYECTO.supabase.co"
export SUPABASE_ANON_KEY="tu_clave_publicable"
npm start
# → http://localhost:3001
```

Endpoints:

| Endpoint | Qué hace |
|---|---|
| `POST /reportes` | Reporta un número como sospechoso. Body: `{ "numero": "3001234567", "categoria": "whatsapp_otp" }` |
| `GET /reputacion/:numero` | Reputación agregada: total de reportes, reportantes distintos, si supera el umbral de "sospechoso" (3 reportantes distintos) |
| `GET /reportes/:numero/denuncia` | Paquete de denuncia listo para copiar: resumen formateado (categorías, fechas, conteo) para adjuntar en A Denunciar/SPOA. Devuelve 409 si el número no llega al umbral de "sospechoso" todavía |
| `GET /salud` | Health check |

Categorías válidas: `whatsapp_otp`, `suplantacion_banco`, `suplantacion_entidad`, `llamada_spam`, `otro`.

Protecciones: rate limiting (10 solicitudes / 15 min por IP, vía función Postgres `verificar_limite_ip`), un solo reporte por IP+número, umbral mínimo de reportantes distintos antes de marcar un número como sospechoso. La tabla `reportes` no permite `DELETE` desde el cliente (ni el propio backend) — así nadie puede borrar reportes hechos sobre su propio número.

Tests: `npm test` (test runner nativo de Node, sin dependencias nuevas) — son de integración real contra el proyecto Supabase de desarrollo, requieren `SUPABASE_URL`/`SUPABASE_ANON_KEY` en el entorno.

## App Android

Detecta el momento exacto de riesgo: llamada activa + SMS con código OTP llegando al mismo tiempo → alerta a pantalla completa. El contenido de llamadas y SMS nunca sale del dispositivo. El número telefónico sí viaja al backend en dos casos: automáticamente en cada llamada entrante (para consultar reputación y poder bloquear antes de que timbre) y cuando el usuario reporta un número manualmente — ver el detalle en `docs/legal-privacidad-colombia.md`.

- **Descarga directa**: APK firmado en [Releases](https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas/releases) — instálalo habilitando "orígenes desconocidos" para tu navegador/gestor de archivos.
- **Compilar desde el código**: `cd app-android && ./gradlew assembleDebug` (o `assembleRelease` con tu propio keystore, ver `app-android/app/keystore.properties.example`). Ver `app-android/README.md` para el detalle completo.

## Estructura

| Carpeta | Qué es |
|---|---|
| `docs/guia-anti-estafas.md` | Guía para compartir por WhatsApp: cómo reconocer el engaño y blindarse |
| `docs/legal-privacidad-colombia.md` | Marco legal (Ley 1581, Ley 1273), por qué el diseño es privacy-first, checklist antes de distribución pública |
| `docs/restricciones-tecnicas.md` | Lista verificable de lo que la app NUNCA hace, mapeada contra el AndroidManifest.xml real |
| `docs/patrones-psicologicos-colombia.md` | Los 6 principios de persuasión aplicados a estafas colombianas (DIAN, Fiscalía, empleo falso), investigación sobre por qué la educación no protege, frases gatillo para futuras fases de detección |
| `docs/canales-reporte-operadoras.md` | Qué canales de reporte a operadoras/SIC tienen efecto real vs cuáles no — sin prometer bloqueos que las operadoras no hacen |
| `docs/palanca-regulatoria.md` | Ley 2573 de 2026 (verificación biométrica de SIM, vigente desde noviembre 2026) y dónde aporta realmente este proyecto: vigilancia de cumplimiento, no pedir una ley que ya existe |
| `docs/declaracion-permisos-play-store.md` | Qué exige Google Play para RECEIVE_SMS bajo la excepción de detección de spam, y el riesgo real de que la aprobación no esté garantizada |
| `docs/protocolo-desarrollo-marco-etico.docx` | Protocolo formal (Word) con marco ético, legal, arquitectura y metodología de validación — preparado para revisión de un comité de ética institucional |
| `docs/analisis-brechas-produccion.md` | Qué falta para escalar y, si se decide, monetizar: monitoreo, auto-actualización, límites de los planes gratuitos, camino a Play Store, formalización empresarial |
| `backend/` | API de reportes comunitarios (Node + Express + Supabase Postgres, desplegado en Vercel) |
| `app-android/` | App Kotlin: detección de llamada+OTP, screening de llamadas, overlay de alerta |
