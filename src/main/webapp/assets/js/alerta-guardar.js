// guard-unsaved.js
// Muestra aviso si hay cambios sin guardar y previene salir accidentalmente

let dirty = false;

export function setDirty(value) {
    dirty = !!value;
    // Añade clase a form de edición (si existe)
    document.querySelectorAll('.entity-edit__form,.cliente-edit__form').forEach(f => {
        f.classList.toggle('is-dirty', dirty);
    });
}

function bindFormDirtyWatcher(root = document) {
    // Marca dirty si cambia cualquier input en formularios de edición o creación
    root.querySelectorAll('form').forEach(form => {
        form.addEventListener('input', () => setDirty(true), { once: true });
    });
}

export function confirmIfDirty(next) {
    if (!dirty) return true;
    const go = window.confirm('Hay cambios sin guardar. ¿Seguro que querés salir de esta vista?');
    if (go) setDirty(false);
    return go;
}

window.addEventListener('beforeunload', (e) => {
    if (!dirty) return;
    e.preventDefault();
    e.returnValue = '';
});

document.addEventListener('DOMContentLoaded', () => {
    bindFormDirtyWatcher();
});

// Para re-vincular watchers cada vez que se cambia de vista:
export function rebindDirtyWatchers() {
    setDirty(false);
    // Espera a que el router termine de inyectar
    setTimeout(() => {
        // Re-vincula a los formularios de la vista actual
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('input', () => setDirty(true), { once: true });
        });
    }, 0);
}
