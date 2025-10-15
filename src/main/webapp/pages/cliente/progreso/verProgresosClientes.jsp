<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 08/10/2025
  Time: 21:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
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

    <!-- rstilos para el dashboard cliente, forzamos actualizar estilos al cambiar css con ?v=20251011a-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css?v=20251011a">
</head>

<body class="fondo-oscuro texto-claro">
<main class="main-panel u-mx-auto u-p-24">

    <!-- Encabezado -->
    <section class="alinear-centro-between u-mb-24">
        <h2 class="titillium-negra texto-dorado">Progreso de Ejercicios</h2>
        <button id="btnAddProgreso" class="boton-primario" type="button">+ Añadir Progreso</button>
    </section>

    <!-- Lista de progresos -->
    <section class="lista-progresos">
        <c:forEach var="p" items="${listaProgresos}">
            <article class="progreso-item tarjeta borde-redondeado sombra-suave p-3 u-mb-24 alinear-centro-between">
                <div class="progreso-item__info">
<%--                    <h3 class="m-0">${p.ejercicioNombre}</h3>--%>
                    <p class="m-0 mt-1 texto-dorado">
                        Fecha: <fmt:formatDate value="${p.fecha}" pattern="dd/MM/yyyy"/>
                    </p>
                    <p class="m-0 mt-1">Peso usado: <strong>${p.pesoUsado}</strong> kg</p>
                    <p class="m-0">Repeticiones: <strong>${p.repeticiones}</strong></p>
                </div>

                <div class="progreso-item__acciones alinear-centro gap-2">
                    <button class="btn-accion" data-id="${p.id}" data-action="editar" title="Editar">⚙️</button>
                    <button class="btn-accion" data-id="${p.id}" data-action="eliminar" title="Eliminar">🗑️</button>
                </div>
            </article>
        </c:forEach>

        <c:if test="${empty listaProgresos}">
            <div class="tarjeta borde-redondeado sombra-suave p-3 alinear-centro-center">
                <p>No hay registros de progreso todavía. ¡Agrega tu primer medición!</p>
            </div>
        </c:if>
    </section>

</main>

<!-- MODAL: AÑADIR NUEVO PROGRESO -->
<div id="modalNuevo" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <h3 class="texto-dorado">Nuevo Progreso</h3>
        <form method="post" action="${pageContext.request.contextPath}/cliente/progreso" class="u-mt-16">
            <input type="hidden" name="action" value="add">

            <label>Ejercicio</label>
            <select name="idEjercicio" class="input u-w-full" required>
                <c:forEach var="ej" items="${listaEjercicios}">
                    <option value="${ej.id}">${ej.nombre}</option>
                </c:forEach>
            </select>

            <label class="u-mt-16">Fecha</label>
            <input type="date" name="fecha" class="input u-w-full" required>

            <label class="u-mt-16">Peso usado (kg)</label>
            <input type="number" step="0.1" name="pesoUsado" class="input u-w-full" required>

            <label class="u-mt-16">Repeticiones</label>
            <input type="number" name="repeticiones" class="input u-w-full" required>

            <div class="alinear-centro-between u-mt-24">
                <button type="button" class="btn btn--ghost" id="btnCancelarNuevo">Cancelar</button>
                <button type="submit" class="boton-primario">Guardar</button>
            </div>
        </form>
    </div>
</div>

<!-- MODAL: EDITAR PROGRESO -->
<div id="modalEditar" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <h3 class="texto-dorado">Editar Progreso</h3>
        <form id="formEditar" method="post" action="${pageContext.request.contextPath}/cliente/progreso" class="u-mt-16">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="id" id="editId">

            <label>Fecha</label>
            <input type="date" name="fecha" id="editFecha" class="input u-w-full" required>

            <label class="u-mt-16">Peso usado (kg)</label>
            <input type="number" step="0.1" name="pesoUsado" id="editPeso" class="input u-w-full" required>

            <label class="u-mt-16">Repeticiones</label>
            <input type="number" name="repeticiones" id="editReps" class="input u-w-full" required>

            <div class="alinear-centro-between u-mt-24">
                <button type="button" class="btn btn--ghost" id="btnCancelarEditar">Cancelar</button>
                <button type="submit" class="boton-primario">Guardar Cambios</button>
            </div>
        </form>
    </div>
</div>

<!-- MODAL: ELIMINAR -->
<div id="modalEliminar" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <h3 class="texto-dorado">¿Eliminar este progreso?</h3>
        <p>Esta acción no se puede deshacer.</p>
        <form method="post" action="${pageContext.request.contextPath}/cliente/progreso">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" id="deleteId">
            <div class="alinear-centro-between u-mt-24">
                <button type="button" class="btn btn--ghost" id="btnCancelarEliminar">Cancelar</button>
                <button type="submit" class="boton-primario">Eliminar</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/progresoCliente.js" defer></script>

<style>
    .btn-accion {
        background: transparent;
        border: none;
        color: var(--color-blanco);
        font-size: 1.4rem;
        cursor: pointer;
        transition: color .2s ease;
    }
    .btn-accion:hover { color: var(--color-principal); }

    .modal {
        position: fixed;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(0,0,0,0.7);
        z-index: 1000;
    }
    .modal.oculto { display: none; }
    .modal__contenido { width: min(90%, 400px); background: var(--gg-surface); }
    label { display: block; font-weight: 600; margin-bottom: .25rem; }
</style>

</body>
</html>
