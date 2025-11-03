<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Rutinas - Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@400;600;700;900&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/forms-staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/gestionRutinas.css">
</head>

<body class="layout vista--rutina-crear">

<jsp:include page="/pages/modulos/aside-nav-staff.jsp" />

<main class="layout__content">

    <section class="view">
        <header class="view__header">
            <h1 class="view__title titillium-negra">Gestión de Rutinas</h1>
            <p class="view__sub">Creá, editá o eliminá las plantillas de rutinas del gimnasio.</p>
        </header>

        <div class="admin-action-bar">
            <button id="btn-abrir-modal-crear" class="btn btn--primary">
                <svg xmlns="http://www.w3.org/2000/svg"
                     width="20"
                     height="20"
                     viewBox="0 0 24 24"
                     fill="none"
                     stroke="currentColor"
                     stroke-width="3"
                     stroke-linecap="round"
                     stroke-linejoin="round"
                     style="margin-right: 8px;">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                <span>Crear Nueva Rutina</span>
            </button>
        </div>

        <section class="filtros-container card">
            <div class="filtro-item filtro-item--nombre">
                <label for="filtro-nombre" class="label">Buscar por nombre:</label>
                <input type="text" id="filtro-nombre" class="control" placeholder="Ej: Full Body, PPL, etc.">
            </div>

            <div class="filtro-item">
                <label for="filtro-objetivo" class="label">Objetivo:</label>
                <select id="filtro-objetivo" class="control">
                    <option value="">Todos</option>
                    <option value="HIPERTROFIA">Hipertrofia</option>
                    <option value="FUERZA">Fuerza</option>
                    <option value="RESISTENCIA">Resistencia</option>
                    <option value="TONIFICAR">Tonificar</option>
                    <option value="PERDIDA_PESO">Pérdida de Peso</option>
                </select>
            </div>

            <div class="filtro-item">
                <label for="filtro-duracion" class="label">Duración:</label>
                <select id="filtro-duracion" class="control">
                    <option value="">Todas</option>
                    <option value="1-4">1-4 Semanas</option>
                    <option value="5-8">5-8 Semanas</option>
                    <option value="9-12">9-12 Semanas</option>
                </select>
            </div>
            <div class="filtro-item filtro-item--boton">
                <button type="button" id="btn-buscar-filtro" class="btn btn--primary">
                    <svg class="icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                    </svg>
                    <span>Buscar</span>
                </button>
            </div>
        </section>

        <div class="resultados-header">
            <span id="contador-resultados" class="view__sub"></span>
        </div>

        <section id="lista-rutinas-admin" class="rutina-list">
            <c:choose>
                <c:when test="${not empty listaTodasRutinas}">
                    <c:forEach items="${listaTodasRutinas}" var="rutina">
                        <div class="rutina-card"
                             data-id="${rutina.id}"
                             data-nombre="${rutina.nombre}"
                             data-objetivo="${rutina.objetivo}"
                             data-duracion="${rutina.duracionSemanas}">

                            <div class="rutina-card__info">
                                <h3 class="m-0 titillium-negra">${rutina.nombre}</h3>
                                <p class="m-0 mt-1">
                                    Objetivo:
                                    <c:choose>
                                        <c:when test="${rutina.objetivo == 'HIPERTROFIA'}">Hipertrofia</c:when>
                                        <c:when test="${rutina.objetivo == 'FUERZA'}">Fuerza</c:when>
                                        <c:when test="${rutina.objetivo == 'RESISTENCIA'}">Resistencia</c:when>
                                        <c:when test="${rutina.objetivo == 'TONIFICAR'}">Tonificar</c:when>
                                        <c:when test="${rutina.objetivo == 'PERDIDA_PESO'}">Pérdida de Peso</c:when>
                                        <c:otherwise>${rutina.objetivo}</c:otherwise>
                                    </c:choose>
                                    <c:if test="${rutina.duracionSemanas > 0}">
                                        | Duración: ${rutina.duracionSemanas} sem.
                                    </c:if>
                                </p>
                            </div>
                            <div class="rutina-card__actions">
                                <button class="btn btn--sm btn--detalle" data-id="${rutina.id}">Editar</button>
                                <a href="${pageContext.request.contextPath}/admin/editar-rutina?id=${rutina.id}" class="btn btn--sm btn--ejercicios">Ejercicios</a>
                                <button class="btn btn--sm btn--eliminar btn-eliminar-rutina" data-id="${rutina.id}">Eliminar</button>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="view__sub">No hay rutinas creadas todavía. ¡Creá la primera!</p>
                </c:otherwise>
            </c:choose>
        </section>

        <div id="paginacion-rutinas-controles" style="display: none;">
            <button id="btn-anterior-rutinas" class="btn btn--primary">
                <span class="texto-largo">← Anterior</span>
                <span class="texto-corto">←</span>
            </button>
            <div id="numeros-pagina-rutinas" class="paginacion-numeros"></div>
            <button id="btn-siguiente-rutinas" class="btn btn--primary">
                <span class="texto-largo">Siguiente →</span>
                <span class="texto-corto">→</span>
            </button>
        </div>
    </section>
