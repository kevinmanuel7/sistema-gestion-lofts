package com.example.tenant_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.tenant_service.modelo.Tenant;
import com.example.tenant_service.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("null")
public class TenantService {

    private final TenantRepository repository;

    public TenantService(TenantRepository repository, org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder) {
        this.repository = repository;
    }

    public List<Tenant> listarTodos(){
        log.info("Iniciando consulta de todos los arrendatarios registrados");
        return repository.findAll();
    }

    public Optional<Tenant> buscarporId(long id){
        log.info("Buscando arrendatario con ID: {}", id);
        return repository.findById(id);
    }

    // CORREGIDO: Guardar plano, directo y autónomo en db_tenant
    public Tenant guardar(Tenant tenant){
        log.info("Procesando registro de: {} {}", tenant.getNombre(), tenant.getApellido());

        // 1. Validaciones locales (RUT y Email)
        if (repository.existsByRut(tenant.getRut())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUT ya se encuentra registrado");
        }
        
        if (repository.existsByEmail(tenant.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya se encuentra registrado");
        }

        // 4. Guardar al inquilino de forma directa
        return repository.save(tenant);
    }

    public Tenant actualizar(Long id, Tenant tenantDetalles){
        log.info("Actualizar arrendatario con ID {}", id);
        return repository.findById(id).map(tenant ->{
            tenant.setNombre(tenantDetalles.getNombre());
            tenant.setApellido(tenantDetalles.getApellido());
            tenant.setTelefono(tenantDetalles.getTelefono());
            tenant.setEmail(tenantDetalles.getEmail());
            return repository.save(tenant);
        }).orElseThrow(() -> new RuntimeException("No se encontró el arrendatario con ID: " + id));
    }

    public void eliminar(Long id) {
        log.info("Eliminando arrendatario con ID: {}", id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: ID no encontrado.");
        }
        repository.deleteById(id);
    }
}