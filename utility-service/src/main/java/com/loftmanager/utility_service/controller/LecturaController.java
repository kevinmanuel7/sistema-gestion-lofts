package com.loftmanager.utility_service.controller;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.service.LecturaService;
import com.loftmanager.utility_service.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// Importaciones de la documentacion de Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/utility")
@Tag(name = "Utility - Consumos Electricos", description = "Endpoints para la gestion de lecturas de energia y cobros de los lofts.")
public class LecturaController {

    private final LecturaService lecturaService;
    private final JwtUtil jwtUtil;

    public LecturaController(LecturaService lecturaService, JwtUtil jwtUtil) {
        this.lecturaService = lecturaService;
        this.jwtUtil = jwtUtil;
    }

    // FUNCIÓN AUXILIAR DE SEGURIDAD (Centraliza la validación de cargos)
    private boolean esAdminOOperador(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("ROLE_ADMIN") || r.getAuthority().equalsIgnoreCase("ADMIN")
                            || r.getAuthority().equalsIgnoreCase("ROLE_OPERADOR") || r.getAuthority().equalsIgnoreCase("OPERADOR"));
    }

    // LEER GET: /api/utility/status (PÚBLICO)
    @Operation(summary = "Verificar estado del servicio", description = "Comprueba si el microservicio se encuentra activo y respondiendo.")
    @ApiResponse(responseCode = "200", description = "Servicio activo y en ejecucion.")
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Utility-Service protegido por JWT y en ejecución.");
    }

    // LEER GET: /api/utility/lecturas (LISTAR TODO - PRIVADO ADMIN/OPERADOR)
    @Operation(summary = "Listar todas las lecturas", description = "Obtiene el historial completo de lecturas electricas registradas. Requiere rol ADMIN o OPERADOR.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida con exito."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: Permisos insuficientes.")
    })
    @GetMapping("/lecturas")
    public ResponseEntity<?> getAll(Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: No tienes permisos para listar todas las lecturas del complejo.");
        }
        return ResponseEntity.ok(lecturaService.getAllLecturas());
    }

    // LEER GET: /api/utility/lecturas/{id} (PRIVADO ADMIN/OPERADOR)
    @Operation(summary = "Consultar lectura por ID", description = "Recupera un registro de consumo especifico a partir de su ID. Requiere rol ADMIN o OPERADOR.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lectura localizada."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado."),
        @ApiResponse(responseCode = "404", description = "Lectura no encontrada.")
    })
    @GetMapping("/lecturas/{id}")
    public ResponseEntity<?> getById(@Parameter(description = "ID unico de la lectura", example = "1") @PathVariable Long id, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Permisos insuficientes para consultar lecturas por ID.");
        }
        return lecturaService.getLecturaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // LEER GET: /api/utility/lecturas/loft/{idLoftFk} (PRIVADO ADMIN/OPERADOR)
    @Operation(summary = "Listar lecturas por Loft", description = "Filtra el historial de consumos de un loft especifico. Requiere rol ADMIN o OPERADOR.", security = @SecurityRequirement(name = "BearerAuth"))
    @GetMapping("/lecturas/loft/{idLoftFk}")
    public ResponseEntity<?> getByLoft(@Parameter(description = "ID del Loft para filtrar", example = "3") @PathVariable Long idLoftFk, Authentication authentication) {
        if (!esAdminOOperador(authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acceso denegado: Los usuarios inquilinos no pueden mapear lecturas de otros Lofts.");
        }
        return ResponseEntity.ok(lecturaService.getLecturasByLoft(idLoftFk));
    }

    // LEER GET: /api/utility/lecturas/mi-loft (PÚBLICO PARA INQUILINOS - ULTRA SEGURO)
    @Operation(summary = "Consultar consumos de mi loft", description = "Permite a un inquilino obtener sus propios consumos, extrayendo el ID asignado directamente de su token JWT.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial del inquilino cargado."),
        @ApiResponse(responseCode = "400", description = "El token no cuenta con un Loft asociado."),
        @ApiResponse(responseCode = "401", description = "Token ausente o invalido.")
    })
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
    @Operation(summary = "Registrar nueva lectura", description = "Registra una lectura mensual. Realiza los calculos automaticos de consumo y cobros. Requiere rol ADMIN o OPERADOR.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Registro creado con exito."),
        @ApiResponse(responseCode = "400", description = "Inconsistencia en los datos enviados o fuera de rango."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado.")
    })
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
    @Operation(summary = "Actualizar lectura existente", description = "Modifica un registro existente del historial de consumos y recalcula el monto financiero. Requiere rol ADMIN o OPERADOR.", security = @SecurityRequirement(name = "BearerAuth"))
    @PutMapping("/lecturas/{id}")
    public ResponseEntity<?> update(@Parameter(description = "ID del registro a modificar") @PathVariable Long id, @RequestBody Lectura lectura, Authentication authentication) {
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
    @Operation(summary = "Eliminar registro historico", description = "Remueve permanentemente una lectura de la base de datos de auditoria. Requiere rol ADMIN.", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Registro eliminado con exito."),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: Operacion de maxima seguridad."),
        @ApiResponse(responseCode = "404", description = "ID de lectura no encontrado.")
    })
    @DeleteMapping("/lecturas/{id}")
    public ResponseEntity<?> delete(@Parameter(description = "ID del registro a eliminar") @PathVariable Long id, Authentication authentication) {
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