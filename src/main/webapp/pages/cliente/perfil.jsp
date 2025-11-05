<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 16/10/2025
  Time: 11:03
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
  <title>Cliente | Perfil · Golden Gym</title>

  <!-- Favicons -->
  <link rel="apple-touch-icon" sizes="180x180" href="${pageContext.request.contextPath}/assets/img/apple-touch-icon.png">
  <link rel="icon" type="image/png" sizes="32x32" href="${pageContext.request.contextPath}/assets/img/favicon-32x32.png">
  <link rel="icon" type="image/png" sizes="16x16" href="${pageContext.request.contextPath}/assets/img/favicon-16x16.png">
  <link rel="manifest" href="${pageContext.request.contextPath}/assets/img/site.webmanifest">
  <link rel="icon" href="${pageContext.request.contextPath}/assets/img/favicon.ico">

  <!-- Tipografía + Normalize -->
  <link href="https://fonts.googleapis.com/css2?family=Titillium+Web:wght@300;400;600;700;900&display=swap" rel="stylesheet">
  <link rel="stylesheet"
        href="https://cdnjs.cloudflare.com/ajax/libs/normalize/8.0.1/normalize.min.css"
        integrity="sha512-NhSC1X0f3zp3p2JtYh8C2W4TyTX0b6x1n00x4bZ4Zk3E2b9GmZy1wKkPe4v5YyX1Y9i6w5W2rszj0o9uGZ7xwA=="
        crossorigin="anonymous" referrerpolicy="no-referrer" />

  <!-- Estilos base existentes -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/utilidades.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/layout-spa.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

  <!-- Estilos del dashboard de cliente (referencia visual/hero/buttons) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-dashboard.css">

  <!-- Estilos específicos del perfil (ajustes mínimos) -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cliente-perfil.css?v=20251016b">
</head>

