package usco.edu.co.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import usco.edu.co.models.Asignatura;
import usco.edu.co.models.RoleName;
import usco.edu.co.models.Usuario;
import usco.edu.co.repositories.UsuarioRepository;
import usco.edu.co.services.AsignaturaService;

import java.util.List;

@Controller
@RequestMapping("/rector")
@RequiredArgsConstructor
public class RectorController {

    private final AsignaturaService asignaturaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/asignaturas")
    public String listar(Model model) {
        model.addAttribute("asignaturas", asignaturaService.listarTodas());
        return "rector/lista-asignaturas";
    }

    @GetMapping("/asignaturas/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("asignatura", new Asignatura());
        List<Usuario> docentes = usuarioRepository.findByRol_Nombre(RoleName.DOCENTE);
        model.addAttribute("docentes", docentes);
        return "rector/form-asignatura";
    }

    @PostMapping("/asignaturas")
    public String crear(@Valid @ModelAttribute("asignatura") Asignatura asignatura,
                        BindingResult br,
                        @RequestParam("docenteId") Long docenteId,
                        Model model) {
        if (br.hasErrors()) {
            model.addAttribute("docentes", usuarioRepository.findByRol_Nombre(RoleName.DOCENTE));
            return "rector/form-asignatura";
        }
        try {
            Usuario docente = usuarioRepository.findById(docenteId).orElseThrow();
            asignatura.setDocenteEncargado(docente);
            asignaturaService.crearComoRector(asignatura);
            return "redirect:/rector/asignaturas";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("docentes", usuarioRepository.findByRol_Nombre(RoleName.DOCENTE));
            return "rector/form-asignatura";
        }
    }

    @GetMapping("/asignaturas/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("asignatura", asignaturaService.obtenerPorId(id));
        model.addAttribute("docentes", usuarioRepository.findByRol_Nombre(RoleName.DOCENTE));
        return "rector/form-asignatura";
    }

    @PostMapping("/asignaturas/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("asignatura") Asignatura asignatura,
                             BindingResult br,
                             @RequestParam("docenteId") Long docenteId,
                             Model model) {
        if (br.hasErrors()) {
            model.addAttribute("docentes", usuarioRepository.findByRol_Nombre(RoleName.DOCENTE));
            return "rector/form-asignatura";
        }
        try {
            Usuario docente = usuarioRepository.findById(docenteId).orElseThrow();
            asignatura.setDocenteEncargado(docente);
            asignaturaService.actualizarComoRector(id, asignatura);
            return "redirect:/rector/asignaturas";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("docentes", usuarioRepository.findByRol_Nombre(RoleName.DOCENTE));
            return "rector/form-asignatura";
        }
    }

    @PostMapping("/asignaturas/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        asignaturaService.eliminarComoRector(id);
        return "redirect:/rector/asignaturas";
    }
}
