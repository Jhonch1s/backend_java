(() => {
    'use strict';

    // ====== Constantes, helpers y estado ======
    const ctx = document.body.dataset.ctx || '';
    const endpointsBase = document.body.dataset.endpointBase || `${ctx}/cliente/stats`;
    const ym = document.body.dataset.ym || null;

    const state = { range: '4w', ejercicioId: null };

    const $  = (sel) => document.querySelector(sel);
    const $$ = (sel) => Array.from(document.querySelectorAll(sel));
    const nf0 = new Intl.NumberFormat('es-UY', { maximumFractionDigits: 0 });

    async function getJSON(url) {
        const r = await fetch(url, { credentials: 'same-origin', headers: { 'Accept': 'application/json' } });
        if (!r.ok) throw new Error(`${r.status} ${r.statusText}`);
        return r.json();
    }

    // ====== Overview (KPIs del mes) ======
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

    // ====== Racha semanal ======
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

        // Label con el total real (preferí el valor del backend si viene)
        let totalStreak = 0;
        if (data.streak !== undefined && data.streak !== null) {
            const n = parseInt(data.streak, 10);
            totalStreak = Number.isFinite(n) ? n : 0;
        } else if (Array.isArray(data.weeks)) {
            // calcular cola contigua por si no viene .streak
            let s = 0, w = data.weeks;
            for (let i = w.length - 1; i >= 0; i--) { if (w[i]?.active) s++; else break; }
            totalStreak = s;
        }
        const label = document.getElementById('streak-weeks-label');
        if (label) label.textContent = `${totalStreak} semanas`;

        // ----- Pintar SÓLO la racha (cola contigua), no todos los active históricos -----
        const cont  = document.getElementById('weeks-streak');
        if (!cont) return;
        const dots  = Array.from(cont.querySelectorAll('.week-dot'));
        const weeks = Array.isArray(data.weeks) ? data.weeks : [];

        // Limpieza base
        for (const d of dots) {
            d.classList.remove('is-active', 'is-reserve', 'is-current');
            quitarFlamaAsset(d);
        }

        if (dots.length === 0) return;

        // Reservar el último siempre
        const reservedIndex = dots.length - 1;
        dots[reservedIndex].classList.add('is-reserve');

        // Alinear a derecha la ventana de weeks dentro de dots
        // y calcular la cola contigua (k) de semanas activas al final de esa ventana
        const usableDots = dots.length - 1; // sin el reservado
        const take = Math.min(weeks.length, usableDots);
        const winStart = usableDots - take;        // dónde empieza la “ventana” de weeks dentro de dots
        let k = 0;                                 // tamaño de la racha (cola contigua)
        for (let j = weeks.length - 1; j >= 0; j--) {
            if (weeks[j]?.active) k++; else break;
        }
        k = Math.max(0, Math.min(k, usableDots));  // clamp

        // Pinta sólo las k últimas bolitas, nunca el reservado
        const startPaint = usableDots - k;         // con k=0 -> startPaint = usableDots (no pinta ninguna)
        for (let i = 0; i < k && i < reservedIndex; i++) {
            dots[i].classList.add('is-active');
        }

        // Marcar la “current” (última de la racha), si hay
        if (k > 0) {
            const currentIdx = Math.min(k - 1, reservedIndex - 1);
            dots[currentIdx].classList.add('is-current');
            agregarFlamaAsset(dots[currentIdx]);
        }
    }


    // ====== Select de ejercicios y Mini-KPIs ======
    async function fetchExercises() {
        return getJSON(`${endpointsBase}/exercises`).catch(() => ({ ok:false, items:[] }));
    }
    function renderExerciseSelect(items) {
        const sel = $('#sel-ej');
        if (!sel) return;
        sel.innerHTML = '';
        if (!items?.length) {
            $('#empty-exercise')?.classList.remove('u-hide');
            return;
        }
        const frag = document.createDocumentFragment();
        for (const it of items) {
            const opt = document.createElement('option');
            opt.value = it.id;
            opt.textContent = it.nombre;
            frag.appendChild(opt);
        }
        sel.appendChild(frag);
        state.ejercicioId = sel.value;
        // Disparar evento para cargar minis y charts del primer ejercicio
        document.dispatchEvent(new CustomEvent('exercise:change', { detail: { ejercicioId: sel.value } }));
    }
    async function fetchMiniKpis(ejercicioId) {
        const url = `${endpointsBase}/exercise/mini?ej=${encodeURIComponent(ejercicioId)}`;
        const res = await fetch(url, { headers: { 'Accept':'application/json' } });
        if (!res.ok) return { ok:false };
        return res.json();
    }
    const n2 = (v, fd='–') => (v==null) ? fd : Number(v).toLocaleString('es-UY', {maximumFractionDigits: 2});
    function renderMiniKpis(data){
        // e1RM kg
        $('#mk-e1rm').textContent = data.ok && data.bestE1rm!=null ? `${n2(data.bestE1rm)} kg` : '–';
        // Mejor set kg×reps
        const best = data.ok ? data.bestSet : null;
        $('#mk-bestset').textContent = best ? `${n2(best.kg)}×${best.reps} kg·reps` : '–';
        // Δ e1RM
        const deltaEl = $('#mk-delta');
        if (data.ok && data.deltaE1rm != null){
            const val = Number(data.deltaE1rm);
            deltaEl.textContent = `${val>0? '+' : ''}${val.toFixed(2)} kg`;
            deltaEl.classList.toggle('is-up', val>0);
            deltaEl.classList.toggle('is-down', val<0);
        } else {
            deltaEl.textContent = '–';
            deltaEl.classList.remove('is-up','is-down');
        }
        // Volumen 4w
        $('#mk-vol4w').textContent = data.ok && data.vol4w!=null ? `${n2(data.vol4w)} kg·reps` : '–';
    }

    document.addEventListener('exercise:change', async (e) => {
        const id = e.detail.ejercicioId;
        try { renderMiniKpis(await fetchMiniKpis(id)); } catch { console.warn('Error mini-KPIs'); }
        // refrescar charts con el rango actual
        document.dispatchEvent(new CustomEvent('charts:refresh', {
            detail: { ejercicioId: id, range: state.range || '4w' }
        }));
    });

    // ====== Tooltips (ayudas) ======
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
        if (btn) { e.preventDefault(); openInfoModal(btn.dataset.help); }
    });

    // ====== Charts ======
    let chartE1RM, chartVolume, chartScatter;
    function initCharts() {
        // e1RM
        const ph1 = $('#ph-e1rm');
        if (ph1) {
            const c = document.createElement('canvas'); c.id = 'chart-e1rm';
            ph1.replaceWith(c);
            chartE1RM = new Chart(c.getContext('2d'), {
                type: 'line',
                data: { labels: [], datasets: [{ label:'e1RM (kg)', data:[], borderColor:'#ff9800', tension:.3, pointRadius:3 }]},
                options: { plugins:{legend:{display:false}}, scales:{y:{beginAtZero:true}} }
            });
        }
        // Volumen semanal
        const ph2 = $('#ph-volume');
        if (ph2) {
            const c = document.createElement('canvas'); c.id = 'chart-volume-weekly';
            ph2.replaceWith(c);
            chartVolume = new Chart(c.getContext('2d'), {
                type: 'bar',
                data: { labels: [], datasets: [{ label:'Volumen (kg·reps)', data:[], backgroundColor:'#9724A6' }]},
                options: { plugins:{legend:{display:false}}, scales:{y:{beginAtZero:true}} }
            });
        }
        // Scatter
        const ph3 = $('#ph-scatter');
        if (ph3) {
            const c = document.createElement('canvas'); c.id = 'chart-scatter';
            ph3.replaceWith(c);
            chartScatter = new Chart(c.getContext('2d'), {
                type: 'scatter',
                data: { datasets: [{ label:'Carga × Reps', data:[], backgroundColor:'#D92BCD' }]},
                options: {
                    plugins:{legend:{display:false}},
                    scales:{ x:{title:{text:'Reps',display:true}}, y:{title:{text:'Carga (kg)',display:true}, beginAtZero:true } }
                }
            });
        }
    }

    function destroyChartsIfAny() {
        for (const ch of [chartE1RM, chartVolume, chartScatter]) {
            try { ch && ch.destroy(); } catch(_){}
        }
        chartE1RM = chartVolume = chartScatter = null;
    }

