package usco.edu.co.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "asignaturas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre no debe superar 30 caracteres")
    @Pattern(regexp = "^[\\p{L}\\p{N} ]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no debe superar 100 caracteres")
    private String descripcion;

    @Min(value = 1, message = "El salón debe ser numérico y mayor o igual a 1")
    private int salon;

    @NotNull(message = "El horario de inicio es obligatorio")
    private LocalTime horarioInicio;

    @NotNull(message = "El horario de fin es obligatorio")
    private LocalTime horarioFin;

    @ManyToOne(optional = false)
    @JoinColumn(name = "docente_id")
    private Usuario docenteEncargado; // debe tener rol DOCENTE, validado en el servicio
}
