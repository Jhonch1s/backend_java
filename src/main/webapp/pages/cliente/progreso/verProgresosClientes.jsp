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
    <title>Progresos Ejercicio · Golden Gym</title>

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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/ProgresoClientes.css">



</head>

<body class="fondo-oscuro texto-claro">
<main class="main-panel u-mx-auto u-p-24">

    <!-- Encabezado -->
    <section class="alinear-centro-between u-mb-24 pr-item">
        <h2 class="titillium-negra texto-dorado">Progreso de Ejercicios</h2>

        <button id="btnAddProgreso" class="boton-primario" type="button">+ Añadir Progreso</button>
    </section>

    <!-- Filtro -->
    <section class="alinear-centro-between u-mb-24 arrow">
        <div class="filtros-izquierda">

        </div>
        <div class="filtros-derecha">
            <button id="btnFiltroFecha"
                    class="btn-accion"
                    data-order="${ordenActual == 'asc' ? 'asc' : 'desc'}"
                    title="Ordenar por fecha">
                <svg class="icono" height="20px" width="20px" id="svg-filtro" xmlns="http://www.w3.org/2000/svg"
                     viewBox="0 0 512 512"><g id="SVGRepo_bgCarrier" stroke-width="0"></g>
                    <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
                    <g id="SVGRepo_iconCarrier">
                        <polygon
                                points="247.5,0 34.2,213.3 34.2,341.3 204.8,170.7 204.8,512 290.2,512 290.2,170.7 460.8,341.3 460.8,213.3 "></polygon>
                    </g></svg>
            </button>
        </div>
    </section>

    <!-- Lista de progresos -->
    <section class="lista-progresos">
        <c:forEach var="p" items="${listaProgresos}">
            <article class="progreso-item tarjeta borde-redondeado sombra-suave p-3 u-mb-24 alinear-centro-between"
                     data-ejercicio="${p.idEjercicio}"
                     data-fecha="<fmt:formatDate value='${p.fecha}' pattern='yyyy-MM-dd'/>">
            <div class="progreso-item__info">
                    <!-- Mostrar nombre del ejercicio -->
                    <c:forEach var="e" items="${listaEjercicios}">
                        <c:if test="${e.id == p.idEjercicio}">
                            <h3 class="m-0">${e.nombre}</h3>
                        </c:if>
                    </c:forEach>

                    <p class="m-0 mt-1 texto-dorado p">
                        Fecha:
                        <fmt:formatDate value="${p.fecha}" pattern="dd/MM/yyyy"/>
                    </p>
                <div class="kdata">
                    <p class="m-0 mt-1">Peso usado: <strong>${p.pesoUsado}</strong> kg</p>
                    <p class="m-0">Repeticiones: <strong>${p.repeticiones}</strong></p>
                </div>
            </div>

                <div class="progreso-item__acciones alinear-centro gap-2">
                    <button class="btn-accion" id="svg-editar" data-id="${p.id}" data-action="editar" title="Editar">
                        <svg class="icono" width="20px" height="20px" viewBox="0 0 16 16" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                            <g id="SVGRepo_bgCarrier" stroke-width="1"></g>
                            <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
                            <g id="SVGRepo_iconCarrier">
                                <path d="M8.29289 3.70711L1 11V15H5L12.2929 7.70711L8.29289 3.70711Z"
                                ></path>
                                <path d="M9.70711 2.29289L13.7071 6.29289L15.1716 4.82843C15.702 4.29799 16 3.57857 16 2.82843C16 1.26633 14.7337 0 13.1716 0C12.4214 0 11.702 0.297995 11.1716 0.828428L9.70711 2.29289Z">
                                </path>
                            </g>
                        </svg>
                    </button>

                    <button class="btn-accion" data-id="${p.id}" data-action="eliminar" title="Eliminar">
                        <svg class="icono" id="svg-eliminar"
                             width="20px" height="20px"
                             viewBox="0 0 24 24"
                             fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                            <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
                            <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
                            <g id="SVGRepo_iconCarrier">
                                <path d="M3 6.38597C3 5.90152 3.34538 5.50879 3.77143 5.50879L6.43567 5.50832C6.96502 5.49306 7.43202 5.11033 7.61214 4.54412C7.61688 4.52923 7.62232 4.51087 7.64185 4.44424L7.75665 4.05256C7.8269 3.81241 7.8881 3.60318 7.97375 3.41617C8.31209 2.67736 8.93808 2.16432 9.66147 2.03297C9.84457 1.99972 10.0385 1.99986 10.2611 2.00002H13.7391C13.9617 1.99986 14.1556 1.99972 14.3387 2.03297C15.0621 2.16432 15.6881 2.67736 16.0264 3.41617C16.1121 3.60318 16.1733 3.81241 16.2435 4.05256L16.3583 4.44424C16.3778 4.51087 16.3833 4.52923 16.388 4.54412C16.5682 5.11033 17.1278 5.49353 17.6571 5.50879H20.2286C20.6546 5.50879 21 5.90152 21 6.38597C21 6.87043 20.6546 7.26316 20.2286 7.26316H3.77143C3.34538 7.26316 3 6.87043 3 6.38597Z"
                                     ></path>
                                <path fill-rule="evenodd" clip-rule="evenodd"
                                      d="M11.5956 22.0001H12.4044C15.1871 22.0001 16.5785 22.0001 17.4831 21.1142C18.3878 20.2283 18.4803 18.7751 18.6654 15.8686L18.9321 11.6807C19.0326 10.1037 19.0828 9.31524 18.6289 8.81558C18.1751 8.31592 17.4087 8.31592 15.876 8.31592H8.12404C6.59127 8.31592 5.82488 8.31592 5.37105 8.81558C4.91722 9.31524 4.96744 10.1037 5.06788 11.6807L5.33459 15.8686C5.5197 18.7751 5.61225 20.2283 6.51689 21.1142C7.42153 22.0001 8.81289 22.0001 11.5956 22.0001ZM10.2463 12.1886C10.2051 11.7548 9.83753 11.4382 9.42537 11.4816C9.01321 11.525 8.71251 11.9119 8.75372 12.3457L9.25372 17.6089C9.29494 18.0427 9.66247 18.3593 10.0746 18.3159C10.4868 18.2725 10.7875 17.8856 10.7463 17.4518L10.2463 12.1886ZM14.5746 11.4816C14.9868 11.525 15.2875 11.9119 15.2463 12.3457L14.7463 17.6089C14.7051 18.0427 14.3375 18.3593 13.9254 18.3159C13.5132 18.2725 13.2125 17.8856 13.2537 17.4518L13.7537 12.1886C13.7949 11.7548 14.1625 11.4382 14.5746 11.4816Z"
                                      ></path>
                            </g>
                        </svg>
                    </button>
                </div>
            </article>
        </c:forEach>

        <c:if test="${empty listaProgresos}">
            <div class="tarjeta borde-redondeado sombra-suave p-3 alinear-centro-center">
                <p>No hay registros de progreso todavía. Agrega tu primer medicion</p>
            </div>
        </c:if>
    </section>

