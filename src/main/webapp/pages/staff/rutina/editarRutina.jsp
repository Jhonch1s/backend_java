<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editor de Rutina</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/forms-staff.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/editarRutina.css">
</head>
<body class="layout">
<jsp:include page="/pages/modulos/aside-nav-staff.jsp" />

<main class="layout__content">
    <section class="view">
        <header class="view__header">
            <h1 class="view__title titillium-negra">Editando: <span style="var(--color-principal)">${rutina.nombre}</span></h1>
            <p class="view__sub">Arrastrá ejercicios de la Biblioteca y soltalos en el día que quieras.</p>
        </header>

        <input type="hidden" id="rutina-id" value="${rutina.id}">

        <div class="constructor-layout">

            <div class="constructor-columna card">
                <h3>
                    <svg class="icon icon-book" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path>
                        <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path>
                    </svg>
                    <span>Biblioteca de Ejercicios</span>
                </h3>

                <div class="filtros-biblioteca">

                    <div class="filtro-item">
                        <label for="filtro-nombre-ej" class="label">Buscar por nombre:</label>
                        <input type="text" id="filtro-nombre-ej" class="control" placeholder="Ej: Press de banca...">
                    </div>

                    <div class="filtros-grid">

                        <div class="filtro-item">
                            <label for="filtro-grupo-ej" class="label">Grupo Muscular:</label>
                            <select id="filtro-grupo-ej" class="control">
                                <option value="">Todos</option>
                                <c:forEach items="${listaGruposMusculares}" var="grupo">
                                    <option value="${grupo.nombre}">${grupo.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="filtro-item">
                            <label for="filtro-dificultad-ej" class="label">Dificultad:</label>
                            <select id="filtro-dificultad-ej" class="control">
                                <option value="">Todas</option>
                                <option value="PRINCIPIANTE">Principiante</option>
                                <option value="INTERMEDIO">Intermedio</option>
                                <option value="AVANZADO">Avanzado</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div id="biblioteca-lista" class="lista-scroll">
                </div>
                <div class="biblioteca-paginacion">
                    <button id="biblio-anterior" class="btn-paginacion">‹ Ant</button>
                    <span id="biblio-contador">Pág 1 / 1</span>
                    <button id="biblio-siguiente" class="btn-paginacion">Sig ›</button>
                </div>
            </div>

            <div class="constructor-columna">

                <div id="dias-container" class="card">

                    <div class="card-header">

                        <nav class="builder__dias-tabs">
                            <button class="tab-dia activo" data-dia="LUNES">Lunes</button>
                            <button class="tab-dia" data-dia="MARTES">Martes</button>
                            <button class="tab-dia" data-dia="MIERCOLES">Miércoles</button>
                            <button class="tab-dia" data-dia="JUEVES">Jueves</button>
                            <button class="tab-dia" data-dia="VIERNES">Viernes</button>
                            <button class="tab-dia" data-dia="SABADO">Sábado</button>
                            <button class="tab-dia" data-dia="DOMINGO">Domingo</button>
                        </nav>

                        <button id="btn-guardar-rutina" class="btn btn--primary">
                            Guardar Rutina
                        </button>

                    </div>

                    <div class="dia-panel lista-scroll activo" id="dia-LUNES"></div>
                    <div class="dia-panel lista-scroll" id="dia-MARTES"></div>
                    <div class="dia-panel lista-scroll" id="dia-MIERCOLES"></div>
                    <div class="dia-panel lista-scroll" id="dia-JUEVES"></div>
                    <div class="dia-panel lista-scroll" id="dia-VIERNES"></div>
                    <div class="dia-panel lista-scroll" id="dia-SABADO"></div>
                    <div class="dia-panel lista-scroll" id="dia-DOMINGO"></div>
                </div>
        </div>
    </section>
</main>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="https://cdn.jsdelivr.net/npm/sortablejs@latest/Sortable.min.js"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
    const jsonDataEjercicios = ${not empty requestScope.jsonDataTodosEjercicios ? requestScope.jsonDataTodosEjercicios : '[]'};
    const jsonDataAsignados = ${not empty requestScope.jsonDataEjerciciosAsignados ? requestScope.jsonDataEjerciciosAsignados : '[]'};
</script>

<script src="${pageContext.request.contextPath}/assets/js/staff/editarRutina.js"></script>
</body>
</html>