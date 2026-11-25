// Casilla del encabezadoque  marca o desmarca todas las filas
const seleccionarTodos = document.getElementById('seleccionarTodos');
const casillas = document.querySelectorAll('.casilla-fila');

seleccionarTodos.addEventListener('change', function () {
    casillas.forEach(function (c) { c.checked = seleccionarTodos.checked; });
});

// Confirmar antes de eliminar un producto
document.querySelectorAll('.btn-eliminar-uno').forEach(function (boton) {
    boton.addEventListener('click', function (e) {
        if (!confirm('¿Seguro que quieres eliminar este producto?')) {
            e.preventDefault();
        }
    });
});

// Confirmar antes de eliminar los productos seleccionados
document.getElementById('btnEliminarSeleccionados').addEventListener('click', function (e) {
    const marcadas = document.querySelectorAll('.casilla-fila:checked').length;
    if (marcadas > 0 && !confirm('¿Eliminar ' + marcadas + ' producto(s) seleccionado(s)?')) {
        e.preventDefault();
    }
});