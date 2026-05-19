package com.loftmanager.loftservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDto {
    private Long idInventory;
    private String muebles; 
    private Long idLoft;
}