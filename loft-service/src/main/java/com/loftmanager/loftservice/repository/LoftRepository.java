package com.loftmanager.loftservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loftmanager.loftservice.model.LoftModel;

@Repository
public interface LoftRepository extends JpaRepository<LoftModel, Long> {
}