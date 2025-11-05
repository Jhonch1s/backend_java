<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.example.gymtrackerweb.dao.*" %>
<%@ page import="com.example.gymtrackerweb.model.*" %>
<%@ page import="com.example.gymtrackerweb.dto.IdNombre" %>
<%@ page import="java.time.LocalDateTime" %><%--
  Created by IntelliJ IDEA.
  User: Juan
  Date: 10/28/2025
  Time: 4:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Registrar Membresía · Golden Gym</title>
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
<body class="layout vista--membresia-registrar">
<%@ include file="/pages/modulos/icons-sprite.jsp" %>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>
<% SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
   ClienteDAO clienteDAO = new ClienteDAO();
   Cliente cliente = clienteDAO.buscarPorCi(request.getParameter("ci"));
   PlanDAO planDAO = new PlanDAO();
   List<Plan> planes = planDAO.listarActivos();
   MovimientoDAO movimientoDAO = new MovimientoDAO();
   MembresiaDAO membresiaDAO = new MembresiaDAO();
   EventoMembresiaDAO eventoDAO = new EventoMembresiaDAO();
   Plan plan = null;
   Date fechaDeInicio = null;
   Date fechaDeFin = null;
   List<IdNombre> mediosPago = movimientoDAO.listarMediosPago();
   Staff logueado = (Staff) request.getSession().getAttribute("admin");
   %>
<main class="layout__content">
    <section class="view card form-card membresia-registrar">
        <header class="view__header card__header">
            <a href="javascript:history.back()" class="breadcrumb__link u-mb-16">← Volver</a>
            <h1 class="view__title titillium-negra">Registrar Membresía</h1>
            <p class="view__sub">Registrando membresía para <%= cliente.getNombre() %> <%= cliente.getApellido() %>.</p>
        </header>
        <% if (membresiaDAO.obtenerMembresiaPorCedula(request.getParameter("ci")) != null) { %>
        <div class="card__body">Este cliente ya tiene una membresía registrada. Puede <a href="renovar?ci=<%= request.getParameter("ci") %>">renovarla</a> o <a href="cambiar?ci=<%= request.getParameter("ci") %>">cambiarla</a>.</div>
        <% } else if (request.getMethod().equals("POST")) {
            String idplan = request.getParameter("plan");
            String idcliente = request.getParameter("ci");
            Byte medioPago = Byte.valueOf(request.getParameter("mp"));
            plan = planDAO.buscarPorId(Integer.parseInt(idplan));
            fechaDeInicio = Date.valueOf(LocalDate.now());
            if (plan.getDuracionUnidadId() == 1) {
                fechaDeFin = Date.valueOf(LocalDate.now().plusDays(plan.getDuracionTotal()));
            }
            if (plan.getDuracionUnidadId() == 2) {
                fechaDeFin = Date.valueOf(LocalDate.now().plusWeeks(plan.getDuracionTotal()));
            }
            if (plan.getDuracionUnidadId() == 3) {
                fechaDeFin = Date.valueOf(LocalDate.now().plusMonths(plan.getDuracionTotal()));
            }
            if (plan.getDuracionUnidadId() == 4) {
                fechaDeFin = Date.valueOf(LocalDate.now().plusYears(plan.getDuracionTotal()));
            }
            int idNuevaMembresia = membresiaDAO.agregarMembresia(new Membresia(-1, Integer.parseInt(idplan), idcliente, fechaDeInicio, fechaDeFin, 1));
            if (idNuevaMembresia == -1) { %>
        <div class="card__body">Hubo un error mega-fatal al registrar esta membresía. Por favor comuníquese inmediatamente con soporte técnico: 2907 3991 int. 201 de 9 a 17 hs. Gracias.</div>
        <%
            }
            else {
                eventoDAO.agregarEventoMembresia(new EventoMembresia(-1, logueado.getId(), idNuevaMembresia, 2, Timestamp.from(Instant.now()), "web"));
                movimientoDAO.insertarMovimiento(new Movimiento(logueado.getId(), LocalDateTime.now(), plan.getValor(), medioPago, (byte) 1, (byte) 1, idNuevaMembresia, idcliente)); %>
        <div class="card__body">Se registró la membresía con el plan <%= plan.getNombre() %>.<br>Fecha de inicio: <%= formateador.format(fechaDeInicio) %><br>Fecha de fin: <%= formateador.format(fechaDeFin)%></div>
         <% }
            } else { %>
        <form id="form-registrar-membresia"
              class="form card__body"
              method="post" novalidate>

            <div class="form__grid">
                <div class="field">
                    <label for="membresia-plan" class="label">Plan</label>
                    <select id="membresia-plan" name="plan" required class="control">
                        <% for (Plan i : planes) {%>
                        <option value="<%= i.getId() %>"><%= i.getNombre() %> (<%= i.getDuracionTotal() %> <%= (i.getDuracionTotal() == 1 ? i.getDuracionUnidadNombre().toLowerCase() : (i.getDuracionUnidadNombre().equals("Mes") ? i.getDuracionUnidadNombre().toLowerCase() + "es" : i.getDuracionUnidadNombre().toLowerCase() + "s")) %> - $<%= i.getValor().toString() %>)</option>
                        <% } %>
                    </select>
                    <small class="hint">Seleccione el plan que el cliente desea.</small>
                    <div class="error" id="error-membresia-plan"></div>
                </div>
                <div class="field">
                    <label for="membresia-mp" class="label">Medio de pago</label>
                    <select id="membresia-mp" name="mp" required class="control">
                        <% for (IdNombre i : mediosPago) {%>
                        <option value="<%= i.getId() %>"><%= i.getNombre() %></option>
                        <% } %>
                    </select>
                    <small class="hint">Seleccione el medio de pago usado para pagar.</small>
                    <div class="error" id="error-membresia-mp"></div>
                </div>
            </div>



            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">
                    <svg class="icon" width="20" height="20" aria-hidden="true">
                        <use href="#i-user-add"></use>
                    </svg>
                    Registrar Membresía
                </button>
            </div>
        </form>
        <% } %>
    </section>
</main>

</body>
</html>
