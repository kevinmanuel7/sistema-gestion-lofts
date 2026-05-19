package com.inventory.inventory_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.inventory.inventory_service.model.Inventory;
import com.inventory.inventory_service.repository.InventoryRepository;

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;

    // Inyectamos el RestTemplate que creamos en RestTemplateConfig
    @Autowired
    private RestTemplate restTemplate;

    public Optional<Inventory> buscarPorLoft(Long idLoft) {
        return inventoryRepository.findByIdLoft(idLoft);
    }

    public List<Inventory> listarTodos() {
        return inventoryRepository.findAll();
    }

    public Inventory guardar(Inventory inventory) {
        // REGLA DE NEGOCIO: Validar que el Loft exista en el loft-service antes de inventariar
        validarLoftExiste(inventory.getIdLoft());
        
        return inventoryRepository.save(inventory);
    }

    public void eliminar(Long id) {
        inventoryRepository.deleteById(id);
    }

    public Inventory actualizarPorLoft(Long idLoft, Inventory nuevoInventario) {
        return inventoryRepository.findByIdLoft(idLoft)
            .map(inventarioExistente -> {
                inventarioExistente.setMuebles(nuevoInventario.getMuebles());
                return inventoryRepository.save(inventarioExistente);
            })
            .orElseThrow(() -> new RuntimeException("No se encontró una ficha de inventario para el Loft ID: " + idLoft));
    }

    // --- MÉTODOS PRIVADOS DE VALIDACIÓN INTER-SERVICIOS ---

    private void validarLoftExiste(Long idLoft) {
        try {
            // 1. Extraer el Token JWT de la petición actual que hizo el Admin en Postman
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String token = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

            // 2. Preparar los Headers para reenviar el Token
            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set(HttpHeaders.AUTHORIZATION, token);
            }
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Consultar al Loft-Service a través del Gateway (Puerto 8080)
            String url = "http://localhost:8080/api/loft/" + idLoft;
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Error al validar Loft");
            }
        } catch (HttpClientErrorException.NotFound e) {
            // Si el Loft-Service devuelve un 404, lanzamos el error de negocio
            throw new IllegalArgumentException("Error de negocio: No se puede crear el inventario porque el Loft ID " + idLoft + " no existe en los registros.");
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            throw new RuntimeException("Error de Autenticación al intentar comunicarse con el Loft-Service.");
        }
    }
}