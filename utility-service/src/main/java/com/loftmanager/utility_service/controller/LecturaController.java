package com.loftmanager.utility_service.controller;

import com.loftmanager.utility_service.model.Lectura;
import com.loftmanager.utility_service.service.LecturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/utility")
public class LecturaController {

    private final LecturaService lecturaService;

    public LecturaController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    //LEER GET: /api/utility/status
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Utility-Service protegido por JWT y en ejecución.");
    }

    //LEER GET: /api/utility/lecturas (LISTAR TODO)
    @GetMapping("/lecturas")
    public ResponseEntity<List<Lectura>> getAll() {
        return ResponseEntity.ok(lecturaService.getAllLecturas());
    }

    //LEER GET: /api/utility/lecturas/{id} (OBTENER UNA)
    @GetMapping("/lecturas/{id}")
    public ResponseEntity<Lectura> getById(@PathVariable Long id) {
        return lecturaService.getLecturaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //LEER GET: /api/utility/lecturas/loft/{idLoftFk} (OBTENER LECTURAS DE UN LOFT ESPECÍFICO)
    @GetMapping("/lecturas/loft/{idLoftFk}")
    public ResponseEntity<List<Lectura>> getByLoft(@PathVariable Long idLoftFk) {
        return ResponseEntity.ok(lecturaService.getLecturasByLoft(idLoftFk));
    }

    //CREAR POST: /api/utility/lecturas (CREAR NUEVA LECTURA)
    @PostMapping("/lecturas")
    public ResponseEntity<Lectura> create(@RequestBody Lectura lectura) {
        return ResponseEntity.ok(lecturaService.createLectura(lectura));
    }

    //ACTUALIZAR PUT: /api/utility/lecturas/{id} (ACTUALIZAR)
    @PutMapping("/lecturas/{id}")
    public ResponseEntity<Lectura> update(@PathVariable Long id, @RequestBody Lectura lectura) {
        return ResponseEntity.ok(lecturaService.updateLectura(id, lectura));
    }

    // DELETE: /api/utility/lecturas/{id} (ELIMINAR)
    @DeleteMapping("/lecturas/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lecturaService.deleteLectura(id);
        return ResponseEntity.noContent().build();
    }
}