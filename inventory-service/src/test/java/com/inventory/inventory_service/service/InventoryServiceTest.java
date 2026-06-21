package com.inventory.inventory_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.inventory.inventory_service.model.Inventory;
import com.inventory.inventory_service.repository.InventoryRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventarioBase;

    @BeforeEach
    void setUp() {
        inventarioBase = new Inventory();
        inventarioBase.setIdLoft(1L);
        inventarioBase.setMuebles("Sillón, Cama, Comedor, Microondas");
    }

    @Test
    void guardarInventory_ErrorLoftNoExiste() {
        // GIVEN (Simulamos que el RestTemplate lanza un 404 NOT FOUND al consultar el Loft)
        HttpClientErrorException.NotFound excepcionHttp = (HttpClientErrorException.NotFound) HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, 
                "Not Found", 
                null, null, null
        );

        when(restTemplate.exchange(
                contains("/api/loft/"), 
                eq(HttpMethod.GET), 
                any(HttpEntity.class), 
                eq(String.class)
        )).thenThrow(excepcionHttp);

        // WHEN & THEN (Debe generar rror de negocio controlado)
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.guardar(inventarioBase);
        });

        assertTrue(excepcion.getMessage().contains("No se puede crear el inventario porque el Loft ID"));
        
        // Verificamos que nunca se llame al repositorio para guardar datos corruptos
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void actualizarPorLoft_Exitoso() {
        // GIVEN (El inventario ya existe en la BD local)
        Inventory inventarioExistente = new Inventory();
        inventarioExistente.setIdLoft(1L);
        inventarioExistente.setMuebles("Muebles Viejos");

        when(inventoryRepository.findByIdLoft(1L)).thenReturn(Optional.of(inventarioExistente));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventarioExistente);

        // WHEN
        Inventory resultado = inventoryService.actualizarPorLoft(1L, inventarioBase);

        // THEN 
        assertNotNull(resultado);
        assertEquals("Sillón, Cama, Comedor, Microondas", resultado.getMuebles());
        verify(inventoryRepository, times(1)).save(inventarioExistente);
    }

    @Test
    void actualizarPorLoft_ErrorNoEncontrado() {
        // GIVEN 
        when(inventoryRepository.findByIdLoft(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inventoryService.actualizarPorLoft(1L, inventarioBase);
        });

        assertEquals("No se encontró una ficha de inventario para el Loft ID: 1", excepcion.getMessage());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}