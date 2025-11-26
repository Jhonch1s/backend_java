(() => {
    // ----- BASE desde data-base del <body>
    const BASE = document.body?.dataset?.base || "";
    const ENDPOINT_RESUMEN = BASE + "/api/clientes/resumen";
    const ENDPOINT_DETALLE = (document.body.dataset.base || "") + "/api/clientes/detalle";


    // Esperar DOM por si el script no tiene defer
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }

    function init() {
        // refs modal (tolerantes a null)
        const modal  = document.getElementById("modal-resumen");
        if (!modal) return console.warn("[modal] No existe #modal-resumen en el DOM");
        const dialog = modal.querySelector(".modal__dialog");

        // refs aside
        const avatar= document.getElementById("sum-avatar");
        const kpi = {
            visitasMes:      document.getElementById("sum-visitas-mes"),
            minPromMes:      document.getElementById("sum-prom-minutos"),
            entrenosTotales: document.getElementById("sum-total-entrenos"),
        };
        const sum = {
            nombre:     document.getElementById("sum-nombre"),
            ci:         document.getElementById("sum-ci"),
            email:      document.getElementById("sum-email"),
            tel:        document.getElementById("sum-tel"),
            dir:        document.getElementById("sum-dir"),
            ciudadPais: document.getElementById("sum-ciudad-pais"),
            ingreso:    document.getElementById("sum-ingreso"),
            planNombre: document.getElementById("sum-plan-nombre"),
            memEstado:  document.getElementById("sum-membresia-estado"),
            memVence:   document.getElementById("sum-membresia-vence"),
        };
        const memImg    = document.getElementById("sum-membresia-img");
        const daysBadge = document.getElementById("sum-membresia-dias");

        // helpers
        const nvl = (v, d="—") => (v == null || v === "" ? d : v);
        const num = (v) => Number(v ?? 0);
        const diffDaysFromToday = (iso) => {
            if (!iso) return null;
            const d = new Date(iso + "T00:00:00");
            const t = new Date(); t.setHours(0,0,0,0);
            return Math.round((d.getTime() - t.getTime()) / 86400000);
        };
        const setDiasBadge = (daysLeft) => {
            if (!daysBadge) return;
            daysBadge.className = "mini-badge";
            if (daysLeft == null) { daysBadge.textContent = "—"; return; }
            if (daysLeft > 0)  { daysBadge.textContent = `Faltan ${daysLeft} días`; daysBadge.classList.add("chip-ok"); return; }
            if (daysLeft === 0){ daysBadge.textContent = "Vence hoy";            daysBadge.classList.add("chip-warn"); return; }
            daysBadge.textContent = `Venció hace ${Math.abs(daysLeft)} días`;     daysBadge.classList.add("chip-alerta");
        };
        const setBusy = (busy) => {
            const aside = modal.querySelector(".client-summary");
            if (aside) aside.setAttribute("aria-busy", busy ? "true" : "false");
        };
        const openModal  = () => { modal.classList.remove("u-hide"); modal.setAttribute("aria-hidden","false"); };
        const closeModal = () => { modal.classList.add("u-hide");    modal.setAttribute("aria-hidden","true");  };

        modal.addEventListener("click", (e) => {
            if (e.target.dataset.close) closeModal();
        });
        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape" && !modal.classList.contains("u-hide")) closeModal();
        });

        // fetch + render
        async function fetchResumen(ci) {
            setBusy(true);
            try {
                const url = new URL(ENDPOINT_RESUMEN, location.origin);
                url.searchParams.set("ci", ci);
                const res = await fetch(url.toString(), { headers: { "Accept":"application/json" } });
                if (!res.ok) throw new Error("HTTP " + res.status);
                const json = await res.json();
                if (!json || json.success === false) return;

                const r   = json.resumen || {};
                const mem = r.membresia || {};

                if (kpi.visitasMes)      kpi.visitasMes.textContent      = String(num(r.visitasMes));
                if (kpi.minPromMes)      kpi.minPromMes.textContent      = r.minutosPromedioMes == null ? "—" : String(num(r.minutosPromedioMes));
                if (kpi.entrenosTotales) kpi.entrenosTotales.textContent = String(num(r.entrenosTotales));

                if (sum.planNombre) sum.planNombre.textContent = nvl(r.planNombre, "Sin plan");
                if (memImg)         memImg.src = r.planImgUrl ? r.planImgUrl : (BASE + "/assets/img/plan-placeholder.png");

                if (sum.memEstado)  sum.memEstado.textContent  = (mem.estado.charAt(0).toUpperCase() + mem.estado.slice(1).toLowerCase()).replace("_", " ");
                if (sum.memVence)   sum.memVence.textContent   = nvl(mem.venceHuman, "—");
                setDiasBadge(diffDaysFromToday(mem.venceIso || null));

                if (sum.ci) sum.ci.textContent = "CI " + nvl(r.ci, "—");
            } catch (err) {
                console.error("[clientes-listar] resumen error:", err);
            } finally {
                setBusy(false);
            }
        }

        // delegación: botón "Ver" (NO requiere otros data-*, solo data-ver-ci)
        document.addEventListener("click", (e) => {
            const btn = e.target.closest("[data-ver-ci]");
            if (!btn) return;

            const ci = btn.getAttribute("data-ver-ci");
            if (!ci) return;

            const tr = btn.closest("tr");
            const tds = tr ? tr.querySelectorAll("td") : null;

            // --- prefill básico desde la fila ---
            const nombre = tds?.[1]?.textContent?.trim() || "—";
            const email  = tds?.[2]?.textContent?.trim() || "—";
            const ciudad = tds?.[3]?.textContent?.trim() || "";
            const pais   = tds?.[4]?.textContent?.trim() || "";
            const ingreso= tds?.[5]?.textContent?.trim() || "—";
            const tel    = tr?.querySelector("[data-tel]")?.dataset?.tel || "—";

            // Asignación rápida a los elementos del modal
            const set = (id, val) => { const el = document.getElementById(id); if (el) el.textContent = val; };
            set("sum-nombre", nombre);
            set("sum-ci", "CI " + ci);
            set("sum-email", email);
            set("sum-tel", tel);
            set("sum-ciudad-pais", (ciudad && pais) ? `${ciudad} / ${pais}` : (ciudad || pais || "—"));
            set("sum-ingreso", ingreso);

            // Reset placeholders de plan/membresía mientras carga
            const memImg = document.getElementById("sum-membresia-img");
            if (memImg) memImg.src = (document.body.dataset.base || "") + "/assets/img/plan-placeholder.png";
            set("sum-plan-nombre", "—");
            set("sum-membresia-estado", "—");
            set("sum-membresia-vence", "—");
            const daysBadge = document.getElementById("sum-membresia-dias");
            if (daysBadge) { daysBadge.textContent = "—"; daysBadge.className = "mini-badge"; }

            // Setear botones de membresía
            const registrarBtn = document.getElementById("sum-registrar-btn");
            const renovarBtn = document.getElementById("sum-renovar-btn");
            const cambiarBtn = document.getElementById("sum-cambiar-btn");
            registrarBtn.href = APP_ROOT + "/staff/membresia/registrar?ci=" + ci;
            renovarBtn.href = APP_ROOT + "/staff/membresia/renovar?ci=" + ci;
            cambiarBtn.href = APP_ROOT + "/staff/membresia/cambiar?ci=" + ci;

            // Abrir modal y pedir KPIs/plan/membresía al servlet
            modal.style.opacity = 0;
            openModal();
            setAvatar(null);
            fetchResumen(ci).then(r => fetchAvatar(ci).then(r =>
                modal.style.opacity = 1));
        });

        function setAvatar(url, altText) {
            const img = document.getElementById("sum-avatar");
            if (!img) return;
            if (url) {
                img.src = url;
                if (altText && !img.alt) img.alt = altText;
                img.style.visibility = "visible";
            } else {
                img.removeAttribute("src");
                img.style.visibility = "hidden";
            }
        }

        async function fetchAvatar(ci) {
            try {
                const url = new URL(ENDPOINT_DETALLE, location.origin);
                url.searchParams.set("ci", ci);
                const res = await fetch(url.toString(), { headers: { "Accept": "application/json" } });
                if (!res.ok) throw new Error("HTTP " + res.status);
                const json = await res.json();
                const fotoUrl = json?.fotoUrl || null;

                // alt con nombre si viene en la respuesta (opcional)
                const nombre = [json?.cliente?.nombre, json?.cliente?.apellido].filter(Boolean).join(" ").trim();
                const alt = nombre ? `Foto de ${nombre}` : "Foto del cliente";

                setAvatar(fotoUrl, alt);
            } catch (err) {
                console.warn("[clientes-listar] avatar error:", err);
                setAvatar(null);
            }
        }

    }
})();
