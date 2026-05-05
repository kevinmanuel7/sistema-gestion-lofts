package com.example.tenant_service.service;

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

    @Autowired
    private TenantRepository repository;

    //Metodo para listar arrendatarios
    public List<Tenant> listarTodos(){
        log.info("Iniciando consulta de todos los arrendatarios registrados");
        return repository.findAll();
    }

    //Metodo buscar por ID (Util para usar después en el microservicio Lease-Service.)
    public Optional<Tenant>buscarporId(long id){
        log.info("Buscando arrendatario con ID: {}", id);
        return repository.findById(id);
    }

    //Metodo para guardar con lógica de negocio.
    public Tenant guardar(Tenant tenant){
        log.info("Procesando registro de: {}", tenant.getNombre());

        //Lógica de validación: No puede realizar un registro con el RUT Duplicado.
        if (repository.existsByRut(tenant.getRut())){
            log.warn("Intento de registro duplicado para el RUT: {}", tenant.getRut());
            throw new RuntimeException("El arrendatario ya se encuentra registrado");

        }

        return repository.save(tenant);
    }
    
}
