package usco.edu.co.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated()) {
            var auths = authentication.getAuthorities();
            if (auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECTOR"))) {
                return "redirect:/rector/asignaturas";
            } else if (auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
                return "redirect:/docente/mis-asignaturas";
            } else if (auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"))) {
                return "redirect:/estudiante/asignaturas";
            }
            return "redirect:/";
        }
        return "login";
    }
}

