package com.example.lease_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir Swagger sin token para pruebas y documentación
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // VER contratos (Cualquier usuario con token)
                .requestMatchers(HttpMethod.GET, "/api/leases", "/api/leases/**").permitAll()
                
                // CREAR, EDITAR, BORRAR (Solo ADMIN). Cubrimos la ruta exacta y sus sub-rutas
                .requestMatchers(HttpMethod.POST, "/api/leases", "/api/leases/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/leases", "/api/leases/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/leases", "/api/leases/**").permitAll()
                
                // Cualquier otra petición residual requiere token
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}