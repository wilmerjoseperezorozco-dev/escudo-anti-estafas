const URL_REPO = 'https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas';

/**
 * Versión HTML de docs/politica-privacidad.md, con URL pública estable
 * (Google Play exige un enlace a la política de privacidad, no un archivo
 * markdown en un repositorio). Si el contenido de fondo cambia, actualizar
 * ambos archivos — el markdown sigue siendo la fuente que se versiona con
 * más detalle técnico; esta página es la versión de cara al usuario.
 */
const paginaPrivacidadHtml = `<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Política de privacidad — Escudo Anti-Estafas</title>
<meta name="description" content="Qué datos recoge Escudo Anti-Estafas, para qué se usan, con quién se comparten y cómo ejercer tus derechos.">
<style>
  :root {
    --azul-oscuro: #0D47A1;
    --azul: #1565C0;
    --azul-claro: #1E88E5;
    --azul-fondo: #E3F2FD;
    --texto: #1A2027;
    --texto-suave: #4A5568;
    --borde: #E2E8F0;
  }
  * { box-sizing: border-box; }
  body {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    color: var(--texto);
    line-height: 1.6;
    background: #FFFFFF;
    font-size: 17px;
  }
  .envoltorio { max-width: 720px; margin: 0 auto; padding: 0 24px 64px; }

  header {
    background: linear-gradient(135deg, var(--azul-claro) 0%, var(--azul-oscuro) 100%);
    color: #FFFFFF;
    padding: 40px 0;
    margin-bottom: 40px;
  }
  header .envoltorio { padding-bottom: 0; }
  .marca { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
  .marca svg { width: 32px; height: 32px; flex-shrink: 0; }
  .marca span { font-size: 17px; font-weight: 700; }
  h1 { font-size: 28px; font-weight: 800; margin: 0 0 8px; letter-spacing: -0.4px; }
  .actualizado { color: #E3F2FD; font-size: 14px; }

  .aviso {
    background: var(--azul-fondo);
    border-left: 4px solid var(--azul);
    border-radius: 10px;
    padding: 16px 20px;
    font-size: 15px;
    color: #1A3A5C;
    margin-bottom: 40px;
  }
  .aviso a { color: var(--azul-oscuro); }

  h2 {
    font-size: 21px;
    font-weight: 800;
    margin: 40px 0 14px;
    letter-spacing: -0.2px;
    border-bottom: 2px solid var(--borde);
    padding-bottom: 8px;
  }
  p { color: var(--texto-suave); margin: 0 0 14px; }
  strong { color: var(--texto); }
  ul, ol { color: var(--texto-suave); padding-left: 22px; }
  li { margin-bottom: 8px; }
  a { color: var(--azul); }

  table {
    width: 100%;
    border-collapse: collapse;
    margin: 16px 0;
    font-size: 14px;
  }
  th, td {
    text-align: left;
    padding: 10px 12px;
    border: 1px solid var(--borde);
    vertical-align: top;
  }
  th { background: #F7F9FC; font-weight: 700; color: var(--texto); }
  td { color: var(--texto-suave); }

  .contacto {
    background: #F7F9FC;
    border-radius: 12px;
    padding: 20px 24px;
    margin-top: 40px;
  }
  .contacto a { font-weight: 700; }

  footer {
    text-align: center;
    font-size: 14px;
    color: #94A3B8;
    padding: 24px;
  }

  @media (max-width: 480px) {
    body { font-size: 16px; }
    h1 { font-size: 24px; }
    table { font-size: 13px; }
  }
</style>
</head>
<body>

<header>
  <div class="envoltorio">
    <div class="marca">
      <svg viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <path fill="#FFFFFF" d="M54,23 C46,23 33,29 32,31 C31,32.5 31,34 31,36 L31,55 C31,73.5 40,85.5 54,91 C68,85.5 77,73.5 77,55 L77,36 C77,34 77,32.5 76,31 C75,29 62,23 54,23 Z"/>
        <path fill="#1976D2" d="M45,58l-9,-9l5,-5l4,4l17,-17l5,5l-22,22z"/>
      </svg>
      <span>Escudo Anti-Estafas</span>
    </div>
    <h1>Política de privacidad</h1>
    <div class="actualizado">Última actualización: 14 de julio de 2026</div>
  </div>
</header>

<div class="envoltorio">

  <div class="aviso">
    Aplica a la app Android "Escudo Anti-Estafas" (<code>com.escudoantiestafas.app</code>) y a su backend en
    <a href="https://escudo-anti-estafas.vercel.app">escudo-anti-estafas.vercel.app</a>. Responsable del tratamiento:
    Wilmer José Pérez Orozco, Barranquilla, Colombia. Contacto: <a href="mailto:wilmerjoseperezorozco@gmail.com">wilmerjoseperezorozco@gmail.com</a>.
  </div>

  <h2>1. Resumen en una frase</h2>
  <p>Escudo Anti-Estafas analiza, <strong>únicamente dentro de tu teléfono</strong>, si una llamada activa coincide con la llegada de un código de verificación por SMS; lo único que sale de tu dispositivo hacia nuestro servidor es un número telefónico, y solo en las situaciones concretas descritas abajo — nunca el contenido de tus llamadas o mensajes.</p>

  <h2>2. Qué datos recogemos, y de quién</h2>
  <table>
    <tr><th>Dato</th><th>¿De quién?</th><th>¿Cuándo viaja al servidor?</th></tr>
    <tr><td>Número de teléfono de una llamada entrante</td><td>Del <strong>llamante</strong> (puede ser un tercero, no necesariamente tú)</td><td>Automáticamente, en cada llamada entrante, para consultar si ese número ya fue reportado por la comunidad</td></tr>
    <tr><td>Número de teléfono que decides reportar</td><td>Del número que tú reportas manualmente</td><td>Solo cuando tocas explícitamente "Reportar este número"</td></tr>
    <tr><td>Dirección IP de tu conexión</td><td>Tuya</td><td>Automáticamente en cada solicitud al servidor, usada solo para limitar abusos</td></tr>
    <tr><td>Un conteo anónimo de que la app detectó el patrón de riesgo</td><td>Nadie en particular — es un evento, no un dato personal</td><td>Cada vez que la correlación llamada+OTP dispara la alerta, sin número, sin IP asociada al evento y sin identificador de dispositivo o usuario</td></tr>
  </table>
  <p>Sobre el número del llamante: es un dato personal de un tercero que no dio consentimiento directo a esta app. Lo tratamos bajo la misma base que usan apps de identificación de llamadas/spam: interés legítimo en la prevención de fraude, limitado estrictamente al número — nunca se asocia a nombre, foto, ni ningún otro dato de esa persona.</p>
  <p>Sobre el conteo anónimo de detecciones: existe para poder demostrar, con evidencia real y pública, qué tan efectiva es la protección. No se puede usar para identificarte ni reconstruir qué le pasó a un usuario específico — no guarda nada que te vincule al evento, ni siquiera tu IP.</p>

  <h2>3. Qué NUNCA se recoge</h2>
  <ul>
    <li>El contenido de tus llamadas (no se graba audio, no hay transcripción).</li>
    <li>El contenido de tus SMS — se analiza localmente y se descarta de inmediato; nunca se transmite ni se guarda.</li>
    <li>Tu lista de contactos.</li>
    <li>Tu ubicación.</li>
    <li>Datos bancarios o financieros.</li>
    <li>Ningún identificador publicitario, ni SDK de analítica o rastreo de terceros.</li>
    <li>Nombre, correo o cualquier dato de registro — no existe cuenta de usuario ni login.</li>
  </ul>

  <h2>4. Para qué usamos estos datos</h2>
  <ul>
    <li><strong>Reputación de números:</strong> calcular si varias personas distintas han reportado un número como sospechoso.</li>
    <li><strong>Prevención de abuso del propio sistema:</strong> la IP se usa solo para limitar solicitudes por IP.</li>
    <li><strong>Evidencia de impacto real:</strong> el conteo anónimo de detecciones permite mostrar, con datos reales, que la protección funciona.</li>
  </ul>
  <p>No usamos tus datos para publicidad, no los vendemos, y no construimos un perfil de tu comportamiento.</p>

  <h2>5. Con quién se comparten los datos</h2>
  <p>Usamos dos proveedores de infraestructura como <strong>encargados de tratamiento</strong>: <strong>Supabase</strong> (base de datos, Estados Unidos) y <strong>Vercel</strong> (hosting, Estados Unidos). Esto implica una transferencia internacional de datos fuera de Colombia. No compartimos tus datos con ningún otro tercero, no los vendemos.</p>

  <h2>6. Cuánto tiempo se conservan los datos</h2>
  <p>Los números reportados se conservan indefinidamente por ahora, porque son la base del sistema de reputación comunitaria. Puedes solicitar en cualquier momento que se elimine un número que tú mismo reportaste (sección 7).</p>

  <h2>7. Tus derechos (Ley 1581 de 2012 — Habeas Data)</h2>
  <p>Si eres residente en Colombia, tienes derecho a conocer, actualizar, rectificar, suprimir y revocar la autorización sobre tus datos, y a presentar quejas ante la Superintendencia de Industria y Comercio (SIC). Para ejercerlos, escribe a <a href="mailto:wilmerjoseperezorozco@gmail.com">wilmerjoseperezorozco@gmail.com</a> indicando el número involucrado y qué solicitas.</p>

  <h2>8. Seguridad</h2>
  <p>Todo el tráfico viaja cifrado (HTTPS/TLS). La base de datos tiene Row Level Security habilitado. No hay contraseñas que proteger porque no existe sistema de cuentas.</p>

  <h2>9. Menores de edad</h2>
  <p>La app no está dirigida a menores de edad y no solicita ni verifica edad de forma activa, porque no recolecta datos de identificación personal directa.</p>

  <h2>10. Sobre Apple / iOS</h2>
  <p>Hoy Escudo Anti-Estafas es una aplicación exclusivamente Android — la detección central depende de APIs que iOS no expone a apps de terceros. Esta política ya usa un lenguaje compatible con el estándar de Apple por si en el futuro existe una versión iOS.</p>

  <h2>11. Mapeo a "Data Safety" de Google Play</h2>
  <table>
    <tr><th>Categoría</th><th>¿Se recolecta?</th><th>¿Se comparte?</th></tr>
    <tr><td>Información personal (teléfono)</td><td>Sí</td><td>No</td></tr>
    <tr><td>Identificadores de dispositivo</td><td>No</td><td>No</td></tr>
    <tr><td>Ubicación</td><td>No</td><td>No</td></tr>
    <tr><td>Contactos</td><td>No</td><td>No</td></tr>
    <tr><td>Información financiera</td><td>No</td><td>No</td></tr>
    <tr><td>Actividad en la app / analítica</td><td>Sí, pero anónimo (solo conteo de detecciones)</td><td>No</td></tr>
    <tr><td>Registros de diagnóstico (IP)</td><td>Sí (automático)</td><td>No</td></tr>
  </table>

  <h2>12. Cambios a esta política</h2>
  <p>Si esta política cambia de forma material, se actualizará la fecha al inicio de esta página.</p>

  <div class="contacto">
    <strong>13. Contacto</strong><br>
    <a href="mailto:wilmerjoseperezorozco@gmail.com">wilmerjoseperezorozco@gmail.com</a> — preguntas sobre esta política, solicitudes de acceso/rectificación/supresión de datos, o cualquier inquietud de privacidad.
  </div>

</div>

<footer>
  <a href="/">Escudo Anti-Estafas</a> · <a href="${URL_REPO}">Código fuente</a>
</footer>

</body>
</html>
`;

module.exports = { paginaPrivacidadHtml };
