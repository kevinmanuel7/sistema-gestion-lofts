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

import com.loftmanager.loftservice.Model.LoftModel;
import com.loftmanager.loftservice.Repository.LoftRepository;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loft")
public class LoftController {

    @Autowired
    private LoftRepository loftRepository;

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
