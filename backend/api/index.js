// Punto de entrada para Vercel: cada request se enruta aquí (ver
// vercel.json) y Express maneja el resto internamente. crearApp() crea
// una instancia por invocación en frío; Vercel reutiliza el mismo
// proceso entre invocaciones "calientes" cercanas en el tiempo.
const { crearApp } = require('../src/server');

module.exports = crearApp();
