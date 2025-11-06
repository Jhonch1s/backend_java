<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 23/10/2025
  Time: 21:13
  To change this template use File | Settings | File Templates.
--%>
<aside class="sidebar" aria-label="Navegación principal">
  <div class="sidebar__brand">
    <span class="sidebar__title">Golden Gym</span>
  </div>

  <nav class="sidebar__nav">

    <!-- RUTINA -->
      <h3 class="sidebar__section"><span>Rutina</span></h3>
      <ul>
          <li>
              <a href="${pageContext.request.contextPath}/admin/gestion-rutinas" data-link>
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                      <line x1="8" y1="8" x2="16" y2="8"/>
                      <line x1="8" y1="12" x2="16" y2="12"/>
                      <line x1="8" y1="16" x2="13" y2="16"/>
                  </svg>
                  <span>Gestionar Rutinas</span>
              </a>
          </li>
      </ul>

    <h3 class="sidebar__section"><span>Cliente</span></h3>
    <ul>
      <li>
        <a href="${pageContext.request.contextPath}/staff/clientes/crear" data-link>
          <!-- ícono usuario plus -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="9" cy="7" r="4"/>
            <path d="M17 11v6m3-3h-6M5.5 21a6.5 6.5 0 0 1 13 0"/>
          </svg>
          <span>Crear Cliente</span>
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/staff/clientes/modificar" data-link>
          <!-- ícono lápiz -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 20h9"/>
            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>
          </svg>
          <span>Modificar Cliente</span>
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/staff/cliente/listar" data-link>
          <!-- ícono lista -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="8" y1="6" x2="21" y2="6"/>
            <line x1="8" y1="12" x2="21" y2="12"/>
            <line x1="8" y1="18" x2="21" y2="18"/>
            <circle cx="4" cy="6" r="1"/>
            <circle cx="4" cy="12" r="1"/>
            <circle cx="4" cy="18" r="1"/>
          </svg>
          <span>Ver Clientes</span>
        </a>
      </li>
    </ul>

    <h3 class="sidebar__section"><span>Plan</span></h3>
    <ul>
      <li>
        <a href="${pageContext.request.contextPath}/staff/planes" data-link>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 20h9"/>
            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>
          </svg>
          <span>Administrar Planes</span>
        </a>
      </li>
    </ul>

    <!-- SECCIÓN MOVIMIENTOS -->
    <h3 class="sidebar__section"><span>Movimientos</span></h3>
    <ul>
      <li>
        <a href="${pageContext.request.contextPath}/staff/movimientos" data-link>
          <!-- ícono credencial -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="8" y1="6" x2="21" y2="6"/>
            <line x1="8" y1="12" x2="21" y2="12"/>
            <line x1="8" y1="18" x2="21" y2="18"/>
            <circle cx="4" cy="6" r="1"/>
            <circle cx="4" cy="12" r="1"/>
            <circle cx="4" cy="18" r="1"/>
          </svg>
          <span>Ver Movimientos</span>
        </a>
      </li>
    </ul>

    <!-- SECCIÓN CUENTA -->
    <h3 class="sidebar__section"><span>Cuenta</span></h3>
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/logout" data-link>
                <!-- ícono credencial -->
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <span>Cerrar sesión</span>
            </a>
        </li>
    </ul>
  </nav>
</aside>



