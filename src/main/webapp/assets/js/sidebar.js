// Exports the function to initialize sidebar toggle logic
export function initSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const toggle = document.querySelector('.sidebar__toggle');
    const overlay = document.querySelector('.sidebar-overlay');

    // Basic check if elements exist
    if (!sidebar || !toggle || !overlay) {
        console.warn("Sidebar elements not found, skipping initialization.");
        return;
    }

    function toggleSidebar(forceClose = false) {
        if (forceClose) {
            sidebar.classList.remove('is-open');
            overlay.classList.remove('is-open');
            toggle.setAttribute('aria-expanded', 'false');
        } else {
            const isOpen = sidebar.classList.toggle('is-open');
            overlay.classList.toggle('is-open');
            toggle.setAttribute('aria-expanded', String(isOpen));
        }
    }

    // Event listeners
    toggle.addEventListener('click', () => toggleSidebar());
    overlay.addEventListener('click', () => toggleSidebar());

    // Close sidebar on navigation link click (especially useful on mobile)
    sidebar.querySelectorAll('a[data-link]').forEach(link => {
        link.addEventListener('click', () => {
            // Check if the sidebar is likely in mobile/overlay mode
            if (window.innerWidth < 768) { // Adjust breakpoint if needed
                toggleSidebar(true); // Force close
            }
        });
    });
}