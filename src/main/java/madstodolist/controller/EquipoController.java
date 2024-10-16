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

        if (idUsuario == null){
            throw new UsuarioNoLogeadoException();
        }else{
            Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

            if (!idUsuario.equals(idUsuarioLogeado))
                throw new UsuarioNoLogeadoException();
        }
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

        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);
            List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
            model.addAttribute("equipos", equipos);
            model.addAttribute("logeado", true);
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuarioPrincipal", usuario);
        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
        }

        return "listaEquipos";
    }

    @GetMapping("/equipos/{id}/usuarios")
    public String listaUsuariosEquipo(Model model, @PathVariable(value="id") Long idEquipo, RedirectAttributes flash){

        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);

            List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
            model.addAttribute("usuarios", usuarios);

            model.addAttribute("logeado", true);

            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuarioPrincipal", usuario);

            EquipoData equipoData = equipoService.recuperarEquipo(idEquipo);
            model.addAttribute("equipo", equipoData);

        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }

        return "listaUsuariosEquipo";
    }

    @GetMapping("equipos/crear")
    public String crearEquipo(Model model) {
        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);

            model.addAttribute("logeado", true);

            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuarioPrincipal", usuario);

            model.addAttribute("equipoData", new EquipoData());

        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
        }

        return "formCrearEquipo";
    }

    @PostMapping("/equipos/crear")
    public String crearEquipoSubmit(@Valid EquipoData equipoData, BindingResult result, Model model, RedirectAttributes flash) {

        EquipoData equipo = new EquipoData();
        equipo.setNombre(equipoData.getNombre());

        try{
            equipoService.crearEquipo(equipo.getNombre());
        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }

        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @PostMapping("equipos/{id}/añadirUsuario")
    public String añadirUsuarioAEquipo(Model model, @PathVariable(value="id") Long idEquipo, RedirectAttributes flash){

        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);
            equipoService.añadirUsuarioAEquipo(idEquipo, IdUsuarioLogeado);
        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
        }

        flash.addFlashAttribute("mensaje", "Te has añadido al equipo correctamente");
        return "redirect:/equipos";
    }

    @PostMapping("equipos/{id}/eliminarUsuario")
    public String eliminarUsuarioDeEquipo(Model model, @PathVariable(value="id") Long idEquipo, RedirectAttributes flash){

        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);
            equipoService.eliminarUsuarioDeEquipo(idEquipo, IdUsuarioLogeado);
        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
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

        try{
            Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
            comprobarUsuarioLogeado(IdUsuarioLogeado);
            comprobarUsuarioAdministrador(IdUsuarioLogeado);

            EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
            model.addAttribute("equipo", equipo);
            model.addAttribute("usuario", usuarioService.findById(IdUsuarioLogeado));
            model.addAttribute("equipoData", new EquipoData());

        }catch (EquipoServiceException e){
            flash.addFlashAttribute("mensaje", e.getMessage());
            return "redirect:/equipos";
        }catch (UsuarioNoLogeadoException e){
            return "redirect:/login";
        }catch (UsuarioNoAdministradorException e){
            flash.addFlashAttribute("mensaje", "No tienes permisos para editar este equipo");
            return "redirect:/equipos";
        }

        return "formEditarEquipo";
    }

    @PostMapping("/equipos/{id}/editar")
    public String grabaModificacion(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData, Model model, RedirectAttributes flash) {

            try{
                Long IdUsuarioLogeado = managerUserSession.usuarioLogeado();
                comprobarUsuarioLogeado(IdUsuarioLogeado);
                comprobarUsuarioAdministrador(IdUsuarioLogeado);
                equipoService.modificarEquipo(idEquipo, equipoData.getNombre());
            }catch (EquipoServiceException e){
                flash.addFlashAttribute("mensaje", e.getMessage());
                return "redirect:/equipos";
            }catch (UsuarioNoLogeadoException e){
                return "redirect:/login";
            }catch (UsuarioNoAdministradorException e){
                flash.addFlashAttribute("mensaje", "No tienes permisos para editar este equipo");
                return "redirect:/equipos";
            }

            flash.addFlashAttribute("mensaje", "Equipo modificado correctamente");
            return "redirect:/equipos";
    }
}
