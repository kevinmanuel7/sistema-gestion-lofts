package com.example.lease_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lease_service.model.Lease;
import com.example.lease_service.service.LeaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {
    
    @Autowired
    private LeaseService leaseService;

    // Crear un nuevo contrato de arriendo.
    @PostMapping
    public ResponseEntity<Lease> createLease(@Valid @RequestBody Lease lease) {
        
        Lease nuevoLease = leaseService.saveLease(lease);
        return new ResponseEntity<>(nuevoLease, HttpStatus.CREATED);
    }

    // Obtener todos los contratos de arriendos.
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

    // Eliminar un contrato
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLease(@PathVariable Long id) {
        leaseService.deleteLease(id);
        return ResponseEntity.noContent().build();
    }

}