// Crea los 3 charts con plantilla de 2 datasets
    function initChartsCompare() {
        console.log('🟣 initChartsCompare() DISPARADO');

        function ensureCanvas(placeholderId, canvasId) {
            let host = document.getElementById(placeholderId);
            if (!host) {
                console.warn(`⚠️ placeholder ${placeholderId} no encontrado; usamos fallback`);
                // fallback: buscamos el primer contenedor .chart-inset disponible
                host = document.querySelector('.chart-inset:not([data-has-canvas])');
                if (!host) return null;
            }
            host.setAttribute('data-has-canvas', '1');
            const c = document.createElement('canvas');
            c.id = canvasId;
            // si el host era <small>, lo reemplazamos; si era un div .chart-inset, lo apendeamos
            if (host.tagName.toLowerCase() === 'small') {
                host.replaceWith(c);
            } else {
                host.innerHTML = '';
                host.appendChild(c);
            }
            return c.getContext('2d');
        }

        // destruimos previos
        for (const ch of [chartE1RM, chartVolume, chartScatter]) { try { ch && ch.destroy(); } catch(_){} }
        chartE1RM = chartVolume = chartScatter = null;

        // e1RM
        const ctx1 = ensureCanvas('ph-e1rm', 'chart-e1rm');
        console.log('ctx1:', !!ctx1);
        if (ctx1) {
            chartE1RM = new Chart(ctx1, {
                type: 'line',
                data: {
                    labels: [],
                    datasets: [
                        { label: 'Compartido (e1RM)', data: [], borderColor: '#ff9800', borderWidth: 2, pointStyle: 'circle',   pointRadius: 3, tension: .25, spanGaps: true, fill: false },
                        { label: 'Tú (e1RM)',        data: [], borderColor: '#00bcd4', borderWidth: 2, pointStyle: 'triangle', pointRadius: 3, tension: .25, spanGaps: true, fill: false, borderDash: [6,4] }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: true, position: 'top' } },
                    scales: { y: { beginAtZero: true, title: { display:true, text: 'kg (e1RM)' } } }
                }
            });
        }

        // Volumen
        const ctx2 = ensureCanvas('ph-volume', 'chart-volume-weekly');
        console.log('ctx2:', !!ctx2);
        if (ctx2) {
            chartVolume = new Chart(ctx2, {
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
                    scales: { y: { beginAtZero: true, title: { display:true, text:'Volumen (kg·reps)' } } }
                }
            });
        }

        // Scatter
        const ctx3 = ensureCanvas('ph-scatter', 'chart-scatter');
        console.log('ctx3:', !!ctx3);
        if (ctx3) {
            chartScatter = new Chart(ctx3, {
                type: 'scatter',
                data: {
                    datasets: [
                        { label: 'Compartido', data: [], backgroundColor: '#ff9800', pointStyle: 'circle',   pointRadius: 3 },
                        { label: 'Tú',         data: [], backgroundColor: '#00bcd4', pointStyle: 'triangle', pointRadius: 3 }
                    ]
                },
                options: {
                    responsive: true,
                    plugins: { legend: { display: true, position: 'top' } },
                    scales: {
                        x: { title: { display:true, text: 'Repeticiones' }, ticks: { precision: 0 } },
                        y: { title: { display:true, text: 'Carga (kg)' }, beginAtZero: true }
                    }
                }
            });
        }

        console.log('chartE1RM:', chartE1RM, 'chartVolume:', chartVolume, 'chartScatter:', chartScatter);
    }



    function updateChart(chart, labels, values){
        if (!chart) return;
        chart.data.labels = labels || [];
        chart.data.datasets[0].data = values || [];
        chart.update();
    }
    function updateScatter(chart, points){
        if (!chart) return;
        chart.data.datasets[0].data = points || [];
        chart.update();
    }
    document.addEventListener('charts:refresh', async e => {
        if (document.body.dataset.mode === 'compare') return;
        const { ejercicioId, range } = e.detail;
        const res = await fetch(`${endpointsBase}/exercise/series?ej=${encodeURIComponent(ejercicioId)}&r=${encodeURIComponent(range)}`, { headers:{'Accept':'application/json'} });
        if (!res.ok) return;
        const data = await res.json();
        if (!data.ok) return;

        updateChart(chartE1RM, data.series.map(x => x.label), data.series.map(x => x.e1rm));
        updateChart(chartVolume, data.volumeWeekly.map(x => x.weekLabel), data.volumeWeekly.map(x => x.volume));
        updateScatter(chartScatter, data.scatter.map(x => ({ x:x.reps, y:x.kg })));
    });

    // ====== Chips de rango (único lugar) ======
    function bindRangeSelector() {
        const rangeSelector = $('#rangeSelector');
        if (!rangeSelector) return;

        rangeSelector.addEventListener('click', (e) => {
            const btn = e.target.closest('.chip');
            if (!btn) return;

            rangeSelector.querySelectorAll('.chip').forEach(c => c.classList.remove('is-active'));
            btn.classList.add('is-active');

            state.range = btn.dataset.range || '4w';

            if (state.ejercicioId) {
                document.dispatchEvent(new CustomEvent('charts:refresh', {
                    detail: { ejercicioId: state.ejercicioId, range: state.range }
                }));
            }
        });
    }

    // ====== Select de ejercicio (único bind) ======
    function bindExerciseSelect() {
        const sel = $('#sel-ej');
        if (!sel) return;
        sel.addEventListener('change', () => {
            state.ejercicioId = sel.value || null;
            document.dispatchEvent(new CustomEvent('exercise:change', { detail: { ejercicioId: state.ejercicioId } }));
        });
    }

    // ====== Botón Compartir ======
    let _shareBound = false;
    async function copyToClipboard(text) {
        try {
            if (navigator.clipboard && window.isSecureContext) {
                await navigator.clipboard.writeText(text);
                return true;
            }
        } catch (_) {}
        // Fallback
        const ta = document.createElement('textarea');
        ta.value = text; ta.style.position = 'fixed'; ta.style.opacity = '0';
        document.body.appendChild(ta); ta.focus(); ta.select();
        let ok = false;
        try { ok = document.execCommand('copy'); } catch (_) {}
        ta.remove();
        return ok;
    }

    function initShare() {
        if (_shareBound) return;
        _shareBound = true;

        const btn   = $('#btn-share');
        const input = $('#share-url');
        const help  = $('#share-help');
        const selEj = $('#sel-ej');
        if (!btn || !input || !selEj) return;

        btn.addEventListener('click', async () => {
            const ejId = parseInt(selEj.value, 10);
            if (!ejId || Number.isNaN(ejId)) { alert('Elegí un ejercicio primero.'); return; }

            const activeChip = document.querySelector('#rangeSelector .chip.is-active');
            const range = activeChip?.dataset.range || state.range || '4w';

            try {
                const body = new URLSearchParams({ ejId: String(ejId), range });
                const res = await fetch(`${ctx}/cliente/stats/share/create`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                        'Accept': 'application/json' },
                    credentials: 'same-origin',
                    body
                });

                const raw = await res.text();            // <-- siempre leé texto primero
                let data = null;
                try { data = JSON.parse(raw); }          // <-- intentá parsear
                catch { /* no es JSON */ }

                if (!res.ok) {
                    const msg = (data && data.error) ? data.error : (raw?.slice(0,200) || `${res.status} ${res.statusText}`);
                    throw new Error(`Servidor respondió error: ${msg}`);
                }

                if (!data || !data.url) {
                    throw new Error(`Respuesta sin 'url'. Payload: ${raw?.slice(0,200)}`);
                }

                // construir fullUrl sin asumir que empieza con http
                const u = String(data.url);
                const fullUrl = (/^https?:\/\//i.test(u)) ? u : (location.origin + u);

                input.value = fullUrl;
                input.style.display = 'block';
                help.style.display = 'block';

                const ok = await copyToClipboard(fullUrl);
                const prev = btn.textContent;
                btn.textContent = ok ? '¡Enlace copiado!' : 'Enlace listo (copia manual)';
                if (!ok) { input.focus(); input.select(); }
                setTimeout(() => btn.textContent = prev, 2200);

            } catch (e) {
                console.error(e);
                alert('Error al generar el enlace de compartición.\n' + (e?.message || ''));
            }
        });
    }
    function enableCompareMode() {
        const isCompare = (document.body.dataset.mode === 'compare');
        if (!isCompare) return;

        // Re-crear charts para 2 datasets
        initChartsCompare();

        document.addEventListener('charts:refresh', async e => {
            const { ejercicioId, range } = e.detail;
            const ctx = document.body.dataset.ctx || '';
            const token = document.body.dataset.token || '';
            const url = `${ctx}/cliente/stats/compare?t=${encodeURIComponent(token)}&ej=${encodeURIComponent(ejercicioId)}&r=${encodeURIComponent(range)}`;

            /*
            const res = await fetch(url, { headers: { 'Accept':'application/json' } });
            if (!res.ok) return;
            const data = await res.json();
            if (!data.ok) return;
            */
            console.log("🟣 charts:refresh compare triggered:", ejercicioId, range);
            const res = await fetch(url, { headers: { 'Accept':'application/json' } });
            console.log("📡 Fetch status:", res.status);
            const txt = await res.text();
            console.log("📦 Raw JSON response:", txt);
            let data;
            try {
                data = JSON.parse(txt);
            } catch (err) {
                console.error("❌ JSON parse error:", err);
                return;
            }
            console.log("✅ Parsed data:", data);

            console.log('compare meta:', data.meta);
            console.log('owner len:', (data.owner||[]).length, 'viewer len:', (data.viewer||[]).length);


            const ownerName  = (data.meta && data.meta.ownerName)  || 'Compartido';
            const viewerName = (data.meta && data.meta.viewerName) || 'Tú';

            console.log("chartE1RM:", chartE1RM);
            console.log("chartVolume:", chartVolume);
            console.log("chartScatter:", chartScatter);

            // Renombrar datasets (por si cambiaron nombres)
            if (chartE1RM) {
                chartE1RM.data.datasets[0].label = ownerName + ' (e1RM)';
                chartE1RM.data.datasets[1].label = viewerName + ' (e1RM)';
            }
            if (chartVolume) {
                chartVolume.data.datasets[0].label = ownerName;
                chartVolume.data.datasets[1].label = viewerName;
            }
            if (chartScatter) {
                chartScatter.data.datasets[0].label = ownerName;
                chartScatter.data.datasets[1].label = viewerName;
            }

            // Helpers
            const e1rm = (kg, reps) => (kg == null || !isFinite(kg) || reps <= 0) ? null : +(kg * (1 + reps / 30)).toFixed(2);
            const byDate = (a,b) => (a.fecha < b.fecha ? -1 : (a.fecha > b.fecha ? 1 : 0));

            const owner = (data.owner || []).filter(r => r.kg != null && r.reps > 0).sort(byDate);
            const viewer = (data.viewer || []).filter(r => r.kg != null && r.reps > 0).sort(byDate);

            // e1RM
            if (chartE1RM) {
                const labels = Array.from(new Set([...owner.map(r=>r.fecha), ...viewer.map(r=>r.fecha)])).sort();
                const arrFor = (rows) => {
                    const m = new Map(rows.map(r => [r.fecha, e1rm(+r.kg, +r.reps)]));
                    return labels.map(l => m.has(l) ? m.get(l) : null);
                };
                chartE1RM.data.labels = labels;
                chartE1RM.data.datasets[0].data = arrFor(owner);
                chartE1RM.data.datasets[1].data = arrFor(viewer);
                chartE1RM.update();
            }

            // Volumen semanal (agrupadas)
            const weekly = (rows) => {
                const M = new Map();
                for (const r of rows) {
                    if (r.kg == null || r.reps <= 0) continue;
                    const d = new Date(r.fecha + 'T00:00:00');
                    const year = d.getFullYear();
                    const oneJan = new Date(year,0,1);
                    const day = Math.floor((d - oneJan)/86400000) + 1;
                    const week = Math.max(1, Math.floor((day + ((d.getDay()+6)%7)) / 7));
                    const key = `${year}-W${String(week).padStart(2,'0')}`;
                    const o = M.get(key) || { vol: 0 };
                    o.vol += (+r.kg) * (+r.reps);
                    M.set(key, o);
                }
                const labs = Array.from(M.keys()).sort();
                return { labels: labs, vols: labs.map(k => +M.get(k).vol.toFixed(2)) };
            };

            if (chartVolume) {
                const wO = weekly(owner);
                const wV = weekly(viewer);
                const labs = Array.from(new Set([ ...wO.labels, ...wV.labels ])).sort();
                const remap = (target, src, vals) => {
                    const map = new Map(src.map((d,i)=>[d, vals[i]]));
                    return target.map(l => map.get(l) || 0);
                };
                chartVolume.data.labels = labs;
                chartVolume.data.datasets[0].data = remap(labs, wO.labels, wO.vols);
                chartVolume.data.datasets[1].data = remap(labs, wV.labels, wV.vols);
                chartVolume.update();
            }

            // Scatter
            if (chartScatter) {
                chartScatter.data.datasets[0].data = owner.map(r => ({ x:+r.reps, y:+r.kg }));
                chartScatter.data.datasets[1].data = viewer.map(r => ({ x:+r.reps, y:+r.kg }));
                chartScatter.update();
            }
        });
    }




    // ====== Init principal ======
    async function init() {
        // KPIs overview
        try { renderOverview(await fetchOverview()); }
        catch (err) {
            console.error('Error overview:', err);
            renderOverview({ dias: 0, minTotales: 0, minPromedio: 0 });
        }

        // Racha
        try { renderWeeklyStreak(await fetchWeeklyStreak()); }
        catch (err) {
            console.warn('Streak no disponible aún:', err);
            setText('streak-weeks-label', '– semanas');
        }

        // Binds de UI
        bindRangeSelector();
        bindExerciseSelect();

        // Cargar ejercicios
        const data = await fetchExercises();
        if (data.ok) renderExerciseSelect(data.items);
    }

    function boot() {
        init();
        if (document.body.dataset.mode === 'compare') {
            initChartsCompare();
        } else {
            initCharts();
        }
        enableCompareMode();
        initShare();
    }


    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', boot, { once: true });
    } else {
        boot();
    }


})();
