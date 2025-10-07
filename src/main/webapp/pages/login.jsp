<!DOCTYPE html>
<html lang="es-UY">

<head>
    <meta charset="utf-8" />
    <%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Iniciar sesión | Golden Gym</title>

    <!-- Favicons para dar iconos a la web si se usa desde móbil y se quiere anclar al inicio, etc. -->
    <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
    <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

    <!-- Tipografía, la sacamo de google fonts -->
    <link
            href="https://fonts.googleapis.com/css2?family=Titillium+Web:ital,wght@0,200;0,300;0,400;0,600;0,700;0,900;1,200;1,300;1,400;1,600;1,700&display=swap"
            rel="stylesheet">

    <!-- Normalize -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
          integrity="sha512-NhSC1X0f3zp3p2JtYh8C2W4TyTX0b6x1n00x4bZ4Zk3E2b9GmZy1wKkPe4v5YyX1Y9i6w5W2rszj0o9uGZ7xwA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Utilidades y estilos -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="fondo-oscuro texto-claro">

<main class="login contenedor-pantalla alinear-centro">
    <section class="login__tarjeta sombra-suave borde-redondeado p-3">
        <header class="login__encabezado alinear-centro-column gap-2">
            <h1 class="login__marca titillium-negra texto-dorado m-0">GOLDEN GYM</h1>
            <img class="login__logo" src="${pageContext.request.contextPath}/assets/img/icon2.svg" alt="Golden Gym">
        </header>

        <form class="login__formulario mt-3" id="form-login" action="${pageContext.request.contextPath}/login" method="POST" novalidate>
            <div class="login__campo">
                <label class="login__label" for="usuario">Usuario</label>
                <input class="login__input" type="text" id="usuario" name="usuario" inputmode="numeric"
                       placeholder="Nombre de Usuario" autocomplete="username" required
                       aria-describedby="error-usuario">
                <p class="login__error" id="error-usuario" role="alert" aria-live="polite"></p>
            </div>

            <div class="login__campo">
                <label class="login__label" for="contrasena">Contraseña</label>
                <div class="login__input-grupo">
                    <input class="login__input" type="password" id="contrasenia" name="contrasenia"
                           placeholder="Contraseña" autocomplete="current-password" required minlength="8"
                           aria-describedby="error-contrasena">
                    <button class="login__ver-contrasena" type="button" aria-label="Mostrar u ocultar contraseña">
                        <!-- Ícono con SVG para ahorrar memoria -->
                        <svg class="icono" viewBox="0 0 24 24" aria-hidden="true">
                            <path
                                    d="M12 5c-7 0-11 7-11 7s4 7 11 7 11-7 11-7-4-7-11-7Zm0 12a5 5 0 1 1 0-10 5 5 0 0 1 0 10Z" />
                        </svg>
                    </button>
                </div>
                <p class="login__error" id="error-contrasena" role="alert" aria-live="polite"></p>
            </div>

            <div class="login__extras alinear-centro-between mt-2">
                <label class="login__recordarme alinear-centro gap-1">
                    <input type="checkbox" id="recordarme" name="recordarme" />
                    <span>Recordarme</span>
                </label>

                <a href="#" class="login__olvido">¿Olvidaste tu contraseña?</a>
            </div>

            <div class="login__acciones mt-3">
                <button class="login__boton boton-primario" type="submit">Ingresar</button>
            </div>

            <p class="login__error-global" id="error-global" role="alert" aria-live="polite"></p>
        </form>
    </section>
</main>

<script src="${pageContext.request.contextPath}/assets/js/login.js" defer></script>
</body>

</html>