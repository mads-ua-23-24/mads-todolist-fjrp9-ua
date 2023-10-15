# PRÁCTICA 2 - MADS - Francisco José Ramírez Paraíso
## Listado de nuevas clases y métodos implementados.
### Capa de controlador.

Se han añadido las clases **HomeController.java**, **UsuarioController.java** y **UsuarioNoAdministradorException.java**.

- **HomeController.java**, sus métodos añadidos son:
    - **`about()`**: Está mapeado a la ruta `/about`. Su función principal es la de gestionar la información de la página _Acerca De_.
    
        Verifica si el usuario que realiza la petición está logeado mediante la función `managerUserSession.usuarioLogeado()`. Si no está logeado, establece los atributos "logeado", "usuario" y "administrador" como falsos en el modelo. En caso de estar logeado, recupera el id del usuario con la función anterior y utiliza la función `findById()` para recuperar toda la información de este. Además, determina si el usuario es un administrador mediante el método `esAdmin()` de **UsuarioService** y establece el atributo "administrador" en consecuencia. Todo esto sirve para poder poblar la barra de menú. 

        Devuelve la vista **`about.html`**.
- **UsuarioController.java**, sus métodos añadidos son:
    - **`listadoUsuarios()`**: Está mapeado a la ruta `/registrados`. Su función principal es la de devolver los usuarios que estén registrados en el sistema.

        Utiliza la función `comprobarUsuarioAdministrador()` para verificar si el usuario que realiza la petición es un usuario administrador. Posteriormente, utiliza la función `allUsuarios()` de **UsuarioService** para recuperar todos los usuarios registrados y los añade al modelo con el nombre "usuarios".

        Además, establece el atributo "logeado" como verdadero y obtiene información detallada del usuario actual mediante la función `findById()` de **UsuarioService**, utilizando el ID obtenido a través de `managerUserSession.usuarioLogeado()`. La información del usuario se agrega al modelo con el nombre "usuarioPrincipal".

        Devuelve la vista **`listaUsuarios.html`**.

    - **`descripcionUsuario()`**: Está mapeado a la ruta `/registrados/{id}`. Su función principal es la de mostrar la descripción de un usuario específico. Utiliza `comprobarUsuarioAdministrador()` para validar los privilegios, luego agrega la información del usuario al modelo usando `findById()`. 
    
        Devuelve la vista **`descripcionUsuario.html`**. 


    - **`bloqueaUsuario()`**: Está mapeado a la ruta `/usuarios/{id}/bloquear`. Su función principal es la de bloquear al usuario con el id especificado. Utiliza `bloquearUsuario()` de **UsuarioService** para bloquear al usuario y muestra un mensaje correspondiente a través de flash.  
    
        Redirige a la página de lista de usuarios mediante `/registrados`.

    - **`desbloqueaUsuario()`**: Está mapeado a la ruta `/usuarios/{id}/desbloquear`. Su función es la de desbloquear al usuario con el id proporcionado. Utiliza `desbloquearUsuario()` de **UsuarioService** para desbloquear al usuario y muestra un mensaje correspondiente a través de flash. 
    
        Redirige a la página de lista de usuarios mediante `/registrados`.

- **UsuarioNoAdministradorException.java** representa un caso en el que un usuario intenta acceder a funciones restringidas para administradores. La anotación @ResponseStatus configura la respuesta HTTP 401 (No autorizado) cuando se lanza **UsuarioNoAdministradorException**, con el mensaje "No autorizado".

### Capa de servicio.
En esta capa se han añadido a **UsuarioService.java** los siguientes métodos:

- **`allUsuarios()`**: Recupera todos los usuarios registrados en la base de datos mediante el método `findAll()` de **UsuarioRepository**. Mapea los objetos Usuario a objetos UsuarioData utilizando `modelMapper.map(usuario, UsuarioData.class)`.

    Devuelve una lista de objetos UsuarioData que contiene la información de todos los usuarios registrados en el sistema.

