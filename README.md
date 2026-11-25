# Arquitecturas de Software

Proyecto final de la materia **Arquitecturas de Software**.

Desarrollo de una aplicación web CRUD (Create, Read, Update, Delete) utilizando Spring Boot, Java y MySQL.

## Integrantes

| Basto | Basulto | Osorno | Polanco |
| :---: | :---: | :---: | :---: |
| <img src="Fotos/JoelBasto.jpeg" width="80" height="80" style="border-radius: 50%; object-fit: cover;" alt="Foto Basto"> | <img src="Fotos/JoseLuisBasulto.jpeg" width="80" height="80" style="border-radius: 50%; object-fit: cover;" alt="Foto Basulto"> | <img src="Fotos/AlbertoOsorno.jpeg" width="80" height="80" style="border-radius: 50%; object-fit: cover;" alt="Foto Osorno"> | <img src="Fotos/FernandoPolanco.jpeg" width="80" height="80" style="border-radius: 50%; object-fit: cover;" alt="Foto Polanco"> |

---

## Tecnologías utilizadas

- Java 21
- Spring Boot 4.2.0
- Maven
- Spring Web
- Spring Data JPA
- Thymeleaf
- MySQL 8.0
- Hibernate
- MySQL Connector/J

## Requisitos previos

Antes de ejecutar el proyecto, es necesario tener instalado:

- Java JDK 21.
- MySQL Server 8.0 o compatible.
- Git.

No es necesario instalar Maven, ya que el proyecto incluye Maven Wrapper.

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone URL_DEL_REPOSITORIO
cd arquitecturas-de-software
```

## Proceso de configuración y ejecución en Linux

### 2. Configurar MySQL

Iniciar el servicio de MySQL:

```bash
sudo systemctl start mysql
```

Acceder a MySQL:

```bash
sudo mysql -u root
```

Crear la base de datos:

```sql
CREATE DATABASE IF NOT EXISTS proyecto_crud;
```

Crear un usuario exclusivo para la aplicación:

```sql
CREATE USER 'app_crud'@'localhost'
IDENTIFIED BY 'TU_CONTRASENA';
```

Conceder permisos sobre la base de datos:

```sql
GRANT ALL PRIVILEGES ON proyecto_crud.*
TO 'app_crud'@'localhost';
```

Salir de MySQL:

```sql
EXIT;
```

**Nota:** Cada integrante debe crear su propio usuario y establecer una contraseña local.

### 3. Configurar las variables de entorno

La aplicación utiliza variables de entorno para evitar almacenar credenciales directamente en el repositorio.

En Ubuntu, ejecutar:

```bash
export DB_USERNAME=app_crud
export DB_PASSWORD='TU_CONTRASENA'
```

Sustituir `TU_CONTRASENA` por la contraseña configurada anteriormente.

Estas variables deben estar disponibles en el entorno desde el que se ejecutará Spring Boot.

### 4. Ejecutar el proyecto

Desde la raíz del repositorio, ejecutar:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Nota: Maven descargará automáticamente las dependencias necesarias e iniciará la aplicación.

## Configuración y ejecución en Windows

### 1. Configurar MySQL

Desde la terminal integrada del sistema en Intellij ejecutar:

```bash
Get-Service -Name *mysql*
Start-Service -Name MySQL80
```

Nota: ajustar el nombre si tu instalación usa `MySQL` o `MySQL84` en lugar de `MySQL80`.

Conéctate a MySQL como root:

```bash
mysql -u root -p
```

Dentro de la consola mysql>, crea la base de datos proyecto_crud, el usuario app_crud y asígnale privilegios (sustituye TU_CONTRASENA por tu contraseña local):

```bash
CREATE DATABASE IF NOT EXISTS proyecto_crud;
CREATE USER IF NOT EXISTS 'app_crud'@'localhost' IDENTIFIED BY 'TU_CONTRASENA';
GRANT ALL PRIVILEGES ON proyecto_crud.* TO 'app_crud'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Nota: deben aparecer mensajes de `Ok` para confirmar que todo salió bien.

### 2. Configurar las variables de entorno

```bash
$env:DB_USERNAME="app_crud"
$env:DB_PASSWORD="TU_CONTRASENA"
```

Nota: sustituir `TU_CONTRASENA` por la contraseña configurada anteriormente.

### 3. Ejecutar el proyecto

Inicia la aplicación Spring Boot desde la raíz del repositorio:

```bash
.\mvnw.cmd spring-boot:run
```

Nota: Maven descargará automáticamente las dependencias necesarias e iniciará la aplicación.

## Acceder a la aplicación

Una vez iniciada correctamente, acceder desde el navegador:

http://localhost:8080

---

## Estructura del proyecto

```text
arquitecturas-de-software/
│
├── Fotos/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   │
│   └── test/
│
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
└── README.md
```

## Configuración de la base de datos

La aplicación utiliza MySQL como sistema gestor de bases de datos y Spring Data JPA para la persistencia.

Hibernate se encarga de generar y actualizar las tablas a partir de las entidades Java durante el desarrollo.

La configuración de conexión se encuentra en:

```text
src/main/resources/application.properties
```

Las credenciales se proporcionan mediante las variables de entorno `DB_USERNAME` y `DB_PASSWORD`.

## Consideraciones para el desarrollo

- No subir contraseñas ni credenciales personales al repositorio.
- No subir archivos compilados ni carpetas generadas por el IDE.
- Mantener actualizadas las dependencias en `pom.xml`.
- Trabajar preferentemente mediante ramas de Git para desarrollar nuevas funcionalidades.
- Realizar commits descriptivos que permitan identificar los cambios realizados.