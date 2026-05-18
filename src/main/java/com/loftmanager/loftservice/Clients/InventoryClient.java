package com.loftmanager.loftservice.Clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.loftmanager.loftservice.Dtos.InventoryDto;


@FeignClient(name = "inventory-service", url = "localhost:8084") // O el nombre que uses en Eureka
public interface InventoryClient {

    @GetMapping("/api/inventory/by-loft/{id_loft}")
    List<InventoryDto> getItemsByLoftId(@PathVariable("id_loft") Long id_loft);
}