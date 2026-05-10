package com.loftmanager.utility_service.config;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final LecturaRepository lecturaRepository;

    public DataSeeder(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    @Override
    @SuppressWarnings("null")
    public void run(String... args) throws Exception {
        if (lecturaRepository.count() == 0) {
            lecturaRepository.saveAll(List.of(
                new Lectura(null, 150.5, LocalDateTime.now().minusDays(30), 101L),
                new Lectura(null, 180.2, LocalDateTime.now(), 101L)
            ));
            System.out.println("--> Datos de consumo iniciales creados en db_utility.");
        }
    }
}