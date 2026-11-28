package proyecto_crud.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import proyecto_crud.exception.ProductoNoEncontradoException;
import proyecto_crud.model.Producto;
import proyecto_crud.repository.ProductoRepository;

// @Service para que Spring cree esta clase y la deja disponible para el controlador
/**
 * Implementación de {@link ProductoService}; coordina las operaciones de negocio utilizando {@link proyecto_crud.repository.ProductoRepository}.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    /**
     * Repositorio utilizado para consultar y modificar los registros de productos.
     */
    private final ProductoRepository repository;

    // Spring entrega el repositorio automáticamente por el constructor
    /**
     * Construye el servicio con el repositorio requerido.
     * @param repository repositorio de productos proporcionado por inyección de dependencias.
     */
    public ProductoServiceImpl(ProductoRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene todos los productos en páginas delegando la consulta al repositorio.
     * @param paginacion configuración de página, tamaño y orden.
     * @return página de productos.
     */
    @Override
    public Page<Producto> listarPaginado(Pageable paginacion) {
        return repository.findAll(paginacion);
    }

    /**
     * Realiza una búsqueda paginada por nombre sin distinguir mayúsculas y minúsculas.
     * @param nombre texto que debe contener el nombre.
     * @param paginacion configuración de página, tamaño y orden.
     * @return página de productos coincidentes.
     */
    @Override
    public Page<Producto> buscarPorNombrePaginado(String nombre, Pageable paginacion) {
        return repository.findByNombreContainingIgnoreCase(nombre, paginacion);
    }

    /**
     * Busca un producto por su identificador y lanza una excepción si no existe.
     * @param id identificador del producto.
     * @return producto encontrado.
     * @throws ProductoNoEncontradoException cuando el identificador no corresponde a un producto.
     */
    @Override
    public Producto obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    /**
     * Persiste un producto nuevo o actualizado.
     * @param producto entidad que se desea guardar.
     * @return entidad guardada.
     */
    @Override
    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    /**
     * Comprueba la existencia del producto y, si existe, lo elimina.
     * @param id identificador del producto.
     * @return {@code true} cuando se elimina; {@code false} cuando no existe.
     */
    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    /**
     * Busca y elimina los productos existentes entre los identificadores recibidos.
     * @param ids lista de identificadores solicitados.
     * @return cantidad de registros eliminados.
     */
    @Override
    public int eliminarVarios(List<Long> ids) {
        // Si no llega ninguno no se borra
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        // Solo borra los que existen
        List<Producto> encontrados = repository.findAllById(ids);
        repository.deleteAll(encontrados);
        return encontrados.size();
    }
}
