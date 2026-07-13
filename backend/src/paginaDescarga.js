const URL_DESCARGA_APK =
  'https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas/releases/latest/download/app-release.apk';
const URL_RELEASES =
  'https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas/releases';
const URL_REPO = 'https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas';
const URL_PRIVACIDAD =
  'https://github.com/wilmerjoseperezorozco-dev/escudo-anti-estafas/blob/main/docs/politica-privacidad.md';

/**
 * Landing pública de descarga, servida directamente por el backend en "/".
 * El botón de descarga usa el enlace "latest/download" de GitHub: apunta
 * siempre a la versión más reciente sin que esta página necesite tocarse
 * en cada release (mientras el nombre del asset siga siendo
 * "app-release.apk", que es la convención ya establecida).
 */
const paginaDescargaHtml = `<!doctype html>
<html lang="es">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Escudo Anti-Estafas — Protege tu WhatsApp del robo de código</title>
<meta name="description" content="Detecta en tiempo real el momento exacto del robo de tu código de WhatsApp. Gratis, de código abierto, 100% del análisis ocurre en tu teléfono.">
<style>
  :root {
    --azul-oscuro: #0D47A1;
    --azul: #1565C0;
    --azul-claro: #1E88E5;
    --azul-fondo: #E3F2FD;
    --texto: #1A2027;
    --texto-suave: #4A5568;
    --ambar: #F57C00;
    --ambar-fondo: #FFF3E0;
    --borde: #E2E8F0;
  }
  * { box-sizing: border-box; }
  html { scroll-behavior: smooth; }
  body {
    margin: 0;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    color: var(--texto);
    line-height: 1.55;
    background: #FFFFFF;
    font-size: 18px;
  }
  .envoltorio { max-width: 720px; margin: 0 auto; padding: 0 24px; }

  header.hero {
    background: linear-gradient(135deg, var(--azul-claro) 0%, var(--azul-oscuro) 100%);
    color: #FFFFFF;
    padding: 56px 0 64px;
    text-align: center;
  }
  .marca { display: flex; align-items: center; justify-content: center; gap: 12px; margin-bottom: 28px; }
  .marca svg { width: 44px; height: 44px; flex-shrink: 0; }
  .marca span { font-size: 20px; font-weight: 700; letter-spacing: -0.2px; }
  h1 {
    font-size: 34px;
    font-weight: 800;
    line-height: 1.2;
    margin: 0 0 16px;
    letter-spacing: -0.5px;
  }
  .hero p.subtitulo {
    font-size: 19px;
    color: #E3F2FD;
    max-width: 480px;
    margin: 0 auto 32px;
  }
  .boton-descarga {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    background: #FFFFFF;
    color: var(--azul-oscuro);
    font-size: 19px;
    font-weight: 700;
    padding: 18px 32px;
    border-radius: 14px;
    text-decoration: none;
    box-shadow: 0 8px 24px rgba(0,0,0,0.18);
    transition: transform 0.15s ease, box-shadow 0.15s ease;
  }
  .boton-descarga:active { transform: scale(0.97); }
  .boton-descarga svg { width: 22px; height: 22px; }
  .hero-nota {
    margin-top: 16px;
    font-size: 14px;
    color: #BBDEFB;
  }
  .hero-nota a { color: #FFFFFF; }

  section { padding: 48px 0; }
  section.gris { background: #F7F9FC; }

  .historia {
    background: var(--ambar-fondo);
    border-left: 4px solid var(--ambar);
    border-radius: 12px;
    padding: 22px 24px;
    font-size: 17px;
    color: #5D4037;
  }
  .historia strong { color: #E65100; }

  h2 {
    font-size: 25px;
    font-weight: 800;
    margin: 0 0 24px;
    letter-spacing: -0.3px;
  }

  .rejilla-caracteristicas {
    display: grid;
    gap: 20px;
  }
  .caracteristica {
    display: flex;
    gap: 16px;
    align-items: flex-start;
  }
  .caracteristica .icono {
    flex-shrink: 0;
    width: 44px; height: 44px;
    border-radius: 12px;
    background: var(--azul-fondo);
    display: flex; align-items: center; justify-content: center;
  }
  .caracteristica .icono svg { width: 22px; height: 22px; color: var(--azul); }
  .caracteristica h3 { margin: 0 0 4px; font-size: 17px; font-weight: 700; }
  .caracteristica p { margin: 0; color: var(--texto-suave); font-size: 16px; }

  .pasos { counter-reset: paso; display: grid; gap: 18px; }
  .paso {
    display: flex; gap: 16px; align-items: flex-start;
    background: #FFFFFF;
    border: 1px solid var(--borde);
    border-radius: 14px;
    padding: 18px 20px;
  }
  .paso .numero {
    counter-increment: paso;
    flex-shrink: 0;
    width: 32px; height: 32px;
    border-radius: 50%;
    background: var(--azul-oscuro);
    color: #FFFFFF;
    display: flex; align-items: center; justify-content: center;
    font-weight: 700; font-size: 15px;
  }
  .paso .numero::before { content: counter(paso); }
  .paso h3 { margin: 0 0 4px; font-size: 16px; font-weight: 700; }
  .paso p { margin: 0; color: var(--texto-suave); font-size: 15px; }

  .confianza {
    display: grid;
    gap: 14px;
  }
  .confianza li {
    display: flex; gap: 10px; align-items: flex-start;
    list-style: none; font-size: 16px; color: var(--texto-suave);
  }
  .confianza li svg { flex-shrink: 0; width: 20px; height: 20px; color: #2E7D32; margin-top: 2px; }
  .confianza ul { padding: 0; margin: 0; }
  .enlaces-confianza { margin-top: 20px; font-size: 15px; }
  .enlaces-confianza a { color: var(--azul); text-decoration: none; font-weight: 600; }
  .enlaces-confianza a:hover { text-decoration: underline; }

  .cta-final { text-align: center; padding: 56px 24px; }
  .cta-final .boton-descarga { background: var(--azul-oscuro); color: #FFFFFF; }

  footer {
    padding: 32px 24px 40px;
    text-align: center;
    font-size: 14px;
    color: #94A3B8;
    border-top: 1px solid var(--borde);
  }
  footer a { color: #64748B; }

  @media (max-width: 480px) {
    body { font-size: 17px; }
    h1 { font-size: 28px; }
    header.hero { padding: 44px 0 52px; }
  }
</style>
</head>
<body>

<header class="hero">
  <div class="envoltorio">
    <div class="marca">
      <svg viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
        <path fill="#FFFFFF" d="M54,23 C46,23 33,29 32,31 C31,32.5 31,34 31,36 L31,55 C31,73.5 40,85.5 54,91 C68,85.5 77,73.5 77,55 L77,36 C77,34 77,32.5 76,31 C75,29 62,23 54,23 Z"/>
        <path fill="#1976D2" d="M45,58l-9,-9l5,-5l4,4l17,-17l5,5l-22,22z"/>
      </svg>
      <span>Escudo Anti-Estafas</span>
    </div>
    <h1>No compartas tu código de WhatsApp sin saberlo</h1>
    <p class="subtitulo">Detecta en tiempo real el momento exacto en que te intentan robar el código de verificación — y te avisa antes de que sea tarde.</p>
    <a class="boton-descarga" href="${URL_DESCARGA_APK}">
      <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><path d="M12 3v12m0 0l-4-4m4 4l4-4M5 21h14" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>
      Descargar gratis para Android
    </a>
    <div class="hero-nota">APK directo · sin Play Store · <a href="${URL_REPO}">código abierto</a></div>
  </div>
</header>

<section>
  <div class="envoltorio">
    <div class="historia">
      Esta app nació después de que un estafador casi le roba el WhatsApp a una señora de 54 años en Colombia — llamándola, presionándola para que "tomara lápiz y papel" mientras el código de verificación le llegaba por SMS. <strong>Es el mismo engaño que reciben miles de personas cada día.</strong>
    </div>
  </div>
</section>

<section class="gris">
  <div class="envoltorio">
    <h2>Cómo te protege</h2>
    <div class="rejilla-caracteristicas">
      <div class="caracteristica">
        <div class="icono"><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/><path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg></div>
        <div>
          <h3>Detecta el momento exacto del riesgo</h3>
          <p>Cuando una llamada activa coincide con la llegada de un código de verificación, te muestra una alerta a pantalla completa imposible de ignorar.</p>
        </div>
      </div>
      <div class="caracteristica">
        <div class="icono"><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><rect x="5" y="11" width="14" height="9" rx="2" stroke="currentColor" stroke-width="2"/><path d="M8 11V8a4 4 0 118 0v3" stroke="currentColor" stroke-width="2"/></svg></div>
        <div>
          <h3>Todo el análisis ocurre en tu teléfono</h3>
          <p>El contenido de tus llamadas y mensajes nunca sale de tu dispositivo. Nunca se graba, nunca se transmite, nunca se guarda.</p>
        </div>
      </div>
      <div class="caracteristica">
        <div class="icono"><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M3 12s3.5-7 9-7 9 7 9 7-3.5 7-9 7-9-7-9-7z" stroke="currentColor" stroke-width="2"/><circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/></svg></div>
        <div>
          <h3>Reportes comunitarios</h3>
          <p>Reporta un número sospechoso con un toque para avisar a otras personas antes de que contesten esa llamada.</p>
        </div>
      </div>
    </div>
  </div>
</section>

<section>
  <div class="envoltorio">
    <h2>Cómo instalarla</h2>
    <div class="pasos">
      <div class="paso">
        <div class="numero"></div>
        <div>
          <h3>Descarga el archivo</h3>
          <p>Toca el botón "Descargar gratis para Android" arriba. El archivo se guarda en tu carpeta de Descargas.</p>
        </div>
      </div>
      <div class="paso">
        <div class="numero"></div>
        <div>
          <h3>Permite instalar desde esta fuente</h3>
          <p>Tu teléfono va a preguntarte si confías en instalar esta app fuera de Play Store. Toca "Configuración" y luego activa "Permitir esta fuente" — es normal, pasa porque esta app aún no está en Play Store.</p>
        </div>
      </div>
      <div class="paso">
        <div class="numero"></div>
        <div>
          <h3>Abre e instala</h3>
          <p>Vuelve a abrir el archivo descargado, toca "Instalar" y listo. Al abrir la app, sigue los tres pasos de activación.</p>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="gris">
  <div class="envoltorio">
    <h2>Por qué puedes confiar</h2>
    <ul class="confianza">
      <li><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg> Es gratis, sin anuncios, y no pide crear ninguna cuenta.</li>
      <li><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg> Es de código abierto — cualquiera puede revisar exactamente qué hace, línea por línea.</li>
      <li><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg> Nunca accede a tus contactos, tu ubicación, ni tus fotos.</li>
      <li><svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M20 6L9 17l-5-5" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/></svg> Desarrollada de forma independiente en Barranquilla, Colombia.</li>
    </ul>
    <div class="enlaces-confianza">
      <a href="${URL_PRIVACIDAD}">Leer la política de privacidad completa</a> · <a href="${URL_REPO}">Ver el código fuente</a> · <a href="${URL_RELEASES}">Ver todas las versiones</a>
    </div>
  </div>
</section>

<div class="cta-final">
  <a class="boton-descarga" href="${URL_DESCARGA_APK}">
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true"><path d="M12 3v12m0 0l-4-4m4 4l4-4M5 21h14" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg>
    Descargar gratis para Android
  </a>
</div>

<footer>
  Escudo Anti-Estafas — proyecto independiente y de código abierto.<br>
  Contacto: <a href="mailto:wilmerjoseperezorozco@gmail.com">wilmerjoseperezorozco@gmail.com</a> ·
  <a href="${URL_REPO}">GitHub</a>
</footer>

</body>
</html>
`;

module.exports = { paginaDescargaHtml, URL_DESCARGA_APK, URL_RELEASES };
