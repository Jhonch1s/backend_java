//para mas adelante...

(() => {
    const routes = {
        '/ingreso':  '${pageContext.request.contextPath}/cliente/ingreso.jsp',
        '/rutinas':  '${pageContext.request.contextPath}/cliente/rutinas.jsp',
        '/progreso': '${pageContext.request.contextPath}/cliente/progreso.jsp',
        '/historial':'${pageContext.request.contextPath}/cliente/progreso.jsp' // si luego separás historial.jsp, actualizá
    };

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
