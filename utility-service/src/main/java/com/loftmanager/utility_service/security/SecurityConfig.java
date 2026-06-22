package com.loftmanager.utility_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SwaggerBypassFilter swaggerBypassFilter;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, SwaggerBypassFilter swaggerBypassFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.swaggerBypassFilter = swaggerBypassFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                // Rutas públicas SIEMPRE permitidas
                auth.requestMatchers("/api/utility/status", "/error").permitAll();
                // Swagger SIEMPRE permitido en desarrollo
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
                
                // Aquí está la magia: el resto de la seguridad se aplica
                // pero si estamos en perfil "dev", permitimos todo para facilitar el testeo
                auth.anyRequest().authenticated();
            })
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .addFilterBefore(swaggerBypassFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}