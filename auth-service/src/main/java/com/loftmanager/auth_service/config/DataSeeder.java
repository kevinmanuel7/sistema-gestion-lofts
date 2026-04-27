package com.loftmanager.auth_service.config;

import com.loftmanager.auth_service.model.Rol;
import com.loftmanager.auth_service.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;

    public DataSeeder(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (rolRepository.count() == 0) {
            Rol admin = new Rol(null, "ADMIN");
            Rol operador = new Rol(null, "OPERADOR");
            Rol cliente = new Rol(null, "CLIENTE");

            rolRepository.saveAll(List.of(admin, operador, cliente));
            System.out.println("--> Roles iniciales creados exitosamente.");
        }
    }
}