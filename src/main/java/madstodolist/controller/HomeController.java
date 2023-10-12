package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/about")
    public String about(Model model) {

        if(managerUserSession.usuarioLogeado() == null){
            model.addAttribute("logeado",false);
            model.addAttribute("usuario", null);
            model.addAttribute("administrador", false);
        }else{
            Long idUsuario = managerUserSession.usuarioLogeado();
            model.addAttribute("logeado", true);
            UsuarioData usuario = usuarioService.findById(idUsuario);
            model.addAttribute("usuario", usuario);
            boolean administrador = usuarioService.esAdmin(idUsuario);
            model.addAttribute("administrador", administrador);
        }

        return "about";
    }

}
