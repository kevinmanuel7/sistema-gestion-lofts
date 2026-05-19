package com.loftmanager.loftservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loftmanager.loftservice.dto.InventoryDto;
import com.loftmanager.loftservice.model.LoftModel;
import com.loftmanager.loftservice.repository.LoftRepository;
import com.loftmanager.loftservice.service.LoftService;
import com.loftmanager.loftservice.cliente.InventoryClient;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loft")
@SuppressWarnings("null")
public class LoftController {

    @Autowired
    private LoftRepository loftRepository;

    @Autowired
    private InventoryClient InventoryClient;

    @Autowired
    private LoftService loftService;

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLoftPorId(@PathVariable("id") Long id) {
        java.util.Optional<LoftModel> loft = loftRepository.findById(id);
        
        if (loft.isPresent()) {
            return ResponseEntity.ok(loft.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loft no encontrado");
        }
    }
    
    @GetMapping("/{idLoft}/muebles")
    public ResponseEntity<?> listarMueblesPorloft(@PathVariable("idLoft") Long idLoft) { 
        if (!loftRepository.existsById(idLoft)) {
            return new ResponseEntity<>("El loft con ID " + idLoft + " no existe.", HttpStatus.NOT_FOUND);
        }
        InventoryDto inventario = InventoryClient.getItemsByLoftId(idLoft); 
        return ResponseEntity.ok(inventario);
    }
    @GetMapping("/listar")
    public List<LoftModel> listar() {
        return loftRepository.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody LoftModel loft) {
        try {
            if (loftService.existePorId(loft.getIdLoft())) {
                return new ResponseEntity<>("Error: El Loft con ID " + loft.getIdLoft() + " ya existe.", HttpStatus.BAD_REQUEST);
            }
            
            // 2. Llamamos al service, que es quien ejecuta la validación del 1 al 9
            LoftModel nuevoLoft = loftService.guardar(loft);
            return new ResponseEntity<>(nuevoLoft, HttpStatus.CREATED);
            
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/actualizar/{idLoft}")
    public ResponseEntity<?> actualizar(@PathVariable("idLoft") Long idLoft, @Valid @RequestBody LoftModel nuevoLoft) {
        java.util.Optional<LoftModel> loftOptional = loftRepository.findById(idLoft);
        
        if (loftOptional.isPresent()) {
            LoftModel loftExistente = loftOptional.get();
            
            loftExistente.setNombreLoft(nuevoLoft.getNombreLoft());
            loftExistente.setIsOcupado(nuevoLoft.getIsOcupado());
            
            LoftModel loftActualizado = loftRepository.save(loftExistente);
            return ResponseEntity.ok(loftActualizado);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: El Loft con ID " + idLoft + " no existe.");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        if (loftRepository.existsById(id)) {
            loftRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
