package com.inventory.inventory_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "INVENTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventory") // ID único de la ficha de inventario
    private Long idInventory;

    // columnDefinition = "TEXT" para soportar listas largas separadas por comas
    @Column(name = "muebles", nullable = false, columnDefinition = "TEXT") 
    private String muebles;

    @Column(name = "id_loft", nullable = false, unique = true) // unique = true garantiza UNA ficha por Loft
    private Long idLoft;
}