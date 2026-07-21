# Canales reales de reporte — operadoras y entidades

Este documento existe para no prometer en la app algo que las operadoras no hacen. Investigué cada canal antes de escribirlo — nada aquí es inventado.

## Lo primero, sin rodeos: los operadores NO bloquean el número de un tercero solo porque tú lo reportes

Los números `*611` (Claro/Movistar) y `*300`/`*302` (Tigo/WOM) son para **bloquear tu propia línea** si te robaron el celular o la SIM — no existe un botón equivalente para "bloquéenme este número que me llamó a estafar". Revisé la página pública de Claro sobre llamadas fraudulentas (claro.com.co/institucional/llamadas-fraudulentas) y es puramente educativa (qué es el spoofing, cómo desconfiar) — no ofrece un mecanismo de bloqueo por reporte. Esto es intencional por parte de las operadoras: permitir que cualquiera bloquee el número de otro solo con una queja sería un vector de abuso obvio (bloquear a un ex, a un competidor, etc.).

**Conclusión para el producto**: no vamos a decirle al usuario "reporta a tu operadora y bloquean al estafador" porque no es cierto. Sí hay canales reales, con efectos reales, pero son otros:

## Canales que sí tienen efecto

| Canal | Para qué sirve | Cómo |
|---|---|---|
| **Registro de Números Excluidos (RNE)** — CRC | Te saca de listas de llamadas/mensajes comerciales no autorizados. Preventivo, no bloquea estafadores específicos, pero reduce el volumen de contactos no deseados en general | Gratis, en línea: `tramitescrcom.gov.co` |
| **Superintendencia de Industria y Comercio (SIC)** | Denuncia formal contra la conducta de un operador o empresa relacionada con la estafa (ej. si un número se hace pasar por una empresa real) | `sedeelectronica.sic.gov.co` → Denuncias de telecomunicaciones |
| **Tu propia operadora (canal general)** | Dejar constancia/radicado de la llamada fraudulenta — no garantiza bloqueo, pero genera un registro que puede servir como evidencia adicional | Claro: WhatsApp 311-200-0000 o app Mi Claro. Cada operador tiene su propio canal de atención — verificar directamente con cada uno |
| **Policía Nacional / Fiscalía** | **Este es el que sí tiene dientes** — solo la Fiscalía, con orden judicial, puede obtener el bloqueo o la identificación real de una línea a través de la operadora | Ver `docs/guia-anti-estafas.md` y el paquete de denuncia (`GET /reportes/:numero/denuncia`) |

## Qué va en la app

Un botón "Ver canales de reporte" que muestra esta tabla — no un botón falso de "bloquear número" que no cumple lo que promete. La honestidad aquí importa: un usuario que cree que ya "bloqueó" a su estafador y baja la guardia es peor que uno que sabe que tiene que seguir el proceso formal.

Sources:
- [¿Cómo reportar un celular como robado en Claro y los demás operadores en Colombia?](https://www.xataka.com.co/celulares/como-reportar-celular-como-robado-claro-demas-operadores-colombia)
- [Reportar celular robado Movistar Colombia: *611, Policía y MinTIC](https://selectra.com.co/empresas/movistar/robo)
- [Reportar celular robado en Tigo Colombia: paso a paso](https://selectra.com.co/empresas/tigo/reportar-celular-robado)
- [Detecta y reporta las llamadas fraudulentas — Claro](https://www.claro.com.co/institucional/llamadas-fraudulentas/)
- [¿Cómo reporto el robo? — CRC](https://www.crcom.gov.co/es/preguntas-frecuentes/como-reporto-robo)
- [Denuncias de telecomunicaciones y televisión — SIC](https://sedeelectronica.sic.gov.co/atencion-y-servicios-a-la-ciudadania/denuncias-de-telecomunicaciones-y-television)
- [Así puede usar la ley para bloquear mensajes y llamadas spam en Colombia](https://www.elcolombiano.com/tecnologia/registro-numeros-excluidos-colombia-bloquear-llamadas-spam-JO28737952)
