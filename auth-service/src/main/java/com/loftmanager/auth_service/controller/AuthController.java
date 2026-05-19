package com.loftmanager.auth_service.controller;

import com.loftmanager.auth_service.dto.AuthResponse;
import com.loftmanager.auth_service.dto.LoginRequest;
import com.loftmanager.auth_service.model.Usuario;
import com.loftmanager.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importado para leer el pasaporte de Spring
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //MÉTODOS AUXILIARES DE CONTROL DE ACCESO (Programación Defensiva)
    private boolean esAdminOOperador(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || r.getAuthority().equalsIgnoreCase("ADMIN")
                            || r.getAuthority().equalsIgnoreCase("ROLE_OPERADOR") || r.getAuthority().equalsIgnoreCase("OPERADOR"));
    }

    private boolean esAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || r.getAuthority().equalsIgnoreCase("ADMIN"));
    }

    // CREATE POST (PRIVADO)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Captura caídas de servidores o errores imprevistos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }

    //CREATE POST: /api/auth/login (PÚBLICO)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    //LEER GET: /api/auth/users (PRIVADO - REQUIERE PRIVILEGIOS DE PERSONAL)
    @GetMapping("/users")
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: No tienes privilegios para listar las cuentas del personal.");
        }
        return ResponseEntity.ok(authService.getAllUsers());
    }

    //LEER GET: /api/auth/users/{id} (PRIVADO - REQUIERE PRIVILEGIOS DE PERSONAL)
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Permisos insuficientes para consultar usuarios de forma individual.");
        }
        return authService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //ACTUALIZAR PUT: /api/auth/users/{id} (PRIVADO - REQUIERE PRIVILEGIOS DE PERSONAL)
    @PutMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario user, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: No tienes permisos para modificar datos de cuentas externas.");
        }
        return ResponseEntity.ok(authService.updateUser(id, user));
    }

    // DELETE: /api/auth/users/{id} (PRIVADO CRÍTICO - SOLO EL ADMINISTRADOR GENERAL)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        if (!esAdmin(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Operación crítica. Solo el Administrador general puede dar de baja cuentas del sistema.");
        }
        authService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente de los registros");
    }
}