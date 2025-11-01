document.addEventListener('DOMContentLoaded', () => {

    // --- REFERENCIAS DEL DOM ---
    const bibliotecaLista = document.getElementById('biblioteca-lista');
    const diasContainer = document.getElementById('dias-container');
    const tabsContainer = document.querySelector('.builder__dias-tabs');
    const btnGuardar = document.getElementById('btn-guardar-rutina');
    const rutinaId = document.getElementById('rutina-id').value;

    // Filtros
    const filtroNombre = document.getElementById('filtro-nombre-ej');
    const filtroGrupo = document.getElementById('filtro-grupo-ej');
    const filtroDificultad = document.getElementById('filtro-dificultad-ej');

    // ¡NUEVO! Referencias de paginación
    const btnBiblioAnt = document.getElementById('biblio-anterior');
    const btnBiblioSig = document.getElementById('biblio-siguiente');
    const biblioContador = document.getElementById('biblio-contador');


    let diaActivo = 'LUNES'; // Estado inicial

    // ¡NUEVO! Estado de paginación
    let biblioPaginaActual = 1;
    const BIBLIO_POR_PAGINA = 10;
    let ejerciciosFiltrados = [...jsonDataEjercicios]; // Array que vive globalmente

    // --- FUNCIÓN 1: INICIALIZACIÓN ---
    function inicializar() {
        // 1. Llenar la biblioteca (¡AHORA RENDERIZA LA PÁG. 1!)
        filtrarBiblioteca(); // Llama a filtrar (que ahora también renderiza)

        // 2. Llenar los días con ejercicios ya guardados
        jsonDataAsignados.forEach(asig => {
            const panelDia = document.getElementById(`dia-${asig.diaSemana}`);
            if (panelDia) {
                const el = crearTarjetaAsignada(asig.ejercicio, asig.series, asig.repeticiones);
                panelDia.appendChild(el);
            }
        });

        // 3. Activar SortableJS
        inicializarDragAndDrop();

        // 4. Activar listeners
        tabsContainer.addEventListener('click', cambiarTabDia);
        btnGuardar.addEventListener('click', guardarRutina);

        // ¡NUEVO! Listeners de paginación
        btnBiblioAnt.addEventListener('click', () => {
            if (biblioPaginaActual > 1) {
                biblioPaginaActual--;
                renderizarBiblioteca();
            }
        });
        btnBiblioSig.addEventListener('click', () => {
            const totalPaginas = Math.ceil(ejerciciosFiltrados.length / BIBLIO_POR_PAGINA);
            if (biblioPaginaActual < totalPaginas) {
                biblioPaginaActual++;
                renderizarBiblioteca();
            }
        });

        // ¡NUEVO! Listeners de filtros (ahora resetean la pág. a 1)
        filtroNombre.addEventListener('input', () => {
            biblioPaginaActual = 1;
            filtrarBiblioteca();
        });
        filtroGrupo.addEventListener('change', () => {
            biblioPaginaActual = 1;
            filtrarBiblioteca();
        });

        filtroDificultad.addEventListener('change', () => {
            biblioPaginaActual = 1;
            filtrarBiblioteca();
        });

        // Listener para botones "Quitar" y "+/-" (delegación de eventos)
        diasContainer.addEventListener('click', (e) => {
            const target = e.target;

            if (target.classList.contains('btn-quitar-ej')) {
                target.closest('.ejercicio-card.asignado').remove();
                actualizarChecksBiblioteca();
                return;
            }

            if (target.classList.contains('btn-spin')) {
                e.preventDefault();
                const action = target.dataset.action;
                const wrapper = target.closest('.input-number-wrapper');
                const input = wrapper.querySelector('.input-num');

                if (!input) return;
                let currentValue = parseInt(input.value) || 0;

                if (action === 'increment') currentValue++;
                else if (action === 'decrement') currentValue--;

                if (currentValue < 1) currentValue = 1;
                input.value = currentValue;
            }
        });

        actualizarChecksBiblioteca();
    }

    // --- FUNCIÓN 2: ACTIVAR SORTABLEJS ---
    function inicializarDragAndDrop() {
        // (Esta función no cambia, se queda igual)
        new Sortable(bibliotecaLista, {
            group: { name: 'rutina', pull: 'clone', put: false },
            sort: false,
            animation: 150
        });

        document.querySelectorAll('.dia-panel').forEach(panel => {
            new Sortable(panel, {
                group: 'rutina',
                sort: true,
                animation: 150,
                onAdd: (evt) => {
                    const item = evt.item;
                    const targetPanel = evt.to;
                    const ejercicioId = item.dataset.id;

                    let count = 0;
                    for (const child of targetPanel.children) {
                        if (child.dataset.id === ejercicioId) count++;
                    }

                    if (count > 1) {
                        item.remove();
                        alert("Ese ejercicio ya está asignado a este día.");
                        return;
                    }

                    convertirTarjeta(item);
                    actualizarChecksBiblioteca();
                }
            });
        });
    }

    // --- FUNCIÓN 3: HELPERS HTML ---
    // (Estas funciones no cambian, se quedan igual)
    function crearTarjetaBiblioteca(ej) {
        const div = document.createElement('div');
        div.className = 'ejercicio-card';
        div.dataset.id = ej.id;
        div.dataset.nombre = ej.nombre.toLowerCase();
        div.dataset.grupo = (ej.grupoMuscularNombre || '').toLowerCase();

        // Guardamos la dificultad (por si el filtro la usa, aunque ya la filtra desde jsonData)
        div.dataset.dificultad = (ej.dificultad || 'PRINCIPIANTE');

        // Formateamos el texto para que se vea "Principiante" en lugar de "PRINCIPIANTE"
        let dificultadTexto = 'Principiante'; // Default
        if (ej.dificultad) {
            dificultadTexto = ej.dificultad.charAt(0).toUpperCase() + ej.dificultad.slice(1).toLowerCase();
        }

        div.innerHTML = `
            <strong>${ej.nombre}</strong>
            <div class="card-labels">
                <span class="grupo-label">${ej.grupoMuscularNombre}</span>
                
                <span class="dificultad-label ${ej.dificultad}">${dificultadTexto}</span>
            </div>
        `;
        return div;
    }
    function crearTarjetaAsignada(ejercicio, series = 3, repeticiones = 10) {
        const div = document.createElement('div');
        div.className = 'ejercicio-card asignado';
        div.dataset.id = ejercicio.id;

        // Formatear texto de dificultad (copiado de crearTarjetaBiblioteca)
        let dificultadTexto = 'Principiante'; // Default
        if (ejercicio.dificultad) {
            dificultadTexto = ejercicio.dificultad.charAt(0).toUpperCase() + ejercicio.dificultad.slice(1).toLowerCase();
        }

        // --- ¡NUEVO HTML! ---
        div.innerHTML = `
            <div class="card-top-row">
                <span class="drag-handle">::</span>
                <strong class="ej-nombre">${ejercicio.nombre}</strong>
                <button class="btn-quitar-ej">×</button>
            </div>
            
            <div class="card-bottom-row">
                <div class="card-labels">
                    <span class="grupo-label">${ejercicio.grupoMuscularNombre}</span>
                    <span class="dificultad-label ${ejercicio.dificultad}">${dificultadTexto}</span>
                </div>
                
                <div class="card-inputs-wrapper">
                    <div class="ej-inputs">
                        <label>Series</label>
                        <div class="input-number-wrapper">
                            <button type="button" class="btn-spin" data-action="decrement" aria-label="Disminuir series">-</button>
                            <input type="number" class="input-num input-series" value="${series}" min="1">
                            <button type="button" class="btn-spin" data-action="increment" aria-label="Aumentar series">+</button>
                        </div>
                    </div>
                    <div class="ej-inputs">
                        <label>Reps</label>
                        <div class="input-number-wrapper">
                            <button type="button" class="btn-spin" data-action="decrement" aria-label="Disminuir reps">-</button>
                            <input type="number" class="input-num input-reps" value="${repeticiones}" min="1">
                            <button type="button" class="btn-spin" data-action="increment" aria-label="Aumentar reps">+</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        return div;
    }
    function convertirTarjeta(item) {
        const id = item.dataset.id;
        const ejercicioObj = jsonDataEjercicios.find(ej => ej.id == id);
        if (!ejercicioObj) return;
        const tarjetaCompleta = crearTarjetaAsignada(ejercicioObj);
        item.innerHTML = tarjetaCompleta.innerHTML;
        item.className = tarjetaCompleta.className;
    }

    // --- FUNCIÓN 4: MANEJO DE UI (TABS, FILTROS) ---
    function cambiarTabDia(e) {
        // (Esta función no cambia, se queda igual)
        const tab = e.target.closest('.tab-dia');
        if (!tab) return;
        diaActivo = tab.dataset.dia;
        tabsContainer.querySelectorAll('.tab-dia').forEach(t => t.classList.remove('activo'));
        diasContainer.querySelectorAll('.dia-panel').forEach(p => p.classList.remove('activo'));
        tab.classList.add('activo');
        document.getElementById(`dia-${diaActivo}`).classList.add('activo');
        actualizarChecksBiblioteca();
    }

    // ¡NUEVO! Esta función ahora FILTRA y guarda el resultado
    function filtrarBiblioteca() {
        const nombreF = filtroNombre.value.toLowerCase();
        const grupoF = filtroGrupo.value.toLowerCase();
        const dificultadF = filtroDificultad.value;

        ejerciciosFiltrados = jsonDataEjercicios.filter(ej => {
            const matchNombre = (ej.nombre || '').toLowerCase().includes(nombreF);
            const matchGrupo = (grupoF === "") || (ej.grupoMuscularNombre || '').toLowerCase().includes(grupoF);
            const matchDificultad = (dificultadF === "") || (ej.dificultad === dificultadF);
            return matchNombre && matchGrupo && matchDificultad;
        });

        // Llama a renderizar la página actual
        renderizarBiblioteca();
    }

    // ¡NUEVO! Esta función ahora RENDERIZA LA PÁGINA ACTUAL
    function renderizarBiblioteca() {
        bibliotecaLista.innerHTML = ''; // Limpiar lista

        // Paginar los ejercicios ya filtrados
        const totalPaginas = Math.ceil(ejerciciosFiltrados.length / BIBLIO_POR_PAGINA);
        if (biblioPaginaActual > totalPaginas) biblioPaginaActual = 1;

        const inicio = (biblioPaginaActual - 1) * BIBLIO_POR_PAGINA;
        const fin = inicio + BIBLIO_POR_PAGINA;
        const ejerciciosPaginados = ejerciciosFiltrados.slice(inicio, fin);

        // Renderizar solo la página
        ejerciciosPaginados.forEach(ej => {
            const el = crearTarjetaBiblioteca(ej);
            bibliotecaLista.appendChild(el);
        });

        // Actualizar checks y botones
        actualizarChecksBiblioteca();
        renderizarPaginacionBiblioteca(totalPaginas);
    }

    // ¡NUEVO! Función para actualizar los botones de paginación
    function renderizarPaginacionBiblioteca(totalPaginas) {
        if (totalPaginas <= 0) totalPaginas = 1; // Asegura que muestre "Pág 1 / 1"

        biblioContador.textContent = `Pág ${biblioPaginaActual} / ${totalPaginas}`;
        btnBiblioAnt.disabled = (biblioPaginaActual === 1);
        btnBiblioSig.disabled = (biblioPaginaActual === totalPaginas);
    }

    // (Esta función no cambia, se queda igual)
    function actualizarChecksBiblioteca() {
        const panelActivo = document.getElementById(`dia-${diaActivo}`);
        if (!panelActivo) return;
        const idsEnElDia = new Set();
        panelActivo.querySelectorAll('.ejercicio-card.asignado').forEach(card => {
            idsEnElDia.add(card.dataset.id);
        });
        bibliotecaLista.querySelectorAll('.ejercicio-card').forEach(card => {
            if (idsEnElDia.has(card.dataset.id)) {
                card.classList.add('asignado-a-dia');
            } else {
                card.classList.remove('asignado-a-dia');
            }
        });
    }

    // --- FUNCIÓN 5: GUARDAR RUTINA ---
    async function guardarRutina() {
        btnGuardar.textContent = 'Guardando...';
        btnGuardar.disabled = true;

        // 1. MUESTRA EL SPINNER (con tema oscuro)
        Swal.fire({
            theme: 'dark', // <-- AÑADIDO
            title: 'Guardando Rutina...',
            text: 'Por favor esperá mientras se procesan los cambios.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        // (Esta parte de construir el JSON queda igual)
        const payload = {
            rutinaId: rutinaId,
            dias: {}
        };
        diasContainer.querySelectorAll('.dia-panel').forEach(panel => {
            const dia = panel.id.replace('dia-', '');
            payload.dias[dia] = [];
            panel.querySelectorAll('.ejercicio-card.asignado').forEach(card => {
                payload.dias[dia].push({
                    ejercicioId: card.dataset.id,
                    series: card.querySelector('.input-series').value,
                    repeticiones: card.querySelector('.input-reps').value
                });
            });
        });

        // 2. ENVÍA EL JSON AL SERVLET
        try {
            const response = await fetch(`${contextPath}/admin/guardar-rutina-completa`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const resultado = await response.json();

            if (response.ok && resultado.success) {
                // 3. MUESTRA ALERTA DE ÉXITO (con tema oscuro)
                Swal.fire({
                    theme: 'dark', // <-- AÑADIDO
                    icon: 'success',
                    title: '¡Guardada!',
                    text: 'La rutina se actualizó correctamente.',
                    timer: 1500,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = `${contextPath}/admin/gestion-rutinas`;
                });

            } else {
                throw new Error(resultado.error || 'Error desconocido al guardar');
            }
        } catch (error) {
            // 4. MUESTRA ALERTA DE ERROR (con tema oscuro)
            Swal.fire({
                theme: 'dark', // <-- AÑADIDO
                icon: 'error',
                title: 'Oops... Hubo un error',
                text: error.message
            });
            // Reactiva el botón solo si falló
            btnGuardar.textContent = 'Guardar Rutina Completa';
            btnGuardar.disabled = false;
        }
    }

    // --- INICIAR TODO ---
    inicializar();
});