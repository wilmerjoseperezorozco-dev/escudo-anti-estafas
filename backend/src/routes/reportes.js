const express = require('express');
const { esNumeroValido, normalizarNumero } = require('../numero');
const { crearLimitarPorIp } = require('../middleware/limitarPorIp');

const CATEGORIAS_VALIDAS = new Set([
  'whatsapp_otp',
  'suplantacion_banco',
  'suplantacion_entidad',
  'llamada_spam',
  'otro',
]);

// Mínimo de reportantes distintos (por IP) antes de marcar un número como
// sospechoso. Evita que una sola persona envenene la reputación de un
// número legítimo repitiendo el reporte.
const UMBRAL_MINIMO_REPORTANTES = 3;

const ETIQUETAS_CATEGORIA = {
  whatsapp_otp: 'Intento de robo de código OTP de WhatsApp',
  suplantacion_banco: 'Suplantación de entidad bancaria',
  suplantacion_entidad: 'Suplantación de otra entidad (Fiscalía, DIAN, etc.)',
  llamada_spam: 'Llamada de estafa/spam repetitiva',
  otro: 'Otro tipo de fraude reportado',
};

function generarDocumentoDenuncia(numero, resumen) {
  const listaCategorias = resumen.categorias
    .map((c) => `  - ${ETIQUETAS_CATEGORIA[c.categoria] ?? c.categoria}: ${c.conteo} reporte(s)`)
    .join('\n');

  return [
    'RESUMEN DE REPORTES COMUNITARIOS — Escudo Anti-Estafas',
    '',
    `Número reportado: ${numero}`,
    `Total de reportes: ${resumen.totalReportes}`,
    `Personas distintas que reportaron: ${resumen.reportantesDistintos}`,
    `Primer reporte: ${resumen.primerReporte}`,
    `Último reporte: ${resumen.ultimoReporte}`,
    '',
    'Categorías reportadas:',
    listaCategorias,
    '',
    'Este documento es un resumen generado automáticamente a partir de reportes',
    'ciudadanos — no es una denuncia formal por sí solo. Adjúntalo como evidencia',
    'de respaldo al presentar tu denuncia en:',
    '  - Policía Nacional (A Denunciar): https://adenunciar.policia.gov.co',
    '  - Fiscalía General (SPOA), denuncia virtual',
    '',
    'Si tú mismo fuiste víctima, agrega tu relato de los hechos, fecha y hora',
    'exactas, y cualquier captura de pantalla o comprobante que tengas.',
  ].join('\n');
}

/** Trae todas las filas de un número y las resume en memoria — el volumen
 * de reportes por número es pequeño (decenas, no miles), así que no hace
 * falta una función agregada en Postgres para esto. */
async function resumirReportes(supabase, numero) {
  const { data, error } = await supabase
    .from('reportes')
    .select('categoria, ip_reportante, creado_en')
    .eq('numero', numero);

  if (error) throw error;

  const reportantesDistintos = new Set(data.map((fila) => fila.ip_reportante)).size;
  const fechas = data.map((fila) => fila.creado_en).sort();
  const conteoPorCategoria = new Map();
  for (const fila of data) {
    conteoPorCategoria.set(fila.categoria, (conteoPorCategoria.get(fila.categoria) ?? 0) + 1);
  }

  return {
    totalReportes: data.length,
    reportantesDistintos,
    primerReporte: fechas[0] ?? null,
    ultimoReporte: fechas[fechas.length - 1] ?? null,
    categorias: [...conteoPorCategoria.entries()]
      .map(([categoria, conteo]) => ({ categoria, conteo }))
      .sort((a, b) => b.conteo - a.conteo),
  };
}

