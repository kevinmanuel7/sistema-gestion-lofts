package com.inventory.inventory_service.security;

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
                    // VER inventario (Cualquier usuario con token)
                    .requestMatchers(HttpMethod.GET, "/api/inventory", "/api/inventory/**").authenticated()
                    
                    // CREAR, EDITAR, BORRAR (Solo ADMIN). Cubrimos la ruta exacta y sus sub-rutas
                    .requestMatchers(HttpMethod.POST, "/api/inventory", "/api/inventory/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/inventory", "/api/inventory/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/inventory", "/api/inventory/**").hasAuthority("ROLE_ADMIN")
                    
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