</main>
<!-- Modal Añadir Progreso -->
<div id="modalNuevo" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <button class="modal-cerrar" title="Cerrar">
            <svg viewBox="0 0 24 24" >
                <path fill="currentColor"
                      d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7a1 1 0 1 0-1.41 1.41L10.59 12l-4.9 4.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.9a1 1 0 0 0 1.41-1.41L13.41 12l4.9-4.89a1 1 0 0 0-.01-1.4z"/>
            </svg>
        </button>

        <h3 class="texto-dorado">Nuevo Progreso</h3>
        <form method="post" action="${pageContext.request.contextPath}/cliente/progreso" class="u-mt-16">
            <input type="hidden" name="accion" value="add">

            <label>Ejercicio</label>
            <select name="idEjercicio" class="plan-create__control input u-w-full" required>
                <c:forEach var="ej" items="${listaEjercicios}">
                    <option value="${ej.id}">${ej.nombre}</option>
                </c:forEach>
            </select>

            <label class="u-mt-16">Fecha</label>
            <input type="date" name="fecha" class="plan-create__control input u-w-full"  max="<%= java.time.LocalDate.now() %>" required>

            <label class="u-mt-16">Peso usado (kg)</label>
            <input type="number" step="0.1" name="pesoUsado" class="plan-create__control input u-w-full" required>
            <label class="u-mt-16">Repeticiones</label>
            <input type="number" name="repeticiones" class="plan-create__control input u-w-full" required>
            <p class="input-error oculto"></p>

            <div class="alinear-centro-between u-mt-24">
                <button type="submit" class="boton-primario">Guardar</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Editar Progreso -->
