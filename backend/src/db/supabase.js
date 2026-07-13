const { createClient } = require('@supabase/supabase-js');

function crearClienteSupabase() {
  const url = process.env.SUPABASE_URL;
  const clave = process.env.SUPABASE_ANON_KEY;

  if (!url || !clave) {
    throw new Error('Faltan SUPABASE_URL y/o SUPABASE_ANON_KEY en el entorno.');
  }

  return createClient(url, clave, {
    auth: { persistSession: false },
  });
}

module.exports = { crearClienteSupabase };
