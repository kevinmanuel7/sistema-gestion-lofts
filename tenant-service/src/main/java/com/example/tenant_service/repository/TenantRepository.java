package com.example.tenant_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tenant_service.modelo.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long>{


    //Verificar si ya existe un arrendatario con un RUT especifico
    boolean existsByRut(String rut);

    //Verificar si ya existe un arrendatario con un Email especifico.
    boolean existsByEmail(String email);

    //Buscar un arrendatario por su rut.
    Optional<Tenant> findByRut(String rut);

}
