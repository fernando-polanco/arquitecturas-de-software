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
            @RequestParam(name = "page", required = false) String pageParam,
            @RequestParam(name = "size", required = false) String sizeParam,
            Model model) {

        int requestedPage = PaginationHelper.parsePage(pageParam);
        int pageSize = PaginationHelper.parseSize(sizeParam);
        String nombreBuscado = (nombre == null || nombre.isBlank()) ? null : nombre.trim();

        Page<Producto> productosPage = consultarPagina(nombreBuscado, requestedPage - 1, pageSize);

        if (productosPage.getTotalPages() > 0 && requestedPage > productosPage.getTotalPages()) {
            requestedPage = productosPage.getTotalPages();
            productosPage = consultarPagina(nombreBuscado, requestedPage - 1, pageSize);
        }

        int totalPages = productosPage.getTotalPages();
        long totalElements = productosPage.getTotalElements();
        int currentPage = totalPages == 0 ? 0 : productosPage.getNumber() + 1;
        long startItem = totalElements == 0 ? 0 : (long) productosPage.getNumber() * pageSize + 1;
        long endItem = totalElements == 0 ? 0 : startItem + productosPage.getNumberOfElements() - 1;

        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("nombreBuscado", nombreBuscado);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("minPageSize", PaginationHelper.MIN_PAGE_SIZE);
        model.addAttribute("maxPageSize", PaginationHelper.MAX_PAGE_SIZE);
        model.addAttribute("hasPrevious", productosPage.hasPrevious());
        model.addAttribute("hasNext", productosPage.hasNext());
        model.addAttribute("isFirst", totalPages == 0 || productosPage.isFirst());
        model.addAttribute("isLast", totalPages == 0 || productosPage.isLast());
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("pageItems", PaginationHelper.buildEllipsisPages(currentPage, totalPages));

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
                           @RequestParam(name = "page", required = false) String pageParam,
                           @RequestParam(name = "size", required = false) String sizeParam,
                           @RequestParam(name = "nombre", required = false) String nombre,
                           RedirectAttributes redirect) {
        if (service.eliminar(id)) {
            redirect.addFlashAttribute("exito", "Producto eliminado correctamente");
        } else {
            redirect.addFlashAttribute("error", "El producto no existe");
        }
        agregarParametrosPaginacion(redirect, pageParam, sizeParam, nombre);
        return "redirect:/productos";
    }

    @PostMapping("/productos/eliminar-seleccionados")
    public String eliminarSeleccionados(@RequestParam(name = "ids", required = false) List<Long> ids,
                                        @RequestParam(name = "page", required = false) String pageParam,
                                        @RequestParam(name = "size", required = false) String sizeParam,
                                        @RequestParam(name = "nombre", required = false) String nombre,
                                        RedirectAttributes redirect) {
        if (ids == null || ids.isEmpty()) {
            redirect.addFlashAttribute("error", "No seleccionaste ningún producto");
            agregarParametrosPaginacion(redirect, pageParam, sizeParam, nombre);
            return "redirect:/productos";
        }
        int eliminados = service.eliminarVarios(ids);
        redirect.addFlashAttribute("exito", eliminados + " producto(s) eliminado(s)");
        agregarParametrosPaginacion(redirect, pageParam, sizeParam, nombre);
        return "redirect:/productos";
    }

    private Page<Producto> consultarPagina(String nombreBuscado, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").ascending());
        if (nombreBuscado == null) {
            return service.listarPaginado(pageable);
        }
        return service.buscarPorNombrePaginado(nombreBuscado, pageable);
    }

    private void agregarParametrosPaginacion(RedirectAttributes redirect,
                                             String pageParam,
                                             String sizeParam,
                                             String nombre) {
        if (pageParam != null && !pageParam.isBlank()) {
            redirect.addAttribute("page", PaginationHelper.parsePage(pageParam));
        }
        if (sizeParam != null && !sizeParam.isBlank()) {
            redirect.addAttribute("size", PaginationHelper.parseSize(sizeParam));
        }
        if (nombre != null && !nombre.isBlank()) {
            redirect.addAttribute("nombre", nombre.trim());
        }
    }
}
