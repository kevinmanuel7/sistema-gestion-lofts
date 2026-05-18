package com.loftmanager.loftservice.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loftmanager.loftservice.Clients.InventoryClient;
import com.loftmanager.loftservice.Dtos.InventoryDto;
import com.loftmanager.loftservice.Model.LoftModel;
import com.loftmanager.loftservice.Repository.LoftRepository;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loft")
public class LoftController {

    @Autowired
    private LoftRepository loftRepository;

    @Autowired
    private InventoryClient inventoryClient;

    @GetMapping("/{idLoft}/muebles")
public ResponseEntity<?> listarMueblesPorloft(@PathVariable("idLoft") Long idLoft) { 
    // Ahora "idLoft" coincide en ambos lados
    if (!loftRepository.existsById(idLoft)) {
        return new ResponseEntity<>("El loft con ID " + idLoft + " no existe.", HttpStatus.NOT_FOUND);
    }
    List<InventoryDto> muebles = inventoryClient.getItemsByLoftId(idLoft);
    return ResponseEntity.ok(muebles);
}
    @GetMapping("/listar")
    public List<LoftModel> listar() {
        return loftRepository.findAll();
    }

    @PostMapping("/crear")
    public ResponseEntity<LoftModel> crear(@Valid @RequestBody LoftModel loft) {
        return new ResponseEntity<>(loftRepository.save(loft), HttpStatus.CREATED);
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
