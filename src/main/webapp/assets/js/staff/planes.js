document.addEventListener("DOMContentLoaded", () => {
    // Modales
    const modalNuevo  = document.getElementById("modalNuevo");
    const modalEditar = document.getElementById("modalEditar");
    const modalEstado = document.getElementById("modalEstado");
    const abrir  = (m) => m?.classList.remove("oculto");
    const cerrar = (m) => m?.classList.add("oculto");
    document.getElementById("btnNuevoPlan")?.addEventListener("click", () => abrir(modalNuevo));

    // X
    document.querySelectorAll(".modal .modal-cerrar").forEach(x => {
        x.addEventListener("click", (e) => cerrar(e.target.closest(".modal")));
    });

    // Cerrar haciendo click fuera
    [modalNuevo, modalEditar, modalEstado].forEach(m => {
        m?.addEventListener("click", (e) => { if (e.target === m) cerrar(m); });
    });

    // Utilidades
    const defaultPlanImg = (window.contextPath || '') + '/assets/img/plan-default.jpg';

    function validarPlan(form) {
        const nombre = form.nombre.value.trim();
        const valor = parseFloat(form.valor.value);
        const cant  = parseInt(form.cantidad.value, 10);
        const unidad = form.unidad.value;
        const err = form.querySelector(".input-error");

        let msg = "";
        if (!nombre) msg = "El nombre es obligatorio.";
        else if (!(valor > 0)) msg = "El precio debe ser mayor a 0.";
        else if (!(cant >= 1)) msg = "La cantidad debe ser 1 o más.";
        else if (!unidad) msg = "Selecciona una unidad.";

        if (msg) {
            err.textContent = msg;
            err.classList.remove("oculto");
            err.style.color = 'var(--color-rojo)';
            return false;
        }
        err.classList.add("oculto");
        return true;
    }

    // Nuevo / Editar
    document.getElementById("formNuevo")?.addEventListener("submit", (e) => {
        if (!validarPlan(e.target)) e.preventDefault();
    });
    document.getElementById("formEditar")?.addEventListener("submit", (e) => {
        if (!validarPlan(e.target)) e.preventDefault();
    });

    // --- Referencias del modal Editar para preview ---
    const editId       = document.getElementById("editId");
    const editNombre   = document.getElementById("editNombre");
    const editValor    = document.getElementById("editValor");
    const editCantidad = document.getElementById("editCantidad");
    const editUnidad   = document.getElementById("editUnidad");
    const editActivo   = document.getElementById("editActivo");

    const editPreview  = document.getElementById("editPreview");
    const editFile     = document.getElementById("editFile");

    document.querySelectorAll(".fila-plan").forEach(row => {
        const btnEditar = row.querySelector(".btn--editar");
        const btnEstado = row.querySelector(".btn--estado");

        // Al cargar la página, el botón de estado muestra "Activar"/"Desactivar" acorde al estado
        if (btnEstado) {
            const estado = row.dataset.estado === "true";
            btnEstado.textContent = estado ? "Desactivar" : "Activar";
            btnEstado.title = estado ? "Desactivar" : "Activar";
        }

        // --- EDITAR ---
        btnEditar?.addEventListener("click", () => {
            const id       = row.dataset.id;
            const nombre   = row.dataset.nombre;
            const valor    = row.dataset.valor;
            const cantidad = row.dataset.cantidad;
            const unidad   = row.dataset.unidad;
            const estado   = row.dataset.estado === "true";

            // Rellenar campos
            if (editId)       editId.value = id;
            if (editNombre)   editNombre.value = nombre;
            if (editValor)    editValor.value = valor;
            if (editCantidad) editCantidad.value = cantidad;
            if (editUnidad)   editUnidad.value = unidad;
            if (editActivo)   editActivo.checked = estado;

            // Cargar preview:
            // preferimos data-urlimagen; si no existe, tomamos el <img> de la fila
            let currentUrl = row.dataset.urlimagen;
            if (!currentUrl || currentUrl === "null" || currentUrl === "undefined") {
                const imgInRow = row.querySelector(".plan-thumb img, td img");
                currentUrl = imgInRow?.getAttribute("src") || defaultPlanImg;
            }
            if (editPreview) editPreview.src = currentUrl || defaultPlanImg;

            abrir(modalEditar);
        });

        // Preview inmediata al elegir un archivo nuevo en el modal Editar
        editFile?.addEventListener("change", () => {
            const f = editFile.files && editFile.files[0];
            if (!f) return;
            if (!f.type || !f.type.startsWith("image/")) {
                alert('Seleccioná una imagen válida.');
                editFile.value = '';
                return;
            }
            if (f.size > 5 * 1024 * 1024) { // 5MB
                alert('La imagen supera los 5MB.');
                editFile.value = '';
                return;
            }
            if (editPreview) editPreview.src = URL.createObjectURL(f);
        });

        // Cambiar estado
        btnEstado?.addEventListener("click", () => {
            const id = row.dataset.id;
            const estadoActual = row.dataset.estado === "true";
            const proximo = !estadoActual;

            document.getElementById("estadoId").value  = id;
            document.getElementById("toEstado").value  = proximo ? "true" : "false";
            document.getElementById("msgEstado").textContent =
                proximo ? "¿Activar este plan?" : "¿Desactivar este plan?";

            abrir(modalEstado);
        });
    });
});