<body>
<main class="cliente-home cliente-home--fixed">

  <section class="hero">
    <div class="hero__row">
      <!-- Columna izquierda: avatar + nombre + meta -->
      <div class="hero__left">
        <div class="hero__top alinear-centro">
          <c:choose>
            <c:when test="${not empty clienteFotoUrl}">
              <c:set var="heroAvatar"
                     value="${fn:replace(clienteFotoUrl, '/upload/', '/upload/w_128,h_128,c_fill,g_face,q_auto,f_auto/')}"/>
              <img src="${heroAvatar}"
                   alt="Foto de ${cliente.nombre} ${cliente.apellido}"
                   class="hero__avatar-img"
                   style="width:64px;height:64px;border-radius:50%;object-fit:cover;border:1px solid var(--gg-border);" />
            </c:when>
            <c:otherwise>
              <div class="hero__avatar">${fn:substring(cliente.nombre, 0, 1)}${fn:substring(cliente.apellido, 0, 1)}</div>
            </c:otherwise>
          </c:choose>
          <div>
            <h1 class="m-0 titillium-negra" id="hero-nombre">${cliente.nombre} ${cliente.apellido}</h1>
            <p class="m-0 hero__meta" id="hero-meta">${metaLinea}</p>
          </div>
        </div>
      </div>
      <div class="hero__actions">
        <a id="btn-editar-perfil" class="btn btn--lg btn--primary-yellow btn-hero" href="javascript:void(0)">
          Editar perfil
        </a>
      </div>
    </div>

    <!-- kpis que quedan lindos -->
    <div class="hero__stats mt-3">
      <div>
        <p class="hero__stat-number">${kpiEntrenamientosMes}</p>
        <p class="hero__stat-label">Trainings (mes)</p>
      </div>
      <div>
        <p class="hero__stat-number">${kpiMinutosPromedioMes}</p>
        <p class="hero__stat-label">Min. promedio</p>
      </div>
      <div>
        <p class="hero__stat-number">${kpiKgMes}</p>
        <p class="hero__stat-label">Kg levantados</p>
      </div>
    </div>
  </section>

  <section class="perfil-grid">
    <article class="tarjeta">
      <div class="bloque__head">
        <h2 class="bloque__title m-0">Datos de contacto</h2>
      </div>
      <ul class="lista-datos mt-3">
        <c:if test="${not empty cliente.tel}">
          <li>
            <span class="label">Teléfono</span>
            <span id="contacto-tel">
        <a href="tel:${fn:replace(fn:replace(cliente.tel, ' ', ''), '-', '')}">
            ${cliente.tel}
        </a>
      </span>
          </li>
        </c:if>

        <c:if test="${not empty cliente.direccion}">
          <li>
            <span class="label" >Dirección</span>
            <span id="contacto-direccion" >${cliente.direccion}</span>
          </li>
        </c:if>

        <c:if test="${not empty cliente.email}">
          <li>
            <span class="label">Email</span>
            <span id="contacto-email"><a href="mailto:${cliente.email}">${cliente.email}</a></span>
          </li>
        </c:if>
      </ul>

    </article>

    <!-- Alta en el gym -->
    <article class="tarjeta">
      <div class="bloque__head">
        <h2 class="bloque__title m-0">Alta en el gimnasio</h2>
      </div>
      <p class="m-0">Ingreso: <strong>${fechaIngresoFmt}</strong></p>
      <p class="m-0">Antigüedad: <strong>${antiguedadHumana}</strong></p>
    </article>

    <!-- Membresía -->
    <article class="tarjeta">
      <div class="bloque__head">
        <h2 class="bloque__title m-0">Membresía</h2>
      </div>

      <c:choose>
        <c:when test="${not empty membresiaActiva}">
          <!-- Imagen del plan -->
          <div class="mt-2">
            <img src="${membresiaImg}"
                 alt="Plan ${membresiaPlanNombre}"
                 style="width:100%;max-height:160px;object-fit:cover;border-radius:12px;border:1px solid var(--gg-border);" />
          </div>

          <div class="membresia-grid mt-2">
          <p class="m-0 mt-2">Plan: <strong>${membresiaPlanNombre}</strong></p>


          <p class="m-0 mt-1">Vence: <strong>${membresiaVenceFmt}</strong></p>
          <c:if test="${not empty membresiaDiasRestantes}">
            <p class="m-0 mt-1"><span class="mini-badge
                 <c:if test='${membresiaBadgeTipo == "warning"}'> mini-badge--warning</c:if>
                 <c:if test='${membresiaBadgeTipo == "alerta"}'> mini-badge--alerta</c:if>">
      <c:choose>
        <c:when test="${membresiaDiasRestantes == 0}">Vence hoy</c:when>
        <c:when test="${membresiaDiasRestantes == 1}">Falta 1 día</c:when>
        <c:otherwise>Faltan ${membresiaDiasRestantes} días</c:otherwise>
      </c:choose>
    </span>
            </p>
          <p class="m-0 mt-1">
            Estado:
            <c:choose>
              <c:when test="${membresiaEstadoId == 1}">
                <span class="chip-ok">${membresiaEstadoTexto}</span>
              </c:when>
              <c:otherwise>
                <span>${membresiaEstadoTexto}</span>
              </c:otherwise>
            </c:choose>
          </p>
          </c:if>

        </c:when>

        <c:otherwise>
          <div class="mt-2">
            <img src="${membresiaImg}"
                 alt="Plan por defecto"
                 style="width:100%;max-height:160px;object-fit:cover;border-radius:12px;border:1px solid var(--gg-border);" />
          </div>

          <p class="m-0 mt-2">Sin membresía activa.</p>
          <p class="m-0 mt-1">Podés contratar o renovar un plan.</p>
          <a class="btn btn--lg btn--primary-yellow mt-2"
             href="${pageContext.request.contextPath}/membresias?ci=${cliente.ci}">
            Contratar / Renovar
          </a>
        </c:otherwise>
      </c:choose>
          </div>
    </article>
    <article class="tarjeta">
      <form action="${pageContext.request.contextPath}/logout" method="get" style="margin-top: 0rem;">
        <button type="submit" class="btn btn-logout">Cerrar sesión</button>
      </form>
    </article>

  </section>
