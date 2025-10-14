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
                <div class="tarjeta-ejercicio" data-ejercicio-id="<%= e.getIdEjercicio() %>" data-ejercicio-nombre="<%= e.getNombreEjercicio() %>">
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
            <button class="btn-volver boton-primario">← Volver</button>
            <h2 id="nombre-ejercicio"></h2>

            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <!-- SVG gráfico de estadísticas -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="var(--color-principal)" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 3v18h18"/>
                        <path d="m19 9-5 5-4-4-3 3"/>
                    </svg>
                    Registros recientes
                </h3>
                <div id="registros-recientes" class="contenedor-tarjetas"></div>
            </div>

            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <!-- SVG trofeo -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="var(--color-principal)" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/>
                        <path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/>
                        <path d="M4 22h16"/>
                        <path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/>
                        <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/>
                        <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
                    </svg>
                    Mejores PRs
                </h3>
                <div id="mejores-prs" class="contenedor-tarjetas"></div>
            </div>
        </section>


    </main>
</div>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const contextPath = '<%= request.getContextPath() %>';
        const pantallaLista = document.getElementById("pantalla-lista");
        const pantallaProgreso = document.getElementById("pantalla-progreso");
        const nombreEjercicio = document.getElementById("nombre-ejercicio");

        async function verProgresoPorId(id, nombre) {
            if (!id) {
                console.error("ID de ejercicio no definido");
                alert("No se pudo obtener el ID del ejercicio.");
                return;
            }

            try {
                const resp = await fetch(contextPath + "/detalle-progreso?id=" + encodeURIComponent(id));
                const data = await resp.json();
                console.log("Datos recibidos:", data);

                if (!Array.isArray(data.registros)) {
                    throw new Error("Respuesta inválida del servidor");
                }

                nombreEjercicio.textContent = nombre;

                // Mostrar registros recientes
                const registrosList = document.getElementById("registros-recientes");
                registrosList.innerHTML = "";

                if (Array.isArray(data.registros) && data.registros.length > 0) {
                    data.registros.forEach(r => {
                        const tarjeta = document.createElement("div");
                        tarjeta.className = "tarjeta-registro";

                        const fecha = document.createElement("strong");
                        fecha.className = "registro-fecha";
                        fecha.textContent = r.fecha || "sin fecha";

                        const peso = document.createElement("p");
                        peso.className = "registro-detalle";
                        peso.textContent = (r.pesoUsado != null) ? String(r.pesoUsado) + " kg" : "-";

                        const reps = document.createElement("p");
                        reps.className = "registro-detalle";
                        reps.textContent = (r.repeticiones != null) ? String(r.repeticiones) + " reps" : "-";

                        tarjeta.appendChild(fecha);
                        tarjeta.appendChild(peso);
                        tarjeta.appendChild(reps);
                        registrosList.appendChild(tarjeta);
                    });
                } else {
                    registrosList.innerHTML = "<div class='tarjeta-registro'>No hay registros recientes</div>";
                }

                // Mostrar PRs (mismo formato que registros)
                const prsList = document.getElementById("mejores-prs");
                prsList.innerHTML = "";

                if (Array.isArray(data.prs) && data.prs.length > 0) {
                    data.prs.forEach(r => {
                        const tarjeta = document.createElement("div");
                        tarjeta.className = "tarjeta-registro";

                        const fecha = document.createElement("strong");
                        fecha.className = "registro-fecha";
                        fecha.textContent = r.fecha || "sin fecha";

                        const peso = document.createElement("p");
                        peso.className = "registro-detalle";
                        peso.textContent = (r.pesoUsado != null) ? String(r.pesoUsado) + " kg" : "-";

                        const reps = document.createElement("p");
                        reps.className = "registro-detalle";
                        reps.textContent = (r.repeticiones != null) ? String(r.repeticiones) + " reps" : "-";

                        tarjeta.appendChild(fecha);
                        tarjeta.appendChild(peso);
                        tarjeta.appendChild(reps);
                        prsList.appendChild(tarjeta);
                    });
                } else {
                    prsList.innerHTML = "<div class='tarjeta-registro'>No hay PRs registrados</div>";
                }



                pantallaLista.classList.remove("activa");
                pantallaProgreso.classList.add("activa");
                window.scrollTo({ top: 0 });
            } catch (err) {
                console.error("Error cargando progreso:", err);
                alert("No se pudo cargar el progreso. Verificá la consola.");
            }
        }

        const btnVolver = document.querySelector(".btn-volver");
        btnVolver.addEventListener("click", () => {
            pantallaProgreso.classList.remove("activa");
            pantallaLista.classList.add("activa");
            document.getElementById("registros-recientes").innerHTML = "";
            document.getElementById("mejores-prs").innerHTML = "";
        });

        document.querySelectorAll(".tarjeta-ejercicio").forEach(el => {
            el.addEventListener("click", () => {
                const id = parseInt(el.dataset.ejercicioId, 10);
                const nombre = el.dataset.ejercicioNombre;
                verProgresoPorId(id, nombre);
            });
        });
    });
</script>


</body>
</html>
