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
        <a href="#/rutinas/gestionar" data-link>
          <!-- ícono rutina -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2v20M2 12h20"/>
          </svg>
          <span>Crear / Editar / Eliminar Rutina</span>
        </a>
      </li>
      <li>
        <a href="#/rutinas/asignar-ejercicios" data-link>
          <!-- ícono asignar ejercicios -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1 1 0 0 0 .2-1l-2-3.5a1 1 0 0 0-.9-.5H7.3a1 1 0 0 0-.9.5L4.4 14a1 1 0 0 0 .2 1l3 5a1 1 0 0 0 .9.5h7a1 1 0 0 0 .9-.5l3-5z"/>
          </svg>
          <span>Asignar Ejercicios</span>
        </a>
      </li>
      <li>
        <a href="#/rutinas/asignar" data-link>
          <!-- ícono clipboard -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="9" y="2" width="6" height="4" rx="1" ry="1"/>
            <path d="M4 6h16v14H4z"/>
          </svg>
          <span>Asignar Rutina</span>
        </a>
      </li>
      <li>
        <a href="#/rutinas/ver" data-link>
          <!-- ícono lupa -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <span>Ver Rutinas (por filtros)</span>
        </a>
      </li>
    </ul>

    <!-- SECCIÓN CLIENTE -->
    <h3 class="sidebar__section"><span>Cliente</span></h3>
    <ul>
      <li>
        <a href="${pageContext.request.contextPath}/pages/staff/cliente/crearCliente.jsp" data-link>
          <!-- ícono usuario plus -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="9" cy="7" r="4"/>
            <path d="M17 11v6m3-3h-6M5.5 21a6.5 6.5 0 0 1 13 0"/>
          </svg>
          <span>Crear Cliente</span>
        </a>
      </li>
      <li>
        <a href="${pageContext.request.contextPath}/pages/staff/cliente/modificarCliente.jsp" data-link>
          <!-- ícono lápiz -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 20h9"/>
            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>
          </svg>
          <span>Modificar Cliente</span>
        </a>
      </li>
      <li>
        <a href="#/clientes/listar" data-link>
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

    <!-- SECCIÓN PLAN -->
    <h3 class="sidebar__section"><span>Plan</span></h3>
    <ul>
      <li>
        <a href="#/planes/nuevo" data-link>
          <!-- ícono plus -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/>
            <line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          <span>Crear Plan</span>
        </a>
      </li>
      <li>
        <a href="#/planes/modificar" data-link>
          <!-- ícono editar -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 20h9"/>
            <path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>
          </svg>
          <span>Modificar Plan</span>
        </a>
      </li>
      <li>
        <a href="#/planes/desactivar" data-link>
          <!-- ícono prohibido -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="9"/>
            <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/>
          </svg>
          <span>Desactivar Plan</span>
        </a>
      </li>
    </ul>

    <!-- SECCIÓN MEMBRESÍA -->
    <h3 class="sidebar__section"><span>Membresía</span></h3>
    <ul>
      <li>
        <a href="#/membresias/nueva" data-link>
          <!-- ícono credencial -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="16" rx="2"/>
            <circle cx="12" cy="10" r="3"/>
            <path d="M6 18a6 6 0 0 1 12 0"/>
          </svg>
          <span>Crear Membresía</span>
        </a>
      </li>
      <li>
        <a href="#/membresias/renovar" data-link>
          <!-- ícono renovar -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="23 4 23 10 17 10"/>
            <polyline points="1 20 1 14 7 14"/>
            <path d="M3.51 9a9 9 0 0 1 14.13-3.36L23 10M1 14l5.36 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
          <span>Renovar Membresía</span>
        </a>
      </li>
      <li>
        <a href="#/membresias/cambiar" data-link>
          <!-- ícono swap -->
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="16 3 21 8 16 13"/>
            <line x1="4" y1="8" x2="21" y2="8"/>
            <polyline points="8 21 3 16 8 11"/>
            <line x1="3" y1="16" x2="20" y2="16"/>
          </svg>
          <span>Cambiar Membresía</span>
        </a>
      </li>
    </ul>

    <!-- SECCIÓN CUENTA -->
    <h3 class="sidebar__section"><span>Cuenta</span></h3>
    <ul>
      <li>
        <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
          <button type="submit" class="btn logout-button">
            <!-- ícono candado -->
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2"/>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
            </svg>
            <span>Cerrar sesión</span>
          </button>
        </form>
      </li>
    </ul>
  </nav>
</aside>



