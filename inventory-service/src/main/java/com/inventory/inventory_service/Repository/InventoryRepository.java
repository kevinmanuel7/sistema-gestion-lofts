package com.inventory.inventory_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.inventory_service.Model.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
}
