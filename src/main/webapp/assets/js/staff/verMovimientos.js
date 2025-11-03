document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("filtroForm");
    const btnReiniciar = document.getElementById("btnReiniciar");
    const errorMsg = document.getElementById("filtroError");

    form.addEventListener("submit", function (e) {
        const ci = form.cliente.value.trim();
        const desde = form.desde.value;
        const hasta = form.hasta.value;
        errorMsg.textContent = "";

        const errores = [];

        if (ci && !/^\d{8}$/.test(ci))
            errores.push("La cédula debe tener exactamente 8 dígitos.");

        if ((desde && !hasta) || (!desde && hasta))
            errores.push("Debes ingresar ambas fechas: 'Desde' y 'Hasta'.");

        if (desde && hasta && new Date(hasta) < new Date(desde))
            errores.push("La fecha 'Hasta' no puede ser anterior a 'Desde'.");

        if (errores.length > 0) {
            e.preventDefault();
            errorMsg.textContent = errores.join(" ");
        }
    });

    btnReiniciar.addEventListener("click", () => {
        window.location.href = contextPath + "/staff/movimientos";
    });
});

function cambiarPagina(nuevaPagina) {
    const form = document.getElementById("filtroForm");
    let inputPage = form.querySelector("input[name='page']");

    if (!inputPage) {
        inputPage = document.createElement("input");
        inputPage.type = "hidden";
        inputPage.name = "page";
        form.appendChild(inputPage);
    }

    inputPage.value = nuevaPagina;
    form.submit();
}