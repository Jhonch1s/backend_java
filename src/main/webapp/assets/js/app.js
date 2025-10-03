// app.js
import { renderRoute, getPathFromHash } from './router.js';
import { confirmIfDirty, rebindDirtyWatchers } from './alerta-guardar.js';

function navigate() {
    // Si hay cambios sin guardar, confirmar antes de cambiar de ruta
    if (!confirmIfDirty()) {
        // Revertir hash si el usuario cancela
        // (volvemos a la ruta anterior sin disparar otro evento)
        history.pushState(null, '', '#' + (currentPath || '/'));
        return;
    }
    currentPath = getPathFromHash();
    renderRoute();
    rebindDirtyWatchers();
}

let currentPath = '';

window.addEventListener('hashchange', navigate);
document.addEventListener('DOMContentLoaded', () => {
    // Ruta inicial por defecto
    if (!location.hash) location.hash = '/planes/nuevo';
    navigate();
});
