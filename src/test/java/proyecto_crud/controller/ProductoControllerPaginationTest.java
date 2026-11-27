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

@ExtendWith(MockitoExtension.class)
class ProductoControllerPaginationTest {

    @Mock
    private ProductoService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(engine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductoController(service))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void listarUsesDefaultPageSizeFiveAndRendersControls() throws Exception {
        List<Producto> items = List.of(producto(1L, "Teclado"), producto(2L, "Mouse"));
        Page<Producto> page = new PageImpl<>(items, PageRequest.of(0, 5, Sort.by("id").ascending()), 42);
        when(service.listarPaginado(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("productos/lista"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("pageSize", 5))
                .andExpect(model().attribute("totalPages", 9))
                .andExpect(model().attribute("totalElements", 42L))
                .andExpect(content().string(containsString("Página 1 de 9")))
                .andExpect(content().string(containsString("First")))
                .andExpect(content().string(containsString("Previous")))
                .andExpect(content().string(containsString("Next")))
                .andExpect(content().string(containsString("Last")))
                .andExpect(content().string(containsString("&hellip;")));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(service).listarPaginado(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isZero();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
    }

    @Test
    void listarClampsCustomPageSizeWithinOneAndHundred() throws Exception {
        Page<Producto> pageMax = new PageImpl<>(List.of(), PageRequest.of(0, 100), 0);
        when(service.listarPaginado(any(Pageable.class))).thenReturn(pageMax);

        mockMvc.perform(get("/productos").param("size", "250"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageSize", 100));

        mockMvc.perform(get("/productos").param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pageSize", 1));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(service, times(2)).listarPaginado(captor.capture());
        assertThat(captor.getAllValues().get(0).getPageSize()).isEqualTo(100);
        assertThat(captor.getAllValues().get(1).getPageSize()).isEqualTo(1);
    }

    @Test
    void listarPreservesSearchFilterAndAdjustsOutOfRangePage() throws Exception {
        Page<Producto> emptyPage10 = new PageImpl<>(List.of(), PageRequest.of(9, 10), 25);
        Page<Producto> lastValidPage = new PageImpl<>(List.of(producto(21L, "Laptop")), PageRequest.of(2, 10), 25);

        when(service.buscarPorNombrePaginado(eq("Laptop"), any(Pageable.class)))
                .thenReturn(emptyPage10)
                .thenReturn(lastValidPage);

        mockMvc.perform(get("/productos")
                        .param("nombre", " Laptop ")
                        .param("page", "10")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("currentPage", 3))
                .andExpect(model().attribute("totalPages", 3))
                .andExpect(model().attribute("nombreBuscado", "Laptop"))
                .andExpect(content().string(containsString("Página 3 de 3")));

        verify(service, times(2)).buscarPorNombrePaginado(eq("Laptop"), any(Pageable.class));
    }

    @Test
    void eliminarPreservesPaginationStateOnRedirect() throws Exception {
        when(service.eliminar(5L)).thenReturn(true);

        mockMvc.perform(post("/productos/eliminar/5")
                        .param("page", "2")
                        .param("size", "15")
                        .param("nombre", "Monitor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos?page=2&size=15&nombre=Monitor"));
    }

    private Producto producto(Long id, String nombre) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setDescripcion("Descripción " + nombre);
        p.setPrecio(100.0);
        p.setStock(10);
        return p;
    }
}
