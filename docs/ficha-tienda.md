# Ficha de tienda — Google Play

Preparada para publicación futura (ver `docs/declaracion-permisos-play-store.md` para el riesgo real de aprobación de `RECEIVE_SMS`, que sigue siendo el paso crítico antes de esto). Todo el copy está listo para pegar directo en Play Console.

## Título

**Escudo Anti-Estafas: Protección OTP**

30 caracteres máx. en Play Store para el título — este usa 29. La segunda mitad ("Protección OTP") existe porque el nombre solo ("Escudo Anti-Estafas") no comunica qué hace la app a alguien que la ve por primera vez en una búsqueda; "OTP" es el término que la gente ya busca cuando le robaron el código.

## Subtítulo / descripción corta (80 caracteres máx.)

**Detecta el robo de tu código de WhatsApp antes de que te lo quiten**

(69 caracteres)

## Descripción larga (4000 caracteres máx.)

```
¿Te ha llamado alguien haciéndose pasar por tu banco, WhatsApp o una entidad
del Estado, pidiéndote "el código que te acaba de llegar"? Ese es el momento
exacto en que te están robando la cuenta — y pasa en Colombia todos los días.

CÓMO FUNCIONA EL ROBO

El estafador te llama, se hace pasar por soporte técnico o por tu banco, te
mete presión ("no cuelgues", "es urgente", "toma lápiz y papel") mientras en
segundo plano solicita el código de verificación de tu WhatsApp. Ese código
te llega por SMS mientras sigues en la llamada. Si lo compartes, en segundos
pierden tu WhatsApp — y desde ahí, tus contactos y a veces tus cuentas
bancarias.

QUÉ HACE ESCUDO ANTI-ESTAFAS

La app reconoce la firma exacta de este ataque: una llamada activa + un
código de verificación llegando por SMS al mismo tiempo. Cuando eso pasa,
te muestra una alerta a pantalla completa, imposible de ignorar:
"NO COMPARTAS EL CÓDIGO — cuelga ahora."

No necesitas configurar nada ni entender de tecnología. Actívala una vez y
queda protegiéndote en segundo plano.

TU PRIVACIDAD, EN NÚMEROS CONCRETOS

• El contenido de tus llamadas: nunca se graba, nunca se analiza, nunca sale
  del teléfono.
• El texto de tus SMS: se revisa solo dentro del teléfono para reconocer el
  patrón de un código de verificación, y se descarta al instante. Nunca se
  transmite.
• Lo único que puede viajar a nuestro servidor es un número de teléfono —
  el de quien te llama, para consultar si la comunidad ya lo reportó como
  sospechoso, o el que tú decidas reportar manualmente. Nada más.

Puedes leer el detalle técnico completo, verificable línea por línea contra
el código fuente, porque este proyecto es de código abierto.

REPORTES COMUNITARIOS

Si identificas un número usado para estafar, repórtalo con un toque. Ese
reporte ayuda a que la app avise a otras personas antes de que contesten la
llamada — sin necesidad de crear cuenta ni compartir ningún otro dato tuyo.

PARA QUIÉN ES

Pensada especialmente para quienes son el blanco más frecuente de este
fraude: personas mayores de 40 años, sin necesidad de conocimiento técnico
previo. Si ya te pasó, o casi te pasa, esta app existe por esa misma razón.

Escudo Anti-Estafas es un proyecto independiente, de código abierto,
desarrollado en Barranquilla, Colombia.
```

(Aproximadamente 1850 caracteres — hay margen amplio bajo el límite de 4000 para agregar capturas de pantalla adicionales con texto superpuesto más adelante.)

## Palabras clave sugeridas

`estafa`, `whatsapp`, `otp`, `código de verificación`, `fraude telefónico`, `suplantación`, `phishing`, `seguridad`, `antifraude`, `bloqueador de llamadas`, `spam`, `Colombia`

## Categoría sugerida en Play Store

**Herramientas** (alternativa: **Seguridad**, si Play Console la separa como categoría propia al momento de publicar).

## Clasificación de contenido

Sin contenido sensible — clasificación esperada "Para todos". El formulario de clasificación de contenido de Play Console debe llenarse en la consola directamente al publicar (no es algo que se pueda pre-rellenar fuera de ella).

## Assets visuales — estado real

| Asset | Estado | Ubicación |
|---|---|---|
| Ícono adaptativo (en la app) | **Listo.** Rediseñado esta sesión: escudo con degradado azul, sombra interior sutil, check más grueso y legible a tamaño real. Verificado en el launcher de un dispositivo físico. | `app-android/app/src/main/res/drawable/ic_launcher_foreground.xml` y `ic_launcher_background.xml` |
| Vista previa del ícono | Listo — captura real del launcher, con zoom | `store-assets/icono-preview.png` |
| Ícono 512×512 PNG (Play Console) | **Pendiente de exportar.** El ícono ya está finalizado como vector — falta rasterizarlo a PNG 512×512. Es un paso de un solo comando (`Android Studio > Image Asset Studio`, o cualquier convertidor SVG→PNG) que no se pudo completar en esta sesión por una falla puntual del navegador de vista previa (timeout al capturar pantalla, verificado que no era problema del contenido — el HTML/SVG carga y renderiza correctamente). | — |
| Gráfico de funciones (1024×500) | **Diseño listo en código, falta exportar a imagen.** HTML+SVG completo con la misma paleta del ícono, título y tagline. Se puede abrir en cualquier navegador a tamaño 1024×500 y capturar pantalla directamente. | `store-assets/feature-graphic.html` |
| Captura de pantalla — pantalla principal | **Lista, es una captura real del dispositivo**, no un mockup. | `store-assets/screenshot-1-pantalla-principal.png` |
| Captura de pantalla — alerta de riesgo | **No generada intencionalmente.** La pantalla de alerta (`AlertActivity`) no es exportable por diseño de seguridad (no debe poder lanzarse desde fuera de la app) — forzar su exportación solo para una captura de marketing sería debilitar esa protección a cambio de una imagen, así que no se hizo. Alternativa recomendada: capturar la pantalla en el momento en que ocurra una prueba real de correlación llamada+OTP (como se hizo en sesiones de verificación anteriores), o construir un mockup de diseño explícitamente etiquetado como tal (no como screenshot real) usando el mismo estilo visual (`activity_alert.xml`: fondo rojo `#C62828`, texto blanco). |

Google Play exige un mínimo de 2 capturas de pantalla de teléfono para publicar — con el estado actual falta una segunda. La vía más honesta es la primera alternativa (capturar una prueba real), no una recreación artificial de una pantalla de seguridad crítica.
