package usco.edu.co.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usco.edu.co.models.*;
import usco.edu.co.repositories.AsignaturaRepository;
import usco.edu.co.repositories.UsuarioRepository;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final UsuarioRepository usuarioRepository;

    public List<Asignatura> listarTodas() {
        return asignaturaRepository.findAll();
    }

    public List<Asignatura> listarPorDocente(Usuario docente) {
        return asignaturaRepository.findByDocenteEncargado(docente);
    }

    public Asignatura obtenerPorId(Long id) {
        return asignaturaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada"));
    }

    // Operaciones Rector
    public Asignatura crearComoRector(Asignatura a) {
        validarCampos(a);
        validarDocente(a.getDocenteEncargado());
        validarHorario(a.getHorarioInicio(), a.getHorarioFin());
        validarNoSolapamientos(a, null);
        return asignaturaRepository.save(a);
    }

    public Asignatura actualizarComoRector(Long id, Asignatura datos) {
        Asignatura existente = obtenerPorId(id);
        validarCampos(datos);
        validarDocente(datos.getDocenteEncargado());
        validarHorario(datos.getHorarioInicio(), datos.getHorarioFin());
        validarNoSolapamientos(datos, id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setSalon(datos.getSalon());
        existente.setHorarioInicio(datos.getHorarioInicio());
        existente.setHorarioFin(datos.getHorarioFin());
        existente.setDocenteEncargado(datos.getDocenteEncargado());
        return asignaturaRepository.save(existente);
    }

    public void eliminarComoRector(Long id) {
        asignaturaRepository.deleteById(id);
    }

    // Operación Docente: solo horario
    public Asignatura actualizarHorarioComoDocente(Long id, Usuario docente, LocalTime inicio, LocalTime fin) {
        Asignatura existente = obtenerPorId(id);
        if (!existente.getDocenteEncargado().getId().equals(docente.getId())) {
            throw new SecurityException("No puede modificar una asignatura que no es suya");
        }
        validarHorario(inicio, fin);
        // Comprobar solapamientos con el nuevo horario, por docente y por salón
        if (!asignaturaRepository.findConflictsByDocenteExcludingId(docente, inicio, fin, id).isEmpty()) {
            throw new IllegalArgumentException("El nuevo horario se solapa con otra asignatura del docente");
        }
        if (!asignaturaRepository.findConflictsBySalonExcludingId(existente.getSalon(), inicio, fin, id).isEmpty()) {
            throw new IllegalArgumentException("El nuevo horario se solapa con otra asignatura en el mismo salón");
        }
        existente.setHorarioInicio(inicio);
        existente.setHorarioFin(fin);
        return asignaturaRepository.save(existente);
    }

    private void validarCampos(Asignatura a) {
        if (a.getNombre() == null || a.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (a.getNombre().length() > 30)
            throw new IllegalArgumentException("El nombre no debe superar 30 caracteres");
        if (!a.getNombre().matches("^[\\p{L}\\p{N} ]+$"))
            throw new IllegalArgumentException("El nombre solo puede contener letras, números y espacios");
        if (a.getDescripcion() == null || a.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        if (a.getDescripcion().length() > 100)
            throw new IllegalArgumentException("La descripción no debe superar 100 caracteres");
        if (a.getSalon() < 1)
            throw new IllegalArgumentException("El salón debe ser numérico y mayor o igual a 1");
    }

    private void validarDocente(Usuario docente) {
        if (docente == null) throw new IllegalArgumentException("Docente es obligatorio");
        Usuario actual = usuarioRepository.findById(docente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Docente no existe"));
        if (actual.getRol() == null || actual.getRol().getNombre() != RoleName.DOCENTE) {
            throw new IllegalArgumentException("El usuario asignado no tiene rol DOCENTE");
        }
    }

    private void validarHorario(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) throw new IllegalArgumentException("Horario inválido: ambos campos son obligatorios");
        if (!fin.isAfter(inicio)) throw new IllegalArgumentException("El horario de fin debe ser posterior al de inicio");
    }

    private void validarNoSolapamientos(Asignatura a, Long excludeId) {
        // Solapamiento por docente (misma persona con otra asignatura a la vez)
        boolean conflictoDocente = (excludeId == null)
                ? !asignaturaRepository.findConflictsByDocente(a.getDocenteEncargado(), a.getHorarioInicio(), a.getHorarioFin()).isEmpty()
                : !asignaturaRepository.findConflictsByDocenteExcludingId(a.getDocenteEncargado(), a.getHorarioInicio(), a.getHorarioFin(), excludeId).isEmpty();
        if (conflictoDocente) {
            throw new IllegalArgumentException("El horario se solapa con otra asignatura del docente");
        }
        // Solapamiento por salón (dos clases en el mismo salón a la vez)
        boolean conflictoSalon = (excludeId == null)
                ? !asignaturaRepository.findConflictsBySalon(a.getSalon(), a.getHorarioInicio(), a.getHorarioFin()).isEmpty()
                : !asignaturaRepository.findConflictsBySalonExcludingId(a.getSalon(), a.getHorarioInicio(), a.getHorarioFin(), excludeId).isEmpty();
        if (conflictoSalon) {
            throw new IllegalArgumentException("El horario se solapa con otra asignatura en el mismo salón");
        }
    }

    // Métodos adicionales para API REST
    public java.util.Optional<Asignatura> buscarPorId(Long id) {
        return asignaturaRepository.findById(id);
    }

    public List<Asignatura> buscarPorDocente(Long docenteId) {
        Usuario docente = usuarioRepository.findById(docenteId)
                .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
        return asignaturaRepository.findByDocenteEncargado(docente);
    }

    public void eliminar(Long id) {
        asignaturaRepository.deleteById(id);
    }
}
