package com.loftmanager.utility_service.service;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class LecturaService {

    private final LecturaRepository lecturaRepository;

    // Inyectamos de forma limpia el PRECIO desde el application.properties
    @Value("${utility.precio-kwh}")
    private Double precioKwhConfig;

    public LecturaService(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    // CREATE (POST)
    public Lectura createLectura(Lectura lectura) {
        // 1. Validación de rango (Lofts del 1 al 9)
        if (lectura.getIdLoftFk() == null || lectura.getIdLoftFk() < 1 || lectura.getIdLoftFk() > 9) {
            throw new IllegalArgumentException("Operación inválida: El sistema solo administra los Lofts del 1 al 9.");
        }

        // 2. Buscar la última lectura del medidor registrada el mes anterior
        Optional<Lectura> lecturaAnteriorOpt = lecturaRepository.findFirstByIdLoftFkOrderByFechaLecturaDesc(lectura.getIdLoftFk());
        
        // Si no hay mes anterior (es nuevo), la marca anterior del medidor empieza en 0.0
        Double lecturaMedidorAnterior = lecturaAnteriorOpt.map(Lectura::getLecturaMedidor).orElse(0.0);

        // Validar que el medidor no corra hacia atrás
        if (lectura.getLecturaMedidor() < lecturaMedidorAnterior) {
            throw new IllegalArgumentException("Error: La lectura actual (" + lectura.getLecturaMedidor() 
                    + ") no puede ser menor a la lectura anterior (" + lecturaMedidorAnterior + ").");
        }

        // 3. LA RESTA AUTOMÁTICA: Lectura Actual (Mayo: 1340) - Lectura Anterior (Abril: 1250) = 90
        Double consumoNeto = lectura.getLecturaMedidor() - lecturaMedidorAnterior;
        
        // 4. EL CÁLCULO EN PESOS AUTOMÁTICO: 90 kWh * $200 = $18.000
        Double montoCalculado = consumoNeto * precioKwhConfig;

        // 5. Seteamos los campos calculados automáticamente antes de guardar en MySQL
        lectura.setConsumoPeriodoKwh(consumoNeto);
        lectura.setMontoCobro(montoCalculado);

        if (lectura.getFechaLectura() == null) {
            lectura.setFechaLectura(java.time.LocalDateTime.now());
        }
        return lecturaRepository.save(lectura);
    }

    public List<Lectura> getAllLecturas() {
        return lecturaRepository.findAll();
    }

    public Optional<Lectura> getLecturaById(Long id) {
        return lecturaRepository.findById(id);
    }

    public List<Lectura> getLecturasByLoft(Long idLoftFk) {
        return lecturaRepository.findByIdLoftFk(idLoftFk);
    }

    // UPDATE (PUT)
    public Lectura updateLectura(Long id, Lectura lecturaDetails) {
        return lecturaRepository.findById(id).map(lectura -> {
            lectura.setLecturaMedidor(lecturaDetails.getLecturaMedidor());
            lectura.setIdLoftFk(lecturaDetails.getIdLoftFk());
            if (lecturaDetails.getFechaLectura() != null) {
                lectura.setFechaLectura(lecturaDetails.getFechaLectura());
            }
            
            // Recalcular en caso de actualización
            Optional<Lectura> lecturaAnteriorOpt = lecturaRepository.findFirstByIdLoftFkOrderByFechaLecturaDesc(lecturaDetails.getIdLoftFk());
            Double lecturaMedidorAnterior = lecturaAnteriorOpt.map(Lectura::getLecturaMedidor).orElse(0.0);
            Double consumoNeto = lecturaDetails.getLecturaMedidor() - lecturaMedidorAnterior;
            
            lectura.setConsumoPeriodoKwh(consumoNeto);
            lectura.setMontoCobro(consumoNeto * precioKwhConfig);
            
            return lecturaRepository.save(lectura);
        }).orElseThrow(() -> new RuntimeException("Lectura no encontrada con ID: " + id));
    }

    public void deleteLectura(Long id) {
        if (!lecturaRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Lectura no encontrada con ID: " + id);
        }
        lecturaRepository.deleteById(id);
    }
}