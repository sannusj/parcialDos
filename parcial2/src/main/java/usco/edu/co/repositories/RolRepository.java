package usco.edu.co.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import usco.edu.co.models.Rol;
import usco.edu.co.models.RoleName;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RoleName nombre);
}

