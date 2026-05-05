package com.example.tenant_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.example.tenant_service.modelo.Tenant;
import com.example.tenant_service.service.TenantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/tenants")

public class TenantController {
    
    @Autowired
    private TenantService service;

    @GetMapping
    public ResponseEntity<List<Tenant>> listar(){
          //retorno 200 OK con la lista.
          return ResponseEntity.ok(service.listarTodos());

    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Tenant tenant){
      try{
        Tenant newTenant = service.guardar(tenant);
        //retorno 201 Created si sale bien.
        return new ResponseEntity<>(newTenant, HttpStatus.CREATED);

      } catch(RuntimeException e){
        //retorna 400 Bad Request si la lógica del negocio falla (RUT Duplicado)
        return ResponseEntity.badRequest().body(e.getMessage());
      }

    }

    
    
    


}
