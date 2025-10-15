(() => {

    'use strict';
    const ctx = document.body.dataset.ctx || '';
    const endpointsBase = document.body.dataset.endpointBase || `${ctx}/cliente/stats`;
    const ym = document.body.dataset.ym || null;

    const state = { range: '4w', ejercicioId: null };

    const $  = (sel) => document.querySelector(sel);
    const $$ = (sel) => Array.from(document.querySelectorAll(sel));
    const nf0 = new Intl.NumberFormat('es-UY', { maximumFractionDigits: 0 });

    async function getJSON(url) {
        const r = await fetch(url, { credentials: 'same-origin' });
        if (!r.ok) throw new Error(`${r.status} ${r.statusText}`);
        return r.json();
    }

    async function fetchOverview() {
        const url = ym ? `${endpointsBase}/overview?ym=${ym}` : `${endpointsBase}/overview`;
        const data = await getJSON(url);
        if (!data.ok) throw new Error(data.error || 'overview error');
        return data;
    }

    function setText(id, val) {
        const el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    function renderOverview({ dias, minTotales, minPromedio }) {
        setText('kpi-dias', nf0.format(dias ?? 0));
        setText('kpi-min-tot', nf0.format(minTotales ?? 0));
        setText('kpi-min-prom', nf0.format(minPromedio ?? 0));
    }

// racha semanal
    async function fetchWeeklyStreak() {
        const url = `${endpointsBase}/weekly-streak?weeks=12`;
        const j = await getJSON(url);
        if (!j.ok) throw new Error(j.error || 'weekly-streak error');
        return j;
    }
    function agregarFlamaAsset(dot) {
        if (!dot || dot.querySelector('.flame-wrap')) return;
        const wrap = document.createElement('span');
        wrap.className = 'flame-wrap';

        const img = new Image();
        img.src = `${ctx}/assets/img/streakFlame.webp?v=2`;
        img.className = 'flame-asset';
        img.alt = '';

        wrap.appendChild(img);
        dot.appendChild(wrap);
    }


    function quitarFlamaAsset(dot) {
        if (!dot) return;
        const el = dot.querySelector('img.flame-asset');
        if (el) el.remove();
    }

    function renderWeeklyStreak(data) {
        if (!data) return;

        // label con el total real
        let totalStreak = 0;
        if (data.streak !== undefined && data.streak !== null) {
            const n = parseInt(data.streak, 10);
            totalStreak = Number.isFinite(n) ? n : 0;
        } else if (Array.isArray(data.weeks)) {
            // fallback: calcula desde weeks vieja -> reciente
            let s = 0; const w = data.weeks;
            for (let i = w.length - 1; i >= 0; i--) { if (w[i]?.active) s++; else break; }
            totalStreak = s;
        }
        const label = document.getElementById('streak-weeks-label');
        if (label) label.textContent = `${totalStreak} semanas`;

        // pintar puntos de racha
        const cont  = document.getElementById('weeks-streak');
        if (!cont) return;
        const dots  = cont.querySelectorAll('.week-dot'); // 12 en total en el DOM, la ultima nunca se pinta
        const weeks = Array.isArray(data.weeks) ? data.weeks : [];

        // alinea a la derecha si llegan menos semanas que puntos, con weeksDots=11 queda el último libre siempre, cosa que de la sensasión de que se pueda seguir
        const offset = Math.max(0, dots.length - weeks.length);
        for (let i = 0; i < dots.length; i++) {
            const w = weeks[i - offset]; // undefined si sobran puntos
            dots[i].classList.toggle('is-active', !!(w && w.active));
            dots[i].classList.remove('is-reserve', 'is-current');
            quitarFlamaAsset(dots[i]); //quitamos algun asset previo si quedaba guardado de alguna manera de racha desactualizada
        }
        // reservamos SIEMPRE!! el último punto (semana que entra)
        // y además reforzar la reserva cuando la racha >= 12
        if (dots.length > 0) {
            const last = dots[dots.length - 1];
            last.classList.remove('is-active');
            last.classList.add('is-reserve');
        }

        // marcar el ultimo activo visible como "is-current", evitamos el que debe representar la sig semana
        const reservedIndex = dots.length - 1;
        let currentIdx = -1;
        for (let i = dots.length - 1; i >= 0; i--) {
            if (i === reservedIndex) continue;
            if (dots[i].classList.contains('is-active')) { currentIdx = i; break; }
        }
        if (currentIdx >= 0) {
            const currentDot = dots[currentIdx];
            currentDot.classList.add('is-current');
            agregarFlamaAsset(currentDot);
        }

    }


    // Interacciones de UI con chips de rangos
    function bindRangeChips() {
        const chips = $('#chips-range');
        if (!chips) return;
        chips.addEventListener('click', (e) => {
            const btn = e.target.closest('.chip');
            if (!btn) return;
            $$('#chips-range .chip').forEach((c) => c.classList.remove('is-active'));
            btn.classList.add('is-active');
            state.range = btn.dataset.range || '4w';
            // Futuro: refrescar paneles dependientes del rango (ejercicio)
            refreshExercisePanels();
        });
    }

    function bindExerciseSelector() {
        const sel = $('#sel-ej');
        if (!sel) return;
        sel.addEventListener('change', (e) => {
            state.ejercicioId = e.target.value || null;
            refreshExercisePanels();
        });
    }

    function refreshExercisePanels() {
        if (!state.ejercicioId) return;
        // Por ahora solo actualizamos los mini-KPIs
        fetchMiniKpis(state.ejercicioId)
            .then(renderMiniKpis)
            .catch(() => console.warn('Error al cargar mini-KPIs'));
    }

    //ejercicios del cliente
    async function fetchExercises() {
        const base = document.body.dataset.endpointBase; // ej: /app/cliente/stats
        const res = await fetch(`${base}/exercises`, { headers: { 'Accept': 'application/json' } });
        if (!res.ok) return { ok: false, items: [] };
        return res.json();
    }
    function renderExerciseSelect(items) {
        const sel = document.getElementById('sel-ej');
        sel.innerHTML = ''; // limpiamo
        if (!items?.length) {
            document.getElementById('empty-exercise')?.classList.remove('u-hide');
            return;
        }
        // opciones
        const frag = document.createDocumentFragment();
        for (const it of items) {
            const opt = document.createElement('option');
            opt.value = it.id;
            opt.textContent = it.nombre;
            frag.appendChild(opt);
        }
        sel.appendChild(frag);

        state.ejercicioId = sel.value;
        // disparamos evento para que los gráficos carguen el primer ejercicio
        document.dispatchEvent(new CustomEvent('exercise:change', { detail: { ejercicioId: sel.value } }));
    }

    function bindExerciseSelectChange() {
        const sel = document.getElementById('sel-ej');
        sel.addEventListener('change', () => {
            document.dispatchEvent(new CustomEvent('exercise:change', { detail: { ejercicioId: sel.value } }));
        });
    }
    // Mini kpis del ejercicio
    async function fetchMiniKpis(ejercicioId) {
        const base = document.body.dataset.endpointBase; // /cliente/stats en teoria
        const url = `${base}/exercise/mini?ej=${encodeURIComponent(ejercicioId)}`;
        const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
        if (!res.ok) return { ok:false };
        return res.json();
    }
    //fallback
    function n2(v, fd='–'){ return (v==null) ? fd : Number(v).toLocaleString('es-UY', {maximumFractionDigits: 2}); }

    function renderMiniKpis(data){
        // e1RM kg
        document.getElementById('mk-e1rm').textContent = data.ok && data.bestE1rm!=null
            ? `${n2(data.bestE1rm)} kg`
            : '–';

        // Mejor set kg×reps
        const best = data.ok ? data.bestSet : null;
        document.getElementById('mk-bestset').textContent = best
            ? `${n2(best.kg)}×${best.reps} kg·reps`
            : '–';

        // Δ e1RM kg, con signo
        const deltaEl = document.getElementById('mk-delta');
        if (data.ok && data.deltaE1rm != null){
            const val = Number(data.deltaE1rm);
            deltaEl.textContent = `${val>0? '+' : ''}${val.toFixed(2)} kg`;
            deltaEl.classList.toggle('is-up', val>0);
            deltaEl.classList.toggle('is-down', val<0);
        } else {
            deltaEl.textContent = '–';
            deltaEl.classList.remove('is-up','is-down');
        }

        // Volumen 4 sem kg x reps
        document.getElementById('mk-vol4w').textContent = data.ok && data.vol4w!=null
            ? `${n2(data.vol4w)} kg·reps`
            : '–';
    }


    document.addEventListener('exercise:change', async (e) => {
        const id = e.detail.ejercicioId;
        const data = await fetchMiniKpis(id);
        renderMiniKpis(data);

        // refrescar charts con el rango actual
        document.dispatchEvent(new CustomEvent('charts:refresh', {
            detail: { ejercicioId: id, range: state.range || '4w' }
        }));
    });


    // ayudas (i) centrada en pantalla
    const HELP_TEXT = {
        bestE1rm: '<strong>Mejor e1RM</strong><br>Estimación de tu 1RM con fórmula de Epley: peso × (1 + repeticiones/30).',
        bestSet:  '<strong>Mejor marca (kg×reps)</strong><br>Serie con mayor volumen total (peso × repeticiones).',
        delta:    '<strong>Δ e1RM (ventana)</strong><br>Cambio promedio del e1RM entre las últimas 4 semanas y las 4 previas.',
        vol4w:    '<strong>Volumen 4 sem</strong><br>Suma de todos los pesos levantados (kg×reps) en los últimos 28 días.'
    };

    function openInfoModal(key) {
        const overlay = document.createElement('div');
        overlay.className = 'info-overlay';

        const modal = document.createElement('div');
        modal.className = 'info-modal';
        modal.innerHTML = `
    <button class="close-info" aria-label="Cerrar">×</button>
    ${HELP_TEXT[key] || '<strong>Sin descripción</strong>'}
  `;

        overlay.appendChild(modal);
        document.body.appendChild(overlay);

        const close = () => overlay.remove();
        overlay.addEventListener('click', e => { if (e.target === overlay) close(); });
        modal.querySelector('.close-info').addEventListener('click', close);
    }

    document.addEventListener('click', e => {
        const btn = e.target.closest('.info-dot');
        if (btn) {
            e.preventDefault();
            openInfoModal(btn.dataset.help);
        }
    });

    //graficas!! D:
    let chartE1RM, chartVolume, chartScatter;

    function initCharts() {
        console.log("📊 initCharts() lanzado");

        // Tendencia e1RM
        const ph1 = document.getElementById('ph-e1rm');
        if (ph1) {
            const canvas = document.createElement('canvas');
            canvas.id = 'chart-e1rm';
            ph1.replaceWith(canvas);
            const ctx = canvas.getContext('2d');
            chartE1RM = new Chart(ctx, {
                type: 'line',
                data: { labels: [], datasets: [{
                        label: 'e1RM (kg)',
                        data: [],
                        borderColor: '#ff9800',
                        tension: .3,
                        pointRadius: 3
                    }]},
                options: { plugins:{legend:{display:false}}, scales:{y:{beginAtZero:true}} }
            });
        }

        // Volumen semanal
        const ph2 = document.getElementById('ph-volume');
        if (ph2) {
            const canvas = document.createElement('canvas');
            canvas.id = 'chart-volume-weekly';
            ph2.replaceWith(canvas);
            const ctx = canvas.getContext('2d');
            chartVolume = new Chart(ctx, {
                type: 'bar',
                data: { labels: [], datasets: [{
                        label: 'Volumen (kg·reps)',
                        data: [],
                        backgroundColor: '#9724A6'
                    }]},
                options: { plugins:{legend:{display:false}}, scales:{y:{beginAtZero:true}} }
            });
        }

        // Scatter carga–reps
        const ph3 = document.getElementById('ph-scatter');
        if (ph3) {
            const canvas = document.createElement('canvas');
            canvas.id = 'chart-scatter';
            ph3.replaceWith(canvas);
            const ctx = canvas.getContext('2d');
            chartScatter = new Chart(ctx, {
                type: 'scatter',
                data: { datasets: [{
                        label: 'Carga × Reps',
                        data: [],
                        backgroundColor: '#D92BCD'
                    }]},
                options: {
                    plugins:{legend:{display:false}},
                    scales:{x:{title:{text:'Reps',display:true}},
                        y:{title:{text:'Carga (kg)',display:true},beginAtZero:true}}
                }
            });
        }
    }


    document.addEventListener('charts:refresh', async e => {
        const { ejercicioId, range } = e.detail;
        const base = document.body.dataset.endpointBase;

        // endpoint que devuelva JSON {}
        const res = await fetch(`${base}/exercise/series?ej=${ejercicioId}&r=${range}`);
        if (!res.ok) return;
        const data = await res.json();
        if (!data.ok) return;

        // actualizar los 3 charts
        updateChart(chartE1RM, data.series.map(x => x.label), data.series.map(x => x.e1rm));
        updateChart(chartVolume, data.volumeWeekly.map(x => x.weekLabel), data.volumeWeekly.map(x => x.volume));
        updateScatter(chartScatter, data.scatter.map(x => ({x:x.reps, y:x.kg})));
    });

    function updateChart(chart, labels, values){
        chart.data.labels = labels;
        chart.data.datasets[0].data = values;
        chart.update();
    }
    function updateScatter(chart, points){
        chart.data.datasets[0].data = points;
        chart.update();
    }

    // init
    async function init() {
        try {
            // 1) KPIs overview del mes
            const ov = await fetchOverview();
            renderOverview(ov);
        } catch (err) {
            console.error('Error overview:', err);
            renderOverview({ dias: 0, minTotales: 0, minPromedio: 0 });
        }

        try {
            // 2) Racha semanal (cuando tengamos endpoint)
            const streak = await fetchWeeklyStreak();
            renderWeeklyStreak(streak);
        } catch (err) {
            console.warn('Streak no disponible aún:', err);
            const label = document.getElementById('streak-weeks-label');
            if (label) label.textContent = '– semanas';
        }

        // 3) Binds de UI
        bindRangeChips();
        bindExerciseSelector();

        // 4) select de ejercicios
        const data = await fetchExercises();
        if (data.ok) renderExerciseSelect(data.items);
        bindExerciseSelectChange();



    }

    // Correr cuando el documento esté listo
    function boot() {
        init();// KPIs, streak, selects, binds...
        initCharts();  // crea los canvas + instancias Chart.js
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            init();
            initCharts();
        });
    } else {
        boot();
    }
    //comenzamos con los chips y los rangos
    const rangeSelector = document.getElementById('rangeSelector');
    let currentRange = '4w';

    rangeSelector?.addEventListener('click', (e) => {
        const btn = e.target.closest('.chip');
        if (!btn) return;

        rangeSelector.querySelectorAll('.chip').forEach(c => c.classList.remove('is-active'));
        btn.classList.add('is-active');

        currentRange = btn.dataset.range;
        state.range = currentRange;// <-- guarda en state

        // si hay ejercicio seleccionado, refrescar charts
        if (state.ejercicioId) {
            document.dispatchEvent(new CustomEvent('charts:refresh', {
                detail: { ejercicioId: state.ejercicioId, range: currentRange }
            }));
        }
    });


})()