- **`existeAdmin()`**: Verifica si existe al menos un administrador en el sistema. Utiliza `countUsuariosAdministradores()` de **UsuarioRepository** para contar el número de usuarios que son administradores. Si el resultado obtenido es igual a 1, devuelve true. Si es distinto de 1, devuelve false.

- **`esAdmin()`**: Determina si un usario es usuario administrador o no. Para ello, obtiene el usuario mediante `usuarioRepository.findById(usuarioId).orElse(null)` con el id recibido. En caso de ser nulo el objeto obtenido o que reciba un false usando `usario.getEsAdministrador()` devuelve false. En caso contrario, devuelve false.

- **`bloquearUsuario()`**: Obtiene el usuario que se desea bloquear mediante `findById(usuarioId)` para bloquearlo con `usuarioRepository.updateUsuarioBloqueo(true, usuario.getEmail())`. Usa la función `estaBloqueado()` pasandole el email del usuario para verificar si se ha bloqueado correctamente o no, devolviendo true o false respectivamente.

- **`desbloquearUsuario()`**: Obtiene el usuario que se desea desbloquear mediante `findById(usuarioId)` para desbloquearlo con `usuarioRepository.updateUsuarioBloqueo(false, usuario.getEmail())`. Usa la función `estaBloqueado()` pasandole el email del usuario para verificar si se ha desbloqueado correctamente o no, devolviendo true o false respectivamente.

- **`estaBloqueado()`**:  Utiliza el método `comprobarBloqueo()` de **UsuarioRepository** para obtener el estado de bloqueo del usuario identificado por su email. Retorna un valor booleano que indica si el usuario está bloqueado o no.

### Capa de persistencia.
Se han añadido los siguientes métodos a la clase **UsuarioRepository.java**:

- **`countUsuariosAdministradores()`**: Utiliza la anotación **@Query** para realizar una consulta personalizada en la base de datos. En este caso, cuenta el número de usuarios que son administradores. La consulta SQL es la siguiente: 

    ```sql
    SELECT COUNT(u) FROM Usuario u WHERE u.esAdministrador = TRUE
    ```

- **`updateUsuarioBloqueo(boolean bloqueado, String emailUsuario)`**: Anotado con **@Modifying** y **@Query**, este método realiza una actualización en la base de datos. La consulta SQL es: 

    ```sql
    UPDATE Usuario u SET u.estaBloqueado = ?1 WHERE u.email = ?2
    ```

    Actualiza el estado de bloqueo de un usuario basado en el valor booleano proporcionado y su email.

- **`comprobarBloqueo(String emailUsuario)`**: Utiliza la anotación **@Query** para realizar una consulta en la base de datos. Esta consulta busca y devuelve el estado de bloqueo de un usuario específico según su email. La consulta SQL es: 

    ```sql
    SELECT u.estaBloqueado FROM Usuario u WHERE u.email = ?1
    ```
  
## Listado de plantillas thyemeleaf añadidas.
Estan son las plantillas thymeleaf que se han ido añadiendo conforme se ha ido realizando la práctica:

- **about.html**: Esta vista muestra los detalles del proyecto, es decir, el desarrollador, la versión del proyecto y la fecha del último release.

- **listaUsuario.html**: Esta vista muestra un listado de usuarios registrados en el sistema. Para cada usuario, se muestra un enlace que lleva a su descripción, junto con dos botones. El primer botón, "Desbloquear", estará activo si el usuario puede ser desbloqueado y desactivado si ya está desbloqueado. El segundo botón, "Bloquear", estará activo si el usuario puede ser bloqueado y desactivado si ya está bloqueado.
    
    El enlace de la descripción de usuario hace una petición **GET** a la ruta `/registrados/{id}`. El botón de desbloquear hace una petición **POST** a la ruta `/usuarios/{id}/desbloquear`. EL botón bloquear hace una petición **POST** a la ruta `/usuarios/{id}/bloquear`. En todas las rutas `id` es el id del usuario.

    Todas las rutas anteriores están implementadas en el controlador **UsuarioCntroller.java**.

