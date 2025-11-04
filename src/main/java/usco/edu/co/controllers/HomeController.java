package usco.edu.co.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        var auth = authentication.getAuthorities();
        if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECTOR"))) {
            return "redirect:/rector/asignaturas";
        } else if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
            return "redirect:/docente/mis-asignaturas";
        } else if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"))) {
            return "redirect:/estudiante/asignaturas";
        }
        return "redirect:/login";
    }
}

