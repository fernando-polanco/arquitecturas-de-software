const seleccionarTodos = document.getElementById('seleccionarTodos');
const casillas = document.querySelectorAll('.casilla-fila');

seleccionarTodos.addEventListener('change', () => {
    casillas.forEach((c) => {
        c.checked = seleccionarTodos.checked;
    });
});

const modal = document.getElementById('modalConfirmacion');
const mensajeModal = document.getElementById('modalMensaje');

const btnCancelar = document.getElementById('btnCancelar');
const btnConfirmar = document.getElementById('btnConfirmar');

const formLista = document.getElementById('formLista');
const btnEliminarSeleccionados = document.getElementById('btnEliminarSeleccionados');

let botonPendiente = null;

const abrirModal = (boton, mensaje) => {
    botonPendiente = boton;
    mensajeModal.textContent = mensaje;

    modal.classList.add('activo');
    modal.setAttribute('aria-hidden', 'false');

    btnConfirmar.focus();
};

const cerrarModal = () => {
    modal.classList.remove('activo');
    modal.setAttribute('aria-hidden', 'true');
    botonPendiente = null;
};

document.querySelectorAll('.btn-eliminar-uno').forEach((boton) => {
    boton.addEventListener('click', (e) => {
        e.preventDefault();
        abrirModal(
            boton,
            '¿Seguro que quieres eliminar este producto?'
        );
    });
});

btnEliminarSeleccionados.addEventListener('click', (e) => {
    const marcadas = document.querySelectorAll(
        '.casilla-fila:checked'
    ).length;

    if (marcadas > 0) {
        e.preventDefault();
        abrirModal(
            btnEliminarSeleccionados,
            '¿Seguro que quieres eliminar ' +
            marcadas + ' producto(s) seleccionado(s)?'
        );
    }
});

btnCancelar.addEventListener('click', () => {
    cerrarModal();
});

btnConfirmar.addEventListener('click', () => {
    if (botonPendiente) {
        // Ejecutar el formulario utilizando
        // el botón original como submitter.
        formLista.requestSubmit(botonPendiente);
    }
});

// Cerrar si se da clic afuera del modal
modal.addEventListener('click', (e) => {
    if (e.target === modal) {
        cerrarModal();
    }
});