- **navbar.html**: Actua como un fragment, contiene la implementación de la barra de menú. Cuando se utiliza este, se le debe pasar por parámetro un objeto **UsuarioData**, un booleano que indique si está o no logeado y un booleano que indique si el usuario es o no administrador.

    - En caso de no estar logeado un usuario, se le muestra el enlace "Login", el cual realiza una petición **GET** a `/login` para logearse y un enlace "Registro", el cual realiza una petición **GET** a `/registro` para registrarse.

    - En caso de estar logeado el usuario, se le muestra un enlace "ToDoList", el cual realiza una petición **GET** a `/about` para ver la página _Acerca De_. El segundo enlace es "Tareas", el cual realiza una petición **GET** a la ruta `/usuarios/{id}/tareas` para acceder al listado de tareas del usuario. A la detecha aparece un dropleft con las opciones de "Cuenta", todavía no implementado y "Cerrar Sesión", la cual realiza una petición **GET** a la ruta `/logout`.

    - En caso de estar logeado y sea un usuario administrador, aparecerán las mismas opciones que un usuario normal, como se ha descrito anteriormente, con la diferencia que se mostrará un enlace "Registrados", el cual realiza una petición **GET** a la ruta `/registrados` para obtener una lista de usuarios registrados.

    Todas estas rutas están implementadas en los ficheros **UsuarioController.java**, **LoginController.java** y **HomeController.java**.

- **descripcionUsuario.html**: Esta vista muestra el id, el email, el nombre y la fecha de nacimiento del usuario recibido.

## Explicación de los tests implementados.

En cuanto a los test, se han implementado test tanto para la capa de controlador como para la capa de servicio y persistencia. 

En la capa de controlador tenemos la clase **AcercaDeWebTest.java** que contiene tres test. El primero verifica que se muestre el nombre de la aplcación cuando hacemos un llamada a `/about`. 

Para testear la barra de menú se han implementado los test `servicioBarraMenuUsuarioLogeado()` y `servicioBarraMenuUsuarioNoLogeado()` en la clase **AcercaDeWebTest.java** de la capa de controlador y comprueban si se muestran las opciones correctas cuando un usuario está logeado y cuando no, respectivamente.

Para testear el listado de usuarios, se han añadido el test `servicioListadoUsuarios()` en la clase **UsuarioWebTest.java** de la capa de controlador, que comprueba si se carga correctamente la página _listaUsuarios_ y muestra correctamente el usuario registrado. También se han implementado el test `servicioListarUsuarios()` en la clase **UsuarioServiceTest.java** de la capa de servicio para verificar que la función `allUsuarios()` devuelva una lista de usuarios correctamente.

Para testear la descripción de usuario, se ha añadido el test  `comprobarDatosUsuariosDescripcion()` en la clase **UsuarioWebTest.java** de la capa de controlador, que comprueba que al acceder a la descripción de un usuario específico, se muestre tanto el nombre como el email del usuario.

Para testear la funcionalidad de usuario administrador, se han implementado los test `servicioExisteAdminRedirectARegistradosEnLogin()`, `servicioExisteAdminMuestraCheckBoxAdmin()` y `servicioExisteAdminNoMuestraCheckBoxAdmin()` en la clase **UsuarioWebTest.java** de la capa de controlador. El primero simula el logeo de usuario administrador y verifica si es redireccionado a la lista de usuarios, el segundo verifica que se muestra el _check box_ en el forumulario de registro en caso de no haber un administrador y el tercero verifica que no se muestra el _check box_ en caso de haber un administrador. También se ha implementado `servicioExisteAdmin()`, `servicioNoExisteAdmin()`, `servicioNoEsAdmin()`, `servicioEsAdmin()` en la clase **UsuarioServiceTest.java** de la capa de servicio. El primer test comprueba que haya un usuario administrador en la base de datos y el segundo que no exista, el tercer test verifica que dado un usuario normal, la función `esAdmin()` false y en el cuarto que dado un usuario administrador, devuelva true. Finalmente se ha implementado `comprobarNoHayAdministrador()` y `comprobarHayAdministrador()` en la clase **UsuarioTest.java** de la capa de persistencia. Los dos hacen uso de la función `countUsuariosAdministradores()` para verificar la existencia o no de un usuario administrador. 

