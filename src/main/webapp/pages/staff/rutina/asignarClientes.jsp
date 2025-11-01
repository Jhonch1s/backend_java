<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Asignar Rutinas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/forms-staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/asignarClientes.css">
</head>
<body class="layout">
<jsp:include page="/pages/modulos/aside-nav-staff.jsp" />

<main class="layout__content">
    <section class="view">
        <header class="view__header">
            <h1 class="view__title titillium-negra">Asignar Rutinas a Clientes</h1>
            <p class="view__sub">Verificá la rutina actual de cada cliente y asigná una nueva.</p>
        </header>

        <div class="filtros-container card" style="padding: 1.25rem;">
            <div class="filtros-grid">
                <div class="filtro-item">
                    <label for="filtro-ci" class="label">Buscar por Cédula (CI):</label>
                    <input type="text" id="filtro-ci" class="control" placeholder="Ej: 5123456...">
                </div>
                <div class="filtro-item">
                    <label for="filtro-estado-rutina" class="label">Mostrar Clientes:</label>
                    <select id="filtro-estado-rutina" class="control">
                        <option value="todos">Mostrar Todos</option>
                        <option value="con_activa">Con Rutina Activa</option>
                        <option value="sin_activa">Sin Rutina Activa</option>
                    </select>
                </div>
            </div>
        </div>

        <section id="lista-clientes-rutinas" class="rutina-list" style="margin-top: 1.5rem;">
            <c:choose>
                <c:when test="${not empty listaClientesRutinas}">
                    <c:forEach items="${listaClientesRutinas}" var="clienteCard">

                        <div class="rutina-card cliente-rutina-card"
                             data-nombre="${clienteCard.clienteNombre} ${clienteCard.clienteApellido}"
                             data-ci="${clienteCard.clienteCi}"
                             data-estado-rutina="${not empty clienteCard.rutinas ? 'con_activa' : 'sin_activa'}">

                            <div class="rutina-card__info">
                                <h3 class="m-0 titillium-negra">${clienteCard.clienteNombre} ${clienteCard.clienteApellido}</h3>
                                <p class="m-0 mt-1" style="color: var(--gg-text-muted);">CI: ${clienteCard.clienteCi}</p>

                                <c:choose>
                                    <c:when test="${not empty clienteCard.rutinas}">
                                        <p class="m-0 mt-2" style="font-weight: 600;">Rutina(s) Activa(s):</p>

                                        <ul style="padding-left: 20px; margin: 0.25rem 0 0; font-size: 0.9rem;">
                                            <c:forEach items="${clienteCard.rutinas}" var="rutina">
                                                <li style="color: var(--color-principal); margin-bottom: 0.25rem;">
                                                        ${rutina.rutinaNombre}
                                                    <span style="color: var(--gg-text-muted); font-size: 0.85rem;">
                                                        (Asignada: ${rutina.fechaAsignacion})
                                                    </span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="m-0 mt-2" style="font-weight: 600; color: var(--color-peligro);">
                                            Sin rutina activa
                                        </p>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="rutina-card__actions">
                                <button class="btn btn--sm btn--ejercicios btn-abrir-modal"
                                        data-cliente-id="${clienteCard.clienteCi}"
                                        data-cliente-nombre="${clienteCard.clienteNombre} ${clienteCard.clienteApellido}">
                                    Asignar / Cambiar
                                </button>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="view__sub">No se encontraron clientes.</p>
                </c:otherwise>
            </c:choose>
        </section>
        <div id="paginacion-controles" class="paginacion-controles">
            <button id="btn-anterior" class="btn btn--primary">‹ Anterior</button>
            <span id="contador-pagina"></span>
            <button id="btn-siguiente" class="btn btn--primary">Siguiente ›</button>
        </div>
    </section>
</main>

<div id="modal-asignar-rutina" class="modal">
    <div class="modal-contenido">
        <button id="btn-cerrar-modal" class="modal-cerrar" aria-label="Cerrar modal">
            <svg class="icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
        <h2 class="titillium-negra">Asignar Rutina a <br><span id="modal-cliente-nombre" style="color: var(--color-principal);"></span></h2>

        <form id="form-asignar-rutina" class="form">
            <input type="hidden" id="modal-cliente-id" name="clienteId">
            <div class="field">
                <label for="modal-select-rutina" class="label">Nueva Rutina:</label>
                <select id="modal-select-rutina" name="rutinaId" class="control" required>
                    <option value="" disabled selected>Cargando rutinas...</option>
                </select>
                <div class="error" id="error-rutina"></div>
            </div>
            <div class="field">
                <label for="modal-fecha-asignacion" class="label">Fecha de Inicio:</label>
                <input type="date" id="modal-fecha-asignacion" name="fechaAsignacion" class="control" required>
                <div class="error" id="error-fecha"></div>
            </div>
            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">Guardar Asignación</button>
            </div>
            <p id="feedback-asignacion" class="modal-feedback"></p>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/assets/js/staff/asignarClientes.js"></script>
</body>
</html>