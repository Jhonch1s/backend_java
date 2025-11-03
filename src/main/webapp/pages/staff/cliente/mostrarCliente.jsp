<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 27/10/2025
  Time: 8:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Listar Clientes · Golden Gym</title>

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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/mostrarCliente.css" />
</head>
<body class="layout vista--cliente-mostrar"
      data-base="${pageContext.request.contextPath}">

<%@ include file="/pages/modulos/icons-sprite.jsp" %>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>

<main class="layout__content vista--cliente-modificar">

    <!-- Encabezado -->
    <header class="view__header">
        <div class="view__container">
        <h1 class="view__title m-0">Clientes</h1>
        <p class="view__sub">
            <c:choose>
                <c:when test="${not empty total}">
                    ${total} clientes – página ${page}/${pages}
                </c:when>
                <c:otherwise>
                    Listado general de clientes
                </c:otherwise>
            </c:choose>
        </p>
        </div>
        <div class="card client-lookup-card form__buscador">
            <div class="card__body alinear-centro gap-2">

                <c:url var="chipTodos" value="/staff/cliente/listar">
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                    <c:param name="estado" value="todos" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="dir" value="${dir}" />
                    <c:param name="page" value="${page}" />
                    <c:param name="size" value="${size}" />
                </c:url>

                <c:url var="chipActivos" value="/staff/cliente/listar">
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                    <c:param name="estado" value="activos" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="dir" value="${dir}" />
                    <c:param name="page" value="${page}" />
                    <c:param name="size" value="${size}" />
                </c:url>

                <c:url var="chipLt10" value="/staff/cliente/listar">
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                    <c:param name="estado" value="vencidos_lt10" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="dir" value="${dir}" />
                    <c:param name="page" value="${page}" />
                    <c:param name="size" value="${size}" />
                </c:url>

                <c:url var="chipGte10" value="/staff/cliente/listar">
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                    <c:param name="estado" value="vencidos_gte10" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="dir" value="${dir}" />
                    <c:param name="page" value="${page}" />
                    <c:param name="size" value="${size}" />
                </c:url>

                <a href="${chipTodos}" class="btn btn--ghost ${estado=='todos' ? 'is-active' : ''} btn__buscador">Todos</a>
                <a href="${chipActivos}" class="btn btn--ghost ${estado=='activos' ? 'is-active' : ''} btn__buscador">Activos</a>
                <a href="${chipLt10}"   class="btn btn--ghost ${estado=='vencidos_lt10' ? 'is-active' : ''} btn__buscador">Vencidos &lt; 10 días</a>
                <a href="${chipGte10}"  class="btn btn--ghost ${estado=='vencidos_gte10' ? 'is-active' : ''} btn__buscador">Vencidos ≥ 10 días</a>
            </div>
        </div>
    </header>

    <!-- Bloque de error (si hubo problemas en el servlet) -->
    <c:if test="${not empty errorMsg}">
        <div class="toast toast--error u-mb-24">
            <div class="toast__icon">⚠️</div>
            <div class="toast__msg"><c:out value="${errorMsg}" /></div>
            <c:if test="${not empty requestId}">
                <div class="toast__msg">ID: <code>${requestId}</code></div>
            </c:if>
            <button class="toast__close" onclick="this.closest('.toast').remove()">×</button>
        </div>
    </c:if>

    <div class="card client-lookup-card">
        <div class="card__body">
            <form action="${pageContext.request.contextPath}/staff/cliente/listar" method="get" class="form">
                <div class="form__grid">
                    <!-- Búsqueda -->
                    <div class="field">
                        <label class="label" for="q">Buscar</label>
                        <input class="control" type="search" id="q" name="q"
                               placeholder="Buscar por CI, email o Nombre Apellido"
                               value="${q}"/>
                        <p class="hint">CI exacta, email parcial/case-insensitive, nombre+apellido parcial.</p>
                    </div>

                    <!-- Ciudad -->
                    <div class="field">
                        <label class="label" for="ciudad">Ciudad</label>
                        <select class="control" id="ciudad" name="ciudad">
                            <option value="">Todas</option>
                            <c:forEach var="cdd" items="${ciudades}">
                                <option value="${cdd}" ${cdd == ciudad ? 'selected' : ''}>${cdd}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- País -->
                    <div class="field">
                        <label class="label" for="pais">País</label>
                        <select class="control" id="pais" name="pais">
                            <option value="">Todos</option>
                            <c:forEach var="pys" items="${paises}">
                                <option value="${pys}" ${pys == pais ? 'selected' : ''}>${pys}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- Fechas de ingreso -->
                    <div class="field">
                        <label class="label" for="fi_from">Desde</label>
                        <input class="control" type="date" id="fi_from" name="fi_from" value="${fi_from}"/>
                    </div>
                    <div class="field">
                        <label class="label" for="fi_to">Hasta</label>
                        <input class="control" type="date" id="fi_to" name="fi_to" value="${fi_to}"/>
                    </div>

                    <!-- Mantener chip de estado al buscar -->
                    <input type="hidden" name="estado" value="${estado}"/>
                    <!-- Mantener sort/dir -->
                    <input type="hidden" name="sort" value="${sort}"/>
                    <input type="hidden" name="dir"  value="${dir}"/>
                    <!-- Reiniciar a página 1 en búsquedas -->
                    <input type="hidden" name="page" value="1"/>
                    <input type="hidden" name="size" value="${size}"/>
                    <div class="form__actions">
                        <button type="submit" class="btn btn--xl">Buscar</button>

                        <c:url var="limpiarUrl" value="/staff/cliente/listar">
                            <c:param name="estado" value="${estado}" />
                            <c:param name="sort" value="${sort}" />
                            <c:param name="dir" value="${dir}" />
                            <c:param name="size" value="${size}" />
                            <c:param name="page" value="1" />
                        </c:url>
                        <a href="${limpiarUrl}" class="btn btn--ghost">Limpiar</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Tabla de clientes -->
    <div class="card">
        <div class="card__body">

            <!-- Helpers para sort: construir URLs por columna preservando filtros -->
            <c:set var="dirToggled" value="${dir == 'ASC' ? 'DESC' : 'ASC'}"/>

            <c:url var="sort_ci" value="/staff/cliente/listar">
                <c:param name="sort" value="ci"/><c:param name="dir" value="${sort=='ci' ? dirToggled : 'ASC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_nombre" value="/staff/cliente/listar">
                <c:param name="sort" value="nombre"/><c:param name="dir" value="${sort=='nombre' ? dirToggled : 'ASC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_email" value="/staff/cliente/listar">
                <c:param name="sort" value="email"/><c:param name="dir" value="${sort=='email' ? dirToggled : 'ASC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_ciudad" value="/staff/cliente/listar">
                <c:param name="sort" value="ciudad"/><c:param name="dir" value="${sort=='ciudad' ? dirToggled : 'ASC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_pais" value="/staff/cliente/listar">
                <c:param name="sort" value="pais"/><c:param name="dir" value="${sort=='pais' ? dirToggled : 'ASC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_ingreso" value="/staff/cliente/listar">
                <c:param name="sort" value="fecha_ingreso"/><c:param name="dir" value="${sort=='fecha_ingreso' ? dirToggled : 'DESC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <c:url var="sort_fin" value="/staff/cliente/listar">
                <c:param name="sort" value="fecha_fin_membresia"/><c:param name="dir" value="${sort=='fecha_fin_membresia' ? dirToggled : 'DESC'}"/>
                <c:param name="estado" value="${estado}"/><c:param name="q" value="${q}"/>
                <c:param name="ciudad" value="${ciudad}"/><c:param name="pais" value="${pais}"/>
                <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}"/></c:if>
                <c:param name="page" value="${page}"/><c:param name="size" value="${size}"/>
            </c:url>

            <table class="tabla-clientes u-w-full">
                <thead>
                <tr>
                    <th><a href="${sort_ci}">CI</a></th>
                    <th><a href="${sort_nombre}">Nombre</a></th>
                    <th><a href="${sort_email}">Email</a></th>
                    <th><a href="${sort_ciudad}">Ciudad</a></th>
                    <th><a href="${sort_pais}">País</a></th>
                    <th><a href="${sort_ingreso}">Ingreso</a></th>
                    <th><a href="${sort_fin}">Membresía</a></th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="cli" items="${clientes}">
                    <tr>
                        <td>${cli.ci}</td>
                        <td>${cli.nombre} ${cli.apellido}</td>
                        <td>${cli.email}</td>
                        <td>${cli.ciudad}</td>
                        <td>${cli.pais}</td>
                        <td><fmt:formatDate value="${cli.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${cli.bucketTemporalStr == 'ACTIVA'}">
                                    <span class="badge" style="color:var(--accent-success)">Activa</span>
                                </c:when>
                                <c:when test="${cli.bucketTemporalStr == 'VENCE_HOY'}">
                                    <span class="badge" style="color:var(--color-principal)">Vence hoy</span>
                                </c:when>
                                <c:when test="${cli.bucketTemporalStr == 'VENCIDA_LT10'}">
                                    <span class="badge" style="color:var(--color-rojo)">Venció hace ${cli.diasDesdeVencimiento} días</span>
                                </c:when>
                                <c:when test="${cli.bucketTemporalStr == 'VENCIDA_GTE10'}">
                                    <span class="badge" style="color:var(--gg-text-muted)">Vencida ≥ 10 días</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge" style="color:var(--gg-text-muted)">Sin membresía</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="acciones" style="white-space:nowrap">
                            <button type="button" class="btn btn--ghost btn--sm" data-ver-ci="${cli.ci}">
                                Ver
                            </button>
                            <!-- Editar -->
                            <a class="btn btn--ghost btn--sm"
                               href="${pageContext.request.contextPath}/staff/clientes/modificar?ci=${cli.ci}">
                                Editar
                            </a>
                            <c:choose>
                                <c:when test="${cli.whatsappSemaforoStr == 'VERDE' && not empty cli.telNormalizado}">
                                    <a class="icon-whatsapp verde"
                                       href="https://wa.me/${cli.telNormalizado}"
                                       target="_blank" title="Chat (membresía activa)">
                                        <img src="${pageContext.request.contextPath}/assets/img/svgs/whatsapp-green.svg" alt="WhatsApp" width="20">
                                    </a>
                                </c:when>

                                <c:when test="${cli.whatsappSemaforoStr == 'ROJO' && not empty cli.telNormalizado}">
                                    <a class="icon-whatsapp rojo"
                                       href="https://wa.me/${cli.telNormalizado}?text=${cli.mensajeReenganche}"
                                       target="_blank" title="Invitar a reingresar">
                                        <img src="${pageContext.request.contextPath}/assets/img/svgs/whatsapp-red.svg" alt="WhatsApp" width="20">
                                    </a>
                                </c:when>

                                <c:when test="${cli.whatsappSemaforoStr == 'AMARILLO' && not empty cli.telNormalizado}">
                                    <a class="icon-whatsapp amarillo"
                                       href="https://wa.me/${cli.telNormalizado}?text=${cli.mensajeReenganche}"
                                       target="_blank" title="Invitar a volver (recupero)">
                                        <img src="${pageContext.request.contextPath}/assets/img/svgs/whatsapp-yellow.svg" alt="WhatsApp" width="20">
                                    </a>
                                </c:when>

                                <c:otherwise>
                                    <span class="icon-whatsapp gris" title="Sin contacto">
                                        <img src="${pageContext.request.contextPath}/assets/img/svgs/whatsapp-gray.svg" alt="WhatsApp" width="20">
                                    </span>
                                </c:otherwise>
                            </c:choose>
                            <span class="u-hide" data-tel="${cli.telNormalizado}"></span>
                        </td>
                    </tr>
                </c:forEach>
                <span class="u-hide" data-tel="${cli.telNormalizado}"></span>
                </tbody>
            </table>

            <c:if test="${empty clientes}">
                <p class="u-mt-16">No se encontraron clientes que coincidan con los criterios seleccionados.</p>
                <c:url var="quitarFiltros" value="/staff/cliente/listar">
                    <c:param name="estado" value="${estado}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="dir" value="${dir}" />
                    <c:param name="size" value="${size}" />
                    <c:param name="page" value="1" />
                </c:url>
                <a href="${quitarFiltros}" class="btn btn--ghost u-mt-8">Quitar filtros</a>
            </c:if>
        </div>
    </div>

    <!-- paginacion -->
    <div class="card">
        <div class="card__body alinear-centro gap-2">
            <c:if test="${page > 1}">
                <c:url var="prevUrl" value="/staff/cliente/listar">
                    <c:param name="page" value="${page - 1}" /><c:param name="size" value="${size}" />
                    <c:param name="estado" value="${estado}" /><c:param name="sort" value="${sort}" /><c:param name="dir" value="${dir}" />
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                </c:url>
                <a href="${prevUrl}" class="btn btn--ghost">Anterior</a>
            </c:if>

            <span>Página ${page} de ${pages}</span>

            <c:if test="${page < pages}">
                <c:url var="nextUrl" value="/staff/cliente/listar">
                    <c:param name="page" value="${page + 1}" /><c:param name="size" value="${size}" />
                    <c:param name="estado" value="${estado}" /><c:param name="sort" value="${sort}" /><c:param name="dir" value="${dir}" />
                    <c:if test="${not empty q}"><c:param name="q" value="${q}" /></c:if>
                    <c:if test="${not empty ciudad}"><c:param name="ciudad" value="${ciudad}" /></c:if>
                    <c:if test="${not empty pais}"><c:param name="pais" value="${pais}" /></c:if>
                    <c:if test="${not empty fi_from}"><c:param name="fi_from" value="${fi_from}" /></c:if>
                    <c:if test="${not empty fi_to}"><c:param name="fi_to" value="${fi_to}" /></c:if>
                </c:url>
                <a href="${nextUrl}" class="btn btn--ghost">Siguiente</a>
            </c:if>

            <!-- Selector de tamaño -->
            <form action="${pageContext.request.contextPath}/staff/cliente/listar" method="get" class="alinear-centro gap-2" style="margin-left:12px">
                <input type="hidden" name="page" value="1"/>
                <input type="hidden" name="estado" value="${estado}"/>
                <input type="hidden" name="sort" value="${sort}"/>
                <input type="hidden" name="dir"  value="${dir}"/>
                <c:if test="${not empty q}"><input type="hidden" name="q" value="${q}"/></c:if>
                <c:if test="${not empty ciudad}"><input type="hidden" name="ciudad" value="${ciudad}"/></c:if>
                <c:if test="${not empty pais}"><input type="hidden" name="pais" value="${pais}"/></c:if>
                <c:if test="${not empty fi_from}"><input type="hidden" name="fi_from" value="${fi_from}"/></c:if>
                <c:if test="${not empty fi_to}"><input type="hidden" name="fi_to" value="${fi_to}"/></c:if>

                <label for="size" class="label">Tamaño</label>
                <select id="size" name="size" class="control" onchange="this.form.submit()">
                    <option value="20"  ${size==20  ? 'selected' : ''}>20</option>
                    <option value="50"  ${size==50  ? 'selected' : ''}>50</option>
                    <option value="100" ${size==100 ? 'selected' : ''}>100</option>
                </select>
            </form>
        </div>
    </div>


