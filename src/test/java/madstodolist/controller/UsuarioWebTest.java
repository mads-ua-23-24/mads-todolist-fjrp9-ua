package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
//
// A diferencia de los tests web de tarea, donde usábamos los datos
// de prueba de la base de datos, aquí vamos a practicar otro enfoque:
// moquear el usuarioService.
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    // Moqueamos el usuarioService.
    // En los tests deberemos proporcionar el valor devuelto por las llamadas
    // a los métodos de usuarioService que se van a ejecutar cuando se realicen
    // las peticiones a los endpoint.
    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;


    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.login para que
        // devuelva un LOGIN_OK y la llamada a usuarioServicie.findByEmail
        // para que devuelva un usuario determinado.

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición POST al login pasando los datos
        // esperados en el mock, la petición devolverá una redirección a la
        // URL con las tareas del usuario

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/1/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // USER_NOT_FOUND
        when(usuarioService.estaBloqueado("pepito.perez@gmail.com")).thenReturn(false);
        when(usuarioService.login("pepito.perez@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.USER_NOT_FOUND);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "No existe usuario"
        this.mockMvc.perform(post("/login")
                        .param("eMail","pepito.perez@gmail.com")
                        .param("password","12345678"))
                .andExpect(content().string(containsString("No existe usuario")));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        // GIVEN
        // Moqueamos el método usuarioService.login para que devuelva
        // ERROR_PASSWORD
        when(usuarioService.estaBloqueado("ana.garcia@gmail.com")).thenReturn(false);
        when(usuarioService.login("ana.garcia@gmail.com", "000"))
                .thenReturn(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // WHEN, THEN
        // Realizamos una petición POST con los datos del usuario mockeado y
        // se debe devolver una página que contenga el mensaja "Contraseña incorrecta"
        this.mockMvc.perform(post("/login")
                        .param("eMail","ana.garcia@gmail.com")
                        .param("password","000"))
                .andExpect(content().string(containsString("Contraseña incorrecta")));
    }

    @Test
    public void servicioListadoUsuarios() throws Exception {

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);
        anaGarcia.setEsAdministrador(true);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(true);
        when(usuarioService.findById(anaGarcia.getId())).thenReturn(anaGarcia);


        List<UsuarioData> usuarios = new ArrayList<>();
        usuarios.add(anaGarcia);

        when(usuarioService.allUsuarios()).thenReturn(usuarios);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk()) // Esperamos que la respuesta tenga un estado HTTP 200 OK
                .andExpect(view().name("listaUsuarios"))
                .andExpect(content().string(containsString("ana.garcia@gmail.com")));
    }

    @Test
    public void comprobarDatosUsuariosDescripcion() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(true);
        when(usuarioService.findById(anaGarcia.getId())).thenReturn(anaGarcia);

        String urlDEscripcion = "/registrados/" + anaGarcia.getId().toString();
        this.mockMvc.perform(get(urlDEscripcion))
                .andExpect(content().string(
                        allOf(containsString("Ana García"),
                                containsString("ana.garcia@gmail.com"))));
    }

    @Test
    public void servicioExisteAdminRedirectARegistradosEnLogin() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.findByEmail para que
        // devuelva un LOGIN_OK y la llamada a usuarioServicie.findByEmail
        // para que devuelva un usuario determinado.

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setId(1L);
        anaGarcia.setEsAdministrador(true);

        when(usuarioService.login("ana.garcia@gmail.com", "12345678"))
                .thenReturn(UsuarioService.LoginStatus.LOGIN_OK);
        when(usuarioService.findByEmail("ana.garcia@gmail.com"))
                .thenReturn(anaGarcia);

        // WHEN, THEN
        // Realizamos una petición POST al login pasando los datos
        // esperados en el mock, la petición devolverá una redirección a la
        // URL con las registrados

        this.mockMvc.perform(post("/login")
                        .param("eMail", "ana.garcia@gmail.com")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registrados"));
    }

    @Test
    public void servicioExisteAdminMuestraCheckBoxAdmin() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.existeAdmin para que
        // devuelva false en la existencia de un admin

        when(usuarioService.existeAdmin()).thenReturn(false)
;
        // WHEN, THEN
        // Realizamos una petición get al registro
        // esperando que se muestre el check box Administrador

        this.mockMvc.perform(get("/registro"))
                .andExpect(content().string(containsString("Administrador")));
    }

    @Test
    public void servicioExisteAdminNoMuestraCheckBoxAdmin() throws Exception {
        // GIVEN
        // Moqueamos la llamada a usuarioService.existeAdmin para que
        // devuelva false en la existencia de un admin

        when(usuarioService.existeAdmin()).thenReturn(true)
        ;
        // WHEN, THEN
        // Realizamos una petición get al registro
        // esperando que se muestre el check box Administrador

        this.mockMvc.perform(get("/registro"))
                .andExpect(content().string(not(containsString("Administrador"))));
    }

    @Test
    public void servicioListadoUsuariosNoSaltaExcepcion() throws Exception {

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(true);
        when(usuarioService.findById(anaGarcia.getId())).thenReturn(anaGarcia);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk());;
    }

    @Test
    public void servicioDatosUsuariosDescripcionNoSaltaExcepcion() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(true);
        when(usuarioService.findById(anaGarcia.getId())).thenReturn(anaGarcia);

        String urlDEscripcion = "/registrados/" + anaGarcia.getId().toString();
        this.mockMvc.perform(get(urlDEscripcion))
                .andExpect(status().isOk());
    }

    @Test
    public void servicioListadoUsuariosSaltaExcepcion() throws Exception {

        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(false);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());;
    }

    @Test
    public void servicioDatosUsuariosDescripcionSaltaExcepcion() throws Exception {
        UsuarioData anaGarcia = new UsuarioData();
        anaGarcia.setNombre("Ana García");
        anaGarcia.setEmail("ana.garcia@gmail.com");
        anaGarcia.setId(1L);

        when(managerUserSession.usuarioLogeado()).thenReturn(anaGarcia.getId());
        when(usuarioService.esAdmin(anaGarcia.getId())).thenReturn(false);

        String urlDEscripcion = "/registrados/" + anaGarcia.getId().toString();
        this.mockMvc.perform(get(urlDEscripcion))
                .andExpect(status().isUnauthorized());
    }
}
