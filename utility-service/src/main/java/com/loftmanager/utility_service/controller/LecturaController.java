package com.loftmanager.utility_service.controller;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.service.LecturaService;
import com.loftmanager.utility_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utility")
public class LecturaController {

    private final LecturaService lecturaService;
    private final JwtUtil jwtUtil;

    public LecturaController(LecturaService lecturaService, JwtUtil jwtUtil) {
        this.lecturaService = lecturaService;
        this.jwtUtil = jwtUtil;
    }

    // 🛡️ FUNCIÓN AUXILIAR DE SEGURIDAD (Centraliza la validación de cargos)
    private boolean esAdminOOperador(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || r.getAuthority().equalsIgnoreCase("ADMIN")
                            || r.getAuthority().equalsIgnoreCase("ROLE_OPERADOR") || r.getAuthority().equalsIgnoreCase("OPERADOR"));
    }

    // LEER GET: /api/utility/status (PÚBLICO)
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Utility-Service protegido por JWT y en ejecución.");
    }

    // LEER GET: /api/utility/lecturas (LISTAR TODO - PRIVADO ADMIN/OPERADOR)
    @GetMapping("/lecturas")
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: No tienes permisos para listar todas las lecturas del complejo.");
        }
        return ResponseEntity.ok(lecturaService.getAllLecturas());
    }

    // LEER GET: /api/utility/lecturas/{id} (PRIVADO ADMIN/OPERADOR)
    @GetMapping("/lecturas/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Permisos insuficientes para consultar lecturas por ID.");
        }
        return lecturaService.getLecturaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // LEER GET: /api/utility/lecturas/loft/{idLoftFk} (PRIVADO ADMIN/OPERADOR)
    @GetMapping("/lecturas/loft/{idLoftFk}")
    public ResponseEntity<?> getByLoft(@PathVariable Long idLoftFk, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Los usuarios inquilinos no pueden mapear lecturas de otros Lofts.");
        }
        return ResponseEntity.ok(lecturaService.getLecturasByLoft(idLoftFk));
    }

    // LEER GET: /api/utility/lecturas/mi-loft (PÚBLICO PARA INQUILINOS - ULTRA SEGURO)
    @GetMapping("/lecturas/mi-loft")
    public ResponseEntity<?> getLecturasMiLoft(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente o inválido.");
        }
        String token = authHeader.substring(7);
        Long idLoftAsignado = jwtUtil.extractIdLoft(token);
        
        if (idLoftAsignado == null) {
            return ResponseEntity.badRequest().body("Error: Este usuario no tiene ningún Loft asociado en el token.");
        }
        return ResponseEntity.ok(lecturaService.getLecturasByLoft(idLoftAsignado));
    }

    // CREAR POST: /api/utility/lecturas (BLINDADO - SOLO ADMIN/OPERADOR)
    @PostMapping("/lecturas")
    public ResponseEntity<?> create(@RequestBody Lectura lectura, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Solo el Administrador o el Operador pueden ingresar nuevas lecturas al medidor.");
        }
        try {
            Lectura nuevaLectura = lecturaService.createLectura(lectura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaLectura);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ACTUALIZAR PUT: /api/utility/lecturas/{id} (BLINDADO - SOLO ADMIN/OPERADOR)
    @PutMapping("/lecturas/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Lectura lectura, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: No tienes permisos para modificar el historial de consumos.");
        }
        try {
            return ResponseEntity.ok(lecturaService.updateLectura(id, lectura));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ELIMINAR DELETE: /api/utility/lecturas/{id} (BLINDADO - SOLO ADMIN)
    @DeleteMapping("/lecturas/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        // Validación estricta: Borrar registros es una acción crítica que solo le pertenece al dueñoo/ADMIN
        boolean esAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || r.getAuthority().equalsIgnoreCase("ADMIN"));

        if (!esAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Operación crítica. Solo el Administrador general puede eliminar registros de auditoría.");
        }
        try {
            lecturaService.deleteLectura(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}