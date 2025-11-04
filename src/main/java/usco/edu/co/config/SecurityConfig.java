package usco.edu.co.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import usco.edu.co.services.UsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) {
        return usuarioService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var auth = authentication.getAuthorities();
            String redirectUrl = "/";
            if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECTOR"))) {
                redirectUrl = "/rector/asignaturas";
            } else if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCENTE"))) {
                redirectUrl = "/docente/mis-asignaturas";
            } else if (auth.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"))) {
                redirectUrl = "/estudiante/asignaturas";
            }
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler,
                                                   DaoAuthenticationProvider provider) throws Exception {
        http
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/acceso-denegado").permitAll()
                        // Swagger/OpenAPI Documentation (público para todos)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                        // API REST endpoints (público para consultas)
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/rector/**").hasRole("RECTOR")
                        .requestMatchers("/docente/**").hasRole("DOCENTE")
                        .requestMatchers("/estudiante/**").hasRole("ESTUDIANTE")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                )
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))
                .logout(logout -> logout
                        // Aceptar logout tanto por POST (recomendado) como por GET (fallback)
                        .logoutRequestMatcher(new OrRequestMatcher(
                                new AntPathRequestMatcher("/logout", "POST"),
                                new AntPathRequestMatcher("/logout", "GET")
                        ))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // Deshabilitar CSRF solo para API REST
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        return http.build();
    }
}

