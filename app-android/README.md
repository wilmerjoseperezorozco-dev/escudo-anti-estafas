# App Android — Escudo Anti-Estafas

## Estado

**Compila limpio y verificado en dispositivos físicos reales** (Tecno BG6/Android 13 y Samsung Galaxy A05/Android 15) — la correlación central (llamada activa + SMS con código OTP) se confirmó disparándose en una llamada telefónica real, no solo en simulación. 14 tests unitarios pasando (`RiskCorrelator` + `ReporteRepository`), 0 fallas.

Pendiente real: publicación en Play Store (ver `docs/declaracion-permisos-play-store.md`, la aprobación no está garantizada), y detección de mensajes que llegan como RCS en vez de SMS clásico (limitación de plataforma documentada, no un bug).

## Cómo abrirlo en Android Studio

1. Android Studio → **Open** → selecciona la carpeta `app-android/`.
2. Deja que Gradle sincronice (usará el AGP 8.5.2 y Kotlin 1.9.24 declarados en `build.gradle.kts`; ya hay un `gradle-wrapper.properties` generado, así que sincroniza sin pedir nada adicional).
3. Conecta un dispositivo físico Android 8+ con SIM activa por USB y ejecuta desde ahí (el emulador no tiene stack de telefonía real).

## Cómo compilar y probar por línea de comandos (sin abrir la IDE)

```bash
export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr"   # o cualquier JDK 17+
./gradlew.bat assembleDebug        # compilar
./gradlew.bat testDebugUnitTest    # correr los 14 tests unitarios
```

Requiere `local.properties` con `sdk.dir` apuntando al Android SDK (`platform-tools`, `platforms;android-34`, `build-tools;34.0.0` instalados vía `sdkmanager`). Ese archivo está en `.gitignore` — cada máquina genera el suyo.

## Qué hace el núcleo (`core/RiskCorrelator.kt`)

Correlaciona dos señales que por separado son inofensivas:
1. Llamada activa (`call/PhoneStateReceiver.kt`, broadcast `ACTION_PHONE_STATE_CHANGED` del sistema — funciona en cualquier SIM de un dispositivo de doble SIM, a diferencia de escuchar solo la suscripción "por defecto").
2. SMS con forma de código OTP (`sms/SmsOtpReceiver.kt`, vía regex en `core/OtpPatterns.kt`).

Si ambas ocurren dentro de 60 segundos → `alert/AlertNotifier.kt` dispara una notificación full-screen-intent que abre `alert/AlertActivity.kt` encima del lockscreen, incluso con la pantalla apagada.

`call/MonitoringService.kt` solo mantiene la notificación persistente de "protección activa" (tipo `specialUse`, no `phoneCall` — ese tipo está reservado a apps con rol de marcador, que esta app no pide). La detección real no depende de que este servicio siga vivo.

`call/CallScreeningServiceImpl.kt` es una capa adicional: consulta la reputación del número entrante contra el backend (`data/ReputacionApi.kt`) y bloquea automáticamente solo los números que superan el umbral de "sospechoso" del backend.

## Reintentos de reportes (`data/ReporteRepository.kt`)

Si el usuario reporta un número sin internet, el reporte ya no se pierde: se encola en una tabla SQLite local (`data/ColaDeReportes.kt`) y `work/ReintentoReportesWorker.kt` (WorkManager, cada 15 min, solo con red disponible) reintenta el envío hasta 10 veces antes de descartarlo. Nota: todavía no hay un botón de UI que llame a `ReporteRepository.reportar()` — el mecanismo de resiliencia está construido y probado, falta conectarlo a una pantalla real de "reportar este número".

## Antes de correr en un dispositivo real

- **`data/ReputacionApi.kt`**: `BASE_URL` viene de `BuildConfig` (debug → `http://localhost:3001` vía `adb reverse tcp:3001 tcp:3001`; release → dominio real). `network_security_config.xml` (solo en el build `debug`) permite cleartext exclusivamente a `localhost` y `10.0.2.2`; el build `release` bloquea cleartext por completo. No amplíes esa lista a IPs de red arbitrarias — usa `adb reverse`.
- **Rol de screening de llamadas + exención de batería**: el onboarding (`MainActivity.kt`) pide ambos en secuencia. La exención de batería es crítica en fabricantes con gestión agresiva (Transsion/HiOS, MIUI, ColorOS) — sin ella, el sistema puede congelar el proceso y la detección deja de funcionar en la práctica aunque el código esté correcto.
- **Doble SIM**: `PhoneStateReceiver` funciona con cualquiera de los dos SIM activos — verificado en dispositivos con 2 líneas reales.

## Qué falta

- Conectar `ReporteRepository` a una pantalla real de "reportar este número" (el mecanismo ya existe y está probado, falta la UI).
- Ícono de app real con más detalle (hay un ícono adaptativo simple en `res/mipmap-anydpi-v26/ic_launcher.xml`).
- Transcripción en vivo de la llamada para detectar frases de manipulación — documentado como Fase 1.5 en `../docs/patrones-psicologicos-colombia.md`, deliberadamente fuera del MVP.
- Detección de OTP en mensajes RCS (no solo SMS clásico) — requeriría NotificationListenerService, un permiso más invasivo que merece su propio análisis de proporcionalidad antes de implementarse.
- Publicación en Play Store: ver `../docs/declaracion-permisos-play-store.md` — la aprobación bajo la excepción de "detección de spam" no está garantizada para una app nueva sin trayectoria.
