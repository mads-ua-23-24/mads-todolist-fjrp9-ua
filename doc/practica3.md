# PRÁCTICA 3 - MADS - Francisco José Ramírez Paraíso
## Pantalla de la base de datos PostgreSQL.

En la primera imagen se puede ver la pantalla de la base de datos PostgreSQL con las tablas creadas y sin datos:

![1.JPG](img%2F1.JPG)

En esta segunda imagen, se puede ver la pantalla de la base de datos PostgreSQL con las tablas creadas y con datos:

![2.JPG](img%2F2.JPG)

Los datos de la segunda imgen son los que se han ido guardando de una prueba de funcionamiento de la aplicación web.

## Historias de usuario:
### 009 Gestionar pertenencia al equipo

En el modelo **Equipo** he añadido el método  **`delUsuario()`** que recibe como parámetro un objeto de la clase **Usuario** y lo elimina de la lista de usuarios del equipo. También elimina de la lista de equipos del usuario el equipo en el que se ha ejecutado el método. Para probar este nuevo método he creado un test en **EquipoTest** que dado un equipo y un usuario perteneciente al equipo, elimina al usuario del equipo y verifica su correcta eliminación.

En cuanto a la capa de servicio, he añadido el método **`eliminarUsuarioDeEquipo()`** en **EquipoService** que recibe como parámetro el id del equipo y el id del usuario para eliminar al usuario del equipo haciendo uso del método anterior. Antes de realizar la eliminación, compruebo la existencia del equipo y del usuario y compruebo que el usuario pertenece al equipo. Si no se cumple alguna de estas condiciones, lanzo la excepción **EquipoServiceException** con un mensaje de error. Para probar este nuevo método he creado un test en **EquipoServiceTest** que dado un equipo y un usuario perteneciente al equipo, elimina al usuario del equipo y verifica su correcta eliminación.

En cuanto a la capa de controlador, he añadido cuatro métodos en **EquipoController**:

- **`crearEquipo()`**, con la ruta __`equipos/crear`__ mapeada en un GET. 
- **`crearEquipoSubmit()`**, con la ruta __`equipos/crear`__ mapeada en un POST.
- **`añadirUsuarioAEquipo()`**, con la ruta __`equipos/{id}/añadirUsuario`__ mapeada en un POST.
- **`eliminarUsuarioDeEquipo()`**, con la ruta __`equipos/{id}/eliminarUsuario`__ mapeada en un POST.



### 010 Gestión de equipos