package com.example.lease_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Crear un nuevo contrato de arriendo
    @PostMapping
    public ResponseEntity<?> createLease(@Valid @RequestBody Lease lease) {
        try {
            return new ResponseEntity<>(leaseService.saveLease(lease), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Obtener la lista completa de contratos registrados en db_leases
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

    //Actualizar contrato existente.
    //Vuelve  a validar la disponibilidad si se cambia fecha o loft. 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLease(@PathVariable Long id, @Valid @RequestBody Lease lease) {
        try {
            return ResponseEntity.ok(leaseService.updateLease(id, lease));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Eliminar un contrato del sistema por su ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable Long id) {
        leaseService.deleteLease(id);
        return ResponseEntity.noContent().build();
    }

}
