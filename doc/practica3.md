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

```java
public void delUsuario(Usuario usuario){
    //Si el usuario no está en la lista, no lo eliminamos
    if (!this.getUsuarios().contains(usuario)) return;
    //Eliminamos el usuario de la lista de usuarios del equipo
    this.getUsuarios().remove(usuario);
    //Eliminamos la relación inversa del equipo en el usuario
    if (usuario.getEquipos().contains(this)) {
        usuario.getEquipos().remove(this);
    }
}
```

En cuanto a la capa de servicio, he añadido el método **`eliminarUsuarioDeEquipo()`** en **EquipoService** que recibe como parámetro el id del equipo y el id del usuario para eliminar al usuario del equipo haciendo uso del método anterior. Antes de realizar la eliminación, compruebo la existencia del equipo y del usuario y compruebo que el usuario pertenece al equipo. Si no se cumple alguna de estas condiciones, lanzo la excepción **EquipoServiceException** con un mensaje de error. Para probar este nuevo método he creado un test en **EquipoServiceTest** que dado un equipo y un usuario perteneciente al equipo, elimina al usuario del equipo y verifica su correcta eliminación.

```java
@Transactional
public void eliminarUsuarioDeEquipo(Long id, Long id1) {
    Equipo equipo = equipoRepository.findById(id).orElse(null);
    Usuario usuario = usuarioRepository.findById(id1).orElse(null);
    if (equipo == null) throw new EquipoServiceException("No existe el equipo con id " + id);
    else if (usuario == null) throw new UsuarioServiceException("No existe el usuario con id " + id1);
    else if (!equipo.getUsuarios().contains(usuario))
        throw new EquipoServiceException("El usuario no pertenece al equipo");
    equipo.delUsuario(usuario);
}
```

En cuanto a la capa de controlador, he añadido cuatro métodos en **EquipoController**:

- **`crearEquipo()`**, con la ruta __`equipos/crear`__ mapeada en un **GET**. Este método devuelve la vista **crearEquipo.html** con el formulario para crear equipo.
- **`crearEquipoSubmit()`**, con la ruta __`equipos/crear`__ mapeada en un **POST**. Este método recibe los datos del formulario en un objeto **EquipoData** y crea el equipo haciendo uso del método **`crearEquipo()`** de **EquipoService**. Si se produce algún error, se muestra la vista **crearEquipo.html** con el mensaje de error.
- **`añadirUsuarioAEquipo()`**, con la ruta __`equipos/{id}/añadirUsuario`__ mapeada en un **POST**. Este método recibe en la url el id del equipo para añadir al usuario que está logeado y se quiere unir a ese equipo. Para ello, hace uso del método **`añadirUsuarioAEquipo()`** de **EquipoService**. Si se produce algún error, se muestra la vista **listaEquipos.html** con el mensaje de error.
- **`eliminarUsuarioDeEquipo()`**, con la ruta __`equipos/{id}/eliminarUsuario`__ mapeada en un **POST**. ESte método recibe en la url el id del equipo para eliminar al usuario que está logeado y se quiere salir de ese equipo. Para ello, hace uso del método **`eliminarUsuarioDeEquipo()`** de **EquipoService**. Si se produce algún error, se muestra la vista **listaEquipos.html** con el mensaje de error.

Finalemte, he modificado la vista **listaEquipos.html** para que muestre los botones de unirse a un equipo y salir de un equipo. También he añadido el botón de crear equipo, creando así la vista **formCrearEquipo.html**.



### 010 Gestión de equipos