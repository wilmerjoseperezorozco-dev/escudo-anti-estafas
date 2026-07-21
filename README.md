# Escudo Anti-Estafas — cómo correrlo

Defensa contra el secuestro de WhatsApp vía ingeniería social + robo de código OTP: el vector que casi le funcionó a un estafador contra una víctima de 54 años en Colombia. Ver contexto completo en `docs/guia-anti-estafas.md`.

## Backend (hoy, funcional)

```bash
cd backend
npm install
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

Protecciones: rate limiting (10 solicitudes / 15 min por IP), un solo reporte por IP+número, umbral mínimo de reportantes distintos antes de marcar un número como sospechoso (evita envenenar la reputación de un número legítimo). Base de datos SQLite local en `backend/datos/reportes.db` (se crea sola, se puede borrar para empezar de cero).

**Importante para despliegue en producción**: si el backend corre detrás de un proxy/hosting real (Render, Railway, nginx...), define `TRUST_PROXY=1` en el entorno. Sin esto, `req.ip` devuelve la IP del proxy para todas las peticiones y el conteo de "reportantes distintos" se rompe (todo colapsa a 1 reportante).

Tests: `npm test` (usa el test runner nativo de Node, sin dependencias nuevas).

## App Android (compila, falta probar en dispositivo real)

Detecta el momento exacto de riesgo: llamada activa + SMS con código OTP llegando al mismo tiempo → alerta a pantalla completa. El contenido de llamadas y SMS nunca sale del dispositivo. El número telefónico sí viaja al backend en dos casos: automáticamente en cada llamada entrante (para consultar reputación y poder bloquear antes de que timbre) y cuando el usuario reporta un número manualmente — ver el detalle en `docs/legal-privacidad-colombia.md`.

`./gradlew assembleDebug` compila limpio (0 errores, 0 warnings). Ver `app-android/README.md` para cómo compilarlo y qué falta antes de correrlo en un teléfono real.

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
| `backend/` | API de reportes comunitarios (Node + Express + SQLite) |
| `app-android/` | App Kotlin: detección de llamada+OTP, screening de llamadas, overlay de alerta |

---

## 🌐 Overview · Resumen

<table>
<tr>
<td width="50%">

### 🇬🇧 English

**Community defense system against WhatsApp hijacking and OTP theft via social engineering — Colombia** — on-device detection, privacy-first, no call or SMS content leaves the phone.

**What it solves:** In Colombia, a common attack vector targets ordinary users: a fraudster calls posing as a bank or a known contact, keeps the victim talking, and simultaneously triggers a WhatsApp account transfer OTP to the victim's phone. The victim reads the code aloud without realizing what it unlocks. This system detects the exact moment of risk — an active call and an incoming OTP arriving at the same time — and triggers a full-screen alert before the victim can make the mistake.

**At maturity:**
- **Android app** → detects active call + OTP SMS simultaneously → full-screen alert freezes the conversation before the code is read aloud → checks the caller's reputation against the community database automatically
- **Community reporting API** → any user reports a suspicious number → after 3 distinct reporters, the number is flagged as suspicious → generates a ready-to-file denuncia package for Colombia's A Denunciar / SPOA platform

**Quick start:**
```bash
# Backend (functional today)
cd backend && npm install && npm start
# → http://localhost:3001

# Android
./gradlew assembleDebug   # compiles clean (0 errors, 0 warnings)
```

**Privacy:** The phone number travels to the backend (for reputation lookup on every incoming call and when manually reported). Call content and SMS text never leave the device. Full legal framework: `docs/legal-privacidad-colombia.md`.

**Status:** Backend functional · Android compiles (0 errors) · Pending: real-device testing + Google Play RECEIVE_SMS exception approval.

</td>
<td width="50%">

### 🇨🇴 Español

**Sistema de defensa comunitaria contra el secuestro de WhatsApp y el robo de códigos OTP por ingeniería social — Colombia** — detección en el dispositivo, privacidad primero, ningún contenido de llamadas o SMS sale del teléfono.

**Qué resuelve:** En Colombia, un vector de ataque frecuente apunta a usuarios comunes: un estafador llama haciéndose pasar por un banco o un conocido, mantiene a la víctima en conversación y simultáneamente activa un código OTP de transferencia de cuenta WhatsApp al teléfono de la víctima. La víctima lee el código en voz alta sin entender lo que está desbloqueando. Este sistema detecta el momento exacto de riesgo — una llamada activa y un SMS OTP llegando al mismo tiempo — y activa una alerta a pantalla completa antes de que la víctima cometa el error.

**En fase madura:**
- **App Android** → detecta llamada activa + SMS OTP simultáneos → alerta a pantalla completa congela la conversación antes de que el código sea leído en voz alta → consulta automáticamente la reputación del llamante en la base de datos comunitaria
- **API de reportes comunitarios** → cualquier usuario reporta un número sospechoso → después de 3 reportantes distintos, el número queda marcado → genera un paquete de denuncia listo para presentar en A Denunciar / SPOA

**Inicio rápido:**
```bash
# Backend (funcional hoy)
cd backend && npm install && npm start
# → http://localhost:3001

# Android
./gradlew assembleDebug   # compila limpio (0 errores, 0 advertencias)
```

**Privacidad:** El número telefónico viaja al backend (para consulta de reputación en cada llamada entrante y al reportar manualmente). El contenido de las llamadas y los SMS nunca sale del dispositivo. Marco legal completo: `docs/legal-privacidad-colombia.md`.

**Estado:** Backend funcional · Android compila (0 errores) · Pendiente: prueba en dispositivo real + aprobación de excepción RECEIVE_SMS en Google Play.

</td>
</tr>
</table>
