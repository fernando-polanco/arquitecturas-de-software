package proyecto_crud.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Proporciona funciones auxiliares y constantes para validar parámetros y construir la navegación de páginas.
 */
public final class PaginacionUtil {

    /**
     * Número de página utilizado cuando el parámetro recibido es nulo, vacío o inválido.
     */
    public static final int PAGINA_POR_DEFECTO = 1;

    /**
     * Cantidad de registros por página utilizada cuando el tamaño recibido es inválido.
     */
    public static final int TAMANO_POR_DEFECTO = 5;

    /**
     * Cantidad mínima de registros permitida por página.
     */
    public static final int TAMANO_MINIMO = 1;

    /**
     * Cantidad máxima de registros permitida por página.
     */
    public static final int TAMANO_MAXIMO = 100;

    /**
     * Máximo de números de página consecutivos que se muestran en la navegación.
     */
    public static final int MAX_PAGINAS_VISIBLES = 5;

    /**
     * Evita la instanciación de esta clase de utilidades.
     */
    private PaginacionUtil() {
    }

    /**
     * Convierte el texto recibido en un número de página válido; los valores nulos, vacíos, no numéricos o menores que uno usan el valor predeterminado.
     * @param paginaTexto texto del parámetro de página recibido desde la solicitud.
     * @return número de página válido, comenzando en uno.
     */
    public static int obtenerNumeroPagina(String paginaTexto) {
        if (paginaTexto == null || paginaTexto.isBlank()) {
            return PAGINA_POR_DEFECTO;
        }
        try {
            int numero = Integer.parseInt(paginaTexto.trim());
            return Math.max(PAGINA_POR_DEFECTO, numero);
        } catch (NumberFormatException ex) {
            return PAGINA_POR_DEFECTO;
        }
    }

    /**
     * Convierte el texto recibido en un tamaño de página y lo limita al intervalo permitido.
     * @param tamanoTexto texto del tamaño de página recibido desde la solicitud.
     * @return tamaño de página entre {@link #TAMANO_MINIMO} y {@link #TAMANO_MAXIMO}, o el valor predeterminado si no puede interpretarse.
     */
    public static int obtenerTamanoPagina(String tamanoTexto) {
        if (tamanoTexto == null || tamanoTexto.isBlank()) {
            return TAMANO_POR_DEFECTO;
        }
        try {
            int tamano = Integer.parseInt(tamanoTexto.trim());
            return Math.clamp(tamano, TAMANO_MINIMO, TAMANO_MAXIMO);
        } catch (NumberFormatException ex) {
            return TAMANO_POR_DEFECTO;
        }
    }

    /**
     * Construye los elementos numéricos y las elipsis que se mostrarán en la navegación de páginas.
     * @param paginaActual número de la página seleccionada.
     * @param totalPaginas cantidad total de páginas disponibles.
     * @return lista ordenada de elementos de paginación; queda vacía si no existen páginas.
     */
    public static List<ElementoPagina> construirPaginasConElipsis(int paginaActual, int totalPaginas) {
        if (totalPaginas <= 0) {
            return List.of();
        }

        int paginaSegura = Math.max(1, Math.min(paginaActual, totalPaginas));
        List<ElementoPagina> elementos = new ArrayList<>();

        if (totalPaginas <= MAX_PAGINAS_VISIBLES) {
            for (int i = 1; i <= totalPaginas; i++) {
                elementos.add(ElementoPagina.pagina(i, i == paginaSegura));
            }
            return elementos;
        }

        if (paginaSegura <= 3) {
            for (int i = 1; i <= 4; i++) {
                elementos.add(ElementoPagina.pagina(i, i == paginaSegura));
            }
            elementos.add(ElementoPagina.elementoElipsis());
            elementos.add(ElementoPagina.pagina(totalPaginas, false));
        } else if (paginaSegura >= totalPaginas - 2) {
            elementos.add(ElementoPagina.pagina(1, false));
            elementos.add(ElementoPagina.elementoElipsis());
            for (int i = totalPaginas - 3; i <= totalPaginas; i++) {
                elementos.add(ElementoPagina.pagina(i, i == paginaSegura));
            }
        } else {
            elementos.add(ElementoPagina.pagina(1, false));
            elementos.add(ElementoPagina.elementoElipsis());
            for (int i = paginaSegura - 1; i <= paginaSegura + 1; i++) {
                elementos.add(ElementoPagina.pagina(i, i == paginaSegura));
            }
            elementos.add(ElementoPagina.elementoElipsis());
            elementos.add(ElementoPagina.pagina(totalPaginas, false));
        }

        return elementos;
    }
}
