<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.gymtrackerweb.dto.EjercicioConProgresoView" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Rutina - Cliente</title>

    <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
    <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@400;500;600;700;900&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/progresosCliente.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
</head>

<body class="fondo-oscuro texto-claro">
<div class="app">
    <main>
        <section id="pantalla-lista" class="pantalla activa">
            <%
                String nombreRutina = (String) request.getAttribute("nombreRutina");
                if (nombreRutina == null || nombreRutina.isBlank()) {
                    nombreRutina = "Rutina sin nombre";
                }
            %>

            <h1 class="titillium-negra texto-dorado" style="display: flex; align-items: center; gap: 0.3em;">
                <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="1em"
                        height="1em"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="var(--color-principal)"
                        stroke-width="1.75"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        style="display: inline; vertical-align: text-bottom; margin-right: 0.25em;"
                >

                <path d="M2 12h1" />
                    <path d="M6 8h-2a1 1 0 0 0 -1 1v6a1 1 0 0 0 1 1h2" />
                    <path d="M6 7v10a1 1 0 0 0 1 1h1a1 1 0 0 0 1 -1v-10a1 1 0 0 0 -1 -1h-1a1 1 0 0 0 -1 1z" />
                    <path d="M9 12h6" />
                    <path d="M15 7v10a1 1 0 0 0 1 1h1a1 1 0 0 0 1 -1v-10a1 1 0 0 0 -1 -1h-1a1 1 0 0 0 -1 1z" />
                    <path d="M18 8h2a1 1 0 0 1 1 1v6a1 1 0 0 1 -1 1h-2" />
                    <path d="M22 12h-1" />
                </svg><span> <%= nombreRutina %> </span>
            </h1>

            <h2 class="plan-create__label noMargin">
                Listado de Ejercicios
                <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 24 24" fill="none"
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
                        int maxSVG = 9;
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
                            Último: <%= e.getPesoUsado() %> kg × <%= e.getRepeticiones() %> Reps
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
            <div class="contenedor-botones-volver">
                <button class="btn-volver boton-primario">← Volver a la lista</button>
                <button id="btn-volver-limitado" class="boton-primario" style="display: none;">← Ver solo recientes</button>
            </div>
            <h2 id="nombre-ejercicio"></h2>

            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <img src="${pageContext.request.contextPath}/assets/img/growth.png"
                         width="32"
                         height="32"
                         alt="Icono de gráfico">
                    Registros recientes
                </h3>
                <div id="registros-recientes" class="contenedor-tarjetas"></div>
                <button id="btn-ver-mas" class="boton-primario btn-historial" style="display: none; margin-top: 1rem; width: 100%;">
                    Ver historial completo
                </button>
            </div>

            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <img src="${pageContext.request.contextPath}/assets/img/svgs/cup-svg.svg"
                         width="32"
                         height="32"
                         alt="Icono de copa">
                    Mejores PRs
                </h3>
                <div id="mejores-prs" class="contenedor-tarjetas"></div>
            </div>
        </section>


    </main>
