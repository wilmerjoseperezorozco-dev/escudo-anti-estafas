const { test, before, after } = require('node:test');
const assert = require('node:assert/strict');
const { crearApp } = require('./server');

let servidor;
let baseUrl;

before(() => {
  servidor = crearApp().listen(0);
  baseUrl = `http://localhost:${servidor.address().port}`;
});

after(() => {
  servidor.close();
});

test('GET /privacidad sirve la política de privacidad en HTML', async () => {
  const respuesta = await fetch(`${baseUrl}/privacidad`);
  assert.equal(respuesta.status, 200);
  assert.match(respuesta.headers.get('content-type'), /html/);

  const cuerpo = await respuesta.text();
  assert.match(cuerpo, /Política de privacidad/);
  assert.match(cuerpo, /wilmerjoseperezorozco@gmail\.com/);
});
