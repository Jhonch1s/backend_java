import { routes, getRouteInfo, markActiveLink } from './router.js';
// --- CAMBIO 1: Importa desde el archivo renombrado ---
import { inicializarValidacion } from './validation.js';
import { initSidebar } from './sidebar.js';

document.addEventListener('DOMContentLoaded', () => {
    const appView = document.getElementById('app-view');
    const contextPath = window.APP_CONTEXT_PATH || '';

    initSidebar(); // Inicializa sidebar

    async function navigate() {
        const { path, params } = getRouteInfo();
        let currentPath = path;

        appView.innerHTML = '<div class="main__placeholder"><h1>Cargando...</h1></div>';
        appView.setAttribute('aria-busy', 'true');

        let templateId = routes[path];

        // Ruta por defecto
        if (path === '/') {
            templateId = routes['/planes/lista'];
            history.replaceState(null, '', '#/planes/lista');
            currentPath = '/planes/lista';
            templateId = routes[currentPath]; // Actualiza el templateId
        }

        // Renderizado de vistas
        if (path === '/planes/editar') {
            const id = params.get('id');
            if (id) {
                await loadAndDisplayEditForm(id); // Asume que esta función existe y llama a renderView
            } else {
                showErrorView('Se necesita un ID para editar el plan.');
            }
        } else if (templateId) {
            renderView(templateId); // Renderiza la vista

            // --- CAMBIO 2: Llama a inicializarValidacion para AMBOS formularios ---
            // Verifica qué template se renderizó y llama a la validación con el ID correcto
            if (templateId === 'tpl-plan-crear') {
                console.log("Inicializando validación para form-crear-plan..."); // Debug
                inicializarValidacion('form-crear-plan');
            } else if (templateId === 'tpl-cliente-crear') {
                console.log("Inicializando validación para form-crear-cliente..."); // Debug
                inicializarValidacion('form-crear-cliente');
            }
            // Agrega aquí la llamada para editar si es necesario
            // else if (templateId === 'tpl-plan-editar') {
            //     inicializarValidacion('form-editar-plan');
            // }

        } else {
            showErrorView(`Página no encontrada para la ruta: ${path}`);
        }

        markActiveLink(currentPath);
        appView.setAttribute('aria-busy', 'false');
    }

    // --- Funciones Auxiliares (renderView, etc.) ---
    function renderView(templateId) {
        const template = document.getElementById(templateId);
        if (template) {
            console.log(`Renderizando template: ${templateId}`); // Debug
            appView.innerHTML = '';
            appView.appendChild(template.content.cloneNode(true));
            const h1 = appView.querySelector('.view__title') || appView.querySelector('h1');
            if (h1) h1.setAttribute('tabindex', '-1');
            (h1 || appView).focus({ preventScroll: true });
        } else {
            console.error(`Template "${templateId}" no encontrado.`); // Debug
            showErrorView(`El template "${templateId}" no se encontró.`);
        }
    }

    async function loadAndDisplayEditForm(planId) {
        // ... (Tu lógica para cargar datos y rellenar el form de edición) ...
        renderView('tpl-plan-editar'); // Muestra el form (aún vacío o con datos)
        console.log(`TODO: Cargar datos para plan ID ${planId} y rellenar formulario.`);
        // IMPORTANTE: La validación para editar debe llamarse DESPUÉS de rellenar el form
        // inicializarValidacion('form-editar-plan');
    }

    function showErrorView(message) {
        appView.innerHTML = `<div class="main__placeholder"><h1>Error</h1><p style="color:red;">${message}</p></div>`;
    }

    // --- Inicialización (sin cambios) ---
    function initializeApp() {
        window.addEventListener('hashchange', navigate);
        const initialHash = window.location.hash;
        if (!initialHash || initialHash === '#/' || initialHash === '#') {
            history.replaceState(null, '', '#/planes/lista');
        }
        navigate();
    }
    initializeApp();
});