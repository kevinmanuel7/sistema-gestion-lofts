package com.loftmanager.auth_service.controller;

import com.loftmanager.auth_service.dto.AuthResponse;
import com.loftmanager.auth_service.dto.LoginRequest;
import com.loftmanager.auth_service.model.Usuario;
import com.loftmanager.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST: /api/auth/register (PÚBLICO)
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Usuario request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST: /api/auth/login (PÚBLICO)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // GET: /api/auth/users (REQUIERE TOKEN)
    @GetMapping("/users")
    public ResponseEntity<List<Usuario>> getAll() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // GET: /api/auth/users/{id} (REQUIERE TOKEN)
    @GetMapping("/users/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return authService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: /api/auth/users/{id} (REQUIERE TOKEN)
    @PutMapping("/users/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario user) {
        return ResponseEntity.ok(authService.updateUser(id, user));
    }

    // DELETE: /api/auth/users/{id} (REQUIERE TOKEN)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado correctamente de los registros");
    }
}