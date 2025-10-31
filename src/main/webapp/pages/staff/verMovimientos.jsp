<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>Cliente | Perfil · Golden Gym</title>

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

    <!-- Estilos base existentes -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff/cliente-movimientos.css">


    <!-- Estilos del dashboard de cliente (referencia visual/hero/buttons) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css">

    <!-- Estilos específicos del perfil (ajustes mínimos) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-perfil.css?v=20251016b">
</head>


<body>
<main class="u-maxw-1100 u-mx-auto u-p-32">
    <h1 class="view__title">Listado de Movimientos</h1>

    <form id="filtroForm" class="filtros">
        <form id="filtroForm" class="filtros">
            <label>Staff (usuario):
                <input type="text" name="staff" placeholder="Ej: MaxiStaff"
                       value="${param.staff != null ? param.staff : ''}">
            </label>
            <label>Cliente CI:
                <input type="number" name="cliente" placeholder="Ej: 12345678"
                       value="${param.cliente != null ? param.cliente : ''}">
            </label>
            <label>Desde:
                <input type="date" name="desde"
                       value="${param.desde != null ? param.desde : ''}">
            </label>
            <label>Hasta:
                <input type="date" name="hasta"
                       value="${param.hasta != null ? param.hasta : ''}">
            </label>

        <div class="filtros__acciones">
            <button type="submit" class="btn btn--amarillo">Filtrar</button>
            <button type="button" class="btn btn--gris" id="btnReiniciar">Reiniciar</button>
        </div>
    </form>
    <p id="filtroError" class="form__error-message" style="text-align:center;"></p>
    <c:if test="${totalFiltrado ne null}">
        <p class="total-filtrado">
            Ingresos por filtro actuales:
            <strong>
                <fmt:formatNumber value="${totalFiltrado}" type="currency" currencySymbol="$ " />
            </strong>
        </p>
    </c:if>
    <!-- Tabla de resultados -->
    <c:choose>
        <c:when test="${not empty listaMovimientos}">
            <div class="tabla-wrapper">
                <table class="tabla-datos">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Fecha</th>
                        <th>Importe</th>
                        <th>Staff</th>
                        <th>Cliente CI</th>
                        <th>Cliente</th>
                        <th>Tipo Cliente</th>
                        <th>Medio Pago</th>
                        <th>Origen</th>
                        <th>Plan</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="m" items="${listaMovimientos}">
                        <tr>
                            <td>${m.idMov}</td>
                            <td>${m.fechaHora}</td>
                            <td>$ ${m.importe}</td>
                            <td>${m.staffNombre}</td>
                            <td><c:out value="${m.clienteCi != null ? m.clienteCi : '-'}"/></td>
                            <td><c:out value="${m.clienteNombre != null ? m.clienteNombre : '-'}"/></td>
                            <td>${m.tipoClienteNombre}</td>
                            <td>${m.medioPagoNombre}</td>
                            <td>${m.origenNombre}</td>
                            <td><c:out value="${m.planNombre != null ? m.planNombre : '-'}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="paginacion">
                <c:if test="${page > 1}">
                    <a href="#" onclick="cambiarPagina(${page - 1})">← Anterior</a>
                </c:if>
                <span>Página ${page} de ${totalPages}</span>
                <c:if test="${page < totalPages}">
                    <a href="#" onclick="cambiarPagina(${page + 1})">Siguiente →</a>
                </c:if>
            </div>

        </c:when>
        <c:otherwise>
            <p style="text-align:center; margin-top:2rem;">No se encontraron movimientos.</p>
        </c:otherwise>
    </c:choose>
</main>
<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/assets/js/staff/verMovimientos.js" defer></script>

</body>
</html>
