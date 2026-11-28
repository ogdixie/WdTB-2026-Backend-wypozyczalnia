/* Padający śnieg w tle dokumentacji (sezonowy dodatek, od 15 listopada do 31 stycznia).
   Płatki spadają nad treścią i usypują się w zaspę na dole ekranu: po ok. 3–4 minutach sięga do napisu w stopce i wtedy przestaje padać.
   Poziom zaspy przechodzi na kolejną stronę dokumentacji w tej samej karcie.
   Płótno nie przechwytuje kliknięć. Przycisk „Śnieg” pod menu, wyrównany do „Start”, wyłącza i włącza efekt; wybór jest zapamiętany
   w przeglądarce. Przy systemowym „ogranicz ruch” śnieg się nie pokazuje. */
(function () {
  var now = new Date(), m = now.getMonth() + 1, d = now.getDate();
  var season = (m === 11 && d >= 15) || m === 12 || m === 1;
  if (!season) return;
  if (window.matchMedia && matchMedia('(prefers-reduced-motion: reduce)').matches) return;

  var KEY = 'wdtb-snow';
  var on = true;
  try { on = localStorage.getItem(KEY) !== 'off'; } catch (e) {}

  var canvas = document.createElement('canvas');
  canvas.setAttribute('aria-hidden', 'true');
  canvas.style.cssText = 'position:fixed;inset:0;width:100%;height:100%;pointer-events:none;z-index:50';
  document.body.appendChild(canvas);
  var ctx = canvas.getContext('2d');

  // przycisk w nowym wierszu menu, wyrównany do lewej pod „Start” (wygląd jak „Motyw”); bez menu: w rogu ekranu
  var button = document.createElement('button');
  button.type = 'button';
  var nav = document.querySelector('nav.jump');
  if (nav) {
    var lineBreak = document.createElement('span');
    lineBreak.setAttribute('aria-hidden', 'true');
    lineBreak.style.cssText = 'flex-basis:100%;height:0';
    button.className = 'theme-toggle';
    nav.appendChild(lineBreak);
    nav.appendChild(button);
  } else {
    button.style.cssText = 'position:fixed;right:14px;bottom:14px;z-index:51;padding:6px 10px;border-radius:8px;'
      + 'border:1px solid rgba(127,127,127,.35);background:rgba(127,127,127,.12);cursor:pointer';
    document.body.appendChild(button);
  }

  var COLUMN = 4;          // szerokość kolumny zaspy w pikselach
  var MAX_DRIFT = 46;      // najwyższa zaspa; po załadowaniu strony: do napisu „Wprowadzenie do technologii backendowych” w stopce
  var FILL_SECONDS = 180;   // po tylu sekundach zaspa sięga do napisu w stopce (przy 60 klatkach na sekundę)
  var deposit = 0.9;       // ile śniegu dosypuje jeden płatek na piksel promienia; liczone w resize()
  var FILL_KEY = 'wdtb-snow-fill';
  var flakes = [], drift = [], w = 0, h = 0, dpr = 1, running = false;
  var FULL = 0.97;         // przy takim wypełnieniu zaspy przestaje padać: spadają tylko płatki, które już lecą
  var stopping = false, frames = 0;

  function resize() {
    dpr = window.devicePixelRatio || 1;
    w = window.innerWidth; h = window.innerHeight;
    canvas.width = w * dpr; canvas.height = h * dpr;
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    var count = Math.min(320, Math.round(w * h / 5000));
    if (!stopping) {
      while (flakes.length < count) flakes.push(flake(true));
      flakes.length = count;
    }
    MAX_DRIFT = driftLimit();
    var columns = Math.ceil(w / COLUMN) + 1;
    while (drift.length < columns) drift.push(0);
    drift.length = columns;
    // średnio płatek spada h / 0.95 klatek, więc co sekundę ląduje ok. count * 0.95 * 60 / h płatków o promieniu ok. 2,3;
    // dobieramy dosypkę tak, żeby cała szerokość urosła do MAX_DRIFT w FILL_SECONDS
    var landingsPerSecond = Math.max(1, count * 0.95 * 60 / h);
    deposit = (MAX_DRIFT * columns) / (landingsPerSecond * FILL_SECONDS * 2.3);
  }

  // zaspa może sięgnąć do napisu z nazwą przedmiotu w stopce (pierwszy span), ale go nie przykrywa:
  // limit to odległość od dołu napisu do końca strony, bo po przewinięciu na koniec ten koniec jest dołem ekranu
  function driftLimit() {
    var title = document.querySelector('footer span');
    if (!title) return 46;
    var pageEnd = Math.max(document.documentElement.scrollHeight, document.body.scrollHeight);
    var limit = pageEnd - (title.getBoundingClientRect().bottom + window.scrollY) - 2;
    return Math.max(24, Math.min(limit, h * 0.25));
  }

  function flake(anywhere) {
    return {
      x: Math.random() * w,
      y: anywhere ? Math.random() * h * 0.8 : -10,
      r: 1 + Math.random() * 2.6,
      vy: 0.4 + Math.random() * 1.1,
      sway: Math.random() * Math.PI * 2,
      swaySpeed: 0.005 + Math.random() * 0.015,
      alpha: 0.45 + Math.random() * 0.5
    };
  }

  function dark() {
    var t = document.documentElement.getAttribute('data-theme');
    if (t) return t === 'dark';
    return window.matchMedia && matchMedia('(prefers-color-scheme: dark)').matches;
  }

  // płatek dolatuje do zaspy: dosypuje śnieg w swojej kolumnie, nadmiar zsypuje się na boki
  function land(x, r) {
    var i = Math.max(0, Math.min(drift.length - 1, Math.round(x / COLUMN)));
    drift[i] = Math.min(MAX_DRIFT, drift[i] + r * deposit);
    for (var pass = 0; pass < 2; pass++) {
      for (var k = Math.max(1, i - 6); k < Math.min(drift.length - 1, i + 7); k++) {
        var left = drift[k] - drift[k - 1], right = drift[k] - drift[k + 1];
        if (left > 1.5 && drift[k - 1] < MAX_DRIFT) { drift[k] -= left / 3; drift[k - 1] += left / 3; }
        if (right > 1.5 && drift[k + 1] < MAX_DRIFT) { drift[k] -= right / 3; drift[k + 1] += right / 3; }
      }
    }
  }

  function drawDrift(isDark) {
    ctx.beginPath();
    ctx.moveTo(0, h);
    for (var i = 0; i < drift.length; i++) {
      ctx.lineTo(i * COLUMN, h - drift[i]);
    }
    ctx.lineTo(w, h);
    ctx.closePath();
    ctx.fillStyle = isDark ? 'rgba(240,246,255,.92)' : 'rgba(236,243,250,.95)';
    ctx.fill();
    // na jasnym tle zaspa potrzebuje konturu, żeby było ją widać
    if (!isDark) {
      ctx.strokeStyle = 'rgba(150,180,215,.8)';
      ctx.lineWidth = 1;
      ctx.stroke();
    }
  }

  function filled() {
    var sum = 0;
    for (var i = 0; i < drift.length; i++) sum += drift[i];
    return drift.length ? sum / drift.length / MAX_DRIFT : 0;
  }

  // sama zaspa, bez płatków: po zasypaniu albo po zmianie rozmiaru okna, gdy animacja już stoi
  function drawStill() {
    ctx.clearRect(0, 0, w, h);
    drawDrift(dark());
  }

  function frame() {
    if (!running) return;
    if (!stopping && ++frames % 60 === 0 && filled() >= FULL) stopping = true;
    var isDark = dark();
    ctx.clearRect(0, 0, w, h);
    // na jasnym tle biały śnieg byłby niewidoczny: wtedy chłodny błękit
    var color = isDark ? '255,255,255' : '150,180,215';
    for (var i = 0; i < flakes.length; i++) {
      var f = flakes[i];
      f.sway += f.swaySpeed;
      f.y += f.vy;
      f.x += Math.sin(f.sway) * 0.5;
      if (f.x > w + 10) f.x = -10; else if (f.x < -10) f.x = w + 10;
      var column = Math.max(0, Math.min(drift.length - 1, Math.round(f.x / COLUMN)));
      if (f.y + f.r >= h - drift[column]) {
        land(f.x, f.r);
        if (stopping) { flakes.splice(i, 1); i--; } else { flakes[i] = flake(false); }
        continue;
      }
      ctx.beginPath();
      ctx.arc(f.x, f.y, f.r, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(' + color + ',' + f.alpha + ')';
      ctx.fill();
    }
    drawDrift(isDark);
    // zasypane i wszystkie płatki leżą: koniec animacji, zaspa zostaje na ekranie
    if (stopping && !flakes.length) { running = false; return; }
    requestAnimationFrame(frame);
  }

  function set(value) {
    on = value;
    try { localStorage.setItem(KEY, on ? 'on' : 'off'); } catch (e) {}
    button.textContent = on ? '❄ Śnieg: wł.' : '❄ Śnieg: wył.';
    button.title = on ? 'Wyłącz padający śnieg' : 'Włącz padający śnieg';
    button.setAttribute('aria-label', button.title);
    button.setAttribute('aria-pressed', on ? 'true' : 'false');
    canvas.style.display = on ? 'block' : 'none';
    if (on && !running && !stopping) {
      if (!flakes.length) resize();
      running = true;
      requestAnimationFrame(frame);
    }
    if (on && stopping && !running) drawStill();
    if (!on) {
      running = false;
      stopping = false;
      for (var i = 0; i < drift.length; i++) drift[i] = 0;
      try { sessionStorage.removeItem(FILL_KEY); } catch (e) {}
    }
  }

  button.addEventListener('click', function () { set(!on); });
  function relayout() {
    resize();
    if (on && !running) drawStill();
  }
  window.addEventListener('resize', relayout);
  window.addEventListener('load', relayout);

  // poziom zaspy przechodzi na kolejną stronę dokumentacji w tej samej karcie (sessionStorage)
  function saveFill() {
    if (!on || !drift.length) return;
    var sum = 0;
    for (var i = 0; i < drift.length; i++) sum += drift[i];
    try { sessionStorage.setItem(FILL_KEY, String(sum / drift.length / MAX_DRIFT)); } catch (e) {}
  }
  function restoreFill() {
    var fill = 0;
    try { fill = parseFloat(sessionStorage.getItem(FILL_KEY)) || 0; } catch (e) {}
    fill = Math.max(0, Math.min(1, fill));
    for (var i = 0; i < drift.length; i++) {
      // pagórki zamiast płaskiej warstwy
      var wave = 0.85 + 0.15 * Math.sin(i * 0.07) + 0.08 * Math.sin(i * 0.23 + 1.3);
      drift[i] = Math.min(MAX_DRIFT, fill * MAX_DRIFT * wave);
    }
    // na poprzedniej stronie już zasypało: od razu sama zaspa, bez padania
    if (fill >= FULL) {
      for (var k = 0; k < drift.length; k++) drift[k] = MAX_DRIFT;
      stopping = true;
      flakes.length = 0;
    }
  }
  window.addEventListener('pagehide', saveFill);
  // zmiana motywu, gdy animacja już stoi: przerysuj zaspę w nowych kolorach
  document.addEventListener('click', function (e) {
    if (e.target !== button && e.target.closest && e.target.closest('.theme-toggle') && on && !running) setTimeout(drawStill, 0);
  });
  resize();
  restoreFill();
  set(on);
})();
