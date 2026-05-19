package com.inventory.inventory_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inventory.inventory_service.model.Inventory;
import com.inventory.inventory_service.service.InventoryService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/inventory")
@SuppressWarnings("null")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    //GET BUSCAR POR IDLOFT
    @GetMapping("/loft/{id_loft}")
    public ResponseEntity<?> obtenerMueblesPorLoft(@PathVariable("id_loft") Long id_loft) {
        return inventoryService.buscarPorLoft(id_loft)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    //GET LISTAR TODOS 
        @GetMapping("/listar")
    public List<Inventory> listar() {
        return inventoryService.listarTodos();
    }

    //POST CREAR UN INVENTARIO
    @PostMapping("/crear")
    public ResponseEntity<Inventory> crear(@Valid @RequestBody Inventory inventory) {
        inventory.setIdInventory(null);
        Inventory nuevoItem = inventoryService.guardar(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoItem);
    }

    //PUT ACTUALIZAR UN INVENTARIO A TRAVES DEL IDLOFT
    @PutMapping("/actualizar/{id_loft}")
    public ResponseEntity<?> actualizar(@PathVariable("id_loft") Long id_loft, @RequestBody Inventory inventory) {
        try {
            Inventory inventarioActualizado = inventoryService.actualizarPorLoft(id_loft, inventory);
            return ResponseEntity.ok(inventarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //DELETE ELIMINAR INVENTARIO
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventoryService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}