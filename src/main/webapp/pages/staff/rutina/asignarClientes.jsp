<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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

                                <p class="cliente-rutina-card__ci">CI: ${clienteCard.clienteCi}</p>

                                <c:choose>
                                    <c:when test="${not empty clienteCard.rutinas}">

                                        <p class="cliente-rutina-card__label">Rutina(s) Activa(s):</p>

                                        <ul class="cliente-rutina-card__lista">
                                            <c:forEach items="${clienteCard.rutinas}" var="rutina">
                                                <li class="cliente-rutina-card__lista-item">
                                                        ${rutina.rutinaNombre}
                                                    <span class="cliente-rutina-card__lista-fecha">
                                                        <c:if test="${not empty rutina.fechaAsignacion}">
                                                            (Asignada:
                                                            <fmt:parseDate value="${rutina.fechaAsignacion}" pattern="yyyy-MM-dd" var="fechaParseada" />
                                                            <fmt:formatDate value="${fechaParseada}" pattern="dd/MM/yyyy" />)
                                                        </c:if>
                                                    </span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="cliente-rutina-card__label cliente-rutina-card__label--sin-rutina">
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

        <h2 class="titillium-negra">Gestionar Rutinas de <br><span id="modal-cliente-nombre" style="color: var(--color-principal);"></span></h2>

        <input type="hidden" id="modal-cliente-id-hidden">

        <div class="modal-layout-split">

            <div class="modal-columna">
                <h4>Rutina(s) Activa(s) Actuales</h4>
                <div id="modal-rutinas-actuales" class="lista-scroll-modal">
                </div>
            </div>

            <div class="modal-columna">
                <h4>Asignar Nueva Rutina</h4>

                <div class="modal-filtros-grid">
                    <div class="filtro-item-modal">
                        <label for="modal-filtro-rutina" class="label-sm">Buscar por Nombre:</label>
                        <input type="text" id="modal-filtro-rutina" class="control" placeholder="Ej: Full body...">
                    </div>

                    <div class="filtro-item-modal">
                        <label for="modal-filtro-objetivo" class="label-sm">Filtrar por Objetivo:</label>
                        <select id="modal-filtro-objetivo" class="control">
                            <option value="todos">Todos los objetivos</option>
                            <option value="HIPERTROFIA">Hipertrofia</option>
                            <option value="FUERZA">Fuerza</option>
                            <option value="TONIFICAR">Tonificar</option>
                            <option value="RESISTENCIA">Resistencia</option>
                            <option value="PERDIDA_PESO">Pérdida de Peso</option>
                        </select>
                    </div>

                    <div class="filtro-item-modal">
                        <label for="modal-filtro-duracion" class="label-sm">Filtrar por Duración:</label>
                        <select id="modal-filtro-duracion" class="control">
                            <option value="todas">Todas las duraciones</option>
                            <option value="4">4 semanas</option>
                            <option value="6">6 semanas</option>
                            <option value="8">8 semanas</option>
                            <option value="12">12 semanas</option>
                        </select>
                    </div>
                </div>

                <div id="modal-rutinas-disponibles" class="lista-scroll-modal">
                </div>

                <div id="modal-paginacion-controles" class="biblioteca-paginacion" style="display: none;">
                    <button type="button" id="modal-anterior" class="btn-paginacion">‹</button>
                    <span id="modal-contador">Pág 1 / 1</span>
                    <button type="button" id="modal-siguiente" class="btn-paginacion">›</button>
                </div>

                <div class="error" id="error-rutina" style="margin-top: 0.5rem; min-height: 1.1em;"></div>
            </div>
        </div>

        <form id="form-asignar-rutina" class="form" style="margin-top: 1rem;">

            <input type="hidden" id="modal-rutinas-ids-seleccionadas" name="rutinasIds" value="">

            <div class="field">
                <label for="modal-fecha-asignacion" class="label">Fecha de Inicio (para la nueva rutina):</label>
                <input type="date" id="modal-fecha-asignacion" name="fechaAsignacion" class="control" required>
                <div class="error" id="error-fecha"></div>
            </div>

            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 8px;">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                        <polyline points="17 21 17 13 7 13 7 21"></polyline>
                        <polyline points="7 3 7 8 15 8"></polyline>
                    </svg>
                    Guardar Asignación
                </button>
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