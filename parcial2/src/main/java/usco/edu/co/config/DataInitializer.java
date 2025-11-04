package usco.edu.co.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import usco.edu.co.models.*;
import usco.edu.co.repositories.*;

import java.time.LocalTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(RolRepository rolRepository,
                               UsuarioRepository usuarioRepository,
                               AsignaturaRepository asignaturaRepository) {
        return args -> {
            // Crear roles si no existen
            if (rolRepository.count() == 0) {
                rolRepository.save(new Rol(1L, RoleName.RECTOR));
                rolRepository.save(new Rol(2L, RoleName.DOCENTE));
                rolRepository.save(new Rol(3L, RoleName.ESTUDIANTE));
            }

            Rol rectorRol = rolRepository.findById(1L).orElseThrow();
            Rol docenteRol = rolRepository.findById(2L).orElseThrow();
            Rol estudianteRol = rolRepository.findById(3L).orElseThrow();

            // Usuarios demo
            if (usuarioRepository.findByNombreUsuario("rector").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .nombreUsuario("rector")
                        .contrasena(passwordEncoder.encode("rector123"))
                        .rol(rectorRol)
                        .build());
            }
            if (usuarioRepository.findByNombreUsuario("docente1").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .nombreUsuario("docente1")
                        .contrasena(passwordEncoder.encode("docente123"))
                        .rol(docenteRol)
                        .build());
            }
            if (usuarioRepository.findByNombreUsuario("estudiante1").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .nombreUsuario("estudiante1")
                        .contrasena(passwordEncoder.encode("estudiante123"))
                        .rol(estudianteRol)
                        .build());
            }

            // Asignatura demo
            if (asignaturaRepository.count() == 0) {
                Usuario docente = usuarioRepository.findByNombreUsuario("docente1").orElseThrow();
                asignaturaRepository.save(Asignatura.builder()
                        .nombre("Matemáticas")
                        .descripcion("Álgebra básica")
                        .salon(101)
                        .horarioInicio(LocalTime.of(8, 0))
                        .horarioFin(LocalTime.of(10, 0))
                        .docenteEncargado(docente)
                        .build());
            }
        };
    }
}

