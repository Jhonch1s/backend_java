<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <title>Panel Administracion · Golden Gym</title>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tabla-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/toastCliente.css">
</head>
<body class="u-bg">

<div class="layout">
    <button class="sidebar__toggle" aria-label="Alternar menú" aria-expanded="false">☰</button>
    <div class="sidebar-overlay"></div>

    <aside class="sidebar" aria-label="Navegación principal">
        <div class="sidebar__brand">
            <span class="sidebar__title">Golden Gym</span>
        </div>
        <nav class="sidebar__nav">
            <h3 class="sidebar__section"><span>Planes</span></h3>
            <ul>
                <li><a href="#/planes/lista" data-link>📋 <span>Listado de Planes</span></a></li>
                <li><a href="#/planes/nuevo" data-link>➕ <span>Crear Plan</span></a></li>
            </ul>
            <h3 class="sidebar__section"><span>Clientes</span></h3>
            <ul>
                <li>
                    <a href="#/clientes/lista" data-link>
                        <img src="${pageContext.request.contextPath}/assets/img/group.png" alt="grupo_usuario" width="18" height="18" style="vertical-align: middle;">
                        <span>Listado de Clientes</span>
                    </a>
                </li>
                <li>
                    <a href="#/clientes/nuevo" data-link>
                        <img src="${pageContext.request.contextPath}/assets/img/user-plus-29-48.png" alt="usuario_crear" width="18" height="18" style="vertical-align: middle;">
                        <span>Crear Cliente</span>
                    </a>
                </li>
            </ul>
            <h3 class="sidebar__section"><span>Cuenta</span></h3>
            <ul>
                <li>
                    <form action="${pageContext.request.contextPath}/logout" method="post" style="margin: 0;">
                        <button type="submit" class="btn logout-button">🔒 <span>Cerrar sesión</span></button>
                    </form>
                </li>
            </ul>
        </nav>
    </aside>

    <main id="app-view" class="main u-mx-auto u-maxw-1100 u-p-32 main-panel" tabindex="-1" aria-live="polite">
        <div class="main__placeholder"><h1>Cargando...</h1></div>
    </main>
</div>

<template id="tpl-planes-lista">
    <section class="view">
        <header class="view-header">
            <div> <h1 class="view__title">Planes de Membresía</h1> <p class="view__sub">Gestiona los planes disponibles.</p> </div>
            <a href="#/planes/nuevo" class="btn btn--primary">✨ Crear Nuevo Plan</a>
        </header>
        <div class="tabla-container u-mt-24">
            <table class="tabla-entidades"><thead><tr><th>ID</th><th>Nombre</th><th>Valor</th><th>Duración</th><th>Estado</th><th class="u-text-right">Acciones</th></tr></thead><tbody><tr><td><span class="tabla-entidades__id">1</span></td><td><strong>Plan Ejemplo</strong></td><td>$50.00</td><td>1 Mes</td><td><span class="badge badge--activo">Activo</span></td><td class="u-text-right"><a href="#/planes/editar?id=1" class="btn btn--icon">✏️</a><button class="btn btn--icon btn--peligro">🗑️</button></td></tr></tbody></table>
        </div>
    </section>
</template>

