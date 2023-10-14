# PRÁCTICA 2 - MADS - Francisco José Ramírez Paraíso
## Listado de nuevas clases y métodos implementados.
### Capa de controlador.
### Capa de servicio.
### Capa de persistencia.

## Listado de plantillas thyemeleaf añadidas.
Estan son las plantillas thymeleaf que se han ido añadiendo conforme se ha ido realizando la práctica:

- **about.html**: Esta vista muestra los detalles del proyecto, es decir, el desarrollador, la versión del proyecto y la fecha del último release.

- **listaUsuario.html**: Esta vista muestra un listado de usuarios registrados en el sistema. Para cada usuario, se muestra un enlace que lleva a su descripción, junto con dos botones. El primer botón, "Desbloquear", estará activo si el usuario puede ser desbloqueado y desactivado si ya está desbloqueado. El segundo botón, "Bloquear", estará activo si el usuario puede ser bloqueado y desactivado si ya está bloqueado.
    
    El enlace de la descripción de usuario hace una petición **GET** a la ruta `/registrados/{id}`. El botón de desbloquear hace una petición **POST** a la ruta `/usuarios/{id}/desbloquear`. EL botón bloquear hace una petición **POST** a la ruta `/usuarios/{id}/bloquear`. En todas las rutas `id` es el id del usuario.

    Todas las rutas anteriores están implementadas en el controlador **UsuarioCntroller.java**.

- **navbar.html**: Actua como un fragment, contiene la implementación de la barra de menú. Cuando se utiliza este, se le debe pasar por parámetro un objeto UsuarioData, un booleano que indique si está o no logeado y un booleano que indique si el usuario es o no administrador.

    - En caso de no estar logeado un usuario, se le muestra el enlace "Login", el cual realiza una petición **GET** a `/login` para logearse y un enlace "Registro", el cual realiza una petición **GET** a `/registro` para registrarse.

    - En caso de estar logeado el usuario, se le muestra un enlace "ToDoList", el cual realiza una petición **GET** a `/about` para ver la página _Acerca De_. El segundo enlace es "Tareas", el cual realiza una petición **GET** a la ruta `/usuarios/{id}/tareas` para acceder al listado de tareas del usuario. A la detecha aparece un dropleft con las opciones de "Cuenta", todavía no implementado y "Cerrar Sesión", la cual realiza una petición **GET** a la ruta `/logout`.

    - En caso de estar logeado y sea un usuario administrador, aparecerán las mismas opciones que un usuario normal, como se ha descrito anteriormente, con la diferencia que se mostrará un enlace "Registrados", el cual realiza una petición **GET** a la ruta `/registrados` para obtener una lista de usuarios registrados.

    Todas estas rutas están implementadas en los ficheros **UsuarioController.java**, **LoginController.java** y **HomeController.java**.

- **descripcionUsuario.html**: Esta vista muestra los el id, el email, el nombre y la fecha de nacimiento del usuario recibido.

## Explicación de los tests implementados.

## Explicación de código fuente relevante de las nuevas funcionalidades implementadas.
