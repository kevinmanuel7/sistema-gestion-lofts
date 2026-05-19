package com.loftmanager.loftservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loftmanager.loftservice.model.LoftModel;
import com.loftmanager.loftservice.repository.LoftRepository;

@Service
@SuppressWarnings("null")
public class LoftService {

    @Autowired
    private LoftRepository loftRepository;

    public List<LoftModel> listarTodos() {
        return loftRepository.findAll();
    }

    public LoftModel guardar(LoftModel loft) {
        // Regla de Negocio: Validar que el ID (casilla física) esté entre 1 y 9
        if (loft.getIdLoft() == null || loft.getIdLoft() < 1 || loft.getIdLoft() > 9) {
            throw new IllegalArgumentException("Error: Solo se pueden registrar Lofts en las casillas físicas del 1 al 9.");
        }
        
        return loftRepository.save(loft);
    }

    public void eliminar(Long id) {
        loftRepository.deleteById(id);
    }
    
    public boolean existePorId(Long id) {
        return loftRepository.existsById(id);
    }
}