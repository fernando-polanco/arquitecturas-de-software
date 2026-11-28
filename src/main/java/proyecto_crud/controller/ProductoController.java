package proyecto_crud.controller;

import java.util.List;

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
/**
 * Controlador web que expone las operaciones de gestión de productos y prepara los datos para las vistas Thymeleaf.
 */
@Controller
public class ProductoController {

    /**
     * Servicio de aplicación utilizado para consultar, guardar y eliminar productos.
     */
    private final ProductoService service;

    /**
     * Construye el controlador con el servicio de productos requerido.
     * @param service servicio que implementa las operaciones de negocio sobre productos.
     */
    public ProductoController(ProductoService service) {
        this.service = service;
    }

    /**
     * Redirige la ruta raíz a la pantalla principal de productos.
     * @return ruta de redirección a {@code /productos}.
     */
    @GetMapping("/")
    public String inicio() {
        return "redirect:/productos";
    }

    /**
     * Muestra la lista de productos con búsqueda opcional, paginación y datos de navegación.
     * @param nombre texto opcional para filtrar productos por nombre.
     * @param paginaTexto número de página solicitado como texto.
     * @param tamanoTexto cantidad de registros por página como texto.
     * @param model modelo utilizado para enviar los datos a la vista.
     * @return nombre de la vista de listado de productos.
     */
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

    /**
     * Prepara el formulario para registrar un producto nuevo.
     * @param model modelo al que se agregan el producto vacío, el título y la ruta de envío.
     * @return nombre de la vista del formulario.
     */
    @GetMapping("/productos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Agregar producto");
        model.addAttribute("accion", "/productos");
        return "productos/formulario";
    }

    /**
     * Valida y registra un producto nuevo; si hay errores, vuelve a mostrar el formulario.
     * @param producto datos del producto recibidos y validados.
     * @param result resultado de la validación de los campos.
     * @param model modelo utilizado si es necesario volver al formulario.
     * @param redirect atributos temporales para mostrar mensajes después de redirigir.
     * @return vista del formulario si hay errores o redirección al listado si se guarda.
     */
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

    /**
     * Busca un producto existente y prepara el formulario de edición.
     * @param id identificador del producto que se desea editar.
     * @param model modelo al que se agregan el producto y los datos del formulario.
     * @return nombre de la vista del formulario.
     */
    @GetMapping("/productos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = service.obtenerPorId(id);

        model.addAttribute("producto", producto);
        model.addAttribute("titulo", "Editar producto");
        model.addAttribute("accion", "/productos/actualizar/" + id);

        return "productos/formulario";
    }

    /**
     * Valida y guarda los cambios de un producto existente.
     * @param id identificador del producto que se actualiza.
     * @param producto datos modificados recibidos desde el formulario.
     * @param result resultado de la validación.
     * @param model modelo utilizado para volver al formulario si hay errores.
     * @param redirect atributos temporales para comunicar el resultado de la operación.
     * @return vista del formulario si hay errores o redirección al listado.
     */
    @PostMapping("/productos/actualizar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("producto") Producto producto,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirect) {

        service.obtenerPorId(id);

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

    /**
     * Elimina un producto y conserva los parámetros de búsqueda y paginación al regresar al listado.
     * @param id identificador del producto que se desea eliminar.
     * @param paginaTexto número de página actual recibido como texto.
     * @param tamanoTexto tamaño de página actual recibido como texto.
     * @param nombre filtro de búsqueda actual, si existe.
     * @param redirect atributos temporales para mensajes y parámetros de redirección.
     * @return redirección al listado de productos.
     */
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

    /**
     * Elimina los productos seleccionados y conserva el estado de la lista al redirigir.
     * @param ids identificadores de los productos seleccionados; puede ser nulo o estar vacío.
     * @param paginaTexto número de página actual recibido como texto.
     * @param tamanoTexto tamaño de página actual recibido como texto.
     * @param nombre filtro de búsqueda actual, si existe.
     * @param redirect atributos temporales para mensajes y parámetros de redirección.
     * @return redirección al listado de productos.
     */
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

    /**
     * Obtiene una página de productos ordenada por identificador, aplicando el filtro de nombre cuando corresponde.
     * @param nombreBuscado filtro opcional por nombre; {@code null} solicita todos los productos.
     * @param indicePagina índice de página basado en cero.
     * @param tamanoPagina cantidad de registros por página.
     * @return página de productos resultante.
     */
    private Page<Producto> consultarPagina(String nombreBuscado, int indicePagina, int tamanoPagina) {
        Pageable paginacion = PageRequest.of(indicePagina, tamanoPagina, Sort.by("id").ascending());
        if (nombreBuscado == null) {
            return service.listarPaginado(paginacion);
        }
        return service.buscarPorNombrePaginado(nombreBuscado, paginacion);
    }

    /**
     * Agrega a la redirección los parámetros válidos de paginación y búsqueda para conservar el estado del listado.
     * @param redirect objeto que acumula atributos de redirección.
     * @param paginaTexto número de página recibido como texto.
     * @param tamanoTexto tamaño de página recibido como texto.
     * @param nombre filtro de nombre recibido desde la solicitud.
     */
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
