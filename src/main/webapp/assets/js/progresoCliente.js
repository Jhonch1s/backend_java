document.addEventListener("DOMContentLoaded", () => {
    const modalNuevo = document.getElementById("modalNuevo");
    const modalEditar = document.getElementById("modalEditar");
    const modalEliminar = document.getElementById("modalEliminar");

    const btnAddProgreso = document.getElementById("btnAddProgreso");
    const btnCancelarEditar = document.getElementById("btnCancelarEditar");
    const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");

    const abrirModal = (modal) => modal.classList.remove("oculto");
    const cerrarModal = (modal) => modal.classList.add("oculto");

    // Abrir / cerrar modales
    btnAddProgreso?.addEventListener("click", () => abrirModal(modalNuevo));
    btnCancelarEditar?.addEventListener("click", () => cerrarModal(modalEditar));
    btnCancelarEliminar?.addEventListener("click", () => cerrarModal(modalEliminar));

    document.querySelectorAll(".modal-cerrar").forEach(btn => {
        btn.addEventListener("click", () => {
            const modal = btn.closest(".modal");
            if (modal) modal.classList.add("oculto");
        });
    });


    // Editar progreso
    document.querySelectorAll(".btn-accion[data-action='editar']").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;

            const card = btn.closest(".progreso-item");
            const idEjercicio = card.dataset.ejercicio;
            const fechaTexto = card.querySelector(".texto-dorado").innerText.replace("Fecha: ", "").trim();
            const peso = card.querySelector("p:nth-of-type(2)").innerText.match(/\d+/)?.[0] || "";
            const reps = card.querySelector("p:nth-of-type(3)").innerText.match(/\d+/)?.[0] || "";

            const partes = fechaTexto.split("/");
            const fechaISO = partes.length === 3 ? `${partes[2]}-${partes[1]}-${partes[0]}` : "";

            document.getElementById("editId").value = id;
            document.getElementById("editFecha").value = fechaISO;
            document.getElementById("editPeso").value = peso;
            document.getElementById("editReps").value = reps;

            // Seleccionar el ejercicio correcto en el select
            const selectEj = document.getElementById("editEjercicio");
            if (selectEj && idEjercicio) {
                selectEj.value = idEjercicio;
            }

            abrirModal(modalEditar);
        });
    });

    // Eliminar progreso
    document.querySelectorAll(".btn-accion[data-action='eliminar']").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            document.getElementById("deleteId").value = id;
            abrirModal(modalEliminar);
        });
    });

    btnCancelarEliminar?.addEventListener("click", () => cerrarModal(modalEliminar));

    function validarFormulario(form) {
        const fecha = new Date(form.fecha.value);
        const hoy = new Date();
        const peso = parseFloat(form.pesoUsado.value);
        const reps = parseInt(form.repeticiones.value);
        const errorMsg = form.querySelector('.input-error');
        let error = "";

        if (fecha > hoy) {
            error = "No se pueden registrar progresos en fechas futuras.";
        } else if (peso <= 0) {
            error = "El peso debe ser mayor que 0.";
        } else if (reps <= 0) {
            error = "Las repeticiones deben ser mayores que 0.";
        }

        if (error) {
            errorMsg.textContent = error;
            errorMsg.classList.remove('oculto');
            errorMsg.style.color = 'var(--color-rojo)';
            return false;
        }

        errorMsg.classList.add('oculto');
        return true;
    }

    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', e => {
            if (!validarFormulario(form)) e.preventDefault();
        });
    });

    // Cerrar modales al hacer clic fuera
    [modalNuevo, modalEditar, modalEliminar].forEach(modal => {
        modal?.addEventListener("click", (e) => {
            if (e.target === modal) cerrarModal(modal);
        });
    });
});