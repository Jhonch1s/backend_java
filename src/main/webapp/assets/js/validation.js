/**
 * Módulo de validación de formularios
 * Se encarga de validar los campos requeridos en el frontend
 * y de enviar los datos vía Fetch al servlet correspondiente.
 * También muestra los errores devueltos por el servlet.
 */
export function inicializarValidacion(formId) {
    const form = document.getElementById(formId);
    if (!form) {
        console.error(`Formulario con ID "${formId}" no encontrado.`);
        return;
    }

    const submitButton = form.querySelector('button[type="submit"]');
    if (!submitButton) {
        console.warn(`Botón de envío no encontrado en el formulario "${formId}".`);
        return;
    }
    const originalButtonText = submitButton.innerHTML;

    // Controlar el envío del formulario
    form.addEventListener("submit", (e) => {
        e.preventDefault(); // Previene el envío HTML tradicional

        // Limpia errores previos antes de validar de nuevo
        limpiarTodosLosErrores(form);

        // Valida campos requeridos en el frontend
        if (form.checkValidity() === false) {
            mostrarErroresFormulario(form); // Muestra errores de campos vacíos
            return; // No envía si falta algo básico
        }

        // --- Si la validación básica del navegador pasa, envía al servlet ---
        submitButton.disabled = true;
        submitButton.innerHTML = 'Enviando...';

        const formData = new FormData(form);

        fetch(form.action, {
            method: 'POST',
            body: formData,
        })
            .then(async response => { // Usamos async para poder leer el cuerpo del error
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    const data = await response.json(); // Intenta leer el JSON siempre
                    if (!response.ok) {
                        // Si la respuesta no es OK (ej: 400, 409, 500), lanza un error con los datos
                        const error = new Error(data.message || 'Error desconocido del servidor');
                        error.errors = data.errors; // Adjunta el mapa de errores
                        throw error;
                    }
                    return data; // Si la respuesta es OK, devuelve los datos de éxito
                } else {
                    // Si la respuesta NO es JSON, es un error grave del servidor.
                    const text = await response.text();
                    throw new Error(`Respuesta inesperada del servidor (no es JSON): ${text.substring(0, 200)}...`);
                }
            })
            .then(data => {
                // --- ÉXITO ---
                // 1. Muestra la notificación verde
                showToast(data.message || "¡Operación exitosa!", 'success');

                // 2. NO redirige (línea comentada/eliminada)
                // if (formId.includes('plan')) {
                //     window.location.hash = "#/planes/lista";
                // } else if (formId.includes('cliente')) {
                //     window.location.hash = "#/clientes/lista";
                // }

                // 3. (Opcional) Limpia el formulario para permitir nueva entrada
                form.reset();

                // 4. (Opcional) Pone el foco en el primer campo
                const firstInput = form.querySelector('input, select');
                if (firstInput) firstInput.focus();

            })
            .catch(error => {
                // --- MANEJO DE ERRORES ---
                console.error("Error al enviar formulario:", error);
                // Muestra la notificación roja
                showToast("Error al guardar: " + error.message, 'error');

                // Si el error tiene un mapa de 'errors' (viene del servlet)
                if (error.errors) {
                    mostrarErroresDelServidor(form, error.errors);
                }
            })
            .finally(() => {
                // --- SIEMPRE SE EJECUTA ---
                // Restaura el botón
                if(submitButton){
                    submitButton.disabled = false;
                    submitButton.innerHTML = originalButtonText;
                }
            });
    });

    // Limpiar errores mientras el usuario escribe o cambia selección
    form.querySelectorAll("[required], input[type='email']").forEach(input => {
        input.addEventListener("input", () => {
            if (input.validity.valid) limpiarError(input);
        });
        input.addEventListener("change", () => {
            if (input.validity.valid) limpiarError(input);
        });
    });
}

/** Muestra errores para todos los campos requeridos que estén inválidos */
function mostrarErroresFormulario(form) {
    form.querySelectorAll("[required]").forEach(input => {
        if (!input.validity.valid) {
            mostrarError(input, "Este campo es obligatorio.");
        } else {
            limpiarError(input);
        }
    });
    form.querySelectorAll("input[type='email']").forEach(input => {
        if(input.value && !input.validity.valid){ // Si hay valor pero es inválido
            mostrarError(input, "El formato del email no es válido.");
        }
    });
}

/** Muestra un mensaje de error específico debajo de un input */
function mostrarError(input, mensaje) {
    const errorContainer = document.getElementById(`error-${input.id}`);
    if (errorContainer) {
        errorContainer.textContent = mensaje;
    }
    input.classList.add("is-invalid"); // Aplica el estilo de borde rojo, etc.
}

/** Limpia el mensaje de error de un input específico */
function limpiarError(input) {
    const errorContainer = document.getElementById(`error-${input.id}`);
    if (errorContainer) {
        errorContainer.textContent = "";
    }
    input.classList.remove("is-invalid");
}

/** Limpia todos los mensajes de error de un formulario */
function limpiarTodosLosErrores(form) {
    form.querySelectorAll('.form__error-message').forEach(el => el.textContent = '');
    form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
}

/** Muestra los errores específicos devueltos por el servlet */
function mostrarErroresDelServidor(form, errors) {
    Object.keys(errors).forEach(fieldId => {
        // Busca el input usando el ID que coincide con la clave del error (ej: 'cliente-ci')
        const input = form.querySelector(`#${fieldId}`);
        if (input) {
            mostrarError(input, errors[fieldId]);
        } else {
            console.warn(`No se encontró el input con id "${fieldId}" para mostrar el error del servidor.`);
        }
    });
}

// --- Función para mostrar Notificaciones Toast ---
// Asegúrate de tener el CSS y el div #toast-container en tu HTML
function showToast(message, type = 'success', duration = 4000) {
    const container = document.getElementById('toast-container');
    if (!container) {
        console.error("Elemento #toast-container no encontrado en el DOM.");
        // Fallback a alert si no existe el contenedor
        alert(`${type === 'success' ? '✅' : '❌'} ${message}`);
        return;
    }

    const toast = document.createElement('div');
    toast.className = `toast toast--${type}`;
    toast.textContent = message;

    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        toast.addEventListener('animationend', () => {
            if (toast.parentNode === container) { // Doble chequeo por si acaso
                container.removeChild(toast);
            }
        });
    }, duration);
}