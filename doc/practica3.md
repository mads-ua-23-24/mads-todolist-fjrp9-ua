# PRÁCTICA 3 - MADS - Francisco José Ramírez Paraíso
## Pantalla de la base de datos PostgreSQL.

Aquí tenemos el diagrama de la base de datos PostgreSQL:

![0.JPG](img%2F0.JPG)

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
- **`añadirUsuarioAEquipo()`**, con la ruta __`equipos/{id}/añadirUsuario`__ mapeada en un **POST**. Este método recibe en la url el id del equipo para añadir al usuario que está logeado y se quiere unir a ese equipo. Para ello, hace uso del método **`añadirUsuarioAEquipo()`** de **EquipoService**. Si se produce algún error, se redirecciona a __`/equipos`__ con el mensaje de error.
- **`eliminarUsuarioDeEquipo()`**, con la ruta __`equipos/{id}/eliminarUsuario`__ mapeada en un **POST**. ESte método recibe en la url el id del equipo para eliminar al usuario que está logeado y se quiere salir de ese equipo. Para ello, hace uso del método **`eliminarUsuarioDeEquipo()`** de **EquipoService**. Si se produce algún error, se redirecciona a __`/equipos`__ con el mensaje de error.

En cuanto a las vistas, he modificado la vista **listaEquipos.html** para que muestre los botones “Unirse“ de unirse a un equipo y “Salir“ para salir de un equipo. También he añadido el botón "Añadir nuevo Equipo", creando así la vista **formCrearEquipo.html**.

Finalmente, la capa de controlador y vistas las he testeado manualmente y con los siguientes test:

- Verificar que aparecen todos los botones nuevos (“Añadir nuevo Equipo“, “Unirse“, “Salir“) en Equipo.
- Verificar que se crea correctamente un equipo y se muestra en el listado.
- Verificar que un usuario puede unirse correctamente a un equipo.
- Verificar que un usuario puede salir correctamente de un equipo.
- Verificar que un usuario recibe un mensaje de error al intentar unirse a un grupo que ya está unido.
- Verificar que un usuario recibe un mensaje de error al intentar salirse de un grupo que no está.

Un ejemplo de test es el siguiete:

```java
@Test
public void crearEquipoCorrectamente() throws Exception {
    // GIVEN
    // Un usuario logeado
    when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());

    // WHEN
    // Creamos un equipo
    this.mockMvc.perform(post("/equipos/crear")
            .param("nombre", "EquipoAAA"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/equipos"));

    // THEN
    // Comprobamos que se ha creado correctamente
    this.mockMvc.perform(get("/equipos"))
            .andExpect(status().isOk())
            .andExpect(view().name("listaEquipos"))
            .andExpect(model().attributeExists("equipos"))
            .andExpect(model().attribute("equipos", hasSize(1)))
            .andExpect(model().attribute("equipos", hasItem(
                    allOf(
                            hasProperty("nombre", is("EquipoAAA"))
                    )
            )));
}
```

### 010 Gestión de equipos

En la capa de servicio, se ha añadido el método **`modificarEquipo()`** y **`borraEquipo()`** en **EquipoService**. El primero recibe como parámetro el id del equipo a modificar y el nuevo nombre de ese equipo y modifica el equipo con los nuevos datos. El segundo recibe como parámetro el id del equipo y lo elimina. Para probar estos nuevos métodos he creado cuatro test en **EquipoServiceTest** que dado un equipo, modifica el equipo y verifica su correcta modificación y dado un equipo, lo elimina y verifica su correcta eliminación. Los otros dos son para comprobar que se lanza la excepción **EquipoServiceException** cuando se intenta modificar o eliminar un equipo que no existe.

```java
@Transactional
public EquipoData modificarEquipo(Long id, String nombre) {
    Equipo equipo = equipoRepository.findById(id).orElse(null);
    if (equipo == null) throw new EquipoServiceException("No existe el equipo con id " + id);
    else if (nombre == null || nombre.isEmpty())
        throw new EquipoServiceException("El nombre del equipo no puede estar vacío");
    equipo.setNombre(nombre);
    equipo = equipoRepository.save(equipo);
    return modelMapper.map(equipo, EquipoData.class);
}

@Transactional
public void borraEquipo(Long id) {
    Equipo equipo = equipoRepository.findById(id).orElse(null);
    if (equipo == null) throw new EquipoServiceException("No existe el equipo con id " + id);
    equipoRepository.delete(equipo);
}
```

En cuanto a la capa de controlador, he añadido tres métodos en **EquipoController**:

- **`eliminarEquipo()`**, con la ruta __`/equipos/{id}`__ mapeada en un **DELETE**. Este método recibe en la url el id del equipo a eliminar y hace uso del método **`borraEquipo()`** de **EquipoService**.
- **`formEditaEquipo()`**, con la ruta __`/equipos/{id}/editar`__ mapeada en un **GET**. Este método recibe en la url el id del equipo a editar y devuelve la vista **formEditarEquipo.html** con el formulario para editar el equipo.
- **`grabaModificacion()`**, con la ruta __`/equipos/{id}/editar`__ mapeada en un **POST**. Este método recibe en la url el id del equipo a editar y recibe los datos del formulario en un objeto **EquipoData** y modifica el equipo haciendo uso del método **`modificarEquipo()`** de **EquipoService**. Si se produce algún error, se redirecciona a __`/equipos`__ con el mensaje de error.

Como para acceder a estos métodos se necesitan permisos de administrador, he añadido un método que comprueba si el usuario logeado es administrador o no.

```java
private void comprobarUsuarioAdministrador(Long idUsuario){

    if(idUsuario != null){
        boolean esAdmin  = usuarioService.esAdmin(idUsuario);
        if (!esAdmin){
            throw new UsuarioNoAdministradorException();
        }
    }else{
        throw new UsuarioNoAdministradorException();
    }
}
```

En cuanto a las vistas, he añadido la vista **formEditarEquipo.html** y modificado la vista **listaEquipos.html** para que muestre los botones “Editar“ y “Eliminar“ de editar y eliminar un equipo. Para que se muestren solo si eres administrador, he usado la equiqueta con el if de Thymeleaf:

```html
<td th:if="${usuarioPrincipal.esAdministrador}">
```

Finalmente, la capa de controlador y vistas las he testeado manualmente y con los siguientes test:

- Comprobar que siendo usuario administrador aparezcan los botones de editar y eliminar Equipo.
- Comprobar que siendo usuario normal no aparezcan los botones de editar y eliminar Equipo.
- Comprobar que se elimina un equipo correctamente.
- Comprobar que se modifica un equipo correctamente.