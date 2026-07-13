// Normaliza y valida números telefónicos colombianos.
// Acepta móviles (10 dígitos, empiezan en 3) con o sin prefijo +57 / 57.
const PATRON_MOVIL_CO = /^3\d{9}$/;

function normalizarNumero(entrada) {
  if (typeof entrada !== 'string') return null;

  let limpio = entrada.replace(/[\s\-().]/g, '');

  if (limpio.startsWith('+57')) limpio = limpio.slice(3);
  else if (limpio.startsWith('57') && limpio.length === 12) limpio = limpio.slice(2);

  return limpio;
}

function esNumeroValido(entrada) {
  const normalizado = normalizarNumero(entrada);
  return normalizado !== null && PATRON_MOVIL_CO.test(normalizado);
}

module.exports = { normalizarNumero, esNumeroValido };
