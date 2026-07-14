// Igual que reportes.test.js: db/supabase.js usa producción por defecto,
// así que estos tests se saltan sin SUPABASE_URL explícito apuntando a un
// proyecto de desarrollo — evita escribir filas de prueba en producción.
const { test, before, after } = require('node:test');
const assert = require('node:assert/strict');
const { crearApp } = require('../server');

const SALTAR = !process.env.SUPABASE_URL;

if (SALTAR) {
  test('tests de estadísticas omitidos: define SUPABASE_URL (proyecto de desarrollo) para correrlos', () => {});
}

let servidor;
let baseUrl;

before(() => {
  if (SALTAR) return;
  servidor = crearApp().listen(0);
  baseUrl = `http://localhost:${servidor.address().port}`;
});

after(() => {
  if (SALTAR) return;
  servidor.close();
});

test('POST /telemetria/alerta registra una detección anónima', { skip: SALTAR }, async () => {
  const res = await fetch(`${baseUrl}/telemetria/alerta`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'X-Forwarded-For': '10.9.0.1' },
    body: JSON.stringify({ version_app: '0.1.3-mvp' }),
  });

  assert.equal(res.status, 201);
});

test('POST /telemetria/alerta funciona sin body', { skip: SALTAR }, async () => {
  const res = await fetch(`${baseUrl}/telemetria/alerta`, {
    method: 'POST',
    headers: { 'X-Forwarded-For': '10.9.0.2' },
  });

  assert.equal(res.status, 201);
});

test('GET /estadisticas devuelve conteos numéricos', { skip: SALTAR }, async () => {
  const res = await fetch(`${baseUrl}/estadisticas`);
  const cuerpo = await res.json();

  assert.equal(res.status, 200);
  assert.equal(typeof cuerpo.detecciones_totales, 'number');
  assert.equal(typeof cuerpo.detecciones_ultimo_mes, 'number');
  assert.equal(typeof cuerpo.numeros_reportados, 'number');
});
