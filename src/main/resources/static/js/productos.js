const seleccionarTodos = document.getElementById('seleccionarTodos');
const casillas = document.querySelectorAll('.casilla-fila');

if (seleccionarTodos) {
    seleccionarTodos.addEventListener('change', () => {
        casillas.forEach((c) => {
            c.checked = seleccionarTodos.checked;
        });
    });
}

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

if (btnEliminarSeleccionados) {
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
}

if (btnCancelar) {
    btnCancelar.addEventListener('click', () => {
        cerrarModal();
    });
}

if (btnConfirmar) {
    btnConfirmar.addEventListener('click', () => {
        if (botonPendiente) {
            // Ejecutar el formulario utilizando
            // el botón original como submitter.
            formLista.requestSubmit(botonPendiente);
        }
    });
}

// Cerrar si se da clic afuera del modal
if (modal) {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            cerrarModal();
        }
    });
}

const mensajes = document.querySelectorAll(
    '.alert-success, .alert-danger'
);

mensajes.forEach((mensaje) => {
    // Activar la transición de Bootstrap
    mensaje.classList.add('fade', 'show');

    setTimeout(() => {
        // Iniciar desaparición
        mensaje.classList.remove('show');
        // Eliminar del HTML después de la transición
        setTimeout(() => {
            mensaje.remove();
        }, 200);
    }, 3000);
});

const inputPageSize = document.getElementById('pageSize');

if (inputPageSize) {
    inputPageSize.addEventListener('invalid', () => {
        inputPageSize.setCustomValidity('El tamaño de página debe ser un entero entre 1 y 100.');
    });

    inputPageSize.addEventListener('input', () => {
        inputPageSize.setCustomValidity('');
    });
}
