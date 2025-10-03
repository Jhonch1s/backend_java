<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <!-- Favicons para dar iconos a la web si se usa desde móbil y se quiere anclar al inicio, etc. -->
    <link rel="apple-touch-icon" sizes="180x180" href="assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="assets/img/favicon-16x16.png">
    <link rel="manifest" href="assets/img/site.webmanifest">
    <link rel="icon" href="assets/img/favicon.ico">

    <!-- Tipografía, la sacamo de google fonts -->
    <link
            href="https://fonts.googleapis.com/css2?family=Titillium+Web:ital,wght@0,200;0,300;0,400;0,600;0,700;0,900;1,200;1,300;1,400;1,600;1,700&display=swap"
            rel="stylesheet">

    <!-- Normalize -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
          integrity="sha512-NhSC1X0f3zp3p2JtYh8C2W4TyTX0b6x1n00x4bZ4Zk3E2b9GmZy1wKkPe4v5YyX1Y9i6w5W2rszj0o9uGZ7xwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Utilidades y estilos -->
    <title>Panel Administracion · Golden Gym</title>
    <link rel="stylesheet" href="assets/css/layout-spa.css">
    <link rel="stylesheet" href="assets/css/utilidades.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>

<body class="u-bg">

<div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar" aria-label="Navegación principal">
        <div class="sidebar__brand">
            <button class="sidebar__toggle" aria-label="Alternar menú" aria-expanded="false">☰</button>
            <span class="sidebar__title">Golden Gym</span>
        </div>

        <nav class="sidebar__nav">
            <h3 class="sidebar__section">Planes</h3>
            <ul>
                <li><a href="#/planes/nuevo" data-link>➕ Crear plan</a></li>
                <li><a href="#/planes/editar" data-link>✏️ Editar plan</a></li>
            </ul>

            <h3 class="sidebar__section">Clientes</h3>
            <ul>
                <li><a href="#/clientes/nuevo" data-link>👤 Crear cliente</a></li>
                <li><a href="#/clientes/editar" data-link>✏️ Editar cliente</a></li>
            </ul>

            <h3 class="sidebar__section">Listados</h3>
            <ul>
                <li><a href="#/planes/lista" data-link>📋 Lista de planes</a></li>
                <li><a href="#/clientes/lista" data-link>📋 Lista de clientes</a></li>
            </ul>
        </nav>
    </aside>

    <!-- Contenido -->
    <main id="app-view" class="main u-mx-auto u-maxw-1100 u-p-32 main-panel" tabindex="-1" aria-live="polite" aria-busy="false">
        <!-- El router inyecta aquí la vista -->
        <div class="main__placeholder">
            <h1>Bienvenido/a</h1>
            <p>Usá el menú lateral para navegar por las secciones.</p>
        </div>
    </main>
</div>

<!-- ===================== -->
<!-- TEMPLATES DE LAS VISTAS -->
<!-- ===================== -->

<!-- PLAN · CREAR (ejemplo completo) -->
<template id="tpl-plan-crear">
    <section class="view plan-create">
        <header class="view__header">
            <h1 class="view__title">Crear Plan de Membresía</h1>
            <p class="view__sub">Alta de un nuevo plan. Los campos marcados con * son obligatorios.</p>
        </header>

        <!-- crear plan -->
        <form class="plan-create__form" action="php/crear/crearplan.php" method="post" enctype="multipart/form-data"
              novalidate>

            <!-- Nombre + Valor -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="plan-nombre" class="plan-create__label">Nombre del Plan</label>
                    <input id="plan-nombre" name="nombre" type="text" placeholder="Ej: Plan Premium"
                           class="plan-create__control input input--text u-w-full" required maxlength="80" />
                    <small class="plan-create__hint">Nombre comercial visible para los clientes.</small>
                </div>

                <div class="plan-create__field">
                    <label for="plan-valor" class="plan-create__label">Valor ($)</label>
                    <input id="plan-valor" name="valor" type="number" step="0.01" min="0" inputmode="decimal"
                           placeholder="0.00" class="plan-create__control input input--text u-w-full" required />
                    <small class="plan-create__hint">Precio final en moneda local.</small>
                </div>
            </div>

            <!-- Duración -->
            <fieldset class="plan-create__grid u-gap-24" aria-label="Duración del Plan">
                <div class="plan-create__field">
                    <label for="plan-cantidad" class="plan-create__label">Cantidad</label>
                    <input id="plan-cantidad" name="cantidad" type="number" min="1" step="1" value="1"
                           class="plan-create__control input input--text u-w-full" required />
                    <small class="plan-create__hint">Número de unidades de tiempo.</small>
                </div>

                <div class="plan-create__field">
                    <label for="plan-unidad" class="plan-create__label">Unidad</label>
                    <select id="plan-unidad" name="unidad" class="plan-create__control select u-w-full" required>
                        <option value="" selected disabled>Seleccionar unidad</option>
                        <option value="1">Días</option>
                        <option value="2">Semanas</option>
                        <option value="3">Meses</option>
                        <option value="4">Años</option>
                    </select>
                    <small class="plan-create__hint">Período en el que se mide la duración.</small>
                </div>
            </fieldset>

            <!-- Imagen -->
            <div class="plan-create__field u-mt-8">
                <label for="plan-imagen" class="plan-create__label">Imagen del Plan</label>
                <div class="file file--drop plan-create__control u-w-full" data-js="file-drop">
                    <input id="plan-imagen" name="imagen" type="file" accept="image/*" class="file__input" />
                    <div class="file__body">
                        <span class="file__icon" aria-hidden="true">📁</span>
                        <span class="file__label">Seleccionar imagen</span>
                        <span class="file__hint">PNG, JPG o SVG · máx. 2&nbsp;MB</span>
                    </div>
                </div>
            </div>

            <!-- Estado -->
            <div class="plan-create__status u-mt-16">
                <label class="checkbox checkbox--lg plan-create__checkbox">
                    <input type="checkbox" id="plan-activo" name="activo" checked />
                    <span>Plan activo y disponible</span>
                </label>
            </div>

            <!-- Acciones -->
            <div class="plan-create__actions u-mt-24">
                <button type="submit" class="btn btn--primary btn--xl u-w-full">
                    💪 Crear Plan de Membresía
                </button>
            </div>
        </form>
    </section>
