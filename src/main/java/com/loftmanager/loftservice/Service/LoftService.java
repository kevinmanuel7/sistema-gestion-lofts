package com.loftmanager.loftservice.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loftmanager.loftservice.Model.LoftModel;
import com.loftmanager.loftservice.Repository.LoftRepository;

@Service
public class LoftService {

    @Autowired
    private LoftRepository loftRepository;

    public List<LoftModel> listarTodos() {
        return loftRepository.findAll();
    }

    public LoftModel guardar(LoftModel loft) {
        return loftRepository.save(loft);
    }

    public void eliminar(Long id) {
        loftRepository.deleteById(id);
    }
    
    public boolean existePorId(Long id) {
        return loftRepository.existsById(id);
    }
}