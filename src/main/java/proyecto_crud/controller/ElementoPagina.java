package proyecto_crud.controller;

public record ElementoPagina(Integer numeroPagina, boolean elipsis, boolean activo) {

    public static ElementoPagina pagina(int numeroPagina, boolean activo) {
        return new ElementoPagina(numeroPagina, false, activo);
    }

    public static ElementoPagina elementoElipsis() {
        return new ElementoPagina(null, true, false);
    }
}