</template>

<!-- PLAN · EDITAR -->
<template id="tpl-plan-editar">
    <section class="view plan-create">
        <header class="view__header">
            <h1 class="view__title">Editar Plan</h1>
            <p class="view__sub">Modificá un plan existente.</p>
        </header>
        <!-- editar plan-->
        <form class="plan-create__form entity-edit__form" action="#" method="post" enctype="multipart/form-data"
              novalidate>
            <!-- ID oculto del plan por si luego lo usamo en backend -->
            <input type="hidden" name="plan_id" value="1">

            <!-- Banner modo edición -->
            <div class="cliente-edit__banner" role="status" aria-live="polite">
                🛠️ Modo edición — Estás modificando un plan existente
            </div>

            <!-- Nombre + Valor -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="plan-nombre" class="plan-create__label">Nombre del Plan</label>
                    <input id="plan-nombre" name="nombre" type="text" maxlength="80"
                           class="plan-create__control input input--text u-w-full" value="Anual policial" required />
                    <small class="plan-create__hint">Nombre comercial visible para los clientes.</small>
                </div>

                <div class="plan-create__field">
                    <label for="plan-valor" class="plan-create__label">Valor ($)</label>
                    <input id="plan-valor" name="valor" type="number" step="0.01" min="0" inputmode="decimal"
                           class="plan-create__control input input--text u-w-full" value="14000" required />
                    <small class="plan-create__hint">Precio final en moneda local.</small>
                </div>
            </div>

            <!-- Duración -->
            <fieldset class="plan-create__grid u-gap-24" aria-label="Duración del Plan">
                <div class="plan-create__field">
                    <label for="plan-cantidad" class="plan-create__label">Cantidad</label>
                    <input id="plan-cantidad" name="cantidad" type="number" min="1" step="1"
                           class="plan-create__control input input--text u-w-full" value="12" required />
                    <small class="plan-create__hint">Número de unidades de tiempo.</small>
                </div>

                <div class="plan-create__field">
                    <label for="plan-unidad" class="plan-create__label">Unidad</label>
                    <select id="plan-unidad" name="unidad" class="plan-create__control select u-w-full" required>
                        <option value="" disabled>Seleccionar unidad</option>
                        <option value="1">Día</option>
                        <option value="2">Semana</option>
                        <option value="3">Mes</option>
                        <option value="4">Año</option>
                    </select>
                    <small class="plan-create__hint">Período en el que se mide la duración.</small>
                </div>
            </fieldset>

            <!-- Imagen del Plan -->
            <div class="plan-create__field u-mt-8">
                <label class="plan-create__label">Imagen del Plan</label>

                <div class="entity-edit__media-row">
                    <!-- Vista actual -->
                    <img class="entity-edit__media"
                         src="assets/img/img-plan.webp"
                         alt="Arte actual del plan" />

                    <!-- Acciones -->
                    <div class="entity-edit__media-actions">
                        <label class="checkbox">
                            <input type="checkbox" name="mantener_imagen" value="1" checked />
                            <span>Mantener imagen actual</span>
                        </label>

                        <div class="file file--drop plan-create__control u-w-full u-mt-8" data-js="file-drop">
                            <input id="plan-imagen" name="imagen" type="file" accept="image/*"
                                   class="file__input" />
                            <div class="file__body">
                                <span class="file__icon" aria-hidden="true">📁</span>
                                <span class="file__label">Reemplazar imagen</span>
                                <span class="file__hint">PNG, JPG o SVG · máx. 2&nbsp;MB</span>
                            </div>
                        </div>
                        <small class="plan-create__hint">Desmarcá “Mantener imagen” si querés subir una
                            nueva.</small>
                    </div>
                </div>
            </div>

            <!-- Estado -->
            <div class="plan-create__status u-mt-16">
                <label class="checkbox checkbox--lg plan-create__checkbox">
                    <input type="checkbox" id="plan-activo" name="activo" checked>
                    <span>Plan activo y disponible</span>
                </label>
            </div>

            <!-- Acciones -->
            <div class="plan-create__actions u-mt-24">
                <div class="entity-edit__actions-row">
                    <button type="submit" class="btn btn--warning btn--xl">💾 Guardar cambios</button>
                    <button type="reset" class="btn btn--ghost btn--xl">↩️ Deshacer</button>
                </div>
                <small class="entity-edit__unsaved" aria-live="polite">Cuidado: hay cambios sin guardar.</small>
            </div>
        </form>
    </section>
