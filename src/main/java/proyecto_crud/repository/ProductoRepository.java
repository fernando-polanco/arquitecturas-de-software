package proyecto_crud.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_crud.model.Producto;

// Extendemos JpaRepository porque Spring nos da las funciones del CRUD ya hechas
// de crear, leer, actualizar y eliminar sin escribir consultas de SQL.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable paginacion);
}
