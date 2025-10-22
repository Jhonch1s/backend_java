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
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/cliente"
         data-tab="/inicio" aria-current="page">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/>
        </svg>
        <span class="bottom-nav__label">Inicio</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/pages/UploadImage.jsp"
         data-tab="/rutinas" aria-current="false">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Rutinas</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/cliente/estadisticas"
         data-tab="/estadisticas" aria-current="false">
        <?xml version="1.0" ?><!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
        <svg class="bottom-nav__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
          <line x1="2" y1="20" x2="22" y2="20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M5 20V8.2a.2.2 0 0 1 .2-.2h2.6a.2.2 0 0 1 .2.2V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M11 20V4.2667c0-.1473.0895-.2667.2-.2667h2.6c.1105 0 .2.1194.2.2667V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M17 20V11.15c0-.0828.0895-.15.2-.15h2.6c.1105 0 .2.0672.2.15V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="bottom-nav__label">Estadísticas</span>
      </a>
    </li>

    <li>
      <a class="bottom-nav__btn" href="${pageContext.request.contextPath}/cliente/perfil"
         data-tab="/perfil" aria-current="false">
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Perfil</span>
      </a>
    </li>
  </ul>
</nav>


