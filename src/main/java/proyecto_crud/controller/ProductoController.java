package proyecto_crud.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import proyecto_crud.model.Producto;
import proyecto_crud.service.ProductoService;

// @Controller hace que reciba las rutas del navegador y devuelve vistas HTML Thymeleaf
@Controller
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/productos";
    }

    @GetMapping("/productos")
    public String listar(Model model) {
        model.addAttribute("productos", service.listar());
        return "productos/lista";
    }

    @GetMapping("/productos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Agregar producto");
        model.addAttribute("accion", "/productos");
        return "productos/formulario";
    }

    @PostMapping("/productos")
    public String crear(@Valid @ModelAttribute("producto") Producto producto,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Agregar producto");
            model.addAttribute("accion", "/productos");
            return "productos/formulario";
        }
        producto.setId(null);
        service.guardar(producto);
        redirect.addFlashAttribute("exito", "Producto agregado correctamente");
        return "redirect:/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Producto> producto = service.buscarPorId(id);
        if (producto.isEmpty()) {
            redirect.addFlashAttribute("error", "El producto no existe");
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto.get());
        model.addAttribute("titulo", "Editar producto");
        model.addAttribute("accion", "/productos/actualizar/" + id);
        return "productos/formulario";
    }

    @PostMapping("/productos/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("producto") Producto producto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {
        if (service.buscarPorId(id).isEmpty()) {
            redirect.addFlashAttribute("error", "El producto no existe");
            return "redirect:/productos";
        }
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar producto");
            model.addAttribute("accion", "/productos/actualizar/" + id);
            return "productos/formulario";
        }
        producto.setId(id);
        service.guardar(producto);
        redirect.addFlashAttribute("exito", "Producto actualizado correctamente");
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        if (service.eliminar(id)) {
            redirect.addFlashAttribute("exito", "Producto eliminado correctamente");
        } else {
            redirect.addFlashAttribute("error", "El producto no existe");
        }
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar-seleccionados")
    public String eliminarSeleccionados(@RequestParam(name = "ids", required = false) List<Long> ids,
                                        RedirectAttributes redirect) {
        if (ids == null || ids.isEmpty()) {
            redirect.addFlashAttribute("error", "No seleccionaste ningún producto");
            return "redirect:/productos";
        }
        int eliminados = service.eliminarVarios(ids);
        redirect.addFlashAttribute("exito", eliminados + " producto(s) eliminado(s)");
        return "redirect:/productos";
    }
}
