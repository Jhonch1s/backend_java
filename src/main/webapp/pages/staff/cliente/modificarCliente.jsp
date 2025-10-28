<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 24/10/2025
  Time: 16:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Modificar Cliente · Golden Gym</title>
  <!-- Favicons para dar iconos a la web si se usa desde móbil y se quiere anclar al inicio, etc. -->
  <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
  <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
  <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
  <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
  <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">


  <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@400;600;700;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/forms-staff.css">
</head>
<body class="layout vista--cliente-modificar" data-base="${pageContext.request.contextPath}">

<%@ include file="/pages/modulos/icons-sprite.jsp" %>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>

<main class="layout__content">

  <!-- CARD 1: BÚSQUEDA POR CI (APARTE, ARRIBA) -->
  <section class="view card client-lookup-card" aria-labelledby="lookup-title">
    <header class="card__header">
      <h2 id="lookup-title" class="view__title">Buscar cliente por CI</h2>
      <p class="view__sub">Escribe la CI para seleccionar el cliente a modificar.</p>
    </header>

    <div class="card__body client-lookup">
     <div class="lookup__control" data-state="idle">
        <input id="lookup-ci"
               type="text"
               class="control"
               placeholder="Escribe CI… (sin puntos ni guiones)"
               autocomplete="off"
               role="combobox"
               aria-expanded="false"
               aria-autocomplete="list"
               aria-controls="lookup-list"
               aria-describedby="lookup-hint" />
        <div class="lookup__spinner" aria-hidden="true" hidden>
          <svg width="16" height="16"><use href="#i-search"></use></svg>
        </div>
      </div>

      <small id="lookup-hint" class="hint">Usa ↑/↓ y Enter para seleccionar.</small>

      <ul id="lookup-list" class="lookup__list" role="listbox" aria-label="Resultados"></ul>
    </div>
  </section>

  <!-- CARD 2: FORM + RESUMEN (MISMO BLOQUE A DOS COLUMNAS) -->
  <section class="view card form-and-summary" aria-labelledby="edit-title">
    <header class="card__header">
      <h2 id="edit-title" class="view__title">Modificar Cliente</h2>
      <p class="view__sub">Selecciona arriba un cliente y edita sus datos.</p>
    </header>

    <!-- Grid a dos columnas DENTRO del mismo bloque/card -->
    <div class="card__body split">
      <!-- Columna izquierda: FORM -->
      <form id="form-modificar-cliente"
            class="form"
            action="${pageContext.request.contextPath}/api/clientes/modificar"
            method="post"
            novalidate>

        <div class="field">
          <input type="hidden" name="ci" id="ci-hidden" />
          <label for="cliente-ci" class="label">Cédula de Identidad</label>
          <input id="cliente-ci" name="ci" type="text" maxlength="20"
                 class="control" value="${cliente.ci}" readonly />
          <small class="hint">Identificador único (solo lectura).</small>
          <div class="error" id="error-cliente-ci"></div>
        </div>

        <div class="form__grid">
          <div class="field">
            <label for="cliente-email" class="label">Email</label>
            <input id="cliente-email" name="email" type="email" maxlength="120"
                   placeholder="cliente@dominio.com" class="control"
                   value="${cliente.email}" />
            <small class="hint">Correo de contacto.</small>
            <div class="error" id="error-cliente-email"></div>
          </div>

          <div class="field">
            <label for="cliente-telefono" class="label">Teléfono</label>
            <input id="cliente-telefono" name="telefono" type="tel" maxlength="30"
                   placeholder="+598 99 000 000" class="control"
                   value="${cliente.tel}" />
            <small class="hint">Número de contacto.</small>
            <div class="error" id="error-cliente-telefono"></div>
          </div>
        </div>

        <div class="form__grid">
          <div class="field">
            <label for="cliente-nombre" class="label">Nombre</label>
            <input id="cliente-nombre" name="nombre" type="text" maxlength="60"
                   placeholder="Nombre" class="control"
                   value="${cliente.nombre}" />
            <small class="hint">Nombre del cliente.</small>
            <div class="error" id="error-cliente-nombre"></div>
          </div>

          <div class="field">
            <label for="cliente-apellido" class="label">Apellido</label>
            <input id="cliente-apellido" name="apellido" type="text" maxlength="60"
                   placeholder="Apellido" class="control"
                   value="${cliente.apellido}" />
            <small class="hint">Apellido del cliente.</small>
            <div class="error" id="error-cliente-apellido"></div>
          </div>
        </div>

        <div class="form__grid">
          <div class="field">
            <label for="cliente-ciudad" class="label">Ciudad</label>
            <input id="cliente-ciudad" name="ciudad" type="text" maxlength="80"
                   placeholder="Ciudad" class="control"
                   value="${cliente.ciudad}" />
            <small class="hint">Ciudad de residencia.</small>
            <div class="error" id="error-cliente-ciudad"></div>
          </div>

          <div class="field">
            <label for="cliente-pais" class="label">País</label>
            <input id="cliente-pais" name="pais" type="text" maxlength="60"
                   placeholder="Uruguay" class="control"
                   value="${cliente.pais}" />
            <small class="hint">País de residencia.</small>
            <div class="error" id="error-cliente-pais"></div>
          </div>
        </div>

        <div class="field">
          <label for="cliente-direccion" class="label">Dirección</label>
          <input id="cliente-direccion" name="direccion" type="text" maxlength="160"
                 placeholder="Calle, número, apto" class="control"
                 value="${cliente.direccion}" />
          <small class="hint">Dirección de residencia.</small>
          <div class="error" id="error-cliente-direccion"></div>
        </div>

        <div class="field">
          <label for="cliente-fecha-ingreso" class="label">Fecha de Ingreso</label>
          <input id="cliente-fecha-ingreso" name="fecha_ingreso" type="date"
                 class="control" value="${cliente.fechaIngreso}" />
          <small class="hint">Fecha en la que se registró el cliente.</small>
          <div class="error" id="error-cliente-fecha-ingreso"></div>
        </div>

        <div class="form__actions">
          <button type="submit" class="btn btn--primary btn--xl">
            <svg class="icon" width="20" height="20" aria-hidden="true">
              <use href="#i-edit"></use>
            </svg>
            Guardar cambios
          </button>
        </div>
      </form>

      <!-- Columna derecha: RESUMEN (MISMA CARD) -->
      <aside class="client-summary" aria-live="polite" aria-busy="false">
        <div class="client-summary__header">
          <img class="client-summary__avatar"
               src="${pageContext.request.contextPath}/assets/img/user-placeholder.jpg"
               alt="Foto del cliente">
          <div>
            <div class="client-summary__name" id="sum-nombre">—</div>
            <div class="client-summary__meta" id="sum-ci">CI —</div>
          </div>
        </div>

        <!-- ====== Card de Membresía (tu diseño) ====== -->
        <article class="tarjeta mt-2">
          <div class="bloque__head">
            <h2 class="bloque__title m-0">Membresía</h2>
          </div>

          <!-- Imagen del plan -->
          <div class="mt-2">
            <img id="sum-membresia-img"
                 src="${pageContext.request.contextPath}/assets/img/plan-placeholder.jpg"
                 alt="Plan"
                 style="width:100%;max-height:160px;object-fit:cover;border-radius:12px;border:1px solid var(--gg-border);" />
          </div>

          <div class="membresia-grid mt-2">
            <p class="m-0 mt-2">
              Plan: <strong id="sum-plan-nombre">Sin plan</strong>
            </p>

            <p class="m-0 mt-1">
              Vence: <strong id="sum-membresia-vence">-</strong>
            </p>

            <!-- Badge de días restantes (el JS setea texto y, si querés, clases mini-badge--warning/--alerta) -->
            <p class="m-0 mt-1">
              <span id="sum-membresia-dias" class="mini-badge">-</span>
            </p>

            <p class="m-0 mt-1">
              Estado:
              <span id="sum-membresia-estado" class="">Sin membresía</span>
              <!-- el JS puede cambiar a chip-ok si es Activa -->
            </p>
          </div>
        </article>

        <!-- ====== KPIs ====== -->
        <div class="client-summary__kpis">
          <div class="kpi">
            <div class="kpi__label">Visitas mes</div>
            <div class="kpi__value" id="sum-visitas-mes">-</div>
          </div>
          <div class="kpi">
            <div class="kpi__label">Prom. minutos</div>
            <div class="kpi__value" id="sum-prom-minutos">-</div>
          </div>
          <div class="kpi">
            <div class="kpi__label">Entrenos totales</div>
            <div class="kpi__value" id="sum-total-entrenos">-</div>
          </div>
        </div>

        <!-- ====== Contacto ====== -->
        <div class="client-summary__contact">
          <p><strong>Email:</strong> <span id="sum-email">-</span></p>
          <p><strong>Teléfono:</strong> <span id="sum-tel">-</span></p>
          <p><strong>Dirección:</strong> <span id="sum-dir">-</span></p>
          <p><strong>Ciudad/Pais:</strong> <span id="sum-ciudad-pais">-</span></p>
          <p><strong>Ingreso:</strong> <span id="sum-ingreso">-</span></p>
        </div>
      </aside>


      <div id="toast-container" class="toast-container"></div>
</main>


<div id="toast-container" class="toast-container"></div>
<script src="${pageContext.request.contextPath}/assets/js/staff/clientes-modificar.js" defer></script>
</body>
</html>
