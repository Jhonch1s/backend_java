// sidebar.js
// Maneja el botón ☰ para colapsar/expandir el sidebar en pantallas chicas

document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.sidebar__toggle');

    if (!sidebar || !toggle) return;

    const applyState = (open) => {
        sidebar.classList.toggle('is-open', open);
        toggle.setAttribute('aria-expanded', String(open));
    };

    // Estado inicial: cerrado en mobile, abierto en desktop
    const startOpen = window.matchMedia('(min-width: 901px)').matches;
    applyState(startOpen);

    toggle.addEventListener('click', () => {
        const open = !sidebar.classList.contains('is-open');
        applyState(open);
    });

    // Cerrar sidebar al navegar (solo en mobile)
    document.querySelectorAll('.sidebar__nav a[data-link]').forEach(a => {
        a.addEventListener('click', () => {
            if (window.matchMedia('(max-width: 900px)').matches) {
                applyState(false);
            }
        });
    });
});
