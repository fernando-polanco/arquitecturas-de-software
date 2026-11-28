package proyecto_crud.exception;

/**
 * Excepción de ejecución que indica que no existe un producto con el identificador solicitado.
 */
public class ProductoNoEncontradoException extends RuntimeException {
    /**
     * Construye la excepción con un mensaje que identifica el producto inexistente.
     * @param id identificador del producto que no se encontró.
     */
    public ProductoNoEncontradoException(Long id) {
        super("El producto con ID " + id + " no existe");
    }
}
