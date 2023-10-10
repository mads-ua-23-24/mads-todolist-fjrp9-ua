package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNoAdministradorException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

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

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model){

        comprobarUsuarioAdministrador(managerUserSession.usuarioLogeado());

        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuarios", usuarios);

        model.addAttribute("logeado", true);
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuarioPrincipal", usuario);
        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session){

        comprobarUsuarioAdministrador(managerUserSession.usuarioLogeado());

        model.addAttribute("usuarioDescrito", usuarioService.findById(idUsuario));

        model.addAttribute("logeado", true);
        UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        model.addAttribute("usuario", usuario);

        return "descripcionUsuario";
    }

    @PostMapping("/usuarios/{id}/bloquear")
    public String bloqueaUsuario(@PathVariable(value="id") Long idUsuario, Model model, RedirectAttributes flash, HttpSession session) {
        usuarioService.bloquearUsuario(idUsuario);

        flash.addFlashAttribute("mensaje", "Usuario bloqueado correctamente");
        return "redirect:/registrados";
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloqueaUsuario(@PathVariable(value="id") Long idUsuario, Model model, RedirectAttributes flash, HttpSession session) {
        usuarioService.desbloquearUsuario(idUsuario);

        flash.addFlashAttribute("mensaje", "Usuario desbloqueado correctamente");
        return "redirect:/registrados";
    }
}