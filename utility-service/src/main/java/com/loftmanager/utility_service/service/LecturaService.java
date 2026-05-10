package com.loftmanager.utility_service.service;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@SuppressWarnings("null")
public class LecturaService {

    private final LecturaRepository lecturaRepository;

    public LecturaService(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    //CRUD

    // CREATE (POST)
    public Lectura createLectura(Lectura lectura) {
        if (lectura.getFechaLectura() == null) {
            lectura.setFechaLectura(java.time.LocalDateTime.now());
        }
        return lecturaRepository.save(lectura);
    }

    // READ ALL (GET)
    public List<Lectura> getAllLecturas() {
        return lecturaRepository.findAll();
    }

    // READ BY ID (GET)
    public Optional<Lectura> getLecturaById(Long id) {
        return lecturaRepository.findById(id);
    }

    // READ BY LOFT ID (GET)
    public List<Lectura> getLecturasByLoft(Long idLoftFk) {
        return lecturaRepository.findByIdLoftFk(idLoftFk);
    }

    // UPDATE (PUT)
    public Lectura updateLectura(Long id, Lectura lecturaDetails) {
        return lecturaRepository.findById(id).map(lectura -> {
            lectura.setValorKwh(lecturaDetails.getValorKwh());
            lectura.setIdLoftFk(lecturaDetails.getIdLoftFk());
            if (lecturaDetails.getFechaLectura() != null) {
                lectura.setFechaLectura(lecturaDetails.getFechaLectura());
            }
            return lecturaRepository.save(lectura);
        }).orElseThrow(() -> new RuntimeException("Lectura no encontrada con ID: " + id));
    }

    // DELETE
    public void deleteLectura(Long id) {
        if (!lecturaRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar. Lectura no encontrada con ID: " + id);
        }
        lecturaRepository.deleteById(id);
    }
}