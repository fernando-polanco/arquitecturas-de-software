package proyecto_crud.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import proyecto_crud.model.Producto;

public interface ProductoService {

    List<Producto> listar();

    Page<Producto> listarPaginado(Pageable pageable);

    List<Producto> buscarPorNombre(String nombre);

    Page<Producto> buscarPorNombrePaginado(String nombre, Pageable pageable);

    Optional<Producto> buscarPorId(Long id);

    Producto guardar(Producto producto);

    boolean eliminar(Long id);

    int eliminarVarios(List<Long> ids);
}
