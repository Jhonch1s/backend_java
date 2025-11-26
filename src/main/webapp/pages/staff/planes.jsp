<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover"/>
    <title>Planes</title>

    <!-- Favicons -->
    <link rel="apple-touch-icon" sizes="180x180"
          href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32"
          href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16"
          href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
    <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/manifest.json">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">


    <!-- Tipografía + Normalize -->
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap"
          rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
          crossorigin="anonymous" referrerpolicy="no-referrer"/>

    <!-- Estilos base existentes -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/cliente-movimientos.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/forms-staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/planes.css">
</head>

<body class="layout vista--plan-administrar">
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>

<main class="u-maxw-1100 u-mx-auto u-p-32 main-panel" tabindex="-1">
    <header class="view-header">
        <div>
            <h1 class="view__title">Planes de Membresía</h1>
            <p class="view__sub">Gestiona los planes disponibles.</p>
        </div>
        <button id="btnNuevoPlan" class="btn btn--primary">Crear Nuevo Plan</button>
    </header>

    <!-- Tabla -->
    <c:choose>
        <c:when test="${not empty listaPlanes}">
            <div class="tabla-wrapper u-mt-24">
                <table class="tabla-datos">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Precio</th>
                        <th>Duracion</th>
                        <th>Imagen</th>
                        <th>Estado</th>
                        <th class="u-text-right">Acciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${listaPlanes}">
                        <tr class="fila-plan"
                            data-id="${p.id}"
                            data-nombre="${p.nombre}"
                            data-valor="${p.valor}"
                            data-cantidad="${p.duracionTotal}"
                            data-unidad="${p.duracionUnidadId}"
                            data-estado="${p.estado}"
                            data-urlimagen="${pageContext.request.contextPath}${p.urlImagen}">
                            <td><span class="tabla-entidades__id">${p.id}</span></td>
                            <td><strong>${p.nombre}</strong></td>
                            <td>
                                $ <fmt:formatNumber value="${p.valor}" type="number" minFractionDigits="2"
                                                    maxFractionDigits="2"/>
                            </td>
                            <td>${p.duracionTotal} ${p.duracionUnidadNombre}</td>
                            <td class="col-imagen">
                                <div class="plan-thumb">
                                    <c:choose>
                                        <c:when test="${not empty p.urlImagen}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(p.urlImagen, 'http')}">
                                                    <img src="${p.urlImagen}" alt="${p.nombre}" class="plan-img-clickable">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}${p.urlImagen}" alt="${p.nombre}" class="plan-img-clickable">
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/img/plan-default.jpg" alt="Sin imagen" class="plan-img-clickable">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                            <td>
                                    <span class="badge ${p.estado ? 'badge--activo' : 'badge--inactivo'}">
                                            ${p.estado ? 'Activo' : 'Inactivo'}
                                    </span>
                            </td>
                            <td class="u-text-right">
                                <button class="btn btn--ghost btn--editar" title="Editar">
                                    Editar
                                </button>
                                <button class="btn btn--peligro btn--estado"
                                        title="${p.estado ? 'Desactivar' : 'Activar'}">
                                    Cambiar estado
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p style="text-align:center; margin-top:2rem;">No hay planes cargados todavía.</p>
        </c:otherwise>
    </c:choose>
</main>
</div>

<!-- Modal Nuevo Plan -->
<div id="modalNuevo" class="modal oculto" role="dialog">
    <div class="modal__contenido borde-redondeado sombra-suave tarjeta">
        <button class="modal-cerrar">✖</button>
        <h3 id="titulo-modal-nuevo" class="titillium-negra texto-dorado" style="margin:0 0 1rem;">Crear Plan</h3>

        <form id="formNuevo" action="${pageContext.request.contextPath}/staff/planes" method="post" novalidate
              class="form-grid" enctype="multipart/form-data">
            <input type="hidden" name="accion" value="add"/>
            <div class="group-2">
                <label>Nombre
                    <input type="text" name="nombre" class="plan-create__control" placeholder="Ej: Plan Premium"
                           required>
                </label>
                <label>Precio ($)
                    <input type="number" min="0" name="valor" class="plan-create__control"
                           placeholder="0.00" required>
                </label>
            </div>
            <div class="group-2">
                <label>Cantidad
                    <input type="number" min="1" name="cantidad" class="plan-create__control" value="1"
                           required>
                </label>
                <label>Unidad
                    <select name="unidad" class="plan-create__control" required>
                        <option value="" disabled selected>Seleccionar…</option>
                        <c:forEach var="u" items="${unidadesDuracion}">
                            <option value="${u.id}">${u.nombre}</option>
                        </c:forEach>
                    </select>
                </label>
            </div>

            <div class="group-2">
                <div>
                    <label>Reemplazar imagen:
                        <input type="file" name="imagen" id="editFile" accept="image/*">
                    </label>
                    <small class="help">JPG/PNG, max 5MB.</small>
                </div>
            </div>

            <label class="toggle" for="nuevoActivo">
                <span class="toggle__label">Plan activo</span>
                <input type="checkbox" id="nuevoActivo" name="activo" checked>
                <span class="toggle__track" aria-hidden="true"><span class="toggle__thumb">
                </span>
                </span>
            </label>
            <p class="input-error oculto"></p>

            <div class="acciones">
                <button type="submit" class="btn btn--primary">Crear</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Editar Plan -->
