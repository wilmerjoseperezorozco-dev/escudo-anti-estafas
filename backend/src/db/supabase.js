const { createClient } = require('@supabase/supabase-js');

// Proyecto de producción por defecto (escudo-anti-estafas). Ambos valores
// son seguros de tener en el código fuente: es la clave "publicable" de
// Supabase, diseñada para exponerse — la protección real es Row Level
// Security en las tablas (ver docs/ para el detalle de las políticas), no
// el secreto de esta clave. SUPABASE_URL/SUPABASE_ANON_KEY en el entorno
// siguen funcionando para apuntar a otro proyecto (ej. uno de pruebas).
const URL_POR_DEFECTO = 'https://rwxalkhkjyxfosjwzlpo.supabase.co';
const CLAVE_POR_DEFECTO = 'sb_publishable_hwEPc4OdZAwa1t7p8m2TEQ_oaf1Irrx';

function crearClienteSupabase() {
  const url = process.env.SUPABASE_URL || URL_POR_DEFECTO;
  const clave = process.env.SUPABASE_ANON_KEY || CLAVE_POR_DEFECTO;

  return createClient(url, clave, {
    auth: { persistSession: false },
  });
}

module.exports = { crearClienteSupabase };
