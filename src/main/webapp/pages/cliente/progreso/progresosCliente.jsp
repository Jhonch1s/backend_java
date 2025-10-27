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
        <section id="pantalla-lista" class="pantalla">
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
                        int maxSVG = 30;
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
                        <p class="grupo-muscular-label"><%= e.getGrupoMuscular() %></p>
                        <p class="plan-create__label">
                            <%= e.getSeries() %> series × <%= e.getRepeticionesRutina() %> Reps
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
            </div>
            <h2 id="nombre-ejercicio"></h2>

            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <svg xmlns="http://www.w3.org/2000/svg"
                         width="32"
                         height="32"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="var(--color-principal)"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         style="background: none; display: block;">
                        <path d="M4 15l4 -4l3 3l5 -5l4 4"/>
                    </svg>
                    Registros recientes
                </h3>
                <div id="registros-recientes" class="contenedor-tarjetas"></div>
                <div id="paginacion-controles" style="display: none; justify-content: space-between; align-items: center; margin-top: 1.5rem; width: 100%;">
                    <button id="btn-anterior" class="boton-primario">
                        <span class="texto-largo">← Anterior</span>
                        <span class="texto-corto">←</span>
                    </button>

                    <div id="numeros-pagina" class="paginacion-numeros"></div>

                    <button id="btn-siguiente" class="boton-primario">
                        <span class="texto-largo">Siguiente →</span>
                        <span class="texto-corto">→</span>
                    </button>
                </div>
            </div>
            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <svg xmlns="http://www.w3.org/2000/svg"
                         width="32"
                         height="32"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="var(--color-principal)"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         style="background: none; display: block;">
                        <path d="M8 21h8M12 17v4M7 4h10v5a5 5 0 0 1-10 0V4zM5 4h14M5 4a3 3 0 0 0 0 6M19 4a3 3 0 0 1 0 6"/>
                    </svg>
                    Mejores PRs
                </h3>
                <div id="mejores-prs" class="contenedor-tarjetas"></div>

                <div id="paginacion-controles-prs" style="display: none; justify-content: space-between; align-items: center; margin-top: 1.5rem; width: 100%;">
                    <button id="btn-anterior-prs" class="boton-primario">
                        <span class="texto-largo">← Anterior</span>
                        <span class="texto-corto">←</span>
                    </button>
                    <div id="numeros-pagina-prs" class="paginacion-numeros"></div>
                    <button id="btn-siguiente-prs" class="boton-primario">
                        <span class="texto-largo">Siguiente →</span>
                        <span class="texto-corto">→</span>
                    </button>
                </div>
            </div>
            <div class="bloque">
                <h3 class="texto-blanco titillium-negra" style="display: flex; align-items: center; gap: 0.5rem;">
                    <svg xmlns="http://www.w3.org/2000/svg"
                         width="32"
                         height="32"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="var(--color-principal)"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         style="background: none; display: block;">
                        <path d="M12 6L9 9L5 7L6 13L3 17H21L18 13L19 7L15 9L12 6Z" />
                    </svg>
                    Mejores RMs
                </h3>
                <div id="mejores-rms" class="contenedor-tarjetas"></div>

                <div id="paginacion-controles-rms" style="display: none; justify-content: space-between; align-items: center; margin-top: 1.5rem; width: 100%;">
                    <button id="btn-anterior-rms" class="boton-primario">
                        <span class="texto-largo">← Anterior</span>
                        <span class="texto-corto">←</span>
                    </button>
                    <div id="numeros-pagina-rms" class="paginacion-numeros"></div>
                    <button id="btn-siguiente-rms" class="boton-primario">
                        <span class="texto-largo">Siguiente →</span>
                        <span class="texto-corto">→</span>
                    </button>
                </div>
            </div>
        </section>
    </main>
</div>

<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script>
    const contextPath = '<%= request.getContextPath() %>';
</script>
<script src="${pageContext.request.contextPath}/assets/js/VerProgresoCliente.js" defer></script>

</body>
</html>
