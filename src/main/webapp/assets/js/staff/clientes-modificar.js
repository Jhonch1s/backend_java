// File: /assets/js/staff/clientes-modificar.js
(() => {
    "use strict";

    // =============================
    // Config
    // =============================
    const BASE = (document.body && document.body.dataset && document.body.dataset.base) || "";
    const ENDPOINT_SEARCH  = `${BASE}/api/clientes/search`;   // ya existente
    const ENDPOINT_DETALLE = `${BASE}/api/clientes/detalle`;  // nuevo
    const ENDPOINT_RESUMEN = `${BASE}/api/clientes/resumen`;

    const DEBOUNCE_MS = 350;

    // =============================
    // DOM Hooks (según tu JSP)
    // =============================
    const $search = document.querySelector("#lookup-ci");
    const $list   = document.querySelector("#lookup-list");
    const $avatar = document.querySelector(".client-summary__avatar");
    const $kpi = {
        visitasMes:      document.querySelector("#sum-visitas-mes"),
        minPromMes:      document.querySelector("#sum-prom-minutos"),
        entrenosTotales: document.querySelector("#sum-total-entrenos"),
    };

    const $ciHidden = document.querySelector("#ci-hidden");
    const $sumExtra = {
        planNombre:    document.querySelector("#sum-plan-nombre"),
        memEstado:     document.querySelector("#sum-membresia-estado"),
        memVencimiento:document.querySelector("#sum-membresia-vence"),
        ci:            document.querySelector("#sum-ci"),
    };


    const $fields = {
        ci:            document.querySelector("#cliente-ci"),
        nombre:        document.querySelector("#cliente-nombre"),
        apellido:      document.querySelector("#cliente-apellido"),
        email:         document.querySelector("#cliente-email"),
        tel:           document.querySelector("#cliente-telefono"),
        ciudad:        document.querySelector("#cliente-ciudad"),
        direccion:     document.querySelector("#cliente-direccion"),
        pais:          document.querySelector("#cliente-pais"),
        fechaIngreso:  document.querySelector("#cliente-fecha-ingreso")
    };

    const $memImg = document.querySelector("#sum-membresia-img");
    const $memDias = document.querySelector("#sum-membresia-dias");

    // Resumen (si están presentes, los actualizamos)
    const $sum = {
        email:       document.querySelector("#sum-email"),
        tel:         document.querySelector("#sum-tel"),
        dir:         document.querySelector("#sum-dir"),
        ciudadPais:  document.querySelector("#sum-ciudad-pais"),
        ingreso:     document.querySelector("#sum-ingreso")
    };

    if (!$search || !$list) {
        console.warn("[clientes-modificar] No se encontraron #lookup-ci o #lookup-list");
        return;
    }
    $search.setAttribute("role", "combobox");
    $search.setAttribute("aria-autocomplete", "list");
    $search.setAttribute("aria-controls", "lookup-list");
    $search.setAttribute("aria-expanded", "false");


    // Accesibilidad básica del listbox
    $list.setAttribute("role", "listbox");
    $list.setAttribute("aria-label", "Resultados de búsqueda de clientes");
    $list.hidden = true;

    // =============================
    // Estado
    // =============================
    let state = {
        items: [],
        activeIndex: -1,
        selectedCI: null
    };

    // =============================
    // Utils
    // =============================

    // Parsea fecha ISO (YYYY-MM-DD) a Date (sin TZ shift)
    const parseISO = (iso) => {
        if (!iso) return null;
        const [y, m, d] = iso.split("-").map(Number);
        // Date.UTC para evitar desfasajes por timezone
        return new Date(Date.UTC(y, (m - 1), d));
    };

    // Diferencia en días entre hoy y una fecha (fecha - hoy)
    const diffDaysFromToday = (iso) => {
        const target = parseISO(iso);
        if (!target) return null;
        const today = new Date();
        // normalizar "hoy" a UTC 00:00
        const utcToday = new Date(Date.UTC(today.getUTCFullYear(), today.getUTCMonth(), today.getUTCDate()));
        const ms = target.getTime() - utcToday.getTime();
        return Math.round(ms / (1000 * 60 * 60 * 24));
    };

    // Aplica texto y clases a la mini-badge de días restantes
    const setDiasBadge = (days) => {
        if (!$memDias) return;
        // limpiar clases previas
        $memDias.classList.remove("mini-badge--warning", "mini-badge--alerta");

        if (days == null) {
            $memDias.textContent = "-";
            return;
        }

        if (days < 0) {
            $memDias.textContent = `Vencida hace ${Math.abs(days)} día${Math.abs(days) === 1 ? "" : "s"}`;
            $memDias.classList.add("mini-badge--alerta");
        } else if (days === 0) {
            $memDias.textContent = "Vence hoy";
            $memDias.classList.add("mini-badge--alerta");
        } else if (days === 1) {
            $memDias.textContent = "Falta 1 día";
            $memDias.classList.add("mini-badge--warning");
        } else {
            $memDias.textContent = `Faltan ${days} días`;
            // umbrales: <=7 alerta, <=15 warning
            if (days <= 7) $memDias.classList.add("mini-badge--alerta");
            else if (days <= 15) $memDias.classList.add("mini-badge--warning");
        }
    };

// Aplica estilo de estado: chip-ok si ACTIVA
    const setEstadoChip = (estado) => {
        const el = document.querySelector("#sum-membresia-estado");
        if (!el) return;
        el.classList.remove("chip-ok");
        el.textContent = estado || "Sin membresía";
        if ((estado || "").toUpperCase() === "ACTIVA") {
            el.classList.add("chip-ok");
        }
    };

    // Devuelve solo los elementos de sugerencia (ignora spinner u otros hijos)
    const getItemEls = () => Array.from($list.querySelectorAll(".lookup__item"));

    const nvl = (v, def = "-") => (v === undefined || v === null || v === "" ? def : v);
    const num = (v) => (typeof v === "number" ? v : (v != null ? Number(v) : 0));

    // Muestra/oculta spinner si existe (puede estar dentro de #lookup-list)
    const $spinner = document.querySelector("#lookup-list .lookup__spinner");
    const showSpinner = (show) => {
        if (!$spinner) return;
        if (show) {
            $spinner.hidden = false;
            $list.hidden = true; // oculto lista mientras cargo
        } else {
            $spinner.hidden = true;
        }
    };

    const highlightMatch = (text, queryRaw) => {
        if (!text) return "";
        const q = (queryRaw || "").trim();
        if (!q) return esc(text);
        // Buscamos el texto “normalizado” (quitando puntos/guiones) para CI
        const norm = (s) => s.replace(/[.\-\s]/g, "");
        const idx = norm(text).toLowerCase().indexOf(norm(q).toLowerCase());
        if (idx === -1) return esc(text);

        // Mapeo: necesitamos reconstruir el índice sobre el string original
        let origStart = 0, count = 0;
        for (let i = 0; i < text.length && count < idx; i++) {
            if (!/[.\-\s]/.test(text[i])) count++;
            origStart = i + 1;
        }
        // Longitud del match: cantidad de caracteres no separadores en query
        const qLen = norm(q).length;
        let origEnd = origStart, matched = 0;
        for (let i = origStart; i < text.length && matched < qLen; i++) {
            if (!/[.\-\s]/.test(text[i])) matched++;
            origEnd = i + 1;
        }

        const before = esc(text.slice(0, origStart));
        const match  = esc(text.slice(origStart, origEnd));
        const after  = esc(text.slice(origEnd));
        return `${before}<mark class="lookup__hl">${match}</mark>${after}`;
    };

    const normalizeCI = (ci) => (ci || "").replace(/[.\-\s]/g, "").trim();

    const debounce = (fn, ms) => {
        let t;
        return (...args) => {
            clearTimeout(t);
            t = setTimeout(() => fn.apply(null, args), ms);
        };
    };

    const esc = (s) => {
        if (s == null) return "";
        return String(s)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    };

    const clearList = () => {
        $list.innerHTML = "";
        $list.hidden = true;
        state.items = [];
        state.activeIndex = -1;
        $search.setAttribute("aria-expanded", "false");
    };

    const setAvatar = (url, altText) => {
        if (!$avatar) return;
        if (url) {
            $avatar.src = url;
            if (altText && !$avatar.alt) $avatar.alt = altText;
            $avatar.style.visibility = "visible";
        } else {
            $avatar.removeAttribute("src");
            $avatar.style.visibility = "hidden";
        }
    };

    const fillForm = (cliente) => {
        if (!cliente) return;
        $fields.ci && ($fields.ci.value = cliente.ci ?? "");
        $fields.nombre && ($fields.nombre.value = cliente.nombre ?? "");
        $fields.apellido && ($fields.apellido.value = cliente.apellido ?? "");
        $fields.email && ($fields.email.value = cliente.email ?? "");
        $fields.tel && ($fields.tel.value = cliente.tel ?? "");
        $fields.ciudad && ($fields.ciudad.value = cliente.ciudad ?? "");
        $fields.direccion && ($fields.direccion.value = cliente.direccion ?? "");
        $fields.pais && ($fields.pais.value = cliente.pais ?? "");
        $fields.fechaIngreso && ($fields.fechaIngreso.value = cliente.fechaIngreso ?? "");
        if ($fields.ci) $fields.ci.value = cliente.ci ?? "";
        if ($ciHidden)  $ciHidden.value  = cliente.ci ?? "";
    };

    const fillSummary = (cliente, fotoUrl) => {
        if ($sum.email)      $sum.email.textContent = cliente.email || "-";
        if ($sum.tel)        $sum.tel.textContent = cliente.tel || "-";
        if ($sum.dir)        $sum.dir.textContent = cliente.direccion || "-";
        if ($sum.ciudadPais) $sum.ciudadPais.textContent = [cliente.ciudad, cliente.pais].filter(Boolean).join(", ") || "-";
        if ($sum.ingreso)    $sum.ingreso.textContent = cliente.fechaIngreso || "-";
        const alt = `Foto de ${[cliente.nombre, cliente.apellido].filter(Boolean).join(" ")}`.trim();
        setAvatar(fotoUrl || null, alt);

        // Nombre completo y CI en el header
        const $sumNombre = document.querySelector("#sum-nombre");
        if ($sumNombre) $sumNombre.textContent = [cliente.nombre, cliente.apellido].filter(Boolean).join(" ") || "—";

        const $sumCi = document.querySelector("#sum-ci");
        if ($sumCi) $sumCi.textContent = cliente.ci ? `CI ${cliente.ci}` : "CI —";

    };

    const clearFormAndSummary = () => {
        fillForm({
            ci: "", nombre: "", apellido: "", email: "",
            tel: "", ciudad: "", direccion: "", pais: "", fechaIngreso: ""
        });
        fillSummary({
            email: "", tel: "", direccion: "", ciudad: "", pais: "", fechaIngreso: "",
            nombre: "", apellido: ""
        }, null);
        state.selectedCI = null;
    };
    const renderResumen = (resumen) => {
        if (!resumen) return;

        // KPIs
        if ($kpi.visitasMes)      $kpi.visitasMes.textContent      = String(num(resumen.visitasMes));
        if ($kpi.minPromMes)      $kpi.minPromMes.textContent      = resumen.minutosPromedioMes == null ? "-" : String(num(resumen.minutosPromedioMes));
        if ($kpi.entrenosTotales) $kpi.entrenosTotales.textContent = String(num(resumen.entrenosTotales));

        // Plan (nombre) — si no hay, mostrará "Sin plan"
        if ($sumExtra.planNombre) $sumExtra.planNombre.textContent = nvl(resumen.planNombre, "Sin plan");

        // Membresía
        const mem = resumen.membresia || {};
        if ($sumExtra.memEstado)      $sumExtra.memEstado.textContent      = nvl(mem.estado, "Sin membresía");
        if ($sumExtra.memVencimiento) $sumExtra.memVencimiento.textContent = nvl(mem.venceHuman, "-");

        const daysLeft = diffDaysFromToday(mem.venceIso || null);
        setDiasBadge(daysLeft);
        // CI visible (al lado del avatar si tenés #sum-ci)
        if ($sumExtra.ci) $sumExtra.ci.textContent = nvl(resumen.ci, "-");

        // Como fallback, si no hay #sum-ci, al menos dejamos la CI en el alt del avatar
        if (!$sumExtra.ci && $avatar && resumen.ci) {
            $avatar.alt = ($avatar.alt ? $avatar.alt + " — " : "") + `CI: ${resumen.ci}`;
        }
        // Imagen del plan (si viene desde el backend)
        if ($memImg) {
            if (resumen.planImgUrl) {
                $memImg.src = resumen.planImgUrl;
            } else {
                $memImg.src = `${BASE}/assets/img/plan-placeholder.jpg`;
            }
        }

    };

    const renderSuggestions = (items) => {
        // Limpio solo los items previos, no el spinner
        getItemEls().forEach(n => n.remove());

        state.items = items || [];
        state.activeIndex = -1;

        if (!state.items.length) {
            // Sin resultados: oculto lista, oculto spinner
            showSpinner(false);
            $list.hidden = true;
            $search.setAttribute("aria-expanded", "false");
            return;
        }

        const q = $search.value || "";
        const frag = document.createDocumentFragment();

        state.items.forEach((it, idx) => {
            const li = document.createElement("div");
            li.setAttribute("role", "option");
            li.setAttribute("id", `lookup-opt-${idx}`);
            li.className = "lookup__item";
            li.tabIndex = -1;

            const nombreCompleto = [it.nombre, it.apellido].filter(Boolean).join(" ");
            const nameHTML = highlightMatch(nombreCompleto || "-", q);
            const ciHTML   = highlightMatch(it.ci || "", q);
            const emailHTML = esc(it.email || "");

            li.innerHTML = `
      <div class="lookup__row">
        <strong class="lookup__name">${nameHTML}</strong>
        <span class="lookup__ci" aria-label="CI">${ciHTML}</span>
      </div>
      <div class="lookup__email"><small>${emailHTML || "-"}</small></div>
    `;

            li.addEventListener("mousedown", (e) => {
                e.preventDefault();
                selectItem(idx);
            });
            li.addEventListener("mouseenter", () => { state.activeIndex = idx; highlightActive(); });
            li.addEventListener("mouseleave", () => { state.activeIndex = -1; highlightActive(); });

            frag.appendChild(li);
        });

        $list.appendChild(frag);
        showSpinner(false);
        $list.hidden = false;
        $search.setAttribute("aria-expanded", "true");
    };



    const highlightActive = () => {
        const items = getItemEls();
        items.forEach((el, i) => {
            const isActive = i === state.activeIndex;
            el.classList.toggle("is-active", isActive);
            el.setAttribute("aria-selected", isActive ? "true" : "false");
            if (isActive) {
                $search.setAttribute("aria-activedescendant", el.id);
                el.scrollIntoView({ block: "nearest" });
            }
        });
    };



    // =============================
    // Data fetchers
    // =============================
    const fetchResumen = async (ci) => {
        try {
            const url = new URL(ENDPOINT_RESUMEN, location.origin);
            url.searchParams.set("ci", ci);
            const res = await fetch(url.toString(), { headers: { "Accept": "application/json" } });
            if (!res.ok) throw new Error("HTTP " + res.status);
            const json = await res.json();
            if (!json || json.success === false) return;
            renderResumen(json.resumen);
        } catch (err) {
            console.error("[clientes-modificar] resumen error:", err);
        }
    };

    const fetchSuggest = async (ciPartial) => {
        const q = normalizeCI(ciPartial);
        if (!q) {
            getItemEls().forEach(n => n.remove());
            $list.hidden = true;
            showSpinner(false);
            return;
        }
        try {
            showSpinner(true);
            const url = new URL(ENDPOINT_SEARCH, location.origin);
            url.searchParams.set("ci", q);
            url.searchParams.set("limit", "8");

            const res = await fetch(url.toString(), { headers: { "Accept": "application/json" } });
            if (!res.ok) throw new Error("HTTP " + res.status);
            const json = await res.json();

            renderSuggestions(Array.isArray(json?.items) ? json.items : []);
        } catch (err) {
            console.error("[clientes-modificar] suggest error:", err);
            showSpinner(false);
            $list.hidden = true;
        }
    };


    const fetchDetalle = async (ci) => {
        const norm = normalizeCI(ci);
        if (!norm) return;

        try {
            const url = new URL(ENDPOINT_DETALLE, location.origin);
            url.searchParams.set("ci", norm);

            const res = await fetch(url.toString(), { headers: { "Accept": "application/json" } });
            if (!res.ok) throw new Error("HTTP " + res.status);
            const json = await res.json();

            if (!json || json.success === false || !json.cliente) {
                clearFormAndSummary();
                return;
            }
            if ($ciHidden) $ciHidden.value = json.cliente.ci || norm;
            fillForm(json.cliente);
            fillSummary(json.cliente, json.fotoUrl || null);
            state.selectedCI = json.cliente.ci || norm;
            await fetchResumen(norm);
        } catch (err) {
            console.error("[clientes-modificar] detalle error:", err);
            clearFormAndSummary();
        }

    };

    // =============================
    // Selección
    // =============================
    const selectItem = (index) => {
        if (index < 0 || index >= state.items.length) return;
        const it = state.items[index];
        const ci = it?.ci ? normalizeCI(it.ci) : null;
        if (!ci) return;

        // Reflejar en el input de búsqueda y cerrar lista
        $search.value = it.ci;
        clearList();

        // Traer detalle
        fetchDetalle(ci);
    };

    // =============================
    // Eventos
    // =============================
    const onInput = debounce((e) => {
        const val = e.target.value || "";
        state.selectedCI = null;
        fetchSuggest(val);
    }, DEBOUNCE_MS);

    const onKeyDown = (e) => {
        if ($list.hidden) return;
        const items = getItemEls();
        const max = items.length - 1;
        if (max < 0) return;

        switch (e.key) {
            case "ArrowDown":
                e.preventDefault();
                state.activeIndex = Math.min(max, state.activeIndex + 1);
                highlightActive();
                break;
            case "ArrowUp":
                e.preventDefault();
                state.activeIndex = Math.max(0, state.activeIndex - 1);
                highlightActive();
                break;
            case "Enter":
                if (state.activeIndex >= 0) {
                    e.preventDefault();
                    selectItem(state.activeIndex);
                } else if (items.length === 1) {
                    e.preventDefault();
                    selectItem(0);
                }
                break;
            case "Escape":
                getItemEls().forEach(n => n.remove());
                $list.hidden = true;
                $search.setAttribute("aria-expanded", "false");
                break;
        }
    };


    const onBlurClose = () => {
        setTimeout(() => {
            if (!document.activeElement || !($list.contains(document.activeElement))) {
                clearList();
            }
        }, 120);
    };

    document.addEventListener("click", (e) => {
        if ($list.hidden) return;
        if (e.target === $search) return;
        if (!$list.contains(e.target)) clearList();
    });

    // Wire up
    $search.addEventListener("input", onInput);
    $search.addEventListener("keydown", onKeyDown);
    $search.addEventListener("blur", onBlurClose);

    // Autoselección si viene precargado
    if ($search.value && normalizeCI($search.value).length > 0) {
        fetchSuggest($search.value);
    }
    document.addEventListener("DOMContentLoaded", () => {
        const base = document.body.dataset.base || "";
        const prefillCi = document.body.dataset.prefillCi ||
            (new URLSearchParams(location.search).get("ci") || "").replace(/[.\-\s]/g, "");

        if (prefillCi) {
            console.log("[clientes-modificar] Autocargando cliente", prefillCi);
            // Inyecta la CI en el campo de búsqueda y dispara el flujo normal
            const $search = document.querySelector("#lookup-ci");
            if ($search) $search.value = prefillCi;

            // Activar búsqueda y relleno
            if (typeof fetchDetalle === "function") {
                fetchDetalle(prefillCi);
            }
        }
    });
})();
