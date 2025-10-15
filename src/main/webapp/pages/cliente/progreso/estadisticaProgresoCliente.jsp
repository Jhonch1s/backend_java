<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es-UY">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>Estadísticas & Progreso · Golden Gym</title>

    <!-- Fuentes y CSS base del proyecto (ya los tenés) -->
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css"> <!--por estilos del nav.. etc-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estadisticaProgresoCliente.css">

</head>

<body class="fondo-oscuro texto-claro"
      data-endpoint-base="${pageContext.request.contextPath}/cliente/stats"
      data-ym=""
      data-ctx="${pageContext.request.contextPath}">
<main class="stats-page">

    <!-- encabezado + KPIs del mes + racha a lo duolingo -->
    <header class="panel" id="blk-overview">
        <div class="panel__head">
            <h1 class="panel__title">Estadísticas &amp; Progreso</h1>
            <span class="texto-dorado">Cliente</span>
        </div>

        <!-- KPIs (medidas clave de progreso) del mesc actula-->
        <div class="kpi-row u-mt-12">
            <div class="kpi">
                <p class="kpi__num" id="kpi-dias">–</p>
                <p class="kpi__label">Días este mes</p>
            </div>
            <div class="kpi">
                <p class="kpi__num" id="kpi-min-tot">–</p>
                <p class="kpi__label">Min totales</p>
            </div>
            <div class="kpi">
                <p class="kpi__num" id="kpi-min-prom">–</p>
                <p class="kpi__label">Min promedio</p>
            </div>
        </div>

        <!-- racha -->
        <div class="u-mt-12">
            <div class="panel__head">
                <strong>Racha semanal</strong>
                <span id="streak-weeks-label" class="texto-dorado">– semanas</span>
            </div>
            <div class="weeks-streak u-mt-8" id="weeks-streak">
                <!-- 12 puntitos (semanas), se marcan con .is-active vía JS a partir de datos de la bd -->
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
                <span class="week-dot"></span>
            </div>
        </div>
    </header>

    <!-- Selector de rango global para los graficos -->
    <section class="panel" id="blk-range">
        <div class="chip-scroll">
            <div id="rangeSelector" class="chip-group">
                <button class="chip is-active" data-range="4w">4 sem</button>
                <button class="chip" data-range="3m">3 meses</button>
                <button class="chip" data-range="6m">6 meses</button>
                <button class="chip" data-range="12m">12 meses</button>
                <button class="chip" data-range="all">Todo</button>
                <button class="chip" data-range="custom">Personalizado</button>
            </div>
        </div>

    </section>

    <!-- Progreso por ejercicio -->
    <section class="panel" id="blk-exercise">
        <div class="panel__head">
            <h2 class="panel__title">Progreso por ejercicio</h2>
            <span class="texto-dorado">Fuerza &amp; volumen</span>
        </div>

        <!-- Selector de ejercicio dinamico -->
        <div class="u-mt-8">
            <label for="sel-ej" class="u-visually-hidden">Elegir ejercicio</label>
            <select id="sel-ej" class="select">
                <!-- opciones inyectadas por JS: value = id_ejercicio -->
            </select>
        </div>

        <!-- Mini-KPIs del ejercicio elegido -->
        <div class="mini-kpis u-mt-12" id="mini-kpis">
            <div class="mini">
                <p class="mini__num" id="mk-e1rm">–</p>
                <p class="mini__label">Mejor e1RM
                    <button class="info-dot" data-help="bestE1rm" aria-label="¿Qué es esto?">i</button>
                </p>
            </div>

            <div class="mini">
                <p class="mini__num" id="mk-bestset">–</p>
                <p class="mini__label">Mejor marca (kg×reps)
                    <button class="info-dot" data-help="bestSet" aria-label="¿Qué es esto?">i</button>
                </p>
            </div>

            <div class="mini">
                <p class="mini__num" id="mk-delta">–</p>
                <p class="mini__label">Δ e1RM (ventana)
                    <button class="info-dot" data-help="delta" aria-label="¿Qué es esto?">i</button>
                </p>
            </div>

            <div class="mini">
                <p class="mini__num" id="mk-vol4w">–</p>
                <p class="mini__label">Volumen 4 sem
                    <button class="info-dot" data-help="vol4w" aria-label="¿Qué es esto?">i</button>
                </p>
            </div>

        </div>

        <!-- Gráficos principales -->
        <div class="subgrid u-mt-12">
            <!-- Tendencia de fuerza (e1RM) -->
            <article class="chart-card">
                <h3 class="chart-card__title">Tendencia de fuerza (e1RM)</h3>
                <div class="chart-inset">
                    <small id="ph-e1rm">Gráfico de líneas aquí</small>
                </div>
            </article>

            <!-- Volumen y frecuencia por semana -->
            <article class="chart-card">
                <h3 class="chart-card__title">Volumen &amp; frecuencia (semanal)</h3>
                <div class="chart-inset">
                    <small id="ph-volume">Barras/mixto aquí</small>
                </div>
            </article>

            <!-- Curva carga–reps -->
            <article class="chart-card">
                <h3 class="chart-card__title">Curva carga–reps</h3>
                <div class="chart-inset">
                    <small id="ph-scatter">Scatter aquí</small>
                </div>
            </article>
        </div>
    </section>

    <!-- 4) PRs y mejores del período -->
    <section class="panel" id="blk-prs">
        <div class="panel__head">
            <h2 class="panel__title">PRs y mejores del período</h2>
            <span class="texto-dorado" id="label-periodo">–</span>
        </div>

        <div class="prs u-mt-8" id="list-prs">
            <!-- Items inyectados por JS:
                 .pr-item > .pr-title | .pr-sub (fecha, reps×kg, e1RM) | .pr-meta (badge PR) -->
        </div>
    </section>

    <!-- 5) Empty states (ocultos por default) -->
    <section class="panel u-hide" id="empty-exercise">
        <p>No hay registros para este ejercicio en el rango seleccionado.</p>
        <a class="btn btn--ghost-yellow btn--sm"
           href="${pageContext.request.contextPath}/pages/modulos/cliente/abm-progreso.jsp">
            Gestionar registros de progreso →
        </a>
    </section>

    <!-- 6) Acciones (historial completo / ABM progreso) -->
    <section class="panel" id="blk-actions">
        <div class="grid-2">
            <a class="btn btn--ghost-yellow btn--lg"
               href="${pageContext.request.contextPath}/pages/modulos/cliente/historial.jsp">
                Ver historial completo →
            </a>
            <a class="btn btn--ghost-yellow btn--lg"
               href="${pageContext.request.contextPath}/pages/modulos/cliente/abm-progreso.jsp">
                Gestionar registros de progreso →
            </a>
        </div>
    </section>
</main>
<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/estadisticaProgresoCliente.js" defer></script>
</body>
</html>
