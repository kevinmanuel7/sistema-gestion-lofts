package com.inventory.inventory_service.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inventory.inventory_service.Model.Inventory;

import feign.Param;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // Usamos @Query porque el nombre con guion bajo confunde al generador automático
    @Query("SELECT i FROM Inventory i WHERE i.id_loft = :idLoft")
    List<Inventory> findById_loft(@Param("idLoft") Long idLoft);
}