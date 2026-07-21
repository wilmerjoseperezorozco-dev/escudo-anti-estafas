/** Límite de 10 solicitudes / 15 min por IP, vía la función Postgres
 * `verificar_limite_ip` (SECURITY DEFINER) — compartido entre rutas para
 * no duplicar la lógica de rate limiting. */
function crearLimitarPorIp(supabase) {
  return async function limitarPorIp(req, res, next) {
    try {
      const { data: permitido, error } = await supabase.rpc('verificar_limite_ip', {
        ip_cliente: req.ip,
      });

      if (error) throw error;

      if (!permitido) {
        return res.status(429).json({
          error: 'demasiadas_solicitudes',
          mensaje: 'Intenta de nuevo más tarde.',
        });
      }

      next();
    } catch (error) {
      console.error('Fallo el chequeo de rate limiting:', error);
      res.status(500).json({ error: 'error_interno' });
    }
  };
}

module.exports = { crearLimitarPorIp };
