package com.inventory.inventory_service.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ConditionalOnDiscoveryHealthIndicatorEnabled;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.inventory_service.Model.Inventory;
import com.inventory.inventory_service.Repository.InventoryRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/listar")
    public List<Inventory> listar(){
        return inventoryRepository.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<Inventory> crear(@Valid @RequestBody Inventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventory);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventoryRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}