package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAdministradorException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoServiceException;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import madstodolist.service.EquipoService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String crearEquipoSubmit(@Valid EquipoData equipoData, BindingResult result, Model model, RedirectAttributes flash) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        EquipoData equipo = new EquipoData();
        equipo.setNombre(equipoData.getNombre());

        equipoService.crearEquipo(equipo.getNombre());
        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @PostMapping("equipos/{id}/añadirUsuario")
    public String añadirUsuarioAEquipo(Model model, @PathVariable(value="id") Long idEquipo, RedirectAttributes flash){

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);

        try{
            equipoService.añadirUsuarioAEquipo(idEquipo, IdUsuarioLogeado);
        }catch (Exception e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }
        flash.addFlashAttribute("mensaje", "Te has añadido al equipo correctamente");

        return "redirect:/equipos";
    }

    @PostMapping("equipos/{id}/eliminarUsuario")
    public String eliminarUsuarioDeEquipo(Model model, @PathVariable(value="id") Long idEquipo, RedirectAttributes flash){

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);

        try{
            equipoService.eliminarUsuarioDeEquipo(idEquipo, IdUsuarioLogeado);
        }catch (Exception e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }
        flash.addFlashAttribute("mensaje", "Te has eliminado del equipo correctamente");

        return "redirect:/equipos";
    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    public String eliminarEquipo(@PathVariable(value="id") Long idEquipo, Model model){

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);
        comprobarUsuarioAdministrador(IdUsuarioLogeado);

        equipoService.borraEquipo(idEquipo);
        return "";
    }

    @GetMapping("/equipos/{id}/editar")
    public String formEditaEquipo(@PathVariable(value="id") Long idEquipo, Model model, RedirectAttributes flash) {

        Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(IdUsuarioLogeado);
        comprobarUsuarioAdministrador(IdUsuarioLogeado);

        try{
            EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
            model.addAttribute("equipo", equipo);
            model.addAttribute("usuario", usuarioService.findById(IdUsuarioLogeado));
            model.addAttribute("equipoData", new EquipoData());
        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }

        return "formEditarEquipo";
    }

    @PostMapping("/equipos/{id}/editar")
    public String grabaModificacion(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData, Model model, RedirectAttributes flash) {

            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);
            comprobarUsuarioAdministrador(IdUsuarioLogeado);

            try{
                equipoService.modificarEquipo(idEquipo, equipoData.getNombre());
            }catch (EquipoServiceException e){
                flash.addFlashAttribute("mensaje", e.getMessage());
                return "redirect:/equipos";
            }

            flash.addFlashAttribute("mensaje", "Equipo modificado correctamente");
            return "redirect:/equipos";
    }
}
