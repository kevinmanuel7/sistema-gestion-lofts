package com.loftmanager.loftservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // VER (GET) requiere estar autenticado (Cualquier rol)
                .requestMatchers(HttpMethod.GET, "/api/loft", "/api/loft/**").authenticated()
                
                // CREAR, EDITAR, BORRAR requiere rol ADMIN
                .requestMatchers(HttpMethod.POST, "/apí/loft", "/api/loft/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT,"/api/loft", "/api/loft/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/loft", "/api/loft/**").hasAuthority("ROLE_ADMIN")
                
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}