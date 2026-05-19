package com.example.tenant_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.example.tenant_service.modelo.Tenant;
import com.example.tenant_service.service.TenantService;

@Slf4j
@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    
    private final TenantService service;

    public TenantController(TenantService service) {
        this.service = service;
    }

    // GET /api/tenants
    @GetMapping
    public ResponseEntity<List<Tenant>> listar(){
        return ResponseEntity.ok(service.listarTodos());
    }
    
    // GET /api/tenants/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> obtenerPorId(@PathVariable Long id){
        return service.buscarporId(id)
                     .map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // CORREGIDO: POST plano que recibe directamente el objeto Tenant
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Tenant tenant) {
        try {
            Tenant newTenant = service.guardar(tenant);
            log.info("Arrendatario creado con éxito: {}", newTenant.getRut());
            return new ResponseEntity<>(newTenant, HttpStatus.CREATED);
        } catch (Exception e) {
            log.warn("Error al crear arrendatario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/tenants/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Tenant tenant) {
        try {
            return ResponseEntity.ok(service.actualizar(id, tenant));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE /api/tenants/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}