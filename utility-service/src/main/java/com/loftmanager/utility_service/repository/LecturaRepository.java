package com.loftmanager.utility_service.repository;

import com.loftmanager.utility_service.model.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {
    List<Lectura> findByIdLoftFk(Long idLoftFk);

    Optional<Lectura> findFirstByIdLoftFkOrderByFechaLecturaDesc(Long idLoftFk);

}
