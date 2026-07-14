# Contenido listo para Play Console

Copiar y pegar directo en los formularios reales cuando exista la cuenta de desarrollador pagada. Todo el texto de aquí ya es consistente con lo que la app hace de verdad — no hay nada que declarar distinto a lo que el código realmente hace, porque un revisor de Google (o cualquiera) puede verificarlo contra el repositorio público.

---

## 1. Formulario de Declaración de Permisos Sensibles (RECEIVE_SMS)

Play Console → App content → Permissions declaration form.

**Permiso declarado:** `RECEIVE_SMS`

**Categoría de caso de uso a seleccionar:** *Caller ID, spam and fraud prevention* (o la traducción equivalente: "Identificador de llamadas, prevención de spam y fraude").

**Campo: "Core app functionality that requires this permission" (funcionalidad central):**
```
Escudo Anti-Estafas detecta en tiempo real el patrón exacto del fraude de
"secuestro de WhatsApp": una llamada telefónica activa que coincide, dentro
de una ventana de 60 segundos, con la llegada de un SMS con forma de código
de verificación (OTP). Cuando ambas señales ocurren juntas, la app muestra
una alerta a pantalla completa advirtiendo al usuario que no comparta el
código. RECEIVE_SMS es el permiso que permite detectar la segunda señal
(la llegada del SMS) — sin él, la función central de la app (que es
exactamente esta correlación) no puede existir.
```

**Campo: "Why isn't there an alternative method to accomplish this" (por qué no hay alternativa):**
```
Android no expone ninguna API de solo-metadatos que permita saber "¿llegó
un SMS con forma de código de verificación?" sin permiso de lectura de SMS
entrantes. No existe un broadcast, notificación del sistema, o API pública
que exponga ese dato de forma indirecta.
```

**Campo: "How the app protects user data" (salvaguardas):**
```
El contenido de cada SMS se analiza exclusivamente dentro del dispositivo,
contra un conjunto de patrones de texto locales (nunca se envía a ningún
servidor). El resultado de ese análisis es un valor booleano (¿coincide
con forma de OTP, sí o no?) que se descarta de inmediato tras usarse para
la correlación. El texto del SMS nunca se transmite, nunca se almacena en
disco ni en memoria más allá del procesamiento inmediato, nunca se
comparte con terceros, y nunca se usa con fines publicitarios. Código
fuente verificable públicamente: OtpPatterns.kt y SmsOtpReceiver.kt en
https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas
```

**Campo de video de demostración (si el formulario lo pide):**
```
Pendiente de grabar — debe mostrar: (1) el flujo de onboarding donde se
solicita el permiso con explicación clara al usuario, (2) una llamada real
o simulada con un SMS de código de verificación llegando durante la
llamada, (3) la alerta a pantalla completa disparándose, (4) confirmación
de que ningún dato sale del dispositivo salvo lo declarado en la política
de privacidad.
```

**Evidencia de "trayectoria de protección real" (la parte no garantizada):**
```
Estadísticas públicas y verificables en tiempo real:
https://escudo-anti-estafas.vercel.app/estadisticas

Estas cifras se generan automáticamente por el propio funcionamiento de la
app en producción — no son una estimación. El endpoint es público y sin
autenticación para que cualquier revisor pueda confirmarlo directamente.
```
*(Nota: mientras las cifras reales sean bajas, esta sección es la más débil de la solicitud — ver `docs/declaracion-permisos-play-store.md` para el riesgo real de que la aprobación no esté garantizada incluso completando el formulario correctamente.)*

---

## 2. Sección "Data Safety" (Seguridad de los datos)

Play Console → App content → Data safety.

**¿Recopila o comparte datos del usuario?** Sí.

| Tipo de dato | ¿Se recopila? | ¿Se comparte? | ¿Es opcional? | Propósito |
|---|---|---|---|---|
| Números de teléfono | Sí | No | No (es la función central) | Funcionalidad de la app (prevención de fraude) |
| Otra información (conteo anónimo de detecciones) | Sí | No | No | Analítica (agregada, no personal) |
| Dirección IP / información del dispositivo | Sí | No | No | Prevención de abuso, seguridad |

**¿Los datos están cifrados en tránsito?** Sí.

**¿Los usuarios pueden solicitar la eliminación de sus datos?** Sí, por contacto directo (no hay sistema de cuentas automatizado) — ver la política de privacidad.

**URL de la política de privacidad:**
```
https://escudo-anti-estafas.vercel.app/privacidad
```

**Descripción general para la sección de datos (texto libre, si el formulario lo pide):**
```
La app procesa números de teléfono (el del llamante, para consultar
reputación comunitaria, y el que el usuario decide reportar) y mantiene
un conteo agregado y anónimo de cuántas veces detectó el patrón de riesgo
— sin número, sin IP asociada, sin identificador de dispositivo. Nunca se
accede a contenido de llamadas, contactos, ubicación, ni archivos. No hay
SDKs de analítica o publicidad de terceros integrados. Detalle completo,
verificable contra el código fuente, en la política de privacidad.
```

---

## 3. Ficha de tienda (Store listing)

Ya está completa y lista en [`docs/ficha-tienda.md`](ficha-tienda.md) — título, descripciones corta/larga, palabras clave, categoría, y estado de cada asset visual. Revisar esa ficha antes de publicar; el ícono y la captura de pantalla principal ya están listos, el gráfico de funciones (1024×500) sigue pendiente de exportar a PNG.

---

## 4. Checklist antes de enviar a revisión

- [ ] Cuenta de desarrollador de Google Play activa (pago único de USD 25).
- [ ] Formulario de Declaración de Permisos completado con el texto de la sección 1.
- [ ] Video de demostración grabado y subido.
- [ ] Sección Data Safety completada con la tabla de la sección 2.
- [ ] Ficha de tienda completa (`docs/ficha-tienda.md`).
- [ ] APK de release firmado subido (ya existe: ver GitHub Releases, siempre usar el más reciente).
- [ ] Clasificación de contenido completada en la consola (cuestionario propio de Play Console, no se puede pre-rellenar fuera de ella).
- [ ] Revisar que `docs/estadisticas` tenga cifras reales — mientras más alto el conteo real de detecciones y reportes, más fuerte la evidencia de "trayectoria de protección real" que exige Google.
