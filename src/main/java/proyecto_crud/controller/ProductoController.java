package proyecto_crud.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String listar(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "page", required = false) String paginaTexto,
            @RequestParam(name = "size", required = false) String tamanoTexto,
            Model model) {

        int paginaSolicitada = PaginacionUtil.obtenerNumeroPagina(paginaTexto);
        int tamanoPagina = PaginacionUtil.obtenerTamanoPagina(tamanoTexto);
        String nombreBuscado = (nombre == null || nombre.isBlank()) ? null : nombre.trim();

        Page<Producto> paginaProductos = consultarPagina(nombreBuscado, paginaSolicitada - 1, tamanoPagina);

        if (paginaProductos.getTotalPages() > 0 && paginaSolicitada > paginaProductos.getTotalPages()) {
            paginaSolicitada = paginaProductos.getTotalPages();
            paginaProductos = consultarPagina(nombreBuscado, paginaSolicitada - 1, tamanoPagina);
        }

        int totalPaginas = paginaProductos.getTotalPages();
        long totalRegistros = paginaProductos.getTotalElements();
        int paginaActual = totalPaginas == 0 ? 0 : paginaProductos.getNumber() + 1;
        long registroInicio = totalRegistros == 0 ? 0 : (long) paginaProductos.getNumber() * tamanoPagina + 1;
        long registroFin = totalRegistros == 0 ? 0 : registroInicio + paginaProductos.getNumberOfElements() - 1;

        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("nombreBuscado", nombreBuscado);
        model.addAttribute("paginaActual", paginaActual);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", totalRegistros);
        model.addAttribute("tamanoPagina", tamanoPagina);
        model.addAttribute("tamanoMinimo", PaginacionUtil.TAMANO_MINIMO);
        model.addAttribute("tamanoMaximo", PaginacionUtil.TAMANO_MAXIMO);
        model.addAttribute("tieneAnterior", paginaProductos.hasPrevious());
        model.addAttribute("tieneSiguiente", paginaProductos.hasNext());
        model.addAttribute("esPrimera", totalPaginas == 0 || paginaProductos.isFirst());
        model.addAttribute("esUltima", totalPaginas == 0 || paginaProductos.isLast());
        model.addAttribute("registroInicio", registroInicio);
        model.addAttribute("registroFin", registroFin);
        model.addAttribute("elementosPagina", PaginacionUtil.construirPaginasConElipsis(paginaActual, totalPaginas));

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
    public String eliminar(@PathVariable Long id,
                           @RequestParam(name = "page", required = false) String paginaTexto,
                           @RequestParam(name = "size", required = false) String tamanoTexto,
                           @RequestParam(name = "nombre", required = false) String nombre,
                           RedirectAttributes redirect) {
        if (service.eliminar(id)) {
            redirect.addFlashAttribute("exito", "Producto eliminado correctamente");
        } else {
            redirect.addFlashAttribute("error", "El producto no existe");
        }
        agregarParametrosPaginacion(redirect, paginaTexto, tamanoTexto, nombre);
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar-seleccionados")
    public String eliminarSeleccionados(@RequestParam(name = "ids", required = false) List<Long> ids,
                                        @RequestParam(name = "page", required = false) String paginaTexto,
                                        @RequestParam(name = "size", required = false) String tamanoTexto,
                                        @RequestParam(name = "nombre", required = false) String nombre,
                                        RedirectAttributes redirect) {
        if (ids == null || ids.isEmpty()) {
            redirect.addFlashAttribute("error", "No seleccionaste ningún producto");
            agregarParametrosPaginacion(redirect, paginaTexto, tamanoTexto, nombre);
            return "redirect:/productos";
        }
        int eliminados = service.eliminarVarios(ids);
        redirect.addFlashAttribute("exito", eliminados + " producto(s) eliminado(s)");
        agregarParametrosPaginacion(redirect, paginaTexto, tamanoTexto, nombre);
        return "redirect:/productos";
    }

    private Page<Producto> consultarPagina(String nombreBuscado, int indicePagina, int tamanoPagina) {
        Pageable paginacion = PageRequest.of(indicePagina, tamanoPagina, Sort.by("id").ascending());
        if (nombreBuscado == null) {
            return service.listarPaginado(paginacion);
        }
        return service.buscarPorNombrePaginado(nombreBuscado, paginacion);
    }

    private void agregarParametrosPaginacion(RedirectAttributes redirect,
                                             String paginaTexto,
                                             String tamanoTexto,
                                             String nombre) {
        if (paginaTexto != null && !paginaTexto.isBlank()) {
            redirect.addAttribute("page", PaginacionUtil.obtenerNumeroPagina(paginaTexto));
        }
        if (tamanoTexto != null && !tamanoTexto.isBlank()) {
            redirect.addAttribute("size", PaginacionUtil.obtenerTamanoPagina(tamanoTexto));
        }
        if (nombre != null && !nombre.isBlank()) {
            redirect.addAttribute("nombre", nombre.trim());
        }
    }
}
