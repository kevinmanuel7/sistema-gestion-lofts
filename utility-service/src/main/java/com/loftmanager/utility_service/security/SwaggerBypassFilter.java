package com.loftmanager.utility_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SwaggerBypassFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String referer = request.getHeader("Referer");
        String requestURI = request.getRequestURI();

        // Si la petición viene de la página de Swagger, inyectamos credenciales de ADMIN
        if ((referer != null && referer.contains("swagger-ui")) || requestURI.contains("/swagger-ui") || requestURI.contains("/v3/api-docs")) {
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            UsernamePasswordAuthenticationToken fakeAuth = 
                    new UsernamePasswordAuthenticationToken("swagger_admin", null, authorities);
            
            SecurityContextHolder.getContext().setAuthentication(fakeAuth);
        }

        filterChain.doFilter(request, response);
    }
}