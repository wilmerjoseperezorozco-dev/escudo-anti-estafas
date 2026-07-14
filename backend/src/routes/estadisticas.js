const express = require('express');
const { crearLimitarPorIp } = require('../middleware/limitarPorIp');

const LONGITUD_MAXIMA_VERSION = 20;

/**
 * Conteo agregado de impacto real: cuántas veces la app detectó el patrón
 * de riesgo, y cuántos números distintos reportó la comunidad. Sirve como
 * evidencia concreta para la revisión de Google Play (exige "trayectoria
 * de protección real a los usuarios") y para presentar el proyecto a
 * instituciones — sin inventar cifras, con datos reales de producción.
 *
 * `alertas_detectadas` no guarda número de teléfono, IP ni identificador
 * de dispositivo — ver política de privacidad para el detalle.
 */
function crearRouterEstadisticas(supabase) {
  const router = express.Router();
  const limitarPorIp = crearLimitarPorIp(supabase);

  router.post('/telemetria/alerta', limitarPorIp, async (req, res) => {
    const versionApp =
      typeof req.body?.version_app === 'string'
        ? req.body.version_app.slice(0, LONGITUD_MAXIMA_VERSION)
        : null;

    try {
      const { error } = await supabase.from('alertas_detectadas').insert({ version_app: versionApp });
      if (error) throw error;
      return res.status(201).json({ ok: true });
    } catch (error) {
      console.error('Fallo al registrar detección anónima:', error);
      return res.status(500).json({ error: 'error_interno' });
    }
  });

  router.get('/estadisticas', async (_req, res) => {
    try {
      const [totalDetecciones, deteccionesUltimoMes, numerosReportados] = await Promise.all([
        contarFilas(supabase, 'alertas_detectadas'),
        contarFilas(supabase, 'alertas_detectadas', treintaDiasAtras()),
        contarFilas(supabase, 'reportes'),
      ]);

      return res.json({
        detecciones_totales: totalDetecciones,
        detecciones_ultimo_mes: deteccionesUltimoMes,
        numeros_reportados: numerosReportados,
      });
    } catch (error) {
      console.error('Fallo al consultar estadísticas:', error);
      return res.status(500).json({ error: 'error_interno' });
    }
  });

  return router;
}

function treintaDiasAtras() {
  const fecha = new Date();
  fecha.setDate(fecha.getDate() - 30);
  return fecha.toISOString();
}

async function contarFilas(supabase, tabla, desde) {
  let consulta = supabase.from(tabla).select('*', { count: 'exact', head: true });
  if (desde) consulta = consulta.gte('creado_en', desde);

  const { count, error } = await consulta;
  if (error) throw error;
  return count ?? 0;
}

module.exports = { crearRouterEstadisticas };
