<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.gymtrackerweb.dto.EjercicioConProgresoView" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Rutina - Cliente</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/progresosCliente.css" />
</head>

<body class="fondo-oscuro texto-claro">
<div class="app">
    <main>
        <section id="pantalla-lista" class="pantalla activa">
            <h1 class="titillium-negra texto-dorado" style="display: flex; align-items: center; gap: 0.5rem; margin-top: -0.5rem; margin-bottom: 0.5rem;">
                <svg xmlns="http://www.w3.org/2000/svg" width="58" height="58" viewBox="0 0 24 24" fill="none"
                     stroke="var(--color-principal)" stroke-width="1.75" stroke-linecap="round"
                     stroke-linejoin="round">
                    <path d="M2 12h1" />
                    <path d="M6 8h-2a1 1 0 0 0 -1 1v6a1 1 0 0 0 1 1h2" />
                    <path d="M6 7v10a1 1 0 0 0 1 1h1a1 1 0 0 0 1 -1v-10a1 1 0 0 0 -1 -1h-1a1 1 0 0 0 -1 1z" />
                    <path d="M9 12h6" />
                    <path d="M15 7v10a1 1 0 0 0 1 1h1a1 1 0 0 0 1 -1v-10a1 1 0 0 0 -1 -1h-1a1 1 0 0 0 -1 1z" />
                    <path d="M18 8h2a1 1 0 0 1 1 1v6a1 1 0 0 1 -1 1h-2" />
                    <path d="M22 12h-1" />
                </svg>
                Fuerza 5x5 – Básica
            </h1>

            <h2 class="plan-create__label" style="display: flex; align-items: center; gap: 0.75rem;">
                Listado de Ejercicios
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none"
                     stroke="var(--color-principal)" stroke-width="1.5" stroke-linecap="round"
                     stroke-linejoin="round">
                    <path d="M9 5h-2a2 2 0 0 0 -2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2 -2v-12a2 2 0 0 0 -2 -2h-2" />
                    <path d="M9 3m0 2a2 2 0 0 1 2 -2h2a2 2 0 0 1 2 2v0a2 2 0 0 1 -2 2h-2a2 2 0 0 1 -2 -2z" />
                    <path d="M9 17v-4" />
                    <path d="M12 17v-1" />
                    <path d="M15 17v-2" />
                    <path d="M12 17v-1" />
                </svg>
            </h2>

            <div class="lista-ejercicios">
                <%
                    List<EjercicioConProgresoView> ejercicios =
                            (List<EjercicioConProgresoView>) request.getAttribute("ejercicios");
                    if (ejercicios != null && !ejercicios.isEmpty()) {
                        int contador = 1;
                        int maxSVG = 9; // Número máximo de SVGs que tienes
                        for (EjercicioConProgresoView e : ejercicios) {
                            int numeroSVG = Math.min(contador, maxSVG);
                %>
                <div class="tarjeta-ejercicio" onclick="verProgreso('<%= e.getNombreEjercicio() %>')">
                    <div class="tarjeta-ejercicio__icono">
                        <img src="${pageContext.request.contextPath}/assets/img/svgs/hexagon-number-<%= numeroSVG %>.svg"
                             width="34" height="34" alt="Icono ejercicio" />
                    </div>
                    <div class="tarjeta-ejercicio_contenido">
                        <h3 class="texto-dorado"><%= e.getNombreEjercicio() %></h3>
                        <p class="plan-create__label">
                            <% if (e.getPesoUsado() != null) { %>
                            Último: <%= e.getPesoUsado() %> kg × <%= e.getRepeticiones() %>
                            (<%= new java.text.SimpleDateFormat("dd/MM").format(e.getFechaUltimoRegistro()) %>)
                            <% } else { %>
                            Sin registros aún
                            <% } %>
                        </p>
                    </div>
                </div>
                <%
                        contador++;
                    }
                } else {
                %>
                <p class="texto-claro">No se encontraron ejercicios activos.</p>
                <% } %>
            </div>
        </section>
        <section id="pantalla-progreso" class="pantalla">
            <button class="btn-volver" onclick="volver()">← Volver</button>
            <h2 id="nombre-ejercicio"></h2>

            <h3 class="texto-dorado">Registros recientes</h3>
            <ul id="registros-recientes" class="lista-detalle"></ul>

            <h3 class="texto-dorado">Mejores PRs</h3>
            <ul id="mejores-prs" class="lista-detalle"></ul>
        </section>
    </main>
</div>
<script>
    const pantallaLista = document.getElementById("pantalla-lista");
    const pantallaProgreso = document.getElementById("pantalla-progreso");
    const nombreEjercicio = document.getElementById("nombre-ejercicio");

    async function verProgreso(nombre) {
        nombreEjercicio.textContent = nombre;

        try {
            const resp = await fetch(`detalle-progreso?nombre=${encodeURIComponent(nombre)}`);
            const data = await resp.json();

            document.getElementById("registros-recientes").innerHTML =
                data.registros.map(r => `<li>${r.fecha} → ${r.peso} kg × ${r.reps}</li>`).join("");

            document.getElementById("mejores-prs").innerHTML =
                data.prs.map(r => `<li>${r.peso} kg × ${r.reps} (${r.fecha})</li>`).join("");

            pantallaLista.classList.remove("activa");
            pantallaProgreso.classList.add("activa");
            window.scrollTo({ top: 0 });
        } catch (err) {
            console.error("Error cargando progreso:", err);
        }
    }

    function volver() {
        pantallaProgreso.classList.remove("activa");
        pantallaLista.classList.add("activa");
    }
</script>

</body>
</html>
