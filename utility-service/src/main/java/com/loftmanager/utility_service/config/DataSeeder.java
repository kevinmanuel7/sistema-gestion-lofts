package com.loftmanager.utility_service.config;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final LecturaRepository lecturaRepository;

    public DataSeeder(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    @Override
    @SuppressWarnings("null")
    public void run(String... args) throws Exception {
        // Si la tabla está vacía, inicializamos las bases de los 9 lofts
        if (lecturaRepository.count() == 0) {
            
            // Recorremos del loft 1 al 9 automáticamente
            for (long i = 1; i <= 9; i++) {
                
                // Le asignamos a cada loft una lectura base diferente en abril para simular realidad
                // Loft 1 partirá en 1200, Loft 2 en 1250, Loft 3 en 1300, etc.
                Double lecturaBaseAbril = 1200.0 + (i * 50.0); 

                Lectura lecturaAbril = new Lectura();
                lecturaAbril.setLecturaMedidor(lecturaBaseAbril); 
                lecturaAbril.setConsumoPeriodoKwh(0.0); // Es la lectura inicial de configuración
                lecturaAbril.setMontoCobro(0.0);
                lecturaAbril.setFechaLectura(LocalDateTime.now().minusDays(30)); // Hace 30 días
                lecturaAbril.setIdLoftFk(i); // El ID va cambiando del 1 al 9 gracias al bucle

                lecturaRepository.save(lecturaAbril);
            }
            
            System.out.println("--> ÉXITO: Se han sembrado las lecturas base de Abril para los 9 Lofts en la db_utility.");
        }
    }
}