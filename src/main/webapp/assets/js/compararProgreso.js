(() => {
    'use strict';

    // ===== Helpers de DOM / entorno =====
    const $ = (s) => document.querySelector(s);
    const ctxPath = document.body.dataset.ctx || '';
    const token   = document.body.dataset.token || '';
    const ejId    = document.body.dataset.ej;
    const range   = document.body.dataset.range || '4w';

    // URLs: usamos el servlet de compare (mismo endpoint tanto HTML como JSON)
    const compareJsonUrl = () =>
        `${ctxPath}/cliente/stats/compare?t=${encodeURIComponent(token)}&ej=${encodeURIComponent(ejId)}&r=${encodeURIComponent(range)}`;

    // ===== Chart instances =====
    let chartE1RM = null, chartVolume = null, chartScatter = null;

    // ===== Utils de tiempo/semana =====
    function weekKey(dateStr) {
        const d = new Date(dateStr + 'T00:00:00');
        const year = d.getFullYear();
        const oneJan = new Date(year, 0, 1);
        const day = Math.floor((d - oneJan) / 86400000) + 1;
        const wk = Math.max(1, Math.floor((day + ((d.getDay() + 6) % 7)) / 7));
        return `${year}-W${String(wk).padStart(2, '0')}`;
    }

    // e1RM estimado (Epley)
    const e1rm = (kg, reps) => (kg == null || !isFinite(kg) || reps <= 0) ? null : +(kg * (1 + reps / 30)).toFixed(2);
    const byDate = (a, b) => (a.fecha < b.fecha ? -1 : (a.fecha > b.fecha ? 1 : 0));

    // Agrupar por semana: máximo e1RM semanal
    function weeklyBestE1rm(rows) {
        const M = new Map();
        for (const r of rows) {
            if (r.kg == null || !isFinite(r.kg) || r.reps <= 0) continue;
            const key = weekKey(r.fecha);
            const val = e1rm(+r.kg, +r.reps);
            const prev = M.get(key);
            if (prev == null || (val != null && val > prev)) M.set(key, val);
        }
        const labels = Array.from(M.keys()).sort();
        return { labels, data: labels.map(k => M.get(k)) };
    }

    // Volumen semanal (kg·reps)
    function weeklyVolume(rows) {
        const M = new Map();
        for (const r of rows) {
            if (r.kg == null || !isFinite(r.kg) || r.reps <= 0) continue;
            const key = weekKey(r.fecha);
            const cur = M.get(key) || 0;
            M.set(key, cur + (+r.kg) * (+r.reps));
        }
        const labels = Array.from(M.keys()).sort();
        return { labels, data: labels.map(k => +M.get(k).toFixed(2)) };
    }

    function remapTo(labels, srcLabels, srcData, fill = null) {
        const m = new Map(srcLabels.map((l, i) => [l, srcData[i]]));
        return labels.map(l => (m.has(l) ? m.get(l) : fill));
    }

    // ===== Crear charts (2 datasets) =====
    function initCharts() {
        // e1RM
        const ph1 = $('#ph-e1rm');
        if (ph1) {
            const c = document.createElement('canvas');
            ph1.replaceWith(c);
            chartE1RM = new Chart(c.getContext('2d'), {
                type: 'line',
                data: {
                    labels: [],
                    datasets: [
                        { label: 'Compartido (e1RM)', data: [], borderColor: '#ff9800', borderWidth: 2, tension: .25, spanGaps: true, pointStyle: 'circle',   pointRadius: 3, fill: false },
                        { label: 'Tú (e1RM)',        data: [], borderColor: '#00bcd4', borderWidth: 2, tension: .25, spanGaps: true, pointStyle: 'triangle', pointRadius: 4, fill: false, borderDash: [6,4] }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: true, position: 'top' } },
                    scales: { y: { beginAtZero: true, title: { display: true, text: 'kg (e1RM)' } } }
                }
            });
        }

        // Volumen (barras agrupadas)
        const ph2 = $('#ph-volume');
        if (ph2) {
            const c = document.createElement('canvas');
            ph2.replaceWith(c);
            chartVolume = new Chart(c.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: [],
                    datasets: [
                        { label: 'Compartido', data: [], backgroundColor: '#ff9800' },
                        { label: 'Tú',         data: [], backgroundColor: '#00bcd4' }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: true, position: 'top' } },
                    scales: { y: { beginAtZero: true, title: { display: true, text: 'Volumen (kg·reps)' } } }
                }
            });
        }

        // Scatter
        const ph3 = $('#ph-scatter');
        if (ph3) {
            const c = document.createElement('canvas');
            ph3.replaceWith(c);
            chartScatter = new Chart(c.getContext('2d'), {
                type: 'scatter',
                data: {
                    datasets: [
                        { label: 'Compartido', data: [], backgroundColor: '#ff9800', pointStyle: 'circle',   pointRadius: 3 },
                        { label: 'Tú',         data: [], backgroundColor: '#00bcd4', pointStyle: 'triangle', pointRadius: 4 }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: true, position: 'top' } },
                    scales: {
                        x: { title: { display: true, text: 'Reps' }, ticks: { precision: 0 } },
                        y: { title: { display: true, text: 'Carga (kg)' }, beginAtZero: true }
                    }
                }
            });
        }
    }

    // ===== Render de datos en charts =====
    function updateCharts(payload) {
        const ownerName  = (payload.meta && payload.meta.ownerName)  || 'Compartido';
        const viewerName = (payload.meta && payload.meta.viewerName) || 'Tú';

        const ownerRows  = (payload.owner  || []).filter(r => r.kg != null && r.reps > 0).sort(byDate);
        const viewerRows = (payload.viewer || []).filter(r => r.kg != null && r.reps > 0).sort(byDate);

        // e1RM semanal (máximo)
        const wO = weeklyBestE1rm(ownerRows);
        const wV = weeklyBestE1rm(viewerRows);
        const wkLabels = Array.from(new Set([ ...wO.labels, ...wV.labels ])).sort();

        if (chartE1RM) {
            chartE1RM.data.datasets[0].label = ownerName + ' (e1RM)';
            chartE1RM.data.datasets[1].label = viewerName + ' (e1RM)';

            chartE1RM.data.labels = wkLabels;
            chartE1RM.data.datasets[0].data = remapTo(wkLabels, wO.labels, wO.data, null);
            chartE1RM.data.datasets[1].data = remapTo(wkLabels, wV.labels, wV.data, null);
            chartE1RM.update();
        }

        // Volumen semanal (agrupadas)
        const vO = weeklyVolume(ownerRows);
        const vV = weeklyVolume(viewerRows);
        const volLabels = Array.from(new Set([ ...vO.labels, ...vV.labels ])).sort();

        if (chartVolume) {
            chartVolume.data.datasets[0].label = ownerName;
            chartVolume.data.datasets[1].label = viewerName;

            chartVolume.data.labels = volLabels;
            chartVolume.data.datasets[0].data = remapTo(volLabels, vO.labels, vO.data, 0);
            chartVolume.data.datasets[1].data = remapTo(volLabels, vV.labels, vV.data, 0);
            chartVolume.update();
        }

        // Scatter
        if (chartScatter) {
            chartScatter.data.datasets[0].label = ownerName;
            chartScatter.data.datasets[1].label = viewerName;

            chartScatter.data.datasets[0].data = ownerRows.map(r => ({ x: +r.reps, y: +r.kg }));
            chartScatter.data.datasets[1].data = viewerRows.map(r => ({ x: +r.reps, y: +r.kg }));
            chartScatter.update();
        }

        // Hint si el viewer no tiene datos
        if (!viewerRows.length) {
            const host = document.querySelector('#blk-exercise .chart-card');
            if (host && !host.querySelector('.cmp-hint')) {
                const hint = document.createElement('div');
                hint.className = 'cmp-hint';
                hint.style.cssText = 'margin:6px 0;font-size:12px;opacity:.8';
                hint.textContent = 'Tu usuario no tiene registros de este ejercicio en este rango; por eso puede verse una sola serie.';
                host.prepend(hint);
            }
        }
    }

    // ===== Carga de datos =====
    async function loadAndRender() {
        try {
            const res = await fetch(compareJsonUrl(), { headers: { 'Accept': 'application/json' } });
            const raw = await res.text();
            // Debug opcional:
            // console.log('compare raw:', raw);
            if (!res.ok) throw new Error(raw.slice(0, 200));
            const data = JSON.parse(raw);
            if (!data.ok) throw new Error('ok=false');
            updateCharts(data);
        } catch (err) {
            console.error('compare load error:', err);
        }
    }

    // ===== Boot =====
    function boot() {
        initCharts();
        loadAndRender();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot, { once: true });
    } else {
        boot();
    }
})();