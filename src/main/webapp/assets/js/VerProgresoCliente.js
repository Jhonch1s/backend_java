document.addEventListener("DOMContentLoaded", () => {
    const pantallaLista = document.getElementById("pantalla-lista");
    const pantallaProgreso = document.getElementById("pantalla-progreso");
    const nombreEjercicio = document.getElementById("nombre-ejercicio");

    const btnVolverPrincipal = document.querySelector(".btn-volver");

    const paginacionControles = document.getElementById("paginacion-controles");
    const btnAnterior = document.getElementById("btn-anterior");
    const btnSiguiente = document.getElementById("btn-siguiente");
    const numerosPagina = document.getElementById("numeros-pagina");

    let ejercicioIdActual = null;
    let ejercicioNombreActual = null;
    let paginaActual = 1;

    async function verProgresoPorId(id, nombre, pagina = 1) {
        if (!id) {
            console.error("ID de ejercicio no definido");
            return;
        }

        ejercicioIdActual = id;
        ejercicioNombreActual = nombre;
        paginaActual = pagina;

        // La URL ahora pide una página específica
        let url = contextPath + "/detalle-progreso?id=" + encodeURIComponent(id) + "&pagina=" + pagina;

        try {
            const resp = await fetch(url);
            const data = await resp.json();
            console.log("Datos recibidos:", data);

            if (!Array.isArray(data.registros)) {
                throw new Error("Respuesta inválida del servidor");
            }
            nombreEjercicio.textContent = nombre;

            // Lógica de "volver" simplificada
            btnVolverPrincipal.style.display = 'inline-flex';

            const registrosList = document.getElementById("registros-recientes");
            registrosList.innerHTML = "";
            if (Array.isArray(data.registros) && data.registros.length > 0) {
                data.registros.forEach((r, index) => {
                    const tarjeta = document.createElement("div");
                    tarjeta.classList.add("tarjeta-registro");
                    tarjeta.style.animationDelay = `${index * 0.07}s`;

                    if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
                        tarjeta.classList.add(r.diferenciaPeso > 0 ? "mejora" : "retroceso");
                    } else {
                        tarjeta.classList.add("sin-cambio");
                    }
                    const fecha = document.createElement("strong");
                    fecha.className = "registro-fecha titillium-negra";
                    fecha.textContent = (r.fecha || "sin fecha").trim();
                    const detalle = document.createElement("p");
                    detalle.className = "registro-detalle titillium-base";
                    let texto = "";
                    if (r.pesoUsado != null && r.repeticiones != null) {
                        texto = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
                    } else if (r.pesoUsado != null) {
                        texto = r.pesoUsado + " kg";
                    } else if (r.repeticiones != null) {
                        texto = r.repeticiones + " Repeticiones";
                    } else {
                        texto = "-";
                    }
                    detalle.textContent = texto.trim();
                    tarjeta.appendChild(fecha);
                    tarjeta.appendChild(detalle);
                    const diferencia = document.createElement("p");
                    diferencia.className = "registro-diferencia";
                    if (r.diferenciaPeso != null && r.diferenciaPeso !== 0) {
                        // Caso Mejora (verde) o Retroceso (rojo)
                        const simbolo = r.diferenciaPeso > 0 ? "+" : "-";
                        const icono = r.diferenciaPeso > 0 ?
                            "↑ " : "↓ ";
                        diferencia.textContent = (icono + simbolo + Math.abs(r.diferenciaPeso) + " kg").trim();
                    } else {
                        diferencia.textContent = "±0 kg"; // <-- ¡AQUÍ ESTÁ TU TEXTO!
                        diferencia.classList.add("sin-cambio"); // Le ponemos la clase para el estilo
                    }
                    tarjeta.appendChild(diferencia);
                    registrosList.appendChild(tarjeta);
                });
            } else {
                registrosList.innerHTML = "<div class='tarjeta-registro'>No hay registros recientes</div>";
            }

            const prsList = document.getElementById("mejores-prs");
            prsList.innerHTML = "";
            if (Array.isArray(data.prs) && data.prs.length > 0) {
                data.prs.forEach((r, index) => {
                    const tarjeta = document.createElement("div");
                    tarjeta.className = "tarjeta-registro";
                    tarjeta.style.animationDelay = `${index * 0.07}s`;
                    const fecha = document.createElement("strong");
                    fecha.className = "registro-fecha titillium-negra";
                    fecha.textContent = (r.fecha || "sin fecha").trim();
                    const detalle = document.createElement("p");
                    detalle.className = "registro-detalle titillium-base";
                    let texto = "";
                    if (r.pesoUsado != null && r.repeticiones != null) {
                        texto = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
                    } else if (r.pesoUsado != null) {
                        texto = r.pesoUsado + " kg";
                    } else if (r.repeticiones != null) {
                        texto = r.repeticiones + " Repeticiones";
                    } else {
                        texto = "-";
                    }
                    detalle.textContent = texto.trim();
                    tarjeta.appendChild(fecha);
                    tarjeta.appendChild(detalle);
                    prsList.appendChild(tarjeta);
                });
            } else {
                prsList.innerHTML = "<div class='tarjeta-registro'>No hay PRs registrados</div>";
            }

            const rmsList = document.getElementById("mejores-rms");
            rmsList.innerHTML = "";
            if (Array.isArray(data.rms) && data.rms.length > 0) {
                data.rms.forEach((r, index) => {
                    const tarjeta = document.createElement("div");
                    tarjeta.className = "tarjeta-registro";
                    tarjeta.style.animationDelay = `${index * 0.07}s`;
                    const fecha = document.createElement("strong");
                    fecha.className = "registro-fecha titillium-negra";
                    fecha.textContent = (r.fecha || "sin fecha").trim();
                    const detalle = document.createElement("p");
                    detalle.className = "registro-detalle titillium-base";
                    let texto = "";
                    if (r.pesoUsado != null && r.repeticiones != null) {
                        texto = r.pesoUsado + " kg × " + r.repeticiones + " Repeticiones";
                    } else if (r.pesoUsado != null) {
                        texto = r.pesoUsado + " kg";
                    } else if (r.repeticiones != null) {
                        texto = r.repeticiones + " Repeticiones";
                    } else {
                        texto = "-";
                    }
                    detalle.textContent = texto.trim();
                    tarjeta.appendChild(fecha);
                    tarjeta.appendChild(detalle);
                    rmsList.appendChild(tarjeta);
                });
            } else {
                rmsList.innerHTML = "<div class='tarjeta-registro'>No hay RMs registrados</div>";
            }

            console.log("RMs recibidos:", data.rms);
            console.log("Datos completos del progreso:", data);


            numerosPagina.innerHTML = "";
            if (data.totalPaginas > 1) {

                btnAnterior.disabled = (data.paginaActual <= 1);
                btnSiguiente.disabled = (data.paginaActual >= data.totalPaginas);

                const tamanoVentana = 5;
                const offset = Math.floor(tamanoVentana / 2); // 2
                let startPage = data.paginaActual - offset;
                let endPage = data.paginaActual + offset;

                // Ajustar si estamos cerca del inicio (página 1)
                if (startPage < 1) {
                    startPage = 1;
                    endPage = Math.min(tamanoVentana, data.totalPaginas);
                }

                // Ajustar si estamos cerca del final (última página)
                if (endPage > data.totalPaginas) {
                    endPage = data.totalPaginas;
                    startPage = Math.max(1, data.totalPaginas - tamanoVentana + 1);
                }


                for (let i = startPage; i <= endPage; i++) {

                    const btnPagina = document.createElement("button");
                    btnPagina.className = "boton-pagina";
                    btnPagina.textContent = i;

                    if (i === data.paginaActual) {
                        btnPagina.classList.add("activo");
                    }


                    btnPagina.addEventListener('click', () => {
                        if (ejercicioIdActual && ejercicioNombreActual) {
                            verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, i);
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                        }
                    });

                    numerosPagina.appendChild(btnPagina);
                }

                paginacionControles.style.display = 'flex';
            } else {

                paginacionControles.style.display = 'none';
            }


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

    btnVolverPrincipal.addEventListener("click", () => {
        pantallaProgreso.classList.remove("activa");
        pantallaLista.classList.add("activa");
    });

    // --- EVENTOS DE PAGINACIÓN ---
    btnSiguiente.addEventListener('click', () => {
        if (ejercicioIdActual && ejercicioNombreActual) {
            // Llama a la misma función, pero para la página siguiente
            verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, paginaActual + 1);
            window.scrollTo({ top: 0, behavior: 'smooth' }); // Subir al inicio
        }
    });

    btnAnterior.addEventListener('click', () => {
        if (ejercicioIdActual && ejercicioNombreActual) {
            verProgresoPorId(ejercicioIdActual, ejercicioNombreActual, paginaActual - 1);
            window.scrollTo({ top: 0, behavior: 'smooth' }); // Subir al inicio
        }
    });

    document.querySelectorAll(".tarjeta-ejercicio").forEach(el => {
        el.addEventListener("click", () => {
            const id = parseInt(el.dataset.ejercicioId, 10);
            const nombre = el.dataset.ejercicioNombre;
            verProgresoPorId(id, nombre, 1);
        });
    });
});