function crearRouterReportes(supabase) {
  const router = express.Router();
  const limitarPorIp = crearLimitarPorIp(supabase);

  router.use('/reportes', limitarPorIp);

  router.post('/reportes', async (req, res) => {
    const { numero, categoria } = req.body ?? {};

    if (typeof numero !== 'string' || !esNumeroValido(numero)) {
      return res.status(400).json({
        error: 'numero_invalido',
        mensaje: 'El número debe ser un móvil colombiano válido (10 dígitos, empieza en 3, con o sin +57).',
      });
    }

    if (typeof categoria !== 'string' || !CATEGORIAS_VALIDAS.has(categoria)) {
      return res.status(400).json({
        error: 'categoria_invalida',
        mensaje: `La categoría debe ser una de: ${[...CATEGORIAS_VALIDAS].join(', ')}`,
      });
    }

    const numeroNormalizado = normalizarNumero(numero);
    const ip = req.ip;

    try {
      const { data: existente, error: errorConsulta } = await supabase
        .from('reportes')
        .select('id')
        .eq('numero', numeroNormalizado)
        .eq('ip_reportante', ip)
        .limit(1);

      if (errorConsulta) throw errorConsulta;

      if (existente.length > 0) {
        return res.status(409).json({
          error: 'reporte_duplicado',
          mensaje: 'Ya reportaste este número anteriormente.',
        });
      }

      const { error: errorInsercion } = await supabase
        .from('reportes')
        .insert({ numero: numeroNormalizado, categoria, ip_reportante: ip });

      if (errorInsercion) throw errorInsercion;

      return res.status(201).json({ ok: true });
    } catch (error) {
      console.error('Fallo al registrar el reporte:', error);
      return res.status(500).json({ error: 'error_interno' });
    }
  });

  router.get('/reportes/:numero/denuncia', async (req, res) => {
    if (!esNumeroValido(req.params.numero)) {
      return res.status(400).json({ error: 'numero_invalido' });
    }

    const numeroNormalizado = normalizarNumero(req.params.numero);

    try {
      const resumen = await resumirReportes(supabase, numeroNormalizado);

      if (resumen.reportantesDistintos < UMBRAL_MINIMO_REPORTANTES) {
        return res.status(409).json({
          error: 'reportes_insuficientes',
          mensaje: `Este número tiene ${resumen.reportantesDistintos} reportante(s) distinto(s). Se necesitan al menos ${UMBRAL_MINIMO_REPORTANTES} para generar un paquete de denuncia.`,
          reportantes_distintos: resumen.reportantesDistintos,
          umbral_minimo: UMBRAL_MINIMO_REPORTANTES,
        });
      }

      return res.json({
        numero: numeroNormalizado,
        total_reportes: resumen.totalReportes,
        reportantes_distintos: resumen.reportantesDistintos,
        primer_reporte: resumen.primerReporte,
        ultimo_reporte: resumen.ultimoReporte,
        categorias: resumen.categorias,
        documento_texto: generarDocumentoDenuncia(numeroNormalizado, resumen),
      });
    } catch (error) {
      console.error('Fallo al generar el paquete de denuncia:', error);
      return res.status(500).json({ error: 'error_interno' });
    }
  });

  router.get('/reputacion/:numero', async (req, res) => {
    if (!esNumeroValido(req.params.numero)) {
      return res.status(400).json({ error: 'numero_invalido' });
    }

    const numeroNormalizado = normalizarNumero(req.params.numero);

    try {
      const resumen = await resumirReportes(supabase, numeroNormalizado);

      return res.json({
        numero: numeroNormalizado,
        total_reportes: resumen.totalReportes,
        reportantes_distintos: resumen.reportantesDistintos,
        sospechoso: resumen.reportantesDistintos >= UMBRAL_MINIMO_REPORTANTES,
        categorias: resumen.categorias,
      });
    } catch (error) {
      console.error('Fallo al consultar reputación:', error);
      return res.status(500).json({ error: 'error_interno' });
    }
  });

  return router;
}

module.exports = { crearRouterReportes, UMBRAL_MINIMO_REPORTANTES, CATEGORIAS_VALIDAS };
