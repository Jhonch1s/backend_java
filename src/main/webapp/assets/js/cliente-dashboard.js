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

// router basado en anchors con data-route
(() => {
    function setActiveTab(path) {
        document.querySelectorAll('[data-tab]').forEach(el => {
            el.setAttribute('aria-current', el.dataset.tab === path ? 'page' : 'false');
        });
    }

    function onRouteChange() {
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
    onRouteChange();
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
