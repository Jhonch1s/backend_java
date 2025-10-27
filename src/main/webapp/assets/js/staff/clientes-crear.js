(function () {
    const form = document.getElementById('form-crear-cliente');
    const toastContainer = document.getElementById('toast-container');

    // (por si esta vista no agregó la clase en <body>)
    document.body.classList.add('vista--cliente-crear');

    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearFieldErrors();

        const url = form.getAttribute('action');
        const fd = new FormData(form);

        // Serializar a x-www-form-urlencoded
        const body = new URLSearchParams();
        for (const [k, v] of fd.entries()) body.append(k, v);

        try {
            const BASE = document.body.getAttribute('data-base') || '';
            const res = await fetch(`${BASE}/api/clientes?ci=${encodeURIComponent(ci)}`, {
                headers: { 'Accept': 'application/json' }
            });

            const data = await res.json().catch(() => ({}));
            // data: { success:boolean, message:string, errors?:{...} }

            if (!res.ok || data.success === false) {
                // Errores de validación
                if (data && data.errors) paintFieldErrors(data.errors);
                showToast('error', data?.message || 'No se pudo crear el cliente.');
                return;
            }

            // Éxito
            showToast('success', data.message || 'Cliente creado exitosamente.');
            form.reset();

            // TODO: si querés redirigir luego de unos segundos:
            // setTimeout(()=> window.location.href = `${contextPath}/pages/staff/cliente/listar.jsp`, 1200);

        } catch (err) {
            console.error(err);
            showToast('error', 'Error de red: intenta nuevamente.');
        }
    });

    function paintFieldErrors(errors) {
        // errors viene con keys como "cliente-ci", "cliente-email", etc.
        Object.entries(errors).forEach(([id, msg]) => {
            const el = document.getElementById(`error-${id}`) || document.getElementById(id);
            if (el) el.textContent = msg;

            // marcar el control asociado si existe (asume id="cliente-ci" y class="control")
            const controlId = id.replace('cliente-', 'cliente-'); // coincide con tus ids
            const control = document.getElementById(controlId);
            if (control) control.classList.add('is-invalid');
        });
    }

    function clearFieldErrors() {
        document.querySelectorAll('.error').forEach(e => e.textContent = '');
        document.querySelectorAll('.control.is-invalid').forEach(e => e.classList.remove('is-invalid'));
    }

    // --- Toast minimal ---
    function showToast(type, message) {
        if (!toastContainer) return;

        const toast = document.createElement('div');
        toast.className = `toast toast--${type}`;
        toast.innerHTML = `
      <div class="toast__icon">
        <svg width="18" height="18" aria-hidden="true">
          <use href="${getIconHref(type)}"></use>
        </svg>
      </div>
      <div class="toast__msg">${escapeHtml(message || '')}</div>
      <button class="toast__close" aria-label="Cerrar">&times;</button>
    `;

        toastContainer.appendChild(toast);

        const close = () => {
            toast.classList.add('is-hiding');
            setTimeout(() => toast.remove(), 150);
        };

        toast.querySelector('.toast__close').addEventListener('click', close);
        setTimeout(close, 4000);
    }

    function getIconHref(type) {
        // usa símbolos del sprite si existen; si no, usa uno que ya tengas
        switch (type) {
            case 'success': return '#i-check' in symbolExists() ? '#i-check' : '#i-user-add';
            case 'error':   return '#i-ban';
            case 'info':    return '#i-info' in symbolExists() ? '#i-info' : '#i-list';
            default:        return '#i-list';
        }
    }

    function symbolExists() {
        // devuelve un objeto proxy simple para "in"
        const ids = {};
        document.querySelectorAll('symbol[id]').forEach(s => { ids['#'+s.id] = true; });
        return new Proxy(ids, { has: (t, p) => !!t[p] });
    }

    function escapeHtml(str) {
        return String(str).replace(/[&<>"']/g, s => ({
            '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
        }[s]));
    }
})();
