package com.loftmanager.loftservice.cliente;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.loftmanager.loftservice.dto.InventoryDto;

@FeignClient(name = "inventory-service", url = "http://localhost:8084")
public interface InventoryClient {

       @GetMapping("/api/inventory/loft/{id_loft}")
    InventoryDto getItemsByLoftId(@PathVariable("id_loft") Long id_loft);
}