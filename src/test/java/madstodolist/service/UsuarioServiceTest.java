package madstodolist.service;

import madstodolist.dto.UsuarioData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve el identificador del usuario de la BD
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    Long addUsuarioBloqueadoBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario.setEstaBloqueado(true);
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    Long addAdministradorBD(){
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario.setEsAdministrador(true);
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    @Test
    public void servicioLoginUsuario() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // WHEN
        // intentamos logear un usuario y contraseña correctos
        UsuarioService.LoginStatus loginStatus1 = usuarioService.login("user@ua", "123");

        // intentamos logear un usuario correcto, con una contraseña incorrecta
        UsuarioService.LoginStatus loginStatus2 = usuarioService.login("user@ua", "000");

        // intentamos logear un usuario que no existe,
        UsuarioService.LoginStatus loginStatus3 = usuarioService.login("pepito.perez@gmail.com", "12345678");

        // THEN

        // el valor devuelto por el primer login es LOGIN_OK,
        assertThat(loginStatus1).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);

        // el valor devuelto por el segundo login es ERROR_PASSWORD,
        assertThat(loginStatus2).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // y el valor devuelto por el tercer login es USER_NOT_FOUND.
        assertThat(loginStatus3).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    public void servicioRegistroUsuario() {
        // WHEN
        // Registramos un usuario con un e-mail no existente en la base de datos,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        usuarioService.registrar(usuario);

        // THEN
        // el usuario se añade correctamente al sistema.

        UsuarioData usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getEmail()).isEqualTo("usuario.prueba2@gmail.com");
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // WHEN, THEN
        // Si intentamos registrar un usuario con un password null,
        // se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // THEN
        // Si registramos un usuario con un e-mail ya existente en la base de datos,
        // , se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("12345678");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {

        // WHEN
        // Si registramos en el sistema un usuario con un e-mail no existente en la base de datos,
        // y un password no nulo,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        // THEN
        // se actualiza el identificador del usuario

        assertThat(usuarioNuevo.getId()).isNotNull();

        // con el identificador que se ha guardado en la BD.

        UsuarioData usuarioBD = usuarioService.findById(usuarioNuevo.getId());
        assertThat(usuarioBD).isEqualTo(usuarioNuevo);
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN
        // Un usuario en la BD

        Long usuarioId = addUsuarioBD();

        // WHEN
        // recuperamos un usuario usando su e-mail,

        UsuarioData usuario = usuarioService.findByEmail("user@ua");

        // THEN
        // el usuario obtenido es el correcto.

        assertThat(usuario.getId()).isEqualTo(usuarioId);
        assertThat(usuario.getEmail()).isEqualTo("user@ua");
        assertThat(usuario.getNombre()).isEqualTo("Usuario Ejemplo");
    }

    @Test
    public void servicioListarUsuarios(){

        //Inicializamos los datos de prueba en la BD
        addUsuarioBD();

        //Obtenemos todos los usuarios de la BD llamando al método que estamos testeando
        List<UsuarioData> usuarios = usuarioService.allUsuarios();

        //Verificamos que la lista contiene el número de usuarios correctos
        assertThat(usuarios.size()).isEqualTo(1);
    }

    @Test
    public void servicioExisteAdmin(){
        // GIVEN
        // Un usuario administrador en la BD

        addAdministradorBD();

        // WHEN
        // comprobamos si existe algun administrador,

        boolean existeAdmin = usuarioService.existeAdmin();

        // THEN
        // verificamos que existe.

        assertThat(existeAdmin).isEqualTo(true);
    }

    @Test
    public void servicioNoExisteAdmin(){
        // GIVEN
        // Un usuario administrador en la BD

        addUsuarioBD();

        // WHEN
        // comprobamos si existe algun administrador,

        boolean existeAdmin = usuarioService.existeAdmin();

        // THEN
        // verificamos que no existe.

        assertThat(existeAdmin).isEqualTo(false);
    }

    @Test
    public void servicioNoEsAdmin(){
        // GIVEN
        // Un usuario administrador en la BD

        Long idUser = addUsuarioBD();

        // WHEN
        // comprobamos si es administrador,

        boolean esAdmin = usuarioService.esAdmin(idUser);

        // THEN
        // verificamos que no es admin.

        assertThat(esAdmin).isEqualTo(false);
    }

    @Test
    public void servicioEsAdmin(){
        // GIVEN
        // Un usuario administrador en la BD

        Long idUser = addAdministradorBD();

        // WHEN
        // comprobamos si es administrador,

        boolean esAdmin = usuarioService.esAdmin(idUser);

        // THEN
        // verificamos que no es admin.

        assertThat(esAdmin).isEqualTo(true);
    }

    @Test
    public void servicioBloquearUsuario(){
        // GIVEN
        // Un usuario no bloqueado
        Long idUser = addUsuarioBD();

        // WHEN
        // bloqueamos al usuario,

        usuarioService.bloquearUsuario(idUser);

        // THEN
        // verificamos que se ha bloqueado al usuario.
        UsuarioData usuario = usuarioService.findById(idUser);
        assertThat(usuario.getEstaBloqueado()).isTrue();
    }

    @Test
    public void servicioNoBloquearUsuario(){
        // GIVEN
        // Un usuario no bloqueado
        Long idUser = addUsuarioBloqueadoBD();

        // WHEN
        // desbloqueamos al usuario,

        usuarioService.desbloquearUsuario(idUser);

        // THEN
        // verificamos que se ha desbloqueado al usuario.
        UsuarioData usuario = usuarioService.findById(idUser);
        assertThat(usuario.getEstaBloqueado()).isFalse();
    }

    @Test
    public void servicioComprobarSiUsuarioNoBloqueado(){
        // GIVEN
        // Un usuario no bloqueado
        Long idUser = addUsuarioBD();
        UsuarioData usuario = usuarioService.findById(idUser);

        // WHEN
        // comprobamos si el usuario está bloqueado,

        boolean bloqueado = usuarioService.estaBloqueado(usuario.getEmail());

        // THEN
        // verificamos que está desbloqueado usuario.
        assertThat(bloqueado).isFalse();
    }

    @Test
    public void servicioComprobarSiUsuarioBloqueado(){
        // GIVEN
        // Un usuario no bloqueado
        Long idUser = addUsuarioBloqueadoBD();
        UsuarioData usuario = usuarioService.findById(idUser);

        // WHEN
        // comprobamos si el usuario está bloqueado

        boolean bloqueado = usuarioService.estaBloqueado(usuario.getEmail());

        // THEN
        // verificamos que está bloqueado usuario.
        assertThat(bloqueado).isTrue();
    }
}