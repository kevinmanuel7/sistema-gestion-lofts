package com.example.lease_service.service;

import com.example.lease_service.model.Lease;
import com.example.lease_service.repository.LeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final WebClient.Builder webClientBuilder; // Nuevo: Para interconexión

    public LeaseService(LeaseRepository leaseRepository, WebClient.Builder webClientBuilder) {
        this.leaseRepository = leaseRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<Lease> getAllLeases() {
        return leaseRepository.findAll();
    }

    public Optional<Lease> getLeaseById(Long id) {
        return leaseRepository.findById(id);
    }

    // Guardar con todas las reglas de negocio
    public Lease saveLease(Lease lease) {
        // 1. Validación de consistencia de fechas (Tuya)
        validarConsistenciasFechas(lease);

        // 2. Validar si el loft ya se encuentra ocupado (Tuya)
        validarDisponibilidad(lease);

        // 3. NUEVO: Validar rango de Loft (1 a 9)
        validarRangoLoft(lease.getLoftId());

        // 4. NUEVO: Validar existencia del inquilino en tenant-service (Interconexión)
        validarInquilinoExistente(lease.getTenantId());

        return leaseRepository.save(lease);
    }

    // Metodo privado: Validar rango permitido (1-9)
    private void validarRangoLoft(Long loftId) {
        if (loftId < 1 || loftId > 9) {
            throw new IllegalArgumentException("Error de negocio: El ID del loft " + loftId + " no existe. Solo operamos con los lofts del 1 al 9.");
        }
    }

    // Metodo privado: Consultar al tenant-service
    private void validarInquilinoExistente(Long tenantId) {
        try {
            webClientBuilder.build()
                .get()
                .uri("http://tenant-service/api/tenants/" + tenantId)
                .retrieve()
                .toBodilessEntity()
                .block(); // Espera la respuesta
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: El inquilino con ID " + tenantId + " no está registrado. No se puede crear el contrato.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de comunicación: El servicio de inquilinos no responde.");
        }
    }

    private void validarConsistenciasFechas(Lease lease) {
        if (lease.getFechaTermino() != null) {
            if (lease.getFechaInicio().isAfter(lease.getFechaTermino())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de termino");
            }
        }
    }

    private void validarDisponibilidad(Lease nuevo) {
        LocalDate fin = (nuevo.getFechaTermino() != null) ? nuevo.getFechaTermino() : LocalDate.MAX;
        if (leaseRepository.existsOverlap(nuevo.getLoftId(), nuevo.getFechaInicio(), fin)) {
            throw new IllegalStateException("El Loft ya está ocupado en esas fechas.");
        }
    }

    public Lease updateLease(Long id, Lease leaseDetalles) {
        Lease leaseExistente = leaseRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ID no existe"));

        validarConsistenciasFechas(leaseDetalles);
        validarRangoLoft(leaseDetalles.getLoftId()); // También validamos en el update
        validarInquilinoExistente(leaseDetalles.getTenantId()); // También validamos en el update

        leaseExistente.setTenantId(leaseDetalles.getTenantId());
        leaseExistente.setLoftId(leaseDetalles.getLoftId());
        leaseExistente.setFechaInicio(leaseDetalles.getFechaInicio());
        leaseExistente.setFechaTermino(leaseDetalles.getFechaTermino());
        leaseExistente.setPrecioMensual(leaseDetalles.getPrecioMensual());
        leaseExistente.setMontoGarantia(leaseDetalles.getMontoGarantia());
        leaseExistente.setEstadoContrato(leaseDetalles.getEstadoContrato());

        return leaseRepository.save(leaseExistente);
    }

    public void deleteLease(Long id) {
        leaseRepository.deleteById(id);
    }

    public boolean verificarLoftArrendado(Long loftId) {
        return leaseRepository.existsByLoftIdAndEstadoContratoIgnoreCase(loftId, "ACTIVO");
    }
}