document.addEventListener("DOMContentLoaded", () => {
    // Modales
    const modalNuevo = document.getElementById("modalNuevo");
    const modalEditar = document.getElementById("modalEditar");
    const modalEstado = document.getElementById("modalEstado");
    const abrir = (m) => m?.classList.remove("oculto");
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

    function validarPlan(form) {
        const nombre = form.nombre.value.trim();
        const valor = parseFloat(form.valor.value);
        const cant = parseInt(form.cantidad.value, 10);
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

    // Editar / Estado
    document.querySelectorAll(".fila-plan").forEach(row => {
        const btnEditar = row.querySelector(".btn--editar");
        const btnEstado = row.querySelector(".btn--estado");

        btnEditar?.addEventListener("click", () => {
            const id = row.dataset.id;
            const nombre = row.dataset.nombre;
            const valor = row.dataset.valor;
            const cantidad = row.dataset.cantidad;
            const unidad = row.dataset.unidad;
            const estado = row.dataset.estado === "true";

            document.getElementById("editId").value = id;
            document.getElementById("editNombre").value = nombre;
            document.getElementById("editValor").value = valor;
            document.getElementById("editCantidad").value = cantidad;

            const sel = document.getElementById("editUnidad");
            if (sel) sel.value = unidad;

            const chk = document.getElementById("editActivo");
            if (chk) chk.checked = estado;

            abrir(modalEditar);
        });

        btnEstado?.addEventListener("click", () => {
            const id = row.dataset.id;
            const estadoActual = row.dataset.estado === "true";
            const toEstado = !estadoActual;

            document.getElementById("estadoId").value = id;
            document.getElementById("toEstado").value = toEstado ? "true" : "false";
            document.getElementById("msgEstado").textContent =
                toEstado ? "¿Activar este plan?" : "¿Desactivar este plan?";
            abrir(modalEstado);
        });
    });
});