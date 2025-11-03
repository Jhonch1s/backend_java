document.addEventListener("DOMContentLoaded", () => {
    const TAMANO_PAGINA_FE = 5; // 5 items por página para PRs y RMs (Frontend)

    const pantallaLista = document.getElementById("pantalla-lista");
    const pantallaProgreso = document.getElementById("pantalla-progreso");
    const nombreEjercicio = document.getElementById("nombre-ejercicio");
    const btnVolverPrincipal = document.querySelector(".btn-volver");

    const paginacionControles = document.getElementById("paginacion-controles");
    const btnAnterior = document.getElementById("btn-anterior");
    const btnSiguiente = document.getElementById("btn-siguiente");
    const numerosPagina = document.getElementById("numeros-pagina");
    const registrosList = document.getElementById("registros-recientes");

    const paginacionControlesPRs = document.getElementById("paginacion-controles-prs");
    const btnAnteriorPRs = document.getElementById("btn-anterior-prs");
    const btnSiguientePRs = document.getElementById("btn-siguiente-prs");
    const numerosPaginaPRs = document.getElementById("numeros-pagina-prs");
    const prsList = document.getElementById("mejores-prs");

    const paginacionControlesRMs = document.getElementById("paginacion-controles-rms");
    const btnAnteriorRMs = document.getElementById("btn-anterior-rms");
    const btnSiguienteRMs = document.getElementById("btn-siguiente-rms");
    const numerosPaginaRMs = document.getElementById("numeros-pagina-rms");
    const rmsList = document.getElementById("mejores-rms");

    let ejercicioIdActual = null;
    let ejercicioNombreActual = null;
    let paginaActual = 1;
    let vinoDeDashboard = false;

    let prsCompletos = [];
    let rmsCompletos = [];
    let paginaActualPRs = 1;
    let paginaActualRMs = 1;


    const urlParams = new URLSearchParams(window.location.search);
    const ejIdFromUrl = urlParams.get('ejId');
    const ejNombreFromUrl = urlParams.get('ejNombre');

    if (ejIdFromUrl && ejNombreFromUrl) {
        vinoDeDashboard = true;
        const nombreDecodificado = decodeURIComponent(ejNombreFromUrl);
        pantallaProgreso.classList.add("activa");
        nombreEjercicio.textContent = nombreDecodificado;
        btnVolverPrincipal.innerHTML = "← Volver al Dashboard";
        btnVolverPrincipal.style.display = 'inline-flex';
        verProgresoPorId(parseInt(ejIdFromUrl, 10), nombreDecodificado, 1);
    } else {
        pantallaLista.classList.add("activa");
    }


    async function verProgresoPorId(id, nombre, pagina = 1) {
        if (!id) return;

        ejercicioIdActual = id;
        ejercicioNombreActual = nombre;
        paginaActual = pagina;

        let url = contextPath + "/detalle-progreso?id=" + encodeURIComponent(id) + "&pagina=" + pagina;

        try {
            const resp = await fetch(url);
            if (!resp.ok) throw new Error(`Error ${resp.status}: ${await resp.text()}`);
            const data = await resp.json();
            console.log("Datos recibidos:", data);

            if (!Array.isArray(data.registros)) throw new Error("Respuesta inválida");

            nombreEjercicio.textContent = nombre;
            if (!vinoDeDashboard) {
                btnVolverPrincipal.style.display = 'inline-flex';
                btnVolverPrincipal.innerHTML = "← Volver a la lista";
            }

            registrosList.innerHTML = "";
            if (Array.isArray(data.registros) && data.registros.length > 0) {
                data.registros.forEach((r, index) => {
                    registrosList.appendChild(crearTarjetaRegistro(r, index));
                });
            } else {
                registrosList.innerHTML = "<div class='tarjeta-registro'>No hay registros recientes</div>";
            }

            renderizarBotonesPaginacionConNumeros(
                data.totalPaginas,
                data.paginaActual,
                numerosPagina,
                (nuevaPagina) => {
                    verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, nuevaPagina);
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
            );
            btnAnterior.disabled = (data.paginaActual <= 1);
            btnSiguiente.disabled = (data.paginaActual >= data.totalPaginas);
            paginacionControles.style.display = data.totalPaginas > 1 ? 'flex' : 'none';


            prsCompletos = Array.isArray(data.prs) ? data.prs : [];
            rmsCompletos = Array.isArray(data.rms) ? data.rms : [];

            paginaActualPRs = 1; // Resetea la página al cargar
            paginaActualRMs = 1; // Resetea la página al cargar

            renderizarPRs();
            renderizarRMs();

            if (!pantallaProgreso.classList.contains('activa')) {
                pantallaLista.classList.remove("activa");
                pantallaProgreso.classList.add("activa");
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }
        } catch (err) {
            console.error("Error cargando progreso:", err);
            alert("No se pudo cargar el progreso. Verificá la consola.");
        }
    }


    function renderizarPRs() {
        const totalPaginas = Math.ceil(prsCompletos.length / TAMANO_PAGINA_FE);
        const inicio = (paginaActualPRs - 1) * TAMANO_PAGINA_FE;
        const fin = inicio + TAMANO_PAGINA_FE;
        const prsPagina = prsCompletos.slice(inicio, fin);

        prsList.innerHTML = "";
        if (prsPagina.length > 0) {
            prsPagina.forEach((pr, index) => {
                prsList.appendChild(crearTarjetaPR(pr, index));
            });
        } else {
            prsList.innerHTML = "<div class='tarjeta-registro'>No hay PRs registrados</div>";
        }

        renderizarBotonesPaginacionConNumeros(
            totalPaginas,
            paginaActualPRs,
            numerosPaginaPRs, // El div de números de PRs
            (nuevaPagina) => {
                paginaActualPRs = nuevaPagina;
                renderizarPRs();
            }
        );

        // Lógica de botones
        btnAnteriorPRs.disabled = (paginaActualPRs <= 1);
        btnSiguientePRs.disabled = (paginaActualPRs >= totalPaginas);
        paginacionControlesPRs.style.display = totalPaginas > 1 ? 'flex' : 'none';
    }

    function renderizarRMs() {
        const totalPaginas = Math.ceil(rmsCompletos.length / TAMANO_PAGINA_FE);
        const inicio = (paginaActualRMs - 1) * TAMANO_PAGINA_FE;
        const fin = inicio + TAMANO_PAGINA_FE;
        const rmsPagina = rmsCompletos.slice(inicio, fin);

        rmsList.innerHTML = "";
        if (rmsPagina.length > 0) {
            rmsPagina.forEach((rm, index) => {
                rmsList.appendChild(crearTarjetaPR(rm, index));// Reutilizamos el creador de tarjeta
            });
        } else {
            rmsList.innerHTML = "<div class='tarjeta-registro'>No hay RMs registrados</div>";
        }

        // --- PINTAR NÚMEROS ---
        renderizarBotonesPaginacionConNumeros(
            totalPaginas,
            paginaActualRMs,
            numerosPaginaRMs, // El div de números de RMs
            (nuevaPagina) => {
                paginaActualRMs = nuevaPagina;
                renderizarRMs();
            }
        );

        // Lógica de botones
        btnAnteriorRMs.disabled = (paginaActualRMs <= 1);
        btnSiguienteRMs.disabled = (paginaActualRMs >= totalPaginas);
        paginacionControlesRMs.style.display = totalPaginas > 1 ? 'flex' : 'none';
    }


    function crearTarjetaRegistro(r, index) {
        const tarjeta = document.createElement("div");
        tarjeta.classList.add("tarjeta-registro");
        tarjeta.style.animationDelay = `${index * 0.07}s`;

        if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
            tarjeta.classList.add(r.diferenciaPeso > 0 ? "mejora" : "retroceso");
        } else {
            tarjeta.classList.add("sin-cambio");
        }

        tarjeta.appendChild(crearElemento("strong", "registro-fecha titillium-negra", (r.fecha || "sin fecha").trim()));

        let textoDetalle = "-";
        if (r.pesoUsado != null && r.repeticiones != null) {
            textoDetalle = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
        } else if (r.pesoUsado != null) {
            textoDetalle = r.pesoUsado + " kg";
        } else if (r.repeticiones != null) {
            textoDetalle = r.repeticiones + " Repeticiones";
        }
        tarjeta.appendChild(crearElemento("p", "registro-detalle titillium-base", textoDetalle.trim()));

        const diferencia = crearElemento("p", "registro-diferencia", "");
        if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
            const simbolo = r.diferenciaPeso > 0 ? "+" : "-";
            const icono = r.diferenciaPeso > 0 ? "↑ " : "↓ ";
            diferencia.textContent = (icono + simbolo + Math.abs(r.diferenciaPeso) + " kg").trim();
        } else {
            diferencia.textContent = "±0 kg";
            diferencia.classList.add("sin-cambio");
        }
        tarjeta.appendChild(diferencia);
        return tarjeta;
    }


    function crearTarjetaPR(r, index) {
        const tarjeta = document.createElement("div");
        tarjeta.className = "tarjeta-registro"; // Solo la clase base
        tarjeta.style.animationDelay = `${index * 0.07}s`;

        tarjeta.appendChild(crearElemento("strong", "registro-fecha titillium-negra", (r.fecha || "sin fecha").trim()));

        let textoDetalle = "-";
        if (r.pesoUsado != null && r.repeticiones != null) {
            textoDetalle = r.pesoUsado + " kg × " + r.repeticiones + " Reps"; // Usamos "Reps"
        } else if (r.pesoUsado != null) {
            textoDetalle = r.pesoUsado + " kg";
        } else if (r.repeticiones != null) {
            textoDetalle = r.repeticiones + " Reps";
        }
        tarjeta.appendChild(crearElemento("p", "registro-detalle titillium-base", textoDetalle.trim()));

        const diferencia = crearElemento("p", "registro-diferencia", "");
        if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
            const simbolo = r.diferenciaPeso > 0 ? "+" : "-";
            const icono = r.diferenciaPeso > 0 ? "↑ " : "↓ ";
            diferencia.textContent = (icono + simbolo + Math.abs(r.diferenciaPeso) + " kg").trim();

        } else {
            diferencia.textContent = "±0 kg";
        }
        tarjeta.appendChild(diferencia);
        return tarjeta;
    }

    function crearElemento(tag, className, textContent) {
        const el = document.createElement(tag);
        el.className = className;
        if (textContent) el.textContent = textContent;
        return el;
    }

    function renderizarBotonesPaginacionConNumeros(totalPaginas, paginaActual, contenedorBotones, callbackClick) {
        contenedorBotones.innerHTML = "";
        if (totalPaginas <= 1) return;

        const tamanoVentana = 5;
        const offset = Math.floor(tamanoVentana / 2);
        let startPage = paginaActual - offset;
        let endPage = paginaActual + offset;

        if (startPage < 1) {
            startPage = 1;
            endPage = Math.min(tamanoVentana, totalPaginas);
        }
        if (endPage > totalPaginas) {
            endPage = totalPaginas;
            startPage = Math.max(1, totalPaginas - tamanoVentana + 1);
        }

        // Botones de la ventana
        for (let i = startPage; i <= endPage; i++) {
            contenedorBotones.appendChild(crearBotonPagina(i, paginaActual, callbackClick));
        }
    }

    function crearBotonPagina(numeroPagina, paginaActual, callbackClick) {
        const btnPagina = document.createElement("button");
        btnPagina.className = "boton-pagina";
        btnPagina.textContent = numeroPagina;
        if (numeroPagina === paginaActual) {
            btnPagina.classList.add("activo");
        }
        btnPagina.addEventListener('click', () => {
            callbackClick(numeroPagina);
        });
        return btnPagina;
    }

    btnVolverPrincipal.addEventListener("click", () => {
        if (vinoDeDashboard) {
            window.location.href = contextPath + '/cliente'; //
        } else {
            pantallaProgreso.classList.remove("activa");
            pantallaLista.classList.add("activa");
        }
    });

    btnSiguiente.addEventListener('click', () => {
        verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, paginaActual + 1);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
    btnAnterior.addEventListener('click', () => {
        verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, paginaActual - 1);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });

    // --- Paginación PRs (Frontend) ---
    btnSiguientePRs.addEventListener('click', () => {
        paginaActualPRs++;
        renderizarPRs();
    });
    btnAnteriorPRs.addEventListener('click', () => {
        paginaActualPRs--;
        renderizarPRs();
    });

    // --- Paginación RMs (Frontend) ---
    btnSiguienteRMs.addEventListener('click', () => {
        paginaActualRMs++;
        renderizarRMs();
    });
    btnAnteriorRMs.addEventListener('click', () => {
        paginaActualRMs--;
        renderizarRMs();
    });

    document.querySelectorAll(".tarjeta-ejercicio").forEach(el => {
        el.addEventListener("click", () => {
            const id = parseInt(el.dataset.ejercicioId, 10);
            const nombre = el.dataset.ejercicioNombre;
            vinoDeDashboard = false;
            verProgresoPorId(id, nombre, 1);
        });
    });
});