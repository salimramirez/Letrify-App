package com.letrify.app.config;

import com.letrify.app.service.UserDetailsServiceImpl;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService; // Servicio personalizado para cargar usuarios desde la base de datos

    // Constructor con inyección de dependencias
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // @Bean
    // public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    //     UserDetails user = User.builder()
    //             .username("admin")
    //             .password(passwordEncoder.encode("password"))
    //             .roles("USER")
    //             .build();

    //     return new InMemoryUserDetailsManager(user);
    // }

    // Bean para codificación de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración del AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) throws Exception {
        return new ProviderManager(
            List.of(new DaoAuthenticationProvider() {{
                setUserDetailsService(userDetailsService); // Configura el servicio para cargar usuarios
                setPasswordEncoder(passwordEncoder); // Configura el codificador de contraseñas
            }})
        );
    }

    // Configuración del SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf // Configuración CSRF (opcional, usa predeterminado si no lo necesitas)
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Verificar lo siguiente:
                .ignoringRequestMatchers("/api/**") // Desactiva CSRF solo para las rutas de la API
            )
            .authorizeHttpRequests(auth -> auth
                // .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/img/**", // Permitir acceso público a los recursos estáticos
                //      "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permitir acceso a Swagger
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/site.webmanifest").permitAll() // Permitir acceso público a estos recursos
                .requestMatchers("/api/**").authenticated()   // Permite acceso a usuarios autenticados
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").hasRole("ADMIN") // Restringir acceso a Swagger a usuarios con rol ADMIN
                .anyRequest().authenticated() // Proteger las demás rutas
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true) // Redirigir tras login exitoso
                .failureUrl("/login?error") // Redirigir en caso de error
                .permitAll() // Permitir acceso público al login
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll() // Permitir acceso público al logout
            );
            // .rememberMe(rememberMe -> rememberMe
            //     .key("uniqueAndSecretKey") // Clave única para la cookie (cámbiala en producción)
            //     .tokenValiditySeconds(14 * 24 * 60 * 60) // Duración de la cookie (14 días en este caso)
            //     .rememberMeParameter("remember-me") // Nombre del parámetro del checkbox en el formulario
            // );
        return http.build();
    }
    
    
}
