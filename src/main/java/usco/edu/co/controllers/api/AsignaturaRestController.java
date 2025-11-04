package usco.edu.co.controllers.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import usco.edu.co.models.Asignatura;
import usco.edu.co.services.AsignaturaService;

import java.util.List;

/**
 * Controlador REST para la gestión de Asignaturas
 * Proporciona endpoints para operaciones CRUD y consultas
 */
@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
@Tag(name = "1. Asignaturas", description = "Gestión de asignaturas del colegio")
public class AsignaturaRestController {

    private final AsignaturaService asignaturaService;

    @GetMapping
    @Operation(
        summary = "Listar todas las asignaturas",
        description = """
                Obtiene un listado completo de todas las asignaturas registradas en el sistema.
                
                **Acceso:** Público (no requiere autenticación)
                
                **Retorna:** Lista de asignaturas con toda su información (nombre, descripción, salón, horarios, docente encargado)
                """
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de asignaturas obtenida exitosamente",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Asignatura.class))
        )
    )
    public ResponseEntity<List<Asignatura>> listarTodas() {
        List<Asignatura> asignaturas = asignaturaService.listarTodas();
        return ResponseEntity.ok(asignaturas);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener asignatura por ID",
        description = """
                Busca y retorna los detalles completos de una asignatura específica mediante su identificador único.
                
                **Acceso:** Público (no requiere autenticación)
                
                **Retorna:** Información detallada de la asignatura incluyendo:
                - Nombre y descripción
                - Número de salón
                - Horario de inicio y fin
                - Docente encargado
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Asignatura encontrada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Asignatura.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Asignatura no encontrada con el ID proporcionado",
            content = @Content
        )
    })
    public ResponseEntity<Asignatura> obtenerPorId(
            @Parameter(
                description = "ID único de la asignatura",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        return asignaturaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('RECTOR')")
    @Operation(
        summary = "Crear nueva asignatura",
        description = """
                Crea una nueva asignatura en el sistema con todas sus propiedades.
                
                **Acceso:** Solo usuarios con rol RECTOR
                
                **Validaciones automáticas:**
                - Nombre: obligatorio, máximo 30 caracteres, solo letras, números y espacios
                - Descripción: obligatoria, máximo 100 caracteres
                - Salón: número entero mayor o igual a 1
                - Horario de fin debe ser posterior al horario de inicio
                - No puede haber solapamiento de horarios con otras asignaturas del mismo docente
                - No puede haber solapamiento de horarios en el mismo salón
                - El docente asignado debe tener el rol DOCENTE
                
                **Ejemplo de JSON:**
                ```json
                {
                  "nombre": "Matemáticas",
                  "descripcion": "Curso de matemáticas avanzadas",
                  "salon": 101,
                  "horarioInicio": "08:00:00",
                  "horarioFin": "10:00:00",
                  "docenteEncargado": {
                    "id": 2
                  }
                }
                ```
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Asignatura creada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Asignatura.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o validación fallida. Revise el mensaje de error para más detalles",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado. Solo usuarios con rol RECTOR pueden crear asignaturas",
            content = @Content
        )
    })
    public ResponseEntity<?> crear(
            @Parameter(
                description = "Datos de la asignatura a crear",
                required = true
            )
            @Valid @RequestBody Asignatura asignatura) {
        try {
            Asignatura nuevaAsignatura = asignaturaService.crearComoRector(asignatura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAsignatura);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECTOR')")
    @Operation(
        summary = "Actualizar asignatura completa",
        description = """
                Actualiza todos los datos de una asignatura existente.
                
                **Acceso:** Solo usuarios con rol RECTOR
                
                **Validaciones:** Las mismas que para crear una asignatura
                
                **Nota:** Se aplican todas las validaciones de negocio incluyendo verificación de solapamientos
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Asignatura actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Asignatura.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Asignatura no encontrada con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o validación fallida",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado. Solo usuarios con rol RECTOR pueden actualizar asignaturas",
            content = @Content
        )
    })
    public ResponseEntity<?> actualizar(
            @Parameter(
                description = "ID de la asignatura a actualizar",
                example = "1",
                required = true
            )
            @PathVariable Long id,
            @Parameter(
                description = "Nuevos datos de la asignatura",
                required = true
            )
            @Valid @RequestBody Asignatura asignatura) {
        try {
            return asignaturaService.buscarPorId(id)
                    .map(asignaturaExistente -> {
                        Asignatura actualizada = asignaturaService.actualizarComoRector(id, asignatura);
                        return ResponseEntity.ok(actualizada);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECTOR')")
    @Operation(
        summary = "Eliminar asignatura",
        description = """
                Elimina permanentemente una asignatura del sistema.
                
                **Acceso:** Solo usuarios con rol RECTOR
                
                **Advertencia:** Esta acción no se puede deshacer
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Asignatura eliminada exitosamente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Asignatura no encontrada con el ID proporcionado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado. Solo usuarios con rol RECTOR pueden eliminar asignaturas",
            content = @Content
        )
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(
                description = "ID de la asignatura a eliminar",
                example = "1",
                required = true
            )
            @PathVariable Long id) {
        return asignaturaService.buscarPorId(id)
                .map(asignatura -> {
                    asignaturaService.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(
        summary = "Listar asignaturas por docente",
        description = """
                Obtiene todas las asignaturas asignadas a un docente específico.
                
                **Acceso:** Público (no requiere autenticación)
                
                **Caso de uso:** Permite consultar la carga académica de un docente
                """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de asignaturas del docente obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Asignatura.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Docente no encontrado con el ID proporcionado",
            content = @Content
        )
    })
    public ResponseEntity<?> listarPorDocente(
            @Parameter(
                description = "ID del docente",
                example = "2",
                required = true
            )
            @PathVariable Long docenteId) {
        try {
            List<Asignatura> asignaturas = asignaturaService.buscarPorDocente(docenteId);
            return ResponseEntity.ok(asignaturas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * Clase interna para respuestas de error
     */
    private record ErrorResponse(String mensaje) {
    }
}

