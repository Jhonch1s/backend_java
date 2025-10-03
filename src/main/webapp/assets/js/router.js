// router.js
// Pequeño enrutador por hash (#/ruta) que inyecta <template> en #app-view

export const routes = {
    '/planes/nuevo': 'tpl-plan-crear',
    '/planes/editar': 'tpl-plan-editar',
    '/clientes/nuevo': 'tpl-cliente-crear',
    '/clientes/editar': 'tpl-cliente-editar',
    '/planes/lista': 'tpl-planes-lista',
    '/clientes/lista': 'tpl-clientes-lista',
};

export function getPathFromHash() {
    // Normaliza "#/planes/nuevo" -> "/planes/nuevo"
    const h = window.location.hash || '';
    const clean = h.startsWith('#') ? h.slice(1) : h;
    return clean || '/';
}

export function renderRoute() {
    const view = document.getElementById('app-view');
    const path = getPathFromHash();

    // Marca el main como "cargando" para accesibilidad
    view.setAttribute('aria-busy', 'true');

    // Mapea rutas a templates
    const tplId = routes[path];
    if (!tplId) {
        view.innerHTML = `<div class="main__placeholder"><h1>404</h1><p>Sección no encontrada.</p></div>`;
        view.setAttribute('aria-busy', 'false');
        view.focus();
        markActiveLink(path);
        return;
    }

    const tpl = document.getElementById(tplId);
    if (!tpl) {
        view.innerHTML = `<div class="main__placeholder"><h1>Error</h1><p>No existe el template "${tplId}".</p></div>`;
        view.setAttribute('aria-busy', 'false');
        view.focus();
        markActiveLink(path);
        return;
    }

    // Inyección del contenido del template
    view.innerHTML = '';
    view.appendChild(tpl.content.cloneNode(true));

    // Foco en el título de la vista (si existe)
    const h1 = view.querySelector('.view__title') || view.querySelector('h1');
    if (h1) h1.setAttribute('tabindex', '-1');

    // Listo
    view.setAttribute('aria-busy', 'false');
    (h1 || view).focus();

    markActiveLink(path);
}

export function markActiveLink(path) {
    // Resalta el link activo en el sidebar
    document.querySelectorAll('.sidebar__nav a[data-link]').forEach(a => {
        const href = a.getAttribute('href') || '';
        a.classList.toggle('is-active', href.replace('#', '') === path);
    });
}