<div id="modalEditar" class="modal oculto" role="dialog">
    <div class="modal__contenido borde-redondeado sombra-suave tarjeta">
        <button class="modal-cerrar">✖</button>
        <h3 id="titulo-modal-editar" class="titillium-negra texto-dorado" style="margin:0 0 1rem;">Editar Plan</h3>

        <form id="formEditar" action="${pageContext.request.contextPath}/staff/planes" method="post" novalidate
              class="form-grid" enctype="multipart/form-data">
            <input type="hidden" name="accion" value="edit"/>
            <input type="hidden" name="id" id="editId"/>

            <div class="group-2">
                <label>Nombre
                    <input type="text" name="nombre" id="editNombre" class="plan-create__control" required>
                </label>
                <label>Precio ($)
                    <input type="number" min="0" name="valor" id="editValor" class="plan-create__control"
                           required>
                </label>
            </div>
            <div class="group-2">
                <label>Cantidad
                    <input type="number" min="1" name="cantidad" id="editCantidad" class="plan-create__control"
                           required>
                </label>
                <label>Unidad
                    <select name="unidad" id="editUnidad" class="plan-create__control" required>
                        <option value="" disabled>Seleccionar…</option>
                        <c:forEach var="u" items="${unidadesDuracion}">
                            <option value="${u.id}">${u.nombre}</option>
                        </c:forEach>
                    </select>
                </label>
            </div>

            <div class="group-2">
                <div>
                    <label>Reemplazar imagen:
                        <input type="file" name="imagen" id="editFile" accept="image/*">
                    </label>
                    <small class="help">JPG/PNG, max 5MB.</small>
                </div>
            </div>

            <label class="toggle" for="editActivo">
                <span class="toggle__label">Plan activo</span>
                <input type="checkbox" id="editActivo" name="activo">
                <span class="toggle__track" aria-hidden="true"> <span class="toggle__thumb"></span>
                </span>
            </label>

            <p class="input-error oculto"></p>

            <div class="acciones">
                <button type="submit" class="btn btn--primary boton-primario">Guardar cambios</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Activar/Desactivar -->
<div id="modalEstado" class="modal oculto" role="dialog">
    <div class="modal__contenido borde-redondeado sombra-suave tarjeta">
        <button class="modal-cerrar">✖</button>
        <h3 id="titulo-modal-estado" class="titillium-negra texto-dorado" style="margin:0 0 1rem;">Confirmar</h3>

        <p id="msgEstado" class="u-mb-24">¿Cambiar estado del plan?</p>

        <form id="formEstado" action="${pageContext.request.contextPath}/staff/planes" method="post">
            <input type="hidden" name="accion" value="toggle"/>
            <input type="hidden" name="id" id="estadoId"/>
            <input type="hidden" name="toEstado" id="toEstado"/>
            <div class="acciones">
                <button type="submit" class="btn btn--primary boton-primario">Si, confirmar</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Imagen !-->
<div id="modalImagen" class="modal oculto" role="dialog" aria-hidden="true" aria-modal="true">
    <div class="modal__overlay"></div>
    <div class="modal__dialog">
        <button class="modal-cerrar" aria-label="Cerrar">✖</button>
        <img id="modalImagenVista" src="" alt="Vista ampliada">
    </div>
</div>

<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/assets/js/staff/planes.js" defer></script>
</body>
</html>
