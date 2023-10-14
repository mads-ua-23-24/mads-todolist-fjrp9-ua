# PRÁCTICA 2 - MADS - Francisco José Ramírez Paraíso
## Listado de nuevas clases y métodos implementados.
### Capa de controlador.

Se han añadido las clases **HomeController.java**, **UsuarioController.java** y **UsuarioNoAdministradorException.java**.

- **HomeController.java**, sus métodos añadidos son:
    - **`about()`**: Está mapeado a la ruta `/about`. Su función principal es la de gestionar la información de la página _Acerca De_.
    
        Verifica si el usuario que realiza la petición está logeado mediante la función `managerUserSession.usuarioLogeado()`. Si no está logeado, establece los atributos "logeado", "usuario" y "administrador" como falsos en el modelo. En caso de estar logeado, recupera el id del usuario con la función anterior y utiliza la función `findById()` para recuperar toda la información de este. Además, determina si el usuario es un administrador mediante el método `esAdmin()` de **`UsuarioService`** y establece el atributo "administrador" en consecuencia. Todo esto sirve para poder poblar la barra de menú. 

        Devuelve la vista **`about.html`**.
- **UsuarioController.java**, sus métodos añadidos son:
    - **`listadoUsuarios()`**: Está mapeado a la ruta `/registrados`. Su función principal es la de devolver los usuarios que estén registrados en el sistema.

        Utiliza la función `comprobarUsuarioAdministrador()` para verificar si el usuario que realiza la petición es un usuario administrador. Posteriormente, utiliza la función `allUsuarios()` de **`UsuarioService`** para recuperar todos los usuarios registrados y los añade al modelo con el nombre "usuarios".

        Además, establece el atributo "logeado" como verdadero y obtiene información detallada del usuario actual mediante la función `findById()` de **`UsuarioService`**, utilizando el ID obtenido a través de `managerUserSession.usuarioLogeado()`. La información del usuario se agrega al modelo con el nombre "usuarioPrincipal".

        Devuelve la vista **`listaUsuarios.html`**.

    - **`descripcionUsuario()`**: Está mapeado a la ruta `/registrados/{id}`. Su función principal es la de mostrar la descripción de un usuario específico. Utiliza `comprobarUsuarioAdministrador()` para validar los privilegios, luego agrega la información del usuario al modelo usando `findById()`. 
    
        Devuelve la vista **`descripcionUsuario.html`**. 


    - **`bloqueaUsuario()`**: Está mapeado a la ruta `/usuarios/{id}/bloquear`. Su función principal es la de bloquear al usuario con el id especificado. Utiliza `bloquearUsuario()` de **`UsuarioService`** para bloquear al usuario y muestra un mensaje correspondiente a través de flash.  
    
        Redirige a la página de lista de usuarios mediante `/registrados`.

    - **`desbloqueaUsuario()`**: Está mapeado a la ruta `/usuarios/{id}/desbloquear`. Su función es la de desbloquear al usuario con el id proporcionado. Utiliza `desbloquearUsuario()` de **`UsuarioService`** para desbloquear al usuario y muestra un mensaje correspondiente a través de flash. 
    
        Redirige a la página de lista de usuarios mediante `/registrados`.

- **UsuarioNoAdministradorException.java** representa un caso en el que un usuario intenta acceder a funciones restringidas para administradores. La anotación @ResponseStatus configura la respuesta HTTP 401 (No autorizado) cuando se lanza **UsuarioNoAdministradorException**, con el mensaje "No autorizado".

### Capa de servicio.
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

    Para usar correctamente este fragment, se usa de las siguientes tres maneras:

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
- **descripcionUsuario.html**: Esta vista muestra el id, el email, el nombre y la fecha de nacimiento del usuario recibido.

## Explicación de los tests implementados.

## Explicación de código fuente relevante de las nuevas funcionalidades implementadas.
