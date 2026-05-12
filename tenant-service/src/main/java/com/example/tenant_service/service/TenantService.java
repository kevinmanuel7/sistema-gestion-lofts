package com.example.tenant_service.service;

import com.example.tenant_service.controller.TenantController;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tenant_service.modelo.Tenant;
import com.example.tenant_service.repository.TenantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TenantService {

    private final TenantRepository repository;

    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    //Obtener los arrendatarios registrados.
    public List<Tenant> listarTodos(){
        log.info("Iniciando consulta de todos los arrendatarios registrados");
        return repository.findAll();
    }

    //Buscar  arrendatario por su ID.
    public Optional<Tenant>buscarporId(long id){
        log.info("Buscando arrendatario con ID: {}", id);
        return repository.findById(id);
    }

    //Guardar nuevo arrendatario con validaciones de duplicidad.
    public Tenant guardar(Tenant tenant){
        log.info("Procesando registro de: {}", tenant.getNombre(), tenant.getApellido());

        //1. Validación de Rut duplicado.
        if (repository.existsByRut(tenant.getRut())){
            log.warn("Intento de registro duplicado para el RUT: {}", tenant.getRut());
            throw new RuntimeException("El arrendatario ya se encuentra registrado");

        }
        
        //2. Validación de email duplicado.
        if (repository.existsByEmail(tenant.getEmail())){
            log.warn("Intento de registro duplicado para el email: {}", tenant.getEmail());
            throw new RuntimeException("El email ya se encuentra registrado");
        }

        return repository.save(tenant);
        
    }
    //Actualización de datos de un arrendatario registrado.
    public Tenant actualizar(Long id, Tenant tenantDetalles){
        log.info("Actualizar arrendatario con ID {}", id);

        return repository.findById(id).map(tenant ->{
            //Por seguridad, el Rut no se puede modificar.
            tenant.setNombre(tenantDetalles.getNombre());
            tenant.setApellido(tenantDetalles.getApellido());
            tenant.setTelefono(tenantDetalles.getTelefono());
            tenant.setEmail(tenantDetalles.getEmail());
            return repository.save(tenant);

        }).orElseThrow(() -> new RuntimeException("No se encontró el arrendatario con ID: " + id));
    }

    //Eliminar un arrendatario del sistema.
    public void eliminar(Long id) {
        log.info("Eliminando arrendatario con ID: {}", id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: ID no encontrado.");
        }
        repository.deleteById(id);
    }
    
}
