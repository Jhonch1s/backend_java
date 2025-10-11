<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 08/10/2025
  Time: 21:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.gymtrackerweb.utils.FechaUtils" %>
<%@ page import="com.example.gymtrackerweb.model.Cliente" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="es-UY">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>Cliente | Dashboard · Golden Gym</title>

    <!-- Favicons -->
    <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
    <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

    <!-- Tipografía + Normalize -->
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
          integrity="sha512-NhSC1X0f3zp3p2JtYh8C2W4TyTX0b6x1n00x4bZ4Zk3E2b9GmZy1wKkPe4v5YyX1Y9i6w5W2rszj0o9uGZ7xwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Estilos existentes (no modificados) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <!-- NUEVO: estilos mínimos para el dashboard cliente (usa tus variables) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css">
</head>

<body class="fondo-oscuro texto-claro">
<main id="app" class="cliente-home cliente-home--fixed">
    <!-- Perfil / hero -->
    <section class="hero tarjeta borde-redondeado sombra-suave">
        <header class="hero__top alinear-centro-between">
            <div class="alinear-centro-left u-gap-12">
                <div class="hero__avatar">MR</div>
                <div>
                    <h2 class="m-0 titillium-negrita">
                        ${sessionScope.usuario.nombre} ${sessionScope.usuario.apellido}
                    </h2>
                    <p class="m-0 hero__meta">
                        ¡Has levantado
                        <strong>
                            <fmt:formatNumber value="${requestScope.kgLevantadosTotal}" maxFractionDigits="0"/>
                        </strong>
                        Kg!
                    </p>
                </div>
            </div>
            <div class="hero__member">
                <p class="m-0 hero__member-label">Miembro desde</p>
                <%
                Cliente usuario = (Cliente) session.getAttribute("usuario");
                %>
                <p class="m-0 hero__member-date">${FechaUtils.formatearMesAnio(usuario.getFechaIngreso())}</p>
            </div>
        </header>

        <div class="u-mt-12">
            <a href="${pageContext.request.contextPath}/pages/modulos/cliente/ingreso.jsp"
               class="btn btn--primary-yellow btn--xl u-w-full alinear-centro-center u-gap-8">
                <span class="emoji" aria-hidden="true">🏷️</span> Registrar Entrada
            </a>
        </div>

        <div class="hero__stats u-mt-16">
            <div class="hero__stat">
                <p class="hero__stat-number">
                    ${diasEntrenadosMes != null ? diasEntrenadosMes : 0}
                </p>
                <p class="hero__stat-label">Días este mes</p>
            </div>
            <div class="hero__stat">
                <p class="hero__stat-number">
                    ${minPromedioSesion != null ? minPromedioSesion : 0}
                </p>
                <p class="hero__stat-label">Min promedio</p>
            </div>
            <div class="hero__stat">
                <p class="hero__stat-number">
                    ${totalSesionesTotal != null ? totalSesionesTotal : 0}
                </p>
                <p class="hero__stat-label">Entrenamientos</p>
            </div>
        </div>
    </section>

    <!-- Rutinas -->
    <c:set var="hayActiva" value="false"/>
    <c:forEach var="r" items="${rutinasTop}">
        <c:if test="${r.estado eq 'activa'}">
            <c:set var="hayActiva" value="true"/>
        </c:if>
    </c:forEach>

    <section class="bloque tarjeta borde-redondeado sombra-suave">
        <header class="bloque__head alinear-centro-between">
            <h3 class="bloque__title">Rutinas</h3>
            <span class="bloque__ok" aria-hidden="true">
                <c:choose>
                    <c:when test="${hayActiva}">✔</c:when>
                    <c:otherwise>•</c:otherwise>
                </c:choose>
            </span>
        </header>

        <div class="lista-tiles u-mt-8">
            <c:forEach var="r" items="${rutinasTop}">
                <a class="tile"
                   href="${pageContext.request.contextPath}/pages/modulos/cliente/rutinas.jsp?id=${r.idRutina}">
                    <div class="tile__text">
                        <p class="tile__title">${r.nombre}</p>
                        <p class="tile__desc">${r.gruposTop3}</p>
                    </div>
                    <span class="tile__meta ${r.estado eq 'activa' ? 'is-active' : 'is-inactive'}">
                            ${fn:toUpperCase(r.estado)}
                    </span>
                </a>
            </c:forEach>

            <c:if test="${empty rutinasTop}">
                <div class="tile tile--empty">
                    <div class="tile__text">
                        <p class="tile__title">Sin rutinas asignadas</p>
                        <p class="tile__desc">Tu entrenador pronto te asignará una rutina.</p>
                    </div>
                    <span class="tile__meta">—</span>
                </div>
            </c:if>
        </div>

        <a class="btn btn--ghost-yellow btn--lg u-w-full u-mt-16"
           href="${pageContext.request.contextPath}/pages/modulos/cliente/rutinas.jsp">
            Ver todas las rutinas →
        </a>
    </section>

    <!-- Progresos -->
    <section class="bloque tarjeta borde-redondeado sombra-suave">
        <header class="bloque__head alinear-centro-between">
            <h3 class="bloque__title">Progresos</h3>
            <!-- ícono flecha tendencia -->
            <svg class="bloque__icon" width="20" height="20" viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <path d="M4 16l6-6 4 4 6-6" stroke="#fff112" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        </header>

        <div class="prog-list u-mt-8">
            <c:forEach var="p" items="${progresosTop}">
                <c:set var="claseEstado"
                       value="${p.difKg > 0 ? 'prog-item--pos-green' : (p.difKg < 0 ? 'prog-item--neg' : 'prog-item--neu')}" />

                <a class="prog-item ${claseEstado}"
                   href="${pageContext.request.contextPath}/pages/modulos/cliente/progreso.jsp?ej=${p.idEjercicio}">
                    <div class="prog-item__text">
                        <p class="prog-item__title">${p.ejercicio}</p>
                        <p class="prog-item__sub">
                            Última medición:
                            <fmt:formatNumber value="${p.ultimo}" minFractionDigits="0" maxFractionDigits="2"/>kg
                        </p>
                    </div>

                    <span class="prog-item__delta">
          <c:choose>
              <c:when test="${p.difKg > 0}">+<fmt:formatNumber value="${p.difKg}" minFractionDigits="0" maxFractionDigits="2"/>kg</c:when>
              <c:when test="${p.difKg < 0}"><fmt:formatNumber value="${p.difKg}" minFractionDigits="0" maxFractionDigits="2"/>kg</c:when>
              <c:otherwise>±0kg</c:otherwise>
          </c:choose>
        </span>
                </a>
            </c:forEach>

            <c:if test="${empty progresosTop}">
                <div class="prog-item prog-item--neu">
                    <div class="prog-item__text">
                        <p class="prog-item__title">Sin progresos recientes</p>
                        <p class="prog-item__sub">Registrá una nueva medición para ver cambios.</p>
                    </div>
                    <span class="prog-item__delta">—</span>
                </div>
            </c:if>
        </div>

        <a class="btn btn--ghost-yellow btn--lg u-w-full u-mt-16"
           href="${pageContext.request.contextPath}/pages/modulos/cliente/progreso.jsp">
            Ver todos los progresos →
        </a>
    </section>

</main>
<!-- sticky del nav abajo -->
<%@ include file="/pages/modulos/bottom-nav.jsp" %>
</body>
</html>

