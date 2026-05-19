package com.inventory.inventory_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("👉 [RADAR] Petición recibida a la URL: " + request.getRequestURI());

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Petición rechazada: No trae Header Authorization o no empieza con Bearer.");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        System.out.println("Token capturado. Intentando validar firma...");

        try {
            if (jwtUtil.isTokenValid(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                String role = jwtUtil.extractRole(jwt);
                
                System.out.println("TOKEN VÁLIDO. Usuario: " + username + " | Rol: " + role);

                String roleWithPrefix = (role != null && !role.startsWith("ROLE_")) ? "ROLE_" + role : role;
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix)));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.out.println("El método isTokenValid devolvió FALSE (Posible firma incorrecta).");
            }
        } catch (Exception e) {
            System.out.println("Explotó la validación del Token: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}