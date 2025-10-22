(() => {
    const form = document.getElementById('form-login');
    const usuario = document.getElementById('usuario');
    const contrasena = document.getElementById('contrasena');
    const errorUsuario = document.getElementById('error-usuario');
    const errorContrasena = document.getElementById('error-contrasena');
    const errorGlobal = document.getElementById('error-global');
    const togglePassBtn = document.querySelector('.login__ver-contrasena');

    // Mostrar / ocultar contraseña
    togglePassBtn?.addEventListener('click', () => {
        const isPassword = contrasena.getAttribute('type') === 'password';
        contrasena.setAttribute('type', isPassword ? 'text' : 'password');
        togglePassBtn.setAttribute('aria-pressed', String(isPassword));
    });

    // Helpers
    const limpiarErrores = () => {
        errorUsuario.textContent = '';
        errorContrasena.textContent = '';
        errorGlobal.textContent = '';
    };

    const validarUsuario = () => {
        const valor = usuario.value.trim();
        if (!valor) {
            errorUsuario.textContent = 'El usuario es obligatorio.';
            return false;
        }
        // Validación simple para cédula (7 a 8 dígitos) — puedes ajustar a tu regla exacta
        const esCedulaSimple = /^\d{7,8}$/.test(valor);
        if (!esCedulaSimple) {
            errorUsuario.textContent = 'Ingresá una cédula válida (7–8 dígitos).';
            return false;
        }
        return true;
    };

    const validarContrasena = () => {
        const valor = contrasena.value;
        if (!valor) {
            errorContrasena.textContent = 'La contraseña es obligatoria.';
            return false;
        }
        if (valor.length < 8) {
            errorContrasena.textContent = 'La contraseña debe tener al menos 8 caracteres.';
            return false;
        }
        return true;
    };

    // Validación on-the-fly
    usuario.addEventListener('input', () => { errorUsuario.textContent = ''; });
    contrasena.addEventListener('input', () => { errorContrasena.textContent = ''; });

    // Submit
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        limpiarErrores();

        const okUsuario = validarUsuario();
        const okContrasena = validarContrasena();

        if (!okUsuario || !okContrasena) {
            errorGlobal.textContent = 'Revisá los campos marcados.';
            return;
        }

        errorGlobal.textContent = '';
        form.querySelector('.login__boton').disabled = true;
        form.querySelector('.login__boton').textContent = 'Ingresando…';

        // Simulación de respuesta
        setTimeout(() => {
            // Para la demo, siempre “falla” la credencial:
            form.querySelector('.login__boton').disabled = false;
            form.querySelector('.login__boton').textContent = 'Ingresar';
            errorGlobal.textContent = 'Credenciales inválidas. Intentá nuevamente.';
            contrasena.focus();
            contrasena.select();
        }, 900);
    });
})();
