package madstodolist.service;

import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import madstodolist.dto.EquipoData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class EquipoServiceTest {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Test
    public void crearRecuperarEquipo() {
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThat(equipo.getId()).isNotNull();

        EquipoData equipoBd = equipoService.recuperarEquipo(equipo.getId());
        assertThat(equipoBd).isNotNull();
        assertThat(equipoBd.getNombre()).isEqualTo("Proyecto 1");
    }

    @Test
    public void listadoEquiposOrdenAlfabetico() {
        // GIVEN
        // Dos equipos en la base de datos
        equipoService.crearEquipo("Proyecto BBB");
        equipoService.crearEquipo("Proyecto AAA");

        // WHEN
        // Recuperamos los equipos
        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();

        // THEN
        // Los equipos están ordenados por nombre
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto AAA");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto BBB");
    }

    @Test
    public void añadirUsuarioAEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");

        // WHEN
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(1);
        assertThat(usuarios.get(0).getEmail()).isEqualTo("user@ua");
    }

    @Test
    public void recuperarEquiposDeUsuario() {
        // GIVEN
        // Un usuario y dos equipos en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo1 = equipoService.crearEquipo("Proyecto 1");
        EquipoData equipo2 = equipoService.crearEquipo("Proyecto 2");
        equipoService.añadirUsuarioAEquipo(equipo1.getId(), usuario.getId());
        equipoService.añadirUsuarioAEquipo(equipo2.getId(), usuario.getId());

        // WHEN
        // Recuperamos los equipos del usuario
        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());

        // THEN
        // El usuario pertenece a los dos equipos
        assertThat(equipos).hasSize(2);
        assertThat(equipos.get(0).getNombre()).isEqualTo("Proyecto 1");
        assertThat(equipos.get(1).getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void comprobarExcepciones() {
        // Comprobamos las excepciones lanzadas por los métodos
        // recuperarEquipo, añadirUsuarioAEquipo, usuariosEquipo y equiposUsuario
        assertThatThrownBy(() -> equipoService.recuperarEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(1L, 1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.usuariosEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
        assertThatThrownBy(() -> equipoService.equiposUsuario(1L))
                .isInstanceOf(EquipoServiceException.class);

        // Creamos un equipo pero no un usuario y comprobamos que también se lanza una excepción
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        assertThatThrownBy(() -> equipoService.añadirUsuarioAEquipo(equipo.getId(), 1L))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void eliminarUsuarioDeEquipo() {
        // GIVEN
        // Un usuario y un equipo en la base de datos
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        // Añadimos el usuario al equipo
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        // WHEN
        //Eliminamos el usuario del equipo
        equipoService.eliminarUsuarioDeEquipo(equipo.getId(), usuario.getId());

        // THEN
        // El usuario no pertenece al equipo
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipo.getId());
        assertThat(usuarios).hasSize(0);
        assertThat(usuarios).doesNotContain(usuario);
    }

    @Test
    public void excepcionSiIntentaCrearUnEquipoConNombreVacio() {
        assertThatThrownBy(() -> equipoService.crearEquipo(""))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testModificarEquipo() {
        // GIVEN
        // Un equipo en la BD
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        // WHEN
        // Modificamos el nombre del equipo
        EquipoData equipoModificado = equipoService.modificarEquipo(equipo.getId(), "Proyecto 2");
        // THEN
        // El nombre del equipo se ha modificado
        assertThat(equipoModificado.getNombre()).isEqualTo("Proyecto 2");
    }

    @Test
    public void testBorraEquipo() {
        //GIVEN
        // Un equipo en la BD y un usuario en el equipo
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario.getId());

        //WHEN
        // Borramos el equipo
        equipoService.borraEquipo(equipo.getId());

        //THEN
        // El equipo se ha borrado y el usuario ya no pertenece a ningún equipo
        assertThatThrownBy(() -> equipoService.recuperarEquipo(equipo.getId()))
                .isInstanceOf(EquipoServiceException.class);

        List<EquipoData> equipos = equipoService.equiposUsuario(usuario.getId());
        assertThat(equipos).hasSize(0);
    }

    @Test
    public void testIntentarModificarUnEquipoQueNoExiste() {
        assertThatThrownBy(() -> equipoService.modificarEquipo(1L, "Proyecto 2"))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testIntentarModificarUnEquipoConNuevoNombreVacio() {
        // GIVEN
        // Un equipo en la BD
        EquipoData equipo = equipoService.crearEquipo("Proyecto 1");
        // WHEN
        // Modificamos el nombre del equipo
        assertThatThrownBy(() -> equipoService.modificarEquipo(equipo.getId(), ""))
                .isInstanceOf(EquipoServiceException.class);
    }

    @Test
    public void testIntentarBorrarUnEquipoQueNoExiste() {
        assertThatThrownBy(() -> equipoService.borraEquipo(1L))
                .isInstanceOf(EquipoServiceException.class);
    }
}
