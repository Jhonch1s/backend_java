// Defines which template corresponds to each route
export const routes = {
    '/planes/lista': 'tpl-planes-lista',
    '/planes/nuevo': 'tpl-plan-crear',
    '/planes/editar': 'tpl-plan-editar',
    '/clientes/lista': 'tpl-clientes-lista',
    '/clientes/nuevo': 'tpl-cliente-crear',
    // Add other routes here, e.g., '/clientes/editar': 'tpl-cliente-editar',
};

// Gets the path and query parameters (?id=X) from the current hash
export function getRouteInfo() {
    const hash = window.location.hash || '#/';
    // Clean the # and separate the path from parameters
    const [path, queryString] = hash.substring(1).split('?');
    const params = new URLSearchParams(queryString);
    // Return the path (ensuring it's at least '/') and parameters
    return { path: path || '/', params };
}

// Highlights the active link in the sidebar
export function markActiveLink(path) {
    document.querySelectorAll('.sidebar__nav a[data-link]').forEach(a => {
        const href = a.getAttribute('href') || '';
        // Compare the current path with the link's href (removing # and query params)
        const linkPath = href.split('?')[0].replace('#', '');
        a.classList.toggle('is-active', linkPath === path);
    });
}