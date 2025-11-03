document.addEventListener('DOMContentLoaded', () => {

    const filtroCI = document.getElementById('filtro-ci');
    const filtroEstado = document.getElementById('filtro-estado-rutina');
    const listaClientesCont = document.getElementById('lista-clientes-rutinas');
    const todasLasTarjetas = listaClientesCont ? Array.from(listaClientesCont.querySelectorAll('.cliente-rutina-card')) : [];
    const paginacionControles = document.getElementById('paginacion-controles');
    const btnAnterior = document.getElementById('btn-anterior');
    const btnSiguiente = document.getElementById('btn-siguiente');
    const contadorPagina = document.getElementById('contador-pagina');
    let paginaActual = 1;
    const ITEMS_POR_PAGINA = 12;
    let tarjetasFiltradasGlobal = [...todasLasTarjetas];

    const modal = document.getElementById('modal-asignar-rutina');
    const btnCerrarModal = document.getElementById('btn-cerrar-modal');
    const modalClienteNombre = document.getElementById('modal-cliente-nombre');
    const modalClienteIdHidden = document.getElementById('modal-cliente-id-hidden');
    const modalRutinasActuales = document.getElementById('modal-rutinas-actuales');
    const modalRutinasDisponibles = document.getElementById('modal-rutinas-disponibles');
    const formAsignar = document.getElementById('form-asignar-rutina');
    const modalFecha = document.getElementById('modal-fecha-asignacion');
    const modalRutinasIdsSeleccionadas = document.getElementById('modal-rutinas-ids-seleccionadas');
    const errorRutina = document.getElementById('error-rutina');
    const errorFecha = document.getElementById('error-fecha');
    const feedbackAsignacion = document.getElementById('feedback-asignacion');

    const modalFiltroRutina = document.getElementById('modal-filtro-rutina');

    const modalFiltroObjetivo = document.getElementById('modal-filtro-objetivo');
    const modalFiltroDuracion = document.getElementById('modal-filtro-duracion');

    const modalPaginacionControles = document.getElementById('modal-paginacion-controles');
    const btnModalAnt = document.getElementById('modal-anterior');
    const btnModalSig = document.getElementById('modal-siguiente');
    const modalContador = document.getElementById('modal-contador');

    let modalPaginaActual = 1;
    const MODAL_POR_PAGINA = 5;
    let rutinasDisponiblesGlobal = [];
    let rutinasFiltradasGlobal = [];
    let idsRutinasAsignadas = new Set();
    let idsSeleccionados = [];
    let idsAsignacionesARemover = new Set();
    let ciTimer;


    function actualizarVista() {
        const estadoQuery = filtroEstado.value;
        tarjetasFiltradasGlobal = todasLasTarjetas.filter(card => {
            const estadoTarjeta = card.dataset.estadoRutina;
            if (estadoQuery === 'todos') return true;
            return estadoTarjeta === estadoQuery;
        });
        const totalPaginas = Math.ceil(tarjetasFiltradasGlobal.length / ITEMS_POR_PAGINA);
        if (paginaActual > totalPaginas) paginaActual = 1;
        if (totalPaginas <= 0) paginaActual = 1;
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
            paginacionControles.style.display = 'none';
        }
    }

    async function buscarClientesPorCI(ci) {
        paginacionControles.style.display = 'none';
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

    async function cargarDatosModal(clienteId) {
        try {
            const response = await fetch(`${contextPath}/api/rutinas-disponibles?clienteId=${clienteId}`);
            const data = await response.json();
            if (!response.ok || !data.success) {
                throw new Error(data.error || "No se pudieron cargar los datos");
            }

            idsRutinasAsignadas.clear();

            data.asignadas.forEach(r => idsRutinasAsignadas.add(String(r.rutinaId)));

            renderRutinasAsignadas(data.asignadas);

            rutinasDisponiblesGlobal = data.disponibles;

            filtrarYRenderizarModal();

        } catch (error) {
            console.error("Error cargando datos del modal:", error);
            modalRutinasActuales.innerHTML = `<p style="color:var(--color-peligro)">Error: ${error.message}</p>`;
            modalRutinasDisponibles.innerHTML = `<p style="color:var(--color-peligro)">Error: ${error.message}</p>`;
        }
    }

    function filtrarYRenderizarModal() {
        // 1. Obtenemos los 3 valores de los filtros
        const queryNombre = modalFiltroRutina.value.toLowerCase().trim();
        const queryObjetivo = modalFiltroObjetivo.value;
        const queryDuracion = modalFiltroDuracion.value;

        // 2. Aplicamos los 3 filtros
        rutinasFiltradasGlobal = rutinasDisponiblesGlobal.filter(rutina => {
            // Condición 1: Nombre
            const matchNombre = rutina.nombre && rutina.nombre.toLowerCase().includes(queryNombre);

            // (rutina.objetivo ya viene del backend, ej: "HIPERTROFIA")
            const matchObjetivo = (queryObjetivo === 'todos') || (rutina.objetivo === queryObjetivo);

            const matchDuracion = (queryDuracion === 'todas') || (rutina.duracionSemanas == queryDuracion);

            return matchNombre && matchObjetivo && matchDuracion;
        });

        modalPaginaActual = 1;
        renderRutinasDisponiblesPaginado();
    }

    function renderRutinasAsignadas(asignadas) {
        modalRutinasActuales.innerHTML = '';
        if (asignadas.length === 0) {
            modalRutinasActuales.innerHTML = '<p>Este cliente no tiene rutinas activas.</p>';
            return;
        }
        asignadas.forEach(rutina => {
            let fechaFormateada = "";
            if (rutina.fechaAsignacion) {
                let fechaObj = rutina.fechaAsignacion;
                if (typeof fechaObj === 'string') {
                    const partes = fechaObj.split('-');
                    if (partes.length === 3) {
                        fechaFormateada = `${partes[2]}/${partes[1]}/${partes[0]}`;
                    }
                } else if (fechaObj.year && fechaObj.month && fechaObj.day) {
                    fechaFormateada = `${String(fechaObj.day).padStart(2, '0')}/${String(fechaObj.month).padStart(2, '0')}/${fechaObj.year}`;
                }
            }
            const itemHtml = `
                <div class="rutina-actual-item" id="asignacion-${rutina.asignacionId}">
                    <span>
                        ${rutina.rutinaNombre}
                        <br><span class="fecha">(Asignada: ${fechaFormateada})</span>
                    </span>
                    <button class="btn-quitar-simple" data-asignacion-id="${rutina.asignacionId}" data-rutina-id="${rutina.rutinaId}" data-nombre-rutina="${rutina.rutinaNombre}">×</button>
                </div>
            `;
            modalRutinasActuales.insertAdjacentHTML('beforeend', itemHtml);
        });
    }

    // --- FUNCIÓN 6: RENDERIZAR RUTINAS DISPONIBLES (SIN CAMBIOS) ---
    function renderRutinasDisponiblesPaginado() {
        modalRutinasDisponibles.innerHTML = '';

        if (rutinasFiltradasGlobal.length === 0) {
            modalRutinasDisponibles.innerHTML = '<p>No se encontraron rutinas.</p>';
            modalPaginacionControles.style.display = 'none';
            return;
        }

        const totalPaginas = Math.ceil(rutinasFiltradasGlobal.length / MODAL_POR_PAGINA);
        if (totalPaginas <= 0) modalPaginaActual = 1;

        const inicio = (modalPaginaActual - 1) * MODAL_POR_PAGINA;
        const fin = inicio + MODAL_POR_PAGINA;
        const rutinasPaginadas = rutinasFiltradasGlobal.slice(inicio, fin);

        rutinasPaginadas.forEach(rutina => {
            let objTexto = (rutina.objetivo?.toLowerCase() || "").replace('_', ' ');
            objTexto = objTexto.charAt(0).toUpperCase() + objTexto.slice(1);

            const estaSeleccionada = idsSeleccionados.includes(String(rutina.id));
            const estaAsignada = idsRutinasAsignadas.has(String(rutina.id));

            const itemHtml = `
                <label class="checkbox-card-rutina ${estaAsignada ? 'ya-asignada' : ''}">
                    <input 
                        type="checkbox" 
                        class="checkbox-rutina"
                        value="${rutina.id}" 
                        ${estaSeleccionada ? 'checked' : ''}
                        ${estaAsignada ? 'disabled' : ''}
                    >
                    <div class="checkbox-card-info">
                        <strong>${rutina.nombre}</strong>
                        <div class="card-labels">
                            <span class="grupo-label">${objTexto}</span>
                        </div>
                    </div>
                </label>
                `;
            modalRutinasDisponibles.insertAdjacentHTML('beforeend', itemHtml);
        });

        if (totalPaginas > 1) {
            modalPaginacionControles.style.display = 'flex';
            modalContador.textContent = `Pág ${modalPaginaActual} / ${totalPaginas}`;
            btnModalAnt.disabled = (modalPaginaActual === 1);
            btnModalSig.disabled = (modalPaginaActual === totalPaginas);
        } else {
            modalPaginacionControles.style.display = 'none';
        }
    }


    // --- INICIALIZACIÓN Y LISTENERS ---

    if (filtroEstado) {
        filtroEstado.addEventListener('change', () => {
            filtroCI.value = '';
            paginaActual = 1;
            actualizarVista();
        });
    }

    if (filtroCI) {
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
    }

    if (btnAnterior) {
        btnAnterior.addEventListener('click', () => {
            if (paginaActual > 1) {
                paginaActual--;
                actualizarVista();
            }
        });
    }

    if (btnSiguiente) {
        btnSiguiente.addEventListener('click', () => {
            const totalPaginas = Math.ceil(tarjetasFiltradasGlobal.length / ITEMS_POR_PAGINA);
            if (paginaActual < totalPaginas) {
                paginaActual++;
                actualizarVista();
            }
        });
    }

    if (modalFiltroRutina) {
        modalFiltroRutina.addEventListener('input', filtrarYRenderizarModal);
    }
    if (modalFiltroObjetivo) {
        modalFiltroObjetivo.addEventListener('change', filtrarYRenderizarModal);
    }
    if (modalFiltroDuracion) {
        modalFiltroDuracion.addEventListener('change', filtrarYRenderizarModal);
    }

    if (btnModalAnt) {
        btnModalAnt.addEventListener('click', (e) => {
            e.preventDefault();
            if (modalPaginaActual > 1) {
                modalPaginaActual--;
                renderRutinasDisponiblesPaginado();
            }
        });
    }

    if (btnModalSig) {
        btnModalSig.addEventListener('click', (e) => {
            e.preventDefault();
            const totalPaginas = Math.ceil(rutinasFiltradasGlobal.length / MODAL_POR_PAGINA);
            if (modalPaginaActual < totalPaginas) {
                modalPaginaActual++;
                renderRutinasDisponiblesPaginado();
            }
        });
    }

    if (listaClientesCont) {
        listaClientesCont.addEventListener('click', (e) => {
            const btn = e.target.closest('.btn-abrir-modal');
            if (btn) {
                const id = btn.dataset.clienteId;
                const nombre = btn.dataset.clienteNombre;
                modalClienteNombre.textContent = nombre;
                modalClienteIdHidden.value = id;
                modalFecha.value = new Date().toISOString().split('T')[0];
                feedbackAsignacion.textContent = '';
                errorRutina.textContent = '';
                errorFecha.textContent = '';
                modalRutinasActuales.innerHTML = '<p>Cargando...</p>';
                modalRutinasDisponibles.innerHTML = '<p>Cargando...</p>';
                idsSeleccionados = [];
                idsAsignacionesARemover.clear();
                modalRutinasIdsSeleccionadas.value = "";
                modalPaginaActual = 1;

                // --- ¡NUEVO! Resetear filtros ---
                modalFiltroRutina.value = "";
                modalFiltroObjetivo.value = "todos";
                modalFiltroDuracion.value = "todas";

                modal.style.display = 'flex';
                cargarDatosModal(id);
            }
        });
    }

    if (btnCerrarModal) {
        btnCerrarModal.addEventListener('click', () => modal.style.display = 'none');
    }
    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.style.display = 'none';
    });


    if (modalRutinasDisponibles) {
        modalRutinasDisponibles.addEventListener('change', (e) => {
            const check = e.target;
            if (!check.classList.contains('checkbox-rutina')) return;

            const rutinaId = check.value;

            if (check.checked) {
                if (!idsSeleccionados.includes(rutinaId)) {
                    idsSeleccionados.push(rutinaId);
                }
            } else {
                idsSeleccionados = idsSeleccionados.filter(id => id !== rutinaId);
            }
            modalRutinasIdsSeleccionadas.value = idsSeleccionados.join(',');

            if (idsSeleccionados.length > 0) {
                errorRutina.textContent = '';
            }
        });
    }


    if (modalRutinasActuales) {
        modalRutinasActuales.addEventListener('click', (e) => {
            const btn = e.target.closest('.btn-quitar-simple');
            if (!btn) return;

            const asignacionId = btn.dataset.asignacionId;
            const itemDiv = document.getElementById(`asignacion-${asignacionId}`);

            if (!itemDiv) return;

            if (idsAsignacionesARemover.has(asignacionId)) {
                idsAsignacionesARemover.delete(asignacionId);
                itemDiv.classList.remove('marcada-para-quitar');
            } else {
                idsAsignacionesARemover.add(asignacionId);
                itemDiv.classList.add('marcada-para-quitar');
            }
        });
    }

    if (formAsignar) {
        formAsignar.addEventListener('submit', async (e) => {
            e.preventDefault();
            let esValido = true;

            const hayNuevas = idsSeleccionados.length > 0;
            const hayParaQuitar = idsAsignacionesARemover.size > 0;

            if (!hayNuevas && !hayParaQuitar) {
                errorRutina.textContent = 'No se realizó ningún cambio.';
                return;
            }

            if (hayNuevas && !modalFecha.value) {
                errorFecha.textContent = 'Debes seleccionar una fecha para las nuevas rutinas.';
                esValido = false;
            } else {
                errorFecha.textContent = '';
            }

            if (hayNuevas) {
                errorRutina.textContent = '';
            }

            if (!esValido) return;

            let tituloSwal = "";
            if (hayNuevas && hayParaQuitar) {
                tituloSwal = "Guardando cambios...";
            } else if (hayNuevas) {
                tituloSwal = `Asignando ${idsSeleccionados.length} rutina(s)...`;
            } else if (hayParaQuitar) {
                tituloSwal = `Quitando ${idsAsignacionesARemover.size} rutina(s)...`;
            }

            Swal.fire({
                title: tituloSwal,
                text: 'Por favor esperá.',
                allowOutsideClick: false,

                background: '#19191d',
                color: '#e9e9ee',
                loaderColor: '#fff112',

                didOpen: () => {
                    Swal.showLoading();
                }
            });

            const formData = new FormData(formAsignar);
            formData.append('clienteId', modalClienteIdHidden.value);

            formData.set('rutinasIdsAAgregar', idsSeleccionados.join(','));
            formData.set('asignacionesIdsARemover', Array.from(idsAsignacionesARemover).join(','));
            formData.delete('rutinasIds');

            try {
                const response = await fetch(`${contextPath}/admin/guardar-asignacion-rutina`, {
                    method: 'POST',
                    body: new URLSearchParams(formData)
                });
                const resultado = await response.json();
                if (response.ok && resultado.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '¡Cambios Guardados!',
                        text: 'El cliente ha sido actualizado.',
                        background: '#19191d',
                        color: '#e9e9ee',
                        loaderColor: '#fff112',
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => {
                        location.reload();
                    });
                } else {
                    throw new Error(resultado.error || 'Error en el servidor');
                }
            } catch (error) {
                Swal.fire('Error al Guardar', error.message, 'error');
            }
        });
    }

    if (todasLasTarjetas.length > 0) {
        actualizarVista();
    }
});