const express = require('express');
const { crearClienteSupabase } = require('./db/supabase');
const { crearRouterReportes } = require('./routes/reportes');
const { crearRouterEstadisticas } = require('./routes/estadisticas');
const { paginaDescargaHtml } = require('./paginaDescarga');
const { paginaPrivacidadHtml } = require('./paginaPrivacidad');

function crearApp() {
  const app = express();
  const supabase = crearClienteSupabase();

  // Necesario siempre: en Vercel (y cualquier hosting real) la app corre
  // detrás de un proxy. Sin esto, req.ip devuelve la IP del proxy para
  // todas las peticiones y el conteo de "reportantes distintos" colapsa
  // a 1 sin importar cuánta gente reporte. El valor "1" confía solo en
  // el primer proxy inmediato, no en cabeceras X-Forwarded-For arbitrarias
  // que un cliente podría falsear.
  app.set('trust proxy', 1);

  app.use(express.json({ limit: '10kb' }));

  // "framework": null en vercel.json es obligatorio para que esta ruta
  // funcione: sin eso, la detección automática de proyectos Express de
  // Vercel toma el control de "/" e ignora el rewrite hacia api/index.js,
  // causando FUNCTION_INVOCATION_FAILED (intenta invocar src/server.js
  // directamente, que exporta { crearApp } y no un handler).
  app.get('/', (_req, res) => res.type('html').send(paginaDescargaHtml));
  app.get('/privacidad', (_req, res) => res.type('html').send(paginaPrivacidadHtml));

  app.use(crearRouterReportes(supabase));
  app.use(crearRouterEstadisticas(supabase));

  app.get('/salud', (_req, res) => res.json({ ok: true }));

  app.use((err, _req, res, _next) => {
    console.error(err);
    res.status(500).json({ error: 'error_interno' });
  });

  return app;
}

if (require.main === module) {
  const PUERTO = process.env.PORT || 3001;
  crearApp().listen(PUERTO, () => {
    console.log(`Escudo Anti-Estafas backend escuchando en http://localhost:${PUERTO}`);
  });
}

module.exports = { crearApp };
