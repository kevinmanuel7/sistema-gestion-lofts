package com.inventory.inventory_service.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventory.inventory_service.Model.Inventory;
import com.inventory.inventory_service.Repository.InventoryRepository;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> buscarPorLoft(Long id_loft) {
        return inventoryRepository.findById_loft(id_loft);
    }

    public List<Inventory> listarTodos() {
        return inventoryRepository.findAll();
    }

    public Inventory guardar(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public void eliminar(Long id) {
        inventoryRepository.deleteById(id);
    }
}
