package com.loftmanager.loftservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loftmanager.loftservice.Model.LoftModel;

@Repository
public interface LoftRepository extends JpaRepository<LoftModel, Long> {
}