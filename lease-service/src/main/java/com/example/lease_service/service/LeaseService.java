package com.example.lease_service.service;

import com.example.lease_service.model.Lease;
import com.example.lease_service.repository.LeaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final WebClient.Builder webClientBuilder;

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

    public Lease saveLease(Lease lease) {
        validarRangoLoft(lease.getLoftId());
        validarConsistenciasFechas(lease);
        validarDisponibilidad(lease);
        
        // Validaciones externas (Interconexión con Token Relay)
        String token = obtenerTokenDePeticion();
        validarLoftExistente(lease.getLoftId(), token);
        validarInquilinoExistente(lease.getTenantId(), token);

        return leaseRepository.save(lease);
    }

    public Lease updateLease(Long id, Lease leaseDetalles) {
        Lease leaseExistente = leaseRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("ID de contrato no existe"));

        validarRangoLoft(leaseDetalles.getLoftId());
        validarConsistenciasFechas(leaseDetalles);
        
        String token = obtenerTokenDePeticion();
        validarLoftExistente(leaseDetalles.getLoftId(), token);
        validarInquilinoExistente(leaseDetalles.getTenantId(), token);

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

    // --- MÉTODOS DE VALIDACIÓN ---

    private void validarRangoLoft(Long loftId) {
        if (loftId < 1 || loftId > 9) {
            throw new IllegalArgumentException("Error de negocio: El ID del loft " + loftId + " no existe. Solo operamos con los lofts del 1 al 9.");
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

    // Extrae el JWT actual
    private String obtenerTokenDePeticion() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }

    // DTO para Loft
    public static class LoftDTO {
        public boolean isOcupado; 
    }

    // Validación para LOFT (Puerto 8083)
    private void validarLoftExistente(Long loftId, String token) {
        try {
            LoftDTO loft = WebClient.create()
                .get()
                .uri("http://api-gateway:8080/api/loft/" + loftId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(LoftDTO.class)
                .block();

            if (loft == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loft no encontrado");
            }

            if (loft.isOcupado) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede crear un contrato en un loft que está ocupado.");
            }
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: El Loft ID " + loftId + " no existe.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de comunicación con Loft-Service (8083): " + e.getMessage());
        }
    }

    // Validación para INQUILINO (Puerto 8086)
    private void validarInquilinoExistente(Long tenantId, String token) {
        try {
            WebClient.create()
                .get()
                .uri("http://api-gateway:8080/api/tenants/" + tenantId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: El inquilino con ID " + tenantId + " no existe.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de comunicación con Tenant-Service (8086): " + e.getMessage());
        }
    }
    
}