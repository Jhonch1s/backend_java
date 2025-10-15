<%@ page import="com.example.gymtrackerweb.dao.RutinaClienteDetalleDAO" %>
<%@ page import="com.example.gymtrackerweb.model.Cliente" %>
<%@ page import="com.example.gymtrackerweb.model.RutinaClienteDetalle" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 08/10/2025
  Time: 21:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es-UY">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>Rutina · Golden Gym</title>

    <!-- Tipografía + Normalize -->
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
          integrity="sha512-NhSC1X0f3zp3p2JtYh8C2W4TyTX0b6x1n00x4bZ4Zk3E2b9GmZy1wKkPe4v5YyX1Y9i6w5W2rszj0o9uGZ7xwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Estilos existentes del proyecto -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <!-- CSS del dashboard cliente -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css">

    <style>
        /* ------- extras mínimos para esta vista ------- */
        .stats-page{
            min-height: 100svh;
            padding: 16px;
            padding-bottom: calc(var(--gg-nav-h) + 12px + env(safe-area-inset-bottom));
            display: grid; gap: 16px;
            background: var(--gg-bg, #101010); color: var(--gg-white, #f3f3f3);
            box-sizing: border-box;
        }
        .kpi-row{
            display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px;
        }
        .kpi{
            background: var(--gg-card, #1a1a1a); border: 1px solid var(--gg-border, #2a2a2a);
            border-radius: 16px; padding: 12px; text-align: center;
        }
        .kpi__num{ margin: 0; font-size: 1.6rem; font-weight: 800; }
        .kpi__label{ margin: 2px 0 0; color: rgba(255,255,255,.8); font-size: .8rem; }

        .panel{ background: var(--gg-card,#1a1a1a); border: 1px solid var(--gg-border,#2a2a2a);
            border-radius: 18px; padding: 14px; }
        .panel__head{ display:flex; align-items:center; justify-content:space-between; }
        .panel__title{ margin:0; font-size:1.1rem; font-weight:800; }

        .exercise-picker{ display:grid; gap:10px; }
        .select{
            width: 100%; background: #121212; color: var(--gg-white); border:1px solid #2a2a2a;
            border-radius: 12px; padding: 10px 12px;
        }

        .charts{ display:grid; gap: 12px; }
        .chart-card{
            background: #121212; border:1px solid #2a2a2a; border-radius:16px; padding: 10px;
        }
        .chart-card__title{ margin:0 0 8px 0; font-weight:700; font-size:.95rem; color: rgba(255,255,255,.9); }
        .chart-inset{
            border-radius:12px; background:#0f0f0f; border:1px dashed rgba(255,255,255,.08);
            display:grid; place-items:center; min-height: 160px;
        }
        .chart-inset small{ color: rgba(255,255,255,.6); }

        .prs{ display:grid; gap:10px; }
        .pr-item{
            display:grid; grid-template-columns: 1fr auto; align-items:center; gap:10px;
            background: var(--gg-chip,#2a2a2a); border:1px solid var(--gg-border,#2a2a2a); border-radius:14px;
            padding: 12px;
        }
        .pr-title{ margin:0; font-weight:800; }
        .pr-sub{ margin:2px 0 0; color: rgba(255,255,255,.8); font-size:.9rem; }
        .pr-meta{ font-weight:800; color: var(--gg-yellow,#fff112); }

        .actions{ display:grid; gap:10px; }
        .actions .btn{ width:100%; }
    </style>
</head>

<body class="fondo-oscuro texto-claro">
<main class="stats-page">

    <%
        Cliente usuario = (Cliente) session.getAttribute("usuario");
    %>
    <% RutinaClienteDetalleDAO rcdDao = new RutinaClienteDetalleDAO();
        List<RutinaClienteDetalle> rutinas = rcdDao.listarPorCliente(usuario.getCi());
        int totalRutinas = rutinas.size(); %>
    <!-- PRs -->
    <section class="panel">
        <div class="panel__head">
            <h2 class="panel__title">Mis rutinas</h2>
            <span class="texto-dorado">Total: <%= totalRutinas %></span>
        </div>

        <div class="prs u-mt-8">
            <%
                for (RutinaClienteDetalle i : rutinas) {
                    %>

            <div class="pr-item">
                <div>
                    <p class="pr-title"><%= i.getRutinaNombre() %></p>
                    <p class="pr-sub"><%= i.getEstado() %> · Asignada el <%= i.getFechaAsignacion() %></p>
                </div>
                <span class="pr-meta"><a href="detallerutina?id=<%= i.getId() %>">Más detalles</a></span>
            </div>
            <%
                }
            %>

        </div>
    </section>

</main>

<%@ include file="/pages/modulos/bottom-nav.jsp" %>

<!-- Hook opcional para gráficos (más adelante) -->
<!--
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
  // Ejemplo mínimo:
  // const ctx = document.getElementById('chart-peso');
  // new Chart(ctx, { type: 'line', data: {...}, options: {...} });
</script>
-->
</body>
</html>
