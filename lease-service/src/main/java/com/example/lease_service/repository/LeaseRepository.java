package com.example.lease_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.lease_service.model.Lease;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    
    // Un método útil que podrías necesitar mañana:
    // Buscar todos los arriendos de un arrendatario específico
    List<Lease> findByTenantId(Long tenantId);
}
