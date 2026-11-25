package proyecto_crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_crud.model.Producto;

// Extendemos JpaRepository por que Spring nos da las funciones del CRUD ya hechas
// de crear, leer, actualizar y eliminar sin escribir consultas de SQL.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
