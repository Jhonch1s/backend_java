<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es-UY">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <title>Estadísticas y Progreso | Golden Gym</title>

    <!-- FavIcons-->
    <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
    <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

    <!-- Fuentes y CSS base del proyecto -->
    <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/estadisticaProgresoCliente.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css"> <!--por estilos del nav.. etc-->

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
                <!-- opciones inyectadas por js-->
            </select>
        </div>

        <!-- mini kpis del ejercicio elegido -->
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
    <section class="panel u-mt-12" id="blk-share">
        <button id="btn-share" class="btn btn--lg btn--primary-yellow">
            <!-- svg hecho con copilot -->
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-share">
                <path d="M4 12v7a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-7" />
                <polyline points="16 6 12 2 8 6" />
                <line x1="12" y1="2" x2="12" y2="15" />
            </svg>
             Compartir Progreso</button>
        <input id="share-url" class="input u-mt-6" type="text" readonly style="width:100%;display:none">
        <small id="share-help" class="u-mt-4 u-block texto-dorado" style="display:none">
            Enlace copiado al portapapeles.
        </small>
    </section>

</main>
<%@ include file="/pages/modulos/bottom-nav.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/estadisticaProgresoCliente.js" defer></script>
</body>
</html>