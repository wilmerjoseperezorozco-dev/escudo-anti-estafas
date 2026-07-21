# Patrones psicológicos y sociales de las estafas en Colombia

Este documento existe para que el producto no se quede en "detectar un código OTP", sino que entienda **por qué la manipulación funciona**, específicamente en el contexto colombiano. Sirve para tres cosas: mejorar el contenido de la guía de usuario, alimentar la lista de frases de riesgo de la Fase 1.5 (transcripción en vivo), y — lo más importante — quitarle la culpa a la víctima con evidencia, no con buenas intenciones.

## Lo primero: esto no es cuestión de inteligencia

La investigación en psicología del fraude es consistente en un punto que contradice la intuición: **tener más educación no protege contra la estafa, y en algunos casos es un factor de riesgo**. Las personas con más conocimiento de internet muestran exceso de confianza ("a mí no me va a pasar"), lo cual las hace bajar la guardia más rápido que alguien que ya sospecha por defecto. Los estafadores no atacan el intelecto — atacan la emoción, la confianza y los sesgos cognitivos, y esos mecanismos son universales, no dependen de qué tan preparada esté la persona.

Esto es directamente relevante para tu mamá: que ella diga "si supiera hacerlo, hubiera mandado el código" no es un signo de vulnerabilidad especial — es la descripción exacta de cómo estos ataques están diseñados para funcionar en cualquiera.

## Marco teórico: los 6 principios de persuasión (Cialdini) aplicados a estafas colombianas

Los estafadores no improvisan — usan, consciente o inconscientemente, los mismos 6 principios que estudia la psicología de la persuasión. Cada uno tiene una versión colombiana reconocible:

| Principio | Cómo se ve en Colombia |
|---|---|
| **Autoridad** | Suplantación de DIAN, Fiscalía, Policía, banco. La DIAN misma ha alertado sobre llamadas de supuestos "directivos" ofreciendo devolución de saldos a favor, pidiendo datos bancarios. El uniforme (aunque sea solo una voz seria por teléfono) baja las defensas. |
| **Urgencia / escasez** | "Su cuenta será bloqueada en minutos", "tiene que resolver esto ahora o pierde la vacante", "es la última oportunidad". Anula el pensamiento crítico porque no da tiempo a verificar. |
| **Miedo a sanción** | Variante específica de autoridad, pero merece categoría propia en Colombia: amenaza de embargo, multa de la DIAN, proceso penal de la Fiscalía. El miedo legal es más efectivo aquí que en otros países porque los procesos judiciales colombianos son lentos y opacos — la gente no sabe cómo verificar si algo es real, así que asume que sí. |
| **Reciprocidad / oportunidad económica** | La modalidad más reportada en 2026: falsa oferta de empleo por WhatsApp ("hemos recibido tu hoja de vida, agrégame"). Funciona particularmente bien en un país con alta informalidad laboral y necesidad económica real — no es codicia, es necesidad. |
| **Prueba social / familiaridad** | Variante "eres tú" o "es un familiar en apuros" — el mensaje llega aparentando venir de un contacto conocido ("me robaron el celular, este es mi nuevo número, necesito que me ayudes"). Funciona porque explota la cercanía familiar característica de la cultura colombiana. |
| **Simpatía / compromiso progresivo** | La llamada empieza amable, conversacional, genera rapport antes de pedir nada — para cuando llega la petición del código, la víctima ya se siente en una relación de confianza construida en los últimos 2-3 minutos. |

## Por qué el guion "papel y lápiz" es particularmente efectivo

No es una distracción genérica — es una instrucción que suena razonable (parece que te están ayudando a anotar algo importante) mientras logra el objetivo real: que bajes la mirada al papel justo cuando aparece la ventana emergente de WhatsApp con la advertencia "no compartas este código". Es manipulación de la **atención visual**, no solo de la confianza — el estafador no necesita que confíes ciegamente, solo necesita 4 segundos donde no estés mirando la pantalla.

## Aplicación al producto

**Guía de usuario** (`guia-anti-estafas.md`): ya cubre el guion general; se puede reforzar con el mensaje "esto no es cuestión de inteligencia" respaldado en investigación real, no solo como consuelo.

**Fase 1.5 (transcripción en vivo, futura)**: lista de frases gatillo agrupadas por principio de persuasión, para el scoring de riesgo cuando se implemente:

- *Autoridad*: "le habla de la Fiscalía", "de la DIAN", "de su banco", "somos la entidad de..."
- *Urgencia*: "tiene que ser ahora", "en los próximos minutos", "se le vence hoy", "última oportunidad"
- *Miedo legal*: "proceso penal", "embargo", "multa", "bloqueo de su cuenta"
- *Instrucción de distracción*: "tome lápiz y papel", "anote esto", "no cuelgue"
- *Solicitud directa del código*: "léame el código", "necesito que me diga los números que le llegaron", "reenvíeme el mensaje"

Esta lista es un punto de partida para el clasificador de riesgo, no un reemplazo de la correlación OTP+llamada, que sigue siendo la garantía dura del MVP.
