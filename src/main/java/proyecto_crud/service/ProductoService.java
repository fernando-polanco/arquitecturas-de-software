package proyecto_crud.service;

import java.util.List;
import java.util.Optional;

import proyecto_crud.model.Producto;

public interface ProductoService {

    List<Producto> listar();

    List<Producto> buscarPorNombre(String nombre);

    Optional<Producto> buscarPorId(Long id);

    Producto guardar(Producto producto);

    boolean eliminar(Long id);

    int eliminarVarios(List<Long> ids);
}
