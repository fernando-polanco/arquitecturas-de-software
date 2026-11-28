package proyecto_crud.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Manejador global de excepciones MVC que transforma los errores de producto no encontrado en mensajes visibles y redirecciones.
 */
@ControllerAdvice
public class ManejadorGlobalException {
    /**
     * Gestiona las excepciones de producto inexistente, publica el mensaje como atributo temporal y redirige al listado.
     * @param excepcion excepción que contiene el identificador del producto no encontrado.
     * @param atributosRedireccion contenedor de atributos temporales para la siguiente solicitud.
     * @return ruta de redirección al listado de productos.
     */
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public String manejarProductoNoEncontrado(
            ProductoNoEncontradoException excepcion,
            RedirectAttributes atributosRedireccion) {

        atributosRedireccion.addFlashAttribute("error", excepcion.getMessage());
        return "redirect:/productos";
    }
}
