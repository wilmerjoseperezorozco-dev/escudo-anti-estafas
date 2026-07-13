# Nota legal y de privacidad (Colombia)

> **Esto es orientación inicial para guiar decisiones de diseño técnico, no asesoría legal formal.** Antes de distribuir la app públicamente (Fase 3), este documento debe ser revisado por un abogado especializado en protección de datos y derecho de las telecomunicaciones en Colombia.

## Por qué el diseño es "todo se queda en el dispositivo"

La app (Fase 1) analiza dos señales para detectar el momento de riesgo: si hay una llamada activa, y si llega un SMS con formato de código OTP. **Ambas señales se procesan y se descartan en el propio teléfono del usuario.** Ningún contenido de llamada, transcripción o texto de SMS sale del dispositivo, nunca.

Lo único que sale hacia el backend es el **número telefónico**, en dos situaciones distintas — importante no confundirlas:
1. **Consulta automática de reputación** (`CallScreeningServiceImpl`): cada vez que entra una llamada, la app envía ese número al backend para preguntar si ya fue reportado, y así poder bloquear antes de que timbre. Esto ocurre en cada llamada, no requiere un toque adicional del usuario — el consentimiento se da una sola vez, en el onboarding, al activar el rol de screening de llamadas.
2. **Reporte manual** (`ReputacionApi.reportarNumero`): solo ocurre si el usuario toca explícitamente "Reportar este número como sospechoso".

En ambos casos viaja únicamente el número — nunca contenido de la llamada, nunca el SMS, nunca contactos ni ubicación.

Esta decisión de arquitectura no es solo buena práctica — es lo que mantiene el proyecto fuera de las obligaciones más pesadas de tratamiento masivo de datos personales sensibles, al menos en el MVP.

## Marco legal relevante

- **Grabación de llamadas propias**: en Colombia, quien participa en una conversación puede grabarla sin necesidad de informar previamente a la otra parte (consentimiento de una sola parte), a diferencia de la interceptación de comunicaciones ajenas, que sí requiere orden judicial (Constitución Art. 15, Código Penal Art. 269C sobre interceptación de datos). La app, en su versión MVP, **no graba ni almacena audio de llamadas** — solo reacciona a metadatos (llamada activa sí/no) y al texto plano de SMS que el propio usuario ya recibe. La transcripción en vivo (Fase 1.5, futura) sí tocaría esta zona más de cerca y requiere disclosure explícito al usuario en el onboarding, incluso si es su propia llamada.
- **Ley 1581 de 2012 (Habeas Data)**: regula el tratamiento de datos personales en Colombia. Un número de teléfono es dato personal. El backend de reportes comunitarios (Fase 2) sí trata datos personales (números reportados) y por tanto:
  - Debe tener una política de tratamiento de datos publicada.
  - Debe permitir al titular del dato ejercer sus derechos (conocer, actualizar, rectificar, suprimir).
  - Debe registrarse ante la Superintendencia de Industria y Comercio (SIC) si el volumen de datos tratados supera los umbrales del Registro Nacional de Bases de Datos (RNBD) — a evaluar cuando el proyecto escale más allá del MVP.
- **Ley 1273 de 2009 (Delitos Informáticos)**: protege contra acceso abusivo a sistemas y datos. Relevante en dirección contraria — es la ley que ampara a las víctimas de estos secuestros de WhatsApp, y es la que sustenta la denuncia penal que se recomienda en la guía de usuario.
- **Ley 1266 de 2008**: régimen general de habeas data financiero, relevante si en el futuro el producto interactúa directamente con datos bancarios (fuera del alcance del MVP, que no toca datos financieros, solo alerta genérica).

## Riesgos de abuso del propio sistema (a mitigar en Fase 2)

- **Envenenamiento de reputación**: alguien podría reportar masivamente un número legítimo (competencia desleal, venganza personal, error). Mitigación en el backend: umbral mínimo de reportes distintos antes de marcar un número como sospechoso, más rate limiting por IP/dispositivo.
- **Falsos negativos**: un estafador con número nuevo no aparecerá en la base de reputación. Por diseño, el sistema no depende solo de la reputación de números — la correlación llamada+OTP funciona incluso contra un número que nunca ha sido reportado antes, porque no depende de una lista negra.

## Antes de Fase 3 (distribución pública masiva)

- [ ] Revisión por abogado de protección de datos.
- [ ] Política de tratamiento de datos publicada y enlazada desde la app.
- [ ] Evaluar necesidad de registro RNBD ante la SIC según volumen proyectado.
- [ ] Términos de uso que dejen claro que la app es una ayuda de detección, no una garantía absoluta contra fraude.
