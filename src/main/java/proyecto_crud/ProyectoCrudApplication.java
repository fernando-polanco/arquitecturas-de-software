package proyecto_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación CRUD de productos. Configura y arranca el contexto de Spring Boot.
 */
@SpringBootApplication
public class ProyectoCrudApplication {

	/**
	 * Inicia la aplicación Spring Boot y el contenedor de componentes de la aplicación.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProyectoCrudApplication.class, args);
	}

}
