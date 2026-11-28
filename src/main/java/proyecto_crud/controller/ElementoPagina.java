package proyecto_crud.controller;

/**
 * Representa un elemento de la navegación de paginación: un número de página o un indicador de elipsis.
 * @param numeroPagina número de página; es nulo para una elipsis.
 * @param elipsis indica si el elemento es una elipsis.
 * @param activo indica si la página representada está seleccionada.
 */
public record ElementoPagina(Integer numeroPagina, boolean elipsis, boolean activo) {

    /**
     * Crea un elemento que representa un número de página.
     */
    public static ElementoPagina pagina(int numeroPagina, boolean activo) {
        return new ElementoPagina(numeroPagina, false, activo);
    }

    /**
     * Crea un elemento que representa una elipsis entre grupos de páginas.
     */
    public static ElementoPagina elementoElipsis() {
        return new ElementoPagina(null, true, false);
    }
}
