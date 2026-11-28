package proyecto_crud.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import proyecto_crud.model.Producto;
import proyecto_crud.service.ProductoService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas del controlador de productos centradas en la paginación, la búsqueda, la representación de la vista y la conservación de parámetros al eliminar.
 */
@ExtendWith(MockitoExtension.class)
class ProductoControllerPaginacionTest {

    /**
     * Servicio de productos simulado para aislar el controlador durante las pruebas.
     */
    @Mock
    private ProductoService service;

    /**
     * Cliente de pruebas MVC que permite ejecutar solicitudes HTTP simuladas contra el controlador.
     */
    private MockMvc mockMvc;

    /**
     * Configura Thymeleaf y MockMvc para probar el controlador sin iniciar un servidor web completo.
     */
    @BeforeEach
    void configurar() {
        FileTemplateResolver resolvedor = new FileTemplateResolver();
        resolvedor.setPrefix("src/main/resources/templates/");
        resolvedor.setSuffix(".html");
        resolvedor.setTemplateMode(TemplateMode.HTML);
        resolvedor.setCharacterEncoding("UTF-8");

        SpringTemplateEngine motor = new SpringTemplateEngine();
        motor.setTemplateResolver(resolvedor);

        ThymeleafViewResolver resolvedorVistas = new ThymeleafViewResolver();
        resolvedorVistas.setTemplateEngine(motor);
        resolvedorVistas.setCharacterEncoding("UTF-8");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductoController(service))
                .setViewResolvers(resolvedorVistas)
                .build();
    }

    /**
     * Verifica el tamaño de página predeterminado, los datos del modelo y los controles de navegación renderizados.
     */
    @Test
    void listarUsaTamanoCincoPorDefectoYRenderizaControles() throws Exception {
        List<Producto> elementos = List.of(crearProducto(1L, "Teclado"), crearProducto(2L, "Mouse"));
        Page<Producto> pagina = new PageImpl<>(elementos, PageRequest.of(0, 5, Sort.by("id").ascending()), 42);
        when(service.listarPaginado(any(Pageable.class))).thenReturn(pagina);

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attribute("paginaActual", 1))
                .andExpect(model().attribute("tamanoPagina", 5))
                .andExpect(model().attribute("totalPaginas", 9))
                .andExpect(model().attribute("totalRegistros", 42L))
                .andExpect(content().string(containsString("Página 1 de 9")))
                .andExpect(content().string(containsString("First")))
                .andExpect(content().string(containsString("Previous")))
                .andExpect(content().string(containsString("Next")))
                .andExpect(content().string(containsString("Last")))
                .andExpect(content().string(containsString("&hellip;")));

        ArgumentCaptor<Pageable> captorPaginacion = ArgumentCaptor.forClass(Pageable.class);
        verify(service).listarPaginado(captorPaginacion.capture());
        assertThat(captorPaginacion.getValue().getPageNumber()).isZero();
        assertThat(captorPaginacion.getValue().getPageSize()).isEqualTo(5);
    }

    /**
     * Verifica que el controlador limita los tamaños de página personalizados al intervalo permitido.
     */
    @Test
    void listarLimitaTamanoPersonalizadoEntreUnoYCien() throws Exception {
        Page<Producto> paginaVacia = new PageImpl<>(List.of(), PageRequest.of(0, 100), 0);
        when(service.listarPaginado(any(Pageable.class))).thenReturn(paginaVacia);

        mockMvc.perform(get("/productos").param("size", "250"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tamanoPagina", 100));

        mockMvc.perform(get("/productos").param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tamanoPagina", 1));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service, times(2)).listarPaginado(captor.capture());
        assertThat(captor.getAllValues().get(0).getPageSize()).isEqualTo(100);
        assertThat(captor.getAllValues().get(1).getPageSize()).isEqualTo(1);
    }

    /**
     * Verifica que se conserva el filtro de búsqueda y que una página fuera de rango se ajusta a la última página disponible.
     */
    @Test
    void listarConservaFiltroYAjustaPaginaFueraDeRango() throws Exception {
        Page<Producto> paginaDiezVacia = new PageImpl<>(List.of(), PageRequest.of(9, 10), 25);
        Page<Producto> ultimaPaginaValida = new PageImpl<>(List.of(crearProducto(21L, "Laptop")), PageRequest.of(2, 10), 25);

        when(service.buscarPorNombrePaginado(eq("Laptop"), any(Pageable.class)))
                .thenReturn(paginaDiezVacia)
                .thenReturn(ultimaPaginaValida);

        mockMvc.perform(get("/productos")
                        .param("nombre", " Laptop ")
                        .param("page", "10")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paginaActual", 3))
                .andExpect(model().attribute("totalPaginas", 3))
                .andExpect(model().attribute("nombreBuscado", "Laptop"))
                .andExpect(content().string(containsString("Página 3 de 3")));

        verify(service, times(2)).buscarPorNombrePaginado(eq("Laptop"), any(Pageable.class));
    }

    /**
     * Verifica que la eliminación individual conserva página, tamaño y filtro en la URL de redirección.
     */
    @Test
    void eliminarConservaEstadoDePaginacionAlRedirigir() throws Exception {
        when(service.eliminar(5L)).thenReturn(true);

        mockMvc.perform(post("/productos/eliminar/5")
                        .param("page", "2")
                        .param("size", "15")
                        .param("nombre", "Monitor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos?page=2&size=15&nombre=Monitor"));
    }

    /**
     * Crea un producto de prueba con los datos proporcionados.
     * @param id identificador asignado al producto de prueba.
     * @param nombre nombre del producto de prueba.
     * @return producto preparado para las pruebas.
     */
    private Producto crearProducto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setDescripcion("Descripción " + nombre);
        producto.setPrecio(100.0);
        producto.setStock(10);
        return producto;
    }
}