package proyecto_crud.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import proyecto_crud.exception.ProductoNoEncontradoException;
import proyecto_crud.model.Producto;
import proyecto_crud.repository.ProductoRepository;

// @Service para que Spring cree esta clase y la deja disponible para el controlador
@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;

    // Spring entrega el repositorio automáticamente por el constructor
    public ProductoServiceImpl(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Producto> listarPaginado(Pageable paginacion) {
        return repository.findAll(paginacion);
    }

    @Override
    public Page<Producto> buscarPorNombrePaginado(String nombre, Pageable paginacion) {
        return repository.findByNombreContainingIgnoreCase(nombre, paginacion);
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @Override
    public Producto guardar(Producto producto) {
        return repository.save(producto);
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

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
