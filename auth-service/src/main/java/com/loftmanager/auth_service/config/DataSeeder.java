package com.loftmanager.auth_service.config;

import com.loftmanager.auth_service.model.Rol;
import com.loftmanager.auth_service.model.Usuario;
import com.loftmanager.auth_service.repository.RolRepository;
import com.loftmanager.auth_service.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; 

    public DataSeeder(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @SuppressWarnings("null")
    public void run(String... args) throws Exception {
        
        //Crear los Roles únicamente si la tabla está vacía
        if (rolRepository.count() == 0) {
            Rol admin = new Rol(null, "ADMIN");
            Rol operador = new Rol(null, "OPERADOR");
            Rol cliente = new Rol(null, "CLIENTE");

            rolRepository.saveAll(List.of(admin, operador, cliente));
            System.out.println("--> Roles iniciales creados exitosamente.");
        }

        List<Rol> todosLosRoles = rolRepository.findAll();
        
        Rol adminRol = todosLosRoles.stream()
                .filter(r -> "ADMIN".equalsIgnoreCase(r.getNombreRol()))
                .findFirst()
                .orElseGet(() -> rolRepository.save(new Rol(null, "ADMIN")));

        if (!usuarioRepository.findByUsername("admin_kevin").isPresent()) {
            Usuario adminMaestro = new Usuario();
            adminMaestro.setUsername("admin_kevin");
            adminMaestro.setPassword(passwordEncoder.encode("password123")); 
            adminMaestro.setNombreReal("Kevin Maturana");
            adminMaestro.setCargoUsuario("ADMINISTRADOR");
            adminMaestro.setEmail("kevin@loftmanager.com");
            adminMaestro.setRol(adminRol); 
            adminMaestro.setIdLoft(null); 

            usuarioRepository.saveAll(List.of(adminMaestro));
            System.out.println("--> Cuenta maestra (ADMIN) inicializada con éxito en MySQL.");
        }
    }
}