package com.example.lease_service.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.lease_service.model.Lease;
import com.example.lease_service.service.LeaseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {
    
    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    // 🛡️ ENDPOINT EXCLUSIVO PARA EL COLADOR (Consistencia inter-servicios)
    // El auth-service llamará aquí para saber si el loft está arrendado legalmente
    @GetMapping("/validar-loft/{loftId}")
    public ResponseEntity<Boolean> validarLoftActivo(@PathVariable Long loftId) {
        boolean estaArrendado = leaseService.verificarLoftArrendado(loftId);
        return ResponseEntity.ok(estaArrendado);
    }

    // Crear un nuevo contrato de arriendo
    @PostMapping
    public ResponseEntity<?> createLease(@Valid @RequestBody Lease lease) {
        try {
            // 🛡️ EL COLADOR: Consultamos al tenant-service a través del Gateway si el inquilino realmente existe
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String urlValidarTenant = "http://localhost:8080/api/tenants/" + lease.getTenantId();
            
            try {
                // Intenta buscar al inquilino por su ID. Si existe, responderá un estatus exitoso.
                restTemplate.getForEntity(urlValidarTenant, Object.class);
            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                // Interceptamos si el tenant-service devuelve 404 (No existe) y frenamos con un 400 controlado
                return ResponseEntity.badRequest().body("Operación rechazada: El inquilino con ID " + lease.getTenantId() + " no existe en el sistema. Debe registrar al arrendatario antes de crear un contrato.");
            } catch (Exception e) {
                // Si el microservicio vecino está apagado o no responde en la red, protegemos el flujo congelando el registro
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error técnico de consistencia: El tenant-service no responde a través del API Gateway.");
            }

            // Si el inquilino sí existe y pasó la aduana, se ejecuta tu guardado original en db_leases
            return new ResponseEntity<>(leaseService.saveLease(lease), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener la lista completa de contratos registrados en db_leases
    @GetMapping
    public List<Lease> getAllLeases() {
        return leaseService.getAllLeases();
    }

    // Obtener un contrato de arriendo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Lease> getLeaseById(@PathVariable Long id) {
        return leaseService.getLeaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar contrato existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLease(@PathVariable Long id, @Valid @RequestBody Lease lease) {
        try {
            return ResponseEntity.ok(leaseService.updateLease(id, lease));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar un contrato del sistema por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable Long id) {
        leaseService.deleteLease(id);
        return ResponseEntity.noContent().build();
    }
}