<div id="modalEditar" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <button class="modal-cerrar" title="Cerrar">
            <svg viewBox="0 0 24 24" >
                <path fill="currentColor"
                      d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7a1 1 0 1 0-1.41 1.41L10.59 12l-4.9 4.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.9a1 1 0 0 0 1.41-1.41L13.41 12l4.9-4.89a1 1 0 0 0-.01-1.4z"/>
            </svg>
        </button>
        <h3 class="texto-dorado">Editar Progreso</h3>
        <form id="formEditar" method="post" action="${pageContext.request.contextPath}/cliente/progreso" class="u-mt-16">
            <input type="hidden" name="accion" value="edit">
            <input type="hidden" name="id" id="editId">

            <label>Ejercicio</label>
            <select name="idEjercicio" id="editEjercicio" class="plan-create__control input u-w-full" required>
                <c:forEach var="ej" items="${listaEjercicios}">
                    <option value="${ej.id}">${ej.nombre}</option>
                </c:forEach>
            </select>

            <label>Fecha</label>
            <input type="date" name="fecha" id="editFecha" class="plan-create__control input u-w-full"  max="<%= java.time.LocalDate.now() %>" required>

            <label class="u-mt-16">Peso usado (kg)</label>
            <input type="number" name="pesoUsado" id="editPeso" class="plan-create__control input u-w-full"  required>

            <label class="u-mt-16">Repeticiones</label>
            <input type="number" name="repeticiones" id="editReps" class="plan-create__control input u-w-full"  required>
            <p class="input-error oculto"></p>

            <div class="alinear-centro-between u-mt-24">
                <button type="submit" class="boton-primario">Guardar Cambios</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Eliminar Progreso -->
<div id="modalEliminar" class="modal oculto">
    <div class="modal__contenido tarjeta borde-redondeado sombra-suave p-3">
        <button class="modal-cerrar" title="Cerrar">
            <svg viewBox="0 0 24 24" >
                <path fill="currentColor"
                      d="M18.3 5.71a1 1 0 0 0-1.41 0L12 10.59 7.11 5.7a1 1 0 1 0-1.41 1.41L10.59 12l-4.9 4.89a1 1 0 1 0 1.41 1.41L12 13.41l4.89 4.9a1 1 0 0 0 1.41-1.41L13.41 12l4.9-4.89a1 1 0 0 0-.01-1.4z"/>
            </svg>
        </button>
        <h3 class="texto-dorado">¿Eliminar este progreso?</h3>
        <p>Esta accion no se puede deshacer.</p>
        <form method="post" action="${pageContext.request.contextPath}/cliente/progreso">
            <input type="hidden" name="accion" value="delete">
            <input type="hidden" name="id" id="deleteId">
            <div class="alinear-centro-between u-mt-24">
                <button type="submit" class="boton-primario">Eliminar</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/progresoCliente.js" defer></script>

</body>
</html>
