package proyecto_crud.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto_crud.model.Producto;

// Extendemos JpaRepository porque Spring nos da las funciones del CRUD ya hechas
// de crear, leer, actualizar y eliminar sin escribir consultas de SQL.
/**
 * Repositorio de acceso a datos para la entidad {@link proyecto_crud.model.Producto}. Hereda las operaciones CRUD y paginación de Spring Data JPA.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    /**
     * Busca productos cuyo nombre contenga el texto indicado, sin distinguir mayúsculas y minúsculas, y devuelve los resultados paginados.
     * @param nombre fragmento de nombre que se desea buscar.
     * @param paginacion configuración de página, tamaño y orden de los resultados.
     * @return página con los productos coincidentes.
     */
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable paginacion);
}
