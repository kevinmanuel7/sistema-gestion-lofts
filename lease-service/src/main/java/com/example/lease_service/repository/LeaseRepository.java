package com.example.lease_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.lease_service.model.Lease;

import feign.Param;

@Repository
public interface LeaseRepository extends JpaRepository<Lease, Long> {
    
    List<Lease> findByTenantId(Long tenantId);

    
    @Query("SELECT COUNT(l) > 0 FROM Lease l WHERE l.loftId = :loftId " +
    "AND (l.fechaTermino >= :inicio AND l.fechaInicio <= :fin)")
    boolean existsOverlap(@Param("loftId") Long loftId, 
                   @Param("inicio") LocalDate inicio, 
                   @Param("fin") LocalDate fin);
}
