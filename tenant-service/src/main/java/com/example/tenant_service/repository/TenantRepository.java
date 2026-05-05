package com.example.tenant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tenant_service.modelo.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long>{
    boolean existsByRut(String rut);
}
