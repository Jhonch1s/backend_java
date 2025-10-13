(() => {
    'use strict';

    //estao global minimo
    const ctxPath = document.body.dataset?.ctx || ''; // si lo usás en tu SPA
    const endpointsBase =
        document.body.dataset.endpointBase ||
        `${ctxPath}/cliente/stats`; // ej: /cliente/stats

    // Podés setear "ym" externamente si lo necesitás:
    // <body data-ym="2025-10">
    const ym = document.body.dataset.ym || null;

    const state = {
        range: '4w',
        ejercicioId: null, // se setea cuando carguemos los ejercicios
    };

    // Utils
    const $ = (sel) => document.querySelector(sel);
    const $$ = (sel) => Array.from(document.querySelectorAll(sel));

    async function getJSON(url) {
        const r = await fetch(url, { credentials: 'same-origin' });
        if (!r.ok) throw new Error(`${r.status} ${r.statusText}`);
        return r.json();
    }

    // Formatters (por si querés bonito)
    const nf0 = new Intl.NumberFormat('es-UY', { maximumFractionDigits: 0 });
    function setText(id, val) {
        const el = document.getElementById(id);
        if (el) el.textContent = val;
    }

    // kpi's del mes
    async function fetchOverview() {
        const url = ym ? `${endpointsBase}/overview?ym=${ym}` : `${endpointsBase}/overview`;
        const data = await getJSON(url);
        if (!data.ok) throw new Error(data.error || 'overview error');
        return data;
    }

    function renderOverview({ dias, minTotales, minPromedio }) {
        setText('kpi-dias', nf0.format(dias ?? 0));
        setText('kpi-min-tot', nf0.format(minTotales ?? 0));
        setText('kpi-min-prom', nf0.format(minPromedio ?? 0));
    }

    // Streak semanal (placeholder, para usar despues)
    async function fetchWeeklyStreak() {
        // Ej futuro: `${endpointsBase}/weekly-streak?weeks=12`
        // return getJSON(url);
        return null;
    }

    function renderWeeklyStreak(data) {
        // data: { weeks:[{iso:'2025-W40', active:true}, ...], streak: 4 }
        // Por ahora, sólo limpiamos/persistimos el label si llega algo.
        if (!data) return;
        const label = $('#streak-weeks-label');
        if (label && typeof data.streak === 'number') {
            label.textContent = `${data.streak} semanas`;
        }
        const cont = $('#weeks-streak');
        if (cont && Array.isArray(data.weeks)) {
            // Pintar puntitos según weeks[].active
            const dots = cont.querySelectorAll('.week-dot');
            dots.forEach((dot, i) => {
                dot.classList.toggle('is-active', !!data.weeks[i]?.active);
            });
        }
    }

    // Interacciones de UI
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

    // ======= Render de paneles por ejercicio (placeholders) =======
    async function refreshExercisePanels() {
        // En próximos pasos:
        // - fetch overview del ejercicio (e1RM, best set, Δ, volumen 4w)
        // - fetch trend e1RM
        // - fetch volumen semanal + frecuencia
        // - fetch scatter carga–reps
        // - fetch PRs período
        // y luego renderizar en sus placeholders.
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
        }

        // 3) Binds de UI
        bindRangeChips();
        bindExerciseSelector();
    }

    // Correr cuando el documento esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
