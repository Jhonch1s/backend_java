// utilidades de contexto y fetch JSON
const DOM = {
    body: document.body,
    ctx: document.body?.dataset?.ctx || '',   // viene del <body data-ctx="...">
};
const fetchJSON = async (input, init) => {
    const r = await fetch(input, { credentials: 'same-origin', ...init });
    if (!r.ok) throw new Error('HTTP ' + r.status);
    return r.json();
};

(() => {
    // Solo corre si explícitamente la página lo pide
    if (!document.body || document.body.getAttribute('data-hash-router') !== '1') return;

    const nav = document.querySelector('.bottom-nav');
    if (!nav) return;

    function setActiveTab(path) {
        nav.querySelectorAll('[data-tab]').forEach(el => {
            el.setAttribute('aria-current', el.dataset.tab === path ? 'page' : 'false');
        });
    }

    function onRouteChange() {
        if (!location.hash) return; // <- clave: no toques nada si no hay hash
        const hash = location.hash.replace('#', '');
        const path = hash.startsWith('/') ? hash : '/' + hash;
        setActiveTab(path);
    }

    document.addEventListener('click', (e) => {
        const a = e.target.closest('a[data-route]');
        if (!a) return;
        const hash = a.getAttribute('data-route');
        if (!hash) return;
        e.preventDefault();
        location.hash = hash;
    });

    window.addEventListener('hashchange', onRouteChange);
    // No llamamos onRouteChange() en load si no hay hash
    if (location.hash) onRouteChange();
})();

// marcar entrada/salida sin recargar
(() => {
    const btn = document.getElementById('btnCheckin');
    if (!btn) return; // esta página no tiene botón

    const lbl = document.getElementById('btnCheckinLabel');
    const endpoint = btn.dataset.endpoint || '/cliente/checkin';
    const url = (DOM.ctx || '') + endpoint;

    const setBusy = (busy) => {
        if (busy) { btn.classList.add('is-busy'); btn.setAttribute('aria-busy','true'); }
        else { btn.classList.remove('is-busy'); btn.removeAttribute('aria-busy'); }
    };

    const applyState = (state, label) => {
        btn.dataset.state = state;
        lbl.textContent = label || (state === 'open' ? 'Marcar salida' : 'Marcar entrada');
        btn.classList.remove('btn--primary-yellow', 'btn--ghost-yellow');
        btn.classList.toggle('is-open',  state === 'open');
        btn.classList.toggle('is-closed',state === 'closed');
    };

    // Estado inicial
    (async () => {
        try {
            const d = await fetchJSON(url, { method: 'GET' });
            if (d.ok) applyState(d.state, d.label);
            else throw new Error('bad payload');
        } catch {
            alert('No se pudo registrar la entrada/salida. Intenta de nuevo.');
        }
    })();

    // Toggle al click
    btn.addEventListener('click', async () => {
        setBusy(true);
        try {
            const d = await fetchJSON(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: '{}' // sin payload
            });
            if (d?.ok) {
                applyState(d.state, d.label);
            } else {
                alert('No se pudo registrar la entrada/salida. Intenta de nuevo.');
            }
        } catch (e) {
            console.error('[checkin click] POST falló:', e);
            alert('No se pudo registrar la entrada/salida. Intenta de nuevo.');
        } finally {
            setBusy(false);
        }
    });
})();
