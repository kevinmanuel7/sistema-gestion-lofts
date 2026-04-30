package com.loftmanager.utility_service.repository;

import com.loftmanager.utility_service.model.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {
    // Método para buscar todas las lecturas de un loft específico
    List<Lectura> findByIdLoftFk(Long idLoftFk);
}