<template id="tpl-plan-crear">
    <section class="view plan-create">
        <header class="view__header"> <a href="#/planes/lista" class="breadcrumb__link u-mb-16">← Volver a la lista</a> <h1 class="view__title">Crear Plan de Membresía</h1> <p class="view__sub">Completa los datos para un nuevo plan.</p> </header>
        <form id="form-crear-plan" class="plan-create__form" action="${pageContext.request.contextPath}/api/planes/crear" method="post" enctype="multipart/form-data" novalidate>
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field"> <label for="plan-nombre" class="plan-create__label">Nombre del Plan</label> <input id="plan-nombre" name="nombre" type="text" placeholder="Ej: Plan Premium" required class="plan-create__control"/> <small class="plan-create__hint">Nombre comercial visible.</small> <div class="form__error-message" id="error-plan-nombre"></div> </div>
                <div class="plan-create__field"> <label for="plan-valor" class="plan-create__label">Valor ($)</label> <input id="plan-valor" name="valor" type="number" step="0.01" min="0" placeholder="0.00" required class="plan-create__control"/> <small class="plan-create__hint">Precio final en moneda local.</small> <div class="form__error-message" id="error-plan-valor"></div> </div>
            </div>
            <fieldset class="plan-create__grid u-gap-24 u-mt-16" style="border:0; padding:0; margin:0;">
                <div class="plan-create__field"> <label for="plan-cantidad" class="plan-create__label">Cantidad</label> <input id="plan-cantidad" name="cantidad" type="number" min="1" step="1" value="1" required class="plan-create__control"/> <small class="plan-create__hint">Número de unidades.</small> <div class="form__error-message" id="error-plan-cantidad"></div> </div>
                <div class="plan-create__field"> <label for="plan-unidad" class="plan-create__label">Unidad</label> <select id="plan-unidad" name="unidad" required class="plan-create__control"><option value="" selected disabled>Seleccionar</option><option value="1">Días</option><option value="2">Semanas</option><option value="3">Meses</option><option value="4">Años</option></select> <small class="plan-create__hint">Período de duración.</small> <div class="form__error-message" id="error-plan-unidad"></div> </div>
            </fieldset>
            <div class="plan-create__field u-mt-16"> <label for="plan-imagen" class="plan-create__label">Imagen (Opcional)</label> <div class="file file--drop"><input id="plan-imagen" name="imagen" type="file" accept="image/png, image/jpeg" class="file__input" /><div class="file__body"><span class="file__icon">🖼️</span> <span class="file__label">Seleccionar imagen</span> <span class="file__hint">PNG, JPG · máx. 5MB</span></div></div> </div>
            <div class="plan-create__status u-mt-16"> <label class="checkbox"><input type="checkbox" id="plan-activo" name="activo" checked /><span>Plan activo</span></label> </div>
            <div class="plan-create__actions u-mt-24">
                <button type="submit" class="btn btn--primary btn--xl u-w-full">
                    <img src="${pageContext.request.contextPath}/assets/img/muscle.png" alt="" width="24" height="24" style="margin-right: 0.5rem; vertical-align: middle;">
                    Crear Plan
                </button>
            </div>
        </form>
    </section>
</template>

<template id="tpl-plan-editar">
    <section class="view plan-create">
        <header class="view__header"> <a href="#/planes/lista" class="breadcrumb__link u-mb-16">← Volver a la lista</a> <h1 class="view__title">Editar Plan</h1> </header>
        <form id="form-editar-plan" class="plan-create__form entity-edit__form" novalidate> <p>Formulario de edición...</p> </form>
    </section>
</template>

<template id="tpl-clientes-lista">
    <section class="view">
        <header class="view-header"> <div> <h1 class="view__title">Clientes</h1> <p class="view__sub">Gestiona los clientes registrados.</p> </div> <a href="#/clientes/nuevo" class="btn btn--primary">👤 Agregar Cliente</a> </header>
        <div class="tabla-container u-mt-24"> <table class="tabla-entidades"><thead><tr><th>CI</th><th>Nombre Completo</th><th>Email</th><th>Fecha Ingreso</th><th class="u-text-right">Acciones</th></tr></thead><tbody><tr><td><span class="tabla-entidades__id">5.123.456-7</span></td><td><strong>Ana García</strong></td><td>a.garcia@mail.com</td><td>15/10/2025</td><td class="u-text-right"><a href="#/clientes/editar?ci=51234567" class="btn btn--icon">✏️</a><button class="btn btn--icon btn--peligro">🗑️</button></td></tr></tbody></table> </div>
    </section>
</template>