</main>
<!-- Modal resumen de cliente -->
<div id="modal-resumen" class="modal u-hide" aria-hidden="true">
    <div class="modal__overlay" data-close="1"></div>
    <div class="modal__dialog">
        <header class="modal__head">
            <h3 class="modal__title">Resumen del cliente</h3>
            <button type="button" class="modal__close" data-close="1">×</button>
        </header>
        <div class="modal__body">
            <!-- Reutilizamos tu aside tal cual -->
            <aside class="client-summary" aria-live="polite" aria-busy="false">
                <div class="client-summary__header">
                    <img class="client-summary__avatar" id="sum-avatar"
                         src="${pageContext.request.contextPath}/assets/img/user-placeholder.svg"
                         alt="Foto del cliente">
                    <div>
                        <div class="client-summary__name" id="sum-nombre">—</div>
                        <div class="client-summary__meta" id="sum-ci">CI —</div>
                    </div>
                </div>

                <article class="tarjeta mt-2">
                    <div class="bloque__head">
                        <h2 class="bloque__title m-0">Membresía</h2>
                    </div>

                    <div class="mt-2">
                        <img id="sum-membresia-img"
                             src="${pageContext.request.contextPath}/assets/img/plan-placeholder.png"
                             alt="Plan"
                             style="width:100%;max-height:160px;object-fit:cover;border-radius:12px;border:1px solid var(--gg-border);" />
                    </div>

                    <div class="membresia-grid mt-2">
                        <p class="m-0 mt-2">Plan: <strong id="sum-plan-nombre">Sin plan</strong></p>
                        <p class="m-0 mt-1">Vence: <strong id="sum-membresia-vence">-</strong></p>
                        <p class="m-0 mt-1"><span id="sum-membresia-dias" class="mini-badge">-</span></p>
                        <p class="m-0 mt-1">Estado: <span id="sum-membresia-estado">Sin membresía</span></p>
                    </div>
                </article>

                <div class="client-summary__kpis">
                    <div class="kpi"><div class="kpi__label">Visitas mes</div><div class="kpi__value" id="sum-visitas-mes">-</div></div>
                    <div class="kpi"><div class="kpi__label">Prom. minutos</div><div class="kpi__value" id="sum-prom-minutos">-</div></div>
                    <div class="kpi"><div class="kpi__label">Entrenos totales</div><div class="kpi__value" id="sum-total-entrenos">-</div></div>
                </div>

                <div class="client-summary__contact">
                    <p><strong>Email:</strong> <span id="sum-email">-</span></p>
                    <p><strong>Teléfono:</strong> <span id="sum-tel">-</span></p>
                    <p><strong>Ciudad/Pais:</strong> <span id="sum-ciudad-pais">-</span></p>
                    <p><strong>Ingreso:</strong> <span id="sum-ingreso">-</span></p>
                </div>
            </aside>
        </div>
    </div>
</div>

</main>
<script src="${pageContext.request.contextPath}/assets/js/staff/clientes-mostrar.js" defer></script>

</body>
</html>
