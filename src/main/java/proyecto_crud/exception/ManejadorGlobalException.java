package proyecto_crud.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ManejadorGlobalException {
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public String manejarProductoNoEncontrado(
            ProductoNoEncontradoException excepcion,
            RedirectAttributes atributosRedireccion) {

        atributosRedireccion.addFlashAttribute("error", excepcion.getMessage());
        return "redirect:/productos";
    }
}