Para testear la protección del listado de usuario y descripción de usuario, se ha implementado los test `servicioListadoUsuariosNoSaltaExcepcion()` y `servicioDatosUsuariosDescripcionNoSaltaExcepcion()` para verificar que cuando un usuario administrador entre a esas páginas no salte la excepción y los test `servicioListadoUsuariosSaltaExcepcion()` y `servicioDatosUsuariosDescripcionSaltaExcepcion()` para verificar que cuando un usuario normal intente entrar a una de esas páginas salte la excepción. Todos ellos implementados en la clase **UsuarioWebTest.java** de la capa de controlador.
 
Para testear la funconalidad de bloquear y desbloquear usuarios, se ha implementado `servicioBloquearUsuario()` y `servicioDesbloquearUsuario()` en la clase **UsuarioWebTest.java** de la capa de controlador, donde se comprueba que al realizar una solicitud de bloqueo o desbloqueo de un usuario, se redirija correctamente a la página de listado de usuarios. También se ha implementado los test `servicioBloquearUsuario()` y `servicioNoBloquearUsuario()` para verificar que dado un usuario se bloquea o desbloquea correctamente y los test `servicioComprobarSiUsuarioNoBloqueado()` y `servicioComprobarSiUsuarioBloqueado()` verifican que dado un usario comprobar si no está bloqueado o lo está mediante la función `estaBloqueado()`. Estos anteriores pertenencientes a la clase **UsuarioServiceTest.java** de la capa de servicio. Finalmente, se han imeplementado los test `comprobarBloqueoCorrecto()` y `comprobarDesbloqueoCorrecto()`  para comprobar que la función `updateUsuarioBloqueo()` bloquea y desbloquea correctamente y el test `comprobarEstadoBloqueo()` que verifica que la función `comprobarBloqueo() `devuelve el estado del bloqueo de un usuario correctamente. Implementado en **UsuarioTest.java** de la capa de persistencia.

## Explicación de código fuente relevante de las nuevas funcionalidades implementadas.

Para usar correctamente el fragment **`navbar.html`**, se usa de las siguientes tres maneras:

1. La primera es esta manera, la cual son para páginas donde puede entrar cualquier tipo de usuario.

```html
<nav th:replace="navbar :: navbar (usuario=${usuario}, logeado=${logeado}, administrador=${administrador})"></nav>
```

2. La segunda es esta manera, la cual son para páginas donde puede entrar solamente usuarios logeados y puede ser o no administrador.

```html
<nav th:replace="navbar :: navbar (usuario=${usuario}, logeado=true, administrador=${administrador})"></nav>
```

3. La tercera es esta manera, la cual son para páginas donde puede entrar solamente un usuario administrador.

```html
<nav th:replace="navbar :: navbar (usuario=${usuario}, logeado=${logeado}, administrador=true)"></nav>
```

Algo interesante a destacar es la adición en el método `loginSubmit()` de el _if_ que comprueba si un usuario está bloqueado:

```java
if(!usuarioService.estaBloqueado(loginData.geteMail())){}
```

Y el _if_ que comprueba si el usuario es administrador o no:

```java
if(usuario.getEsAdministrador()){
    return "redirect:/registrados";
}else{
    return "redirect:/usuarios/" + usuario.getId() + "/tareas";
}
```

# Links a los repositorios de GitHub y DockerHub:
- GitHub:
	- https://github.com/mads-ua-23-24/mads-todolist-fjrp9-ua.git
- DockerHub:
	- https://hub.docker.com/r/fjrp9/mads-todolist/tags

