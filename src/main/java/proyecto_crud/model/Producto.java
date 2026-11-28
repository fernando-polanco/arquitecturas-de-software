package proyecto_crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// @Entity para que se tome como un tabla en MySql
/**
 * Entidad JPA que representa un producto almacenado en la base de datos y sus reglas básicas de validación.
 */
@Entity
public class Producto {

    // tomamos el id como llave primaria
    /**
     * Identificador único del producto; su valor es generado automáticamente por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto. No puede estar vacío.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Descripción del producto, obligatoria y limitada a 255 caracteres.
     */
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede pasar de 255 caracteres")
    private String descripcion;

    /**
     * Precio del producto. Es obligatorio y no puede ser negativo.
     */
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    /**
     * Cantidad disponible del producto. Es obligatoria y no puede ser negativa.
     */
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    /**
     * Obtiene el identificador único del producto.
     * @return identificador del producto.
     */
    public Long getId() { return id; }
    /**
     * Establece el identificador del producto.
     * @param id identificador que se asignará.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el nombre del producto.
     * @return nombre del producto.
     */
    public String getNombre() { return nombre; }
    /**
     * Establece el nombre del producto.
     * @param nombre nombre que se asignará.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene la descripción del producto.
     * @return descripción del producto.
     */
    public String getDescripcion() { return descripcion; }
    /**
     * Establece la descripción del producto.
     * @param descripcion descripción que se asignará.
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Obtiene el precio del producto.
     * @return precio del producto.
     */
    public Double getPrecio() { return precio; }
    /**
     * Establece el precio del producto.
     * @param precio precio que se asignará.
     */
    public void setPrecio(Double precio) { this.precio = precio; }

    /**
     * Obtiene la cantidad disponible del producto.
     * @return cantidad en existencia.
     */
    public Integer getStock() { return stock; }
    /**
     * Establece la cantidad disponible del producto.
     * @param stock cantidad que se asignará.
     */
    public void setStock(Integer stock) { this.stock = stock; }
}
