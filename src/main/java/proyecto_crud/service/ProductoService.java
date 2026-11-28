package proyecto_crud.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import proyecto_crud.model.Producto;

/**
 * Define los servicios de aplicación disponibles para consultar, registrar, actualizar y eliminar productos.
 */
public interface ProductoService {
    /**
     * Obtiene todos los productos en páginas.
     * @param paginacion configuración de página, tamaño y orden.
     * @return página de productos.
     */
    Page<Producto> listarPaginado(Pageable paginacion);

    /**
     * Busca productos por coincidencia parcial de nombre, sin distinguir mayúsculas y minúsculas.
     * @param nombre texto que debe contener el nombre del producto.
     * @param paginacion configuración de página, tamaño y orden.
     * @return página de productos coincidentes.
     */
    Page<Producto> buscarPorNombrePaginado(String nombre, Pageable paginacion);

    /**
     * Obtiene un producto por su identificador.
     * @param id identificador del producto.
     * @return producto encontrado.
     * @throws proyecto_crud.exception.ProductoNoEncontradoException si no existe un producto con ese identificador.
     */
    Producto obtenerPorId(Long id);

    /**
     * Guarda un producto nuevo o persiste los cambios de uno existente.
     * @param producto entidad que se desea guardar.
     * @return entidad persistida.
     */
    Producto guardar(Producto producto);

    /**
     * Elimina un producto si existe.
     * @param id identificador del producto.
     * @return {@code true} si se eliminó; {@code false} si no existía.
     */
    boolean eliminar(Long id);

    /**
     * Elimina todos los productos cuyos identificadores existan en la base de datos.
     * @param ids identificadores de los productos que se desean eliminar.
     * @return cantidad de productos que efectivamente fueron eliminados.
     */
    int eliminarVarios(List<Long> ids);
}
