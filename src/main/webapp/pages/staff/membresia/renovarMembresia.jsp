<%@ page import="java.util.List" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.time.Instant" %>
<%@ page import="java.sql.Date" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="com.example.gymtrackerweb.dao.*" %>
<%@ page import="com.example.gymtrackerweb.dto.IdNombre" %>
<%@ page import="com.example.gymtrackerweb.model.*" %>
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
    <title>Renovar Membresía · Golden Gym</title>
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
<body class="layout vista--membresia-renovar">
<%@ include file="/pages/modulos/icons-sprite.jsp" %>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>
<% SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
   ClienteDAO clienteDAO = new ClienteDAO();
   Cliente cliente = clienteDAO.buscarPorCi(request.getParameter("ci"));
   PlanDAO planDAO = new PlanDAO();
   List<Plan> planes = planDAO.listarTodos();
   MovimientoDAO movimientoDAO = new MovimientoDAO();
   MembresiaDAO membresiaDAO = new MembresiaDAO();
   EventoMembresiaDAO eventoDAO = new EventoMembresiaDAO();
   Plan plan = null;
   LocalDate fechaDeInicio = null;
   LocalDate fechaDeFin = null;
   Membresia membresia = membresiaDAO.obtenerMembresiaPorCedula(request.getParameter("ci"));
   List<IdNombre> mediosPago = movimientoDAO.listarMediosPago();
   Staff logueado = (Staff) request.getSession().getAttribute("admin");
   %>
<main class="layout__content">
    <section class="view card form-card membresia-registrar">
        <header class="view__header card__header">
            <a href="javascript:history.back()" class="breadcrumb__link u-mb-16">← Volver</a>
            <h1 class="view__title titillium-negra">Renovar Membresía</h1>
            <p class="view__sub">Renovando membresía de <%= cliente.getNombre() %> <%= cliente.getApellido() %>.</p>
        </header>
        <% if (membresia == null) { %>
        <div class="card__body">Este cliente no tiene una membresía registrada. Debe <a href="registrar?ci=<%= request.getParameter("ci") %>">registrarla</a> primero.</div>
        <% } else if (request.getMethod().equals("POST")) {
            String idplan = request.getParameter("plan");
            String idcliente = request.getParameter("ci");
            Byte medioPago = Byte.valueOf(request.getParameter("mp"));
            plan = planDAO.buscarPorId(Integer.parseInt(idplan));
            fechaDeInicio = membresia.getFechaInicio().toLocalDate();
            fechaDeFin = membresia.getFechaFin().toLocalDate();
            if (ChronoUnit.DAYS.between(fechaDeFin, LocalDate.now()) > 10) {
                fechaDeInicio = LocalDate.now();
                fechaDeFin = LocalDate.now();
            }
            else if (LocalDate.now().isAfter(fechaDeFin)) {
                fechaDeFin = LocalDate.now();
            }
            if (plan.getDuracionUnidadId() == 1) {
                fechaDeFin = fechaDeFin.plusDays(plan.getDuracionTotal());
            }
            if (plan.getDuracionUnidadId() == 2) {
                fechaDeFin = fechaDeFin.plusWeeks(plan.getDuracionTotal());
            }
            if (plan.getDuracionUnidadId() == 3) {
                fechaDeFin = fechaDeFin.plusMonths(plan.getDuracionTotal());
            }
            if (plan.getDuracionUnidadId() == 4) {
                fechaDeFin = fechaDeFin.plusYears(plan.getDuracionTotal());
            }
            membresiaDAO.modificarMembresia(new Membresia(membresia.getId(), Integer.parseInt(idplan), idcliente, Date.valueOf(fechaDeInicio), Date.valueOf(fechaDeFin), 1));
            eventoDAO.agregarEventoMembresia(new EventoMembresia(-1, 1, membresia.getId(), 1, Timestamp.from(Instant.now()), "web"));
            movimientoDAO.insertarMovimiento(new Movimiento(logueado.getId(), LocalDateTime.now(), plan.getValor(), medioPago, (byte) 1, (byte) 1, membresia.getId(), idcliente)); %>
        <div class="card__body">Se renovó la membresía con el plan <%= plan.getNombre() %>.<br>Fecha de inicio: <%= formateador.format(Date.valueOf(fechaDeInicio)) %><br>Fecha de fin: <%= formateador.format(Date.valueOf(fechaDeFin))%></div>
         <% } else {
         Plan planActual = planDAO.buscarPorId(membresia.getIdPlan());%>
        <div class="card__body">
            <h2>Membresía actual</h2>
            Plan: <%= planActual.getNombre() %><br>
            Fecha de inicio: <%= formateador.format(membresia.getFechaInicio()) %><br>
            Fecha de fin: <%= formateador.format(membresia.getFechaFin()) %><br>
            Estado: <%= (LocalDate.now().isAfter(membresia.getFechaFin().toLocalDate()) ? "Inactiva" : "Activa") %>
        </div>
        <form id="form-renovar-membresia"
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
                    Renovar Membresía
                </button>
            </div>
        </form>
        <% } %>
    </section>
</main>

</body>
</html>
