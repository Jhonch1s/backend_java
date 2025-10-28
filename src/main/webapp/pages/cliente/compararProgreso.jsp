<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 16/10/2025
  Time: 22:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.example.gymtrackerweb.dao.ProgresoEjercicioDAO" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es-UY">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
  <title>Comparar progreso | Golden Gym</title>

  <!-- FavIcons -->
  <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
  <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
  <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
  <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
  <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

  <!-- CSS base del proyecto para mantener look&feel -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estadisticaProgresoCliente.css">

  <style>
    /* Ajustes mínimos para esta vista */
    .cmp-header .who {
      display: inline-flex; gap: .5rem; align-items: baseline; flex-wrap: wrap;
    }
    .cmp-header .pill { padding: .2rem .5rem; border-radius: .5rem; background: rgba(217,43,205,.15); }
    .subgrid { display: grid; grid-template-columns: 1fr; gap: 12px; }
    @media (min-width: 960px) { .subgrid { grid-template-columns: 1fr 1fr 1fr; } }
    .chart-card { background: var(--blk, #111); border-radius: 16px; padding: 12px; box-shadow: 0 2px 6px rgba(0,0,0,.2); }
    .chart-card__title { margin: 0 0 8px 0; }
    .chart-inset { background: rgba(255,255,255,.03); border-radius: 12px; padding: 8px; min-height: 220px; }
    .kpis { display: flex; gap: 16px; flex-wrap: wrap; }
    .kpis .k { min-width: 120px; }
    .muted { opacity: .75; }
  </style>
</head>

<body
        data-ctx="${pageContext.request.contextPath}"
        data-endpoint-base="${pageContext.request.contextPath}/cliente/stats"
        data-mode="compare"
        data-token="${param.t}"
        data-ej="${ejId}"
        data-range="${range}">


<main class="stats-page">

  <%
    // === Recuperar atributos del request (ya seteados por el servlet) ===
    Integer ejId = (Integer) request.getAttribute("ejId");
    String range = (String) request.getAttribute("range");
    Object fromObj = request.getAttribute("from");
    Object toObj   = request.getAttribute("to");
    String ownerDisplay = (String) request.getAttribute("ownerDisplay");
    String viewerDisplay = (String) request.getAttribute("viewerDisplay");

    List<ProgresoEjercicioDAO.ProgresoDato> ownerSeries =
            (List<ProgresoEjercicioDAO.ProgresoDato>) request.getAttribute("ownerSeries");
    List<ProgresoEjercicioDAO.ProgresoDato> viewerSeries =
            (List<ProgresoEjercicioDAO.ProgresoDato>) request.getAttribute("viewerSeries");

    if (ownerSeries == null) ownerSeries = java.util.Collections.emptyList();
    if (viewerSeries == null) viewerSeries = java.util.Collections.emptyList();
  %>

  <!-- Encabezado -->
  <header class="panel cmp-header" id="blk-overview">
    <div class="panel__head">
      <h1 class="panel__title">Comparar progreso</h1>
      <div class="who">
        <span class="pill">Ejercicio ID: <%= ejId != null ? ejId : -1 %></span>
        <span class="pill">Rango: <%= range %></span>
      </div>
    </div>
    <p class="u-mt-8">
      <strong class="texto-dorado"><c:out value="${ownerDisplay}" /></strong>
      <span class="muted">vs</span>
      <strong><c:out value="${viewerDisplay}" /></strong>
    </p>

  </header>

  <section class="panel" id="blk-exercise">
    <div class="panel__head">
      <h2 class="panel__title">Progreso por ejercicio (comparación)</h2>
      <span class="texto-dorado">Fuerza &amp; volumen</span>
    </div>

    <!-- Placeholders IDENTICOS a la vista normal -->
    <div class="subgrid u-mt-12">
      <article class="chart-card">
        <h3 class="chart-card__title">Tendencia de fuerza (e1RM)</h3>
        <div class="chart-inset">
          <small id="ph-e1rm">Gráfico de líneas aquí</small>
        </div>
      </article>

      <article class="chart-card">
        <h3 class="chart-card__title">Volumen &amp; frecuencia (semanal)</h3>
        <div class="chart-inset">
          <small id="ph-volume">Barras/mixto aquí</small>
        </div>
      </article>

      <article class="chart-card">
        <h3 class="chart-card__title">Curva carga–reps</h3>
        <div class="chart-inset">
          <small id="ph-scatter">Scatter aquí</small>
        </div>
      </article>
    </div>
  </section>


</main>
<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/compararProgreso.js" defer></script>
</body>
</html>