<div class="modal" id="modal-editar" aria-hidden="true">
  <div class="modal__overlay" data-close="true"></div>
  <div class="modal__dialog" role="dialog" aria-modal="true" aria-labelledby="modal-editar-titulo" tabindex="-1">
    <div class="modal__header">
      <h3 id="modal-editar-titulo" class="m-0 titillium-negra">Editar perfil</h3>
      <button class="modal__close js-close" aria-label="Cerrar">×</button>
    </div>
    <form class="modal__body" method="post" action="${pageContext.request.contextPath}/cliente/editar">
      <div class="form-grid">
        <label class="login__label">Nombre
          <input class="login__input" type="text" name="nombre" value="${cliente.nombre}" required>
        </label>

        <label class="login__label">Apellido
          <input class="login__input" type="text" name="apellido" value="${cliente.apellido}" required>
        </label>

        <label class="login__label">Email
          <input class="login__input" type="email" name="email" value="${cliente.email}">
        </label>

        <label class="login__label">Teléfono
          <input class="login__input" type="text" name="tel" value="${cliente.tel}">
        </label>

        <label class="login__label">Dirección
          <input class="login__input" type="text" name="direccion" value="${cliente.direccion}">
        </label>

        <label class="login__label">Ciudad
          <input class="login__input" type="text" name="ciudad" value="${cliente.ciudad}">
        </label>

        <label class="login__label">País
          <input class="login__input" type="text" name="pais" value="${cliente.pais}">
        </label>

        <input type="hidden" name="ci" value="${cliente.ci}">
      </div>

      <div class="modal__actions">
        <button type="button" class="btn btn--lg btn--ghost-yellow js-close">Cancelar</button>
        <button type="submit" class="btn btn--lg btn--primary-yellow">Guardar cambios</button>
      </div>
    </form>
    <!-- FORM 2: Foto de perfil (independiente) -->
    <form class="modal__body mt-16"
          method="post"
          action="${pageContext.request.contextPath}/cliente/foto/subir"
          enctype="multipart/form-data">
      <input type="hidden" name="ci" value="${cliente.ci}">
      <div class="form-grid">
        <div>
          <label class="login__label">Foto de perfil
            <input class="login__input" type="file" name="imagen" accept="image/*" id="file-foto" required>
          </label>
          <small class="help">Formatos: JPG/PNG. Máx ~5MB.</small>
        </div>
        <div class="avatar-preview">
          <c:choose>
            <c:when test="${empty clienteFotoUrl}">
              <!-- arma la URL al asset con el contextPath -->
              <c:url var="previewSrc" value="/assets/img/avatar-default.svg"/>
            </c:when>
            <c:otherwise>
              <!-- aplica la transformación de Cloudinary -->
              <c:set var="previewSrc"
                     value="${fn:replace(clienteFotoUrl, '/upload/', '/upload/w_96,h_96,c_fill,g_face,q_auto,f_auto/')}"/>
            </c:otherwise>
          </c:choose>

          <img id="preview-foto"
               src="${previewSrc}"
               alt="Vista previa"
               style="width:96px;height:96px;border-radius:50%;object-fit:cover;border:1px solid var(--gg-border);" />

        </div>
      </div>

      <div class="modal__actions">
        <button type="submit" class="btn btn--lg btn--primary-yellow">Actualizar foto</button>
      </div>
    </form>
  </div>
</div>
  <div id="toast" class="toast" aria-live="polite" aria-atomic="true"></div>

  <!-- sticky del nav abajo -->
  <%@ include file="/pages/modulos/bottom-nav.jsp" %>
</main>

