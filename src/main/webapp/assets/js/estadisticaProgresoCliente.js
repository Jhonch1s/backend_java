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
            const label = document.getElementById('streak-weeks-label');
            if (label) label.textContent = '– semanas';
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
})()