</main>

<div id="modal-crear-rutina" class="modal">
    <div class="modal-contenido">
        <button id="btn-cerrar-modal" class="modal-cerrar" aria-label="Cerrar modal">
            <svg class="icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
        <h2>Crear Nueva Rutina</h2>

        <form id="form-crear-rutina" class="form">
            <div class="field">
                <label for="rutina-nombre" class="label">Nombre de la Rutina:</label>
                <input type="text" id="rutina-nombre" name="nombre" class="control" required placeholder="Ej: Tren Superior Semana 1">
                <div class="error" id="error-nombre"></div>
            </div>

            <div class="field">
                <label for="rutina-objetivo" class="label">Objetivo Principal:</label>
                <select id="rutina-objetivo" name="objetivo" class="control" required>
                    <option value="" disabled selected>Selecciona un objetivo...</option>
                    <option value="HIPERTROFIA">Hipertrofia</option>
                    <option value="FUERZA">Fuerza</option>
                    <option value="RESISTENCIA">Resistencia</option>
                    <option value="TONIFICAR">Tonificar</option>
                    <option value="PERDIDA_PESO">Pérdida de Peso</option>
                </select>
                <div class="error" id="error-objetivo"></div>
            </div>

            <div class="field">
                <label for="rutina-duracion" class="label">Duración (semanas):</label>
                <input type="number" id="rutina-duracion" name="duracionSemanas" class="control" min="1" placeholder="Opcional">
                <div class="error" id="error-duracion"></div>
            </div>

            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">Guardar y Cargar Ejercicios</button>
            </div>
            <p id="feedback-creacion" class="error modal-feedback"></p>
        </form>
    </div>
</div>

<div id="modal-editar-detalles" class="modal">
    <div class="modal-contenido">
        <button id="btn-cerrar-detalles" class="modal-cerrar" aria-label="Cerrar modal">
            <svg class="icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
        <h2 class="titillium-negra">Editando: <span id="nombre-rutina-modal-detalles"></span></h2>

        <form id="form-editar-detalles" class="form">
            <div class="field">
                <label for="edit-nombre" class="label">Nombre de la Rutina:</label>
                <input type="text" id="edit-nombre" name="nombre" class="control" required>
            </div>

            <div class="field">
                <label for="edit-objetivo" class="label">Objetivo Principal:</label>
                <select id="edit-objetivo" name="objetivo" class="control" required>
                    <option value="HIPERTROFIA">Hipertrofia</option>
                    <option value="FUERZA">Fuerza</option>
                    <option value="RESISTENCIA">Resistencia</option>
                    <option value="TONIFICAR">Tonificar</option>
                    <option value="PERDIDA_PESO">Pérdida de Peso</option>
                </select>
            </div>

            <div class="field">
                <label for="edit-duracion" class="label">Duración (semanas):</label>
                <input type="number" id="edit-duracion" name="duracionSemanas" class="control" min="1" placeholder="Opcional">
            </div>

            <input type="hidden" id="edit-id" name="id">

            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">Guardar Cambios</button>
            </div>
            <p id="feedback-detalles" class="error modal-feedback"></p>
        </form>
    </div>
</div>

<div id="modal-eliminar-rutina" class="modal">
    <div class="modal-contenido">
        <button id="btn-cerrar-eliminar" class="modal-cerrar" aria-label="Cerrar modal">
            <svg class="icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
        <h2 class="titillium-negra">Confirmar Eliminación</h2>

        <p style="text-align: center; font-size: 1.1rem; margin-top: 1rem;">
            ¿Estás seguro de que querés eliminar la rutina:
            <br>
            <strong id="nombre-rutina-eliminar" style="color: var(--color-peligro);"></strong>?
        </p>
        <p style="text-align: center; color: var(--gg-text-muted);">Esta acción no se puede deshacer.</p>

        <div class="form__actions" id="eliminar-actions">
            <button id="btn-cancelar-eliminar" class="btn btn--secondary btn--xl">Cancelar</button>
            <button id="btn-confirmar-eliminar" class="btn btn--eliminar btn--xl">Confirmar Eliminación</button>
        </div>
        <p id="feedback-eliminar" class="modal-feedback"></p>
    </div>
</div>

<script>const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/assets/js/staff/gestionRutinas.js" defer></script>
</body>
</html>