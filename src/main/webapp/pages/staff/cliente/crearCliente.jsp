<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 24/10/2025
  Time: 16:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="es">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Crear Cliente · Golden Gym</title>
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
<body class="layout vista--cliente-crear">
<%@ include file="/pages/modulos/icons-sprite.jsp" %>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>

<main class="layout__content">
    <section class="view card form-card client-create">
        <header class="view__header card__header">
            <h1 class="view__title titillium-negra">Crear Nuevo Cliente</h1>
            <p class="view__sub">Ingresa los datos del nuevo cliente. Todos los campos son obligatorios.</p>
        </header>

        <form id="form-crear-cliente"
              class="form card__body"
              action="${pageContext.request.contextPath}/api/clientes/crear"
              method="post" novalidate>

            <div class="form__grid">
                <div class="field">
                    <label for="cliente-ci" class="label">Cédula de Identidad</label>
                    <input id="cliente-ci" name="ci" type="text" maxlength="20"
                           placeholder="Ej: 4.123.456-7" required class="control"/>
                    <small class="hint">Identificador único del cliente.</small>
                    <div class="error" id="error-cliente-ci"></div>
                </div>

                <div class="field">
                    <label for="cliente-email" class="label">Email</label>
                    <input id="cliente-email" name="email" type="email" maxlength="120"
                           placeholder="cliente@dominio.com" required class="control"/>
                    <small class="hint">Correo de contacto.</small>
                    <div class="error" id="error-cliente-email"></div>
                </div>
            </div>

            <div class="form__grid">
                <div class="field">
                    <label for="cliente-nombre" class="label">Nombre</label>
                    <input id="cliente-nombre" name="nombre" type="text" maxlength="60"
                           placeholder="Nombre" required class="control"/>
                    <small class="hint">Nombre del cliente.</small>
                    <div class="error" id="error-cliente-nombre"></div>
                </div>

                <div class="field">
                    <label for="cliente-apellido" class="label">Apellido</label>
                    <input id="cliente-apellido" name="apellido" type="text" maxlength="60"
                           placeholder="Apellido" required class="control"/>
                    <small class="hint">Apellido del cliente.</small>
                    <div class="error" id="error-cliente-apellido"></div>
                </div>
            </div>

            <div class="form__grid">
                <div class="field">
                    <label for="cliente-ciudad" class="label">Ciudad</label>
                    <input id="cliente-ciudad" name="ciudad" type="text" maxlength="80"
                           placeholder="Ciudad" required class="control" />
                    <small class="hint">Ciudad de residencia.</small>
                    <div class="error" id="error-cliente-ciudad"></div>
                </div>

                <div class="field">
                    <label for="cliente-pais" class="label">País</label>
                    <input id="cliente-pais" name="pais" type="text" maxlength="60"
                           placeholder="Uruguay" required class="control" />
                    <small class="hint">País de residencia.</small>
                    <div class="error" id="error-cliente-pais"></div>
                </div>
            </div>

            <div class="form__grid">
                <div class="field">
                    <label for="cliente-direccion" class="label">Dirección</label>
                    <input id="cliente-direccion" name="direccion" type="text" maxlength="160"
                           placeholder="Calle, número, apto" required class="control"/>
                    <small class="hint">Dirección de residencia.</small>
                    <div class="error" id="error-cliente-direccion"></div>
                </div>

                <div class="field">
                    <label for="cliente-telefono" class="label">Teléfono</label>
                    <input id="cliente-telefono" name="telefono" type="tel" maxlength="30"
                           placeholder="+598 99 000 000" required class="control"/>
                    <small class="hint">Número de contacto.</small>
                    <div class="error" id="error-cliente-telefono"></div>
                </div>
            </div>

            <div class="field">
                <label for="cliente-fecha-ingreso" class="label">Fecha de Ingreso</label>
                <input id="cliente-fecha-ingreso" name="fecha_ingreso" type="date"
                       class="control" required/>
                <small class="hint">Fecha en la que se registró el cliente.</small>
                <div class="error" id="error-cliente-fecha-ingreso"></div>
            </div>

            <div class="form__actions">
                <button type="submit" class="btn btn--primary btn--xl">
                    <svg class="icon" width="20" height="20" aria-hidden="true">
                        <use href="#i-user-add"></use>
                    </svg>
                    Crear Cliente
                </button>
            </div>
        </form>
    </section>
</main>
<div id="toast-container" class="toast-container"></div>

<script src="${pageContext.request.contextPath}/assets/js/staff/clientes-crear.js" defer></script>

</body>
</html>
