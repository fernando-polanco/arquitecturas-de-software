package proyecto_crud.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_crud.model.Producto;

// Extendemos JpaRepository porque Spring nos da las funciones del CRUD ya hechas
// de crear, leer, actualizar y eliminar sin escribir consultas de SQL.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    // Línea que se traduce a SELECT * FROM producto WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%'));

    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
