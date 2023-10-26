package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import madstodolist.service.EquipoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class EquipoWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private UsuarioService usuarioService;

    // Moqueamos el managerUserSession para poder moquear el usuario logeado
    @MockBean
    private ManagerUserSession managerUserSession;

    List<EquipoData> addEquipoBD() {
        equipoService.crearEquipo("EquipoAAA");
        equipoService.crearEquipo("EquipoBBB");
        return equipoService.findAllOrdenadoPorNombre();
    }

    Long addUsuariosAEquipoBD() {
        EquipoData equipo = equipoService.crearEquipo("EquipoAAA");

        UsuarioData usuario1 = new UsuarioData();
        usuario1.setEmail("user1@ua");
        usuario1.setPassword("123");
        usuario1 = usuarioService.registrar(usuario1);

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("user2@ua");
        usuario2.setPassword("123");
        usuario2 = usuarioService.registrar(usuario2);

        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario1.getId());
        equipoService.añadirUsuarioAEquipo(equipo.getId(), usuario2.getId());

        return equipo.getId();
    }

    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user5@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    @Test
    public void listaEquipos() throws Exception {

        List<EquipoData> equipos = addEquipoBD();

        // GIVEN
        // Un usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());

        // WHEN
        // Accedemos a la lista de equipos
        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaEquipos"))
                .andExpect(model().attributeExists("equipos"))
                .andExpect(model().attribute("equipos", hasSize(2)))
                .andExpect(model().attribute("equipos", hasItem(
                        allOf(
                                hasProperty("nombre", is("EquipoAAA")),
                                hasProperty("id", is(equipos.get(0).getId()))
                        )
                )))
                .andExpect(model().attribute("equipos", hasItem(
                        allOf(
                                hasProperty("nombre", is("EquipoBBB")),
                                hasProperty("id", is(equipos.get(1).getId()))
                        )
                )));
    }

    @Test
    public void listaUsuariosEquipo() throws  Exception {

        Long equipoId = addUsuariosAEquipoBD();
        List<UsuarioData> usuarios = equipoService.usuariosEquipo(equipoId);

        // GIVEN
        // Un usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());

        // WHEN
        // Accedemos a la lista de usuarios de un equipo
        this.mockMvc.perform(get("/equipos/" + equipoId.toString() + "/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(2)))
                .andExpect(model().attribute("usuarios", hasItem(
                        allOf(
                                hasProperty("email", is("user1@ua")),
                                hasProperty("id", is(usuarios.get(0).getId()))
                        )
                )))
                .andExpect(model().attribute("usuarios", hasItem(
                        allOf(
                                hasProperty("email", is("user2@ua")),
                                hasProperty("id", is(usuarios.get(1).getId()))
                        )
                )));
    }

    @Test
    public void listaEquiposMuestraMensajeInfo() throws Exception {

        // GIVEN
        // Un usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());

        // WHEN
        // Accedemos a la lista de equipos
        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaEquipos"))
                .andExpect(content().string(containsString("Actualmente no hay equipos registrados.")));
    }

    @Test
    public void listaUsuariosDeEquipoMuestraMensajeInfo() throws Exception {

        Long idEquipo = addEquipoBD().get(1).getId();

        // GIVEN
        // Un usuario logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());

        // WHEN
        // Accedemos a la lista de equipos
        String url = "/equipos/" + idEquipo.toString() + "/usuarios";
        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(content().string(containsString("Actualmente no hay usuarios registrados en este equipo.")));
    }

    @Test
    public void listaEquiposContieneBotonesCorrectos() throws Exception{
        // GIVEN
        // Un usuario logeado y un equipo en la BD
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());
        addEquipoBD();

        // WHEN
        // Accedemos a la lista de equipos
        this.mockMvc.perform(get("/equipos"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaEquipos"))
                .andExpect(content().string(containsString("Unirse")))
                .andExpect(content().string(containsString("Salir")))
                .andExpect(content().string(containsString("Añadir nuevo Equipo")));
    }

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

    @Test
    public void usuarioSeUneCorrectamenteAUnEquipo() throws Exception {
        // GIVEN
        // Un usuario logeado y un equipo en la BD
        when(managerUserSession.usuarioLogeado()).thenReturn(addUsuarioBD());
        Long equipoId = addEquipoBD().get(0).getId();

        // WHEN
        // Un usuario se une a un equipo
        this.mockMvc.perform(post("/equipos/" + equipoId.toString() + "/añadirUsuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        // THEN
        // Comprobamos que se ha unido correctamente
        this.mockMvc.perform(get("/equipos/" + equipoId.toString() + "/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(1)))
                .andExpect(model().attribute("usuarios", hasItem(
                        allOf(
                                hasProperty("email", is("user5@ua"))
                        )
                )));
    }

    @Test
    public void usuarioSaleCorrectamenteDeUnEquipo() throws Exception {
        // GIVEN
        // Un usuario logeado, un equipo en la BD y un usuario en el equipo
        Long idUsuario = addUsuarioBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idUsuario);
        Long equipoId = addEquipoBD().get(0).getId();
        equipoService.añadirUsuarioAEquipo(equipoId, idUsuario);

        // WHEN
        // Un usuario sale de un equipo
        this.mockMvc.perform(post("/equipos/" + equipoId.toString() + "/eliminarUsuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));

        // THEN
        // Comprobamos que se ha salido correctamente
        this.mockMvc.perform(get("/equipos/" + equipoId.toString() + "/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(0)));
    }

    @Test
    public void usuarioEnUnEquipoIntentaUnirseAlMismoEquipo() throws Exception {
        // GIVEN
        // Un usuario logeado, un equipo en la BD y un usuario en el equipo
        Long idUsuario = addUsuarioBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idUsuario);
        Long equipoId = addEquipoBD().get(0).getId();
        equipoService.añadirUsuarioAEquipo(equipoId, idUsuario);

        // WHEN
        // Un usuario intenta unirse al mismo equipo
        this.mockMvc.perform(post("/equipos/" + equipoId.toString() + "/añadirUsuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje", "El usuario ya pertenece al equipo"));

        // THEN
        // Comprobamos que no se ha unido
        this.mockMvc.perform(get("/equipos/" + equipoId.toString() + "/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(1)));
    }

    @Test
    public void usuarioInentaSalirDeUnEquipoQueNoEsta() throws Exception {
        // GIVEN
        // Un usuario logeado, un equipo en la BD y un usuario en el equipo
        Long idUsuario = addUsuarioBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idUsuario);
        Long equipoId = addEquipoBD().get(0).getId();

        // WHEN
        // Un usuario intenta salir de un equipo que no está
        this.mockMvc.perform(post("/equipos/" + equipoId.toString() + "/eliminarUsuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"))
                .andExpect(flash().attribute("mensaje","El usuario no pertenece al equipo"));

        // THEN
        // Comprobamos que no se ha salido
        this.mockMvc.perform(get("/equipos/" + equipoId.toString() + "/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("listaUsuariosEquipo"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(model().attribute("usuarios", hasSize(0)));
    }
}
