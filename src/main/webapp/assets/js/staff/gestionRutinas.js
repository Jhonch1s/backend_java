document.addEventListener('DOMContentLoaded', () => {

    // --- Variables de Modales (Agrupadas) ---
    const modalCrear = document.getElementById('modal-crear-rutina');
    const modalDetalles = document.getElementById('modal-editar-detalles');
    const modalEliminar = document.getElementById('modal-eliminar-rutina');
    const modalAsignar = document.getElementById('modal-asignar-cliente');
    // --- ¡NUEVO! Modal de Notificación ---
    const modalNotificacion = document.getElementById('modal-notificacion-simple');
    const mensajeNotificacion = document.getElementById('mensaje-notificacion');
    const btnCerrarNotificacion = document.getElementById('btn-cerrar-notificacion');


    // --- Formulario CREAR ---
    const btnAbrirModalCrear = document.getElementById('btn-abrir-modal-crear');
    const formCrear = document.getElementById('form-crear-rutina');
    const feedbackCreacion = document.getElementById('feedback-creacion');
    const inputNombre = document.getElementById('rutina-nombre');
    const inputObjetivo = document.getElementById('rutina-objetivo');
    const inputDuracion = document.getElementById('rutina-duracion');
    const errorNombre = document.getElementById('error-nombre');
    const errorObjetivo = document.getElementById('error-objetivo');
    const errorDuracion = document.getElementById('error-duracion');

    // --- Formulario EDITAR DETALLES ---
    const formDetalles = document.getElementById('form-editar-detalles');
    const feedbackDetalles = document.getElementById('feedback-detalles');
    const inputEditNombre = document.getElementById('edit-nombre');
    const selectEditObjetivo = document.getElementById('edit-objetivo');
    const inputEditDuracion = document.getElementById('edit-duracion');
    const inputEditId = document.getElementById('edit-id');
    const nombreRutinaModal = document.getElementById('nombre-rutina-modal-detalles');

    // --- Modal ELIMINAR ---
    const btnCancelarEliminar = document.getElementById('btn-cancelar-eliminar');
    const btnConfirmarEliminar = document.getElementById('btn-confirmar-eliminar');
    const spanNombreEliminar = document.getElementById('nombre-rutina-eliminar');
    const feedbackEliminar = document.getElementById('feedback-eliminar');

    // --- Filtros y Paginación (Existente) ---
    const filtroNombre = document.getElementById('filtro-nombre');
    const filtroObjetivo = document.getElementById('filtro-objetivo');
    const filtroDuracion = document.getElementById('filtro-duracion');
    const btnBuscar = document.getElementById('btn-buscar-filtro');
    const listaContenedor = document.getElementById('lista-rutinas-admin');
    const todasLasTarjetas = listaContenedor ? Array.from(listaContenedor.querySelectorAll('.rutina-card')) : [];
    const contadorResultados = document.getElementById('contador-resultados');
    const mensajeVacioOriginal = listaContenedor ? listaContenedor.querySelector('.view__sub') : null;
    const paginacionControles = document.getElementById('paginacion-rutinas-controles');
    const btnAnterior = document.getElementById('btn-anterior-rutinas');
    const btnSiguiente = document.getElementById('btn-siguiente-rutinas');
    const numerosPagina = document.getElementById('numeros-pagina-rutinas');
    const ITEMS_POR_PAGINA = 7;
    let paginaActual = 1;
    let tarjetasFiltradas = [...todasLasTarjetas];

    // --- ¡NUEVO! Elementos del modal ASIGNAR ---
    const inputBuscarCliente = document.getElementById('input-buscar-cliente');
    const listaResultados = document.getElementById('lista-resultados-clientes');
    const formAsignarCliente = document.getElementById('form-asignar-cliente');
    const btnConfirmarAsignacion = document.getElementById('btn-confirmar-asignacion');
    const inputRutinaIdHidden = document.getElementById('id-rutina-para-asignar');
    const inputClienteIdHidden = document.getElementById('id-cliente-seleccionado');
    const inputFechaAsignacion = document.getElementById('fecha-asignacion-simple');
    let searchTimer;


    function formatearObjetivo(objetivoEnum) {
        switch(objetivoEnum) {
            case 'HIPERTROFIA': return 'Hipertrofia';
            case 'FUERZA': return 'Fuerza';
            case 'RESISTENCIA': return 'Resistencia';
            case 'TONIFICAR': return 'Tonificar';
            case 'PERDIDA_PESO': return 'Pérdida de Peso';
            default: return objetivoEnum;
        }
    }

    // --- Lógica MODAL CREAR ---
    if (btnAbrirModalCrear) {
        btnAbrirModalCrear.addEventListener('click', () => {
            formCrear.reset();
            feedbackCreacion.textContent = '';
            errorNombre.textContent = '';
            errorObjetivo.textContent = '';
            errorDuracion.textContent = '';
            inputNombre.classList.remove('is-invalid');
            inputObjetivo.classList.remove('is-invalid');
            inputDuracion.classList.remove('is-invalid');
            modalCrear.style.display = 'flex';
            inputNombre.focus();
        });
    }

    if (formCrear) {
        formCrear.addEventListener('submit', async (event) => {
            event.preventDefault();
            feedbackCreacion.textContent = 'Guardando...';
            feedbackCreacion.className = 'modal-feedback';

            let esValido = true;
            if (!inputNombre.value || inputNombre.value.trim() === '') {
                errorNombre.textContent = 'El nombre es obligatorio.';
                inputNombre.classList.add('is-invalid');
                esValido = false;
            }
            if (!inputObjetivo.value) {
                errorObjetivo.textContent = 'Selecciona un objetivo.';
                inputObjetivo.classList.add('is-invalid');
                esValido = false;
            }
            if (!esValido) {
                feedbackCreacion.textContent = '';
                return;
            }

            try {
                const formData = new FormData(formCrear);
                formData.append('action', 'crear');

                const response = await fetch(`${contextPath}/admin/rutina-crud`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams(Object.fromEntries(formData.entries()))
                });
                const resultado = await response.json();

                if (response.ok && resultado.success) {
                    feedbackCreacion.textContent = '¡Rutina creada! Redirigiendo...';
                    feedbackCreacion.className = 'modal-feedback feedback-exito';
                    window.location.href = `${contextPath}/admin/editar-rutina?id=${resultado.nuevaRutinaId}`;
                } else {
                    feedbackCreacion.textContent = resultado.error || 'Error al crear la rutina.';
                    feedbackCreacion.className = 'modal-feedback feedback-error';
                }
            } catch (error) {
                console.error('Error en fetch:', error);
                feedbackCreacion.textContent = 'Error de conexión al guardar.';
                feedbackCreacion.className = 'modal-feedback feedback-error';
            }
        });
    }

    // --- Lógica FILTROS y PAGINACIÓN (Existente, sin cambios) ---
    function actualizarVista() {
        if (!listaContenedor) return;

        filtrarTarjetas();

        const totalPaginas = Math.ceil(tarjetasFiltradas.length / ITEMS_POR_PAGINA);
        if (paginaActual > totalPaginas && totalPaginas > 0) {
            paginaActual = totalPaginas;
        } else if (totalPaginas === 0) {
            paginaActual = 1;
        }

        const inicio = (paginaActual - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;
        const tarjetasPagina = tarjetasFiltradas.slice(inicio, fin);

        const h2Titulo = listaContenedor.querySelector('h2');
        listaContenedor.innerHTML = '';
        if(h2Titulo) listaContenedor.appendChild(h2Titulo);

        if (todasLasTarjetas.length === 0) {
            if (mensajeVacioOriginal) {
                listaContenedor.appendChild(mensajeVacioOriginal);
            }
        } else if (tarjetasFiltradas.length === 0) {
            listaContenedor.innerHTML += '<p class="view__sub">No se encontraron rutinas con esos filtros.</p>';
        } else {
            tarjetasPagina.forEach(card => {
                card.style.display = 'grid';
                listaContenedor.appendChild(card);
            });
        }

        renderizarBotonesPaginacion(totalPaginas);
        if (btnAnterior) btnAnterior.disabled = (paginaActual === 1);
        if (btnSiguiente) btnSiguiente.disabled = (paginaActual === totalPaginas || totalPaginas === 0);
        if (paginacionControles) paginacionControles.style.display = totalPaginas > 1 ? 'flex' : 'none';

        if (contadorResultados) {
            if (tarjetasFiltradas.length !== todasLasTarjetas.length) {
                contadorResultados.textContent = `Mostrando ${tarjetasPagina.length} de ${tarjetasFiltradas.length} rutinas filtradas. (Total: ${todasLasTarjetas.length})`;
            } else if (todasLasTarjetas.length > 0) {
                contadorResultados.textContent = `Mostrando ${tarjetasPagina.length} de ${todasLasTarjetas.length} rutinas.`;
            } else {
                contadorResultados.textContent = 'No hay rutinas para mostrar.';
            }
        }
    }

    function filtrarTarjetas() {
        const nombreVal = filtroNombre ? filtroNombre.value.toLowerCase().trim() : '';
        const objetivoVal = filtroObjetivo ? filtroObjetivo.value : '';
        const duracionVal = filtroDuracion ? filtroDuracion.value : '';

        tarjetasFiltradas = todasLasTarjetas.filter(card => {
            const cardNombre = (card.dataset.nombre || '').toLowerCase();
            const cardObjetivo = card.dataset.objetivo || '';
            const cardDuracion = parseInt(card.dataset.duracion, 10);

            const matchNombre = cardNombre.includes(nombreVal);
            const matchObjetivo = (objetivoVal === "") || (cardObjetivo === objetivoVal);

            let matchDuracion = false;
            if (duracionVal === "") {
                matchDuracion = true;
            } else if (!isNaN(cardDuracion) && cardDuracion > 0) {
                const [min, max] = duracionVal.split('-').map(Number);
                matchDuracion = cardDuracion >= min && cardDuracion <= max;
            } else if (duracionVal !== "" && (isNaN(cardDuracion) || cardDuracion === 0)) {
                matchDuracion = false;
            }

            return matchNombre && matchObjetivo && matchDuracion;
        });
    }

    function renderizarBotonesPaginacion(totalPaginas) {
        if (!numerosPagina) return;
        numerosPagina.innerHTML = "";
        if (totalPaginas <= 1) return;

        const tamanoVentana = 5;
        const offset = Math.floor(tamanoVentana / 2);
        let startPage = paginaActual - offset;
        let endPage = paginaActual + offset;

        if (startPage < 1) { startPage = 1; endPage = Math.min(tamanoVentana, totalPaginas); }
        if (endPage > totalPaginas) { endPage = totalPaginas; startPage = Math.max(1, totalPaginas - tamanoVentana + 1); }

        for (let i = startPage; i <= endPage; i++) {
            numerosPagina.appendChild(crearBotonPagina(i));
        }
    }

    function crearBotonPagina(numero) {
        const btnPagina = document.createElement("button");
        btnPagina.className = "boton-pagina";
        btnPagina.textContent = numero;
        if (numero === paginaActual) {
            btnPagina.classList.add("activo");
        }
        btnPagina.addEventListener('click', () => {
            paginaActual = numero;
            actualizarVista();
        });
        return btnPagina;
    }

    // Listeners de filtros
    if (btnBuscar) {
        btnBuscar.addEventListener('click', () => {
            paginaActual = 1;
            actualizarVista();
        });
    }
    if(filtroNombre) filtroNombre.addEventListener('input', () => { paginaActual = 1; actualizarVista(); });
    if(filtroObjetivo) filtroObjetivo.addEventListener('change', () => { paginaActual = 1; actualizarVista(); });
    if(filtroDuracion) filtroDuracion.addEventListener('change', () => { paginaActual = 1; actualizarVista(); });
    if (btnSiguiente) {
        btnSiguiente.addEventListener('click', () => {
            if (paginaActual < Math.ceil(tarjetasFiltradas.length / ITEMS_POR_PAGINA)) {
                paginaActual++;
                actualizarVista();
            }
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

    function abrirModalEliminar(id, nombre) {
        btnConfirmarEliminar.dataset.id = id;
        spanNombreEliminar.textContent = nombre;
        feedbackEliminar.textContent = '';
        feedbackEliminar.className = 'modal-feedback';
        btnConfirmarEliminar.disabled = false;
        modalEliminar.style.display = 'flex';
    }

    // Listener para el botón de cancelar del modal de eliminar
    if (btnCancelarEliminar) {
        btnCancelarEliminar.addEventListener('click', () => {
            modalEliminar.style.display = 'none';
        });
    }

    // Listener para el botón de confirmar del modal de eliminar
    if (btnConfirmarEliminar) {
        btnConfirmarEliminar.addEventListener('click', async (event) => {
            const boton = event.currentTarget;
            const rutinaId = boton.dataset.id;

            feedbackEliminar.textContent = 'Eliminando...';
            feedbackEliminar.className = 'modal-feedback';
            btnConfirmarEliminar.disabled = true;

            try {
                const response = await fetch(`${contextPath}/admin/rutina-crud`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ action: 'eliminar', id: rutinaId })
                });
                const resultado = await response.json();

                if (response.ok && resultado.success) {
                    feedbackEliminar.textContent = '¡Eliminada con éxito!';
                    feedbackEliminar.className = 'modal-feedback feedback-exito';

                    const index = todasLasTarjetas.findIndex(card => card.dataset.id === rutinaId);
                    if (index > -1) {
                        todasLasTarjetas.splice(index, 1);
                    }
                    actualizarVista();
                    setTimeout(() => modalEliminar.style.display = 'none', 1000);
                } else {
                    feedbackEliminar.textContent = resultado.error || 'Error al eliminar.';
                    feedbackEliminar.className = 'modal-feedback feedback-error';
                    btnConfirmarEliminar.disabled = false;
                }
            } catch (error) {
                console.error('Error en fetch al eliminar:', error);
                feedbackEliminar.textContent = 'Error de conexión al eliminar.';
                feedbackEliminar.className = 'modal-feedback feedback-error';
                btnConfirmarEliminar.disabled = false;
            }
        });
    }

    // --- Lógica MODAL EDITAR DETALLES ---
    if (formDetalles) {
        formDetalles.addEventListener('submit', async (event) => {
            event.preventDefault();
            feedbackDetalles.textContent = 'Guardando cambios...';

            try {
                const formData = new FormData(formDetalles);
                formData.append('action', 'modificar');

                const response = await fetch(`${contextPath}/admin/rutina-crud`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams(Object.fromEntries(formData.entries()))
                });
                const resultado = await response.json();

                if (response.ok && resultado.success) {
                    feedbackDetalles.textContent = '¡Rutina modificada con éxito!';
                    feedbackDetalles.className = 'modal-feedback feedback-exito';

                    const idModificado = inputEditId.value;
                    const nombreNuevo = inputEditNombre.value;
                    const objetivoNuevo = selectEditObjetivo.value;
                    const duracionNueva = inputEditDuracion.value;

                    // Actualizar tarjeta en la lista principal
                    const tarjetaDOM = todasLasTarjetas.find(card => card.dataset.id === idModificado);
                    if (tarjetaDOM) {
                        tarjetaDOM.querySelector('h3').textContent = nombreNuevo;
                        const duracionTexto = (duracionNueva && duracionNueva > 0) ? ` | Duración: ${duracionNueva} sem.` : '';
                        const objetivoFormateado = formatearObjetivo(objetivoNuevo);
                        tarjetaDOM.querySelector('p').textContent = `Objetivo: ${objetivoFormateado}${duracionTexto}`;

                        tarjetaDOM.dataset.nombre = nombreNuevo;
                        tarjetaDOM.dataset.objetivo = objetivoNuevo;
                        tarjetaDOM.dataset.duracion = duracionNueva || 0;
                    }
                    setTimeout(() => modalDetalles.style.display = 'none', 1000);
                } else {
                    feedbackDetalles.textContent = resultado.error || 'Error al guardar los cambios.';
                    feedbackDetalles.className = 'modal-feedback feedback-error';
                }
            } catch (error) {
                console.error('Error de conexión al guardar detalles:', error);
                feedbackDetalles.textContent = 'Error de conexión con el servidor.';
                feedbackDetalles.className = 'modal-feedback feedback-error';
            }
        });
    }

    if (listaContenedor) {
        listaContenedor.addEventListener('click', async (e) => {

            // --- Clic en "Eliminar" ---
            const btnEliminar = e.target.closest('.btn-eliminar-rutina');
            if (btnEliminar) {
                e.stopPropagation();
                const tarjeta = btnEliminar.closest('.rutina-card');
                const rutinaId = btnEliminar.dataset.id;
                const rutinaNombre = tarjeta.dataset.nombre;
                if (rutinaId) {
                    // ¡CAMBIO! Llama al modal HTML
                    abrirModalEliminar(rutinaId, rutinaNombre);
                }
                return;
            }

            const btnDetalles = e.target.closest('.btn--detalle');
            if (btnDetalles) {
                e.preventDefault();
                const idRutina = btnDetalles.dataset.id;

                nombreRutinaModal.textContent = 'Cargando...';
                feedbackDetalles.textContent = '';
                formDetalles.reset();

                try {
                    const response = await fetch(`${contextPath}/admin/rutina-crud?id=${idRutina}`);
                    const data = await response.json();
                    if (!response.ok || !data.success) {
                        throw new Error(data.error || 'Fallo al obtener detalles');
                    }
                    const rutina = data.rutina;
                    nombreRutinaModal.textContent = rutina.nombre;
                    inputEditNombre.value = rutina.nombre;
                    selectEditObjetivo.value = rutina.objetivo;
                    inputEditDuracion.value = (rutina.duracionSemanas && rutina.duracionSemanas > 0) ? rutina.duracionSemanas : '';
                    inputEditId.value = rutina.id;
                    modalDetalles.style.display = 'flex';
                } catch (error) {
                    console.error('Error al cargar detalles:', error);
                    alert('No se pudieron cargar los detalles de la rutina.');
                }
                return;
            }

            const btnAsignar = e.target.closest('.btn-abrir-modal-asignar');
            if (btnAsignar) {
                const rutinaId = btnAsignar.dataset.id;
                const rutinaNombre = btnAsignar.dataset.nombre;

                inputRutinaIdHidden.value = rutinaId;
                document.getElementById('nombre-rutina-asignar').textContent = rutinaNombre;
                inputBuscarCliente.value = '';
                listaResultados.innerHTML = '<p id="feedback-busqueda" style="text-align: center; color: var(--gg-text-muted);">Escribe al menos 3 caracteres...</p>';
                btnConfirmarAsignacion.disabled = true;
                inputClienteIdHidden.value = '';
                inputFechaAsignacion.value = new Date().toISOString().split('T')[0];

                modalAsignar.style.display = 'flex';
                inputBuscarCliente.focus();
                return;
            }
        });
    }

    // --- Cierre de Modales (Optimizado) ---
    document.addEventListener('click', (e) => {
        if (e.target.closest('.modal-cerrar')) {
            const modalAbierto = e.target.closest('.modal');
            if (modalAbierto) {
                modalAbierto.style.display = 'none';
            }
        }
    });

    window.addEventListener('click', (event) => {
        if (event.target === modalCrear) modalCrear.style.display = 'none';
        if (event.target === modalDetalles) modalDetalles.style.display = 'none';
        if (event.target === modalEliminar) modalEliminar.style.display = 'none';
        if (event.target === modalAsignar) modalAsignar.style.display = 'none';
        if (event.target === modalNotificacion) modalNotificacion.style.display = 'none';
    });


    if (listaContenedor) {
        actualizarVista();
    }

    if (inputBuscarCliente) {
        inputBuscarCliente.addEventListener('input', () => {
            clearTimeout(searchTimer);
            const query = inputBuscarCliente.value.trim();

            if (query.length < 3) {
                listaResultados.innerHTML = '<p id="feedback-busqueda" style="text-align: center; color: var(--gg-text-muted);">Escribe al menos 3 caracteres...</p>';
                return;
            }

            listaResultados.innerHTML = '<p id="feedback-busqueda" style="text-align: center; color: var(--gg-text-muted);">Buscando...</p>';

            searchTimer = setTimeout(() => {
                buscarClientesAPI(query);
            }, 300);
        });
    }

    async function buscarClientesAPI(query) {
        try {
            const response = await fetch(`${contextPath}/api/clientes/search?ci=${query}&limit=5`);
            const data = await response.json();

            if (!response.ok || !data.success || !data.items || data.items.length === 0) {
                listaResultados.innerHTML = '<p id="feedback-busqueda" style="text-align: center; color: var(--gg-text-muted);">No se encontraron clientes.</p>';
                return;
            }

            listaResultados.innerHTML = '';
            data.items.forEach(cliente => {
                const itemHTML = `
                    <div class="cliente-resultado-item" data-cliente-id="${cliente.ci}">
                        <p class="m-0">${cliente.nombre} ${cliente.apellido}</p>
                        <span class="m-0">CI: ${cliente.ci}</span>
                    </div>
                `;
                listaResultados.insertAdjacentHTML('beforeend', itemHTML);
            });

        } catch (error) {
            console.error('Error buscando clientes:', error);
            listaResultados.innerHTML = '<p id="feedback-busqueda" style="text-align: center; color: var(--color-peligro);">Error al cargar clientes.</p>';
        }
    }

    if (listaResultados) {
        listaResultados.addEventListener('click', (e) => {
            const item = e.target.closest('.cliente-resultado-item');
            if (!item) return;

            document.querySelectorAll('.cliente-resultado-item').forEach(el => {
                el.classList.remove('seleccionado');
            });
            item.classList.add('seleccionado');

            inputClienteIdHidden.value = item.dataset.clienteId;
            btnConfirmarAsignacion.disabled = false;
        });
    }

    if (formAsignarCliente) {
        formAsignarCliente.addEventListener('submit', async (e) => {
            e.preventDefault();

            const rutinaId = inputRutinaIdHidden.value;
            const clienteId = inputClienteIdHidden.value;
            const fecha = inputFechaAsignacion.value;

            if (!clienteId || !rutinaId || !fecha) {
                // ¡CAMBIO! Llama al nuevo modal de notificación
                mostrarNotificacion('Faltan datos (cliente, rutina o fecha)', 'error');
                return;
            }

            btnConfirmarAsignacion.disabled = true;
            btnConfirmarAsignacion.textContent = 'Asignando...';

            try {
                const formData = new URLSearchParams();
                formData.append('action', 'agregar');
                formData.append('clienteId', clienteId);
                formData.append('rutinaId', rutinaId);
                formData.append('fechaAsignacion', fecha);

                // Apunta al servlet de AsignarCliente
                const response = await fetch(`${contextPath}/admin/asignar-cliente-rutina`, {
                    method: 'POST',
                    body: formData
                });

                const resultado = await response.json();
                if (!response.ok || !resultado.success) {
                    throw new Error(resultado.error || 'Error al asignar');
                }

                // --- ¡CAMBIO! ---
                modalAsignar.style.display = 'none';
                mostrarNotificacion('¡Rutina Asignada con éxito!');

            } catch (error) {
                // --- ¡CAMBIO! ---
                mostrarNotificacion(`Error: ${error.message}`, 'error');
            } finally {
                btnConfirmarAsignacion.disabled = false;
                btnConfirmarAsignacion.textContent = 'Asignar Rutina';
            }
        });
    }

    function mostrarNotificacion(mensaje, tipo = 'success') {
        mensajeNotificacion.textContent = mensaje;
        if (tipo === 'error') {
            mensajeNotificacion.style.color = 'var(--color-peligro)';
        } else {
            mensajeNotificacion.style.color = 'var(--gg-text)';
        }
        modalNotificacion.style.display = 'flex';
    }

    if (btnCerrarNotificacion) {
        btnCerrarNotificacion.addEventListener('click', () => {
            modalNotificacion.style.display = 'none';
        });
    }

});