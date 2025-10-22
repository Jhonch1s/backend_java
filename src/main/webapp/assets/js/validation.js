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

    form.addEventListener("submit", (e) => {
        e.preventDefault();

        limpiarTodosLosErrores(form);

        if (form.checkValidity() === false) {
            mostrarErroresFormulario(form);
            return;
        }

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
                        const error = new Error(data.message || 'Error desconocido del servidor');
                        error.errors = data.errors; // Adjunta el mapa de errores
                        throw error;
                    }
                    return data;
                } else {
                    const text = await response.text();
                    throw new Error(`Respuesta inesperada del servidor (no es JSON): ${text.substring(0, 200)}...`);
                }
            })
            .then(data => {
                showToast(data.message || "¡Operación exitosa!", 'success');

                form.reset();

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
    input.classList.add("is-invalid");
}

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
        const input = form.querySelector(`#${fieldId}`);
        if (input) {
            mostrarError(input, errors[fieldId]);
        } else {
            console.warn(`No se encontró el input con id "${fieldId}" para mostrar el error del servidor.`);
        }
    });
}

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
            if (toast.parentNode === container) {
                container.removeChild(toast);
            }
        });
    }, duration);
}