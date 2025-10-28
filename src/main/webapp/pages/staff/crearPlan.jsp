<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 23/10/2025
  Time: 21:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Crear Plan · Golden Gym</title>

  <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@400;600;700;900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tabla-dashboard.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/toastCliente.css">
</head>
<body>
<%@ include file="/pages/modulos/aside-nav-staff.jsp" %>
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
</body>
</html>