<template id="tpl-cliente-crear">
    <section class="view plan-create">
        <header class="view__header">
            <a href="#/clientes/lista" class="breadcrumb__link u-mb-16">← Volver a la lista</a>
            <h1 class="view__title">Crear Nuevo Cliente</h1>
            <p class="view__sub">Ingresa los datos del nuevo cliente. Todos los campos son obligatorios.</p>
        </header>
        <form id="form-crear-cliente" class="plan-create__form" action="${pageContext.request.contextPath}/api/clientes/crear" method="post" novalidate>
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cliente-ci" class="plan-create__label">Cédula de Identidad</label>
                    <input id="cliente-ci" name="ci" type="text" maxlength="20" placeholder="Ej: 4.123.456-7" required class="plan-create__control"/>
                    <small class="plan-create__hint">Identificador único del cliente.</small>
                    <div class="form__error-message" id="error-cliente-ci"></div>
                </div>
                <div class="plan-create__field">
                    <label for="cliente-email" class="plan-create__label">Email</label>
                    <input id="cliente-email" name="email" type="email" maxlength="120" placeholder="cliente@dominio.com" required class="plan-create__control"/>
                    <small class="plan-create__hint">Correo de contacto.</small>
                    <div class="form__error-message" id="error-cliente-email"></div>
                </div>
            </div>
            <div class="plan-create__grid u-gap-24 u-mt-16">
                <div class="plan-create__field">
                    <label for="cliente-nombre" class="plan-create__label">Nombre</label>
                    <input id="cliente-nombre" name="nombre" type="text" maxlength="60" placeholder="Nombre" required class="plan-create__control"/>
                    <small class="plan-create__hint">Nombre del cliente.</small>
                    <div class="form__error-message" id="error-cliente-nombre"></div>
                </div>
                <div class="plan-create__field">
                    <label for="cliente-apellido" class="plan-create__label">Apellido</label>
                    <input id="cliente-apellido" name="apellido" type="text" maxlength="60" placeholder="Apellido" required class="plan-create__control"/>
                    <small class="plan-create__hint">Apellido del cliente.</small>
                    <div class="form__error-message" id="error-cliente-apellido"></div>
                </div>
            </div>
            <div class="plan-create__grid u-gap-24 u-mt-16">
                <div class="plan-create__field">
                    <label for="cliente-ciudad" class="plan-create__label">Ciudad</label>
                    <input id="cliente-ciudad" name="ciudad" type="text" maxlength="80" placeholder="Ciudad" required class="plan-create__control" />
                    <small class="plan-create__hint">Ciudad de residencia.</small>
                    <div class="form__error-message" id="error-cliente-ciudad"></div>
                </div>
                <div class="plan-create__field">
                    <label for="cliente-pais" class="plan-create__label">País</label>
                    <input id="cliente-pais" name="pais" type="text" maxlength="60" placeholder="Uruguay" required class="plan-create__control" />
                    <small class="plan-create__hint">País de residencia.</small>
                    <div class="form__error-message" id="error-cliente-pais"></div>
                </div>
            </div>
            <div class="plan-create__grid u-gap-24 u-mt-16">
                <div class="plan-create__field">
                    <label for="cliente-direccion" class="plan-create__label">Dirección</label>
                    <input id="cliente-direccion" name="direccion" type="text" maxlength="160" placeholder="Calle, número, apto" required class="plan-create__control"/>
                    <small class="plan-create__hint">Dirección de residencia.</small>
                    <div class="form__error-message" id="error-cliente-direccion"></div>
                </div>
                <div class="plan-create__field">
                    <label for="cliente-telefono" class="plan-create__label">Teléfono</label>
                    <input id="cliente-telefono" name="telefono" type="tel" maxlength="30" placeholder="+598 99 000 000" required class="plan-create__control"/>
                    <small class="plan-create__hint">Número de contacto.</small>
                    <div class="form__error-message" id="error-cliente-telefono"></div>
                </div>
            </div>
            <div class="plan-create__field u-mt-16">
                <label for="cliente-fecha-ingreso" class="plan-create__label">Fecha de Ingreso</label>
                <input id="cliente-fecha-ingreso" name="fecha_ingreso" type="date" class="plan-create__control" required/>
                <small class="plan-create__hint">Fecha en la que se registró el cliente.</small>
                <div class="form__error-message" id="error-cliente-fecha-ingreso"></div>
            </div>
            <div class="plan-create__actions u-mt-24">
                <button type="submit" class="btn btn--primary btn--xl u-w-full">
                    <img src="${pageContext.request.contextPath}/assets/img/user.png" alt="" width="20" height="20" style="margin-right: 0.5rem; vertical-align: middle;">
                    Crear Cliente
                </button>
            </div>
        </form>
    </section>
</template>

<div id="toast-container" class="toast-container"></div>

<script> window.APP_CONTEXT_PATH = "${pageContext.request.contextPath}"; </script>
<script type="module" src="${pageContext.request.contextPath}/assets/js/app.js"></script>

</body>
</html>