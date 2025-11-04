package usco.edu.co.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Asignación de Asignaturas - Colegio USCO")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de asignación de asignaturas en un colegio.
                                
                                **Funcionalidades:**
                                - Gestión de asignaturas (CRUD completo)
                                - Consulta de asignaturas por docente
                                - Validación de horarios y salones
                                - Control de acceso por roles (Rector, Docente, Estudiante)
                                
                                **Roles:**
                                - **RECTOR**: Puede crear, modificar y eliminar asignaturas
                                - **DOCENTE**: Puede modificar horarios de sus asignaturas
                                - **ESTUDIANTE**: Puede consultar asignaturas disponibles
                                """)
                        .contact(new Contact()
                                .name("Universidad Surcolombiana")
                                .email("contacto@usco.edu.co")
                                .url("https://www.usco.edu.co"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo Local")
                ));
    }
}

