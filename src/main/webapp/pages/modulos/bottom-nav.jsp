<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 09/10/2025
  Time: 12:26
  To change this template use File | Settings | File Templates.
--%>
<nav class="bottom-nav" role="navigation" aria-label="Navegación principal">
  <ul class="bottom-nav__list">
    <!-- Inicio (activo) -->
    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/pages/modulos/cliente/index.jsp"
         data-tab="/inicio" aria-current="page">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/>
        </svg>
        <span class="bottom-nav__label">Inicio</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/pages/modulos/cliente/rutinas.jsp"
         data-tab="/rutinas" aria-current="false">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Rutinas</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/pages/modulos/cliente/progreso.jsp"
         data-tab="/estadisticas" aria-current="false">
        <svg class="bottom-nav__icon" viewBox="0 0 24 24" fill="none" aria-hidden="true">
          <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6m8 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="bottom-nav__label">Estadísticas</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/pages/modulos/cliente/perfil.jsp"
         data-tab="/perfil" aria-current="false">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Perfil</span>
      </a>
    </li>
  </ul>
</nav>


