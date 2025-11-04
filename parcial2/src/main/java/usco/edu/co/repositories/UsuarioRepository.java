package usco.edu.co.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import usco.edu.co.models.Usuario;
import usco.edu.co.models.RoleName;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    List<Usuario> findByRol_Nombre(RoleName nombre);
}
