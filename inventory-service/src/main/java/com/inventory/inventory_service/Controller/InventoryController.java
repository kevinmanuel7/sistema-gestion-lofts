package com.inventory.inventory_service.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.inventory_service.Model.Inventory;
import com.inventory.inventory_service.Service.InventoryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/by-loft/{id_loft}")
    public ResponseEntity<List<Inventory>> obtenerMueblesPorLoft(@PathVariable("id_loft") Long id_loft) {
        List<Inventory> muebles = inventoryService.buscarPorLoft(id_loft);
        return ResponseEntity.ok(muebles); 
    }

    @GetMapping("/listar")
    public List<Inventory> listar() {
        return inventoryService.listarTodos();
    }

    @PostMapping("/crear")
    public ResponseEntity<Inventory> crear(@Valid @RequestBody Inventory inventory) {
        Inventory nuevoItem = inventoryService.guardar(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoItem);
    }

    @GetMapping("/test")
    public String test() {
        return "El servicio de inventario funciona";
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}