</div>
<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const contextPath = '<%= request.getContextPath() %>';
        const pantallaLista = document.getElementById("pantalla-lista");
        const pantallaProgreso = document.getElementById("pantalla-progreso");
        const nombreEjercicio = document.getElementById("nombre-ejercicio");

        // --- Referencias a todos los botones ---
        const btnVerMas = document.getElementById("btn-ver-mas");
        const btnVolverPrincipal = document.querySelector(".btn-volver");
        const btnVolverLimitado = document.getElementById("btn-volver-limitado"); // Nuevo botón
        let ejercicioIdActual = null;
        let ejercicioNombreActual = null;

        async function verProgresoPorId(id, nombre, limite = null) {
            if (!id) {
                console.error("ID de ejercicio no definido");
                return;
            }

            ejercicioIdActual = id;
            ejercicioNombreActual = nombre;
            let url = contextPath + "/detalle-progreso?id=" + encodeURIComponent(id);
            if (limite) {
                url += "&limite=" + limite;
            }

            try {
                const resp = await fetch(url);
                const data = await resp.json();
                console.log("Datos recibidos:", data);

                if (!Array.isArray(data.registros)) {
                    throw new Error("Respuesta inválida del servidor");
                }
                nombreEjercicio.textContent = nombre;

                // --- Lógica para mostrar el botón "Volver" correcto ---
                if (limite) {
                    // Si estamos en vista limitada (ej: 5), mostramos el volver principal.
                    btnVolverPrincipal.style.display = 'inline-flex';
                    btnVolverLimitado.style.display = 'none';
                } else {
                    // Si estamos en vista completa, ocultamos el principal y mostramos el nuevo.
                    btnVolverPrincipal.style.display = 'none';
                    btnVolverLimitado.style.display = 'inline-flex';
                }

                const registrosList = document.getElementById("registros-recientes");
                registrosList.innerHTML = "";
                if (Array.isArray(data.registros) && data.registros.length > 0) {
                    // Añadimos 'index' para la animación en cascada
                    data.registros.forEach((r, index) => {
                        const tarjeta = document.createElement("div");
                        tarjeta.classList.add("tarjeta-registro");

                        // --- LÓGICA DE ANIMACIÓN ---
                        // Aplicamos un retraso creciente a cada tarjeta.
                        tarjeta.style.animationDelay = `${index * 0.07}s`;

                        if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
                            tarjeta.classList.add(r.diferenciaPeso > 0 ? "mejora" : "retroceso");
                        } else {
                            tarjeta.classList.add("sin-cambio");
                        }
                        const fecha = document.createElement("strong");
                        fecha.className = "registro-fecha titillium-negra";
                        fecha.textContent = (r.fecha || "sin fecha").trim();
                        const detalle = document.createElement("p");
                        detalle.className = "registro-detalle titillium-base";
                        let texto = "";
                        if (r.pesoUsado != null && r.repeticiones != null) {
                            texto = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
                        } else if (r.pesoUsado != null) {
                            texto = r.pesoUsado + " kg";
                        } else if (r.repeticiones != null) {
                            texto = r.repeticiones + " Repeticiones";
                        } else {
                            texto = "-";
                        }
                        detalle.textContent = texto.trim();
                        tarjeta.appendChild(fecha);
                        tarjeta.appendChild(detalle);
                        if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
                            const diferencia = document.createElement("p");
                            diferencia.className = "registro-diferencia"; // Ya no necesitas "titillium-base"

                            // --- LÍNEAS MODIFICADAS ---
                            const simbolo = r.diferenciaPeso > 0 ? "+" : "-";
                            const icono = r.diferenciaPeso > 0 ? "↑ " : "↓ "; // <-- AÑADIMOS EL ICONO

                            diferencia.textContent = (icono + simbolo + Math.abs(r.diferenciaPeso) + " kg").trim();
                            // --- FIN DE MODIFICACIÓN ---

                            tarjeta.appendChild(diferencia);
                        }
                        registrosList.appendChild(tarjeta);
                    });
                } else {
                    registrosList.innerHTML = "<div class='tarjeta-registro'>No hay registros recientes</div>";
                }

                const prsList = document.getElementById("mejores-prs");
                prsList.innerHTML = "";
                if (Array.isArray(data.prs) && data.prs.length > 0) {
                    // Añadimos 'index' también aquí para consistencia
                    data.prs.forEach((r, index) => {
                        const tarjeta = document.createElement("div");
                        tarjeta.className = "tarjeta-registro";
                        tarjeta.style.animationDelay = `${index * 0.07}s`; // Animación

                        const fecha = document.createElement("strong");
                        fecha.className = "registro-fecha titillium-negra";
                        fecha.textContent = (r.fecha || "sin fecha").trim();
                        const detalle = document.createElement("p");
                        detalle.className = "registro-detalle titillium-base";
                        let texto = "";
                        if (r.pesoUsado != null && r.repeticiones != null) {
                            texto = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
                        } else if (r.pesoUsado != null) {
                            texto = r.pesoUsado + " kg";
                        } else if (r.repeticiones != null) {
                            texto = r.repeticiones + " Repeticiones";
                        } else {
                            texto = "-";
                        }
                        detalle.textContent = texto.trim();
                        tarjeta.appendChild(fecha);
                        tarjeta.appendChild(detalle);
                        prsList.appendChild(tarjeta);
                    });
                } else {
                    prsList.innerHTML = "<div class='tarjeta-registro'>No hay PRs registrados</div>";
                }

                if (data.hayMasRegistros) {
                    btnVerMas.style.display = 'block';
                } else {
                    btnVerMas.style.display = 'none';
                }

                if (!pantallaProgreso.classList.contains('activa')) {
                    pantallaLista.classList.remove("activa");
                    pantallaProgreso.classList.add("activa");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
            } catch (err) {
                console.error("Error cargando progreso:", err);
                alert("No se pudo cargar el progreso. Verificá la consola.");
            }
        }

        // Evento para el botón principal (volver a la lista de ejercicios)
        btnVolverPrincipal.addEventListener("click", () => {
            pantallaProgreso.classList.remove("activa");
            pantallaLista.classList.add("activa");
        });

        // --- Evento para el NUEVO botón (volver a la vista limitada) ---
        btnVolverLimitado.addEventListener('click', () => {
            if(ejercicioIdActual && ejercicioNombreActual) {
                // Llama a la función pidiendo solo los 5 recientes.
                verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, 5);
            }
        });

        // Evento para ver el historial completo
        btnVerMas.addEventListener('click', () => {
            btnVerMas.style.display = 'none';
            if (ejercicioIdActual && ejercicioNombreActual) {
                verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, null);
            }
        });

        // Evento para cada tarjeta de ejercicio
        document.querySelectorAll(".tarjeta-ejercicio").forEach(el => {
            el.addEventListener("click", () => {
                const id = parseInt(el.dataset.ejercicioId, 10);
                const nombre = el.dataset.ejercicioNombre;
                verProgresoPorId(id, nombre, 5);
            });
        });
    });
</script>
</body>
</html>
