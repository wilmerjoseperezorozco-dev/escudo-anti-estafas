// Estos tests son de integración real: requieren SUPABASE_URL y
// SUPABASE_ANON_KEY en el entorno, apuntando al proyecto de desarrollo
// (nunca a uno con datos reales de usuarios). Usan números de prueba con
// prefijo fijo (3001110xxx) para poder identificarlos después.
//
// Nota importante: la política RLS de `reportes` NO permite DELETE desde
// el cliente anon a propósito — así un estafador no puede borrar sus
// propios reportes. Eso significa que estos tests NO pueden autolimpiarse
// con la clave pública; los datos de prueba se acumulan en el proyecto de
// desarrollo. Para purgarlos, usar el MCP de Supabase con un DELETE
// preciso por número exacto (nunca un comodín amplio tipo LIKE):
//   delete from reportes where numero in ('3001110001', '3001110002', '3001110003');
const { test, before, after } = require('node:test');
const assert = require('node:assert/strict');
const { crearApp } = require('../server');

const NUMEROS_DE_PRUEBA = ['3001110001', '3001110002', '3001110003'];

let servidor;
let baseUrl;

before(() => {
  servidor = crearApp().listen(0);
  baseUrl = `http://localhost:${servidor.address().port}`;
});

after(() => {
  servidor.close();
});

async function reportar(numero, categoria, ip) {
  return fetch(`${baseUrl}/reportes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'X-Forwarded-For': ip },
    body: JSON.stringify({ numero, categoria }),
  });
}

test('GET /reportes/:numero/denuncia rechaza con menos de 3 reportantes distintos', async () => {
  const numero = NUMEROS_DE_PRUEBA[0];
  await reportar(numero, 'llamada_spam', '10.1.0.1');

  const res = await fetch(`${baseUrl}/reportes/${numero}/denuncia`);
  const cuerpo = await res.json();

  assert.equal(res.status, 409);
  assert.equal(cuerpo.error, 'reportes_insuficientes');
  assert.equal(cuerpo.reportantes_distintos, 1);
});

test('GET /reportes/:numero/denuncia genera el documento con 3+ reportantes distintos', async () => {
  const numero = NUMEROS_DE_PRUEBA[1];

  await reportar(numero, 'whatsapp_otp', '10.2.0.1');
  await reportar(numero, 'suplantacion_banco', '10.2.0.2');
  await reportar(numero, 'llamada_spam', '10.2.0.3');

  const res = await fetch(`${baseUrl}/reportes/${numero}/denuncia`);
  const cuerpo = await res.json();

  assert.equal(res.status, 200);
  assert.equal(cuerpo.numero, numero);
  assert.equal(cuerpo.total_reportes, 3);
  assert.equal(cuerpo.reportantes_distintos, 3);
  assert.equal(cuerpo.categorias.length, 3);
  assert.match(cuerpo.documento_texto, /RESUMEN DE REPORTES COMUNITARIOS/);
  assert.match(cuerpo.documento_texto, /adenunciar\.policia\.gov\.co/);
  assert.match(cuerpo.documento_texto, new RegExp(numero));
});

test('GET /reportes/:numero/denuncia rechaza número inválido', async () => {
  const res = await fetch(`${baseUrl}/reportes/123/denuncia`);
  assert.equal(res.status, 400);
});

test('POST /reportes rechaza el mismo IP reportando dos veces el mismo número', async () => {
  const numero = NUMEROS_DE_PRUEBA[2];
  await reportar(numero, 'otro', '10.3.0.1');
  const res = await reportar(numero, 'otro', '10.3.0.1');

  assert.equal(res.status, 409);
});
