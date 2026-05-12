package com.example.tenant_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import com.example.tenant_service.modelo.Tenant;
import com.example.tenant_service.service.TenantService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping("/api/tenants")

public class TenantController {
    
  private final TenantService service;

  public TenantController(TenantService service) {
      this.service = service;

  }

    //GET /api/tenants : Listar todos los arrendatarios.
    @GetMapping
    public ResponseEntity<List<Tenant>> listar(){
          //retorno 200 OK con la lista.
          return ResponseEntity.ok(service.listarTodos());

    }
    
    //GET /api/tenants/{id} : Buscar arrendatario por su ID.
    @GetMapping("/{id}")
    public ResponseEntity<Tenant> obtenerPorId(@PathVariable Long id){
      return service.buscarporId(id)
             .map(ResponseEntity::ok)
             .orElse(ResponseEntity.notFound().build());

    }

    //POST /api/tenants : Crear un nuevo arrendatario
    //@Valid para activar validaciones de el modelo (Rut, email)

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Tenant tenant) {
      try {
          Tenant newTenant = service.guardar(tenant);
          log.info("Arrendatario creado con éxito: {}", tenant.getRut());
          return new ResponseEntity<>(newTenant, HttpStatus.CREATED);
      } catch (RuntimeException e) {
          log.warn("Error al crear arrendatario: {}", e.getMessage());
          return ResponseEntity.badRequest().body(e.getMessage());
      }
    }

    //PUT /api/tenants/{id}: Actualiza datos de un arrendatario existente.
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Tenant tenant) {
        try {
            return ResponseEntity.ok(service.actualizar(id, tenant));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //DELETE /api/tenants/{id} : Eliminar un arrendatario del sistema por su ID.
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
