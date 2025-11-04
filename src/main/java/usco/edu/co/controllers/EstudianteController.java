package usco.edu.co.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import usco.edu.co.services.AsignaturaService;

@Controller
@RequestMapping("/estudiante")
@RequiredArgsConstructor
public class EstudianteController {

    private final AsignaturaService asignaturaService;

    @GetMapping("/asignaturas")
    public String verAsignaturas(Model model) {
        model.addAttribute("asignaturas", asignaturaService.listarTodas());
        return "estudiante/asignaturas";
    }
}
