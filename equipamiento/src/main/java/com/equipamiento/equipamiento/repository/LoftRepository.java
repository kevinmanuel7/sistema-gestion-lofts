package com.equipamiento.equipamiento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamiento.equipamiento.model.Loft;

@Repository

public interface LoftRepository extends JpaRepository< Loft, Long>{

    
}