</template>

<!-- CLIENTE · CREAR -->
<template id="tpl-cliente-crear">
    <section class="view plan-create">
        <header class="view__header">
            <h1 class="view__title">Crear Cliente</h1>
            <p class="view__sub">Alta de un nuevo cliente.</p>
        </header>
        <!-- nuevo cliente-->
        <form class="plan-create__form" action="php/crear/crearcliente.php" method="post"
              enctype="multipart/form-data" novalidate>

            <!-- Identificación + Contacto -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-ci" class="plan-create__label">CI / Documento</label>
                    <input id="cli-ci" name="ci" type="text" maxlength="20" placeholder="Ej: 4.123.456-7"
                           class="plan-create__control input input--text u-w-full" required />
                    <small class="plan-create__hint">Identificador único del cliente.</small>
                </div>

                <div class="plan-create__field">
                    <label for="cli-email" class="plan-create__label">Email</label>
                    <input id="cli-email" name="email" type="email" maxlength="120"
                           placeholder="cliente@dominio.com" class="plan-create__control input input--text u-w-full"
                           required />
                    <small class="plan-create__hint">Correo de contacto.</small>
                </div>
            </div>

            <!-- Nombre y Apellido -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-nombre" class="plan-create__label">Nombre</label>
                    <input id="cli-nombre" name="nombre" type="text" maxlength="60" placeholder="Nombre"
                           class="plan-create__control input input--text u-w-full" required />
                    <small class="plan-create__hint">Nombre del cliente.</small>
                </div>

                <div class="plan-create__field">
                    <label for="cli-apellido" class="plan-create__label">Apellido</label>
                    <input id="cli-apellido" name="apellido" type="text" maxlength="60" placeholder="Apellido"
                           class="plan-create__control input input--text u-w-full" required />
                    <small class="plan-create__hint">Apellido del cliente.</small>
                </div>
            </div>

            <!-- Ubicación -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-ciudad" class="plan-create__label">Ciudad</label>
                    <input id="cli-ciudad" name="ciudad" type="text" maxlength="80" placeholder="Ciudad"
                           class="plan-create__control input input--text u-w-full" />
                </div>

                <div class="plan-create__field">
                    <label for="cli-pais" class="plan-create__label">País</label>
                    <input id="cli-pais" name="pais" type="text" maxlength="60" placeholder="Uruguay"
                           class="plan-create__control input input--text u-w-full" />
                </div>
            </div>

            <!-- Dirección y Teléfono -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-direccion" class="plan-create__label">Dirección</label>
                    <input id="cli-direccion" name="direccion" type="text" maxlength="160"
                           placeholder="Calle, número, apto" class="plan-create__control input input--text u-w-full" />
                </div>

                <div class="plan-create__field">
                    <label for="cli-tel" class="plan-create__label">Teléfono</label>
                    <input id="cli-tel" name="tel" type="tel" maxlength="30" placeholder="+598 99 000 000"
                           class="plan-create__control input input--text u-w-full" />
                </div>
            </div>

            <!-- Fecha de ingreso -->
            <div class="plan-create__field u-mt-8">
                <label for="cli-fecha" class="plan-create__label">Fecha de ingreso</label>
                <input id="cli-fecha" name="fecha_ingreso" type="date"
                       class="plan-create__control input input--text u-w-full" required />
                <small class="plan-create__hint">Fecha en la que se registró el cliente.</small>
            </div>

            <!-- Acciones -->
            <div class="plan-create__actions u-mt-24">
                <button type="submit" class="btn btn--primary btn--xl u-w-full">
                    👤 Crear Cliente
                </button>
            </div>
        </form>
    </section>
