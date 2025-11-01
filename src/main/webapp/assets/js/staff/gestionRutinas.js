document.addEventListener('DOMContentLoaded', () => {

    // --- Referencias a Elementos del Modal (Crear) ---
    const modal = document.getElementById('modal-crear-rutina');
    const btnAbrirModal = document.getElementById('btn-abrir-modal-crear');
    const btnCerrarModal = document.getElementById('btn-cerrar-modal');
    const formCrear = document.getElementById('form-crear-rutina');
    const feedbackCreacion = document.getElementById('feedback-creacion');
    // Referencias a inputs para limpiar errores
    const inputNombre = document.getElementById('rutina-nombre');
    const inputObjetivo = document.getElementById('rutina-objetivo');
    const inputDuracion = document.getElementById('rutina-duracion');
    const errorNombre = document.getElementById('error-nombre');
    const errorObjetivo = document.getElementById('error-objetivo');
    const errorDuracion = document.getElementById('error-duracion');

    // --- Referencias a Elementos del Modal (Editar/Detalles) ---
    const modalDetalles = document.getElementById('modal-editar-detalles');
    const btnCerrarDetalles = document.getElementById('btn-cerrar-detalles');
    const formDetalles = document.getElementById('form-editar-detalles');
    const feedbackDetalles = document.getElementById('feedback-detalles');

    const inputEditNombre = document.getElementById('edit-nombre');
    const selectEditObjetivo = document.getElementById('edit-objetivo');
    const inputEditDuracion = document.getElementById('edit-duracion');
    const inputEditId = document.getElementById('edit-id');
    const nombreRutinaModal = document.getElementById('nombre-rutina-modal-detalles');

    const modalEliminar = document.getElementById('modal-eliminar-rutina');
    const btnCerrarEliminar = document.getElementById('btn-cerrar-eliminar');
    const btnCancelarEliminar = document.getElementById('btn-cancelar-eliminar');
    const btnConfirmarEliminar = document.getElementById('btn-confirmar-eliminar');
    const spanNombreEliminar = document.getElementById('nombre-rutina-eliminar');
    const feedbackEliminar = document.getElementById('feedback-eliminar');

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

    // --- Lógica Modal Crear ---
    function abrirModal() {
        formCrear.reset();
        feedbackCreacion.textContent = '';
        errorNombre.textContent = '';
        errorObjetivo.textContent = '';
        errorDuracion.textContent = '';
        inputNombre.classList.remove('is-invalid');
        inputObjetivo.classList.remove('is-invalid');
        inputDuracion.classList.remove('is-invalid');
        modal.style.display = 'flex';
        inputNombre.focus();
    }
    function cerrarModal() {
        modal.style.display = 'none';
    }

    if (btnAbrirModal) {
        btnAbrirModal.addEventListener('click', abrirModal);
    }
    if (btnCerrarModal) {
        btnCerrarModal.addEventListener('click', cerrarModal);
    }
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            cerrarModal();
        }
    });

    if (formCrear) {
        formCrear.addEventListener('submit', async (event) => {
            event.preventDefault();
            feedbackCreacion.textContent = 'Guardando...';
            feedbackCreacion.className = 'modal-feedback';

            errorNombre.textContent = '';
            errorObjetivo.textContent = '';
            errorDuracion.textContent = '';
            inputNombre.classList.remove('is-invalid');
            inputObjetivo.classList.remove('is-invalid');

            const formData = new FormData(formCrear);
            const data = Object.fromEntries(formData.entries());

            let esValido = true;
            if (!data.nombre || data.nombre.trim() === '') {
                errorNombre.textContent = 'El nombre es obligatorio.';
                inputNombre.classList.add('is-invalid');
                esValido = false;
            }
            if (!data.objetivo) {
                errorObjetivo.textContent = 'Selecciona un objetivo.';
                inputObjetivo.classList.add('is-invalid');
                esValido = false;
            }
            if (!esValido) {
                feedbackCreacion.textContent = '';
                return;
            }

            try {
                const response = await fetch(`${contextPath}/admin/crear-rutina`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams(data)
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

    // --- Lógica Filtros y Paginación ---
    const ITEMS_POR_PAGINA = 7;

    const filtroNombre = document.getElementById('filtro-nombre');
    const filtroObjetivo = document.getElementById('filtro-objetivo');
    const filtroDuracion = document.getElementById('filtro-duracion');
    const btnBuscar = document.getElementById('btn-buscar-filtro');

    const listaContenedor = document.getElementById('lista-rutinas-admin');
    // Corregido: Asegurarse de que listaContenedor exista antes de querySelectorAll
    const todasLasTarjetas = listaContenedor ? Array.from(listaContenedor.querySelectorAll('.rutina-card')) : [];
    const contadorResultados = document.getElementById('contador-resultados');
    const mensajeVacioOriginal = listaContenedor ? listaContenedor.querySelector('.view__sub') : null; // Guardamos el mensaje de "no hay rutinas"

    const paginacionControles = document.getElementById('paginacion-rutinas-controles');
    const btnAnterior = document.getElementById('btn-anterior-rutinas');
    const btnSiguiente = document.getElementById('btn-siguiente-rutinas');
    const numerosPagina = document.getElementById('numeros-pagina-rutinas');

    let paginaActual = 1;
    let tarjetasFiltradas = [...todasLasTarjetas]; // Al inicio, son todas

    function actualizarVista() {
        if (!listaContenedor) return; // No hacer nada si no hay contenedor

        // 1. Filtrar
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

    function abrirModalEliminar(id, nombre, tarjeta) {
        btnConfirmarEliminar.dataset.id = id;

        btnConfirmarEliminar.dataset.tarjetaId = tarjeta.dataset.id;

        spanNombreEliminar.textContent = nombre;
        feedbackEliminar.textContent = '';
        feedbackEliminar.className = 'modal-feedback';
        btnConfirmarEliminar.disabled = false;

        modalEliminar.style.display = 'flex';
    }

    function cerrarModalEliminar() {
        modalEliminar.style.display = 'none';
    }

    // Asignar eventos de cierre
    if (btnCerrarEliminar) btnCerrarEliminar.addEventListener('click', cerrarModalEliminar);
    if (btnCancelarEliminar) btnCancelarEliminar.addEventListener('click', cerrarModalEliminar);
    window.addEventListener('click', (event) => {
        if (event.target === modalEliminar) {
            cerrarModalEliminar();
        }
    });

    document.querySelectorAll('.btn-eliminar-rutina').forEach(button => {
        button.addEventListener('click', (event) => {
            event.stopPropagation();

            const boton = event.currentTarget;
            const tarjeta = boton.closest('.rutina-card');
            const rutinaId = boton.dataset.id;
            const rutinaNombre = tarjeta.dataset.nombre;

            if (!rutinaId) {
                alert('Error: No se pudo encontrar el ID de la rutina.');
                return;
            }

            abrirModalEliminar(rutinaId, rutinaNombre, tarjeta);
        });
    });

    if (btnConfirmarEliminar) {
        btnConfirmarEliminar.addEventListener('click', async (event) => {

            const boton = event.currentTarget;
            const rutinaId = boton.dataset.id;

            feedbackEliminar.textContent = 'Eliminando...';
            feedbackEliminar.className = 'modal-feedback';
            btnConfirmarEliminar.disabled = true;

            try {
                const response = await fetch(`${contextPath}/admin/eliminar-rutina`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ id: rutinaId })
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

                    setTimeout(cerrarModalEliminar, 1000);

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

    document.querySelectorAll('.btn--detalle').forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault();
            // *** CORREGIDO: dataset.id
            const idRutina = event.currentTarget.dataset.id;

            nombreRutinaModal.textContent = 'Cargando...';
            feedbackDetalles.textContent = '';
            feedbackDetalles.className = 'modal-feedback';
            formDetalles.reset();

            try {
                const response = await fetch(`${contextPath}/admin/rutina/detalles?id=${idRutina}`);
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
                nombreRutinaModal.textContent = 'Error';
            }
        });
    });

    if (btnCerrarDetalles) {
        btnCerrarDetalles.addEventListener('click', () => {
            modalDetalles.style.display = 'none';
        });
    }

    window.addEventListener('click', (event) => {
        if (event.target === modalDetalles) {
            modalDetalles.style.display = 'none';
        }
    });

    if (formDetalles) {
        formDetalles.addEventListener('submit', async (event) => {
            event.preventDefault();
            feedbackDetalles.textContent = 'Guardando cambios...';
            feedbackDetalles.className = 'modal-feedback'; // Clase por defecto para feedback

            const formData = new FormData(formDetalles);

            try {
                const response = await fetch(`${contextPath}/admin/rutina/modificar-detalles`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams(formData)
                });

                const resultado = await response.json();

                if (response.ok && resultado.success) {
                    feedbackDetalles.textContent = '¡Rutina modificada con éxito!';
                    feedbackDetalles.className = 'modal-feedback feedback-exito';

                    const idModificado = inputEditId.value;
                    const nombreNuevo = inputEditNombre.value;
                    const objetivoNuevo = selectEditObjetivo.value;
                    const duracionNueva = inputEditDuracion.value;

                    const botonDetalleDOM = document.querySelector(`.btn--detalle[data-id="${idModificado}"]`);
                    const tarjetaDOM = botonDetalleDOM ? botonDetalleDOM.closest('.rutina-card') : null;

                    if (tarjetaDOM) {
                        tarjetaDOM.querySelector('h3').textContent = nombreNuevo;
                        const duracionTexto = (duracionNueva && duracionNueva > 0) ? ` | Duración: ${duracionNueva} sem.` : '';
                        const objetivoFormateado = formatearObjetivo(objetivoNuevo);
                        tarjetaDOM.querySelector('p').textContent = `Objetivo: ${objetivoFormateado}${duracionTexto}`;
                        tarjetaDOM.querySelector('p').textContent = `Objetivo: ${objetivoNuevo.toLowerCase()}${duracionTexto}`;

                        tarjetaDOM.dataset.nombre = nombreNuevo;
                        tarjetaDOM.dataset.objetivo = objetivoNuevo;
                        tarjetaDOM.dataset.duracion = duracionNueva || 0;
                    }

                    setTimeout(() => {
                        modalDetalles.style.display = 'none';
                    }, 1000);

                } else {
                    feedbackDetalles.textContent = resultado.error || 'Error al guardar los cambios.';
                    feedbackDetalles.className = 'modal-feedback feedback-error'; // Clase para error
                }

            } catch (error) {
                console.error('Error de conexión al guardar detalles:', error);
                feedbackDetalles.textContent = 'Error de conexión con el servidor.';
                feedbackDetalles.className = 'modal-feedback feedback-error';
            }
        });
    }

    if(listaContenedor) {
        actualizarVista();
    }
});