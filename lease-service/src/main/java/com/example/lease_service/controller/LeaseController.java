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

    // El auth-service llamará aquí para saber si el loft está arrendado legalmente
    @GetMapping("/validar-loft/{loftId}")
    public ResponseEntity<Boolean> validarLoftActivo(@PathVariable Long loftId) {
        boolean estaArrendado = leaseService.verificarLoftArrendado(loftId);
        return ResponseEntity.ok(estaArrendado);
    }

    //POST Crear un nuevo contrato de arriendo
    @PostMapping
    public ResponseEntity<?> createLease(@Valid @RequestBody Lease lease) {
        try {
            // Delegamos toda la lógica y validaciones (con su token) directamente al LeaseService
            return new ResponseEntity<>(leaseService.saveLease(lease), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //GET Obtener la lista completa de contratos registrados en db_leases
    @GetMapping
    public List<Lease> getAllLeases() {
        return leaseService.getAllLeases();
    }

    //GET Obtener un contrato de arriendo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Lease> getLeaseById(@PathVariable Long id) {
        return leaseService.getLeaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //PUT Actualizar contrato existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLease(@PathVariable Long id, @Valid @RequestBody Lease lease) {
        try {
            return ResponseEntity.ok(leaseService.updateLease(id, lease));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //DELETE Eliminar un contrato del sistema por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable Long id) {
        leaseService.deleteLease(id);
        return ResponseEntity.noContent().build();
    }
}