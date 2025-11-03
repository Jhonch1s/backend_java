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
            const peso = card.querySelector(".kdata p:nth-of-type(1)").innerText.match(/\d+/)?.[0] || "";
            const reps = card.querySelector(".kdata p:nth-of-type(2)").innerText.match(/\d+/)?.[0] || "";

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


    // Cerrar modal al hacer click afuera
    [modalNuevo, modalEditar, modalEliminar].forEach(modal => {
        modal?.addEventListener("click", (e) => {
            if (e.target === modal) cerrarModal(modal);
        });
    });





    // Filtro
    const btnFiltroFecha = document.getElementById("btnFiltroFecha");
    const svg = document.getElementById("svg-filtro");

    // Filtro ejercicio
    const formFiltros = document.getElementById("formFiltros");
    const filtroEjercicio = document.getElementById("filtroEjercicio");
    filtroEjercicio?.addEventListener("change", () => {
        const pageInput = formFiltros.querySelector('input[name="page"]');
        if (pageInput) pageInput.value = "1";
        formFiltros.submit();
    });

    // Boton de filtro asc/desc
    if (btnFiltroFecha && svg) {
        const initial = btnFiltroFecha.dataset.order;
        svg.style.transform = initial === "asc" ? "rotate(180deg)" : "rotate(0deg)";

        btnFiltroFecha.addEventListener("click", () => {
            const order = btnFiltroFecha.dataset.order === "asc" ? "desc" : "asc";
            btnFiltroFecha.dataset.order = order;
            svg.style.transform = order === "asc" ? "rotate(180deg)" : "rotate(0deg)";

            const url = new URL(window.location.href);
            url.searchParams.set("orden", order);
            url.searchParams.set("page", "1"); // reset de paginación
            // preserva idEjercicio si ya estaba en la URL
            window.location.href = url.toString();
        });
    }

    const inputIrPagina = document.getElementById("irPagina");
    const btnIr = document.getElementById("btnIr");

    if (inputIrPagina && btnIr) {
        const ir = () => {
            let page = parseInt(inputIrPagina.value, 10);
            const max = parseInt(inputIrPagina.max, 10);
            if (isNaN(page) || page < 1) page = 1;
            if (page > max) page = max;

            const params = new URLSearchParams(window.location.search);
            const orden = params.get("orden") || "desc";
            const idEjercicio = params.get("idEjercicio");

            let nuevaUrl = `${window.location.pathname}?page=${page}&orden=${orden}`;
            if (idEjercicio) nuevaUrl += `&idEjercicio=${idEjercicio}`;
            window.location.href = nuevaUrl;
        };

        btnIr.addEventListener("click", ir);
        inputIrPagina.addEventListener("keydown", (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                ir();
            }
        });
    }

});