</template>

<!-- CLIENTE · EDITAR -->
<template id="tpl-cliente-editar" class="plan-create">
    <section class="view plan-create">
        <header class="view__header">
            <h1 class="view__title">Editar Cliente</h1>
            <p class="view__sub">Modificá un cliente existente.</p>
        </header>
        <!-- editar cliente -->

        <form class="plan-create__form cliente-edit__form" action="#" method="post" enctype="multipart/form-data"
              novalidate>

            <!-- Cinta para diferenciar de insert a usr-->
            <div class="cliente-edit__banner" role="status" aria-live="polite">
                ✏️ Modo edición — Estás modificando un cliente existente
            </div>

            <!-- Identificación + Contacto -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-ci" class="plan-create__label">CI / Documento</label>
                    <input id="cli-ci" name="ci" type="text" maxlength="20"
                           class="plan-create__control input input--text u-w-full" value="12345678" readonly
                           aria-readonly="true" />
                    <small class="plan-create__hint">Este campo es fijo y no puede modificarse.</small>
                </div>

                <div class="plan-create__field">
                    <label for="cli-email" class="plan-create__label">Email</label>
                    <input id="cli-email" name="email" type="email" maxlength="120"
                           class="plan-create__control input input--text u-w-full" value="ejemplo@ejemplo.com"
                           required />
                    <small class="plan-create__hint">Correo de contacto.</small>
                </div>
            </div>

            <!-- Nombre y Apellido -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-nombre" class="plan-create__label">Nombre</label>
                    <input id="cli-nombre" name="nombre" type="text" maxlength="60"
                           class="plan-create__control input input--text u-w-full" value="Javier" required />
                </div>

                <div class="plan-create__field">
                    <label for="cli-apellido" class="plan-create__label">Apellido</label>
                    <input id="cli-apellido" name="apellido" type="text" maxlength="60"
                           class="plan-create__control input input--text u-w-full" value="Hornos" required />
                </div>
            </div>

            <!-- Ubi -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-ciudad" class="plan-create__label">Ciudad</label>
                    <input id="cli-ciudad" name="ciudad" type="text" maxlength="80"
                           class="plan-create__control input input--text u-w-full" value="Río Negro" />
                </div>

                <div class="plan-create__field">
                    <label for="cli-pais" class="plan-create__label">País</label>
                    <input id="cli-pais" name="pais" type="text" maxlength="60"
                           class="plan-create__control input input--text u-w-full" value="Uruguay" />
                </div>
            </div>

            <!-- Dir y Tel -->
            <div class="plan-create__grid u-gap-24">
                <div class="plan-create__field">
                    <label for="cli-direccion" class="plan-create__label">Dirección</label>
                    <input id="cli-direccion" name="direccion" type="text" maxlength="160"
                           class="plan-create__control input input--text u-w-full" value="Sarandí 1344" />
                </div>

                <div class="plan-create__field">
                    <label for="cli-tel" class="plan-create__label">Teléfono</label>
                    <input id="cli-tel" name="tel" type="tel" maxlength="30"
                           class="plan-create__control input input--text u-w-full" value="098987789" />
                </div>
            </div>

            <!-- Fecha de ingreso -->
            <div class="plan-create__field u-mt-8">
                <label for="cli-fecha" class="plan-create__label">Fecha de ingreso</label>
                <input id="cli-fecha" name="fecha_ingreso" type="date"
                       class="plan-create__control input input--text u-w-full" value="05/02/2020" required />
                <small class="plan-create__hint">DD-MM-AAAA</small>
            </div>

            <!-- Acciones (guardar deshacer) -->
            <div class="plan-create__actions u-mt-24">
                <div class="cliente-edit__actions-row">
                    <button type="submit" class="btn btn--warning btn--xl">💾 Guardar cambios</button>
                    <button type="reset" class="btn btn--ghost btn--xl">↩️ Deshacer</button>
                </div>
                <small class="cliente-edit__unsaved" aria-live="polite">Cuidado: hay cambios sin
                    guardar.</small>
            </div>
        </form>
    </section>
</template>

<!-- Listas (stub para futuro) -->
<template id="tpl-planes-lista">
    <section class="view">
        <header class="view__header">
            <h1 class="view__title">Lista de planes</h1>
        </header>
        <p>Acá podés renderizar tu tabla de planes.</p>
    </section>
</template>

<template id="tpl-clientes-lista">
    <section class="view">
        <header class="view__header">
            <h1 class="view__title">Lista de clientes</h1>
        </header>
        <p>Acá podés renderizar tu tabla de clientes.</p>
    </section>
</template>

<!-- JS (siempre al final) -->
<script type="module" src="assets/js/app.js"></script>
<script src="assets/js/sidebar.js" defer></script>
</body>

</html>