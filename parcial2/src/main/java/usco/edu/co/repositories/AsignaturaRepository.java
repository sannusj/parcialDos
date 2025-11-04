package usco.edu.co.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usco.edu.co.models.Asignatura;
import usco.edu.co.models.Usuario;

import java.time.LocalTime;
import java.util.List;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    List<Asignatura> findByDocenteEncargado(Usuario docente);

    @Query("select a from Asignatura a where a.docenteEncargado = :docente and (:inicio < a.horarioFin and :fin > a.horarioInicio)")
    List<Asignatura> findConflictsByDocente(@Param("docente") Usuario docente,
                                            @Param("inicio") LocalTime inicio,
                                            @Param("fin") LocalTime fin);

    @Query("select a from Asignatura a where a.docenteEncargado = :docente and a.id <> :excludeId and (:inicio < a.horarioFin and :fin > a.horarioInicio)")
    List<Asignatura> findConflictsByDocenteExcludingId(@Param("docente") Usuario docente,
                                                       @Param("inicio") LocalTime inicio,
                                                       @Param("fin") LocalTime fin,
                                                       @Param("excludeId") Long excludeId);

    @Query("select a from Asignatura a where a.salon = :salon and (:inicio < a.horarioFin and :fin > a.horarioInicio)")
    List<Asignatura> findConflictsBySalon(@Param("salon") int salon,
                                          @Param("inicio") LocalTime inicio,
                                          @Param("fin") LocalTime fin);

    @Query("select a from Asignatura a where a.salon = :salon and a.id <> :excludeId and (:inicio < a.horarioFin and :fin > a.horarioInicio)")
    List<Asignatura> findConflictsBySalonExcludingId(@Param("salon") int salon,
                                                     @Param("inicio") LocalTime inicio,
                                                     @Param("fin") LocalTime fin,
                                                     @Param("excludeId") Long excludeId);
}
