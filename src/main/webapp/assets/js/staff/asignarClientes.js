document.addEventListener('DOMContentLoaded', () => {

    const filtroCI = document.getElementById('filtro-ci');
    const filtroEstado = document.getElementById('filtro-estado-rutina');
    const listaClientesCont = document.getElementById('lista-clientes-rutinas');
    const todasLasTarjetas = Array.from(listaClientesCont.querySelectorAll('.cliente-rutina-card'));

    const paginacionControles = document.getElementById('paginacion-controles');
    const btnAnterior = document.getElementById('btn-anterior');
    const btnSiguiente = document.getElementById('btn-siguiente');
    const contadorPagina = document.getElementById('contador-pagina');

    let paginaActual = 1;
    const ITEMS_POR_PAGINA = 10;
    let tarjetasFiltradasGlobal = [...todasLasTarjetas]; // Lista que se va a paginar

    function actualizarVista() {
        const estadoQuery = filtroEstado.value;

        tarjetasFiltradasGlobal = todasLasTarjetas.filter(card => {
            const estadoTarjeta = card.dataset.estadoRutina;
            if (estadoQuery === 'todos') {
                return true; // Mostrar todos
            }
            return estadoTarjeta === estadoQuery;
        });

        const totalPaginas = Math.ceil(tarjetasFiltradasGlobal.length / ITEMS_POR_PAGINA);
        if (paginaActual > totalPaginas) {
            paginaActual = 1; // Resetea si la página actual ya no existe
        }
        if (totalPaginas <= 0) { // Caso sin resultados
            paginaActual = 1;
        }

        const inicio = (paginaActual - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;
        const tarjetasDeEstaPagina = tarjetasFiltradasGlobal.slice(inicio, fin);

        todasLasTarjetas.forEach(card => card.style.display = 'none');
        tarjetasDeEstaPagina.forEach(card => card.style.display = 'grid');

        if (totalPaginas > 1) {
            paginacionControles.style.display = 'flex';
            contadorPagina.textContent = `Pág ${paginaActual} / ${totalPaginas}`;
            btnAnterior.disabled = (paginaActual === 1);
            btnSiguiente.disabled = (paginaActual === totalPaginas);
        } else {
            paginacionControles.style.display = 'none'; // Oculta si solo hay 1 pág
        }
    }

    filtroEstado.addEventListener('change', () => {
        filtroCI.value = '';
        paginaActual = 1;
        actualizarVista();
    });

    let ciTimer;
    filtroCI.addEventListener('input', () => {
        clearTimeout(ciTimer);
        const query = filtroCI.value.trim();

        filtroEstado.value = 'todos';

        if (query.length === 0) {
            paginaActual = 1;
            actualizarVista();
            return;
        }

        ciTimer = setTimeout(() => {
            if (query.length >= 3) {
                buscarClientesPorCI(query);
            }
        }, 300);
    });

    btnAnterior.addEventListener('click', () => {
        if (paginaActual > 1) {
            paginaActual--;
            actualizarVista();
        }
    });

    btnSiguiente.addEventListener('click', () => {
        const totalPaginas = Math.ceil(tarjetasFiltradasGlobal.length / ITEMS_POR_PAGINA);
        if (paginaActual < totalPaginas) {
            paginaActual++;
            actualizarVista();
        }
    });

    async function buscarClientesPorCI(ci) {
        paginacionControles.style.display = 'none'; // Oculta paginación
        try {
            const response = await fetch(`${contextPath}/api/clientes/search?ci=${ci}&limit=50`);
            const data = await response.json();

            if (data.success && data.items) {
                todasLasTarjetas.forEach(card => card.style.display = 'none');
                data.items.forEach(clienteEncontrado => {
                    const tarjeta = listaClientesCont.querySelector(`.cliente-rutina-card[data-ci="${clienteEncontrado.ci}"]`);
                    if (tarjeta) {
                        tarjeta.style.display = 'grid';
                    }
                });
            }
        } catch (error) {
            console.error("Error buscando por CI:", error);
        }
    }



    const modal = document.getElementById('modal-asignar-rutina');
    const btnCerrarModal = document.getElementById('btn-cerrar-modal');
    const modalClienteNombre = document.getElementById('modal-cliente-nombre');
    const modalClienteIdInput = document.getElementById('modal-cliente-id');
    const modalSelectRutina = document.getElementById('modal-select-rutina');
    const modalFecha = document.getElementById('modal-fecha-asignacion');
    const formAsignar = document.getElementById('form-asignar-rutina');
    const feedbackAsignacion = document.getElementById('feedback-asignacion');
    const errorRutina = document.getElementById('error-rutina');
    const errorFecha = document.getElementById('error-fecha');

    listaClientesCont.addEventListener('click', (e) => {
        const btn = e.target.closest('.btn-abrir-modal');
        if (btn) {
            const id = btn.dataset.clienteId;
            const nombre = btn.dataset.clienteNombre;
            modalClienteNombre.textContent = nombre;
            modalClienteIdInput.value = id;
            modalFecha.value = new Date().toISOString().split('T')[0];
            feedbackAsignacion.textContent = '';
            errorRutina.textContent = '';
            errorFecha.textContent = '';
            modal.style.display = 'flex';
            cargarRutinasDisponibles();
        }
    });

    btnCerrarModal.addEventListener('click', () => modal.style.display = 'none');
    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.style.display = 'none';
    });

    let rutinasCargadas = false;
    async function cargarRutinasDisponibles() {
        if (rutinasCargadas) return;
        modalSelectRutina.disabled = true;
        modalSelectRutina.innerHTML = '<option value="">Cargando rutinas...</option>';
        try {
            const response = await fetch(`${contextPath}/api/rutinas-disponibles`);
            const rutinas = await response.json();
            if (response.ok) {
                modalSelectRutina.innerHTML = '<option value="" disabled selected>Selecciona una rutina...</option>';
                rutinas.forEach(rutina => {
                    modalSelectRutina.innerHTML += `<option value="${rutina.id}">${rutina.nombre}</option>`;
                });
                modalSelectRutina.disabled = false;
                rutinasCargadas = true;
            } else {
                throw new Error("No se pudieron cargar las rutinas");
            }
        } catch (error) {
            console.error("Error cargando rutinas:", error);
            modalSelectRutina.innerHTML = '<option value="">Error al cargar</option>';
        }
    }

    formAsignar.addEventListener('submit', async (e) => {
        e.preventDefault();
        let esValido = true;
        if (!modalSelectRutina.value) {
            errorRutina.textContent = 'Debes seleccionar una rutina.';
            esValido = false;
        } else {
            errorRutina.textContent = '';
        }
        if (!modalFecha.value) {
            errorFecha.textContent = 'Debes seleccionar una fecha.';
            esValido = false;
        } else {
            errorFecha.textContent = '';
        }
        if (!esValido) return;

        Swal.fire({
            title: 'Asignando rutina...',
            text: 'Por favor esperá.',
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading()
        });

        const formData = new FormData(formAsignar);
        try {
            const response = await fetch(`${contextPath}/admin/guardar-asignacion-rutina`, {
                method: 'POST',
                body: new URLSearchParams(formData)
            });
            const resultado = await response.json();
            if (response.ok && resultado.success) {
                Swal.fire({
                    icon: 'success',
                    title: '¡Rutina Asignada!',
                    text: 'El cliente ha sido actualizado.',
                    timer: 1500,
                    showConfirmButton: false
                }).then(() => {
                    location.reload();
                });
            } else {
                throw new Error(resultado.error || 'Error en el servidor');
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error al Asignar',
                text: error.message
            });
        }
    });

    actualizarVista();
});