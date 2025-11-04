package usco.edu.co.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import usco.edu.co.models.Usuario;
import usco.edu.co.repositories.UsuarioRepository;
import usco.edu.co.services.AsignaturaService;

import java.time.LocalTime;

@Controller
@RequestMapping("/docente")
@RequiredArgsConstructor
public class DocenteController {

    private final AsignaturaService asignaturaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/mis-asignaturas")
    public String misAsignaturas(@AuthenticationPrincipal UserDetails user, Model model) {
        Usuario docente = usuarioRepository.findByNombreUsuario(user.getUsername()).orElseThrow();
        model.addAttribute("asignaturas", asignaturaService.listarPorDocente(docente));
        return "docente/mis-asignaturas";
    }

    @GetMapping("/asignaturas/{id}/editar-horario")
    public String editarHorario(@PathVariable Long id, Model model) {
        model.addAttribute("asignatura", asignaturaService.obtenerPorId(id));
        return "docente/editar-horario";
    }

    @PostMapping("/asignaturas/{id}/editar-horario")
    public String actualizarHorario(@PathVariable Long id,
                                    @RequestParam("horarioInicio") String inicio,
                                    @RequestParam("horarioFin") String fin,
                                    @AuthenticationPrincipal UserDetails user,
                                    Model model) {
        Usuario docente = usuarioRepository.findByNombreUsuario(user.getUsername()).orElseThrow();
        try {
            asignaturaService.actualizarHorarioComoDocente(id, docente, LocalTime.parse(inicio), LocalTime.parse(fin));
            return "redirect:/docente/mis-asignaturas";
        } catch (IllegalArgumentException | SecurityException ex) {
            model.addAttribute("asignatura", asignaturaService.obtenerPorId(id));
            model.addAttribute("error", ex.getMessage());
            return "docente/editar-horario";
        }
    }
}
