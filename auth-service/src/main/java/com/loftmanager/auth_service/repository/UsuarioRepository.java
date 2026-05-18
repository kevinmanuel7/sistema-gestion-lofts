package com.loftmanager.auth_service.repository;

import com.loftmanager.auth_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Forzamos el query de forma manual para saltarnos el bug de parseo de Spring Data
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.idLoft = :idLoft")
    boolean existsByIdLoft(@Param("idLoft") Long idLoft);

    Optional<Usuario> findByUsername(String username);
    
}