package proyecto_crud.controller;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaginacionUtilTest {

    @Test
    void obtenerTamanoPaginaUsaCincoPorDefectoSiFaltaOEsInvalido() {
        assertThat(PaginacionUtil.obtenerTamanoPagina(null)).isEqualTo(5);
        assertThat(PaginacionUtil.obtenerTamanoPagina("")).isEqualTo(5);
        assertThat(PaginacionUtil.obtenerTamanoPagina("abc")).isEqualTo(5);
    }

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

    @Test
    void obtenerNumeroPaginaUsaUnoPorDefectoYRechazaNoPositivos() {
        assertThat(PaginacionUtil.obtenerNumeroPagina(null)).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("  ")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("xyz")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("0")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("-4")).isEqualTo(1);
        assertThat(PaginacionUtil.obtenerNumeroPagina("7")).isEqualTo(7);
    }

    @Test
    void construirPaginasConElipsisDevuelveVacioCuandoHayCeroPaginas() {
        assertThat(PaginacionUtil.construirPaginasConElipsis(1, 0)).isEmpty();
    }

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
