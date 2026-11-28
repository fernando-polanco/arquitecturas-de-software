package proyecto_crud.controller;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de {@link PaginacionUtil} para validar parámetros y la construcción de elementos de paginación.
 */
class PaginacionUtilTest {

    /**
     * Comprueba que se usa el tamaño predeterminado cuando el parámetro falta, está vacío o no es numérico.
     */
    @Test
    void obtenerTamanoPaginaUsaCincoPorDefectoSiFaltaOEsInvalido() {
        assertThat(PaginacionUtil.obtenerTamanoPagina(null)).isEqualTo(5);
        assertThat(PaginacionUtil.obtenerTamanoPagina("")).isEqualTo(5);
        assertThat(PaginacionUtil.obtenerTamanoPagina("abc")).isEqualTo(5);
    }

    /**
     * Comprueba que el tamaño de página queda limitado al intervalo de uno a cien.
     */
    @Test
    void obtenerTamanoPaginaLimitaEntreUnoYCien() {
        assertThat(PaginacionUtil.obtenerTamanoPagina("0")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerTamanoPagina("-15")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerTamanoPagina("1")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerTamanoPagina("25")).isEqualTo(25);
        assertThat(PaginacionUtil.obtenerTamanoPagina("100")).isEqualTo(100);
        assertThat(PaginacionUtil.obtenerTamanoPagina("101")).isEqualTo(100);
        assertThat(PaginacionUtil.obtenerTamanoPagina("999")).isEqualTo(100);
    }

    /**
     * Comprueba que los números de página nulos, vacíos, no numéricos o no positivos se sustituyen por uno.
     */
    @Test
    void obtenerNumeroPaginaUsaUnoPorDefectoYRechazaNoPositivos() {
        assertThat(PaginacionUtil.obtenerNumeroPagina(null)).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("  ")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("xyz")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("0")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("-4")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("7")).isEqualTo(7);
    }

    /**
     * Comprueba que no se generan elementos de navegación cuando no hay páginas.
     */
    @Test
    void construirPaginasConElipsisDevuelveVacioCuandoHayCeroPaginas() {
        assertThat(PaginacionUtil.construirPaginasConElipsis(1, 0)).isEmpty();
    }

    /**
     * Comprueba que se muestran todos los números cuando hay cinco páginas o menos.
     */
    @Test
    void construirPaginasConElipsisMuestraTodasSiHayCincoOMenos() {
        List<ElementoPagina> elementos = PaginacionUtil.construirPaginasConElipsis(3, 5);
        assertThat(elementos).containsExactly(
                ElementoPagina.pagina(1, false),
                ElementoPagina.pagina(2, false),
                ElementoPagina.pagina(3, true),
                ElementoPagina.pagina(4, false),
                ElementoPagina.pagina(5, false)
        );
    }

    /**
     * Comprueba la ventana de páginas cuando la página actual está al inicio de una lista extensa.
     */
    @Test
    void construirPaginasConElipsisMuestraVentanaInicialConMaximoCincoPaginas() {
        List<ElementoPagina> elementos = PaginacionUtil.construirPaginasConElipsis(2, 10);
        assertThat(elementos).containsExactly(
                ElementoPagina.pagina(1, false),
                ElementoPagina.pagina(2, true),
                ElementoPagina.pagina(3, false),
                ElementoPagina.pagina(4, false),
                ElementoPagina.elementoElipsis(),
                ElementoPagina.pagina(10, false)
        );
    }

    /**
     * Comprueba la ventana de páginas y las elipsis cuando la página actual está en una posición intermedia.
     */
    @Test
    void construirPaginasConElipsisMuestraVentanaIntermediaConMaximoCincoPaginas() {
        List<ElementoPagina> elementos = PaginacionUtil.construirPaginasConElipsis(5, 10);
        assertThat(elementos).containsExactly(
                ElementoPagina.pagina(1, false),
                ElementoPagina.elementoElipsis(),
                ElementoPagina.pagina(4, false),
                ElementoPagina.pagina(5, true),
                ElementoPagina.pagina(6, false),
                ElementoPagina.elementoElipsis(),
                ElementoPagina.pagina(10, false)
        );
    }

    /**
     * Comprueba la ventana de páginas cuando la página actual está cerca del final.
     */
    @Test
    void construirPaginasConElipsisMuestraVentanaFinalConMaximoCincoPaginas() {
        List<ElementoPagina> elementos = PaginacionUtil.construirPaginasConElipsis(9, 10);
        assertThat(elementos).containsExactly(
                ElementoPagina.pagina(1, false),
                ElementoPagina.elementoElipsis(),
                ElementoPagina.pagina(7, false),
                ElementoPagina.pagina(8, false),
                ElementoPagina.pagina(9, true),
                ElementoPagina.pagina(10, false)
        );
    }

    /**
     * Comprueba que la navegación nunca muestra más de cinco números de página y marca exactamente uno como activo.
     */
    @Test
    void construirPaginasConElipsisNuncaSuperaCincoPaginasVisibles() {
        for (int total = 1; total <= 20; total++) {
            for (int actual = 1; actual <= total; actual++) {
                List<ElementoPagina> elementos = PaginacionUtil.construirPaginasConElipsis(actual, total);
                List<ElementoPagina> numericos = elementos.stream().filter(e -> !e.elipsis()).toList();
                assertThat(numericos.size()).isLessThanOrEqualTo(5);
                assertThat(numericos.stream().filter(ElementoPagina::activo).count()).isEqualTo(1);
            }
        }
    }
}