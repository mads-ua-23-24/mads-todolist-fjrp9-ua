package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model){
        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuarios", usuarios);

        if(managerUserSession.usuarioLogeado() == null){
            model.addAttribute("logeado",false);
            model.addAttribute("usuario", null);
        }else{
            model.addAttribute("logeado", true);
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuario", usuario);
        }
        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session){

        //Todavía queda terminarlo

        if(managerUserSession.usuarioLogeado() == null){
            model.addAttribute("logeado",false);
            model.addAttribute("usuario", null);
        }else{
            model.addAttribute("logeado", true);
            UsuarioData usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
            model.addAttribute("usuario", usuario);
        }
        return "descripcionUsuario";
    }
}