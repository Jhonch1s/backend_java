document.addEventListener("DOMContentLoaded", () => {
    const modalNuevo = document.getElementById("modalNuevo");
    const modalEditar = document.getElementById("modalEditar");
    const modalEliminar = document.getElementById("modalEliminar");

    const btnAddProgreso = document.getElementById("btnAddProgreso");
    const btnCancelarNuevo = document.getElementById("btnCancelarNuevo");
    const btnCancelarEditar = document.getElementById("btnCancelarEditar");
    const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");

    // ---------- Funciones utilitarias ----------
    const abrirModal = (modal) => modal.classList.remove("oculto");
    const cerrarModal = (modal) => modal.classList.add("oculto");

    // ---------- Abrir / cerrar modales ----------
    btnAddProgreso?.addEventListener("click", () => abrirModal(modalNuevo));
    btnCancelarNuevo?.addEventListener("click", () => cerrarModal(modalNuevo));
    btnCancelarEditar?.addEventListener("click", () => cerrarModal(modalEditar));
    btnCancelarEliminar?.addEventListener("click", () => cerrarModal(modalEliminar));

    // ---------- Editar progreso ----------
    document.querySelectorAll(".btn-accion[data-action='editar']").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;

            // Buscar datos dentro del mismo card
            const card = btn.closest(".progreso-item");
            const ejercicio = card.querySelector("h3").innerText;
            const fechaTexto = card.querySelector(".texto-dorado").innerText.replace("Fecha: ", "").trim();
            const peso = card.querySelector("p:nth-of-type(2)").innerText.match(/\d+/)?.[0] || "";
            const reps = card.querySelector("p:nth-of-type(3)").innerText.match(/\d+/)?.[0] || "";

            // Convertir fecha dd/MM/yyyy a yyyy-MM-dd
            const partes = fechaTexto.split("/");
            const fechaISO = partes.length === 3 ? `${partes[2]}-${partes[1]}-${partes[0]}` : "";

            document.getElementById("editId").value = id;
            document.getElementById("editFecha").value = fechaISO;
            document.getElementById("editPeso").value = peso;
            document.getElementById("editReps").value = reps;

            abrirModal(modalEditar);
        });
    });

    // ---------- Eliminar progreso ----------
    document.querySelectorAll(".btn-accion[data-action='eliminar']").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            document.getElementById("deleteId").value = id;
            abrirModal(modalEliminar);
        });
    });

    btnCancelarEliminar?.addEventListener("click", () => cerrarModal(modalEliminar));

    // ---------- Cerrar modales al hacer clic fuera ----------
    [modalNuevo, modalEditar, modalEliminar].forEach(modal => {
        modal?.addEventListener("click", (e) => {
            if (e.target === modal) cerrarModal(modal);
        });
    });
});