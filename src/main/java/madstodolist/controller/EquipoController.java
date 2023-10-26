package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAdministradorException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import madstodolist.service.EquipoService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    EquipoService equipoService;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/equipos")
    public String listarEquipos(Model model){

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        model.addAttribute("equipos", equipos);
        model.addAttribute("logeado", true);
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuarioPrincipal", usuario);
        boolean administrador = usuarioService.esAdmin(IdUsuarioLogeado);
        model.addAttribute("administrador", administrador);

        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}/usuarios")
    public String listaUsuariosEquipo(Model model, @PathVariable(value="id") Long idEquipo){

            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);

            List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("logeado", true);
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuarioPrincipal", usuario);
            boolean administrador = usuarioService.esAdmin(IdUsuarioLogeado);
            model.addAttribute("administrador", administrador);
            EquipoData equipoData = equipoService.recuperarEquipo(idEquipo);
            model.addAttribute("equipo", equipoData);

            return "listaUsuariosEquipo";
    }

    @GetMapping("equipos/crear")
    public String crearEquipo(Model model) {

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);

        model.addAttribute("logeado", true);
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuarioPrincipal", usuario);
        boolean administrador = usuarioService.esAdmin(IdUsuarioLogeado);
        model.addAttribute("administrador", administrador);

        model.addAttribute("equipoData", new EquipoData());

        return "formCrearEquipo";
    }

    @PostMapping("/equipos/crear")
    public String crearEquipoSubmit(@Valid EquipoData equipoData, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        EquipoData equipo = new EquipoData();
        equipo.setNombre(equipoData.getNombre());

        equipoService.crearEquipo(equipo.getNombre());
        return "redirect:/equipos";
    }
}
