package com.example.lease_service.service;

import com.example.lease_service.LeaseServiceApplication;
import com.example.lease_service.model.Lease;
import com.example.lease_service.repository.LeaseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaseService {

    private final LeaseServiceApplication leaseServiceApplication;
    @Autowired
    public LeaseRepository leaseRepository;

    LeaseService(LeaseServiceApplication leaseServiceApplication) {
        this.leaseServiceApplication = leaseServiceApplication;
    }

    //Obtener todo.
    public List<Lease> getAllLeases(){
        return leaseRepository.findAll();
    } 

    //Obtener por ID.
    public Optional<Lease> getLeaseById(Long id){
        return leaseRepository.findById(id);
    }
    
    //Guardar.
    public Lease saveLease(Lease lease) {

        //1. Validación de consistencia de fecha.
        validarConsistenciasFechas(lease);

        //2. Validar si el loft ya se encuentra ocupado
        validarDisponibilidad(lease);
        
        //3. Si todo está OK, guardar.
        return leaseRepository.save(lease);
    }

    //Metodo privado: Validar que una fecha de inicio no sea después de la fecha de fin.
    private void validarConsistenciasFechas(Lease lease){
    if (lease.getFechaTermino() != null){
        if (lease.getFechaInicio().isAfter(lease.getFechaTermino())){
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de termino");
        }
    }
    }

    //Metodo privado: Validar que Loft no se encuentre ocupado en el rango de fecha seleccionado.
    private void validarDisponibilidad(Lease nuevo) {
    LocalDate fin = (nuevo.getFechaTermino() != null) ? nuevo.getFechaTermino() : LocalDate.MAX;
    
    if (leaseRepository.existsOverlap(nuevo.getLoftId(), nuevo.getFechaInicio(), fin)) {
        throw new IllegalStateException("El Loft ya está ocupado en esas fechas.");
    }
    }   

    // Eliminar un contrato
    public void deleteLease(Long id) {
        leaseRepository.deleteById(id);
    }


}
