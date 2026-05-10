package com.loftmanager.utility_service.controller;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.repository.LecturaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/utility")
public class LecturaController {

    private final LecturaRepository lecturaRepository;

    public LecturaController(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    @GetMapping("/lecturas")
    public ResponseEntity<List<Lectura>> getAllLecturas() {
        return ResponseEntity.ok(lecturaRepository.findAll());
    }

    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Utility-Service protegido por JWT y en ejecución.");
    }
}