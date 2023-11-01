# PRÁCTICA 3 - MADS - Francisco José Ramírez Paraíso
## Pantalla de la base de datos PostgreSQL.

En la primera imagen se puede ver la pantalla de la base de datos PostgreSQL con las tablas creadas y sin datos:

![1.JPG](img%2F1.JPG)

En esta segunda imagen, se puede ver la pantalla de la base de datos PostgreSQL con las tablas creadas y con datos:

![2.JPG](img%2F2.JPG)

Los datos de la segunda imgen son los que se han ido guardando de una prueba de funcionamiento de la aplicación web.

## Historias de usuario:
### 009 Gestionar pertenencia al equipo

En el modelo `Equipo` he añadido el método  **delUsuario()** que recibe como parámetro un objeto de la clase `Usuario` y lo elimina de la lista de usuarios del equipo. También elimina de la lista de equipos del usuario el equipo en el que se ha ejecutado el método.

### 010 Gestión de equipos