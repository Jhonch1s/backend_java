<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Tomamos la URI ORIGINARIA si hay FORWARD; si no, la actual --%>
<c:set var="__fwd" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty __fwd}">
  <c:set var="__fwd" value="${requestScope['javax.servlet.forward.request_uri']}" />
</c:if>
<c:set var="__uri_eff" value="${empty __fwd ? pageContext.request.requestURI : __fwd}" />
<c:set var="__ctx" value="${pageContext.request.contextPath}" />
<c:set var="__path_eff" value="${fn:substring(__uri_eff, fn:length(__ctx), fn:length(__uri_eff))}" />

<%-- Inicio activo si la ruta efectiva es /cliente (con o sin /) o si forwardea al JSP de inicio --%>
<c:set var="isInicio"
       value="${__path_eff == '/cliente'
               or __path_eff == '/cliente/'
               or __path_eff == '/pages/cliente/index.jsp'}" />
<!-- DEBUG opcional:
__uri_eff=/GymTrackerWeb/cliente  __path_eff=/cliente  isInicio=true -->
<!-- DEBUG: __uri_eff=<c:out value="${__uri_eff}"/> __path_eff=<c:out value="${__path_eff}"/> isInicio=<c:out value="${isInicio}"/> -->

<nav class="bottom-nav" role="navigation" aria-label="Navegación principal">
  <ul class="bottom-nav__list">

    <li>
      <a class="bottom-nav__btn ${isInicio ? 'is-active' : ''}"
         href="${pageContext.request.contextPath}/cliente"
         data-tab="/inicio"
         <c:if test="${isInicio}">aria-current="page"</c:if>>
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z"/>
        </svg>
        <span class="bottom-nav__label">Inicio</span>
      </a>
    </li>

    <!-- RUTINAS -->
    <li>
      <a class="bottom-nav__btn ${requestScope.navActive == 'rutinas' ? 'is-active' : ''}"
         href="${pageContext.request.contextPath}/cliente/listarutinas"
         data-tab="/rutinas"
         <c:if test="${requestScope.navActive == 'rutinas'}">aria-current="page"</c:if>>
        <!-- SVG INTACTO -->
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Rutinas</span>
      </a>
    </li>

    <!-- PROGRESOS -->
    <li>
      <a class="bottom-nav__btn ${requestScope.navActive == 'progresos' ? 'is-active' : ''}"
         href="${pageContext.request.contextPath}/cliente/progreso"
         data-tab="/progresos"
         <c:if test="${requestScope.navActive == 'progresos'}">aria-current="page"</c:if>>
        <!-- SVG INTACTO -->
        <?xml version="1.0" ?><!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
        <!--<svg class="bottom-nav__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
          <line x1="2" y1="20" x2="22" y2="20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M5 20V8.2a.2.2 0 0 1 .2-.2h2.6a.2.2 0 0 1 .2.2V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M11 20V4.2667c0-.1473.0895-.2667.2-.2667h2.6c.1105 0 .2.1194.2.2667V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M17 20V11.15c0-.0828.0895-.15.2-.15h2.6c.1105 0 .2.0672.2.15V20" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        -->
        <svg width="24" height="24" viewBox="0 0 100 100" stroke="currentColor" xmlns="http://www.w3.org/2000/svg">
          <rect x="10" y="70" width="10" height="20" fill="currentColor"/>
          <rect x="30" y="60" width="10" height="30" fill="currentColor"/>
          <rect x="50" y="50" width="10" height="40" fill="currentColor"/>
          <rect x="70" y="40" width="10" height="50" fill="currentColor"/>
          <path d="M10 70 L30 60 L50 50 L70 40" stroke="#2196F3" stroke-width="2" fill="none"/>
          <polygon points="70,40 65,45 75,45" fill="#2196F3"/>
        </svg>
        <span class="bottom-nav__label">Progresos</span>
      </a>
    </li>

    <!-- ESTADÍSTICAS -->
    <li>
      <a class="bottom-nav__btn ${requestScope.navActive == 'estadisticas' ? 'is-active' : ''}"
         href="${pageContext.request.contextPath}/cliente/estadisticas"
         data-tab="/estadisticas"
         <c:if test="${requestScope.navActive == 'estadisticas'}">aria-current="page"</c:if>>
        <!-- SVG INTACTO -->
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

    <!-- PERFIL -->
    <li>
      <a class="bottom-nav__btn ${requestScope.navActive == 'perfil' ? 'is-active' : ''}"
         href="${pageContext.request.contextPath}/cliente/perfil"
         data-tab="/perfil"
         <c:if test="${requestScope.navActive == 'perfil'}">aria-current="page"</c:if>>
        <!-- SVG INTACTO -->
        <svg class="bottom-nav__icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
        </svg>
        <span class="bottom-nav__label">Perfil</span>
      </a>
    </li>

  </ul>
</nav>