<script>
  // por ahora dejamos aqui el js del modal para edit perfil
  (function(){
    const modal = document.getElementById('modal-editar');
    const dialog  = modal.querySelector('.modal__dialog');
    const form = modal.querySelector('form[action$="/cliente/editar"]');
    const btnSave = form.querySelector('button[type="submit"]');
    const toastEl = document.getElementById('toast');
    const openBtn = document.getElementById('btn-editar-perfil');

    function openModal(){
      modal.classList.add('is-open');
      document.body.classList.add('modal-open');
      // foco accesible
      setTimeout(()=> dialog.focus(), 0);
    }
    function closeModal(){
      modal.classList.remove('is-open');
      document.body.classList.remove('modal-open');
    }
    openBtn?.addEventListener('click', openModal);
    modal.addEventListener('click', (e)=>{
      if (e.target.classList.contains('js-close') || e.target.dataset.close === 'true') {
        closeModal();
      }
    });
    document.addEventListener('keydown', (e)=>{
      if (e.key === 'Escape' && modal.classList.contains('is-open')) closeModal();
    });

    function showToast(msg, ok=true){
      if(!toastEl) return;
      toastEl.textContent = msg;
      toastEl.className = 'toast ' + (ok ? 'toast--ok' : 'toast--err');
      void toastEl.offsetWidth; // reflow
      toastEl.classList.add('toast--show');
      setTimeout(()=> toastEl.classList.remove('toast--show'), 2200);
    }
    function closeModal(){
      modal.classList.remove('is-open');
      document.body.classList.remove('modal-open');
    }

    function setDisabled(disabled){
      btnSave.disabled = disabled;
      btnSave.textContent = disabled ? 'Guardando…' : 'Guardar cambios';
    }

    // update en vivo del header y contacto
    function liveUpdateFromForm(fd){
      // header: nombre + apellido
      const nombre = (fd.get('nombre') || '').trim();
      const apellido = (fd.get('apellido') || '').trim();
      const email = (fd.get('email') || '').trim();
      const ciudad = (fd.get('ciudad') || '').trim();
      const pais = (fd.get('pais') || '').trim();
      const tel = (fd.get('tel') || '').trim();
      const dir = (fd.get('direccion')|| '').trim();

      const heroNombre = document.getElementById('hero-nombre');
      if (heroNombre) heroNombre.textContent = (nombre + ' ' + apellido).trim();

      // CI +email + ubicación
      const heroMeta  = document.getElementById('hero-meta');
      if (heroMeta) {
        // Intentamos leer la CI que ya está en el meta actual
        const metaActual = heroMeta.textContent || '';
        const ciMatch = metaActual.match(/CI\s+([0-9\.]+)/i);
        const ci = ciMatch ? ('CI ' + ciMatch[1]) : '';
        const partes = [];
        if (ci) partes.push(ci);
        if (email) partes.push(email);
        if (ciudad || pais) partes.push([ciudad, pais].filter(Boolean).join(', '));
        heroMeta.textContent = partes.join(' · ');
      }

      // Chips de ubi
      const chips = document.getElementById('chips-ubicacion');
      if (chips) {
        const frags = [];
        if (ciudad) frags.push('<span class="chip">' + ciudad + '</span>');
        if (pais) frags.push('<span class="chip">' + pais   + '</span>');
        chips.innerHTML = frags.join(' ');
      }

      // Datos de contacto
      const telSpan = document.getElementById('contacto-tel');
      if (telSpan) telSpan.textContent = tel || '—';

      const dirSpan = document.getElementById('contacto-direccion');
      if (dirSpan) dirSpan.textContent = dir || '—';

      const mailSpan = document.getElementById('contacto-email');
      if (mailSpan) mailSpan.textContent = email || '—';
    }

    form.addEventListener('submit', async (e)=>{
      e.preventDefault();
      const fd = new FormData(form);
      setDisabled(true);
      try{
        const resp = await fetch(form.action, {
          method: 'POST',
          body: new URLSearchParams(fd),
          headers: { 'Accept':'application/json' }
        });
        const data = await resp.json().catch(()=>({ok:false,msg:'Respuesta inválida'}));
        if (!resp.ok || !data.ok) {
          showToast(data.msg || 'No se pudo guardar', false);
          setDisabled(false);
          return;
        }
        //  cerramos modal, toast y actualizamos la UI
        closeModal();
        showToast(data.msg || 'Perfil actualizado', true);
        liveUpdateFromForm(fd);
        setDisabled(false);
      } catch(err){
        showToast('Error de red: ' + (err?.message || 'desconocido'), false);
        setDisabled(false);
      }
    });
  })();
</script>
<script>
  (function(){
    const input = document.getElementById('file-foto');
    const img   = document.getElementById('preview-foto');
    if (!input || !img) return;

    input.addEventListener('change', () => {
      const f = input.files && input.files[0];
      if (!f) return;
      if (!f.type || !f.type.startsWith('image/')) {
        alert('Seleccioná una imagen válida.');
        input.value = '';
        return;
      }
      if (f.size > 5 * 1024 * 1024) { // 5MB
        alert('La imagen supera los 5MB.');
        input.value = '';
        return;
      }
      const url = URL.createObjectURL(f);
      img.src = url;
    });
  })();
</script>
</body>
</html>
